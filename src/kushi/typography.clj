(ns kushi.typography
  (:require
   [clojure.spec.alpha :as s]
   [garden.core :as garden]
   [garden.stylesheet :refer [at-font-face]]
   [kushi.printing2 :as printing2 :refer [kushi-expound]]
   [kushi.specs2 :as specs2]
   [kushi.state2 :as state2]
   [kushi.utils :as util :refer [keyed]]))


;; @FONT-FACE ------------------------------------------------------------

(defn- missing
  [problems]
  (let [missing*
        (into []
              (keep #(when (= (:in %) [0])
                       (let [k (some-> % :pred last last)]
                         {:path [0 k]
                          :key  k}))
                    problems))]
    (when (seq missing*) missing*)))

(defn- bad
  [problems]
  (some->> problems
           (keep (fn [{:keys [in val]}]
                   (when (< 1 (count in))
                     {:path  in
                      :entry [(last in) val]})))
           distinct
           (into [])))

(defn- fatal
  [bad missing]
  (some->> bad
           (filter #(contains? #{:font-family :src} %))
           (concat missing)
           (into [])))

(defn- weird [m]
  (let [valid-ks  specs2/valid-font-face-map-ks
        clean-map (select-keys m valid-ks)
        ret*      (filter (fn [[k _]] (not (get clean-map k))) m)
        ret       (when (seq ret*)
                    (mapv (fn [[k v]]
                            {:path  [0 k]
                             :entry [k v]})
                          ret*))]
    ret))

(defn add-font-face* [m]

  (let [spec        ::specs2/add-font-face-args
        problems    (some->> [m]
                             (s/explain-data spec)
                             :clojure.spec.alpha/problems)
        missing     (missing problems)
        bad         (bad problems)
        fatal       (fatal bad missing)
        weird       (weird m)
        cache-map   (state2/cached {:process :add-font-face
                                    :args [m]})
        css-rule    (when-not fatal
                      (or (:cached cache-map)
                          (garden/css (at-font-face m))))
        expound-str (when problems (kushi-expound spec [m]))]
    (merge (keyed css-rule)
           {:expound-str                 expound-str
            :entries/bad                 bad
            :entries/weird               weird
            :entries/missing             missing
            :entries/fatal               fatal
            :clojure.spec.alpha/problems problems
            :cache-map                   cache-map})))
