(ns ^:dev/always kushi.stylesheet
  (:require
   [clojure.spec.alpha :as s]
   [clojure.string :as string]
   [io.aviso.ansi :as ansi]
   [clj-ph-css.core :as ph-css]
   [garden.stylesheet]
   [garden.core :as garden]
   [kushi.config :refer [user-config]]
   [kushi.printing :refer [ansi-rainbow]]
   [kushi.state :as state]
   [kushi.specs :as specs]
   [kushi.utils :as util]))

(defn garden-vecs-injection
  [garden-vecs]
  (into []
        (map
         :rule-css
         (remove
          (fn [{x :garden-vec}] (and (vector? x) (nil? (second x))))
          (map (fn [v]
                 {:garden-vec v
                  :rule-css (garden.core/css v)})
               garden-vecs)))))

(def user-css-file-path
  (str (or (:css-dir user-config) (:static-css-dir user-config))
       "/"
       (or (:css-filename user-config) "kushi.css")))

(defn append-css-chunk!
  [{:keys [css-text
           comment
           content
           defclass?]}]
  (let [cmnt (when comment (str "\n\n/*" comment "*/\n\n"))
        content (str cmnt content)]
    (reset! css-text
            (if defclass?
              (str content "\n" @css-text)
              (str @css-text "\n" content)))))

(defn has-mqs? [coll]
  (and (map? coll)
        (some-> coll :value :media-queries)
        coll))

(defn bunch-mqs [garden-vecs]
  (reduce (fn [acc m]
            (let [mq (-> m :value :media-queries)]
              (let [existing-rules (get acc mq)
                    rules (some-> m :value :rules)]
                (assoc acc mq (concat existing-rules rules)))))
          {}
          (filter has-mqs? garden-vecs)))

(defn atomic-classes-mq
  [garden-vecs*]
  (let [medias (-> user-config :media vals)
        mq-idx (fn [x]
                 (let [mq  (-> x :value :media-queries)
                       idx (first (keep-indexed (fn [idx v] (when (= mq v) idx)) medias))]
                   idx))
        ret*   (mapv #(let [[mq args] %]
                        (apply (partial garden.stylesheet/at-media mq) args))
                     (bunch-mqs garden-vecs*))
        ret    (sort-by mq-idx < ret*)]
    ret))

(defn print-status [n kind]
  (println (str "    " n " unique " kind)))

;; ! Update kushi version here for console printing
(def version* "1.0.0")

;; You can optionally unsilence the ":LOCAL" bit when developing kushi from local filesystem (for visual feedback sanity check).
(def version (str version* #_":LOCAL"))

(def license-comment-header
  (str "/*! kushi v" version " | EPL License | https://github.com/paintparty/kushi */"))

(defn simple-report
  [selected-ns-msg
   printables-pre
   printables-post]
  (string/join
   "\n"
   (remove nil?
           [(str "\n-- kushi v" version " -------------------------------\n")
            selected-ns-msg
            (str "Writing to " user-css-file-path " ...")
            (str (string/join "\n" printables-pre) "\n")
            (str "Parsing css from " user-css-file-path " ...")
            (str (string/join "\n" printables-post))
            "\n-----------------------------------------------\n"])))

(defn banner-report [selected-ns-msg printables]
  (apply ansi-rainbow
         (concat
          [(str (ansi/bold (str "kushi v" version)))
           (when selected-ns-msg :br)
           selected-ns-msg
           :br
           (str "Writing to " user-css-file-path " ...")
           :br]
          printables)))

(defn rules-under-styles [mq-count rules-under-mq-count]
  (let [plural? (> rules-under-mq-count 1)]
    (when plural?
      (str ", including "
           rules-under-mq-count
           " rule" (when plural? "s") " under "
           mq-count
           " media quer" (if (> mq-count 1) "ies" "y")))))

(defn check-or-x [check?]
 (if check? (ansi/bold-green "✓ ") (ansi/bold-red "✘ ")))

(defn line-item-check
  [expected results k label]
  (let [n (get expected k nil)]
    (when (pos? n)
      (let [check? (= n (get results k nil))]
        (str (check-or-x check?)
             n
             " "
             label
             " rule" (when (> n 1) "s"))))))

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
   [(when (pos? font-face) (str font-face " @font-face rule" (when (> font-face 1) "s")))
    (when (pos? keyframes) (str keyframes " @keyframes rule" (when (> keyframes 1) "s")))
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

(defn append-at-font-face!
  [{:keys [css-text to-be-printed]}]
  (let [font-face-count (count @state/user-defined-font-faces)]
    (swap! to-be-printed assoc :font-face font-face-count)
    (when (pos-int? font-face-count)
      (append-css-chunk!
       {:css-text css-text
        :comment  "Font faces"
        :content  (string/join "\n" @state/user-defined-font-faces)})
      (reset! state/user-defined-font-faces []))))

(defn defkeyframe-content []
  (string/join
   "\n"
   (map (fn [[nm frames]]
          (str "@keyframes "
               (name nm)
               " {\n"
               (garden.core/css frames)
               "\n}\n"))
        @state/user-defined-keyframes)))

(defn append-defkeyframes!
  [{:keys [css-text to-be-printed]}]
  (let [keyframes-count (count @state/user-defined-keyframes)]
    (swap! to-be-printed assoc :keyframes keyframes-count)
    (when (pos-int? keyframes-count)
      (append-css-chunk!
       {:css-text css-text
        :comment  "Animation Keyframes"
        :content  (defkeyframe-content)})
      (reset! state/user-defined-keyframes {}))))

(defn count-mqs-rules [mqs]
  (count (apply concat (map #(some-> % :value :rules) mqs))))

(defn no-declarations? [coll]
  (and (vector? coll)
       (= (count coll) 2)
       (string? (first coll))
       (nil? (second coll))))

(defn append-defclasses!
  [{:keys [pretty-print? css-text to-be-printed]}]
  (when-not (empty? @state/atomic-declarative-classes-used)
    (let [gv                  (map #(let [normalized-class-kw (util/normalized-class-kw %)]
                                      (some-> @state/kushi-atomic-user-classes
                                              normalized-class-kw
                                              :garden-vecs))
                                   @state/atomic-declarative-classes-used)
          garden-vecs*        (apply concat (concat gv))
          garden-vecs         (->> garden-vecs*
                                   (remove has-mqs?)
                                   (remove no-declarations?))
          atomic-classes-mq   (atomic-classes-mq garden-vecs*)

          defclass-mq-count   (count atomic-classes-mq)
          defclass-style-rules-under-mqs (count-mqs-rules atomic-classes-mq)
          total-defclasses    (+ defclass-style-rules-under-mqs (count garden-vecs))]
      (swap! to-be-printed
             assoc
             :defclass-style-rules-under-mqs
             defclass-style-rules-under-mqs
             :defclass-style-rules
             (count garden-vecs)
             :defclass-mq-count
             defclass-mq-count)
      (append-css-chunk!
       {:css-text css-text
        :content  (garden/css {:pretty-print? pretty-print?} garden-vecs)
        :comment  "Atomic classes"})

      (when (pos-int? defclass-mq-count)
        (append-css-chunk!
         {:css-text css-text
          :content  (garden/css {:pretty-print? pretty-print?} atomic-classes-mq)
          :comment  "Atomic classes, media queries"})))
    (reset! state/atomic-declarative-classes-used #{})))


(defn append-rules!
  [{:keys [css-text pretty-print? to-be-printed]}]
  (let [rules       (map (fn [[k v]] (when v [k v]))
                         (:rules @state/garden-vecs-state))
        mqs         (remove
                     nil?
                     (map (fn [[k v]]
                            (when-let [as-seq (seq v)]
                              (apply garden.stylesheet/at-media
                                     (cons k as-seq))))
                          (dissoc @state/garden-vecs-state :rules)))
        garden-vecs (remove nil? (concat rules mqs))]

    (swap! to-be-printed
           assoc
           :normal-style-rules-under-mqs
           (count-mqs-rules mqs)
           :normal-style-rules
           (count rules)
           :normal-mq-count
           (count mqs)
           )
    (append-css-chunk!
     {:css-text css-text
      :content  (garden/css {:pretty-print? pretty-print?} garden-vecs)
      :comment  "Component styles"})))


(defn create-css-file
  {:shadow.build/stage :compile-finish}
  [build-state]
  (let [pretty-print? (if (:shadow.build/mode build-state) true false)
        printables    (atom [])
        to-be-printed (atom {})
        css-text      (atom license-comment-header)
        m             {:css-text      css-text
                       :pretty-print? pretty-print?
                       :printables    printables
                       :to-be-printed to-be-printed}]

    (append-at-font-face! m)
    (append-defkeyframes! m)
    (append-defclasses! m)
    (append-rules! m)

    (use 'clojure.java.io)
    (spit user-css-file-path @css-text :append false)

    

    (print-report! to-be-printed )

    (reset! state/garden-vecs-state state/garden-vecs-state-init))

  ;; Must return the build state
  build-state)

(defn garden-mq-rule? [v]
  (and (map? v) (= :media (:identifier v))))
