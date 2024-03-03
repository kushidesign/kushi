(ns ^:dev/always kushi.stylesheet
  (:require
   [clojure.string :as string]
   [clojure.java.io :as io]
   [garden.stylesheet]
   [garden.core :as garden]
   [kushi.config :refer [user-config
                         user-css-file-path
                         version]]
   [kushi.state2 :as state2]
   [kushi.log :as kushi.log]
   [kushi.utils :as util :refer [keyed]]
   [kushi.defs :refer [rule-type-report-order token-types]]
   [kushi.reporting :as reporting]))


(def license-comment-header
  (str "/*! kushi v" version " | EPL License | https://github.com/kushidesign/kushi */"))


(defn add-utility-class?
  [m]
  (let [selector    (-> m :selector :selector*)
        registered? (contains? @state2/registered-shared-classes selector)
        always-add? (some #(re-find % selector) (:kushi-utility-classes-to-always-add user-config))]
    (or registered? always-add?)))

(defn keep-garden-vecs [chunk]
  (let [ret (reduce (fn [acc {gv  :garden-vecs
                              :as m} ]
                      (let [matching-chunk?           (= (:kushi/chunk m) chunk)
                            non-empty-matching-chunk? (and matching-chunk? (seq gv))
                            utility?                  (contains? #{:kushi/utility :kushi/utility-override} chunk)]
                        (if non-empty-matching-chunk?
                          (if utility?
                            (if (or (not (:elide-unused-kushi-utility-classes? user-config))
                                    (add-utility-class? m))
                              (conj acc gv)
                              acc)
                            (conj acc gv))
                          acc)))
                    []
                    @state2/css)]
    (distinct ret)))

(defn garden-vecs-injection
  [garden-vecs]
  (->> garden-vecs
       (map (fn [v]
              {:garden-vec v
               :rule-css   (garden.core/css {:pretty-print? false} v)}))
       (remove (fn [{x :garden-vec}] (and (vector? x) (nil? (second x)))))
       (map :rule-css)
       (remove string/blank?)
       (into [])))

(defn append-css-chunk!
  [{:keys [css-text
           comment
           content]}]
  (let [cmnt (when comment (str "\n\n/* " comment " */\n\n"))
        print? (and content (not (string/blank? content)))
        content (str cmnt content)]
    (when print?
      (reset! css-text (str @css-text "\n" content))) ))


(defn design-tokens-css
  [{:keys [pretty-print?]}
   toks]
  (when (:add-design-tokens? user-config)
    (let [gvecs (->> toks
                     (mapv (fn [[prop val]]
                             (let [prop+ (if (util/nameable? prop)
                                           (keyword (string/replace (name prop) #"^\$" "--"))
                                           prop)]
                               {prop+ (util/maybe-wrap-cssvar val)})))
                     (cons (or (when-let [selector* (:design-tokens-root user-config)]
                                 (name selector*))
                               ":root"))
                     (into [])
                     vector)]
      (garden/css {:pretty-print? pretty-print?} gvecs))))


(defn defkeyframes->css [[nm frames]]
  (str "@keyframes "
       (name nm)
       " {\n"
       (garden.core/css frames)
       "\n}\n"))

(defn font-face->css
  [{:keys [css-rule :kushi/process]}]
  (when (and css-rule
             (= process :kushi.core/add-font-face)
             (string? css-rule))
    css-rule))

(defn number-of-rules [f coll]
  (if (seq coll)
    (if (= f font-face->css)
      (reduce (fn [acc {:keys [css-rule]}]
                (let [num-font-face-rules (cond
                                            (string? css-rule)
                                            1
                                            (seq css-rule)
                                            (count css-rule)
                                            :else
                                            0)]
                  (+ acc num-font-face-rules)))
              0
              coll)
      (count coll))
    0))

(defn append-css!*
  [{:keys [css-text to-be-printed]}
   {:keys [coll k comment map-fn content]
    :as   m}]
  (let [num    (number-of-rules map-fn coll)
        f       (or map-fn identity)
        content (or content
                    (->> coll
                         (map f)
                         (string/join "\n")))]

    (swap! to-be-printed assoc k num)
    (when (pos-int? num)
      (append-css-chunk!
       (keyed css-text comment content) ))))


(defn append-from-gv!*
  [{:keys [css-text to-be-printed pretty-print?] :as m1}
   {:keys [config-key to-be-printed-log-key :kushi/chunk comment] :as m2}]
  (when (config-key user-config)
    (let [rules              (keep-garden-vecs chunk)
          content            (garden/css {:pretty-print? pretty-print?}
                                         rules)
          to-be-printed-key* (or to-be-printed-log-key
                                 (when (some-> config-key
                                               name
                                               (string/starts-with? "add-"))
                                   (-> config-key
                                       name
                                       (subs 4)
                                       keyword))
                                 config-key)
          to-be-printed-key  (->> to-be-printed-key*
                                  name
                                  drop-last
                                  (apply str)
                                  keyword)
          num-rules          (count rules)
          num-rules+         (reduce (fn [acc coll] (+ acc (count coll)) ) 0 rules)]

      ;; (when (= :kushi/theme chunk)
      ;;   (println (keyed rules to-be-printed-key num-rules)))

      ;; (when (= :kushi/utility chunk)
      ;;   (println (keyed rules to-be-printed-key num-rules)))

      ;; (when (= :kushi/utility-override chunk)
      ;;   (println (keyed rules to-be-printed-key num-rules)))

      (swap! to-be-printed assoc to-be-printed-key num-rules+)

      ;; (when true (= :kushi.core/defclass-override chunk)
      ;;         (println "\n" chunk to-be-printed-key* to-be-printed-key*)
      ;;         (println  @to-be-printed)
      ;;         (println "\n"))

      (append-css-chunk! (keyed comment content css-text)))))


(defn create-css-text
  ([]
   (create-css-text nil))
  ([caller]
   #_(println "\n"
              (str "kushi.stylesheet/create-css-text from "
                   (or caller "kushi.stylesheet/create-css-file"))
              "\n"
              "(count @state2/css) => " (count @state2/css))
   (let [pretty-print?   true
         printables      (atom [])
         to-be-printed   (atom {})
         css-text        (atom license-comment-header)
         m               {:css-text      css-text
                          :pretty-print? pretty-print?
                          :printables    printables
                          :to-be-printed to-be-printed}
         append-from-gv! (partial append-from-gv!* m)
         append-css!     (partial append-css!* m)]

    ;; css-reset
     (append-from-gv! {:config-key  :add-css-reset?
                       :kushi/chunk :kushi/css-reset
                       :comment     (str "CSS Reset rules via:\n"
                                         "The new CSS reset - version 1.6.0 (last updated 29.4.2022)\n"
                                         "GitHub page: https://github.com/elad2412/the-new-css-reset")})

    ;; font-face
     (append-css! {:k       :font-face
                   :coll    @state2/user-defined-font-faces
                   :map-fn  font-face->css
                   :comment "Font faces"})


    ;; design-tokens
     (when (:add-design-tokens? user-config)
       (append-css! (let [coll @state2/design-tokens]
                      {:k       :design-tokens
                       :coll    coll
                       :comment "Design tokens"
                       :content (design-tokens-css m coll)}))

       (append-css! (let [coll @state2/theming-tokens]
                      {:k       :theming-tokens
                       :coll    coll
                       :comment "theming design tokens"
                       :content (design-tokens-css m coll)}))

       (append-css! (let [coll @state2/used-tokens]
                      {:k       :used-tokens
                       :coll    coll
                       :comment "used design tokens"
                       :content (design-tokens-css m coll)})))


    ;; keyframes
     (append-css! {:k       :keyframes
                   :coll    @state2/user-defined-keyframes
                   :map-fn  defkeyframes->css
                   :comment "Animation Keyframes"})

    ;; kushi.ui theming classes
     (append-from-gv! {:config-key  :add-kushi-ui-theming?
                       :kushi/chunk :kushi/theme
                       :comment     "kushi.ui component theming rules"})

     (append-from-gv! {:config-key  :add-kushi-ui-theming-defclass?
                       :kushi/chunk :kushi/kushi-ui-defclass
                       :comment     "kushi.ui component theming rules (shared, via defclass or defclass-with-override)"})

    ;; kushi base utility classes
     (append-from-gv! {:config-key  :add-kushi-defclass?
                       :kushi/chunk :kushi/utility
                       :comment     "Base kushi utility classes"})

    ;; user shared classes
     (append-from-gv! {:config-key  :add-user-defclass?
                       :kushi/chunk :kushi.core/defclass
                       :comment     "User-defined shared classes (via defclass)"})

    ;; user sx classes
     (append-from-gv! {:config-key  :add-user-sx?
                       :kushi/chunk :kushi.core/sx
                       :comment     "User styles generated from kushi.core/sx"})

    ;; kushi base utility classes, overrides
     (append-from-gv! {:config-key    :add-kushi-defclass-overrides?
                       :kushi/chunk   :kushi/utility-override
                       :comment       "Kushi base utility classes, override versions"
                       :override?     true})

    ;; user shared classes, overrides
     (append-from-gv! {:config-key    :add-user-defclass-overrides?
                       :kushi/chunk   :kushi.core/defclass-override
                       :comment       "User-defined shared classes, override versions (via defclass-with-override)"
                       :override?     true})

     (reset! state2/->css @css-text)
     (reset! state2/->css-to-be-printed-previously @state2/->css-to-be-printed)
     (reset! state2/->css-to-be-printed @to-be-printed))))

(defn to-be-printed+ [to-be-printed]
  (let [with-diffs     (into {}
                             (map (fn [[k current-count]]
                                    (let [previous-count (k @state2/->css-to-be-printed-previously)
                                          ndiff          (when (every? int? [current-count previous-count])
                                                           (- current-count previous-count))
                                          tup            [current-count ndiff]]
                                      [k tup]))
                                  @to-be-printed))
        num-rules      (->> (apply dissoc (concat [@to-be-printed] token-types))
                            vals
                            (remove #(not (int? %)))
                            (apply +))
        num-tokens     (->> (select-keys @to-be-printed token-types)
                            vals
                            (remove #(not (int? %)))
                            (apply +))
        to-be-printed+ (mapv (fn [k] [k (k with-diffs)])
                             rule-type-report-order)]
    (keyed with-diffs
           to-be-printed+
           num-rules
           num-tokens)))


(defn add-stylesheet? []
  (or (and (:add-stylesheet-dev? user-config)
           @state2/KUSHIDEBUG)
      (and (:add-stylesheet-prod? user-config)
           (not @state2/KUSHIDEBUG))))


;; Used for build hook
(defn create-css-file
  {:shadow.build/stage :compile-finish}
  [build-state]

  (create-css-text "kushi.stylesheet/create-css-file")

  (let [to-be-printed                                        state2/->css-to-be-printed
        zero-total-rules?                                    (nil? (some #(not (zero? %)) (vals @to-be-printed)))
        something-to-write?                                  (not zero-total-rules?)
        caching?                                             (true? (:caching? user-config))
        [cache-will-update?
         cache-diff-count
         diff-callsites] (when caching?
                           (let [{:keys [equal? diff-count diff-callsites]} (state2/cache-is-equal?)]
                             (state2/write-cache! equal?)
                             [(not equal?) diff-count diff-callsites]))
        {:keys [to-be-printed+
                num-rules
                num-tokens]}        (to-be-printed+ to-be-printed)]
    #_(prn
       (keyed
        ;;  cache-will-update?
        ;;  @to-be-printed
        ;;  to-be-printed
        ;;  to-be-printed+
        ;;  num-rules
        ;;  previously-printed
        ))

    (when (and (add-stylesheet?) something-to-write?)
      (use 'clojure.java.io)
      (spit user-css-file-path @state2/->css :append false))

    (when (and (:log-build-report? user-config)
               something-to-write?)
      (reporting/print-report! (assoc (keyed to-be-printed
                                             build-state
                                             cache-will-update?
                                             to-be-printed+
                                             num-rules
                                             num-tokens
                                             cache-diff-count
                                             diff-callsites)
                                      :kushi-cache-path
                                      state2/kushi-cache-path
                                      :initial-build?
                                      @state2/initial-build?))))

   ;; Last, reset build states for subsequent builds at dev
  (state2/reset-build-states!)

  (kushi.log/reset-log-states!)
  ;; Must return the build state
  build-state)


