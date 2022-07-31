(ns kushi.ui.icon.mui.core
  (:require
   [kushi.core :refer (merge-attrs sx inject-stylesheet)]
   [kushi.ui.core   :refer (defcom)]))

(inject-stylesheet {:rel "preconnet"
                    :href "https://fonts.gstatic.com"
                    :cross-origin "anonymous"})
(inject-stylesheet {:rel "preconnet"
                    :href "https://fonts.googleapis.com"})
(inject-stylesheet {:rel "stylesheet"
                    :href "https://fonts.googleapis.com/css2?family=Material+Icons"})


(defcom mui-icon
  [:div
   (merge-attrs
    (sx
     'kushi-icon
     :.relative
     :.transition
     :.flex-row-c
     :ta--center
     :d--ib
     :ai--c
     {:data-kushi-ui :icon})
    &attrs)
   [:span
    (sx
     'kushi-mui-icon
     :.transition
     :.material-icons
     {:data-kushi-ui :icon.mui
      :style         {:&.material-icons:fs "var(--mui-icon-relative-font-size)"}})
    &children]])
