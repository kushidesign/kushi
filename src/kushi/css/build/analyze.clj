(ns kushi.css.build.analyze
  (:require
   [fireworks.core :refer [? !? ?> !?> pprint]]
   [babashka.process :refer [shell]]
   [bling.core :refer [callout bling]]
   [edamame.core :as e]
   [kushi.css.build.utility-classes :as utility-classes]
   [kushi.css.build.tokens :refer [design-tokens-by-category
                                   design-tokens-by-token
                                   design-tokens-by-token-array-map]]
   [kushi.core :refer [css-rule*]]
   [kushi.css.hydrated :as hydrated]
   [kushi.css.specs :as kushi-specs]
   [kushi.util :refer [maybe keyed]]
   [clojure.walk :as walk]
   [clojure.string :as string]
   [clojure.java.io :as io]
   [clojure.set :as set]
   [clojure.spec.alpha]
  ;;  [tick.core :as tick]
   ))


;; Perf Timing -----------------------------------------------------------------
;; (def add-ticks? false)

;; (def ticks (atom [(tick/instant)]))
        
;; (defn tick-msg
;;   ([msg a b]
;;    (tick-msg msg a b nil))
;;   ([msg a b id]
;;    (let [ms      (tick/between a b :millis)
;;          seconds (format "%.2f" (* ms 0.001))]
;;      (str (when id (str "[" id "] "))
;;           msg
;;           ". ("
;;           (string/join (repeat (- 33 (count msg)) " "))
;;           seconds
;;           "s)"))))

(defn add-tick! [x])

;; (defn add-tick! [msg]
;;   (when add-ticks?
;;     (let [[prev current]
;;           (-> ticks
;;               (swap! conj (tick/instant))
;;               (->> (take-last 2)))]
;;       (println (tick-msg msg prev current)))))


;; Project config --------------------------------------------------------------

(def dev-sample-proj-dir "docs")

(def user-config*
  ;; Should be "./docs/kushi.edn" only for test-refresh dev
  (let [config (try (-> "./kushi.edn" slurp read-string)
                    (catch Exception e
                      (try (-> (str "./" dev-sample-proj-dir "/kushi.edn") 
                               slurp
                               read-string)
                           (catch Exception e
                             {}))))]
    (assoc config
           :css-dir
           (string/replace (or (:css-dir config)
                               "./public/css")
                           #"\/$"
                           "")
           :css-filename
           (or (:css-filename config) "main")
           :css-bundle-filename
           (or (:css-bundle-filename config) "bundle")
           )))

(def debugging 
  #{
    ;; :narrative
    ;; :macro-hydration
    ;; :design-token-registration
    })


(def kushi-macros
  '#{css-include
     defcss 
     css
     sx
     ?css
     ?sx
     ?defcss
     register-design-tokens
     register-design-tokens-by-category
     ;; should be register-css-classes
     utilitize})


(def kushi-layers
  (array-map 
   "css-reset"
   "List of rulesets that reset default browser styles"

   "design-tokens"
   "Global CSS custom properties"

   "user-design-tokens"
   "Global CSS custom properties defined in user's theme"

   "kushi-utility"
   "Baseline utility classes defined by kushi"

   "kushi-ui-theming" 
   "Theming rules for kushi.ui components"

   "kushi-ui-shared" ;; <- determine if anything shares styles outside of popovers, tooltips, toasts etc.
   "Styles to be shared across or within families of kushi ui components such as tooltips, popovers, etc"

   "kushi-ui-styles"
   "Styles defined for elements of kushi ui components."

   "kushi-playground-shared" 
   "Styles to be shared across or within families of kushi.playground namespaces."

   "kushi-playground-styles"
   "Styles defined for elements of kushi.playground components."

   "user-shared-styles"
   "User-defined shared styles"

   "user-styles"
   "User-defined styles, via kushi.core/css or kushi.core/sx"
   
   "kushi-utility-overrides"
   "Baseline utility classes defined by kushi, override versions"
   
   "user-shared-overrides"
   "Baseline utility classes defined by kushi, override versions"))


(defn namespaces-with-matching-path
  "coll is a collection of namespace prefixes culled from the entrypoint of each
   module in the shadow-cljs build. If our build config contains this:
   {:modules {:main {:init-fn site.browser/init}}}
   coll would be a list like this:
   '(\"site\")"
  [coll [_ s]]
  (or (some #(string/starts-with? s %) coll)
      (string/starts-with? s "kushi/ui")
      (string/starts-with? s "kushi/playground")
      ;; (string/starts-with? s "kushi/css")
      (= s "kushi/core.cljs") ;; <- this is for using css-include to pull in
                              ;;    build/kushi-reset.css (or others for dev)
      (contains? #{"kushi/css/build/css_reset.cljs"
                   "kushi/css/build/kushi_ui_component_theming.cljs"
                   "kushi/css/build/legacy_color_tokens.cljs"}
                 s)))


(defn gather-macros [m1 m2 f]
  (->> m1 (merge m2) f (into #{})))


(defn namespaces-with-macro-usage
  [[[_ path]
    {{:keys [uses use-macros rename-macros renames] :as m} :ns-info}]]
  (!? 'namespaces-with-macro-usage m)
  (let [used-renamed-macros (gather-macros rename-macros renames vals)
        used-macros         (gather-macros uses use-macros keys)
        used-macros         (->> used-macros
                                 (set/union used-renamed-macros)
                                 (set/select #(contains? kushi-macros %)))]
    #_(when (seq used-macros)
      (? {:display-metadata? false :label path} used-macros))
    (seq used-macros)))


;; TODO - share with kushi.core
(defn- layer+sel [sel-og]
  (if (string/starts-with? sel-og "@layer")
    (let [[_ layer & sel-bits]
          (string/split sel-og #"[\t\n\r\s]+")]
      {:layer layer
       :sel   (string/join " " sel-bits)})
    {:sel sel-og})  )


(defn- loc-sel [x ns-str]
  (let [{:keys [row col]} (meta x)
        sel (str "."
                 (string/replace ns-str #"/" "_")
                 "__L"
                 row
                 "_C"
                 col)]
    {:sel sel}))


(defn resolve-css-layer-for-shared-classes [ns-str]
  (cond (re-find #"^kushi_ui" ns-str)
        "kushi-ui-styles"
        
        (re-find #"^kushi_playground" ns-str)
        "kushi-playground-styles"))
  
(defn initialize-layer-vector! [*css ns layer]
  (when-not (get-in @*css [:sources ns layer])

    ;; TODO this causes error in fireworks.core/formatted - investigate
    ;; (? (keyed [*css ns layer]))

    (vswap! *css
            assoc-in
            [:sources ns layer]
            [])))

(declare register-design-tokens!)
(declare vswap-design-tokens!)


(defn ^:public css-var [x]
  (if-let [s (let [nm (name x)]
               (when (and (keyword? x)
                          (or (string/starts-with? nm "$")
                              (string/starts-with? nm "--")))
                 nm))]
    (hydrated/hydrated-css-var s)
    x))


(defn register-design-tokens-call-data
  [{:keys [args]} *css] 
  (doseq [tok args]
    (when-let [tok (css-var tok)]
      (vswap-design-tokens! [tok] *css) )))


(defn register-design-tokens-by-category-call-data
  [{:keys [args]} *css] 
  (doseq [tag args]
    (when-let [toks (get design-tokens-by-category tag)]
      #_(when (= tag "popover")
        (? 'popover-toks toks))
      (vswap-design-tokens! toks *css))))


(defn css-call-data
  [{:keys [form ns-str ns-meta ns args] :as m} 
   *css] 
  (let [sel-og
        (-> args first (maybe string?))
        
        ;; debug?  (= ns 'kushi.ui.checkbox.demo)

        {:keys [layer sel]}
        (if sel-og
          (layer+sel sel-og)
          (loc-sel form ns-str))

        ;; layer_1
        ;; layer

        layer-from-ns-info
        (some-> ns-meta :kushi/layer)

        layer
        (or layer
            layer-from-ns-info
            (resolve-css-layer-for-shared-classes ns-str)
            "user-styles")

        ;; _ (when debug? (? :pp 
        ;;                   {:layer_1            layer_1
        ;;                    :layer_2            layer
        ;;                    :ns-meta            ns-meta
        ;;                    :layer-from-ns-info layer-from-ns-info
        ;;                    :macro              (first form)
        ;;                    :sel-og             sel-og}))

        kushi-utils
        (filter #(when (clojure.spec.alpha/valid? ::kushi-specs/class-kw %)
                   ;; TODO - just use the utility-classes map?
                   (contains? utility-classes/utility-class-ks-set (name %)))
                args)

        result
        (merge m
               (meta form)
               (keyed [sel-og sel args layer kushi-utils]))] 

    (some->> kushi-utils
             seq
             (vswap! *css
                     update-in
                     [:utils :used/kushi-utility]
                     conj))

    (initialize-layer-vector! *css ns layer)

    (vswap! *css
            update-in
            [:sources ns layer]
            conj
            result)

    nil))

(defn- hydrated-class-kw-callout
  [class-kw? util-args x *css rel-path form]
  (when class-kw?
    (if (seq util-args)
      (let [s    (name x)
            kind (cond (get utility-classes/utility-classes s)
                       :kushi-utility
                       (get-in @*css [:utils :kushi-ui-shared s])
                       :kushi-ui-shared
                       (get-in  @*css [:utils :user-shared s])
                       :user-shared
                       :else
                       :unknown)]
        (callout {:type  :white
                  :label (bling [:neutral "Hydrating "]
                                [:olive.bold x]
                                " from "
                                [:blue.italic kind])}))

      ;; TODO - augment this callout and make standard for dev
      (callout {:type        :warning
                :padding-top 1}
               (bling [:italic.neutral (str rel-path
                                            ":"
                                            (-> form meta :row)
                                            ":"
                                            (-> form meta :col))]
                      "\n\n"
                      [:neutral "No utility or shared class found for "]
                      [:olive.bold x])))))


(defn defcss-mixin? [x]
  (clojure.spec.alpha/valid? ::kushi-specs/class-kw x))

(defn hydrate-util-args*
  "Recursively hydrates defcss calls. Args to defcss which are keywords that  
   match the name of rulesets that were previously defined with defcss will be
   hydrated into a list of the original args."
  [*css *util-args args]
  (doseq [x args]
    (let [mixin?     
          (defcss-mixin? x)

          redundant-mixin?
          (!? (str "Is " x " redundant?\n")
              (and mixin? (contains? (:seen @*util-args) x)))

          mixin-args
          (when (and mixin? (not redundant-mixin?))
            (let [s   (name x)]
              (or (get utility-classes/utility-classes s)
                  (get-in @*css [:utils :kushi-ui-shared s])
                  (get-in  @*css [:utils :user-shared s]))))]
      
      (if (seq mixin-args)
        (do
          (vswap! *util-args update-in [:seen] conj x)
          (hydrate-util-args* *css *util-args mixin-args))

        (when-not redundant-mixin?
          (vswap! *util-args update-in [:acc] conj x))))

    args))

(defn- hydrated-util-args
  [args *css sel form]
  (let [dbg? false #_(contains?
              #{"@layer kushi-ui-shared .kushi-pane-t"
                "@layer kushi-ui-shared .kushi-pane-block-arrow-offset-mixin"}
              (second form))]
    (if (some defcss-mixin? args)
      (let [*util-args (volatile! {:seen #{(keyword sel)} :acc []})]
        (hydrate-util-args* *css *util-args args)
        (!? {:when (fn [_] dbg?)} (:acc @*util-args)))
      args)))

(defn- defcss-callout [sel k]
  (callout {:type          :neutral
            :border-weight :medium
            :margin-top    2
            :label         (bling [:bold.italic "defcss "]
                                  [:bold.magenta sel]
                                  " "
                                  [:italic.blue k])}))

(defn defcss-call-data
  [{:keys [args form ns-str ns-meta ns rel-path]
    :as m}
   *css]
  (let [[sel-og & args]
        args

        {:keys [layer sel]}
        (layer+sel sel-og)

        layer-from-defcss-first-arg
        layer

        layer-from-ns-info
        (some-> ns-meta :kushi/layer)

        layer
        (or layer
            layer-from-ns-info
            "user-shared-styles")

        _
        (when (string/starts-with? sel ".")
          (let [k (cond (string/starts-with? ns-str "kushi_ui")
                        :kushi-ui-shared

                        (string/starts-with? ns-str "kushi_playground")
                        :kushi-playground-shared

                        :else
                        :user-shared)]
            (when (contains? debugging :macro-hydration)
              (defcss-callout sel k))
            (vswap! *css update-in [:utils k] assoc sel args)))

        args
        (hydrated-util-args args *css sel form)

        result
        (merge m
               (meta form)
               (keyed [sel-og sel args layer]))
        
        ;; dbg?
        ;; (= sel ".kushi-slider-step-label-marker")
        ]

    ;; (when dbg?
    ;;   (keyed [layer-from-defcss-first-arg
    ;;           layer-from-ns-info
    ;;           layer])

    ;;   #_(let [coll (get-in @*css [:sources ns layer])]
    ;;     (? :result [sel (some-> coll count)])
    ;;     (!? :result (get-in @*css [:sources ns layer]))))

    (initialize-layer-vector! *css ns layer)

    (vswap! *css
            update-in
            [:sources ns layer]
            conj
            result)
    nil))


;; TODO - add error boundery here
(defn parse-all-forms [file]
  (-> file
      slurp
      (e/parse-string-all {:fn           true
                           :regex        true
                           :quote        true
                           :syntax-quote true
                           :readers      {'js (fn [v] (list 'js v))}})
      rest))

(defn filter-build-sources [bs]
  (let [project-namespace-prefixes-to-analyze
        (some->> bs
                 :shadow.build/config
                 :modules
                 (reduce-kv (fn [acc _ {:keys [init-fn]}]
                              (conj acc (-> init-fn
                                            str
                                            (string/split #"/")
                                            first
                                            (string/split #"\.")
                                            first)))
                            [])
                 (into #{}))]
    (->> bs
         :build-sources
         (filter (partial namespaces-with-matching-path
                          project-namespace-prefixes-to-analyze))
         (reduce (fn [acc k]
                   (conj acc [k (get (:sources bs) k)]))
                 [])
         (filter namespaces-with-macro-usage)
         (sequence cat)
         (apply array-map))))

(defn- css-file-path [layer ns-str]
  (let [path (str (:css-dir user-config*) "/" layer "/" ns-str ".css")]
    (io/make-parents path)
    path))

(defn- stage-callout 
  ([label]
   (stage-callout label nil))
  ([label attrs]
   (callout (merge {:type          :magenta
                    :border-weight :medium
                    :margin-bottom 0}
                   attrs)
            (bling [:italic.magenta.bold (str label)]))))


(defn- css-vars-re-seq [s]
  (re-seq #"var\((--[^_][^\)\, ]+)" s))


(defn- css-var-kw? [x]
  (and (keyword? x) (string/starts-with? (name x) "$")))


(defn- css-var-str [v]
  (when (css-var-kw? v)
    (str "var(--" (subs (name v) 1) ")")))


(defn print-ns-containing-design-token [design-token css ns]
  (let [design-token design-token]
      (when (re-find (re-pattern design-token) css)
        (? design-token  ns))))


(defn vswap-design-tokens! [toks *css]
  (vswap! *css
          update-in 
          [:used/design-tokens] 
          (fn [coll args] (apply conj coll args))
          toks))

;; could be 
(defn- register-design-tokens!
  [css-str *css ns]
  #_(when (contains? debugging :design-token-registration)
      (print-ns-containing-design-token "--button-border-width" css ns))
  (let [toks (some->> css-str
                      css-vars-re-seq
                      (map #(nth % 1 nil))
                      seq)]
    (some-> toks (vswap-design-tokens! *css))))


(defn- css-includes-block [css-includes ns]
  (when-let [css-includes (seq css-includes)]
    (str "/* css-includes via " (str ns) " start --------- */\n\n"
         (string/join "\n\n" css-includes)
         "/* css-includes via " (str ns) " end --------- */\n\n")))


(defn- ruleset->css
  [{:keys [sel args form row col rel-path]}]
  (let [css (css-rule* sel
                       args
                       (with-meta form
                         {:line   row
                          :column col
                          :file   rel-path})
                       nil)]
    [css :others]))

(defn new-toks-callout-template [s ns layer]
  (callout {:type          :subtle
            :margin-bottom 0
            :label         (bling [:italic.neutral s]
                                  [:orange.italic ns]
                                  ", "
                                  [:blue.italic layer]
                                  "\n ")}))

(defn new-toks-callout [ns layer used-toks *css]
  (let [new-toks
        (set/difference (:used/design-tokens @*css)
                        used-toks)]
    (when (contains? new-toks :$button-border-width)
      (? new-toks ns))
    #_(if (seq new-toks) 
      (do (new-toks-callout-template "Registering design tokens for " ns layer)
          (? :result new-toks))
      (new-toks-callout-template  "No design tokens for " ns layer))))


;; TODO css-fp should be renamed because of clash with entry in rulesets
(defn- spit-css-file
  [css-fp layer rulesets *css]
  (let [debug?
        (contains? debugging :design-token-registration)

        ns 
        (some-> rulesets seq first :ns)

        used-toks
        (when debug? (:used/design-tokens @*css))

        {:keys [css-includes others]}
        (reduce (fn [acc {:keys [css]
                          :as   ruleset}]
                  (let [[css k]
                        (or (some-> css (vector :css-includes))
                            (ruleset->css ruleset))]
                    
                    (update-in acc [k] conj css)))
                {:css-includes []
                 :others       []}
                rulesets)

        css-str
        (str (css-includes-block css-includes ns)
             (string/join "\n\n" others))]

    ;; This is where design tokens get registered
    ;; They are identified based on the the actual css-rules produced

    (register-design-tokens! css-str *css ns)
    (when debug? 
      (when (= ns 'kushi.ui.button.core)
        (new-toks-callout ns layer used-toks *css)))
    #_(when (re-find #"design-tokens" css-fp)
      (? :pp css-fp))
    (spit css-fp
          (str (css-includes-block css-includes ns)
               (string/join "\n\n" others))
          :append false)))


(defn bs-epoch [build-state]
  (let [init? true
        ;; Check if files are new or deleted
        ;; Check if any css imports within namespaces added or deleted
        deleted? false
        added? false
        ;; Check if files are new or deleted
        ;; Check if any css imports within namespaces added or deleted
        new-or-deleted? true #_(or deleted? added?)
        ;; Check if any css imports within namespaces changed
        ;; Check if any css namespaces changed
        existing-css-changed? true]
    (keyed [init? deleted? added? new-or-deleted? existing-css-changed?])))

(defn css-include-call-data
  [{:keys [args form ns-str ns-meta ns file url] :as m}
   *css]
  (let [sel-og                 (first args)
        {css-file-path :sel
         layer         :layer} (layer+sel sel-og)

        ;; TODO incorporate this
        ;; layer-from-ns-info
        ;; (some-> ns-meta :kushi/layer)
        
        ;; layer
        ;; (or layer
        ;;     layer-from-ns-info ;; <- layer assigned at ns -level
        ;;     (resolve-css-layer-for-shared-classes ns-str)
        ;;     "user-shared-styles")
        

        ;; TODO - Currently assumes css is file in same dir as current
        ;;        ns of form. Check for relative filepath.
        layer (or layer
                  (resolve-css-layer-for-shared-classes ns-str)
                  "user-shared-styles")

        ;; TODO - is there a more efficient way to do this path manipulation?
        ;; Add error boundry here
        css-fp (-> file
                   .getPath
                   (string/split #"/")
                   drop-last
                   (->> (string/join "/"))
                   (str "/" css-file-path))

        ;; TODO - add try/catch to this slurp + issue warning if file-not-found
        css    (slurp (? css-fp))
        result (merge m (meta form) (keyed [sel-og css css-fp args layer]))]
     (initialize-layer-vector! *css ns layer)
     (vswap! *css update-in [:sources ns layer] conj result)
     nil))


(declare layer+css-path)


(defn- analyze-forms
  [tl-form
   {:keys [*css ns-str rel-path ns ns-meta file url]
    :as tl-form-data}]
  (walk/prewalk
   (fn [form] 
     (let [[macro-sym & args] (when (list? form) form)
           kushi-macro?       (contains? kushi-macros macro-sym)]
       (if kushi-macro? 
         ;; TODO - possibly just (merge tl-form-data (keyed macro-sym args))
         ;;      - then change sig of fns in cond branch
         (let [m (keyed [form
                         ns-str
                         ns
                         ns-meta
                         rel-path
                         file
                         url
                         macro-sym
                         args])]
           (cond 
             (contains? '#{?css-include css-include} macro-sym)
             (css-include-call-data m *css)

             (contains? '#{?defcss defcss} macro-sym)
             (defcss-call-data m *css)

             (contains? '#{register-design-tokens} macro-sym)
             (register-design-tokens-call-data m *css)

             (contains? '#{register-design-tokens-by-category} macro-sym)
             (register-design-tokens-by-category-call-data m *css)

             (contains? '#{?css css ?sx sx} macro-sym)
             (css-call-data m *css))
           ;; prewalk return nil for perf
           nil)
         form)))
   tl-form))


(defn- write-css-files+layer-profile
  [{:keys [*css ns ns-str msg]}]
  (let [reified-*css
        @*css

        ret-new
        (reduce 
         (fn [acc layer]
           #_(when (re-find #"design-tokens" layer)
             (? :pp layer))
           (if-let [rulesets (get-in reified-*css [:sources ns layer])]
             (let [css-fp (css-file-path layer ns-str)
                  ;;  dbg? (= layer "css-reset")
                   ]
               #_(when dbg? (? rulesets))
               (spit-css-file css-fp layer rulesets *css)
               (vswap! msg 
                       str
                       "\nLayer "
                       (bling [:blue layer])
                       ", writing "
                       (bling [:olive css-fp]))
               (update-in acc [layer] merge (keyed [css-fp ns rulesets])))
             acc))
         {}
         (keys kushi-layers))]
    ret-new))

(defn- analyze-sources
  [*css
   acc
   [[_ rel-path] {:keys [ns file url ns-info] :as m-}]]
  (let [ns-str    (string/replace (str ns) #"\." "_")
        ns-meta   (:meta ns-info)
        all-forms (parse-all-forms (or file url))
        m         (keyed [*css ns ns-str ns-meta rel-path file url])
         
        ;; dbg? (= ns
        ;;         #_'kushi.ui.text-field.core
        ;;         'kushi.playground.shared-styles)
        ]

    #_(do (println "  " ns)
        (add-tick! "    parse-all-forms"))
    #_(when dbg? (? all-forms))
    #_(stage-callout ns)

    ;; Currently can't build up state because we are using prewalk in
    ;; analyze-forms to mutate css-data.
    (doseq [tl-form all-forms]
      (analyze-forms tl-form m))

    ;; #_(add-tick! "    analyze-forms")

    ;; TODO - maybe this should be broken out into another step and
    ;; anaylze-sources should just return mutated css-data volatile
    ;; should css-data and *css be the same thing?
    ;; css-data could be map like this, forget about ordering:
    ;; {:sources {mvp.browser {"user-styles"        {...}
    ;;                         "user-shared-styles" {...}}}
    ;;  :utils   {"user-shared-styles"        {".my-shared-style" '(...) } ;;
    ;;            "kushi-ui-shared" {".pane-arrow" '(...) }}


    ;; Iterate thru layers within namespace and write css files e.g.: 
    ;; public/css/kushi_ui_component/kushi_ui_icon_core.css
    ;; public/css/user-shared-styles/mvp_browser.css
    ;; public/css/user-styles/mvp_browser.css
    (let [msg (volatile! (str "Analyzing source for " ns "..."))
          ret (conj acc (write-css-files+layer-profile (assoc m :msg msg)))]
      #_(when (= ns 'kushi.ui.text-field.core)
          (? 'analyze-sources/ret ret))

      #_(do (add-tick! "    write-css-files+layer-profile")
            (println ""))

      (when (contains? debugging :narrative)
        (callout {:margin-top    0
                  :margin-bottom 0}
                 (bling @msg)))
      ret)))


(defn dep-toks-seq [v]
  (some->> (or (css-var-str v)
               (when (string? v) v))
           css-vars-re-seq
           (map second)
           seq))


(defn- dep-toks-set [tok m]
  (some->> tok m (dep-toks-seq) (into #{})))


;; Need to figure out policy for keys of the design maps
;; Should they be strings or keywords, probably strings
(defn unique-toks [tok *css]
  (when-not (or (contains? (:required/kushi-design-tokens @*css) tok)
                (contains? (:required/user-design-tokens @*css) tok))
   (let [dep-toks
         (or (dep-toks-set tok design-tokens-by-token)
             (dep-toks-set tok (:theme/user-design-tokens @*css)))]
     (some-> dep-toks
             (set/difference (:required/kushi-design-tokens @*css))
             seq))))


(defn register-toks+deps [tok *css] 
  (if-let [[tok-val k]
           (let [kushi-tok
                 (get design-tokens-by-token tok)
                 
                 user-tok
                 (get (:theme/user-design-tokens @*css) tok)]
                     (or (some-> kushi-tok (vector :kushi))
                         (some-> user-tok (vector :user))))]

    ;; TODO - make sure this order of doseq then vswap! is correct
    (do (when-let [uniques (unique-toks tok *css)]
          (doseq [dep-tok uniques]
            (register-toks+deps dep-tok *css)))
        (vswap! *css
                update-in
                [:required/kushi-design-tokens]
                conj
                [tok tok-val]))
    (vswap! *css
            update-in
            [:not-found/design-tokens]
            conj
            tok)))


(defn- spit-css-layer+profile [path css k]
  (io/make-parents path)
  (spit path css :append false)
  {k {:css-fp path}})


(defn layer+css-path
  ([layer]
   (layer+css-path layer layer))
  ([layer filename]
   (let [css-base (:css-dir user-config*)]
     [layer
      (str css-base "/" layer "/" filename ".css")])))


(def design-tokens-layer-name
  "design-tokens")


(def user-design-tokens-layer-name
  "user-design-tokens")


(def kushi-utility-overrides-layer-name
  "kushi-utility-overrides")


(def kushi-utility-classes-layer-name
  "kushi-utility")


;; TODO - Maybe introduce :release? and/or init? for perf?
;; Currently this is registering all user-design tokens for both dev and
;; release builds
(defn user-design-tokens-profile-all
  [*css]
  (let [[layer path] (layer+css-path user-design-tokens-layer-name)]
    ;; TODO - put this back in for stats?
    #_(let [toks (:used/design-tokens @*css)]
      (doseq [tok toks]
        (register-toks+deps tok *css)))
    (spit-css-layer+profile
     path 
     (css-rule* ":root"
                [(:theme/user-design-tokens @*css)]
                nil
                nil)
     layer)))


;; TODO - maybe unite this with fn below and pass a map with :release? and :init?
#_(defn design-tokens-profile-all
  [*css]
  (let [[layer path] (layer+css-path design-tokens-layer-name)]
    ;; TODO - put this back in for stats?
    #_(let [toks (:used/design-tokens @*css)]
      (doseq [tok toks]
        (register-toks+deps tok *css)))
    (spit-css-layer+profile
     path 
     (css-rule* ":root"
                [design-tokens-by-token-array-map]
                nil
                nil)
     layer)))


(defn design-tokens-profile
  [*css {:keys [release? init?]}]

  ;; Maybe do this during dev as well?
  (when release?
    (let [toks (:used/design-tokens @*css)]
      (doseq [tok toks]
        (register-toks+deps tok *css))))

  (let [[layer path] (layer+css-path design-tokens-layer-name)
        tokens-coll (if release? 
                      (:required/kushi-design-tokens @*css)
                      [design-tokens-by-token-array-map])]
    (spit-css-layer+profile 
     path 
     (css-rule* ":root"
                tokens-coll
                nil
                nil)
     layer)))


(defn kushi-utility-classes-overrides-profile-all
  []
  (let [[layer path] (layer+css-path kushi-utility-overrides-layer-name)
        util-classes (apply array-map
                            (apply concat 
                                   utility-classes/all-classes))     
        css          (string/join 
                      "\n\n"
                      (for [[class v] util-classes
                            :let      [classname (str (name class) "\\!")]
                            :when     (not (re-find #"\]$" (name class)))]
                        (css-rule* classname
                                   [v]
                                   nil
                                   nil)))]
    (spit-css-layer+profile path css layer)))



(defn kushi-utility-classes-overrides-profile
  [*css]
  (let [reified (reduce (fn [acc coll]
                          (apply conj acc coll))
                        #{}
                        (-> @*css :utils :used/kushi-utility))]
    (when (contains? debugging :narrative)
      ;; TODO - Add callout here 
      nil)

    (when (!? :used/kushi-utility (seq reified))
      (let [[layer path] (layer+css-path kushi-utility-overrides-layer-name)
            css          (string/join 
                          "\n\n"
                          (for [class reified
                                :let  [classname (str (name class) "\\!")]
                                :when (not (re-find #"\]$" (name class)))]
                            (css-rule* classname
                                       [(get utility-classes/utility-classes
                                             (name class))]
                                       nil
                                       nil)))
            debug-toks? (contains? debugging :design-token-registration)
            used-toks   (when debug-toks?
                          (:used/design-tokens @*css))]

        ;; This is where design tokens for utility classes get registered.
        ;; They are identified based on the the actual css-rules produced.
        (register-design-tokens! css *css :kushi-utility)
        #_(when debug-toks? (new-toks-callout "kushi-utility"
                                              nil
                                              used-toks
                                              *css))
        (spit-css-layer+profile path css layer)))))


(defn kushi-utility-classes-profile-all
  [*css]
  (let [[layer path] (layer+css-path kushi-utility-classes-layer-name)
        util-classes (apply array-map
                            (apply concat 
                                   utility-classes/all-classes))     
        css          (string/join 
                      "\n\n"
                      (for [[class v] util-classes
                            :let      [classname (name class)]]
                        (css-rule* classname
                                   [v]
                                   nil
                                   nil)))]
    (register-design-tokens! css *css :kushi-utility)
    (spit-css-layer+profile path css layer)))


(defn kushi-utility-classes-profile
  [*css]
  (let [reified (reduce (fn [acc coll]
                          (apply conj acc coll))
                        #{}
                        (-> @*css :utils :used/kushi-utility))]

    (when (contains? debugging :narrative)
      ;; TODO - Add callout here 
      nil)

    (when (!? :used/kushi-utility (seq reified))
      (let [[layer path] (layer+css-path kushi-utility-classes-layer-name)
            css          (string/join 
                          "\n\n"
                          (for [class reified
                                :let  [classname (name class)]]
                            (css-rule* classname
                                       [(get utility-classes/utility-classes
                                             classname)]
                                       nil
                                       nil)))
            debug-toks?  (contains? debugging :design-token-registration)
            used-toks    (when debug-toks? (:used/design-tokens @*css))]

        ;; This is where design tokens for utility classes get registered.
        ;; They are identified based on the the actual css-rules produced.
        (register-design-tokens! css *css :kushi-utility)
        #_(when debug-toks? (new-toks-callout "kushi-utility"
                                              nil
                                              used-toks
                                              *css))
        (spit-css-layer+profile path css layer)))))


(defn spit-css-imports [coll]
 (spit (str (:css-dir user-config*) "/" (:css-filename user-config*) ".css")
       (str "/* Kushi build system - development build */\n\n"
            (string/join 
             "\n\n"
             (map (fn [[[layer layer-desc] css-files]]
                    #_(when (= layer "kushi-utility") 
                      (? layer css-files))
                    (str 
                     "/* " layer "\n   " layer-desc "\n*/\n"
                     (string/join
                      "\n"
                      (map (fn [m]
                             (str "@import \""
                                  (string/replace
                                   (:css-fp m)
                                   (re-pattern (str "^"
                                                    "\\"
                                                    (:css-dir user-config*)
                                                    "/"))
                                   "")
                                  "\";"))
                           css-files))))
                  coll)))
       :append false) )


;; just for dev start --------------------------------
(declare build-sources-callout)
(declare analyzed-callout)
(declare design-token-summary-callout)
(declare spit-filtered-build-sources-with-paths)
;; just for dev end ----------------------------------

(defn- user-design-tokens [theme]
  (some->> theme
           :design-tokens
           (map-indexed (fn [i v]
                          (if (even? i)
                            (name v)
                            (or (css-var-str v)
                                (some-> v (maybe keyword?) name)
                                v))))
           (apply hash-map)))


;; TODO - is there a difference between :used/ and :required/
(def *css-state
  (array-map
   :base/kushi-design-tokens
   {:desc  "Base design tokens defined in Kushi lib. See kushi.css.build.tokens ns."
    :value design-tokens-by-token}

   :theme/user-design-tokens 
   {:desc  "Design tokens defined in user theme. These could be overrides for tokens in :tokens/kushi-base, or the user's own custom global design tokens."
    :value {}}

   :theme/user-ui
   {:value {}}

   :user-shared                  
   {:desc "CSS rules defined by user, to be shared across namespaces."
    :value {}}

   :kushi-ui-shared             
   {:desc "CSS rules defined within kushi.ui.*, to be shared across namespaces."
    :value {}}

   :used/kushi-utility           
   {:desc ""
    :value []}

   :used/kushi-ui-shared         
   {:desc ""
    :value []}

   :used/user-shared             
   {:desc ""
    :value []}

   :used/design-tokens           
   {:desc ""
    :value #{}}

   :required/kushi-design-tokens 
   {:desc ""
    :value #{}}

   :required/user-design-tokens   
   {:desc ""
    :value #{}}

   :not-found/design-tokens      
   {:desc "Design tokens referenced in styling, but not defined."
    :value #{}}))

(defn hook* [config filtered-build-sources release?]
  (!? 'kushi.core/hook*:config config)
  (let [user-design-tokens
        (user-design-tokens (:theme config))

        _ (add-tick! "user-design-tokens")

        *css
        (volatile!
         (-> (assoc (reduce-kv (fn [m k v]
                                 (assoc m k (:value v)))
                               {} 
                               *css-state)
                    :theme/user-design-tokens
                    user-design-tokens)))

        _ (add-tick! "creating *css state")

        ;; Just for creating a new mock build-state to use for dev
        ;; _ (spit-filtered-build-sources-with-paths filtered-build-sources)
        
        
        reduced-sources
        (reduce (partial analyze-sources *css)
                []
                filtered-build-sources)

        _ (add-tick! "reducing filtered build sources")

        ;; just for dev
        ;; _ (analyzed-callout reduced-sources *css)
        
        kushi-utility-classes-profile
        (if release?
          (kushi-utility-classes-profile *css)
          (kushi-utility-classes-profile-all *css)) 

        _ (add-tick! "utility-classes-profile")

        kushi-utility-classes-overrides-profile
        (if release?
          (kushi-utility-classes-overrides-profile *css)
          (kushi-utility-classes-overrides-profile-all)) 

        _ (add-tick! "utility-classes-overrides-profile")

        ;; TODO - is this order-dependent?
        design-tokens-profile
        (design-tokens-profile *css {:release? release?})

        _ (add-tick! "design-tokens-profile")

        ;; Currently this is registering all user-design tokens for both dev and
        ;; release builds
        user-design-tokens-profile
        (user-design-tokens-profile-all *css)

        _ (add-tick! "user-design-tokens-profile")
        ret
        (conj reduced-sources
              design-tokens-profile
              kushi-utility-classes-profile
              kushi-utility-classes-overrides-profile
              user-design-tokens-profile
              #_user-shared-classes-profile)]

    (add-tick! "conjing to reduced sources")
    (when (contains? debugging :design-token-registration)
      (design-token-summary-callout *css))

    ret))

(defn- lightning-bundle
  [{:keys [input-path output-path]}
   flags]
  (apply shell (concat ["npx" "lightningcss"]
                       flags
                       [input-path "-o" output-path])))

(defn lightning-bundle-warning [e flags opts]
  (let [body (bling "Error when creating CSS bundle via lightningcss-cli."
                    "\n\n"
                    [:italic.subtle.bold "User options from kushi.edn:"]
                    "\n"
                    (with-out-str (fireworks.core/pprint opts))
                    "\n\n"
                    [:italic.subtle.bold
                     "Flags passed to lightningcss:\n"]
                    (with-out-str (fireworks.core/pprint flags))
                    "\n\n"
                    [:italic.subtle.bold
                     "No css bundle was written."])] 
    (callout
     (merge opts
            {:type        :error
             :label       (str "ERROR: "
                               (string/replace (type e) #"^class " "" )
                               " (Caught)")
             :padding-top 1})
     body)))

(defn lightning-path [filename-key]
  (str (:css-dir user-config*) "/" (filename-key user-config*) ".css"))

(def lightning-options-via-user-config
  {:input-path  (lightning-path :css-filename)
   :output-path (lightning-path :css-bundle-filename)
   :targets     ">= 0.25%"})

(defn bad-bundle-path-warning
  [{:keys [k must-be supplied extra]
    :or {k :input-path}}]
  (bling "The value of "
         [:bold (str k)]
         (or must-be " must be a string, and a valid file path.")
         "\n\n"
         [:italic.subtle.bold "Supplied value:\n"]
         supplied
         extra))

(defn- create-css-bundle []
  (let [opts  lightning-options-via-user-config
        flags (kushi.core/lightning-cli-flags
               (dissoc opts :input-path :output-path)
               (-> kushi.core/lightning-opts
                   (dissoc :browserslist)
                   (assoc :bundle true)))
        in    (:input-path opts)
        out   (:output-path opts)
        fp?   #(clojure.spec.alpha/valid? ::kushi-specs/css-file-path %)]
    (if (and (fp? in) (fp? out))
      (try (lightning-bundle opts flags)
           ;; TODO - test this exception handler
           (catch Exception e
             (lightning-bundle-warning e flags opts)))
      (let [body
            (cond
              (not (fp? in))
              (bad-bundle-path-warning
               {:supplied [:bold (if (string? in) (str "\"" in "\"") in)]})
              
              (not (.exists? (io/as-file in)))
              (bad-bundle-path-warning
               {:must-be  "must be a path to an existing css file."
                :supplied in
                :extra    (str "\n\n" "File not found")})
              
              (not (fp? out))
              (bad-bundle-path-warning
               {:k        :output-path
                :supplied out
                :extra    (str "\n\n" "File not found")})

              :else
              (bling "Problem with"
                     [:bold " :input-path"]
                     "entry or"
                     [:bold " :output-path"]
                     "entry."))]
        (callout {:type :warning}
                 (bling [:yellow.bold "[kushi.css.build/create-css-bundle]"]
                        "\n\n"
                        body))))))


(defn- user-config [path]
  (some-> path slurp read-string))


(defn write-css-imports* [coll]
  (->> kushi-layers
       (reduce (fn [acc [layer layer-desc]]
                 (conj acc [layer layer-desc] (keep #(get % layer) coll)))
               [])
       (apply array-map)
       spit-css-imports))


(defn write-css-imports [path filtered-build-sources build-state]
  (->  path
       user-config
       (hook* filtered-build-sources build-state)
       write-css-imports*))


(defn hook2
  {:shadow.build/stage :compile-finish}
  [{:keys [:shadow.build/build-id
           :shadow.build/build-info
           :build-sources
           :sources
           :output]
    :as   build-state}]
  ;; (println "compile-finish")
  ;; (? :pp (count build-sources))
  ;; (? :pp (count sources))
  ;; (? :pp (count output))
  ;; (? :pp output)
  build-state)

(defn hook
  {:shadow.build/stage :compile-prepare}
  [{:keys [:shadow.build/build-id
           :shadow.build/build-info
           :build-sources
           :sources
           :output]
    :as build-state}]
  ;; TODO maybe deleted? and added? should be seqs or nils
  (let [{:keys [init? new-or-deleted? existing-css-changed?]}
        (bs-epoch build-state)]
    (when (or existing-css-changed? new-or-deleted? init?)
      (let [filtered-build-sources
            (filter-build-sources build-state)
            
            release?
            (not= :dev (:shadow.build/mode build-state))]

        ;; (? :pp (keys filtered-build-sources))
        (? :pp (count filtered-build-sources))
        (doseq [resource-id (keys filtered-build-sources)]
          (let [recompiled? (not (boolean (get-in build-state [:output resource-id])))
                color       (if recompiled? :gray :green)]
            (println (bling [{:color color} (second resource-id)]))))

        ;; If necessary write the css imports chain
        ;; (add-tick! "Filtered build sources")
        (when (or init? new-or-deleted?)
          (write-css-imports "./kushi.edn"
                             filtered-build-sources 
                             release?)
          ;; (add-tick! "Analyzed sources and wrote css imports")
          (create-css-bundle))))

    ;; (? :pp build-info)
    ;; (println)
    ;; (? :pp (count build-sources))
    ;; (? :pp (count sources))
    ;; (? :pp build-sources)
    ;; (? :pp (count output))
    ;; (? :pp (keys output))
    ;; (? :pp output)

    ;; (println (tick-msg "Finished kushi.build.analyze/hook"
    ;;                    (first @ticks) 
    ;;                    (tick/instant)
    ;;                    (:build-id build-state)))
    build-state))

#_(try (lightning-bundle opts flags)
           ;; TODO - test this exception handler
           (catch Exception e
             (lightning-bundle-warning e flags opts)))

(defn hook-dev
  "Just for dev"
  [filtered-build-sources release?]
  ;; TODO maybe deleted? and added? should be seqs or nils
  (write-css-imports (str "./" dev-sample-proj-dir "/kushi.edn" )
                     filtered-build-sources release?)
  #_(create-css-bundle))


(defn- design-token-summary-callout [*css]
  (callout {:type  :subtle
            :label (bling 
                    [:italic "Total number of registered design-tokens"])} 
           (bling [:orange.bold (-> @*css :used/design-tokens count)]))
  (callout {:type  :subtle
            :label (bling 
                    [:italic "Total number of kushi design-tokens"])} 
           (bling [:orange.bold (-> @*css
                                    :required/kushi-design-tokens
                                    count)]))
  (callout {:type  :subtle
            :label (bling 
                    [:italic "Total number of design tokens not found"])} 
           (bling 
            [:orange.bold (count (:not-found/design-tokens @*css))]
            "\n\n"
            [:italic
             "These design tokens do not reference kushi library tokens,\n"]
            [:italic
             "or design tokens from the user theme. They are most likely\n"]
            [:italic
             "referencing locally scoped css-vars.\n"]
            "\n"
            (-> (? :data (:not-found/design-tokens @*css))
                :formatted
                :string))))


(defn analyzed-callout
  "Just for dev"
  [reduced-sources *css]
  (when (contains? debugging :narrative)  
    (do (stage-callout "ANALYZED SOURCES" {:margin-top 1 :margin-bottom 1})
        (? {:label
            (str 
             "Each source has been analyzed and produces a map with entries\n"
             "corresponding to a named CSS layer, defined in `kushi-layers`.\n\n"
             "Each of these entries has a `:rulesets` entry, which contains\n"
             "data from the macros calls that was used to write the css rules.")}
           (->> reduced-sources
                (reduce (fn [acc m]
                          (conj acc
                                (walk/postwalk (fn [x]
                                                 (if (seq? x) '(...) x))
                                               m)))
                        []))))))

(defn build-sources-callout
  "Just for dev"
  [filtered-build-sources]
  (stage-callout "FILTERED BUILD SOURCES" {:margin-top 1 :margin-bottom 1})
  (? {:label
      (str 
           "These are sources that use kushi macros\n\n"
           "This structure of the keys and values is from shadow-cljs\n"
           "It has been cast to an array map, ordered by dependency order\n")}
     (->> filtered-build-sources
          (reduce (fn [acc [k v]]
                    (conj acc
                          [k {'... '...}]))
                  []
                  )
          (sequence cat)
          (apply array-map))))

(defn hydrate-paths-into-files
  "Just for dev"
  [m]
  (->> m
       (reduce (fn [acc [k v]]
                 ;; TODO use refactor below
                 (conj acc #_[k (assoc v :file (clojure.java.io/file (:file v)))]
                       [k (-> v
                              (assoc :file
                                     (-> v
                                         :file
                                         clojure.java.io/file)))]))
               [])
       (sequence cat)
       (apply array-map)))

(def dev-mock-filtered-build-sources-path
  (str "./" dev-sample-proj-dir "/dev/mock/filtered-build-sources.edn"))

;; Dev with lein-refresh
;; TODO - create a fake :build.state/mode here?
#_(let [release?               false
      filtered-build-sources (-> dev-mock-filtered-build-sources-path
                                 slurp
                                 read-string
                                 hydrate-paths-into-files
                                 (hook-dev release?))])

(defn- spit-filtered-build-sources-with-paths 
  "Just for dev"
  [filtered-build-sources]
  (let [filtered-build-sources-with-paths
        (->> filtered-build-sources
             (reduce (fn [acc [k v]]
                       (conj acc
                             [k (-> v
                                    (assoc :file (-> v :file .getPath))
                                    (dissoc :url))]))
                     [])
             (sequence cat)
             (apply array-map))

        _                                 
        (spit (? "Spitting dev-mock-filtered-build-sources edn file to:"
                  "dev/mock/filtered-build-sources.edn")
              (with-out-str (pprint filtered-build-sources-with-paths))
              :append false)]))

;; namespaces-using-kushi-macros is an array-map, produced by filtering
;; the build-state for namespaces which pull in kushi.core macros
;;
;; Example entry:
;; {[:shadow.build.classpath/resource "mvp.browser.cljs"]
;;  {...}}

;; namespaces-using-kushi-macros
;; (filter-build-sources build-state)


;; (when deleted? 
;;   ;; Remove corresponding css files or deleted
;;   ;; probably don't need to delete exising css files that are imported
;;   ;;     unless they are copied into the public/css dir
;;    )
