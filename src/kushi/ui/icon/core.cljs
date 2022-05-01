(ns kushi.ui.icon.core
  (:require
   [kushi.core :refer (sx)]
   [kushi.ui.core :refer (defcom)]))

(def icon-base
  [:div:!attr
   (sx
    'kushi-icon:ui
    :.relative
    :.transition
    :.flex-row-c
    :ta--center
    :d--ib
    :ai--c)])

(defcom icon
  icon-base)
