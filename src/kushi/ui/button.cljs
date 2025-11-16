;; TODO bring faint down to table bg gray, or just above
;; TODO bring soft down a notch
;; Add soft outline surface variant

(ns kushi.ui.button
  (:require
   [bling.core :refer [callout]]
   [fireworks.core :refer [? !? ?> !?>]]
   [kushi.core :refer (css sx ?sx merge-attrs)]
   [kushi.ui.span :refer (span)]
   [kushi.ui.core :refer (defui)]
   [kushi.ui.shared :refer [add-enhancer]]
   [kushi.ui.decoration :as decoration]
   [kushi.ui.util :as util]))


(defui button
 {:doc          "Buttons are fundamental components that allow users to process actions or navigate an experience."
  :summary      "Buttons provide cues for actions and events."
  :props/shared [:text-size
                 :text-weight
                 :end-enhancer
                 :start-enhancer
                 [:colorway {:default :neutral}]
                 :packing
                 :stroke
                 :stroke-align
                 :stroke-width
                 :stroke-color
                 :position
                 [:shape {:default :rounded}]
                 [:surface {:default :soft}]
                 :transition
                 :shadow
                 :shadow-color]}
 [& args]
 (let [{:keys [stroke
               stroke-width
               surface
               shadow]}
       &props

       classic-variant?
       (contains? #{:solid-classic :soft-classic} surface)

       button
       [:button 
        (merge-attrs
         {:class      (css ".ks-button"
                           {:d              :flex
                            :flex-direction :row
                            :jc             :center
                            :ai             :center
                            :pi             :$padding-inline||$button-padding-inline
                            :pb             :$padding-block||$button-padding-block
                            :w              :fit-content
                            :h              :fit-content
                            :gap            :$icon-enhanceable-gap
                            :cursor         :pointer
                            :--stroke-width :$button-stroke-width})}

         (!? :pp (some-> stroke-width
                        (decoration/stroke-width-cssvar "button")))

         #_(? :pp (when-not classic-variant? 
                  (decoration/shadow-and-stroke-attrs &props)))
         
         &attrs)]

       body
       (add-enhancer &props &children)]

   (if (and classic-variant?
            (or shadow stroke))
     [span (merge-attrs 
            (let [{:keys [stroke-align shape colorway]
                   :or {stroke-align :inside}}
                  &props]
              (when (and js/goog.DEBUG stroke (= stroke-align :inside))
                (callout {:type       :warning
                          :side-label (:data-ks-at &attrs)}
                         "kushi.ui.button"
                         "\n\n"
                         "Inset strokes will not be visible on buttons with the \n"
                         "surface value of `:soft-classic` or `:solid-classic`"
                         "\n\n"
                         "Try using `{:stroke-align :outside}`"))
              (assoc &props :surface :transparent))
            (sx ".ks-button-decoration-wrapper"
                :w--fit-content 
                :h--fit-content))
      (into button body)]
     (into button body))))

