(ns kushi.ui.util
  (:require
   [clojure.string :as string]))

(defn data-kushi-attr [x]
 (-> x name (string/replace #"^kushi\.ui\." "") (string/replace #".core$" "")) )

(defn ->fixed-float [points n]
  (.toFixed (.parseFloat js/Number n) points))

(defn range-of-floats
  "`step` must be float"
  [start end step]
  (let [numsteps (/ (- end start) step)
        numfixed (-> step str (string/split #"\.") last count)
        rng*     (range 0 (inc numsteps))
        rng      (map #(->fixed-float numfixed (* % step)) rng*)]
    rng))

(defn numeric-string?
  "String representation of float or int?"
  [s]
  (when (and (string? s) (not (string/blank? s)))
    (if (re-find  #"^[-+]?[0-9]*\.?[0-9]*$" s) true false)))

(defn aspect-ratio->number [x]
  (when (or (string? x) (keyword? x))
    (let [[w h] (-> x name (string/split #":"))]
      (when (and (numeric-string? w) (numeric-string? h))
        (/ h w)))))

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
