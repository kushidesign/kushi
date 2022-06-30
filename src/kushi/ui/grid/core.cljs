(ns kushi.ui.grid.core
  (:require
   [par.core :refer-macros [!? ?]]
   [kushi.core :refer (sx merge-with-style)]
   [kushi.ui.util :refer [aspect-ratio->number]]
   [kushi.ui.core :refer (opts+children)] ))

(defn grid
  {:desc ["Flexible grid layout with fixed-aspect ratio grid-items"]
   :opts '[{:name    column-min-width
            :type    :keyword
            :default :150px
            :desc    ["The minimum width of the columns."
                      "The width of the columns will expand evenly to fill out the parent container."
                      "Value must be a string or keyword representing a"
                      "[valid CSS value for `min-width`](https://developer.mozilla.org/en-US/docs/Web/CSS/min-width)"]}
           {:name    aspect-ratio
            :type    :keyword
            :default :1:1
            :desc    ["The aspect ratio of the grid cells."
                      "Value must be a string or keyword representing a formula of width to height."
                      "For example, a value of `:1:1` would result in square elements,"
                      "while a value of `:2:3` would result in elements with a portrait orientation."]}
           {
            :name    gap
            :type    :keyword
            :default "20px"
            :desc    ["The aspect ratio of the grid cells."
                      "Value must be a string or keyword representing a"
                      "[valid CSS value for `grid-gap`](https://developer.mozilla.org/en-US/docs/Web/CSS/min-width)"]
            }]}
  [& args]
  (let [[opts attr & children]                     (opts+children args)
        {:keys [column-min-width aspect-ratio gap]
         :or   {column-min-width :150px
                gap              :20px
                aspect-ratio     :1:1}}            opts
        ar                                         (aspect-ratio->number aspect-ratio)
        aspect-ratio-pct                           (str (* 100 (if (number? ar) (js/Math.abs ar) 1)) "%")]
    (into
     [:section
      (merge-with-style
       (sx 'kushi-grid
           {:data-kushi-ui :grid
            :style         {:>*:w        :auto
                            :>*:h        0
                            :>*:pbs      aspect-ratio-pct
                            :>*:position :relative
                            :d           :grid
                            :gtc         (str "repeat(auto-fit, minmax(" (name column-min-width) ", 1fr))")
                            :grid-gap    gap
                            :width       :100%}})
       attr)]
     children)))
