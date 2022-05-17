(ns ^:dev/always kushi.core
  (:require-macros [kushi.core :refer [keyed #_theme!]])
  (:require [clojure.string :as string]
            [kushi.clean :as clean]
            [kushi.sheets :as sheets]
            [kushi.utils :as util] ;; For aliasing merge-with-style
            ;; [par.core :refer [? !?]] ;; only use when developing kushi itself
            ))

(defn css-sync! [s]
  (let [id     (:kushi-css-sync sheets/sheet-ids-by-type)
        el     (js/document.getElementById id)
        el-new (js/document.createElement "style")]
    (set! (.-innerHTML el-new) s)
    (doto el-new
      (.setAttribute "id" id))
    (.replaceChild (.-parentNode el) el-new el)) )

(defn insert-style-tag!
  ([id]
   (insert-style-tag! id nil))
  ([id css-text]
   (when-not (js/document.getElementById id)
     (let [head (or js/document.head
                    (-> (js/document.getElementsByTagName "head")
                        (aget 0)))
           tag  (js/document.createElement "style")]
       (when css-text (set! (.-innerHTML tag) css-text))

       (.appendChild head tag)
       (doto tag
         (.setAttribute "type" "text/css")
         (.setAttribute "id" id))))))

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


(clean/clean! (vals (select-keys sheets/sheet-ids-by-type
                                 sheets/sheet-types-ordered)))

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
  ;; (!? :inject-css* css-rules)
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


;; Public function for injecting Google Fonts via stylesheet injection ------------------------------------------------------------
(defn- weights->str
  ([xs]
   (weights->str xs nil) )
  ([xs n]
   (map #(str (when n (str n ",")) %)
        (if (= xs :all) [100 200 300 400 500 600 700 800 900] (seq xs)))))

(defn- weight-coll? [x]
  (or (= x :all)
      (and (seq x)
           (every? number? x))))

(defn- m->str [acc {family :family
                   {:keys [normal italic]} :styles
                   :as m}]
  (if-not (and (string? family)
               (or (weight-coll? normal) (weight-coll? italic)))
   (do
     (js/console.warn
      "\n[WARNING] kushi.core/add-google-font!"
      "\n\nMalformed font map argument:\n\n"
      m
      "\n\nMust be a map with the entries :family and :styles"
      "\n\n:family must be a string representing a font family"
      "\n\n:styles must be a map with the entries of :normal and/or :italic, both of which must be vectors of numbers representing font weights."
      "\n\n\nExample:\n"
      {:family "Fira Code" :styles {:normal [300 400] :italic [300 400]}})
     acc)
   (let [italics? (weight-coll? italic)
         weights* (if-not italics?
                    (weights->str normal)
                    (concat (weights->str normal 0)
                            (weights->str italic 1)))
         weights  (str (when italics? "ital,")
                       "wght@"
                       (string/join ";" weights*))]
     (conj acc (str "family="
                    (string/replace family #" " "+")
                    ":"
                    weights)))))

(defn add-google-font!
  [& maps]
  ;; (!? :add-google-font! maps)
  (let [families* (reduce m->str [] maps)
        families  (str (string/join "&" families*) "&display=swap")]
   (do
     (inject-stylesheet {:rel "preconnet"
                         :href "https://fonts.gstatic.com"
                         :cross-origin "anonymous"})
     (inject-stylesheet {:rel "preconnet"
                         :href "https://fonts.googleapis.com"})
     (inject-stylesheet {:rel "stylesheet"
                         :href (str "https://fonts.googleapis.com/css2?" families)}))))

#_(add-google-font! {:family "Fira Code" :style {:normal [300 400] :italic [300 400]}})

;; Functionaltiy for kushi style decoration -----------------------------------------------------------------

(defn merged-attrs-map
  [{:keys [attrs-base prefixed-classlist css-vars]
    :as   m}]
  (assoc attrs-base :class (distinct prefixed-classlist) :style css-vars))

#_(defn merged-attrs-map
  ([attrs-base classlist css-vars]
   (merged-attrs-map attrs-base classlist css-vars nil))
  ([attrs-base classlist css-vars data-cljs]
   (assoc attrs-base
          :class
          (distinct classlist)
          :style css-vars
          :data-cljs data-cljs)))

(def merge-with-style kushi.utils/merge-with-style)

#_(theme!)
