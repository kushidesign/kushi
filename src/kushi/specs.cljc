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
(def kushi-css-val-re ":?[a-z0-9-]+[_a-zA-Z0-9-:|\\.\\*]*")
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

(s/def ::responsive-config
  (s/and map? not-empty))

(s/def ::namespaced-keyword
  (s/and keyword? #(re-find #"/" (str %))))

;; TODO remove or move dependancy on atomic
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

(s/def ::css-selector-base
  (s/and string?
         #(re-find #"^-?[_a-zA-Z]+[_a-zA-Z0-9-]*$" %)))

(s/def ::class-kw
  (s/and keyword?
         #(s/valid? ::css-selector-base (name %))))

(s/def ::kushi-style-css-prop
  (s/and ::s-or-kw
         #(find-with % kushi-style-css-prop-re)))

(s/def ::with-valid-pseudo-order
  #(let [s (kw?->s %)]
     (if (and (re-find #":" s)
              (re-find (re-pattern css-pseudo-element-re-fast) (str ":" s)))
       (not (re-find (re-pattern css-pseudo-element-followed-by-pseudo-class-re) s))
       true)))

(s/def ::defclass-name
  (s/and symbol?
         #(s/valid? ::css-selector-base (name %))))

(s/def ::dot-kw
  (s/and keyword?
         #(-> % name (string/starts-with? "."))))

(s/def ::conditional-tokenized-class
  (s/and ::conditional-sexp
         #(some (fn [x]
                  (s/valid? ::dot-kw x))
                %)))

(s/def ::tokenized-classes
(s/or :dot-kw ::dot-kw
      :conditional ::conditional-tokenized-class))

(s/def ::kushi-class-kw
  (s/and keyword?
         #(not (s/valid? ::kushi-style-kw-dynamic %))
         #(s/valid? ::css-selector-base (kw->s %))))

(s/def ::kushi-dot-class-kw
  (s/and keyword?
         #(-> % name (string/starts-with? "."))
         #(not (re-find #"--|/" (kw->s %)))
         #(s/valid? ::css-selector-base (subs (kw->s %) 1))))

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

(s/def ::kushi-dot-class-with-mods-vector
  (s/and vector?
         #(and (= 1 (count %))
               (s/valid? ::kushi-dot-class-string-with-mods (first %)))))


(s/def ::style-tuple-prop
  (s/or :valid-kushi-style-css-prop (s/and ::kushi-style-css-prop
                                           ::with-valid-pseudo-order)
        :valid-css-custom-property #(->> % name (re-find #"^--.+$"))))

(s/def ::style-tuple-value
  (s/or :symbol? symbol? :list? list? :vector? vector? :string? string? :keyword? keyword? :number? number?))

(s/def ::style-tuple-value-from-kushi-style-kw
  #(re-find (re-pattern kushi-css-val-re) %))

(s/def ::style-tuple-sequence
  (s/cat :style-prop ::style-tuple-prop
         :style-value ::style-tuple-value))

(s/def ::style-tuple-sequence-from-kushi-style-kw
  (s/cat :style-prop    ::style-tuple-prop
         :css-var-colon (s/? #(= % ":"))
         :style-value   ::style-tuple-value-from-kushi-style-kw))

(s/def ::style-tuple
  (s/and vector? ::style-tuple-sequence))

(s/def ::derefed (s/and (s/coll-of symbol? :count 2) #(= (first %) 'clojure.core/deref)))

;; kushi style-kw related specs ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
(s/def ::kushi-style-kw-dynamic
  (s/and keyword?
         #(when-let [coll (string/split (kw->s %) #"--")]
            (s/valid? ::style-tuple-sequence-from-kushi-style-kw coll))))

(s/def ::with-colon #(re-find #":" (name %)))

(s/def ::kw-with-colon
  (s/and keyword?
         ::with-colon))

(s/def ::style-kw-declarative-with-modifiers
  (s/and keyword?
         #(when (re-find #":" (name %))
            (let [modifiers&prop (clojure.string/split (name %) #":")
                  prop (when (> (count (clojure.string/split (name %) #":")) 1)
                         (some-> modifiers&prop last keyword))]
              (or (get atomic/declarative-classes prop)
                  (get atomic/declarative-classes (-> prop name (subs 1) keyword)))))))

(s/def ::kushi-tokenized-custom-property
  (s/and keyword?
         #(->> % name (re-find #"^--.+--.+$"))))

(s/def ::style-kw
  (s/or :dynamic ::kushi-style-kw-dynamic
        :declarative ::style-kw-declarative
        :declarative-with-modifiers ::style-kw-declarative-with-modifiers
        :kushi-tokenized-custom-property ::kushi-tokenized-custom-property))


;; defclass related specs  ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

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


(s/def ::kushi-tokenized-keyword
  (s/or :kushi-style-kw-dynamic ::kushi-style-kw-dynamic
        :kushi-class-like ::kushi-class-like
        :kushi-tokenized-custom-property ::kushi-tokenized-custom-property))

(s/def ::map-mode-style+attr
  #(and (map? (first %))
        (map? (second %))))

;; user-config specs
(s/def ::select-ns-vector (s/and vector? #(seq %) (s/coll-of symbol?)))

;; css-reset-related specs
(s/def ::css-reset-selector (s/or
                             :string? string?
                             :vector? (s/and vector? #(seq %) (s/coll-of string?))))
