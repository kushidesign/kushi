(ns kushi.specs
  (:require
   [kushi.state :as state]
   [clojure.string :as string]
   [kushi.atomic :as atomic]
   [kushi.defs :as defs]
   [clojure.spec.alpha :as s]
   [kushi.config :refer [user-config]]))

;; Utility regex ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
(def mq-key "[a-z0-9-]+")
(def css-pseudo-element-re-fast ":(?:af|be|se|ba|pl|cu|fi|gr|ma|pa|sl|sp|-m|-w)")
(def css-pseudo-element-re (str "(?:" (string/join "|" (map name defs/pseudo-elements)) ")(?:\\(.*\\))?"))
(def css-pseudo-class-re (str "(?:" (string/join "|" (map name defs/pseudo-classes)) ")(?:\\(.*\\))?"))
(def css-pseudo-element-followed-by-pseudo-class-re (str css-pseudo-element-re ":" css-pseudo-class-re))
(def css-pseudo* (str "(?:" css-pseudo-element-re "|" css-pseudo-class-re ")"))
(def css-pseudo-re "[a-z]+[a-z-]*(?:\\(.*\\))?")
(def css-prop-re "-?[a-z]+[a-z-]*")
(def css-prop-at-media-re (str mq-key ":" css-prop-re))
;; This needs a concept of short hand and comma-seperated (where end val ends with *)
(def kushi-css-val-re "[a-z0-9-]+[_a-zA-Z0-9-:|\\.\\*]*")
(def kushi-css-prop&val-re (str css-prop-re "(?:--)" kushi-css-val-re))
(def kushi-css-prop&val-at-media-re (str mq-key ":" kushi-css-prop&val-re))
(def kushi-css-prop&val-with-mods-re (str "(?:" css-pseudo* ":)+" kushi-css-prop&val-re))
(def kushi-css-prop&val-with-mods-at-media-re (str mq-key ":" kushi-css-prop&val-with-mods-re))
(def kushi-style-css-prop-re (str "(?:[^:\t\n\r]+:)*" css-prop-re))
(def mq-re (some->> user-config :media keys (map name) (string/join "|")))
;; Utility regex end ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;



;; spec & regex utils ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
(defn kw->s [x] (subs (str x) 1))

(defn kw?->s [x] (if (keyword? x) (kw->s x) x))

(defn find-in [x pattern]
  (let [s (kw?->s x)]
    (re-find (re-pattern pattern) s)))

(defn find-with [x pattern]
  (let [s (kw?->s x)]
    (re-find (re-pattern (str "^" pattern "$")) s)))
;;spec & regex utils end ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;



;; kushi specs ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
(s/def ::config
  (s/keys :req-un [(or ::element ::prefix ::ns-attr-key ::f ::ident ::ancestor ::classname)]))

(s/def ::responsive-config
  (s/and map? not-empty))

(s/def ::namespaced-keyword
  (s/and keyword? #(re-find #"/" (str %))))

(s/def ::style-kw-declarative
  (s/and keyword?
         #(or (get atomic/declarative-classes %)
              ; Nix this
              (get atomic/declarative-classes (-> % name (subs 1) keyword)))))

(s/def ::style-kw-declarative2
  (s/and keyword?
         #(or (get atomic/declarative-classes %)
              (get atomic/declarative-classes (-> % name (subs 1) keyword)))))

(s/def ::s-or-kw #(or (keyword? %) (string? %)))

(s/def ::css-pseudo-class
  (s/and ::s-or-kw
         #(find-with % css-pseudo-class-re)))

(s/def ::css-pseudo-element
  (s/and ::s-or-kw
         #(find-with % css-pseudo-element-re)))

(s/def ::css-pseudo
  (s/and ::s-or-kw
         #(find-with % css-pseudo-re)))

(s/def ::css-prop
  (s/and ::s-or-kw
         #(find-with % css-prop-re)))

(s/def ::kushi-style-css-prop
  (s/and ::s-or-kw
         #(find-with % kushi-style-css-prop-re)))

(s/def ::with-valid-pseudo-order
  #(let [s (kw?->s %)]
     (if (and (re-find #":" s)
              (re-find (re-pattern css-pseudo-element-re-fast) (str ":" s)))
       (not (re-find (re-pattern css-pseudo-element-followed-by-pseudo-class-re) s))
       true)))

(s/def ::css-prop-at-media
  (s/and ::s-or-kw
         #(find-with % css-prop-at-media-re)))

(s/def ::kushi-css-value
  (s/and ::s-or-kw
         #(not (find-in % "--"))
         #(find-with % kushi-css-val-re)))

(s/def ::css-selector-base
  (s/and string?
         #(re-find #"^-?[_a-zA-Z]+[_a-zA-Z0-9-]*$" %)))

(s/def ::class-kw
  (s/and keyword?
         #(s/valid? ::css-selector-base (name %))))

(s/def ::defclass-name
  (s/and symbol?
         #(s/valid? ::css-selector-base (name %))))

;; Excpects a keyword w/o a preceding dot
(s/def ::kushi-class-defined #(get @state/kushi-atomic-user-classes %))

(s/def ::kushi-class-kw
  (s/and keyword?
         #(not (s/valid? ::kushi-style-kw-dynamic %))
         #(s/valid? ::css-selector-base (kw->s %))))

(s/def ::kushi-dot-class-kw
  (s/and keyword?
         #(-> % name (string/starts-with? "."))
         #(not (re-find #"--|/" (kw->s %)))
         #(s/valid? ::css-selector-base (subs (kw->s %) 1))))

(s/def ::kushi-dot-class-kw-defined
  (s/and ::kushi-dot-class-kw
         #(s/valid? ::kushi-class-defined (keyword (subs (kw->s %) 1)))))

(s/def ::no-mods-on-child-styles
  #(let [kw (some-> % name (string/split #":") last (subs 1) keyword)]
     (when-let [class-styles (some-> @state/kushi-atomic-user-classes kw :args)]
       ;; TODO need specs for ::style-tuple-with-mods AND ::kushi-style-kw-with-mods
       (not-any? (fn [v]
                   (let [css-prop (if (vector? v) (first v) (-> v name (string/split #"--") first))]
                     (s/valid? ::with-colon css-prop)))
                 class-styles))))

(s/def ::kushi-dot-class-*-with-mods
  (s/and ::s-or-kw
         ::no-mods-on-child-styles))

(s/def ::kushi-dot-class-string-with-mods
  (s/and string?
         ::with-colon
         ::kushi-dot-class-*-with-mods))

(s/def ::kushi-dot-class-kw-with-mods
  (s/and ::kw-with-colon
         ::kushi-dot-class-*-with-mods))

(s/def ::kushi-dot-tuple
  (s/tuple ::kushi-dot-class-string-with-mods))

(s/def ::kushi-dot-class-with-mods-vector
  (s/and vector?
         #(and (= 1 (count %))
               (s/valid? ::kushi-dot-class-string-with-mods (first %)))))

;; nix?
(s/def ::kushi-class-kw-with-mods
  (s/and ::kw-with-colon
         #(not (s/valid? ::kushi-style-kw-dynamic %))))

;; nix?
(s/def ::kushi-class-kw-with-mods-defined
  (s/and ::kw-with-colon
         #(not (s/valid? ::kushi-style-kw-dynamic %))))

;; nix?
(s/def ::kushi-class-with-mods-defined
  #(get @state/kushi-atomic-user-classes (some-> % name (string/split #":") last)))

;; nix?
(s/def ::scoped-class-syntax
  #(let [as-str (if (keyword? %) (-> % str (subs 1)) %)]
     (some-> as-str (string/split #":") last (string/starts-with? "."))))

(s/def ::coll-of-2
  #(= 2 (count %)))

(s/def ::vector-of-2
  (s/and vector? ::coll-of-2))

(s/def ::list-of-2
  (s/and list? ::coll-of-2))

(s/def ::kushi-style-prop-string
  (s/and string?
         #(find-with % kushi-style-css-prop-re)))

(s/def ::kushi-style-prop-string-leading-space
  (s/and string?
         #(when (re-find #":" %)
            (let [mod (-> % (string/split #":") first)]
              (or
               (find-with mod css-pseudo-class-re)
               (find-with mod css-pseudo-element-re)
               (find-with mod mq-re)
               (string/starts-with? mod " ")
               (string/starts-with? mod "_"))))
         ::kushi-style-prop-string))

(s/def ::kushi-style-prop-kw
  (s/and keyword?
         #(let [s (kw->s %)]
            (s/valid? ::kushi-style-prop-string s))))

(s/def ::style-tuple-prop
  (s/and ::kushi-style-css-prop
         ::with-valid-pseudo-order))

(s/def ::style-tuple-value
  (s/or :symbol? symbol? :list? list? :vector? vector? :string? string? :keyword? keyword? :number? number?))

(s/def ::style-tuple-value-from-kushi-style-kw
  #(re-find (re-pattern kushi-css-val-re) %))

(s/def ::style-tuple-sequence
  (s/cat :style-prop ::style-tuple-prop
         :style-value ::style-tuple-value))

(s/def ::style-tuple-sequence-from-kushi-style-kw
  (s/cat :style-prop ::style-tuple-prop
         :style-value ::style-tuple-value-from-kushi-style-kw))

(s/def ::style-tuple
  (s/and vector? ::style-tuple-sequence))

(s/def ::style-declaration
  (s/or :style-kw ::style-kw
        :combinatorial ::combinatorial
        :style-tuple ::style-tuple))

(s/def ::derefed (s/and (s/coll-of symbol? :count 2) #(= (first %) 'clojure.core/deref)))

;; kushi style-kw related specs ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
(s/def ::kushi-style-kw-dynamic
  (s/and keyword?
         #(when-let [coll (string/split (kw->s %) #"--")]
            (s/valid? ::style-tuple-sequence-from-kushi-style-kw coll))))

(s/def ::kushi-style-kw-dynamic-at-media
  (s/and ::s-or-kw
         #(find-with % kushi-css-prop&val-at-media-re)))

(s/def ::kushi-style-kw-dynamic-with-mods
  (s/and ::s-or-kw
         #(find-with % kushi-css-prop&val-with-mods-re)))

(s/def ::kushi-style-kw-dynamic-with-mods-at-media
  (s/and ::s-or-kw
         #(find-with % kushi-css-prop&val-with-mods-at-media-re)))

(s/def ::with-colon #(re-find #":" (name %)))

(s/def ::kw-with-colon
  (s/and keyword?
         ::with-colon))

(s/def ::kw-with-no-colons
  #(not (s/valid? ::kw-with-colon %)))

(s/def ::style-kw-declarative-with-modifiers
  (s/and keyword?
         #(when (re-find #":" (name %))
            (let [modifiers&prop (clojure.string/split (name %) #":")
                  prop (when (> (count (clojure.string/split (name %) #":")) 1)
                         (some-> modifiers&prop last keyword))]
              (or (get atomic/declarative-classes prop)
                  (get atomic/declarative-classes (-> prop name (subs 1) keyword)))))))

(s/def ::style-kw
  (s/or :dynamic ::kushi-style-kw-dynamic
        :declarative ::style-kw-declarative
        :declarative-with-modifiers ::style-kw-declarative-with-modifiers))


;; defclass related specs  ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(s/def ::defclass-arg
  (s/or :dynamic ::kushi-style-kw-dynamic
        :style-tuple ::style-tuple
        :kushi-dot-class-kw-defined ::kushi-dot-class-kw-defined))

(s/def ::defclass-arg-map-mode
  (s/or :dynamic ::kushi-style-kw-dynamic
        :style-tuple ::style-tuple
        :kushi-dot-class-kw-defined ::kushi-dot-class-kw-defined))

(s/def ::conditional-sexp
  (s/and seq?
         #(contains? #{'if 'when 'cond 'if-let 'when-let 'if-not 'when-not 'case} (first %))))

(s/def ::kushi-conditional-class
  (s/and ::conditional-sexp
         #(some (fn [x]
                  (s/valid? ::kushi-dot-class-kw x))
                %)))

(s/def ::kushi-dot-class-with-mods
  (s/or :kushi-dot-class-kw-with-mods ::kushi-dot-class-kw-with-mods
        :kushi-dot-class-with-mods-vector ::kushi-dot-class-with-mods-vector))

(s/def ::kushi-class-like
  (s/or :kushi-dot-class-kw ::kushi-dot-class-kw
        :kushi-conditional-class ::kushi-conditional-class
        :kushi-dot-class-with-mods ::kushi-dot-class-with-mods))

(s/def ::kushi-arg
  (s/or :kushi-style-kw-dynamic ::kushi-style-kw-dynamic
        :style-tuple ::style-tuple
        :kushi-class-like ::kushi-class-like))

(s/def ::styles-as-map
  (s/and (s/coll-of map?) #(< 0 (count %) 3)))

(s/def ::single-map
  (s/coll-of map? :count 1))

(s/def ::single-attr-map
  (s/and ::single-map ::s+config))

(s/def ::valid-arg-by-type
  (s/or :keyword? keyword?
        :qualified-keyword? qualified-keyword?
        :vector? vector?
        :symbol? symbol?
        :list? list?))

(s/def ::not-kushi-kw
  (s/and #(not (s/valid? ::style-kw %))
         #(not (qualified-keyword? %))))

(s/def ::css-kw-prop
  (s/and ::not-kushi-kw keyword?))

(s/def ::css-kw-prop-value
  (s/and ::not-kushi-kw
         (s/or :vector? vector? :symbol? symbol? :list? list?)))

; Must be a selector that uses the following chars: "\"", "[", "]", "(" or ")".
; This would be an attribute selector or pseudo selector such as nth-child, nth-of-type etc.
(s/def ::css-string-prop
  (s/and string? #_#(re-find #"\"|\(|\[" %)))

(s/def ::css-string-prop-value
  (s/and ::not-kushi-kw
         (s/or :keyword? keyword? :vector? vector? :symbol? symbol? :list? list?)))

;This is a garden-mode vector
(s/def ::garden-mode-stylemap map?)

(s/def ::garden-mode-nested-vec vector?)

(s/def ::garden-mode-mq
  (s/and seq? #(= (first %) 'at-media)))

(s/def ::garden-mode-vec
  (s/and
   vector?
   #(some->> % first (s/valid? ::garden-mode-stylemap))))

;; Map mode
(s/def ::map-mode-only-attr
  #(and (= (count %) 1)
        (map? (first %))
        (= (some-> % first meta :attr) true)))

(s/def ::map-mode-only-style
  #(and (map? (first %))
        (not (map? (second %)))
        (not (s/valid? ::map-mode-only-attr %))))

(s/def ::map-mode-style+attr
  #(and (map? (first %))
        (map? (second %))))

;; user-config specs
 (s/def ::select-ns-vector (s/and vector? #(seq %) (s/coll-of symbol?)))

;; Validates args to macro. Finds fatally bad args.
(s/def ::kushi-args
  (s/cat
   :style-declaration (s/* ::style-declaration)
   :attr-map (s/? map?)))

(s/def ::kushi-args-garden-mode
  (s/cat
   :garden-mode-vec ::garden-mode-vec
   :attr-map (s/? map?)))
