(ns kushi.ui.label.core
  (:require
   [kushi.ui.core :refer (opts+children)]
   [kushi.css.core :refer (css merge-attrs)]))

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
            {:class         (css
                             ".kushi-label"
                             :.flex-row-c
                             :.enhanceable-with-icon
                             :.transition
                             :jc--fs
                             :d--inline-flex
                             :w--fit-content)
             :data-kushi-ui :label}
            attrs)]
          children)))
