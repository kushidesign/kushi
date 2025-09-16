;; (ns kushi.ui.lightswitch
;;   (:require
;;    [fireworks.core :refer [? !? ?> !?>]]
;;    [kushi.core :refer [sx merge-attrs]]
;;    [kushi.ui.label :refer [label]]
;;    [kushi.ui.icon :refer [icon]]
;;    [kushi.ui.icon.mui.svg :as mui.svg]
;;    [kushi.ui.core :refer [extract]]))

;; (def light-mode-label-attrs
;;   (sx ["has-ancestor(.dark):display" :none]
;;       ["has-ancestor(.kushi-playground-mobile-nav):c" :white]))

;; (def dark-mode-label-attrs
;;   (sx :d--none
;;       ["has-ancestor(.dark):display" :block]
;;       :c--white
;;       ["has-ancestor(.kushi-playground-mobile-nav):c" :black]))

;; (defn light-dark-mode-switch [& args]
;;   (let [{:keys [attrs children]} (extract args)]
;;     [:button (merge-attrs 
;;               (sx :.minimal
;;                   :fs--$size-large
;;                   :.pointer
;;                   :pb--0.5rem!important)
;;               {:on-click #(lightswitch!)}
;;               attrs)
;;      [label light-mode-label-attrs
;;       ;; TODO put mui back in
;;       [icon :light-mode #_mui.svg/light-mode]]
;;      [label dark-mode-label-attrs
;;       ;; TODO put mui back in
;;       [icon :dark-mode #_mui.svg/dark-mode]]]))

;; (defn desktop-lightswitch []
;;   [:div (sx :.kushi-light-dark-switch-desktop
;;             :d--none
;;             ["md:has-ancestor(.hide-lightswitch):d" :none]
;;             :md:d--block
;;             :position--fixed
;;             :inset-inline--auto:0.75rem
;;             :inset-block--1rem:auto)
;;    [light-dark-mode-switch]])


(ns kushi.ui.lightswitch
  (:require
   [bling.core]
   [kushi.core :refer (sx merge-attrs)]
   [kushi.ui.core :refer (defui)]
   [kushi.ui.icon :refer [icon]]
   [kushi.ui.icon-button :refer [icon-button]]
   [kushi.ui.util :as util])
   ;; (:require-macros [kushi.ui.button])
  )

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

(defui lightswitch
 {:doc          "Icon button that toggles a `dark` class on the body element"
  :props/shared [:size
                 :colorway
                 :packing
                 :loading
                 :stroke
                 :stroke-align
                 :stroke-width
                 :position
                 :shape
                 :surface
                 :transition]}
 [& args]
 [:div (sx {:w                               :fit-content
            :_.ks-lightswitch-icon-dark-mode {:display :none
                                              :color   :white}
            "has-ancestor(.dark)"              {:_.ks-lightswitch-icon-light-mode:display :none
                                                :_.ks-lightswitch-icon-dark-mode:display  :block}}
           )
  [icon-button 
   (merge-attrs (first args) 
                {:class    :ks-lightswitch-icon-light-mode
                 :on-click #(lightswitch!)})
   :light-mode]
  [icon-button
   (merge-attrs (first args) 
                {:class    :ks-lightswitch-icon-dark-mode
                 :on-click #(lightswitch!)})
   :dark-mode]]
 

 #_[:button (merge-attrs 
              (sx :.minimal
                  :fs--$size-large
                  :.pointer
                  :pb--0.5rem!important)
              {:on-click #(lightswitch!)}
              attrs)
     [label (sx ["has-ancestor(.dark):display" :none])
      ;; TODO put mui back in
      [icon :light-mode #_mui.svg/light-mode]]
     [label (sx :d--none
                ["has-ancestor(.dark):display" :block]
                :c--white
                ["has-ancestor(.kushi-playground-mobile-nav):c" :black])
      ;; TODO put mui back in
      [icon :dark-mode #_mui.svg/dark-mode]]])
