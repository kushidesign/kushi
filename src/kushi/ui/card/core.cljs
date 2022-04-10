(ns kushi.ui.card.core
  (:require [par.core :refer-macros [!? ?]]
            [kushi.core :refer-macros (sx cssfn defclass)]
            [kushi.gui :refer-macros (defcom)]))


(defclass card-base
  {:w :100%
   :p :2rem
   :bgc :white
   })


(defcom card
  [:div (sx :.relative :.card-base {:prefix :kushi- :ident :card})])
