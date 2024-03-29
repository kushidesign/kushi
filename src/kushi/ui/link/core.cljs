(ns kushi.ui.link.core
  (:require [kushi.core :refer (merge-attrs) :refer-macros (sx)]
            [kushi.ui.core :refer (opts+children)]
            [kushi.ui.label.core :refer (label)]))

(defn link
  "Desc for"
  [& args]
  (let [[opts attrs & children] (opts+children args)
        {:keys []}              opts]
    [:a
     (merge-attrs
      (sx 'kushi-link
          :.pointer
          :d--ib
          :>span:jc--fs
          :td--underline
          :tup--under
          {:data-kushi-ui :link})
      attrs)
     [apply label children]]))
