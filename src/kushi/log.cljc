(ns ^:dev/always kushi.log
  (:require
   [clojure.pprint :refer [pprint]]
   [clojure.data :as data]))

(def compare-a (atom []))
(def compare-b (atom []))

(defn reset-log-states! []
  ;; For comparing results of same call with 2 different versions of the same fn during build process
  #_(let [eq? (= @kushi.log/compare-a @kushi.log/compare-b)]
    (pprint eq?)
    (when-not eq?
      (pprint (data/diff @compare-a @compare-b))))
  (reset! compare-a [])
  (reset! compare-b []))
