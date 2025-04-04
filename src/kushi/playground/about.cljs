(ns kushi.playground.about
  (:require [clojure.string :as string]
            [domo.core :refer (copy-to-clipboard!)]
            [kushi.colors]
            [kushi.core :refer (sx css merge-attrs css-vars-map)]
            [kushi.playground.shared-styles]
            [kushi.ui.button.core :refer [button]]
            [kushi.ui.divisor.core :refer (divisor)]
            [kushi.ui.icon.core :refer [icon]]
            [kushi.ui.link.core :refer [link]]
            [kushi.ui.snippet.core :refer (copy-to-clipboard-button)]
            [kushi.ui.tooltip.core :refer [tooltip-attrs]]
            [me.flowthing.pp :refer [pprint]]))

(defn diamond-swatch [s n]
  [:span (merge-attrs
          (sx :h--0px
              :pbe--100%
              :br--0px
              :md:scale--1.2
              :rotate---45deg)
          {:style {:background-color (str "var(--" s "-" n ")")}})])

(defn diamond-swatches [coll s]
  (into [:div (sx :pb--4%:3%)]
        (for [n coll]
          [diamond-swatch s n])))

(defn diamond-swatch-labels [coll]
  (into [:div (sx :ji--c)]
        (for [n coll]
          [:code.xxxsmall n])))

(defn swatches-1-row [levels color-name]
  (let [grid-repeat-sm (str "repeat(" (count levels) ", 1fr)" )]
    [:div {:class (css {:d    :none
                        :md:d :block
                        :w    "calc(100% - 95px)"
                        :>div {:d   :grid
                               :jc  :fe
                               :gtc :$grid-repeat-sm}})
           :style (css-vars-map grid-repeat-sm)}
     (into [:div (sx :pb--4%:2%)]
           (for [n levels]
             [diamond-swatch color-name n]))
     [diamond-swatch-labels levels]]))

(defn swatches-2-row [levels color-name]
  (let [grid-repeat (str "repeat(" (/ (count levels) 2) ", 1fr)" )
        first-10    (take 10 levels)
        last-10     (take-last 10 levels)]
    [:div (merge-attrs 
           (sx {:md:d                       :none
                :pi                         :0.5rem
                :>div:gtc                   :$grid-repeat
                :>div:jc                    :fe
                :>div:d                     :grid})
           {:style (css-vars-map grid-repeat)})

     [diamond-swatches first-10 color-name]
     [diamond-swatch-labels first-10]
     
     [diamond-swatches last-10 color-name]
     [diamond-swatch-labels last-10]]))

(defn kushi-colors-about []
  [:section
   (sx 
    :>p:max-width--$playground-main-content-max-width
    :>p:first-child:mbs--0
    :>p:mb--2em
    :>p:lh--1.7)
   [:p.prose "Kushi includes a foundation of global and alias color tokens."]
   [:p]
   [divisor]
   (into [:div (sx :.transition
                   :mbs--2rem
                   :dark:bgc--black
                   ;; TODO - can you transition box-shadow
                   :dark:box-shadow---200px:1px:0px:black|200px:1px:0px:black)]
         (for [k    [:gray :purple :blue :green :lime :yellow :gold :orange :red :magenta :brown]
               :let [levels (range 50 1050 50)
                     color-name (name k)
                     alias (get kushi.colors/aliases-by-color color-name)]]
           [:div (sx :.flex-col-sb
                     :gap--7px
                     :md:gap--0
                     :md:flex-direction--row
                     :md:ai--c
                     :mbs--3rem
                     :md:mbs--1rem)
            [:h2 (sx :.semi-bold
                     :.relative
                     :w--fit-content
                     :translate--0:-4px)
             (string/capitalize color-name)
             (when alias
               [:<> 
                [:code (sx :.absolute
                           :.xxsmall 
                           :md:d--none
                           :bottom--0%
                           [:left "calc(100% + 0.5rem)"])
                 alias]
                [:code (sx :.absolute 
                           :.xxsmall
                           :d--none
                           :md:d--block
                           :mbs--0.5em
                           :top--100% 
                           :left--0)
                 alias]])]
            [swatches-2-row levels color-name]
            [swatches-1-row levels color-name]]))])


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
          (str "Type " (string/lower-case label) " scale")]
         (let [[kind-of-scale
                start
                end]
               (case label
                 "Size"     ["sizing" "xxxsmall" "xxxxlarge"]
                 "Weight"   ["weight" "thin" "heavy"]
                 "Tracking" ["tracking" "xxxtight" "xxxxloose"])]
           [:p.prose
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
              (tooltip-attrs {:-text          (name x)
                              :-placement     [:inline-end :center]
                              :-tooltip-class (css :.code
                                                   :fw--$wee-bold
                                                   :fs--$small)}))
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
    (merge-attrs
     (sx :position--absolute
         :inset-block-start--0
         :inset-inline-end--0)
     {:on-click #(copy-to-clipboard! s)})]])


(def typography-tokens-snippet
  '[:span
    (sx :fs--$xxlarge
        :fw--$bold
        :letter-spacing--$xloose)
    "My text "])


(def typography-utility-classes-snippet
  '[:span (sx :.xxlarge :.bold :.xloose :.uppercase :.italic) "My text"] )


(def typescale [:xxxsmall :xxsmall :xsmall :small :medium :large :xlarge :xxlarge :xxxlarge :xxxxlarge])


(defn kushi-typography-about
  [m]
  [:section (sx :>*:max-width--$playground-main-content-max-width
                :>p:first-child:mbs--0
                :>p:mb--2em
                :>p:lh--1.7)

   [:p.prose "Kushi includes a foundation of global tokens and utility class scales for type size, weight, letter-spacing, sizing, and capitalization."]

   [:label.prose (sx :mbe--0.8em) "Utility classes can be used like this:"]
   [typography-snippet typography-utility-classes-snippet]

   [:label.prose (sx :mbe--0.8em) "If you need finer control, underlying design tokens can be used like this:"]
   [typography-snippet typography-tokens-snippet]

   (into [:p.prose
          [:span (sx :d--block :mbe--1em) "The following utility classes are available for font-style and capitalization:"]]
         (for [x [:sans :serif :italic :oblique :uppercase :lowercase :capitalize]]
           [:span (sx :.code :fs--0.875rem!important :d--ib :ws--n :mie--0.5em :mbe--0.5em) (str ":." (name x))]))

   (into [:p.prose
          [:span (sx :d--block :mbe--1em)
           "Kushi employes the typefaces "
           [link {:href   "https://fonts.google.com/specimen/Inter"
                  :target :_blank}
            "Inter"]
           " and "
           [link {:href   "https://fonts.google.com/specimen/Fira+Code"
                  :target :_blank}
            "Fira Code"]
           " by default."
           [:br]
           "These can be changed via the following tokens in the " [:code "[:theme :design-tokens]"] " entry in your theming config map (in " [:code "kushi.edn"] " file):"
           [:br]
          ;; "See example "
          ;; [link (sx {:href   "https://github.com/kushidesign/kushi-quickstart/blob/main/src/main/starter/theme.cljc"
          ;;            :target :_blank})
          ;;  "here."]
           ]]
         (for [x [:--sans-serif-font-stack
                  :--serif-font-stack
                  :--code-font-stack]]
           [:span (sx :.code
                      :fs--0.875rem!important
                      :d--ib
                      :ws--n
                      :mie--0.5em
                      :mbe--0.5em) 
            (str ":." (name x))]))

   [type-scale {:label "Size"
                :coll  typescale}]
   [type-scale {:label "Weight"
                :coll  [:thin :extra-light :light :normal :wee-bold :semi-bold :bold :extra-bold :heavy]}]
   [type-scale {:label "Tracking"
                :coll  [:xxxtight :xxtight :xtight :tight :loose :xloose :xxloose :xxxloose]}] ])


(defn component-playground-about []
  [:section
   (sx :>*:max-width--550px
       :>p:first-child:mbs--0
       :>p:mb--2em
       :>p:lh--1.7)
   [:p.prose 
    "This page provides interactive documentation, detailed usage options, and snippet generation for easy inclusion of Kushi UI components in your own project."] ])
