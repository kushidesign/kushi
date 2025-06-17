(ns kushi.ui.callout
  (:require
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


(defn callout-close-button
  {:desc "The `callout-close-button` is meant to be a cta for closing a callout.
          It is typically a single × icon positioned in the upper right or left
          corner of the dialog."
   :opts '[{:name    icon-name
            :schema    keyword?
            :default :close
            :desc    "Optional. A name of a Google Material Symbols icon.
                      Defaults to a close (×) icon."}
           {:name    colorway
            :schema    keyword?
            :default :neutral
            :desc    [""]}
           {:name    icon-svg
            :schema    vector?
            :default nil
            :desc    "Optional. A Hiccup representation of an svg icon. Supply
                      this as an alternative to using the Google Material
                      Symbols icon font"}
           ]}
  [& args]
  (let [[opts attrs & _]
        (extract args)

        {:keys     [icon-svg colorway]
         icon-name :icon
         :or {colorway "neutral"}}
        opts

        icon-name              
        (when-not icon-svg
          (if (and icon-name
                   (or (string? icon-name)
                       (keyword? icon-name)))
            icon-name
            :close))]
    [button
     (merge-attrs
      {:contour        :pill
       :colorway     colorway
       :surface      :minimal
       :class         (css ".kushi-callout-close-button"
                           :.absolute-centered!
                           :fs--inherit
                           :pb--0.5rem
                           :pis--0.5rem
                           :pie--0.449rem
                           :hover:bgc--$transparent-white-40
                           :active:bgc--transparent)
       :on-mouse-down close-callout}
      attrs)
     (if icon-svg
       [icon {:icon-svg icon-svg}]
       [icon icon-name])]))


(defn callout
  {:summary "Callouts provide contextual feedback information for the user."
   :desc "To position the callout at the top of the viewport, use the
          `:.fixed-block-start-inside` utility class, or the
          `:.fixed-block-end-inside` utility class for positioning
          at the bottom of the viewport."
   :opts '[{:name    icon
            :schema    vector?
            :default nil
            :desc    "An instance of a `kushi.ui.icon/icon` component. Places
                      an icon anchored to the inline-start area of the callout.
                      Optional."}
           {:name    user-actions
            :schema    fn?
            :default nil
            :desc    "Component rendering fn for CTA interactions. Can also be
                      a close button component via
                      `kushi.ui.callout/close-button`. Optional."}
           {:name    header-text
            :schema    string
            :default nil
            :desc    "The header text to render in the callout. Optional."}
           {:name    colorway
            :schema    #{:neutral :accent :positive :negative :warning}
            :default nil
            :desc    "Colorway of the callout. Can also be a named color from
                      Kushi's design system, e.g `:red`, `:purple`, `:gold`,
                      etc."}
           {:name    surface
            :schema    #{:faint :solid :minimal :outline}
            :default :round
            :desc    "Surface variant of the callout."}
           {:name    shape
            :schema    #{:sharp :round :pill}
            :default :round
            :desc    "Shape of the callout."}
           {:name    packing
            :schema    #{:compact :roomy}
            :default nil
            :desc    "General amount of padding inside the callout"}
          ;;  Leave this out for now
          ;;   {:name    duration
          ;;    :schema    pos-int?
          ;;    :default nil
          ;;    :desc    ["When supplied, the callout will dismiss itself after "
          ;;              "the given time (in milliseconds) has passed."]}

           ]}

  [& args]

  (let [{:keys [opts attrs children]}    
        (extract args)

        {:keys [loading?
                icon
                user-actions
                header-text
                stroke-align
                #_duration
                colorway
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

             ;; different from button
             ;;  :data-ks-ia      ""
             ;; different from button
        
        :data-ks-surface surface
        :data-ks-contour   shape}
       (when loading? {:data-ks-ui-spinner true})

 ;; different from button and tag
      ;;  (when (and (not icon) end-enhancer) (data-ks- "" :end-enhancer))
      ;;  (when (and (not icon) start-enhancer) (data-ks- "" :start-enhancer))
 ;; different from button and tag

       (some-> stroke-align 
               (maybe #{:outside "outside"})
               (data-ks- :stroke-align))
       (some-> (or semantic-colorway
                   (when hue-style-map ""))
               (data-ks- :colorway))

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
               :.flex-row-sb
               :position--relative
               :ta--center
               :gap--0.5em
               :p--0.85em:0.75em
               :w--100%)
      [:div (sx ".kushi-callout-header-icon-wrap"
                :.flex-col-fs
                :min-width--1em)
       icon]
      (if (or (string? header-text)
                (number? header-text)
                (keyword? header-text))
        [:span header-text]
        header-text)
      [:div (sx ".kushi-callout-header-close-button-wrap"
                :position--relative
                :min-width--1em
                :min-height--1em)
       (if (= user-actions callout-close-button)
         [user-actions {:colorway colorway :surface surface}]
         (when user-actions (user-actions)))
       ]]
     (when (seq children)
       (into [:div (sx ".kushi-callout-body" :p--1rem)]
             children))]))
