(ns ^:dev/always kushi.arguments
 (:require
  [clojure.spec.alpha :as s]
  [clojure.string :as string]
  [par.core :refer [!? ? ?+]]
  [kushi.parse :as parse]
  [kushi.specs :as specs]
  [kushi.state :as state]
  [kushi.utils :as util]))

;TODO move this to utils
(defmacro keyed [& ks]
  `(let [keys# (quote ~ks)
         keys# (map keyword keys#)
         vals# (list ~@ks)]
     (zipmap keys# vals#)))

(defn reduce-by-registered
  [coll state-coll]
  (util/partition-by-pred #(contains? state-coll %) coll))

(defn register-classes [coll state-coll]
  (when (seq coll)
    (doseq [k coll]
      #_(println (str "swapping " k " into " @state-coll))
      (swap! state-coll conj k))))

(defn resolved-classes [x]
  #_(?+ :resolved-classes x)
  (when x
    (let [class                  (util/into-coll x)
          [kushi-classes
           non-kushi-classes]    (util/partition-by-pred
                                  #(or (contains? (:defclass @state/declarations) %)
                                       (contains? @state/kushi-atomic-user-classes %))
                                  class)
          [defclasses
           kushi-atomics]        (reduce-by-registered
                                  kushi-classes
                                  (:defclass @state/declarations))
          [defclasses-used
           defclasses-unused]    (reduce-by-registered
                                  defclasses
                                  @state/defclasses-used)
          [kushi-atomics-used
           kushi-atomics-unused] (reduce-by-registered
                                  kushi-atomics
                                  @state/atomic-declarative-classes-used)]
      #_(?+ :rc-class class)

      #_(when (some #(= :foo %) class)
        #_(? @state/declarations)
        #_(? @state/atomic-declarative-classes-used)
        #_(? (keyed kushi other defclasses kushi-atomics kushi-atomics-unused kushi-atomics-used defclasses-unused defclasses-used))
        (register-classes defclasses-unused state/defclasses-used)
        (register-classes kushi-atomics-unused state/atomic-declarative-classes-used))

      (keyed defclasses kushi-atomics non-kushi-classes))))

(defn tokens+attrs [args]
  (let [[arg & more]  args
        [tokens attrs] (cond (map? arg) [nil arg]
                             more       (if (map? (last args))
                                          [(drop-last args) (last args)]
                                          [args nil]))]
    [tokens attrs]))

(defn dotkw->kw [x] (cond (s/valid? ::specs/dot-kw x) (parse/kw-subs1 x)
                          (coll? x) (map dotkw->kw x)
                          :else x))


(defn style-tokens-map [coll]
  (reduce (fn [acc v]
            (if (s/valid? ::specs/style-kw v)
              (let [[k v] (-> v name (string/split #"--"))]
                (assoc acc (keyword k) (keyword v)))
              acc))
          {}
          coll))

(defn new-class-sorted
  [classes-from-tokens+
   attrs*]
  (let [sortfn    (fn [a] (if (coll? a) (first "zzz") (first (name a))))
        new-class (distinct (concat classes-from-tokens+ (:class attrs*)))
        ret       (into [] (sort-by sortfn new-class))]
    ret))

(defn new-args
  "Takes args and reorganizes it into internal/legacy format"
  [args]
  (let [[tokens attrs*]              (tokens+attrs args)
        [class-tokens* style-tokens] (util/partition-by-spec ::specs/tokenized-classes tokens)
        style-tokens-map             (style-tokens-map style-tokens)
        class-tokens                 (map dotkw->kw class-tokens*)
        classes-from-attrs           (resolved-classes (:class attrs*))
        classes-from-tokens          (resolved-classes class-tokens)
        classes-from-tokens+         (apply concat (vals classes-from-tokens))
        new-style                    (merge style-tokens-map (:style attrs*))
        new-class                    (new-class-sorted classes-from-tokens+ attrs*)
        attrs                        (merge attrs* {:class new-class
                                                    :style new-style})
        attrs+                       (dissoc attrs :class :style)
        new-args                     [(assoc (:style attrs) :. (:class attrs)) attrs+]]

    #_(? consolidated)
    (!? :bam
        (keyed
         args
         tokens
         style-tokens
         style-tokens-map
         class-tokens*
         class-tokens
         attrs*
         attrs
         classes-from-attrs
         classes-from-tokens
         classes-from-tokens+
         new-args))))

(defn combine-classes [coll]
  (let [f     (fn [acc v] (if (coll? v) (into [] (concat acc v)) (conj acc v)))
        ret  (->> coll
                  (reduce f [])
                  (remove nil?)
                  distinct
                  (into []))]
    ret))

(defn merge-attr [kushi-map attr-map]
  #_(?+ (keyed m maps))
  (let [classes [(:. kushi-map) (:class attr-map)]
        class   (combine-classes classes)
        styles* (dissoc kushi-map :.)
        style   (merge styles* (:style attr-map))
        ret     (assoc attr-map :class class :style style)]
    #_(?+ (keyed classes class* class ret))
    ret))
  

(defn consolidated [args]
  (if (s/valid? ::specs/map-mode-style+attr args)
    (vector (merge-attr (first args) (second args)))
    args))
