(ns ^:dev/always kushi.ui.core
  (:require [clojure.walk :as walk]))

(defmacro &*->val
  ([opts attrs children coll f]
   (&*->val opts attrs children coll f nil))
  ([opts attrs children coll f form-meta]
   (let [form-meta2 (meta &form)
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
                           :kushi/qualified-caller (symbol (str (name caller-ns) "/" (name nm)))
                           )]
    `(defn ~nm
       [& args#]
       (let [[opts# attrs# & children#] (kushi.ui.core/opts+children args#)]
         (kushi.ui.core/&*->val opts#
                                attrs#
                                children#
                                ~coll
                                ~f
                                ~form-meta)))))

(defmacro defcom2
  [nm mm coll f]
  (let [form-meta  (meta &form)]
    `(defn ~nm
       ~mm
       [& args#]
       (let [[opts# attrs# & children#] (kushi.ui.core/opts+children args#)]
         (kushi.ui.core/&*->val opts#
                                attrs#
                                children#
                                ~coll
                                ~f
                                ~form-meta)))))
