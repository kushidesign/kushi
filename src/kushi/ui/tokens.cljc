(ns ^:dev/always kushi.ui.tokens)

;; TODO
;; Use a vector of maps like this map:
;; {:name         "neutral-secondary-fg"
;;  :desc         {:en "Foreground text that is slightly de-emphasized (such as text input field helper text)."}
;;  :value        :$gray-700
;;  :added        "1.0"
;;  :categories   ["Color" "Typography"]
;;  :tags         ["label" "de-emphasis"]
;;  :family       :font-weight  ;; :code :font-weight :font-size :letter-spacing etc (could be nil)
;;  :alias-token? <reactive based on :value>
;;  :provenance   {:namespace 'kushi.ui.tokens :added "1.0"}

;;  TODO need some kind of curve fn which creates a scale of values, e.g. more precise at beginning with larger jumps towards end
;;  -- we would only use this :suggested for like padding or similar
;;  -- or infer it in general based on css-prop?
;;  :suggested    {"em" [{:min 0 :max 4 :step 0.05}
;;                       {:min 0 :max 12 :step 0.5}]
;;                 "px" {:min 0 :max 30}}}

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

   :$kushi-root-font-size                   :1rem

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
   :$icon-button-padding-inline-ems         :0.8em
   :$button-padding-block-ems               :0.8em
   :$button-with-icon-padding-inline-offset :0.8em
   :$button-border-width                    :1.5px

   ;; Tags
   :$tag-border-width                       :1px


   ;; Tooltips
   ;; ------------------------------------------------------
   :$tooltip-arrow-depth                    :5px
   :$tooltip-arrow-x-offset                 :7px
   :$tooltip-arrow-y-offset                 :2px

   :$tooltip-padding-inline                 :1.25em
   :$tooltip-padding-block                  :0.65em
   :$tooltip-border-radius                  :0.5rem
   :$tooltip-line-height                    1.25
   :$tooltip-font-family                    :$sans-serif-font-stack
   :$tooltip-font-size                      :$xsmall
   :$tooltip-font-weight                    :$wee-bold
   :$tooltip-color                          :white
   :$tooltip-color-inverse                  :black
   :$tooltip-background-color               :black
   :$tooltip-background-color-inverse       :white
   :$tooltip-text-transform                 :none
   :$tooltip-offset                         :10px
   :$tooltip-reveal-on-click-duration       :2000ms

   ;; Modals
   ;; ------------------------------------------------------
   :$modal-border-radius                    :$rounded-medium
   :$modal-border                           :none
   :$modal-padding-block                    :2rem
   :$modal-padding-inline                   :2rem
   :$modal-backdrop-color                   :$black-transparent-50
   :$modal-margin                           :1rem
   :$modal-min-width                        :500px


   ;; Material UI icons
   ;; ------------------------------------------------------
   :$mui-icon-relative-font-size            :inherit


   ;; General icons
   ;; ------------------------------------------------------
   :$icon-enhanceable-gap                   :0.25em


   ;; Intended for css props: border-radius
   ;; ------------------------------------------------------
   :$rounded-xxxsmall                       :0.625rem         ;; 1px
   :$rounded-xxsmall                        :0.125rem         ;; 2px
   :$rounded-xsmall                         :0.25rem          ;; 4px
   :$rounded-small                          :0.375rem         ;; 6px
   :$rounded-medium                         :0.5rem           ;; 8px
   :$rounded-large                          :0.75rem          ;; 12px
   :$rounded-xlarge                         :0.1rem           ;; 16px
   :$rounded-xxlarge                        :1.25rem          ;; 20px
   :$rounded-xxxlarge                       :1.5625rem        ;; 25px
   :$rounded                                :0.4em

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
   :$timing-ease-in-curve          "cubic-bezier(.8, .2, .6, 1)"
   :$timing-ease-in-out-curve      "cubic-bezier(0.4, 0, 0.2, 1)"

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
   :$kushi-collapse-transition-duration :$slow

   ;; kushi.ui.input.text.core/input
   :$kushi-text-input-helper-margin-block-start :0.3em
   :$kushi-text-input-label-inline-margin-inline-end :0.7em
   :$kushi-text-input-label-block-margin-block-end :0.4em


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
  ;;  :$switch-on-positive-background-color-inverse--$lime-600
  ;;  :$switch-on-positive-background-color-hover-inverse--$lime-650
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

