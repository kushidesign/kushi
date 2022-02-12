(ns ^:dev/always kushi.core
 (:require
  [clojure.spec.alpha :as s]
  [clojure.string :as string]
  [garden.core :as garden]
  [garden.def]
  [garden.stylesheet :refer [at-font-face]]
  [par.core :refer [? ?+]]
  [kushi.arguments :as arguments]
  [kushi.atomic :as atomic]
  [kushi.config :refer [user-config]]
  [kushi.parse :as parse]
  [kushi.printing :as printing]
  [kushi.selector :as selector]
  [kushi.specs :as specs]
  [kushi.state :as state]
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
  #_(? m)
  (when (and (:map-mode? user-config) (map? m))
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
      ret)))

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
        {invalid-map-args :invalid}               (when (:map-mode? user-config) (util/reduce-by-pred map? coll*))
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
        m                        {:n             defclass-name
                                  :selector      selector
                                  :selector*     selector*
                                  :args          hydrated-styles
                                  :garden-vecs   garden-vecs
                                  :__classtype__ :defclass}]

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
        #_(?+ :m m)
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
     (if @KUSHIDEBUG
       `(do
          (let [logfn# (fn [f# js-array#] (.apply js/console.warn js/console (f# js-array#)))]
            (when ~dupe-defkeyframes-warning
              (logfn# cljs.core/to-array (:browser ~dupe-defkeyframes-warning))))
          nil)
       `(do nil)))))

(defn cssfn [& args]
  (cons 'cssfn (list args)))

(def meta-ks [:ancestor :prefix :ident :element])

(defn shared-classes-inj
  [distinct-classes]
  (let [ret* (remove nil? (map #(get @state/kushi-atomic-user-classes %) distinct-classes))
        ret  (mapv (fn [{:keys [selector garden-vecs]}]
                     (let [css-injection-for-kushi-atomic (stylesheet/garden-vecs-injection garden-vecs)]
                       [selector css-injection-for-kushi-atomic]))
                   ret*)]
    ret))


(defmacro sx
  [& args*]
  #_(?+ (meta &form))
  (let [{:keys [caching?
                cache-key
                cached]}            (state/cached :sx args*)
        form-meta                   (meta &form)
        new-args                    (or cached
                                        (-> args*
                                            arguments/consolidated
                                            (arguments/new-args form-meta)))
        {:keys [prefixed-classlist
                distinct-classes
                attrs-base
                kushi-attr
                selector
                css-vars
                garden-vecs
                data-cljs]}         new-args
        _                           (state/set-current-macro! args* form-meta :sx kushi-attr)
        element-style-inj           (stylesheet/garden-vecs-injection garden-vecs)
        shared-styles-inj           (shared-classes-inj distinct-classes)

        ; printing
        _ (printing/set-warnings!)
        warnings-js @state/warnings-js]

    (when (and caching? (not cached))
      (swap! state/styles-cache-updated assoc cache-key new-args))
    (state/add-styles! garden-vecs)
    (printing/print-warnings!)

    #_(?
       (keyed
      ;; shared-classes-inj
      ;; new-style
      ;; selector
      ;; selector*
      ;; css-vars
      ;; garden-vecs
      ;; data-cljs-prefix
      ;; data-cljs
      ;; classlist
        ))

    ;;TODO - fix injection mode
    (if @KUSHIDEBUG
       ;; dev builds
       ;; TODO move cljs.core/to-array inside fn and rename js-array# ?
      `(let [logfn#    (fn [f# js-array#]
                         (.apply js/console.warn js/console (f# js-array#)))]
         (do
           (when ~warnings-js
             (doseq [warning# ~warnings-js]
               (logfn# cljs.core/to-array warning#)))

            ;; TODO why does first arg need to be quoted? vars?
           (kushi.core/inject-style-rules (quote ~element-style-inj) ~selector)
           (kushi.core/inject-kushi-atomics ~shared-styles-inj)

            ;; return attributes map for the element
           (kushi.core/merged-attrs-map
            ~attrs-base
            ~prefixed-classlist
            ~css-vars
            ~data-cljs)))

       ;; release builds
      (if (:runtime-injection? user-config)
        `(do
           (kushi.core/inject-style-rules (quote ~element-style-inj) ~selector)
           (kushi.core/inject-kushi-atomics ~shared-styles-inj)
           {:class ~prefixed-classlist})

        `(do
            ;; return attributes map for the element
           (kushi.core/merged-attrs-map
            ~attrs-base
            ~prefixed-classlist
            ~css-vars))))))
