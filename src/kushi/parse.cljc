(ns kushi.parse
  (:require
   [clojure.string :as string]
   [clojure.set :as set]
   [garden.stylesheet]
   [garden.core]
   [clojure.spec.alpha :as s]
   [kushi.state :as state]
   [kushi.specs :as specs]
   [kushi.shorthand :as shorthand]
   [kushi.defs :as defs]
   [kushi.printing :as printing]
   [kushi.config :refer [user-config]]
   [kushi.utils :as util :refer [pprint+ ?]]))

(defn derefed? [x]
  (s/valid? ::specs/derefed x))

(defn extract-vars* [coll]
  (mapv #(cond
           (symbol? %) %
           (vector? %) (extract-vars* %)
           :else nil)
        coll))

(defn extract-vars [selector* [css-prop val]]
  #_(util/pprint+ "<< extract-vars"
                {:selector* selector* :css-prop css-prop :val val})
  #_(util/pprint+
   "extract-vars"
   (let [!important? (and (list? val) (= '!important (first val)))
        val         (if !important? (second val) val)]
    (cond
      (symbol? val)  val
      (derefed? val) (second val)
      (vector? val)  (extract-vars* val)
      (list? val)    (when-not (= 'cssfn (first val))
                       {:__logic   (apply list val)
                        :selector* selector*
                        :css-prop  css-prop})
      :else          nil)))

  (let [!important? (and (list? val) (= '!important (first val)))
        val         (if !important? (second val) val)]
    (cond
      (symbol? val)  val
      (derefed? val) {:val-type  :derefed
                      :val       (second val)
                      :css-prop  css-prop}
      (vector? val)  (extract-vars* val)
      (list? val)    (when-not (= 'cssfn (first val))
                       {:val-type  :logic
                        :__logic   (apply list val)
                        :selector* selector*
                        :css-prop  css-prop})
      :else          nil)))

(defn sanitize-for-css-var-name [v]
  (string/escape
   v
   {\? "_QMARK"
    \! "_BANG"
    \# "_HASH"
    \+ "_PLUS"
    \$ "_DOLLAR"
    \% "_PCT"
    \= "_EQUALS"
    \< "_LT"
    \> "_GT"
    \( "_OB"
    \) "_CB"
    \& "_AMP"
    \* "_STAR"}))

(defn css-vars-map
  [extracted-vars]
  #_(util/pprint+ "<< css-vars-map" extracted-vars)
  (let [debug (= (-> extracted-vars first :selector*) "sfs-my-pee")
        ret (reduce
             (fn [acc v]
               #_(when debug (util/pprint+ "css-vars map: acc >>" acc))
               (cond
                 (and (map? v) (= :logic (:val-type v)))
                 (let [{:keys [selector* __logic css-prop]} v
                       k (util/css-var-for-sexp selector* css-prop)
                       v (util/process-sexp __logic selector* css-prop)
                       ]
                   #_(when debug (util/pprint+ "css-vars map: branch >>" k))
                   #_(when debug (util/pprint+ "css-vars map: branch >>" v))
                   (assoc acc
                          k #_(util/css-var-for-sexp selector* css-prop)
                          v #_(util/process-sexp __logic selector* css-prop)))

                 (and (map? v) (= :derefed (:val-type v)))
                 (assoc acc
                        (str "--" (sanitize-for-css-var-name (:val v)))
                        (list 'clojure.core/deref (:val v)))

                 :else
                 (assoc acc
                        (str "--" (sanitize-for-css-var-name v))
                        v)))
             {}
             extracted-vars)]
    #_(util/pprint+ "css-vars-map >>" ret)
    #_(when debug (util/pprint+ "css-vars map:RET!!! >>" ret))
    #_(when (= selector* "sfs-my-pee") (util/pprint+ "css-vars map >>" ret))
    ret))

(defn css-vars
  [styles selector*]
  #_(util/pprint+
   "<< css-vars"
   {:styles styles
    :selector* selector*})
  (let [ret (some->> styles
                     (filter vector?)
                     (map (partial extract-vars selector*))
                     flatten
                     (remove nil?)
                     distinct
                     css-vars-map)]
    #_(util/pprint+ "css-vars >>" ret)
    ret))

(defn scoped-class-syntax? [x]
  (and (keyword? x)
       (re-find #"\.-?[_a-zA-Z]+[_a-zA-Z0-9-\\/]*" (name x))))

(defn atomic-user-class [k]
  (when (keyword? k)
    (get @state/kushi-atomic-user-classes k)))

(defn +vars [styles* selector*]
  (map
   (fn [v]
     (if (s/valid? ::specs/style-tuple v)
       (let [[prop val] v]
         (cond
           (derefed? val) [prop (util/css-var-string (second val))]

           (symbol? val)  [prop (util/css-var-string val)]

           (list? val)    (cond
                            (= 'cssfn (first val))     [prop val]
                            (util/!important-var? val) [prop (util/css-var-string-!important val selector* prop)]
                            :else                      [prop (str "var(" (util/css-var-for-sexp selector* prop) ")")])

           :else          [prop val]))
        v))
   styles*))

(defn- conditionals [styles*]
  (remove nil?
          (apply concat
                 (map (fn [v]
                        (when (s/valid? ::specs/conditional-sexp v)
                          (map #(when (keyword? %)
                                  (or (some-> (subs (str %) 1) atomic-user-class :n)
                                      %))
                               v)))
                      styles*))))

(defn scoped-classname [x]
  (if-let [m (when (keyword? x)
               (-> x
                   (some-> name (subs 1) keyword)
                   atomic-user-class))]
    (some-> m :garden-vecs ffirst (subs 1))
    x))

(defn- +conditionals [coll]
  (map
   (fn [v]
     (if (s/valid? ::specs/conditional-sexp v)
       (if (some scoped-class-syntax? v)
         {:_class (map scoped-classname v)}
         {:_class v})
       v))
   coll))

(defn parse-classes
  "Converts any values with runtime dynamics into css var syntax"
  [coll]
  (let [conditional-class-keys (conditionals coll)
        +conditionals (+conditionals coll)
        conditional-class-sexprs (->> +conditionals
                                      (filter #(and (map? %) (:_class %)))
                                      (mapv #(:_class %)))
        classes* (filter #(not (or (map? %) (seq? %))) +conditionals)
        classes (-> (concat conditional-class-keys classes*) distinct)]

    {:conditional-class-sexprs conditional-class-sexprs
     :conditional-class-keys conditional-class-keys
     :classes classes}))


;; PARSING ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

;; Nix this stuff if not needed
(def quoted-string-with-spaces "\\_")
(def quoted-string-with-spaces-regex (re-pattern (str "\\s*" quoted-string-with-spaces "\\s*")))
(defn quoted-string-with-spaces? [s] (re-find quoted-string-with-spaces-regex s))
(def multiple-css-value-separator "\\|")
(def multiple-css-value-regex (re-pattern (str "\\s*" multiple-css-value-separator "\\s*")))
(defn multiple-css-values? [s] (re-find multiple-css-value-regex  s))
(def css-value-sh-separator "\\:")
(defn css-value-shorthand? [s] (re-find #"\:" s))
(defn css-var? [s] (re-find #"^var\(--.+\)(?:!important)?$" s))

(defn warn-if-bad-number
  [prop prop-hydrated numeric-string]
  (when-not (or (= "0" numeric-string)
                (contains? defs/int-vals (keyword prop-hydrated)))
    (let [m {:warning-type   :unitless-number
             :prop           prop
             :prop-hydrated  prop-hydrated
             :numeric-string numeric-string
             :current-macro  @state/current-macro
             :form-meta      (:form-meta @state/current-sx)}]
      #_(printing/console-warning-number (vector m))
      #_(util/pprint+ "warn" {:m m})
      (swap! state/compilation-warnings conj m))
    ))

(defn parse-sh-value
  "Parses a shorthand css value string (which has been parsed out of an atomic keyword) into a potentially nested vector"
  [{:keys [hydrated-k k nested-shorthand?]} s]
  (or
   ;; If value is using s+ shorthand syntax with an enumerated value
   ;; Example: parse-sh-value :ta :c), which is shorthand for {:text-align :center}, return :center
   (shorthand/enum-prop-shorty (keyword hydrated-k) (keyword s))

   (cond
     (or (= "content" hydrated-k) (= "content" k) (= :content k))
     s

     ;; Parse mulitiple shorthand syntax
     ;; Example: (parse-sh-value :box-shadow "0:0:0:10:black|0:0:5:red")
     (multiple-css-values? s)
     (mapv (partial parse-sh-value {:hydrated-k hydrated-k :k k :nested-shorthand? true})
           (string/split s (re-pattern multiple-css-value-separator)))

     ;; Parse shorthand syntax into double vector
     ;; Example: (parse-sh-value :p "15px:20px") ;=> [[:15px :20px]]
     ;; Maybe check and make sure hydrated-k is not :animation?
     (css-value-shorthand? s)
     (let [ret (mapv (partial parse-sh-value {:hydrated-k hydrated-k :k k})
                     (string/split s (re-pattern css-value-sh-separator)))]
       (if nested-shorthand? ret [ret]))

     ;; If numeric, convert number to px value based on prop.
     (util/numeric-string? s)
     (do (warn-if-bad-number k hydrated-k s)
         s)

     ;; If string with spaces as underscores, convert to string with spaces.
     (and (not (css-var? s)) (quoted-string-with-spaces? s))
     (str "\"" (string/replace s #"_" " ") "\"")

     :else
     s)))




;; This adds dot-class to any kushi-classes
(defn with-dot-kushi-classes
  [coll]
  (map (fn [x]
         (cond
           (and (s/valid? ::specs/kushi-class-kw x)
                (get @state/kushi-atomic-user-classes x))
           (->> x name (str ".") keyword)

           (list? x)
           (map #(or
                  (some->> %
                           atomic-user-class
                           :n
                           name
                           (str ".")
                           keyword)
                  %)
                x)
           :else x))
       coll))

#_(defn maybe-convert-map
  [x]
  (if (and (= true (:map-mode? user-config))
           (= 1 (count x))
           (-> x first map?))
    (->> x
         first
         (map (fn [[k v]]
                ; TODO add coverage for these specs
                (if (or (and (= v :kushi/class) (s/valid? ::specs/scoped-class-syntax k))
                        (and (= v :kushi/mixin) (keyword? k)))
                  k
                  [k v]))))
    x))

(defn hydrate-css
  [{css-prop :css-prop val* :val :as m} selector*]
  #_(util/pprint+ "hydrate-css" {:m m})
  (if (and css-prop val)
    (let [hydrated-css-prop-kw (shorthand/key-sh (if (string? css-prop) (keyword css-prop) css-prop))
          val (if (or (string? val*) (keyword? val*))
                 (parse-sh-value {:hydrated-k (name hydrated-css-prop-kw)
                                  :k css-prop}
                                 (name val*))
                 val*)
          val+ (util/process-value val hydrated-css-prop-kw selector*)]
      #_(? "hydrate-css" {:val* val* :val val :val+ val+})

      (assoc m :css-prop (name hydrated-css-prop-kw) :val val+))
    m))

(defn format-combo [s]
  (-> s
      (string/replace #"_" " ")
      (string/replace #"([>\+\~])" " $1 ")))

(defn format-mod
  "If mod is pseudo-class, prefixes a \":\".
   If mod is pseudo-element, prefixes a \"::\".
   If mod is combo-selector, formats appropriately.
   Otherwise returns nil for dud selector."
  [mods&prop s]
  ;; First, extract the key by string/replacing any suffixed syntax.
  ;; Example: :nth-child(3) => :nth-child
  (let [k (some-> s (string/replace #"\(.*\)$" "") keyword)]
    (cond
      (contains? defs/pseudo-classes k)
      (str ":" s)
      (contains? defs/pseudo-elements k)
      (str "::" s)
      (re-find #"^[\.\>\+\~\_].+$" s)
      (format-combo s)
      :else
      ;; Save this for reporting bad modifier to user
      (do
        (swap!
           state/current-sx
           assoc-in
           [:bad-mods mods&prop]
           (if-let [bad-mods (get (some-> @state/current-sx :bad-mods) mods&prop nil)]
             (conj bad-mods s)
             [s]))
        nil))))


(defn mods&prop->map [mods&prop]
  (let [coll (string/split mods&prop #":")
        prop (last coll)
        mods* (drop-last coll)
        mq (when-not (empty? mods*) (specs/find-with (first mods*) specs/mq-re))
        formatted-mods (remove nil? (map (partial format-mod mods&prop) (if mq (rest mods*) mods*)))
        mods (when-not (empty? formatted-mods) (string/join formatted-mods))]
    #_(util/pprint+ "mods&prop->map" {:mods&prop mods&prop
                                    :mods* mods* :mods mods
                                    :formatted-mods formatted-mods})
    (into {}
          (filter (comp some? val)
                  {:mods mods :css-prop prop :mq mq}))))


(defn kushi-style->token [selector* x]
  (when (or (keyword? x) (vector? x))
    (let [[mods&prop val] (if (vector? x)
                            (let [[p v] x] [(name p) (specs/kw?->s v)])
                            (string/split (name x) #"--"))
          m2* (mods&prop->map mods&prop)
          m2 (if val (assoc m2* :val val) m2*)
          hydrated (hydrate-css m2 selector*)]
      #_(util/pprint+ "hydrated" {:mods&prop mods&prop :val val :m2* m2* :m2 m2})
      hydrated)))


(defn reduce-styles
  [styles]
  (let [styles-flat (map #(-> % (select-keys [:css-prop :val]) vals vec) styles)
        ret (reduce (fn [acc [k v]]
                      (if (and k v) (assoc acc k v) acc))
                    {}
                    styles-flat)]
    ret))


(defn reduce-media-queries
  [grouped selector]
   (reduce
    (fn [acc [mq-key styles+mixins]]
      (let [defaults (when-let [defaults (:_no-mods_ styles+mixins)]
                       [selector defaults])
            mods (reduce (fn [acc [mod-key style-map]]
                           (conj acc [(str selector mod-key) style-map]))
                         []
                         (dissoc styles+mixins :_no-mods_))
            mq-map ((keyword mq-key) (:media user-config))
            at-media-args (concat [mq-map] [defaults] mods)
            at-media-rule (apply garden.stylesheet/at-media at-media-args)]
        (conj acc at-media-rule)))
    []
    (dissoc grouped :_media-default_)))

(defn kw-subs1 [x]
  (when (keyword? x)
    (-> x name (subs 1) keyword)))

(defn class->styles [x]
  (when-let [kw-no-dot (kw-subs1 x)]
    (some-> (get @state/kushi-atomic-user-classes kw-no-dot) :args)))

(defn add-mods-to-classes
  [styles mq&mods]
  (remove nil?
          (map (fn [v]
                 (cond
                   (vector? v)
                   (let [[css-prop value] v] [(str mq&mods ":" (name css-prop)) value])
                   (keyword? v)
                   (keyword (str mq&mods ":" (name v)))))
               styles)))

(defn class-with-mods->styles [x]
  (when (s/valid? ::specs/kushi-dot-class-with-mods x)
    (let [as-string (if (vector? x) (first x) (name x))
          [mq & args] (string/split as-string #":")
          kushi-class (-> args last keyword)
          styles (some->> kushi-class keyword class->styles)
          mq&mods (string/join ":" (cons mq (drop-last args)))
          ret (add-mods-to-classes styles mq&mods)]
      ret)))

(defn with-hydrated-classes [coll]
  (reduce
   (fn [acc x]
     (if-let [rules (class->styles x)]
       (apply conj acc rules)
       (if-let [rules-w-mods (class-with-mods->styles x)]
         (apply conj acc rules-w-mods)
         (conj acc x))))
   []
   coll))


(defn styles-by-mod-reducer
  [acc [mod-key* tokenized-styles]]
  (let [mod-key (or (when-not (string/blank? mod-key*) mod-key*) :_no-mods_)]
    (assoc acc
           mod-key
           (reduce-styles tokenized-styles))))


(defn styles-by-mq-reducer
  [acc [mq-key* tokenized-styles]]
  (let [mq-key (or mq-key* :_media-default_)]
    (assoc acc
           mq-key
           (reduce styles-by-mod-reducer
                   {}
                   (group-by :mods tokenized-styles)))))


(def mq-ks (->> kushi.config/user-config :media keys (map name)))

(defn grouped-and-sorted-by-mqs
  [tokenized-styles]
  (into {}
        (sort-by (fn [[k _]] (when (string? k) (.indexOf mq-ks k)))
                 (group-by :mq tokenized-styles))))

(defn grouped-by-mqs [tokenized-styles]
  (let [by-mqs (grouped-and-sorted-by-mqs tokenized-styles)
        grouped (reduce
                 styles-by-mq-reducer
                 {}
                 by-mqs)]
    grouped))

(defn garden-vecs [grouped selector]
  (let [base [selector (-> grouped :_media-default_ :_no-mods_)]
        mods (reduce (fn [acc [mod-key style-map]]
                       (conj acc [(str selector mod-key) style-map]))
                     []
                     (-> grouped :_media-default_ (dissoc :_no-mods_)))
        mqs (reduce-media-queries grouped selector)
        ret (into [] (concat [base] mods mqs))
        ]
    ret))

