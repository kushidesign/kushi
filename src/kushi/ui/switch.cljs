(ns kushi.ui.switch
  (:require
   [clojure.string :as string]
   [fireworks.core :refer [? !? ?> !?>]]
   [kushi.core :refer (sx defcss merge-attrs)]
   [domo.core :as domo]
   [kushi.ui.variants :refer [variants-by-custom-opt-key]]
   [kushi.ui.icon :refer (icon)]
   [kushi.ui.util :as util]
   [kushi.ui.thumb :refer (thumb)]
   [kushi.ui.core :refer (defui)]))

(defn- toggle-switch [%]
  (let [node* (domo/et %)
        node  (if (domo/has-class? node* "kushi-switch")
                node*
                (domo/nearest-ancestor node* ".kushi-switch"))]
    (domo/toggle-boolean-attribute node "aria-checked")))


(defui switch
  {:summary "Switches are used to toggle an individual option on or off."
   :desc "Switches can be custom styled via a variety of tokens in your theme.
          
          `--switch-width-ratio`
          Setting this to 1.5 will result in a switch that has the aspect ratio
          of 1.5:1 (width:height). The default value is 2.
          
          `--switch-border-width`
          The default value is `2px`. If customizing the value, it is
          recommended to use a `px` or `rem` value, especially if you are using
          the `:track-label-on` or `:track-label-off` options.
          
          `--switch-border-color`
          The default value is `transparent`, which will read as \"padding\"
          between the switch \"thumb\" and the switch \"track\", as by default
          the switch will have a solid background color in both the on and off
          states. You can supply a color value which will result in more of an
          \"outlined\" styling.
          
          `--switch-thumb-scale-factor`
          The default value is `1`. Setting this to a value greater than 1 will
          result in the thumb height being greater than the track height."
          
          
   :props/shared [:colorway
                  :size
                  :weight
                  [:shape {:default :pill}]]
   :props {:on?                {:schema   :boolean
                                :default  false
                                :desc     "Control the initial on/off state of the switch"
                                :data-ks? false}
           :disable-events?    {:schema  :boolean
                                :default false
                                :desc    "Set this to true if you would like to control the state of the switch in a reactive manner via the `:on?` option"}
           :thumb-props        {:schema  :map 
                                :default nil
                                :desc    "Map of props + HTML attributes applied to the thumb of the switch. See `kushi.ui.thumb` component docs."}
           :thumb-icon-props   {:schema  :map 
                                :default nil
                                :desc    "Map of props + HTML attributes applied to the optional icon on the thumb of the switch. See `kushi.ui.icon` component docs."}
           :thumb-label-off    {:schema   :string
                                :default  nil
                                :desc     "String that will be placed in center of thumb, when in the \"off\" position"
                                :data-ks? false}
           :thumb-label-on     {:schema   :string
                                :default  nil
                                :desc     "String that will be placed in center of thumb, when in the \"on\" position"
                                :data-ks? false}
           :thumb-icon-off     {:schema   :keyword
                                :default  nil
                                :desc     "Name of icon that will be placed in center of thumb, when in the \"off\" position"
                                :data-ks? false}
           :thumb-icon-on      {:schema   :keyword
                                :default  nil
                                :desc     "Name of icon that will be placed in center of thumb, when in the \"on\" position"
                                :data-ks? false}
           ;; track-inset-gap cannot be negative e.g. :-1px or "-1px"
           :track-inset-gap    {:schema   [:or :string :keyword]
                                :default  "1px"
                                :desc     "Size of the gap between the outer edge of thumb and the inner edge of the track."
                                :data-ks? false}
           :thumb-scale-factor {:schema   [:or :string :float]
                                :default  nil
                                :desc     "If set to 1, the thumb height will be 100% the height of the switch track, minus the value of the track-inset-gap"
                                :data-ks? false}
           :switch-width-ratio {:schema  :float
                                :default nil
                                :desc    "Width ratio of switch"}}}
  [& args]
  (let [{:keys [disable-events?
                on?
                colorway
                shape
                thumb-props
                thumb-label-off
                thumb-label-on
                thumb-icon-off
                thumb-icon-on
                thumb-icon-props
                thumb-scale-factor
                track-inset-gap
                switch-width-ratio]
         :or   {shape :pill}}
        (!? &props)

        rounded-shape?
        (!? 'rounded-shape?
            (contains? (:shape/rounded+rounded-absolute variants-by-custom-opt-key)
                       shape))

        relative-rounded-shape?
        (!? 'relative-rounded-shape? 
            (contains? (:shape/rounded variants-by-custom-opt-key) shape))

        shape
        (if relative-rounded-shape?
          (!? (-> shape name (str "-absolute") keyword))
          shape)

        track-inset-gap
        (string/replace (util/as-str track-inset-gap) #"^\-" "")

        disabled?                  
        (util/html-attr? &attrs :disabled)
        
        thumb-icon-provided? 
        (boolean (and thumb-icon-on thumb-icon-off))

        thumb-label-provided? 
        (boolean (and thumb-label-on thumb-label-off)) 

        icon-on-thumb? 
        thumb-icon-provided?

        label-on-thumb? 
        (and (not icon-on-thumb?) thumb-label-provided?)]

    [:button
     (merge-attrs
      (if (pos? thumb-scale-factor)
        {:style {"--thumb-scale-factor" thumb-scale-factor
                 "--track-inset-gap"   :0px
                 "overflow"             :visible}}
        {:style {"--thumb-scale-factor" 1}})

      (when track-inset-gap {:style {"--track-inset-gap" track-inset-gap}})

      (when switch-width-ratio {:style {"--switch-width-ratio" switch-width-ratio}})

      (sx
       ".kushi-switch"
       :.surface-solid
       :.transition
       :.display-flex-row
       {:--thumb-height                        "calc(var(--thumb-scale-factor, var(--switch-thumb-scale-factor, 1)) * (1em - (var(--track-inset-gap, 1px) * 2)))"
        :--height                              :1em
        :--switch-track-inset-box-shadow-color :$transparent-black-05
        :flex-shrink                           0
        :overflow                              :clip
        :transition-duration                   :$xxfast
        :position                              :relative
        :cursor                                :pointer
        :width                                 "calc((var(--height) *  max(1.25, var(--switch-width-ratio, 1.8))) - var(--switch-border-width))"
        :height                                :$height
        :padding                               :$track-inset-gap||1px
        :box-shadow                            "inset 0 0 0.15em 0.01em var(--switch-track-inset-box-shadow-color)"
        :_.ks-thumb                            {:width                                       :$thumb-height
                                                :height                                      :$thumb-height
                                                "has-ancestor(.kushi-switch[disabled]):cursor" :not-allowed}
        "[aria-checked='true']"                  {:_.ks-thumb:inset-inline-start          "calc(100% - var(--thumb-height) - var(--track-inset-gap) + (((var(--thumb-height) * var(--thumb-scale-factor, 1)) - var(--thumb-height)) / 2))"
                                                  :justify-content                        :flex-end
                                                  :_.kushi-switch-thumb-icon-on:display   :flex
                                                  :_.kushi-switch-thumb-icon-off:display  :none
                                                  :_.kushi-switch-thumb-label-on:display  :flex
                                                  :_.kushi-switch-thumb-label-off:display :none}
        "[aria-checked='false']"                 {:_.ks-thumb:inset-inline-start          "calc(var(--track-inset-gap) - (((var(--thumb-height) * var(--thumb-scale-factor, 1)) - var(--thumb-height)) / 2))"
                                                  :_.kushi-switch-thumb-icon-on:display   :none
                                                  :_.kushi-switch-thumb-icon-off:display  :flex
                                                  :_.kushi-switch-thumb-label-on:display  :none
                                                  :_.kushi-switch-thumb-label-off:display :flex
                                                  :_.kushi-switch-thumb-content:c    :$foreground-color-secondary
                                                  :bgc                                    :$switch-off-background-color
                                                  :hover:bgc                              :$switch-off-background-color-hover
                                                  ".inert:bgc"                     :$switch-off-background-color
                                                  ".inert:hover:bgc"               :$switch-off-background-color-hover
                                                  :dark:bgc                               :$background-color-neutral-soft-3-dark-mode
                                                  :dark:hover:bgc                         :$background-color-neutral-soft-4-dark-mode
                                                  }

        :_.kushi-switch-thumb-content          {:display                                     :none
                                                :flex-direction                              :column
                                                :jc                                          :center
                                                :ai                                          :center
                                                :w                                           :100%
                                                :h                                           :100%
                                                :.kushi-switch-thumb-label                   {:fs :$switch-thumb-label-font-size||0.3em
                                                                                              :fw :$switch-thumb-label-font-weight||$semi-bold}
                                                :.kushi-switch-thumb-icon                    {:fs :$switch-thumb-icon-font-size||0.55em
                                                                                              :fw :$switch-thumb-icon-font-weight||$size-medium}}})

      {:disabled           disabled?
       :role               :switch
       :aria-checked       (if on? true false)
       }

      (when disabled? {:data-ks-inert ""})
      
      (domo/mouse-down-a11y #(when-not disable-events? (toggle-switch %)))

      &attrs
      
      {:class (some->> shape util/as-str (str "shape-"))})

     (let [thumb-props      
           (!? (merge {:surface  :minimal-light-mode
                       :inert    true
                       :shape    shape
                       :position :absolute}
                      thumb-props
                      (when rounded-shape?
                        {:style (merge {:border-radius (str "calc(var(--shape-" (util/as-str shape) ") - var(--track-inset-gap))")}
                                       (some-> thumb-props
                                               :style
                                               (util/maybe map?)))})))
           thumb-icon-props (merge {:icon-filled true} thumb-icon-props)]

       

       [thumb
        thumb-props
        (when label-on-thumb? 
          [:<> 
           [:div {:class [:kushi-switch-thumb-content
                          :kushi-switch-thumb-label
                          :kushi-switch-thumb-label-on]}
            thumb-label-on]
           [:div {:class [:kushi-switch-thumb-content
                          :kushi-switch-thumb-label
                          :kushi-switch-thumb-label-off]}
            thumb-label-off]])

        (when icon-on-thumb? 
          [:<> 
           [:div {:class [:kushi-switch-thumb-content
                          :kushi-switch-thumb-icon
                          :kushi-switch-thumb-icon-on]}
            [icon thumb-icon-props thumb-icon-on]]
           [:div {:class [:kushi-switch-thumb-content
                          :kushi-switch-thumb-icon
                          :kushi-switch-thumb-icon-off]}
            [icon thumb-icon-props thumb-icon-off]]])])]))
