;; TODO bring faint down to table bg gray, or just above
;; TODO bring soft down a notch
;; Add soft outline surface variant

(ns kushi.ui.button
  (:require
   [bling.core :refer [callout]]
   [fireworks.core :refer [? !? ?> !?>]]
   [kushi.core :refer (defcss sx ?sx merge-attrs)]
   [kushi.ui.span :refer (span)]
   [kushi.ui.core :refer (defui)]
   [kushi.ui.shared :refer [add-enhancer]]
   [kushi.ui.decoration :as decoration]
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
                 :stroke
                 :stroke-align
                 :stroke-width
                 :stroke-color
                 :multi-stroke
                 :position
                 [:shape {:default :rounded}]
                 [:surface {:default :soft}]
                 :transition
                 :drop-shadow
                 :shadow-color]}
 [& args]
 (let [{:keys [loading
               stroke
               stroke-width
               surface
               drop-shadow]}
       &props

       classic-variant?
       (contains? #{:solid-classic :soft-classic} surface)

       button
       [:button 
        (merge-attrs
         {:data-ks-display :flex
          :data-ks-fd      :row
          :data-ks-jc      :center
          :data-ks-ai      :center
          :aria-busy       loading
          :aria-label      (when loading "loading")}

         ;; TODO - should this be (sx {:data-ks-ui :button} ...) => "[data-ks-ui=\"button\"]"
         ;; TODO - why this selector not working with (css ...) ?
         (sx ".ks-button"
             {:pi                :$padding-inline||$button-padding-inline
              :pb                :$padding-block||$button-padding-block
              :w                 :fit-content
              :h                 :fit-content
              :gap               :$icon-enhanceable-gap
              :cursor            :pointer
              :--stroke-width    :$button-stroke-width
              })

         (!? :pp (decoration/stroke-width-cssvar stroke-width "button"))

         (when-not classic-variant? 
           (decoration/drop-shadow-and-stroke-attrs &props))
         
         &attrs)]

       body
       (add-enhancer &props &children)]

   (if (and classic-variant?
            (or drop-shadow stroke))
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

