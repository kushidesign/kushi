(ns kushi.playground.colors
  (:require
   [clojure.string :as string]
   ["tinycolor2" :as tinycolor]
   [kushi.playground.util :as util :refer-macros (keyed)]
   [kushi.core :refer (sx merge-attrs)]
   [kushi.ui.modal.core :refer [modal open-kushi-modal]]
   [kushi.ui.snippet.core :refer (copy-to-clipboard-button)]
   [kushi.ui.dom :refer (copy-to-clipboard)]
   [kushi.ui.core :refer [defcom]]
   [kushi.ui.icon.core :refer [icon]]
   [kushi.color :refer [base-color-map]]
   [kushi.colors :as kushi.colors]
   [kushi.ui.label.core :refer [label]]))

(defcom text-sample-sticker
  (let [{:keys [color bgc]} &opts]
    [label
     [:span (merge-attrs
             (sx :.flex-row-c
                 :fs--$kushi-playground-text-sample-sticker_font-size||$small
                 :sm:w--54px
                 :sm:h--54px
                 :w--44px
                 :h--44px
                 :border-radius--50%
                 :mis--10px
                 [:c color]
                 [:bgc bgc])
             &attrs)
      "Text"]]))

(defn copy-color [s]
 [:span (sx :.flex-row-fs
            [:&_.kushi-button>img:transform '(scale 0.75)])
  [:code s]
  [copy-to-clipboard-button
   {:-placement :right
    :on-click   #(copy-to-clipboard s)}]])

(defn color-modal
  [{:keys [k
           hsl
           color-name
           color-level]
    :as m}]
  (let [token-name (name k)]
    [:<>
     [label (sx :.pointer
                :.code
                :&.code:bgc--transparent
                :fs--0.7em
                :sm:fs--0.9em
                :ws--n
                {:on-click #(open-kushi-modal token-name)})
      [:span (sx :sm:d--none) color-level]
      [:span (sx :.code :sm:d--block :d--none) token-name]
      [icon (sx :.neutral-secondary-fg :mis--0.5em {:-icon-style :outlined}) :help]]
     [modal
      {:id token-name}
      [:div
       (sx :.flex-col-sa
           :ai--c
           :gap--50px
           :pb--2rem
           :h--100%
           :w--100%)
       [:div (sx :.huge
                 :.normal
                 :w--100px
                 :h--100px
                 [:bgc hsl])]
       (let [[s l]     (map #(-> % (string/replace #"\)$" "") string/trim)
                            (rest (string/split hsl #",")))
             hue-key   (as-> color-name $
                         (name $)
                         (str "--" $ "-hue")
                         (keyword $))
             color-obj (tinycolor #js {:h (hue-key base-color-map)
                                       :s s
                                       :l l})
             hex       (.toHexString color-obj)
             hsl       (.toHslString color-obj)
             rgb       (.toRgbString color-obj)]
         [:div
          (sx :d--grid
              :ai--c
              :grid-gap--20px
              :gtc--1fr:3fr
              :&_.kushi-copy-to-clipboard-button-graphic:width--13px)
          [:span.kushi-playground-meta-desc-label "name"] [copy-color (string/replace token-name #"^\$" "")]
          [:span.kushi-playground-meta-desc-label "token"] [copy-color (str ":" token-name)]
          [:span.kushi-playground-meta-desc-label "css var"] [copy-color (str "var(" (string/replace token-name #"^\$" "--") ")")]
          [:span.kushi-playground-meta-desc-label "hex"] [copy-color hex]
          [:span.kushi-playground-meta-desc-label "hsl"] [copy-color hsl]
          [:span.kushi-playground-meta-desc-label "rgb"] [copy-color rgb]])]]]) )


;; TODO refactor this into some subcomponents
;; TODO Add an interactive "index" by color to jump to specific colors
(defn color-rows [color-scales]
  [:<>
   (let [row-height   :75px
         kushi-colors (reduce (fn [acc [k v]]
                                (assoc acc (keyword k) v))
                              {}
                              (partition 2 kushi.colors/colors))]
     (for [{:keys [color-name scale]} color-scales
           :let [semantic-alias (some-> kushi-colors
                                        (get color-name)
                                        :alias)]]
       (into
        ^{:key color-name}
        [:div (sx 'color-scale-wrapper
                  :.transition
                  :mbs--4.5rem)
         [:h3 (sx :.xxlarge
                  :mbe--2rem
                  :.wee-bold
                  :tt--capitalize)
          color-name]
         (when semantic-alias
           [:p (sx :.normal
                   :mb--1em:2.5em)
            "All "
            [:code (str "--" (name color-name) "*")]
            " values on the scale have a corresponding "
            [:code (str "--" semantic-alias "*")]
            " alias token"])]
        (for [[k v color-level] scale
              :let  [hsl (if (number? v) (str v) (name v))]]
          ^{:key hsl}
          [:div (sx :.flex-row-fs
                    :ai--stretch
                    :bgc--white
                    :dark:bgc--black
                    [:h row-height])
           [:div (sx :sm:flex-basis--150px
                     :width--66px
                     :sm:width--unset
                     :.no-grow
                     :.no-shrink
                     [:bgc hsl]
                    ;;  :.flex-row-c
                    ;;  :ai--stretch
                     )
            #_[:div (sx :w--50% [:bgc hsl])]
            #_[:div (sx :w--50% :$yellow-hue--59 [:bgc hsl])]]
           [:div (sx :.flex-row-sb
                     :.grow
                     :pis--0.5em
                     :bbes--solid
                     :bbew--1px
                     [:bbec hsl])
            [color-modal (keyed k hsl color-name color-level)]
            [:div (sx :.flex-row-fe
                      :.wee-bold)
             [text-sample-sticker (sx {:-color :white
                                       :-bgc   hsl})]
             [text-sample-sticker (sx {:-color :black
                                       :-bgc   hsl})]
             [text-sample-sticker (sx :bs--solid
                                      :bw--1px
                                      [:bc hsl]
                                      {:-color hsl
                                       :-bgc :white})]
             [text-sample-sticker (sx [:bc hsl]
                                      :bs--solid
                                      :bw--1px
                                      {:-bgc   :black
                                       :-color hsl})]]]]))))])
(defcom color-grid
  (let [{:keys [row-gap
                column-gap
                labels?
                select-colors
                swatch-attrs]
         :or   {row-gap     :2px
                column-gap  :2px
                labels?     true}}
        &opts]
    (into [:div
           (merge-attrs
            (sx :.flex-row-fs
                :.rounded-large
                [:gap column-gap])
            &attrs)]
          (for [[color _] (partition 2 kushi.colors/colors)
                :when (or (not (seq select-colors))
                          (contains? (into #{} select-colors) color))]
            (into [:div (sx :.flex-col-fs
                            :bgc--white
                            :dark:bgc--black
                            :outline--7px:solid:white
                            :dark:outline--7px:solid:black
                            :last-child:beer--$rounded-large
                            :last-child:bser--$rounded-large
                            :first-child:bssr--$rounded-large
                            :first-child:besr--$rounded-large
                            [:gap row-gap]
                            :.relative
                            [:before:content (str "\"" color "\"")]
                            [:before:d (if labels? :block :none)]
                            :before:fs--$xsmall
                            :before:ff--$code-font-stack
                            :before:fw--$wee-bold
                            :before:ta--inline-end
                            :before:position--absolute
                            :before:top--100%
                            :before:left--50%
                            :before:transform-origin--top:left
                            [:before:transform "translate(0, 0.75em) rotate(45deg)"]
                            [:first-child>div:before:d (if labels? :block :none)])]
                  (for [n (range 50 1050 50) ]
                    [:div (merge-attrs
                           (sx
                            :.relative
                            :.pill
                            :w--26px
                            :h--26px
                            [:bgc (str "var(--" color "-" n ")")]
                            [:before:content (str "\"" n "\"")]
                            :before:d--none
                            :before:fs--$xsmall
                            :before:ff--$code-font-stack
                            :before:fw--$wee-bold
                            :before:position--absolute
                            :before:top--50%
                            :before:right--100%
                            [:before:transform '(translate :-1em :-50%)])
                           swatch-attrs)]))))))

