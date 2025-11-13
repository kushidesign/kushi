(ns kushi.ui.header
  (:require
   [fireworks.core :refer [? !? ?> !?>]]
   [kushi.core :refer (merge-attrs sx)]
   [kushi.ui.shared.theming :refer [component-attrs variant-basics]]
   [kushi.ui.core :refer (extract defui)]
   [clojure.string :as string]))

(defui header
  {:props/family [:container]
   :props/shared [[:display {:default :flex-row}]]}
  [& args]
  (into
   [:header
    (merge-attrs {:data-ks-ui "header"} &attrs)]
   &children))
