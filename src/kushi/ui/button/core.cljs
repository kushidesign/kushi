(ns kushi.ui.button.core
  (:require-macros
   [kushi.core :refer (sx cssfn defclass)]
   [kushi.ui.core :refer (defcom defcom+)])
  (:require
   [kushi.core :refer (merged-attrs-map merge-with-style)]
   [kushi.ui.core :refer (gui)]
   [kushi.ui.scaling :refer [icon-margin icon-font-size]]
   [kushi.ui.tooltip.core :refer (tooltip-mouse-leave tooltip-mouse-enter)]))


(def label-base-sx
 (kushi.core/sx
    'kushi-label-base:ui
    ;; :.normal
    {:data-kushi-tooltip true
     :aria-expanded "false"
     :on-mouse-enter tooltip-mouse-enter
     :on-mouse-leave tooltip-mouse-leave
     :style {;; Remove this position relative?
             :position                   :relative
             :min-width                  :1rem
             :h                          :fit-content
             :w                          :fit-content
             ;;  :fs                         "var(--text-medium)"
             ;;  :&_.kushi-icon:fs           :1.1875em #_"var(--mui-icon-medium-font-size)"
             :m                          0
             :p                          0
             :line-height                :normal
             :>span:d                    :flex
             :>span:jc                   :center
             :>span:ai                   :center
             :&_.kushi-label-text+.kushi-icon:mis "var(--mui-icon-margin-inline-ems)"
             :&_.kushi-icon+.kushi-label-text:mis "var(--mui-icon-margin-inline-ems)"
             :bgi                        :none}}))

(defcom+ button
  [:button
   (merge-with-style
    label-base-sx
    (sx
     'kushi-button
     :.transition
     {:style {:>span:padding [[:0.8em :1.2em]]
              :cursor        :pointer}})
    &attrs)
   [:span &children]]
  ;; fn for processing children
  #(if (string? %) [:span.kushi-label-text %] %))
