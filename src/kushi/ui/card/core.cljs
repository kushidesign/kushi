(ns kushi.ui.card.core
  (:require [par.core :refer-macros [!? ?]]
            [kushi.core :refer-macros (sx cssfn defclass)]
            [kushi.gui :refer-macros (defcom)]))

(defcom card
  [:div (sx 'kushi-card:ui
            :.relative
            :w--100%
            :p--2rem
            :bgc--white)])
