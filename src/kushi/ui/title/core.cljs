(ns kushi.ui.title.core
  (:require [kushi.core :refer (merge-with-style) :refer-macros [sx]]
            [kushi.ui.core :refer (opts+children)]
            [kushi.ui.label.core :refer (label)]))

(defn title
  [& args]
  (let [[opts attrs & children] (opts+children args)
        {:keys []}             opts]
    [:span
     (merge-with-style
      (sx 'kushi-title
          :d--ib
          :>span:jc--fs
          {:data-kushi-ui :title})
      attrs)
      [apply label children]]))
