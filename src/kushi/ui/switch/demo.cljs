(ns ^{:kushi/layer "user-styles"}
  kushi.ui.switch.demo
  (:require [kushi.core :refer (sx css at)]
            [kushi.showcase.core
             :as showcase
             :refer [samples samples-with-variant]]
            [kushi.ui.flex :refer [flex-row flex-col]]
            [kushi.ui.switch :refer [switch]]))

(def demos
  #_[
   #_{:samples (samples [[:div (sx :display--flex :gap--1rem)
                        [switch {:at              (at)
                                 :on?             true
                                 :text-size          :xxxlarge
                                 :shape         :rounded
                                 :track-inset-gap :1px}]
                        [switch {:at              (at)
                                 :on?             true
                                 :text-size          :xxxlarge
                                 :shape         :rounded-absolute
                                 :track-inset-gap :1px}]]])}
   {:samples (samples-with-variant
              {:variant       :shape,
               :variant-scale :shape/basic+rounded,
               :label         "Contour",
               :attrs         {:at              (at)
                               :on?             true
                               :text-size          :xxxlarge
                               :track-inset-gap :1px}})}
   ]
  [
   {:label   "Basic"
    :desc    "Basic"
    :samples (samples [[flex-row (sx :gap--1rem)
                        [switch {:colorway :neutral
                                 :text-size   :xxxlarge}]
                        [switch {:colorway :neutral
                                 :text-size   :xxxlarge
                                 :on?      true}]]])}


   {:samples (samples-with-variant
              {:variant         :colorway,
              ;;  :variant-labels? false
               :variant-scale   :colorway/semantic,
               :label           "Colorway",
               :row-style       {:justify-content :flex-start
                                 :gap             :1rem}
               :attrs           {:text-size :xxxlarge
                                 :on?    true}})}

   
   {:samples (samples-with-variant
              {:variant :text-size,
               :label   "size",
               :attrs   {:on? true}})}

   {:samples (samples-with-variant
              {:variant       :shape,
               :variant-scale :shape/basic,
               :label         "Contour",
               :attrs         {:at              (at)
                               :on?             true
                               :text-size          :xxxlarge}})}

   ;; why rows not working?
   {:samples (samples-with-variant
              {:variant         :text-size
               :variant-labels? false
               :variant-scale   :text-size/large-xxxlarge,
               :row-style       {:justify-content :flex-start
                                 :gap             :1rem}
               :label           "Convex thumb",
               :attrs           {:on?         true
                                 :thumb-props {:surface :convex-light-mode
                                               :class (css {:--convex-shadow-strength      :40%
                                                            :dark:--convex-shadow-strength :70%})}}})}

   {:samples (samples-with-variant
              {:variant         :text-size
               :variant-labels? false
               :variant-scale   :text-size/large-xxxlarge,
               :row-style       {:justify-content :flex-start
                                 :gap             :1rem}
               :label           "Oversized thumb",
               :attrs           {:on?                true
                                 :thumb-scale-factor 1.25
                                 :thumb-props        {:stroke  :medium}}})}

   {:samples (samples-with-variant
              {:variant         :text-size
               :variant-labels? false
               :variant-scale   :text-size/large-xxxlarge,
               :row-style       {:justify-content :flex-start
                                 :gap             :1rem}
               :label           "Labeled thumb",
               :attrs           {:thumb-label-on  "ON"
                                 :thumb-label-off "OFF"}})}
   
   {:samples (samples-with-variant
              {:variant         :text-size
               :variant-labels? false
               :variant-scale   :text-size/large-xxxlarge,
               :row-style       {:justify-content :flex-start
                                 :gap             :1rem}
               :label           "Labeled thumb, on",
               :attrs           {:on?             true
                                 :thumb-label-on  "ON"
                                 :thumb-label-off "OFF"}})}
   
   {:samples (samples-with-variant
              {:variant         :text-size
               :variant-labels? false
               :variant-scale   :text-size/large-xxxlarge,
               :row-style       {:justify-content :flex-start
                                 :gap             :1rem}
               :label           "Icon thumb",
               :attrs           {:thumb-icon-on  :visibility
                                 :thumb-icon-off :visibility-off}})}
   
   {:samples (samples-with-variant
              {:variant         :text-size
               :variant-labels? false
               :variant-scale   :text-size/large-xxxlarge,
               :row-style       {:justify-content :flex-start
                                 :gap             :1rem}
               :label           "Icon thumb, on",
               :attrs           {:on?            true
                                 :thumb-icon-on  :visibility
                                 :thumb-icon-off :visibility-off}})}
   
   {:label   "Disabled"
    :desc    "Disabled"
    :samples (samples [[flex-row (sx :gap--1rem) 
                        [switch {:colorway :neutral
                                 :text-size   :xxxlarge
                                 :disabled true}]
                        [switch {:colorway :neutral
                                 :text-size   :xxxlarge
                                 :on?      true
                                 :disabled true}]]])}
   
   ])
