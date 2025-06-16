(ns kushi.ui.lightswitch.core
  (:require
   [fireworks.core :refer [? !? ?> !?>]]
   [kushi.core :refer [sx merge-attrs]]
   [kushi.ui.label.core :refer [label]]
   [kushi.ui.icon.core :refer [icon]]
   [kushi.ui.icon.mui.svg :as mui.svg]
   [kushi.ui.core :refer [extract]]))

(defn ^:public lightswitch!
  "Expects a querySelector string and toggles a .dark class on that element.
   If no querySelector string provided, it will toggle .dark on the body element."
  ([]
   (lightswitch! nil))
  ([s]
   (.toggle (-> (if s
                  (js/document.querySelector s)
                  js/document.body)
                .-classList)
            "dark")))

(def light-mode-label-attrs
  (sx ["has-ancestor(.dark):display" :none]
      ["has-ancestor(.kushi-playground-mobile-nav):c" :white]))

(def dark-mode-label-attrs
  (sx :d--none
      ["has-ancestor(.dark):display" :block]
      :c--white
      ["has-ancestor(.kushi-playground-mobile-nav):c" :black]))

(defn light-dark-mode-switch [& args]
  (let [{:keys [attrs children]} (extract args)]
    [:button (merge-attrs 
              (sx :.minimal
                  :fs--$large
                  :.pointer
                  :pb--0.5rem!important)
              {:on-click #(lightswitch!)}
              attrs)
     [label light-mode-label-attrs
      ;; TODO put mui back in
      [icon :light-mode #_mui.svg/light-mode]]
     [label dark-mode-label-attrs
      ;; TODO put mui back in
      [icon :dark-mode #_mui.svg/dark-mode]]]))

(defn desktop-lightswitch []
  [:div (sx :.kushi-light-dark-switch-desktop
            :d--none
            ["md:has-ancestor(.hide-lightswitch):d" :none]
            :md:d--block
            :position--fixed
            :inset-inline--auto:0.75rem
            :inset-block--1rem:auto)
   [light-dark-mode-switch]])
