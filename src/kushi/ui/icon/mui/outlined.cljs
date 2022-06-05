(ns kushi.ui.icon.mui.outlined
  (:require
   [kushi.core :refer (sx inject-stylesheet)]
   [kushi.ui.icon.core  :refer (icon-base)]
   [kushi.ui.core   :refer (defcom)]))

(inject-stylesheet {:rel "preconnet"
                    :href "https://fonts.gstatic.com"
                    :cross-origin "anonymous"})
(inject-stylesheet {:rel "preconnet"
                    :href "https://fonts.googleapis.com"})
(inject-stylesheet {:rel "stylesheet"
                    :href "https://fonts.googleapis.com/css2?family=Material+Icons+Outlined"})
(def mui-icon-span
  [:span:!children
   (sx
    'kushi-mui-icon
    :.transition
    :.material-icons-outlined
    {:data-kushi-ui :icon.mui.outlined
     :style {:&.material-icons:fs "var(--mui-icon-relative-font-size)"}})])

(defcom mui-icon-outlined
  (conj icon-base mui-icon-span))
