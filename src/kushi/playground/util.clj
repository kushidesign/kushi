(ns kushi.playground.util)

(defmacro sx-call 
  [coll]
  `{:evaled ~coll
    :quoted (quote ~coll)})

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
