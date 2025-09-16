(ns kushi.ui.checkbox
  (:require
   [kushi.core :refer (css sx merge-attrs)]
   [kushi.ui.core :refer (defui)]))

(defui checkbox
  {:doc          "Checkboxes are used to provide multiple options for selection."
   :props/shared [:size :colorway :transition :weight]
   :props        {:label-attrs {:schema  :map
                                :default nil
                                :desc    "HTML attributes map applied to the wrapping `label` div."}}}
  [& args]

  [:input
   (merge-attrs
    {:class           (css
                       "[data-ks-ui=\"checkbox\"]"
                       {:transition-duration      :$xxfast
                        :cursor                   :pointer
                        :+label:cursor            :pointer
                        :+label:pis               :0.369em
                       ;; why inline-grid?
                        :display                  :inline-grid
                        :place-content            :center
                        :-webkit-appearance       :none
                        :appearance               :none
                        :bgc                      :transparent
                        :color                    :currentColor
                        :m                        :0
                        :width                    :1em
                        :height                   :1em
                        ;; :font                     :inherit
                        :border-style             :solid
                        :border-width             :0.15em
                        :border-color             "color-mix(in hsl, currentColor 55%, transparent)"
                        :border-radius            :0em
                        :before:box-shadow        "inset 1em 1em white"
                        :before:clip-path         "polygon(14% 44%, 0 65%, 50% 100%, 100% 16%, 80% 0%, 43% 62%)"
                        :before:content           "\"\""
                        :before:height            :0.65em
                        :before:transform         "scale(0) rotate(15deg)"
                        :before:transform-origin  :center:center
                        :before:transition        :120ms:transform:ease-in-out
                        :before:width             :0.65em
                        :checked:bgc              :currentColor
                        :checked:o                :1
                        :checked:border-color     :currentColor
                        :checked:before:transform "scale(1) rotate(15deg)"})
     :data-ks-ui      :checkbox
     :data-ks-surface :transparent
     :type            :checkbox}
    &attrs)])
