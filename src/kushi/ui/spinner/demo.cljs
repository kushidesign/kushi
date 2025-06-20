(ns ^{:kushi/layer "user-styles"} kushi.ui.spinner.demo
  (:require 
   [kushi.showcase.core
    :as showcase
    :refer [samples-with-variant]]
   [kushi.ui.spinner :refer [spinner]]))



(def demos
  [{:variant       :spinner-type
    :label         "Spinner type"
    :desc          "The variety of spinner"
    :row-style     {:gap :2rem}
    :samples       (samples-with-variant
                    {:variant       :spinner-type
                     :label         "Spinner type"
                     :desc          "The variety of spinner"
                     :attrs/display {:sizing :xlarge}
                     })}

   {:label     "Propeller, sizes"
    :desc      "Corresponds to the font-size based on Kushi's font-size scale."
    :samples   (samples-with-variant 
                {:variant         :sizing
                 :attrs           {:spinner-type :propeller}
                 :variant-labels? false})}

   {:desc      "Corresponds to the font-size based on Kushi's font-size scale."
    :label     "Donut, sizes"
    :samples   (samples-with-variant 
                {:variant         :sizing
                 :label           "Donut, sizes"
                 :attrs           {:spinner-type :donut}
                 :variant-labels? false})}

   {:desc    "Colorway of the spinner. Can also be a named color from Kushi's design system, e.g `:red`, `:purple`, `:gold`, etc."
    :label   "Propeller, colorways"
    :samples (samples-with-variant
              {:variant         :colorway
               :variant-scale   :colorway/named
               :attrs           {:spinner-type :propeller}
               :attrs/display   {:sizing :xlarge}
               :variant-labels? false})}

   {:desc    "Colorway of the spinner. Can also be a named color from Kushi's design system, e.g `:red`, `:purple`, `:gold`, etc."
    :label   "Donut, colorways"
    :samples (samples-with-variant
              {:variant         :colorway
               :variant-scale   :colorway/named
               :attrs           {:spinner-type :donut}
               :attrs/display   {:sizing :xlarge}
               :variant-labels? false})}
   
   {:desc    "Colorway of the spinner. Can also be a named color from Kushi's design system, e.g `:red`, `:purple`, `:gold`, etc."
    :label   "Thinking, colorways"
    :samples (samples-with-variant
              {:variant         :colorway
               :variant-scale   :colorway/named
               :attrs           {:spinner-type :thinking}
               :attrs/display   {:sizing :xxsmall}
               :variant-labels? false})}])
