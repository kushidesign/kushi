(ns kushi.ui.card.core
  (:require [kushi.core :refer (sx merge-attrs)]
            [kushi.ui.core :refer (opts+children)]))

(defn card
  {:desc ["Cards are typically visually contained sections of information. They are often part of a series of cards with related content."]}
  [& args]
  (let [[opts attrs & children] (opts+children args)
        {:keys []}              opts]
    (into
     [:div (merge-attrs
            (sx 'kushi-card
                :.neutralize
                :.relative
                :w--100%
                :p--1.25em
                {:data-kushi-ui :card})
            attrs)]
     children)))

