(ns kushi.ui.dom.pane.styles
  (:require
   [goog.string]
   [kushi.core :refer (defcss)]))

(defcss "@layer kushi-ui-shared .kushi-pane"
  :position--fixed
  :border-width--$pane-border-width
  :border-style--$pane-border-style
  :border-color--$pane-border-color
  :dark:border-color--$pane-border-color-inverse
  :box-shadow--$pane-box-shadow
  :dark:box-shadow--$pane-box-shadow-inverse
  :border-radius--$pane-border-radius
  :transition-property--opacity|translate|scale|transform
  :.transition
  :transition-duration--$pane-transition-duration
  :transition-timing-function--$pane-transition-timing-function
  ;; :scale--1
  :scale--$pane-initial-scale
  :transition-delay--$pane-delay-duration
  :zi--$pane-z-index
  :top--0
  :left--0
  :bgc--$pane-background-color
  :dark:bgc--$pane-background-color-inverse
  :w--max-content
  :h--max-content
  :p--$pane-padding-block:$pane-padding-inline)

;; TODO maybe move to tooltip.styles
(defcss "@layer kushi-ui-shared .kushi-tooltip"
  :.kushi-pane
  :border-width--$tooltip-border-width
  :border-style--$tooltip-border-style
  :border-color--$tooltip-border-color
  :dark:border-color--$tooltip-border-color-inverse
  :box-shadow--$tooltip-box-shadow||none
  :dark:box-shadow--$tooltip-box-shadow-inverse||none
  :fs--$tooltip-font-size
  :fw--$tooltip-font-weight
  :line-height--$tooltip-line-height
  :border-radius--$tooltip-border-radius
  :transition-duration--$tooltip-transition-duration
  :transition-timing-function--$tooltip-transition-timing-function
  :scale--$tooltip-initial-scale
  :transition-delay--$tooltip-delay-duration
  :zi--$tooltip-z-index
  :c--$tooltip-color
  :dark:c--$tooltip-color-inverse
  :bgc--$tooltip-background-color
  :dark:bgc--$tooltip-background-color-inverse
  :p--$tooltip-padding-block:$tooltip-padding-inline
  ;; span.code
  :_span.code:fs--$tooltip-font-size
  :dark:_span.code:bgc--$code-background-color
  :dark:_span.code:c--$code-color
  :_span.code:bgc--$code-background-color-inverse
  :_span.code:c--$code-color-inverse)

;; TODO maybe move to popover.styles
(defcss "@layer kushi-ui-shared .kushi-popover"
  :.kushi-pane
  :p--0
  :min-width--$popover-min-width
  :min-height--$popover-min-height
  :border-width--$popover-border-width
  :border-style--$popover-border-style
  :border-color--$popover-border-color
  :dark:border-color--$popover-border-color-inverse
  :box-shadow--$popover-box-shadow
  :dark:box-shadow--$popover-box-shadow-inverse
  :border-radius--$popover-border-radius
  :transition-duration--$popover-transition-duration
  :transition-timing-function--$popover-transition-timing-function
  :scale--$popover-initial-scale
  :transition-delay--$popover-delay-duration
  :zi--$popover-z-index
  :c--$popover-color
  :bgc--$popover-background-color
  :dark:bgc--$popover-background-color-inverse
  [:--_auto-dismiss-duration :$popover-auto-dismiss-duration])

;; TODO maybe move to toast.styles
(defcss "@layer kushi-ui-shared .kushi-toast-slot"
  :.flex-col-c
  :transition-duration--$toast-transition-duration||$pane-transition-duration
  ;; :.xxxslow
  :transition-property--width|height
  :gap--$toast-slot-gap||1rem
  :w--fit-content
  :h--0
  [:--_pb :$toast-slot-padding-block||1rem]
  [:--_pi :$toast-slot-padding-inline||1rem]
  :zi--$toast-slot-z-index
  
  ;; leave these out for now
  ;; ["[data-kushi-ui-toast-slot='left']:p" :0:0:0:$_pi]
  ;; ["[data-kushi-ui-toast-slot='left-top']:p" :_$pb:0:0:$_pi]
  ;; ["[data-kushi-ui-toast-slot='top-left-corner']:p" :_$pb:0:0:$_pi]
  ;; ["[data-kushi-ui-toast-slot='top-left']:p" :$_pb:0:0:$_pi]
  ;; ["[data-kushi-ui-toast-slot='top']:p" :$_pb:0:0:0]
  ;; ["[data-kushi-ui-toast-slot='top-right']:p" :$_pb:$_pi:0:0]
  ;; ["[data-kushi-ui-toast-slot='top-right-corner']:p" :$_pb:$_pi:0:0]
  ;; ["[data-kushi-ui-toast-slot='right-top']:p" :$_pb:$_pi:0:0]
  ;; ["[data-kushi-ui-toast-slot='right']:p" :0:$_pi:0:0]
  ;; ["[data-kushi-ui-toast-slot='right-bottom']:p" :0:0:$_pb:0]
  ;; ["[data-kushi-ui-toast-slot='bottom-right-corner']:p" :0:$_pi:$_pb:0]
  ;; ["[data-kushi-ui-toast-slot='bottom-right']:p" :0:$_pi:$_pb:0]
  ;; ["[data-kushi-ui-toast-slot='bottom']:p" :0:0:$_pb:0]
  ;; ["[data-kushi-ui-toast-slot='bottom-left']:p" :0:0:$_pb:$_pi]
  ;; ["[data-kushi-ui-toast-slot='bottom-left-corner']:p" :0:0:$_pb:$_pi]
  ;; ["[data-kushi-ui-toast-slot='left-bottom']:p" :0:0:$_pb:$_pi]
  )

(defcss "@layer kushi-ui-shared .kushi-toast"
  :.kushi-pane
  :position--relative!important ;; <- do we need this !important?
  :p--0
  :translate--$_tx:$_ty
  :border-width--$toast-border-width
  :border-style--$toast-border-style
  :border-color--$toast-border-color
  :dark:border-color--$toast-border-color-inverse
  :box-shadow--$toast-box-shadow
  :dark:box-shadow--$toast-box-shadow-inverse
  :border-radius--$toast-border-radius
  :transition-duration--$toast-transition-duration
  :transition-timing-function--$toast-transition-timing-function
  :scale--$toast-initial-scale
  :transition-delay--$toast-delay-duration
  :bgc--$toast-background-color
  :dark:bgc--$toast-background-color-inverse
  [:--_auto-dismiss-duration :$toast-auto-dismiss-duration]
  [:max-width "calc(100vw - (2 * var(--toast-slot-padding-inline)))"])


;; block mixins
;; ------------------------------------------------

(defcss "@layer kushi-ui-shared .kushi-pane-block-arrow-offset-mixin"
  [:--arrow-plus-radius "calc(var(--arrow-inline-inset) + var(--border-radius))"])

(defcss "@layer kushi-ui-shared .kushi-pane-block-right-mixin"
  [:--tx "calc((var(--oe-right) - 100%))"]
  [:--arrow-tx "calc(0px - (100% + var(--arrow-plus-radius)))"]
  :_.kushi-pane-arrow:left--100%)

(defcss "@layer kushi-ui-shared .kushi-pane-block-left-mixin"
  [:--tx :$oe-left]
  [:--arrow-tx "calc(0px + var(--arrow-plus-radius))"]
  :_.kushi-pane-arrow:left--0%)

(defcss 
 "@layer kushi-ui-shared .kushi-pane-block-center-mixin
  .kushi-pane-block-center-mixin"
  [:--tx "calc(var(--oe-x-center) - 50%)"]
  [:--arrow-tx :-50%]
  :_.kushi-pane-arrow:left--50%)



;; Top
;; ------------------------------------------------

(defcss
 "@layer kushi-ui-shared .kushi-pane-top-mixin"
  :.kushi-pane-block-arrow-offset-mixin
  :translate--$tx:$ty
  [:--ty :$top-plc]
  [:--_arrow-gradient-direction "to top left"]
  [:--arrow-ty "calc(-50% + (var(--border-width) * 0.7))"]
  [:_.kushi-pane-arrow {:top :100%} ])
(defcss
  "@layer kushi-ui-shared .kushi-pane-tl" 
  :.kushi-pane-top-mixin
  :.kushi-pane-block-left-mixin
  :transform-origin--bottom:left)

(defcss 
  "@layer kushi-ui-shared .kushi-pane-t" 
  :.kushi-pane-top-mixin
  :.kushi-pane-block-center-mixin
  :transform-origin--bottom:center)

(defcss 
  "@layer kushi-ui-shared .kushi-pane-tr"
  :.kushi-pane-top-mixin
  :.kushi-pane-block-right-mixin
  :transform-origin--bottom:right)


;; Bottom
;; ------------------------------------------------
(defcss
  "@layer kushi-ui-shared .kushi-pane-bottom-mixin"
  :.kushi-pane-block-arrow-offset-mixin
  :translate--$tx:$ty
  [:--ty :$bottom-plc]
  [:--_arrow-gradient-direction "to bottom right"]
  [:--arrow-ty "calc(50% - (var(--border-width) * 0.7))"]
  [:_.kushi-pane-arrow {:bottom :100%}])

(defcss
  "@layer kushi-ui-shared .kushi-pane-bl"
  :.kushi-pane-bottom-mixin
  :.kushi-pane-block-left-mixin
  :transform-origin--top:left
  )

(defcss
  "@layer kushi-ui-shared .kushi-pane-b"
  :.kushi-pane-bottom-mixin
  :.kushi-pane-block-center-mixin
  :transform-origin--top:center
  [:--tx "calc((var(--oe-x-center) - 50%))"]
  [:--arrow-tx :-50%]
  :_.kushi-pane-arrow:left--50%)

(defcss
  "@layer kushi-ui-shared .kushi-pane-br"
  :.kushi-pane-bottom-mixin
  :.kushi-pane-block-right-mixin
  :transform-origin--top:right)


;; Inline mixins
;; ------------------------------------------------

(defcss
  "@layer kushi-ui-shared .kushi-pane-inline-arrow-offset-mixin"
  [:--arrow-plus-radius "calc(var(--arrow-inline-inset) + var(--pane-border-radius))"])

(defcss
  "@layer kushi-ui-shared .kushi-pane-inline-top-mixin"
  [:--ty :$oe-top]
  [:--arrow-ty "var(--arrow-plus-radius)"]
  :_.kushi-pane-arrow:top--0%)

(defcss
  "@layer kushi-ui-shared .kushi-pane-inline-center-mixin"
  [:--ty "calc(var(--oe-y-center) - 50%)"]
  [:--arrow-ty :-50%]
  :_.kushi-pane-arrow:top--50%)

(defcss
  "@layer kushi-ui-shared .kushi-pane-inline-bottom-mixin"
  [:--ty "calc(var(--oe-bottom) - 100%)"]
  [:--arrow-ty "calc(0px - var(--arrow-plus-radius))"]
  :_.kushi-pane-arrow:bottom--0)


;; Right 
;; ------------------------------------------------
(defcss
  "@layer kushi-ui-shared .kushi-pane-right-mixin"
  :.kushi-pane-inline-arrow-offset-mixin
  :translate--$tx:$ty
  [:--tx :$right-plc]
  [:--_arrow-gradient-direction "to top right"]
  [:--arrow-tx "calc(50% - (var(--border-width) * 0.7))"]
  [:_.kushi-pane-arrow {:right    :100%
                        ;; :translate :$arrow-tx:$arrow-ty
                         }])

(defcss 
  "@layer kushi-ui-shared .kushi-pane-rt"
  :.kushi-pane-right-mixin
  :.kushi-pane-inline-top-mixin
  :transform-origin--top:left)

(defcss 
  "@layer kushi-ui-shared .kushi-pane-r" 
  :.kushi-pane-right-mixin
  :.kushi-pane-inline-center-mixin
  :transform-origin--center:left)

;; (defcss
;;   ["[kushi-ui-pane-placement=\"r\"]"] 
;;   :.kushi-pane-right-mixin
;;   :.kushi-pane-inline-center-mixin
;;   :transform-origin--center:left)

(defcss
  "@layer kushi-ui-shared .kushi-pane-rb"
  :.kushi-pane-right-mixin
  :.kushi-pane-inline-bottom-mixin
  :transform-origin--bottom:left)


;; Left
;; ------------------------------------------------
(defcss
  "@layer kushi-ui-shared .kushi-pane-left-mixin"
  :.kushi-pane-inline-arrow-offset-mixin
  :translate--$tx:$ty
  [:--tx :$left-plc]
  [:--_arrow-gradient-direction "to bottom left"]
  [:--arrow-tx "calc(-50% + (var(--border-width) * 0.7))"]
  [:_.kushi-pane-arrow {:left      :100%
                        ;;  :translate :$arrow-tx:$arrow-ty
                         }])

(defcss 
  "@layer kushi-ui-shared .kushi-pane-lt"
  :transform-origin--top:right
  :.kushi-pane-left-mixin
  :.kushi-pane-inline-top-mixin )

(defcss 
  "@layer kushi-ui-shared .kushi-pane-l" 
  :transform-origin--center:right
  :.kushi-pane-left-mixin
  :.kushi-pane-inline-center-mixin )

(defcss
  "@layer kushi-ui-shared .kushi-pane-lb"
  :transform-origin--bottom:right
  :.kushi-pane-left-mixin
  :.kushi-pane-inline-bottom-mixin )

;; corner positioning 
;; ------------------------------------------------
(defcss
  "@layer kushi-ui-shared .kushi-pane-top-corner-mixin" 
  [:--ty "calc(var(--top-plc) + (0.5 * var(--offset)))"]
  :translate--$tx:$ty)

(defcss
  "@layer kushi-ui-shared .kushi-pane-bottom-corner-mixin" 
  [:--ty "calc(var(--bottom-plc) - (0.5 * var(--offset)))"]
  :translate--$tx:$ty)

(defcss
  "@layer kushi-ui-shared .kushi-pane-right-corner-mixin" 
  [:--tx "calc(var(--oe-right) + (0.5 * var(--offset)))"] )

(defcss
  "@layer kushi-ui-shared .kushi-pane-left-corner-mixin" 
  [:--tx "calc((var(--oe-left) - 100%) - (0.5 * var(--offset)))"] )

;; Top left corner
(defcss
  "@layer kushi-ui-shared .kushi-pane-tlc" 
  :.kushi-pane-top-corner-mixin
  :.kushi-pane-left-corner-mixin
  :beer--0
  :transform-origin--bottom:right)

(defcss
  "@layer kushi-ui-shared .kushi-pane-trc"
  :.kushi-pane-top-corner-mixin
  :.kushi-pane-right-corner-mixin
  :besr--0
  :transform-origin--bottom:left )

(defcss
  "@layer kushi-ui-shared .kushi-pane-brc"
  :.kushi-pane-bottom-corner-mixin
  :.kushi-pane-right-corner-mixin
  :bssr--0
  :transform-origin--top:left)

(defcss
  "@layer kushi-ui-shared .kushi-pane-blc"
  :.kushi-pane-bottom-corner-mixin
  :.kushi-pane-left-corner-mixin
  :bser--0
  :transform-origin--top:right)


(defcss
  "@layer kushi-ui-shared .kushi-pane-arrowless"
  :beer--$pane-border-radius
  :besr--$pane-border-radius
  :bssr--$pane-border-radius
  :bser--$pane-border-radius
  :>.kushi-pane-arrow:d--none)

;; (defcss 
;;   kushi-pane-arrow
;;   :w--0
;;   :h--0
;;   :zi--10
;;   :.absolute
;;   :.transition
;;   :.xxfast
;;   :.hidden:border--0!important
;;   [:translate "var(--tx) var(--ty)"])

(defcss
  "@layer kushi-ui-shared .kushi-pane-arrow"
  :position--absolute
  :bw--inherit
  :bs--inherit
  :bc--inherit
  [:--sz "calc(sqrt(2)* var(--arrow-depth))"]
  :w--$sz
  :h--$sz
  [:--arrow-inline-inset :-50%]
  [:--arrow-block-inset :-50%]
  :bgc--inherit
  :h--$sz
  [:--_arrow-stop "calc(50% + max(1px, (var(--border-width) * 0.72)))"]
  [:mask-image "linear-gradient(var(--_arrow-gradient-direction), black var(--_arrow-stop), transparent var(--_arrow-stop))"]
  [:transform "translate(var(--arrow-tx), var(--arrow-ty)) rotate(45deg)"]

  [".kushi-pane-tl &" {:border-top-width :0!important
                                 :border-left-width :0!important}]
  [".kushi-pane-t &" {:border-top-width :0!important
                                :border-left-width :0!important}]
  [".kushi-pane-tr &" {:border-top-width :0!important
                                 :border-left-width :0!important}]

  [".kushi-pane-rt &" {:border-top-width :0!important
                                 :border-right-width :0!important}]
  [".kushi-pane-r &" {:border-top-width :0!important
                                :border-right-width :0!important}]
  [".kushi-pane-rb &" {:border-top-width :0!important
                                 :border-right-width :0!important}]

  [".kushi-pane-br &" {:border-bottom-width :0!important
                                 :border-right-width :0!important}]
  [".kushi-pane-b &" {:border-bottom-width :0!important
                                :border-right-width :0!important}]
  [".kushi-pane-bl &" {:border-bottom-width :0!important
                                 :border-right-width :0!important}]

  [".kushi-pane-l &" {:border-bottom-width :0!important
                                :border-left-width :0!important}]
  [".kushi-pane-lt &" {:border-bottom-width :0!important
                                 :border-left-width :0!important}]
  [".kushi-pane-lb &" {:border-bottom-width :0!important
                                 :border-left-width :0!important}])

(defcss
  "@layer kushi-ui-shared .kushi-pane-mounting" ;; TODO is this even used?
  :visibility--hidden)
