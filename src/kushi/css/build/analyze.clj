(ns kushi.css.build.analyze
  (:require
   [fireworks.core :refer [? !? ?> !?> pprint]]
   [babashka.process :refer [shell]]
   [bling.core :refer [callout bling]]
   [edamame.core :as e]
   [kushi.css.build.utility-classes :as utility-classes]
   [kushi.css.build.tokens]
   [kushi.css.core :refer [css-rule*]]
   [kushi.css.specs :as kushi-specs]
   [kushi.utils :refer [maybe keyed]]
   [clojure.walk :as walk]
   [clojure.string :as string]
   [clojure.java.io :as io]
   [clojure.set :as set]
   [clojure.spec.alpha]
   [kushi.css.build.tokens :as tokens]
   [clojure.spec.alpha :as s]))


;; NEW shadow-css-build-hook-based approach ------------------------------------

(def debugging 
  #{
    ;; :narrative
    ;; :macro-hydration
    ;; :design-token-registration
    })


(def kushi-macros
  '#{css-include
     defcss 
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

   "kushi-utility"
   "Baseline utility classes defined by kushi"

   "kushi-ui-theming" 
   "Theming rules for kushi.ui components"

   "kushi-ui-shared" ;; <- determine if anything shares styles outside of popovers, tooltips, toasts etc.
   "Styles to be shared across or within families of kushi ui components such as tooltips, popovers, etc"

   "kushi-ui-styles"
   "Styles defined for elements of kushi ui components."

   "kushi-playground-shared" 
   "Styles to be shared across or within families of kushi.playground namespaces."

   "kushi-playground-styles"
   "Styles defined for elements of kushi.playground components."

   "user-shared-styles"
   "User-defined shared styles"

   "user-styles"
   "User-defined styles, via kushi.core/css or kushi.core/sx"
   
   "kushi-utility-overrides"
   "Baseline utility classes defined by kushi, override versions"
   
   "user-shared-overrides"
   "Baseline utility classes defined by kushi, override versions"))


(defn namespaces-with-matching-path [[_ s]]
  ;; TODO  get this "mvp/" from project entry point
  #_(when (string/starts-with? s "kushi")
    (? s))
  (or (string/starts-with? s "mvp/") 
      (string/starts-with? s "kushi/ui")
      (string/starts-with? s "kushi/playground")
      (= s "kushi/css/core.cljs");; <- this is for using css-include to pull in
                                 ;;    build/kushi-reset.css (or others for dev)
      ))


(defn gather-macros [m1 m2 f]
  (->> m1 (merge m2) f (into #{})))


(defn namespaces-with-macro-usage
  [[[_ path]
    {{:keys [uses use-macros rename-macros renames] :as m} :ns-info}]]
  (!? 'namespaces-with-macro-usage m)
  (let [used-renamed-macros (gather-macros rename-macros renames vals)
        used-macros         (gather-macros uses use-macros keys)
        used-macros         (->> used-macros
                                 (set/union used-renamed-macros)
                                 (set/select #(contains? kushi-macros %)))]
    #_(when (seq used-macros)
      (? {:display-metadata? false :label path} used-macros))
    (seq used-macros)))


(defn- layer+sel [sel-og]
  (if (string/starts-with? sel-og "@layer")
    (let [[_ layer sel]
          (string/split sel-og #"[\t\n\r\s]+")]
      {:layer layer
       :sel   sel})
    {:sel sel-og})  )


(defn- loc-sel [x ns-str]
  #_(when (= ns-str "mvp_views")
    (? 'mvp_views (meta x)))
  (let [{:keys [row col]} (meta x)
        sel (str "."
                 (string/replace ns-str #"/" "_")
                 "__L"
                 row
                 "_C"
                 col)]
    {:sel sel}))


(defn resolve-css-layer-for-shared-classes [ns-str]
  (cond (re-find #"^kushi_ui" ns-str)
        "kushi-ui-styles"
        
        (re-find #"^kushi_playground" ns-str)
        "kushi-playground-styles"))
  
(defn initialize-layer-vector! [css-new ns layer]
  (when-not (get-in @css-new [:sources ns layer])
    (vswap! css-new
            assoc-in
            [:sources ns layer]
            [])))

(defn css-call-data
  [{:keys [form ns-str ns-meta ns args] :as m} 
   css-new] 
  (let [sel-og
        (-> args first (maybe string?))
        
        ;; debug?  (= ns 'kushi.ui.checkbox.demo)

        {:keys [layer sel]}
        (if sel-og
          (layer+sel sel-og)
          (loc-sel form ns-str))

        ;; layer_1
        ;; layer

        layer-from-ns-info
        (some-> ns-meta :kushi/layer)

        layer
        (or layer
            layer-from-ns-info ;; <- layer assigned at ns -level
            (resolve-css-layer-for-shared-classes ns-str)
            "user-styles")

        ;; _ (when debug? (? :pp 
        ;;                   {:layer_1            layer_1
        ;;                    :layer_2            layer
        ;;                    :ns-meta            ns-meta
        ;;                    :layer-from-ns-info layer-from-ns-info
        ;;                    :macro              (first form)
        ;;                    :sel-og             sel-og}))

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
             (vswap! css-new
                     update-in
                     [:utils :used/kushi-utility]
                     conj))

    (initialize-layer-vector! css-new ns layer)

    (vswap! css-new
            update-in
            [:sources ns layer]
            conj
            result)

    nil))

(defn- hydrated-class-kw-callout
  [class-kw? util-args x css-new rel-path form]
  (when class-kw?
    (if (seq util-args)
      (let [s    (name x)
            kind (cond (get utility-classes/utility-classes s)
                       :kushi-utility
                       (get-in @css-new [:utils :kushi-ui-shared s])
                       :kushi-ui-shared
                       (get-in  @css-new [:utils :user-shared s])
                       :user-shared
                       :else
                       :unknown)]
        (callout {:type  :white
                  :label (bling [:neutral "Hydrating "]
                                [:olive.bold x]
                                " from "
                                [:blue.italic kind])}))

      ;; TODO - augment this callout and make standard for dev
      (callout {:type        :warning
                :padding-top 1}
               (bling [:italic.neutral (str rel-path
                                            ":"
                                            (-> form meta :row)
                                            ":"
                                            (-> form meta :col))]
                      "\n\n"
                      [:neutral "No utility or shared class found for "]
                      [:olive.bold x])))))

(defn- hydrated-util-args
  [args css-new rel-path form]
  (reduce 
   (fn [acc x]
     (let [class-kw? (clojure.spec.alpha/valid? ::kushi-specs/class-kw x)
           util-args (when class-kw?
                       (let [s (name x)]
                         (or (get utility-classes/utility-classes s)
                             (get-in @css-new [:utils :kushi-ui-shared s])
                             (get-in  @css-new [:utils :user-shared s]))))]

       (when (contains? debugging :macro-hydration)
         (hydrated-class-kw-callout class-kw? util-args x css-new rel-path form))
       (if (seq util-args)
         (apply conj acc util-args)
         (conj acc x))))
   []
   args))

(defn- defcss-callout [sel k]
  (callout {:type          :neutral
            :border-weight :medium
            :margin-top    2
            :label         (bling [:bold.italic "defcss "]
                                  [:bold.magenta sel]
                                  " "
                                  [:italic.blue k])}))

(defn defcss-call-data
  [{:keys [args form ns-str ns-meta ns rel-path]
    :as m}
   css-new]
  (let [[sel-og & args]
        args

        {:keys [layer sel]}
        (layer+sel sel-og)

        layer
        (or layer "user-shared-styles")

        ;; TODO incorporate this
        ;; layer-from-ns-info
        ;; (some-> ns-meta :kushi/layer)

        ;; layer
        ;; (or layer
        ;;     layer-from-ns-info ;; <- layer assigned at ns -level
        ;;     "user-shared-styles")

        _
        (when (string/starts-with? sel ".")
          (let [k (cond (string/starts-with? ns-str "kushi_ui")
                        :kushi-ui-shared

                        (string/starts-with? ns-str "kushi_playground")
                        :kushi-playground-shared

                        :else
                        :user-shared)]
            (when (contains? debugging :macro-hydration)
              (defcss-callout sel k))
            (vswap! css-new update-in [:utils k] assoc sel args)))

        args
        (hydrated-util-args args css-new rel-path form)

        result
        (merge m
               (meta form)
               (keyed sel-og sel args layer))
        
        ;; dbg?
        ;; (= ns 'kushi.playground.shared-styles)
        ]

    ;; (when dbg?
    ;;   (let [coll (get-in @css-new [:sources ns layer])]
    ;;     (? :result [sel (some-> coll count)])
    ;;     (!? :result (get-in @css-new [:sources ns layer]))))

    (initialize-layer-vector! css-new ns layer)

    (vswap! css-new
            update-in
            [:sources ns layer]
            conj
            result)
    nil))


(defn parse-all-forms [file]
  (-> file
      slurp
      (e/parse-string-all {:fn           true
                           :regex        true
                           :quote        true
                           :syntax-quote true
                           :readers      {'js (fn [v] (list 'js v))}})
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

(defn- css-vars-re-seq [s]
  (re-seq #"var\((--[^_][^\)\, ]+)" s))

(defn- css-var-kw? [x]
  (and (keyword? x) (string/starts-with? (name x) "$")))

(defn- css-var-str [v]
  (when (css-var-kw? v)
    (str "var(--" (subs (name v) 1) ")")))

(defn- register-design-tokens!
  [css css-new ns]
  (when true #_(string/starts-with? css ".mvp_browser__L_C6")
        (let [toks (some->> css
                            css-vars-re-seq
                            (map #(nth % 1 nil))
                            seq)]
          (some->> toks 
                   (vswap! css-new
                           update-in 
                           [:used/design-tokens] 
                           (fn [coll args] (apply conj coll args)))))))



(defn- css-includes-block [css-includes ns]
  (when-let [css-includes (seq css-includes)]
    (str "/* css-includes via " (str ns) " start --------- */\n\n"
         (string/join "\n\n" css-includes)
         "/* css-includes via " (str ns) " end --------- */\n\n")))


(defn- ruleset->css
  [{:keys [sel args form row col rel-path]}]
  (let [css (css-rule* sel
                       args
                       (with-meta form
                         {:line   row
                          :column col
                          :file   rel-path})
                       nil)]
    [css :others]))

(defn new-toks-callout-template [s ns layer]
  (callout {:type          :subtle
            :margin-bottom 0
            :label         (bling [:italic.neutral s]
                                  [:orange.italic ns]
                                  ", "
                                  [:blue.italic layer]
                                  "\n ")}))

(defn new-toks-callout [ns layer used-toks css-new]
  (let [new-toks
        (set/difference (:used/design-tokens @css-new)
                        used-toks)]
    (if (seq new-toks) 
      (do (new-toks-callout-template "Registering design tokens for " ns layer)
          (? :result new-toks))
      (new-toks-callout-template  "No design tokens for " ns layer))))

(defn- spit-css-file [css-fp layer rulesets css-new]
  (let [debug?
        (contains? debugging :design-token-registration)

        ns 
        (some-> rulesets seq first :ns)

        used-toks
        (when debug? (:used/design-tokens @css-new))

        {:keys [css-includes others]}
        (reduce (fn [acc {:keys [css]
                          :as   ruleset}]
                  (let [[css k]
                        (or (some-> css (vector :css-includes))
                            (ruleset->css ruleset))]
                    
                    (update-in acc [k] conj css)))
                {:css-includes []
                 :others       []}
                rulesets)

        css-str
        (str (css-includes-block css-includes ns)
             (string/join "\n\n" others))]

    ;; This is where design tokens get registered
    ;; They are identified based on the the actual css-rules produced
    (register-design-tokens! css-str css-new ns)
    #_(when debug? (new-toks-callout ns layer used-toks css-new))
    (spit css-fp
          (str (css-includes-block css-includes ns)
               (string/join "\n\n" others))
          :append false)))

(defn bs-epoch [build-state]
  (let [init? true
        ;; Check if files are new or deleted
        ;; Check if any css imports within namespaces added or deleted
        deleted? false
        added? false
        ;; Check if files are new or deleted
        ;; Check if any css imports within namespaces added or deleted
        new-or-deleted? true #_(or deleted? added?)
        ;; Check if any css imports within namespaces changed
        ;; Check if any css namespaces changed
        existing-css-changed? true]
    (keyed init? deleted? added? new-or-deleted? existing-css-changed?)))

(defn css-include-call-data
  [{:keys [args form ns-str ns-meta ns file] :as m}
   css-new]
  (let [sel-og (first args)
        {css-file-path :sel
         layer         :layer} (layer+sel sel-og)

        
        ;; TODO incorporate this
        ;; layer-from-ns-info
        ;; (some-> ns-meta :kushi/layer)

        ;; layer
        ;; (or layer
        ;;     layer-from-ns-info ;; <- layer assigned at ns -level
        ;;     (resolve-css-layer-for-shared-classes ns-str)
        ;;     "user-shared-styles")


        ;; TODO - Currently assumes css is file in same dir as current
        ;;        ns of form. Check for relative filepath.
        layer (or layer
                  (resolve-css-layer-for-shared-classes ns-str)
                  "user-shared-styles")

        ;; TODO - is there a more efficient way to do this path manipulation?
        css-fp (-> file
                   .getPath
                   (string/split #"/")
                   drop-last
                   (->> (string/join "/"))
                   (str "/" css-file-path))
        ;; TODO - add try/catch to this slurp + issue warning if file-not-found
        css    (slurp css-fp)
        result (merge m
                      (meta form) 
                      (keyed sel-og css css-fp args layer))]
               (vswap! css-new
                       update-in
                       [:sources ns layer]
                       conj
                       result)
               nil))

(defn- analyze-forms
  [tl-form
   {:keys [css-new ns-str rel-path ns ns-meta file] :as tl-form-data}]
  (walk/prewalk
   (fn [form] 
     (let [[macro-sym & args] (when (list? form) form)
           kushi-macro?       (contains? kushi-macros macro-sym)]
       (if kushi-macro? 
         ;; TODO - possibly just (merge tl-form-data (keyed macro-sym args))
         ;;      - then change sig of fns in cond branch
         (let [m (keyed form
                        ns-str
                        ns
                        ns-meta
                        rel-path
                        file
                        macro-sym
                        args)]
           (cond 
             (contains? '#{?css-include css-include} macro-sym)
             (css-include-call-data m css-new)

             (contains? '#{?defcss defcss} macro-sym)
             (defcss-call-data m css-new)

             (contains? '#{?css css ?sx sx} macro-sym)
             (css-call-data m css-new))
           ;; prewalk return nil for perf
           nil)
         form)))
   tl-form))

(defn- write-css-files+layer-profile
  [{:keys [css-new ns ns-str msg]}]
  (let [reified-css-new
        @css-new

        ret-new
        (reduce 
         (fn [acc layer]
           (if-let [rulesets (get-in reified-css-new [:sources ns layer])]
             (let [css-fp (css-file-path layer ns-str)
                  ;;  dbg? (= ns
                  ;;          #_'kushi.ui.text-field.core
                  ;;          'kushi.playground.shared-styles)
                   ]
               #_(when dbg? (? rulesets))
               (spit-css-file css-fp layer rulesets css-new)
               (vswap! msg 
                       str
                       "\nLayer "
                       (bling [:blue layer])
                       ", writing "
                       (bling [:olive css-fp]))
               (update-in acc [layer] merge (keyed css-fp ns rulesets)))
             acc))
         {}
         (keys kushi-layers))]
    ret-new))

(defn- analyze-sources
  [css-new
   acc
   [[_ rel-path] {:keys [ns file ns-info] :as m-}]]
  (let [ns-str    (string/replace (str ns) #"\." "_")
        ns-meta   (:meta ns-info)
        all-forms (parse-all-forms file)
        m         (keyed css-new ns ns-str ns-meta rel-path file)
         
        ;; dbg? (= ns
        ;;         #_'kushi.ui.text-field.core
        ;;         'kushi.playground.shared-styles)
        ]
    #_(when dbg? (? all-forms))

    #_(stage-callout ns)

    ;; Currently can't build up state because we are using prewalk in
    ;; analyze-forms to mutate css-data.
    (doseq [tl-form all-forms]
      (analyze-forms tl-form m))

    ;; TODO - maybe this should be broken out into another step and
    ;; anaylze-sources should just return mutated css-data volatile
    ;; should css-data and css-new be the same thing?
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
      #_(when (= ns 'kushi.ui.text-field.core)
        (? 'analyze-sources/ret ret))
      (when (contains? debugging :narrative)
        (callout {:margin-top    0
                  :margin-bottom 0}
                 (bling @msg)))
      ret)))


(defn dep-toks-seq [v]
  (some->> (or (css-var-str v)
               (when (string? v) v))
           css-vars-re-seq
           (map second)
           seq))


(defn- dep-toks-set [tok m]
  (some->> tok m (dep-toks-seq) (into #{})))


;; Need to figure out policy for keys of the design maps
;; Should they be strings or keywords, probably strings
(defn unique-toks [tok css-new]
  (when-not (or (contains? (:required/kushi-design-tokens @css-new) tok)
                (contains? (:required/user-design-tokens @css-new) tok))
   (let [dep-toks
         (or (dep-toks-set tok kushi.css.build.tokens/design-tokens-by-token)
             (dep-toks-set tok (:theme/user-design-tokens @css-new)))]
     (some-> dep-toks
             (set/difference (:required/kushi-design-tokens @css-new))
             seq))))


(defn register-toks+deps [tok css-new] 
  (if-let [[tok-val k]
           (let [kushi-tok
                 (get kushi.css.build.tokens/design-tokens-by-token tok)
                 
                 user-tok
                 (get (:theme/user-design-tokens @css-new) tok)]
                     (or (some-> kushi-tok (vector :kushi))
                         (some-> user-tok (vector :user))))]

    ;; TODO - make sure this order of doseq then vswap! is correct
    (do (when-let [uniques (unique-toks tok css-new)]
          (doseq [dep-tok uniques]
            (register-toks+deps dep-tok css-new)))
        (vswap! css-new
                update-in
                [:required/kushi-design-tokens]
                conj
                [tok tok-val]))
    (vswap! css-new
            update-in
            [:not-found/design-tokens]
            conj
            tok)))


(defn- spit-css-layer+profile [path css k]
  (io/make-parents path)
  (spit path css :append false)
  {k {:css-fp path}})


;; TODO - maybe unite this with fn below and pass a map with :release? and :init?
(defn user-design-tokens-profile-all
  [css-new]
  (let [path "./public/css/user-design-tokens/user-design-tokens.css"]
    ;; TODO - put this back in for stats?
    #_(let [toks (:used/design-tokens @css-new)]
      (doseq [tok toks]
        (register-toks+deps tok css-new)))
    (spit-css-layer+profile
     path 
     (css-rule* ":root"
                [(:theme/user-design-tokens @css-new)]
                nil
                nil)
     "user-design-tokens")))


(defn user-design-tokens-profile
  [css-new]
  (let [path "./public/css/user-design-tokens/user-design-tokens.css"]
    (let [toks (:used/design-tokens @css-new)]
      (doseq [tok toks]
        (register-toks+deps tok css-new)))
    (spit-css-layer+profile 
     path 
     (css-rule* ":root"
                (:required/kushi-design-tokens @css-new)
                nil
                nil)
     "user-design-tokens")))


;; TODO - maybe unite this with fn below and pass a map with :release? and :init?
(defn design-tokens-profile-all
  [css-new]
  (let [path      "./public/css/design-tokens/design-tokens.css"
        ;; TODO - need this path-user?
        ;; path-user "./public/css/tokens/user-tokens.css"
        ]
    ;; TODO - put this back in for stats?
    #_(let [toks (:used/design-tokens @css-new)]
      (doseq [tok toks]
        (register-toks+deps tok css-new)))
    (spit-css-layer+profile
     path 
     (css-rule* ":root"
                [kushi.css.build.tokens/design-tokens-by-token-array-map]
                nil
                nil)
     "design-tokens")))


(defn design-tokens-profile
  [css-new]
  (let [path      "./public/css/tokens/tokens.css"
        ;; TODO - need this path-user?
        ;; path-user "./public/css/tokens/user-tokens.css"
        ]
    (let [toks (:used/design-tokens @css-new)]
      (doseq [tok toks]
        (register-toks+deps tok css-new)))
    (spit-css-layer+profile 
     path 
     (css-rule* ":root"
                (:required/kushi-design-tokens @css-new)
                nil
                nil)
     "design-tokens")))


(defn kushi-utility-classes-overrides-profile-all
  []
  (let [path         "./public/css/kushi-utility/utility-overrides.css"
        util-classes (apply array-map
                            (apply concat 
                                   utility-classes/all-classes))     
        css          (string/join 
                      "\n\n"
                      (for [[class v] util-classes
                            :let      [classname (str (name class) "\\!")]]
                        (css-rule* classname
                                   [v]
                                   nil
                                   nil)))]
    (spit-css-layer+profile path css "kushi-utility-overrides")))


(defn kushi-utility-classes-overrides-profile
  [css-new]
  (let [reified (reduce (fn [acc coll]
                          (apply conj acc coll))
                        #{}
                        (-> @css-new :utils :used/kushi-utility))]
    (when (contains? debugging :narrative)
      ;; TODO - Add callout here 
      nil)

    (when (!? :used/kushi-utility (seq reified))
      (let [path        "./public/css/kushi-utility/utility-overrides.css"
            css         (string/join 
                         "\n\n"
                         (for [class reified
                               :let  [classname (name class)]]
                           (css-rule* classname
                                      [(get utility-classes/utility-classes
                                            classname)]
                                      nil
                                      nil)))
            debug-toks? (contains? debugging :design-token-registration)
            used-toks   (when debug-toks?
                          (:used/design-tokens @css-new))]

        ;; This is where design tokens for utility classes get registered.
        ;; They are identified based on the the actual css-rules produced.
        (register-design-tokens! css css-new :kushi-utility)
        #_(when debug-toks? (new-toks-callout "kushi-utility"
                                              nil
                                              used-toks
                                              css-new))
        (spit-css-layer+profile path css "kushi-utility-overrides")))))


(defn kushi-utility-classes-profile-all
  []
  (let [path         "./public/css/kushi-utility/utility.css"
        util-classes (apply array-map
                            (apply concat 
                                   utility-classes/all-classes))     
        css          (string/join 
                      "\n\n"
                      (for [[class v] util-classes
                            :let      [classname (name class)]]
                        (css-rule* classname
                                   [v]
                                   nil
                                   nil)))]
    (spit-css-layer+profile path css "kushi-utility")))


(defn kushi-utility-classes-profile
  [css-new]
  (let [reified (reduce (fn [acc coll]
                          (apply conj acc coll))
                        #{}
                        (-> @css-new :utils :used/kushi-utility))]
    (when (contains? debugging :narrative)
      ;; TODO - Add callout here 
      nil)

    (when (!? :used/kushi-utility (seq reified))
      (let [path        "./public/css/kushi-utility/utility.css"
            css         (string/join 
                         "\n\n"
                         (for [class reified
                               :let  [classname (name class)]]
                           (css-rule* classname
                                      [(get utility-classes/utility-classes
                                            classname)]
                                      nil
                                      nil)))
            debug-toks? (contains? debugging :design-token-registration)
            used-toks   (when debug-toks?
                          (:used/design-tokens @css-new))]

        ;; This is where design tokens for utility classes get registered.
        ;; They are identified based on the the actual css-rules produced.
        (register-design-tokens! css css-new :kushi-utility)
        #_(when debug-toks? (new-toks-callout "kushi-utility"
                                              nil
                                              used-toks
                                              css-new))
        (spit-css-layer+profile path css "kushi-utility")))))


(defn spit-css-imports [coll]
 ;; TODO - grab  skip <public> folder dynamically here or user-supplied filename
 ;; form kushi.edn
 (spit "./public/css/main2.css"
       (str "/* Kushi build system - development build */\n\n"
            (string/join 
             "\n\n"
             (map (fn [[[layer layer-desc] css-files]]
                    #_(when (= layer "kushi-utility") 
                      (? layer css-files))
                    (str 
                     "/* " layer "\n   " layer-desc "\n*/\n"
                     (string/join
                      "\n"
                      (map (fn [m]
                             (str "@import \""
                                  (string/replace (:css-fp m)
                                                  #"^\./public/css/"
                                                  "")
                                  "\";"))
                           css-files))))
                  coll)))
       :append false) )


;; just for dev start --------------------------------
(declare build-sources-callout)
(declare analyzed-callout)
(declare design-token-summary-callout)
(declare spit-filtered-build-sources-with-paths)
;; just for dev end ----------------------------------

(defn- user-design-tokens [theme]
  (some->> theme
           :design-tokens
           (map-indexed (fn [i v]
                          (if (even? i)
                            (name v)
                            (or (css-var-str v)
                                (some-> v (maybe keyword?) name)
                                v))))
           (apply hash-map)))


;; TODO - is there a difference between :used/ and :required/
(def css-new* 
  (array-map
   :base/kushi-design-tokens
   {:desc  "Base design tokens defined in Kushi lib. See kushi.css.build.tokens ns."
    :value tokens/design-tokens-by-token}

   :theme/user-design-tokens 
   {:desc  "Design tokens defined in user theme. These could be overrides for tokens in :tokens/kushi-base, or the user's own custom global design tokens."
    :value {}}

   :theme/user-ui
   {:value {}}

   :user-shared                  
   {:desc "CSS rules defined by user, to be shared across namespaces."
    :value {}}

   :kushi-ui-shared             
   {:desc "CSS rules defined within kushi.ui.*, to be shared across namespaces."
    :value {}}

   :used/kushi-utility           
   {:desc ""
    :value []}

   :used/kushi-ui-shared         
   {:desc ""
    :value []}

   :used/user-shared             
   {:desc ""
    :value []}

   :used/design-tokens           
   {:desc ""
    :value #{}}

   :required/kushi-design-tokens 
   {:desc ""
    :value #{}}

   :required/user-design-tokens   
   {:desc ""
    :value #{}}

   :not-found/design-tokens      
   {:desc "Design tokens referenced in styling, but not defined."
    :value #{}}
    ))

        

(defn hook* [config filtered-build-sources release?]
  (!? 'kushi.css.core/hook*:config config)
  (let [user-design-tokens
        (user-design-tokens (:theme config))

        css-new
        (volatile!
         (assoc (reduce-kv (fn [m k v] (assoc m k (:value v))) {} css-new*)
                :theme/user-design-tokens
                user-design-tokens))

        ;; just for creating a new mock build-state
        ;; _ (spit-filtered-build-sources-with-paths filtered-build-sources)

        reduced-sources
        (reduce (partial analyze-sources css-new)
                []
                filtered-build-sources)

        ;; just for dev
        _ (analyzed-callout reduced-sources css-new)

        kushi-utility-classes-profile
        (if release?
          (kushi-utility-classes-profile css-new)
          (kushi-utility-classes-profile-all)) 

        ;; kushi-utility-classes-overrides-profile
        ;; (if release?
        ;;   (kushi-utility-classes-overrides-profile css-new)
        ;;   (kushi-utility-classes-overrides-profile-all)) 

        design-tokens-profile
        (if release? 
          (design-tokens-profile css-new)
          (design-tokens-profile-all css-new))

        user-design-tokens-profile
        (if release? 
          (user-design-tokens-profile css-new)
          (user-design-tokens-profile-all css-new))

        ret
        (conj reduced-sources
              design-tokens-profile
              kushi-utility-classes-profile
              ;; kushi-utility-classes-overrides-profile
              user-design-tokens-profile
              #_user-shared-classes-profile)]

    (when (contains? debugging :design-token-registration)
      (design-token-summary-callout css-new))

    ret))

(defn- lightning-bundle
  [{:keys [input-path output-path]}
   flags]
  (apply shell (concat ["npx"
                        "lightningcss"]
                       flags
                       [input-path
                        "-o"
                        output-path])))

(defn lightning-bundle-warning [e flags opts]
  (let [body (bling "Error when creating CSS bundle via lightningcss-cli."
                    "\n\n"
                    [:italic.subtle.bold "User options from kushi.edn:"]
                    "\n"
                    (with-out-str (fireworks.core/pprint opts))
                    "\n\n"
                    [:italic.subtle.bold
                     "Flags passed to lightningcss:\n"]
                    (with-out-str (fireworks.core/pprint flags))
                    "\n\n"
                    [:italic.subtle.bold
                     "No css bundle was written."])] 
    (callout
     (merge opts
            {:type        :error
             :label       (str "ERROR: "
                               (string/replace (type e)
                                               #"^class "
                                               "" )
                               " (Caught)")
             :padding-top 0})
     body)))

(def lightning-options-via-user-config
  {:input-path  "./public/css/main2.css"
   :output-path "./public/css/bundle.css"
   :targets     ">= 0.25%"})

(defn bad-bundle-path-warning
  [{:keys [k must-be supplied extra]
    :or {k :input-path}}]
  (bling "The value of "
         [:bold (str k)]
         (or must-be " must be a string, and a valid file path.")
         "\n\n"
         [:italic.subtle.bold "Supplied value:\n"]
         supplied
         extra))

(defn- create-css-bundle []
  (let [opts  lightning-options-via-user-config
        flags (kushi.css.core/lightning-cli-flags
               (dissoc opts :input-path :output-path)
               (-> kushi.css.core/lightning-opts
                   (dissoc :browserslist)
                   (assoc :bundle true)))
        in    (:input-path opts)
        out   (:output-path opts)
        fp?   #(s/valid? ::kushi-specs/css-file-path %)]
    (if (and (fp? in) (fp? out))
      (try (lightning-bundle opts flags)
           ;; TODO - test this exception handler
           (catch Exception e
             (lightning-bundle-warning e flags opts)))
      (let [body
            (cond
              (not (fp? in))
              (bad-bundle-path-warning
               {:supplied [:bold (if (string? in) (str "\"" in "\"") in)]})
              
              (not (.exists? (io/as-file in)))
              (bad-bundle-path-warning
               {:must-be  "must be a path to an existing css file."
                :supplied in
                :extra    (str "\n\n" "File not found")})
              
              (not (fp? out))
              (bad-bundle-path-warning
               {:k        :output-path
                :supplied out
                :extra    (str "\n\n" "File not found")})

              :else
              (bling "Problem with"
                     [:bold " :input-path"]
                     "entry or"
                     [:bold " :output-path"]
                     "entry."))]
        (callout {:type :warning}
                 (bling [:yellow.bold "[kushi.css.build/create-css-bundle]"]
                        "\n\n"
                        body))))))


(defn- user-config [path]
  (some-> path slurp read-string))


(defn write-css-imports* [coll]
  (->> kushi-layers
       (reduce (fn [acc [layer layer-desc]]
                 (conj acc [layer layer-desc] (keep #(get % layer) coll)))
               [])
       (apply array-map)
       spit-css-imports))


(defn write-css-imports [path filtered-build-sources build-state]
  (->  path
       user-config
       (hook* filtered-build-sources build-state)
       write-css-imports*))


(defn hook
  {:shadow.build/stage :compile-prepare}
  [{:keys [:shadow.build/build-id] :as build-state}]
  ;; TODO maybe deleted? and added? should be seqs or nils
  (let [{:keys [init? new-or-deleted? existing-css-changed?]}
        (bs-epoch build-state)]
    (when (or existing-css-changed? new-or-deleted? init?)
      (let [filtered-build-sources
            (filter-build-sources build-state)
            
            release?
            (not= :dev (:shadow.build/mode build-state))]

        ;; If necessary write the css imports chain
        (when (or init? new-or-deleted?)
          (write-css-imports "./kushi.edn" filtered-build-sources release?)
          #_(create-css-bundle))))
  build-state))


(defn hook-dev
  "Just for dev"
  [filtered-build-sources release?]
  ;; TODO maybe deleted? and added? should be seqs or nils
  (write-css-imports "./site/kushi.edn" filtered-build-sources release?)
  #_(create-css-bundle))


;; TODO 


;; 1) Get kushi.design working with new changes
;; TODO
;; 1c) get components section working
;;     start from bottom?


;; 2) Get overrides working 
;;    (problem with lightning not supporting ".wtf\!", or ".wtf!")

;; 3) - Sort out core fns from kushi.css into kushi.core.
;;    - Rename css-new

;; 4) Keep track of changes or deletions in proj via shadow build-state

;; 5) Get layer resolution from ns form working for
;;    - defcss
;;    - css-includes

;; 5) New build reporting system

;; 6) Add comments, docstrings, reporting.

;; 7) (stab at) Figure out how to pull in css from sources with relative file path 

;; Merge branch and BREAK -----------------------------

;; 8) New color system based on oklch

;; 9) New theming system

;; Add to playground
;; - link
;; - divisor
;; - accordian
;; - avatar
;; - label



;; 10)  
;;      Later:
;;      3b) 
;;          - Figure out how to index and save non-base shared styles in .edn or smthg
;;          - kushi-theming
;;          - kushi-ui-shared
;;          - user-shared

;;      3d) Hydrate args that use shared sources
;;      3e) Maybe resave hydrated shared sources without infinite loop?
;;      3f) Write styles

(defn- design-token-summary-callout [css-new]
  (callout {:type  :subtle
            :label (bling 
                    [:italic "Total number of registered design-tokens"])} 
           (bling [:orange.bold (-> @css-new :used/design-tokens count)]))
  (callout {:type  :subtle
            :label (bling 
                    [:italic "Total number of kushi design-tokens"])} 
           (bling [:orange.bold (-> @css-new
                                    :required/kushi-design-tokens
                                    count)]))
  (callout {:type  :subtle
            :label (bling 
                    [:italic "Total number of design tokens not found"])} 
           (bling 
            [:orange.bold (count (:not-found/design-tokens @css-new))]
            "\n\n"
            [:italic
             "These design tokens do not reference kushi library tokens,\n"]
            [:italic
             "or design tokens from the user theme. They are most likely\n"]
            [:italic
             "referencing locally scoped css-vars.\n"]
            "\n"
            (-> (? :data (:not-found/design-tokens @css-new))
                :formatted
                :string))))

(defn analyzed-callout
  "Just for dev"
  [reduced-sources css-new]
  (when (contains? debugging :narrative)  
    (do (stage-callout "ANALYZED SOURCES" {:margin-top    1
                                           :margin-bottom 1})
        (? {:label
            (str 
             "Each source has been analyzed and produces a map with entries\n"
             "corresponding to a named CSS layer, defined in `kushi-layers`.\n\n"
             "Each of these entries has a `:rulesets` entry, which contains\n"
             "data from the macros calls that was used to write the css rules.")}
           (->> reduced-sources
                (reduce (fn [acc m]
                          (conj acc
                                (walk/postwalk (fn [x]
                                                 (if (seq? x) '(...) x))
                                               m)))
                        [])))
        (!? (get-in @css-new [:sources 'mvp.browser])))))

(defn build-sources-callout
  "Just for dev"
  [filtered-build-sources]
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

(defn hydrate-paths-into-files
  "Just for dev"
  [m]
  (->> m
       (reduce (fn [acc [k v]]
                 (conj acc
                       [k (-> v
                              (assoc :file (-> v :file clojure.java.io/file)))]))
               [])
       (sequence cat)
       (apply array-map)))

;; Dev
;; TODO - create a fake :build.state/mode here?
#_(let [release?               false
      filtered-build-sources (-> "./site/filtered-build-sources.edn"
                                 slurp
                                 read-string
                                 hydrate-paths-into-files
                                 (hook-dev release?))])

(defn- spit-filtered-build-sources-with-paths 
  "Just for dev"
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
              :append false)]))

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
