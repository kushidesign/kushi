(ns kushi.cssfn
  (:require
   [fireworks.core :refer [? !? ?> !?>]]
   [fireworks.pp :refer [pprint]]
   [bling.explain :refer [explain-malli]]
   [bling.core :refer [bling]]
   [bling.hifi :refer [hifi]]
   [clojure.string :as string]
   [clojure.walk :as walk]
   [kushi.util :as util :refer [when-> when->> as-str kw->cssvar2 str-ml]])
  #?(:cljs
     (:require-macros [kushi.cssfn :refer [fallback-value]])))

;; CoDE FROM DOCSTRING LIB -----------------------------------------------------
;; Maybe move this into bling.explain?

(defn- dequote-vec [x]
  (if (and (list? x)
           (= (first x) 'quote))
    (second x)
    x))

(defn- example-form->str [forms opts]
  (string/join 
   "\n"
   (mapv (fn [[form result :as vc]]
           (let [result 
                 (if (= 2 (count vc)) 
                   result
                   ::result-unsupplied)]

             (if (:hifi? opts)
               ;; TODO - use hifi, when you get quoted lists working
               (bling (-> form
                          (pprint {:max-width 70})
                          with-out-str)
                      (when-not (= result ::result-unsupplied)
                        (bling [:italic.dim.yellow ";; =>\n"]
                               (-> result
                                   (pprint {:max-width 70})
                                   with-out-str))))
               (str (-> form
                        (pprint {:max-width 70})
                        with-out-str)
                    (when-not (= result ::result-unsupplied)
                      (str ";; =>\n"
                           (-> result
                               (pprint {:max-width 24})
                               with-out-str)))))))
         (dequote-vec forms))))


(defn- adjusted-indentation [s]
  (let [re #"\n( +)"
        n  (some->> s
                    str
                    (re-seq re)
                    (group-by #(count (second %)))
                    keys
                    (apply min))
        f  (fn [[a]] (str "\n" (subs a (inc n))))
        s  (string/replace s re f)]
    s))

(defn- escape-escaped-double-quote [s]
  (string/replace s #"\"" "\\\""))

(defn- escape-escaped-backslashes [s]
  (string/replace s #"\\" "\\\\\\\\"))

(defn- desc->str [s]
  (some-> s adjusted-indentation))

(defn- example->md
  ([example]
   (example->md example nil))
  ([example {:keys [markdown? hifi?]
             :or   {markdown? true
                    hifi?     false}
             :as   opts}]
   (let [{:keys [desc forms]} example
         desc+forms?          (boolean (and desc forms))
         no-doc?              (-> example meta :no-doc)]
     (!? {:example    example
          :no-doc?    no-doc?
          :desc+form? desc+forms?})
     (when-not no-doc?
       (when desc+forms?
         (str (when-not markdown? (bling  [:italic.dim.yellow (str ";; " (desc->str desc))]))
              "\n"
              (when markdown? "```clojure\n")
              (if (string? forms)
                ;; Example form (and maybe result) supplied as a potentially
                ;; multi-line string
                ;; E.g.
                ;; "(callout {:type        :error   ; :warning, :info
                ;; |          :label-theme :simple  ; :simple :marquee
                ;; |         }
                ;; |         (bling [:bold (str \"Line 1\" \"\\n\" \"Line 2\")]))"
                (-> (string/join "\n" (string/split forms #"\n *\|"))
                    escape-escaped-double-quote
                    escape-escaped-backslashes)

                ;; Example form(s) (and maybe corresponding result(s)) supplied
                ;; as a vector or 1 or 2 element vectors
                ;; E.g.
                ;; [[(my-adder 1 2) 3]
                ;;  [(my-adder 1 2 3) 6]]
                (-> forms
                    (example-form->str opts) 
                    (string/replace #"\n$" "")))
              (when markdown? "\n```")))))))

;; CoDE FROM DOCSTRING LIB END -------------------------------------------------

(defn- usage-examples
  [selected-example-descriptions
   examples]
  ;; (? selected-example-descriptions)
  ;; (? examples)
  (let [examples
        (if (nil? selected-example-descriptions)
          examples
          (some->> selected-example-descriptions
                   (keep (fn [desc]
                           (some #(when (= (:desc %) desc) %)
                                 examples)))
                   (when->> #(not (empty? %)))))]
    (some->> examples
             (when->> #(not (empty? %)))
             ;; TODO - move this and supporting code to malli-explain ----------
             ;; refactor back into docstring and share code
             (keep #(example->md % {:markdown? false
                                    :hifi?     true}))
             (string/join "\n\n")
             ;; ----------------------------------------------------------------
             

             (hash-map :usage-examples-body))))

;; TODO - should this have a stub, for clojurescript prod?
(defmacro ^:public ^:experimental fallback-value 
  "Returns a fallback value if a function's supplied args are malformed, meaning
   not validated with `malli.core/valid`.

   Prints a warning with bling.explain/explain-malli.

   First argument must be the function itself.
   
   Second argument must be a map with the following entries:
   ```
   [:args                ; <- collection of the args
    :fallback-value      ; <- this will be returned if validation fails
    :fallback-value-desc ; <- this will be used to annotate the `malli-explain` callout 
    :examples]           ; <- `:examples` from metadata map, used for annotation in `malli-explain` callout
   ```
   
   The `:fallback-value-desc` and `:examples` entries are optional.

   The function must have a metadata map with a `:schema` entry, which must be
   a valid Malli schema. Additional entries such as `:doc` and `:examples` are
   optional but recommended."
  [fn-sym {:keys                         [args fallback-value]
           selected-example-descriptions :examples
           :as                           fallback-opts}]
  `(let [m#              (-> ~fn-sym var meta)
         schema#         (:schema m#)
         fn-ns#          (:ns m#)
         fn-name#        (:name m#)
         doc#            (:doc m#)
         examples#       (:examples m#)
         usage-examples# (usage-examples ~selected-example-descriptions
                                         examples#)
         fq-fn-name#     (str fn-ns# "/" fn-name#)]
     
     (when-not (bling.explain/explain-malli*
                schema#
                (list (quote ~fn-sym) ~@args)
                (merge {:highlighted-problem-section-label (hifi (symbol fq-fn-name#))
                        :docs-section-label                (str "Docs for " fq-fn-name# ":")
                        :docs-section-body                 (str-ml doc#)
                        :section-body-indentation          3
                        :return-boolean?                   true}
                       usage-examples#
                       (dissoc ~fallback-opts :args :examples)))
       ~fallback-value)))


;; TODO - make similar fns for css min, max, and clamp, which all use commas and spaces
;; Examples:
;; `min(2, 3)`              ;; <- css
;; (min 2 3)                ;; <- clj

;; `min(2 + 3, 4 + 2)`      ;; <- css
;; (min '(+ 2 3) '(+ 4 2))  ;; <- clj 


(defn cssfn-wrap-str [s cssfn-nm]
  (str (as-str cssfn-nm) "(" s ")"))


(defn calc-impl [coll]
  (walk/postwalk
   (fn [x]
     (if-let [[op & nums] (some-> x (when-> list?) seq)]
       (if (and (contains? #{'+ '- '/ '*} op)
                (seq nums))
         (interpose op nums)
         x)
       ;; maybe remove?
       (-> x kw->cssvar2 symbol)))
   coll))


;; TODO maybe css math should only be used like '(calc (* :12px 0.5))
;; Then you can have css-calc, css-atan, etc for dispatch and runtime
;; Maybe make a cssfn namespace?


(defn ^:public css-calc
  {:doc      "Converts prefix arithmetic to infix css calc() format.
            
              Expects a quoted list with a valid arithematic
              operator (+, -, *, /) at index 0.

              Returns a css calc string."
   :examples [{:desc  "Typical, idiomatic usage.
                       ;; When using with a macro from kushi.core,
                       ;; such as `sx` or `defcss`, drop the `css-`
                       ;; prefix and use within a quoted list."
               :forms '[[(sx '(calc (+ 1 2)))]]}
              {:desc  "Usage of actual `css-calc` at runtime."
               :forms '[[(css-calc (+ 1 2))
                         "calc(1 + 2)"]]}
              {:desc  "With nesting"
               :forms '[[(css-calc '(* 2 (- 10 (/ 6 3) 1) 4))
                         "calc(2 * (10 - (6 / 3) - 1) * 4)"]]}]
   :schema   [:and
              list?
              [:cat
               [:= 'css-calc]
               [:and
                list?
                [:cat
                 '[:enum + - * /]
                 [:+ any?]]]]]}
  [ls]
  (or (fallback-value
       css-calc
       {:args                [ls]
        :fallback-value      "_INVALID-VALUE__KUSHI_UTIL_CALC_"
        :fallback-value-desc "This is equivalent to a css value of `unset`."
        ;; :examples            ["With nesting"]
        })
      (some-> ls
              (when-> list?)
              calc-impl
              (pr-str)
              (->> (str "calc")))))




#_(defmacro ^:public calc* [coll]
    (let [infixed (calc-impl coll)]
      `(-> ~infixed
           quote
           str
           (cssfn-wrap-str "calc"))))


(defn- arithmetic-impl
  [coll]
  (string/join ", "
               (mapv #(if (list? %)
                        (-> % calc-impl pr-str)
                        (kw->cssvar2 %))
                     coll)))

(defn- arithmetic
  [fn-sym args]
  (or (some-> 
       args
       seq
       arithmetic-impl
       ?
       (cssfn-wrap-str fn-sym))
      "_INVALID-VALUE__KUSHI_CSS_" (string/upper-case (name fn-sym)) "_"))


(defn ^:public css-min [& args]
  (arithmetic 'min args))


(defn ^:public css-max [& args]
  (arithmetic 'max args))


(defn ^:public css-clamp [& args]
  (arithmetic 'clamp args))


(defn ^:public css-calc-size [& args]
  (arithmetic 'calc-size args))


(defn css-fn [fname & args]
  (str fname "(" (string/join ", " (!? args)) ")"))


(defn- cssfn-color-string
  "(cssfn-color-string \"hsla\" \"100deg\" \"50%\" \"33%\" \"0.8\")
   => \"hsla(100deg 50% 33% / 0.8)\""
  ([nm a b c]
   (cssfn-color-string nm a b c nil))
  ([nm a b c alpha]
   (str (as-str nm)
        "("
        (string/join " " (mapv kw->cssvar2 [a b c]))
        (when alpha (str " / " alpha))
        ")")))


(defn ^:public css-oklch
  "(oklch \"100%\" \"0.2\" \"33\" \"0.8\")
   => \"oklch(100% 0.2 33 / 0.8)\""
  [& args]
  (apply cssfn-color-string (into ["oklch"] args)))


(defn ^:public css-rgb
  "( \"100\" \"20\" \"33\" \"0.8\")
   => \"rgb(100 20 33 / 0.8)\""
  [& args]
  (apply cssfn-color-string (into ["rgb"] args)))

(defn ^:public css-color-mix [color-space & args]
  (->> args
       util/map-css-tuple-args
       (into ["color-mix" color-space])
       (apply css-fn)))

(defn ^:public css-linear-gradient [direction & args]
  (->> args
       util/map-css-tuple-args
       (into ["linear-gradient" direction])
       (apply css-fn)))
