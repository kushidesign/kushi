(ns kushi.ui.card
  (:require [kushi.core :refer (sx merge-attrs)]
            [kushi.ui.decoration :as decoration]
            [kushi.ui.core :refer (defui)]))

(defui card
  {:doc "Cards are typically visually contained sections of information.
         They are often part of a series of cards with related content."
   :props/family [:container]
   :props/shared  [[:surface {:default :transparent}]
                   [:shape {:default :rounded}]
                   [:stroke {:default :xsoft}]]}
  [& args]
  (let [{:keys [stroke-width shadow-opacity]} &props]
    (into
     [:div (merge-attrs
            (sx ".ks-card"
                [:--stroke-width :$card-stroke-width]
                :position--relative 
                :w--fit-content
                :p--1.25em)

            (some->> shadow-opacity
                     (hash-map "--shadow-opacity")
                     (hash-map :style))

            (some-> stroke-width
                    (decoration/stroke-width-cssvar "card"))

            &attrs)]
     &children)))

