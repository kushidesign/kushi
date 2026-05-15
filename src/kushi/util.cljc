(ns kushi.util
  (:require
   [kushi.css.shorthand :as shorthand]
   [fireworks.core :refer [? !? ?> !?>]]
   [fireworks.pp :refer [pprint]]
   [bling.explain :refer [explain-malli]]
   [bling.core :refer [bling]]
   [bling.hifi :refer [hifi]]
   [clojure.string :as str]
   [clojure.walk :as walk])
  #?(:cljs
     (:require-macros [kushi.util :refer [fallback-value]])))

(defn ^:public when->
  "If `(= (pred x) true)`, returns x, otherwise nil.
   Useful in a `clojure.core/some->` threading form."
  [x pred]
  (when (or (true? (pred x))
            (when (set? pred) (contains? pred x)))
    x))

(defn ^:public when->>
  "If (= (pred x) true), returns x, otherwise nil.
   Useful in a `clojure.core/some->>` threading form."
  [pred x]
  (when (or (true? (pred x))
            (when (set? pred) (contains? pred x)))
    x))

;; TODO - convert all to when-> or when->> and delete
(defn ^:public maybe [x pred]
  (when (if (set? pred)
          (contains? pred x)
          (pred x))
    x))

(defn as-str [x]
  (str (if (or (keyword? x) (symbol? x)) (name x) x)))

(defn nameable? [x]
  (or (string? x) (keyword? x) (symbol? x)))

(defn stringify [x]
  (if (nameable? x) (name x) (str x)))

(defn kebab->shorthand [x]
  (->> (-> x
           stringify
           (str/split #"-"))
       (map #(nth % 0 nil))
       str/join))

(defn- normalize-blank-lines
  "Any blank lines with whitespace will be collapsed to an empty string"
  [s]
  (->> s
       str/split-lines
       (mapv #(if (and (str/blank? %) (some-> % count pos?))
                ""
                %))
       (str/join "\n")))

(defn- ml-str-with-adjusted-indentation [s]
  (let [re #"\n( +)"
        s  (normalize-blank-lines s)
        n  (some->> s
                    str
                    (re-seq re)
                    (group-by #(count (second %)))
                    keys
                    (apply min))
        f  (fn [[a]] (str "\n" (subs a (inc n))))
        s  (str/replace s re f)]
    s))

(defn str-ml
  {:doc "Takes a multi-line string and normalizes the indentation.
         Useful for multi-line strings that are nested inside data structures,
         because some editors automatically format these for readability, but
         the resulting strings have unexpected indentations on lines after the
         first."
   :examples '[{:desc "String as map entry value"
                :forms [[(ml-str "Line one
                                  Line two
                                  Line three
                                    - Line four")
                         "Line one\nLine two\nLine three\n  - Line four"]]}]}
  [s]
  (ml-str-with-adjusted-indentation s))

(defn string-ml? [x]
  (boolean (and (string? x) (re-find #"\n" x))))

(defn ml-str->vec [s]
  (-> s
      str-ml
      (str/split #"\n")
      vec))


;; TODO - get this to support ||
;; Check out kushi.css.hydrated/hydrated-css-var
(defn extract-cssvar-token [s]
  (some-> s
          (when-> #(str/starts-with? % "$"))
          (subs 1)))


(defn css-varize [& args] (str "var(--" (apply str args) ")"))

(defn- s->cssvar [s]
  (if-let [token (extract-cssvar-token s)]
    (css-varize token)
    s))

(defn kw->cssvar  [x]
  (if-let [token (some-> x
                         (when-> keyword?)
                         name
                         extract-cssvar-token)]
    (css-varize token)
    (as-str x)))


;; Supports up to 2 fallbacks
(defn kw->cssvar2  [x]
  (if-let [token (some-> x
                         (when-> keyword?)
                         name
                         extract-cssvar-token)]
    (let [[token fallback1 fallback2] (str/split token #"\|\|")]
      (css-varize token
                  (some->> fallback1 s->cssvar (str ", "))
                  (some->> fallback2 s->cssvar (str ", "))))
    (as-str x)))


(let [transforms {:keys keyword
                  :strs str
                  :syms identity}]
  (defmacro ^:public keyed
    "Create a map in which, for each symbol S in vars, (keyword S) is a
       key mapping to the value of S in the current scope. If passed an optional
     :strs or :syms first argument, use strings or symbols as the keys."
    ([vars] `(keyed :keys ~vars))
    ([key-type vars]
     (let [transform (comp (partial list `quote)
                           (transforms key-type))]
       (into {} (map (juxt transform identity) vars))))))




(defn deep-merge [& maps]
  (apply merge-with (fn [& args]
                      (if (every? map? args)
                        (apply deep-merge args)
                        (last args)))
         maps))

(defn kwargs-keys
  "Expects an even-numbered kwarg-style collection of key/values.
   Returns a coll of the keys."
  [kwargs]
  (keep-indexed (fn [idx x] (when (even? idx) x)) kwargs))

(defn ordered-pairs
  "Takes a vector of keys, and a map.
   All values in vector of keys are expected to have a corresponding entry in map.
   Useful when you want to express an ordered-map as a vector of values (similar to kwargs),
   subsequently convert it to a map for further processing, then convert it back to an vector
   of kv tuples with an ordering based on original kwargs vector."
  [ks m]
  (into [] (keep (fn [k] (when-let [v (get m k)] [k v])) ks)))

(defn sort-a-with-b
  "Takes two sequential collections and orders coll a by coll b."
  [a b]
  (sort-by
   (->> (range)
        (interleave b)
        (apply hash-map))
   a))


;; Utility fns below are taken from: http://weavejester.github.io/medley/medley.core.html

(defn remove-nth
  "Returns a lazy sequence of the items in coll, except for the item at the
  supplied index. Runs in O(n) time. Returns a transducer when no collection is
  provided."
  {:added "1.2.0"}
  ([index]
   (fn [rf]
     (let [idx (volatile! (inc index))]
       (fn
         ([] (rf))
         ([result] (rf result))
         ([result x]
          (if (zero? (vswap! idx dec))
            result
            (rf result x)))))))
  ([index coll]
   (lazy-seq
    (if (zero? index)
      (rest coll)
      (when (seq coll)
        (cons (first coll) (remove-nth (dec index) (rest coll))))))))

(defn replace-nth
  "Returns a lazy sequence of the items in coll, with a new item replacing the
  item at the supplied index. Runs in O(n) time. Returns a transducer when no
  collection is provided."
  {:added "1.2.0"}
  ([index item]
   (fn [rf]
     (let [idx (volatile! (inc index))]
       (fn
         ([] (rf))
         ([result] (rf result))
         ([result x]
          (if (zero? (vswap! idx dec))
            (rf result item)
            (rf result x)))))))
  ([index item coll]
   (lazy-seq
    (if (zero? index)
      (cons item (rest coll))
      (when (seq coll)
        (cons (first coll) (replace-nth (dec index) item (rest coll))))))))

(defn- editable? [coll]
  #?(:cljs
     (satisfies? cljs.core/IEditableCollection coll)
     :clj
     (instance? clojure.lang.IEditableCollection coll)))

(defn- reduce-map [f coll]
  (let [coll' (if (record? coll) (into {} coll) coll)]
    (if (editable? coll')
      (persistent! (reduce-kv (f assoc!) (transient (empty coll')) coll'))
      (reduce-kv (f assoc) (empty coll') coll'))))

(defn map-keys
  "Maps a function over the keys of an associative collection."
  [f coll]
  (reduce-map (fn [xf] (fn [m k v] (xf m (f k) v))) coll))

(defn map-vals
  "Maps a function over the values of one or more associative collections.
  The function should accept number-of-colls arguments. Any keys which are not
  shared among all collections are ignored."
  ([f coll]
   (reduce-map (fn [xf] (fn [m k v] (xf m k (f v)))) coll))
  ([f c1 & colls]
   (reduce-map
    (fn [xf]
      (fn [m k v]
        (if (every? #(contains? % k) colls)
          (xf m k (apply f v (map #(get % k) colls)))
          m)))
    c1)))



(defn vec-of-vecs? [v]
  (and (vector? v)
       (every? vector? v)))


(defn more-than-one? [coll]
  (> (count coll) 1))


(defn partition-by-pred [pred coll]
  "Given a coll and a pred, returns a vector of two vectors. The first vector
   contains all the values from coll that satisfy the pred. The second vector
   contains all the values from the coll that do not satisfy the pred."
  (let [ret* (reduce (fn [acc v]
                       (let [k (if (pred v) :valid :invalid)]
                         (assoc acc k (conj (k acc) v))))
                     {:valid [] :invalid []}
                     coll)]
    [(:valid ret*) (:invalid ret*)]))




(defn map-css-tuple-args [coll]
  (map #(let [x (if (vector? %) % [%])]
          (->> x
               (map kw->cssvar2)
               (str/join " ")))
       coll))


(defn double-quote-data-attr-selector-values [v]
  (if (re-find #"=" v)
    (str/replace v
                    #"\[([a-z-\*\|\$\~\^]+)=([^\"\]]+)\]"
                    "[$1=\"$2\"]")
    v))


(defn insert-at [vc i elem]
  (into (conj (subvec vc 0 i) elem)
        (subvec vc i)))





(defn parse-numeric-string [s]
  #?(:clj  (if (str/includes? s ".")
             (Double/parseDouble s)
             (Long/parseLong s))
     :cljs (if (str/includes? s ".")
             (js/parseFloat s)
             (js/parseInt s 10))))

;; Constants & Regexes for css-str->clj
(def css-math-ops-syms #{'+ '- '* '/})
(def css-math-ops-strs ["+" "-" "*" "/"])
(def css-color-fns #{"oklch" "rgb" "rgba" "hsl" "hsla" "lch" "lab" "color"})

(def numeric-re #"^-?\d+(\.\d+)?$")
(def string-literal-re #"^\"(.*)\"$|^'(.*)'$")
(def css-var-re #"(?s)^var\((.*)\)$")
(def css-fn-re #"(?s)^([a-zA-Z-]+)\((.*)\)$")
(def css-fn-check-re #"(?s)^[a-zA-Z-]+\(.*$")
(def surrounding-parens-re #"(?s)^\(.*\)$")

(defn cssval->ks
  "Converts a standard css property value string to kushi syntax.
   For dev, converting code at repl, or tool use with rewrite-clj etc."
  [css-str]
  (letfn [
          ;; Splits a string by a given delimiter, but only at the top level
          ;; (depth 0). Ignores delimiters inside nested parentheses or quotes.
          (split-top-level 
           [s delim-char]
            (loop [chars    (seq s)
                   depth    0
                   in-quote nil
                   current  []
                   result   []]
              (if (empty? chars)
                (let [last-str (str/trim (apply str current))]
                  (if (empty? last-str) result (conj result last-str)))
                (let [c         (first chars)
                      new-quote (cond
                                  (and (nil? in-quote) 
                                       (or (= c \")
                                           (= c \')))
                                  c

                                  (= in-quote c) 
                                  nil

                                  :else 
                                  in-quote)
                      new-depth (if new-quote 
                                  depth
                                  (cond (= c \() (inc depth)
                                        (= c \)) (dec depth)
                                        :else depth))]
                  (if (and (zero? new-depth)
                           (nil? new-quote)
                           (= c delim-char))
                    (recur (rest chars) 
                           new-depth
                           new-quote
                           []
                           (let [trimmed (str/trim (apply str current))]
                             (if (empty? trimmed) 
                               result 
                               (conj result trimmed))))
                    (recur (rest chars) 
                           new-depth
                           new-quote
                           (conj current c)
                           result))))))

          ;; Converts a base string literal into its corresponding clj type:
          ;; math operator symbol, number, explicitly quoted string, or keyword.
          (parse-literal [s]
            (cond
              (some #{(str s)} css-math-ops-strs) (symbol s)
              
              (re-matches numeric-re s)
              (parse-numeric-string s)

              (re-matches string-literal-re s)
              (let [[_ d-q s-q] (re-matches string-literal-re s)]
                (or d-q s-q))

              :else
              (keyword s)))

          ;; Parses a css custom prop (var) into a keyword.
          ;; Chains any fallback values together using the '||' separator.
          (parse-var [s]
            (let [[_ inner-args] (re-matches css-var-re s)
                  parts          (split-top-level inner-args \,)
                  var-name       (let [v (str/trim (first parts))]
                                   (if (str/starts-with? v "--")
                                     (str "$" (subs v 2))
                                     (str "$" v)))
                  fallbacks      (map (fn [fallback-str]
                                        (let [parsed (parse-node fallback-str)]
                                          (cond
                                            (keyword? parsed) (name parsed)
                                            (symbol? parsed) (name parsed)
                                            :else (str parsed))))
                                      (rest parts))]
              (keyword (str/join "||" (cons var-name fallbacks)))))


          ;; Helper to construct a quoted list representing a css function call,
          ;; parsing its arguments recursively based on the specified delimiter.
          (build-fn-call [fn-name args-str delim]
            (apply list 
                   (symbol fn-name)
                   (map parse-node (split-top-level args-str delim))))


          ;; Identifies the specific css function and routes it to the correct
          ;; formatting logic (e.g., calc prefixing vs space-separated vs
          ;;  comma-separated etc).
          (parse-func [s]
            (let [[_ fn-name args-str] (re-matches css-fn-re s)]
              (cond
                (= fn-name "calc")
                (list 'calc (parse-node args-str))

                (css-color-fns fn-name)
                (build-fn-call fn-name args-str \space)

                :else
                (build-fn-call fn-name args-str \,))))


          ;; The core recursive fn. Determines the type of the current string
          ;; fragment and dispatches to the appropriate parsing function.
          (parse-node [s]
            (let [s           (str/trim s)
                  s           (if (re-matches surrounding-parens-re s)
                                (str/trim (subs s 1 (dec (count s))))
                                s)
                  comma-parts (split-top-level s \,)
                  space-parts (split-top-level s \space)]
              
              (cond
                (> (count comma-parts) 1)
                (mapv parse-node comma-parts)

                (> (count space-parts) 1)
                (let [parts (mapv parse-node space-parts)]
                  (if (some css-math-ops-syms parts)
                    (loop [res (nth parts 0)
                           idx 1]
                      (if (< idx (count parts))
                        (recur (list (nth parts idx)
                                     res
                                     (nth parts (inc idx)))
                               (+ idx 2))
                        res))
                    parts))

                (str/starts-with? s "var(")
                (parse-var s)

                (re-matches css-fn-check-re s)
                (parse-func s)

                :else
                (parse-literal s))))]
    
    (parse-node css-str)))


(defn tokenized-css-shorthand-value->double-vector [x]
  (or (some-> x
              (when-> keyword?)
              name
              (when-> #(re-find #":" %))
              (str/split #":")
              (->> (mapv keyword))
              vector)
      x))


(defn css-str-prop-values->structured-syntax [vc]
  (->> vc
       (map-indexed 
        (fn [i v]
          (if (even? i)
            (if (and (string? v)
                     (not (re-find #" " v)))
              (keyword v)
              v)
            ;; values
            (if (vector? v)
              (css-str-prop-values->structured-syntax v)
              (cond
                (= v "\"\"")
                "\"\""

                (string? v)
                (cssval->ks v)

                (keyword? v)
                (tokenized-css-shorthand-value->double-vector v)

                :else
                v)))))
       (apply array-map))) 


(defn- legacy-sx-args->vec [args classes]
  (reduce (fn [acc x]
            (cond (keyword? x)
                  (if (re-find #"--" (name x))
                    (apply conj acc
                           (let [[k v] (str/split (name x) #"--")
                                 k     (or (get-in shorthand/shorthand-syntax [1 k])
                                           (get-in shorthand/shorthand-syntax [2 k])
                                           (get-in shorthand/shorthand-syntax [3 k])
                                           k)
                                 v     (or (get-in shorthand/shorthand-syntax [:enums k v])
                                           v)]

                             [k v]))
                    (swap! classes conj (name x)))
                  (vector? x)
                  (apply conj acc x)
                  (map? x)
                  (apply conj acc (reduce-kv (fn [vc k v] (conj vc k v)) [] x))
                  :else
                  x))
          []
          args))


(defn legacy-sx-call->sx2 
  "This can be used to transform sx calls to newer syntax

   (legacy-sx-call->sx2 
    '(merge-attrs
      (sx \".ks-callout\"
          :position--relative
          :d--flex
          :flex-direction--row
          :jc--c
          :ai--c
          :w--100%
          :gap--$icon-enhanceable-gap
          [:--padding-block-start \"calc (var (--callout-padding-block) * var (--callout-padding-block-start-reduction-ratio, 1))\"]
          [:--padding-block-end   :$callout-padding-block]
          [:--padding-inline      :$callout-padding-inline]
          :pi--$_padding-inline
          :pbs--$_padding-block-start
          :pbe--$_padding-block-end)

      {:aria-busy  loading
       :aria-label (when loading \"loading\")}

      (when stroke-width
        {:style {\"--_stroke-width\" (name stroke-width)}})

      (when-not (false? inert) {:data-ks-inert \"\"})
      (when loading {:data-ks-ui-spinner \"\"})

      &attrs))

      =>
          
      (sx
        {:position              :relative
          :display               :flex
          :flex-direction        :row
          :justify-content       :center
          :align-items           :center
          :width                 :100%
          :gap                   :$icon-enhanceable-gap
          :--padding-block-start '(calc
                                  (* :$callout-padding-block :$callout-padding-block-start-reduction-ratio||1))
          :--padding-block-end   :$callout-padding-block
          :--padding-inline      :$callout-padding-inline
          :padding-inline        :$_padding-inline
          :padding-block-start   :$_padding-block-start
          :padding-block-end     :$_padding-block-end}
        {:aria-busy loading :aria-label '(when loading \"loading\")}
        (when stroke-width {:style {\"--_stroke-width\" '(name stroke-width)}})
        (when-not '(false? inert) {:data-ks-inert \"\"})
        (when loading {:data-ks-ui-spinner \"\"})
        &attrs)"
  [coll]
  (let [merge-attrs? (-> coll first (= 'merge-attrs))

        coll         (if merge-attrs?
                       (rest coll)
                       coll)

        vc           (mapv (fn [x]
                             (cond
                               (some-> x (when-> list?) first (= 'sx))
                               (let [[_ a & args*] x
                                     class         (when (and (string? a)
                                                              (str/starts-with? a "."))
                                                     a)
                                     args          (if class args* (cons a args*))
                                     classes       (atom [])
                                     ;; flat vec of kvs
                                     kvs           (legacy-sx-args->vec args classes)]
                                 (kushi.util/css-str-prop-values->structured-syntax kvs))

                               (map? x)
                               x

                               (list? x)
                               (cons (first x) (rest x))

                               :else
                               x))
                           coll)]
    (cons 'sx (if merge-attrs? vc (rest vc)))))



;; -----------------------------------------------------------------------------
;; css beautification  ---------------------------------------------------------


(declare beautify-css)

(defn printcss 
  ([s]
   (printcss s nil))
  ([s opts]
   (println 
    (beautify-css s
                  (merge
                   (dissoc opts :theme)
                   {:indentation 2
                    :theme       (merge {:selectors      :magenta
                                         :curly-brackets :blue
                                         :round-brackets :yellow
                                         :semi-colons    :gray
                                         :properties     :green
                                         :values         :neutral
                                         :vars           :blue
                                         :commas         :red
                                         :colons         :red
                                         :numbers        :neutral
                                         :cssfn          :purple
                                         :psuedo-colons  :green
                                         }
                                        (:theme opts))})))))



(def ^:private ansi-colors
  "A map of theme keys to their corresponding 256-color ANSI escape codes."
  {:red     "\033[38;5;196m"
   :orange  "\033[38;5;172m"
   :yellow  "\033[38;5;178m"
   :olive   "\033[38;5;106m"
   :green   "\033[38;5;76m"
   :blue    "\033[38;5;75m"
   :purple  "\033[38;5;141m"
   :magenta "\033[38;5;171m"
   :gray    "\033[38;5;247m"
   :black   "\033[38;5;16m"
   :white   "\033[38;5;231m"
   :reset   "\033[0m"})

(defn- colorize
  "Applies an ANSI color code to a string based on a provided theme map.
  Returns the original string if no matching theme or color is found.

  Example:
    (colorize \"{\" :curly-brackets {:curly-brackets :blue})"
  [text theme-key theme]
  (if (or (empty? text) (nil? text))
    text
    (let [color-kw  (get theme theme-key)
          ansi-code (get ansi-colors color-kw)]
      (if ansi-code
        (str ansi-code text (:reset ansi-colors))
        text))))

(defn- make-indent
  "Generates a string of spaces for the given indentation level.

  Example:
    (make-indent 2 2) ;; Returns \"    \""
  [level spaces]
  (apply str (repeat (* level spaces) " ")))

(def ^:private css-token-re
  #"(?i)(?s)/\*.*?\*/|url\([^)]+\)|'(?:\\'|[^'])*'|\"(?:\\\"|[^\"])*\"|[{};()]|[^{};()'\"]+")

(def ^:private selector-token-re 
  #"::|:|,|\(|\)|\s+|[a-zA-Z0-9_.-]+|.")

(def ^:private value-token-re
  #"(?i)'(?:\\'|[^'])*'|\"(?:\\\"|[^\"])*\"|--[a-zA-Z0-9_-]+|[a-zA-Z0-9_-]+\(|#[a-zA-Z0-9]+|(?:\d*\.\d+|\d+)(?:[a-zA-Z]+|%)?|\(|\)|,|\s+|[a-zA-Z_-]+|.")

{
 :a         1
 :basdfsadf "afasdfasfds"
 :cadfsaf   3}

(defn- block-max-prop-length
  "Looks ahead in the token stream to find the maximum property length
  within the current CSS block, ignoring nested blocks. Used for
  left-justifying values.

  Example:
    (block-max-prop-length '(\"width\" \":\" \" 10px\" \";\" \"}\"))"
  [toks]
  (loop [ts toks
         chunk []
         max-len 0
         depth 0]
    (if (empty? ts)
      max-len
      (let [t (first ts)]
        (cond
          ;; Skip over comments without affecting block depth or length
          (str/starts-with? t "/*")
          (recur (rest ts) chunk max-len depth)

          ;; Increment depth when entering a nested scope (e.g., media queries)
          (= t "{")
          (recur (rest ts) [] max-len (inc depth))

          ;; Decrement depth, or calculate final property length if exiting
          (= t "}")
          (if (zero? depth)
            (let [full-str  (str/trim (str/join "" chunk))
                  colon-idx (str/index-of full-str ":")]
              (if colon-idx
                (max max-len (count (str/trim (subs full-str 0 colon-idx))))
                max-len))
            (recur (rest ts) [] max-len (dec depth)))

          ;; Check property length at the end of a standard declaration
          (= t ";")
          (if (zero? depth)
            (let [full-str  (str/trim (str/join "" chunk))
                  colon-idx (str/index-of full-str ":")
                  prop-str  (if colon-idx 
                              (subs full-str 0 colon-idx) 
                              "")
                  prop-len  (if colon-idx 
                              (count (str/trim prop-str)) 
                              0)]
              (recur (rest ts) [] (max max-len prop-len) depth))
            (recur (rest ts) [] max-len depth))

          ;; Accumulate standard tokens into the current chunk
          :else
          (recur (rest ts) (conj chunk t) max-len depth))))))

(defn- format-selector
  "Tokenizes and applies syntax highlighting to a CSS selector string.

  Example:
    (format-selector \".btn:hover\" {:selectors :blue :pseudo-colons :red})"
  [sel-str theme]
  (let [tokens (re-seq selector-token-re sel-str)]
    (->> tokens
         (map (fn [t]
                (cond
                  ;; Colorize pseudo-classes and pseudo-elements
                  (or (= t ":") (= t "::"))
                  (colorize t :pseudo-colons theme)

                  ;; Colorize commas separating multiple selectors
                  (= t ",")
                  (colorize t :commas theme)

                  ;; Colorize round brackets in structural pseudo-classes
                  (or (= t "(") (= t ")"))
                  (colorize t :round-brackets theme)

                  ;; Preserve whitespace exactly as authored
                  (re-matches #"\s+" t)
                  t

                  ;; Treat all other text as standard selector text
                  :else
                  (colorize t :selectors theme))))
         (str/join ""))))

(defn- format-value-aligned
  "Tokenizes a CSS value string and applies syntax highlighting.
  Also manages parenthesis depth to correctly align multi-layered values
  on newlines without breaking internal function arguments.

  Example:
    (format-value-aligned \"calc(2px + 3px)\" my-theme 12)"
  [val-str theme align-spaces]
  (let [tokens    (re-seq value-token-re val-str)
        align-str (apply str (repeat align-spaces " "))]
    (loop [toks        tokens
           depth       0
           skip-space? true
           out         []]
      (if (empty? toks)
        (str/join "" out)
        (let [t         (first toks)
              next-toks (rest toks)]
          (cond
            ;; Handle spaces: drop them if they follow an alignment newline
            (re-matches #"\s+" t)
            (if skip-space?
              (recur next-toks depth skip-space? out)
              (recur next-toks depth false (conj out t)))

            ;; Catch CSS functions (e.g., calc(, min(, linear-gradient()
            (str/ends-with? t "(")
            (let [fn-name (subs t 0 (dec (count t)))]
              (recur next-toks
                     (inc depth)
                     false
                     (conj out
                           (colorize fn-name :cssfn theme)
                           (colorize "(" :round-brackets theme))))

            ;; Catch standalone opening brackets
            (= t "(")
            (recur next-toks
                   (inc depth)
                   false
                   (conj out (colorize t :round-brackets theme)))

            ;; Catch standalone closing brackets and reduce depth
            (= t ")")
            (recur next-toks
                   (max 0 (dec depth))
                   false
                   (conj out (colorize t :round-brackets theme)))

            ;; Top-level commas trigger a newline; nested commas do not
            (= t ",")
            (let [colored-comma (colorize t :commas theme)]
              (if (zero? depth)
                (recur next-toks
                       depth
                       true
                       (conj out colored-comma "\n" align-str))
                (recur next-toks
                       depth
                       false
                       (conj out colored-comma))))

            ;; Colorize the argument side of CSS variables
            (str/starts-with? t "--")
            (recur next-toks
                   depth
                   false
                   (conj out (colorize t :vars theme)))

            ;; Colorize numerical values including percentages and units
            (re-matches #"(?i)^(?:\d*\.\d+|\d+)(?:[a-zA-Z]+|%)?$" t)
            (recur next-toks
                   depth
                   false
                   (conj out (colorize t :numbers theme)))

            ;; Default to standard value styling for text and hex codes
            :else
            (recur next-toks
                   depth
                   false
                   (conj out (colorize t :values theme)))))))))

(defn- process-declaration
  "Formats a single CSS property-value declaration. Applies padding
  to the property side so that all values in the block align vertically.

  Example:
    (process-declaration [\"width\" \":\" \" 10px\"] 1 2 theme 15)"
  [chunk indent indent-spaces theme prop-align]
  (let [full-str  (str/trim (str/join "" chunk))
        colon-idx (str/index-of full-str ":")]
    (if (and colon-idx (not (empty? full-str)))
      (let [prop               (str/trim (subs full-str 0 colon-idx))
            val-part           (subs full-str (inc colon-idx))
            indent-str         (make-indent indent indent-spaces)
            effective-prop-len (max prop-align (count prop))
            pad-len            (- effective-prop-len (count prop))
            padding            (apply str (repeat pad-len " "))
            align-spaces       (+ (* indent indent-spaces)
                                  effective-prop-len
                                  2)]
        (str indent-str
             (colorize prop :properties theme)
             (colorize ":" :colons theme)
             padding
             " "
             (format-value-aligned val-part theme align-spaces)))
      
      ;; Handle blocks missing a colon (e.g., nested selectors or media queries)
      (when-not (empty? full-str)
        (str (make-indent indent indent-spaces)
             (format-selector full-str theme))))))

(defn beautify-css
  "Formats and syntax highlights a valid CSS string for Clojure/Babashka.
  Maintains exact alignment for layered values and left-justifies properties.

  Options map supports:
    :indentation   - Number of spaces for each level (default: 2)
    :align-values? - Left-justify values across the block (default: true)
    :theme         - Map of syntax elements to color keywords
                     (e.g., :properties :blue, :numbers :orange)

  Example:
    (beautify-css \".btn { color: red; }\" {:indentation 2})"
  [css-string & [opts]]
  (let [indent-spaces (:indentation opts 2)
        theme         (:theme opts {})
        align-vals?   (:align-values? opts true)
        tokens        (re-seq css-token-re css-string)]
    (loop [toks        tokens
           indent      0
           chunk       []
           out         []
           align-stack '(0)]
      (if (empty? toks)
        (str (str/trim (str/join "" out)) "\n")
        (let [t         (first toks)
              next-toks (rest toks)]
          (cond
            ;; Format floating comments and maintain indentation levels
            (str/starts-with? t "/*")
            (recur next-toks
                   indent
                   []
                   (conj out
                         (make-indent indent indent-spaces)
                         (colorize t :comments theme)
                         "\n")
                   align-stack)

            ;; Enter a new block: format the selector and scan for max prop len
            (= t "{")
            (let [selector   (str/trim (str/join "" chunk))
                  next-align (if align-vals?
                               (block-max-prop-length next-toks)
                               0)]
              (recur next-toks
                     (inc indent)
                     []
                     (conj out
                           (format-selector selector theme)
                           " "
                           (colorize "{" :curly-brackets theme)
                           "\n")
                     (conj align-stack next-align)))

            ;; Exit a block: process remaining declarations and decrease indent
            (= t "}")
            (let [decl       (process-declaration chunk
                                                  indent
                                                  indent-spaces
                                                  theme
                                                  (first align-stack))
                  new-indent (max 0 (dec indent))
                  new-stack  (if (next align-stack)
                               (pop align-stack)
                               '(0))]
              (recur next-toks
                     new-indent
                     []
                     (conj out
                           (if decl (str decl "\n") "")
                           (make-indent new-indent indent-spaces)
                           (colorize "}" :curly-brackets theme)
                           "\n\n")
                     new-stack))

            ;; End of declaration: process property and value string
            (= t ";")
            (let [decl (process-declaration chunk
                                            indent
                                            indent-spaces
                                            theme
                                            (first align-stack))]
              (recur next-toks
                     indent
                     []
                     (conj out
                           (if decl
                             (str decl (colorize ";" :semi-colons theme) "\n")
                             ""))
                     align-stack))

            ;; Accumulate text into the chunk until a structural token is hit
            :else
            (recur next-toks
                   indent
                   (conj chunk t)
                   out
                   align-stack)))))))
