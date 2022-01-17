(ns ^:dev/always kushi.reporting
  (:require
   [clojure.spec.alpha :as s]
   [clojure.string :as string]
   [io.aviso.ansi :as ansi]
   #_[clj-ph-css.core :as ph-css]
   [kushi.config :refer [user-config user-css-file-path version]]
   [kushi.printing :as printing :refer]
   [kushi.utils :as util]
   [kushi.specs :as specs]))

(defn simple-report
  [selected-ns-msg
   printables-pre
   printables-post]
  (let [bl "   " #_((first printing/rainbow2) "⡇  "
                                     #_"⋆   "
                                     #_"┆  "
                                     #_"│  ")
        header (str "kushi v" version)]
    (str "\n\n"
        ;;  (string/join (map (fn [ans char] (ans char)) (take (count header) (cycle printing/rainbow2)) header))
        ;;  (str (ansi/red "[") (ansi/magenta (str "kushi v" version))  (ansi/red "]"))
         (str "[kushi v" version "]")
         "\n"
         (str bl selected-ns-msg)
         "\n"
         (str bl "Writing to " user-css-file-path " ...")
         "\n"
         (str bl "(" (string/join ", " printables-pre) ")")
        ;;  "\n└"
         "\n\n")))

(def banner-border-color ansi/bold-black)

(defn hz-brdr
  [{:keys [width top? header color] :or {color ansi/black}}]
  (if top?
    (str (color (str "\n\n┌── ")) header " " (color (string/join (repeat (- width (+ 5 (count header))) "─"))))
    (color (str "\n└" (string/join (repeat width "─")) "\n\n"))))

(def v-border-char "│")

(def v-border-indent (str "\n" v-border-char "   "))

(defn v-border [color] (color v-border-indent))

(defn banner-report
  [selected-ns-msg
   printables-pre
   printables-post]
  (let [color         banner-border-color
        sep           (v-border color)
        hz-brdr-width 26
        header        (str "kushi v" version)
        brdr-opts     {:width hz-brdr-width
                       :header header
                       :theme printing/bold-rainbow2
                      ;;  :theme printing/green-red
                       :s "──"}
        report-lines (remove
                      nil?
                      (concat
                       [(printing/rainbow-border-title brdr-opts)
                        " "
                        selected-ns-msg
                        (when selected-ns-msg " ")
                        (str "Writing to " user-css-file-path " ...")
                        " "]
                       printables-pre
                       [(when printables-post
                          (str "Parsing css from " user-css-file-path " ..."))
                        (when printables-post
                          (str (string/join sep printables-post)))]))
        color-cycle  (take (count report-lines)
                           (cycle (printing/shift-cycle
                                   printing/bold-rainbow2
                                   1)))
        lines        (interleave
                      report-lines
                      (map #(% v-border-indent) color-cycle))
        bb-opts      (assoc brdr-opts :color-cycle color-cycle)]
    (string/join (concat lines [(printing/rainbow-border-bottom bb-opts)]))))


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
  (let [banner?                (= :banner (-> user-config :reporting-style))
        selected               (:select-ns user-config)
        selected-ns-msg        (when (s/valid? ::specs/select-ns-vector selected) (str "Target namespaces: " selected))
        report-format-fn       (if banner? banner-report simple-report)
        report-line-items-pre  (report-line-items @to-be-printed)
        report-line-items-post (when (:report-output? user-config)
                                 (line-items-confirmation @to-be-printed (parse-generated-css)))]
    (println
     (report-format-fn
      selected-ns-msg
      report-line-items-pre
      report-line-items-post))))




