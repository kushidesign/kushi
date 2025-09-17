(ns ^{:kushi/layer "user-styles"} kushi.ui.button.demo
  (:require
   [kushi.ui.icon :refer [icon]]
   [kushi.ui.button :refer [button]]
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
               :variant-scale :shape/rounded-medium-xxxlarge
               :attrs         {:size       :xxxlarge
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
              {:args          ["Next"],
              ;;  :row-attrs     (sx :_.surface-transparent:display--none)
               :variant       :colorway,
               :variant-scale :colorway/named,
               :x-variants    [:surface/basic],
               :snippets?     false
               ;;  :attrs/snippet  {:surface :solid},
               ;; :attrs/display  {:size :small},
               ;;  :label          "Colorways, surfaces × shapes",
               :label         "Colorways × surfaces",
               :attrs         {:end-enhancer :east
                               :shape      :rounded},
               :rows?         true})}

   {:samples (samples-with-variant
              {:args          ["Next"],
               :row-attrs     (sx :_.surface-transparent:display--none)
               :variant       :shape,
               :variant-scale :shape/basic,
               :x-variants    [:surface/basic],
               :snippets?     false
              ;;  :attrs/snippet  {:surface :solid},
               ;; :attrs/display  {:size :small},
              ;;  :label          "Colorways, surfaces × shapes",
               :label         "Contour × surfaces",
               :attrs         {:end-enhancer :east
                               :colorway     :accent},
               :rows?         true})}

   {:desc    "Surface variant of the button.",
    :label   "Surface",
    :samples (samples-with-variant
              {:attrs         {:end-enhancer :east
                               :colorway     :accent},
               :args          ["Next"],
               :variant       :surface
               :variant-scale :surface/basic})}
   
   {:label   "Contour"
    :desc    "Contour of the button."
    :samples (samples-with-variant
              {:variant       :shape
               :variant-scale :shape/basic+rounded
               :attrs         {:end-enhancer :east
                               :colorway     :accent
                               :surface      :solid}
               :args          ["Next"]})}
   
   {:desc    "General amount of padding inside the button",
    :label   "Packing",
    :samples (samples-with-variant
              {:attrs   {:end-enhancer :east
                         :colorway     :accent
                         :surface      :solid},
               :args    ["Next"],
               :variant :packing})}

   {:label   "size"
    ;;  :label/modal "Colorways ..."
    :desc    "Sizes from xxxsmall to xxxlarge"
    ;; :row-style {:border "1px solid red"}
    :samples (samples-with-variant
              {:variant :size
              ;; :variant-labels? false
               :attrs   {:surface      :solid
                         :end-enhancer :east
                         :colorway     :accent}
               :args    ["Next"]})}

   {:label   "Stroke"
    :desc    "Preset stroke styles."
    :samples (samples-with-variant
              {:variant       :stroke
              ;;  :variant-scale :shape/basic+rounded
               :attrs         {:end-enhancer :east
                               :colorway     :accent
                               :surface      :minimal}
               :args          ["Next"]})}

   {:label   "Stroke width"
    :desc    "Custom stroke widths."
    :samples (samples [[button
                        {:end-enhancer :east,
                         :colorway     :accent,
                         :surface      :minimal,
                         :stroke       :medium
                         :stroke-width :1px
                         :stroke-align :inside}
                        "Next"]
                       [button
                        {:end-enhancer :east,
                         :colorway     :accent,
                         :surface      :minimal,
                         :stroke       :medium
                         :stroke-width :2px
                         :stroke-align :inside}
                        "Next"]
                       [button
                        {:end-enhancer :east,
                         :colorway     :accent,
                         :surface      :minimal,
                         :stroke       :medium
                         :stroke-width :3px
                         :stroke-align :inside}
                        "Next"]
                       [button
                        {:end-enhancer :east,
                         :colorway     :accent,
                         :surface      :minimal,
                         :stroke       :medium
                         :stroke-width :4px
                         :stroke-align :inside}
                        "Next"]
                       ])}

   {:schema  #{:inside :outside},
    :desc    "Alignment of the stroke. Only applies to `:surface` `:outline`",
    :label   "Stroke alignment",
    :samples (samples [[button
                        {:end-enhancer :east,
                         :colorway     :accent,
                         :surface      :minimal,
                         :stroke       :medium
                         :stroke-width :2px
                         :stroke-align :inside}
                        "Next"]
                       [button
                        {:end-enhancer :east,
                         :colorway     :accent,
                         :surface      :minimal,
                         :stroke       :medium
                         :stroke-width :2px
                         :stroke-align :outside}
                        "Next"]])}

   {:desc    "Content at the inline-start position following the button text. Typically an icon.",
    :label   "Start-enhancer",
    :samples (samples [[button
                        {:start-enhancer :west,
                         :colorway       :accent,
                         :surface        :solid}
                        "Back"]
                       [button
                        {:start-enhancer :auto-awesome,
                         :colorway       :accent,
                         :surface        :soft}
                        "Enhance"]
                       [button
                        {:start-enhancer :play-arrow,
                         :colorway       :accent,
                         :surface        :faint
                         :stroke         :soft
                         }
                        "Play"]
                       [button
                        {:start-enhancer :check-circle,
                         :colorway       :accent,
                         :surface        :minimal
                         :stroke         :xsoft}
                        "Confirm"]
                       ])}
   
   {:desc    "Content at the inline-end position preceding the button text. Typically an icon.",
    :label   "End-enhancer",
    :require [[kushi.ui.icon :refer [icon]]],
    :samples (samples [[button
                        {:end-enhancer :east,
                         :colorway     :accent,
                         :surface      :solid}
                        "Next"]
                       [button
                        {:end-enhancer :auto-awesome,
                         :colorway     :accent,
                         :surface      :soft}
                        "Enhance"]
                       [button
                        {:end-enhancer :play-arrow,
                         :colorway     :accent,
                         :surface      :faint
                         :stroke       :soft}
                        "Play"]
                       [button
                        {:end-enhancer :check-circle,
                         :colorway     :accent,
                         :surface      :minimal
                         :stroke       :xsoft}
                        "Confirm"]
                       ])}

   {:label   "Loading states",
    :desc    "When `true` this will set the appropriate values for `aria-busy` and `aria-label`",
    :schema  boolean?,
    :require '[[kushi.ui.button :refer [button]]
               [kushi.ui.icon :refer [icon]]
               [kushi.ui.spinner :refer [spinner]]],
    :samples (samples
              [[button
                {:loading      true,
                 :colorway     :accent,
                 :surface      :solid,
                 :end-enhancer [spinner {:spinner-type :donut}]
                 }
                "Play"]
               [button
                {:loading      true,
                 :colorway     :accent,
                 :surface      :solid,
                 :end-enhancer [spinner {:spinner-type :propeller}]}
                "Play"]
               [button
                {:loading      true,
                 :colorway     :accent,
                 :surface      :solid,
                 :end-enhancer [spinner {:spinner-type :thinking}]}
                "Play"]
               [button
                {:loading  true
                 :colorway :accent
                 :surface  :solid}
                [:span {:style {:visibility :hidden
                                :width      :0px}} 
                 "Play"]
                [spinner {:spinner-type :thinking}]] ])}
   
   {:label   "Disabled state",
    :desc    "When `true` this will set the appropriate values for `aria-busy` and `aria-label`",
    :schema  boolean?,
    :require '[[kushi.ui.button :refer [button]]
               [kushi.ui.icon :refer [icon]]
               [kushi.ui.spinner :refer [spinner]]],
    :samples (samples
              [[button
                {:disabled     true,
                 :colorway     :accent,
                 :surface      :solid,
                 :end-enhancer :play-arrow}
                "Play"]])}])
