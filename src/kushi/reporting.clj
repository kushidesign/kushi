(ns ^:dev/always kushi.reporting
  (:require
   [get-rich.core :refer [callout]]
   [clojure.string :as string]
   [kushi.config :refer [user-config version]]))

(defn spaces-str [max x]
  (let [x (if (keyword? key) (name x) (str x))
        n (inc (- max (count x)))]
    (-> n (repeat " ") string/join)))

(defn format-line-items
  [coll]
  (when (seq coll)
    (let [max-label-length (->> coll
                                (into {})
                                keys
                                (map (comp count name))
                                (apply max)
                                (+ 2))]
      (reduce (fn [acc [k [current]]]
                (let [current (or current 0)
                      spaces  (spaces-str max-label-length k)]
                  (str acc (name k) spaces current "\n")))
              "Writing rules...\n\n"
              coll))))

(defn kushi-logging-prefix [build-id version]
  (str "[" build-id "] [Kushi v" version "]" ))


;; Kushi build report ----------------------------------------------------------

(defn print-report!
  [{:keys [to-be-printed+
           num-rules
           num-tokens
           build-state]
    :as   m}]
  (if (= :detailed (:log-build-report-style user-config))
      ;; With individual counts
    (callout {:type  :subtle
              :label (str "[Kushi v" version "]" )}
             (str "\n"
                  (format-line-items to-be-printed+)
                  "\n"
                  (str "Wrote " num-rules " rules and " num-tokens " tokens.")))
      ;; Simple version
    (println
     (str (kushi-logging-prefix (:shadow.build/build-id build-state) version)
          " - "
          (str "Writing " num-rules " rules and " num-tokens " tokens.")))))

(defn report! [build-id msg]
  (println (str "\n" (kushi-logging-prefix build-id version) msg "\n")))
