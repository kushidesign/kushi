(ns kushi.ui.icon-button
  (:require
   [bling.core]
   [kushi.core :refer (sx merge-attrs)]
   [kushi.ui.core :refer (defui)]
   [kushi.ui.icon :refer [icon]]
   [kushi.ui.util :refer [as-str]])
   ;; (:require-macros [kushi.ui.button])
  )

(defui icon-button
 {:doc          "Buttons are fundamental components that allow users to process actions or navigate an experience. Icon buttons feature a single icon or symbol, with no text"
  :summary      "Buttons provide cues for actions and events."
  :props/shared [:sizing
                 :colorway
                 :packing
                 :loading
                 :stroke-align
                 :stroke-width
                 :position
                 :contour
                 :surface
                 :transition]}
 [& args]
 (let [{:keys [loading stroke-width]} &props
       [icon*]                        &children]
   [:button
     (merge-attrs
      (sx
       "[data-ks-ui=\"icon-button\"]"
       :d--flex
       :flex-direction--row
       :jc--c
       :ai--c
       :w--fit-content
       :cursor--pointer
       ;; TODO - is this local/private css var necessary?
       [:--_padding-block :$icon-button-padding-block]
       [:--_padding-inline :$icon-button-padding-inline]
       :pi--$_padding-inline
       :pb--$_padding-block
       )
      {:aria-busy  loading
       :aria-label (when loading "loading")}

      &attrs

      (when stroke-width 
        {:style {"--_stroke-width" (as-str stroke-width)}}))
        [icon icon*]]))
