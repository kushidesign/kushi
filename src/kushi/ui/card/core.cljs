(ns kushi.ui.card.core
  (:require [kushi.core :refer (sx merge-with-style)]
            [kushi.ui.core :refer (opts+children)]))

(defn card
  "Desc for"
  [& args]
  (let [[opts attrs & children] (opts+children args)
        {:keys []}              opts]
    (into
     [:div (merge-with-style
            (sx 'kushi-card
                :.relative
                :w--100%
                :p--2rem
                :bgc--white
                {:data-kushi-ui :card})
            attrs)]
     children)))

