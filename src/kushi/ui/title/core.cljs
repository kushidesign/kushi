(ns kushi.ui.title.core
  (:require [kushi.core :refer (merge-with-style) :refer-macros (sx cssfn defclass)]
            [kushi.ui.core :refer-macros (defcom)]
            [kushi.ui.button.core :refer (label-base-sx)]))

(defcom title
  [:span
   (merge-with-style
     label-base-sx
     (kushi.core/sx
        'kushi-title:ui
        :d--ib
        :>span:jc--fs))
   [:span:!children]]
   nil
  #(if (string? %) [:span.kushi-label-text %] %))
