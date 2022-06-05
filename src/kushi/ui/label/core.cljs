(ns kushi.ui.label.core
  (:require-macros
   [kushi.core :refer (sx)])
  (:require
   [kushi.ui.core :refer (opts+children)]
   [kushi.core :refer (merge-with-style)]))

(defn label
  [& args]
  (let [[opts attrs & children] (opts+children args)
        {:keys []}              opts]
    (into
     [:span
      (merge-with-style
       (sx 'kushi-label
           :.flex-row-c
           :w--fit-content
           :>.kushi-label-text+.kushi-icon:mis--:--mui-icon-margin-inline-ems
           :>.kushi-icon+.kushi-label-text:mis--:--mui-icon-margin-inline-ems
           {:data-kushi-ui :label})
       attrs)]
     (map #(if (string? %) [:span.kushi-label-text %] %) children))))
