(ns kushi.playground.ui
  (:require
   [kushi.core :refer [sx merge-attrs]]
   [kushi.ui.label.core :refer [label]]
   [kushi.ui.icon.core :refer [icon]]
   [kushi.ui.icon.mui.svg :as mui.svg]
   [kushi.ui.core :refer [defcom lightswitch!]]))

(def light-mode-label-attrs
  (sx ["has-ancestor(.dark):display" :none]
      ["has-ancestor(.kushi-playground-mobile-nav):c" :white]))

(def dark-mode-label-attrs
  (sx :d--none
      ["has-ancestor(.dark):display" :block]
      :c--white
      ["has-ancestor(.kushi-playground-mobile-nav):c" :black]))

(def button-attrs
  (sx :.guh
      :.minimal
      :fs--$large
      :.pointer
      :pb--0.5rem!important))

(defcom light-dark-mode-switch
  [:button (merge-attrs 
            button-attrs
            {:on-click #(lightswitch!)}
            &attrs)
   [label light-mode-label-attrs
    ;; TODO put mui back in
    [icon :light-mode #_mui.svg/light-mode]]
   [label dark-mode-label-attrs
    ;; TODO put mui back in
    [icon :dark-mode #_mui.svg/dark-mode]]])

(defn desktop-lightswitch []
  [:div (sx :.kushi-light-dark-switch-desktop
            :d--none
            ["md:has-ancestor(.hide-lightswitch):d" :none]
            :md:d--block
            :position--fixed
            :inset-inline--auto:0.75rem
            :inset-block--1rem:auto)
   [light-dark-mode-switch]])
