(ns kushi.ui.icon-button
  (:require
   [fireworks.core :refer [? !? ?> !?>]]
   [bling.core]
   [kushi.core :refer (sx merge-attrs)]
   [kushi.ui.core :refer (defui)]
   [kushi.ui.icon :refer [icon]]
   [kushi.ui.util :as util])
   ;; (:require-macros [kushi.ui.button])
  )

(defui icon-button
 {:doc          "Buttons are fundamental components that allow users to process actions or navigate an experience. Icon buttons feature a single icon or symbol, with no text"
  :summary      "Buttons provide cues for actions and events."
  :props/shared [:text-size
                 :text-weight
                 [:colorway {:default :neutral}]
                 [:surface {:default :soft}]
                 [:shape {:default :pill}]
                 :packing
                 :stroke
                 :stroke-align
                 :stroke-width
                 :position
                 :icon-filled
                 :transition]}
 [& args]
 (let [{:keys [surface stroke-width colorway icon-filled]} &props
       [icon*]                                         &children
       
       classic-variant?
       (contains? #{:solid-classic :soft-classic} surface)]
   [:button
    (merge-attrs
     (sx
      ".ks-icon-button"
      :d--flex
      :flex-direction--row
      :jc--c
      :ai--c
      :w--fit-content
      :cursor--pointer
       ;; TODO - is this local/private css var necessary?
      [:--padding-block :$icon-button-padding-block]
      [:--padding-inline :$icon-button-padding-inline]
      :pi--$padding-inline
      :pb--$padding-block
      )

     (util/stroke-width-cssvar stroke-width "button")

     (when-not classic-variant? 
       (util/shadow-and-stroke-attrs &props))

     &attrs

     (when stroke-width 
       {:style {"--_stroke-width" (util/as-str stroke-width)}}))
    [icon
     (merge #_(some->> colorway (hash-map :colorway))
            (some->> icon-filled (hash-map :icon-filled)))
     icon*]]))
