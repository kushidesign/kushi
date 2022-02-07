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
  [kushi.utils :as util :refer [#_? #_keyed]]))

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

#_(defn- scoped-atomic-classname
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

#_(defn- atomic-classes
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
     :invalid-map-args invalid-map-args}) )

(def meta-ks [:ancestor :prefix :ident :element])





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

(defn shared-classlist
  [atomic-class-keys]
  #_(? atomic-class-keys)
  (into
   []
   (remove
    nil?
    (doall
     (for [k* atomic-class-keys
           :let [k (keyword (subs (name k*) 1))
                 {:keys [selector selector*]} (-> {:defclass-name k} selector/selector-name)]]
       (keyed k selector* selector))))))

(defn shared-classes-inj
  [distinct-classes selector]
  (let [ret* (remove nil? (map #(get @state/kushi-atomic-user-classes %) distinct-classes))

        ret (mapv (fn [{:keys [selector garden-vecs]}]
                    (let [css-injection-for-kushi-atomic (stylesheet/garden-vecs-injection garden-vecs)]
                      [selector css-injection-for-kushi-atomic]))
                            ret*)]
    ret))

;; TODO
;; finish printing
;; bad args warning
;; only attr
;; bad args as tuple outside of map
;; 

(defmacro sx
  [& args*]
  #_(?+ (meta &form))
  (let [form-meta                  (meta &form)
        new-args                   (-> args*
                                       arguments/consolidated
                                       (arguments/new-args form-meta))
        {:keys [;; new-style
                prefixed-classlist
                distinct-classes
                attrs-base
                kushi-attr
                selector
                ;; selector*
                css-vars
                garden-vecs
                data-cljs]}        new-args
        _                         (state/set-current-macro! args* form-meta :sx kushi-attr)
        element-style-inj         (stylesheet/garden-vecs-injection garden-vecs)
        shared-styles-inj         (shared-classes-inj distinct-classes selector)

        ; printing
        _ (printing/set-warnings!)
        warnings-js @state/warnings-js]

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

    (or
     #_(only-attr args)
     (if @KUSHIDEBUG
       ;; dev builds
       `(let [;; move  cljs.core/to-array inside fn and rename js-array# ?
              logfn#    (fn [f# js-array#]
                          (.apply js/console.warn js/console (f# js-array#)))]

          (do
              ;; Leave commented
            #_(when ~dupe-ident-warning
                (logfn# cljs.core/to-array (:browser ~dupe-ident-warning)))

            (when ~warnings-js
              (doseq [warning# ~warnings-js]
                (logfn# cljs.core/to-array warning#)))

              ;; (when ~bad-mods-warning-js (logfn# cljs.core/to-array ~bad-mods-warning-js))

            ;; js-args-warning should already be nil if-not (seq ~invalid-args)
            ;;   (when (seq ~invalid-args) (logfn# cljs.core/to-array ~js-args-warning))

            ;; TODO why does first arg need to be quoted? vars?
            (kushi.core/inject-style-rules (quote ~element-style-inj) ~selector)
            (kushi.core/inject-kushi-atomics ~shared-styles-inj)

            ;; return attribute map for the component to be rendered
            (kushi.core/merged-attrs-map
             ~attrs-base
             ~prefixed-classlist
             ~css-vars
             ~data-cljs)))

      ;; release builds

        ;;  (let
        ;;   [{:keys [only-class? only-class+style?]} (util/analyze-attr m)
        ;;    inject?                                 (:runtime-injection? user-config)]
        ;;    (cond
        ;;      only-class?
        ;;      `(do
        ;;         (when ~inject?
        ;;           (kushi.core/inject-style-rules (quote ~css-injection) ~selector)
        ;;           (kushi.core/inject-kushi-atomics ~kushi-atomics))
        ;;         {:class (quote ~classlist)})

        ;;      only-class+style?
        ;;      `(do
        ;;         (when ~inject?
        ;;           (kushi.core/inject-style-rules (quote ~css-injection) ~selector)
        ;;           (kushi.core/inject-kushi-atomics ~kushi-atomics))
        ;;         {:class (quote ~classlist)
        ;;          :style (merge (:style ~attr) ~css-vars)})

        ;;      :else
        ;;      `(let [og-cls#   (:class ~attr)
        ;;             cls#      (when og-cls# (if (coll? og-cls#) og-cls# [og-cls#]))
        ;;             attr-map# (merge ~attr-base
        ;;                              {:class (distinct (concat cls# (quote ~classlist) ~conditional-class-sexprs))
        ;;                               :style (merge (:style ~attr) ~css-vars)})]
        ;;         (when ~inject?
        ;;           (kushi.core/inject-style-rules (quote ~css-injection) ~selector)
        ;;           (kushi.core/inject-kushi-atomics ~kushi-atomics))
        ;;         attr-map#)))
       ))))
