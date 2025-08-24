(ns kushi.ui.span
  (:require
   [bling.core]
   [kushi.core :refer (merge-attrs)]
   [kushi.ui.core :refer (defui)]
   [kushi.ui.util :as util]))

(defui span 
  {:props/shared [:contour :stroke :stroke-align :drop-shadow :colorway]}
  [& args]
  (let [{:keys [drop-shadow stroke]} &props]
    (into [:span 
           (merge-attrs
            (when (or drop-shadow stroke)
              {:style {:box-shadow (util/box-shadow 
                                    {:shadows drop-shadow
                                     :strokes stroke})}})
            {:data-ks-surface :transparent}
            &attrs)]
          &children)))
