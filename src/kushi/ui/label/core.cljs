(ns kushi.ui.label.core
  (:require-macros
   [kushi.core :refer (sx)])
  (:require
   [kushi.ui.core :refer (opts+children)]
   [kushi.core :refer (merge-attrs)]))

(defn label
  {:desc ["A label is typically used for providing titles to sections of content."]}
  [& args]
  (let [[_ attrs & children] (opts+children args)
        children (map #(if (string? %)
                         [:span.kushi-label-text %]
                         %)
                      children)]
    (into [:span
           (merge-attrs
            (sx 'kushi-label
                :.flex-row-c
                :jc--fs
                :.enhanceable
                :.transition
                :d--inline-flex
                :w--fit-content
                {:data-kushi-ui :label})
            attrs)]
          children)))
