(ns ^:dev/always kushi.reporting
  (:require
   [clojure.spec.alpha :as s]
   [clojure.string :as string]
   [io.aviso.ansi :as ansi]
   [clj-ph-css.core :as ph-css]
   [kushi.config :refer [user-config user-css-file-path version kushi-cache-path]]
   [kushi.printing :as printing]
   [kushi.ansiformat :as ansiformat]
   [kushi.utils :as util :refer [? keyed]]
   [kushi.specs :as specs]
   ))

;; Build report messages ---------------------------------------------------------------
(def writing-to-css-msg (str "Writing to " user-css-file-path " ..."))
(def parsing-css-msg (str "Parsing " user-css-file-path " ..."))



;; Build report helpers ---------------------------------------------------------------
(defn check-or-x [check?]
 (if check? (ansi/bold-green "✓ ") (ansi/bold-red "✘ ")))

(defn line-item-check
  [expected results k label]
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

(defn format-rule-count [n label {:keys [before]}]
  (str before
       n
       " "
       label
       (when (and label (not (string/blank? label))) " ")
       (str "rule" (when (> n 1) "s"))))

(defn rules-under-styles [mq-count rules-under-mq-count]
  (when (pos? rules-under-mq-count)
    (str "    including "
         (format-rule-count rules-under-mq-count "" {})
         " under "
         mq-count
         " mq" (when (> mq-count 1) "s"))))

(defn style-rules-details
  [total under-mq? mq-count rules-under-mqs label]
  [(when (pos? total)
     (str (format-rule-count total label {:bold? false :before "  - "}) (when under-mq? ",")))
   (when under-mq?
     (rules-under-styles mq-count rules-under-mqs))])

(defn report-line-items
  [{:keys [font-face
           keyframes
           total-style-rules
           defclass-style-rules-total
           normal-style-rules-total
           defclass-style-rules-under-mqs
           normal-style-rules-under-mqs] :as m}]
  (let [defclass-under-mq? (when defclass-style-rules-under-mqs
                             (pos? defclass-style-rules-under-mqs))
        normal-under-mq?   (when normal-style-rules-under-mqs
                             (pos? normal-style-rules-under-mqs))
        banner?            (= :banner (:reporting-style user-config))
        opts               {}]
   (remove
    nil?
    (concat
     [(when (pos? font-face) (format-rule-count font-face "font-face" opts))
      (when (and banner? (pos? font-face)) " ")
      (when (pos? keyframes) (format-rule-count keyframes "keyframes" opts))
      (when (and banner? (pos? keyframes)) " ")
      (when (pos? total-style-rules) (format-rule-count total-style-rules "style" opts))]
     (when (and banner? (pos? defclass-style-rules-total))
        (style-rules-details
         defclass-style-rules-total
         defclass-under-mq?
         (:defclass-mq-count m)
         defclass-style-rules-under-mqs
         "defclass style"))
     (when (and banner? (pos? normal-style-rules-total))
        (style-rules-details
         normal-style-rules-total
         normal-under-mq?
         (:normal-mq-count m)
         normal-style-rules-under-mqs
         "element style"))))))


(defn calculate-total-style-rules!
  [m]
  (let [normal-style-rules-total   (+ (or (:normal-style-rules @m) 0)
                                      (or (:normal-style-rules-under-mqs @m) 0))
        defclass-style-rules-total (+ (or (:defclass-style-rules @m) 0)
                                      (or (:defclass-style-rules-under-mqs @m) 0))
        style-rules                (+ (or (:normal-style-rules @m) 0)
                                      (or (:defclass-style-rules @m) 0))
        style-rules-under-mqs      (+ (or (:normal-style-rules-under-mqs @m) 0)
                                      (or (:defclass-style-rules-under-mqs @m) 0))
        total-style-rules          (+ style-rules style-rules-under-mqs)]
    (swap! m
           merge
           (keyed normal-style-rules-total
                  defclass-style-rules-total
                  style-rules
                  style-rules-under-mqs
                  total-style-rules))))

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

(defn format-line-items [banner? coll]
  (when (and coll (seq coll))
    (if banner? coll (str "(" (string/join ", " coll) ")"))))


;; Formatting for kushi build report :simple option --------------------------------------------------
(defn simple-report
  [{:keys [header]} & lines*]
  (let [bl           "   "
        lines        (reduce (fn [acc v] (concat acc (if (coll? v) v [v]))) [] (remove nil? lines*))
        lines-indent (map #(str bl %) lines)]
    (string/join "\n" (concat ["\n" header] lines-indent ["\n"]))))

(defn select-ns-msg []
  (let [selected (:select-ns user-config)]
    (when (s/valid? ::specs/select-ns-vector selected)
      (str "Targeting namespaces: " selected))))

;; Kushi build report --------------------------------------------------------------------------------
(defn print-report! [to-be-printed cache-will-update?]
  (calculate-total-style-rules! to-be-printed)
  (let [banner?                 (= :banner (-> user-config :reporting-style))
        report-format-fn        (if banner? ansiformat/panel simple-report)
        report-line-items-pre*  (report-line-items @to-be-printed)
        report-line-items-pre   (format-line-items banner? report-line-items-pre*)
        report-line-items-post* (when (:report-output? user-config)
                                  (line-items-confirmation @to-be-printed (parse-generated-css)))
        report-line-items-post  (format-line-items banner? report-line-items-post*)
        cache-report            (when (and (:report-cache-update? user-config) cache-will-update?)
                                  (str "Updated " kushi-cache-path))
        header-text             (str "kushi v" version)
        header-simple           (str (ansi/red "[") (ansi/blue header-text)  (ansi/red "]"))
        header                  (if banner? header-text header-simple)]

    (println
     (report-format-fn
      {:header       header
       ;;  :header-weight :bold
       :theme        printing/bold-rainbow2
       ;; :border-color :red
       ;; :border-seq       bs
       ;; :border-bl-string bs
       ;; :border-tl-string bs
       ;; :border-v-char    bs
       ;; :header-color :bold-blue
       :border-width 50
       ;;  :border-weight :bold
       :indent       3}
      (:selected-ns-msg @to-be-printed)
      (when report-line-items-pre (remove nil? [(when banner? "\n") writing-to-css-msg (when banner? "\n")]))
      report-line-items-pre
      (when report-line-items-post parsing-css-msg)
      report-line-items-post
      cache-report))))

(defn report! [ns msg]
 (println (str "\n" (ansi/red "[") (ansi/blue ns) (ansi/red "]") msg "\n")))



;; Reporting for duplicate idents, defclasses, and keyframes  ------------------------------------------------
