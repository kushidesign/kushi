(ns kushi.ui.input.switch.demo
  (:require-macros
   [kushi.core :refer (sx)])
  (:require
   [kushi.ui.input.switch.core :refer [switch]]))

(defn switch-demo [{dark? :-dark? :or {dark? false}}]
  (into [:div
         (sx :.flex-col-fs
            :gap--0.5em
            :padding--1rem
            :.xxxlarge
            {:class [(when dark? :dark)]})]
        (for [semantic [:foo
                        :neutral :accent
                        :positive :warning :negative
                        ]]
          [:div (sx :.flex-row-fs :gap--0.5em)
           [switch (sx {:-disabled? false
                        :class      [semantic]})]
           [switch (sx {:-disabled? false
                          ;; :-disable-events? true
                        :-on?       true
                        :class      [semantic]})]])))

(defn switch-demo-light+dark []
  [:div.flex-row-fs
   [switch-demo]
   [switch-demo {:-dark? true}]])
