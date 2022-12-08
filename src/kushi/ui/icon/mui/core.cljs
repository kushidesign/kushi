(ns kushi.ui.icon.mui.core
  (:require
   [clojure.string :as string]
   [kushi.core :refer (merge-attrs sx inject-stylesheet defclass)]
   [kushi.ui.core :refer (defcom)]))

(inject-stylesheet {:rel "preconnet"
                    :href "https://fonts.gstatic.com"
                    :cross-origin "anonymous"})
(inject-stylesheet {:rel "preconnet"
                    :href "https://fonts.googleapis.com"})
(inject-stylesheet {:rel "stylesheet"
                    :href "https://fonts.googleapis.com/css2?family=Material+Icons&family=Material+Icons+Outlined&family=Material+Icons+Sharp&family=Material+Icons+Round&family=Material+Icons+Two+Tone"})

(defn kw->string [coll]
  (mapv #(cond
           (= % :<>)
           :<>
           (keyword? %)
           (-> % name (string/replace #"-" "_"))
           :else %)
        coll))

(defclass kushi-mui-icon
  :.relative
  :d--inline-flex
  :flex-direction--row
  :jc--c
  :ta--center
  :ai--c
  [:>span:fs "var(--mui-icon-relative-font-size, inherit)"])

(defcom mui-icon-outlined
  [:div
   (merge-attrs
    (sx
     'kushi-mui-icon
     {:data-kushi-ui :mui-icon-outlined})
    &attrs)
   [:span.material-icons-outlined (kw->string &children)]])

(defcom mui-icon-round
  [:div
   (merge-attrs
    (sx
     'kushi-mui-icon
     {:data-kushi-ui :mui-icon-round})
    &attrs)
   [:span.material-icons-round (kw->string &children)]])

(defcom mui-icon-sharp
  [:div
   (merge-attrs
    (sx
     'kushi-mui-icon
     {:data-kushi-ui :mui-icon-sharp})
    &attrs)
   [:span.material-icons-sharp (kw->string &children)]])

(defcom mui-icon-two-tone
  [:div
   (merge-attrs
    (sx
     'kushi-mui-icon
     {:data-kushi-ui :mui-icon-two-tone})
    &attrs)
   [:span.material-icons-two-tone (kw->string &children)]])

(defcom mui-icon
  [:div
   (merge-attrs
    (sx
     'kushi-mui-icon
     {:data-kushi-ui :mui-icon})
    &attrs)
   [:span.material-icons (kw->string &children)]])
