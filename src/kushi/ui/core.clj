(ns ^:dev/always kushi.ui.core
  (:require
   [clojure.pprint :refer [pprint]]
   [fireworks.core :refer [? !? ?> !?>]]
   [kushi.ui.variants :refer [variants-by-custom-opt-key variants]]
   [edamame.core :as e]
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
#_(def defui-syms
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

(defmacro defui
  [sym m body]
  (let [opts-syms (mapv symbol (keys (:opts m)))
        ;; process body here for different frameworks
        ]
    `(defn ~sym 
       ~m
       [& args#]
       (when ^boolean js/goog.DEBUG
             ;; Try to validate opts in here.
             "This is going to be included YEAHHHHHHHH")
       (let [opts#
             (first args#)

             {:keys ~opts-syms
              :as   bones#} 
             opts#]
         ~body))))
