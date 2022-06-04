(ns kushi.ui.util
  (:require
   [clojure.string :as string]))

(defn data-kushi-attr [x]
 (-> x name (string/replace #"^kushi\.ui\." "") (string/replace #".core$" "")) )

(defn compound-override [schema m]
  (reduce (fn [acc [k v]]
            (assoc acc
                   k
                   (into {}
                         (apply
                          concat
                          (map-indexed (fn [idx x]
                                         (mapv (fn [kw]
                                                 [kw (nth v idx nil)])
                                               (if (coll? x) x [x])))
                                       schema)))))
          {}
          m))
