(ns kushi.ui.dom.fune.styles
  (:require
   [goog.string]
   [kushi.core :refer (defclass)]))

(defclass ^{:kushi/chunk :kushi/kushi-ui-defclass}
  kushi-fune
  :.fixed
  :min-height--$fune-min-height
  :min-width--$fune-min-width
  :border-width--$fune-border-width
  :border-style--$fune-border-style
  :border-color--$fune-border-color
  :box-shadow--$fune-box-shadow
  :border-radius--$fune-border-radius
  :transition-property--opacity|translate|scale|transform
  :.transition
  :transition-duration--$fune-transition-duration
  :transition-timing-function--$fune-transition-timing-function
  ;; :scale--1
  :scale--$fune-initial-scale
  :transition-delay--$fune-delay-duration
  :zi--$fune-z-index
  :top--0
  :left--0
  :bgc--$fune-background-color
  :w--max-content
  :p--$fune-padding-block:$fune-padding-inline
  [:$fune-arrow-depth
   "max( min( var(--fune-arrow-depth-max-px, 12px), var(--fune-arrow-depth-ems, 0.3em)), var(--fune-arrow-depth-min-px, 5px))"]
  )

(defclass ^{:kushi/chunk :kushi/kushi-ui-defclass}
  kushi-tooltip
  :.kushi-fune
  :min-height--$tooltip-min-height
  :min-width--$tooltip-min-width
  :border-width--$tooltip-border-width
  :border-style--$tooltip-border-style
  :border-color--$tooltip-border-color
  :box-shadow--$tooltip-box-shadow||none
  :fs--$tooltip-font-size
  :border-radius--$tooltip-border-radius
  :transition-duration--$tooltip-transition-duration
  :transition-timing-function--$tooltip-transition-timing-function
  :scale--$tooltip-initial-scale
  :transition-delay--$tooltip-delay-duration
  :zi--$tooltip-z-index
  :c--$tooltip-color
  :bgc--$tooltip-background-color
  :p--$tooltip-padding-block:$tooltip-padding-inline
  [:$tooltip-arrow-depth
   "max( min( var(--tooltip-arrow-depth-max-px, 12px), var(--tooltip-arrow-depth-ems, 0.3em)), var(--tooltip-arrow-depth-min-px, 5px))"]
  )

(defclass ^{:kushi/chunk :kushi/kushi-ui-defclass}
  kushi-popover
  :.kushi-fune
  :p--0
  :min-height--$popover-min-height
  :min-width--$popover-min-width
  :border-width--$popover-border-width
  :border-style--$popover-border-style
  :border-color--$popover-border-color
  :box-shadow--$popover-box-shadow
  :border-radius--$popover-border-radius
  :transition-duration--$popover-transition-duration
  :transition-timing-function--$popover-transition-timing-function
  :scale--$popover-initial-scale
  :transition-delay--$popover-delay-duration
  :zi--$popover-z-index
  :c--$popover-color
  :bgc--$popover-background-color
  [:$popover-arrow-depth
   "max( min( var(--popover-arrow-depth-max-px, 12px), var(--popover-arrow-depth-ems, 0.3em)), var(--popover-arrow-depth-min-px, 5px))"]
  )


;; block mixins
;; ------------------------------------------------

(defclass ^{:kushi/chunk :kushi/kushi-ui-defclass}
  kushi-fune-block-arrow-offset-mixin
  [:$arrow-plus-radius "calc(var(--arrow-inline-inset) + var(--border-radius))"])

(defclass ^{:kushi/chunk :kushi/kushi-ui-defclass}
  kushi-fune-block-right-mixin
  [:$tx "calc((var(--oe-right) - 100%))"]
  [:$arrow-tx "calc(0px - (100% + var(--arrow-plus-radius)))"]
  :&_.kushi-fune-arrow:left--100%)

(defclass ^{:kushi/chunk :kushi/kushi-ui-defclass}
  kushi-fune-block-left-mixin
  :$tx--$oe-left
  [:$arrow-tx "calc(0px + var(--arrow-plus-radius))"]
  :&_.kushi-fune-arrow:left--0%)

(defclass ^{:kushi/chunk :kushi/kushi-ui-defclass}
  kushi-fune-block-center-mixin
  [:$tx "calc(var(--oe-x-center) - 50%)"]
  [:$arrow-tx :-50%]
  :&_.kushi-fune-arrow:left--50%)



;; Top
;; ------------------------------------------------

(defclass ^{:kushi/chunk :kushi/kushi-ui-defclass}
  kushi-fune-top-mixin
  :.kushi-fune-block-arrow-offset-mixin
  :translate--$tx:$ty
  :$ty--$top-plc
  [:$arrow-ty "calc(-50% + (var(--border-width) * 0.7))"]
  [:&_.kushi-fune-arrow {:top :100%} ])
(defclass ^{:kushi/chunk :kushi/kushi-ui-defclass}
  kushi-fune-tl 
  :.kushi-fune-top-mixin
  :.kushi-fune-block-left-mixin
  :transform-origin--bottom:left)

(defclass ^{:kushi/chunk :kushi/kushi-ui-defclass} 
  kushi-fune-t 
  :.kushi-fune-top-mixin
  :.kushi-fune-block-center-mixin
  :transform-origin--bottom:center)

(defclass ^{:kushi/chunk :kushi/kushi-ui-defclass} 
  kushi-fune-tr
  :.kushi-fune-top-mixin
  :.kushi-fune-block-right-mixin
  :transform-origin--bottom:right)


;; Bottom
;; ------------------------------------------------
(defclass ^{:kushi/chunk :kushi/kushi-ui-defclass}
  kushi-fune-bottom-mixin
  :.kushi-fune-block-arrow-offset-mixin
  :translate--$tx:$ty
  :$ty--$bottom-plc
  [:$arrow-ty "calc(50% - (var(--border-width) * 0.7))"]
  [:&_.kushi-fune-arrow {:bottom :100%}])

(defclass ^{:kushi/chunk :kushi/kushi-ui-defclass}
  kushi-fune-bl
  :.kushi-fune-bottom-mixin
  :.kushi-fune-block-left-mixin
  :transform-origin--top:left
  )

(defclass ^{:kushi/chunk :kushi/kushi-ui-defclass}
  kushi-fune-b
  :.kushi-fune-bottom-mixin
  :.kushi-fune-block-center-mixin
  :transform-origin--top:center
  [:$tx "calc((var(--oe-x-center) - 50%))"]
  [:$arrow-tx :-50%]
  :&_.kushi-fune-arrow:left--50%)

(defclass ^{:kushi/chunk :kushi/kushi-ui-defclass}
  kushi-fune-br
  :.kushi-fune-bottom-mixin
  :.kushi-fune-block-right-mixin
  :transform-origin--top:right)


;; Inline mixins
;; ------------------------------------------------

(defclass ^{:kushi/chunk :kushi/kushi-ui-defclass}
  kushi-fune-inline-arrow-offset-mixin
  [:$arrow-plus-radius "calc(var(--arrow-inline-inset) + var(--fune-border-radius))"])

(defclass ^{:kushi/chunk :kushi/kushi-ui-defclass}
  kushi-fune-inline-top-mixin
  :$ty--$oe-top
  [:$arrow-ty "var(--arrow-plus-radius)"]
  :&_.kushi-fune-arrow:top--0%)

(defclass ^{:kushi/chunk :kushi/kushi-ui-defclass}
  kushi-fune-inline-center-mixin
  [:$ty "calc(var(--oe-y-center) - 50%)"]
  [:$arrow-ty :-50%]
  :&_.kushi-fune-arrow:top--50%)

(defclass ^{:kushi/chunk :kushi/kushi-ui-defclass}
  kushi-fune-inline-bottom-mixin
  [:$ty "calc(var(--oe-bottom) - 100%)"]
  [:$arrow-ty "calc(0px - var(--arrow-plus-radius))"]
  :&_.kushi-fune-arrow:bottom--0)


;; Right 
;; ------------------------------------------------
(defclass ^{:kushi/chunk :kushi/kushi-ui-defclass}
  kushi-fune-right-mixin
  :.kushi-fune-inline-arrow-offset-mixin
  :translate--$tx:$ty
  :$tx--$right-plc
  [:$arrow-tx "calc(50% - (var(--border-width) * 0.7))"]
  [:&_.kushi-fune-arrow {:right    :100%
                        ;;  :translate :$arrow-tx:$arrow-ty
                         }])

(defclass ^{:kushi/chunk :kushi/kushi-ui-defclass} 
  kushi-fune-rt
  :.kushi-fune-right-mixin
  :.kushi-fune-inline-top-mixin
  :transform-origin--top:left)

(defclass ^{:kushi/chunk :kushi/kushi-ui-defclass :kushi/__selector ".kushi-fune-rx"} 
  kushi-fune-r 
  :.kushi-fune-right-mixin
  :.kushi-fune-inline-center-mixin
  :transform-origin--center:left)

;; (defclass ^{:kushi/chunk :kushi/kushi-ui-defclass} 
;;   ["[kushi-ui-fune-placement=\"r\"]"] 
;;   :.kushi-fune-right-mixin
;;   :.kushi-fune-inline-center-mixin
;;   :transform-origin--center:left)

(defclass ^{:kushi/chunk :kushi/kushi-ui-defclass}
  kushi-fune-rb
  :.kushi-fune-right-mixin
  :.kushi-fune-inline-bottom-mixin
  :transform-origin--bottom:left)


;; Left
;; ------------------------------------------------
(defclass ^{:kushi/chunk :kushi/kushi-ui-defclass}
  kushi-fune-left-mixin
  :.kushi-fune-inline-arrow-offset-mixin
  :translate--$tx:$ty
  :$tx--$left-plc
  [:$arrow-tx "calc(-50% + (var(--border-width) * 0.7))"]
  [:&_.kushi-fune-arrow {:left      :100%
                        ;;  :translate :$arrow-tx:$arrow-ty
                         }])

(defclass ^{:kushi/chunk :kushi/kushi-ui-defclass} 
  kushi-fune-lt
  :transform-origin--top:right
  :.kushi-fune-left-mixin
  :.kushi-fune-inline-top-mixin )

(defclass ^{:kushi/chunk :kushi/kushi-ui-defclass} 
  kushi-fune-l 
  :transform-origin--center:right
  :.kushi-fune-left-mixin
  :.kushi-fune-inline-center-mixin )

(defclass ^{:kushi/chunk :kushi/kushi-ui-defclass}
  kushi-fune-lb
  :transform-origin--bottom:right
  :.kushi-fune-left-mixin
  :.kushi-fune-inline-bottom-mixin )

;; corner positioning 
;; ------------------------------------------------
(defclass ^{:kushi/chunk :kushi/kushi-ui-defclass}
  kushi-fune-top-corner-mixin 
  [:$ty "calc(var(--top-plc) + (0.5 * var(--offset)))"]
  :translate--$tx:$ty)

(defclass ^{:kushi/chunk :kushi/kushi-ui-defclass}
  kushi-fune-bottom-corner-mixin 
  [:$ty "calc(var(--bottom-plc) - (0.5 * var(--offset)))"]
  :translate--$tx:$ty)

(defclass ^{:kushi/chunk :kushi/kushi-ui-defclass}
  kushi-fune-right-corner-mixin 
  [:$tx "calc(var(--oe-right) + (0.5 * var(--offset)))"] )

(defclass ^{:kushi/chunk :kushi/kushi-ui-defclass}
  kushi-fune-left-corner-mixin 
  [:$tx "calc((var(--oe-left) - 100%) - (0.5 * var(--offset)))"] )

;; Top left corner
(defclass ^{:kushi/chunk :kushi/kushi-ui-defclass}
  kushi-fune-tlc 
  :.kushi-fune-top-corner-mixin
  :.kushi-fune-left-corner-mixin
  :beer--0
  :transform-origin--bottom:right)

(defclass ^{:kushi/chunk :kushi/kushi-ui-defclass} 
  kushi-fune-trc
  :.kushi-fune-top-corner-mixin
  :.kushi-fune-right-corner-mixin
  :besr--0
  :transform-origin--bottom:left )

(defclass ^{:kushi/chunk :kushi/kushi-ui-defclass} 
  kushi-fune-brc
  :.kushi-fune-bottom-corner-mixin
  :.kushi-fune-right-corner-mixin
  :bssr--0
  :transform-origin--top:left)

(defclass ^{:kushi/chunk :kushi/kushi-ui-defclass}
  kushi-fune-blc
  :.kushi-fune-bottom-corner-mixin
  :.kushi-fune-left-corner-mixin
  :bser--0
  :transform-origin--top:right)


(defclass ^{:kushi/chunk :kushi/kushi-ui-defclass}
  kushi-fune-arrowless
  :beer--$fune-border-radius
  :besr--$fune-border-radius
  :bssr--$fune-border-radius
  :bser--$fune-border-radius
  :&>.kushi-fune-arrow:d--none)

;; (defclass ^{:kushi/chunk :kushi/kushi-ui-defclass} 
;;   kushi-fune-arrow
;;   :w--0
;;   :h--0
;;   :zi--10
;;   :.absolute
;;   :.transition
;;   :.xxfast
;;   :&.hidden:border--0!important
;;   [:translate "var(--tx) var(--ty)"])

(defclass ^{:kushi/chunk :kushi/kushi-ui-defclass} 
  kushi-fune-arrow
  :bw--inherit
  :bs--inherit
  :bc--inherit
  :.absolute
  [:$sz "calc(sqrt(2)* var(--arrow-depth))"]
  :w--$sz
  :h--$sz
  [:$arrow-inline-inset :-50%]
  [:$arrow-block-inset :-50%]
  :bgc--inherit
  :h--$sz
  [:transform "translate(var(--arrow-tx), var(--arrow-ty)) rotate(45deg)"]

  ["has-parent(.kushi-fune-tl)" {:border-top-width :0!important
                                :border-left-width :0!important}]
  ["has-parent(.kushi-fune-t)" {:border-top-width :0!important
                                :border-left-width :0!important}]
  ["has-parent(.kushi-fune-tr)" {:border-top-width :0!important
                                :border-left-width :0!important}]

  ["has-parent(.kushi-fune-rt)" {:border-top-width :0!important
                                :border-right-width :0!important}]
  ["has-parent(.kushi-fune-r)" {:border-top-width :0!important
                                :border-right-width :0!important}]
  ["has-parent(.kushi-fune-rb)" {:border-top-width :0!important
                                :border-right-width :0!important}]

  ["has-parent(.kushi-fune-br)" {:border-bottom-width :0!important
                                :border-right-width :0!important}]
  ["has-parent(.kushi-fune-b)" {:border-bottom-width :0!important
                                :border-right-width :0!important}]
  ["has-parent(.kushi-fune-bl)" {:border-bottom-width :0!important
                                :border-right-width :0!important}]

  ["has-parent(.kushi-fune-l)" {:border-bottom-width :0!important
                                :border-left-width :0!important}]
  ["has-parent(.kushi-fune-lt)" {:border-bottom-width :0!important
                                :border-left-width :0!important}]
  ["has-parent(.kushi-fune-lb)" {:border-bottom-width :0!important
                                :border-left-width :0!important}])

(defclass ^{:kushi/chunk :kushi/kushi-ui-defclass} 
  kushi-fune-mounting
  :.hidden)
