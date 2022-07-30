(ns kushi.ui.label.core
  (:require-macros
   [kushi.core :refer (sx)]
   [kushi.ui.core :refer (defcom2)])
  (:require
   [kushi.ui.core :refer (opts+children)]
   [kushi.core :refer (merge-with-style)]))

(defcom2 label
  {:desc {:desc ["A label is typically used for providing titles to sections of content."]}}
  [:span
   (merge-with-style
    (sx 'kushi-label
        :.flex-row-c
        :ai--c
        :d--inline-flex
        :w--fit-content
        {:data-kushi-ui :label})
    &attrs)
   &children]
  #(if (string? %) [:span.kushi-label-text %] %))
