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
                 :surface
                 :transition]}
 [& args]
 (let [{:keys [start-enhancer
               end-enhancer
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
       "[data-ks-ui=\"button\"]"
       :d--flex
       :flex-direction--row
       :jc--c
       :ai--c
       :w--fit-content
       [:h "calc(1em + (2 * var(--_padding-inline)))"]
       :cursor--pointer
       ;; TODO - is this local/private css var necessary?
       [:--_padding-block :$button-padding-block]
       [:--_padding-inline :$button-padding-inline]
       :pi--$_padding-inline
      ;;  :pb--$_padding-block
       )
      {:aria-busy  loading
       :aria-label (when loading "loading")}

      &attrs

      (when stroke-width 
        {:style {"--_stroke-width" (as-str stroke-width)}}))
        [icon icon*]]))



;; Sample component built with 2-fn macro pattern 
;; (defn big-paw
;;   {:doc "Hi from big button"
;;   ;;  :opts '{size {:schema #{:small :large :xxxlarge}}}
;;    }
;;   [& args]
;;   (let [{:keys [opts attrs children]}
;;         (extract args)]
;;     (into [:div 
;;            (merge-attrs 
;;             #_(sx :c--red)
;;             {:style {"--fs" (case (:size opts)
;;                               :small    "20px"
;;                               :large    "80px"
;;                               :xxxlarge :200px
;;                               nil)}
;;              :class (css :c--red :fs--$fs)}
;;             attrs)]
;;           children)))
