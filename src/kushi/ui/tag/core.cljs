(ns kushi.ui.tag.core
  (:require [kushi.core :refer-macros (sx cssfn defclass)]
            [kushi.ui.core :refer-macros (defcom)]))

(defcom tag
  [:div (sx :.relative)
   [:div:!attr
    (sx
     'kushi-tag:ui
     :line-height--1
     :>span:padding--0.2em:0.6em
     :>span:max-width--140px
     :>span:white-space--nowrap
     :>span:overflow--hidden
     :>span:text-overflow--ellipsis
     :bgc--#efefef
     {:data-kushi-ui :tag})
    [:span:!children (sx :d--block)]]])
