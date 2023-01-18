(ns kushi.playground.util)

(defmacro keyed [& ks]
  `(let [keys# (quote ~ks)
         keys# (map keyword keys#)
         vals# (list ~@ks)]
     (zipmap keys# vals#)))

(defmacro example2 [coll]
  (let [
        ;; argslist* (into [] args)
        ;; f (when (and (vector? (first argslist*))
        ;;              (= 1 (count argslist*))
        ;;              (symbol? (ffirst argslist*)))
        ;;     (ffirst argslist*))
        ;; argslist (if f (into [] (rest (first argslist*))) argslist*)
        ]
    `{:evaled ~coll
      :quoted (quote ~coll)}))

(defmacro example [& args]
  (let [argslist* (into [] args)
        f (when (and (vector? (first argslist*))
                     (= 1 (count argslist*))
                     (symbol? (ffirst argslist*)))
            (ffirst argslist*))
        argslist (if f (into [] (rest (first argslist*))) argslist*)]
    `{:args ~argslist
      :other-fn  ~f
      :code '~args}))

(defmacro let-map
  "Equivalent of
   (let [a 5
         b (+ a 5)]
     {:a a :b b})"
  [kvs]
  (let [keys (keys (apply hash-map kvs))
        keyword-symbols (mapcat #(vector (keyword (str %)) %) keys)]
    `(let [~@kvs]
       (hash-map ~@keyword-symbols))))
