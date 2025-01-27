(ns kushi.util
  (:require [clojure.string :as string])
  #?(:cljs
     (:require-macros [kushi.util])))

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

(defn cssfn-string
  "(cssfn-string \"hsla\" \"100deg\" \"50%\" \"33%\" \"0.8\")
   => \"hsla(100deg, 50%, 33%, 0.8)\"

   Note that is works differently for css calc()

   (cssfn-string \"calc\" \"1px\" \"+\" \"1px\")
   => \"calc(1px + 1px)\"

   "
  [s args]
  (str s
       "("
       (string/join (if (= "calc" s) " " ", ")
                    (map #(if (keyword? %)
                            (name %)
                            %)
                         args))
       ")"))


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


(defn maybe [x pred]
  (when (if (set? pred)
          (contains? pred x)
          (pred x))
    x))



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
