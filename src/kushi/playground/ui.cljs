(ns kushi.playground.ui
  (:require
   [kushi.core :refer [sx]]
   [kushi.ui.label.core :refer [label]]
   [kushi.ui.icon.core :refer [icon]]
   [kushi.ui.core :refer [defcom lightswitch!]]))

(defcom light-dark-mode-switch
  [:button (sx :.minimal
               :.large
               :.pointer
               :pb--0.5rem!important
               {:on-click #(lightswitch!)})
   [label (sx ["has-ancestor(.dark):display" :none]
              ["has-ancestor(.kushi-playground-mobile-nav):c" :white])
    [icon :dark-mode]]
   [label (sx :d--none
              ["has-ancestor(.dark):display" :block]
              :c--white
              ["has-ancestor(.kushi-playground-mobile-nav):c" :black])
    [icon :light-mode]]])
