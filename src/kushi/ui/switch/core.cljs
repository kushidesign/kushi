(ns kushi.ui.switch.core
  (:require
   [kushi.core :refer (sx defcss merge-attrs)]
   [domo.core :as domo]
   [kushi.ui.util :as util]
   [kushi.ui.core :refer (opts+children)]
   [kushi.ui.shared.theming :refer [data-kui- get-variants hue-style-map]]))

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
  {:summary ["Switches are used to toggle an individual option on or off."]
   :desc ["Switches can be custom styled via a variety of tokens in your theme."
          :br
          :br "`--switch-width-ratio`"
          :br "Setting this to 1.5 will result in a switch that has the aspect ratio of 1.5:1 (width:height). The default value is 2."
          :br
          :br "`--switch-border-width`"
          :br "The default value is `2px`. If customizing the value, it is recommended to use a `px` or `rem` value, especially if you are using the `:-track-content-on` or `:-track-content-off` options."
          :br
          :br "`--switch-border-color`"
          :br "The default value is `transparent`, which will read as \"padding\" between the switch \"thumb\" and the switch \"track\", as by default the switch will have a solid background color in both the on and off states. You can supply a color value which will result in more of an \"outlined\" styling."
          :br
          :br "`--switch-thumb-scale-factor`"
          :br "The default value is `1`. Setting this to a value greater than 1 will result in the thumb height being greater than the track height."
          :br
          ;; TODO add documentation for each token
          :br "The following tokens control the background color of the switch:"
          :br
          :br "`--switch-off-background-color`"
          :br "`--switch-off-background-color-hover`"
          :br "`--switch-on-background-color`"
          :br "`--switch-on-background-color-hover`"
          :br "`--switch-on-accent-background-color`"
          :br "`--switch-on-accent-background-color-hover`"
          :br "`--switch-on-positive-background-color`"
          :br "`--switch-on-positive-background-color-hover`"
          :br "`--switch-on-warning-background-color`"
          :br "`--switch-on-warning-background-color-hover`"
          :br "`--switch-on-negative-background-color`"
          :br "`--switch-on-negative-background-color-hover`"
          :br
          :br
          "Each of the above has a corresponding token for dark-mode:"
          :br
          :br "`--switch-off-background-color-dark-mode`"
          :br "`--switch-off-background-color-hover-dark-mode`"
          :br "`--switch-on-background-color-dark-mode`"
          :br "`--switch-on-background-color-hover-dark-mode`"
          :br "`--switch-on-accent-background-color-dark-mode`"
          :br "`--switch-on-accent-background-color-hover-dark-mode`"
          :br "`--switch-on-positive-background-color-dark-mode`"
          :br "`--switch-on-positive-background-color-hover-dark-mode`"
          :br "`--switch-on-warning-background-color-dark-mode`"
          :br "`--switch-on-warning-background-color-hover-dark-mode`"
          :br "`--switch-on-negative-background-color-dark-mode`"
          :br "`--switch-on-negative-background-color-hover-dark-mode`"
          ]
   :opts '[{:name    on?
            :pred    boolean?
            :default false
            :desc    "Use to control the initial on/off state of the switch"}
           {:name    disable-events?
            :pred    boolean?
            :default false
            :desc    "Set this to true if you would like to control the state of the switch in a reactive manner via the `:-on?` option"}
           {:name    thumb-attrs
            :pred    map?
            :default nil
            :desc    "HTML attributes map applied to the inner element, commonly refered to as the \"handle\" or \"thumb\" of the switch."}
           {:name    thumb-content-off
            :pred    #{string? vector?}
            :default nil
            :desc    "String or element that will be placed in center of thumb, when in the \"off\" position"}
           {:name    thumb-content-on
            :pred    #{string? vector?}
            :default nil
            :desc    "String or element that will be placed in center of thumb, when in the \"on\" position"}
           {:name    track-content-off
            :pred    #{string? vector?}
            :default nil
            :desc    "String or element that will be placed in the track, when in the \"off\" position"}
           {:name    track-content-on
            :pred    #{string? vector?}
            :default nil
            :desc    "String or element that will be placed in the track, when in the \"on\" position"}
           ]}
  [& args]
  (let [[opts attrs & _]
        (opts+children args)

        {:keys [disable-events?
                on?
                colorway
                thumb-attrs
                thumb-content-off
                thumb-content-on
                track-content-on
                track-content-off]}
        opts

        {:keys             [shape surface]
         semantic-colorway :colorway}
        (get-variants opts)

        hue-style-map                 
        (when-not semantic-colorway 
          (some-> colorway
                  hue-style-map))

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

      (some-> (or semantic-colorway
                  (when hue-style-map ""))
              (data-kui- :colorway))

      {:disabled         disabled?
       :role             :switch
       :aria-checked     (if on? true false)
       :data-kui-ia      ""
       :data-kui-surface "solid"}
      
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
       (sx
        ".kushi-switch-thumb"
        [:--width :$thumb-height]
        :.transition
        :transition-duration--$xxfast
        :.pill
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
