(ns ^:dev/always kushi.ui.theme
  (:require
   [kushi.config :refer [user-config]]
   [kushi.utils :as util :refer [keyed]]
   [kushi.shorthand :as shorthand]
   [kushi.printing2 :as printing2]
   [kushi.specs2 :as specs2]
   [kushi.state2 :as state2]
   [kushi.ui.basetheme :as basetheme]
   [kushi.ui.utility :refer [utility-class-ks]]
   [expound.alpha :as expound]
   [clojure.spec.alpha :as s]
   [clojure.string :as string]
   [io.aviso.ansi :as ansi]))

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
    (str (name kw))))

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
     :val \"hsl(var(--kushi-green-hue), 75%, 68%)\"})"

  [flat alias-tokens global-tokens]

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
    ret))


  (defn inner2 [kui-key [css-prop css-val]]
    (let [mods&prop   (mods&prop css-prop)
          parts       (-> kui-key
                          name
                          (string/split #"\."))
          parts*      (concat (some->> parts
                                       (remove string/blank?)
                                       (remove nil?)
                                       (map #(if (= % "*") "global" %)))
                              mods&prop)
          theme-token (if (= (first parts*) "dark ")
                        (str (string/join "-" (rest parts*)) "-inverse")
                        (string/replace (string/join "-" parts*) #" -" "_"))]
      [(name css-prop) [theme-token  (if (number? css-val) css-val (name css-val))]]))


(defn inner3 [kui-key [css-prop css-val]]
    (let [mods&prop (mods&prop css-prop)
          css-val   (if (number? css-val) css-val (name css-val))
          css-val   (if (:add-theming-css-vars? user-config)
                      (let [parts       (-> kui-key
                                            name
                                            (string/split #"\."))
                            parts*      (concat (some->> parts
                                                         (remove string/blank?)
                                                         (remove nil?)
                                                         (map #(if (= % "*") "global" %)))
                                                mods&prop)
                            theme-token (if (= (first parts*) "dark ")
                                          (str (string/join "-" (rest parts*)) "-inverse")
                                          (string/replace (string/join "-" parts*) #" -" "_"))]
                        [theme-token css-val])
                      css-val)]
      [(name css-prop) css-val]))


(def sample-merged-theme
  {:code                  {:color :red}
   :.kushi-button.primary {:hover:bgc       :--positive400
                           :text-decoration :underline}})


(defn by-component
  [{:keys [base-ui
           user-ui
           base-ui-ks
           user-ui-ks
           global-tokens
           alias-tokens]}]
  (let [merged (merge base-ui user-ui)
        ui-ks (distinct (concat base-ui-ks user-ui-ks))
        ordered (util/ordered-pairs ui-ks merged)
        ret2   (reduce (fn [acc [kui-key m]]
                         (reset! state2/trace? (= kui-key :.kushi-tag.secondary.negative))
                         (let [flat     (mapv (partial inner3 kui-key) m)
                               toks     (when (:add-theming-css-vars? user-config)
                                          (resolve-tokens flat alias-tokens global-tokens))
                               stylemap (reduce (fn [acc [css-prop x]]
                                                  (if (vector? x)
                                                    (let [[css-var fallback] x]
                                                      (->> fallback
                                                           util/maybe-wrap-css-var
                                                           (util/s->cssvar css-var)
                                                           (assoc acc css-prop)))
                                                    (assoc acc css-prop (util/maybe-wrap-css-var x))))
                                                {}
                                                flat)
                               ret      (conj acc
                                              [{:style                stylemap
                                                :kushi-selector       (name kui-key)
                                                :kushi-theme-selector (name kui-key)}
                                               toks])]
                           ret))
                       []
                       ordered)]
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
        ;; Always adds system font stack unless `:add-system-font-stack?` is set to `false` in theme
        add-system-font-stack? (-> m :add-system-font-stack? false? not)
        system-font-stack-weights* (-> m :system-font-stack-weights)
        system-font-stack-weights  (if (and (seq system-font-stack-weights*)
                                            (every? int? system-font-stack-weights*))
                                     system-font-stack-weights*
                                     [])]
    (keyed google-font-maps add-system-font-stack? system-font-stack-weights)))


(defn merged-theme-props [m1 m2]
  (let [font-loading  (merge (:font-loading m1) (:font-loading m2))
        global-tokens (merge (some-> m1 :tokens :global) (some-> m2 :tokens :global))
        alias-tokens  (merge (some-> m1 :tokens :alias) (some-> m2 :tokens :alias))]
    (keyed font-loading global-tokens alias-tokens)))

;; simple-bad-global-selector-key-warning
(defn remove-global-selector [m]
  (if (and (map? m) (:* m))
    (do (printing2/simple-bad-global-selector-key-warning {:form-meta    {:file "kushi.ui.theme/theme"}
                                                           :invalid-args {:ui (:* m)}})
        (dissoc m :*))
    m))


(defn tokens-in-theme
  [overrides tok-maps]
  (let [override-tok-maps (->> overrides
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
                               (apply concat))]
    tokens-in-theme))


(defn theme []
  (let [user-theme-map    (resolve-user-theme (:theme user-config) :user)
        user-theme-map    (when user-theme-map
                            (if (s/valid? ::specs2/theme user-theme-map)
                              user-theme-map
                              (printing2/simple-warning2
                               {:commentary  (str  "kushi.ui.theme/theme\n"
                                                   "Invalid value(s) in user theming config:\n"
                                                   ansi/bold-font (:theme user-config) ansi/reset-font)
                                :expound-str (expound/expound-str ::specs2/theme user-theme-map)})))
        base-theme-map    (resolve-user-theme 'kushi.ui.basetheme/base-theme-map)

        {:keys [font-loading
                global-tokens
                alias-tokens]}  (merged-theme-props base-theme-map user-theme-map)

        base-ui*          (:ui base-theme-map)
        user-ui*          (:ui user-theme-map)
        base-ui           (remove-global-selector base-ui*)
        user-ui           (->> user-ui* (apply hash-map) remove-global-selector)
        base-ui-ks        (mapcat util/kwargs-keys [basetheme/ui basetheme/kushi-ui])
        user-ui-ks        (util/kwargs-keys user-ui*)

        ;; ui-theming related start
        by-component-args {:base-ui       base-ui
                           :user-ui       user-ui
                           :user-ui-ks    user-ui-ks
                           :base-ui-ks    base-ui-ks
                           :global-tokens global-tokens
                           :alias-tokens  alias-tokens}
        by-component      (by-component by-component-args)
        styles            (map first by-component)
        ;;ui-theming related end

        overrides         (base-theme-map :utility-classes)
        [global-toks
         alias-toks]      (map #(sort-by first %) [global-tokens alias-tokens])
        utility-classes   (when (:add-kushi-defclass? user-config)
                            (varize-overrides overrides))
        utility-classes   (util/ordered-pairs utility-class-ks utility-classes)
        css-reset         (when (:add-css-reset? user-config)
                            (or (:css-reset user-theme-map)
                                (:css-reset base-theme-map)))
        font-loading-opts (let [opts (font-loading-opts font-loading)]
                            (if (false? (:add-system-font-stack? user-config))
                              (assoc opts :add-system-font-stack? false)
                              opts))]
    (merge
     (keyed
      css-reset
      utility-classes
      font-loading-opts
      styles
      global-toks
      alias-toks))))
