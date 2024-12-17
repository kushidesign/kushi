(ns kushi.css.build.analyze
  (:require

   [fireworks.core :refer [? !? ?> !?> pprint]]
   [bling.core :refer [callout bling]]
   [edamame.core :as e]
   [kushi.css.build.utility-classes :as utility-classes]
   [kushi.css.core :refer [css-rule*]]
   [kushi.css.specs :as kushi-specs]
   [kushi.utils :refer [maybe keyed]]
   [clojure.walk :as walk]
   [clojure.string :as string]
   [clojure.java.io :as io]
   [clojure.set :as set]
   [clojure.spec.alpha]

   ))


;; NEW shadow-css-build-hook-based approach ------------------------------------

(def narrative? true)

(def kushi-macros
  '#{defcss 
     css
     sx
     ?css
     ?sx
     ?defcss
     ;; should be register-css-classes
     utilitize})


(def kushi-layers
  (array-map 
   "css-reset"
   "List of rulesets that reset default browser styles"

   "design-tokens"
   "Global CSS custom properties"

   "user-design-tokens"
   "Global CSS custom properties defined in user's theme"

   "kushi-ui-theming" 
   "Theming rules for kushi.ui components"

   "kushi-ui-shared"
   "Styles to be shared across families of kushi ui components such as tooltips, popovers, etc"

   "kushi-ui-styles"
   "Styles defined for elements of kushi ui components."

   "kushi-utility"
   "Baseline utility classes defined by kushi"

   "user-shared-styles"
   "User-defined shared styles"

   "user-styles"
   "User-defined styles, via kushi.core/css or kushi.core/sx"
   
   "kushi-utility-overrides"
   "Baseline utility classes defined by kushi"
   
   "user-shared-overrides"
   "Baseline utility classes defined by kushi"))


(defn namespaces-with-matching-path [[_ s]]
  ;; get this mvp from project entry point
  (or (string/starts-with? s "mvp/")
      (string/starts-with? s "kushi/ui")))

(defn gather-macros [m1 m2 f]
  (->> m1 (merge m2) f (into #{})))

(defn namespaces-with-macro-usage
  [[[_ path]
    {{:keys [uses use-macros rename-macros renames]} :ns-info}]]
  (let [used-renamed-macros (gather-macros rename-macros renames vals)
        used-macros         (gather-macros uses use-macros keys)
        used-macros         (->> used-macros
                                 (set/union used-renamed-macros)
                                 (set/select #(contains? kushi-macros %)))]
    #_(when (seq used-macros)
      (? {:display-metadata? false :label path} used-macros))
    (seq used-macros)))


;; figure out css includes
;; figure out "dirty" namespaces with caching
;; can you load if not using kushi locally e.g. from m2
;; branch to thread, so as not to hold-up shadow

(defn- layer+sel [sel-og]
  (if (string/starts-with? sel-og "@layer")
    (let [[_ layer sel]
          (string/split sel-og #"[\t\n\r\s]+")]
      {:layer layer
       :sel   sel})
    {:sel sel-og})  )

(defn- loc-sel [x ns-str]
  (let [{:keys [line col]} (meta x)
        sel (str "." (string/replace ns-str #"/" "_") "__L" line "_C" col)]
    {:sel sel}))


(defn css-call-data
  [{:keys [form ns-str args] :as m} 
   css-data
   req-util] 

  

  (let [sel-og
        (-> args first (maybe string?))
        
        {:keys [layer sel]}
        (if sel-og
          (layer+sel sel-og)
          (loc-sel form ns-str))

        layer
        (or layer
            (when (re-find #"^kushi_ui" ns-str) "kushi-ui-styles")
            "user-styles")

        kushi-utils
        (filter #(when (clojure.spec.alpha/valid? ::kushi-specs/class-kw %)
                   ;; TODO - just use the utility-classes map?
                   (contains? utility-classes/utility-class-ks-set (name %)))
                args)

        result
        (merge m
               (meta form)
               (keyed sel-og sel args layer kushi-utils))] 

    (some->> kushi-utils
             seq
             (vswap! req-util
                     update-in
                     [:used-base-utility-classes]
                     conj))

    (vswap! css-data
            update-in
            [layer]
            conj
            result)
    nil))

(defn- hydrated-util-args
  [args req-util]
  (!? (:kushi-ui-shared @req-util))
  (reduce 
   (fn [acc x]
     (let [class-kw? (clojure.spec.alpha/valid? ::kushi-specs/class-kw x)
           util-args (when class-kw?
                       (let [s (name x)]
                         (or (get utility-classes/utility-classes s)
                             (get (:kushi-ui-shared @req-util) s)
                             (get (:user-shared @req-util) s))))]
       (if (seq util-args)
         (apply conj acc util-args)
         (conj acc x))))
   []
   args))

(defn defcss-call-data
  [{:keys [args form ns-str] :as m}
   css-data
   req-util]
  (let [[sel-og & args]     (!? args)
        {:keys [layer sel]} (layer+sel sel-og)
        layer               (or layer "user-shared-styles")
        _                   (when (string/starts-with? sel ".")
                              (let [k (if (string/starts-with? (!? ns-str)
                                                               "kushi_ui")
                                        :kushi-ui-shared
                                        :user-shared)]
                                (vswap! req-util
                                        update-in
                                        [k]
                                        assoc
                                        sel
                                        args)
                                (vswap! req-util update-in [k] assoc sel args)))
        args                (hydrated-util-args args req-util)
        result              (merge m
                                   (meta form)
                                   (keyed sel-og sel args layer))]

    ;; (vswap! req-util apply conj kushi-utils)
    #_{:user-shared          {}
     :kushi-ui-shared      {}
     :used/kushi-utility   []
     :used/kushi-ui-shared []
     :used/user-shared     []}
    (vswap! css-data
            update-in
            [layer]
            conj
            result)
    nil
    ))


(defn parse-all-forms [file]
  (-> file
      slurp
      (e/parse-string-all {:fn      true
                           :regex   true
                           :quote   true
                           :readers {'js (fn [v] (list 'js v))}})
      rest))

(defn filter-build-sources [bs]
  (->> bs
       :build-sources
       (filter namespaces-with-matching-path)
       (reduce (fn [acc k]
                 (conj acc [k (get (:sources bs) k)]))
               [])
       (filter namespaces-with-macro-usage)
       (sequence cat)
       (apply array-map)))

(defn spit-css-imports [coll]
 ;; TODO - grab  skip <public> folder dynamically here
 (spit "./public/css/main2.css"
       (str "/* Kushi build system - dev */\n\n"
            (string/join 
             "\n\n"
             (map (fn [[[layer layer-desc] css-files]]
                    (str 
                     "/* " layer " -- " layer-desc "*/\n"
                     (string/join
                      "\n"
                      (map #(str "@import \""
                                 (string/replace % #"^\./public/css/" "")
                                 "\";")
                           css-files))))
                  coll)))
       :append false) )



(defn- css-file-path [layer ns-str]
  (let [path (str "./public/css/" layer "/" ns-str ".css")]
    (io/make-parents path)
    path))

(defn- stage-callout 
  ([label]
   (stage-callout label nil))
  ([label attrs]
   (callout (merge {:type          :magenta
                    :border-weight :medium
                    :margin-bottom 0}
                   attrs)
            (bling [:italic.magenta.bold (str label)]))))

(defn- spit-css-file [css-fp rulesets]
  (spit css-fp
        (string/join 
         "\n\n"
         (for [r rulesets]
           (let [{:keys [sel args form row col rel-path]} r]
             (css-rule* sel
                        args
                        (with-meta form {:line   row
                                         :column col
                                         :file   rel-path})
                        nil))))
        :append false))
 


(defn add-base-utility-classes
  [req-util ret]
  (let [reified (reduce (fn [acc coll]
                          (apply conj acc coll))
                        #{}
                        (:used-base-utility-classes @req-util))] 
    (when narrative?
      ;; TODO - Add callout here 
      nil)
    (if (seq reified)
      (let [path "./public/css/kushi-base-utility/utility.css"
            css  (string/join 
                  "\n\n"
                  (for [class reified
                        :let  [classname (name class)]]
                    (css-rule* classname
                               [(get utility-classes/utility-classes
                                     classname)]
                               nil
                               nil)))]
        (io/make-parents path)
        (spit path css :append false)
        (conj ret {"kushi-base-utility" path}))
      ret)))


#_(defn add-user-shared-classes
  [req-util ret]
  (let [reified (reduce (fn [acc coll]
                          (apply conj acc coll))
                        #{}
                        @req-util)] 
    (if (? (seq reified))
      (let [path "./public/css/kushi-base-utility/utility.css"
            css  (string/join 
                  "\n\n"
                  (for [class reified
                        :let  [classname (name class)]]
                    (css-rule* classname
                               [(get utility-classes/utility-classes
                                     classname)]
                               nil
                               nil)))]
        (io/make-parents path)
        (spit path css :append false)
        (conj ret {"kushi-base-utility" path}))
      ret)))

(defn bs-epoch [build-state]
  (let [init? true
        ;; Check if files are new or deleted ;; Check if any css imports within namespaces added or deleted
        deleted? false
        added? false
        ;; Check if files are new or deleted ;; Check if any css imports within namespaces added or deleted
        new-or-deleted? true #_(or deleted? added?)
        ;; Check if any css imports within namespaces changed ;; Check if any css namespaces changed
        existing-css-changed? true]
    (keyed init? deleted? added? new-or-deleted? existing-css-changed?)))

(defn- analyze-forms
  [tl-form
   {:keys [css-data req-util ns-str rel-path]}]
  (walk/prewalk
   (fn [form] 
     (let [[macro-sym & args] (when (list? form) form)
           kushi-macro?       (contains? kushi-macros macro-sym)]
       (if kushi-macro? 
         (let [m (keyed form ns-str rel-path macro-sym args)]
           (if (contains? '#{defcss ?defcss} macro-sym)
             (defcss-call-data m css-data req-util)
             (css-call-data m css-data req-util))
           ;; prewalk return nil for perf
           nil)
         form)))
   tl-form))

(defn- write-css-files+layer-profile
  [{:keys [css-data ns ns-str msg]}]
  (let [reified-css-data @css-data]
    (reduce (fn [acc layer]
              (if-let [rulesets (get reified-css-data layer)]
                (let [css-fp (css-file-path layer ns-str)]
                  (!? :result (symbol css-fp))
                  (spit-css-file css-fp rulesets)
                  (vswap! msg 
                          str
                          "\nWriting "
                          (bling [:blue layer])
                          " to "
                          (bling [:olive css-fp]))
                  (update-in acc [layer] merge (keyed css-fp ns rulesets)))
                acc))
            {}
            (keys kushi-layers))))

(defn- analyze-sources
  [req-util
   acc
   [[_ rel-path] {:keys [ns file]}]]
  (let [ns-str    (string/replace (str ns) #"\." "_")
        all-forms (parse-all-forms file)
        css-data  (volatile! {})
        m         (keyed css-data req-util ns ns-str rel-path)]

    #_(stage-callout ns)

    ;; Currently can't build up state because we are using prewalk in
    ;; analyze-forms to mutate both css-data & req-util volatiles.
    (doseq [tl-form all-forms]
      (analyze-forms tl-form m))

    (!? (keys @css-data))
    ;; TODO - maybe this should be broken out into another step and
    ;; anaylze-sources should just return mutated css-data volatile
    ;; should css-data and req-util be the same thing?
    ;; css-data could be map like this, forget about ordering:
    ;; {:sources {mvp.browser {"user-styles"        {...}
    ;;                         "user-shared-styles" {...}}}
    ;;  :utils   {"user-shared-styles"        {".my-shared-style" '(...) } ;;
    ;;            "kushi-ui-shared" {".pane-arrow" '(...) }}


    ;; Iterate thru layers within namespace and write css files e.g.: 
    ;; public/css/kushi_ui_component/kushi_ui_icon_core.css
    ;; public/css/user-shared-styles/mvp_browser.css
    ;; public/css/user-styles/mvp_browser.css
    (let [msg (volatile! (str "Analyzing source for " ns "..."))
          ret (conj acc (write-css-files+layer-profile (assoc m :msg msg)))]
      (when narrative?
        (callout {:margin-top    0
                  :margin-bottom 0}
                 (bling @msg)))
      ret)))

(defn write-css-imports [coll]
  (->> kushi-layers
       (reduce (fn [acc [layer :as kv]]
                 (conj acc kv (keep #(get % layer) coll)))
               [])
       (apply array-map)
       spit-css-imports))


(defn- spit-filtered-build-sources-with-paths 
  [filtered-build-sources]
  (let [filtered-build-sources-with-paths
        (->> filtered-build-sources
             (reduce (fn [acc [k v]]
                       (conj acc
                             [k (-> v
                                    (assoc :file (-> v :file .getPath))
                                    (dissoc :url))]))
                     [])
             (sequence cat)
             (apply array-map))

        _                                 
        (spit "./filtered-build-sources.edn"
              (with-out-str (pprint filtered-build-sources-with-paths))
              :append false)])

  )
(defn hook
  {:shadow.build/stage :compile-prepare}
  [{:keys [:shadow.build/build-id] :as build-state}]
  ;; TODO maybe deleted? and added? should be seqs or nils
  (let [{:keys [init?
                deleted?
                added?
                new-or-deleted?
                existing-css-changed?]}
        (bs-epoch build-state)]
    
    (when (or existing-css-changed? new-or-deleted? init?)
      (let [req-util
            (volatile! {:user-shared          {}
                        :kushi-ui-shared      {}
                        :used/kushi-utility   []
                        :used/kushi-ui-shared []
                        :used/user-shared     []})

            filtered-build-sources
            (filter-build-sources build-state)
            
            _ (spit-filtered-build-sources-with-paths filtered-build-sources)

            ret
            (->> filtered-build-sources
                 (reduce (partial analyze-sources req-util)
                         [])
                 (add-base-utility-classes req-util)
                 #_(add-user-shared-classes req-util))]
        (!? (-> @req-util :user-shared))
        ;; If necessary write the css imports chain
        (when (or init? new-or-deleted?)
          (write-css-imports ret))))

  build-state))

(declare build-sources-callout)
(declare analyzed-callout)


;; TODO 

;; 1xtra) Checkout TODO in analyze-sources


;; 2) Do naive approach of hydration, assuming that style was defined first and is in the user-shared entry

;;      2b) later
;;          - Figure out how to index and save non-base shared styles in .edn or smthg
;;          - kushi-theming
;;          - kushi-ui-shared
;;          - user-shared

;;      2d) Hydrate args that use shared sources
;;      2e) Maybe resave hydrated shared sources without infinite loop?
;;      2f) Write styles


;; 3) Figure out how to pull in css from sources


;; 4) Keep track of changes or deletions in proj - shadow


(defn hook-dev
  [filtered-build-sources]
  ;; TODO maybe deleted? and added? should be seqs or nils
  (let [{:keys [init?
                deleted?
                added?
                new-or-deleted?
                existing-css-changed?]}
        (bs-epoch {})]

    #_(build-sources-callout filtered-build-sources)

    (when (or existing-css-changed? new-or-deleted? init?)
      (let [req-util
            (volatile! {:user-shared          {}
                        :kushi-ui-shared      {}
                        :used/kushi-utility   []
                        :used/kushi-ui-shared []
                        :used/user-shared     []})
            reduced-sources
            (reduce (partial analyze-sources req-util)
                    []
                    filtered-build-sources)

            _ (analyzed-callout reduced-sources)
            _ (? (-> reduced-sources (nth 2) (get "kushi-ui-shared") :rulesets))
            ret
            (add-base-utility-classes req-util reduced-sources)]
        #_(doseq [m ret
                :let [k "kushi.ui.component"]]
          (? (keys m))
          (? (str (-> m 
                      (get k)
                      :css-fp) 
                  " has "
                  (-> m 
                      (get k)
                      :rulesets
                      count)
                  " shared styles")))

        (!? ret)
        (!? (-> @req-util :user-shared))
        ;; If necessary write the css imports chain
        (when (or init? new-or-deleted?)
          (write-css-imports ret))))

  nil))

(defn analyzed-callout [reduced-sources]
  (when narrative?  
    (do (stage-callout "ANALYZED SOURCES" {:margin-top    1
                                           :margin-bottom 1})
        (? {:label
            (str 
             "Each source has been analyzed and produces a map with entries\n"
             "corresponding to a named CSS layer, defined in `kushi-layers`.\n\n"
             "Each of these entries has a `:rulesets` entry, which contains\n"
             "data from the macros calls that was used to write the css rules."
             )}
           (->> reduced-sources
                (reduce (fn [acc m]
                          (conj acc
                                (walk/postwalk (fn [x]
                                                 (if (seq? x) '(...) x))
                                               m)))
                        [])
                )))))

(defn build-sources-callout [filtered-build-sources]
  (stage-callout "FILTERED BUILD SOURCES" {:margin-top 1 :margin-bottom 1})
  (? {:label
      (str 
           "These are sources that use kushi macros\n\n"
           "This structure of the keys and values is from shadow-cljs\n"
           "It has been cast to an array map, ordered by dependency order\n")}
     (->> filtered-build-sources
          (reduce (fn [acc [k v]]
                    (conj acc
                          [k {'... '...}]))
                  []
                  )
          (sequence cat)
          (apply array-map))))

(defn hydrate-paths-into-files [m]
  (->> m
       (reduce (fn [acc [k v]]
                 (conj acc
                       [k (-> v
                              (assoc :file (-> v :file clojure.java.io/file)))]))
               [])
       (sequence cat)
       (apply array-map)))

;; Dev
(let [filtered-build-sources (-> "./site/filtered-build-sources.edn"
                                 slurp
                                 read-string
                                 hydrate-paths-into-files
                                 hook-dev)]
  )

;; namespaces-using-kushi-macros is an array-map, produced by filtering
;; the build-state for namespaces which pull in kushi.css.core macros
;;
;; Example entry:
;; {[:shadow.build.classpath/resource "mvp.browser.cljs"]
;;  {...}}

;; namespaces-using-kushi-macros
;; (filter-build-sources build-state)


;; (when deleted? 
;;   ;; Remove corresponding css files or deleted
;;   ;; probably don't need to delete exising css files that are imported
;;   ;;     unless they are copied into the public/css dir
;;    )
