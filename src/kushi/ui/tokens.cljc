(ns kushi.ui.tokens)

(def global-tokens
  {
    ;; Color
    ;; Intended for css props that assign color
    ;; ------------------------------------------------------


    :--white           :#FFFFFF

    :--gray-hue        0

    :--gray50           "hsl(var(--gray-hue), 0%, 98%)"
    :--gray100          "hsl(var(--gray-hue), 0%, 95%)"
    :--gray200          "hsl(var(--gray-hue), 0%, 91%)"
    :--gray300          "hsl(var(--gray-hue), 0%, 85%)"
    :--gray400          "hsl(var(--gray-hue), 0%, 77%)"
    :--gray500          "hsl(var(--gray-hue), 0%, 68%)"
    :--gray600          "hsl(var(--gray-hue), 0%, 57%)"
    :--gray700          "hsl(var(--gray-hue), 0%, 44%)"
    :--gray800          "hsl(var(--gray-hue), 0%, 31%)"
    :--gray900          "hsl(var(--gray-hue), 0%, 20%)"
    :--gray1000         "hsl(var(--gray-hue), 0%, 8%)"

    :--black           :#000000

    :--red-hue         358
    :--red50           "hsl(var(--red-hue), 100%, 98%)"
    :--red100          "hsl(var(--red-hue), 95%, 94%)"
    :--red200          "hsl(var(--red-hue), 90%, 87%)"
    :--red300          "hsl(var(--red-hue), 85%, 76%)"
    :--red400          "hsl(var(--red-hue), 85%, 66%)"
    :--red500          "hsl(var(--red-hue), 85%, 55%)"
    :--red600          "hsl(var(--red-hue), 85%, 45%)"
    :--red700          "hsl(var(--red-hue), 90%, 37%)"
    :--red800          "hsl(var(--red-hue), 95%, 29%)"
    :--red900          "hsl(var(--red-hue), 100%, 20%)"
    :--red1000         "hsl(var(--red-hue), 100%, 11%)"

    :--orange-hue      32
    :--orange50        "hsl(var(--orange-hue), 100%, 97%)"
    :--orange100       "hsl(var(--orange-hue), 98%, 93%)"
    :--orange200       "hsl(var(--orange-hue), 93%, 87%)"
    :--orange300       "hsl(var(--orange-hue), 90%, 76%)"
    :--orange400       "hsl(var(--orange-hue), 90%, 66%)"
    :--orange500       "hsl(var(--orange-hue), 90%, 55%)"
    :--orange600       "hsl(var(--orange-hue), 90%, 45%)"
    :--orange700       "hsl(var(--orange-hue), 93%, 37%)"
    :--orange800       "hsl(var(--orange-hue), 98%, 29%)"
    :--orange900       "hsl(var(--orange-hue), 100%, 20%)"
    :--orange1000      "hsl(var(--orange-hue), 100%, 11%)"

    :--yellow-hue      52
    :--yellow50        "hsl(var(--yellow-hue), 100%, 95%)"
    :--yellow100       "hsl(var(--yellow-hue), 98%, 90%)"
    :--yellow200       "hsl(var(--yellow-hue), 95%, 83%)"
    :--yellow300       "hsl(var(--yellow-hue), 93%, 73%)"
    :--yellow400       "hsl(var(--yellow-hue), 90%, 56%)"
    :--yellow500       "hsl(var(--yellow-hue), 95%, 46%)"
    :--yellow600       "hsl(var(--yellow-hue), 93%, 42%)"
    :--yellow700       "hsl(var(--yellow-hue), 96%, 34%)"
    :--yellow800       "hsl(var(--yellow-hue), 100%, 25%)"
    :--yellow900       "hsl(var(--yellow-hue), 100%, 18%)"
    :--yellow1000      "hsl(var(--yellow-hue), 100%, 11%)"

    :--green-hue      150
    :--green50        "hsl(var(--green-hue), 100%, 96%)"
    :--green100       "hsl(var(--green-hue), 90%, 91%)"
    :--green200       "hsl(var(--green-hue), 85%, 85%)"
    :--green300       "hsl(var(--green-hue), 80%, 75%)"
    :--green400       "hsl(var(--green-hue), 75%, 61%)"
    :--green500       "hsl(var(--green-hue), 75%, 48%)"
    :--green600       "hsl(var(--green-hue), 80%, 41%)"
    :--green700       "hsl(var(--green-hue), 85%, 34%)"
    :--green800       "hsl(var(--green-hue), 90%, 27%)"
    :--green900       "hsl(var(--green-hue), 95%, 20%)"
    :--green1000      "hsl(var(--green-hue), 100%, 11%)"

    :--blue-hue      212
    :--blue50        "hsl(var(--blue-hue), 100%, 97%)"
    :--blue100       "hsl(var(--blue-hue), 98%, 93%)"
    :--blue200       "hsl(var(--blue-hue), 97%, 87%)"
    :--blue300       "hsl(var(--blue-hue), 96%, 78%)"
    :--blue400       "hsl(var(--blue-hue), 94%, 68%)"
    :--blue500       "hsl(var(--blue-hue), 92%, 59%)"
    :--blue600       "hsl(var(--blue-hue), 90%, 45%)"
    :--blue700       "hsl(var(--blue-hue), 92%, 37%)"
    :--blue800       "hsl(var(--blue-hue), 94%, 29%)"
    :--blue900       "hsl(var(--blue-hue), 97%, 20%)"
    :--blue1000      "hsl(var(--blue-hue), 100%, 11%)"

    :--purple-hue      267
    :--purple50        "hsl(var(--purple-hue), 100%, 97%)"
    :--purple100       "hsl(var(--purple-hue), 90%, 93%)"
    :--purple200       "hsl(var(--purple-hue), 85%, 87%)"
    :--purple300       "hsl(var(--purple-hue), 80%, 78%)"
    :--purple400       "hsl(var(--purple-hue), 78%, 68%)"
    :--purple500       "hsl(var(--purple-hue), 78%, 58%)"
    :--purple600       "hsl(var(--purple-hue), 77%, 50%)"
    :--purple700       "hsl(var(--purple-hue), 80%, 42%)"
    :--purple800       "hsl(var(--purple-hue), 85%, 33%)"
    :--purple900       "hsl(var(--purple-hue), 90%, 24%)"
    :--purple1000      "hsl(var(--purple-hue), 100%, 11%)"

    :--magenta-hue      324
    :--magenta50        "hsl(var(--magenta-hue), 100%, 97%)"
    :--magenta100       "hsl(var(--magenta-hue), 90%, 93%)"
    :--magenta200       "hsl(var(--magenta-hue), 85%, 87%)"
    :--magenta300       "hsl(var(--magenta-hue), 80%, 80%)"
    :--magenta400       "hsl(var(--magenta-hue), 78%, 71%)"
    :--magenta500       "hsl(var(--magenta-hue), 78%, 62%)"
    :--magenta600       "hsl(var(--magenta-hue), 77%, 48%)"
    :--magenta700       "hsl(var(--magenta-hue), 80%, 41%)"
    :--magenta800       "hsl(var(--magenta-hue), 85%, 33%)"
    :--magenta900       "hsl(var(--magenta-hue), 90%, 24%)"
    :--magenta1000      "hsl(var(--magenta-hue), 100%, 11%)"

    :--brown-hue      19
    :--brown50        "hsl(var(--brown-hue), 40%, 97%)"
    :--brown100       "hsl(var(--brown-hue), 37%, 93%)"
    :--brown200       "hsl(var(--brown-hue), 35%, 87%)"
    :--brown300       "hsl(var(--brown-hue), 32%, 78%)"
    :--brown400       "hsl(var(--brown-hue), 29%, 68%)"
    :--brown500       "hsl(var(--brown-hue), 27%, 59%)"
    :--brown600       "hsl(var(--brown-hue), 29%, 50%)"
    :--brown700       "hsl(var(--brown-hue), 32%, 42%)"
    :--brown800       "hsl(var(--brown-hue), 35%, 33%)"
    :--brown900       "hsl(var(--brown-hue), 37%, 24%)"
    :--brown1000      "hsl(var(--brown-hue), 40%, 11%)"

    ;; Typography
    ;; ------------------------------------------------------

    ;; font-family
    :--sans-serif-font-stack  "Inter, sys, sans-serif"
    :--serif-font-stack       "Times, serif"
    :--code-font-stack        "Fira Code, monospace"

    ;; Intended for css props: font-weight
    :--text-thin          100
    :--text-extra-light   200
    :--text-light         300
    :--text-normal        400
    :--text-wee-bold      500
    :--text-semi-bold     600
    :--text-bold          700
    :--text-extra-bold    800
    :--text-heavy         900

    ;; Intended for css props: border-weight
    :--input-border-weight-thin          :0.05em
    :--input-border-weight-extra-light   :0.07em
    :--input-border-weight-light         :0.09em
    :--input-border-weight-normal        :0.1em
    :--input-border-weight-wee-bold      :0.12em
    :--input-border-weight-semi-bold     :0.135em
    :--input-border-weight-bold          :0.165em
    :--input-border-weight-extra-bold    :0.195em
    :--input-border-weight-heavy         :0.21em

    ;; Intended for overlay placement
    :--overlay-placement-inline-offset   :12px
    :--overlay-placement-block-offset    :6px

    ;; Intended for css props: font-size
    :--text-xxxsmall   :0.625rem
    :--text-xxsmall    :0.7rem
    :--text-xsmall     :0.775rem
    :--text-small      :0.875rem
    :--text-medium     :1rem
    :--text-large      :1.25rem
    :--text-xlarge     :1.5rem
    :--text-xxlarge    :1.85rem
    :--text-xxxlarge   :2.25rem

    ;; Buttons
    ;; ------------------------------------------------------
    :--button-padding-inline-ems :1.2em
    :--button-padding-block-ems  :0.8em
    :--button-with-icon-padding-inline-offset :1em

    ;; Material UI icons
    ;; ------------------------------------------------------
    :--mui-icon-relative-font-size :inherit

    ;; General icons
    ;; ------------------------------------------------------
    :--icon-enhancer-inline-gap-ems :0.25em

    ;; Intended for css props: border-radius
    ;; ------------------------------------------------------
    :--rounded         :0.5rem

    ;; Intended for css props: box-shadow
    ;; ------------------------------------------------------
    :--elevated        "rgb(0 0 0 / 4%) 12px 10px 16px 2px, rgb(0 0 0 / 5%) 0px 2px 9px 0px;"

    ;; Intended for css animations and transitions
    ;; ------------------------------------------------------
    :--timing-linear-curve "cubic-bezier(0 0 1 1)"
    :--timing-ease-out-curve "cubic-bezier(.2, .8, .4, 1)"
    :--timing-ease-in-curve "cubic-bezier(.8, .2, .6, 1)"
    :--timing-ease-in-out-curve "cubic-bezier(0.4, 0, 0.2, 1)"

    :--duration-instant :0ms
    :--duration-fast  :100ms
    :--duration-normal :200ms
    :--duration-slow :500ms
    :--duration-extra-slow :1s
    :--duration-super-slow :2s
    :--duration-ultra-slow :4s})


(def alias-tokens
  ^{:title "alias colors"}
  {:--primary-a                          :--black
   :--primary-b                          :--white
   :--primary50                          :--gray50
   :--primary100                         :--gray100
   :--primary200                         :--gray200
   :--primary300                         :--gray300
   :--primary400                         :--gray400
   :--primary500                         :--gray500
   :--primary600                         :--gray600
   :--primary700                         :--gray700
   :--primary800                         :--gray800
   :--primary900                         :--gray900
   :--primary1000                        :--gray1000
   :--primary                            :--primary-a
   :--primary-hover                      :--primary
   :--primary-active                     :--primary
   :--primary-background                 :--primary100
   :--primary-background-hover           :--primary200
   :--primary-background-active          :--primary300
   :--primary-border                     :--primary
   :--primary-border-hover               :--primary
   :--primary-border-active              :--primary
   :--primary-inverse                    :--primary-b
   :--primary-hover-inverse              :--primary-inverse
   :--primary-active-inverse             :--primary-inverse
   :--primary-background-inverse         :--primary800
   :--primary-background-hover-inverse   :--primary700
   :--primary-background-active-inverse  :--primary600
   :--primary-border-inverse             :--primary-inverse
   :--primary-border-hover-inverse       :--primary-inverse
   :--primary-border-active-inverse      :--primary-inverse

   :--accent50                           :--blue50
   :--accent100                          :--blue100
   :--accent200                          :--blue200
   :--accent300                          :--blue300
   :--accent400                          :--blue400
   :--accent500                          :--blue500
   :--accent600                          :--blue600
   :--accent700                          :--blue700
   :--accent800                          :--blue800
   :--accent900                          :--blue900
   :--accent1000                         :--blue1000
   :--accent                             :--accent600
   :--accent-hover                       :--accent
   :--accent-active                      :--accent
   :--accent-background                  :--accent50
   :--accent-background-hover            :--accent100
   :--accent-background-active           :--accent200
   :--accent-border                      :--accent
   :--accent-border-hover                :--accent500
   :--accent-border-active               :--accent
   :--accent-inverse                     :--accent200
   :--accent-hover-inverse               :--accent-inverse
   :--accent-active-inverse              :--accent-inverse
   :--accent-background-inverse          :--accent800
   :--accent-background-hover-inverse    :--accent700
   :--accent-background-active-inverse   :--accent600
   :--accent-border-inverse              :--accent-inverse
   :--accent-border-hover-inverse        :--accent-inverse
   :--accent-border-active-inverse       :--accent-inverse

   :--negative50                         :--red50
   :--negative100                        :--red100
   :--negative200                        :--red200
   :--negative300                        :--red300
   :--negative400                        :--red400
   :--negative500                        :--red500
   :--negative600                        :--red600
   :--negative700                        :--red700
   :--negative800                        :--red800
   :--negative900                        :--red900
   :--negative1000                       :--red1000
   :--negative                           :--negative600
   :--negative-hover                     :--negative
   :--negative-active                    :--negative
   :--negative-background                :--negative50
   :--negative-background-hover          :--negative100
   :--negative-background-active         :--negative200
   :--negative-border                    :--negative
   :--negative-border-hover              :--negative500
   :--negative-border-active             :--negative
   :--negative-inverse                   :--negative200
   :--negative-hover-inverse             :--negative-inverse
   :--negative-active-inverse            :--negative-inverse
   :--negative-background-inverse        :--negative900
   :--negative-background-hover-inverse  :--negative800
   :--negative-background-active-inverse :--negative700
   :--negative-border-inverse            :--negative-inverse
   :--negative-border-hover-inverse      :--negative-inverse
   :--negative-border-active-inverse     :--negative-inverse

   :--warning50                          :--yellow50
   :--warning100                         :--yellow100
   :--warning200                         :--yellow200
   :--warning300                         :--yellow300
   :--warning400                         :--yellow400
   :--warning500                         :--yellow500
   :--warning600                         :--yellow600
   :--warning700                         :--yellow700
   :--warning800                         :--yellow800
   :--warning900                         :--yellow900
   :--warning1000                        :--yellow1000
   :--warning                            :--warning800
   :--warning-hover                      :--warning
   :--warning-active                     :--warning
   :--warning-background                 :--warning100
   :--warning-background-hover           :--warning200
   :--warning-background-active          :--warning300
   :--warning-border                     :--warning
   :--warning-border-hover               :--warning700
   :--warning-border-active              :--warning
   :--warning-inverse                    :--warning300
   :--warning-hover-inverse              :--warning-inverse
   :--warning-active-inverse             :--warning-inverse
   :--warning-background-inverse         :--warning900
   :--warning-background-hover-inverse   :--warning800
   :--warning-background-active-inverse  :--warning700
   :--warning-border-inverse             :--warning-inverse
   :--warning-border-hover-inverse       :--warning-inverse
   :--warning-border-active-inverse      :--warning-inverse


   :--positive50                         :--green50
   :--positive100                        :--green100
   :--positive200                        :--green200
   :--positive300                        :--green300
   :--positive400                        :--green400
   :--positive500                        :--green500
   :--positive600                        :--green600
   :--positive700                        :--green700
   :--positive800                        :--green800
   :--positive900                        :--green900
   :--positive1000                       :--green1000
   :--positive                           :--positive800
   :--positive-hover                      :--positive
   :--positive-active                     :--positive
   :--positive-background                 :--positive50
   :--positive-background-hover           :--positive100
   :--positive-background-active          :--positive200
   :--positive-border                     :--positive
   :--positive-border-hover               :--positive700
   :--positive-border-active              :--positive
   :--positive-inverse                    :--positive300
   :--positive-hover-inverse              :--positive-inverse
   :--positive-active-inverse             :--positive-inverse
   :--positive-background-inverse         :--positive900
   :--positive-background-hover-inverse   :--positive800
   :--positive-background-active-inverse  :--positive700
   :--positive-border-inverse             :--positive-inverse
   :--positive-border-hover-inverse       :--positive-inverse
   :--positive-border-active-inverse      :--positive-inverse
   })