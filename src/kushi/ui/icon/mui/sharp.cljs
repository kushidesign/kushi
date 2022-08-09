(ns kushi.ui.icon.mui.sharp
  (:require
   [kushi.core  :refer (sx inject-stylesheet merge-attrs)]
   [kushi.ui.core   :refer (defcom)]))

(inject-stylesheet {:rel "preconnet"
                    :href "https://fonts.gstatic.com"
                    :cross-origin "anonymous"})
(inject-stylesheet {:rel "preconnet"
                    :href "https://fonts.googleapis.com"})
(inject-stylesheet {:rel "stylesheet"
                    :href "https://fonts.googleapis.com/css2?family=Material+Icons+Sharp"})


(defcom mui-icon-sharp
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
     :.material-icons-sharp
     {:data-kushi-ui :icon.mui.sharp
      :style         {:&.material-icons-sharp:fs "var(--mui-icon-relative-font-size, inherit)"}})
    &children]])
