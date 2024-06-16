(ns kushi.ui.util
  (:require [clojure.string :as string]
            [fireworks.core :refer [? !? ?- !?- ?-- !?-- ?> !?> ?i !?i ?l !?l ?log !?log ?log- !?log- ?pp !?pp ?pp- !?pp-]]
            [kushi.core :refer [keyed]]))

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

(defn class-coll? [x]
  (and (seq x)
       (every? nameable? x))) 

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


(defn backtics->hiccup
  [s]
  (if (re-find #"`" s)
    (->> (string/split s #" ")
         (map #(if (re-find #"^`.+`$" %)
                 [:span.code (->> % rest drop-last string/join)]
                 %))
         (map-indexed (fn [idx v]
                        (if (string? v)
                          (if (= idx 0) (str v " ") (str " " v))
                          v)))
         (cons :span)
         (into []))
    s))

(defn backtics->stringified-html
  [s]
  (if (re-find #"`" s)
    (let [spans       (for [i    (-> (re-seq #"`" s) count range)
                            :let [tag (if (even? i) "<span class=\"code\">" "</span>")]]
                        tag)
          spans       (conj (into [] spans) nil)
          splits      (string/split s #"`")
          interleaved (interleave splits spans)]
      (keyed spans splits interleaved)
      (string/join interleaved))
    s))
