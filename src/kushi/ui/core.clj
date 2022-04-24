(ns ^:dev/always kushi.ui.core)

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
