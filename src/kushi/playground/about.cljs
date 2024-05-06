(ns kushi.playground.about
  (:require
   [kushi.ui.label.core :refer [label]]
   [kushi.ui.link.core :refer [link]]
   [domo.core :as domo]
   [kushi.ui.core :refer [defcom]]
   [kushi.core :refer (sx merge-attrs)]
   [kushi.ui.snippet.core :refer (copy-to-clipboard-button)]
   [domo.core :refer (copy-to-clipboard!)]
   [kushi.ui.tooltip.core :refer [tooltip-attrs]]
   [kushi.playground.state :as state]
   [kushi.playground.util :as util]
   [kushi.playground.colors :as playground.colors]
   [kushi.playground.shared-styles]))


(defcom intro-section
  (let [{:keys [header]} &opts]
    [:section
     (merge-attrs
      (sx 'playground-generic-intro-section
          :d--none
          :sm:d--block
          :.transition
          :bbe--$divisor
          :dark:bbe--$divisor-dark
          :pbe--0.5rem
          :md:pbe--0.6rem

         ;; typography
          ["has-ancestor(.kushi-typography):bbe" :none]
          ["has-ancestor(.custom-typography):bbe" :none]
          ["has-ancestor(.kushi-typography)&_.code:h" :fit-content]
          ["has-ancestor(.kushi-typography)&_code:h" :fit-content]
          ["has-ancestor(.kushi-typography)&_.code:lh" :initial]
          ["has-ancestor(.kushi-typography)&_code:lh" :initial]

         ;; colors
          ["has-ancestor(.kushi-colors):bbe" :none]
          ["has-ancestor(.custom-colors):bbe" :none]
          ["has-ancestor(.kushi-colors)&_p.alias-token-scales:mbe" :0.3em])
      &attrs)
     [:div (sx
            'intro-section-title-wrapper
            :.flex-col-fs
            :.wee-bold
            :.relative
            :w--100%
            :mbe--4.25rem)
      [label
       (sx 'intro-section-title
           :fs--$kushi-playground-main-section-header_font-size||$xxlarge
           :pbs--2.25em
           :md:pbs--$vp-top-header-padding-with-offset
           {:style {:transform '(translateX :-1.5px)}})
       (if @state/*md-or-smaller?
         "Playground"
         header)]]
     [:section
      (sx 'intro-section-body
          :.normal
          :.transition
          :pie--2rem
          :&_p:line-height--$body-copy-line-height
          :&_p:margin-block--2em)
      &children]]))


(defn alias-global-mapping-row [a g]
  [:<>
   [:code (sx :.code) (str "--" a)]
   [:span "→"]
   [:code (sx :.code) (str "--" g)]])


(def kushi-colors-about
  [:span
   [:p "Kushi includes a foundation of global and alias color token scales."]

   [:div
    (sx
     :sm:d--none
     :pb--1rem:2.5rem
     :pis--0)
    [playground.colors/color-grid
     {:-row-gap     :4px
      :-column-gap  :8px
      :-labels?     false
      :-swatch-attrs (sx :w--23px :h--23px)}]]

   [:div
    (sx
     :d--none
     :sm:d--block
     :pb--2rem:4.5rem
     :pis--2.5rem)
    [playground.colors/color-grid
     {:-row-gap      :7px
      :-column-gap   :14px
      :-swatch-attrs (sx :w--34px :h--34px)}]]

   [:p.alias-token-scales
    "Semantic alias tokens map to global tokens like so:"
    [:br]]

   (into [:div (sx :.grid
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
    " will decorate the element with the corresponding foreground and background colors."]])


(defn type-scale [{:keys [coll label]}]
  (into [:div
         [:h3 (sx :fs--$kushi-playground-main-section-header_font-size||$xxlarge
                  :.wee-bold
                  :mbs--3em
                  :pbs--1.5em
                  [:bbs "1px solid var(--gray-300)"]
                  [:dark:bbs "1px solid var(--gray-700)"])
          (str "Type " label " Scale")]
         (when (= "Size" label)
           [:p
            (sx :>code:mi--0.25em :>code:ws--n)
            "Kushi offers a typographic scale with t-shirt sizing from"
            [:code "xxxsmall"]
            "up to"
            [:code "xxxxlarge"]])]
        (for [x coll]
          [:div
           (sx :.flex-col-fs :mb--24px)
           [:div (merge
                  (sx :.pointer
                      (when (= label "Tracking") :.uppercase)
                      :mbs--10px))
            [:span.relative
             (merge-attrs
              {:class [x]}
              (tooltip-attrs {:-text          (str ":." (name x))
                              :-placement     [:inline-end :center]
                              :-tooltip-class "code wee-bold"}))
             "The quick brown fox."]]])))


(defn typography-snippet [s]
  [:div
   (sx :.relative :mbe--2em)
   [:div
    (sx :.codebox :.code :w--100%)
    [util/formatted-code s]]
   [:div (sx :.absolute-fill)]
   [copy-to-clipboard-button
    (sx :.absolute
        :inset-block-start--0
        :inset-inline-end--0
        {:on-click #(copy-to-clipboard! s)})]])


(def typography-tokens-snippet
  "[:span
  (sx :fs--$xxlarge
      :fw--$bold
      :letter-spacing--$xloose)
  \"My text\"]" )


(def typography-utility-classes-snippet
  "[:span \n  (sx :.xxlarge :.bold :.xloose :.uppercase :.italic) \n  \"My text\"]" )

(def typescale [:xxxsmall :xxsmall :xsmall :small :medium :large :xlarge :xxlarge :xxxlarge :xxxxlarge])

(defn kushi-typography-about
  [m]
  [:span
   [:p "Kushi includes a foundation of global tokens and utility class scales for type size, weight, letter-spacing, sizing, and capitalization."]

   [:div (sx :mbe--0.8em) "Utility classes can be used like this:"]
   [typography-snippet typography-utility-classes-snippet]

   [:div (sx :mbe--0.8em) "If you need finer control, underlying design tokens can be used like this:"]
   [typography-snippet typography-tokens-snippet]

   (into [:p
          [:span (sx :.block :mbe--1em) "The following utility classes are available for font-style and capitalization:"]]
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

(def kushi-about
  [:span
   [:p
    "Kushi is a base for building web UI with "
    [link (sx {:href   "https://clojurescript.org/"
               :target :_blank}) "ClojureScript"] "."]
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
   [:p
    "The components menu on this site provides interactive documentation, detailed usage options, and snippet generation for easy inclusion of Kushi UI components in your own project."]])


(defn component-playground-about [{:keys [header]}]
  (when (or @state/*md-or-smaller?
            (not @state/*focused-component))
    [intro-section {:-header header}]))
