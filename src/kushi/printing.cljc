(ns kushi.printing
  #?(:clj (:require [io.aviso.ansi :as ansi]))
  (:require
   [clojure.string :as string]
   [clojure.pprint :refer [pprint]]
   [par.core :refer [? !? ?+ !?+]]
   [kushi.utils :as util :refer [keyed]]
   [kushi.state :as state]
   [kushi.ansiformat :as ansiformat]
   [kushi.config :refer [user-config version]]))

;; (ansiformat/ansi-format "wtf" :red)

;; Helpers for logging formatting   ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(def ansi-color-map
  #?(:clj {:red          ansi/red
           :magenta      ansi/magenta
           :blue         ansi/blue
           :cyan         ansi/cyan
           :green        ansi/green
           :yellow       ansi/yellow
           :bold-red     ansi/bold-red
           :bold-magenta ansi/bold-magenta
           :bold-blue    ansi/bold-blue
           :bold-cyan    ansi/bold-cyan
           :bold-green   ansi/bold-green
           :bold-yellow  ansi/bold-yellow
           :bold         ansi/bold}))

(defn k->ansi [k]
  (when (keyword? k)
    (k ansi-color-map)))

(defn shift-cycle [vc* i]
  (let [vc (into [] vc*)]
    (into [] (concat (subvec vc i) (subvec vc 0 i)))))

(def rainbow
  #?(:clj
     [ansi/red-font
      ansi/magenta-font
      ansi/blue-font
      ansi/cyan-font
      ansi/green-font
      ansi/yellow-font]))

(def rainbow2
  #?(:clj
     [ansi/red
      ansi/magenta
      ansi/blue
      ansi/cyan
      ansi/green
      ansi/cyan
      ansi/blue
      ansi/magenta]))

(def bold-rainbow
  #?(:clj
     [ansi/bold-red-font
      ansi/bold-magenta-font
      ansi/bold-blue-font
      ansi/bold-cyan-font
      ansi/bold-green-font
      ansi/bold-yellow-font]))

(def bold-rainbow2
  #?(:clj
     [ansi/bold-red
      ansi/bold-magenta
      ansi/bold-blue
      ansi/bold-cyan
      ansi/bold-green
      ansi/yellow]))

(def warning-stripes
  #?(:clj
     [ansi/bold-yellow-font
      ansi/bold-black-font]))

(def warning-stripes2
  #?(:clj
     [ansi/bold-yellow
      ansi/bold-black]))

(def error-stripes
  #?(:clj
     [ansi/bold-red-font
      ansi/bold-white-font]))

(def error-stripes2
  #?(:clj
     [ansi/bold-red
      ansi/bold-white]))

(def info-dots
  #?(:clj
     [ansi/bold-black-font]))

(def yellow-tape
  #?(:clj
     [ansi/bold-yellow-font]))

(defn str-insert
  "Insert c in string s at index i."
  [s c i]
  (str (subs s 0 i) c (subs s i)))

(defn border*
  ([theme n s]
   (str " " (apply str
                   (interpose s (take n (cycle theme)))))))

(def border-length 18)

(def warning-border
  (border* warning-stripes border-length "---"))

(def error-border
  (border* error-stripes border-length "***"))

(def info-border
  (border* info-dots border-length "..."))

(def rainbow-border
  (border* bold-rainbow border-length "..."))

(defn closest-number
  ([n m]
   (closest-number n m nil))
  ([n m k]
   #?(:clj (let [prev  (- n (rem n m))
                 next  (+ n (- m (rem n m)))
                 prevd (Math/abs (- n prev))
                 nextd (Math/abs (- n next))
                 ret   (cond
                         (= k :up)
                         next
                         (= k :down)
                         prev
                         :else (if (<= prevd nextd) prev next))]
             ret))))

(defn border-seq->styled-string
  [{:keys [border-seq border-width cyc top? bottom?] :as m}]
  ;; #_(?+ "border-gen" m)
  (string/join
   (let [adjusted-border-width (Math/round (float (/ border-width (count border-seq))))]
     (if cyc
       (map #(% (apply str border-seq)) (take adjusted-border-width cyc))
       (apply str (repeat adjusted-border-width (apply str border-seq)))))))

(defn panel-border-top
  [{:keys [border-width
           header
           header-color
           header-width
           header-weight
           post-header-width
           theme
           border-seq
           border-tl-string
           indent]
    :as   m}]
  #?(:clj
     (apply str
            (concat
             ["\n\n"
              ((if theme (nth theme 0) str) border-tl-string)]
             (let [chars (take (dec indent) (cycle border-seq))]
               (if theme
                 (map-indexed (fn [idx char] ((nth theme idx) char)) chars)
                 (apply str chars)))
             (when header [" " ((if (= :bold header-weight) ansi/bold str) ((or header-color str) header)) " "])
             [(border-seq->styled-string
               (assoc m
                      :top? true
                      :border-width post-header-width
                      :cyc (when theme (take border-width (cycle theme)))))]))))

(defn panel-border-bottom
  [{:keys [color-cycle border-width border-bl-string theme header]
    :as   m}]
  #_(? "color-cycle" color-cycle)
  #?(:clj
     (str
      "\n"
      ((last color-cycle) border-bl-string)
      (border-seq->styled-string
       (assoc m
              :bottom? true
              :border-width ((if header dec inc) border-width)
              :cyc (when theme (take border-width (cycle (reverse (shift-cycle color-cycle 1)))))))
      "\n\n")))

(defn squiggly-underline [q s]
  #?(:clj
     (ansi/bold-yellow (str " " (string/join (repeat (+ (if q 2 0) (count (str s))) "^"))))))

(defn js-fmt-args
  [{:keys [invalid-args styles-argument-display fname]}]
  (let [map-mode? (and (:map-mode? user-config) (contains? #{"sx" "defclass"} fname))
        args      (cond-> styles-argument-display map-mode? first)
        num-args  (count args)]
    (map-indexed (fn [idx %]
                   (let [bad? (contains? (into #{} invalid-args) %)
                         q    (when (string? %) "\"")
                         ocb? (and map-mode? (zero? idx))
                         ccb? (and map-mode? (= (inc idx) num-args))
                         sp   (str " " (when-not ocb? (when map-mode? " ")))
                         lips (str (when ocb? "{") "...")
                         frmt (str
                               (when ocb? "{")
                               (if bad?
                                 (str "%c" q (if map-mode? (str (first %) " " (second %)) %) q "%c")
                                 lips)
                               (when ccb? "}"))]
                     (str (if bad? (str "\n" "%c%c") "\n") sp frmt)))
                 args)))

(defn format-wrap
  [{:keys [s style-key js? plain?]}]
  (if plain?
    s
    (let [browser #(str "%c" % "%c")]
      #?(:clj (if js?
                (browser s)
                ((or (k->ansi style-key) str) s))
         :cljs (browser s)))))


(defn warning-call-classname
  [{:keys [defclass-name invalid-args]}]
  (when defclass-name
    (if (some-> invalid-args meta :classname)
      (format-wrap (first invalid-args))
      (name defclass-name))))


(defn bad-sx-args-fmt
  [opts-base acc arg3]
  (let [[[idx style-k] v] arg3
        v+      (if (string? v) (str "\"" v "\"") v)
        bad-val (cond
                  style-k
                  (let [style-map-indent "          "]
                    (concat [" {..." "  :style {..."]
                            (map (fn [[cssprop cssval]]
                                   (format-wrap
                                    (assoc opts-base
                                           :s
                                           (str style-map-indent cssprop " " cssval))))
                                 v+)
                            [(str style-map-indent "...}")]
                            ["  ...}"]))
                  :else
                  (format-wrap (assoc opts-base :s (str " " v+))))]
    (assoc acc idx bad-val)))

(defn reduce-bad-args [args invalid-args opts-base]
  (!?+ (keyed args invalid-args opts-base))
  (reduce (partial bad-sx-args-fmt opts-base)
          (into [] (repeat (count args) " ..."))
          invalid-args))


(defn console-error-ansi-formatting
  [{:keys [fname
           invalid-args
           js?
           args]
    :as m}]
  (!?+ :console-error-ansi-formatting:m m)
  (when (seq invalid-args)
    (let [classname (warning-call-classname m)
          opts-base {:js? js? :style-key :bold}
          args-display  (flatten (reduce-bad-args args invalid-args opts-base))
          lines     (into []
                          (remove nil?
                                  (concat
                                   [(str "(" fname (when classname (str " " classname)))]
                                   args-display)))]
      #_(? :display args-display)
      #_(? :ansi-formatting lines)
      lines)))

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
(def indent-num 2)
(defn indent-line [msg-type* s]
  #?(:clj
     (let [msg-type (name msg-type*)
           color (case msg-type
                   "rainbow" ansi/bold-yellow
                   "error"   ansi/bold-red
                   "warning" ansi/bold-yellow
                   ansi/bold-black)]
       (str (color (str left-border-glyphstring (string/join (repeat indent-num " ")))) s))))

(defn pluralize
  ([s coll]
   (pluralize s coll nil nil))
  ([s coll singular-suffix plural-suffix]
    (str s (if-not (coll? coll)
             "s"
             (if (< 1 (count coll))
               (or plural-suffix "s")
               singular-suffix)))))

(defn pluralize2
  ([s coll]
   (pluralize2 s coll nil nil))
  ([s coll singular-suffix plural-suffix]
   (if-not (coll? coll)
     (str s "s")
     (let [n (count coll)
           plural? (< 1 n) ]
       (str s
            (if plural? (or plural-suffix "s") singular-suffix)
            (when plural? (str " (" n ")")))))))

(defn warning-header
  [{:keys [invalid-args fname js?] :as m}]
  (let [s (pluralize2 "Invalid argument" invalid-args)
        opts {:style-key :bold :s s}
        opts-js (assoc opts :js? true)
        desc #(str % " to kushi.core/" fname)]
   #?(:clj
      (if js?
        (desc (format-wrap opts-js))
        (desc (format-wrap opts)))
      ;; delete this branch?
      :cljs
      (str "%cInvalid argument" (when (< 1 (count invalid-args)) "s") "%c"  " to kushi.core/" fname "."))))


(defn warning-call-with-args
  [{:keys [fname classname js?] :as m}]
  #?(:clj
     (do
       ;; TODO get this same so single function can be used
       #_(? "m" m)
       #_(? "ansi-version" (console-error-ansi-formatting m))
       #_(? "js-fmt-args" (js-fmt-args m))
       (let [lines (console-error-ansi-formatting m)]
         (if js? (string/join "\n" lines) lines)))
     :cljs
     (str "(" fname " "
          (when classname (name classname))
          (string/join (js-fmt-args m))
          ")")))

(defn file-info-str
  [{:keys [js? form-meta plain?]
    :as m}]
  #?(:clj
     (let [{:keys [file line column]} form-meta
           opts                       {:js?       js?
                                       :style-key :bold
                                       :s         (str line ":" column)
                                       :plain?    plain?}]
       (str file ":"  (format-wrap opts)))))

(defn bad-arg-warning-body [{:keys [js?] :as m}]
  (!?+ m)
  [(warning-header m)
   (if js? "\n\n" :br)
   (warning-call-with-args m)
   (if js? "\n\n" :br)
   (file-info-str m)])

(defn browser-formatted-js-vec [warning]
  (let [number-of-formats (count (re-seq #"%c" warning))
        fmttrs            #(repeat (/ number-of-formats 2) %)
        formatting-arg    (interleave (fmttrs "color:black;font-weight:bold")
                                      (fmttrs "color:default;font-weight:normal"))]
    (into [] (concat [warning] formatting-arg))))

(defn preformat-js-warning
  [{:keys [invalid-args] :as m}]
  #?(:clj
     (when (and (vector? invalid-args)
                (seq invalid-args))
       (!?+ :m1 m)
       (let [m       (assoc m :js? true)
             warning (str (string/join "\n\n" (bad-arg-warning-body m)) "\n")]
         (?+ :m2 m)
         (browser-formatted-js-vec warning)))))

(defn ansi-bad-args-warning
  [{:keys [fname invalid-args] :as m}]
  #_(? "m" m)
  #_(? (str "(empty? " invalid-args ")") (empty? invalid-args))
  (when-not (empty? invalid-args)
    (do
      #?(:clj
         (let [fdict      (-> fname keyword dict)
               body*      (bad-arg-warning-body m)
               body       (interpose "\n" body*)]
           (when (seq invalid-args)
             (println #_(str "bad-args: " fname) (apply ansiformat/warning-panel body))))))))

(defn bad-mods-warning
  [{:keys [fname args bad-mods js?] :as m}]
  #?(:clj
     (do
      ;;  (?+ :printing/bad-mods-warning:m m)
      ;;  (?+ :printing/bad-mods-warning:bad-mods bad-mods)
       (when-not (empty? bad-mods)
         (do
          ;;  (?+ :printing/bad-mods-warning:m m)
           (let [more-than-one-bad-stack?       (> (count bad-mods) 1)
                 first-stack-has-multiple-bads? (> (count (-> bad-mods first second)) 1)
                 opts                           {:style-key :bold
                                                 :js?       js?}
                 plural?                        (or more-than-one-bad-stack?
                                                    first-stack-has-multiple-bads?)
                 desc*                          (str "Bad modifier" (when plural? "s"))
                 desc                           (format-wrap (assoc opts :s desc*))
                 sep                            (if js? "\n\n" :br)
                 bad-mods                       (mapv (fn [[prop-stack mods]]
                                                        (str prop-stack
                                                             "  ->  "
                                                             (format-wrap (assoc opts :s (string/join ", " mods)))))
                                                      bad-mods)
                 ret                           [(str desc " passed to kushi.core/" fname ":")
                                                sep
                                                (if js? (string/join "\n" bad-mods) bad-mods)
                                                sep
                                                (file-info-str m)
                                            ;;  :br
                                            ;;  "See kushi docs #pseudos-and-combo-selectors for more details"
                                                ]]
            ;;  (?+ :printing/bad-mods-warning:ret ret)
             ret))))))

(defn bad-mods-warning-js
  [coll]
  (when (seq coll)
    (let [warning (str (string/join coll) "\n")]
      (browser-formatted-js-vec warning))))

(defn ansi-bad-mods-warning!
  [coll]
  (when (seq coll)
    (println (apply ansiformat/warning-panel coll))))

(defn squiggly [lead focus]
  (str
   (string/join (repeat (count lead) " "))
   (string/join (repeat (count (str (str focus))) "^"))))

(defn plain-text-warning-panel [width & lines]
  (let [horizontal-border (string/join (repeat width "─"))]
    (str
     "\n\n"
     "┌" horizontal-border
     "\n│\n"
     (string/join "\n" (map #(str "│  " (if (= :br %) " " %)) lines))
     "\n│\n└" horizontal-border "\n\n")))

(defn throw-assertion-error!
  ([lines]
   (throw-assertion-error! lines 55))
  ([lines width]
   (throw
    (AssertionError.
     (apply
      plain-text-warning-panel
      (into [width] lines))))))


(defn warning-block
  [{:keys [fname]} lines]
  #?(:clj
     (interpose "\n"
                (concat
                 [" "
                  " "
                  (str (ansi/red "[") (ansi/yellow "WARNING") (ansi/red "]") " kushi.core/" fname)]
                 lines))))

(defn print-dupe2!
  [{:keys [terminal plain simple] :as m}]
  (when (or terminal plain simple)
    (let [throw?      (= :error (:handle-duplicates user-config))
          use-simple? (= :simple (:warning-style user-config))
          panel-fn    (if throw? ansiformat/error-panel ansiformat/warning-panel)
          lines       (cond use-simple? simple
                            throw? plain
                            :else terminal)]
      (if use-simple?
        (apply println (warning-block m lines))
        (println (apply panel-fn lines)))
      (when throw? (throw-assertion-error! lines)))))

(defn dupe-desc [msg {:keys [fname] :as opts}]
  (let [ns-fname  (str "kushi.core/" fname)]
    (str (format-wrap (assoc opts :s msg)))))

(defn dupe-file-info [opts]
  (file-info-str opts))

(defn dupe-ident-body
  [{:keys [ident js? plain? simple?] :as m}]
  (let [opts        (assoc m :style-key :bold :js? js? :plain? plain?)
        desc        (dupe-desc "Duplicate prefix+ident" opts)
        deets       (str "{... :ident " (format-wrap (assoc opts :s ident)) " ...}")
        squiggly    (squiggly "{... :ident "  ident)
        file-info   (dupe-file-info opts)
        hint        "All prefix+ident combos must be globally unique"
        sep         (if js? " " :br)
        lines       (if simple?
                      [desc
                       file-info
                       hint
                       deets
                       squiggly]
                      [desc
                       sep
                       deets
                       squiggly
                       (when-not js? sep)
                       file-info
                       sep
                       hint])]
    lines))

(defn message-lines
  [f m]
  (let [targets  {:terminal nil
                  :browser  :js?
                  :plain    :plain?
                  :simple   :simple?}
        add-line (fn [acc [k v]]
                   (let [opts  (assoc m v true)
                         lines (f opts)]
                     (assoc acc k lines)))
        ret*     (reduce add-line {} targets)
        browser  (let [joined (str (string/join "\n" (remove nil? (:browser ret*))))]
                   (browser-formatted-js-vec joined))
        ret      (assoc ret* :browser browser)
        ]
    ret))


(defn dupe-warning-bindings
  [{:keys [kushi-attr ident form-meta fname nm]
    :as   m}]
  (let [dupe-type          (keyword fname)
        prefix             (or (:kushi-class-prefix kushi-attr) (:kushi-class-prefix user-config))
        file-info          (file-info-str {:form-meta form-meta :plain? true})
        k                  (if (= dupe-type :sx) [prefix ident] (keyword nm))
        existing-file-info (get-in @state/declarations [dupe-type k])]

    (keyed prefix file-info k existing-file-info dupe-type)))

(defn dupe-warning*
  [{:keys [caller
           m
           label
           body-lines-fn
           debug?]
    :as args-map}]
  ;; #_(? (str "dupe-warning for " (or (:selector m) (:fname m))) "hi")
  (let [{:keys
         [file-info
          k
          dupe-type
          existing-file-info]
         :as bindings}        (dupe-warning-bindings m)
        label                 (or label (str (name dupe-type) " name"))
        ]
    #_(?+ bindings)
    (if existing-file-info
      (if-not (= file-info existing-file-info)
        (do
          ;; #_(?+ (str label " already used: ") {k file-info})
          ;; #_(?+ :comment "Returning map of preformatted message-line colls...")
          (message-lines body-lines-fn m))
        (do
          ;; #_(?+ (str label ", non-duplicate: ") file-info)
          ))
      (do
        ;; #_(?+ (str label " not yet used..."))
        ;; #_(?+ "Merging into state/prefixed-selectors:" {k file-info})
        (swap! state/declarations assoc-in [dupe-type k] file-info)
        nil))))

(defn dupe-ident-warning [m]
  (dupe-warning*
   {:m             m
    :caller        (meta #'dupe-ident-warning)
    :label         "prefix+ident combo"
    ;; :debug?        true
    :body-lines-fn dupe-ident-body}))

(defn dupe-warning-body-lines
  [{:keys [fname js? plain?] :as m}]
  (let [opts      (assoc m :style-key :bold :js? js? :plain? plain?)
        desc      (dupe-desc (str "Duplicate " fname " name") opts)
        fn-call   (str "(" fname " ")
        nm        (when-let [nm (:nm opts)] (name nm))
        deets     (str fn-call (format-wrap (assoc opts :s nm)) " ...)")
        squiggly  (squiggly fn-call nm)
        file-info (dupe-file-info opts)
        hint      (str fname " names must be globally unique")
        sep         (if js? " " :br)
        lines       [desc
                     sep
                     deets
                     squiggly
                     (when-not js? sep)
                     file-info
                     sep
                     hint]]
    lines))

(defn dupe-defkeyframes-warning [m]
  (dupe-warning*
   {:m             m
    :caller        (meta #'dupe-defkeyframes-warning)
    :body-lines-fn dupe-warning-body-lines}))

(defn dupe-defclass-warning [m*]
  (let [m m*]
    (dupe-warning*
    {:m             m
     :caller        (meta #'dupe-defclass-warning)
    ;; :debug? true
     :body-lines-fn dupe-warning-body-lines})))


;; Warnings for bad numbers   ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn unitless-number-warning [% {:keys [js?] :as opts}]
  (let [sep (if js? "\n\n" :br)]
    [(str (format-wrap (assoc opts :s "Invalid value"))
          " of "
          (format-wrap (assoc opts :s (:numeric-string %)))
          " for "
          (format-wrap (assoc opts :s (:prop-hydrated %))))
     sep
     (file-info-str opts)
     sep
     (str "Did you mean " (format-wrap (assoc opts :s (str (:numeric-string %) "px?"))))]))

(defn compilation-warnings-coll*
  [warnings opts]
  (mapv
   #(cond
      (= (:warning-type %) :unitless-number)
      (unitless-number-warning % opts))
   warnings))

(defn compilation-warnings-coll
  [opts*]
  (let [warnings @state/compilation-warnings]
    (when (not-empty warnings)
      (let [opts    (assoc opts* :style-key :bold)
            opts-js (assoc opts :js? true)
            f       (partial compilation-warnings-coll* warnings)]
        {:terminal (f opts)
         :browser  (f opts-js)}))))

(defn preformat-compilation-warnings-js
  [{browser-warnings :browser}]
  (when (seq browser-warnings)
    (mapv #(let [warning (str (string/join %) "\n")]
             (browser-formatted-js-vec warning))
          browser-warnings)))

(defn compilation-warnings!
  [coll]
  (doseq [lines coll]
    (println #_"comp-warn" (apply ansiformat/warning-panel lines))))

(defn set-warnings! []
  (let [{:keys [ident]
         :as   opts}              @state/current-macro

        ;; bad-mods                  (bad-mods-warning
        ;;                            (if (some-> @state/current-sx :bad-mods not-empty)
        ;;                              @state/current-sx
        ;;                              opts))

        bad-nums                  (compilation-warnings-coll opts)
        bad-nums-js               (preformat-compilation-warnings-js bad-nums)
        dupe-ident                (when ident (dupe-ident-warning opts))
        invalid-style             (when @state/invalid-style-args
                                    (merge opts {:invalid-args (!?+ @state/invalid-style-args)}))
        invalid-style-js          (when invalid-style
                                    (browser-formatted-js-vec
                                     (string/join
                                      (bad-arg-warning-body
                                       (assoc invalid-style :js? true)))))
        warnings-terminal         {
                                  ;;  :bad-mods      bad-mods
                                   :bad-nums      (:terminal bad-nums)
                                   :dupe-ident    dupe-ident
                                   :invalid-style (when invalid-style
                                                    (bad-arg-warning-body invalid-style))}
        warnings-js               (into []
                                        (concat
                                         bad-nums-js
                                         [(:browser dupe-ident)]
                                         [invalid-style-js]))]
    (reset! state/warnings-terminal warnings-terminal)
    (reset! state/warnings-js warnings-js)
    (reset! state/invalid-style-warnings warnings-terminal)))

(defn print-warnings! []
  #_(? :print-warnings @state/warnings-terminal)
  (let [warnings (util/filter-map @state/warnings-terminal (fn [_ v] (not-empty v)))]
    #_(? :warnings warnings)
    (doseq [[warning-type warning-or-warnings] warnings]
      (let [printing-opts @state/current-macro]
        #_(? (keyed warning-type warning-or-warnings printing-opts))
        (when warning-or-warnings
          #_(? :print-warnings:inner (keyed warning-type warning-or-warnings))
          (case warning-type
            :bad-nums      (doseq [warning warning-or-warnings]
                             (println (apply ansiformat/warning-panel warning)))
            :dupe-ident    (print-dupe2! (merge warning-or-warnings printing-opts))
            :invalid-style (println (apply ansiformat/warning-panel warning-or-warnings))
            nil)))))
  (reset! state/compilation-warnings [])
  (reset! state/invalid-style-warnings []))

;; Diagnostics   ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

;; TODO Refactor this

(defn diagnostics
  [kw {:keys [defclass-registered?
              sym
              args
              attr-map
              css-injection-dev
              garden-vecs
              style-is-var?
              ident
              extra ]}]
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
               "\n\n"
               (with-out-str (pprint extra))
               "\n\n"))))))))


(defn simple-warning [{:keys [desc args hint]}]
  (let [{:keys [fname form-meta]} @state/current-macro
        fstr                      (str "kushi.core/" fname)]
    (println
     (str ansi/bold-magenta-font
          "\nWARNING! "
          ansi/reset-font
          ansi/bold-font
          fstr
          ansi/reset-font
          "\n"
          desc
          (when hint (str "\n" hint))
          "\n"
          (string/replace (with-out-str (pprint (cons (symbol fname) args))) #"\n$" "")
          "\n"
          ansi/italic-font
          ansi/cyan-font
          (file-info-str {:plain? true :style-key :italic :form-meta form-meta})
          ansi/reset-font
          "\n"))))
