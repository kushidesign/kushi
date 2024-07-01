(ns kushi.playground.ui
  (:require
   [kushi.core :refer [sx merge-attrs]]
   [kushi.ui.label.core :refer [label]]
   [kushi.ui.icon.core :refer [icon]]
   [kushi.ui.icon.mui.svg :as mui.svg]
   [kushi.ui.core :refer [defcom lightswitch!]]))

(defcom light-dark-mode-switch
  [:button (merge-attrs 
            (sx :.minimal
                :.large
                :.pointer
                :pb--0.5rem!important
                {:on-click #(lightswitch!)})
            &attrs)
   [label (sx ["has-ancestor(.dark):display" :none]
              ["has-ancestor(.kushi-playground-mobile-nav):c" :white])
    [icon :light-mode #_mui.svg/light-mode]]
   [label (sx :d--none
              ["has-ancestor(.dark):display" :block]
              :c--white
              ["has-ancestor(.kushi-playground-mobile-nav):c" :black])
    [icon :dark-mode #_mui.svg/dark-mode]]])

(defn desktop-lightswitch []
  [:div (sx 'kushi-light-dark-switch-desktop
            :d--none
            ["md:has-ancestor(.hide-lightswitch):d" :none]
            :md:d--block
            :position--fixed
            :inset-inline--auto:0.75rem
            :inset-block--1rem:auto)
   [light-dark-mode-switch]])
