(ns kushi.ui.tag.core
  (:require [kushi.core :refer [merge-with-style] :refer-macros (sx cssfn defclass)]
            [kushi.ui.core :refer-macros (defcom)]))

(defcom tag
  [:div (sx :.relative)
   [:div
    (merge-with-style
     (sx
      'kushi-tag
      :line-height--1
      :>span:padding--0.2em:0.6em
      :>span:max-width--140px
      :>span:white-space--nowrap
      :>span:overflow--hidden
      :>span:text-overflow--ellipsis
      :bgc--#efefef
      {:data-kushi-ui :tag})
     &attrs)
    [:span (sx :d--block) &children]]])
