(ns build
  (:require
   [fireworks.core :refer [? !? ?> !?> pprint]]
   [bling.core :refer [callout bling]]
   [edamame.core :as e :refer [parse-string]]
   [kushi.css.core :refer [defcss css css-rule*]]
   [kushi.css.build.utility-classes]
   [kushi.css.build.build :as cb]
   [kushi.utils :refer [maybe keyed]]
   [shadow.cljs.devtools.api :as shadow]
   [clojure.walk :as walk]
   [clojure.string :as string]
   [clojure.edn :as edn]
   [clojure.java.io :as io]
   [clojure.set :as set]))

;; TODO 
;;
;; 1) Use :font-loading from kushi.edn to create appropriate tags
;; 2) Parse the existing index.html with something like hickory
;; 3) Transform that data structure to include font <script> tags, if needed
;; 4) Rewrite the index.html file. (would this require hard refresh for dev?)
;;
;; Alternate convention would be to have something like:
;; <script defer src="/js/font-loading.js"></script>
;; then rewrite that font-loading.js file with vanilla js
;; 
;; Or somehow include the injection logic in main.js bundle
;; maybe with shadow-cljs build-hooks?

(defn css-release [& args]
  #_(let [bs (cb/index-path {:namespaces   {}
                           :alias-groups {}
                           :aliases      {}
                           :spacing      {}}
                          (io/file "../src" "kushi" "ui" "slider") {})]
    (println "BS")
    (pprint (get (-> bs :namespaces) 'kushi.ui.slider.core)))
  (let [user-config
        (with-open [r (clojure.java.io/reader "kushi.edn")]
          (edn/read {:default (fn [tag value] value)}
                    (java.io.PushbackReader. r)))

        build-state
        (-> (cb/start)

            ;; (cb/index-path (io/file "../src" "kushi" "ui" "slider") {})
            ;; (cb/index-path (io/file "../src" "kushi" "ui" "text_field") {})
            ;; (cb/index-path (io/file "../src" "kushi" "ui" "radio") {})
            ;; (cb/index-path (io/file "../src" "kushi" "ui" "checkbox") {})
            ;; (cb/index-path (io/file "../src" "kushi" "ui" "label") {})
            (cb/index-path (io/file "../src" "kushi" "ui" "button") {})
            ;; (cb/index-path (io/file "../src" "kushi" "ui" "icon") {})
            ;; (cb/index-path (io/file "../src" "kushi" "ui" "spinner") {})
            ;; (cb/index-path (io/file "../src" "kushi" "ui" "switch") {})
            ;; (cb/index-path (io/file "../src" "kushi" "ui" "card") {})
            ;; (cb/index-path (io/file "../src" "kushi" "ui" "tag") {})
            ;; (cb/index-path (io/file "../src" "kushi" "ui" "spinner") {})
            ;; (cb/index-path (io/file "../src" "kushi" "ui" "grid") {})
            ;; (cb/index-path (io/file "../src" "kushi" "ui" "callout") {})
            ;; (cb/index-path (io/file "../src" "kushi" "ui" "link") {})
            ;; (cb/index-path (io/file "../src" "kushi" "ui" "collapse") {})
            ;; (cb/index-path (io/file "../src" "kushi" "ui" "modal") {})
            ;; (cb/index-path (io/file "../src" "kushi" "ui" "tooltip") {})
            ;; (cb/index-path (io/file "../src" "kushi" "ui" "popover") {})

            (cb/index-path (io/file "../src" "kushi" "ui" "label") {})
            ;; Check popover with form ^^^

            (cb/index-path (io/file "../src" "kushi" "ui" "toast") {})

            ;; (cb/index-path (io/file "../src" "kushi" "ui" "dom" "pane" "toast") {})
            ;; Done!

            (cb/index-path (io/file "src" "main") {})
            (assoc :user-config user-config)
            (cb/generate
             '{:main {:entries [mvp.browser]}})

            ;; Maybe don't need this if using lightningcss
            #_(cb/minify)

            (cb/write-outputs-to (io/file "public" "css")))]

    (doseq [mod                   (:outputs build-state)
            {:keys [warning-type]
             :as   warning}       (:warnings mod)]
      (prn [:CSS (name warning-type) (dissoc warning :warning-type)]))))

(defn js-release []
  (shadow/release! :app))

(defn all []
  (css-release)
  (js-release)

  :done)

(comment
  (css-release))


;; NEW shadow-css-build-hook-based approach ------------------------------------

(def kushi-macros
  '#{defcss 
     css
     sx
     ?css
     ?sx
     ?defcss
     ;; should be register-css-classes
     utilitize
     })

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
  [{:keys [form ns-str args] :as m} css-data] 
  (let [sel-og (-> args first (maybe string?))
        
        {:keys [layer sel]} (if sel-og
                              (layer+sel sel-og)
                              (loc-sel form ns-str))
        layer (or layer "user-styles")
        result (merge m
                      (meta form)
                      (keyed sel-og sel args layer))] 
    (vswap! css-data
            update-in
            [layer]
            conj
            result)
    nil))

(defn defcss-call-data
  [{:keys [args form] :as m} css-data]
  (let [[sel-og & args]     args
        {:keys [layer sel]} (layer+sel sel-og)
        layer               (or layer "user-shared-styles")
        result              (merge m
                                   (meta form)
                                   (keyed sel-og sel args layer))]
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

(defn- analyze-forms [css-data ns-str rel-path acc tl-form]
  (walk/prewalk
   (fn [form] 
     (let [[macro-sym & args] (when (list? form) form)
           kushi-macro?       (contains? kushi-macros macro-sym)]
       (if kushi-macro? 
         (let [m (keyed form ns-str rel-path macro-sym args)]
           (if (contains? '#{defcss ?defcss} macro-sym)
             (defcss-call-data m css-data)
             (css-call-data m css-data)
             )
           ;; prewalk return nil for perf
           nil)
         form)))
   tl-form)
  acc)

(defn- css-file-path [layer ns-str]
  (let [path (str "./public/css/" layer "/" ns-str ".css")]
    (io/make-parents path)
    path))

(defn- dev-ns-callout [ns]
  (callout {:type          :magenta
            :border-weight :medium
            :margin-bottom 0}
           (bling [:italic.magenta.bold (str ns)])))

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
 
(def kushi-layers
  (array-map "kushi.ui.theming" 
             "Theming rules for kushi.ui components"

             "kushi-ui-theming"
             "Theming rules for kushi.ui components"

             "user-shared-styles"
             "User-defined shared styles, via kushi.core/defclass"

             "kushi-ui-component"
             "Component styles specific to kushi.ui components"

             "user-styles"
             "User-defined styles, via kushi.core/css or kushi.core/sx" ))


(defn analyze-sources
  {:shadow.build/stage :compile-prepare}
  [{:keys [:shadow.build/build-id] :as build-state}]
  (let [init? true
        ;; Check if files are new or deleted ;; Check if any css imports within namespaces added or deleted
        deleted? false
        added? false
        ;; Check if files are new or deleted ;; Check if any css imports within namespaces added or deleted
        new-or-deleted? true #_(or deleted? added?)
        ;; Check if any css imports within namespaces changed ;; Check if any css namespaces changed
        existing-css-changed? true
        namespaces-using-kushi-macros (filter-build-sources build-state)
        ]
    
    (when deleted?
      ;; Remove corresponding css files or deleted
      ;; probably don't need to delete exising css files that are imported
      ;;     unless they are copied into the public/css dir
       )

    (when (or existing-css-changed?
              new-or-deleted?
              init?)
      ;; namespaces-using-kushi-macros is an array-map, produced by filtering
      ;; the build-state for namespaces which pull in kushi.css.core macros
      ;; example entry:
      ;; {[:shadow.build.classpath/resource "mvp.browser.cljs"]
      ;;  {...}}
      ;; See bottom of this namespace for example of build-src map in each entry
      (let [ret (for [[[_ rel-path] {:keys [ns file]}]
                      namespaces-using-kushi-macros]
                  (let [ns-str    (string/replace (str ns) #"\." "_")
                        all-forms (parse-all-forms file)
                        css-data  (volatile! {})]

                    (dev-ns-callout ns)

                    ;; TODO - change to doseq - we are just doing side effects
                    (reduce (partial analyze-forms css-data ns-str rel-path)
                            []
                            all-forms)

                    ;; Iterate thru layers within namespace and write css files
                    ;; each layer getting its own dir within public/css/
                    ;; each css file is named by namespace
                    ;; e.g.
                    ;; public/css/kushi_ui_component/kushi_ui_icon_core.css
                    ;; public/css/user-shared-styles/mvp_browser.css
                    ;; public/css/user-styles/mvp_browser.css
                    (reduce (fn [acc layer]
                              (if-let [rulesets (get @css-data layer)]
                                (let [css-fp (css-file-path layer ns-str)]
                                  (!? :result (symbol css-fp))
                                  (spit-css-file css-fp rulesets)
                                  (assoc acc layer css-fp))
                                acc))
                            {}
                            (keys kushi-layers))))]
        ;; If necessary write the css imports chain
        (when (or init? new-or-deleted?)
          (->> kushi-layers
               (reduce (fn [acc [layer :as kv]]
                         (conj acc kv (keep #(get % layer) ret)))
                       [])
               (apply array-map)
               spit-css-imports))))

  build-state))


                      ;;  (? :result r)
                      ;;  (? :result form)
                      ;;  (? :result (keyed row col rel-path))
                      ;;  (? :result (with-meta form {:line   row
                      ;;                              :column col
                      ;;                              :file   rel-path}))
                      ;; (? :result sel)



;; (doseq [layer ["kushi.ui.theming"
;;                "kushi-ui-theming"
;;                "user-shared-styles"
;;                "kushi-ui-component"
;;                "user-styles"]]
;;   (let [css-filename (str ns-str
;;                           (when-not (= layer "user-styles")
;;                             (str "__" layer))
;;                           ".css")]
;;     (? :result (symbol css-filename))
;;     (? :result
;;        (string/join 
;;         "\n\n"
;;         (for [{:keys [sel args form row col]} (get @css-data layer)]
;;           (css-rule* sel
;;                      args
;;                      (with-meta form {:line   row
;;                                       :column col})
;;                      nil))))))


;; {:cache-key      ["cf493d7c3785a3aae7b5e5b3d062"...]
;;  :deps           [goog
;;                   cljs.core
;;                   reagent.dom
;;                   kushi.ui.icon.mui.svg
;;                   kushi.core
;;                   kushi.css.core
;;                   kushi.ui.icon.core
;;                   kushi.ui.text-field.core
;;                   kushi.ui.toast.core
;;                   kushi.ui.button.core
;;                   kushi.ui.tooltip.core
;;                   kushi.ui.popover.core]
;;  :file           /Users/jc/Dropbox/Dev/kushi/kus...
;;  :fs-root        "src/main"
;;  :last-modified  1733947973544
;;  :macro-requires #{cljs.core}
;;  :ns             mvp.browser
;;  :ns-info        {:cljc           false
;;                   :deps           [goog
;;                                    cljs.core
;;                                    reagent.dom
;;                                    kushi.ui.icon.mui.svg
;;                                    kushi.core
;;                                    kushi.css.core
;;                                    kushi.ui.icon.core
;;                                    kushi.ui.text-field.core
;;                                    kushi.ui.toast.core
;;                                    kushi.ui.button.core
;;                                    kushi.ui.tooltip.core
;;                                    kushi.ui.popover.core]
;;                   :excludes       #{}
;;                   :flags          {:require #{}}
;;                   :imports        nil
;;                   :js-deps        {}
;;                   :meta           {:file       "mvp/browser.cljs"
;;                                    :line       1
;;                                    :column     5
;;                                    :end-line   1
;;                                    :end-column 16}
;;                   :name           mvp.browser
;;                   :reader-aliases {}
;;                   :rename-macros  nil
;;                   :renames        {}
;;                   :require-macros {cljs.core cljs.core}
;;                   :requires       {cljs.core
;;                                    cljs.core

;;                                    goog
;;                                    goog

;;                                    kushi.core
;;                                    kushi.core

;;                                    kushi.css.core
;;                                    kushi.css.core

;;                                    kushi.ui.button.core
;;                                    kushi.ui.button.core

;;                                    kushi.ui.icon.core
;;                                    kushi.ui.icon.core

;;                                    kushi.ui.icon.mui...
;;                                    kushi.ui.icon.mui.svg

;;                                    kushi.ui.popover....
;;                                    kushi.ui.popover.core

;;                                    kushi.ui.text-fie...
;;                                    kushi.ui.text-field.core

;;                                    kushi.ui.toast.core
;;                                    kushi.ui.toast.core

;;                                    kushi.ui.tooltip....
;;                                    kushi.ui.tooltip.core

;;                                    mui.svg
;;                                    kushi.ui.icon.mui.svg

;;                                    rdom
;;                                    reagent.dom

;;                                    reagent.dom
;;                                    reagent.dom}
;;                   :seen           #{:require}
;;                   :use-macros     nil
;;                   :uses           {?css
;;                                    kushi.css.core

;;                                    button
;;                                    kushi.ui.button.core

;;                                    css
;;                                    kushi.css.core

;;                                    css-vars
;;                                    kushi.css.core

;;                                    css-vars-map
;;                                    kushi.css.core

;;                                    defcss
;;                                    kushi.css.core

;;                                    dismiss-popover!
;;                                    kushi.ui.popover.core

;;                                    dismiss-toast!
;;                                    kushi.ui.toast.core

;;                                    grid-template-areas
;;                                    kushi.css.core

;;                                    icon
;;                                    kushi.ui.icon.core

;;                                    merge-attrs
;;                                    kushi.core

;;                                    popover-attrs
;;                                    kushi.ui.popover.core

;;                                    render
;;                                    reagent.dom

;;                                    sx
;;                                    kushi.css.core

;;                                    text-field
;;                                    kushi.ui.text-field.core

;;                                    toast-attrs
;;                                    kushi.ui.toast.core

;;                                    tooltip-attrs
;;                                    kushi.ui.tooltip.core}}
;;  :output-name    "mvp.browser.js"
;;  :provides       #{mvp.browser}
;;  :requires       #{kushi.ui.text-field.core
;;                    kushi.ui.icon.mui.svg
;;                    cljs.core
;;                    goog
;;                    kushi.ui.icon.core
;;                    kushi.ui.tooltip.core
;;                    kushi.ui.button.core
;;                    kushi.ui.popover.core
;;                    kushi.core
;;                    kushi.css.core
;;                    kushi.ui.toast.core
;;                    reagent.dom}
;;  :resource-id    [:shadow.build.classpath/resource
;;                   "mvp/browser.cljs"]
;;  :resource-name  "mvp/browser.cljs"
;;  :type           :cljs
;;  :url            file:/Users/jc/Dropbox/Dev/kush...}
