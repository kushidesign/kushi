;; TODO bring faint down to table bg gray, or just above
;; TODO bring soft down a notch
;; Add soft outline surface variant

(ns kushi.ui.button
  (:require
   [fireworks.core :refer [? !? ?> !?>]]
   [bling.core]
   [clojure.string :as string]
   [kushi.core :refer (css-vars-map css defcss sx merge-attrs validate-option)]
   [kushi.ui.core :refer (extract fn->defui defui)]
   [kushi.ui.icon :refer [icon]]
   [kushi.ui.shared.theming :refer [data-ks- get-variants]]
   [kushi.ui.util :refer [as-str maybe nameable?]])
   ;; (:require-macros [kushi.ui.button])
  )


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
                 :transition]
  :props        {:surface {:default :soft}}}
 [& args]
 (let [{:keys [start-enhancer
               end-enhancer
               colorway
               loading
               stroke-width]}
       &props

       enhancer
       #(if (keyword? %) [icon %] %)

       start-enhancer                                                                                             
       (enhancer start-enhancer)

       end-enhancer                                                                                               
       (enhancer end-enhancer)
       ]
   (into
    [:button
     (merge-attrs
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
        {:style {"--_stroke-width" (as-str stroke-width)}}))]

    (cond start-enhancer
          (concat [start-enhancer] &children)

          end-enhancer
          (concat &children [end-enhancer])

          :else
          &children))))

