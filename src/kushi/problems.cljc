(ns ^:dev/always kushi.problems
  (:require [kushi.utils :as util :refer [keyed]]
            [clojure.spec.alpha :as s]
            [kushi.specs2 :as specs2]))

(defn problematics [coll problems*]
  (let [last-index (dec (count coll))
        [stylemap
         args]     (util/partition-by-pred
                    (fn [{:keys [in val]}]
                      (and (map? val)
                           (-> in first (= last-index))))
                    problems*)
        args       (map #(select-keys % [:val :in]) args)
        stylemap   (-> stylemap first (select-keys [:val :in]))]
    [stylemap args]))

(defn spec-problems
  [coll spec]
  (->> coll
       (s/explain-data spec)
       :clojure.spec.alpha/problems))

(defn bad-stylemap
  [stylemap]
  (when stylemap
    (let [bad-stylemap          (-> stylemap :val :style)
          bad-stylemap-path*    (:in stylemap)
          [_ bad-entries]       (util/partition-by-spec ::specs2/style-tuple bad-stylemap)
          bad-stylemap-path     (conj bad-stylemap-path* :style)
          bad-stylemap-path-map {bad-stylemap-path bad-entries}]
      (keyed bad-stylemap
             bad-entries
             bad-stylemap-path-map))))

(defn problems
  [args
   validation-spec
   conformance-spec]
  (when-not (s/valid? validation-spec args)
    (let [bad-strings      (->> args
                                (map-indexed (fn [idx arg]
                                               (when-not (zero? idx)
                                                 (when (string? arg) [[idx] arg]))))
                                (remove nil?)
                                (into {}))
          problems         (spec-problems args conformance-spec)

          [stylemap*
           args*]          (problematics args problems)

          bad-args         (merge (reduce (fn [acc {:keys [in val]}]
                                            (assoc acc in val))
                                          {}
                                          args*)
                                  bad-strings)

          bad-args-vals    (->> bad-args vals (into #{}))


          bad-stylemap     (bad-stylemap stylemap*)
          ret              (merge
                            bad-stylemap
                            (keyed bad-args bad-args-vals bad-strings))]
      ret)))
