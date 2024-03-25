(ns kushi.ui.util
  (:require
   [clojure.string :as string]))

;; Generic
;; --------------------------------------------------------------------------
(defn calc [& strs]
  (str "calc(" (apply str strs) ")"))

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

(defn ->pairs [coll]
  (->> coll (partition 2) (map vec)))

(defn nameable? [x]
  (or (string? x) (keyword? x) (symbol? x)))

(defn as-str [x]
  (str (if (or (keyword? x) (symbol? x)) (name x) x)))

(defn maybe [x pred]
  (when (if (set? pred)
          (contains? pred x)
          (pred x))
    x))

(defn html-attr? [m k]
  (when (keyword? k)
    (or (true? (k m))
        (true? ((keyword (str (name k) "?")) m)))))

(defn find-index [pred coll]
  (first
   (keep-indexed
    (fn [i x]
      (when (pred x) i))
    coll)))

(defn ck?
 "`contains key?` helper function.
  Used with partial, e.g. `(partial ck? new-placement-kw)`"
 [k keyset]
 (contains? keyset k))

