(ns ^:dev/always kushi.ui.core
    (:require [clojure.walk :as walk]))

(defmacro defcom
  ([nm hiccup]
   `(kushi.ui.core/defcom ~nm ~hiccup nil))
  ([nm hiccup decorator]
   `(kushi.ui.core/defcom ~nm ~hiccup ~decorator nil))
  ([nm hiccup decorator f]
   (let [args '[a b c d e f g h i j k l m n o p q r s t u v w x y z]]
     `(def ~nm
        (fn ~args
          (kushi.ui.core/gui (if ~f (mapv ~f ~args) ~args) ~hiccup ~decorator))))))

(defmacro &*->val
  ([opts attrs children coll f]
   (&*->val opts attrs children coll f nil))
  ([opts attrs children coll f form-meta]
   (let [form-meta2 (meta &form)
         ret (walk/postwalk (fn [x]
                              (cond
                                (= x '&children) (list 'kushi.ui.core/children children f)
                                (= x '&opts)     opts
                                (= x '&attrs)    (list 'assoc attrs :data-amp-form form-meta :data-amp-form2 form-meta2)
                                :else            x))
                            coll)
         ]
     `~ret)))

(defmacro defcom+
  [& args]
  (let [[nm coll f] args
        form-meta   (meta &form)]
    `(defn ~nm
       [& args#]
       (let [[opts# attrs# & children#] (kushi.ui.core/opts+children args#)]
         (kushi.ui.core/&*->val opts#
                                attrs#
                                children#
                                ~coll
                                ~f
                                ~form-meta)))))
