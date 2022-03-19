(ns kushi.ui.collapse.footer
  (:require-macros [kushi.core :refer (sx defclass)])
  (:require [kushi.ui.core]))

(defn collapse-footer-contents
  [{:keys [label-text label-text-expanded]}]
  [:<>
   [:div
    ; horizontal divisor
    (sx :.absolute
        :top--50%
        :left--0
        :right--0
        :bottom--100%
        :bt--1px:solid:black)]
   [:div
    (sx :.relative
        :b--1px:solid:black
        :bgc--white
        :z--1
        :>span:padding--10px:20px
        :>span:ta--c)
    [:span (sx :.absolute-fill) label-text]
    [:span (sx :.absolute-fill) label-text-expanded]
    [:span label-text]]])

(defclass collapse-footer-contents
  :.relative
  :order--2
  :jc--c
  {
   ">div>span:display" "none"
   ">div>span:nth-child(3):display" "block"
   ">div>span:nth-child(3):visibility" "hidden"
   "&[aria-expanded='true']:>div>span:nth-child(2):display" "block"
   "&[aria-expanded='false']:>div>span:nth-child(1):display" "block"
   :>.kui-icon:transition-property :transform
   :>.kui-icon:transition-duration :500ms})
