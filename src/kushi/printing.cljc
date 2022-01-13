(ns kushi.printing
  #?(:clj (:require [io.aviso.ansi :as ansi]))
  (:require
   [clojure.string :as string]
   [clojure.pprint :refer [pprint]]
   [kushi.utils :as util]
   [kushi.state :as state]
   [kushi.atomic :as atomic]
   [kushi.config :refer [user-config]]))


;; Helpers for logging formatting   ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(def rainbow
  #?(:clj
     [ansi/yellow-font
      ansi/red-font
      ansi/magenta-font
      ansi/blue-font
      ansi/cyan-font
      ansi/green-font]))

(def bold-rainbow
  #?(:clj
     [ansi/bold-yellow-font
      ansi/bold-red-font
      ansi/bold-magenta-font
      ansi/bold-blue-font
      ansi/bold-cyan-font
      ansi/bold-green-font]))

(def warning-stripes
  #?(:clj
     [ansi/bold-yellow-font
      ansi/bold-black-font]))

(def error-stripes
  #?(:clj
     [ansi/bold-red-font
      ansi/bold-white-font]))

(def info-dots
  #?(:clj
     [ansi/bold-black-font]))

(def yellow-tape
  #?(:clj
     [ansi/bold-yellow-font]))


(defn border*
  [theme n s]
  (str " " (apply str
                  (interpose s (take n (cycle theme))))))

(def border-length 18)

(def warning-border
  (border* warning-stripes border-length "---"))

(def error-border
  (border* error-stripes border-length "***"))

(def info-border
  (border* info-dots border-length "..."))

(def rainbow-border
  (border* bold-rainbow border-length "..."))

(defn js-fmt-args
  [{:keys [invalid-args :styles-argument-display]}]
  (mapv #(let [bad? (contains? (into #{} invalid-args) %)
               q (when (string? %) "\"")]
           (str (if bad? (str "\n" "%c %c ")
                    "\n  ")
                (when bad? "%c")
                q (if bad? % "...") q
                (when bad? "%c")))
        styles-argument-display))


(defn squiggly-underline [q s]
  #?(:clj
     (ansi/bold-yellow (str " " (string/join (repeat (+ (if q 2 0) (count (str s))) "^"))))))


(defn console-error-ansi-formatting [m]
  #?(:clj
     (let [lines* (mapv
                   #(let [bad? (contains? (into #{} (:invalid-args m)) %)
                          q (when (string? %) "\"")
                          bad-arg-formatted (str ansi/bold-font " " q % q ansi/reset-font)]
                      (if bad?
                        bad-arg-formatted
                        #_[bad-arg-formatted (squiggly-underline q %)]
                        " ..."))
                   (:styles-argument-display m))
           flatlines (into [] (flatten lines*))
           last-val (-> flatlines last (str ")"))
           lines (assoc flatlines (-> flatlines count dec) last-val)]
       lines)))

(defn file-info-str
  [{:keys [js? form-meta]}]
  #?(:clj
     (let [{:keys [file line column]} form-meta]
       (str file ":"  (when-not js? ansi/bold-font) line ":" column (when-not js? ansi/reset-font)))))


;; Dictionaries   ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(def dict
  {:defclass {:expected (str "kushi.core/defclass expects a name (symbol),\n"
                             "followed by any number of the following:"
                             "\n\n  - Keyword representing style declaration, e.g.,"
                             "\n    :color--red"
                             "\n\n  - 2-element vector representing a style declaration, e.g.,"
                             "\n    [\"nth-child(2):color\" :blue]"
                             "\n\n  - Keyword representing an existing kushi class to be \"mixed-in\", e.g.,"
                             "\n    :bold-red-text")

              :learn-more "Look at kushi.core/defclass docs for more details."
              :find-source "Look at browser console for source map info."}

   :sx {:expected (str "kushi.core/sx expects:"
                       "\n\n"
                       "- Any number of the following:"
                       "\n\n  - Keyword representing style declaration, e.g.,"
                       "\n    :color--red"
                       "\n\n  - 2-element vector representing a style declaration, e.g.,"
                       "\n    [:color my-color]"
                       "\n\n  - Keyword representing a class, e.g."
                       "\n    :my-class"
                       "\n\n  - Valid conditional class expression"
                       "\n    (when my-condition :my-class)"
                       "\n\n"
                       "- An optional map of html attributes."
                       "\n  If present, this must be the last argument.")
        :learn-more "See kushi.core/sx docs for more details"}})

;; Warnings for kushi.core/sx   ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(def left-border-glyphstring ": ")

(defn warning-header
  [{:keys [invalid-args fname js?]}]
  #?(:clj
     (if js?
      (str "Warning: %cInvalid argument" (when (< 1 (count invalid-args)) "s") "%c"  " to kushi.core/" fname ".")
      (str
       ansi/bold-font
       "Invalid argument" (when (< 1 (count invalid-args)) "s")
       ansi/reset-font
       " to kushi.core/" fname))
     :cljs
     (str "Warning: %cInvalid argument" (when (< 1 (count invalid-args)) "s") "%c"  " to kushi.core/" fname ".")))

(defn warning-call-classname
  [{:keys [defclass-name invalid-args]}]
  #?(:clj (when defclass-name
            (if (some-> invalid-args meta :classname)
              (str ansi/bold-font (first invalid-args) ansi/reset-font)
              (name defclass-name)))))

(defn warning-call-with-args
  [{:keys [fname classname js?] :as m}]
   #?(:clj
      (if js?
        (str "(" fname " "
             (when classname (name classname))
             (string/join (js-fmt-args m))
             ")")
        (let [resolved-classname (warning-call-classname m)]
          (concat
           [(str "(" fname " " resolved-classname)]
           (console-error-ansi-formatting m))))
      :cljs
      (str "(" fname " "
           (when classname (name classname))
           (string/join (js-fmt-args m))
           ")")))

(defn js-warning*
  [m]
   #?(:cljs
      (when ^boolean js/goog.DEBUG
        (let [warning (string/join
                       "\n\n"
                       [(warning-header m)
                        (warning-call-with-args m)
                      ;; (-> fname keyword dict :expected)
                        (str (-> m :fname keyword dict :learn-more) "\n")])
              number-of-formats (count (re-seq #"%c" warning))]
          (to-array
           (concat
            [warning]
            ["color:black;font-weight:bold" "font-weight:normal" "font-weight:bold;color:#ffaa00" "font-weight:normal"]
            (interleave (repeat (/ (- number-of-formats 4) 2) "color:black;font-weight:bold")
                        (repeat (/ (- number-of-formats 4) 2) "color:default;font-weight:normal")))))
          )))

(defn preformat-js-warning
  [m]
   #?(:clj
      (let [m                 (assoc m :js? true)
            warning           (string/join
                               "\n\n"
                               [(warning-header m)
                                (warning-call-with-args m)
                                ;; (-> fname keyword dict :expected)
                                (file-info-str m)
                                (str (-> m :fname keyword dict :learn-more) "\n")])
            number-of-formats (count (re-seq #"%c" warning))]

        (into []
              (concat
               [warning]
               ["color:black;font-weight:bold" "font-weight:normal" "font-weight:bold;color:#ffaa00" "font-weight:normal"]
               (interleave (repeat (/ (- number-of-formats 4) 2) "color:black;font-weight:bold")
                           (repeat (/ (- number-of-formats 4) 2) "color:default;font-weight:normal")))))))

(defn indent-line [msg-type* s]
  #?(:clj
     (let [msg-type (name msg-type*)
           color (case msg-type
                   "rainbow" ansi/bold-yellow
                   "error" ansi/bold-red
                   "warning" ansi/bold-yellow
                   ansi/bold-black)]
       (str (color (str left-border-glyphstring " ")) s))))


(defn body [lines]
  (flatten
   (remove nil?
           (map
            (fn [x]
              (cond
                (= x :br) [""]
                (coll? x) x
                (nil? x)  x
                :else     (str x)))
            (concat [""] lines [""])))))

(defn ansi* [lines border indent-style]
  #?(:clj
     (string/join
      "\n"
      (concat
       [(str "\n\n" border ansi/reset-font)]
       (map (partial indent-line indent-style) (body lines))
       [(str "" border ansi/reset-font "\n\n")]))))

(defn ansi-rainbow [& lines]
  (ansi* lines rainbow-border :rainbow))

(defn ansi-info [& lines]
  (ansi* lines info-border :info))

(defn ansi-error [& lines]
  (ansi* lines error-border :error))

(defn ansi-warning [& lines]
  (ansi* lines warning-border :warning))

(defn ansi-bad-args-warning
  [{:keys [fname invalid-args] :as m}]
  #?(:clj
     (let [fdict (-> fname keyword dict)]
       (when (seq invalid-args)
         (println
          (ansi-warning
           (warning-header m)
           :br
           (warning-call-with-args m)
           :br
           (file-info-str m)
           :br
           (:learn-more fdict)))))))

(defn console-warning-sx
  [m*]
  (let [m (assoc m* :fname "sx")]
    #?(:clj (ansi-bad-args-warning m)
       :cljs (js-warning* m))))

(defn reset-build-states! []
  (reset! state/user-defined-keyframes {})
  (reset! state/garden-vecs-state state/garden-vecs-state-init)
  (reset! state/garden-vecs-state state/garden-vecs-state-init)
  (reset! state/kushi-atomic-user-classes atomic/kushi-atomic-combo-classes)
  (reset! state/atomic-declarative-classes-used #{}))

(defn squiggly [lead focus]
  (str
   (string/join (repeat (count lead) " "))
   (string/join (repeat (count (str (str focus))) "^"))))

(defn plain-text-warning-panel [width & lines]
  (let [horizontal-border (string/join (repeat (/ width 2) "─ "))]
    (str
     "\n\n"
     "┌" horizontal-border
     "\n│\n"
     (string/join "\n" (map #(str "│  " (if (= :br %) " " %)) lines))
     "\n│\n└" horizontal-border "\n\n")))

(defn console-warning-duplicate-ident
  [{:keys [ident] :as m}]
  (println (ansi-error
            (str (ansi/bold "Duplicate prefix+ident combo") ", kushi.core/sx")
            :br
            (str "{... :ident " (ansi/bold ident) " ...}")
            (squiggly "{... :ident "  ident)
            :br
            (file-info-str (assoc m :js? false))
            :br
            "Each prefix+ident combo must be globally unique")))


(defn assert-error-if-duplicate-ident!
  [{:keys [form-meta ident nm selector]}]

  #_(util/pprint+ (str "Checking for " selector " in (:rules @state/garden-vecs-state)\n:rules")
                (:rules @state/garden-vecs-state))

  #_(util/pprint+ (str "(contains? (:rules @state/garden-vecs-state) " selector ")")
                (contains? (:rules @state/garden-vecs-state) selector))

  (when (contains? (:rules @state/garden-vecs-state) selector)
    (console-warning-duplicate-ident {:form-meta form-meta :ident ident})
    (reset-build-states!)

    #_(util/pprint+
     "state/garden-vecs-state should be reset:"
     (:rules @state/garden-vecs-state))

    (let [{:keys [file line column]} form-meta]
      (throw (AssertionError.
              (plain-text-warning-panel
               80
               "Duplicate :ident in supplied attribute map, kushi.core/sx"
               :br
               (str "{... :ident " ident  " ...}")
               (squiggly "{... :ident " ident)
               :br
               (str file ":" line ":" column)
               :br
               "All :prefix + :ident combos must be globally unique"))))))

(defn console-warning-defkeyframes
  [{:keys [nm] :as m}]
  (println (ansi-error
            (ansi/bold "Duplicate keyframe name")
            :br
            (str "(defkeyframe " (ansi/bold nm) " ...)")
            :br
            (file-info-str (assoc m :js? false))
            :br
            "Keyframes names must be globally unique")))

(defn assert-error-if-duplicate-keyframes!
  [{:keys [form-meta nm] :as m}]
  (when (get @state/user-defined-keyframes (keyword nm))
    (do
      (console-warning-defkeyframes m)
      (reset-build-states!)
      (let [{:keys [file line column]} form-meta]
        (throw (AssertionError.
                (plain-text-warning-panel
                 80
                 "Duplicate keyframe name, kushi.core/defkeyframes"
                 :br
                 (str "(defkeyframe " nm " ...)")
                 :br
                 (str file ":" line ":" column)
                 :br
                 "Keyframes names must be globally unique")))
                 ))))

(defn console-warning-defclass-name
  [{:keys [nm] :as m}]
  (println (ansi-error
            (ansi/bold "Duplicate defclass name")
            :br
            (str "(defclass " (ansi/bold (name nm)) " ...)")
            (squiqqly "(defclass " nm)
            :br
            (file-info-str (assoc m :js? false))
            :br
            "defclass names must be globally unique")))


(defn assert-error-if-duplicate-defclass!
  [{:keys [form-meta nm] :as m}]
  (when (get @state/kushi-atomic-user-classes (keyword nm))
    (do
      (console-warning-defclass-name m)
      (reset-build-states!)
      (let [{:keys [file line column]} form-meta]
        (throw (AssertionError.
                (plain-text-warning-panel
                 80
                 "Duplicate defclass name, kushi.core/defclass"
                 :br
                 (str "(defclass " nm " ...)")
                 (squiqqly "(defclass " nm)
                 :br
                 (str file ":" line ":" column)
                 :br
                 "defclass names must be globally unique")))
                 ))))
;; Warnings for kushi.core/defclass   ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn console-warning-defclass
  [m*]
  (let [m (assoc m* :fname "defclass")]
   #?(:clj (ansi-bad-args-warning m)
      :cljs (js-warning* m) )))

;; Warnings for bad numbers   ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn console-warning-number
  [compilation-warnings]
  #?(:clj
     (println
      (string/join
       "\n\n"
       (map #(cond
               (= (:warning-type %) :unitless-number)
               (ansi-warning
                (str (ansi/bold "Invalid value")  " of " (ansi/bold (:numeric-string %)) " for " (ansi/bold (:prop-hydrated %)) " in kushi.core/" (:current-macro %))
                :br
                (str "Did you mean " (:numeric-string %) "px?")))
            compilation-warnings)))
     :cljs
     (string/join
      "\n\n"
      (map #(cond
              (= (:warning-type %) :unitless-number)
              (string/join
               "\n\n"
               [(str "Warning: Invalid value"  " of " (:numeric-string %) " for " (:prop-hydrated %) " in kushi.core/" (name (:current-macro %)))
                (str " Did you mean " (:numeric-string %) "px?\n\n")]))
           compilation-warnings))))


;; Diagnostics   ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn diagnostics
  [kw {:keys [defclass-registered?
              sym
              args
              attr-map
              css-injection-dev
              garden-vecs
              style-is-var?
              ident]}]
  #?(:clj
     (when-let [d (some->> user-config :diagnose (into #{}))]
       (when (let [diagnose-idents (some->> user-config :diagnose-idents (into #{}))
                   winner? (contains? diagnose-idents ident)]
               (or winner? (not diagnose-idents)))
         (case kw
           :defclass
           (when (contains? d :defclass)
             (println
              (str
               "\n\n\n"
               "--(kushi.core/defclass)---------------"
               "\n\n"
               "(defclass " sym " " (string/join " " args) ")"
               "\n"
               "=>"
               "\n"
               attr-map
               "\n\n"
               "(state/add-styles! " garden-vecs ")"
               "\n\n"
               "CSS to be injected for dev preview:"
               "\n"
               css-injection-dev
               "\n\n")))
           :defclass-register
           (when (contains? d :defclass-register)
             (println "Registering defclass" kw "..." (if defclass-registered? "✔" "✘ FAIL")))
           :sx
           (when (contains? d :sx)
             (println
              (str
               "\n\n\n"
               "--(kushi.core/sx)--------------------"
               "\n\n"
               (str (with-out-str (pprint (cons 'sx args))) "")
               "\n=>\n"
               (with-out-str (pprint attr-map))
               (when style-is-var? "\n!!!Warning!!!\nThe above :style value is not necessarily accurate, as a binding was used as a value for :style in the attributes map (the last arg to the sx macro).\n")
               "\n\n"
               (with-out-str (pprint (list 'state/add-styles! garden-vecs)))
               "\n\n"
               "CSS to be injected for dev preview:"
               "\n\n"
               (string/join "\n\n" (map #(string/replace % #"\\n" "\n") css-injection-dev))
               "\n\n"))))))))

