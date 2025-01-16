(ns kushi.playground.about
  (:require [clojure.string :as string]
            [domo.core :refer (copy-to-clipboard!)]
            [me.flowthing.pp :refer [pprint]]
            [kushi.color :refer [colors->tokens]]
            [kushi.css.core :refer (sx css merge-attrs css-vars-map trans)]
            [kushi.playground.colors :as playground.colors]
            [kushi.playground.nav :refer [route!]]
            [kushi.playground.shared-styles]
            [kushi.playground.state :as state]
            [kushi.ui.button.core :refer [button]]
            [kushi.ui.core :refer [defcom keyed]]
            [kushi.ui.divisor.core :refer (divisor)]
            [kushi.ui.icon.core :refer [icon]]
            [kushi.ui.label.core :refer [label]]
            [kushi.ui.link.core :refer [link]]
            [kushi.ui.snippet.core :refer (copy-to-clipboard-button)]
            [kushi.ui.tooltip.core :refer [tooltip-attrs]]))

(defn alias-global-mapping-row [a g]
  [:<>
   [:code (sx :.code) (str "--" a)]
   [:span "→"]
   [:code (sx :.code) (str "--" g)]])


(defn color-scales2
  [{:keys [colorlist]}]
  (let [tokens (colors->tokens kushi.colors/colors {:format :css})
        coll   (keep (fn [[k v]]
                       (let [color*       (or (->> k name (re-find #"^--([a-zAZ-_]+)-([0-9]+)$"))
                                              (->> k name (re-find #"^\$([a-zAZ-_]+)-([0-9]+)$")))
                             color-name   (some-> color* second)
                             color-level  (some-> color* last js/parseInt)
                             color-token? (contains? (into #{} colorlist) (keyword color-name))]
                         (name k) #_(keyed color*)
                         (when color-token?
                           {:color*      color*
                            :color-name  color-name
                            :color-level color-level
                            :value       v
                            :token       k})))
                     (partition 2 tokens))
        ret    (mapv #(let [scale (into []
                                        (keep (fn [{:keys [color-name token value color-level]}]
                                                (when (= color-name (name %))
                                                  [token value color-level]))
                                              coll))]
                        {:color-name %
                         :scale      scale})
                     colorlist)]
    (keyed coll ret)
    ret))

(defn kushi-colors-about []
  [:section
   (sx 
    :>p:max-width--$main-content-max-width
    :>p:first-child:mbs--0
    :>p:mb--2em
    :>p:lh--1.7)
   [:p "Kushi includes a foundation of global and alias color tokens."]

   [:div
    (sx
     :xsm:d--none
     :pb--1rem:2.5rem
     :pis--0)
    [playground.colors/color-grid
     {:-row-gap      :4px
      :-column-gap   :8px
      :-labels?      false
      :-swatch-attrs (sx :w--23px :h--23px)}]]

   [:div
    (sx
     :d--none
     :max-width--$main-content-max-width
     :xsm:d--block
     :pb--2rem:4.5rem
     :pis--2.5rem)
    [playground.colors/color-grid
     {:-row-gap      :7px
      :-column-gap   :14px
      :-swatch-attrs (sx :sm:w--34px
                         :sm:h--34px
                         :xsm:w--29px
                         :xsm:h--29px
                         :w--26px
                         :h--26px)}]]

   [:p.alias-token-scales
    "Semantic alias tokens map to global tokens like so:"
    [:br]]

   (into [:div (sx :d--grid
                   :mbs--1em
                   :grg--0.5em
                   :gtc--1.3fr:0.3fr:1fr
                   :w--275px)]
         (for [[a g] [["positive200" "green200"]
                      ["negative400" "red400"]
                      ["warning300" "yellow300"]
                      ["accent600" "blue600"]
                      ["neutral50" "neutral50"]]]
           (alias-global-mapping-row a g)))

   [:p
    "The utility classes "
    [:code (sx :.neutral) ":.neutral"]
    ", "
    [:code (sx :.positive) ":.positive"]
    ", "
    [:code (sx :.negative) ":.negative"]
    ", "
    [:code (sx :.warning) ":.warning"]
    ", and "
    [:code (sx :.accent) ":.accent"]
    " will decorate the element with the corresponding foreground and background colors."]
   
   [divisor]

   [playground.colors/color-rows 
    (color-scales2 
     {:colorlist [:gray :red :orange :gold :yellow :green :blue :purple :magenta :brown]})]
   ])


(defn type-scale [{:keys [coll label]}]
  (into [:div
         (sx :>p:lh--1.7)
         [:h2 
          (sx
           :fs--$large
           :fw--$semi-bold
           [:bbs "1px solid var(--gray-300)"]
           [:dark:bbs "1px solid var(--gray-700)"]
           :pbs--2em
           :mb--5rem:1.5rem)
          #_(trans (sx 
               :.large
               :.semi-bold
               :pbs--2em
               :mb--5rem:1.5rem
               [:bbs "1px solid var(--gray-300)"]
               [:dark:bbs "1px solid var(--gray-700)"]))
          (str "Type " (string/lower-case label) " scale")]
         (let [[kind-of-scale
                start
                end]
               (case label
                 "Size"     ["sizing" "xxxsmall" "xxxxlarge"]
                 "Weight"   ["weight" "thin" "heavy"]
                 "Tracking" ["tracking" "xxxtight" "xxxxloose"])]
           [:p
            (sx :>code:mi--0.25em :>code:ws--n)
            (str "Kushi offers a typographic "
                 kind-of-scale
                 " scale with t-shirt sizing from")
            [:code start]
            "up to"
            [:code end]
            "."])]
        (for [x coll]
          [:div
           (sx :.flex-col-fs :mb--24px)
           [:div (merge
                  (let [tt (when (= label "Tracking") :.uppercase)]
                    {:style (css-vars-map tt)
                     :class (css :.pointer
                                 :tt--$tt
                                 :mbs--10px)}))
            [:span.relative
             (merge-attrs
              {:class [x]}
              (tooltip-attrs {:-text          (str ":." (name x))
                              :-placement     [:inline-end :center]
                              :-tooltip-class "code wee-bold"}))
             "The quick brown fox."]]])))

(defn- formatted-code [s]
  [:pre
   [:code {:class :language-clojure
           :style {:white-space :pre
                   :line-height 1.5}}
    s]])

(defn typography-snippet [s]
  [:div
   (sx :position--relative :mbe--2em)
   [:div
    (sx :.codebox :.code :w--100%)
    (-> s
        (pprint {:max-width 50}) 
        with-out-str
        formatted-code)]
   [:div (sx :.absolute-fill)]
   [copy-to-clipboard-button
    (sx :position--absolute
        :inset-block-start--0
        :inset-inline-end--0
        {:on-click #(copy-to-clipboard! s)})]])


(def typography-tokens-snippet
  '[:span
    (sx :fs--$xxlarge
        :fw--$bold
        :letter-spacing--$xloose)
    "My text "])


;; (def typography-utility-classes-snippet
;;   '[:span (trans (sx :.xxlarge :.bold :.xloose :.uppercase :.italic)) "My text"] )


(def typescale [:xxxsmall :xxsmall :xsmall :small :medium :large :xlarge :xxlarge :xxxlarge :xxxxlarge])


(defn kushi-typography-about
  [m]
  [:section (sx :>*:max-width--$main-content-max-width
                :>p:first-child:mbs--0
                :>p:mb--2em
                :>p:lh--1.7)

  ;;  [:p "Kushi includes a foundation of global tokens and utility class scales for type size, weight, letter-spacing, sizing, and capitalization."]

  ;;  [:div (sx :mbe--0.8em) "Utility classes can be used like this:"]
  ;;  [typography-snippet typography-utility-classes-snippet]

   [:div (sx :mbe--0.8em) "If you need finer control, underlying design tokens can be used like this:"]
   [typography-snippet typography-tokens-snippet]

   (into [:p
          [:span (sx :d--block :mbe--1em) "The following utility classes are available for font-style and capitalization:"]]
         (for [x [:sans :sans-serif :italic :oblique :uppercase :lowercase :capitalize]]
           [:span (sx :.code :fs--0.875rem!important :d--ib :ws--n :mie--0.5em :mbe--0.5em) (str ":." (name x))]))

   [:p
    "Kushi employes the typefaces "
    [link (sx {:href   "https://fonts.google.com/specimen/Inter"
               :target :_blank})
     "Inter"]
    " and "
    [link (sx {:href   "https://fonts.google.com/specimen/Fira+Code"
               :target :_blank})
     "Fira Code"]
    " by default."
    [:br]
    "These can be changed via the " [:code ":ui"] " entry in your theming config map."
    [:br]
    ;; "See example "
    ;; [link (sx {:href   "https://github.com/kushidesign/kushi-quickstart/blob/main/src/main/starter/theme.cljc"
    ;;            :target :_blank})
    ;;  "here."]
    ]

   [type-scale {:label "Size"
                :coll  typescale}]
   [type-scale {:label "Weight"
                :coll  [:thin :extra-light :light :normal :wee-bold :semi-bold :bold :extra-bold :heavy]}]
   [type-scale {:label "Tracking"
                :coll  [:xxxtight :xxtight :xtight :tight :loose :xloose :xxloose :xxxloose]}] ])

(defn kushi-about []
  [:section
   (sx :>*:max-width--550px
       :>p:first-child:mbs--0
       :>p:mb--2em
       :>p:last-of-type:mbe--2.5em
       :>p:lh--1.7
       :pbe--2.25rem)
   [:p
    "Kushi is a base for building web UI with "
    [link {:href   "https://clojurescript.org/"
           :target :_blank}
     "ClojureScript"]
    "."]
   [:p "For detailed docs, check out the "
    [link {:href   "https://github.com/kushidesign/kushi"
           :target :_blank} "Readme"]
    " and the "
    [link {:href   "https://github.com/kushidesign/kushi-quickstart"
           :target :_blank}
     "Quickstart repo"] "."]
   [:p
    "In addition to providing a css-in-cljs solution, Kushi offers a basic suite of themeable, headless UI components for free. "
    "This set of building blocks consitutes a base for rolling your own design system."]
   [:a {:href     "/components"
        :on-click (fn [e]
                    (route! "kushi-playground-menu" "/components" e)
                    #_(component-examples/scroll-to-playground-component!
                     {:component-label "button"
                      :scroll-y        16}))}
    [button 
     (sx  :.filled :.rounded :.semi-bold )
     "Explore components"
     [icon :arrow-right-alt]]]])


(defn component-playground-about []
  [:section
   (sx :>*:max-width--550px
       :>p:first-child:mbs--0
       :>p:mb--2em
       :>p:lh--1.7)
   [:p 
    "This page provides interactive documentation, detailed usage options, and snippet generation for easy inclusion of Kushi UI components in your own project."] ])
