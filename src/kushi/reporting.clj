(ns ^:dev/always kushi.reporting
  (:require
   [io.aviso.ansi :as ansi]
   [clojure.pprint :refer [pprint]]
   [clojure.string :as string]
   [kushi.printing2 :refer [file+line+col-str]]
   [kushi.utils :as util :refer [mapj]]
   [kushi.config :refer [user-config version]]))


(defn report-line-item [n s]
  (when (pos-int? n) (str n " " s (when (< 1 n) "s"))))


(defn spaces-str [max x]
  (let [x (if (keyword? key) (name x) (str x))
        n (inc (- max (count x)))]
    (-> n (repeat " ") string/join)))


(defn format-line-items
  [coll initial-build?]
  (when (seq coll)
    (let [
          ;; coll             (mapv (fn [[k v]]
          ;;                          [k v])
          ;;                        coll)
          max-label-length (->> coll
                                (into {})
                                keys
                                (map (comp count name))
                                (apply max)
                                (+ 2))
          max-count-length (->> coll
                                (into {})
                                vals
                                (map #(count (str (first %))))
                                (apply max))]
      (reduce (fn [acc [k [current ndiff]]]
                (let [current (or current 0)
                      spaces  (spaces-str max-label-length k)
                      diff    (let [spaces (spaces-str max-count-length current)]
                                (when (and (int? ndiff)
                                           (not (zero? ndiff)))
                                  (cond (pos? ndiff)
                                        (str spaces ansi/bold-green-font "+" ndiff ansi/reset-font)
                                        (neg? ndiff)
                                        (str spaces ansi/bold-red-font ndiff ansi/reset-font))))]
                  (str acc (name k) spaces current (when diff (str " " diff)) "\n")))
              "Writing rules...\n"
              coll))))

(defn kushi-logging-prefix [build-id version]
  (str "[" build-id "] [Kushi v" version "]" ))

;; Formatting for kushi build report :simple option -------------------------------------------------
(defn simple-report
  [{:keys [build-id]} & lines*]
  (let [body-indent  ""
        lines        (reduce (fn [acc v] (concat acc (if (coll? v) v [v])))
                             []
                             (remove nil? lines*))
        lines-indent (map #(str body-indent %) lines)]
    (str (kushi-logging-prefix build-id version) " - " (string/join lines-indent))))

(defn simple-report2
  [{:keys [build-id initial-build? num-rules num-tokens]} & lines]
  (str (kushi-logging-prefix build-id version)
       " - "
       (string/join lines)
       (str "Wrote " num-rules " rules and " num-tokens " tokens.")))


;; Kushi build report --------------------------------------------------------------------------------


(defn cache-report
  [{:keys [cache-will-update?
           cache-diff-count
           diff-callsites]}]
  (if (and (:log-updates-to-cache? user-config)
           cache-will-update?)
    (str "Updated cache with "
         cache-diff-count
         " "
         (if (= 1 cache-diff-count) "entry" "entries")
         "."
         (when (:log-cache-call-sites? user-config)
           (str
            "\nCache update call sites:\n"
            (mapj "\n"
                  #(file+line+col-str (assoc %
                                             :printing/normal-font-weight?
                                             true))
                  (into [] (distinct diff-callsites)))
            "\n")))
    (when (true? (:caching? user-config))
      #_(str "Caching is not enabled.")
      (str "No updates to cache.\n"))))


(defn print-report!
  [{:keys [to-be-printed+
           num-rules
           num-tokens
           build-state
           initial-build?]
    :as   m}]
  (let [report-line-items-simple (str "Writing " num-rules " rules and " num-tokens " tokens.")
        report-line-items-stack  (format-line-items to-be-printed+ initial-build?)
        cache-report             (cache-report m)
        build-id                 (:shadow.build/build-id build-state)]

    (println
     (if (= :detailed
            (:log-build-report-style user-config))
       (simple-report2 {:build-id       build-id
                        :initial-build? initial-build?
                        :num-rules      num-rules
                        :num-tokens     num-tokens}
                       report-line-items-stack
                       cache-report)
       (simple-report {:build-id build-id}
                      report-line-items-simple)))))

(defn report! [build-id msg]
  (println (str "\n" (kushi-logging-prefix build-id version) msg "\n")))
