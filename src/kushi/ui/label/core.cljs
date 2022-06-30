(ns kushi.ui.label.core
  (:require-macros
   [kushi.core :refer (sx)]
   [kushi.ui.core :refer (defcom)])
  (:require
   [kushi.ui.core :refer (opts+children)]
   [kushi.core :refer (merge-with-style)]))

(defcom label
  [:span
   (merge-with-style
    (sx 'kushi-label
        :.flex-row-c
        :ai--c
        :d--inline-flex
        :w--fit-content
        :>.kushi-label-text+.kushi-icon:mis--:--mui-icon-margin-inline-ems
        :>.kushi-icon+.kushi-label-text:mis--:--mui-icon-margin-inline-ems
        ; TODO remove data-kushi-ui ?
        {:data-kushi-ui :label})
    &attrs)
   &children]
  #(if (string? %) [:span.kushi-label-text %] %))
