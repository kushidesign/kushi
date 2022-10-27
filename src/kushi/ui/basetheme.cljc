(ns ^:dev/always kushi.ui.basetheme
 (:require
   [kushi.ui.tokens :refer [global-tokens alias-tokens]]
   [kushi.ui.utility :refer [utility-classes]]
   [kushi.config :refer [user-config]]
   [kushi.utils :as util]
   [clojure.string :as string]))

(def css-reset
  [["*:where(:not(html, iframe, canvas, img, svg, video):not(svg *, symbol *))"]
   {:all     :unset
    :display :revert}

   ["*" "*::before" "*::after"]
   {:box-sizing :border-box}

   ["a" "button"]
   {:cursor :revert}

   ["ol" "ul" "menu"]
   {:list-style :none }

   ["img"]
   {:max-width :100% }

   ["table"]
   {:border-collapse :collapse}

   ["textarea"]
   {:white-space :revert}

   ["meter"]
   {:-webkit-appearance :revert
    :appearance         :revert}

   ["::placeholder"]
   {:color :unset}

   [":where([hidden])"]
   {:display :none}

   [":where([contenteditable])"]
   {:-moz-user-modify    :read-write
    :-webkit-user-modify :read-write
    :overflow-wrap       :break-word
    :-webkit-line-break  :after-white-space}

   [":where([draggable='true'])"]
   {:-webkit-user-drag :element}])

(def component-tokens
 {:--kushi-collapse-transition-duration :--duration-slow})


(defn tok*
  ([k]
   (tok* k nil))
  ([k n]
   (keyword (str "--" (name k) n))))

(defn tok-darker* [k n]
  (tok* k (if (= n 50) 200 (+ n 100))))

(defn tok-lighter* [k n]
  (tok* k (if (= n 100) 50 (- n 100))))

;; (defn dark-primary* [k n]
;;   {:bgc       (tok* k n)
;;    :hover:bgc (tok* k (+ n 100))
;;    :color     :--primary-a})

(defn dark-primary-intense* [k n]
  {:bgc          (tok* k n)
   :color        :--primary-a})

(defn primary-intense* [k n]
  {:bgc          (tok* k n)
   :color        :--primary-b})

(defn dark-primary-intense-target* [k n]
  {:hover:bgc    (tok-darker* k n)
   :active:bgc   (tok* k n)
   :hover:color  :--primary-a
   :active:color :--primary-a})

(defn primary-intense-target* [k n]
  {:hover:bgc    (tok-lighter* k n)
   :active:bgc   (tok* k n)
   :hover:color  :--primary-b
   :active:color :--primary-b})

(defn dark-outlined* [k n]
  (let [c (tok* k n)
        hc (tok-darker* k n)]
    {:c         c
     :hover:c   hc
     :bc        c
     :hover:bc  hc
     :bgc       :transparent
     :hover:bgc :transparent}))

(defn minimal* [k n]
  {:bgc        :transparent
   :hover:bgc  :transparent
   :active:bgc :transparent
   :c          (tok* k)
   :hover:c    (tok* k n)
   :active:c   (tok* k)})

(defn minimal-inverse* [k n]
  (let [c (tok* k n)
        hc (tok-darker* k n)]
    {:c          c
     :hover:c    hc
     :active:c   c
     :bgc        :transparent
     :hover:bgc  :transparent
     :active:bgc :transparent}))

(defn semantic-cssvar [{:keys [kw inverse?]} [css-prop s]]
  [css-prop (tok* (str (name kw) s (when inverse? "-inverse")))])

(defn semantic-outline-map [opts]
  (into {}
        (map
         (partial semantic-cssvar opts)
         {:c:hover    "500"
          :bc:hover   "-border"})))

(defn semantic-map [opts]
  (into {}
        (map
         (partial semantic-cssvar opts)
         {:c          ""
          :bgc        "-background"
          :bc         "-border"})))

(defn semantic-target-map [opts]
  (into {}
        (map
         (partial semantic-cssvar opts)
         {:hover:c    "-hover"
          :active:c   "-active"
          :hover:bgc  "-background-hover"
          :active:bgc "-background-active"
          :hover:bc   "-border-hover"
          :active:bc  "-border-active"})))

(defn semantic-inverse* [kw]
  (semantic-map {:kw kw :inverse? true}))

(defn semantic* [kw]
  (semantic-map {:kw kw}))

(defn semantic-target-inverse* [kw]
  (semantic-target-map {:kw kw :inverse? true}))

(defn semantic-target* [kw]
  (semantic-target-map {:kw kw}))

(defn semantic-outline-inverse* [kw]
  (semantic-outline-map {:kw kw :inverse? true}))

(defn semantic-outline* [kw]
  (semantic-outline-map {:kw kw}))

(def primary-color* (semantic* :primary))
(def primary-color-inverse* (semantic-inverse* :primary))
(def accent* (semantic* :accent))
(def accent-inverse* (semantic-inverse* :accent))
(def positive* (semantic* :positive))
(def positive-inverse* (semantic-inverse* :positive))
(def warning* (semantic* :warning))
(def warning-inverse* (semantic-inverse* :warning))
(def negative* (semantic* :negative))
(def negative-inverse* (semantic-inverse* :negative))

(def white-text {:c :--primary-b :hover:c :--primary-b :active:c :--primary-b})
(def primary-target* (semantic-target* :primary))
(def primary-target-inverse* (merge (semantic-target-inverse* :primary) white-text))
(def accent-target* (semantic-target* :accent))
(def accent-target-inverse* (merge (semantic-target-inverse* :accent) white-text))
(def positive-target* (semantic-target* :positive))
(def positive-target-inverse* (merge (semantic-target-inverse* :positive) white-text))
(def warning-target* (semantic-target* :warning))
(def warning-target-inverse* (merge (semantic-target-inverse* :warning) white-text))
(def negative-target* (semantic-target* :negative))
(def negative-target-inverse* (merge (semantic-target-inverse* :negative) white-text))


(def outlined*
  {:bgc       :transparent
   :hover:bgc :transparent
   :bw        :2px
   :outline   :none
   :bs        :solid})

(defn outlined-target* [k n]
  (merge (let [c  (tok* k)
               hc (tok* k n)]
           {:hover:c    hc
            :active:c   c
            :bc         c
            :hover:bc   hc
            :active:bc  c
            :active:bgc :transparent})
         outlined*))

(defn outlined-tag* [k]
  (merge (let [c  (tok* k)]
           {:c          c
            :bc         c})
         outlined*
         {:bw :1px}))

(defn outlined-tag-inverse* [k]
  (merge (let [c  (tok* k "-inverse")]
           {:c          c
            :bc         c})
         outlined*
         {:bw :1px}))

(defn outlined-target-inverse* [k n]
  (merge (let [c  (tok* k "-inverse")
               hc (tok* k n)]
           {:c          c
            :hover:c    hc
            :active:c   c
            :bc         c
            :hover:bc   hc
            :active:bc  c
            :active:bgc :transparent})
         outlined*))

(defn tertiary* [k]
  {:bgc        :transparent
   :hover:bgc  (tok* k 100)
   :active:bgc (tok* k 200)})

(defn tertiary-inverse* [k]
  {:bgc        :transparent
   :hover:bgc  (tok* k "-background-inverse")
   :active:bgc (tok* k "-background-active-inverse")})


(def ui
  ["body"
   {:font-family :--sans-serif-font-stack
    :color       :--primary}

   "code"
   {:font-family   :--code-font-stack
    :font-size     :0.9em
    :pi            :0.4em
    :pb            :0.15em:0.08em
    :border-radius :3px
    :bgc           :--gray100
    :h             :fit-content
    :w             :fit-content}

   ".code"
   {:font-family   :--code-font-stack
    :font-size     :0.9em
    :pi            :0.4em
    :pb            :0.15em:0.08em
    :border-radius :3px
    :bgc           :--gray100
    :h             :fit-content
    :w             :fit-content}

   ;; Buttons, tags, & labels

   ".accent"
   accent*

   ".dark .accent"
   accent-inverse*

   ".positive"
   positive*

   ".dark .positive"
   positive-inverse*

   ".warning"
   warning*

   ".dark .warning"
   warning-inverse*

   ".negative"
   negative*

   ".dark .negative"
   negative-inverse*

   ".pill"
   {:border-radius :9999px}])


(defn- semantic-variants*
  [{:keys [selector-base kind semantic-variants]}]
  (map (fn [semantic]
         (let [default-light (str selector-base (when kind (str "." (name kind))))
               default-dark (str ".dark " default-light)
               light (str selector-base "." (when kind (str (name kind) ".")) (name semantic))
               dark  (str ".dark " light)]
           [default-light default-dark light dark]))
       semantic-variants))


(defn- varients-by-kind
  [k {semantic-variants :semantic kind-variants :kind}]
  (let [selector-base (str ".kushi-" (name k))
        m*            {:selector-base     selector-base
                       :semantic-variants semantic-variants}
        default-kind  (semantic-variants* m*)
        by-kind       (map (fn [kind]
                             (conj (semantic-variants* (assoc m* :kind kind))))
                           kind-variants)]
    [default-kind by-kind]))


(def user-narrowed-variants
  (let [{:keys [kushi-ui-variants]} user-config]
    (->> kushi-ui-variants
         (reduce (fn [acc [k m]]
                   (conj acc (varients-by-kind k m)))
                 [])
         flatten
         distinct)))

(def kushi-ui
  [".kushi-button"                          (merge primary-color*
                                                   primary-target*
                                                   {:fw :--text-wee-bold
                                                    :ff :--primary-font-family})
   ".dark .kushi-button"                    (merge primary-color-inverse*
                                                   primary-target-inverse*
                                                   {:fw :--text-wee-bold
                                                    :ff :--primary-font-family})
   ".kushi-button.accent"                   (merge accent* accent-target*)
   ".kushi-button.positive"                 (merge positive* positive-target*)
   ".kushi-button.warning"                  (merge warning* warning-target*)
   ".kushi-button.negative"                 (merge negative* negative-target*)
   ".dark .kushi-button.positive"           (merge positive-inverse* positive-target-inverse*)
   ".dark .kushi-button.negative"           (merge negative-inverse* negative-target-inverse*)
   ".dark .kushi-button.warning"            (merge warning-inverse* warning-target-inverse*)
   ".dark .kushi-button.accent"             (merge accent-inverse* accent-target-inverse*)

   ".kushi-button.secondary.accent"         (merge accent* accent-target*)
   ".kushi-button.secondary.positive"       (merge positive* positive-target*)
   ".kushi-button.secondary.warning"        (merge warning* warning-target*)
   ".kushi-button.secondary.negative"       (merge negative* negative-target*)
   ".dark .kushi-button.secondary.positive" (merge positive-inverse* positive-target-inverse*)
   ".dark .kushi-button.secondary.negative" (merge negative-inverse* negative-target-inverse*)
   ".dark .kushi-button.secondary.warning"  (merge warning-inverse* warning-target-inverse*)
   ".dark .kushi-button.secondary.accent"   (merge accent-inverse* accent-target-inverse*)

   ".kushi-button.tertiary"                 (tertiary* :primary)
   ".kushi-button.tertiary.positive"        (tertiary* :positive)
   ".kushi-button.tertiary.negative"        (tertiary* :negative)
   ".kushi-button.tertiary.warning"         (tertiary* :warning)
   ".kushi-button.tertiary.accent"          (tertiary* :accent)
   ".dark .kushi-button.tertiary"           (merge (tertiary-inverse* :primary) {:c :--primary-b})
   ".dark .kushi-button.tertiary.positive"  (tertiary-inverse* :positive)
   ".dark .kushi-button.tertiary.negative"  (tertiary-inverse* :negative)
   ".dark .kushi-button.tertiary.warning"   (tertiary-inverse* :warning)
   ".dark .kushi-button.tertiary.accent"    (tertiary-inverse* :accent)

   ".kushi-button.primary"                  (merge (primary-intense* :primary 800) (primary-intense-target* :primary 800))
   ".kushi-button.primary.positive"         (merge (primary-intense* :positive 700) (primary-intense-target* :positive 700))
   ".kushi-button.primary.negative"         (merge (primary-intense* :negative 700) (primary-intense-target* :negative 700))
   ".kushi-button.primary.warning"          (merge (primary-intense* :warning 700) (primary-intense-target* :warning 700))
   ".kushi-button.primary.accent"           (merge (primary-intense* :accent 600) (primary-intense-target* :accent 600))

   ".dark .kushi-button.primary"            (dark-primary-intense* :primary 50)
   ".dark .kushi-button.primary.positive"   (dark-primary-intense* :positive 500)
   ".dark .kushi-button.primary.negative"   (dark-primary-intense* :negative 400)
   ".dark .kushi-button.primary.warning"    (dark-primary-intense* :warning 500)
   ".dark .kushi-button.primary.accent"     (dark-primary-intense* :accent 300)

   ".kushi-button.outlined"                 (outlined-target* :primary 800)
   ".kushi-button.outlined.accent"          (outlined-target* :accent 400)
   ".kushi-button.outlined.positive"        (outlined-target* :positive 700)
   ".kushi-button.outlined.warning"         (outlined-target* :warning 700)
   ".kushi-button.outlined.negative"        (outlined-target* :negative 400)

   ".dark .kushi-button.outlined"           {:c         :--primary-b
                                             :hover:c   :--primary100
                                             :bc        :--primary-b
                                             :hover:bc  :--primary100
                                             :bgc       :transparent
                                             :hover:bgc :transparent}

   ".dark .kushi-button.outlined.positive"  (outlined-target-inverse* :positive 400)
   ".dark .kushi-button.outlined.negative"  (outlined-target-inverse* :negative 300)
   ".dark .kushi-button.outlined.warning"   (outlined-target-inverse* :warning 500)
   ".dark .kushi-button.outlined.accent"    (outlined-target-inverse* :accent 300)

   ".kushi-button.minimal"                  (merge (minimal* :primary 700) {:p 0})
   ".kushi-button.minimal.positive"         (minimal* :positive 700)
   ".kushi-button.minimal.negative"         (minimal* :negative 500)
   ".kushi-button.minimal.warning"          (minimal* :warning 700)
   ".kushi-button.minimal.accent"           (minimal* :accent 500)

   ".dark .kushi-button.minimal"            (minimal-inverse* :primary 50)
   ".dark .kushi-button.minimal.positive"   (minimal-inverse* :positive 300)
   ".dark .kushi-button.minimal.negative"   (minimal-inverse* :negative 200)
   ".dark .kushi-button.minimal.warning"    (minimal-inverse* :warning 300)
   ".dark .kushi-button.minimal.accent"     (minimal-inverse* :accent 200)


   ".kushi-tag"                             (merge primary-color* {:fw :--text-wee-bold})
   ".dark .kushi-tag"                       (merge primary-color-inverse* {:fw :--text-wee-bold})
   ".kushi-tag.accent"                      accent*
   ".kushi-tag.positive"                    positive*
   ".kushi-tag.warning"                     warning*
   ".kushi-tag.negative"                    negative*
   ".dark .kushi-tag.positive"              positive-inverse*
   ".dark .kushi-tag.negative"              negative-inverse*
   ".dark .kushi-tag.warning"               warning-inverse*
   ".dark .kushi-tag.accent"                accent-inverse*

   ".kushi-tag.secondary"                   (merge primary-color* {:fw :--text-wee-bold})
   ".dark .kushi-tag.secondary"             (merge primary-color-inverse* {:fw :--text-wee-bold})
   ".kushi-tag.secondary.accent"            accent*
   ".kushi-tag.secondary.positive"          positive*
   ".kushi-tag.secondary.warning"           warning*
   ".kushi-tag.secondary.negative"          negative*
   ".dark .kushi-tag.secondary.positive"    positive-inverse*
   ".dark .kushi-tag.secondary.negative"    negative-inverse*
   ".dark .kushi-tag.secondary.warning"     warning-inverse*
   ".dark .kushi-tag.secondary.accent"      accent-inverse*

   ".kushi-tag.primary"                     (primary-intense* :primary 800)
   ".kushi-tag.primary.positive"            (primary-intense* :positive 700)
   ".kushi-tag.primary.negative"            (primary-intense* :negative 700)
   ".kushi-tag.primary.warning"             (primary-intense* :warning 700)
   ".kushi-tag.primary.accent"              (primary-intense* :accent 600)

   ".dark .kushi-tag.primary"               (dark-primary-intense* :primary 50)
   ".dark .kushi-tag.primary.positive"      (dark-primary-intense* :positive 500)
   ".dark .kushi-tag.primary.negative"      (dark-primary-intense* :negative 400)
   ".dark .kushi-tag.primary.warning"       (dark-primary-intense* :warning 500)
   ".dark .kushi-tag.primary.accent"        (dark-primary-intense* :accent 300)


   ".kushi-tag.outlined"                    (outlined-tag* :primary)
   ".kushi-tag.outlined.accent"             (outlined-tag* :accent)
   ".kushi-tag.outlined.positive"           (outlined-tag* :positive)
   ".kushi-tag.outlined.warning"            (outlined-tag* :warning)
   ".kushi-tag.outlined.negative"           (outlined-tag* :negative)

   ".dark .kushi-tag.outlined"              {:c   :--primary-b
                                             :bc  :--primary-b
                                             :bgc :transparent}

   ".dark .kushi-tag.outlined.positive"     (outlined-tag-inverse* :positive)
   ".dark .kushi-tag.outlined.negative"     (outlined-tag-inverse* :negative)
   ".dark .kushi-tag.outlined.warning"      (outlined-tag-inverse* :warning)
   ".dark .kushi-tag.outlined.accent"       (outlined-tag-inverse* :accent)

  ;;  :.kushi-radio                            (tertiary* :primary)
  ;;  ".dark .kushi-radio"                     (merge (tertiary-inverse* :primary) {:c :--primary-b})
  ;;  ".dark .kushi-button"                    (merge primary-color-inverse*
  ;;                                                  primary-target-inverse*
  ;;                                                  {:fw :--text-wee-bold
  ;;                                                   :ff :--primary-font-family})
   ])

(def global {:font-family      :--sans-serif-stack
             :background-color :crimson
             :color            :--primary})


(def tokens
  {:global global-tokens
   :alias (merge alias-tokens component-tokens)})

(def font-loading
  {
  ;;  :add-system-font-stack? false
  ;;  :system-font-stack-weights  [300 700]
  ;;  :use-default-code-font-family? false
  ;;  :use-default-primary-font-family? false
  ;;  :google-fonts [{:family "Public Sans"
  ;;                  :styles {:normal [100] :italic [300]}}]
   :google-fonts* ["Fira Code" "Inter"]})

(def merged-ui
  (let [darks?            (:add-kushi-ui-dark-theming? user-config)
        lights?           (:add-kushi-ui-light-theming? user-config)
        kushi-ui-theming? (:add-kushi-ui-theming? user-config)
        kushi-ui-theming? (and kushi-ui-theming? (or darks? lights?))
        [ui kushi-ui]     (map #(apply hash-map %) [ui kushi-ui])
        coll              (merge ui
                                 (when kushi-ui-theming?
                                   (select-keys kushi-ui user-narrowed-variants)))]

    (if (and kushi-ui-theming?
             (or (not darks?)
                 (not lights?)))
      (let [[darks lights]
            (util/partition-by-pred (fn [[k _]]
                                      (and (string? k)
                                           (string/starts-with? k ".dark ")))
                                    coll)]
        (into {} (if lights? lights darks)))
      coll)))

(def base-theme-map
  {:css-reset css-reset
   :utility-classes utility-classes
   :tokens tokens
   :font-loading font-loading
   ;; This needs a different name?
   :ui merged-ui})

