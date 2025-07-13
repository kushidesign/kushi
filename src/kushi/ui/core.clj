(ns ^:dev/always kushi.ui.core
  (:require
   [fireworks.core :refer [? !? ?> !?>]]
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


(defn with-schemas [opts]
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
                         (get variants 
                              (keyword (str (name schema) "/set")))

                         ; for surfacing warning
                         :else schema))]
                 (assoc m k (assoc v :schema schema))))
             {}
             opts))

(defmacro validate [args]
  (let [source-form (some-> &env :root-source-info :source-form)
        mm          (nth source-form 2 nil)
        fn-sym      (nth source-form 1 nil)
        opts        (when (map? mm) (:opts mm))
        ns-name     (some-> &env :ns :name str)
        fq-fn-name  (str ns-name "/" fn-sym)]
    (when (seq opts)
      (let [opts    (with-schemas opts)
            ;; opts-unreserved-ks (mapv #(keyword (subs (name %) 1)) (keys opts))
            ]
        `(let [schema# {:ns/name       ~ns-name
                        :fn/name       (quote ~fn-sym)
                        :fn/fq-name    ~fq-fn-name
                        ;; :opts/unreserved-ks ~opts-unreserved-ks
                        :opts/quoted   (quote ~opts)
                        :opts/expanded ~opts}]
           (kushi.ui.core/validate* schema# ~args))))))


(def defui-syms
  '{&props         props
    &attrs         attrs
    &children      children
    &data-ks-attrs data-ks-attrs})


(defn dbg
  "Wrapper for debugging defmacro defui"
  [label x]
  (? :no-file 
     {:label                 label
      :display-metadata?     false
      :non-coll-length-limit 21}
     x))


(def debug-defui 'box)


(defn- props-from-families* [m dbg]
  (some->> (:props/family m)
           (dbg 'family-props)
           (reduce (fn [vc k]
                     (apply conj
                            vc
                            (k props/prop-families)))
                   [])
           (dbg 'constituent-prop-keys)
           (select-keys props/props)
           #_(dbg 'hydrated-constituent-prop-map)))

(defn- props-trimmed* [merged-props dbg]
  (dbg 'props-trimmed
       (reduce-kv (fn [m k v]
                    (assoc m k (dissoc v :desc :schema :required? :data)))
                  {}
                  merged-props)))

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
  
  (let [dbg
        (if (= sym debug-defui) dbg (fn [_ x] x))

        _
        (when (= sym debug-defui)
          (dbg 'sym sym)
          (dbg 'm m))

        ;; groups of props rolled up into families 
        props-from-families
        (props-from-families* m dbg)

        ;; props shared across components
        props-from-shared 
        (dbg 'props-from-shared (select-keys props/props (:props/shared m)))

        
        ;; props specific/unique to the component
        user-props
        (dbg 'user-props (:props m))

        ;; merge all the props
        merged-props        
        (dbg 'merged-props
             (merge user-props props-from-shared props-from-families))

        ;; trims the props to only give data-ks-attrs what it needs at runtime,
        ;; which are the :default and and :data-ks? :data-ks (data trans fn) entries
        props-trimmed
        (props-trimmed* merged-props dbg)
        

        props-keys   
        (into [] (keys merged-props))


        ;; TODO - process body here for different frameworks
        ;; TODO - maybe wrap body here if elevated is in the mix?
        body        
        body


        ;; All the following symbols are available within the body of the macro

        ;; &props         - map of props defined via the :props or :props/family, extracted from the second arg (map) to defui
        ;; &attrs         - map of html attributes extracted from the second arg (map) to defui
        ;; &data-ks-attrs - map of data-ks-* attributes. Some/most of the kushi-specific theming props need to end up as data-ks-* attributes 
        ;; &children      - collection of children passed to components
        ks          
        '[&props &attrs &data-ks-attrs &children args]]
    
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
           (when (= (quote ~sym) (quote ~debug-defui))
             (let [data-ks-debug# :foobar]
               (when (some-> extracted*# :attrs :data-ks-debug (= data-ks-debug#))
                (? {:extracted* extracted*#
                    :extracted  extracted#}))))

           ;; TODO - Try to validate props here.
           (!? "Validation goes ehreeHHEERRRREE"))
         ~body))))
