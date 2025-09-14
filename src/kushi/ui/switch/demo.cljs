(ns ^{:kushi/layer "user-styles"}
  kushi.ui.switch.demo
  (:require [kushi.core :refer (sx at)]
            [kushi.showcase.core
             :as showcase
             :refer [samples samples-with-variant]]
            [kushi.ui.switch :refer [switch]]))

(def demos
  #_[
   #_{:samples (samples [[:div (sx :.flex-row-start :gap--1rem)
                        [switch {:at              (at)
                                 :on?             true
                                 :sizing          :xxxlarge
                                 :shape         :rounded
                                 :track-inset-gap :1px}]
                        [switch {:at              (at)
                                 :on?             true
                                 :sizing          :xxxlarge
                                 :shape         :rounded-absolute
                                 :track-inset-gap :1px}]]])}
   {:samples (samples-with-variant
              {:variant       :shape,
               :variant-scale :shape/basic+rounded,
               :label         "Contour",
               :attrs         {:at              (at)
                               :on?             true
                               :sizing          :xxxlarge
                               :track-inset-gap :1px}})}
   ]
  [
   {:label   "Basic"
    :desc    "Basic"
    :samples (samples [[:div (sx :.flex-row-start :gap--1rem)
                        [switch {:colorway :neutral
                                 :sizing   :xxxlarge}]
                        [switch {:colorway :neutral
                                 :sizing   :xxxlarge
                                 :on?      true}]]])}


   {:samples (samples-with-variant
              {:variant         :colorway,
              ;;  :variant-labels? false
               :variant-scale   :colorway/semantic,
               :label           "Colorway",
               :row-style       {:justify-content :flex-start
                                 :gap             :1rem}
               :attrs           {:sizing :xxxlarge
                                 :on?    true}})}

   
   {:samples (samples-with-variant
              {:variant :sizing,
               :label   "Sizing",
               :attrs   {:on? true}})}

   {:samples (samples-with-variant
              {:variant       :shape,
               :variant-scale :shape/basic,
               :label         "Contour",
               :attrs         {:at              (at)
                               :on?             true
                               :sizing          :xxxlarge}})}

   ;; why rows not working?
   {:samples (samples-with-variant
              {:variant         :sizing
               :variant-labels? false
               :variant-scale   :sizing/large-xxxlarge,
               :row-style       {:justify-content :flex-start
                                 :gap             :1rem}
               :label           "Convex thumb",
               :attrs           {:on?         true
                                 :thumb-props {:surface :convex-light-mode}}})}

   {:samples (samples-with-variant
              {:variant         :sizing
               :variant-labels? false
               :variant-scale   :sizing/large-xxxlarge,
               :row-style       {:justify-content :flex-start
                                 :gap             :1rem}
               :label           "Oversized thumb",
               :attrs           {:on?                true
                                 :thumb-scale-factor 1.25
                                 :thumb-props        {:stroke  :medium}}})}

   {:samples (samples-with-variant
              {:variant         :sizing
               :variant-labels? false
               :variant-scale   :sizing/large-xxxlarge,
               :row-style       {:justify-content :flex-start
                                 :gap             :1rem}
               :label           "Labeled thumb",
               :attrs           {:thumb-label-on  "ON"
                                 :thumb-label-off "OFF"}})}
   
   {:samples (samples-with-variant
              {:variant         :sizing
               :variant-labels? false
               :variant-scale   :sizing/large-xxxlarge,
               :row-style       {:justify-content :flex-start
                                 :gap             :1rem}
               :label           "Labeled thumb, on",
               :attrs           {:on?             true
                                 :thumb-label-on  "ON"
                                 :thumb-label-off "OFF"}})}
   
   {:samples (samples-with-variant
              {:variant         :sizing
               :variant-labels? false
               :variant-scale   :sizing/large-xxxlarge,
               :row-style       {:justify-content :flex-start
                                 :gap             :1rem}
               :label           "Icon thumb",
               :attrs           {:thumb-icon-on  :visibility
                                 :thumb-icon-off :visibility-off}})}
   
   {:samples (samples-with-variant
              {:variant         :sizing
               :variant-labels? false
               :variant-scale   :sizing/large-xxxlarge,
               :row-style       {:justify-content :flex-start
                                 :gap             :1rem}
               :label           "Icon thumb, on",
               :attrs           {:on?            true
                                 :thumb-icon-on  :visibility
                                 :thumb-icon-off :visibility-off}})}
   
   {:label   "Disabled"
    :desc    "Disabled"
    :samples (samples [[:div (sx :.flex-row-start :gap--1rem)
                        [switch {:colorway :neutral
                                 :sizing   :xxxlarge
                                 :disabled true}]
                        [switch {:colorway :neutral
                                 :sizing   :xxxlarge
                                 :on?      true
                                 :disabled true}]]])}
   
   ])
