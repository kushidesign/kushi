(ns ^:dev/always kushi.core
  (:require
   [clojure.spec.alpha :as s]
   [clojure.string :as string]
   [clojure.pprint :refer [pprint]]
   #_[kushi.parstub :refer [? !? ?+ !?+]]
   [kushi.parstub :refer [? !? ?+ !?+]]
   [garden.core :as garden]
   [garden.stylesheet :refer [at-font-face]]
   [kushi.config :refer [user-config]]
   [kushi.ui.basetheme :as basetheme]
   [kushi.arguments :as arguments :refer [args->map]]
   [kushi.printing :as printing]
   [kushi.state :as state]
   [kushi.specs :as specs]
   [kushi.stylesheet :as stylesheet]
   [kushi.typography :refer [system-font-stacks]]
   [kushi.utils :as util]

   ;; [kushi.defclass :refer [defclass-noop? defclass-dispatch]]
   [kushi.ui.theme :as theme]
   [kushi.parse :as parse]
   [kushi.selector :as selector]
   [kushi.cssvarspecs :as cssvarspecs]))

;TODO move this to utils
(defmacro keyed [& ks]
  `(let [keys# (quote ~ks)
         keys# (map keyword keys#)
         vals# (list ~@ks)]
     (zipmap keys# vals#)))

;TODO move this to utils
(defn- exception-args [{:keys [ex]}]
  {:exception-message (.getMessage ex)
   :top-of-stack-trace (get (.getStackTrace ex) 0)})


(defn add-font-face* [m]
  (let [valid-ks                            (->> ::specs/font-face-map
                                                 s/describe
                                                 rest
                                                 (apply hash-map)
                                                 vals
                                                 (apply concat)
                                                 (map (comp keyword name))
                                                 (into []))
        clean-map                           (select-keys m valid-ks)
        bad-entries                         (into {} (filter (fn [[k _]] (not (get clean-map k))) m))
        {:keys [caching? cache-key cached]} (state/cached :add-font-face m)
        aff                                 (or cached (garden/css (at-font-face m)))]
    (keyed valid-ks bad-entries aff caching? cache-key cached)))

;; font-loading related
;; -------------------------------------------------
(defmacro ^:public add-font-face
  "Example:
   (add-font-face {:font-family \"FiraCodeBold\"
                   :font-weight \"Bold\"
                   :font-style \"Normal\"
                   :src [\"local(\\\"Fira Code Bold\\\")\"]})"
  [m]
  (if (s/valid? ::specs/font-face-map m)
    (let [{:keys [valid-ks bad-entries aff caching? cache-key cached]} (add-font-face* m)]
      (printing/simple-add-font-face-warning
       {:form-meta   (meta &form)
        :args        (list m)
        :valid-ks    valid-ks
        :bad-entries bad-entries})

      (reset! state/current-macro :add-font-face)
      (swap! state/user-defined-font-faces conj aff)
      (when (and caching? (not cached))
        (swap! state/styles-cache-updated assoc cache-key aff)))
    (s/explain ::specs/font-face-map m)))


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
                        {:font-family "sys"
                         :font-style  (name style)
                         :font-weight weight
                         :src         (mapv #(str "local(\"" % "\")") fonts)}))))
        ff-rules  (apply concat ff-rules*)]
    ff-rules))


(defmacro ^:public add-system-font-stack
  [& weights*]
  (let [{:keys [caching? cache-key cached]} (state/cached :system-font-stack weights*)
        ff-rules                            (into []
                                                  (or cached
                                                      (system-at-font-face-rules weights*)))]
    (doseq [rule ff-rules]
      (reset! state/current-macro :add-font-face)
      (swap! state/user-defined-font-faces conj rule))
    (when (and caching? (not cached))
      (swap! state/styles-cache-updated assoc cache-key ff-rules))
    @state/user-defined-font-faces))



;; defkeyframes related
;; ----------------------------------------------------
(defn keyframe [[k v]]
  (let [frame-key (if (vector? k)
                    (string/join ", " (map name k))
                    (string/replace (name k) #"\|" ","))
        frame-val (reduce
                   (fn [acc [key val]]
                     (assoc acc key (if (util/cssfn? val) (util/cssfn val) val)))
                   {}
                   v)]
    [frame-key frame-val]))


(defn- defkeyframes-exception-args
  [{:keys [args ex] :as m}]
  (merge m (exception-args m)))

(defmacro ^:public defkeyframes
  [nm & frames*]
  (let [opts         {:fname     "defkeyframes"
                      :nm        nm
                      :form-meta (meta &form)}
        dupe-warning (printing/dupe-defkeyframes-warning opts)]
    (printing/print-dupe2! dupe-warning)
    (reset! state/current-macro :defkeyframes)
    (let [{:keys [caching?
                  cache-key
                  cached]} (state/cached :keyframes nm frames*)
          frames           (try
                             (or cached (mapv keyframe frames*))
                             (catch Exception ex
                               (-> {:form-meta (meta &form)
                                    :fname     "defkeyframes"
                                    :sym       nm
                                    :args      frames*
                                    :ex        ex}
                                   defkeyframes-exception-args
                                   printing/caught-exception)))]
      (when frames
        (swap! state/user-defined-keyframes assoc (keyword nm) frames)
        (when (and caching? (not cached))
          (swap! state/styles-cache-updated assoc cache-key frames))))))


(defn ^:public cssfn [& args]
  ;TODO add some validation here
  (cons 'cssfn (list args)))






;; defclass related
;; ----------------------------------------------------

(defn- defclass-noop? [sym args]
  ;; For skipping defclasses & overrides from theming
  (and (nil? sym) (= args '(nil))))

(defn- sym->classtype [sym]
  (let [meta* (some-> sym meta)]
    (cond
      (:kushi-utility meta*)          :kushi-utility
      (:kushi-utility-override meta*) :kushi-utility-override
      (:user-utility-override meta*)  :user-utility-override
      :else                           :user-utility)))

(defn- style-map->vecs
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
                              :atomic-class? (contains? #{:kushi-utility :kushi-utility-override}
                                                        classtype)})
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



(defn- defclass*
  [{:keys [sym args classtype]}]
  (let [sym                      (if (keyword? sym) (symbol sym) sym)
        defclass-name            (keyword sym)
        last*                    (last args)
        style-map*                (when (map? last*) last*)
        tokens*                  (if style-map* (drop-last args) args)
        [class-tokens* tokens]   (util/partition-by-spec ::specs/tokenized-classes tokens*)
        class-tokens             (arguments/defclass-class-tokens class-tokens*)
        style-map-vecs           (some-> style-map* style-map->vecs)
        coll                     (concat tokens style-map-vecs)
        distinct-defclasses      (->> class-tokens :from-tokens :distinct-classes)
        tokens-from-mixins       (mapv :tokens distinct-defclasses)
        style-maps-from-mixins   (mapv #(->> % (get @state/utility-classes) :style-map) distinct-defclasses)
        style-map                (->> style-map* (conj style-maps-from-mixins) (apply merge))
        tokens                   (into [] (remove nil? (concat tokens-from-mixins tokens)))
        {:keys [valid-styles-from-attrs
                valid-styles-from-tokens
                invalid-style-args]}     (arguments/validate-args args tokens {:style style-map})
        invalid-args             (or
                                  (when-not (s/valid? ::specs/defclass-name sym) ^:classname [sym])
                                  invalid-style-args)
        styles                   (into [] (concat valid-styles-from-tokens valid-styles-from-attrs))
        {:keys [selector
                selector*
                hydrated-styles
                garden-vecs]}    (hydrated-defclass defclass-name classtype styles)

        warnings                (when invalid-args
                                  {:defclass-name defclass-name
                                   :args          (apply vector args)
                                   :invalid-args  invalid-args})]

    #_(when (when (= sym 'hi) (?+ args))
      (?+ (keyed
           garden-vecs
          ;;  last*
           style-map
           distinct-defclasses
           style-maps-from-mixins
           tokens
          ;;  style-map-vecs
          ;;  coll
           )))
    ;; (?+ styles)

    ;; TODO remove or consolidate :n and :args entry
    ;; ...they are redundant with :defclass-name and :hydrated-styles
    (merge {:n    defclass-name
            :args hydrated-styles}
           (keyed
            defclass-name
            style-map*
            style-map
            tokens
            coll
            invalid-args
            hydrated-styles
            warnings
            garden-vecs
            classtype
            selector
            selector*))))


(defn- defclass-exception-args [{:keys [args ex] :as m}]
  (merge m (exception-args m)))


(defn defclass-dispatch [{:keys [sym form-meta args] :as m*}]
  (reset! state/current-macro :defclass)
  (try
    (let [classtype  (sym->classtype sym)
          current-op (assoc m* :macro :defclass :classtype classtype)
          _          (reset! state/current-op current-op)
          cache-map  (state/cached current-op)
          result     (or (:cached cache-map) (defclass* current-op))]
      (state/add-utility-class! result)
      (state/update-cache! cache-map result)

      (when (state/debug?) result)
      (printing/simple-defclass-warning m* result)
      ;; If this is an base class, add an override-class version
      (let [base->override {:user-utility  :user-utility-override
                            :kushi-utility :kushi-utility-override}]
        (when-let [override-class (get base->override classtype)]
          (let [sym (with-meta (-> sym name (str "\\!") symbol) {override-class true})]
            (!?+ :bout-to-dispatch-> (assoc m* :sym sym :sym-meta (meta sym) :classtype-would-be (sym->classtype sym)))
            (defclass-dispatch (assoc m* :sym sym :duplicate-for-override? true))
            #_(!?+ @state/utility-classes-by-classtype))))
      result)
    (catch Exception ex
      (-> {:form-meta form-meta
           :fname     "defclass"
           :sym       sym
           :args      args
           :ex        ex}
          defclass-exception-args
          printing/caught-exception))))


(defmacro ^:public defclass
  [sym & args]
  (when-not (defclass-noop? sym args)
    (try
      (defclass-dispatch {:sym       sym
                          :args      args
                          :form-meta (meta &form)}))
    `(do nil)))



;; sx related
;; -----------------------------------------------------
(defn sx-dispatch
  [{:keys [form-meta args]
    :or   {form-meta {}}}]
  (reset! state/current-op (assoc {} :macro :sx :fname "sx" :args args :form-meta form-meta))
  (let [{:keys [cache-key
                cached]
         :as   cache-map}         (state/cached :sx args)
        {:keys [distinct-classes
                garden-vecs
                inj-type]
         :as   m}                 (or cached
                                      (args->map (keyed args
                                                        form-meta
                                                        cache-key)))]
    (swap! state/current-op merge (select-keys m [:invalid-style-args :kushi-attr :bad-mods :fname]))
    (state/register-utility-class-usage! distinct-classes)
    (state/update-cache! cache-map m)
    (state/add-styles! garden-vecs inj-type)
    ;; TODO use simple warning (after adding improved humanized error formatting)
    #_(printing/simple-sx-warning)
    (printing/set-warnings!)
    (printing/print-warnings!)
    m))

(defn- sx-attrs-sans-styling [args]
  (if-let [attrs (when (-> args last map?) (last args))]
    (dissoc attrs :style)
    {}))

(defn- sx-exception-args [{:keys [args ex] :as m}]
  (merge m
         (exception-args m)
         {:commentary (str "The element you are trying to style" "\n"
                           "will receive the following attribute map:" "\n"
                           (with-out-str (pprint (sx-attrs-sans-styling args))))}))

;; Dupe of public fn in core.cljs. Needed for testing when doing (macroexpand-1 (sx ...)).
(defn merged-attrs-map
  [{:keys [attrs-base prefixed-classlist css-vars]
    :as   m}]
  (assoc attrs-base :class (distinct prefixed-classlist) :style css-vars))

(defmacro ^:public sx
  [& args]
  (when-not (= args '(nil))
    (let [{:keys [attrs-base
                  prefixed-classlist
                  css-vars
                  data-cljs]
           :as   result}             (try
                                       (sx-dispatch {:args args :form-meta (meta &form)})
                                       (catch Exception ex
                                         (-> {:form-meta (meta &form)
                                              :fname     "sx"
                                              :args      args
                                              :ex        ex}
                                             sx-exception-args
                                             printing/caught-exception)))
          attrs-sans-styling        (when (nil? result)
                                      (sx-attrs-sans-styling args))]
         (if attrs-sans-styling
           `(do ~attrs-sans-styling)
           `(kushi.core/merged-attrs-map
             {:attrs-base         ~attrs-base
              :prefixed-classlist ~prefixed-classlist
              :css-vars           ~css-vars
              :data-cljs          ~data-cljs})))))


;; Theme related
;; -----------------------------------------------------
(defn- add-utility-classes! [coll kw]
  (doseq [[k styles] coll]
    (defclass-dispatch {:sym  (with-meta (symbol k) {kw true})
                        :args [styles]})))

(defn- font-loading! [m]
  (let [asfs? (:add-system-font-stack? m)
        gfm   (:google-font-maps m)]
    (when asfs? (add-system-font-stack))
    (!?+ (count @state/user-defined-font-faces))
    (when gfm (state/add-google-font-maps! gfm))))


(defn theme!
 []
 (let [{:keys [css-reset
               css-reset-el
               font-loading-opts
               global-toks
               alias-toks
               tokens-in-theme
               styles
               utility-classes]} theme/theme]
   (doseq [[selector m] (partition 2 css-reset)
           :when        (s/valid? ::specs/css-reset-selector selector)
           :let         [el       (when css-reset-el (str (name css-reset-el) " "))
                         selector (if (vector? selector)
                                    (let [el-bs     (when (and el (= m {:box-sizing :border-box}))
                                                      [el (str el "::before") (str el "::after")])
                                          prepended (map #(str el %) selector)
                                          coll      (concat el-bs prepended)]
                                      (string/join ", " coll))
                                    selector)]]
     (sx-dispatch {:args [selector {:style m :kushi/sheet :reset}]}))

   ;; TODO - conditionalize these 2 for prod vs dev - OR - always add to state and then conditionalize writing of css chunks based on user config setting
   (doseq [tok global-toks] (state/add-global-token! tok))
   (doseq [tok alias-toks] (state/add-alias-token! tok))

   ;; TODO - Maybe not needed if always writing above.
   (doseq [tok tokens-in-theme] (state/add-used-token! tok))

   (add-utility-classes! utility-classes :kushi-utility)
  ;;  (add-utility-classes! base-classes :kushi-utility)
  ;;  (add-utility-classes! override-classes :kushi-utility-override)

   (doseq [m styles] (sx-dispatch {:args [m]}))

   (font-loading! font-loading-opts)))

(defn kushi-debug
  {:shadow.build/stage :compile-prepare}
  [build-state]
  (when-not (:css-dir user-config)
    (printing/build-failure))
  (let [mode (:shadow.build/mode build-state)]
    (when (not= mode :dev)
      (reset! state/KUSHIDEBUG false)))
  (theme!)
  build-state)


(defmacro inject! []
  (stylesheet/create-css-text)
  (let [css-sync         @state/kushi-css-sync
        google-font-maps @state/google-font-maps]
  ;;  (?+ :inject:gfm @state/google-font-maps)
  ;;  (?+ :inject:ff @state/user-defined-font-faces)
   `(do
      (apply kushi.core/add-google-font! ~google-font-maps)
      (kushi.core/css-sync! ~css-sync))))


(defn map-of-all-tokens []
  (into {} (concat @state/global-tokens @state/alias-tokens)))

(defn resolve-token-value [m kw]
  (let [v (kw m)]
    (if (s/valid? ::cssvarspecs/css-var-name v)
      (resolve-token-value m v)
      v)))

(defmacro token->ms [kw]
  (let [v                (-> (map-of-all-tokens) (resolve-token-value kw))
        s                (cond (number? v) (str v) (or (keyword? v) (string? v)) (name v))
        [_ microseconds] (when s (re-find #"^([0-9]+)ms$" s))
        [_ seconds]      (when s (re-find #"^([0-9]+)s$" s))
        n                (or microseconds (some-> seconds (* 1000)))
        ret              (when n (Integer/parseInt n))]
    `~ret))
