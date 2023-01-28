(ns kushi.styles
  (:require
  ;; TODO figure out which of the color fns to pass thru to public api
  ;;  [garden.color]
   [clojure.spec.alpha :as s]
   [clojure.walk :as walk]
   [clojure.string :as string]
   [kushi.color :as color]
   [kushi.selector :as selector]
   [kushi.state2 :as state2]
   [kushi.utils :as util :refer [keyed]]
   [kushi.specs2 :as specs2]))

(defn- fnsym->string [sexp s]
  (concat [(str s (-> sexp first str))] (rest sexp)))

(defn- fname-from-list [x]
  (when (seq? x)
    (when (seq x)
      (some-> x
              first
              (string/split #"__conditional-sexp__")
              second))))

(defn- unescape-sexp [x]
  (if-let [fn-name (fname-from-list x)]
    (util/replace-nth 0 (symbol fn-name) x)
    x))

(defn- escape-list [x]
  (cond
    (s/valid? ::specs2/cssfn-list x)
    (fnsym->string x "__cssfn__")
    :else x))

(defn- cssfn-name-from-escaped [x]
  (when (seq? x)
    (when (seq x)
      (some-> x
              first
              (string/split #"__cssfn__")
              second))))

(defn color-token->hsla [s alpha]
  (if-let [{:keys [h s l]
            :as   hsl-map}  (some-> s
                                    util/extract-cssvar-name
                                    (subs 2)
                                    keyword
                                    color/base-color-map-data)]
    (let [alpha (if (and (number? alpha)
                         (<= 0 alpha 1))
                  alpha
                  1)
          css-string-values [(str h "deg")
                             (str s "%")
                             (str l "%")
                             (str alpha)]]
      {:data              hsl-map
       :css-string        (util/cssfn-string "hsla" css-string-values)
       :css-string-values css-string-values})
    s))

(defn- cssfn-list->string [x]
  (if-let [nm (cssfn-name-from-escaped x)]
    (do
      (let [trans? (= nm "kushi/transparentize")
            nm     (if trans? "hsla" nm)
            hsla   (when trans?
                     (:css-string-values (color-token->hsla (second x) (last x))))
            args   (or hsla (rest x))]
        #_(when (state2/trace-mode?)
          (? (garden.color/as-hsla )))
        (util/cssfn-string nm args)))
    x))

(defn- normalize-css-custom-propery
  "hi"
  [x]
  (if (s/valid? ::specs2/cssvar-name x)
    (str "var(" (util/cssvar-dollar-syntax->double-dash x) ")")
    x))

(defn css-var-string [s x css-vars]
  (let [css-var-string  (str "var(--" s ")")]
    (swap! css-vars assoc css-var-string x)
    css-var-string))

(defn- sexp->css-var [p css-vars x]
  (if (or (s/valid? ::specs2/conditional-sexp x)
          (and (list? x)
               (= (first x) 'str)))
    (let [s (hash (str p x))]
      (css-var-string s x css-vars))
    x))

(defn cssvarized [coll css-vars f]
  (doall
   (map (fn [[p v]]
          (let [v (if (coll? v)
                    (walk/postwalk #(f p css-vars %) v)
                    (f p css-vars v))]
            [p v]))
        coll)))

(defn- sexp-cssvarized [coll css-vars]
  (cssvarized coll css-vars sexp->css-var))

(defn- binding->css-var [p css-vars x]
  (if (symbol? x)
    (css-var-string (name x) x css-vars)
    x))

(defn- bindings-cssvarized [coll css-vars]
  (cssvarized coll css-vars binding->css-var))

(defn postwalk-with-f [f v]
  (if (coll? v)
    (walk/postwalk #(f %) v)
    (f v)) )

(defn- postwalk-style-tuples
  [tups f]
  (map (fn [[p v]]
         (let [v (postwalk-with-f f v)]
           [p v]))
       tups))

(defn- postwalk-list
  [coll f]
  (map (fn [x]
         (let [v (postwalk-with-f f x)]
           v))
       coll))

(defn- args-by-conformance
  "Expects a coll that will conform to conformance spec.
   If it does not conform, there is a problem with the validation spec."
  [coll
   conformance-spec]
  (when (keyword? (s/conform conformance-spec coll))
    (println
     (str "[!Warning] kushi.styles/args-by-conformance\n"
          "There is a problem with either the validation spec or the conformance spec.\n"
          "This is a bug internal to Kushi, please report.")))
  (let [conformed (s/conform conformance-spec coll)
        grouped** (group-by #(first %) conformed)
        grouped*  (map (fn [[k v]] [k (map second v)]) grouped**)
        grouped   (map (fn [[k v]]
                         (if (contains? #{:assigned-class :attrs} k)
                           [k (first v)]
                           [k v]))
                       grouped*)
        by-kind   (into {} grouped)]
    by-kind))

(defn normalize-classnames [%]
  (cond
    (s/valid? ::specs2/dot-kw %)
    (specs2/dot-kw->s %)
    (keyword? %)
    (name %)
    :else %))

(defn classlist
  [attrs class selector]
  (let [selector  (:selector* selector)
        cls       (some-> attrs :class)
        cls       (when cls (if (s/valid? ::specs2/s|kw cls) [cls] cls))
        classlist (concat class cls [selector])
        ret       (postwalk-list classlist normalize-classnames)]
    (into [] ret)))

(defn dequote* [x]
  (if (and (list? x)
           (= 'quote (first x))
           (some->> x
                    second
                    first
                    (re-find #"^__cssfn__")))
    (second x)
    x))

(defn dequote [coll]
  (walk/postwalk dequote* coll))

(defn css-custom-property-keys-normalized [coll]
  (map (fn [[k v]]
         (let [k (if (s/valid? ::specs2/cssvar-name k)
                   (util/cssvar-dollar-syntax->double-dash k)
                   k)]
           [k v]))
       coll))

(defn all-style-tuples
  [coll]
  (let [css-vars (atom {})
        tuples   (-> coll
                     (postwalk-style-tuples normalize-css-custom-propery)
                     (sexp-cssvarized css-vars)
                     (postwalk-style-tuples escape-list)
                     dequote
                     (bindings-cssvarized css-vars)
                     (postwalk-style-tuples cssfn-list->string)
                     (postwalk-style-tuples unescape-sexp)
                     (postwalk-style-tuples specs2/kw?->s)

                     ;; Normalize any tuple keys that are css custom properties
                     ;;  [:$xxx "red"] => ["--xxx" "red"]
                     css-custom-property-keys-normalized)]
    [tuples @css-vars]))


(defn cssvars
  [css-vars cssvar-tuples2]
  (let [with-extracted-names (some->> css-vars
                                      (util/map-keys util/extract-cssvar-name))
        cssvar-tuples        (some->> cssvar-tuples2
                                      (into {})
                                      (util/map-keys name)
                                      (util/map-vals util/hydrate-css-shorthand+alternations))
        ret*                 (merge with-extracted-names cssvar-tuples)

        ;; Normalized kushi-style shorthand syntax within css-var sexp
        ret                  (util/map-vals (fn [v]
                                              (if (s/valid? ::specs2/conditional-sexp v)
                                                (map (fn [x]
                                                       (if (or (s/valid? ::specs2/tokenized-css-shorthand x)
                                                               (s/valid? ::specs2/tokenized-css-alternation x))
                                                         (-> x name util/hydrate-css-shorthand+alternations)
                                                         x))
                                                     v)
                                                v))
                                            ret*)]
    ret))

(defn style-tuples*
  [{:keys [args
           conformance-spec
           bad-args-vals
           bad-stylemap
           :kushi/process]
    :as   m*}]

  (let [shared-class?
        (util/shared-class? process)

        clean*
        (filter #(not (contains? bad-args-vals %)) args)

        attrs*
        (when (map? (last args)) (last args))

        clean-stylemap*
        (if-let [problem-keys (keys bad-stylemap)]
          (when attrs*
            (apply dissoc (cons (:style attrs*) problem-keys)))
          (some-> attrs* :style))

        clean
        (if clean-stylemap*
          (util/replace-last (assoc attrs* :style clean-stylemap*)
                             clean*)
          clean*)

        {:keys                 [assigned-class
                                tokenized-style
                                class]
         style-tuples          :style-tuple
         style-tuples-defclass :style-tuple-defclass
         defclass-classes      :defclass-class
         tokenized-cssvars     :cssvar-tokenized
         cssvar-tuples         :cssvar-tuple
         :as                   by-kind}
        (args-by-conformance clean conformance-spec)

        clean-stylemap
        (or clean-stylemap*
            (when shared-class?
              (some-> by-kind :defclass-stylemap first)))


        assigned-class
        (when assigned-class
          (if (s/valid? ::specs2/quoted-symbol assigned-class)
            (-> assigned-class second)
            assigned-class))

        selector
        (selector/selector-name (assoc m* :assigned-class assigned-class))

        style-tuples-from-tokenized
        (when-let [coll tokenized-style]
          (when (seq coll)
            (map #(let [s (name %)]
                    ;; TODO abstract this
                    (if (re-find #"^.*[^-]--:--[^-]+.+$" s)
                      (string/split (name %) #"--:")
                      (string/split (name %) #"--" 2)))
                 coll)))


        cssvar-tuples-from-tokenized
        (when-let [coll tokenized-cssvars]
          (when (seq coll)
            (map (fn [%]
                   (let [[nm val] (string/split (name %) #"--")]
                     [(util/cssvar-dollar-syntax->double-dash nm) val]))
                 coll)))

        style-tuples-from-defclass-class
        (when defclass-classes
          (mapcat #(let [k (some-> % specs2/dot-kw->s symbol)]
                     (get-in @state2/shared-classes [k]))
                  defclass-classes)
          #_(println " "))

        all-style-tuples*
        (distinct
         (concat cssvar-tuples-from-tokenized
                 cssvar-tuples
                 style-tuples-from-tokenized
                 style-tuples
                 style-tuples-defclass
                 style-tuples-from-defclass-class
                 clean-stylemap))


        [all-style-tuples css-vars]
        (all-style-tuples all-style-tuples*)

        [cssvar-tuples2 all-style-tuples2]
        (util/partition-by-pred #(->> %
                                      first
                                      (s/valid? ::specs2/css-var-name))
                                all-style-tuples)


        defclass-style-tuples
        (when shared-class? all-style-tuples2)

        selector
        (cond
          assigned-class
          selector
          (seq all-style-tuples2)
          selector)

        attrs-no-style
        (when attrs* (dissoc attrs* :style))

        classlist
        (classlist attrs-no-style class selector)

        css-vars
        (cssvars css-vars cssvar-tuples2)


        attrs
        (merge (-> (or attrs-no-style {}) (assoc :class classlist))
               (when css-vars {:style css-vars}))]


  ;; just for debugging
    #_(when (state2/trace?)
      (keyed
        ;; process
        ;; shared-class?
        ;; clean*
        ;; attrs*
                  ;; attrs
        ;; by-kind
        ;; clean-stylemap*
        ;; clean-stylemap
        ;; style-tuples-from-tokenized
        ;; all-style-tuples
       css-vars
       ))

    (merge (when defclass-style-tuples
             {:defclass-style-tuples defclass-style-tuples})
           {:all-style-tuples all-style-tuples2}
           (keyed css-vars attrs classlist selector))))
