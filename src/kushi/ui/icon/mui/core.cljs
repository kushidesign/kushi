(ns kushi.ui.icon.mui.core
  (:require
   [kushi.core :refer (merge-attrs sx inject-stylesheet defclass)]
   [kushi.ui.core   :refer (defcom)]))

(inject-stylesheet {:rel "preconnet"
                    :href "https://fonts.gstatic.com"
                    :cross-origin "anonymous"})
(inject-stylesheet {:rel "preconnet"
                    :href "https://fonts.googleapis.com"})
(inject-stylesheet {:rel "stylesheet"
                    :href "https://fonts.googleapis.com/css2?family=Material+Icons&family=Material+Icons+Outlined&family=Material+Icons+Sharp&family=Material+Icons+Round&family=Material+Icons+Two+Tone"})


(defclass kushi-icon-wrapper
  :.relative
  :.transition
  :d--inline-flex
  :flex-direction--row
  :jc--c
  :ta--center
  :ai--c)

(def mui-icon-fs
  "var(--mui-icon-relative-font-size, inherit)")

(defcom mui-icon-outlined
  [:div
   (merge-attrs
    (sx
     'kushi-icon
     :.kushi-icon-wrapper
     {:data-kushi-ui :mui-icon-outlined})
    &attrs)
   [:span
    (sx
     'kushi-mui-icon
     :.transition
     :.material-icons-outlined
     :&.material-icons-outlined:fs--$mui-icon-fs)
    &children]])

(defcom mui-icon-round
  [:div
   (merge-attrs
    (sx
     'kushi-icon
     :.kushi-icon-wrapper
     {:data-kushi-ui :mui-icon-round})
    &attrs)
   [:span
    (sx
     'kushi-mui-icon
     :.transition
     :.material-icons-round
     :&.material-icons-round:fs--$mui-icon-fs)
    &children]])

(defcom mui-icon-sharp
  [:div
   (merge-attrs
    (sx
     'kushi-icon
     :.kushi-icon-wrapper
     {:data-kushi-ui :mui-icon-sharp})
    &attrs)
   [:span
    (sx
     'kushi-mui-icon
     :.transition
     :.material-icons-sharp
     :&.material-icons-sharp:fs--$mui-icon-fs)
    &children]])

(defcom mui-icon-two-tone
  [:div
   (merge-attrs
    (sx
     'kushi-icon
     :.kushi-icon-wrapper
     {:data-kushi-ui :mui-icon-two-tone})
    &attrs)
   [:span
    (sx
     'kushi-mui-icon
     :.transition
     :.material-icons-two-tone
     :&.material-icons-two-tone:fs--$mui-icon-fs)
    &children]])

(defcom mui-icon
  [:div
   (merge-attrs
    (sx
     'kushi-icon
     :.kushi-icon-wrapper
     {:data-kushi-ui :mui-icon})
    &attrs)
   [:span
    (sx
     'kushi-mui-icon
     :.transition
     :.material-icons
     :&.material-icons:fs--$mui-icon-fs)
    &children]])
