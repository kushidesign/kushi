(ns kushi.playground.colors
  (:require
   [kushi.colors :as kushi.colors]
   [kushi.core :refer (sx css css-vars-map merge-attrs register-design-tokens-by-category)]
   [kushi.ui.core :refer [extract]]
  ;;  [kushi.ui.label.core :refer [label]]
   [kushi.ui.prose.core :refer [prose]]))


(register-design-tokens-by-category "colors" "global colors")

(defn label
  {:desc "A label is typically used for providing titles to sections of
          content."}
  [& args]
  (let [{:keys [opts attrs children]} (extract args label)
        children (map #(if (string? %)
                         [:span.kushi-label-text %]
                         %)
                      children)]
    (into [:span
           (merge-attrs
            {:class         (css
                             ".kushi-label"
                             :.flex-row-c
                             :.enhanceable-with-icon
                             :.transition
                             :jc--fs
                             :d--inline-flex
                             :w--fit-content)
             :data-kushi-ui :label}
            attrs)]
          children)))

(defn text-sample-sticker [& args]
  (let [{:keys [opts attrs children]} (extract args label)
        children                      (map #(if (string? %)
                                              [:span.kushi-label-text %]
                                              %)
                                           children)
        {:keys [sticker-fg-color sticker-bg-bgc]}           opts]
    [label
     [:span (merge-attrs
             {:style (css-vars-map sticker-fg-color sticker-bg-bgc)
              :class (css :.kushi-playground-color-text-sample-sticker
                          :.flex-row-c
                          :fs--$kushi-playground-text-sample-sticker_font-size||$small
                          :sm:w--54px
                          :sm:h--54px
                          :w--44px
                          :h--44px
                          :border-radius--50%
                          :mis--10px
                          :c--$color
                          :bgc--$bgc)}
             attrs)
      "Text"]]))


(defn color-modal
  [{:keys [k
           hsl
           color-name
           color-level]
    :as m}]
  (let [token-name (name k)]
    [:<>
      ;;  [label (sx :.pointer
      ;;             :.code
      ;;             :&.code:bgc--transparent
      ;;             :fs--0.7em
      ;;             :sm:fs--0.9em
      ;;             :ws--n
      ;;             {:on-click #(open-kushi-modal token-name)})
      [label (sx :.kushi-playground-color-sample-row-color-name
                 :.code
                 :_.code:bgc--transparent
                 :fs--0.7em
                 :sm:fs--0.9em
                 :ws--n)
       [:code (sx :.kushi-playground-color-sample-row-color-level
                  :sm:d--none)
        color-level]
       [:code (sx :.kushi-playground-color-sample-row-token-name
                  :sm:d--block
                  :d--none)
        token-name]

      ;; [icon (sx :.accent-secondary-foreground
      ;;           :mis--0.5em
      ;;           {:icon-style :outlined})
      ;;  :help]
       
       ]

    ;; Leave this out until copy to clip is redone

    ;;  [modal
    ;;   {:id token-name}
    ;;   [:div
    ;;    (sx :.flex-col-sa
    ;;        :ai--c
    ;;        :gap--50px
    ;;        :pb--2rem
    ;;        :h--100%
    ;;        :w--100%)
    ;;    [:div (sx :fs--huge
    ;;              :fw--normal
    ;;              :w--100px
    ;;              :h--100px
    ;;              [:bgc hsl])]
    ;;    (let [[s l]     (map #(-> % (string/replace #"\)$" "") string/trim)
    ;;                         (rest (string/split hsl #",")))
    ;;          hue-key   (as-> color-name $
    ;;                      (name $)
    ;;                      (str "--" $ "-hue")
    ;;                      (keyword $))
    ;;          color-obj (tinycolor #js {:h (hue-key base-color-map)
    ;;                                    :s s
    ;;                                    :l l})
    ;;          hex       (.toHexString color-obj)
    ;;          hsl       (.toHslString color-obj)
    ;;          rgb       (.toRgbString color-obj)]
    ;;      [:div
    ;;       (sx :d--grid
    ;;           :ai--c
    ;;           :grid-gap--20px
    ;;           :gtc--1fr:3fr
    ;;           :_.kushi-copy-to-clipboard-button-graphic:width--13px)
    ;;       [:span.kushi-playground-meta-desc-label "name"] [copy-color (string/replace token-name #"^\$" "")]
    ;;       [:span.kushi-playground-meta-desc-label "token"] [copy-color (str ":" token-name)]
    ;;       [:span.kushi-playground-meta-desc-label "css var"] [copy-color (str "var(" (string/replace token-name #"^\$" "--") ")")]
    ;;       [:span.kushi-playground-meta-desc-label "hex"] [copy-color hex]
    ;;       [:span.kushi-playground-meta-desc-label "hsl"] [copy-color hsl]
    ;;       [:span.kushi-playground-meta-desc-label "rgb"] [copy-color rgb]])]]
          
          ]) )


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
        [:div (sx :.kushi-playground-color-scale-wrapper
                  :.transition
                  :max-width--$playground-main-content-max-width
                  :mbs--4.5rem)
         [:h2 
          (sx :.kushi-playground-color-scale-wrapper-header
              :fs--$xlarge
              :fw--$semi-bold
              :tt--capitalize
              :pbs--2em
              :mb--2rem:1.5rem)
          color-name]
         (when semantic-alias
           [prose (sx :.kushi-playground-color-scale-desc
                      :mb--1em:2.5em)
            "All "
            [:code (str "--" (name color-name) "*")]
            " values on the scale have a corresponding "
            [:code (str "--" semantic-alias "*")]
            " alias token"])]

        ;; TODO - refactor hsl css-var stuff below
        (for [[k v color-level] scale
              :let  [hsl         (if (number? v) (str v) (name v))
                     color-token (str "var(--" (name color-name) "-" color-level ")")]]
          ^{:key hsl}
          [:div {:style (css-vars-map row-height color-token hsl)
                 :class (css :.kushi-playground-color-sample-row
                             :.flex-row-fs
                             :ai--stretch
                             :bgc--white
                             :dark:bgc--black
                             :h--$row-height)}
           [:div {:class (css  :.kushi-playground-color-sample-row-swatch
                               :sm:flex-basis--150px
                              :width--66px
                              :sm:width--unset
                              :.no-grow
                              :.no-shrink
                              :bgc--$color-token)}
            #_[:div (sx :w--50% :bgc--$hsl)]
            #_[:div (sx :w--50% :$yellow-hue--59 :bgc--$hsl)]]
           [:div {:class (css :.kushi-playground-color-sample-row-info-container
                              :.flex-row-sb
                              :.grow
                              :pis--0.5em
                              :bbes--solid
                              :bbew--1px
                              :bbec--$color-token)}
            [color-modal {:k           k
                          :hsl         hsl
                          :color-name  color-name
                          :color-level color-level}]
            [:div (sx :.kushi-playground-color-sample-row-text-samples-wrapper
                      :.flex-row-fe :fw--$wee-bold)
             [text-sample-sticker {:sticker-fg-color :white :sticker-bgc-color color-token}]
             [text-sample-sticker {:sticker-fg-color :black :sticker-bgc-color color-token}]
             [text-sample-sticker 
              {:class  (css :.kushi-playground-color-sample-row-text-sample-white-text
                            :bs--solid
                            :bw--1px
                            :bc--$color-token)
               :sticker-fg-color color-token 
               :sticker-bgc-color   :white}]
             [text-sample-sticker 
              {:class  (css :.kushi-playground-color-sample-row-text-sample-black-text
                            :bs--solid :bw--1px :bc--$color-token)
               :sticker-fg-color color-token
               :sticker-bgc-color   :black}]]]]))))])

(defn color-grid [& args]
  (let [{:keys [opts attrs children]}
        (extract args color-grid)

        {:keys [row-gap
                column-gap
                labels?
                select-colors
                swatch-attrs]
         :or   {row-gap     :2px
                column-gap  :2px
                labels?     true}}
        opts]
    (into [:div
           (merge-attrs
            (sx :.kushi-playground-color-sample-grid-column
                :.flex-row-fs
                :border-radius--$rounded-large
                :jc--sb
                ;; [:gap column-gap]
                )
            attrs)]
          (for [[color _] (partition 2 kushi.colors/colors)
                :when     (or (not (seq select-colors))
                              (contains? (into #{} select-colors) color))
                :let [before-content (str "\"" color "\"")
                      ;; TODO can we use kw here?
                      before-display (if labels? "block" "none")]]
            (into [:div 
                   {:style
                    (css-vars-map before-content
                                  before-display
                                  row-gap
                                  column-gap)

                    :class 
                    (css :.kushi-playground-color-sample-grid-column
                         :.flex-col-fs
                         :.transition
                         :bgc--white
                         :position--relative
                         :dark:bgc--black
                         :outline--7px:solid:white
                         :dark:outline--7px:solid:black
                         :last-child:beer--$rounded-large
                         :last-child:bser--$rounded-large
                         :first-child:bssr--$rounded-large
                         :first-child:besr--$rounded-large
                         [:gap :$row-gap]
                         [:before:content :$before-content]
                         [:before:d :$before-display]
                         :before:fs--$xsmall
                         :before:ff--$code-font-stack
                         :before:fw--$wee-bold
                         :before:ta--inline-end
                         :before:position--absolute
                         :before:top--100%
                         :before:left--50%
                         :before:transform-origin--top:left
                         [:before:transform "translate(0, 0.75em) rotate(45deg)"]
                         [:first-child>div:before:d :$before-display])}]
                  (for [n (range 50 1050 50)
                        :let [bgc            (str "var(--" color "-" n ")")
                              before-content (str "\"" n "\"")]]
                    [:div (merge-attrs
                           {:style 
                            (css-vars-map before-content bgc)

                            :class
                            (css :.kushi-playground-color-sample-grid-column-item
                                 :.pill
                                 :position--relative
                                 :w--26px
                                 :h--26px
                                 [:bgc :$bgc]
                                 [:before:content :$before-content]
                                 :before:d--none
                                 :before:fs--$xsmall
                                 :before:ff--$code-font-stack
                                 :before:fw--$wee-bold
                                 :before:position--absolute
                                 :before:top--50%
                                 :before:right--100%
                                 [:before:transform "translate(-1em, -50%)"])}
                           swatch-attrs)]))))))

