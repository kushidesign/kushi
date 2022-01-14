(ns ^:dev/always kushi.reporting
  (:require
   [clojure.spec.alpha :as s]
   [clojure.string :as string]
   [io.aviso.ansi :as ansi]
   [clj-ph-css.core :as ph-css]
   [kushi.config :refer [user-config user-css-file-path]]
   [kushi.printing :refer [ansi-rainbow]]
   [kushi.utils :as util]
   [kushi.specs :as specs]))

;; ! Update kushi version here for console printing
(def version* "1.0.0")

;; You can optionally unsilence the ":LOCAL" bit when developing kushi from local filesystem (for visual feedback sanity check).
(def version (str version* #_":LOCAL"))

(defn simple-report
  [selected-ns-msg
   printables-pre
   printables-post]
  (let [sep "\n:   "]
    (string/join
     sep
     (remove nil?
             [(str "\n\n .. kushi v" version " ..........................................." sep sep)
              selected-ns-msg
              (str "Writing to " user-css-file-path " ...")
              (str (string/join sep printables-pre) sep)
              (str "Parsing css from " user-css-file-path " ...")
              (str (string/join sep printables-post))
              "\n ...........................................................\n"]))))

(defn banner-report
  [selected-ns-msg
   printables-pre
   printables-post]
  (apply ansi-rainbow
         (concat
          [(str (str "kushi v" version))
           (when selected-ns-msg :br)
           selected-ns-msg
           :br
           (str "Writing to " user-css-file-path " ...")
           :br]
          printables-pre
          [:br
           (str "Parsing css from " user-css-file-path " ...")
           :br]
          printables-post)))


(defn rules-under-styles [mq-count rules-under-mq-count]
  (when (pos? rules-under-mq-count)
    (str ", including "
         rules-under-mq-count
         " rule" (when (> rules-under-mq-count 1) "s") " under "
         mq-count
         " media quer" (if (> mq-count 1) "ies" "y"))))

(defn check-or-x [check?]
 (if check? (ansi/bold-green "✓ ") (ansi/bold-red "✘ ")))

(defn line-item-check
  [expected results k label]
  #_(util/pprint+ {:expected expected :results results})
  (let [expected (get expected k nil)
        actual   (get results k nil)]
    (when (pos? expected)
      (let [check? (= expected actual)]
        (str (check-or-x check?)
             actual
             " "
             label
             " rule" (when (> actual 1) "s")
             (when-not check? (str " (" expected " expected)")))))))

(defn line-items-confirmation
  [expected results]
  (remove
   nil?
   [(line-item-check expected results :font-face "font-face")
    (line-item-check expected results :keyframes "keyframes")
    (line-item-check expected results :total-style-rules "style")
    ]))

(defn report-line-items
  [{:keys [font-face
           keyframes
           total-style-rules
           defclass-style-rules-total
           normal-style-rules-total] :as m} ]
  (remove
   nil?
   [(when (pos? font-face) (str font-face " font-face rule" (when (> font-face 1) "s")))
    (when (pos? keyframes) (str keyframes " keyframes rule" (when (> keyframes 1) "s")))
    (when (pos? keyframes) (str total-style-rules " style rules:"))
    (when (pos? defclass-style-rules-total)
      (str "  - " defclass-style-rules-total
           " defclass style rules"
           (rules-under-styles (:defclass-mq-count m) (:defclass-style-rules-under-mqs m))))
    (when (pos? normal-style-rules-total)
      (str "  - " normal-style-rules-total
           " style rules"
           (rules-under-styles (:normal-mq-count m) (:normal-style-rules-under-mqs m))))]))


(defn calculate-total-style-rules!
  [m]
  (let [normal-style-rules-total   (+ (:normal-style-rules @m)
                                      (:normal-style-rules-under-mqs @m))
        defclass-style-rules-total (+ (:defclass-style-rules @m)
                                      (:defclass-style-rules-under-mqs @m))
        style-rules                (+ (:normal-style-rules @m)
                                      (:defclass-style-rules @m))
        style-rules-under-mqs      (+ (:normal-style-rules-under-mqs @m)
                                      (:defclass-style-rules-under-mqs @m))
        total-style-rules          (+ style-rules style-rules-under-mqs)]
    (swap! m
           assoc
           :normal-style-rules-total
           normal-style-rules-total
           :defclass-style-rules-total
           defclass-style-rules-total
           :style-rules
           style-rules
           :style-rules-under-mqs
           style-rules-under-mqs
           :total-style-rules
           total-style-rules)))


(defn parse-generated-css []
  (let [file-contents   (slurp user-css-file-path)
        parsed          (ph-css/string->schema file-contents)
        font-face-rules (filter #(= (:type %) :font-face-rule) parsed)
        keyframes-rules (filter #(= (:type %) :keyframes-rule) parsed)
        mqs             (filter #(= (:type %) :media-rule) parsed)
        mqs-styles      (apply concat (map :rules mqs))
        style-rules     (filter #(= (:type %) :style-rule) parsed)]

    #_(util/pprint+ "media-rules" media-rules)
    #_(util/pprint+ "style-rules" style-rules)
    #_(util/pprint+
       "to-be-printed"
       @to-be-printed)

    {:font-face    (count font-face-rules)
     :keyframes    (count keyframes-rules)
     :style-rules  (count style-rules)
     :style-rules-under-mqs (count mqs-styles)
     :total-style-rules (+ (count style-rules) (count mqs-styles))}))


(defn print-report! [to-be-printed]
  (calculate-total-style-rules! to-be-printed)
  (let [selected               (:select-ns user-config)
        selected-ns-msg        (when (s/valid? ::specs/select-ns-vector selected)
                                 (str "Compiling styles for namespaces: " selected))
        report-format-fn       (if (= :banner (-> user-config :reporting-style))
                                 banner-report
                                 simple-report)
        report-line-items-pre  (report-line-items @to-be-printed)
        report-line-items-post (line-items-confirmation @to-be-printed (parse-generated-css))]
    (println
     (report-format-fn
      selected-ns-msg
      report-line-items-pre
      report-line-items-post))))




