(ns kushi.ui.util
  (:require
   [clojure.string :as string]))

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
