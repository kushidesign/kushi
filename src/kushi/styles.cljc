(ns kushi.styles
  (:require
   ;; TODO figure out which of the color fns to pass thru to public api
   [par.core :refer [!? ?]]
   [garden.color]
   [clojure.spec.alpha :as s]
   [clojure.walk :as walk]
   [clojure.string :as string]
   [kushi.color :as color]
   [kushi.selector :as selector]
   [kushi.state2 :as state2]
   [kushi.utils :as util :refer [keyed]]
   [kushi.specs2 :as specs2]))

(defn- fnsym->string
  "Escapes cssfn symbols.
   Also stringifies css-calc math operators in the case of calc."
  [sexp s]
  (let [cssfn (-> sexp first str)]
    (concat [(str s cssfn)]
            (if (= cssfn "calc")
              (map #(if (s/valid? ::specs2/css-calc-op %)
                      (name %)
                      %)
                   (rest sexp))
              (rest sexp)))))

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


;;;; Custom kushi css functions start
(defn color-token->hsla [s alpha]
  (when (string? s)
    (when-let [{:keys [h s l]
                :as   hsl-map}  (some-> s
                                        util/extract-cssvar-name
                                        (subs 2)
                                        keyword
                                        color/base-color-map-data)]
      (let [alpha             (if (and (number? alpha)
                                       (<= 0 alpha 1))
                                alpha
                                1)
            css-string-values [(str h "deg")
                               (str s "%")
                               (str l "%")
                               (str alpha)]]
        {:data              hsl-map
         :css-string        (util/cssfn-string "hsla" css-string-values)
         :css-string-values css-string-values}))))


(defn transparentize [nm n]
  (or (some-> nm (color-token->hsla n) :css-string)
      (when-let [color (when (or (string? nm)
                                 (keyword? nm))
                         (contains? garden.color/color-name->hex
                                    (keyword nm)))]
        (garden.color/transparentize color n))
      nm))


(defn grid-template-areas
  "Use like this:
   (kushi/grid-template-areas
    \"brc br b  bl blc\"
    \"rt  .  .  .  lt\"
    \"r   .  .  .  l\"
    \"rb  .  .  .  lb\"
    \"trc tr t  tl tlc\")"
  [rows]
  (string/join " " (map #(str "\"" % "\"") rows)))


(defn- cssfn-list->string [x]
  (if-let [nm (cssfn-name-from-escaped x)]
    (let [css-string (case nm
                       "kushi/transparentize"
                       (transparentize (second x) (last x))
                       "kushi/grid-template-areas"
                       (grid-template-areas (rest x))
                       nil)]
      (or css-string
          (util/cssfn-string nm (rest x))))
    x))
;;;; Custom kushi css functions end


(defn css-var-syntax [& args]
  (str "var(" (string/join ", " (remove nil? args)) ")"))


(defn- normalize-css-custom-property
  "Works with keyword or string.
   Supports up to 2 fallback values.
   Examples:
   :$mycssvarname => \"var(--mycssvarname)\"
   :$mycssvarname|$myfallback => \"var(--mycssvarname, var(--myfallback))\"
   :$mycssvarname!important => \"var(--mycssvarname)!important\""
  [x]
  (let [f      util/cssvar-dollar-syntax->double-dash
        valid? (partial (fn [spec x] (s/valid? spec x)) ::specs2/cssvar-name)
        ret    (if (valid? x)
                 (if (re-find #"\|" (name x))
                   (let [[a b c] (string/split (name x) #"\|")
                         c       (when c (if (valid? c)
                                           (css-var-syntax (f c))
                                           c))
                         b       (when b (if (valid? b)
                                           (css-var-syntax (f b) c)
                                           b))
                         ret     (css-var-syntax (f a) b)]
                     ret)
                   (str "var(" (f x) ")"))
                 x)]
    ret))


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
  (!? :css-vars:before @css-vars)
  (let [ret (cssvarized coll css-vars binding->css-var)]
    (!? :css-vars:after @css-vars)
    ret))

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

(defn args-by-conformance
  "Expects a coll that will conform to conformance spec.
   If it does not conform, there is a problem with the validation spec."
  [coll conformance-spec]
  (when (keyword? (s/conform conformance-spec coll))
    (println
     (str "[!Warning] kushi.styles/args-by-conformance\n"
          "There is a problem with either the validation spec or the conformance spec.\n"
          "This is a bug internal to Kushi, feel free to file an issue if you are feeling generous.")))

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
  [attrs
   class
   selector]
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

(defn css-custom-property-values-!important-normalized [coll]
  (map (fn [[k v]]
         [k v]
         (let [v (if (s/valid? ::specs2/cssvar-in-css-with-misplaced-!important v)
                   (string/replace v #"!important\)$" ")!important")
                   v)]
           [k v]))
       coll))

(defn all-style-tuples
  [coll]
  (let [css-vars (atom {})
        tuples   (as-> (!? coll) $
                   (postwalk-style-tuples $ normalize-css-custom-property)

                   (!? :normalize-css-custom-property $)

                   ;; '([:bc (if true mybc mybc2)]) => ([:bc "var(---1429181005)"])
                   ;; TODO - See if you can do this later in this op in order to support things like:
                   ;; [:border (when true [[:1px myborder-style '(rgb 2 mygreenval 2)]])]
                   ;; or
                   ;; [:c (if true '(rgb 2 133 47) :$myvar2)]
                   (sexp-cssvarized $ css-vars)
                   (!? sexp-cssvarized $)

                   ;; ([:b [["var(--myborder-width)" bstyle '(rgb 2 mygreenval 44)]]]) =>
                   ;; ([:b [["var(--myborder-width)" bstyle '("__cssfn__rgb" 2 mygreenval 44)]]])
                   (postwalk-style-tuples $ escape-list)
                   (!? :escape-list $)

                   ;; '([:bgc '("__cssfn__rgb" 2 2 "var(--orange-800)")]) =>
                   ;; '([:bgc ("__cssfn__rgb" 2 2 "var(--orange-800)")]) =>
                   (dequote $)
                   (!? :dequote $)

                   ;; '([:bw mybw]
                   ;;   [:outline [[:1px :solid mybc]]]) =>
                   ;; '([:bw "var(--mybw)"]
                   ;;   [:outline [[:1px :solid "var(--mybc)"]]])
                   (bindings-cssvarized $ css-vars)
                   (!? :bindings-cssvarized $)

                   ;; '([:bgc ("__cssfn__rgb" 2 2 "var(--orange-800)")]) =>
                   ;; '([:bgc "rgb( 2, 2, var(--orange-800))"])
                   (postwalk-style-tuples $ cssfn-list->string)
                   (!? :cssfn-list->string $)

                   ;; TODO -- Maybe don't need this sexp unescaping???
                   (postwalk-style-tuples $ unescape-sexp)
                   (!? :unescape-sexp $)

                   ;; Stringify any tuple values that are keywords
                   ;; These would be from user-supplied 2 element tuples, and/or entries in a stylemap
                   ;; '([:p :1rem]) => '([:p "1rem"])
                   (postwalk-style-tuples $ specs2/kw?->s)
                   (!? :specs2/kw?->s $)

                   ;; Normalize any tuple keys that are css custom properties
                   ;; '([:$xxx "red"]) => '(["--xxx" "red"])
                   (css-custom-property-keys-normalized $)
                   (!? :css-custom-property-keys-normalized $)

                   ;; Proper !important syntax for user-supplied cssvars
                   ;; '(["c" "var(--myvar!important)"]) => '(["c" "var(--myvar!important)"])
                   (css-custom-property-values-!important-normalized $)
                   (!? :css-custom-property-values-!important-normalized $))
        ]
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
  [{:keys [kushi-selector
           cache-key
           :kushi/process

           ;;new stuff
           assigned-class
           clean-stylemap
           clean-attrs

           conformed ;<- map of args by conformace spec

           ]

    :as   m*}]

  (let [shared-class?
        (util/shared-class? process)


        selector
        (selector/selector-name {:assigned-class assigned-class
                                 :kushi-selector kushi-selector
                                 :cache-key      cache-key
                                 :kushi/process  process})


        ;; '(:b--1px:solid:$blue-500
        ;;   :c--$myvar
        ;;   :m--0) =>
        ;; '(["b" "1px:solid:$blue-500"]
        ;;   ["c" "$myvar"]
        ;;   ["m" "0"])}
        style-tuples-from-tokenized
        (when-let [coll (:tokenized-style conformed)]
          (when (seq coll)
            (map #(let [s (name %)]
                    ;; TODO abstract this
                    (if (re-find #"^.*[^-]--:$[^-]+.+$" s)
                      (string/split (name %) #"--:")
                      (string/split (name %) #"--" 2)))
                 coll)))

        _ (!? conformed)
        _ (!? style-tuples-from-tokenized {:before (:tokenized-style conformed)
                                          :after  style-tuples-from-tokenized})

        ;; '($mycssvar--gold $myothervar--red) =>
        ;; '(["$mycssvar" "gold"] ["$myothervar" red])
        cssvar-tuples-from-tokenized
        (when-let [coll (:cssvar-tokenized conformed)]
          (when (seq coll)
            (map #(string/split (name %) #"--") coll)))

        _ (!? cssvar-tuples-from-tokenized {:before (:cssvar-tokenized conformed)
                                           :after  cssvar-tuples-from-tokenized})

        ;; Given a list of existing defclasses,
        ;; for each defclass get all its prop/vals as a list of 2-element vectors
        ;; Then concat these all together
        style-tuples-from-defclass-class
        (when-let [classes (:defclass-class conformed)]
          (mapcat #(let [k (some-> % specs2/dot-kw->s symbol)]
                     (get-in @state2/shared-classes [k]))
                  classes))

        _ (!? style-tuples-from-defclass-class {:before (:defclass-class conformed)
                                               :after  style-tuples-from-defclass-class})

        ;; Concat and distinct all the different kinds of style tuples
        all-style-tuples*
        (distinct
         (concat cssvar-tuples-from-tokenized
                 (:cssvar-tuple conformed)
                 style-tuples-from-tokenized
                 (:style-tuple conformed)
                 (:style-tuple-defclass conformed)
                 style-tuples-from-defclass-class
                 clean-stylemap))


        ;; Deal with runtime vars, cssvars, and cssfns
        [all-style-tuples css-vars]
        (all-style-tuples all-style-tuples*)

        [cssvar-tuples2 all-style-tuples2]
        (util/partition-by-pred #(->> %
                                      first
                                      (s/valid? ::specs2/css-var-name))
                                all-style-tuples)

        defclass-style-tuples
        (when shared-class? all-style-tuples2)


        ;; _ (when (contains? #{'kushi-playground-demobox-ui-icon} assigned-class)
        ;;     (? :styles/tuples* (keyed assigned-class
        ;;                               all-style-tuples*
        ;;                               all-style-tuples
        ;;                               conformed)))

        selector
        (cond
          (:assigned-class conformed)
          ;; assigned-class
          selector
          (seq all-style-tuples2)
          selector)

        ;; maybe handle defclass differently here

        ;; mabye do this in args?
        attrs-no-style
        (when clean-attrs (dissoc clean-attrs :style))

        classlist
        (classlist attrs-no-style (:class conformed) selector)

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
