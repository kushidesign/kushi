(ns kushi.ui.grid.core
  (:require
   [kushi.core :refer (sx merge-with-style)]
   [kushi.ui.core :refer (opts+children)]
   [par.core :refer-macros [!? ?]]
   #_[playground.util :as util :refer-macros (keyed)] ))

(defn grid
  "Flexible grid layout with fixed-aspect ratio grid-items"
  [& args]
  (let [[opts attr & children] (opts+children args)
        {:keys [column-min-width aspect-ratio gap]
         :or {column-min-width :150px
              gap :20px
              aspect-ratio 1}} opts
        aspect-ratio-pct (str (* 100 (if (number? aspect-ratio) (js/Math.abs aspect-ratio) 1)) "%")
        ;; TODO this unwrapping is temp fix - move upstream into opts+children
        children-unwrapped (if (and (seq children) (= 1 (count children)) (coll? (first children)))
                            (first children)
                            children)]
    (into
     [:section
      (merge-with-style
       (sx 'kushi-grid
           {:style {:>*:w        :auto
                    :>*:h        0
                    :>*:pt       aspect-ratio-pct
                    :>*:position :relative
                    :d           :grid
                    :gtc         (str "repeat(auto-fit, minmax(" (name column-min-width) ", 1fr))")
                    :color       :red
                    :grid-gap    gap
                    :width       :100%}})
       attr)]
     children-unwrapped)))
