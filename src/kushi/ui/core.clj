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


;; (defmacro extract [args f]
;;   `(kushi.ui.core/extract* ~args (-> ~f var meta)))


(defmacro sx-call 
  [coll]
  `{:evaled ~coll
    :quoted (quote ~coll)})


(defn ui-demo-samples-partioned [samples]
  (and (even? (count samples))
       (let [partitioned
             (partition 2 samples)]
         (when (every? (fn [[label]]
                         (string? label))
                       partitioned)
           partitioned))))


(defmacro ui-demo [coll]
  (let [w-reqs 
        (mapv #(assoc %
                      :require
                      (str (:require %))
                      :samples
                      (let [samples (:samples %)
                            m*      (fn [sample label]
                                      (merge 
                                       {:code/evaled sample
                                        :code/quoted (list 'quote sample)}
                                       (when label {:label label})))]
                        (if (:render-as %)
                          samples
                          (if-let [partitioned 
                                   (ui-demo-samples-partioned samples)]
                            (mapv (fn [[label sample]] (m* sample label))
                                  partitioned)
                            (mapv (fn [sample] (m* sample nil))
                                  samples)))))
              coll)]
   (!? {:display-metadata? false} w-reqs)
   `~w-reqs))

(defn with-schemas [opts]
  (reduce-kv (fn [m k {:keys [schema]
                       :as   v}]
               (let [schema 
                     (if-not schema
                       ; lookup by opt key e.g. :-custom
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
