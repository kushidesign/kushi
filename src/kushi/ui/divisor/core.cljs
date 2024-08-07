(ns kushi.ui.divisor.core
  (:require [kushi.core :refer (merge-attrs) :refer-macros (sx)]
            [kushi.ui.core :refer (opts+children)]
            [kushi.ui.util :as util]))

(defn divisor
  "Desc for divisor"
  [& args]
  (let [[opts attrs] (opts+children args)
        {:keys [orientation]}  opts]
    [:div
     (merge-attrs
      {:data-kushi-ui :divisor}
      (sx 'kushi-divisor
          :.transition
          :bgc--$divisor-color
          :dark:bgc--$divisor-color-inverse)
      (if (= "vertical" (util/as-str orientation)) 
        (sx 'kushi-divisor-vertical :w--$divisor-thickness)
        (sx 'kushi-divisor-horizontal :h--$divisor-thickness))
      attrs)]))
