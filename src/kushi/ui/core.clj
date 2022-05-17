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
  [opts attrs children coll f]
  (let [ret (walk/postwalk (fn [x]
                             (cond
                               (= x '&children) (list 'into [:<>] (if f (list 'map f children) children))
                               (= x '&opts)     opts
                               (= x '&attrs)    attrs
                               :else            x))
                           coll)]
    `~ret))

(defmacro defcom+
  ([nm coll]
   `(kushi.ui.core/defcom+ ~nm ~coll nil))
  ([nm coll f]
   (let [args '[a b c d e f g h i j k l m n o p q r s t u v w x y z]]
     `(def ~nm
        (fn ~args
          (let [[opts# attrs# & children#] (kushi.ui.core/opts+children ~args)]
            (kushi.ui.core/&*->val opts#
                                   attrs#
                                   children#
                                   #_(keep-indexed (fn [i# x#]
                                                     ^{:key (str (hash x#) "--" i#)}
                                                     x#)
                                                   children#)
                                   ~coll
                                   ~f)))))))
