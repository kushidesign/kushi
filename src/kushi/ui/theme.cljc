(ns ^:dev/always kushi.ui.theme
  (:require
   [kushi.parstub :refer [!?+ ?+]]
   [kushi.config :refer [user-config]]
   [kushi.utils :as util :refer [keyed]]
   [kushi.shorthand :as shorthand]
   [clojure.string :as string]))

(defn resolve-user-theme
  ([x]
   (resolve-user-theme x :user))
  ([x kw]
   (when x
     (try (let [[ns-name themevar] (string/split (str x) #"/")]
            (require (symbol ns-name) :reload)
            (let [bar (find-ns (symbol ns-name))]
              (var-get (ns-resolve bar (symbol themevar)))))
          (catch Exception
                 e
            (println
             (str
              "\n[kushi.core/theme!][ERROR]\n"
              (if (= :user kw)
                "Possibly a bad theme ns specified in kushi.edn user config -> "
                "Can't resolve base kushi ux theme -> ")
              (str x)
              (when (= :user kw)
                (str
                 "\n"
                 "Or possibly a malformed def -> "
                 (last (string/split (str x) #"/"))))
              "\n" (.getMessage e) "\n")))))))


(defn mods&prop [css-prop]
  (let [mods&prop* (-> css-prop name (string/split #":"))
        hydrated (-> mods&prop* last keyword shorthand/key-sh name)
        mods&prop (into [] (concat (drop-last mods&prop*) [hydrated]))]
    mods&prop))

(defn variant-name [kw]
  (when-not (= kw :default)
    (str (some-> user-config :kushi-class-prefix name) (name kw))))

(defn coll->var [compo variant css-prop css-val]
  (keyed compo variant css-prop css-val)
  (let [variant   (when-not (= variant :default) (name variant))
        mods&prop (mods&prop css-prop)
        parts     (remove nil? (concat ["kushi" (name compo) variant] mods&prop))]
    [(string/join "-" parts) (if (number? css-val) css-val (name css-val))]))

(defn resolve-tokens*
  [{:keys [coll global-tokens alias-tokens global?] :as m}]
  (keep (fn [{:keys [val]}]
          (when (util/token? val)
            (let [global-tok (some-> val keyword global-tokens util/stringify)
                  alias-tok (when-not global?
                              (some-> val keyword alias-tokens util/stringify))]
              (keyed global-tok alias-tok)
              (when-let [v (or global-tok alias-tok)]
                {:type   (if (or global? global-tok) :global :alias)
                 :cssvar val
                 :val    v}))))
        coll))

(defn resolve-tokens
  "Called internally for individual variants in themes. Sets up css tokens and resolves the
   corrent token chain based on merged theme global and alias token maps.

   Example below assumes the merged theme map includes such a :ui entry that contains, among other things, the following:
   {:ui {:kushi {:button {:primary {:hover:bgc       :--positive400
                                    :text-decoration :underline}}}}}

   (resolve-tokens
    [[\"hover:bgc\"       [\"kushi-button-primary-hover-background-color\" \"--positive400\"]]
     [\"text-decoration\" [\"kushi-button-primary-text-decoration\"        \"underline\"]]]
    alias-tokens
    global-tokens)

   =>

   ({:type :kushi-ui,
     :cssvar \"--kushi-button-primary-hover-background-color\",
     :val \"--positive400\"}

    {:type :kushi-ui,
     :cssvar \"--kushi-button-primary-text-decoration\",
     :val \"underline\"}

    {:type :alias, :cssvar \"--positive400\", :val \"--green400\"}

    {:type :global,
     :cssvar \"--green400\",
     :val \"hsl(var(--kushi-green-hue), 75%, 68%)\"})
  "
  [flat alias-tokens global-tokens]
  (!?+ (keyed flat alias-tokens global-tokens))
  (let [compo-toks* (map (fn [[_ [prop val]]]
                           {:type :kushi-ui
                            :cssvar (str "--" (name prop))
                            :val val})
                         flat)
        tok-maps    (keyed global-tokens alias-tokens)
        toks1       (resolve-tokens* (merge tok-maps
                                            {:coll             compo-toks*
                                             :alias-tokens-map alias-tokens}))
        toks2       (resolve-tokens* (merge tok-maps
                                            {:coll              toks1
                                             :global-tokens-map global-tokens
                                             :global?           true}))
        ret         (concat compo-toks* toks1 toks2)]
    (!?+ :resolve-tokens (keyed flat compo-toks* #_tok-maps toks1 toks2 ret))
    ret))


  (defn inner2 [kui-key [css-prop css-val]]
    (let [mods&prop   (mods&prop css-prop)
          parts       (-> kui-key
                          name
                          (string/split #"\.") )
          parts*      (concat (some->> parts
                                       (remove string/blank?)
                                       (remove nil?)
                                       (map #(if (= % "*") "global" %)))
                              mods&prop)
          theme-token (string/join "-" parts*)]
      (!?+ (keyed parts* theme-token))
      [(name css-prop) [theme-token  (if (number? css-val) css-val (name css-val))]]))



(def sample-merged-theme
  {:code                  {:color :red}
   :.kushi-button.primary {:hover:bgc       :--positive400
                           :text-decoration :underline}})


(defn by-component
  [{:keys [base-compo-styles
           user-compo-styles
           theme-global-tokens
           theme-alias-tokens]
    :as   by-component-args}]
  (let [merged-theme (util/deep-merge base-compo-styles user-compo-styles)
        ret2         (reduce (fn [acc [kui-key m]]
                               (let [flat     (mapv (partial inner2 kui-key) m)
                                     toks     (resolve-tokens flat theme-alias-tokens theme-global-tokens)
                                     stylemap (reduce (fn [acc [css-prop [css-var fallback]]]
                                                        (->> fallback
                                                             util/maybe-wrap-css-var
                                                             (util/s->cssvar css-var)
                                                             (assoc acc css-prop)))
                                                      {}
                                                      flat)
                                     ret      (conj acc
                                                    [{:style          stylemap
                                                      :kushi-selector (name kui-key)
                                                      :kushi-theme?   true}
                                                     toks])]
                                 (!?+ :ret2 (keyed flat toks stylemap))
                                 ret))
                             []
                             merged-theme)]
    ret2))

(defn vars-by-type [vars* kw]
  (->>  vars*
        (filter #(= (:type %) kw))
        (map (juxt :cssvar :val))
        distinct
        (sort-by first)))

(defn varize-overrides* [m]
  (reduce (fn [acc [k v]]
            (assoc acc
                   k
                   (-> v
                       util/stringify
                       util/maybe-wrap-css-var)))
          {}
          m))

(defn varize-overrides [overrides]
  (reduce (fn [acc [k m]]
            (assoc acc k (varize-overrides* m)))
          {}
          overrides))


(def google-font-maps
  {"Fira Code" {:family "Fira Code"
                :styles {:normal [400 500]}}
   "Inter"     {:family "Inter"
                :styles {:normal [500 700]}}})

(defn font-loading-opts
  "Provides an argument map for kushi.core.ui/init-typography!"
  [m]
  (let [default-code      (when-not (false? (:use-default-code-font-family? m))
                            (get google-font-maps "Fira Code"))
        default-primary   (when-not (false? (:use-default-primary-font-family? m))
                            (get google-font-maps "Inter"))
        google-font-maps1 (when (or default-code default-primary)
                            [default-code default-primary])
        google-font-maps2 (map (fn [s] {:family s :styles {:normal :all :italic :all}}) (:google-fonts* m))
        google-font-maps  (->> m
                               :google-fonts
                               seq
                               (concat google-font-maps1 google-font-maps2)
                               (remove nil?)
                               (into []))
        ;; Always adds system font stack unless `:use-system-font-stack?` is set to `false` in theme
        add-system-font-stack? (-> m :use-system-font-stack? false? not)]
    (keyed google-font-maps add-system-font-stack?)))


(defn merged-theme-props [m1 m2]
  (let [font-loading  (merge (:font-loading m1) (:font-loading m2))
        global-tokens (merge (some-> m1 :tokens :global) (some-> m2 :tokens :global))
        alias-tokens  (merge (some-> m1 :tokens :alias) (some-> m2 :tokens :alias))]
    (keyed font-loading global-tokens alias-tokens)))

(defn merged-theme []
  (let [user-theme-map    (resolve-user-theme (:theme user-config) :user)
        base-theme-map    (resolve-user-theme 'kushi.ui.basetheme/base-theme-map)
        _ (!?+ base-theme-map)
        _ (!?+ user-theme-map)
        css-reset         (when-not (false? (:css-reset user-theme-map))
                            (or (:css-reset user-theme-map)
                                (:css-reset base-theme-map)))
        css-reset-el      (:css-reset-el user-theme-map)
        {:keys
         [font-loading
          global-tokens
          alias-tokens]}  (merged-theme-props base-theme-map user-theme-map)
        font-loading-opts (font-loading-opts font-loading)
        by-component-args {:base-compo-styles   (-> base-theme-map :ui #_:kushi)
                           :user-compo-styles   (-> user-theme-map :ui #_:kushi)
                           :theme-global-tokens global-tokens
                           :theme-alias-tokens  alias-tokens}
        by-component      (by-component by-component-args)
        styles            (map first by-component)

        tok-maps          (keyed global-tokens alias-tokens)

        ;; TODO merge with user-supplied
        ;; base-classes      (base-theme-map :base-classes)
        overrides         (base-theme-map :utility-classes)
        override-tok-maps (->> overrides
                               vals
                               (map vals)
                               (apply concat)
                               distinct
                               (keep #(when (util/nameable? %) (hash-map :val (name %)))))
        override-toks1    (resolve-tokens* (merge tok-maps {:coll override-tok-maps}))
        override-toks2    (resolve-tokens* (merge tok-maps {:coll    override-toks1
                                                            :global? true}))
        override-toks     (concat override-toks1 override-toks2)
        vars*             (apply concat (map second by-component))
        vars              (concat vars* override-toks)
        tokens-in-theme   (->> [:global :alias :kushi-ui]
                               (map #(vars-by-type vars %))
                               (apply concat))
        [global-toks
         alias-toks]      (map #(sort-by first %) [global-tokens alias-tokens])
        utility-classes  (varize-overrides overrides)]

    ;; (?+ (keyed by-component tok-maps override-tok-maps override-toks1 override-toks2))
    (merge
     (keyed
      css-reset
      css-reset-el
      font-loading-opts
      styles
      tokens-in-theme
      global-toks
      alias-toks
      utility-classes))))

(def theme (merged-theme))
