(ns kushi.ui.switch
  (:require
   [kushi.core :refer (sx defcss merge-attrs)]
   [domo.core :as domo]
   [kushi.ui.util :as util]
   [kushi.ui.core :refer (extract)]
   [kushi.ui.shared.theming :refer [data-ks- get-variants]]))

(defcss "@layer kushi-ui-styles .kushi-switch-track-content"
  :.flex-row-c
  :.semi-bold
  :.transition
  :.xxfast!
  :>*:fs--0.40em
  :c--white
  [:w "calc(100% - 50% + var(--switch-border-width))"]
  )

(defcss "@layer kushi-ui-styles .kushi-switch-thumb-content"
  :c--$neutral-foreground
  :d--none 
  :w--100%
  :h--100% 
  :.flex-col-c 
  :ai--c)

(defn- toggle-switch [%]
  (let [node* (domo/et %)
        node  (if (domo/has-class? node* "kushi-switch")
                node*
                (domo/nearest-ancestor node* ".kushi-switch"))]
    (domo/toggle-boolean-attribute node "aria-checked")))

(defn- track-content [opts x]
  (when x
    [:div opts (if (string? x) [:span x] x)]))

(defn switch
  {:summary "Switches are used to toggle an individual option on or off."
   :desc "Switches can be custom styled via a variety of tokens in your theme.
          
          `--switch-width-ratio`
          Setting this to 1.5 will result in a switch that has the aspect ratio
          of 1.5:1 (width:height). The default value is 2.
          
          `--switch-border-width`
          The default value is `2px`. If customizing the value, it is
          recommended to use a `px` or `rem` value, especially if you are using
          the `:track-content-on` or `:track-content-off` options.
          
          `--switch-border-color`
          The default value is `transparent`, which will read as \"padding\"
          between the switch \"thumb\" and the switch \"track\", as by default
          the switch will have a solid background color in both the on and off
          states. You can supply a color value which will result in more of an
          \"outlined\" styling.
          
          `--switch-thumb-scale-factor`
          The default value is `1`. Setting this to a value greater than 1 will
          result in the thumb height being greater than the track height."
          
          
   :opts '[{:name    on?
            :schema    boolean?
            :default false
            :desc    "Use to control the initial on/off state of the switch"}
           {:name    disable-events?
            :schema    boolean?
            :default false
            :desc    "Set this to true if you would like to control the state of
                      the switch in a reactive manner via the `:on?` option"}
           {:name    thumb-attrs
            :schema    map?
            :default nil
            :desc    "HTML attributes map applied to the inner element, commonly
                      refered to as the \"handle\" or \"thumb\" of the switch."}
           {:name    thumb-content-off
            :schema    #{string? vector?}
            :default nil
            :desc    "String or element that will be placed in center of thumb,
                      when in the \"off\" position"}
           {:name    thumb-content-on
            :schema    #{string? vector?}
            :default nil
            :desc    "String or element that will be placed in center of thumb,
                      when in the \"on\" position"}
           {:name    track-content-off
            :schema    #{string? vector?}
            :default nil
            :desc    "String or element that will be placed in the track, when
                      in the \"off\" position"}
           {:name    track-content-on
            :schema    #{string? vector?}
            :default nil
            :desc    "String or element that will be placed in the track, when
                      in the \"on\" position"}
           
           {:name    colorway
            :schema    #{:neutral :accent :positive :negative :warning}
            :default nil
            :desc    "Colorway of the switch. Can also be a named color from
                      Kushi's design system, e.g `:red`, `:purple`, `:gold`,
                      etc."}
           ]}
  [& args]
  (let [[opts attrs & _]
        (extract args)

        {:keys [disable-events?
                on?
                colorway
                thumb-attrs
                thumb-content-off
                thumb-content-on
                track-content-on
                track-content-off]}
        opts


        disabled?                  
        (util/html-attr? opts :disabled)
        ]
    [:button
     (merge-attrs
      (sx
       ".kushi-switch"
       {:--thumb-height "calc(var(--switch-thumb-scale-factor, 1) * (1em - (var(--switch-border-width) * 2)))"
        :--height       :1em}
       :.pill
       :.flex-row-fs
       :.no-shrink
       :.transition
       :transition-duration--$xxfast
       :position--relative
       :cursor--pointer

       ["_.kushi-switch-thumb-content-off:display" :flex]
       ["_.kushi-switch-thumb-content-on:display" :none]
       [".kushi-switch[aria-checked='true']_.kushi-switch-thumb-content-on:display" :flex]
       [".kushi-switch[aria-checked='true']_.kushi-switch-thumb-content-off:display" :none]

       [".kushi-switch_.kushi-switch-track-content-on:opacity" 0]
       [".kushi-switch[aria-checked='true']_.kushi-switch-track-content-off:opacity" 0]
       [".kushi-switch[aria-checked='true']_.kushi-switch-track-content-on:opacity" 1]

       [".kushi-switch[aria-checked='true']:jc" :fe]
       [:w "calc((var(--height) *  max(1.25, var(--switch-width-ratio, 2))) - var(--switch-border-width))"]
       :h--$height
       :bc--$switch-border-color
       :bw--$switch-border-width
       :bs--solid

       [".kushi-switch[aria-checked='false']:bgc" :$switch-off-background-color]
       [".kushi-switch[aria-checked='false']:hover:bgc" :$switch-off-background-color-hover]
       
       )

      {:disabled         disabled?
       :role             :switch
       :aria-checked     (if on? true false)
       :data-ks-ia       ""
       :data-ks-colorway colorway
       :data-ks-surface  "solid"}
      
      (domo/mouse-down-a11y #(when-not disable-events? (toggle-switch %)))

      attrs)

     [track-content
      (sx ".kushi-switch-track-content-on"
          :.absolute-inline-start-inside
          :.kushi-switch-track-content)
      track-content-on]

     [track-content
      (sx ".kushi-switch-track-content-off"
          :.absolute-inline-end-inside
          :.kushi-switch-track-content)
      track-content-off]

     [:div
      (merge-attrs
       {:data-ks-contour  :pill
        :data-ks-colorway colorway
        }
       (sx
        ".kushi-switch-thumb"
        :.transition
        [:--width :$thumb-height]
        :transition-duration--$xxfast
        :border-color--currentColor
        ["has-ancestor(.kushi-switch[aria-checked='false']):border-color" "color-mix(in srgb, currentColor, transparent)"]
        :cursor--pointer
        :bgc--$transparent-white-100
        :box-shadow--0:2px:6px:0:$transparent-black-15
        [:transform "translate(0, -50%)"]
        ["has-ancestor(.kushi-switch[aria-checked='true']):inset-inline-start"
         "calc(100% - var(--width))"]
        ["has-ancestor(.kushi-switch[disabled]):cursor"
         :not-allowed]
        :position--absolute
        :top--50%
        :inset-inline-start--0
        :h--$thumb-height
        :w--$width)
       thumb-attrs)
      [:div (sx ".kushi-switch-thumb-content-on"
                :.kushi-switch-thumb-content
                ["has-ancestor(.kushi-switch[disabled]):cursor" :not-allowed])
       thumb-content-on]
      [:div (sx ".kushi-switch-thumb-content-off"
                :.kushi-switch-thumb-content
                ["has-ancestor(.kushi-switch[disabled]):cursor" :not-allowed])
       thumb-content-off]
      ]]))
