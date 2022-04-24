(ns kushi.ui.icon.mui.outlined
  (:require
   [kushi.core  :refer (inject-stylesheet)]
   [kushi.ui.icon.core  :refer (icon-base)]
   [kushi.ui.core   :refer (defcom)]))

(inject-stylesheet {:rel "preconnet"
                    :href "https://fonts.gstatic.com"
                    :cross-origin "anonymous"})
(inject-stylesheet {:rel "preconnet"
                    :href "https://fonts.googleapis.com"})
(inject-stylesheet {:rel "stylesheet"
                    :href "https://fonts.googleapis.com/css2?family=Material+Icons+Outlined"})

(defcom mui-icon-outlined
  (conj icon-base [:span.material-icons-outlined]))
