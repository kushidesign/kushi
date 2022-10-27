(ns ^:dev/always kushi.utils
  (:require
   [clojure.spec.alpha :as s]
   [clojure.string :as string]))

(defn exception-args [{:keys [ex]}]
  {:exception-message (.getMessage ex)
   :top-of-stack-trace (get (.getStackTrace ex) 0)})

(defmacro keyed [& ks]
  #?(:clj
     `(let [keys# (quote ~ks)
            keys# (map keyword keys#)
            vals# (list ~@ks)]
        (zipmap keys# vals#))))

(defn nameable? [x]
  (or (string? x) (keyword? x) (symbol? x)))

(defn stringify [x]
  (if (nameable? x) (name x) (str x)))

(defn token? [x]
  (when (nameable? x)
    (some-> x name (string/starts-with? "--"))))

(defn s->cssvar
  ([x] (s->cssvar x nil))
  ([x fallback]
    (str "var(--" (name x) (when fallback (str ", " fallback)) ")")))

(defn maybe-wrap-css-var [x]
  (if (token? x)
    (str "var(" (name x) ")")
    x))

(declare replace-nth)

(defn replace-last [item coll]
  (replace-nth (dec (count coll))
                      item
                      coll))

(defn deep-merge [& maps]
  (apply merge-with (fn [& args]
                      (if (every? map? args)
                        (apply deep-merge args)
                        (last args)))
         maps))

(defn partition-by-pred [pred coll]
  (let [ret* (reduce (fn [acc v]
                       (let [k (if (pred v) :valid :invalid)]
                         (assoc acc k (conj (k acc) v))))
                     {:valid [] :invalid []}
                     coll)
        ret [(:valid ret*) (:invalid ret*)]]
    ret))

(defn partition-by-spec [pred coll]
  (let [ret* (reduce (fn [acc v]
                       (let [k (if (s/valid? pred v) :valid :invalid)]
                         (assoc acc k (conj (k acc) v))))
                     {:valid [] :invalid []}
                     coll)
        ret [(:valid ret*) (:invalid ret*)]]
    ret))


(defn mapj [sep f coll]
  (string/join sep (map f coll)))


(defn shared-class? [kw]
  (contains? #{:kushi.core/defclass
               :kushi.core/defclass-override
               :kushi/utility
               :kushi/utility-override}
             kw))

(defn hydrate-css-shorthand+alternations [v]
  ;; TODO detect if value expresses url for something like a background image via image or svg, and don't split on `:` or `|` within the url() part:
  ;; "url(https://blah.blah.com/blah.png)"
  ;; or
  ;; "url(\"data:image/svg+xml,%3Csvg xmlns='http://www.w3.org/2000/svg' height='12px' ...)"

  (-> v
      (string/replace #"\|" ", ")
      (string/replace #"\:" " ")))

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
  #?(:clj  (instance? clojure.lang.IEditableCollection coll)
     :cljs (satisfies? cljs.core/IEditableCollection coll)))

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

