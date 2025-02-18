(ns kushi.ui.callout.core
  (:require [kushi.core :refer [sx css merge-attrs]]
            [kushi.ui.util :refer [maybe]]
            [goog.dom :as gdom]
            [domo.core :as domo]
            [clojure.string :as string]
            [kushi.ui.core :refer (opts+children)]
            [kushi.ui.shared.theming :refer [data-kui- get-variants hue-style-map]]
            [kushi.ui.icon.core :refer [icon]]
            [kushi.ui.button.core :refer [button]]
            )
  #_(:require [kushi.core :refer (css sx merge-attrs)]
            [kushi.ui.core :refer (opts+children)]
            [kushi.ui.icon.core]))

(defn close-callout [e]
  (.stopPropagation e)
  (let [el      (domo/et e)
        callout (domo/nearest-ancestor el ".kui-callout")]
    (when (gdom/isElement callout)
      (let [duration* (.-transitionDuration (js/window.getComputedStyle callout))
            duration  (js/Math.round (* 1000 (js/parseFloat (string/replace duration* #"s$" ""))))]
        (domo/set-style! callout "opacity" 0)
        (js/setTimeout #(.remove callout) duration)))))

(defn callout-close-button
  {:desc ["The `callout-close-button` is meant to be a cta for closing a callout."
          "It is typically a single × icon positioned in the upper right or left corner of the dialog."]
   :opts '[{:name    icon
            :pred    keyword?
            :default :close
            :desc    ["Optional. A name of a Google Material Symbols icon. Defaults to a close (×) icon."]}
           {:name    colorway
            :pred    keyword?
            :default :neutral
            :desc    [""]}
           {:name    icon-svg
            :pred    vector?
            :default nil
            :desc    ["Optional. A Hiccup representation of an svg icon. Supply this as an alternative to using the Google Material Symbols icon font"]}]}
  [& args]
  (let [[opts attrs & _]
        (opts+children args)

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
      {:-shape        :pill
       :-colorway     colorway
       :-surface      :minimal
       :class         (css ".kushi-callout-close-button"
                           :fs--inherit
                           :pb--0.5rem
                           :pis--0.5rem
                           :pie--0.499rem
                           :hover:bgc--transparent
                           :active:bgc--transparent)
       :on-mouse-down close-callout}
      attrs)
     (if icon-svg
       [icon {:icon-svg icon-svg}]
       [icon icon-name])]))

(defn callout
  {:summary ["Callouts provide contextual feedback information for the user."]
   :desc ["To position the callout at the top of the viewport, use the "
          "`:.fixed-block-start-inside` utility class, or the "
          "`:.fixed-block-end-inside` utility class for positioning "
          "at the bottom of the viewport."]
   :opts '[{:name    icon
            :pred    vector?
            :default nil
            :desc    ["An instance of a `kushi.ui.icon/icon` component"
                      "Places an icon anchored to the inline-start area "
                      "of the callout. Optional."]}
           {:name    user-actions
            :pred    fn?
            :default nil
            :desc    ["Component rendering fn for CTA interactions."
                      "Can also be a close button component via `kushi.ui.callout.core/close-button`."
                      "Optional."]}
           {:name    header-text
            :pred    string
            :default nil
            :desc    ["The header text to render in the callout."
                      "Optional."]}
          ;;  Leave this out for now
          ;;   {:name    duration
          ;;    :pred    pos-int?
          ;;    :default nil
          ;;    :desc    ["When supplied, the callout will dismiss itself after "
          ;;              "the given time (in milliseconds) has passed."]}

           ]}

  [& args]

  (let [[opts attrs & children]    
        (opts+children args)

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
     #_(merge-attrs
      {:class (css :.kushi-callout
                   :.info
                   :ai--c
                   :w--100%)
       :id callout-id}
      attrs)
      (merge-attrs
       (sx ".kui-callout"
           :position--relative
           :d--flex
           :flex-direction--row
           :jc--c
           :ai--c
           :w--fit-content
           :gap--$icon-enhanceable-gap
           
           ;; different from button
           ;; :cursor--pointer
           
           :transition-property--all
           :transition-timing-function--$transition-timing-function
           :transition-duration--$transition-duration

                ;; different from button
           [:--_padding-block-start "calc(var(--tag-padding-block) * var(--tag-padding-block-start-reduction-ratio, 1))"]
           [:--_padding-block-end   :$tag-padding-block]
           [:--_padding-inline      :$tag-padding-inline]
                ;; different from button
           
           :pi--$_padding-inline
           :pbs--$_padding-block-start
           :pbe--$_padding-block-end)

       {:aria-busy        loading?
        :aria-label       (when loading? "loading")

             ;; different from button
             ;;  :data-kui-ia      ""
             ;; different from button
        
        :data-kui-surface surface
        :data-kui-shape   shape}
       (when loading? {:data-kushi-ui-spinner true})

 ;; different from button and tag
      ;;  (when (and (not icon) end-enhancer) (data-kui- "" :end-enhancer))
      ;;  (when (and (not icon) start-enhancer) (data-kui- "" :start-enhancer))
 ;; different from button and tag

       (some-> stroke-align 
               (maybe #{:outside "outside"})
               (data-kui- :stroke-align))
       (some-> (or semantic-colorway
                   (when hue-style-map ""))
               (data-kui- :colorway))

 ;; different from button and tag
      ;;  (some-> packing
      ;;          (maybe nameable?)
      ;;          as-str
      ;;          (maybe #{"compact" "roomy"})
      ;;          (data-kui- :packing))
 ;; different from button and tag
       
       hue-style-map
       (some-> surface (data-kui- :surface))
       attrs)     

     [:div (sx :.kushi-callout-header-wrap
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
      header-text
      [:div (sx :.kushi-callout-header-close-button-wrap
                :min-width--1em)
       (if (= user-actions callout-close-button)
         [user-actions {:-colorway colorway :-surface surface}]
         (user-actions))
       ]]
     (when (seq children)
       (into [:div (sx ".kushi-callout-body" :p--1rem)]
             children))]))
