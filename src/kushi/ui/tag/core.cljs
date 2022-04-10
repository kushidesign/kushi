(ns kushi.ui.tag.core
  (:require [par.core :refer-macros [!? ?]]
            [kushi.core :refer-macros (sx cssfn defclass)]
            [kushi.gui :refer-macros (defcom)]))



(defcom tag
  [:div (sx :.relative)
   [:div:! (sx :line-height--1
               :>span:padding--0.2em:0.6em
               :>span:max-width--140px
               :>span:white-space--nowrap
               :>span:overflow--hidden
               :>span:text-overflow--ellipsis
               :bgc--#efefef
               {:prefix :kushi- :ident :tag})
    [:span (sx :d--block)]]])
