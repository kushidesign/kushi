;;;; This ns provides the t+ macro which is designed to be used at the repl (in editor).
;;;; The result transforms the old kushi.core/s+ fn signature to the new syntax.
;;;; Intended to be used internally for manual project conversion, then removed from library.


(ns kushi.repl
  (:require
   [clojure.string :as string]
   [kushi.utils :as util]
   [kushi.defs :as defs]
   [kushi.shorthand :as shorthand]
   [kushi.atomic :as atomic]))

(defn- convert-number2
  ([n]
   (convert-number2 n nil))
  ([n k]
   (if (number? n)
     (if-not (contains? defs/int-vals k)
       (keyword (str n "px"))
       n)
     n)))

(defn convert-vector2
  ([v]
   (convert-vector2 v nil))
  ([v k]
   #?(:cljs (js/console.log v))
   (mapv #(cond
            (vector? %)
            (convert-vector2 % k)
            (number? %)
            (convert-number2 % k)
            :else %)
         v)))


(defonce flex-map
  {:fg "flex-grow"
   :fd "flex-direction"
   :fs "flex-shrink"
   :f "flex"
   :fw "flex-wrap"
   :fb "flex-basis"})

(defn new-style-map-key [k v]
  (let [new-key (or (get flex-map k) (name k))]
    (keyword (str new-key "--" (if (number? v)
                                 (util/num->pxstr-maybe (shorthand/key-sh k) v)
                                 (name v))))))


(defn atomics-converted [atomics]
  (into
   []
   (map #(cond
           (re-find #"^bg-" (name %)) (keyword (clojure.string/replace (name %) #"bg-" "bgc--"))
           (= % :black) (keyword (str "c--" (name %)))
           (re-find #"ex$" (name %)) (keyword (str "letter-spacing--" (name %)))
           (re-find #"^nowrap" (name %)) :ws--nowrap
           (re-find #"^radius-" (name %)) (keyword (clojure.string/replace (name %) #"radius-" "border-radius--"))
           (re-find #"^\d\d?px" (name %)) (keyword (str "fs--" (name %)))
           (re-find #"^\d" (name %)) (keyword (str "fw--" (name %)))
           (contains? #{:uppercase
                        :lowercase
                        :captitalize
                        :full-width} %) (keyword (str "tt--" (name %)))
           (% atomic/declarative-classes) %
           :else (keyword (clojure.string/replace (name %) #"-" "--")))
        atomics)))


(defn style-map->vec [m k v]
  (into
   {}
   (reduce (fn [acc [key value]]
             (let [new-key (if (= key :=)
                             k
                             (keyword (str (name key) ":" (name k))))]
               (conj acc [new-key (if (vector? value) value #_(util/convert-vector2 value) value)])))
           []
           v)))

(defn style-map [style-map*]
  (reduce
   (fn [acc [k v]]
     (conj
      acc
      (cond
        (or (symbol? v) (list? v)) [k v]
        (map? v) (style-map->vec style-map* k v)
        (vector? v) [k v #_(util/convert-vector2 v k)]
        :else (new-style-map-key k v)))) [] style-map*))


(defmacro t+ [x]
  (let [[_ v m] x
        args (if (vector? v)
               (let [[atomics style-map*] v
                     atomics-converted (atomics-converted atomics)
                     style-map (style-map style-map*)
                     no-mqs-or-pseudos (into [] (filter #(not (map? %)) style-map))
                     mqs-or-pseudos (filter map? style-map)
                     sm (reduce
                         (fn [acc m]
                           (reduce (fn [acc [k v]]
                                     (conj acc
                                           (if (vector? v)
                                            [(keyword (str (name k))) (util/convert-vector2 v)]
                                            (keyword (str (name k) "--" (name v))))))
                                   acc
                                   m))
                         no-mqs-or-pseudos
                         mqs-or-pseudos)
                     ]
                 (into [] (concat atomics-converted
                                  sm)))
               v)
        sorted-args (into [] (sort-by (fn [v] (if (vector? v) (first v) v)) args))
        new-args (cons 's+ (remove nil? (conj sorted-args m)))]
    `(do
       (quote ~new-args))))
