(ns kushi.ui.label
  (:require
   [fireworks.core :refer [? !? ?> !?>]]
   [kushi.ui.core :refer (defui)]
   [kushi.ui.shared :refer [add-enhancer]]
   [kushi.core :refer (css sx merge-attrs)]))


(defui label
  {:desc         "A label is typically used for providing titles to sections of content."
   :props/shared [:text-size
                  :end-enhancer
                  :start-enhancer
                  :colorway
                  ;; :packing
                  :stroke-align
                  :stroke-width
                  :position
                  ;; :shape
                  :surface
                  :transition
                  :inert]
   }
  [& args]
  (into [:label
         (merge-attrs
          (sx
           ".ks-label"
           :.flex-row-start
           :d--inline-flex
           :w--fit-content
           :gap--$icon-enhanceable-gap)
          &attrs)]
        (add-enhancer &props &children)))


