(ns kushi.ui.callout
  (:require
   [fireworks.core :refer [? !? ?> !?>]]
   [clojure.string :as string]
   [domo.core :as domo]
   [goog.dom :as gdom]
   [kushi.core :refer [merge-attrs sx]]
   [kushi.ui.icon-button :refer [icon-button]]
   [kushi.ui.core :refer (defui)]
   [kushi.ui.icon :refer [icon]]
   [reagent.dom]))

;; TODO - this is mousedown, so isolate only if primary click
#_(defn close-callout [e]
  (.stopPropagation e)
  (let [el      (domo/et e)
        callout (domo/nearest-ancestor el "[data-ks-ui=\"callout\"]")]
    (when (gdom/isElement callout)
      (let [duration* (.-transitionDuration (js/window.getComputedStyle callout))
            duration  (js/Math.round (* 1000 (js/parseFloat (string/replace duration* #"s$" ""))))]
        (domo/set-style! callout "opacity" 0)
        (js/setTimeout #(reagent.dom/unmount-component-at-node callout) duration)))))


;; TODO - make version without the layout slots
(defui callout
  {:summary "Callouts provide contextual feedback information for the user."
   :desc    "To position the callout at the top of the viewport, use the
          `:.fixed-block-start-inside` utility class, or the
          `:.fixed-block-end-inside` utility class for positioning
          at the bottom of the viewport."
   :props/family [:container]
   :props/shared [:packing :transition [:surface {:default :faint}]]
   :props   {:header-icon     {:schema  :keyword,
                               :default nil,
                               :desc    "Name of the icon to anchored to the inline-start area of the callout."},
             :user-actions    {:schema  [:vector :any]
                               :default nil
                               :desc    "hiccup for CTA interactions."},
             :close-button?   {:schema  :boolean,
                               :default true,
                               :desc    "If true, places a close button to inline-end area of the callout"},
             :close-button-fn {:schema  :any,
                               :default nil,
                               :desc    "If provided, performs action with callout button is clicked"},
             :header-text     {:schema  [:or :string [:vector :any]],
                               :default nil,
                               :desc    "The header text to render in the callout."}, 
             }}
  [& args]
  (let [{:keys [loading
                user-actions
                close-button?
                close-button-fn
                header-text
                header-icon
                inert
                colorway
                stroke-width
                surface
                ]}
        &props

        callout-id                   
        (str (.now js/Date))]

    ;; Leave this out for now
    ;; (when (pos-int? duration)
    ;;   (js/setTimeout #(when-let [el (domo/el-by-id callout-id)]
    ;;                     (.remove el))
    ;;                  duration))

    [:section
      (merge-attrs
       (sx "[data-ks-ui=\"callout\"]"
           :position--relative
           :d--flex
           :flex-direction--row
           :jc--c
           :ai--c
           :w--100%
           :gap--$icon-enhanceable-gap
           [:--_padding-block-start "calc(var(--callout-padding-block) * var(--callout-padding-block-start-reduction-ratio, 1))"]
           [:--_padding-block-end   :$callout-padding-block]
           [:--_padding-inline      :$callout-padding-inline]
           :pi--$_padding-inline
           :pbs--$_padding-block-start
           :pbe--$_padding-block-end)
       
       {:aria-busy  loading
        :aria-label (when loading "loading")}

       (when stroke-width 
         {:style {"--_stroke-width" (name stroke-width)}})

       (when-not (false? inert) {:data-ks-inert ""})
       (when loading {:data-ks-ui-spinner ""})

       &attrs)     

     [:div (sx "[data-ks-ui=\"callout-header-wrap\"]"
               :.flex-row-space-between
               :position--relative
               :ta--center
               :gap--0.5em
               :p--0.85em:0.75em
               :w--100%)
      [:div (sx "[data-ks-ui=\"callout-header-icon-wrap\"]" 
                :.flex-col-fs
                :min-width--1em)
       (if (keyword? header-icon)
         [icon {:colorway colorway} header-icon]
         header-icon)]

      (if (or (string? header-text)
              (number? header-text)
              (keyword? header-text))
        [:span header-text]
        header-text)

      [:div (sx "[data-ks-ui=\"callout-header-user-inline-end-slot\"]"
                :position--relative
                :min-width--1em
                :min-height--1em)
       (when (or user-actions (and close-button? close-button-fn))
         [:div 
          {:data-ks-ui (if user-actions "callout-user-actions" "callout-close-button")}
          (or user-actions 
              (when close-button?
                [icon-button
                 (merge-attrs
                  {:contour       :pill
                   :surface       surface
                   :colorway      colorway
                   :on-mouse-down close-button-fn})
                 :close]))])]]

     (when (seq &children)
       (into [:div (sx "[data-ks-ui=\"callout-body\"]" :p--1rem)]
             &children))]))
