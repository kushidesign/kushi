(ns kushi.ui.tag.core
  (:require [kushi.core :refer [merge-attrs] :refer-macros (sx)]
            [kushi.ui.core :refer-macros (defcom)]))
(defcom tag
  (let [{:keys [dismissable?]} &opts]
    [:div
     (merge-attrs
      (sx
       'kushi-tag
       :line-height--1
       :>span:padding--0.4em:0.6em:0.4em
       :&.lowercase:>span:padding--0.35em:0.6em:0.45em
       :>span:max-width--140px
       :>span:white-space--nowrap
       :>span:overflow--hidden
       :>span:text-overflow--ellipsis
       {:data-kushi-ui :tag})
      &attrs)
     [:span (sx :d--block) &children]]))

#_(defcom tag
  (let [{:keys [dismissable?]} &opts]
    [:div (sx :.relative)
     [:div
      (merge-attrs
       (sx
        'kushi-tag
        :line-height--1
        :>span:padding--0.4em:0.6em:0.4em
        :&.lowercase:>span:padding--0.35em:0.6em:0.45em
        :>span:max-width--140px
        :>span:white-space--nowrap
        :>span:overflow--hidden
        :>span:text-overflow--ellipsis
        :bgc--#efefef
        {:data-kushi-ui :tag})
       &attrs)
      [:span (sx :d--block) &children]]]))
