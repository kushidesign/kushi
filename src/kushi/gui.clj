(ns ^:dev/always kushi.gui)

(defmacro defcom
  ([nm hiccup]
   `(kushi.gui/defcom ~nm ~hiccup nil))
  ([nm hiccup decorator]
   (let [args '[a b c d e f g h i j k l m n o p q r s t u v w x y z]]
     `(def ~nm
        (fn ~args
          (kushi.gui/gui ~args ~hiccup ~decorator))))))
