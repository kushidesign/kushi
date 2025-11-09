(ns kushi.ui.grid
  (:require
   [kushi.core :refer (css css-vars-map merge-attrs)]
   [kushi.ui.core :refer (defui)]
   [kushi.ui.util :refer [aspect-ratio->number]]))

(defui grid
  {:doc   "Elastic grid layout with fixed-aspect ratio grid-items"
   :props {:column-min-width {:schema  :keyword
                              :default :150px
                              :desc    "The minimum width of the columns. The width of the
                                        columns will expand evenly to fill out the parent container. 
                                        Value must be a keyword representing a valid CSS value
                                        for [`min-width`](https://developer.mozilla.org/en-US/docs/Web/CSS/min-width)."}
           :aspect-ratio     {:schema  :keyword
                              :default :1:1
                              :desc    "The aspect ratio of the grid cells. Value must be a
                                        keyword representing a formula of width to height. For
                                        example, a value of `:1:1` would result in square elements, 
                                        while a value of `:2:3` would result in elements with a
                                        portrait orientation."}
           :gap              {:schema  :keyword
                              :default :20px
                              :desc    "The gap between grid cells. Value must be a keyword
                                        representing a valid CSS value for
                                        [`grid-gap`](https://developer.mozilla.org/en-US/docs/Web/CSS/min-width)."} }}
  [& args]
  (let [{:keys [column-min-width aspect-ratio gap]
         :or   {column-min-width :150px
                gap              :20px
                aspect-ratio     :1:1}}            
        &props

        ar                                         
        (aspect-ratio->number aspect-ratio)

        aspect-ratio-pct                           
        (str (* 100 (if (number? ar) (js/Math.abs ar) 1)) "%")
        
        gtc
        (str
         "repeat(auto-fit, minmax("
         (name
          column-min-width)
         ", 1fr))")]
    (into
     [:section
      (merge-attrs
       {:style         (css-vars-map aspect-ratio-pct gtc gap)
        :class         (css
                        ".ks-grid"
                        {:>*:w        :auto
                         :>*:h        0
                         :>*:pbs      :$aspect-ratio-pct
                         :>*:position :relative
                         :d           :grid
                         :gtc         :$gtc
                         :grid-gap    :$gap
                         :width       :100%})}
       &attrs)]
     &children)))
