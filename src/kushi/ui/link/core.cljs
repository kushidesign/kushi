(ns kushi.ui.link.core
  (:require [kushi.core :refer (merge-attrs) :refer-macros (sx)]
            [kushi.ui.core :refer (opts+children)]))

(defn link
  "Desc for"
  [& args]
  (let [[opts attrs & children] (opts+children args)
        {:keys []}              opts]
    (into [:a
           (merge-attrs
            (sx 'kushi-link
                :.pointer
                :td--underline
                :tup--under
                [:tdc "color-mix(in oklch, currentColor 60%, transparent)"]
                [:hover:tdc :currentColor]
                {:data-kushi-ui :link})
            attrs)]
          children)))
