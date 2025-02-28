(ns ^:dev/always kushi.ui.core
  (:require
   [clojure.walk :as walk]
   [fireworks.core :refer [?]]))

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


(defmacro extract [args f]
  `(kushi.ui.core/extract* ~args (-> ~f var meta)))

