(ns ^:dev/always kushi.ui.core
  (:require
   [fireworks.core :refer [? !? ?> !?>]]
   [clojure.string :as string]
   [clojure.walk :as walk]
   [malli.core]
   [kushi.ui.variants :as variants]
   [kushi.ui.util :refer [keyed]]
   [kushi.ui.variants :as props :refer [enum-variants-by-custom-opt-key variants-by-custom-opt-key variants]]))


;; TODO - document why is this needed vs normal fn
;; For now this is unused
(defmacro material-symbol-or-icon-span
  [{:keys [icon-name icon-style icon-filled?]}]
  (let [icon-font  "material-symbols" ;; <- TODO: from user config
        ]
    `(let [style#      (if (clojure.core/contains? #{:outlined :rounded :sharp} ~icon-style)
                         ~icon-style
                         :outlined)
           icon-style# (str ~icon-font "-" (name style#))
           icon-fill#  (when ~icon-filled? :material-symbols-icon-filled)]
       (into [:span {:class [icon-style# icon-fill#]}]
             ~icon-name))))


(def debug? (atom false))


(defn with-schemas
  "Expects a map of props, from the :props entry in the metadata map of the
   component rendering function. This metadata map originates from the 2nd
   arg to the kushi.ui.core/defui.
   
   Each prop map's value is a map, and may contain a :schema entry. If a :schema
   entry is not present, the map will potentially be given a :schema entry,
   pulled from the `kushi.ui.variants` namespace. If no schema is found, a value
   of any? will be used for the schema"
  [props]
  (reduce-kv (fn [m k {:keys [schema]
                       :as   v}]
               (let [schema 
                     (if-not schema
                       ; lookup by opt key e.g. :custom
                       (or (k variants-by-custom-opt-key)        
                           :any)
                       (cond 
                         ; just a schema function e.g. boolean?
                         (symbol? schema)                                 
                         schema

                         ; set literal for enum e.g. #{:rounded :sharp :pill}
                         (set? schema)                                    
                         schema

                         ; kw such as :kushi.ui.variants/colors}
                         (keyword? schema)                                
                         (-> schema
                             name
                             (str "/enum")
                             keyword
                             (->> (get variants)))

                         ; for surfacing warning
                         :else schema))]
                 (assoc m k (assoc v :schema schema))))
             {}
             props))

(def malli-type-schema-keywords
  (->> (malli.core/type-schemas)
       keys
       (into #{})))

(def malli-predicate-schema-symbols
  (->> (malli.core/predicate-schemas)
       keys
       (filter #(symbol? %))
       (into #{})))

(defn with-schemas-2
  "Version for malli"
  [props]
  (reduce-kv (fn [m k {:keys [schema] :as v}]
               (let [debug-schema?
                     (and @debug? (= k :inert?))

                     schema 
                     (if-not schema
                       ; get set from stock e.g. 
                       (or (k enum-variants-by-custom-opt-key)        
                           :any)
                       (cond 
                         ; e.g. boolean? -> :boolean
                         (and (symbol? schema)
                              (contains? malli-predicate-schema-symbols schema))                                 
                         (-> schema name (string/replace #"\?$" "") keyword)

                         ; set literal for enum e.g. #{:rounded :sharp :pill}
                         (set? schema)                                    
                         (into [:enum] schema)

                         ; kw such as :kushi.ui.variants/colors}
                         (keyword? schema)                                
                         (or (when (contains? malli-type-schema-keywords
                                              schema)
                               schema)
                             (-> schema
                                 name
                                 (str "/enum")
                                 keyword
                                 (->> (get variants)))
                             :any)

                         :else
                         :any))]

                 (assoc m 
                        k 
                        (assoc v
                               :schema 
                               (!? {:when  debug-schema?
                                   :label [k v]} schema)))))
             {}
             (!? {:when @debug?} props)))

(defn required-props* [props-with-schemas]
  (into [] 
        (keep (fn [[k v]]
                (when (true? (:required? v)) k))
              props-with-schemas)))

(defn malli-schema*
  [props-with-schemas]
  (reduce-kv 
   (fn [acc k {:keys [required? schema]
               :as   v}]
     (let [schema (if (set? schema)
                    (into [:enum] schema)
                    schema)]
       (conj acc 
             (if (true? required?)
               [k schema]
               [k {:optional true} schema]))))
   [:map]
   (!? {:when (= @debug? 'box)}
       props-with-schemas)))


(defn dbg
  "Wrapper for debugging defmacro defui"
  [label x]
  (? :no-file 
     {:label                 label
      :display-metadata?     false
      :non-coll-length-limit 21}
     x))


(def debug-defui nil #_box)


(defn- props-from-families* [m dbgf]
  (some->> (:props/family m)
           (dbgf 'family-props)
           (reduce (fn [vc k]
                     (apply conj
                            vc
                            (k props/prop-families)))
                   [])
           (dbgf 'constituent-prop-keys)
           (select-keys props/props)
           #_(dbg 'hydrated-constituent-prop-map)))

(defn- props-trimmed* [merged-props dbgf]
  (dbgf 'props-trimmed
       (reduce-kv (fn [m k v]
                    (assoc m k (dissoc v :desc :schema :required? :data)))
                  {}
                  merged-props)))

(defn- merged-props* 
  [m dbgf]
  (let [
        ;; groups of props rolled up into families 
        props-from-families
        (props-from-families* m dbgf)

        ;; props shared across components
        props-from-shared   
        (dbgf 'props-from-shared (select-keys props/props (:props/shared m)))

        
        ;; props specific/unique to the component
        user-props          
        (dbgf 'user-props (:props m))]

    ;; merge all the props
    (dbgf 'merged-props
         (merge user-props props-from-shared props-from-families))))


;; annotate this
(defmacro validate
  [{:keys [args]}]
  (let [
        ;; This pulls the ui component fn name and meta map from the root source form 
        [_ fn-sym mm] (some-> &env :root-source-info :source-form)
        !dbg          (fn [_ x] x)
        dbg           (if (= fn-sym debug-defui) dbg !dbg)
        props         (when (map? mm) (:props mm))
        ns-name       (some-> &env :ns :name str)
        fq-fn-name    (str ns-name "/" fn-sym)
        dbg?          (= 'box fn-sym)]
    #_(when dbg?
      (? (str "defmacro validate " fn-sym)
         (keyed [mm          
                 fn-sym      
                 props        
                 ns-name     
                 fq-fn-name])))
    (when (seq props)
      ;; Augment the props with schemas from kushi.ui.schema
      (let [props (with-schemas-2 props)
            ;; props-unreserved-ks (mapv #(keyword (subs (name %) 1)) (keys props))
            ]
        (when dbg? (? {:label "defmacro validate, props with-schemas"} props))
        #_(? "defmacro validate, props with-schemas" fn-sym)
        `(do (!? "validate, args" ~args)
             (kushi.ui.core/validate* 
              (!? "validate, m"
                 {:ns/name        ~ns-name
                  :fn/name        (quote ~fn-sym)
                  :fn/fq-name     ~fq-fn-name
                  ;; :props/unreserved-ks ~props-unreserved-ks
                  :props/quoted   (quote ~props)
                  :props/expanded ~props})
              ~args))))))


(def defui-syms
  '{&props         props
    &attrs         attrs
    &children      children
    &data-ks-attrs data-ks-attrs})


(defmacro defui
  [sym  ; <- Symbol, name of component

   m    ; <- map e.g.
        ;    {:doc          "xyz component ..."
        ;     :props/family [...]
        ;     :props        {...
        ;                    :my-custom-prop {:schema  string?
        ;                                     :desc    "prop desc"
        ;                                     :default "foo"}
        ;                    ...}}

   _    ; <- The args vector, should always be [& args]

   body ; <- body of component
   ]

  (reset! debug? (if (= sym 'box) true false))

  (let [!dbgf
        (fn [_ x] x)

        dbgf
        (if (= sym debug-defui) dbg !dbgf)

        _
        (when (= sym debug-defui)
          (dbgf 'sym sym)
          (dbgf 'm m))

        merged-props
        (merged-props* m dbgf)

        ;; trims the props to only give data-ks-attrs what it needs at runtime,
        ;; which are the :default and and :data-ks? :data-ks (data trans fn) entries
        props-trimmed
        (props-trimmed* merged-props dbgf)
        

        props-keys   
        (into [] (keys merged-props))


        ;; TODO - process body here for different frameworks
        ;; TODO - maybe wrap body here if elevated is in the mix?
        body        
        (do 
          #_(when (= sym 'box) 
              (? (walk/postwalk (fn [x] (if (= (and (list? x) (first x)) '$)
                                          (into [] (rest x))
                                          x))
                                body)))
          body)


        ;; All the following symbols are available within the body of the macro
        
        ;; &props         - map of props defined via the :props or :props/family, extracted from the second arg (map) to defui
        ;; &attrs         - map of html attributes extracted from the second arg (map) to defui
        ;; &data-ks-attrs - map of data-ks-* attributes. Some/most of the kushi-specific theming props need to end up as data-ks-* attributes 
        ;; &children      - collection of children passed to components
        ks          
        '[&props &attrs &data-ks-attrs &children args]
        
        mm
        (let [
              ;; props-with-schemas-v1   (with-schemas merged-props)
              props-with-schemas (with-schemas-2 merged-props)
              required-props     (required-props* props-with-schemas)
              malli-schema       (malli-schema* props-with-schemas)]
          (assoc m 
                 :props
                 props-with-schemas
                 :required-props 
                 required-props
                 :malli-schema
                 malli-schema))

        [_ fn-sym]
        (some-> &env :root-source-info :source-form)
        
        fn-info
        (assoc (meta &form)
               :fn
               fn-sym
               :fn/name
               (str fn-sym)
               :ns/name
               (some-> &env :ns :name str))]

    `(defn ~sym 
       ~mm
       [& args#]
       (let [extracted*#    (kushi.ui.core/extract args# ~props-keys)
             data-ks-attrs# (kushi.ui.core/data-ks-attrs (:props extracted*#)
                                                         ~props-trimmed)
             props#         (dissoc (:props extracted*#) :ns)
             extracted#     {:&props         props#
                             :&attrs         (:attrs extracted*#)
                             :&data-ks-attrs data-ks-attrs#
                             :&children      (:children extracted*#)
                             :args           args#}
             {:keys ~ks}    extracted#]

         (when ^boolean js/goog.DEBUG
           ;; Internal dev only, debugging specific instance of component, comment this block out if not debugging
           ;; 1. Set kushi.core/debug-defui to the name (symbol) of the component you want to debug
           ;; 2. At the call-site in consuming app, give the instance of that component a unique :data-ks-debug value in the attrs map
           ;; 3. Set the data-ks-debug# binding below to match the value you chose in step 2 
           (when (= (quote ~sym) (quote ~debug-defui))
             (let [data-ks-debug# :foobar]
               (when (some-> extracted*#
                             :attrs
                             :data-ks-debug
                             (= data-ks-debug#))
                 (!? "extracted" extracted#)
                 (!? "extracted*" extracted*#))))

           ;; TODO - Try to validate props here.
           #_(? ~mm)
           #_(validate {:args args#})
           
           (kushi.ui.core/validate*2
            (assoc ~mm 
                   :fn-info
                   ~fn-info
                   :props
                   props#
                   :data-ks-ns
                   (:data-ks-ns data-ks-attrs#))))
         ~body))))

#_(defmacro validate
  [{:keys [args]}]
  (let [
        ;; This pulls the ui component fn name and meta map from the root source form 
        [_ fn-sym mm] (some-> &env :root-source-info :source-form)
        !dbg          (fn [_ x] x)
        dbg           (if (= fn-sym debug-defui) dbg !dbg)
        props         (when (map? mm) (:props mm))
        ns-name       (some-> &env :ns :name str)
        fq-fn-name    (str ns-name "/" fn-sym)
        dbg?          (= 'box fn-sym)]
    (when dbg?
      (? (str "defmacro validate " fn-sym)
         (keyed [mm          
                 fn-sym      
                 props        
                 ns-name     
                 fq-fn-name])))
    (when (seq props)
      ;; Augment the props with schemas from kushi.ui.schema
      (let [props (with-schemas props)
            ;; props-unreserved-ks (mapv #(keyword (subs (name %) 1)) (keys props))
            ]
        (when dbg? (? {:label "defmacro validate, props with-schemas"} props))
        #_(? "defmacro validate, props with-schemas" fn-sym)
        `(do (!? "validate, args" ~args)
             (kushi.ui.core/validate* 
              (? "validate, m"
                 {:ns/name        ~ns-name
                  :fn/name        (quote ~fn-sym)
                  :fn/fq-name     ~fq-fn-name
                  ;; :props/unreserved-ks ~props-unreserved-ks
                  :props/quoted   (quote ~props)
                  :props/expanded ~props})
              ~args))))))

(defmacro fn->defui [form]
  (let [[_ sym {:keys [summary desc opts]} args-vc body] form
        props (reduce-kv
               (fn [m k v]
                 (assoc m k (if-let [m (get variants/props k)]
                              (dissoc m :schema)
                              v)))
               {}
               opts)
        mm {:doc desc :summary summary :props props}
        ret (list 'defui ^:public sym mm args-vc body)]
    (!? (keyed [sym mm args-vc body]))
    (? {:non-coll-length-limit 500} ret)
    `nil))
