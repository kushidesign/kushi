(ns kushi.ui.callout
  (:require
   [fireworks.core :refer [? !? ?> !?>]]
   [clojure.string :as string]
   [domo.core :as domo]
   [goog.dom :as gdom]
   [kushi.core :refer [css merge-attrs sx]]
   [kushi.ui.button :refer [button]]
   [kushi.ui.core :refer (extract)]
   [kushi.ui.icon :refer [icon]]
   [kushi.ui.shared.theming :refer [data-ks- get-variants hue-style-map]]
   [kushi.ui.util :refer [keyed maybe]]))

;; TODO - this is mousedown, so only if primary click
(defn close-callout [e]
  (.stopPropagation e)
  (let [el      (domo/et e)
        callout (domo/nearest-ancestor el ".kushi-callout")]
    (when (gdom/isElement callout)
      (let [duration* (.-transitionDuration (js/window.getComputedStyle callout))
            duration  (js/Math.round (* 1000 (js/parseFloat (string/replace duration* #"s$" ""))))]
        (domo/set-style! callout "opacity" 0)
        (js/setTimeout #(.remove callout) duration)))))


;; TODO - dedupe desc from global variants
(defn callout
  {:summary "Callouts provide contextual feedback information for the user."
   :desc "To position the callout at the top of the viewport, use the
          `:.fixed-block-start-inside` utility class, or the
          `:.fixed-block-end-inside` utility class for positioning
          at the bottom of the viewport."
   :opts {:header-icon   {:schema  keyword?,
                          :default nil,
                          :desc    "Name of the icon to anchored to the inline-start area of the callout."},
          :user-actions  {:schema  fn?
                          :default nil
                          :desc    "hiccup for CTA interactions."},
          :close-button? {:schema  boolean?,
                          :default nil,
                          :desc    "If true, places a close button to inline-end area of the callout"},
          :header-text   {:schema  string?,
                          :default nil,
                          :desc    "The header text to render in the callout."},
          :colorway      {:schema  #{:neutral :positive :negative :warning :accent},
                          :default nil,
                          :desc    "Colorway of the callout. Can also be a named color from Kushi's design system, e.g `:red`, `:purple`, `:gold`, etc."},
          :surface       {:schema  #{:solid :minimal :outline :faint},
                          :default :round,
                          :desc    "Surface variant of the callout."},
          :shape         {:schema  #{:pill :round :sharp},
                          :default :round,
                          :desc    "Shape of the callout."},
          :packing       {:schema  #{:compact :roomy},
                          :default nil,
                          :desc    "General amount of padding inside the callout"}}}

  [& args]

  (let [{:keys [opts attrs children]}    
        (extract args [:header-icon :header-text :loading? :user-actions :close-button?])

        {:keys [loading?
                user-actions
                close-button?
                header-text
                header-icon
                inert?
                #_stroke-align
                #_duration
                colorway
                sizing
                contour
                ]}
        opts

        {:keys             [shape surface]
         semantic-colorway :colorway}
        (get-variants opts)

        hue-style-map                 
        (when-not semantic-colorway 
          (some-> colorway
                  hue-style-map))

        callout-id                   
        (str (.now js/Date))]

    ;; Leave this out for now
    ;; (when (pos-int? duration)
    ;;   (js/setTimeout #(when-let [el (domo/el-by-id callout-id)]
    ;;                     (.remove el))
    ;;                  duration))

    [:section
      (merge-attrs
       (sx ".kushi-callout"
           :position--relative
           :d--flex
           :flex-direction--row
           :jc--c
           :ai--c
           :w--100%
           :gap--$icon-enhanceable-gap
           
           ;; different from button
           ;; :cursor--pointer
           
           :transition-property--all
           :transition-timing-function--$transition-timing-function
           :transition-duration--$transition-duration

                ;; different from button
           [:--_padding-block-start "calc(var(--callout-padding-block) * var(--callout-padding-block-start-reduction-ratio, 1))"]
           [:--_padding-block-end   :$callout-padding-block]
           [:--_padding-inline      :$callout-padding-inline]
                ;; different from button
           
           :pi--$_padding-inline
           :pbs--$_padding-block-start
           :pbe--$_padding-block-end)

       {:aria-busy        loading?
        :aria-label       (when loading? "loading")
        :data-ks-surface  surface
        :data-ks-contour  (or contour :rounded)
        :data-ks-sizing   sizing
        :data-ks-colorway colorway}
       (when-not (false? inert?) {:data-ks-inert ""})
       (when loading? {:data-ks-ui-spinner ""})


      ;; need these?

      ;;  (some-> stroke-align 
      ;;          (maybe #{:outside "outside"})
      ;;          (data-ks- :stroke-align))
      ;;  (some-> (or semantic-colorway
      ;;              (when hue-style-map ""))
      ;;          (data-ks- :colorway))
       

 ;; different from button and tag
      ;;  (when (and (not icon) end-enhancer) (data-ks- "" :end-enhancer))
      ;;  (when (and (not icon) start-enhancer) (data-ks- "" :start-enhancer))
 ;; different from button and tag

 ;; different from button and tag
      ;;  (some-> packing
      ;;          (maybe nameable?)
      ;;          as-str
      ;;          (maybe #{"compact" "roomy"})
      ;;          (data-ks- :packing))
 ;; different from button and tag
       
       hue-style-map
       (some-> surface (data-ks- :surface))
       attrs)     

     [:div (sx ".kushi-callout-header-wrap"
               :.flex-row-space-between
               :position--relative
               :ta--center
               :gap--0.5em
               :p--0.85em:0.75em
               :w--100%)
      [:div (sx ".kushi-callout-header-icon-wrap"
                :.flex-col-fs
                :min-width--1em)
       (if (keyword? header-icon)
         [icon header-icon]
         header-icon)]

      (if (or (string? header-text)
              (number? header-text)
              (keyword? header-text))
        [:span header-text]
        header-text)

      [:div (sx ".kushi-callout-header-user-inline-end-slot"
                :position--relative
                :min-width--1em
                :min-height--1em)
       (if user-actions 
         user-actions
         (when close-button?
           [button
            (merge-attrs
             {:contour       :pill
              :surface       surface
              :colorway      colorway
              :class         (css ".kushi-callout-close-button"
                                  :.absolute-centered!
                                  :.transition
                                  :fs--inherit
                                  :pb--0.5rem
                                  :pis--0.5rem
                                  :pie--0.449rem
                                  :active:bgc--transparent)
              :on-mouse-down close-callout}
             attrs)
            ;; TODO make this svg
            [icon :close]]))]]

     (when (seq children)
       (into [:div (sx ".kushi-callout-body" :p--1rem)]
             children))]))
