(ns ^{:kushi/layer "user-styles"} kushi.ui.button.demo
  (:require
   [fireworks.core :refer [? !? ?> !?>]]
   [kushi.ui.icon :refer [icon]]
   [kushi.ui.button :refer [button]]
   [kushi.ui.spinner :refer [spinner]]
   [kushi.showcase.core
    :as showcase
    :refer [samples samples-with-variant]]
   [clojure.walk :as walk]))


#_(? :pp (mapv (fn [[k {{:keys [samples require] :as demo} :demo :as m}]]
        (merge (dissoc m :default :demo)
               demo
               (when require {:require require})
               (when samples {:samples (walk/postwalk
                                        (fn [x] 
                                          (if (and (vector? x)
                                                   (-> x first (= 'icon))
                                                   (-> x second keyword?))
                                            (-> x second)
                                            x)
                                          )
                                        (mapv #(if (map? %) (:code %) %) samples))})
               (when-not samples {:variant k})))
      legacy))


(def demos
  [{:samples (samples-with-variant
              {:args           ["Next"],
               :desc           "Colorway of the button. Can also be a named color from Kushi's design system e.g `:red` `:purple` `:gold` etc.",
               :variant        :colorway,
               :variant-scale  :colorway/named,
               :x-variants     [:surface],
               :snippets-label "Colorways",
               :attrs/snippet  {:surface :solid},
              ;;  :attrs/display  {:sizing :small},
               :label          "Colorways ... surfaces × contours",
               :attrs          {:end-enhancer :east},
               :rows?          true})}

   {:label   "Contour variants"
    :desc    "Contour of the button."
    :samples (samples-with-variant
              {:variant       :contour
               :variant-scale :contour/basic
               :attrs         {:end-enhancer :east
                               :colorway     :accent
                               :surface      :solid}
               :args          ["Next"]})}
   
   {:desc    "Surface variant of the button.",
    :label   "Surface variants",
    :samples (samples-with-variant
              {:attrs   {:end-enhancer [icon :east]
                         :colorway     :accent},
               :args    ["Next"],
               :variant :surface})}
   
   {:desc    "General amount of padding inside the button",
    :label   "Packing variants",
    :samples (samples-with-variant
              {:attrs   {:end-enhancer :east
                         :colorway     :accent
                         :surface      :solid},
               :args    ["Next"],
               :variant :packing})}

   {:label   "Sizing"
    ;;  :label/modal "Colorways ..."
    :desc    "Sizes from xxxsmall to xxxlarge"
    ;; :row-style {:border "1px solid red"}
    :samples (samples-with-variant
              {:variant :sizing
              ;; :variant-labels? false
               :attrs   {:surface      :solid
                         :end-enhancer :east
                         :colorway     :accent}
               :args    ["Next"]})}

   {:schema  #{:inside :outside},
    :desc    "Alignment of the stroke. Only applies to `:surface` `:outline`",
    :label   "Stroke alignment",
    :require [[kushi.ui.icon :refer [icon]]],
    :samples (samples [[button
                        {:end-enhancer :east,
                         :sizing       :xlarge,
                         :colorway     :accent,
                         :surface      :outline,
                         :stroke-align :inside}
                        "Next"]
                       [button
                        {:end-enhancer ':east,
                         :sizing       :xlarge,
                         :colorway     :accent,
                         :surface      :outline,
                         :stroke-align :outside}
                        "Next"]])}

   {:desc    "Content at the inline-start position following the button text. Typically an icon.",
    :label   "Start-enhancer icons",
    :samples (samples [[button
                        {:start-enhancer :pets,
                         :colorway       :accent,
                         :surface        :solid}
                        "Pets"]
                       [button
                        {:start-enhancer :auto-awesome,
                         :colorway       :accent,
                         :surface        :soft}
                        "Wow"]
                       [button
                        {:start-enhancer :play-arrow,
                         :colorway       :accent,
                         :surface        :outline}
                        "Play"]])}
   
   {:desc    "Content at the inline-end position preceding the button text. Typically an icon.",
    :label   "End-enhancer icons",
    :require [[kushi.ui.icon :refer [icon]]],
    :samples (samples [[button
                        {:end-enhancer :pets,
                         :colorway     :accent,
                         :surface      :solid}
                        "Pets"]
                       [button
                        {:end-enhancer :auto-awesome,
                         :colorway     :accent,
                         :surface      :soft}
                        "Wow"]
                       [button
                        {:end-enhancer :play-arrow,
                         :colorway     :accent,
                         :surface      :outline}
                        "Play"]])}

   {:label   "Loading states",
    :desc    "When `true` this will set the appropriate values for `aria-busy` and `aria-label`",
    :schema  boolean?,
    :require '[[kushi.ui.button :refer [button]]
               [kushi.ui.icon :refer [icon]]
               [kushi.ui.spinner :refer [spinner]]],
    :samples (samples
              [[button
                {:loading?     true,
                 :colorway     :accent,
                 :surface      :solid,
                 :end-enhancer [spinner {:spinner-type :donut}]
                 }
                "Play"]
               [button
                {:loading?     true,
                 :colorway     :accent,
                 :surface      :solid,
                 :end-enhancer [spinner {:spinner-type :propeller}]}
                "Play"]
               [button
                {:loading?     true,
                 :colorway     :accent,
                 :surface      :solid,
                 :end-enhancer [spinner {:spinner-type :thinking}]}
                "Play"]
               [button
                {:loading? true
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
