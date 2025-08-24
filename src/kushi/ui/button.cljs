;; TODO bring faint down to table bg gray, or just above
;; TODO bring soft down a notch
;; Add soft outline surface variant

(ns kushi.ui.button
  (:require
   [bling.core]
   [fireworks.core :refer [? !? ?> !?>]]
   [kushi.core :refer (sx merge-attrs)]
   [kushi.ui.span :refer (span)]
   [kushi.ui.core :refer (defui)]
   [kushi.ui.shared :refer [add-enhancer]]
   [kushi.ui.util :refer [as-str]]
   [kushi.ui.util :as util]))


(defui button
 {:doc          "Buttons are fundamental components that allow users to process actions or navigate an experience."
  :summary      "Buttons provide cues for actions and events."
  :props/shared [:sizing
                 :end-enhancer
                 :start-enhancer
                 :colorway
                 :packing
                 :loading
                 :stroke-align
                 :stroke-width
                 :position
                 :contour
                 [:surface {:default :soft}]
                 :transition
                 :drop-shadow
                 :stroke]}
 [& args]
 (let [{:keys [loading stroke-width stroke surface drop-shadow]}
       &props

       classic-variant?
       (contains? #{:solid-classic :soft-classic} surface)

       button
       [:button (merge-attrs
                 (sx
                  "[data-ks-ui=\"button\"]"
                  :d--flex
                  :flex-direction--row
                  :jc--c
                  :ai--c
                  :w--fit-content
                  :h--fit-content
                  :gap--$icon-enhanceable-gap
                  :cursor--pointer
                 ;; TODO - is this local/private css var necessary?
                  [:--_padding-block :$button-padding-block]
                  [:--_padding-inline :$button-padding-inline]
                  :pi--$_padding-inline
                  :pb--$_padding-block
                  ["[aria-label='loading']>.kushi-spinner-propeller:d" :revert]
                  ["[aria-label='loading']>.kushi-icon:d" :none])
                 {:aria-busy  loading
                  :aria-label (when loading "loading")}

                 &attrs

                 (when stroke-width 
                   {:style {"--_stroke-width" (as-str stroke-width)}})

                 (when (and (not classic-variant?)
                            (or drop-shadow stroke))
                   {:style {:box-shadow (util/box-shadow 
                                         {:shadows drop-shadow
                                          :strokes [stroke]})}}))]

       body
       (add-enhancer &props &children)]

   (if (and classic-variant? (or drop-shadow stroke))
     [span (merge-attrs 
            (let [{:keys [stroke-align contour colorway]} &props]
              {:drop-shadow  drop-shadow
               :stroke       stroke
               :stroke-align stroke-align
               :surface      :transparent
               :contour      contour
               :colorway     colorway})
            (sx "[data-ks-ui=\"button-style-wrapper\"]"
                :w--fit-content 
                :h--fit-content))
      (into button body)]
     (into button body))))

