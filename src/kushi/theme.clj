(ns ^:dev/always kushi.theme
  (:require
   [kushi.config :refer [user-config]]
   [kushi.utils :as util :refer [keyed]]
   [kushi.shorthand :as shorthand]
   [clojure.string :as string]
   [par.core    :refer [? !? ?+ !?+]]))

(def base-theme-sym* 'kushi.basetheme/base-theme)

(defn resolve-user-theme
  ([x]
   (resolve-user-theme x :user))
  ([x kw]
   (when x
     (try (let [[ns-name themevar] (string/split (str x) #"/")]
            (require (symbol ns-name) :reload)
            (let [bar (find-ns (symbol ns-name))]
            ;; #_(?+ bar)
            ;; #_(?+ (symbol themevar))
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



(defn compound-override [schema m]
  (reduce (fn [acc [k v]]
            (assoc acc
                   k
                   (into {}
                         (apply
                          concat
                          (map-indexed (fn [idx x]
                                         (mapv (fn [kw]
                                                 [kw (nth v idx nil)])
                                               (if (coll? x) x [x])))
                                       schema)))))
          {}
          m))

  (def global-tokens
    {
     ;; Color
     ;; Intended for css props: color, border-color, background-color
     :--white           :#FFFFFF
     :--gray50          :#F6F6F6
     :--gray100         :#EEEEEE
     :--gray200         :#E2E2E2
     :--gray300         :#CBCBCB
     :--gray400         :#AFAFAF
     :--gray500         :#6B6B6B
     :--gray600         :#545454
     :--gray700         :#333333
     :--gray800         :#1F1F1F
     :--gray900         :#141414
     :--black           :#000000
     :--platinum50      :#F4FAFB
     :--platinum100     :#EBF5F7
     :--platinum200     :#CCDFE5
     :--platinum300     :#A1BDCA
     :--platinum400     :#8EA3AD
     :--platinum500     :#6C7C83
     :--platinum600     :#556268
     :--platinum700     :#394145
     :--platinum800     :#142328
     :--red50           :#FFEFED
     :--red100          :#FED7D2
     :--red200          :#F1998E
     :--red300          :#E85C4A
     :--red400          :#E11900
     :--red500          :#AB1300
     :--red600          :#870F00
     :--red700          :#5A0A00
     :--orange50        :#FFF3EF
     :--orange100       :#FFE1D6
     :--orange200       :#FABDA5
     :--orange300       :#FA9269
     :--orange400       :#FF6937
     :--orange500       :#C14F29
     :--orange600       :#9A3F21
     :--orange700       :#672A16
     :--yellow50        :#FFFAF0
     :--yellow100       :#FFF2D9
     :--yellow200       :#FFE3AC
     :--yellow300       :#FFCF70
     :--yellow400       :#FFC043
     :--yellow500       :#BC8B2C
     :--yellow600       :#996F00
     :--yellow700       :#674D1B
     :--green50         :#E6F2ED
     :--green100        :#ADDEC9
     :--green200        :#66D19E
     :--green300        :#06C167
     :--green400        :#048848
     :--green500        :#03703C
     :--green600        :#03582F
     :--green700        :#10462D
     :--blue50          :#EFF3FE
     :--blue100         :#D4E2FC
     :--blue200         :#A0BFF8
     :--blue300         :#5B91F5
     :--blue400         :#276EF1
     :--blue500         :#1E54B7
     :--blue600         :#174291
     :--blue700         :#102C60
     :--cobalt50        :#EBEDFA
     :--cobalt100       :#D2D7F0
     :--cobalt200       :#949CE3
     :--cobalt300       :#535FCF
     :--cobalt400       :#0E1FC1
     :--cobalt500       :#0A1899
     :--cobalt600       :#081270
     :--cobalt700       :#050C4D
     :--purple50        :#F3F1F9
     :--purple100       :#E3DDF2
     :--purple200       :#C1B4E2
     :--purple300       :#957FCE
     :--purple400       :#7356BF
     :--purple500       :#574191
     :--purple600       :#453473
     :--purple700       :#2E224C
     :--brown50         :#F6F0EA
     :--brown100        :#EBE0DB
     :--brown200        :#D2BBB0
     :--brown300        :#B18977
     :--brown400        :#99644C
     :--brown500        :#744C3A
     :--brown600        :#5C3C2E
     :--brown700        :#3D281E

     ;; Typography
     ;; Intended for css props: font-weight
     :--text-thin       100
     :--text-light      300
     :--text-normal     400
     :--text-bold       700

     ;; Intended for css props: font-size
     :--text-mini       :0.75rem
     :--text-small      :0.9rem
     :--text-medium     :1rem
     :--text-large      :1.222rem
     :--text-huge       :1.7rem

     ;;  Material UI icons
     :--mui-icon-mini   :1rem
     :--mui-icon-small  :1.25rem
     :--mui-icon-medium :1.7rem
     :--mui-icon-large  :2.1875rem
     :--mui-icon-huge   :2.75rem

     ;; Intended for css props: border-radius
     :--rounded         :0.3rem

     ;; Intended for css props: box-shadow
     :--elevated        "rgb(0 0 0 / 4%) 12px 10px 16px 2px, rgb(0 0 0 / 5%) 0px 2px 9px 0px;"})

  (def alias-tokens
    {:--primary-a   :--black
     :--primary-b   :--white
     :--primary     :--black
     :--primary50   :--gray50
     :--primary100  :--gray100
     :--primary200  :--gray200
     :--primary300  :--gray300
     :--primary400  :--gray400
     :--primary500  :--gray500
     :--primary600  :--gray600
     :--primary700  :--gray700
     :--accent      :--blue400
     :--accent50    :--blue50
     :--accent100   :--blue100
     :--accent200   :--blue200
     :--accent300   :--blue300
     :--accent400   :--blue400
     :--accent500   :--blue500
     :--accent600   :--blue600
     :--accent700   :--blue700
     :--negative    :--red400
     :--negative50  :--red50
     :--negative100 :--red100
     :--negative200 :--red200
     :--negative300 :--red300
     :--negative400 :--red400
     :--negative500 :--red500
     :--negative600 :--red600
     :--negative700 :--red700
     :--warning     :--yellow400
     :--warning50   :--yellow50
     :--warning100  :--yellow100
     :--warning200  :--yellow200
     :--warning300  :--yellow300
     :--warning400  :--yellow400
     :--warning500  :--yellow500
     :--warning600  :--yellow600
     :--warning700  :--yellow700
     :--positive    :--green500
     :--positive50  :--green50
     :--positive100 :--green100
     :--positive200 :--green200
     :--positive300 :--green300
     :--positive400 :--green400
     :--positive500 :--green500
     :--positive600 :--green600
     :--positive700 :--green700
     :--white       :--white
     :--black       :--black
     :--mono100     :--white
     :--mono200     :--gray50
     :--mono300     :--gray100
     :--mono400     :--gray200
     :--mono500     :--gray300
     :--mono600     :--gray400
     :--mono700     :--gray500
     :--mono800     :--gray600
     :--mono900     :--gray700
     :--mono1000    :--black })

(def overrides
  (merge

   (compound-override
    [:fs
     [:&.kushi-icon>*:fs :&_.kushi-icon>*:fs]
     [:&.kushi-custom-icon:w :&_.kushi-custom-icon:w :&.kushi-custom-icon:h :&_.kushi-custom-icon:h]
     [:&.kushi-custom-icon&_path:stroke-width :&_.kushi-custom-icon&_path:stroke-width]]
    {:mini   [:--text-mini :--mui-icon-mini :10px 0.5]
     :small  [:--text-small :--mui-icon-small :12px 1]
     :medium [:--text-medium :--mui-icon-medium :14px 1.5]
     :large  [:--text-large :--mui-icon-large :16px 2]
     :huge   [:--text-huge :--mui-icon-huge :18px 2.25]})

   {:thin   {:fw :--text-thin}
    :light  {:fw :--text-light}
    :normal {:fw :--text-normal}
    :bold   {:fw :--text-bold}}

   {:rounded {:border-radius :--rounded}
    :sharp {:border-radius 0}}

   {:elevated {:box-shadow :--elevated}}))


(def theme*
  {:primary           {:c         :--primary-b
                       :bgc       :--primary
                       :hover:bgc :--gray400}
   :ghosted           {:bw        :1px
                       :bgc       :transparent
                       :hover:bgc :transparent
                       :hover:o   0.6}
   :positive-inverted {:c   :--primary-b
                       :bgc :--positive}
   :warning-inverted  {:c   :--primary-b
                       :bgc :--warning}
   :negative-inverted {:c   :--primary-b
                       :bgc :--negative}
   :disabled          {:o      :40%
                       :cursor :not-allowed}
   :minimal           {:bgc :transparent
                       :p   0}})

(def base-theme
  {:button {
            :default   {:bgc        :--gray100
                        :hover:bgc  :--gray200
                        :color      :--primary}
            :primary   (:primary theme*)
            :link      {:td        :underline
                        :bgc       :transparent
                        :hover:bgc :transparent}
            :secondary {:bgc        :--gray100
                        :hover:bgc  :--gray200
                        :color      :--primary}
            :tertiary  {:bgc       :transparent
                        :hover:bgc :--gray100}
            :minimal   (:minimal theme*)
            :ghosted   (:ghosted theme*)

            }

   :tag    {:default  {:c :--primary}
            :primary  (:primary theme*)
            :positive (:positive-inverted theme*)
            :negative (:negative-inverted theme*)
            :warning  (:warning-inverted theme*)}

   ;; stuff like this needs to be in sync with the var name it is creating
   })

(defn mods&prop [css-prop]
  (let [mods&prop* (-> css-prop name (string/split #":"))
        hydrated (-> mods&prop* last keyword shorthand/key-sh name)
        mods&prop (into [] (concat (drop-last mods&prop*) [hydrated]))]
    mods&prop))

(defn variant-name [kw]
  (when-not (= kw :default)
    (str (some-> user-config :defclass-prefix name) (name kw))))

(defn coll->var [compo variant css-prop css-val]
  (!?+ (keyed compo variant css-prop css-val))
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
                                             :global?           true}))]
    (concat compo-toks* toks1 toks2)))

(defn theme-by-compo-inner
  [kushi-compo acc [variant stylemap*]]
  (let [flat     (mapv (fn [[css-prop css-val]]
                         (let [prop (-> css-prop name (string/replace #"dark:" "has(ancestor(.dark)):"))]
                           [prop (coll->var kushi-compo variant css-prop css-val)]))
                       stylemap*)
        toks     (resolve-tokens flat alias-tokens global-tokens)
        stylemap (reduce (fn [acc [css-prop [css-var fallback]]]
                           (->> fallback
                                util/maybe-wrap-css-var
                                (util/s->cssvar css-var)
                                (assoc acc css-prop)))
                         {}
                         flat)]
    (conj acc
          [{:style        stylemap
            :prefix       (str "kushi-" (name kushi-compo))
            :ident        (let [nm (variant-name variant)]
                            (if nm (str "." nm) ""))
            :kushi-theme? true}
           toks])))

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

(defn theme-by-compo [base-theme]
  (!?+ (resolve-user-theme (:theme user-config)))
  (let [{user-theme :theme
         user-global-tokens :global-tokens
         user-alias-tokens  :alias-tokens} (resolve-user-theme (:theme user-config))
        global-tokens     (merge global-tokens user-global-tokens)
        alias-tokens      (merge alias-tokens user-alias-tokens)
        merged-theme      (util/deep-merge base-theme user-theme)
        by-compo          (reduce (fn [acc [kushi-compo m]]
                                    (apply conj
                                           acc
                                           (reduce (partial theme-by-compo-inner kushi-compo)
                                                   []
                                                   m)))
                                  []
                                  merged-theme)
        styles            (map first by-compo)
        tok-maps          (keyed global-tokens alias-tokens)
        override-tok-maps (->> overrides
                               vals
                               (map vals)
                               (apply concat)
                               distinct
                               (keep #(when (util/nameable? %) (hash-map :val (name %)))))
        override-toks1    (resolve-tokens* (merge tok-maps {:coll override-tok-maps}))
        override-toks2    (resolve-tokens* (merge tok-maps {:coll override-toks1 :global? true}))
        override-toks     (concat override-toks1 override-toks2)
        vars*             (apply concat (map second by-compo))
        vars              (concat vars* override-toks)
        toks              (->> [:global :alias :kushi-ui]
                               (map #(vars-by-type vars %))
                               (apply concat))
        global+alias-toks (apply concat (map #(sort-by first %) [global-tokens alias-tokens]))
        overrides         (varize-overrides overrides)]
    (keyed styles toks global+alias-toks overrides)))
