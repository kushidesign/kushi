(ns kushi.ui.icon.mui.core
  (:require
   [kushi.core :refer (sx inject-stylesheet)]
   [kushi.ui.icon.core  :refer (icon-base)]
   [kushi.gui   :refer (defcom)]))

(inject-stylesheet {:rel "preconnet"
                    :href "https://fonts.gstatic.com"
                    :cross-origin "anonymous"})
(inject-stylesheet {:rel "preconnet"
                    :href "https://fonts.googleapis.com"})
(inject-stylesheet {:rel "stylesheet"
                    :href "https://fonts.googleapis.com/css2?family=Material+Icons"})


(def mui-icon-span
  [:span
   (sx
    'kushi-mui-icon-font:ui
    :.transition
    :.material-icons)])

(defcom mui-icon
  (conj icon-base mui-icon-span))
