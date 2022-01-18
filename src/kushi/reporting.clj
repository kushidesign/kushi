(ns ^:dev/always kushi.reporting
  (:require
   [clojure.spec.alpha :as s]
   [clojure.string :as string]
   [io.aviso.ansi :as ansi]
   [clj-ph-css.core :as ph-css]
   [kushi.config :refer [user-config user-css-file-path version kushi-cache-path]]
   [kushi.printing :as printing :refer [ansi-rainbow]]
   [kushi.utils :as util]
   [kushi.specs :as specs]
   [par.core :refer [? !?]]))

(def writing-to-css-msg (str "Writing to " user-css-file-path " ..."))
(def parsing-css-msg (str "Parsing " user-css-file-path " ..."))

(defn simple-report
  [{:keys [header]} & lines*]
  (let [bl           "   "
        lines        (reduce (fn [acc v] (concat acc (if (coll? v) v [v]))) [] (remove nil? lines*))
        lines-indent (map #(str bl %) lines)]
    (string/join "\n" (concat ["\n" header] lines-indent ["\n"]))))

(def banner-border-color ansi/bold-black)

(defn hz-brdr
  [{:keys [width top? header color] :or {color ansi/black}}]
  (if top?
    (str (color (str "\n\n┌── ")) header " " (color (string/join (repeat (- width (+ 5 (count header))) "─"))))
    (color (str "\n└" (string/join (repeat width "─")) "\n\n"))))

(def v-border-char "│")

(def v-border-indent (str "\n" v-border-char "   "))

(defn v-border [color] (color v-border-indent))

(defn nl->sp [x] (if (= x "\n") " " x))

(defn reduce-report-lines [lines*]
  (reduce
   (fn [acc v]
     (concat acc (if (coll? v) (map nl->sp v) [(nl->sp v)])))
   []
   (remove nil? lines*)))

(defn k->ansi [k]
  (or (when (keyword? k) (k printing/ansi-color-map))
      ansi/bold-black))

(defn banner-report
  [{:keys [header
           indent
           theme
           border-color
           header-color
           border-string
           border-width
           border-v-char]
    :or   {indent        3
           header-color  :black
           border-color  :bold-black
           border-string "──"
           border-width  50
           border-v-char "│"}}
   & lines*]
  (let [indent         (if (or (not (number? indent))
                               (not (pos? indent)))
                         1
                         indent)
        header-width   (+ (count (ansi/strip-ansi header)) 2 indent)
        post-hd-width* (if header (- border-width header-width) border-width)
        post-hd-width  (printing/closest-number post-hd-width* (count (ansi/strip-ansi border-string)) :up)
        post-hd-diff   (- post-hd-width post-hd-width*)
        border-width   (+ border-width post-hd-diff)
        border-color   (k->ansi border-color)
        header-color   (k->ansi header-color)
        theme          (or theme
                           (into [] (repeat 6 border-color)))
        brdr-opts      {:border-width  border-width
                        :header        (header-color header)
                        :theme         theme
                        :border-string border-string
                        :indent        indent}
        report-lines   (concat
                        [(printing/rainbow-border-title brdr-opts) " "]
                        (reduce-report-lines lines*))
        color-cycle    (take (count report-lines)
                             (cycle (printing/shift-cycle
                                     theme
                                     1)))
        lines          (interleave
                        report-lines
                        (map #(% (str "\n" border-v-char (apply str (repeat indent " ")))) color-cycle))
        bb-opts        (assoc brdr-opts :color-cycle color-cycle)]
    (string/join
     (concat lines
             [(printing/rainbow-border-bottom bb-opts)]))))

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

(def simple-bl "   ")

(defn format-line-items [banner? coll]
  (when (and coll (seq coll))
    (if banner? coll (str "(" (string/join ", " coll) ")"))))

(defn print-report! [to-be-printed cache-will-update?]
  (calculate-total-style-rules! to-be-printed)
  (let [banner?                (= :banner (-> user-config :reporting-style))
        selected               (:select-ns user-config)
        selected-ns-msg        (when (s/valid? ::specs/select-ns-vector selected) (str "Target namespaces: " selected))
        report-format-fn       (if banner? banner-report simple-report)
        report-line-items-pre* (report-line-items @to-be-printed)
        report-line-items-pre  (format-line-items banner? report-line-items-pre*)
        report-line-items-post* (when (:report-output? user-config)
                                  (line-items-confirmation @to-be-printed (parse-generated-css)))
        report-line-items-post (format-line-items banner? report-line-items-post*)
        cache-report           (when (and (:report-cache-update? user-config) cache-will-update?)
                                 (str "Updated " kushi-cache-path))
        header-text            (str "kushi v" version)
        header-simple          (str (ansi/red "[") (ansi/blue header-text)  (ansi/red "]"))
        header                 (if banner? header-text header-simple)]

    (println
     (report-format-fn
      {:header header
       :border-color :red
       :header-color :bold-blue
       :indent 3
       }
      selected-ns-msg
      (when report-line-items-pre [(when banner? "\n") writing-to-css-msg (when banner? "\n")])
      report-line-items-pre
      (when report-line-items-post parsing-css-msg)
      report-line-items-post
      cache-report))))

(defn report! [ns msg]
 (println (str "\n" (ansi/red "[") (ansi/blue ns) (ansi/red "]") msg "\n"))                          )
