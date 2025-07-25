(ns ^:dev/always kushi.ui.core
  (:require
   [fireworks.core :refer [? !? ?> !?>]]
   [clojure.walk :as walk]
   [kushi.ui.util :refer [keyed]]
   [kushi.ui.variants :as props :refer [variants-by-custom-opt-key variants]]))


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
                           'any?)
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
                             (str "/set")
                             keyword
                             (->> (get variants)))

                         ; for surfacing warning
                         :else schema))]
                 (assoc m k (assoc v :schema schema))))
             {}
             props))


(defn dbg
  "Wrapper for debugging defmacro defui"
  [label x]
  (? :no-file 
     {:label                 label
      :display-metadata?     false
      :non-coll-length-limit 21}
     x))


(def debug-defui #_nil 'box)


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
        
        ;; props-with-schemas
        ;; (with-schemas merged-props)
        ]

    

    `(defn ~sym 
       ~m
       [& args#]
       (let [extracted*#    (kushi.ui.core/extract args# ~props-keys)
             data-ks-attrs# (kushi.ui.core/data-ks-attrs (:props extracted*#)
                                                         ~props-trimmed)
             extracted#     {:&props         (:props extracted*#)
                             :&attrs         (:attrs extracted*#)
                             :&data-ks-attrs data-ks-attrs#
                             :&children      (:children extracted*#)
                             :args           args#}
             {:keys ~ks}    extracted#]

         (when ^boolean js/goog.DEBUG
           ;; Internal dev only, debugging specific instance of component, comment this block out if not debugging
           ;; 1) Set kushi.core/debug-defui to the name (symbol) of the component you want to debug
           ;; 2) At the call-site in consuming app, give the instance of that component a unique :data-ks-debug value in the attrs map
           ;; 3) Set the data-ks-debug# binding below to match the value you chose in step 2 
           (when (= (quote ~sym) (quote ~debug-defui))
             (let [data-ks-debug# :foobar]
               (when (some-> extracted*#
                             :attrs
                             :data-ks-debug
                             (= data-ks-debug#))
                 (!? "extracted" extracted#)
                 (!? "extracted*" extracted*#))))

           ;; TODO - Try to validate props here.
           (? ~m)
           #_(validate {:args args#}))
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
