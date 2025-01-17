(ns kushi.css.specs
  (:require 
   [clojure.string :as string]
   [clojure.spec.alpha :as s]))

;; ----------------------------------------------------------------------------
;; # Helper fns 
;; ----------------------------------------------------------------------------

(defn as-str [x]
  (str (if (or (keyword? x)
               (symbol? x))
         (name x)
         x)))

(defn dot-kw->s [x] (subs (str x) 2))

;; -----------------------------------------------------------------------------
;; # Regular Expressions 
;; -----------------------------------------------------------------------------

;; ## Utility regexps ----------------------------------------------------------

(defn re-pattern-be
  "re-pattern bookend"
  [s]
  (re-pattern (str "^" s "$")))

(defn alts-zero-or-more  [s]
  (str "[" s "]*"))

(defn alts-one-or-more [s]
  (str "[" s "]+"))

(defn alts-exactly-one [s]
  (str "[" s "]"))

(def css-prop-stack-first-char-allowables
  " 0-9a-z_\\-\\+\\>\\~\\*\\.\\#\\$\\@")

(def css-selector-first-char-allowables
  "\\.\\[\\*\\#a-z\\:")


(def css-prop-stack-allowables
  (str ":" css-prop-stack-first-char-allowables))

(def css-value-shared-re-base "0-9a-zA-Z#%_:\\+\\/\\*\\ \\|\\(\\)\\.\\-\\$")



;; ## Tokenized string regexps ------------------------------------------------

;; TODO - should stuff like this be illegal? 
;; If so, where to catch it?
;; :_:last-child  (should be :_*:last-child)
;; :*:last-child  (should be :_*:last-child)
;; :>:last-child  (should be :>*:last-child)

;; TODO - stress-test appending the & selector
(def css-prop-stack-re-base
  (alts-zero-or-more (str css-prop-stack-allowables
                          "\\(\\[\\'\"\\=\\~\\^\\]\\)"
                          " \\&")))

(def css-prop-stack-re
  (re-pattern-be css-prop-stack-re-base))

(def css-value-re-base
  (alts-one-or-more  (str " "
                          css-value-shared-re-base
                          "\\\"\\`")))

(def css-value-re
  (re-pattern-be css-value-re-base))

(def tok-str-re
  (re-pattern-be (str 
                  (alts-exactly-one css-prop-stack-first-char-allowables)
                  css-prop-stack-re-base
                  "--"
                  css-value-re-base
                  "(?:!important)?")))


;; ## Tokenized keywords regexps ----------------------------------------------

(def tok-kw-css-prop-stack-re-base
  (alts-zero-or-more  css-prop-stack-allowables))

(def tok-kw-css-prop-stack-re
  (re-pattern-be tok-kw-css-prop-stack-re-base))

(def tok-kw-css-value-re-base
  (alts-one-or-more  css-value-shared-re-base))

(def tok-kw-css-value-re
  (re-pattern-be tok-kw-css-value-re-base))

(def tok-kw-re
  (re-pattern-be (str 
                  (alts-exactly-one css-prop-stack-first-char-allowables)
                  tok-kw-css-prop-stack-re-base
                  "--"
                  tok-kw-css-value-re-base
                  "(?:!important)?")))



;; ## Helper regexps ----------------------------------------------------------

(def css-selector-re-base
  (str (alts-one-or-more css-selector-first-char-allowables)
       ".*"))

(def css-selector-re
  (re-pattern-be css-selector-re-base))

(def classname-with-dot-re
  ;; ".foo"     -> valid
  ;; ".foo-3"   -> valid
  ;; ".foo--3"  -> valid
  ;; ".foo--3"  -> valid
  ;; "."        -> invalid
  ;; ".3foo"    -> invalid
  ;; ".foo:c-r" -> invalid
  #"^\.\D[^\:\s]*$")

(def var-re-base
  "([a-zA-Z_-]+[a-zA-Z0-9_-\\|]*)")

(def runtime-var-re
  (re-pattern (str "`" var-re-base "`")))

(def css-custom-property-re
  (re-pattern (str "`\\$" var-re-base "`")))

(def functional-pseudo-re
  #"^([a-z-]+)\([^\)]+\)$")

(def has-ancestor-re
  #"^has-ancestor\(([^\)]+)\)")

;; ----------------------------------------------------------------------------
;; # Specs 
;; ----------------------------------------------------------------------------


;; ## Specs for shared use -----------------------------------------------------

(s/def ::css-file-path 
  (s/and string? #(re-find #"\.css$" %)))

(s/def ::nameable 
  #(or (keyword? %) (string? %) (symbol? %)))

(s/def ::s|kw
  #(or (keyword? %) (string? %)))

(s/def ::s|kw|num
  #(or (keyword? %) (string? %) (number? %)))


;; ## Specs for css-selectors --------------------------------------------------

(s/def ::css-selector
  (s/and 
   string?
   #(re-find css-selector-re %)))


;; ## Specs for at-rule-selector -----------------------------------------------

(s/def ::at-selector
  (s/and 
   string?
   #(re-find #"^@.+$" %)))

(s/def ::keyframe-selector
  (s/and 
   string?
   #(re-find #"^@keyframes [a-z]+.*$" %)))

(s/def ::layer-selector
  (s/and 
   string?
   #(re-find #"^@layer [a-z_-]+.*$" %)))

;; ## Specs for css-values -----------------------------------------------------

(s/def ::css-value ::s|kw|num)



;; ## Specs for css-props ------------------------------------------------------

;; TODO Do we need another one for just :css-prop ?

(s/def ::css-prop-stack
  (s/and ::s|kw
         #(re-find css-prop-stack-re (name %))))

(s/def ::at-rule
  (s/and ::s|kw
         #(string/starts-with? (name %) "@")))


;; ## Specs for classes --------------------------------------------------------
(s/def ::class-kw
  (s/and keyword?
         #(re-find classname-with-dot-re (name %))))

(s/def ::supplied-classname
  (s/and string?
         #(re-find classname-with-dot-re (name %))))


;; ## Specs for keyframes ------------------------------------------------------
(s/def ::keyframe-ident
  #{:from :to "from" "to"})

(s/def ::keyframe-percentage
  (s/and ::s|kw
         #(re-find #"^100%$|^[0-9][0-9]?(?:\.[0-9]+)?%$" (name %))))

(s/def ::keyframe-name 
  (s/or :keyframe-ident ::keyframe-ident
        :keyframe-percentage ::keyframe-percentage))

(s/def ::keyframe
  (s/tuple ::keyframe-name ::style-map))



;; ## Specs for tokenized keywords ---------------------------------------------

(s/def ::tok-kw
  (s/and keyword? #(re-find tok-kw-re (name %))))

;; Spec for the 'prop-side' (left in en) of a tokenized keyword e.g.:
;; :sm:dark:hover:b--1px:solid:red
;; ^^^^^^^^^^^^^^^^
(s/def ::tok-kw-css-prop-stack
  (s/and keyword? #(re-find tok-kw-css-prop-stack-re (name %))))

;; Spec for the 'value-side' (right in en) of a tokenized keyword e.g.:
;; :sm:dark:hover:b--1px:solid:red
;;                   ^^^^^^^^^^^^^
(s/def ::tok-kw-css-value
  (s/and keyword? #(re-find tok-kw-css-value-re (name %))))



;; ## Specs for tokenized strings ----------------------------------------------

(s/def ::tok-str
  (s/and string? #(re-find tok-str-re %)))



;; ## Specs for style-vecs -----------------------------------------------------

(s/def ::semi-hydrated-style-vec
  (s/tuple ::css-prop-stack ::css-value))

(s/def ::style-vec
  ;; TODO - is :css-value redundant here?
  (s/tuple ::css-prop-stack #(or (s/valid? ::css-value %)
                                 (s/valid? ::style-map-value %))))


;; ## Specs for style-maps -----------------------------------------------------

(s/def ::style-map-value
  #(or (s/valid? ::css-value %)
       (s/valid? ::style-map %)))

(s/def ::style-map
  (s/map-of ::css-prop-stack ::style-map-value))


;; ## Specs for css-rule-call --------------------------------------------------

(s/def ::css-rule-call
  (s/and list?
         #(= 'css-rule (nth % 0 nil))))


;; ## Specs for args to `sx` `css` and `defcss` macros -------------------------

(s/def ::tokenized
  #(or (s/valid? ::tok-str %)
       (s/valid? ::tok-kw %)))

(s/def ::valid-sx-arg
  (s/or 
   :supplied-classname ::supplied-classname
   :class-kw           ::class-kw
   :tokenized          ::tokenized
   :style-vec          ::style-vec
   :style-map          ::style-map
   :css-rule-call      ::css-rule-call
   :class-binding      symbol?  ;; <- intended for dynamic classnames (maybe remove?)

   ;; ! removed :logic-sexp
   ;; :logic-sexp    ::logic-sexp

   ;; ! removed vectorized props
   ;; :top-level-vec ::top-level-vec
   ))

(s/def ::sx-args
  (s/coll-of ::valid-sx-arg))

(s/def ::quoted-symbol
  (s/and (s/coll-of symbol? :kind seq? :count 2)
         #(= 'quote (first %))))
