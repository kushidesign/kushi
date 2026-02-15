(ns ^{:kushi/layer "user-styles"} kushi.ui.icon-button.demo
  (:require
   [kushi.ui.icon-button :refer [icon-button]]
   [kushi.ui.spinner :refer [spinner]]
   [kushi.core :refer [sx]]
   [kushi.showcase.core
    :as showcase
    :refer [samples samples-with-variant]]))


(def demos
  #_[
  #_{:label   "Contour"
    :desc    "Contour of the button."
    :samples (samples-with-variant
              {:variant       :shape
               :variant-scale :shape/rounded-md-3xl
               :attrs         {:size       :3xl
                               :end-enhancer :east
                               :colorway     :accent
                               :surface      :solid
                               :style   {:font-size :50px}}
               :args          ["Next"]})}
   #_{:label   "Shadow"
    :desc    "Preset shadow styles."
    :samples (samples-with-variant
              {:variant       :shadow
              ;;  :variant-scale :shape/basic+rounded
               :attrs         {:end-enhancer :east
                               :colorway     :accent
                               :surface      :minimal
                               :shadow-color :red}
               :args          ["Next"]})}
   ]

  [
   {:samples (samples-with-variant
              {:args          [:star],
               :row-attrs     (sx :_.surface-transparent:display--none)
               :variant       :colorway,
               :variant-scale :colorway/named,
               :x-variants    [:surface/basic],
               :snippets?     false
               ;;  :attrs/snippet  {:surface :solid},
               ;; :attrs/display  {:size :sm},
               ;;  :label          "Colorways, surfaces × shapes",
               :label         "Colorways × surfaces",
               :attrs         {:end-enhancer :east
                               :icon-filled  true
                               :shape        :rounded},
               :rows?         true})}

   {:samples (samples-with-variant
              {:args          [:star],
               :row-attrs     (sx :_.surface-transparent:display--none)
               :variant       :shape,
               :variant-scale :shape/basic,
               :x-variants    [:surface/basic],
               :snippets?     false
              ;;  :attrs/snippet  {:surface :solid},
               ;; :attrs/display  {:size :sm},
              ;;  :label          "Colorways, surfaces × shapes",
               :label         "Contour × surfaces",
               :attrs         {:end-enhancer :east
                               :icon-filled  true
                               :colorway     :accent},
               :rows?         true})}

   {:desc    "Surface variant of the button.",
    :label   "Surface",
    :samples (samples-with-variant
              {:attrs         {:end-enhancer :east
                               :icon-filled  true
                               :shape        :rounded
                               :colorway     :accent},
               :args          [:star],
               :variant       :surface
               :variant-scale :surface/basic})}
   
   {:label   "Shape"
    :desc    "Shape of the button"
    :samples (samples-with-variant
              {:variant       :shape
               :variant-scale :shape/basic+rounded
               :attrs         {:end-enhancer :east
                               :icon-filled  true
                               :colorway     :accent
                               :surface      :solid}
               :args          [:star]})}
   
   {:desc    "General amount of padding inside the button",
    :label   "Packing",
    :samples (samples-with-variant
              {:attrs   {:end-enhancer :east
                         :icon-filled  true
                         :shape        :rounded
                         :colorway     :accent
                         :surface      :solid},
               :args    [:star],
               :variant :packing})}

   {:label   "size"
    ;;  :label/modal "Colorways ..."
    :desc    "Text sizes from 3xs to 3xl"
    ;; :row-style {:border "1px solid red"}
    :samples (samples-with-variant
              {:variant :size
              ;; :variant-labels? false
               :attrs   {:surface      :solid
                         :shape        :rounded
                         :icon-filled  true
                         :end-enhancer :east
                         :colorway     :accent}
               :args    [:star]})}

   {:label   "Stroke"
    :desc    "Preset stroke styles."
    :samples (samples-with-variant
              {:variant :stroke
              ;;  :variant-scale :shape/basic+rounded
               :attrs   {:end-enhancer :east
                         :shape        :rounded
                         :icon-filled  true
                         :colorway     :accent
                         :surface      :minimal}
               :args    [:star]})}

   {:label   "Stroke width"
    :desc    "Custom stroke widths."
    :samples (samples [[icon-button
                        {:end-enhancer :east,
                         :shape        :rounded
                         :icon-filled  true
                         :colorway     :accent,
                         :surface      :minimal,
                         :stroke       :hard
                         :stroke-width :1px
                         :stroke-align :inside}
                        :star]
                       [icon-button
                        {:end-enhancer :east,
                         :shape        :rounded
                         :icon-filled  true
                         :colorway     :accent,
                         :surface      :minimal,
                         :stroke       :hard
                         :stroke-width :2px
                         :stroke-align :inside}
                        :star]
                       [icon-button
                        {:end-enhancer :east,
                         :shape        :rounded
                         :icon-filled  true
                         :colorway     :accent,
                         :surface      :minimal,
                         :stroke       :hard
                         :stroke-width :3px
                         :stroke-align :inside}
                        :star]
                       [icon-button
                        {:end-enhancer :east,
                         :shape        :rounded
                         :icon-filled  true
                         :colorway     :accent,
                         :surface      :minimal,
                         :stroke       :hard
                         :stroke-width :4px
                         :stroke-align :inside}
                        :star]
                       ])}


   {:schema  #{:inside :outside},
    :desc    "Alignment of the stroke. Only applies to `:surface` `:outline`",
    :label   "Stroke alignment",
    :samples (samples [[icon-button
                        {:end-enhancer :east,
                         :shape        :rounded
                         :icon-filled  true
                         :colorway     :accent,
                         :surface      :minimal,
                         :stroke       :md
                         :stroke-width :2px
                         :stroke-align :inside}
                        :star]
                       [icon-button
                        {:end-enhancer :east,
                         :shape        :rounded
                         :icon-filled  true
                         :colorway     :accent,
                         :surface      :minimal,
                         :stroke       :md
                         :stroke-width :2px
                         :stroke-align :outside}
                        :star]])}

   
   ;; TODO Fix this
   #_{:label   "Loading states",
    :desc    "When `true` this will set the appropriate values for `aria-busy` and `aria-label`",
    :require '[[kushi.ui.icon :refer [icon]]
               [kushi.ui.spinner :refer [spinner]]],
    :samples (samples
              [[icon-button
                {,
                 :shape        :rounded
                 :icon-filled  true
                 :colorway     :accent,
                 :surface      :solid,
                 :end-enhancer [spinner {:spinner-type :donut}]
                 }
                :star]
               [icon-button
                {,
                 :shape        :rounded
                 :icon-filled  true
                 :colorway     :accent,
                 :surface      :solid,
                 :end-enhancer [spinner {:spinner-type :propeller}]}
                :star]
               [icon-button
                {,
                 :shape        :rounded
                 :icon-filled  true
                 :colorway     :accent,
                 :surface      :solid,
                 :end-enhancer [spinner {:spinner-type :thinking}]}
                :star]
               [icon-button
                {
                 :shape       :rounded
                 :icon-filled true
                 :colorway    :accent
                 :surface     :solid}
                #_[:span {:style {:visibility :hidden
                                :width      :0px}} 
                 :star]
                [spinner {:spinner-type :thinking}]] ])}
   
   {:label   "Disabled state",
    :desc    "When `true` this will set the appropriate values for `aria-busy` and `aria-label`",
    :samples (samples
              [[icon-button
                {:disabled     true,
                 :colorway     :accent,
                 :surface      :solid,}
                :star]])}])
