(ns ^:dev/always kushi.ui.tokens)
;; TODO - describe each tokens using a map

(def design-tokens
  [

   ;; Color
   ;; ------------------------------------------------------
   :$body-color                              :$gray-950
   :$body-color-inverse                      :$gray-50

   :$body-background-color                   :white
   :$body-background-color-inverse           :$gray-1000


   ;; Neutrals
   :$neutral-fg                              :$neutral-minimal-color
   :$neutral-fg-inverse                      :$neutral-minimal-color-inverse
   :$neutral-secondary-fg                    :$gray-700
   :$neutral-secondary-fg-inverse            :$gray-300

   :$accent-fg                               :$accent-minimal-color
   :$accent-fg-inverse                       :$accent-minimal-color-inverse

   :$positive-fg                             :$positive-minimal-color
   :$positive-fg-inverse                     :$positive-minimal-color-inverse

   :$warning-fg                              :$warning-minimal-color
   :$warning-fg-inverse                      :$warning-minimal-color-inverse

   :$negative-fg                             :$negative-minimal-color
   :$negative-fg-inverse                     :$negative-minimal-color-inverse


   ;; Debugging grid
   ;; ------------------------------------------------------
   :$debug-grid-size                         :16px
   :$debug-grid-color                        "hsla(0 0% 90%)"
   :$debug-grid-color-inverse                "hsla(0 0% 25%)"


   ;; Typography
   ;; ------------------------------------------------------


   ;; font-family
   ;; TODO should this live in basetheme?
   :$sans-serif-font-stack                  "Inter, system-ui, sans-serif"
   :$serif-font-stack                       "Cormorant, Times, serif"
   :$code-font-stack                        "Fira Code, monospace"


   ;; code
   :$code-font-size                         :$small
   :$code-padding-inline                    :0.2em
   :$code-padding-block                     :0.08em
   :$code-border-radius                     :3px
   :$code-background-color                  :$gray-100
   :$code-background-color-inverse          :$gray-800
   :$code-color-inverse                     :$gray-50


   ;; Intended for css prop `font-weight`
   :$thin                                   100
   :$extra-light                            200
   :$light                                  300
   :$normal                                 400
   :$wee-bold                               500
   :$semi-bold                              600
   :$bold                                   700
   :$extra-bold                             800
   :$heavy                                  900

   :$root-font-size                   :1rem


   ;; Intended for css prop `font-size`
   :$xxxxsmall                              :0.64rem
   :$xxxsmall                               :0.67rem
   :$xxsmall                                :0.71rem
   :$xsmall                                 :0.77rem
   :$small                                  :0.86rem
   :$medium                                 :1rem
   :$large                                  :1.21rem
   :$xlarge                                 :1.485rem
   :$xxlarge                                :1.86rem
   :$xxxlarge                               :2.36rem
   :$xxxxlarge                              :3.03rem

   :$xxxxsmall-b                            :0.655rem
   :$xxxsmall-b                             :0.685rem
   :$xxsmall-b                              :0.733rem
   :$xsmall-b                               :0.805rem
   :$small-b                                :0.92rem
   :$medium-b                               :1.1rem
   :$large-b                                :1.33rem
   :$xlarge-b                               :1.655rem
   :$xxlarge-b                              :2.085rem
   :$xxxlarge-b                             :2.68rem
   :$xxxxlarge-b                            :3.475rem


   ;; Intended for css prop `letterspacing`
   :$xxxtight                               :-0.09em
   :$xxtight                                :-0.06em
   :$xtight                                 :-0.03em
   :$tight                                  :-0.01em
   :$loose                                  :0.04em
   :$xloose                                 :0.08em
   :$xxloose                                :0.12em
   :$xxxloose                               :0.16em


   ;; Intended for css props `border-width` for inputs
   :$input-border-weight-thin               :0.05em
   :$input-border-weight-extra-light        :0.07em
   :$input-border-weight-light              :0.09em
   :$input-border-weight-normal             :0.1em
   :$input-border-weight-wee-bold           :0.12em
   :$input-border-weight-semi-bold          :0.135em
   :$input-border-weight-bold               :0.165em
   :$input-border-weight-extra-bold         :0.195em
   :$input-border-weight-heavy              :0.21em


   ;; Intended for css props: border-*, general
   :$border-width                           :1px
   :$border-style                           :solid



   ;; Intended for overlay placement
   :$overlay-placement-inline-offset        :12px
   :$overlay-placement-block-offset         :6px



   ;; Buttons
   ;; ------------------------------------------------------
   :$button-padding-inline-ems              :1.2em
   :$icon-button-padding-inline-ems         :0.69em
   :$button-padding-block-ems               :0.69em
   :$button-with-icon-padding-inline-offset :0.69em
   :$button-border-width                    :1px



   ;; Tags
   :$tag-border-width                       :1px



   ;; pane - floating layer abstraction
   ;; ------------------------------------------------------

   ;; pane colors and images
   :$pane-background-color                 :$body-background-color
   :$pane-background-color-inverse         :$body-background-color-inverse
   :$pane-background-image                 :white
   :$pane-box-shadow                       :$elevated-5
   :$pane-box-shadow-inverse               :$elevated-5-inverse
   :$pane-border-width                     :0px
   :$pane-border-style                     :solid
   :$pane-border-color                     :transparent
   :$pane-border-color-inverse             :transparent

   ;; pane geometry
   :$pane-min-width                        :150px
   :$pane-min-height                       :75px
   :$pane-padding-inline                   :1em
   :$pane-padding-block                    :0.5em
   :$pane-border-radius                    :$rounded-absolute-large
   :$pane-offset                           :7px
   :$pane-viewport-padding                 :5px 
   :$pane-flip-viewport-edge-threshold     :32px 
   :$pane-auto-placement-y-threshold       :0.1 

   ;; pane choreography
   :$pane-offset-start                     "calc(var(--pane-offset) + 5px)"
   :$pane-z-index                          :auto
   :$pane-delay-duration                   :0ms
   :$pane-transition-duration              :$xfast 
   :$pane-transition-timing-function       :$timing-ease-out-curve 

   ;; pane arrows
   :$pane-arrow-inline-inset               :7px
   :$pane-arrow-block-inset                :2px
   :$pane-arrow-depth                      :7px


   ;; Modals
   ;; ------------------------------------------------------
   :$modal-border-radius                    :$pane-border-radius
   :$modal-border                           :none
   :$modal-padding                          :1.75rem
   :$modal-padding-inline                   :$modal-padding
   :$modal-padding-block                    :$modal-padding
   :$modal-backdrop-color                   :$black-transparent-50
   :$modal-margin                           :1rem
   :$modal-min-width                        :450px
   :$modal-transition-duration              :$pane-transition-duration



   ;; Tooltips
   ;; ------------------------------------------------------

   ;; tooltip colors and images
   :$tooltip-color                            :$body-color-inverse
   :$tooltip-color-inverse                    :$body-color
   :$tooltip-background-color                 :$body-background-color-inverse
   :$tooltip-background-color-inverse         :$body-background-color
   :$tooltip-background-image                 :none
   :$tooltip-box-shadow                       :none
   :$tooltip-box-shadow-inverse               :none
   :$tooltip-border-width                     :$pane-border-width
   :$tooltip-border-style                     :$pane-border-style
   :$tooltip-border-color                     :$pane-border-color
   :$tooltip-border-color-inverse             :$pane-border-color-inverse

   ;; pane typography
   :$tooltip-line-height                      1.25
   :$tooltip-font-family                      :$sans-serif-font-stack
   :$tooltip-font-size                        :$xsmall
   :$tooltip-font-weight                      :$wee-bold
   :$tooltip-text-transform                   :none

   ;; tooltip geometry
   :$tooltip-min-width                        :1rem
   :$tooltip-min-height                       :1rem
   :$tooltip-padding-inline                   :$pane-padding-inline
   :$tooltip-padding-block                    :$pane-padding-block
   :$tooltip-border-radius                    :5px
   :$tooltip-offset                           :$pane-offset
   :$tooltip-viewport-padding                 :$pane-viewport-padding 
   :$tooltip-flip-viewport-edge-threshold     :$pane-flip-viewport-edge-threshold 
   :$tooltip-auto-placement-y-threshold       :$pane-auto-placement-y-threshold 

   ;; tooltip choreography
   :$tooltip-offset-start                     :$pane-offset-start
   :$tooltip-z-index                          :$pane-z-index
   :$tooltip-delay-duration                   :550ms
   :$tooltip-text-on-click-duration           :2000ms
   :$tooltip-initial-scale                    1
   :$tooltip-transition-duration              :$pane-transition-duration 
   :$tooltip-transition-timing-function       :$pane-transition-timing-function 

   ;; tooltip arrows
   :$tooltip-arrow-inline-inset               :$pane-arrow-inline-inset
   :$tooltip-arrow-block-inset                :$pane-arrow-inline-inset
   :$tooltip-arrow-depth                      :5px





   ;; Popovers
   ;; ------------------------------------------------------

   ;; popover colors and images
   :$popover-background-color                 :$pane-background-color
   :$popover-background-color-inverse         :$pane-background-color-inverse
   :$popover-background-image                 :none
   :$popover-box-shadow                       :$pane-box-shadow
   :$popover-box-shadow-inverse               :$pane-box-shadow-inverse
   :$popover-border-width                     :1px
   :$popover-border-style                     :solid
   :$popover-border-color                     :$neutral-200
   :$popover-border-color-inverse             :$neutral-500

   ;; popover geometry
   :$popover-min-width                        :$pane-min-width
   :$popover-min-height                       :$pane-min-height
   :$popover-border-radius                    :$pane-border-radius
   :$popover-offset                           :$pane-offset
   :$popover-viewport-padding                 :$pane-viewport-padding 
   :$popover-flip-viewport-edge-threshold     :$pane-flip-viewport-edge-threshold 
   :$popover-auto-placement-y-threshold       :$pane-auto-placement-y-threshold 

   ;; popover choreography
   :$popover-offset-start                     :$pane-offset-start
   :$popover-z-index                          :$pane-z-index
   :$popover-delay-duration                   :0ms
   :$popover-initial-scale                    1
   :$popover-transition-duration              :$pane-transition-duration 
   :$popover-transition-timing-function       :$pane-transition-timing-function 
   :$popover-auto-dismiss-duration            :5000ms

   ;; popover arrows
   :$popover-arrow-inline-inset               :$pane-arrow-inline-inset
   :$popover-arrow-block-inset                :$pane-arrow-inline-inset
   :$popover-arrow-depth                      :7px

   
   ;; toasts
   ;; ------------------------------------------------------

   ;; toast colors and images
   :$toast-background-color                 :$pane-background-color
   :$toast-background-color-inverse         :$pane-background-color-inverse
   :$toast-background-image                 :none
   :$toast-box-shadow                       :$pane-box-shadow
   :$toast-box-shadow-inverse               :$pane-box-shadow-inverse
   :$toast-border-width                     :1px
   :$toast-border-style                     :solid
   :$toast-border-color                     :$gray-150
   :$toast-border-color-inverse             :$gray-700

   ;; toast geometry
   :$toast-border-radius                    :$pane-border-radius
   :$toast-slot-padding-inline              :1rem
   :$toast-slot-padding-block               :1rem
   :$toast-slot-gap                         :1rem
   :$toast-slot-z-index                     :auto

   ;; toast choreography
   :$toast-delay-duration                   :200ms
   :$toast-initial-scale                    1
   :$toast-transition-duration              :$pane-transition-duration 
   :$toast-transition-timing-function       :$pane-transition-timing-function 
   :$toast-auto-dismiss-duration            :5000ms
   

   ;; Modals
   ;; ------------------------------------------------------
   :$modal-border-radius                    :$rounded-absolute-large
   :$modal-border                           :none
   :$modal-padding                          :2rem
   :$modal-padding-block                    :$modal-padding
   :$modal-padding-inline                   :$modal-padding
   :$modal-backdrop-color                   :$black-transparent-40
   :$modal-margin                           :1rem
   :$modal-min-width                        :500px
   :$modal-transition-duration              :$xfast


   ;; Material UI icons
   ;; ------------------------------------------------------
   :$mui-icon-relative-font-size            :inherit



   ;; General icons
   ;; ------------------------------------------------------
   :$icon-enhanceable-gap                   :0.25em


   ;; Intended for css props: border-radius
   ;; ------------------------------------------------------

   ;; Absolute versions for panes, cards, etc.
   :$rounded-absolute-xxxsmall              :0.625rem         ;; 1px
   :$rounded-absolute-xxsmall               :0.125rem         ;; 2px
   :$rounded-absolute-xsmall                :0.25rem          ;; 4px
   :$rounded-absolute-small                 :0.375rem         ;; 6px
   :$rounded-absolute-medium                :0.5rem           ;; 8px
   :$rounded-absolute-large                 :0.75rem          ;; 12px
   :$rounded-absolute-xlarge                :0.1rem           ;; 16px
   :$rounded-absolute-xxlarge               :1.25rem          ;; 20px
   :$rounded-absolute-xxxlarge              :1.5625rem        ;; 25px

   ;; Relative (to type size) versions for buttons, badges
   :$rounded-xxxsmall                       :0.4375em  
   :$rounded-xxsmall                        :0.0875em  
   :$rounded-xsmall                         :0.175em   
   :$rounded-small                          :0.2625em  
   :$rounded-medium                         :0.35em    
   :$rounded-large                          :0.525em   
   :$rounded-xlarge                         :0.7em     
   :$rounded-xxlarge                        :0.875em   
   :$rounded-xxxlarge                       :1.09375em 

   :$rounded                                :$rounded-medium

   :$border-weight                          :1px


    ;; Intended for css props: box-shadow
    ;; ------------------------------------------------------

   :$elevated-0
   :none

   :$elevation-shadow-layer-1-color :$black-transparent-08
   :$elevation-shadow-layer-2-color :$black-transparent-05
   :$elevation-shadow-layer-3-color :$black-transparent-03

   :$elevation-shadow-layer-1-color-inverse :$white-transparent-08
   :$elevation-shadow-layer-2-color-inverse :$white-transparent-05
   :$elevation-shadow-layer-3-color-inverse :$white-transparent-03

   ;; maps to MUI2 level 1
   :$elevated-1
   [[:0px :3px :3px :-2px  "var(--elevation-shadow-layer-1-color)"]
    [:0px :3px :4px :0px "var(--elevation-shadow-layer-2-color)"]
    [:0px :1px :8px :0px  "var(--elevation-shadow-layer-3-color)"]]
   :$elevated-1-inverse
   [[:0px :3px :3px :-2px  "var(--elevation-shadow-layer-1-color-inverse)"]
    [:0px :3px :4px :0px "var(--elevation-shadow-layer-2-color-inverse)"]
    [:0px :1px :8px :0px  "var(--elevation-shadow-layer-3-color-inverse)"]]
   ;; "rgb(0 0 0 / 20%) 0px 2px 1px -1px, rgb(0 0 0 / 14%) 0px 1px 1px 0px, rgb(0 0 0 / 12%) 0px 1px 3px 0px"


   ;; maps to MUI2 level 3
   :$elevated-2
   [[:0px :3px :3px :-2px  "var(--elevation-shadow-layer-1-color)"]
    [:0px :3px :4px :0px "var(--elevation-shadow-layer-2-color)"]
    [:0px :1px :8px :0px  "var(--elevation-shadow-layer-3-color)"]]
   :$elevated-2-inverse
   [[:0px :3px :3px :-2px  "var(--elevation-shadow-layer-1-color-inverse)"]
    [:0px :3px :4px :0px "var(--elevation-shadow-layer-2-color-inverse)"]
    [:0px :1px :8px :0px  "var(--elevation-shadow-layer-3-color-inverse)"]]
   ;; "rgb(0 0 0 / 20%) 0px 3px 3px -2px, rgb(0 0 0 / 14%) 0px 3px 4px 0px, rgb(0 0 0 / 12%) 0px 1px 8px 0px"


   ;; maps to MUI2 level 6
   :$elevated-3
   [[:0px :3px :5px :-1px  "var(--elevation-shadow-layer-1-color)"]
    [:0px :6px :10px :0px "var(--elevation-shadow-layer-2-color)"]
    [:0px :1px :18px :0px  "var(--elevation-shadow-layer-3-color)"]]

   :$elevated-3-inverse
   [[:0px :3px :5px :-1px  "var(--elevation-shadow-layer-1-color-inverse)"]
    [:0px :6px :10px :0px "var(--elevation-shadow-layer-2-color-inverse)"]
    [:0px :1px :18px :0px  "var(--elevation-shadow-layer-3-color-inverse)"]]
   ;; "rgb(0 0 0 / 20%) 0px 3px 5px -1px, rgb(0 0 0 / 14%) 0px 6px 10px 0px, rgb(0 0 0 / 12%) 0px 1px 18px 0px"

   ;; maps to MUI2 level 8
   :$elevated-4
   [[:0px :5px :5px :-3px  "var(--elevation-shadow-layer-1-color)"]
    [:0px :8px :18px :1px "var(--elevation-shadow-layer-2-color)"]
    [:0px :6px :20px :2px  "var(--elevation-shadow-layer-3-color)"]]
   :$elevated-4-inverse
   [[:0px :5px :5px :-3px  "var(--elevation-shadow-layer-1-color-inverse)"]
    [:0px :8px :18px :1px "var(--elevation-shadow-layer-2-color-inverse)"]
    [:0px :6px :20px :2px  "var(--elevation-shadow-layer-3-color-inverse)"]]
   ;; "rgb(0 0 0 / 20%) 0px 5px 5px -3px, rgb(0 0 0 / 14%) 0px 8px 10px 1px, rgb(0 0 0 / 12%) 0px 3px 14px 2px"

   ;; maps to MUI2 level 12
   :$elevated-5
   [[:0px :7px :14px :-2px  "var(--elevation-shadow-layer-1-color)"]
    [:0px :6px :26px :0px "var(--elevation-shadow-layer-2-color)"]
    [:0px :8px :27px :0px  "var(--elevation-shadow-layer-3-color)"]]

   :$elevated-5-inverse
   [[:0px :7px :14px :-2px  "var(--elevation-shadow-layer-1-color-inverse)"]
    [:0px :6px :26px :0px "var(--elevation-shadow-layer-2-color-inverse)"]
    [:0px :8px :27px :0px  "var(--elevation-shadow-layer-3-color-inverse)"]]
   ;; "rgb(0 0 0 / 20%) 0px 7px 8px -4px, rgb(0 0 0 / 14%) 0px 12px 17px 2px, rgb(0 0 0 / 12%) 0px 5px 22px 4px"


   :$elevated
   :$elevated-4

   :$elevated-inverse
   :$elevated-4-inverse

   ;; Intended for css animations and transitions
   ;; ------------------------------------------------------
   :$timing-linear-curve           "cubic-bezier(0, 0, 1, 1)"
   :$timing-ease-out-curve         "cubic-bezier(.2, .8, .4, 1)"
   :$timing-ease-out-curve-5       "cubic-bezier(.2, .8, .4, 1)"
   :$timing-ease-in-curve          "cubic-bezier(.8, .2, .6, 1)"
   :$timing-ease-in-out-curve      "cubic-bezier(0.4, 0, 0.2, 1)"
   :$timing-ease-out-curve-extreme "cubic-bezier(0.190, 0.510, 0.125, 0.905)"

   :$instant                       :0ms
   :$xxxfast                       :50ms
   :$xxfast                        :100ms
   :$xfast                         :175ms
   :$fast                          :250ms
   :$moderate                      :500ms
   :$slow                          :700ms
   :$xslow                         :1s
   :$xxslow                        :2s
   :$xxxslow                       :4s

   :$progress-animation-duration   :900ms
   :$loading-spinner-height        :0.8em


   ;; Intended for styling scrollbars with the .styled-scrollbars utility-class
   ;; ------------------------------------------------------
   :$scrollbar-thumb-color                  :$neutral-300
   :$scrollbar-thumb-color-inverse          :$neutral-700
   :$scrollbar-background-color             :$neutral-50
   :$scrollbar-background-color-inverse     :$neutral-900
   :$scrollbar-width                        :5px


   ;; Kushi UI Components (move?)
   ;; ------------------------------------------------------
   :$collapse-transition-duration :$slow

   ;; kushi.ui.input.text.core/input
   :$text-input-helper-margin-block-start      :0.3em
   :$text-input-label-inline-margin-inline-end :0.7em
   :$text-input-label-block-margin-block-end   :0.4em

   ;; Remove wrapper from this
   :$text-input-border-intensity                     :50%
   :$text-input-border-intensity-inverse             :55%
   :$text-input-border-radius                        :0.3em


   ;; Switches
   ;; ------------------------------------------------------
   :$switch-thumb-scale-factor
   1

   :$switch-width-ratio
   2

   :$switch-border-color
   :transparent

   :$switch-border-width
   :2px

   ;; off/unchecked
   :$switch-off-background-color
   :$neutral-400

   :$switch-off-background-color-hover
   :$neutral-500

   ;; off/unchecked dark
   :$switch-off-background-color-inverse
   :$neutral-750

   :$switch-off-background-color-hover-inverse
   :$neutral-700

   ;; Neutral
   :$switch-on-background-color
   :$neutral-700

   :$switch-on-background-color-hover
   :$neutral-750

   :$switch-thumb-on-neutral-color
   :$neutral-minimal-color

   :$switch-thumb-on-neutral-color-hover
   :$neutral-minimal-color-hover

   ;; Accent
   :$switch-on-accent-background-color
   :$accent-500

   :$switch-on-accent-background-color-hover
   :$accent-600

   :$switch-thumb-on-accent-color
   :$accent-minimal-color

   :$switch-thumb-on-accent-color-hover
   :$accent-minimal-color-hover

   ;; Positive
   :$switch-on-positive-background-color
   :$positive-500

   :$switch-on-positive-background-color-hover
   :$positive-600

   :$switch-thumb-on-positive-color
   :$positive-minimal-color

   :$switch-thumb-on-positive-color-hover
   :$positive-minimal-color-hover

   ;; Warning
   :$switch-on-warning-background-color
   :$warning-550

   :$switch-on-warning-background-color-hover
   :$warning-650

   :$switch-thumb-on-warning-color
   :$warning-minimal-color

   :$switch-thumb-on-warning-color-hover
   :$warning-minimal-color-hover

   ;; Negative
   :$switch-on-negative-background-color
   :$negative-filled-background-color

   :$switch-on-negative-background-color-hover
   :$negative-filled-background-color-hover

   :$switch-thumb-on-negative-color
   :$negative-minimal-color

   :$switch-thumb-on-negative-color-hover
   :$negative-minimal-color-hover

   ;; Neutral dark
   :$switch-on-background-color-inverse
   :$neutral-450

   :$switch-on-background-color-hover-inverse
   :$neutral-550

   :$switch-thumb-on-neutral-color-inverse
   :$neutral-minimal-color-inverse

   :$switch-thumb-on-neutral-color-hover-inverse
   :$neutral-minimal-color-hover-inverse

   ;; Accent dark
   :$switch-on-accent-background-color-inverse
   :$accent-450

   :$switch-on-accent-background-color-hover-inverse
   :$accent-500

   :$switch-thumb-on-positive-color-inverse
   :$accent-minimal-color-inverse

   :$switch-thumb-on-positive-color-hover-inverse
   :$accent-minimal-color-hover-inverse

   ;; Positive dark
   ;; :$switch-on-positive-background-color-inverse--$lime-600
   ;; :$switch-on-positive-background-color-hover-inverse--$lime-650
   :$switch-on-positive-background-color-inverse
   :$positive-500

   :$switch-on-positive-background-color-hover-inverse
   :$positive-550

   :$switch-thumb-on-positive-color-inverse
   :$positive-minimal-color-inverse

   :$switch-thumb-on-positive-color-hover-inverse
   :$positive-minimal-color-hover-inverse

   ;; Warning dark
   :$switch-on-warning-background-color-inverse
   :$warning-550

   :$switch-on-warning-background-color-hover-inverse
   :$warning-600

   :$switch-thumb-on-warning-color-inverse
   :$warning-minimal-color-inverse

   :$switch-thumb-on-warning-color-hover-inverse
   :$warning-minimal-color-hover-inverse

   ;; Negative dark
   :$switch-on-negative-background-color-inverse
   :$negative-500

   :$switch-on-negative-background-color-hover-inverse
   :$negative-550

   :$switch-thumb-on-negative-color-inverse
   :$negative-minimal-color-inverse

   :$switch-thumb-on-negative-color-hover-inverse
   :$negative-minimal-color-hover-inverse
   ])

