
;; TODO - DECIDE Between these 2
;; (s/def ::cssvar-name
;;   (s/and ::s|kw
;;          #(re-find (re-pattern (str "^" cssvar-name-re "$")) (name %))))


;; (s/def ::css-var-name
;;   (s/or :keyword? ::css-var-name-kw
;;         :string?  ::css-var-name-string))



(ns kushi.specs2
  (:require
   [clojure.string :as string]
   [kushi.defs :as defs]
   [clojure.spec.alpha :as s]))


;; UTILITY REGEX ---------------------------------------------------------
(def css-prop-base-re "-?[a-z]+[a-z-]*")
(def css-prop-re (str "(?:[^:\t\n\r\\s]+:)*" css-prop-base-re))
(def css-val-re-base "[#-]?[a-zA-Z0-9]+[_a-zA-Z0-9-:|\\.\\*]*")
(def css-val-re (str "^" css-val-re-base))
(def css-pseudo-element-re-fast ":(?:af|be|se|ba|pl|cu|fi|gr|ma|pa|sl|sp|-m|-w)")
(def css-pseudo-element-re (str "(?:" (string/join "|" (map name defs/pseudo-elements)) ")(?:\\(.*\\))?"))
(def css-pseudo-class-re (str "(?:" (string/join "|" (map name defs/pseudo-classes*)) ")(?:\\(.*\\))?"))
(def css-pseudo-element-followed-by-pseudo-class-re (str css-pseudo-element-re ":" css-pseudo-class-re))
(def cssvar-name-base-re "[-_a-zA-Z0-9]+")
(def cssvar-name-re (str "\\$" cssvar-name-base-re))
(def cssvar-in-css-re (str "var\\((" cssvar-name-re ")\\)"))
;; Clojure valid symbol related
(def clj-sym-special-chars "\\*\\+\\!\\-\\_\\'\\?\\<\\>\\=" )
(def clj-sym-special-chars2 "\\/\\:")
(def clj-sym-leading-char (str "[a-zA-Z" clj-sym-special-chars "]"))
(def clj-sym-following-chars (str "[a-zA-Z0-9" clj-sym-special-chars clj-sym-special-chars2 "]"))
(def clj-sym-re-base (str "^" clj-sym-leading-char clj-sym-following-chars "*"))
(def trailing-slash-re "\\/$")
(def trailing-colon-re "\\:$")
(def double-colon-re "\\:\\:")



;; UTILITY FNS -----------------------------------------------------------
(defn dot-kw->s [x] (subs (str x) 2))

(defn kw->s [x] (subs (str x) 1))

(defn kw?->s [x] (if (keyword? x) (kw->s x) x))

(defn re-find-with
  "Take a string for consumption by `re-pattern`, and a string or keyword to search against.
   Bookends the pattern string with \"^\" and \"$\"."
  [pattern x]
  (when (or (string? x) (keyword? x) (symbol? x))
    (let [s (kw?->s x)]
      (re-find (re-pattern (str "^" pattern "$")) s))))

(defn symbol-present? [coll]
  (some #(if (coll? %)
           (symbol-present? %)
           (symbol? %))
        coll))

(defn balanced-parens?
  "This is for checking css string values which are supplied by the user for balanced parens.
   It is easy to make a mistake such as \"rgba(50, 50, 50, var(--my-opacity)\", which will
   silently break the rest of the css that comes after it in a css file."
  ([expr] (balanced-parens? (clojure.string/split expr #"") 0))
  ([[x & xs] count]
   (cond (neg? count) false
         (nil? x) (zero? count)
         (= x "(") (recur xs (inc count))
         (= x ")") (recur xs (dec count))
         :else (recur xs count))))

;; SPECS -----------------------------------------------------------------

(s/def ::double-quoted-string
  (s/and string?
         #(and (< 1 (count %))
               (= \" (first %))
               (= \" (last %)))))

(s/def ::quoted-symbol
  (s/and (s/coll-of symbol? :kind seq? :count 2)
         #(= 'quote (first %))))

(s/def ::s|kw #(or (keyword? %) (string? %)))

(s/def ::s|kw|num #(or (keyword? %) (string? %) (number? %)))

(s/def ::dot-kw
  (s/and keyword?
         #(-> % name (string/starts-with? "."))))

;; Pseudo-ordering ----------------
;; CSS pseudo-elements cannot receive psuedo-classes.
;; For example there is no such selector in css such as `p::after:hover`.
;; The following spec will fail if it finds this pattern.
;; It is meant to check against a style-tuple prop (style or keyword), in isolation.

;; Example: (s/valid? ::with-valid-pseudo-order :after:hover:c) => false
;; Example: (s/valid? ::with-valid-pseudo-order "after:hover:c") => false
;; Example: (s/valid? ::with-valid-pseudo-order "&_.foo:after:hover:c") => false
;; Example: (s/valid? ::with-valid-pseudo-order "&_.foo:after:c") => true

;; Note that unlike css syntax, pseudo elements in kushi use single colon syntax `:`, NOT double colon syntax `::`.
;; This will work:     `:after:hover:c`
;; This will NOT work: `::after:hover:c`
;; This will work:     `"after:hover:c"`
;; This will NOT work: `":after:hover:c"`
(s/def ::with-valid-pseudo-order
  (s/and
    ::s|kw
    #(let [s (kw?->s %)]
       (if (and (re-find #":" s)
                (re-find (re-pattern css-pseudo-element-re-fast) (str ":" s)))
         (not (re-find (re-pattern css-pseudo-element-followed-by-pseudo-class-re) s))
         true))))

(s/def ::tokenized-css-alternation
  (s/and ::s|kw
         #(some->> % name (re-find #"^(?:[^\|]+\|[^\|]+)+$"))))

(s/def ::tokenized-css-shorthand
  (s/and ::s|kw
         #(some->> % name (re-find #"^(?:[^\|]+\:[^\|]+)+$"))))

(s/def ::css-selector-base
  (s/and string?
         #(re-find #"^-?[_a-zA-Z\\!]+[_a-zA-Z0-9-\\!]*$" %)))

(s/def ::str-sexp
  (s/and #(or (list? %) (seq? %))
         #(= 'str (first %))))

(s/def ::str-sexp-valid
  (s/and ::str-sexp
         (s/coll-of (s/or :string string? :binding symbol?))))

(s/def ::conditional-sexp
  (s/and #(or (list? %) (seq? %))
         #(contains? #{'if 'when 'cond 'if-let 'when-let 'if-not 'when-not 'case}
                     (first %))))

(s/def ::conditional-class
  (s/and ::conditional-sexp
         #(some (fn [x]
                  (s/valid? ::dot-kw x))
                %)))

;; New!
(s/def ::dot-kw-classname
  (s/and keyword?
         #(not (s/valid? ::tokenized-style %))
         ::dot-kw
         #(s/valid? ::css-selector-base (dot-kw->s %))))

;; New!
(s/def ::class
  (s/or :dot-kw-classname  ::dot-kw-classname
        :conditional-class ::conditional-class))

;; New!
(s/def ::defclass-class
  #(s/valid? ::dot-kw-classname %))


(s/def ::cssvar-name
  (s/and ::s|kw
         #(re-find (re-pattern (str "^" cssvar-name-re "$")) (name %))))

(s/def ::cssvar-name-base
  (s/and ::s|kw
         #(re-find (re-pattern (str "^" cssvar-name-base-re "$")) (name %))))

(s/def ::css-val-alphanumeric
  (s/and ::s|kw
         #(re-find (re-pattern css-val-re) (name %)) ))


(s/def ::quoted-css-fn-list
  (s/cat :quote #(= % 'quote)
         :list  #(and (seq? %)
                      (some->> % second first (re-find #"^__cssfn__[a-zA-Z-]+$")))))

(s/def ::cssfn-list-nested
  (s/and
    seq?
    (s/cat :cssfn-name (s/+ symbol?)
           :cssfn-args (s/* ::css-value-scalar))))

(s/def ::cssfn-list
  (s/and
    seq?
    (s/cat :cssfn-name (s/+ symbol?)
           :cssfn-args (s/* (s/or :css-value-scalar  ::css-value-scalar
                                  :cssfn-list-nested ::cssfn-list-nested)))))

(s/def ::cssfn-list-defclass-nested
  (s/and
    seq?
    (s/cat :cssfn-name (s/+ symbol?)
           :cssfn-args (s/* ::css-value-scalar-no-bindings))))

(s/def ::cssfn-list-defclass
  (s/and
    seq?
    (s/cat :cssfn-name (s/+ symbol?)
           :cssfn-args (s/* (s/or :css-value-scalar-no-bindings  ::css-value-scalar-no-bindings
                                  :cssfn-list-defclass-nested    ::cssfn-list-defclass-nested)))))

(s/def ::css-shorthand-inner-vector-member
  (s/or :css-value-scalar ::css-value-scalar
        :cssfn-list       ::cssfn-list))

(s/def ::css-shorthand-inner-vector
  (s/coll-of ::css-shorthand-inner-vector-member
             :kind vector?
             :min-count 2))

(s/def ::css-shorthand-vector
  (s/coll-of ::css-shorthand-inner-vector
             :kind vector?
             :count 1))

(s/def ::symbol-present
  #(symbol-present? %))

(s/def ::no-symbol-present
  #(not (symbol-present? %)))

(s/def ::css-shorthand-vector-defclass
  (s/and ::css-shorthand-vector
         ::no-symbol-present))

(s/def ::css-alternation-vector-member
  (s/or :css-value-scalar           ::css-value-scalar
        :css-shorthand-inner-vector ::css-shorthand-inner-vector))

(s/def ::ccs-alternation-vector
  (s/coll-of ::css-alternation-vector-member :kind vector?))

(s/def ::style-tuple-prop
  (s/and ::s|kw
         #(re-find-with css-prop-re (name %))
         ::with-valid-pseudo-order))

(s/def ::css-value-scalar
  (s/or :number                   number?
        :runtime-binding          symbol?
        :css-val-alphanumeric     ::css-val-alphanumeric
        :cssvar-name              ::cssvar-name))

(s/def ::css-value-scalar-no-bindings
  (s/or :number                   number?
        :css-val-alphanumeric     ::css-val-alphanumeric
        :cssvar-name ::cssvar-name))

(s/def ::pseudo-element-content
  (s/and string?
         #(re-find #"^\"[^\"]*\"$" %)))

(s/def ::style-tuple-value
  (s/or :css-value-scalar       ::css-value-scalar
        :cssfn-list             ::cssfn-list
        :ccs-alternation-vector ::ccs-alternation-vector
        :css-shorthand-vector   ::css-shorthand-vector
        :conditional-sexp       ::conditional-sexp
        :str-sexp               ::str-sexp
        :pseudo-element-content ::pseudo-element-content))

(s/def ::style-tuple-value-defclass
  (s/or :css-value-scalar-no-bindings ::css-value-scalar-no-bindings
        :cssfn-list                   ::cssfn-list-defclass
        :ccs-alternation-vector       ::ccs-alternation-vector
        :css-shorthand-vector         ::css-shorthand-vector
        :pseudo-element-content       ::pseudo-element-content))

(s/def ::style-tuple-with-css-var
  (s/tuple ::style-tuple-prop ::cssvar-name))

(s/def ::style-tuple
  (s/tuple ::style-tuple-prop ::style-tuple-value))

(s/def ::style-tuple-defclass
  (s/tuple ::style-tuple-prop ::style-tuple-value-defclass))

(s/def ::cssvar-tuple
  (s/tuple ::cssvar-name ::style-tuple-value))

(s/def ::cssvar-tuple-with-css-var
  (s/tuple ::cssvar-name ::cssvar-name))

(defn kw->tup [x re]
  (into [] (string/split (kw->s x) re 2)))

(defn kw->valid-tup? [x re spec]
  (when-let [v (kw->tup x re)]
    (s/valid? spec v)))

(s/def ::cssvar-tokenized*
  #(and
     (re-find (re-pattern (str "^" cssvar-name-re)) (name %))
     (re-find (re-pattern (str "\\$" css-val-re-base "$")) (name %))))

(s/def ::cssvar-tokenized
  (s/and keyword?
         (s/or :tokenized* ::cssvar-tokenized*
               ;; TODO maybe lose this?
               :valid-cssvar-tuple-on-split #(kw->valid-tup? % #"(?=\$)" ::cssvar-tuple))))




;; CSS RESET -------------------------------------------------------------
(s/def ::css-reset-selector (s/or
                              :string? string?
                              :vector? (s/and vector? #(seq %) (s/coll-of string?))))

;; TOKENIZED STYLE -------------------------------------------------------
(s/def ::tokenized-style
  (s/and keyword?
         (s/or :style-tuple
               #(kw->valid-tup? % #"--" ::style-tuple)

               :style-tuple-with-css-var
               #(kw->valid-tup? % #"--" ::style-tuple-with-css-var)

               :cssvar-tuple-with-css-var
               #(kw->valid-tup? % #"--" ::cssvar-tuple-with-css-var))))



;; STYLEMAP --------------------------------------------------------------
(s/def ::defclass-stylemap
  (s/and map?
         (s/coll-of ::style-tuple-defclass)))

(s/def ::stylemap
  (s/and map?
         (s/coll-of (s/or :style-tuple ::style-tuple
                          :cssvar-tuple ::cssvar-tuple))))

(s/def ::stylemap-conformance
  (s/and map?
         (s/every (fn [[k v]]
                    (and (s/valid? ::style-tuple-prop k)
                         (s/valid? ::style-tuple-value v))))))
(s/def ::style
  #(s/valid? ::stylemap %)
  #_(s/every #_::style-tuple (fn [[k v]]
                               (and (s/valid? ::style-tuple-key k)
                                    (s/valid? ::style-tuple-value v)))))

(s/def ::sx-attrs-map
  (s/and map?
         (s/keys :opt-un [::style])))

(s/def ::text (s/and string? (complement string/blank?)))

(s/def ::assigned-class
  (s/or :symbol symbol?
        :quoted-symbol ::quoted-symbol
        :text ::text))

(s/def ::style-tuple-value-imbalanced-string
  #(when (and (string? %)
              (not (s/valid? ::double-quoted-string %)))
     (not (balanced-parens? %))))

(s/def ::style-tuple-without-imbalanced-string
  #(and (s/valid? ::style-tuple %)
        (not (s/valid? ::style-tuple-value-imbalanced-string
                       (second %)))))

(s/def ::sx-args
  (s/cat
   :assigned-class (s/? ::assigned-class)
   :style-or-class (s/* (s/or :tokenized-style   ::tokenized-style
                              :cssvar-tokenized  ::cssvar-tokenized
                              :cssvar-tuple      ::cssvar-tuple
                              :style-tuple       ::style-tuple-without-imbalanced-string
                              :class             ::class
                              :conditional-class ::conditional-class))
   :sx-attrs-map   (s/? ::sx-attrs-map)))


(s/def ::valid-sx-arg
  (s/or :assigned-class                #(s/valid? ::assigned-class %)
        :tokenized-style               #(s/valid? ::tokenized-style %)
        :cssvar-tokenized              #(s/valid? ::cssvar-tokenized %)
        :cssvar-tuple                  #(s/valid? ::cssvar-tuple %)
        :style-tuple                   #(s/valid? ::style-tuple-without-imbalanced-string %)
        :class                         #(s/valid? ::class %)
        :conditional-class             #(s/valid? ::conditional-class %)
        :sx-attrs-map                  #(s/valid? ::sx-attrs-map %)))


(s/def ::sx-args-conformance
  (s/coll-of ::valid-sx-arg))


;; DEFCLASS --------------------------------------------------------------

(s/def ::defclass-name
  (s/and symbol?
         #(s/valid? ::css-selector-base (name %))))

(s/def ::defclass-style
  (s/or :tokenized-style      ::tokenized-style
        :style-tuple-defclass ::style-tuple-defclass))

(s/def ::defclass-style-or-class
  (s/or :defclass-style   ::defclass-style
        :dot-kw-classname ::dot-kw-classname))

(s/def ::defclass
  (s/cat :defclass-name     ::defclass-name
         :defclass-style    (s/* ::defclass-style-or-class)
         :defclass-stylemap (s/? ::defclass-stylemap)))



;; from og specs start ---------

(s/def ::defclass-class
  #(s/valid? ::dot-kw-classname %))

;; from og specs end -----------



(s/def ::valid-defclass-arg
  ;; TODO spec for symbol name validation
  (s/or :assigned-class       #(s/valid? symbol? %)
        :defclass-class       #(s/valid? ::defclass-class %)
        :style-tuple-defclass #(s/valid? ::style-tuple-defclass %)
        :tokenized-style      #(s/valid? ::tokenized-style %)
        :defclass-stylemap    #(s/valid? ::defclass-stylemap %)))


(s/def ::defclass-args
  ;; TODO spec for symbol name validation
  (s/cat
   :assigned-class          (s/? symbol?)
   :defclass-style-or-class (s/* ::defclass-style-or-class)
   :defclass-stylemap       (s/? ::defclass-stylemap)))


(s/def ::defclass-args2
  (s/coll-of ::valid-defclass-arg))



;; FONT-FACE -------------------------------------------------------------
(s/def ::font-face-val (s/or :keyword? keyword? :string? string?))
(s/def ::ascent-override ::font-face-val)
(s/def ::decent-override ::font-face-val)
(s/def ::font-display ::font-face-val)
(s/def ::font-stretch ::font-face-val)
(s/def ::font-style ::font-face-val)
(s/def ::font-weight ::font-face-val)
(s/def ::font-variant ::font-face-val)
(s/def ::font-feature-settings ::font-face-val)
(s/def ::font-variation-settings ::font-face-val)
(s/def ::line-gap-override ::font-face-val)
(s/def ::unicode-range ::font-face-val)
(s/def ::font-family string?)
(s/def ::src (s/or :string? string? :coll-of-strings? (s/coll-of string?)))

(s/def ::font-face-map
  (s/keys :req-un [::font-family ::src]
          :opt-un [::ascent-override
                   ::decent-override
                   ::font-display
                   ::font-stretch
                   ::font-style
                   ::font-weight
                   ::font-variant
                   ::font-feature-settings
                   ::font-variation-settings
                   ::line-gap-override
                   ::unicode-range]))

(s/def ::add-font-face-args
  (s/cat :opts ::font-face-map))

(s/def ::system-font-stack-weight
  #{300 400 500 700})

(s/def ::add-system-font-stack-args
  (s/coll-of ::system-font-stack-weight))

(defonce valid-font-face-map-ks
  (->> ::font-face-map
       s/describe
       rest
       (apply hash-map)
       vals
       (apply concat)
       (map (comp keyword name))
       (into [])))


;; CSS-VARS -------------------------------------------------------------

(s/def ::css-var-name-string
  (s/and string? #(re-find #"^\$\S+" %)))

(s/def ::css-var-name-kw
  (s/and keyword?
         #(s/valid? ::css-var-name-string (name %))))

(s/def ::css-var-name
  (s/or :keyword? ::css-var-name-kw
        :string?  ::css-var-name-string))


;; DEFKEYFRAMES ----------------------------------------------------------

(s/def ::keyframe-stylemap-prop
  #(re-find-with css-prop-base-re %))

(s/def ::keyframe-stylemap
  (s/map-of ::keyframe-stylemap-prop ::style-tuple-value-defclass))

(s/def ::keyframe
  (s/or :keyframe ::s|kw
        :keyframe-range (s/coll-of ::s|kw :kind vector?)))

(s/def ::keyframe-tuple
  (s/tuple ::keyframe ::keyframe-stylemap))

(s/def ::defkeyframes-args
  (s/cat :keyframes-name (s/or :symbol symbol?
                               :quoted-symbol ::quoted-symbol)
         :keyframes      (s/+ ::keyframe-tuple)))


;; THEME -----------------------------------------------------------------


(s/def ::google-font-weight #{100 200 300 400 500 600 700 800 900 1000})
(s/def ::google-font-weights (s/coll-of ::google-font-weight :kind vector?))
(s/def ::normal ::google-font-weights)
(s/def ::italic ::google-font-weights)
(s/def ::styles (s/and map? (s/keys :req-un [::normal ::italic])))
(s/def ::family string?)
(s/def ::google-font-opts (s/and map? (s/keys :req-un [::family ::styles])))
(s/def ::google-fonts* (s/coll-of string? :kind vector?))
(s/def ::google-fonts (s/coll-of ::google-font-opts :kind vector?))
(s/def ::stylish-map (s/map-of ::s|kw map?))
(s/def ::stylish-pairs (s/and vector?
                              #(even? (count %))
                              #(s/valid? ::stylish-map (apply hash-map %))))
(s/def ::ui ::stylish-pairs)
(s/def ::utility-classes ::stylish-pairs)
(s/def ::tokens (s/map-of ::cssvar-name
                          #(and (s/valid? ::style-tuple-value %)
                                (not (s/valid? ::style-tuple-value-imbalanced-string %)))))
(s/def ::font-loading (s/and map?
                             (s/keys :opt-un
                                     [::google-fonts
                                      ::google-fonts*])))

(s/def ::theme
  (s/and map?
         (s/keys :opt-un
                 [::font-loading
                  ::tokens
                  ::ui
                  ::utility-classes])))
