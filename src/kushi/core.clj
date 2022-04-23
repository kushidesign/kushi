(ns ^:dev/always kushi.core
  (:require
   [clojure.spec.alpha :as s]
   [clojure.string :as string]
   [garden.core :as garden]
   [garden.def]
   [garden.stylesheet :refer [at-font-face]]
   [par.core :refer [? !? ?+ !?+]]
   [kushi.arguments :as arguments]
   [kushi.atomic :as atomic]
   [kushi.shorthand :as shorthand]
   [kushi.config :refer [user-config]]
   [kushi.parse :as parse]
   [kushi.printing :as printing]
   [kushi.selector :as selector]
   [kushi.specs :as specs]
   [kushi.cssvarspecs :as cssvarspecs]
   [kushi.state :as state]
   [kushi.theme :as theme]
   [kushi.stylesheet :as stylesheet]
   [kushi.typography :refer [system-font-stacks]]
   [kushi.utils :as util]))

;TODO move this to utils
(defmacro keyed [& ks]
  `(let [keys# (quote ~ks)
         keys# (map keyword keys#)
         vals# (list ~@ks)]
     (zipmap keys# vals#)))

(def KUSHIDEBUG (atom true))

(defn kushi-debug
  {:shadow.build/stage :compile-prepare}
  [build-state]
  #_(?+ {:shadow.build/stage :compile-prepare} "preparing to reset build states...")
  (state/reset-build-states!)
  #_(?+ "After reset...kushi-debug:garden-vecs-state" @state/garden-vecs-state)
  #_(?+ "After reset...kushi-debug:atomic-user-classes" @state/kushi-atomic-user-classes)
  #_(?+ "After reset...kushi-debug:atomic-declarative-classes-used" @state/atomic-declarative-classes-used)
  #_(?+ "After reset...kushi-debug:state/user-defined-keyframes" @state/user-defined-keyframes)
  #_(?+ "After reset...kushi-debug:state/user-defined-font-faces" @state/user-defined-font-faces)

  (let [mode (:shadow.build/mode build-state)]
    #_(when mode
        (?+ (? "(:shadow.build/mode build-state)") mode))
    (when (not= mode :dev)
      (reset! KUSHIDEBUG false)))
  build-state)


(defn style-map->vecs
  [m]
  (let [kw->dotkw #(some->> (when (keyword? %) %)
                            name
                            (str ".")
                            keyword)
        classes*  (some->> m :. util/into-coll)
        classes   (map #(cond (seq? %)
                              (map (fn [x] (if (keyword? x) (kw->dotkw x) x)) %)
                              (keyword? %)
                              (kw->dotkw %))
                       classes*)
        ret (into [] (concat classes (into [] (dissoc m :.))))]
    #_(? (keyed classes* classes ret))
    ret))


(defn- hydrate-literal-css-vars [coll]
  (mapv #(if (s/valid? ::cssvarspecs/tokenized-style-with-css-var %)
           (arguments/style-kw-with-cssvar->tuple %)
           %)
        coll))

(defn- hydrated-defclass
  [classname classtype coll*]
  (let [{:keys [selector
                selector*]} (selector/selector-name
                             {:defclass-name classname
                              :defclass-hash atomic/defclass-hash
                              :atomic-class? (contains? #{:kushi-atomic :defclass-kushi-override} classtype)})
        hydrated-styles*    (parse/with-hydrated-classes coll*)
        hydrated-styles     (hydrate-literal-css-vars hydrated-styles*)
        tokenized-styles    (mapv (partial parse/kushi-style->token selector*) hydrated-styles)
        grouped-by-mqs      (parse/grouped-by-mqs tokenized-styles)
        garden-vecs         (parse/garden-vecs grouped-by-mqs selector)
        ret                 (keyed selector selector*
                                   hydrated-styles
                                   garden-vecs)]
    (!?+ (keyed hydrated-styles tokenized-styles grouped-by-mqs garden-vecs ret))
    ret))


(defn defclass* [sym* coll* form-meta]
  (let [sym                      (if (keyword? sym*) (symbol sym*) sym*)
        classtype                (cond
                                   (keyword? sym*)               :defclass-kushi-override
                                   (-> sym meta :kushi)          :kushi-atomic
                                   (-> sym meta :kushi-override) :defclass-kushi-override
                                   (-> sym meta :override)       :defclass-user-override
                                   :else                         :defclass)
        defclass-name            (keyword sym)
        last*                    (last coll*)
        style-map                (when (map? last*) last*)
        style-tokens             (if style-map (drop-last coll*) coll*)
        style-map-vecs           (some-> style-map style-map->vecs)
        coll                     (concat style-tokens style-map-vecs)
        {:keys
         [valid-styles-from-attrs
          valid-styles-from-tokens
          invalid-style-args]}     (arguments/validate-args coll*  style-tokens  {:style style-map})

        invalid-args             (or
                                  (when-not (s/valid? ::specs/defclass-name sym) ^:classname [sym])
                                  invalid-style-args)
        styles                   (into [] (concat valid-styles-from-tokens valid-styles-from-attrs))
        {:keys [selector
                selector*
                hydrated-styles
                garden-vecs]}    (hydrated-defclass defclass-name classtype styles)
        styles-argument-display  (apply vector coll)
        console-warning-args     {:defclass-name           defclass-name
                                  :styles-argument-display styles-argument-display
                                  :invalid-args            invalid-args
                                  :form-meta               form-meta
                                  :fname                   "defclass"}
        m                        {:n             defclass-name
                                  :selector      selector
                                  :selector*     selector*
                                  :args          hydrated-styles
                                  :garden-vecs   garden-vecs
                                  :__classtype__ classtype}]

    (!?+ (keyed last* style-map style-tokens style-map-vecs coll))
    ;; (?+ styles)

    {:defclass-name        defclass-name
     :coll                 coll
     :invalid-args         invalid-args
     :hydrated-styles      hydrated-styles
     :console-warning-args console-warning-args
     :m                    m}))

(defn defclass-noop? [sym coll*]
  ;; For skipping defclasses & overrides from theming
  (and (nil? sym) (= coll* '(nil))))

(defmacro defclass
  [sym & coll*]
  (when-not (defclass-noop? sym coll*)
    (let [form-meta             (meta &form)
          dupe-defclass-warning (printing/dupe-defclass-warning
                                 {:fname "defclass"
                                  :nm (if (keyword? sym) (symbol sym) sym)
                                  :form-meta form-meta})]

      (printing/print-dupe2! dupe-defclass-warning)
      (reset! state/current-macro :defclass)

      (let [{:keys [caching?
                    cache-key
                    cached]}             (state/cached :defclass sym coll*)
            {:keys [defclass-name
                    coll
                    invalid-args
                    console-warning-args
                    m]
             :as result}                 (or cached (defclass* sym coll* (meta &form)))
            {:keys [garden-vecs
                    selector
                    __classtype__]}      m
            js-args-warning              (printing/preformat-js-warning console-warning-args)
            garden-vecs-for-shared       (stylesheet/garden-vecs-injection garden-vecs)
            inject?                      (:runtime-injection? user-config)]

        (printing/ansi-bad-args-warning console-warning-args)

      ;; Put atomic class into global registry
        (swap! state/kushi-atomic-user-classes assoc defclass-name m)
        (swap! state/ordered-defclasses conj (:n m))

        (printing/diagnostics :defclass {:defclass-map m
                                         :args         coll
                                         :sym          sym})

        (when caching?
          (swap! state/styles-cache-updated assoc cache-key result))

       ;; Dev-only runtime code for potential warnings (and possible dynamic injection.)
        (if @KUSHIDEBUG
          `(do
            ;; Inject all defclasses for dev, even if they are not used.
            ;; This makes them available for testing / sampling in browser devtools.
             (kushi.core/inject-css* (quote ~garden-vecs-for-shared)
                                     (~__classtype__ kushi.sheets/sheet-ids-by-type))
             (let [logfn# (fn [f# js-array#] (.apply js/console.warn js/console (f# js-array#)))]
               (when (seq ~invalid-args) (logfn# cljs.core/to-array ~js-args-warning))
               (when ~dupe-defclass-warning
                 (logfn# cljs.core/to-array (:browser ~dupe-defclass-warning))))
             nil)
          `(do
             (when ~inject?
               (kushi.core/inject-css* (quote ~garden-vecs-for-shared)
                                       (~__classtype__ kushi.sheets/sheet-ids-by-type)))
             nil))))))

(defmacro add-font-face
  "Example:
   (add-font-face {:font-family \"FiraCodeBold\"
                   :font-weight \"Bold\"
                   :font-style \"Normal\"
                   :src [\"local(\\\"Fira Code Bold\\\")\"]})"
  [m]
  (let [{:keys [caching? cache-key cached]} (state/cached :add-font-face m)
        aff                                 (vector (or cached (garden/css (at-font-face m))))]
    (reset! state/current-macro :add-font-face)
    (swap! state/user-defined-font-faces conj aff)
    (when (and caching? (not cached))
      (swap! state/styles-cache-updated assoc cache-key aff))
    (when (or @KUSHIDEBUG (:runtime-injection? user-config))
      `(do
         (kushi.core/inject-css* ~aff "_kushi-rules-shared_")
         nil))))

(defn system-at-font-face-rules [weights*]
  (let [weights   (if (empty? weights*)
                    system-font-stacks
                    (reduce (fn [acc v]
                              (if (contains? system-font-stacks v)
                                (assoc acc v (get system-font-stacks v))
                                acc))
                            {}
                            weights*))
        ff-rules* (for [[weight fonts-by-style] weights]
                    (for [[style fonts] fonts-by-style]
                      (garden/css
                       (at-font-face
                        {:font-family "system"
                         :font-style (name style)
                         :font-weight weight
                         :src (mapv #(str "local(\"" % "\")") fonts)}))))
        ff-rules (apply concat ff-rules*)]
    ff-rules))

(defmacro add-system-font-stack
  [& weights*]
  (let [{:keys [caching? cache-key cached]} (state/cached :system-font-stack weights*)
        ff-rules (into [] (or cached (system-at-font-face-rules weights*)))]
    (doseq [rule ff-rules]
      (reset! state/current-macro :add-font-face)
      (swap! state/user-defined-font-faces conj rule))
    (when (and caching? (not cached))
      (swap! state/styles-cache-updated assoc cache-key ff-rules))
    (when (or @KUSHIDEBUG (:runtime-injection? user-config))
      `(do
         (kushi.core/inject-css* ~ff-rules "_kushi-rules-shared_")
         nil))
    ;; (if @KUSHIDEBUG
    ;;   `(do
    ;;      (kushi.core/inject-css* ~ff-rules "_kushi-rules-shared_")
    ;;      nil)
    ;;   `(do nil))
    ))

(defn- keyframe [[k v]]
  (let [frame-key (if (vector? k)
                    (string/join ", " (map name k))
                    (string/replace (name k) #"\|" ","))
        frame-val (reduce
                   (fn [acc [key val]]
                     (assoc acc key (if (util/cssfn? val) (util/cssfn val) val)))
                   {}
                   v)]
    [frame-key frame-val]))

(defmacro defkeyframes [nm & frames*]
  (let [opts         {:fname     "defkeyframes"
                      :nm        nm
                      :form-meta (meta &form)}
        dupe-warning (printing/dupe-defkeyframes-warning opts)]
    (printing/print-dupe2! dupe-warning)
    (reset! state/current-macro :defkeyframes)
    (let [{:keys [caching?
                  cache-key
                  cached]}  (state/cached :keyframes nm frames*)
          frames            (or cached (mapv keyframe frames*))
          css-inj           (vector (stylesheet/defkeyframes->css [nm frames]))]
      (swap! state/user-defined-keyframes assoc (keyword nm) frames)
      (when (and caching? (not cached))
        (swap! state/styles-cache-updated assoc cache-key frames))

      (when (or @KUSHIDEBUG (:runtime-injection? user-config))
        `(do
           (let [logfn# (fn [f# js-array#] (.apply js/console.warn js/console (f# js-array#)))]
             (when ~dupe-warning
               (logfn# cljs.core/to-array (:browser ~dupe-warning))))
           (kushi.core/inject-css* ~css-inj "_kushi-rules-shared_")
           nil)))))

(defn cssfn [& args]
  (cons 'cssfn (list args)))


(defmacro sx
  [& args*]
  (when-not (= args* '(nil))
    (let [form-meta                   (meta &form)
          {:keys [caching?
                  cache-key
                  cached]}            (state/cached :sx args*)
          {:keys [prefixed-classlist
                  attrs-base
                  kushi-attr
                  css-vars
                  garden-vecs
                  data-cljs
                  inj-type
                  element-style-inj
                  shared-styles-inj]
           :as new-args}              (or cached (arguments/new-args (keyed args* form-meta cache-key)))
          _                           (printing/set-warnings!)
          warnings-js                 @state/warnings-js]

      (when caching?
        #_(when cached (swap! state/cached-sx-rule-count inc))
        (when-not cached
          (swap! state/styles-cache-updated assoc cache-key new-args)))

      (state/add-styles! garden-vecs inj-type)

      (printing/print-warnings!)

      #_(when true
        (? :sx
           (keyed
            kushi-attr
            attrs-base
            shared-styles-inj
            css-vars
            garden-vecs
            data-cljs
            prefixed-classlist)))

    ;;TODO - fix injection mode
      (if @KUSHIDEBUG
       ;; dev builds ----------------------------------------------------
       ;; TODO move cljs.core/to-array inside fn and rename js-array# ?
        `(let [logfn# (fn [f# js-array#]
                        (.apply js/console.warn js/console (f# js-array#)))]
           (do
             (when ~warnings-js
               (doseq [warning# ~warnings-js]
                 (logfn# cljs.core/to-array warning#)))
             (kushi.core/inject-style-rules (quote ~element-style-inj) ~inj-type)
             (kushi.core/inject-kushi-atomics ~shared-styles-inj)

           ;; return attributes map for the html element
             (kushi.core/merged-attrs-map
              ~attrs-base
              ~prefixed-classlist
              ~css-vars
              ~data-cljs)))

       ;; release builds -----------------------------------------------
        (if (:runtime-injection? user-config)
          `(do
             (kushi.core/inject-style-rules (quote ~element-style-inj) ~inj-type)
             (kushi.core/inject-kushi-atomics ~shared-styles-inj)
             (kushi.core/merged-attrs-map
              ~attrs-base
              ~prefixed-classlist
              ~css-vars))
          `(do
             (kushi.core/merged-attrs-map
              ~attrs-base
              ~prefixed-classlist
              ~css-vars)))))))

;; For generating sx-theme! destructuring vecs at repl
#_(? (mapv (fn [n] [(symbol (str "c" n)) (symbol (str "c" n "m"))]) (range 1 45)))
#_(? (mapv (fn [n] (list 'kushi.core/defclass (symbol (str "~c" n)) (symbol (str "~c" n "m")))) (range 1 45)))
#_(? (mapv (fn [n] (symbol (str "m" n))) (range 1 45)))
#_(? (mapv (fn [n] (list 'kushi.core/sx (symbol (str "~m" n)))) (range 1 45)))

#_(defmacro ui-components!)

(defmacro theme! []
  (let [{:keys [styles used-toks global-toks alias-toks overrides]} (theme/theme-by-compo theme/base-theme)
        [[c1 c1m]
         [c2 c2m]
         [c3 c3m]
         [c4 c4m]
         [c5 c5m]
         [c6 c6m]
         [c7 c7m]
         [c8 c8m]
         [c9 c9m]
         [c10 c10m]
         [c11 c11m]
         [c12 c12m]
         [c13 c13m]
         [c14 c14m]
         [c15 c15m]
         [c16 c16m]
         [c17 c17m]
         [c18 c18m]
         [c19 c19m]
         [c20 c20m]
         [c21 c21m]
         [c22 c22m]
         [c23 c23m]
         [c24 c24m]
         [c25 c25m]
         [c26 c26m]
         [c27 c27m]
         [c28 c28m]
         [c29 c29m]
         [c30 c30m]
         [c31 c31m]
         [c32 c32m]
         [c33 c33m]
         [c34 c34m]
         [c35 c35m]
         [c36 c36m]
         [c37 c37m]
         [c38 c38m]
         [c39 c39m]
         [c40 c40m]
         [c41 c41m]
         [c42 c42m]
         [c43 c43m]
         [c44 c44m]] (into [] overrides)
        [m1
         m2
         m3
         m4
         m5
         m6
         m7
         m8
         m9
         m10
         m11
         m12
         m13
         m14
         m15
         m16
         m17
         m18
         m19
         m20
         m21
         m22
         m23
         m24
         m25
         m26
         m27
         m28
         m29
         m30
         m31
         m32
         m33
         m34
         m35
         m36
         m37
         m38
         m39
         m40
         m41
         m42
         m43
         m44] styles
        kushi-debug   @KUSHIDEBUG
        rt-injection? (:runtime-injection? user-config)
        global-tokens-to-inject (!?+ (stylesheet/design-tokens-css {:toks global-toks :pretty-print? true}))
        alias-tokens-to-inject (!?+ (stylesheet/design-tokens-css {:toks alias-toks :pretty-print? true}))]
    ;; (?+ used-toks)
    ;; (?+ alias-toks)
    ;; (?+ css-tokens-actually-used)
    (doseq [tok global-toks] (state/add-global-token! tok))
    (doseq [tok alias-toks] (state/add-alias-token! tok))
    (doseq [tok used-toks] (state/add-used-token! tok))
    (!?+ @state/global-tokens)
    `(do
       (kushi.core/defclass ~c1 ~c1m)
       (kushi.core/defclass ~c2 ~c2m)
       (kushi.core/defclass ~c3 ~c3m)
       (kushi.core/defclass ~c4 ~c4m)
       (kushi.core/defclass ~c5 ~c5m)
       (kushi.core/defclass ~c6 ~c6m)
       (kushi.core/defclass ~c7 ~c7m)
       (kushi.core/defclass ~c8 ~c8m)
       (kushi.core/defclass ~c9 ~c9m)
       (kushi.core/defclass ~c10 ~c10m)
       (kushi.core/defclass ~c11 ~c11m)
       (kushi.core/defclass ~c12 ~c12m)
       (kushi.core/defclass ~c13 ~c13m)
       (kushi.core/defclass ~c14 ~c14m)
       (kushi.core/defclass ~c15 ~c15m)
       (kushi.core/defclass ~c16 ~c16m)
       (kushi.core/defclass ~c17 ~c17m)
       (kushi.core/defclass ~c18 ~c18m)
       (kushi.core/defclass ~c19 ~c19m)
       (kushi.core/defclass ~c20 ~c20m)
       (kushi.core/defclass ~c21 ~c21m)
       (kushi.core/defclass ~c22 ~c22m)
       (kushi.core/defclass ~c23 ~c23m)
       (kushi.core/defclass ~c24 ~c24m)
       (kushi.core/defclass ~c25 ~c25m)
       (kushi.core/defclass ~c26 ~c26m)
       (kushi.core/defclass ~c27 ~c27m)
       (kushi.core/defclass ~c28 ~c28m)
       (kushi.core/defclass ~c29 ~c29m)
       (kushi.core/defclass ~c30 ~c30m)
       (kushi.core/defclass ~c31 ~c31m)
       (kushi.core/defclass ~c32 ~c32m)
       (kushi.core/defclass ~c33 ~c33m)
       (kushi.core/defclass ~c34 ~c34m)
       (kushi.core/defclass ~c35 ~c35m)
       (kushi.core/defclass ~c36 ~c36m)
       (kushi.core/defclass ~c37 ~c37m)
       (kushi.core/defclass ~c38 ~c38m)
       (kushi.core/defclass ~c39 ~c39m)
       (kushi.core/defclass ~c40 ~c40m)
       (kushi.core/defclass ~c41 ~c41m)
       (kushi.core/defclass ~c42 ~c42m)
       (kushi.core/defclass ~c43 ~c43m)
       (kushi.core/defclass ~c44 ~c44m)

       (kushi.core/sx ~m1)
       (kushi.core/sx ~m2)
       (kushi.core/sx ~m3)
       (kushi.core/sx ~m4)
       (kushi.core/sx ~m5)
       (kushi.core/sx ~m6)
       (kushi.core/sx ~m7)
       (kushi.core/sx ~m8)
       (kushi.core/sx ~m9)
       (kushi.core/sx ~m10)
       (kushi.core/sx ~m11)
       (kushi.core/sx ~m12)
       (kushi.core/sx ~m13)
       (kushi.core/sx ~m14)
       (kushi.core/sx ~m15)
       (kushi.core/sx ~m16)
       (kushi.core/sx ~m17)
       (kushi.core/sx ~m18)
       (kushi.core/sx ~m19)
       (kushi.core/sx ~m20)
       (kushi.core/sx ~m21)
       (kushi.core/sx ~m22)
       (kushi.core/sx ~m23)
       (kushi.core/sx ~m24)
       (kushi.core/sx ~m25)
       (kushi.core/sx ~m26)
       (kushi.core/sx ~m27)
       (kushi.core/sx ~m28)
       (kushi.core/sx ~m29)
       (kushi.core/sx ~m30)
       (kushi.core/sx ~m31)
       (kushi.core/sx ~m32)
       (kushi.core/sx ~m33)
       (kushi.core/sx ~m34)
       (kushi.core/sx ~m35)
       (kushi.core/sx ~m36)
       (kushi.core/sx ~m37)
       (kushi.core/sx ~m38)
       (kushi.core/sx ~m39)
       (kushi.core/sx ~m40)
       (kushi.core/sx ~m41)
       (kushi.core/sx ~m42)
       (kushi.core/sx ~m43)
       (kushi.core/sx ~m44)

       
       #_(when (or ~kushi-debug ~rt-injection?)
         (kushi.core/inject-design-tokens! ~global-tokens-to-inject :global-tokens)
         (kushi.core/inject-design-tokens! ~alias-tokens-to-inject :alias-tokens)))))



