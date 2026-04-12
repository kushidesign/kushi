(ns kushi.util
  (:require [clojure.string :as string]
            [fireworks.core :refer [? !? ?> !?>]]
            )
  #?(:cljs
     (:require-macros [kushi.util])))

(defn ^:public when->
  "If `(= (pred x) true)`, returns x, otherwise nil.
   Useful in a `clojure.core/some->` threading form."
  [x pred]
  (when (or (true? (pred x))
            (when (set? pred) (contains? pred x)))
    x))

(defn ^:public when->>
  "If (= (pred x) true), returns x, otherwise nil.
   Useful in a `clojure.core/some->>` threading form."
  [pred x]
  (when (or (true? (pred x))
            (when (set? pred) (contains? pred x)))
    x))

;; TODO - convert all to when-> or when->> and delete
(defn ^:public maybe [x pred]
  (when (if (set? pred)
          (contains? pred x)
          (pred x))
    x))

(defn as-str [x]
  (str (if (or (keyword? x) (symbol? x)) (name x) x)))

(defn nameable? [x]
  (or (string? x) (keyword? x) (symbol? x)))

(defn stringify [x]
  (if (nameable? x) (name x) (str x)))

(defn kebab->shorthand [x] 
  (->> (-> x
           stringify
           (string/split #"-"))
       (map #(nth % 0 nil))
       string/join))

;; TODO - get this to support ||
;; Check out kushi.css.hydrated/hydrated-css-var
(defn extract-cssvar-token [s]
  (some-> s
          (when-> #(string/starts-with? % "$"))
          (subs 1)))


(defn css-varize [& args] (str "var(--" (apply str args) ")"))

(defn- s->cssvar [s] 
  (if-let [token (extract-cssvar-token s)]
    (css-varize token)
    s))

(defn kw->cssvar  [x]
  (if-let [token (some-> x
                         (when-> keyword?)
                         name
                         extract-cssvar-token)]
    (css-varize token)
    (as-str x)))


;; Supports up to 2 fallbacks
(defn kw->cssvar2  [x] 
  (if-let [token (some-> x
                         (when-> keyword?)
                         name
                         extract-cssvar-token)]
    (let [[token fallback1 fallback2] (string/split token #"\|\|")]
      (css-varize token 
                  (some->> fallback1 s->cssvar (str ", "))
                  (some->> fallback2 s->cssvar (str ", "))))
    (as-str x)))


(defn css-fn [fname & args] (str fname "(" (string/join ", " args) ")"))


(defn- cssfn-color-string
  "(cssfn-color-string \"hsla\" \"100deg\" \"50%\" \"33%\" \"0.8\")
   => \"hsla(100deg 50% 33% / 0.8)\""
  [nm args]
  (str (as-str nm)
       "("
       (string/join " " (mapv kw->cssvar2 args))
       ")"))

(defn ^:public oklch
  "(oklch \"100%\" \"0.2\" \"33\" \"0.8\")
   => \"oklch(100% 0.2 33 / 0.8)\""
  [& args]
  (cssfn-color-string "oklch" args))

(defn deep-merge [& maps]
  (apply merge-with (fn [& args]
                      (if (every? map? args)
                        (apply deep-merge args)
                        (last args)))
         maps))

(defn kwargs-keys
  "Expects an even-numbered kwarg-style collection of key/values.
   Returns a coll of the keys."
  [kwargs]
  (keep-indexed (fn [idx x] (when (even? idx) x)) kwargs))

(defn ordered-pairs
  "Takes a vector of keys, and a map.
   All values in vector of keys are expected to have a corresponding entry in map.
   Useful when you want to express an ordered-map as a vector of values (similar to kwargs),
   subsequently convert it to a map for further processing, then convert it back to an vector
   of kv tuples with an ordering based on original kwargs vector."
  [ks m]
  (into [] (keep (fn [k] (when-let [v (get m k)] [k v])) ks)))

(defn sort-a-with-b
  "Takes two sequential collections and orders coll a by coll b."
  [a b]
  (sort-by
   (->> (range)
        (interleave b)
        (apply hash-map))
   a))


;; Utility fns below are taken from: http://weavejester.github.io/medley/medley.core.html

(defn remove-nth
  "Returns a lazy sequence of the items in coll, except for the item at the
  supplied index. Runs in O(n) time. Returns a transducer when no collection is
  provided."
  {:added "1.2.0"}
  ([index]
   (fn [rf]
     (let [idx (volatile! (inc index))]
       (fn
         ([] (rf))
         ([result] (rf result))
         ([result x]
          (if (zero? (vswap! idx dec))
            result
            (rf result x)))))))
  ([index coll]
   (lazy-seq
    (if (zero? index)
      (rest coll)
      (when (seq coll)
        (cons (first coll) (remove-nth (dec index) (rest coll))))))))

(defn replace-nth
  "Returns a lazy sequence of the items in coll, with a new item replacing the
  item at the supplied index. Runs in O(n) time. Returns a transducer when no
  collection is provided."
  {:added "1.2.0"}
  ([index item]
   (fn [rf]
     (let [idx (volatile! (inc index))]
       (fn
         ([] (rf))
         ([result] (rf result))
         ([result x]
          (if (zero? (vswap! idx dec))
            (rf result item)
            (rf result x)))))))
  ([index item coll]
   (lazy-seq
    (if (zero? index)
      (cons item (rest coll))
      (when (seq coll)
        (cons (first coll) (replace-nth (dec index) item (rest coll))))))))

(defn- editable? [coll]
  #?(:cljs
     (satisfies? cljs.core/IEditableCollection coll)
     :clj
     (instance? clojure.lang.IEditableCollection coll)))

(defn- reduce-map [f coll]
  (let [coll' (if (record? coll) (into {} coll) coll)]
    (if (editable? coll')
      (persistent! (reduce-kv (f assoc!) (transient (empty coll')) coll'))
      (reduce-kv (f assoc) (empty coll') coll'))))

(defn map-keys
  "Maps a function over the keys of an associative collection."
  [f coll]
  (reduce-map (fn [xf] (fn [m k v] (xf m (f k) v))) coll))

(defn map-vals
  "Maps a function over the values of one or more associative collections.
  The function should accept number-of-colls arguments. Any keys which are not
  shared among all collections are ignored."
  ([f coll]
   (reduce-map (fn [xf] (fn [m k v] (xf m k (f v)))) coll))
  ([f c1 & colls]
   (reduce-map
    (fn [xf]
      (fn [m k v]
        (if (every? #(contains? % k) colls)
          (xf m k (apply f v (map #(get % k) colls)))
          m)))
    c1)))



(defn vec-of-vecs? [v]
  (and (vector? v)
       (every? vector? v)))


(defn more-than-one? [coll]
  (> (count coll) 1))


(defn partition-by-pred [pred coll]
  "Given a coll and a pred, returns a vector of two vectors. The first vector
   contains all the values from coll that satisfy the pred. The second vector
   contains all the values from the coll that do not satisfy the pred."
  (let [ret* (reduce (fn [acc v]
                       (let [k (if (pred v) :valid :invalid)]
                         (assoc acc k (conj (k acc) v))))
                     {:valid [] :invalid []}
                     coll)]
    [(:valid ret*) (:invalid ret*)]))


(let [transforms {:keys keyword
                  :strs str
                  :syms identity}]
  (defmacro ^:public keyed
    "Create a map in which, for each symbol S in vars, (keyword S) is a
       key mapping to the value of S in the current scope. If passed an optional
     :strs or :syms first argument, use strings or symbols as the keys."
    ([vars] `(keyed :keys ~vars))
    ([key-type vars]
     (let [transform (comp (partial list `quote)
                           (transforms key-type))]
       (into {} (map (juxt transform identity) vars))))))




(defn map-css-tuple-args [coll]
  (map #(let [x (if (vector? %) % [%])]
          (->> x
               (map kw->cssvar2)
               (string/join " "))) 
       coll))

(defn color-mix [color-space & args] 
  (->> args
       map-css-tuple-args
       (into ["color-mix" color-space])
       (apply css-fn)))

(defn linear-gradient [direction & args] 
  (->> args
       map-css-tuple-args
       (into ["linear-gradient" direction])
       (apply css-fn)))

(defn double-quote-data-attr-selector-values [v]
  (if (re-find #"=" v)
      (string/replace v
                      #"\[([a-z-\*\|\$\~\^]+)=([^\"\]]+)\]"
                      "[$1=\"$2\"]")
       v))


(defn insert-at [vc i elem]
  (into (conj (subvec vc 0 i) elem)
        (subvec vc i)))

(defn- ml-str-with-adjusted-indentation [s]
  (let [re #"\n( +)"
        n  (some->> s
                    str
                    (re-seq re)
                    (group-by #(count (second %)))
                    keys
                    (apply min))
        f  (fn [[a]] (str "\n" (subs a (inc n))))
        s  (string/replace s re f)]
    s))

(defn str-ml
  {:doc "Takes a multi-line string and normalizes the indentation.
         Useful for multi-line strings that are nested inside data structures,
         because some editors automatically format these for readability, but
         the resulting strings have unexpected indentations on lines after the
         first."
   :examples '[{:desc "String as map entry value"
                :forms [[(ml-str "Line one
                                  Line two
                                  Line three
                                    - Line four")
                         "Line one\nLine two\nLine three\n  - Line four"]]}]}
  [s]
  (ml-str-with-adjusted-indentation s))

(defn string-ml? [x]
  (boolean (and (string? x) (re-find #"\n" x))))

(defn ml-str->vec [s]
  (-> s
      str-ml
      (string/split #"\n")
      vec))
