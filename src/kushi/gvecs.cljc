(ns kushi.gvecs
  (:require [kushi.utils :as util]

   [fireworks.core :refer [? !? ?> !?>]]
            [kushi.config :refer [user-config]]
            [garden.stylesheet]))

(defn inner1
  [acc [compound-selector coll]]
  (let [prop-vals* (mapv (fn [m]
                           [(:css-prop m)
                            (:css-value m)])
                         coll)
        prop-vals  (into {} prop-vals*)]
    (conj acc [compound-selector prop-vals])))


(defn mq-inner
  [acc [mq coll]]
  (let [media-maps    (:media user-config)
        mq-map        (some-> mq keyword media-maps)
        at-media-args (concat [mq-map] coll)
        at-media-rule (apply garden.stylesheet/at-media at-media-args)]
    (conj acc at-media-rule)))


(defn mq-gvecs-ordered [mqs]
  (let [mq-gvecs*
        (reduce mq-inner [] mqs)

        ordered-mqmaps
        (-> user-config :media vals)

        mq-gvecs-ordered*
        (map #(filter (fn [x]
                        (= % (some-> x :value :media-queries)))
                      mq-gvecs*)
             ordered-mqmaps)

        mq-gvecs-ordered
        (apply concat mq-gvecs-ordered*)]

    mq-gvecs-ordered))


(defn gvecs [coll]
  (let [grouped-by-mq1
        (group-by :mq coll)

        grouped-by-mq2
        (reduce (fn [acc [mq coll]]
                  (let [grouped* (group-by :compound-selector coll)
                        grouped  (reduce inner1 [] grouped*)]
                    (assoc acc mq grouped)))
                {}
                grouped-by-mq1)

        [no-mq mqs]
        (util/partition-by-pred (fn [[mq _]] (nil? mq)) grouped-by-mq2)

        base-gvecs
        (some-> no-mq first second)

        mq-gvecs-ordered
        (mq-gvecs-ordered mqs)

        gvecs
        (concat base-gvecs mq-gvecs-ordered)]

    gvecs))
