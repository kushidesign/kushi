(ns ^:dev/always kushi.ui.core
  (:require
   [clojure.pprint :refer [pprint]]
   [fireworks.core :refer [? !? ?> !?>]]
   [kushi.ui.variants :as props :refer [variants-by-custom-opt-key variants]]
   [edamame.core :as e]
   [kushi.util :refer [keyed]]
   [clojure.walk :as walk] ))

            
(defmacro &*->val
  ([opts attrs children coll f]
   (&*->val opts attrs children coll f nil))
  ([opts attrs children coll f form-meta]
   (let [form-meta2 (meta &form)
         form-meta-defcom (assoc (select-keys form-meta [:end-column :column :line :end-line])
                                 :file (str "defcom:" (:kushi/enclosing-fn-name form-meta) "@" (:file form-meta) ))
         ret (walk/postwalk (fn [x]
                              (cond
                                (= x 'children)
                                (list 'kushi.ui.core/children children f)

                                (= x 'opts)
                                opts

                                (= x 'attrs)
                                (list 'assoc
                                      attrs
                                      :data-amp-form
                                      form-meta
                                      :data-amp-form2
                                      form-meta2)

                                (and (list? x) (= (first x) 'sx))
                                (list 'sx
                                      {:_kushi/defcom? true
                                       :args          (rest x)
                                       :form-meta     form-meta-defcom})
                                :else
                                x))
                            coll)]
     `~ret)))

(defmacro defcom
  [& args]
  (let [[nm coll f] args
        caller-ns (-> &env :ns :name)
        form-meta   (assoc (meta &form)
                           :kushi/caller-ns caller-ns
                           :kushi/from-defcom? true
                           :kushi/enclosing-fn-name nm
                           :kushi/qualified-caller (symbol (str (name caller-ns)
                                                                "/"
                                                                (name nm))))]
    `(defn ~nm
       [& args#]
       (let [[opts# attrs# & children#] (kushi.ui.core/extract args#)]
         (kushi.ui.core/&*->val opts#
                                attrs#
                                children#
                                ~coll
                                ~f
                                ~form-meta)))))





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
        `(let [schema# {:ns/name            ~ns-name
                        :fn/name            (quote ~fn-sym)
                        :fn/fq-name         ~fq-fn-name
                        ;; :opts/unreserved-ks ~opts-unreserved-ks
                        :opts/quoted        (quote ~opts)
                        :opts/expanded      ~opts}]
           (kushi.ui.core/validate* schema# ~args))))))



;; New
(def defui-syms
  '{&opts          opts
    &attrs         attrs
    &children      children
    &data-ks-attrs data-ks-attrs})

#_(def surface-label-opts
  {:sizing         {:default nil
                    :desc    "Corresponds to the font-size based on Kushi's font-size scale."
                    :data    #(when (false? %) "")}
   :colorway       {:default nil
                    :desc    "Colorway of the button. Can also be a named color from Kushi's design system e.g `:red` `:purple` `:gold` etc." }
   :contour        {:default :round
                    :desc    "Shape of the button." }
   :stroke-align   {:schema  #{:inside :outside}
                    :default nil
                    :desc    "Alignment of the stroke. Only applies to `:surface` `:outline`" }
   :packing        {:default nil
                    :desc    "General amount of padding inside the button" }
   :end-enhancer   {:schema  #(or (string? %) (keyword? %) (vector? %))
                    :default nil
                    :desc    "Content at the inline-end position preceding the button text. Typically an icon." }
   :start-enhancer {:schema  [:or :string :keyword vector?]
                    :default nil
                    :desc    "Content at the inline-start position following the button text. Typically an icon." }
   :surface        {:default :round
                    :desc    "Surface variant of the button."}})

#_(def prop-maps 
  {'surface-label-opts surface-label-opts})

#_(defmacro defui
  [sym m body]
  (let [opts          (some-> m :opts keys)
        opts          (cond (map? opts)
                            opts
                            (symbol? opts)
                            (get prop-maps opts)
                            (and (list? opts) (= 'merge (first opts)))
                            (->> opts
                                 rest
                                 (keep #(cond (symbol? %)
                                              (get prop-maps %)
                                              (map? %)
                                              %))
                                 (apply merge)))
        opts-syms     (mapv symbol opts)
        data-ks-attrs {}                        ; <- fn that takes opts and returns map of data-ks-* attrs
        meta-data     (reduce-kv (fn [m k v] (assoc m k (dissoc :data v)) ) {} opts)
        body          (walk/postwalk
                       (fn [x]
                         (get defui-syms x x))
                       body)]
    `(defn ~sym 
       ~m
       '[& args]
       `(let [{:keys [opts# attrs# children#]} (extract args (into [] ~opts))
              {:keys ~opts-syms}               opts#
              data-ks-attrs#                   ~data-ks-attrs]
          ~body))))


;; Example call


#_(defui button 
  {:summary "Buttons provide cues for actions and events."
   :desc    "Buttons are fundamental components that allow users to process actions or navigate an experience."
   :opts (merge surface-label-opts
                {:loading?       {:schema  boolean?
                                  :default false
                                  :data    false
                                  :desc    "When `true` this will set the appropriate values for `aria-busy` and `aria-label`" }})}
  (let [{:keys [end-enhancer start-enhancer loading?]} &opts]
    (into [:button
           (merge-attrs
            (sx "[data-ks-ui=\"button\"]"
                :.transition
                :position--relative
                :d--flex
                :flex-direction--row
                :jc--c
                :ai--c
                :w--fit-content
                :h--fit-content
                :gap--$icon-enhanceable-gap
                :cursor--pointer
                [:--_padding-block :$button-padding-block]
                [:--_padding-inline :$button-padding-inline]
                :pi--$_padding-inline
                :pb--$_padding-block
                ;; TODO what are these???
                ["[aria-label='loading']>.kushi-spinner-propeller:d" :revert]
                ["[aria-label='loading']>.kushi-icon:d" :none])
            {:aria-busy  loading?
             :aria-label (when loading? "loading")}
            &attrs
            &data-ks-attrs)]
          (cond start-enhancer (concat [start-enhancer] &children)
                end-enhancer   (concat &children [end-enhancer])
                :else          &children))))



(defmacro defn-mm [m]
  (assoc m :doc "WOOHAAAAAG"))

(defn dbg [label x]
  (? :no-file 
     {:label                 label
      :display-metadata?     false
      :non-coll-length-limit 21}
     x))

(def debug-defui 'box)

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
        (some->> (:props/family m)
                 (dbg 'family-props)
                 (reduce (fn [vc k]
                           (apply conj
                                  vc
                                  (k props/prop-families)))
                         [])
                 (dbg 'constituent-prop-keys)
                 (select-keys props/props)
                 #_(dbg 'hydrated-constituent-prop-map))

        ;; props shared across components
        props-from-shared 
        (dbg 'props-from-shared (select-keys props/props (:props/shared m)))

        
        ;; props specific/unique to the component
        user-props
        (dbg 'user-props (:opts m))

        ;; merge all the props
        merged-props        
        (dbg 'merged-opts
             (merge user-props
                    props-from-shared
                    props-from-families))

        ;; trims the opts to only give data-ks-attrs what it needs at runtime,
        ;; which are the :default and and :data-ks? :data-ks (data trans fn) entries
        props-trimmed
        (dbg 'props-trimmed
             (reduce-kv (fn [m k v]
                          (assoc m
                                 k
                                 (dissoc v :desc :schema :required? :data)))
                        {}
                        merged-props))

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
        '[&opts &attrs &data-ks-attrs &children args]]
    
    `(defn ~sym 
       ~m
       [& args#]
       (let [extracted*#    (kushi.ui.core/extract args# ~props-keys)
             data-ks-attrs# (kushi.ui.core/data-ks-attrs (:opts extracted*#) ~props-trimmed)
             extracted#     {:&opts          (:opts extracted*#)
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
