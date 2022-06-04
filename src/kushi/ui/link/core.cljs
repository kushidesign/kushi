(ns kushi.ui.link.core
  (:require [kushi.core :refer (merge-with-style) :refer-macros (sx)]
            [kushi.ui.core :refer-macros (defcom)]
            [kushi.ui.button.core :refer (label-base-sx)]))

(defcom link
  [:a
   (merge-with-style
     label-base-sx
     (kushi.core/sx
        'kushi-link
        :d--ib
        :td--underline
        :tup--under
        :>span:jc--fs
      {:data-kushi-ui :link}))
   [:span:!children]]
   nil
  #(if (string? %) [:span.kushi-label-text %] %))
