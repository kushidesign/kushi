(ns kushi.core
  (:require
   [clojure.string :as string]
   [domo.core :as domo]

   ;; Should these go somewhere else?
   [kushi.css.build.css-reset]
   [kushi.css.build.kushi-ui-component-theming]

   ;; for testing
  ;;  [taoensso.tufte :as tufte :refer [p profile]]
   )
 (:require-macros [kushi.core]))


(defn ^:public class-str
  "Takes a coll of class strings or keywords, at least one of which is a
   dynamic runtime binding, and joins them into a string with each class
   separated by a space. Converts keywords to string and strips off any leading
   dot chars."
  [classes]
  (string/join " "
               (keep #(when (or (keyword? %) (string? %))
                        (let [s (name %)]
                          (if (string/starts-with? s ".")
                            (subs s 1)
                            s)))
                     classes)))

;; TODO - move into macro-land?
(defn grid-template-areas
  "Use like this:
   (kushi/grid-template-areas
    \"brc br b  bl blc\"
    \"rt  .  .  .  lt\"
    \"r   .  .  .  l\"
    \"rb  .  .  .  lb\"
    \"trc tr t  tl tlc\")"
  [& rows]
  (string/join " " (map #(str "\"" % "\"") rows)))


;; TODO - Remove this and use the one from domo.core on next version of domo
(defn token->ms
  "Expects a key or string which maps to an existing design token (css custom
   property). If the value of the token is a valid (css) microseconds or seconds
   unit, an integer representing the number of microseconds will be returned."
  [x]
  (when-let [s (and (or (string? x) (keyword? x))
                    (let [nm (name x)]
                      (when (re-find #"^--\S+$" nm)
                        (some-> nm domo/css-custom-property-value))))]
    (let [[_ ms]   (some->> s (re-find #"^([0-9]+)ms$"))
          [_ secs] (some->> s (re-find #"^([0-9]+)s$"))
          n        (or ms (some-> secs (* 1000)))
          ret      (some-> n js/parseInt)]
      `~ret)))

;; -----------------------------------------------------------------------------
;; Functionality for kushi style decoration
;; -----------------------------------------------------------------------------

(def dom-element-events
  [:on-change
   :on-blur
   :on-aux-click
   :on-click
   :on-composition-end
   :on-composition-start
   :on-composition-update
   :on-context-menu
   :on-copy
   :on-cut
   :on-dbl-click
   :on-error
   :on-focus
   :on-focus-in
   :on-focus-out
   :on-fullscreen-change
   :on-fullscreen-error
   :on-key-down
   :on-key-up
   :on-mouse-down
   :on-mouse-enter
   :on-mouse-leave
   :on-mouse-move
   :on-mouse-out
   :on-mouse-over
   :on-mouse-up
   :on-paste
   :on-scroll
   :on-security-policy-violation
   :on-select
   :on-touch-cancel
   :on-touch-end
   :on-touch-move
   :on-touch-start
   :on-webkit-mouse-force-down
   :on-wheel])

;; TODO fix warning to be less confusing and use bling
(defn- merge-attrs-warning
  [v k n]
  (js/console.warn
   (str
    "kushi.core/merge-attrs:\n\n "
    "The " k " value supplied in the " n " argument must be a map.\n\n "
    "You supplied:\n") v))

(defn- bad-style? [style n]
  (let [bad? (and style (not (map? style)))]
    (when bad? (merge-attrs-warning style :style n))
    bad?))

(defn- bad-class? [class n]
  (let [bad? (and class
                  (not (some #(% class)
                             [seq? vector? keyword? string? symbol?])))]
    (when bad? (merge-attrs-warning class :class n))
    bad?))

(defn- class-coll
  [class bad-class?]
  (when-not bad-class?
    (if (or (string? class) (keyword? class))
      [class]
      class)))

;; (defn- data-sx-str [m]
;;   (when m
;;     (let [{:keys [file line column]} m]
;;       (str file ":"  line ":" column))))

;; (defn- data-sx [m2 s1 s2]
;;   (let [from-defcom (data-sx-str (:data-amp-form m2))
;;         from-user   (data-sx-str (:data-amp-form2 m2))
;;         coll   (remove nil? [from-defcom from-user s1 s2])
;;         joined (when (seq coll) (string/join " + " coll))]
;;     (when joined {:data-sx joined})))

(defn- handler-concat [c1 c2 m2 k]
  (let [block? (contains? (some->> m2 :data-kushi-block-events (into #{})) k)
        f (if block?
            c2
            (if (and c1 c2)
              (do
                ;; (println "merging 2 event handlers")
                ;; (println m2)
                (fn [e] (c1 e) (c2 e)))
              (or c1 c2)))]
    (when f {k f})))


;; TODO - can you avoid bad-style warnings by surfacing it at compile time?
;; Or collecting warnings and surfacing them at end of fn?
(defn- merge-attrs* [& maps]
  (let [[m1 m2]                   (map #(if (map? %) % {}) maps)
        {style1 :style
         class1 :class
        ;;  data-sx1 :data-sx
         }                        m1
        {style2 :style
         class2 :class
        ;;  data-sx2 :data-sx
         }                         m2
        [bad-style1? bad-style2?] (map-indexed (fn [i x] (bad-style? x i))
                                               [style1 style2])
        [bad-class1? bad-class2?] (map-indexed (fn [i x] (bad-class? x i))
                                               [class1 class2])
        merged-style              (merge (when-not bad-style1? style1)
                                         (when-not bad-style2? style2))
        class1-coll               (some-> class1 (class-coll bad-class1?))
        class2-coll               (some-> class2 (class-coll bad-class2?))
        classes                   (concat class1-coll class2-coll)
        ;; data-sx                   (data-sx m2 data-sx1 data-sx2)
        
        m2-keys (into #{} (keys m2))

        user-event-handlers       (when-not (contains? #{#{:style}
                                                         #{:class}
                                                         #{:style :class}
                                                         #{}}
                                                       m2-keys)
                                    (keys (select-keys m2 dom-element-events)))

        merged-event-handlers     (some->> user-event-handlers
                                           (map #(handler-concat (% m1) 
                                                                 (% m2)
                                                                 m2
                                                                 %))
                                           (apply merge))

        ;; m2-                       (dissoc m2 :data-amp-form)
        
        ret                       (assoc (merge m1
                                                m2
                                                ;; m2-
                                                ;; data-sx
                                                merged-event-handlers)
                                         :class classes
                                         :style merged-style)]
    ret))

;; Slimmed version
;; (defn- merge-attrs*SLIM [& maps]
;;   (let [[m1 m2]               (map #(if (map? %) % {}) maps)
;;         merged-style          (merge (:style m1) (:style m2))
;;         class1-coll           (some-> (:class m1) (class-coll false))
;;         class2-coll           (some-> (:class m2) (class-coll false))
;;         classes               (concat class1-coll class2-coll)
;;         m2-keys               (into #{} (keys m2))
;;         user-event-handlers   (when-not (contains? #{#{:style}
;;                                                      #{:class}
;;                                                      #{:style :class}
;;                                                      #{}}
;;                                                    m2-keys)
;;                                 (keys (select-keys m2 dom-element-events)))
;;         merged-event-handlers (some->> user-event-handlers
;;                                        (map #(handler-concat (% m1) 
;;                                                              (% m2)
;;                                                              m2
;;                                                              %))
;;                                        (apply merge))
;;         ret                   (assoc (merge m1
;;                                             m2
;;                                             merged-event-handlers)
;;                                      :class classes
;;                                      :style merged-style)]
;;     ret))

;; (tufte/add-basic-println-handler! {})

;; Public function for style decoration
(defn merge-attrs [& maps]

;; Testing
;; (profile ; Profile any `p` forms called during body execution
;;   {} ; Profiling options; we'll use the defaults for now
;;   (dotimes [_ 10000]
;;     (p :a (reduce merge-attrs*OLD maps))
;;     (p :b (reduce merge-attrs* maps))))

  (reduce merge-attrs* maps))

