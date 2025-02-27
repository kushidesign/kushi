(ns ^:dev/always kushi.ui.core
  (:require
   [clojure.walk :as walk]))

(defmacro &*->val
  ([opts attrs children coll f]
   (&*->val opts attrs children coll f nil))
  ([opts attrs children coll f form-meta]
   (let [form-meta2 (meta &form)
         form-meta-defcom (assoc (select-keys form-meta [:end-column :column :line :end-line])
                                 :file (str "defcom:" (:kushi/enclosing-fn-name form-meta) "@" (:file form-meta) ))
         ret (walk/postwalk (fn [x]
                              (cond
                                (= x '&children)
                                (list 'kushi.ui.core/children children f)

                                (= x '&opts)
                                opts

                                (= x '&attrs)
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
                           :kushi/qualified-caller (symbol (str (name caller-ns) "/" (name nm))))]
    `(defn ~nm
       [& args#]
       (let [[opts# attrs# & children#] (kushi.ui.core/opts+children args#)]
         (kushi.ui.core/&*->val opts#
                                attrs#
                                children#
                                ~coll
                                ~f
                                ~form-meta)))))



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


(defmacro opts+children2 [args f]
  `(kushi.ui.core/opts+children2* ~args
                                  ~f
                                  (-> ~f var meta)))


;; new defcom sketch
'(defcom mycomp
  {:doc  "Doc string"
   :desc "Component desc"
   :opts {packing #{:roomy :compact}
          shape   {:desc "My desc of the shape attr"
                   :pred #{:roomy :compact}}
          n       {:pred number?}}}
  [:div (merge-attrs 
         (data-attrs "kushi" [packing shape])
         {:data-wtf-mycomp ""}
         (sx :flex-row-fs :c--red)
         &attrs)
   (into [:div {:id (* n n)}]
         &children)])

;; Above woulde expand to
;; =>

'(defn mycomp
  {:doc  "Doc string"
   :desc "Component desc"
   :opts {:packing {:pred #{:roomy :compact}}
          :shape   {:desc "My desc"
                    :pred #{:round :square}}
          :n       {:pred number?}}}
 [& args]
  (let [[opts attrs & children]
        ('kushi.core/opts+children args)

        {:keys [packing
                shape
                n]}
        opts]
    
    ;; custom attrs that are not data-attrs are validated here
    (when ^boolean js/goog.DEBUG
      (do (kushi.core/validate-opt
           n
           number?
           'number?)))

   [:div (merge-attrs 
          ;; data-attrs are validated here
          {:data-kushi-packing (do (when ^boolean js/goog.DEBUG
                                     (kushi.core/validate-opt
                                      packing
                                      #{:roomy :compact}))
                                   packing)
           :data-kushi-shape   (do (when ^boolean js/goog.DEBUG
                                     (kushi.core/validate-opt
                                      shape
                                      #{:round :square}))
                                   shape)}
          {:data-wtf-mycomp ""}
          (sx :flex-row-fs :c--red)
          attrs)
    (* n n)]
    (into [:div {:id (* n n)}]
          children)))
