(ns kushi.ui.card
  (:require [kushi.core :refer (sx merge-attrs)]
            [kushi.ui.core :refer (defui)]))

(defui card
  {:doc "Cards are typically visually contained sections of information.
         They are often part of a series of cards with related content."
   :props/family [:container]
   :props/shared  [[:surface {:default :outline}]
                   [:contour {:default :rounded}]]}
  [& args]
  (into
   [:div (merge-attrs
          (sx "[data-ks-ui=\"card\"]"
              :position--relative
              :w--100%
              :p--1.25em)
          &attrs)]
   &children))

