(ns kushi.ui.card.core
  (:require [kushi.core :refer-macros (sx)]
            [kushi.ui.core :refer-macros (defcom)]))

(defcom card
  [:div (sx 'kushi-card
            :.relative
            :w--100%
            :p--2rem
            :bgc--white
            {:data-kushi-ui :card})])
