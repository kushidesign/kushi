(ns ^:dev/always kushi.core
  (:require-macros [kushi.core :refer [sx clean! add-font-face defclass defkeyframes add-system-font-stack]])
  (:require [clojure.string :as string]))


(defn cssfn? [x]
  (and (list? x)
       (= (first x) 'cssfn)
       (keyword? (second x))))

(declare cssfn)

(defn vec-in-cssfn [v]
  (string/join
   " "
   (map #(cond
           (cssfn? %) (cssfn %)
           (vector? %) (vec-in-cssfn %)
           (keyword? %) (name %)
           :else (str %))
        v)))

(defn cssfn* [[_ nm & args]]
  (str (name nm)
       "("
       (string/join
        ", "
        (map #(cond
               (cssfn? %) (cssfn %)
               (vector? %) (vec-in-cssfn %)
               (keyword? %) (name %)
              ;;  (string? %) (str "\"" % "\"")
               :else (str %))
             args))
       ")"))

(defn cssfn [& args]
  (cssfn (cons 'cssfn args)))

(defn !important [x]
  (cssfn (list 'important x)))

(defn inject-stylesheet

  "Expects a map with the following keys: :rel, :href, and :cross-origin(optional).
   Appends stylesheet as <link> element to the document <head>.
   Only appends if link with identical attributes does not already exist in the document head.

   Examples:

   (inject-stylesheet {:rel \"preconnet\"
                       :href \"https://fonts.gstatic.com\"
                       :cross-origin \"anonymous\"})

   (inject-stylesheet {:rel \"preconnet\"
                       :href \"https://fonts.googleapis.com\"})

   (inject-stylesheet {:rel \"stylesheet\"
                       :href \"https://fonts.googleapis.com/css2?family=Rock+Salt&display=swap\"})"

  [m]
  (let [attr-selector* (map (fn [[k v]] (str "[" (name k) "=" "\"" v "\"" "]")) m)
        existing-link (js/document.querySelector (str "link" (string/join "" attr-selector*)))]
    (when-not existing-link
      (let [link (js/document.createElement "link")]
        (doseq [[attr val] m]
          (.setAttribute link (name attr) (name val)))
        (try
          (.appendChild js/document.head link)
          (catch :default e (when ^boolean js/goog.DEBUG
                             (js/console.warn
                              "kushi.core/s+:\n\nFailed attempt to inject stylesheet (or link):\n\n"
                              m
                              "\n\n¯\\_(ツ)_/¯"))))))))
(defn- garden-mq-rule? [v]
  (and (map? v) (= :media (:identifier v))))

(defn inject-style-rules
  "Called at dev time for zippy previews."
  [css-rules selector]
  (let [css-rules-as-indexed-seq (map-indexed vector css-rules)
        sheet (.-sheet (js/document.getElementById "_kushi-dev_"))
        selector-set (into #{} (->> sheet .-rules (map #(aget % "selectorText"))))]

    ;Inject rules only if selector is not already in the sheet
    (when-not (contains? selector-set selector)
      (doseq [[_ rule-css] css-rules-as-indexed-seq
              :let [updated-num-rules-idx (-> sheet .-rules .-length)]]
        (try
          (.insertRule sheet rule-css updated-num-rules-idx)
          (catch :default e (js/console.warn
                             "kushi.core/s+:\n\nFailed attempt to inject malformed css rule:\n\n"
                             rule-css
                             "\n\n¯\\_(ツ)_/¯"
                             e)))))))

(defn- merge-with-style-warning
  [v k n]
  (js/console.warn
   (str
    "kushi.core/merge-with-style:\n\n "
    "The " k " value supplied in the " n " argument must be a map.\n\n "
    "You supplied:\n") v))

(defn- bad-style? [style n]
  (let [bad? (and style (not (map? style)))]
    (when bad? (merge-with-style-warning style :style n))
    bad?))

(defn- bad-class? [class n]
  (let [bad? (and class
                  (not (some #(% class) [seq? vector? keyword? string? symbol?])))]
    (when bad? (merge-with-style-warning class :class n))
    bad?))

(defn merge-with-style-class-coll
  [class bad-class?]
  (when-not bad-class?
    (if (or (string? class) (keyword? class))
      [class]
      class)))

(defn merge-with-style
  [{style1 :style class1 :class :as m1}
   {style2 :style class2 :class :as m2}]
  (let [[bad-style1? bad-style2?] (map-indexed (fn [i x] (bad-style? x i)) [style1 style2])
        [bad-class1? bad-class2?] (map-indexed (fn [i x] (bad-class? x i)) [class1 class2])
        merged-style              (merge (when-not bad-style1? style1) (when-not bad-style2? style2))
        class1-coll               (merge-with-style-class-coll class1 bad-class1?)
        class2-coll               (merge-with-style-class-coll class2 bad-class2?)
        classes                   (concat class1-coll class2-coll)
        ret                       (assoc (merge m1 m2) :class classes :style merged-style)]
    ret))
