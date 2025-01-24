;; TODO - determine how much in this namespace can be done in macro-land

(ns kushi.css.inject
  (:require [clojure.string :as string]))

;; -----------------------------------------------------------------------------
;; Public function for injecting 3rd party stylesheets
;; -----------------------------------------------------------------------------
;; TODO add example that is not a google-font.

(defn add-stylesheet!
  "Expects a map with the keys: :rel, :href, and :cross-origin(optional).
   Appends stylesheet as <link> element to the document <head>.
   Only appends if identical <link> does not already exist in <head>.


   Example of injecting local stylesheet:
   (add-stylesheet! {:rel  \"stylesheet\"
                     :href \"css/my-global-styles.css\"})


   Example of injecting google fonts with preconnects:
   (add-stylesheet! {:rel \"preconnet\"
                     :href \"https://fonts.gstatic.com\"
                     :cross-origin \"anonymous\"})

   (add-stylesheet! {:rel \"preconnet\"
                     :href \"https://fonts.googleapis.com\"})

   (add-stylesheet! {:rel \"stylesheet\"
                     :href \"https://fonts.googleapis.com/css2?family=Rock+Salt&display=swap\"})"

  [m]
  (let [attr-selector*
        (map (fn [[k v]]
               (str "[" (name k) "=" "\"" v "\"" "]")) m)

        existing-link
        (js/document.querySelector (str "link" (string/join "" attr-selector*)))]
    (when-not existing-link
      (let [link (js/document.createElement "link")]
        (doseq [[attr val] m]
          (.setAttribute link (name attr) (name val)))
        (try
          (do (.appendChild js/document.head link)
              (when ^boolean js/goog.DEBUG 
                (js/console.log 
                 (str "(kushi.css.inject/add-stylesheet! ...)\n\n"
                      "   Appended stylesheet to <head>\n")
                 link)))
          (catch :default e
            (when ^boolean js/goog.DEBUG
              (js/console.warn
               "kushi.inject/add-stylesheet!:"
               "\n\n"
               "Failed attempt to inject stylesheet (or link):"
               "\n\n"
               m
               "\n\n"
               "¯\\_(ツ)_/¯"))))))))


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
       "[WARNING] kushi.inject/add-google-fonts!"
       "\n\nMalformed font map argument:\n\n"
       m
       "\n\nMust be a map with the entries :family and :styles"
       "\n\n:family must be a string representing a font family"
       "\n\n:styles must be a map with the entries of :normal and/or :italic, both of which must be vectors of numbers representing font weights."
       "\n\n\nExample:\n"
       {:family "Fira Code" :styles {:normal [300 400] :italic [300 400]}}
       "\n\n")
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

(def material-symbols-font-families
  #{"Material Symbols Outlined"
    "Material Symbols Sharp"
    "Material Symbols Rounded"})

(defn material-symbols-bad-option-warning! [family]
  (let [family-display (if (string? family)
                         (str "\"" family "\"")
                         family)]
    (js/console.warn
     "[WARNING] kushi.inject/add-google-material-symbols!"
     "\n\nInvalid :family name for Google Material Symbols:"
     (str "\n\n" family-display)
     (str "\n" (string/join (repeat (count family-display) "^")))
     "\n\nMust be a map with the entries :family and, optionally, :axes"
     "\n\n:family must be a string representing a Google Material Symbols font family."
     "\n\nCurrently, there are 3 Google Material Symbols font families:"
     "\n"
     (string/join (map #(str "\n\"" % "\"") material-symbols-font-families))
     "\n\n:axes must be a map with the optional entries of :opsz, :wght, :fill, and :grad."
     "\n\n\nExample:\n"
     {:family "Material Symbols Outlined"
      :axes   {:wght 400
               :opsz 24
               :fill 0
               :grad 0}}
     "\n\n")))

(defn- goog-symbols-map->str
  [acc
   {:keys [family axes]
    :as   m}]
  (if-not (contains? material-symbols-font-families family)
    (do (material-symbols-bad-option-warning! family)
        acc)
    (let [axes*     (into {}
                          (map (fn [[k v]]
                                 (let [k (case k
                                           :grad "GRAD"
                                           :fill "FILL"
                                           (name k))
                                       v (cond
                                           (vector? v)
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



;; -----------------------------------------------------------------------------
;; Public fns for adding Google Fonts and Symbols via stylesheet injection
;; -----------------------------------------------------------------------------

;; TODO -- analyze weight savings & tradeoffs if normalization logic, warnings
;; etc associated w kushi.inject/add-google-fonts! is all offloaded into a macro.
;; Still might need a dynamic version if developer wants to allow users to load
;; google fonts on demand. Maybe single source logic could live in a cljc file
;; somewhere and then there is a dynamic, cljs version of the function that user
;; would have to explicitly load from a separate namespace.

(defn- add-google-fonts-with-preconnects [s]
  (add-stylesheet! {:rel          "preconnect"
                    :href         "https://fonts.gstatic.com"
                    :cross-origin "anonymous"})
  (add-stylesheet! {:rel  "preconnect"
                    :href "https://fonts.googleapis.com"})
  (add-stylesheet! {:rel  "stylesheet"
                    :href (str "https://fonts.googleapis.com/css2?" s)}))

;; TODO add check maps keys & vals with alert
(defn ^:public add-google-fonts!
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
      (add-google-fonts-with-preconnects families))))


(defn ^:public add-google-material-symbols!
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
      (add-google-fonts-with-preconnects families))))


;; This should always gets called for both prod or dev builds
#_(kushi.inject/inject-google-fonts!)
