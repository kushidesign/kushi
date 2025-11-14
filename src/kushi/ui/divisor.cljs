(ns kushi.ui.divisor
  (:require [kushi.core :refer [sx merge-attrs]]
            [kushi.ui.core :refer (extract)]
            [kushi.ui.util :as util]))

(defn divisor
  "Desc for divisor"
  [& args]
  (let [{:keys [opts attrs children]} (extract args)
        {:keys [orientation]}  opts]
    [:div
     (merge-attrs
      (sx ".ks-divisor"
          :.transition
          :bgc--$divisor-color
          :dark:bgc--$divisor-color-dark-mode)
      (if (= "vertical" (util/as-str orientation)) 
        (sx ".ks-divisor-vertical" :w--$divisor-thickness)
        (sx ".ks-divisor-horizontal" :h--$divisor-thickness))
      attrs)]))
