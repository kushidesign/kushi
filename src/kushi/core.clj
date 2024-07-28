(ns ^:dev/always kushi.core
  (:require
   [garden.color]
   [clojure.pprint :refer [pprint]]
   [clojure.spec.alpha :as s]
   [clojure.string :as string]
   [clojure.set :as set]
   [kushi.styles :refer [all-style-tuples all-style-tuples]]
   [kushi.config :as config :refer [user-config]]
   [kushi.shorthand :as shorthand]
   [kushi.printing2 :as printing2 :refer [kushi-expound]]
   [kushi.state2 :as state2]
   [kushi.specs2 :as specs2]
   [kushi.stylesheet :as stylesheet]
   [kushi.typography :refer [add-font-face*]]
   [kushi.args :as args]
   [kushi.ui.theme :as theme]
   [kushi.utils :as util]))


(defmacro keyed [& ks]
  `(let [keys# (quote ~ks)
         keys# (map keyword keys#)
         vals# (list ~@ks)]
     (zipmap keys# vals#)))


(defn- update-cache!
  "Expects a coll (map of macro result) and a cache-map map."
  [coll
   {:keys [caching? cached cache-key]}]
  (when (and coll caching? (not cached))
    (swap! state2/styles-cache-updated assoc cache-key coll)))

(defmacro breakpoints
  "Returns (:media kushi.config/user-config).
   The value will be a vector of breakpoints, which will be either kushi.config/defalt-kushi-responsive,
   or a user-provided a vector if the user gives a valid :media entry in their kushi.edn config map."
  []
  (let [ret (:media user-config)]
    `~ret))

;; FONT FACE -------------------------------------------------------------

(defmacro ^:public add-font-face

  "Produces a valid @font-face-css declaration.
   Expects a single map of options.
   The following entries are required:
   :font-family - Must be string.
   :src         - Must be url string, or a vector of url strings.

   Example:
   (add-font-face {:font-family \"FiraCodeBold\"
                   :font-weight \"Bold\"
                   :font-style \"Normal\"
                   :src [\"local(\\\"Fira Code Bold\\\")\"]})"
  [m]
  (let [{:keys [cache-map
                expound-str
                :entries/weird]
         :as   aff}             (add-font-face* m)
        warn?                   (or expound-str weird)
        aff                     (merge (dissoc aff :valid-ks)
                                       {:form-meta     (meta &form)
                                        :kushi/process :kushi.core/add-font-face
                                        :args          (list m)}
                                       (when warn?
                                         {:doc (:doc (meta #'add-font-face))}))]

    (swap! state2/user-defined-font-faces conj aff)

    (when (or expound-str weird)
      (printing2/simple-warning2 aff))

    (update-cache! aff cache-map)))




;; DEFKEYFRAMES ----------------------------------------------------------


;; TODO use existing code to deal with vectors, css lists, and cssvars
(defn- keyframe [[k v]]
  (let [frame-key (if (vector? k)
                    (string/join ", " (map name k))
                    (string/replace (name k) #"\|" ","))
        v         (util/map-keys #(-> % shorthand/key-sh name) v)
        [tups* _] (all-style-tuples v)
        tups      (into {} tups*)]
    [frame-key tups]))


(defmacro ^:public defkeyframes
  "Creates a css @keyframes rule.

   Examples:

   (defkeyframes slide-in
     [:from {:translateX(0%) :opacity 0}]
     [:to {:translateX(100%) :opacity 1}])

   (defkeyframes y-axis-spinner
     [:33% {:transform \"rotateY(0deg)\"}]
     [:100% {:transform \"rotateY(360deg)\"}])"

  [nm & frames*]
  (let [{:keys [cached]
         :as   cache-map}
        (state2/cached {:process :keyframes
                        :sym nm
                        :args frames*})

        spec
        ::specs2/defkeyframes-args

        args
        (cons nm frames*)

        problems
        (some->> args
                 (s/explain-data spec)
                 :clojure.spec.alpha/problems)

        bad-args
        (some->> problems
                 (keep (fn [{:keys [in]}]
                         {:path in
                          :arg  (->> in first (nth args))}))
                 distinct
                 (into []))

        m
        (merge cache-map
               {:kushi/process               :kushi.core/defkeyframes
                :args/bad                    bad-args
                :args                        args
                :form-meta                   (meta &form)
                :clojure.spec.alpha/problems problems}
               (when problems
                 {:doc         (:doc (meta #'defkeyframes))
                  :expound-str (kushi-expound spec args)}))

        frames
        (if problems
          (printing2/simple-warning2 m)
          (try
            (or cached (mapv keyframe frames*))
            (catch Exception ex
              (let [m       (assoc m :ex ex :re #"defkeyframes")
                    ex-args (merge m (util/exception-args m))]
                (printing2/caught-exception2 ex-args)))))]
    (swap! state2/user-defined-keyframes assoc (keyword nm) frames)
    (update-cache! frames cache-map)))


;; DESIGN TOKENS ---------------------------------------------------------

(defn map-of-all-tokens []
  (into {} @state2/design-tokens))

(defn resolve-token-value [m kw]
  (let [v (kw m)]
    (if (s/valid? ::specs2/cssvar-name v)
      (resolve-token-value m v)
      v)))

(defmacro token->ms
  "Expects a key which maps to an existing design token (css custom property).
   If the value of the token is a valid (css) microseconds or seconds unit,
   an integer representing the number of microseconds will be returned."
  [kw]
  (let [v        (-> (map-of-all-tokens) (resolve-token-value kw))
        s        (cond
                   (number? v)
                   (str v)
                   (or (keyword? v)
                       (string? v))
                   (name v))
        [_ ms]   (when s (re-find #"^([0-9]+)ms$" s))
        [_ secs] (when s (re-find #"^([0-9]+)s$" s))
        n        (or ms (some-> secs (* 1000)))
        ret      (when n (Integer/parseInt n))]
    `~ret))




;; DEFCLASS --------------------------------------------------------------

(defn- defclass-noop? [sym args]
  ;; For skipping defclasses & overrides from theming
  (and (nil? sym) (= args '(nil))))

(defn- sym->process [{:keys [sym override]}]
  (or override
      (let [meta* (some-> sym meta)]
        (if (:kushi-defclass meta*)
          :kushi/utility
          :kushi.core/defclass))))

(defn- defclass-exception-args [m]
  (merge m (util/exception-args m)))

(defn update-shared-classes!
  "Updated state for subsequent user defclasses that compose with this class"
  [clean sym]
  (when-let [tups (:defclass-style-tuples clean)]
    (swap! state2/shared-classes assoc sym tups)))

(defn print-warnings [clean]
  (when (or (:entries/weird clean) (:args/bad clean))
    (printing2/simple-warning2 clean)))

(defn defclass-dispatch
  [{:keys [sym
           form-meta
           args
           override
           user-defclass-with-override?]
    :as   m}]

  (try
    (let [dev-trace?        (and @state2/KUSHIDEBUG
                                 (state2/enable-trace? args))
          _                 (when dev-trace? (state2/enable-trace!))
          args              (if dev-trace? (drop-last args) args)

          ;; TODO change :process to :kushi/process?
          {:keys [cached]
           :as   cache-map} (state2/cached
                             {:process :defclass
                              :sym     sym
                              :args    args})

          process           (sym->process m)
          chunk             (or (:kushi/chunk (meta sym))
                                process)

          clean             (or cached
                                (args/clean-args {:args          (cons sym args)
                                                  :kushi/process process
                                                  :form-meta     form-meta}))
          clean             (merge clean
                                   {:kushi/chunk chunk})]

      ;; debugging
      ;; (when (= sym 'foo) (+ 1 true))

      (swap! state2/css conj clean)

      (print-warnings clean)

      (update-shared-classes! clean sym)

      (update-cache! clean cache-map)

      (when (and (not override) user-defclass-with-override?)
        (defclass-dispatch (merge (keyed args form-meta)
                                  {:sym      (symbol (str sym "\\!"))
                                   :override :kushi.core/defclass-override})))

      (when (and (not override) (= :kushi/utility chunk))
        (defclass-dispatch (merge (keyed args form-meta)
                                  {:sym      (symbol (str sym "\\!"))
                                   :override :kushi/utility-override})))

      clean)

    (catch Exception ex
      (-> {:form-meta form-meta
           :fname     "defclass"
           :sym       sym
           :args      args
           :ex        ex}
          defclass-exception-args
          printing2/caught-exception))

    (finally
      (when @state2/KUSHIDEBUG
        (state2/disable-trace!)))))


(defmacro ^:public defclass
  [sym & args]
  (when-not (defclass-noop? sym args)
    (defclass-dispatch {:sym       sym
                        :args      args
                        :form-meta (meta &form)})
    `(do nil)))

(defmacro ^:public defclass-with-override
  [sym & args]
  (when-not (defclass-noop? sym args)
    (defclass-dispatch {:sym                          sym
                        :args                         args
                        :form-meta                    (meta &form)
                        :user-defclass-with-override? true})
    `(do nil)))

;; SX ---------------------------------------------------------------------
(defn register-classes
  [clean]
  (let [used (into #{}
                   (keep #(when (and (string? %)
                                     (->> % first str (re-find #"[a-z]")))
                            (let [ret* (string/replace % #"\!$" "\\\\!")]
                              ret*))
                         (:classlist clean)))]
    (swap! state2/registered-shared-classes set/union used)))

(defn process [args]
  (let [m* (last args)]
    (if-let [m (when (map? m*) m*)]
      (cond
        (:kushi-theme-selector m)
        :kushi/theme
        (= (:kushi/process m) :css-reset)
        :kushi/css-reset
        :else
        :kushi.core/sx)
      :kushi.core/sx)))


(defn sx-dispatch
  [{:keys [form-meta args macro]
    :or   {form-meta {}}}]

  (let [dev-trace? (and @state2/KUSHIDEBUG
                        (state2/enable-trace? args))
        _          (when dev-trace? (state2/enable-trace!))
        args       (if dev-trace? (drop-last args) args)
        cache-map  (state2/cached {:process :sx
                                   :args    args})
        process    (process args)
        clean      (or (:cached cache-map)
                       (args/clean-args {:args          args
                                         :kushi/process process
                                         :cache-key     (:cache-key cache-map)
                                         :form-meta     form-meta}))
        clean      (merge clean {:kushi/chunk process})
        clean      (if (= macro :sx*)
                     (assoc-in clean [:attrs :data-sx-tweak] (str (into [] args)))
                     clean)]

    ;; debugging
    ;; (when (= '(quote foo) (first args))
    ;;   (? :sx2 clean))

    (when (:elide-unused-kushi-utility-classes? user-config)
      (register-classes clean))

    (swap! state2/css conj clean)

    (update-cache! clean cache-map)

    (print-warnings clean)

    (when @state2/KUSHIDEBUG (state2/disable-trace!))

    clean))


;; move to printing
(defn- sx-attrs-sans-styling [args]
  (if-let [attrs (when (-> args last map?) (last args))]
    (dissoc attrs :style)
    {}))


;; move to printing
(defn- sx-exception-args
  [{:keys [args] :as m}]
  (merge m
         (util/exception-args m)
         {:commentary (str "The element you are trying to style" "\n"
                           "will receive the following attribute map:" "\n"
                           (with-out-str (pprint (sx-attrs-sans-styling args))))}))



(defmacro ^:public sx
  [& args]
  (let [m*           (first args)
        from-defcom? (and (map? m*) (:_kushi/defcom? m*))
        args         (if from-defcom? (:args m*) args)
        form-meta    (if from-defcom? (:form-meta m*) (meta &form))]
    (when-not (= args '(nil))
      (let [m               {:args          args
                             :form-meta     form-meta
                             :kushi/process :kushi.core/sx
                             :fname         "sx"
                             :macro         :sx}
            {:keys [attrs]} (try
                              (sx-dispatch m)
                              (catch Exception ex
                                (-> m
                                    (assoc :ex ex)
                                    sx-exception-args
                                    printing2/caught-exception)))]
        `~attrs))))

(defmacro ^:public sx*
  [& args]
  (let [m*           (first args)
        from-defcom? (and (map? m*) (:_kushi/defcom? m*))
        args         (if from-defcom? (:args m*) args)
        form-meta    (if from-defcom? (:form-meta m*) (meta &form))]
    (when-not (= args '(nil))
      (let [m               {:args          args
                             :form-meta     form-meta
                             :kushi/process :kushi.core/sx
                             :fname         "sx"
                             :macro         :sx*}
            {:keys [attrs]} (try
                              (sx-dispatch m)
                              (catch Exception ex
                                (-> m
                                    (assoc :ex ex)
                                    sx-exception-args
                                    printing2/caught-exception)))]
        `~attrs))))



;; THEMING ---------------------------------------------------------------

(defn- add-utility-classes! [coll kw]
  (doseq [[k styles] coll]
    (defclass-dispatch {:sym  (with-meta (symbol k) {kw true})
                        :args [styles]})))

(defn- font-loading!
  [{:keys [google-font-maps
           google-material-symbols-maps]
    :as m}]
  (when (seq google-font-maps)
    (state2/add-google-font-maps!
     google-font-maps))
  (when (seq google-material-symbols-maps)
    (state2/add-google-material-symbols-font-maps!
     google-material-symbols-maps)))


(defmacro form-meta* []
  (let [form-meta (assoc (meta &form) :file "kushi.core/theme!")]
    `~form-meta))


(defn theme!
  []
  (let [{:keys [css-reset
                font-loading
                design-tokens
                theme-design-tokens
                styles
                utility-classes]} (theme/theme)]

    (doseq [[selector m] (partition 2 css-reset)
            :when        (s/valid? ::specs2/css-reset-selector selector)
            :let         [selector (if (vector? selector)
                                     (string/join ", " selector)
                                     selector)]]
      (sx-dispatch {:args [selector {:style m
                                     :kushi/sheet :reset
                                     :kushi/process :css-reset}]
                    :form-meta (form-meta*)
                    :kushi/process :css-reset
                    :kushi/sheet   :reset}))

   ;; TODO - conditionalize these 2 for prod vs dev
   ;; - OR -
   ;; always add to state and then conditionalize writing of css chunks based on user config setting
    (doseq [tok design-tokens] (state2/add-design-token! tok))

   ;; TODO - tokens from theme go in here.
    (doseq [tok theme-design-tokens] (state2/add-theming-token! tok))

    (add-utility-classes! utility-classes :kushi-defclass)

    (when (:add-ui-theming? user-config)
      (doseq [m styles]
        (sx-dispatch {:args          [(:kushi-theme-selector m) m]
                      :form-meta     (form-meta*)
                      :kushi/process :theme})))

    (font-loading! font-loading)))




;; BUILD -----------------------------------------------------------------

(defn kushi-debug
  {:shadow.build/stage :compile-prepare}
  [{:keys [:shadow.build/build-id] :as build-state}]
  (reset! state2/shadow-build-id build-id)
  ;; (when (and @state2/initial-build? (:log-kushi-version? user-config))
  ;;   println)
  (when-not (:css-dir user-config)
    (printing2/build-failure))
  (let [mode (:shadow.build/mode build-state)]
    (when (not= mode :dev)
      (reset! state2/KUSHIDEBUG false)))
  (theme!)
  build-state)


;; RUNTIME ---------------------------------------------------------------
;; todo
;; separate macro for injecting gfonts & webfonts from theme
;; ^^^ always gets called from core.cljs
;; make sure manually inject gfonts still load

(defmacro inject-google-fonts! []
  (let [tag            (str "[" @state2/shadow-build-id "] [Kushi v" config/version "]")
        tag-browser-gf (str tag " - Injecting goog-fonts-map")]
    (do (let [google-font-maps                  @state2/google-font-maps
              google-material-symbols-font-maps @state2/google-material-symbols-font-maps]
          #_(pprint google-font-maps)
          `(do
             #_(js/console.log ~google-font-maps)
             #_(js/console.log ~tag-browser-gf)
             (apply kushi.core/add-google-font! ~google-font-maps)
             (apply kushi.core/add-google-material-symbols-font! ~google-material-symbols-font-maps))))))


(defmacro inject! []
  (let [mode                     (if @state2/KUSHIDEBUG :dev :release)
        inject?                  (or (and (:inject-at-runtime-dev? user-config)
                                          (= mode :dev))
                                     (and (:inject-at-runtime-prod? user-config)
                                          (= mode :release)))
        tag            (str "[" @state2/shadow-build-id "] [Kushi v" config/version "]")
        tag-build      (str tag " - Injecting stylesheet at runtimes? " inject?)
        tag-browser    (str tag " - Injecting stylesheet")
        tag-browser-gf (str tag " - Injecting goog-fonts-map")
        google-font-maps         @state2/google-font-maps]
    #_(println tag-build)
    (if inject?
      (do (stylesheet/create-css-text "kushi.core/inject!")
          (let [css-sync         @state2/->css]
            `(do
              ;; (println ~tag-browser)
               (apply kushi.core/add-google-font! ~google-font-maps)
               (kushi.core/css-sync! ~css-sync))))
      `(do
         ;; (println ~tag-browser-gf)
         (apply kushi.core/add-google-font! ~google-font-maps)))))


