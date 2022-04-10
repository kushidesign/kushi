(ns kushi.ui.icon.mui.sharp
  (:require
   [kushi.core  :refer (inject-stylesheet)]
   [kushi.ui.icon.core  :refer (icon-base)]
   [kushi.gui   :refer (defcom)]))

(inject-stylesheet {:rel "preconnet"
                    :href "https://fonts.gstatic.com"
                    :cross-origin "anonymous"})
(inject-stylesheet {:rel "preconnet"
                    :href "https://fonts.googleapis.com"})
(inject-stylesheet {:rel "stylesheet"
                    :href "https://fonts.googleapis.com/css2?family=Material+Icons+Sharp"})

(defcom mui-icon-sharp
  (conj icon-base [:span.material-icons-sharp]))
