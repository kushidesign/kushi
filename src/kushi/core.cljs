(ns ^:dev/always kushi.core
  (:require-macros [kushi.core :refer [keyed]])
  (:require [clojure.string :as string]
            [clojure.pprint :refer [pprint]]))

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

(defn inject-css*
  [css-rules
   identifier*
   sheet-id]
  (let [keyframes-nm   (and (map? identifier*) (:nm identifier*))
        identifier     (or keyframes-nm identifier*)
        rules-as-seq   (map-indexed vector css-rules)
        sheet          (.-sheet (js/document.getElementById sheet-id))
        identifier-set (->> sheet
                            .-rules
                            (map #(aget % (if keyframes-nm "name" "selectorText")))
                            (remove nil?)
                            (into #{}))
        already-there? (contains? identifier-set identifier)]

    #_(pprint {;;  :sheet (->> sheet .-rules)
             :already-there? already-there?
             :identifier*    identifier*
             :keyframes-nm   keyframes-nm
             :identifier     identifier
             :selector-set   identifier-set})

    ;Inject rules only if identifier is not already in the sheet
    (when-not already-there?
      (doseq [[_ rule-css] rules-as-seq
              :let         [updated-num-rules-idx (-> sheet .-rules .-length)]]
        (try
          (.insertRule sheet rule-css updated-num-rules-idx)
          (catch :default e (js/console.warn
                             "kushi.core/sx:\n\nFailed attempt to inject malformed css rule:\n\n"
                             rule-css
                             "\n\n¯\\_(ツ)_/¯"
                             e)))))))

#_(defn inject-css*
  "Called internally by kushi.core/sx at dev/run time for zippy previews."
  [css-rules selector sheet-id]
  (let [rules-as-seq (map-indexed vector css-rules)
        sheet        (.-sheet (js/document.getElementById sheet-id))
        selector-set (into #{} (->> sheet .-rules (map #(aget % "selectorText"))))]
    #_(js/console.log "cssRuleList" (.-cssRules sheet))
    #_(js/console.log {:css-rules    css-rules
                     :selector     selector
                     :rules-as-seq rules-as-seq
                     :sheet        sheet
                     :selector-set selector-set})

    ;Inject rules only if selector is not already in the sheet
    (when-not (contains? selector-set selector)
      #_(js/console.log "INJECTING:" css-rules)
      (doseq [[_ rule-css] rules-as-seq
              :let         [updated-num-rules-idx (-> sheet .-rules .-length)]]
        (try
          (.insertRule sheet rule-css updated-num-rules-idx)
          (catch :default e (js/console.warn
                             "kushi.core/sx:\n\nFailed attempt to inject malformed css rule:\n\n"
                             rule-css
                             "\n\n¯\\_(ツ)_/¯"
                             e)))))))

(defn inject-style-rules
  [css-rules selector]
   (when (seq css-rules)
     (inject-css* css-rules selector "_kushi-rules_")))

(defn inject-kushi-atomics [kushi-atomics]
  (when (seq kushi-atomics)
    (doseq [[selector css-rules] kushi-atomics]
      (inject-css* css-rules selector "_kushi-rules-shared_"))))

(defn inject! [css-rules selector kushi-atomics]
  (inject-kushi-atomics kushi-atomics)
  (inject-style-rules css-rules selector))

(defn merged-attrs-map
  ([attrs-base classlist css-vars]
   (merged-attrs-map attrs-base classlist css-vars nil))
  ([attrs-base classlist css-vars data-cljs]
   (assoc attrs-base
          :class
          (distinct classlist)
          :style css-vars
          :data-cljs data-cljs)))

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
  [{style1 :style class1 :class data-cljs1 :data-cljs :as m1}
   {style2 :style class2 :class data-cljs2 :data-cljs :as m2}]
  #_(pprint {:m1 m1 :m2 m2})
  (let [[bad-style1? bad-style2?] (map-indexed (fn [i x] (bad-style? x i)) [style1 style2])
        [bad-class1? bad-class2?] (map-indexed (fn [i x] (bad-class? x i)) [class1 class2])
        merged-style              (merge (when-not bad-style1? style1) (when-not bad-style2? style2))
        class1-coll               (merge-with-style-class-coll class1 bad-class1?)
        class2-coll               (merge-with-style-class-coll class2 bad-class2?)
        classes                   (concat class1-coll class2-coll)
        data-cljs                 (string/join " + " (remove nil? [data-cljs1 data-cljs2]))
        ret                       (assoc (merge m1 m2)
                                         :class classes
                                         :style merged-style
                                         :data-cljs data-cljs)]
    ret))


(defn hiccup? [x]
  (and (vector? x) (-> x first keyword?)))

(defn opts&children [xs]
  (let [[a & children*] xs
        attr*    (when (map? a) a)
        attr     attr*
        ;; #_(absorb-style opts*)
        children (if attr children* xs)]
    ;; (when-let [data (:data-cljs opts)]
    ;;   (reset! ui* (str "?" (:name data))))
    ;
    (keyed attr children)))

(defn target-attr [xs]
  (let [[a & children*] xs
        attr*    (when (map? a) a)
        attr     attr*
        ;; #_(absorb-style opts*)
        children (if attr children* xs)]
    ;; (when-let [data (:data-cljs opts)]
    ;;   (reset! ui* (str "?" (:name data))))
    ;
    (keyed attr children)))

(defn split-key [k re] (-> k name (string/split re)))

(defn hiccup-tag [k]
  (let [tokens  (split-key k #":")
        tag     (-> tokens first keyword)
        target? (-> tokens last (= "!"))]
    [tag target?]))

(def kushi-keys
  [:css :ident :data-cljs :element :prefix :ancestor])

(defn ->hiccup [x]
  (when (hiccup? x)
    (let [[tag* attr*] x
          [tag _]      (hiccup-tag tag*)
          attr         (when (map? attr*) attr*)]
      [tag attr])))

(defn target-tag [v]
  (assoc v 0 (keyword (-> v first name (str ":!")))))

; new one
(defn merge-hiccup [tag attr args]
  (let [user-attr   (when (map? (first args)) (first args))
        children    (if user-attr (rest args) args)
        merge?      (and user-attr (map? attr))
        attr-merged (if merge?
                       (merge-with-style attr (first args))
                       attr)]
    (into [] (concat [tag attr-merged] children))))
;
(defn target-vector [hiccup*]
  [[] hiccup*])

(defn gui
  ([hiccup*]
   (gui hiccup* nil) )
  ([hiccup* decorator]
   (let [[child-path target] (if (and (hiccup? hiccup*)
                                      (nil? (some hiccup? hiccup*)))
                               [[] hiccup*]
                               (target-vector hiccup*))
         [tag attr*] (->hiccup target)
         attr (if (map? decorator) (kushi.core/merge-with-style attr* decorator) attr*)]
     (fn [& args]
       (let [ret (when (= child-path []) (merge-hiccup tag attr args))]
         ret)))))
