(ns ^:dev/always kushi.core
  (:require-macros [kushi.core])
  (:require [clojure.string :as string]
            [kushi.clean :as clean] ))


(def sheet-ids-by-type
  ;; Renamings?
  {:kushi-atomic            "_kushi-defclass-utility_"
   :kushi-css-sync          "_kushi-css-sync_"
   :defclass-kushi-override "_kushi-defclass-overrides_"
   :defclass-user-override  "_kushi-defclass-user-overrides_"})

(def sheet-types-ordered
  [:kushi-atomic
   :kushi-css-sync
   :defclass-kushi-override
   :defclass-user-override])

(defn css-sync! [s]
  (let [id     (:kushi-css-sync sheet-ids-by-type)
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
  (doseq [kw sheet-types-ordered ]
    (insert-style-tag! (kw sheet-ids-by-type))))

(initialize-style-tags!)

;; (ui-components!)

;; Functionality for injecting styles into during development builds  ------------------------------------------
;; This is also used in release builds if :runtime-injection? config param is set to true ----------------------

;; Used to keep track of what has been injected, to avoid duplicate injections.
(def injected (atom #{}))

(clean/clean! (vals (select-keys sheet-ids-by-type
                                 sheet-types-ordered)))

;; Toggle for debugging while developing kushi itself
;; (def log-inject-css*? false)




;; Public function for injecting 3rd party stylesheets ------------------------------------------------------------
;; TODO add example that is not a google-font.

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




;; ----------------------------------------------------------------------------------------------------------
;; Public function for injecting Google Fonts via stylesheet injection
;; ----------------------------------------------------------------------------------------------------------

;; TODO -- analyze weight savings & tradeoffs if normalization logic, warnings etc associated
;; with kushi.core/add-google-font! is all offloaded into a macro. Still might need a dynamic version
;; if developer wants to allow users to load google fonts on demand. Maybe single source logic could live in a
;; cljc file somewhere and then there is a dynamic, cljs version of the function that user would have to
;; explicitly load from a separate namespace.

(defn- axis->str
  [kw v]
  (map #(when % (str % ","))
       (if (= v :all)
         (case kw
           :opsz [20 24 40 48]
           :grad [-25 0 200]
           :fill [0 1]
           :wght [100 200 300 400 500 600 700])
         (seq v))))

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

(defn- m->str [acc {family                  :family
                    {:keys [normal italic]} :styles
                    :as                     m}]
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


(defn- goog-symbols-map->str
  [acc {:keys [family axes]
        :as   m}]
  (if-not (string? family)
    (do
      (js/console.warn
       "\n[WARNING] kushi.core/add-google-font!"
       "\n\nMalformed font map argument:\n\n"
       m
       "\n\nMust be a map with the entries :family and, optionally, :axes"
       "\n\n:family must be a string representing a font family"
       "\n\n:axes must be a map with the optional entries of :opsz, :wght, :fill, and :grad."
       "\n\n\nExample:\n"
       {:family "Material Symbols Outlined"
        :axes   {:wght 400
                 :opsz 24
                 :fill 0
                 :grad 0}})
      acc)
    (let [axes*     (into {}
                          (map (fn [[k v]]
                                 (let [k (case k :grad "GRAD" :fill "FILL" (name k))
                                       v (cond (vector? v)
                                               (axis->str k v)
                                               (keyword? v)
                                               (name v)
                                               :else
                                               (str v))]
                                   [k v]))
                               axes))
          f         #(when-let [v (get axes* %)] [% v])
          axes*     (map f ["opsz" "wght" "FILL" "GRAD"])
          axes      (string/join "," (keep #(some-> % first) axes*))
          axes-vals (string/join "," (keep #(some-> % second name) axes*))]
;; (println axes*)
;; (println axes)
;; (println axes-vals)
      (conj acc (str "family="
                     (string/replace family #" " "+")
                     ":"
                     axes
                     (when axes
                       (str "@" axes-vals)))))))


(defn inject-google-fonts-with-preconnects [s]
  (inject-stylesheet {:rel          "preconnect"
                      :href         "https://fonts.gstatic.com"
                      :cross-origin "anonymous"})
  (inject-stylesheet {:rel  "preconnect"
                      :href "https://fonts.googleapis.com"})
  (inject-stylesheet {:rel  "stylesheet"
                      :href (str "https://fonts.googleapis.com/css2?" s)}))

;; TODO add check maps keys & vals with alert
(defn add-google-font!
  [& maps]
  (when (seq maps)
    (let [converted (keep #(if (string? %)
                             (when-not (string/blank? %)
                               {:family %
                                :styles {:normal :all :italic :all}})
                             %)
                          maps)
          families* (reduce m->str [] converted)
          families  (str (string/join "&" families*) "&display=swap")]
      (inject-google-fonts-with-preconnects families))))


(defn add-google-material-symbols-font!
  [& maps]
  (when (seq maps)
    (let [converted (keep #(if (string? %)
                             (when-not (string/blank? %)
                               {:family %
                                :styles {:opsz :20..48
                                         :wght :100..700
                                         :grad :-50..200
                                         :fill :0..1}})
                             %)
                          maps)
          families* (reduce goog-symbols-map->str [] converted)
          families  (str (string/join "&" families*) "&display=swap")]
      (inject-google-fonts-with-preconnects families))))


;; This should always gets called for both prod or dev builds
(kushi.core/inject-google-fonts!)



;; ----------------------------------------------------------------------------------------------------------
;; Functionality for kushi style decoration
;; ----------------------------------------------------------------------------------------------------------


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
   :on-wheel ])

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
                  (not (some #(% class) [seq? vector? keyword? string? symbol?])))]
    (when bad? (merge-attrs-warning class :class n))
    bad?))

(defn- class-coll
  [class bad-class?]
  (when-not bad-class?
    (if (or (string? class) (keyword? class))
      [class]
      class)))

(defn- data-sx-str [m]
  (when m
    (let [{:keys [file line column]} m]
      (str file ":"  line ":" column))))

(defn- data-sx [m2 s1 s2]
  (let [from-defcom (data-sx-str (:data-amp-form m2))
        from-user   (data-sx-str (:data-amp-form2 m2))
        coll   (remove nil? [from-defcom from-user s1 s2])
        joined (when (seq coll) (string/join " + " coll))]
    (when joined {:data-sx joined})))

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


(defn- merge-attrs* [& maps]
  (let [[m1 m2]                   (map #(if (map? %) % {}) maps)
        {style1 :style
         class1 :class
         data-sx1 :data-sx}   m1
        {style2 :style
         class2 :class
         data-sx2 :data-sx}   m2
        [bad-style1? bad-style2?] (map-indexed (fn [i x] (bad-style? x i)) [style1 style2])
        [bad-class1? bad-class2?] (map-indexed (fn [i x] (bad-class? x i)) [class1 class2])
        merged-style              (merge (when-not bad-style1? style1) (when-not bad-style2? style2))
        class1-coll               (class-coll class1 bad-class1?)
        class2-coll               (class-coll class2 bad-class2?)
        classes                   (concat class1-coll class2-coll)
        data-sx                 (data-sx m2 data-sx1 data-sx2)

        user-event-handlers       (keys (select-keys m2 dom-element-events))
        merged-event-handlers     (apply merge (map #(handler-concat (% m1) (% m2) m2 %) user-event-handlers))
        m2-                       (dissoc m2 :data-amp-form)
        ret                       (assoc (merge m1 m2- data-sx merged-event-handlers)
                                         :class classes
                                         :style merged-style)]
    ret))

;; Public function for style decoration
(defn merge-attrs [& maps]
  (reduce merge-attrs* maps))
