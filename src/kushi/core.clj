(ns ^:dev/always kushi.core
 (:require
  [io.aviso.ansi :as ansi]
  [clojure.spec.alpha :as s]
  [clojure.string :as string]
  [clojure.set :as set]
  [clojure.pprint :refer [pprint]]
  [garden.core :as garden]
  [garden.def]
  [garden.stylesheet :refer [at-font-face]]
  [kushi.atomic :as atomic]
  [kushi.ansiformat :as ansiformat]
  [kushi.config :refer [user-config]]
  [kushi.parse :as parse]
  [kushi.printing :as printing]
  [kushi.selector :as selector]
  [kushi.specs :as specs]
  [kushi.state :as state]
  [kushi.stylesheet :as stylesheet]
  [kushi.typography :refer [system-font-stacks]]
  [kushi.utils :as util :refer [? keyed]]))

(def KUSHIDEBUG (atom true))

(defn kushi-debug
  {:shadow.build/stage :compile-prepare}
  [build-state]
  #_(println {:shadow.build/stage :compile-prepare} "preparing to reset build states...")
  (state/reset-build-states!)
  #_(util/pprint+
   {"After reset: kushi-debug:garden-vecs-state" @state/garden-vecs-state
    "kushi-debug:atomic-user-classes" @state/kushi-atomic-user-classes
    "kushi-debug:atomic-declarative-classes-used" @state/atomic-declarative-classes-used
    "kushi-debug:state/user-defined-keyframes" @state/user-defined-keyframes
    "kushi-debug:state/user-defined-font-faces" @state/user-defined-font-faces})

  (let [mode (:shadow.build/mode build-state)]
    #_(when mode
        (println "(:shadow.build/mode build-state) =>" mode))
    (when (not= mode :dev)
      (reset! KUSHIDEBUG false)))
  build-state)


(defn style-map->vecs
  [m]
  (when (and (:map-mode? user-config) (map? m))
    (let [->coll  #(if (coll? %) % [%])
          classes (some->> m :. ->coll (map #(->> % name (str ".") keyword)))]
      (into [] (concat classes (into [] (dissoc m :.)))))))

(defn- scoped-atomic-classname
  "Returns a classname with proper prefixing for scoping.
   Returns an uscoped classname for class not in global registry.

   Example with a user-defined class:
   (defclass myclass :c--red)
   (scoped-atomic-classname {} {:atomic :myclass}) ;=> \"_o25757__myclass\"

   Example with a kushi pre-defined class:
   (scoped-atomic-classname {} {:atomic :flex-row-c}) ;=> \"_o25757_flex-row-c\""
  [meta kw]
  (some-> (assoc meta :defclass-name kw :defclass-hash atomic/defclass-hash)
          selector/selector-name
          :selector*))

(defn- atomic-classes
  [meta ks]
  (remove nil?
          (mapv (partial scoped-atomic-classname meta) ks)))

(defn- hydrated-defclass
  [classname coll*]
  (let [{:keys [selector
                selector*]} (selector/selector-name
                             {:defclass-name classname
                              :defclass-hash atomic/defclass-hash})
        hydrated-styles              (parse/with-hydrated-classes coll*)
        tokenized-styles             (mapv (partial parse/kushi-style->token selector*) hydrated-styles)
        grouped-by-mqs               (parse/grouped-by-mqs tokenized-styles)
        garden-vecs                  (parse/garden-vecs grouped-by-mqs selector)
        ret                          (keyed selector
                                            selector*
                                            hydrated-styles
                                            garden-vecs)]
    #_(? 'ret ret)
    ret))

(defn defclass* [sym coll* form-meta]
  (let [defclass-name            (keyword sym)
        coll                     (if (:map-mode? user-config)
                                   (-> coll* first style-map->vecs)
                                   coll*)
        {invalid-map-args
         :invalid}               (when (:map-mode? user-config) (util/reduce-by-pred map? coll*))
        {styles        :valid
         invalid-args* :invalid} (util/reduce-by-pred #(s/valid? ::specs/defclass-arg %) coll)
        invalid-args             (or
                                  (when-not (s/valid? ::specs/defclass-name sym) ^:classname [sym])
                                  (into [] (concat invalid-map-args invalid-args*)))
        {:keys [selector
                selector*
                hydrated-styles
                garden-vecs]}    (hydrated-defclass defclass-name styles)
        styles-argument-display  (if (:map-mode? user-config)
                                   coll*
                                   (apply vector coll))
        console-warning-args     {:defclass-name           defclass-name
                                  :styles-argument-display styles-argument-display
                                  :invalid-args            invalid-args
                                  :form-meta               form-meta
                                  :fname                   "defclass"}
        m                        {:n           defclass-name
                                  :selector    selector
                                  :selector*   selector*
                                  :args        hydrated-styles
                                  :garden-vecs garden-vecs}]

    #_(? (keyed invalid-map-args
              invalid-args
              styles
              m))

    {:defclass-name        defclass-name
     :coll                 coll
     :invalid-args         invalid-args
     :hydrated-styles      hydrated-styles
     :console-warning-args console-warning-args
     :m                    m}))

(defmacro defclass
  [sym & coll*]
  (let [dupe-defclass-warning (printing/dupe-defclass-warning
                               {:fname "defclass"
                                :nm sym
                                :form-meta (meta &form)})]

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
                  selector]}           m
          js-args-warning              (printing/preformat-js-warning console-warning-args)
          garden-vecs-for-shared       (stylesheet/garden-vecs-injection garden-vecs)
          inject?                      (:runtime-injection? user-config)]

        (printing/ansi-bad-args-warning console-warning-args)

        ;; Put atomic class into global registry
        (swap! state/kushi-atomic-user-classes assoc defclass-name m)

        (printing/diagnostics :defclass {:defclass-map m
                                         :args         coll
                                         :sym          sym})

        (when caching? (swap! state/styles-cache-updated assoc cache-key result))

        ;; Dev-only runtime code for potential warnings (and possible dynamic injection.)
        (if @KUSHIDEBUG
          `(do
            (kushi.core/inject-css* (quote ~garden-vecs-for-shared) ~selector "_kushi-rules-shared_")
             (let [logfn# (fn [f# js-array#] (.apply js/console.warn js/console (f# js-array#)))]
               (when (seq ~invalid-args) (logfn# cljs.core/to-array ~js-args-warning))
               (when ~dupe-defclass-warning
                 (logfn# cljs.core/to-array (:browser ~dupe-defclass-warning))))
             nil)
          `(do
             (when ~inject?
               (kushi.core/inject-css* (quote ~garden-vecs-for-shared) ~selector "_kushi-rules-shared_"))
             nil)))))

(defmacro clean!
  "Removes all existing styles that were injected into
   #_kushi-rules-shared_ or #_kushi-rules_ style tags at dev time.
   Intended to be called by the projects main/core ns on every save/reload."
  []
  (let [log?    (:log-clean!? user-config)
        inject? (:runtime-injection? user-config)]
    `(when (or ^boolean js/goog.DEBUG
               ~inject?)
       ;;TODO make cleaning report more detailed
       (for [sheet-id# ["_kushi-rules-shared_" "_kushi-rules_"]]
         (do (when ~log? (js/console.log (str "[kushi.core/clean!] stylesheet #" sheet-id#)))
             (let [sheet# (.-sheet (js/document.getElementById sheet-id#))
                   rules# (.-rules sheet#)
                   rules-len# (.-length rules#)]
               (clojure.core/doseq [idx# (clojure.core/reverse (clojure.core/range rules-len#))]
                 (.deleteRule sheet# idx#))))))))

(defmacro add-font-face
  "Example:
   (add-font-face {:font-family \"FiraCodeBold\"
                   :font-weight \"Bold\"
                   :font-style \"Normal\"
                   :src [\"local(\\\"Fira Code Bold\\\")\"]})"
  [m]
  (let [{:keys [caching? cache-key cached]} (state/cached :add-font-face m)
        aff (or cached (garden/css (at-font-face m)))]
    (reset! state/current-macro :add-font-face)
    (swap! state/user-defined-font-faces conj aff)
    (when (and caching? (not cached))
      (swap! state/styles-cache-updated assoc cache-key aff))
    nil))


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
        ff-rules (or cached (system-at-font-face-rules weights*))]
    (doseq [rule ff-rules]
      (reset! state/current-macro :add-font-face)
      (swap! state/user-defined-font-faces conj rule))
    (when (and caching? (not cached))
      (swap! state/styles-cache-updated assoc cache-key ff-rules))
    nil))

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
  (let [opts {:fname "defkeyframes" :nm nm :form-meta (meta &form)}
        dupe-defkeyframes-warning (printing/dupe-defkeyframes-warning opts)]
   (printing/print-dupe2! dupe-defkeyframes-warning)
   (reset! state/current-macro :defkeyframes)
   (let [{:keys [caching? cache-key cached]} (state/cached :keyframes nm frames*)
         frames (or cached (mapv keyframe frames*))]
     (swap! state/user-defined-keyframes assoc (keyword nm) frames)
     (when (and caching? (not cached))
       (swap! state/styles-cache-updated assoc cache-key frames))
     #_nil
     (if @KUSHIDEBUG
       `(do
          (let [logfn# (fn [f# js-array#] (.apply js/console.warn js/console (f# js-array#)))]
            (when ~dupe-defkeyframes-warning
              (logfn# cljs.core/to-array (:browser ~dupe-defkeyframes-warning))))
          nil)
       `(do nil)))))

(defn cssfn [& args]
  (cons 'cssfn (list args)))

(defn- style&classes+attr [args]
  (if (:map-mode? user-config)
    (let [only-attr?       (s/valid? ::specs/map-mode-only-attr args)
          only-style?      (s/valid? ::specs/map-mode-only-style args)
          style+attr?      (s/valid? ::specs/map-mode-style+attr args)
          [style attr]     (cond only-style? [(first args) nil]
                                 style+attr? args
                                 only-attr?  [nil (first args)])
          invalid-map-args (remove nil? (map-indexed (fn [idx v] (when-not (and (map? v) (< idx 2)) v)) args))]

      #_(? "style&classes+attr"
         (keyed only-attr?
                only-style?
                style+attr?
                invalid-map-args
                style
                attr))

      {:styles+classes   style
       :attr             attr
       :invalid-map-args invalid-map-args})
    (if (map? (last args))
      {:styles+classes (drop-last args) :attr (last args)}
      {:styles+classes args :attr nil})))

(def meta-ks [:ancestor :prefix :ident :element :data-attr-name])

(defn- parse-attr+meta [args]
  (let [{styles+classes*
         :styles+classes
         attr*
         :attr
         invalid-map-args
         :invalid-map-args}        (style&classes+attr args)
        {ident        :ident
         :as          kushi-attr}  (select-keys attr* meta-ks)
        data-attr-name             (or (:data-attr-name user-config) :data-cljs)
        attr                       (apply dissoc attr* meta-ks)
        styles+classes             (if (:map-mode? user-config) (style-map->vecs styles+classes*) styles+classes*)
        {:keys [valid invalid]}    (util/reduce-by-pred #(s/valid? ::specs/kushi-arg %) styles+classes)
        {classes* :valid
         styles*  :invalid}        (util/reduce-by-pred #(s/valid? ::specs/kushi-class-like %) valid)
        {classes-with-mods :valid} (util/reduce-by-pred #(s/valid? ::specs/kushi-dot-class-with-mods %) classes*)
        classes-with-mods-hydrated (parse/with-hydrated-classes classes-with-mods)
        styles                     (into [] (concat styles* classes-with-mods-hydrated))
        invalid-args               (into [] (concat invalid-map-args invalid))]

    #_(? "parse-attr+meta"
       (keyed
        attr*
        attr
        kushi-attr
        styles+classes*
        styles+classes
        styles*
        classes*
        ident
        data-attr-name
        invalid-map-args
        invalid-args))

    {:attr           attr
     :kushi-attr     kushi-attr
     :styles+classes styles+classes
     :styles*        styles
     :classes*       classes*
     :ident          ident
     :data-attr-name data-attr-name
     :invalid-args   invalid-args}))

(defn classlist [meta classes* selector*]
  #_(? (keyed meta classes* selector*))
  (let [non-conditional-classes                    (filter #(not (seq? %)) classes*)
        {:keys [conditional-class-sexprs classes]} (parse/parse-classes classes*)
        {atomic-class-keys :valid
         other-keys        :invalid}               (util/reduce-by-pred util/starts-with-dot? classes)
        non-conditional-atomic-class-keys          (set/intersection
                                                    (into #{} atomic-class-keys)
                                                    (into #{} non-conditional-classes))
        atomic-classes                             (atomic-classes meta (map
                                                                         util/normalized-class-kw
                                                                         non-conditional-atomic-class-keys))
        classlist                                  (concat atomic-classes [selector*] (map name other-keys))]

    ;; TODO what is difference between classes and atomic-class-keys?

    #_(? (keyed
        non-conditional-classes
        classes
        non-conditional-atomic-class-keys
        atomic-classes
        classlist
        atomic-class-keys
        conditional-class-sexprs))
    (keyed
     classlist
     atomic-class-keys
     conditional-class-sexprs)))

(defn sx* [args]
  (let [{:keys [caching? cache-key cached]} (state/cached :defclass args)]

    #_(util/pprint+
       (str "(get\n  [:sx\n   " 'user-config-args-sx-defclass "\n   "  args "]\n   nil)")
       cached)

    (or cached
        (let [{:keys [styles*
                      classes*
                      invalid-args
                      attr
                      kushi-attr
                      ident
                      data-attr-name]}     (parse-attr+meta args)
              ; TODO sort out prefix
              {:keys [selector selector*]} (selector/selector-name kushi-attr)
              classlist-map                (classlist kushi-attr classes* selector*)
              styles                       (parse/+vars styles* selector*)
              css-vars                     (parse/css-vars styles* selector*)
              tokenized-styles             (mapv (partial parse/kushi-style->token selector*) styles)
              grouped-by-mqs               (parse/grouped-by-mqs tokenized-styles)
              garden-vecs                  (parse/garden-vecs grouped-by-mqs selector)
              attr-base                    (or attr {})
              ret                          (merge
                                            classlist-map
                                            (keyed garden-vecs
                                                   attr
                                                   attr-base
                                                   css-vars
                                                   kushi-attr
                                                   ident
                                                   invalid-args
                                                   data-attr-name
                                                   selector))]

          #_(? "sx*"
               (keyed
                selector*
                selector
                kushi-attr
                classlist-map
                styles*
                styles
                css-vars
                      tokenized-styles
                      grouped-by-mqs
                      garden-vecs
                ))

          (when caching?
            (swap! state/styles-cache-updated assoc cache-key ret))
          ret))))

(defn- only-attr
  [args]
  (let [selected                (:select-ns user-config)
        select-ns-vector-valid? (s/valid? ::specs/select-ns-vector selected)]
    (when select-ns-vector-valid?
      (let [current-ns-sym       (symbol (.toString *ns*))
            current-ns?          (contains? (into #{} selected) current-ns-sym)
            current-ns-ancestor? (some? (some #(re-find
                                                (re-pattern (str %))
                                                (str current-ns-sym))
                                              selected))]
        (when-not (or current-ns-ancestor? current-ns?)
          (let [attr (or (-> args style&classes+attr :attr) {})]
            (apply dissoc attr meta-ks)))))))

;; TODO move all lines with trailing ;print comment into kushi.printing or kushi.reporting
(defmacro sx
  [& args]
  (reset! state/current-macro :sx)
  (reset! state/current-sx {:form-meta (meta &form) :args args :bad-mods {} :fname "sx"})
  (or
   (only-attr args)
   (let [{:keys [atomic-class-keys
                 garden-vecs
                 attr
                 attr-base
                 classlist
                 conditional-class-sexprs
                 css-vars
                 ident
                 invalid-args
                 data-attr-name
                 selector]
          :as   m}               (sx* args)
         printing-opts           (assoc m :form-meta (meta &form) :fname "sx")
         dupe-ident-warning      (printing/dupe-ident-warning printing-opts)
         _                       (printing/print-dupe2! (merge dupe-ident-warning printing-opts))
         styles-argument-display (apply vector args)
         compilation-warnings    (printing/compilation-warnings-coll printing-opts) ;print
         compilation-warnings-js (printing/preformat-compilation-warnings-js compilation-warnings) ;print
         invalid-warning-args    {:invalid-args            invalid-args
                                  :styles-argument-display styles-argument-display
                                  :form-meta               (meta &form)
                                  :fname                   "sx"}
         css-injection           (stylesheet/garden-vecs-injection garden-vecs)
         atomic-used?            #(contains? @state/atomic-declarative-classes-used %)
        ;;  _ (? (keyed atomic-class-keys ident))

        ;; Issue warning if class not found in registry
         shared-classlist        (into
                                  []
                                  (remove
                                   nil?
                                   (doall
                                    (for [k* atomic-class-keys
                                          :let [k (keyword (subs (name k*) 1))
                                                {:keys [selector selector*]} (-> {:defclass-name k} selector/selector-name)]]
                                      (keyed k selector* selector)))))
         inject?                 (:runtime-injection? user-config)
         og-cls                  (:class attr)
         cls                     (when og-cls (if (coll? og-cls) og-cls [og-cls]))
         data-cljs               (let [{:keys [file line column]} (meta &form)]
                                   (str file ":"  line ":" column))
         js-args-warning         (printing/preformat-js-warning invalid-warning-args) ;print
         bad-mods-warning        (printing/bad-mods-warning @state/current-sx) ;print
         bad-mods-warning-js*    (printing/bad-mods-warning (assoc @state/current-sx :js? true)) ;print
         bad-mods-warning-js     (printing/bad-mods-warning-js bad-mods-warning-js*)
         kushi-atomics*          (select-keys atomic/kushi-atomic-combo-classes (mapv :k shared-classlist))
         kushi-atomics           (mapv (fn [{:keys [selector garden-vecs] :as m}]
                                         (let [css-injection-for-kushi-atomic (stylesheet/garden-vecs-injection garden-vecs)]
                                           [selector css-injection-for-kushi-atomic]))
                                       (vals kushi-atomics*))]

     #_(? (keyed
         compilation-warnings
         compilation-warnings-js
         bad-mods-warning-js
         bad-mods-warning-js*
         bad-mods-warning-js
         compilation-warnings-js
         kushi-atomics
         css-injection
         shared-classlist))

     #_(println class-stuff)
    ;; Add classes to previously-used registry
    ;; TODO: move to kushi.state?
     (doseq [{:keys [k]} shared-classlist]
       (when-not (atomic-used? k)
         (swap! state/atomic-declarative-classes-used conj k)
         (printing/diagnostics
          :defclass-register
          {:defclass-registered? (contains? @state/atomic-declarative-classes-used k)})))

     ;; Diagnostic printing for development
     ;; TODO: move to kushi.reporting?
     (printing/diagnostics
      :sx
      (let [style*    (:style attr)
            style     (merge (when (map? style*) style*) css-vars)
            class     (distinct (concat cls classlist conditional-class-sexprs))
            data-cljs {(or data-attr-name :data-cljs) data-cljs}]
        {:ident             ident
         :garden-vecs       garden-vecs
         :css-injection-dev css-injection
         :args              args
         :style-is-var?     (symbol? style)
         :attr-map          (merge attr-base {:class class :style style} data-cljs)
         :extra             (keyed cls classlist conditional-class-sexprs)}))

    ;; Add vecs into garden state
     (state/add-styles! garden-vecs)

     (printing/compilation-warnings! (:terminal compilation-warnings)) ;;print
     (printing/ansi-bad-args-warning invalid-warning-args) ;;print

     (printing/ansi-bad-mods-warning! bad-mods-warning) ;;print

     (reset! state/compilation-warnings []) ;;maybe state/reset-compilation-warnings!


     (if @KUSHIDEBUG
       ;; dev builds
       `(let [og-cls#   (:class ~attr)
              cls#      (when og-cls# (if (coll? og-cls#) og-cls# [og-cls#]))
              attr-map# (merge ~attr-base
                               {:class     (distinct (concat cls# (quote ~classlist) ~conditional-class-sexprs))
                                :style     (merge (:style ~attr) ~css-vars)
                                :data-cljs ~data-cljs})

              ;; move  cljs.core/to-array inside fn and rename js-array# ?
              logfn#    (fn [f# js-array#] (.apply js/console.warn js/console (f# js-array#)))]
          (do
            #_(when ~dupe-ident-warning
              (logfn# cljs.core/to-array (:browser ~dupe-ident-warning)))

            (when ~compilation-warnings-js
              (doseq [warning# ~compilation-warnings-js]
                (logfn# cljs.core/to-array warning#)))

            (when ~bad-mods-warning-js (logfn# cljs.core/to-array ~bad-mods-warning-js))

            ;; js-args-warning should already be nil if-not (seq ~invalid-args)
            (when (seq ~invalid-args) (logfn# cljs.core/to-array ~js-args-warning))

            ;; TODO why does first arg need to be quoted? vars?
            (kushi.core/inject-style-rules (quote ~css-injection) ~selector)
            (kushi.core/inject-kushi-atomics ~kushi-atomics)

            ;; return attribute map for the component to be rendered
            attr-map#))


      ;; release builds
       (let
        [{:keys [only-class? only-class+style?]} (util/analyze-attr m)
         inject? (:runtime-injection? user-config)]
         (cond
           only-class?
           `(do
              (when ~inject?
                (kushi.core/inject-style-rules (quote ~css-injection) ~selector)
                (kushi.core/inject-kushi-atomics ~kushi-atomics))
              {:class (quote ~classlist)})

           only-class+style?
           `(do
              (when ~inject?
                (kushi.core/inject-style-rules (quote ~css-injection) ~selector)
                (kushi.core/inject-kushi-atomics ~kushi-atomics))
              {:class (quote ~classlist)
               :style (merge (:style ~attr) ~css-vars)})

           :else
           `(let [og-cls#   (:class ~attr)
                  cls#      (when og-cls# (if (coll? og-cls#) og-cls# [og-cls#]))
                  attr-map# (merge ~attr-base
                                   {:class (distinct (concat cls# (quote ~classlist) ~conditional-class-sexprs))
                                    :style (merge (:style ~attr) ~css-vars)})]
              (when ~inject?
                (kushi.core/inject-style-rules (quote ~css-injection) ~selector)
                (kushi.core/inject-kushi-atomics ~kushi-atomics))
              attr-map#)))))))
