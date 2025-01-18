(ns kushi.ui.card.core
  (:require [kushi.css.core :refer (css merge-attrs)]
            [kushi.ui.core :refer (opts+children)]))

(defn card
  {:summary ["Cards are typically visually contained sections of information. They are often part of a series of cards with related content."]}
  [& args]
  (let [[opts attrs & children] (opts+children args)
        {:keys []}              opts]
    (into
     [:div (merge-attrs
            {:class         (css ".kushi-card"
                                 :.neutralize
                                 :.bordered
                                 :.rounded
                                 :b--1px:solid:$neutral-200
                                 :dark:b--1px:solid:$neutral-200
                                 :position--relative
                                 :w--100%
                                 :p--1.25em)
             :data-kushi-ui :card}
            attrs)]
     children)))

