(ns kushi.ui.tag.core
  (:require [kushi.core :refer [merge-attrs] :refer-macros (sx)]
            [kushi.ui.core :refer (opts+children)]))

(defn tag
  {:summary ["A tag is typically used for concise information, often in a group with other tags."]}
  [& args]
  (let [[_ attrs & children] (opts+children args)]
    (into [:div
           (merge-attrs
            (sx
             'kushi-tag
             :.flex-row-c
             :.enhanceable
             :.info
             :.neutral
             :.transition
             :line-height--1.25
             :padding--0.4em:0.6em:0.4em
             :&.lowercase:padding--0.35em:0.6em:0.45em
             [:&.bordered:pb "calc(0.4em - var(--button-border-width))"]
             [:&.bordered:pi "calc(0.6em - var(--button-border-width))"]
             {:data-kushi-ui :tag})
            attrs)]
          children)))
