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
      {:data-ks-ui :divisor}
      (sx ".kushi-divisor"
          :.transition
          :bgc--$divisor-color
          :dark:bgc--$divisor-color-dark-mode)
      (if (= "vertical" (util/as-str orientation)) 
        (sx ".kushi-divisor-vertical" :w--$divisor-thickness)
        (sx ".kushi-divisor-horizontal" :h--$divisor-thickness))
      attrs)]))
