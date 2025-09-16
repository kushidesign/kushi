(ns kushi.ui.span
  (:require
   [bling.core]
   [kushi.core :refer (merge-attrs)]
   [kushi.ui.core :refer (defui)]
   [kushi.ui.util :as util]))

(defui span 
  {:props/shared [:shape :stroke :stroke-align :shadow :colorway]}
  [& args]
  (into [:span 
         (merge-attrs
          (when (or (:shadow &props) (:stroke &props))
            (util/shadow-and-stroke-attrs &props))
          {:data-ks-surface :transparent}
          &attrs)]
        &children))
