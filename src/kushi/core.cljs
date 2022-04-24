(ns ^:dev/always kushi.core
  (:require-macros [kushi.core :refer [keyed sx theme! #_ui-components!]])
  (:require [clojure.string :as string]
            [kushi.clean :as clean]
            [kushi.sheets :as sheets]
            ;; [goog.object :as obj]
            [kushi.utils :as util] ;; For aliasing merge-with-style
            [par.core :refer [? !?]] ;; only use when developing kushi itself
            ))

(defn insert-style-tag!
  [id]
  (when-not (js/document.getElementById id)
    (let [head (or js/document.head
                   (-> (js/document.getElementsByTagName "head")
                       (aget 0)))
          tag (js/document.createElement "style")]
      (.appendChild head tag)
      (doto tag
        (.setAttribute "type" "text/css")
        (.setAttribute "id" id)))))

(defn initialize-style-tags!
  []
  (doseq [kw sheets/sheet-types-ordered ]
    (insert-style-tag! (kw sheets/sheet-ids-by-type))))

(initialize-style-tags!)

;; (ui-components!)

;; Functionality for injecting styles into during development builds  ------------------------------------------
;; This is also used in release builds if :runtime-injection? config param is set to true ----------------------

;; Used to keep track of what has been injected, to avoid duplicate injections.
(def injected (atom #{}))

;; Flushes #_kushi-rules-shared_ or #_kushi-rules_ stylesheets during development builds.
#_(def sheet-ids-by-type
  {:global-tokens       "_kushi-rules-global-tokens_"
   :kushi-atomic            "_kushi-rules-utility_"
   :defclass                "_kushi-rules-shared_"
   :theme                   "_kushi-rules-theme_"
   :sx                      "_kushi-rules_"
   :defclass-kushi-override "_kushi-rules-overrides_"
   :defclass-user-override  "_kushi-rules-user-overrides_"})

(clean/clean! (vals sheets/sheet-ids-by-type))

;; Toggle for debugging while developing kushi itself
(def log-inject-css*? false)

(defn lightswitch! []
  (.toggle (-> js/document.body .-classList) "dark"))

(defn sanitize-if-malformed-garden-output [s]
  (if (re-find #"calc\(" s)
    (-> s
        (string/replace #"\) *(\*|\+|\-|\\/) *\(" ") $1 (")
        (string/replace #"(\S)(\+)(\S)" "$1 $2 $3"))
    s))

(defn inject-css*
  "Called internally by kushi.core/sx at dev/run time for zippy previews."
  [css-rules
   sheet-id]
  (!? :inject-css* css-rules)
  (when-let [stylesheet-el (js/document.getElementById sheet-id)]
    (let [;log-inject-css*? (= sheet-id "_kushi-rules_")
          rules-as-seq   (map-indexed vector css-rules)
          sheet          (.-sheet stylesheet-el)
          num-injected   (count @injected)
          sheet-len      (.-length (.-rules sheet))]
     ;; (when (zero? num-injected) (js/console.clear))
      #_(js/console.log (.-rules sheet))
     ;Inject only if rule-css is not already a member of kushi.core/injected atom
      (doseq [[_ rule-css*]  rules-as-seq
              :let         [rule-css  (sanitize-if-malformed-garden-output rule-css*)
                            injected? (contains? @injected rule-css)]]
        (when log-inject-css*?
          (js/console.log "[kushi.core/inject-css*]\n"
                          (keyed css-rules injected? num-injected sheet-id sheet-len)))
        (do
          #_(when log-inject-css*?
              (js/console.log "    Already injected, skipping: " rule-css))
          (when-not injected?
            (swap! injected conj rule-css)
            (let [updated-num-rules-idx (-> sheet .-rules .-length)]
              (try
                (do (.insertRule sheet rule-css updated-num-rules-idx)
                    (when log-inject-css*?
                      (js/console.log "    Injecting: " rule-css)))
                (catch :default e (js/console.warn
                                   "kushi.core/sx:\n\nFailed attempt to inject malformed css rule:\n\n"
                                   rule-css
                                   "\n\n¯\\_(ツ)_/¯\n"
                                   e))))))))))

(defn inject-style-rules
  [css-rules inj-type]
  (when (seq css-rules)
    (inject-css* css-rules (inj-type sheets/sheet-ids-by-type))))

(defn inject-kushi-atomics [m]
  (when (seq m)
    (doseq [[classtype kushi-atomics] m]
      (doseq [[_ css-rules] kushi-atomics]
        (when-let [sheet-id (classtype sheets/sheet-ids-by-type)]
          (inject-css* css-rules sheet-id))))))

(defn inject-design-tokens! [css inj-type]
  (inject-css* [css] (inj-type sheets/sheet-ids-by-type)))

;; cssfn (helper fn for use inside calls to sx macro)  ---------------------------------------------------------
(defn cssfn? [x]
  (and (list? x)
       (= (first x) 'cssfn)
       (keyword? (second x))))

(declare cssfn)

(defn vec-in-cssfn [v]
  (string/join
   " "
   (map #(cond
           (cssfn? %) (cssfn %)
           (vector? %) (vec-in-cssfn %)
           (keyword? %) (name %)
           :else (str %))
        v)))

(defn cssfn* [[_ nm & args]]
  (str (name nm)
       "("
       (string/join
        ", "
        (map #(cond
                (cssfn? %) (cssfn %)
                (vector? %) (vec-in-cssfn %)
                (keyword? %) (name %)
              ;;  (string? %) (str "\"" % "\"")
                :else (str %))
             args))
       ")"))

(defn cssfn [& args]
  (cssfn (cons 'cssfn args)))


;; !important (helper fn for use inside calls to sx macro)  -------------------------------------------------------
(defn !important [x]
  (cssfn (list 'important x)))


;; Public function for injecting 3rd party stylesheets ------------------------------------------------------------
(defn inject-stylesheet

  "Expects a map with the following keys: :rel, :href, and :cross-origin(optional).
   Appends stylesheet as <link> element to the document <head>.
   Only appends if link with identical attributes does not already exist in the document head.

   Examples:

   (inject-stylesheet {:rel \"preconnet\"
                       :href \"https://fonts.gstatic.com\"
                       :cross-origin \"anonymous\"})

   (inject-stylesheet {:rel \"preconnet\"
                       :href \"https://fonts.googleapis.com\"})

   (inject-stylesheet {:rel \"stylesheet\"
                       :href \"https://fonts.googleapis.com/css2?family=Rock+Salt&display=swap\"})"

  [m]
  (let [attr-selector* (map (fn [[k v]] (str "[" (name k) "=" "\"" v "\"" "]")) m)
        existing-link (js/document.querySelector (str "link" (string/join "" attr-selector*)))]
    (when-not existing-link
      (let [link (js/document.createElement "link")]
        (doseq [[attr val] m]
          (.setAttribute link (name attr) (name val)))
        (try
          (.appendChild js/document.head link)
          (catch :default e (when ^boolean js/goog.DEBUG
                              (js/console.warn
                               "kushi.core/s+:\n\nFailed attempt to inject stylesheet (or link):\n\n"
                               m
                               "\n\n¯\\_(ツ)_/¯"))))))))
;; remove?
(defn- garden-mq-rule? [v]
  (and (map? v) (= :media (:identifier v))))


;; Functionaltiy for kushi style decoration -----------------------------------------------------------------

(defn merged-attrs-map
  ([attrs-base classlist css-vars]
   (merged-attrs-map attrs-base classlist css-vars nil))
  ([attrs-base classlist css-vars data-cljs]
   (assoc attrs-base
          :class
          (distinct classlist)
          :style css-vars
          :data-cljs data-cljs)))

(def merge-with-style kushi.utils/merge-with-style)


;; This will inject / and or write ui theme to disc if config says to do so
(theme!)
