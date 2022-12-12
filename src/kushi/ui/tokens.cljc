(ns ^:dev/always kushi.ui.tokens
  (:require
   [kushi.color :refer [semantic-aliases]]))

(def design-tokens
  [
    ;; Typography
    ;; ------------------------------------------------------

    ;; font-family
   :--sans-serif-font-stack                  "Inter, sys, sans-serif"
   :--serif-font-stack                       "Times, serif"
   :--code-font-stack                        "Fira Code, monospace"

   ;; code
   :--code-font-size                         :0.9em
   :--code-padding-inline                    :0.2em
   :--code-padding-block                     :0.08em
   :--code-border-radius                     :3px
   :--code-background-color                  :--gray100
   :--code-background-color-inverse          :--gray750
   :--code-color-inverse                     :--gray50

    ;; Intended for css props: font-weight
   :--text-thin                              100
   :--text-extra-light                       200
   :--text-light                             300
   :--text-normal                            400
   :--text-wee-bold                          500
   :--text-semi-bold                         600
   :--text-bold                              700
   :--text-extra-bold                        800
   :--text-heavy                             900

    ;; Intended for css props: font-size
   :--text-xxxsmall                          :0.625rem
   :--text-xxsmall                           :0.7rem
   :--text-xsmall                            :0.775rem
   :--text-small                             :0.875rem
   :--text-medium                            :1rem
   :--text-large                             :1.25rem
   :--text-xlarge                            :1.5rem
   :--text-xxlarge                           :1.85rem
   :--text-xxxlarge                          :2.25rem

    ;; Intended for css props: letterspacing
   :--text-xxxtight                          :-0.09em
   :--text-xxtight                           :-0.06em
   :--text-xtight                            :-0.03em
   :--text-tight                             :-0.01em
   :--text-loose                             :0.04em
   :--text-xloose                            :0.08em
   :--text-xxloose                           :0.12em
   :--text-xxxloose                          :0.16em

   ;; Intended for css props: border-width for inputs
   :--input-border-weight-thin               :0.05em
   :--input-border-weight-extra-light        :0.07em
   :--input-border-weight-light              :0.09em
   :--input-border-weight-normal             :0.1em
   :--input-border-weight-wee-bold           :0.12em
   :--input-border-weight-semi-bold          :0.135em
   :--input-border-weight-bold               :0.165em
   :--input-border-weight-extra-bold         :0.195em
   :--input-border-weight-heavy              :0.21em

   ;; Intended for css props: border-*, general
   :--border-width                           :1px
   :--border-style                           :solid

    ;; Intended for overlay placement
   :--overlay-placement-inline-offset        :12px
   :--overlay-placement-block-offset         :6px


   ;; Buttons
   ;; ------------------------------------------------------
   :--button-padding-inline-ems              :1.2em
   :--button-padding-block-ems               :0.8em
   :--button-with-icon-padding-inline-offset :1em
   :--button-border-width                    :2px

   ;; Tags
   :--tag-border-width                       :1px

    ;; Tooltips
    ;; ------------------------------------------------------
   :--tooltip-padding-inline                 :1.25em
   :--tooltip-padding-block                  :0.55em
   :--tooltip-border-radius                  :0.5rem
   :--tooltip-font-size                      :--text-xsmall
   :--tooltip-color                          :white
   :--tooltip-background-color               :black

    ;; Material UI icons
    ;; ------------------------------------------------------
   :--mui-icon-relative-font-size            :inherit

    ;; General icons
    ;; ------------------------------------------------------
   :--icon-enhancer-inline-gap-ems           :0.25em

    ;; Intended for css props: border-radius
    ;; ------------------------------------------------------
   :--rounded                                :0.5rem
   :--border-radius                          :0.5rem
   :--border-weight                          :1px

    ;; Intended for css props: box-shadow
    ;; ------------------------------------------------------
   :--elevated                               "rgb(0 0 0 / 4%) 12px 10px 16px 2px, rgb(0 0 0 / 5%) 0px 2px 9px 0px;"

    ;; Intended for css animations and transitions
    ;; ------------------------------------------------------
   :--timing-linear-curve                    "cubic-bezier(0 0 1 1)"
   :--timing-ease-out-curve                  "cubic-bezier(.2, .8, .4, 1)"
   :--timing-ease-in-curve                   "cubic-bezier(.8, .2, .6, 1)"
   :--timing-ease-in-out-curve               "cubic-bezier(0.4, 0, 0.2, 1)"

   :--duration-instant                       :0ms
   :--duration-fast                          :100ms
   :--duration-normal                        :200ms
   :--duration-slow                          :500ms
   :--duration-extra-slow                    :1s
   :--duration-super-slow                    :2s
   :--duration-ultra-slow                    :4s

   ;; Components
   :--kushi-collapse-transition-duration :--duration-slow
   ])

