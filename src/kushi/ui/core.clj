(ns ^:dev/always kushi.ui.core
  (:require
   [fireworks.core :refer [? !? ?> !?>]]
   [bling.core :refer [callout bling]]
   [bling.explain :refer [explain-malli]]
   [bling.hifi :refer [hifi]]
   [clojure.string :as string]
   [clojure.walk :as walk]
   [malli.core]
   [kushi.util :refer [partition-by-pred as-str]]
   [kushi.ui.variants :as variants]
   [kushi.ui.util :refer [keyed]]
   [kushi.ui.variants :as props]
   [kushi.ui.decoration :as decoration]
   [kushi.ui.extract :as extract]
   [malli.core :as m]))

(def ^:private html-attrs 
  #{:bgcolor :accept :accept-charset :access-key :action :allow-full-screen :allow-transparency :alt :async :auto-complete :auto-focus :auto-play :capture
    :cell-padding :cell-spacing :challenge :char-set :checked :cite :class :class-name :cols :col-span :content :content-editable :context-menu :controls :controls-list
    :coords :cross-origin :data :date-time :default :defer :dir :disabled :download :draggable :enc-type :form :form-action :form-enc-type :form-method
    :form-no-validate :form-target :frame-border :headers :height :hidden :high :href :href-lang :html-for :http-equiv :icon :id :input-mode :integrity
    :is :key-params :key-type :kind :label :lang :list :loop :low :manifest
    :margin-height :margin-width :max :max-length :media :media-group :method :min :min-length :multiple :muted :name :no-validate :nonce :open :optimum :pattern :placeholder
    :poster :preload :profile :radio-group :read-only :rel :required :reversed :role :rows :row-span :sandbox :scope :scoped :scrolling :seamless :selected :shape :size :sizes
    :span :spell-check :src :src-doc :src-lang :src-set :start :step :style :summary :tab-index :target :title :type :use-map :value :width :wmode :wrap
    ; React specific 
    :ref :key})

(defn ^:private issue-html-attribute-name-clash-warnings
  [props-keys fn-info] 
  (doseq [k props-keys]
    (when (contains? html-attrs k)
      (callout {:type        :warning
                :label-theme :marquee
                :padding-top 1
                :side-label  (:fn/loc-str fn-info)}
               (str "HTML attribute name clash"
                    "\n\n"
                    (bling.hifi/hifi k {:find {:pred  #(= % k)
                                               :class :highlight-error-underlined}})
                    "\n\n"
                    "You might want to choose a different name\n"
                    "for your custom attribute."
                    "\n\n"
                    (bling [{:href "https://developer.mozilla.org/en-US/docs/Web/HTML/Reference/Attributes"}
                            "cmd+click to view MDN docs ↗"])
                    )))))

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


(def malli-type-schema-keywords
  (->> (malli.core/type-schemas)
       keys
       (into #{})))

(def malli-predicate-schema-symbols
  (->> (malli.core/predicate-schemas)
       keys
       (filter #(symbol? %))
       (into #{})))

(defn with-schemas
  "Expects a map of props, from the :props entry in the metadata map of the
   component rendering function. This metadata map originates from the 2nd
   arg to the kushi.ui.core/defui.
   
   Each prop map's value is a map, and may contain a :schema entry. If a :schema
   entry is not present, the map will potentially be given a :schema entry,
   pulled from the `kushi.ui.variants` namespace. If no schema is found, a value
   of any? will be used for the schema"
  [props]
  (reduce-kv (fn [m k {:keys [schema] :as v}]
               (let [debug-schema?
                     (and @debug? (= k :choices))

                     schema 
                     (if-not schema
                       ; get set from stock e.g. 
                       (or (k props/enum-variants-by-custom-opt-key)        
                           :any)
                       (cond 
                         ;; Assumes valid malli schema e.g. [:or [:vector :string] [:vector :map]]
                         (vector? schema)
                         schema

                         ; e.g. boolean? -> :boolean
                         (and (symbol? schema)
                              (contains? malli-predicate-schema-symbols schema))                                 
                         (-> schema name (string/replace #"\?$" "") keyword)

                         ; set literal for enum e.g. #{:rounded :sharp :pill}
                         (set? schema)                                    
                         (into [:enum] schema)

                         ; kw such as :kushi.ui.variants/colors
                         (keyword? schema)                                
                         (or (when (contains? malli-type-schema-keywords
                                              schema)
                               schema)
                             (-> schema
                                 name
                                 (str "/enum")
                                 keyword
                                 (->> (get props/variants)))
                             :any)

                         :else
                         :any))]

                 (assoc m 
                        k 
                        (assoc v
                               :schema 
                               (!? {:when  debug-schema?
                                    :label [k v]}
                                   schema)))))
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


(defn- defaults-by-prop* [merged-props dbgf]
  (dbgf 'defaults-by-prop*
       (reduce-kv (fn [m k v]
                    (assoc m
                           k
                           (merge (dissoc v :desc :schema :required? :data)
                                  (when (= :boolean (:schema v))
                                    {:boolean? true}))))
                  {}
                  merged-props)))


(defn conflicting-props-warning [user-props fn-info]
  (!? fn-info)
  (when-let [prop-name-conflicts
             (some->> user-props
                      keys
                      seq
                      (filter #(contains? props/props %))
                      (into []))]
    (when (seq prop-name-conflicts)
      (callout {:type        :warning
                :padding-top 1
                :label-theme :marquee
                :side-label  (str (:ns/name fn-info)
                                  "/"
                                  (:fn/name fn-info)
                                  ":"
                                  (:line fn-info)
                                  ":"
                                  (:column fn-info))}
               "The following " (hifi :prop) " entries conflict with shared props"
               "\n"
               "defined in " (bling [:blue 'kushi.ui.variants/props]) ":"
               "\n\n"
               (string/join "\n" (mapv #(hifi %) prop-name-conflicts))
               "\n\n"
               "You should instead use a "  (bling [:blue :props/shared]) " entry like this:"
               "\n\n"
               (hifi {:props/shared prop-name-conflicts})))))


#_(try (malli.core/validate [:map vc] 42)
                                  (catch js/Object
                                         err
                                    (when (= ":malli.core/invalid-schema" (.-message e))
                                      (callout {:label       "Invalid Malli Schema"
                                                :type        :error
                                                :padding-top 1
                                                :side-label  (str (:ns/name fn-info) 
                                                                  ":" 
                                                                  (:line fn-info) 
                                                                  ":" 
                                                                  (:column fn-info))
                                                :label-theme :pipe}
                                               (bling [:italic "Component:"])
                                               "\n\n"
                                               (hifi (symbol (:fn/name fn-info)) {:margin-inline-start 2})
                                               "\n\n\n"
                                               (bling [:italic "Prop:"])
                                               "\n\n"
                                               (hifi k {:margin-inline-start 2})
                                               "\n\n\n"
                                               (bling [:italic "Invalid Schema:"])
                                               "\n\n"
                                               (hifi (some-> custom-props k :schema) {:margin-inline-start 2}))
                                      )))

(defn- validate-custom-prop-schema 
  [k v fn-info]
  (try (do (!? {:when (= k :close-button?)} (some-> v :schema (m/validate 42)))
           true)
       (catch Throwable
              e
         (when (= ":malli.core/invalid-schema" 
                  (.getMessage e))
           (callout
            {:label       "Invalid Malli Schema"
             :type        :error
             :padding-top 1
             :side-label  (str (:ns/name fn-info) 
                               ":" 
                               (:line fn-info) 
                               ":" 
                               (:column fn-info))
             :label-theme :marquee}
            (bling [:italic "Component:"])
            "\n\n"
            (hifi (symbol (:fn/name fn-info))
                  {:margin-inline-start 2})
            "\n\n\n"
            (bling [:italic "Custom prop:"])
            "\n\n"
            (hifi k {:margin-inline-start 2})
            "\n\n\n"
            (bling [:italic "Invalid Schema:"])
            "\n\n"
            (hifi (:schema v) 
                  {:margin-inline-start 2}))))))


(defn- merged-props* 
  [{supplied-user-props :props :as m}
   fn-sym
   fn-info
   dbgf]
  (dbgf fn-sym fn-info)
  (let [
        ;; groups of props rolled up into families 
        props-from-families
        (dbgf 'props-from-families (props-from-families* m dbgf))

        ;; check the shape of :props/shared and warn with malli-explain
        [shared-props-clean malformed-shared-props]
        (dbgf 'shared-props-clean
              (partition-by-pred 
               #(m/validate [:or props/shared-props-enum
                             [:tuple props/shared-props-enum :map]] %)
               (:props/shared m)))

        _
        (when (seq malformed-shared-props)
          (explain-malli [:vector [:or
                                   props/shared-props-enum
                                   [:tuple props/shared-props-enum :map]]]
                         (:props/shared m)
                         {:display-schema? false
                          ;; :padding-top     1
                          :callout-opts {:side-label (:fn/loc-str fn-info)}
                          }))

        ;; props shared across components, partition out ones with overrides on the default val
        [shared-prop-overrides* props-from-shared*]   
        (dbgf 'props-from-shared-partitioned 
              (partition-by-pred #(m/validate [:tuple :keyword :map] %)
                                 (:props/shared m)))

        ;; add the keys from the overrides back in the keys vector
        props-from-shared*
        (dbgf 'props-from-shared*
              (apply conj 
                     (into [] (filter keyword? props-from-shared*)) 
                     (reduce (fn [acc [k]] (conj acc k))
                             []
                             shared-prop-overrides*)))

        ;; resolve keys vector into map
        props-from-shared
        (dbgf 'props-from-shared (select-keys props/props props-from-shared*))

        ;; convert overrides from tuple form into map
        shared-prop-overrides
        (dbgf 'shared-prop-overrides*
              (into {} shared-prop-overrides*))

        ;; props specific/unique to the component, removed overrides for shared props on :default value
        user-props*
        (dbgf 'user-props* 
              (apply dissoc
                     supplied-user-props 
                     (keys shared-prop-overrides)))

        ;; props specific/unique to the component
        validated-user-props-with-schemas
        (dbgf 'validated-user-props-with-schemas
              (reduce-kv
               (fn [m k v]
                 (assoc m
                        k 
                        (if (validate-custom-prop-schema k v fn-info)
                          v
                          (assoc v :schema :any))))
               {}
               (!? {:when @debug?} user-props*)))

        merged-props*
        (dbgf 'merged-props*
              (merge (!? {:when @debug?} validated-user-props-with-schemas)
                     props-from-shared
                     props-from-families))
        
        merged-props-with-default-overrides
        (dbgf 'merged-props-with-default-overrides
              (reduce-kv (fn [m k v]
                           (assoc-in m [k :default] (:default v)))
                         merged-props*
                         shared-prop-overrides))
        
        user-props-with-defaults-values
        (reduce-kv (fn [m k v]
                     (if (not (nil? (:default v)))
                       (assoc m k (:default v))
                       m))
                   {}
                   validated-user-props-with-schemas)]

    (!? (conflicting-props-warning user-props* fn-info))

    ;; merge all the props
    (!? {:when @debug?}
       {:merged-props                   merged-props-with-default-overrides
        :user-props-with-default-values user-props-with-defaults-values})))



(defmacro defui
  [sym  ; <- Symbol, name of component

   m    ; <- Function metadata map with docs and props e.g.
        ;    {
        ;     The docstring
        ;
        ;     :doc "Buttons are primitive UI components that ..."
        ;
        ;
        ;
        ;     Predefined lists of stock props. List of keywords corresponding to
        ;     entries in variants/prop-families
        ;
        ;     :props/family [...] 
        ;
        ;
        ;
        ;     List of keywords corresponding to entries in variants/props.
        ;
        ;     :props/shared [...]
        ;
        ;
        ;
        ;     Props that are unique to the component, each an entry of:
        ;
        ;     [:keyword [:map
        ;                [:schema {:optional? true}]
        ;                [:desc :string]
        ;                [:default {:optional? true} :any]]]
        ;
        ;     :props {...
        ;             :my-custom-prop {:schema  string?
        ;                              :desc    "prop desc"
        ;                              :default "foo"}
        ;             ...}}
        ;
        ;
   _    ; <- The args vector, should always be [& args]
        ;
        ;
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

        [_ fn-sym]
        (some-> &env :root-source-info :source-form)
        
        fn-info
        (let [m (assoc (meta &form)
                       :fn
                       fn-sym
                       :fn/name
                       (str fn-sym)
                       :ns/name
                       (some-> &env :ns :name str))]
          (assoc m :fn/loc-str (str (:ns/name m)
                                    "/"
                                    (:fn/name m)
                                    ":"
                                    (:line m)
                                    ":"
                                    (:column m))))

        {:keys [merged-props user-props-with-default-values]}
        (merged-props* m fn-sym fn-info dbgf)

        props-with-schemas
        (with-schemas merged-props)

        ;; trims the props to only give data-ks-attrs what it needs at runtime,
        ;; which are the :default and :data-ks? :data-ks (data trans fn) entries
        defaults-by-prop
        (!? {:when @debug?} (defaults-by-prop* props-with-schemas dbgf))


        data-ks-attrs-map-with-defaults
        (!? 'data-ks-attrs-map-with-defaults {:when @debug?} 
         (kushi.ui.extract/data-ks-attrs {} defaults-by-prop :comptime))

        props-keys   
        (let [ks (keys merged-props)]
          (issue-html-attribute-name-clash-warnings ks fn-info)
          (into [] ks))

        
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
        
        ;; &props         - map of props defined via the :props or :props/family,
        ;;                  extracted from the second arg (map) to defui
        
        ;; &attrs         - map of html attributes extracted from the second arg
        ;;                  (map) to defui
        
        ;; &data-ks-attrs - map of data-ks-* attributes. Some/most of the
        ;;                  kushi-specific theming props need to end up as
        ;;                  data-ks-* attributes 
        
        ;; &children      - collection of children passed to components
        
        ks          
        '[&props &attrs &data-ks-attrs &children args]
        
        ;; This metadata fn map is used for generation of docs when component is
        ;; included 
        mm
        (let [props-with-schemas props-with-schemas
              malli-schema       (malli-schema* props-with-schemas)]
          (assoc m 
                 :props
                 props-with-schemas
                 :props/custom
                 (:props m)
                 :malli-schema
                 malli-schema))]

    `(defn ~sym 
       ~mm
       [& args#]
       (let [extracted*#           (!? (kushi.ui.core/extract args# ~props-keys ~fn-info))

             props->data-ks-attrs# (!?
                                    (kushi.ui.core/data-ks-attrs 
                                     (:props extracted*#)
                                     (select-keys ~defaults-by-prop (-> extracted*# :props keys))
                                     :runtime))

            ;;  data-ks-attrs_#        (? (kushi.ui.core/data-ks-attrs 
            ;;                         (:props extracted*#)
            ;;                         ~defaults-by-prop))

             data-ks-attrs#        (merge ~data-ks-attrs-map-with-defaults
                                          props->data-ks-attrs#)

            ;;  _#                    (? (= data-ks-attrs_# data-ks-attrs#))            

             props#                (merge (dissoc (:props extracted*#) :at)
                                          ~user-props-with-default-values)
             extracted#            {:&props         props#
                                    :&attrs         (merge (:attrs extracted*#)
                                                           data-ks-attrs#)
                                    :&data-ks-attrs data-ks-attrs#
                                    :&children      (:children extracted*#)
                                    :args           args#}
             {:keys ~ks}  extracted#]

        ;; Dev-only runtime malli validation ===================================
         
         (when ^boolean js/goog.DEBUG
           
          ;;  ------------------------------------------------------------------
          ;;  Internal dev only, debugging specific instance of component ------
          ;;  comment this block out if not debugging
           
          ;;  1. Set kushi.core/debug-defui to the name (symbol) of the
          ;;     component you want to debug.
           
          ;;  2. At the call-site in consuming app, give the instance of that
          ;;     component a unique :data-ks-debug value in the attrs map.
           
          ;;  3. Set the data-ks-debug# binding below to match the value you
          ;;     chose in step 2.
           
           #_(when (= (quote ~sym) (quote ~debug-defui))
               (let [data-ks-debug# :foobar]
                 (when (some-> extracted*#
                               :attrs
                               :data-ks-debug
                               (= data-ks-debug#))
                   (!? "extracted" extracted#)
                   (!? "extracted*" extracted*#))))
           
          ;;  End of internal dev only, debugging specific instance of component
          ;;  ------------------------------------------------------------------
           
          ;;  Dev-only, this is where runtime malli validation happens
           (kushi.ui.core/validate*2
            (assoc ~mm 
                   :fn-info
                   ~fn-info
                   :props
                   props#
                   :data-ks-at
                   (:data-ks-at data-ks-attrs#))))
         
        ;; End of dev-only runtime malli validation ============================
         
         ~body))))


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



;; annotate this
;; (defmacro validate
;;   [{:keys [args]}]
;;   (let [
;;         ;; This pulls the ui component fn name and meta map from the root source form 
;;         [_ fn-sym mm] (some-> &env :root-source-info :source-form)
;;         !dbg          (fn [_ x] x)
;;         dbg           (if (= fn-sym debug-defui) dbg !dbg)
;;         props         (when (map? mm) (:props mm))
;;         ns-name       (some-> &env :ns :name str)
;;         fq-fn-name    (str ns-name "/" fn-sym)
;;         dbg?          (= 'box fn-sym)]
;;     #_(when dbg?
;;       (? (str "defmacro validate " fn-sym)
;;          (keyed [mm          
;;                  fn-sym      
;;                  props        
;;                  ns-name     
;;                  fq-fn-name])))
;;     (when (seq props)
;;       ;; Augment the props with schemas from kushi.ui.schema
;;       (let [props (with-schemas props)
;;             ;; props-unreserved-ks (mapv #(keyword (subs (name %) 1)) (keys props))
;;             ]
;;         (when dbg? (? {:label "defmacro validate, props with-schemas"} props))
;;         #_(? "defmacro validate, props with-schemas" fn-sym)
;;         `(do (!? "validate, args" ~args)
;;              (kushi.ui.core/validate* 
;;               (!? "validate, m"
;;                  {:ns/name        ~ns-name
;;                   :fn/name        (quote ~fn-sym)
;;                   :fn/fq-name     ~fq-fn-name
;;                   ;; :props/unreserved-ks ~props-unreserved-ks
;;                   :props/quoted   (quote ~props)
;;                   :props/expanded ~props})
;;               ~args))))))


;; (def defui-syms
;;   '{&props         props
;;     &attrs         attrs
;;     &children      children
;;     &data-ks-attrs data-ks-attrs})


;; (defn with-schemas
;;   "Expects a map of props, from the :props entry in the metadata map of the
;;    component rendering function. This metadata map originates from the 2nd
;;    arg to the kushi.ui.core/defui.
   
;;    Each prop map's value is a map, and may contain a :schema entry. If a :schema
;;    entry is not present, the map will potentially be given a :schema entry,
;;    pulled from the `kushi.ui.variants` namespace. If no schema is found, a value
;;    of any? will be used for the schema"
;;   [props]
;;   (reduce-kv (fn [m k {:keys [schema]
;;                        :as   v}]
;;                (let [schema 
;;                      (if-not schema
;;                        ; lookup by opt key e.g. :custom
;;                        (or (k variants-by-custom-opt-key)        
;;                            :any)
;;                        (cond 
;;                          ; just a schema function e.g. boolean?
;;                          (symbol? schema)                                 
;;                          schema

;;                          ; set literal for enum e.g. #{:rounded :sharp :pill}
;;                          (set? schema)                                    
;;                          schema

;;                          ; kw such as :kushi.ui.variants/colors}
;;                          (keyword? schema)                                
;;                          (-> schema
;;                              name
;;                              (str "/enum")
;;                              keyword
;;                              (->> (get variants)))

;;                          ; for surfacing warning
;;                          :else schema))]
;;                  (assoc m k (assoc v :schema schema))))
;;              {}
;;              props))


;; Experimental

;; TODO - try pulling out the non-dynamic parts for processing ? 

;; (defmacro pc 
;;   "Optional compile-time computation of HTML attributes and styles related to
;;    strokes and shadows on components defined with kushi.ui.core/defui"
;;   [m]
;;   (if (->> m vals (not-any? symbol?))
;;     (? "no dynamic prop values found, precompiling..." 
;;         (let [flex-attrs       (flex-data-ns-attrs m)
;;               decoration-attrs (some->> m 
;;                                         decoration/drop-shadow-and-stroke-attrs
;;                                         (assoc m :kushi.ui.core/pc)
;;                                         )
;;               decoration-attrs (some-> decoration-attrs
;;                                        (dissoc :stroke :stroke-align :drop-shadow))]
;;           (merge (? decoration-attrs)
;;                  (? flex-attrs))))
;;     (? "dynamic prop values found, passing through..." m)))
