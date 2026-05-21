(ns kushi.core
  (:require ;; for testing
 ;;  [taoensso.tufte :as tufte]
   [babashka.process :refer [shell]] ;; for testing
   [bling.core :refer [bling callout point-of-interest]]
   [bling.explain :refer [explain-malli explain-malli*]]
   [bling.hifi :refer [hifi]]
   [clojure.spec.alpha :as s]
   [clojure.string :as string :refer [replace] :rename {replace sr}]
   [clojure.walk :as walk :refer [postwalk prewalk]]
   [fireworks.core :refer [!? ? ?flop pprint]]
   [fireworks.sample]
   [fireworks.messaging]
   [kushi.css.build.colorways :refer [colorway-args colorway-selector]]
   [kushi.css.defs :as defs]
   [kushi.css.hydrated :as hydrated]
   [kushi.css.block]
   [kushi.css.schemas :as schemas]
   [kushi.css.shorthand :as shorthand]
   [kushi.css.specs :as specs]
   [kushi.cssprops :as cssprops]
   [kushi.specs2 :as specs2]
   [kushi.ui.core :refer [html-attrs]]
   [kushi.ui.variants :as props]
   [kushi.util :as util :refer [as-str keyed maybe more-than-one?
                                spaces
                                partition-by-pred
                                partition-by-spec
                                vec-of-vecs?
                                beautify-css
                                when->
                                when->>]]
   [kushi.validate :refer [validate-sx2]]
   [malli.core :as m :refer [validate]]
   [malli.util :as mu]
   [malli.transform :as mt]
   [kushi.ui.variants :as variants]
   [clojure.spec.alpha :as spec]))



;; HHHHHHHHH     HHHHHHHHH LLLLLLLLLLL              PPPPPPPPPPPPPPPPP   
;; H:::::::H     H:::::::H L:::::::::L              P::::::::::::::::P  
;; H:::::::H     H:::::::H L:::::::::L              P::::::PPPPPP:::::P 
;; HH::::::H     H::::::HH LL:::::::LL              PP:::::P     P:::::P
;;   H:::::H     H:::::H     L:::::L                  P::::P     P:::::P
;;   H:::::H     H:::::H     L:::::L                  P::::P     P:::::P
;;   H::::::HHHHH::::::H     L:::::L                  P::::PPPPPP:::::P 
;;   H:::::::::::::::::H     L:::::L                  P:::::::::::::PP  
;;   H:::::::::::::::::H     L:::::L                  P::::PPPPPPPPP    
;;   H::::::HHHHH::::::H     L:::::L                  P::::P            
;;   H:::::H     H:::::H     L:::::L                  P::::P            
;;   H:::::H     H:::::H     L:::::L         LLLLLL   P::::P            
;; HH::::::H     H::::::HH LL:::::::LLLLLLLLL:::::L PP::::::PP          
;; H:::::::H     H:::::::H L::::::::::::::::::::::L P::::::::P          
;; H:::::::H     H:::::::H L::::::::::::::::::::::L P::::::::P          
;; HHHHHHHHH     HHHHHHHHH LLLLLLLLLLLLLLLLLLLLLLLL PPPPPPPPPP          
;; -----------------------------------------------------------------------------
;; API Helpers
;; -----------------------------------------------------------------------------


;; -----------------------------------------------------------------------------
;; TODO - Use this version of loc-id to investigate weird diff between 
;; -----------------------------------------------------------------------------

;; (? (css :.foo :p--10px :c--red)) => "foo"
;; and
;; (? :pp (css :.foo :p--10px :c--red)) => "foo [\"__35_8\"]"


;; (defn- loc-id
;;   "Returns classname based on namespace and line + column.
;;    e.g. \"starter_browser__41_6\""
;;   [env form]
;;   (let [ns* (some-> env :ns :name (sr #"\." "_"))
;;         fm  (meta form)]
;;     (str ns* "__" (:line fm) "_" (:column fm))))

;; -----------------------------------------------------------------------------

;; TODO - determine if you still need this and why, since we are no longer
;; calling if from css-block*
(defn- user-classlist
  "Expects a conformed map based on `::specs/sx-args`. This map is the
   `:conformed` entry from return val of `kushi.css.flatten/vectorized*`.

   Returns a map like:
   {:class-kw '(...)
    :classes [...]}"
  ([m]
   (user-classlist m nil))
  ([{:keys [class-kw class-binding] :as m}
    loc-id]
   (let [class-kw-stringified (map specs/dot-kw->s class-kw)]
     {:class-binding class-binding
      :classes       (into []
                           (concat class-binding
                                   class-kw-stringified
                                   (some-> loc-id vector)))})))


;; TODO gradually add changes back in
(defn- classlist
  "Returns classlist vector of classnames as strings. Includes user-supplied
   classes, as well as auto-generated, namespace-derived classname from `css`
   macro."
  ([form args]
   (classlist {:ns {:name "[unresolved ns]"}} form args))
  ([env form args]
   (let [fa                  (first args)
         supplied-classname  (when (and (string? fa)
                                        (re-find specs/classname-with-dot-re fa))
                               (subs fa 1))
         id-selector         (and (string? fa)
                                  (re-find specs/id-with-hash-re fa)
                                  fa)
         attr-selector       (and (string? fa)
                                  (re-find specs/attribute-selector-re fa)
                                  fa)
         loc-id-str          (some-> env (kushi.css.block/loc-id form))
         data-ks-at          (when-let [[ns-str loc-str] (some-> loc-id-str (string/split #"__"))]
                               (let [ns-str  (string/replace ns-str #"_" ".")
                                     loc-str (string/replace loc-str #"_" ":")]
                                 (str ns-str ":" loc-str)))
         sel                 (or supplied-classname
                                 id-selector
                                 attr-selector
                                 loc-id-str)
         args                (if supplied-classname (rest args) args)
         m                   (-> args
                                 specs/conformed-args
                                 :conformed-args
                                 kushi.css.block/vectorized*
                                 :conformed-map)
         alternate-selectors (merge (when id-selector
                                      {:id (subs id-selector 1)})
                                    (when attr-selector
                                      (let [[_ attr val] 
                                            (re-find specs/attribute-selector-re-with-capturing 
                                                     attr-selector)

                                            val
                                            (-> val
                                                (string/replace #"^[\"\']" "")
                                                (string/replace #"[\"\']$" ""))]
                                        {attr val})))
         user-classlist     (merge (user-classlist
                                    m 
                                    (when-not (or id-selector attr-selector)
                                      sel))
                                   (keyed [alternate-selectors data-ks-at]))]
     #_(when (= fa ".ui-icon")
       (println (re-find specs/attribute-selector-re-with-capturing ".ui-icon"))
       #_(pprint (keyed [
                      ;;  fa
                      ;;  supplied-classname
                      ;;  sel
                      ;;  args
                      ;;  m
                      ;;  attr-selector
                      ;;  m
                       user-classlist
                       ])))
     user-classlist)))




;; -----------------------------------------------------------------------------
;; Print debugging helpers
;; -----------------------------------------------------------------------------

(defn- print-as-def [{:keys [&form sym]}]
  (-> (cons (symbol (bling [:bold (str sym " \"" (second &form) "\"")]))
            (drop 2 &form))
      fireworks.core/pprint
      with-out-str
      (sr #"\n$" "")
      (sr #"\n" "\n ")))


(defn- print-as-fcall [{:keys [&form sym]}]
  (-> &form
      bling.hifi/hifi
      (string/replace-first #"\n" "")
      (sr #"\n$" "")
      (sr #"^\(|\)$" "")
      (sr #"\n" (str "\n" (spaces (inc (count (name sym)))))))

  #_(-> (rest &form)
      fireworks.core/pprint
      with-out-str
      (sr #"\n$" "")
      (sr #"^\(|\)$" "")
      (sr #"\n" (str "\n" (spaces (inc (count (name sym))))))
      (->> (bling "(" [:bold (name sym)] " " ))
      (str ")")))


(defn- print-css-block [{:keys [sym &form expands-to]
                         :as   m}]
  (callout 
   {:label       (let [{:keys [file line column]} (meta &form)]
                   (str file ":" line ":" column))
    :type        :info
    :padding-top 1}
   (bling (if (= sym '?defcss)
            (print-as-def m)
            (print-as-fcall m))
          "\n\n"
          [:italic.subtle.bold "Expands to:"]
          "\n"
          (bling.hifi/hifi expands-to)
          "\n\n"
          [:italic.subtle.bold "Emits css ruleset:"]
          "\n"
          (kushi.css.block/ansi-colorized-css-block m))))


(defn- classes+class-binding [args &form &env]
  (apply classlist 
         (if-not (:ns &env)
           [&form args]
           [&env &form args])))




;;                AAA               PPPPPPPPPPPPPPPPP   IIIIIIIIII
;;               A:::A              P::::::::::::::::P  I::::::::I
;;              A:::::A             P::::::PPPPPP:::::P I::::::::I
;;             A:::::::A            PP:::::P     P:::::PII::::::II
;;            A:::::::::A             P::::P     P:::::P  I::::I  
;;           A:::::A:::::A            P::::P     P:::::P  I::::I  
;;          A:::::A A:::::A           P::::PPPPPP:::::P   I::::I  
;;         A:::::A   A:::::A          P:::::::::::::PP    I::::I  
;;        A:::::A     A:::::A         P::::PPPPPPPPP      I::::I  
;;       A:::::AAAAAAAAA:::::A        P::::P              I::::I  
;;      A:::::::::::::::::::::A       P::::P              I::::I  
;;     A:::::AAAAAAAAAAAAA:::::A      P::::P              I::::I  
;;    A:::::A             A:::::A   PP::::::PP          II::::::II
;;   A:::::A               A:::::A  P::::::::P          I::::::::I
;;  A:::::A                 A:::::A P::::::::P          I::::::::I
;; AAAAAAA                   AAAAAAAPPPPPPPPPP          IIIIIIIIII
;; -----------------------------------------------------------------------------
;; Public API
;; -----------------------------------------------------------------------------

(defmacro ^:public register-design-tokens [& args]
  nil)

(defmacro ^:public register-design-tokens-by-category [& args]
  nil)

(defmacro ^:public utilize [x]
  `~x)


(defmacro ^:public css-block-data
  "Returns a map with following keys:
   :nested-vector    ->  vector representation of nested css.
   :nested-css-block ->  pretty-printed css ruleset, no selector.
   :classes          ->  user supplied classes.
   :ns               ->  namespace of the callsite, a symbol.
   :file             ->  filename as string
   :line             ->  line number
   :column           ->  column number
   :end-line         ->  end line number
   :end-column       ->  end column number"
  [& args]
  (merge (kushi.css.block/css-block* args)
         (some->> &env :ns :name str symbol (hash-map :ns))
         (meta &form)))


;; Does this need to be a macro?
;; Maybe it just gets callsite info from analyzer fn which calls it.
(defmacro ^:public css-block
  "Returns a pretty-printed css rule block (no selector)."
  [& args]
  (kushi.css.block/nested-css-block args
                                    &form
                                    &env
                                    "kushi.core/css-block"
                                    nil))




(defmacro ^:public defcolorway
  "Used internally to define colorway rulesets for kushi ui theming system.
   `s` must be a string that maps to a color-token base e.g. `\"red\"`, 
   `\"warning\"`, etc.
   The function call will be picked up in the analyzation phase of a build,
   then fed to `css-colorway` to produce a css rule that will be written to disk.
   Expands to nil."
  [s]
  nil)


(defmacro ^:public css-include
  "Used to pull in .css resources. Expands to nil.

   `sel` must be a string and a valid classpath resource, and can be optionally
   proceeded by a layer declaration, e.g.:
   \"@layer my-layer-name my/path/to/style.css\".

   If no @layer info is supplied, the css that is imported will be assigned to
   the \"user-shared-styles\" layer, same as styles produced with defcss.

   The information about the layer and resource will get included in the build."
  [sel]
  nil)


(defmacro ^:public defcss
  "Used to define shared css rulesets.
   `sel` must be a valid css selector in the form of a string.
   `args` must be valid style args, same as the `css` and `sx` macros.
   The function call will be picked up in the analyzation phase of a build, then fed to `css-rule` to produce a css rule that will be written to disk.
   Expands to nil."
  [sel & args]
  nil)


(defmacro ^:public ?defcss
  "Tapping version of `defcss`"
  [sel & args]
  (if-not (or (s/valid? ::specs/css-selector sel)
              (s/valid? ::specs/at-selector sel))
    (kushi.css.block/rule-selector-warning sel &form)
    (let [block (kushi.css.block/css-rule* sel args &form &env)]
      (print-css-block (assoc (keyed [args &form &env block])
                              :sym
                              '?defcss))
      nil)))



(defmacro ^:public ?defcolorway
  "Tapping version of `defcolorway`"
  [s]
  (let [sel   (colorway-selector s)
        args  (colorway-args s)
        block (kushi.css.block/css-rule* sel args &form &env)]
    (print-css-block (assoc (keyed [args &form &env block])
                            :sym
                            '?defcolorway))
    nil))



;; TODO - For release builds we might want to elide the inclusion of the
;;        auto-generated classname (e.g. myns_foo__L20_C11), if that ruleset
;;        does not contain any rules. This happens when css or sx is called with
;;        only kushi utility or shared classes e.g. (sx :.absolute-centered).
;;        It is probably preferrable to include these in dev for debugging.
;;        This release build elision could be turned off with config option.

(defmacro ^:public css
  "Returns classlist string consisting of auto-generated classname and
   user-supplied classnames.
   
   Example of expansion in a component. Let's say the namespace is called
   foo.core, on line 100:

   100 | (defn my-component [text]
   101 |   [:div
   102 |    {:class (css :.absolute :c--red :fs--48px)}
   103 |    text])
   =>
   (defn my-component [text]
    [:div
     {:class \"absolute foo_core__L102_C11\"}
     text])
   
   The call to `css` produces the following class in the build's
   watch/analyze/css generation process:

   .foo_core__L102_C11 {
     color:     red;
     font-size: 48px;
   }"
  [& args]
  (let [
        ;; If calling from a test namespace, it might not resolve a
        ;; val for &env so we will call classlist with 2 args instead of 3.
        {:keys [classes class-binding]} (classes+class-binding args &form &env)]

    ;; If `classes` vector contains any symbols that are runtime bindings
    ;; intended to hold classnames (`class-bindings`) we will need to
    ;; string/join it at runtime, e.g.:
    ;; `[(when my-runtime-var "foo") my-classname "bar"]`
    ;;
    ;; If no conditional class forms, we can string/join it at compile time


    (if (seq class-binding) 
       `(kushi.core/class-str ~classes)
      (string/join " " classes))))


(defmacro ^:public ?css
  "Tapping version of `css`"
  [& args]
  (let [{:keys [classes class-binding]}
        (classes+class-binding args &form &env)

        expands-to
        (if (seq class-binding) 
          `{:class (kushi.core/class-str ~classes)}
          {:class (string/join " " classes)})]

    (print-css-block (assoc (keyed [args &form &env expands-to]) :sym '?css))
    (if (seq class-binding) 
      `(kushi.core/class-str ~classes)
      (string/join " " classes))))


;; TODO - For release builds we might want to elide the inclusion of the
;;        auto-generated classname (e.g. myns_foo__L20_C11), if that ruleset
;;        does not contain any rules. This happens when css or sx is called with
;;        only kushi utility or shared classes e.g. (sx :.absolute-centered).
;;        It is probably preferrable to include these in dev for debugging.
;;        This release build elision could be turned off with config option.

;; TODO - remove / swap with sx2

(defmacro ^:public sx
  "Returns a map with a :class string. Sugar for `{:class (css ...)}`, to avoid
   boilerplate when you are only applying styling to an element and therefore do
   not need to supply any html attributes other than :class."
  [& args]
  (let [{:keys [classes class-binding alternate-selectors]}
        (classes+class-binding args &form &env)]
    (if (seq class-binding) 
      (if alternate-selectors
        `(merge {:class (kushi.core/class-str ~classes)}
                alternate-selectors)
        `{:class (kushi.core/class-str ~classes)})
      (merge {:class (string/join " " classes)}
             alternate-selectors))))



;; -----------------------------------------------------------------------------
;; sx2 Start 
;; -----------------------------------------------------------------------------

(defn- css-prop? [k]
  (boolean 
   (when (s/valid? ::specs/css-prop-standard-potential k)
     (or (contains? shorthand/all-props-as-kws k)
         (contains? cssprops/cherries-set k)
         (contains? cssprops/non-cherries-set k)))))


(defn ^:public props+attrs+css
  [m]
  (reduce-kv 
   (fn [acc k v]
     (let [data-*?
           (-> k util/as-str (string/starts-with? "data-"))

           ks
           (cond 
             ;; kushi-specific shared props for variants such as:
             ;; :stroke, :shadow-size, :colorway, :display, :position, etc.
             (contains? props/shared-props-keys k)
             [:props k]
             
             ;; A :data-* attribute
             data-*?
             [:attrs k]

             ;; Any html attribute such as :id, :class, :name, etc.
             (contains? html-attrs k)
             [:attrs k]

             ;; css syntax such as `:color`,
             ;; or kushi-specific css stacked syntax like `:_p:hover:color`
             (or (s/valid? ::specs/css-custom-prop k)
                 (and (vector? k) (seq k))
                 (!? {:when (= k :w)} (css-prop? k))
                 (!? {:when (= k :debug)}
                     (and (s/valid? ::specs/css-prop-stack k)
                          (not (s/valid? ::specs/css-prop-standard-potential
                                         k)))))
             [:css k]

             ;; user custom props
             :else
             [:custom-props k])
           v
           (if data-*? (as-str v) v)]
       (if ks (assoc-in acc ks v) acc)))
   {:props        {}
    :attrs        {}
    :css          {}
    :custom-props {}}
   (dissoc m :selector)))


(defn ^:public extract-css-props
  [m]
  (!? extract-css-props
      (-> m
          (dissoc :selector)
          props+attrs+css
          :css)))


(defn- class-map [selector m+]
  (let [class-selector
        (!? 'class-selector 
           (or (some-> selector
                       (maybe #(string/starts-with? % ".")))
               (first (:classes m+))))

        cls
        (!? 'cls1 (when-let [cls (some-> m+ :attrs :class)] 
          (cond (string? cls)
                (string/split cls #" ")
                (coll? cls)
                (seq cls))))

        cls 
        (!? 'cls2 (if cls 
          (into []
                (if class-selector
                  (concat cls 
                          (some-> class-selector
                                  vector))
                  cls))
          class-selector))]
    (some->> cls (hash-map :class))))


(defn- data-ks-attrs [m+]
  (reduce-kv (fn [m k v] 
               (if (some-> props/props k :data-ks?)
                 ;; tODO debug ? :trace macro here
                 (or #_(? :trace (some-> k 
                                         (get variants/data-ks-transformers)
                                         (apply [k v])
                                         (->> (merge m))))

                     (some-> (get variants/data-ks-transformers k)
                             (apply [k v])
                             (->> (merge m)))

                     (assoc m
                            (keyword (str "data-ks-"
                                          (name k) 
                                          (when (= k :surface)
                                            "2")))
                            (cond (true? v)
                                  ""
                                  (symbol? v)
                                  v
                                  :else
                                  (as-str v))))
                 m))
             {}
             (:props m+)))





(defn- hydrate-style-attribute-value 
  [m &form &env selector]
  (if-let [x (:style m) #_(when-let [x (:style m)]
               (when (or (map? x)
                         (string? x) 
                         (symbol? x))
                 (let [schema schemas/style-map-for-style-attribute
                       opts   {:highlighted-problem-section-label 
                               (bling [:italic
                                       "Bad value for "
                                       [:purple :style]
                                       " entry supplied to "
                                       [:purple "kushi.core/sx2"]])}]
                   (cond (map? x)
                         ;; TODO Move this validation down into sx2*
                         (validate-sx2-args x &form schema opts)
                         (string? x)
                         (validate-sx2-args x &form schema opts)
                         :else
                         x))))]
    (assoc m
           :style 
           (if (map? x)
             (let [{:keys [conformed-args]}
                   (specs/conformed-args [x])

                   ret                      
                   (->> conformed-args
                        kushi.css.block/grouped-css-declarations
                        (into {}))]
               #_(keyed [args
                         &form
                         &env
                         fname
                         sel
                         conformed-args
                         invalid-args])
               ret)
             x))
    m))

(defn- theme-styles*
  "If some of the kushi-specific shared props such as `:shadow-color` are meant
   to set local tokens in the `style` attribute, e.g. `--shadow-color`, then
   merge those into the style map here, with optional transformation."
  [props]
  (some-> props
          (select-keys variants/local-tokens)
          !?
          (->> (reduce-kv
                (fn [m k v]
                  (!? :no-file {:margin-top 4} k)
                  (merge m
                         (or (!? :transformers (some-> (get variants/local-token-transformers k)
                                                       !?
                                                       (apply [k v])))
                             (!? :- {(str "--" (name k)) (name v)}))))
                {}))))

;; TODO - reconcile if selector is "#foo" and :id is something else

;; TODO Move into sx2* - ?
;; Sanitize with Malli transformers and decoders
;; You need to sanitize everything that is not going to the css pipeline
;; 

(defn- sx2-poi [props k {:keys [file line column]} floating-label-opts] 
  (bling.core/point-of-interest
   {:margin-top             1
    :header-file-info-style {:font-style :italic}
    :form                   (-> (bling.hifi/hifi 
                                 (list
                                  'sx2
                                  (bling.hifi/double-truncated-map props k)))
                                (bling.core/with-floating-label
                                  floating-label-opts))
    :file                   file
    :line                   line
    :column                 column}))


(defn- warn-on-missing-stroke-width! [props &form]
  (when-not (get props :stroke-width)
    (when-let [k (first (filter 
                         #(contains? #{:stroke-color 
                                       :stroke-opacity 
                                       :stroke-align}
                                     %)
                         (-> props keys seq)))]
      (callout {:type            :warning
                :label-theme     :marquee
                :border-notches? true
                :side-label      (bling.core/file-info-str (meta &form))}
               (bling [:p
                       "Unless you supply a value for "
                       (hifi :stroke-width) ","
                       [:br]
                       "no stroke will be rendered."]
                      (sx2-poi props
                               k
                               (meta &form)
                               {:line-index  2
                                :label-text  "<- Missing :stroke-width"
                                :label-style {:color :warning}}))))))


(defn- sx2* [m &form &env]
  (let [selector      (:selector m)
        m             (dissoc m :selector)
        m             (hydrate-style-attribute-value m &form &env selector)
        ret           (props+attrs+css m)
        args          (if selector [selector m] [m])
        m+            (merge ret (classes+class-binding args &form &env))
        props         (:props m+)
        class-map     (class-map selector m+)
        data-ks-attrs (data-ks-attrs m+)
        theme-styles  (theme-styles* props)
        style-map     {:style (merge theme-styles
                                     (some-> m+ :attrs :style))}]
    
    ;; put bad props in here ^
    ;; so you can later use them with a defmacro component
    ;; that might override defaults
    ;;     See if bad props, use defaults instead, if defined 
    ;;     Validate custom props
    ;;     Do you have a different version of sx that pulls out these custom
    ;;     props?
    ;;     Or does the component macro code check for them in the user-props
    ;;     slot?

    (warn-on-missing-stroke-width! props &form)

    {:dynamic-props? (boolean (some->> props vals (some symbol?)))
     :attrs          (merge data-ks-attrs
                            (select-keys m+ [:data-ks-at])
                            (:attrs m+) 
                            class-map
                            style-map)}))


;; Move impl into a cljc file?
(defn ^:public validator
  "This exists if we are testing sx macro in JVM clojure, stub for runtime kushi-core.cljs/validator"
  [attrs-coll _ _]
  (? "kushi-core.clj/validator (clj stub for runtime cljs)" attrs-coll))

;; Move impl into a cljc file?
(defn ^:public merge-attrs-stub [& maps]
  (let [[m1 m2] maps]
    (!? 'merge-attrs-stub 
       (assoc (merge m1 m2)
              :class
              (into [] (concat (:class m1) (:class m2)))
              :style
              (merge (:style m1) (:style m2))))))


;; TODO (defcss {:--foo {:. :7%}})    ; <- the :. causes stack overflow

(defmacro ^:public sx2
  "Returns an html attributes map containing an autogenerated class name,
   supplied attributes, and relevant data-* attributes to control theming.

   Takes any number of args.
   
   Although more of an edge case, the first argument can be a string to manually
   specify a selector. Thi selector can be one of the following patterns:
   \".foo\"
   \"#foo\"
   \"[data-ks-ui=foo]\"

   The rest of the arguments must be maps, or symbols that are bound to vals
   which are maps. 
   
   If multiple args are supplied, all map-literals will be sorted out into a
   coll bound to `attrs-coll`, then macro will expand to:
   `(apply kushi.core/merge-attrs ~attrs-coll)`. 
   
   Pulls out shared kushi props from map literals, validates the values
   (if not dynamic), and converts them to data-ks-* attributes which dictate
   styling using kushi's theming system.
   
   Removes all css prop / values. These css properties and values are separately
   processed in kushi's analyzation phase, and used to create rulesets with the
   appropriate selectors.

   If multiple map literals are passed, only one can contain css properties and
   values (alongside kushi props and html attributes). A unique classname will
   be generated, based on the namespace and row/col, unless a `leading` key is
   present in the map.
   
   
   Examples:

   (sx {:color :red})
   => {:class      \"my_ns__L11_C3\"
       :data-ks-at my.ns:L11:C3}

   (sx {:display :flex-row-center
        :shape   :pill})
   => {:data-ks-display \"flex-row-center\"
       :data-ks-shape   \"pill\"
       :data-ks-at      my.ns:L11:C3}

   (sx \".foo\" {:color :red})
   => {:class      \"foo\"
       :data-ks-at my.ns:L11:C3}

   (let [my-map {:class [:baz :bat]}]
     (sx {:color :red}
         {:id :foo}
         my-map)
   => {:class      [\"my_ns__L11_C3\" \"baz\" \"bat\"]
       :id         \"foo\"
       :data-ks-at my.ns:L11:C3}"

  [& args]
  
  (let [validate-sx2-opts
        {:preamble-section-body (bling (hifi (symbol "kushi.core/sx2")))
         :form-meta             (meta &form)}

        ;; Validate arguments against malli specs
        ;; Print warnings via bling.explain/explain-malli
        ;; Return map of fallback values for invalid entry values
        {:keys [fallbacks-for-invalid-entries]}
        (validate-sx2
         &form
         (!? {:find {:pred #(= % :shadow-size)}} schemas/sx2-args)
         validate-sx2-opts)


        ;; Merge fallback values for invalid entries, if applicable
        ;; Only a few kushi shared props have fallbacks for invalid values,
        ;; because if those values are not present, then the rest of the theming 
        ;; may not take effect.
        args
        (map-indexed (fn [i x] 
                       (if (map? x)
                         (merge x (get fallbacks-for-invalid-entries i))
                         x))
                     args)

        ;; Vector of maps or symbols bound to maps
        ;; If map literal (and not symbol) shape of child map will be:
        ;;   {:dynamic-props? false
        ;;    :attrs          {...
        ;;                     :data-whatever ...
        ;;                     :class         ...
        ;;                     :style         ...
        ;;                     ...}}
        attrs-coll*    
        (reduce (fn [acc x]
                  (if-let [ret (cond (symbol? x)
                                     x
                                     (map? x)
                                     (sx2* x &form &env))]
                    (conj acc ret)
                    acc))
                [] 
                args)

        ;; We need to know if there are any dynamic props, so we can do
        ;; additional validation at runtime.
        
        dynamic-props? 
        (boolean (some :dynamic-props? attrs-coll*))

        ;; Vector of attrs maps from the :attrs entries of attrs-coll* 
        attrs-coll     
        (mapv :attrs attrs-coll*)]

    #_(keyed [attrs-coll* dynamic-props? attrs-coll])


    (if dynamic-props?
      ;; validate map at runtime, dev-only
      (let [sx-args (vec (rest &form))]
        `(kushi.core/validator ~attrs-coll 
                               ~sx-args
                               ~validate-sx2-opts))
      `(apply kushi.core/merge-attrs ~attrs-coll))

    #_(if-let [m (when (= 1 (count attrs-coll)) (nth attrs-coll 0 nil))]

        ;; A single map has been passed to sx2
        (if dynamic-props?
          ;; validate map at runtime, dev-only
          `(kushi.core/validator-stub ~&form ~validate-sx2-opts)
          `~attrs-coll)

        ;; Multiple maps have been passed to sx2
        (if dynamic-props?
          ;; validate merged map at runtime, dev-only
          `(kushi.core/validator-stub ~&form ~validate-sx2-opts)
          `~attrs-coll)
        )

    ;; for testing in pure jvm clj env
    #_(if-let [m (when (= 1 (count attrs-coll)) (nth attrs-coll 0 nil))]
        (if dynamic-props?
          `(kushi.core/validator-stub ~m)
          `~m)
        (if dynamic-props?
          `(kushi.core/validator-stub (apply kushi.core/merge-attrs-stub
                                             ~attrs-coll))
          `(apply kushi.core/merge-attrs-stub
                  ~attrs-coll)))))

(defmacro defui3
  [sym m _ body]
  (let [
        ;; mm {:doc "My doc"}
        ]
    #_(? (->> body
            second
            ?
            rest
            ?
            (reduce (fn [acc x]
                      (if-let [ret (cond (symbol? x)
                                         x
                                         (map? x)
                                         (sx2* x &form &env))]
                        (conj acc ret)
                        acc))
                    [])))


      ;; Validation

      ;; 1) Issue warning if stroke prop other than `:stroke-width` is supplied (without stroke-width)
      ;; 2) Same for shadow ^^^

      ;; Still need to do jams at runtime? maybe not as you could mark thing
      ;; Or if yes you could wrap in a runtime-checking function?


    
    ;; Address

    ;; nail down sx semantics - an optional leading string or not? start with not

    ;; sx in body will work, but will give unresolved ns class

    ;; maybe need to walk body and manually supply selector based on value of 
    ;; &form from the defui - something like "ns-where-defui-happened__L11_C44__1"

    ;; Would this work recursively? like if you used a defui within a defui definition?

    ;; Should be able to use most of the stuff from defui wrt defaults 
   
    
    ;; defui questions
    ;;     Can you pass an additional entry to sx that would be pulled out during
    ;;     macro-expansion and then used to augment the baseline schema, for adding
    ;;     component-specific custom props?

    ;; defui basics
    ;; basically construct a spec from the meta-map, and

   `(defn ~sym 
      ~m
      [& args#]
      ~body)
    ))

;; -----------------------------------------------------------------------------
;; sx2 End 
;; -----------------------------------------------------------------------------


;; TODO - maybe dry this up with ?css
(defmacro ^:public ?sx
  "Tapping version of `sx`"
  [& args]
  (let [{:keys [classes class-binding]}
        (classes+class-binding args &form &env)

        expands-to
        (if (seq class-binding) 
          `{:class (kushi.core/class-str ~classes)}
          {:class (string/join " " classes)})]

    (print-css-block (assoc (keyed [args &form &env expands-to]) :sym '?sx))
    (if (seq class-binding) 
      `{:class (kushi.core/class-str ~classes)}
      {:class (string/join " " classes)})))


(defn- css-vars-map*
  "Constructs a style map for assigning locals to css custom properties."
  [args]
  (reduce (fn [acc sym]
            (assoc acc (str "--" sym) sym))
          {}
          args))


(defmacro ^:public css-vars
  "Intended to construct a style str assigning locals to css custom properties.

   Let's say we have a namespace called foo.core, line 100:

   100 | (let [my-var1 \"blue\"
   101 |       my-var2 \"yellow\"]
   102 |   {:style (css-vars my-var1 my-var2)
   103 |    :class (css :c--$my-var1 :bgc--$my-var2)})
   =>
   {:style {\"--my-var1\" my-var1
            \"--my-var2\" my-var1}
    :class \"foo_core__L103_C11\"}
   
   The call to `css` produces the following class in the build's
   watch/analyze/css generation process:

   .foo_core__L103_C11 {
     color: --my-var1;
     background-color: --my-var2;
   }"
  [& args]
  (let [m (css-vars-map* args)]
    `(reduce-kv (fn [acc# k# v#] (str acc# k# ": " v# ";") ) "" ~m)))


(defmacro ^:public css-vars-map
  "Same as `css-vars`, but returns a map instead of a string."
  [& args]
  (css-vars-map* args))



;; -----------------------------------------------------------------------------
;; lightningcss ala-carte POC
;; 
;; When using modern css features such as nesting and color spacs such as okclh,
;; most projects targeting a broader web audience will need to use some degree
;; of post-processing (on authored CSS) to target older browsers. For the
;; typical cljs frontend workflow (assuming Shadow-cljs or Figwheel), a good way
;; to achieve this is to install `lightningcss-cli` locally in the project via
;; the package.json file, then configure a watcher (for dev) to transform the
;; project css that is either hand-written or generated by Kushi (or similar
;; tool).
;;
;; The POC below is an attempt at providing functionality that can be used
;; inline to transform Kushi-generated CSS with lightningcss. This means the
;; benefits of lightningcss can be leveraged ala-carte, without incorporating
;; extra tooling via a build process. One use for this would be generating
;; CSS on the server. Another use case would be generating CSS in JVM clojure or
;; babashka in the context of build systems that currently use a tool such as
;; garden (https://github.com/noprompt/garden).

;; See example usage in docstring of lightning.
;; Docs on lightningcss: https://lightningcss.dev/
;; -----------------------------------------------------------------------------

(def lightning-opts
  {:browserslist               true
   :bundle                     nil
   :css-modules                nil
   :css-modules-dashed-indents nil
   :css-modules-pattern        nil
   :custom-media               nil
   :outdir-dir                 nil
   :error-recovery             nil
   :minify                     true
   :output-file                nil
   :sourcemap                  nil
  ;;  :targets                    "\">= 0.25%\""
  ;;  :targets                    ">= 0.25%"
  ;;  :help                       nil
  ;;  :version                    nil
   })


(defn lightning-cli-flags
  "Returns even-count seq of lightning flags followed by values
   Example output:
   '(\"--flag-name\" \"flag-value\")"
  [opts default-opts]
  (some->> (merge default-opts
                  (when (or (nil? opts) (map? opts))
                    opts))
           (keep (fn [[flag v]] 
                   (when v 
                     [(str "--" (name flag))
                      (when-not (true? v) v)])))
           (apply concat)
           (remove nil?)))


(defn lightning-warning [e css-str flags opts]
  (let [body (bling "Error when shelling out to lightningcss."
                    "\n\n"
                    [:italic.subtle.bold "CSS:"]
                    "\n"
                    css-str
                    "\n\n"
                    [:italic.subtle.bold
                     "Flags passed to lightningcss:\n"]
                    (with-out-str (fireworks.core/pprint flags))
                    "\n\n"
                    [:italic.subtle.bold
                     "The following css will be returned:\n"]
                    css-str)] 
                  (callout
                   (merge opts
                          {:type        :error
                           :label       (str "ERROR: "
                                             (string/replace (type e)
                                                             #"^class "
                                                             "" )
                                             " (Caught)")
                           :padding-top 0})
                   (point-of-interest
                    (merge opts {:type :error
                                 :body body})))))


(defn lightning
  "Transforms a string of CSS using lightningcss. An (optional) user config map
   is merged with kushi.core/lightning-opts, which is transformed into a
   list of flags that are fed to lightningcss.
   
   Assumes that the user has installed lightningcss-cli locally or globally via
   npm. Uses babaska.process/shell to shell out to lightningcss-cli via JVM
   clojure.

   Example:
   ```Clojure
   (ns your-ns.foo
     (:require [kushi.core :refer [css-rule lightning]]))

   (-> (css-rule \".bar\"
                 :c--red
                 :_.bar:c--green)
       lightning)
   ;; => \".foo{color:red}.foo .bar{color:green}\"
   ```

   Another example - same thing but overrides default minification:
   ```Clojure
   (-> (css-rule \".bar\"
                 :c--red
                 :_.bar:c--green)
       (lightning {:minify false}))
   ;; => 
   \".foo {
     color: red;
   }

   .foo .bar {
     color: green;
   }\"
   ```"
  ([css-str]
   (lightning css-str nil))
  ([css-str opts]
   (let [flags (some->> lightning-opts
                        (lightning-cli-flags opts)
                        (into [{:in css-str :out :string}
                               "npx"
                               "lightningcss"]))]

     (or (try (:out (apply shell flags))
              (catch Exception e
                (lightning-warning e css-str flags opts)))
         css-str))))


(defmacro trans
  "Macro for converting from legacy kushi.core/sx to {:class (css ...) ...}
   For internal use in dev environment."
  [coll]
  (let [{:keys [assigned-class
                tokenized-style               
                cssvar-tokenized              
                cssvar-tuple                  
                style-tuple                   
                class                         
                conditional-class             
                sx-attrs-map]}
        (->> coll
             rest
             (s/conform ::specs2/sx-args-conformance)
             (group-by first)
             (reduce-kv (fn [acc k v]
                          (assoc acc k (mapv second v)))
                        {}))

        sx-attrs-map
        (first sx-attrs-map)

        style-map
        (some->> (:style sx-attrs-map)
                 (reduce-kv (fn [acc k v]
                              (assoc acc
                                     (-> k
                                         name
                                         (string/replace #"\&" "")
                                         keyword)
                                     v))
                            {}))

        sx-attrs-map
        (dissoc sx-attrs-map :style)

        class->kw
        (fn [c s]
          (-> c
              name
              (subs 1)
              (->> (str s (if (contains? #{"letter-spacing"
                                           "transition-duration"
                                           "fw"
                                           "fs"}
                                         s)
                            "--$"
                            "--")))
              keyword))

        remove-amp
        #(let [s (-> % name (string/replace #"\&" ""))]
           (if (string? %) s (keyword s)))
        
        css-cp-def 
        (fn [kw]
          (if (-> kw name (string/starts-with? "$"))
            (let [[cp v] (string/split (-> kw name) #"--")]
              [(keyword (string/replace cp #"^\$" "--"))
               (keyword v)]) 
            kw))

        stringify-double-vectors
        #(string/join ", "
                      (map (fn [x]
                             (cond 
                               (vector? x)
                               (string/join " "
                                            (map 
                                             (fn [xx]
                                               (cond
                                                 (symbol? xx)
                                                 (str "var(--" xx ")")

                                                 (keyword? xx)
                                                 (name xx)

                                                 :else
                                                 xx))
                                             x))

                               (symbol? x)
                               (str "var(--" x ")")

                               (keyword? x)
                               (name x)

                               :else x))
                           %))       

        [css-vars-from-style-map style-map-no-css-vars]
        (partition-by-pred (fn [[k _]]
                             (boolean (when (or (string? k) (keyword? k))
                                        (-> k name (string/starts-with? "$")))))
                           style-map)

        style-map-no-css-vars
        (into {} style-map-no-css-vars)

        reformatted
        (remove nil?
                (concat (list (some-> assigned-class
                                      first 
                                      second
                                      name
                                      (->> (str "."))))
                        (for [c class]
                          (cond
                            (s/valid? ::specs/class-kw c)
                            (cond 
                              (contains? #{:.neutral} c)
                              nil

                              (contains? #{:.tracking-xxxtight      
                                           :.tracking-xxtight       
                                           :.tracking-xtight        
                                           :.tracking-tight         
                                           :.tracking-base 
                                           :.tracking-loose         
                                           :.tracking-xloose        
                                           :.tracking-xxloose       
                                           :.tracking-xxxloose}
                                         c)
                              (class->kw c "letter-spacing")

                              (contains? #{:.transition-instant! 
                                           :.transition-xxxfast! 
                                           :.transition-xxfast!  
                                           :.transition-xfast!   
                                           :.transition-fast!    
                                           :.transition-base!
                                           :.transition-slow!    
                                           :.transition-xslow!   
                                           :.transition-xxslow!  
                                           :.transition-xxxslow!}
                                         c)
                              (class->kw c "transition-duration")

                              (contains? #{:.transition-instant 
                                           :.transition-xxxfast 
                                           :.transition-xxfast  
                                           :.transition-xfast   
                                           :.transition-fast    
                                           :.transition-base!
                                           :.transition-slow    
                                           :.transition-xslow   
                                           :.transition-xxslow  
                                           :.transition-xxxslow}
                                         c)
                              (class->kw c "transition-duration")

                              (contains? #{:.weight-thin
                                           :.weight-extra-light
                                           :.weight-light
                                           :.weight-normal
                                           :.weight-wee-bold
                                           :.weight-semi-bold
                                           :.weight-bold
                                           :.weight-extra-bold
                                           :.weight-heavy}
                                         c)
                              (class->kw c "fw")

                              (contains? #{:.size-3xs
                                           :.size-2xs
                                           :.size-xs
                                           :.size-small
                                           :.size-base
                                           :.size-lg
                                           :.size-xl
                                           :.size-2xl
                                           :.size-3xl
                                           :.size-4xl}
                                         c)
                              (class->kw c "fs")

                              (contains? #{:.position-absolute :.position-relative :.position-fixed} c)
                              (class->kw c "position")

                              (contains? #{:.display-block :.display-flex :.display-grid} c)
                              (class->kw c "d")

                              (contains? #{:.cursor-not-allowed :.cursor-pointer} c)
                              (class->kw c "cursor")

                              (contains? #{:.icon-enhanceable} c)
                              :.enhanceable-with-icon

                              :else c)
                            :else
                            c))
                        
                        (for [[p v] style-tuple]
                          [(remove-amp p) 
                           (if (vector? v)
                             (stringify-double-vectors v)
                             (if (symbol? v)
                               (->> v (str "$") keyword)
                               v))])

                        (for [kw tokenized-style]
                          (-> kw
                              remove-amp
                              css-cp-def))
                        
                        conditional-class

                        (when (seq style-map-no-css-vars)
                          [style-map-no-css-vars])))]

     (? {:display-metadata?            false
         :print-length                   100
         :scalar-mapkey-max-length 80
         :scalar-max-length        80}

      (if sx-attrs-map
        (merge (let [convert-cssvar-name
                     #(keyword (string/replace (name %) #"^\$" "--"))

                     style-map           
                     (merge (or (:style sx-attrs-map) {})
                            (reduce (fn [acc kw]
                                      (let [[k v]
                                            (string/split (name kw) #"--")]
                                        (assoc acc (convert-cssvar-name k) v)))
                                    {}
                                    cssvar-tokenized)

                            (reduce (fn [acc [k v]]
                                      (assoc acc (convert-cssvar-name k) v))
                                    {}
                                    cssvar-tuple)

                            (reduce (fn [acc [k v]]
                                      (assoc acc (convert-cssvar-name k) v))
                                    {}
                                    css-vars-from-style-map))]
                 (when (seq style-map) {:style style-map}))
               {:class (cons (symbol "css") reformatted)}
               sx-attrs-map)
        (cons (symbol "sx") reformatted)))))


;; New validate option macro for kushi.ui components
(defmacro validate-option 
  "To be called from inside kushi component definitions that have a metadata map.
   Body code will be optimized away for prod builds, which effectively returns
   nil."
  [f opt]
  `(do (when js/goog.DEBUG 
         (let [runtime-value#  
               ~opt

               quoted-opt-sym# 
               (quote ~opt)

               {opts#    :opts
                ui-name# :name
                ui-ns#   :ns}  
               (meta (var ~f))

               {pred# :pred}   
               (some->> opts#
                        second
                        (filter (fn [m#] (= quoted-opt-sym# (:name m#))))
                        first)
              ;;  err# (atom nil)
               ]
          ;;  (try (throw (js/Error. "some error"))
          ;;       (catch js/Object e# (reset! err# e#)))
           (kushi.core/validate-option*
            {:x       runtime-value# 
             :opt     quoted-opt-sym#
             :pred    pred#    
             :ui-ns   ui-ns#
             :ui-name ui-name#
            ;;  :err     @err#
             })))
       ~opt))

(defmacro at
  "Provides a data-ks-src with source-code coordinates"
  []
  (let [{:keys [file line column]} (meta &form)
        ns-name (-> &env :ns :name)
        ret (str ns-name ":" line ":" column)]
    `~ret))

(defmacro ^:public css-string
  "Used to pull in css that is in a string format. Expands to nil.
   The information about the layer and resource will get included in the build."
  ([s]
   (css-string "@layer user-shared-styles" s))
  ([layer s]
   nil))
