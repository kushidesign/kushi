(ns kushi.ui.tokens)

(def global-tokens
  {
    ;; Color
    ;; Intended for css props that assign color
    ;; ------------------------------------------------------
    :--white           :#FFFFFF
    :--gray50          :#F6F6F6
    :--gray100         :#EEEEEE
    :--gray200         :#E2E2E2
    :--gray300         :#CBCBCB
    :--gray400         :#AFAFAF
    :--gray500         :#6B6B6B
    :--gray600         :#545454
    :--gray700         :#333333
    :--gray800         :#1F1F1F
    :--gray900         :#141414
    :--black           :#000000
    :--platinum50      :#F4FAFB
    :--platinum100     :#EBF5F7
    :--platinum200     :#CCDFE5
    :--platinum300     :#A1BDCA
    :--platinum400     :#8EA3AD
    :--platinum500     :#6C7C83
    :--platinum600     :#556268
    :--platinum700     :#394145
    :--platinum800     :#142328
    :--red50           :#FFEFED
    :--red100          :#FED7D2
    :--red200          :#F1998E
    :--red300          :#E85C4A
    :--red400          :#E11900
    :--red500          :#AB1300
    :--red600          :#870F00
    :--red700          :#5A0A00
    :--orange50        :#FFF3EF
    :--orange100       :#FFE1D6
    :--orange200       :#FABDA5
    :--orange300       :#FA9269
    :--orange400       :#FF6937
    :--orange500       :#C14F29
    :--orange600       :#9A3F21
    :--orange700       :#672A16
    :--yellow50        :#FFFAF0
    :--yellow100       :#FFF2D9
    :--yellow200       :#FFE3AC
    :--yellow300       :#FFCF70
    :--yellow400       :#FFC043
    :--yellow500       :#BC8B2C
    :--yellow600       :#996F00
    :--yellow700       :#674D1B
    :--green50         :#E6F2ED
    :--green100        :#ADDEC9
    :--green200        :#66D19E
    :--green300        :#06C167
    :--green400        :#048848
    :--green500        :#03703C
    :--green600        :#03582F
    :--green700        :#10462D
    :--blue50          :#EFF3FE
    :--blue100         :#D4E2FC
    :--blue200         :#A0BFF8
    :--blue300         :#5B91F5
    :--blue400         :#276EF1
    :--blue500         :#1E54B7
    :--blue600         :#174291
    :--blue700         :#102C60
    :--cobalt50        :#EBEDFA
    :--cobalt100       :#D2D7F0
    :--cobalt200       :#949CE3
    :--cobalt300       :#535FCF
    :--cobalt400       :#0E1FC1
    :--cobalt500       :#0A1899
    :--cobalt600       :#081270
    :--cobalt700       :#050C4D
    :--purple50        :#F3F1F9
    :--purple100       :#E3DDF2
    :--purple200       :#C1B4E2
    :--purple300       :#957FCE
    :--purple400       :#7356BF
    :--purple500       :#574191
    :--purple600       :#453473
    :--purple700       :#2E224C
    :--brown50         :#F6F0EA
    :--brown100        :#EBE0DB
    :--brown200        :#D2BBB0
    :--brown300        :#B18977
    :--brown400        :#99644C
    :--brown500        :#744C3A
    :--brown600        :#5C3C2E
    :--brown700        :#3D281E

    ;; Typography
    ;; ------------------------------------------------------

    ;; font-family
    :--sans-serif-stack  "Inter, sys, sans-serif"
    :--serif-stack       "Times, serif"

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

    ;; Intended for css props: font-size
    :--text-mini       :0.625rem
    :--text-xxsmall    :0.7rem
    :--text-xsmall     :0.775rem
    :--text-small      :0.875rem
    :--text-medium     :1rem
    :--text-large      :1.25rem
    :--text-xlarge     :1.5rem
    :--text-xxlarge    :1.85rem
    :--text-huge       :2.25rem

    ;;  Material UI icons
    ;; ------------------------------------------------------
    :--mui-icon-relative-font-size :inherit
    :--mui-icon-margin-inline-ems :0.333em

    ;; Intended for css props: border-radius
    ;; ------------------------------------------------------
    :--rounded         :0.3rem

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
  {:--primary-a   :--black
   :--primary-b   :--white
   :--primary     :--black
   :--primary50   :--gray50
   :--primary100  :--gray100
   :--primary200  :--gray200
   :--primary300  :--gray300
   :--primary400  :--gray400
   :--primary500  :--gray500
   :--primary600  :--gray600
   :--primary700  :--gray700
   :--accent      :--blue400
   :--accent50    :--blue50
   :--accent100   :--blue100
   :--accent200   :--blue200
   :--accent300   :--blue300
   :--accent400   :--blue400
   :--accent500   :--blue500
   :--accent600   :--blue600
   :--accent700   :--blue700
   :--negative    :--red400
   :--negative50  :--red50
   :--negative100 :--red100
   :--negative200 :--red200
   :--negative300 :--red300
   :--negative400 :--red400
   :--negative500 :--red500
   :--negative600 :--red600
   :--negative700 :--red700
   :--warning     :--yellow400
   :--warning50   :--yellow50
   :--warning100  :--yellow100
   :--warning200  :--yellow200
   :--warning300  :--yellow300
   :--warning400  :--yellow400
   :--warning500  :--yellow500
   :--warning600  :--yellow600
   :--warning700  :--yellow700
   :--positive    :--green500
   :--positive50  :--green50
   :--positive100 :--green100
   :--positive200 :--green200
   :--positive300 :--green300
   :--positive400 :--green400
   :--positive500 :--green500
   :--positive600 :--green600
   :--positive700 :--green700
   :--mono100     :--white
   :--mono200     :--gray50
   :--mono300     :--gray100
   :--mono400     :--gray200
   :--mono500     :--gray300
   :--mono600     :--gray400
   :--mono700     :--gray500
   :--mono800     :--gray600
   :--mono900     :--gray700
   :--mono1000    :--black })
