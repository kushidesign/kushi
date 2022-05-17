(ns kushi.ui.card.core
  (:require [kushi.core :refer-macros (sx cssfn defclass)]
            [kushi.ui.core :refer-macros (defcom)]))

(defcom card
  [:div (sx 'kushi-card:ui
            :.relative
            :w--100%
            :p--2rem
            :bgc--white)])
