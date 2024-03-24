(ns kushi.ui.dom.fune.styles
  (:require
   [goog.string]
   [kushi.core :refer (defclass)]))

(defclass ^{:kushi/chunk :kushi/kushi-ui-defclass}
  kushi-tooltip
  :.fixed
  :fs--$tooltip-font-size
  ;; :fs--100px
  :border-radius--$tooltip-border-radius
  ;; toggle this to none to get metrics
  :transition-property--opacity|translate|scale|transform
  ;; :transition-property--none
  :.transition
  :transition-duration--$tooltip-transition-duration
  :transition-timing-function--$tooltip-transition-timing-function
  ;; :scale--1
  :scale--$tooltip-initial-scale
  ;; [:$_kushi-tooltip-translate-y-shift :0px]
  ;; [:$_kushi-tooltip-translate-x-shift :0px]
  ;; [:$_kushi-tooltip-translate-y-base :0px]
  ;; [:$_kushi-tooltip-translate-x-base :0px]
  ;; [:$_kushi-tooltip-translate-x "calc(var(--_kushi-tooltip-translate-x-base) + var(--_kushi-tooltip-translate-x-shift))"]
  ;; [:$_kushi-tooltip-translate-y  "calc(var(--_kushi-tooltip-translate-y-base) + var(--_kushi-tooltip-translate-y-shift))"]
  ;; :translate--$_kushi-tooltip-translate-x:$_kushi-tooltip-translate-y
  :transition-delay--$tooltip-delay-duration
  :zi--auto
  :top--0
  :left--0
  :c--$tooltip-color
  :bgc--red
  :bgc--$tooltip-background-color
  :w--max-content
  :p--0.5em:1em)


;; block mixins
;; ------------------------------------------------

(defclass ^{:kushi/chunk :kushi/kushi-ui-defclass}
  kushi-tooltip-block-arrow-offset-mixin
  [:$arrow-plus-radius "calc(var(--tooltip-arrow-x-offset) + var(--tooltip-border-radius))"])

(defclass ^{:kushi/chunk :kushi/kushi-ui-defclass}
  kushi-tooltip-block-right-mixin
  [:$tx "calc((var(--oe-right) - 100%))"]
  [:$arrow-tx "calc(0px - (100% + var(--arrow-plus-radius)))"]
  :&_.kushi-tooltip-arrow:left--100%)

(defclass ^{:kushi/chunk :kushi/kushi-ui-defclass}
  kushi-tooltip-block-left-mixin
  :$tx--$oe-left
  [:$arrow-tx "calc(0px + var(--arrow-plus-radius))"]
  :&_.kushi-tooltip-arrow:left--0%)

(defclass ^{:kushi/chunk :kushi/kushi-ui-defclass}
  kushi-tooltip-block-center-mixin
  [:$tx "calc(var(--oe-x-center) - 50%)"]
  [:$arrow-tx :-50%]
  :&_.kushi-tooltip-arrow:left--50%)



;; Top
;; ------------------------------------------------

(defclass ^{:kushi/chunk :kushi/kushi-ui-defclass}
  kushi-tooltip-top-mixin
  :.kushi-tooltip-block-arrow-offset-mixin
  :translate--$tx:$ty
  :$ty--$top-plc
  [:$arrow-ty "-0.333px"]
  [:&_.kushi-tooltip-arrow {:top       :100%
                            :translate :$arrow-tx:$arrow-ty}])
(defclass ^{:kushi/chunk :kushi/kushi-ui-defclass}
  kushi-tooltip-tl 
  :.kushi-tooltip-top-mixin
  :.kushi-tooltip-block-left-mixin
  :transform-origin--bottom:left)

(defclass ^{:kushi/chunk :kushi/kushi-ui-defclass} 
  kushi-tooltip-t 
  :.kushi-tooltip-top-mixin
  :.kushi-tooltip-block-center-mixin
  :transform-origin--bottom:centr )

(defclass ^{:kushi/chunk :kushi/kushi-ui-defclass} 
  kushi-tooltip-tr
  :.kushi-tooltip-top-mixin
  :.kushi-tooltip-block-right-mixin
  :transform-origin--bottom:right)


;; Bottom
;; ------------------------------------------------
(defclass ^{:kushi/chunk :kushi/kushi-ui-defclass}
  kushi-tooltip-bottom-mixin
  :.kushi-tooltip-block-arrow-offset-mixin
  :translate--$tx:$ty
  :$ty--$bottom-plc
  [:$arrow-ty "0.333px"]
  [:&_.kushi-tooltip-arrow {:bottom    :100%
                            :translate :$arrow-tx:$arrow-ty}])

(defclass ^{:kushi/chunk :kushi/kushi-ui-defclass}
  kushi-tooltip-bl
  :.kushi-tooltip-bottom-mixin
  :.kushi-tooltip-block-left-mixin
  :transform-origin--top:left
  )

(defclass ^{:kushi/chunk :kushi/kushi-ui-defclass}
  kushi-tooltip-b
  :.kushi-tooltip-bottom-mixin
  :.kushi-tooltip-block-center-mixin
  :transform-origin--top:center
  [:$tx "calc((var(--oe-x-center) - 50%))"]
  [:$arrow-tx :-50%]
  :&_.kushi-tooltip-arrow:left--50%)

(defclass ^{:kushi/chunk :kushi/kushi-ui-defclass}
  kushi-tooltip-br
  :.kushi-tooltip-bottom-mixin
  :.kushi-tooltip-block-right-mixin
  :transform-origin--top:right)


;; Inline mixins
;; ------------------------------------------------

(defclass ^{:kushi/chunk :kushi/kushi-ui-defclass}
  kushi-tooltip-inline-arrow-offset-mixin
  [:$arrow-plus-radius "calc(var(--tooltip-arrow-y-offset) + var(--tooltip-border-radius))"])

(defclass ^{:kushi/chunk :kushi/kushi-ui-defclass}
  kushi-tooltip-inline-top-mixin
  :$ty--$oe-top
  [:$arrow-ty "var(--arrow-plus-radius)"]
  :&_.kushi-tooltip-arrow:top--0%)

(defclass ^{:kushi/chunk :kushi/kushi-ui-defclass}
  kushi-tooltip-inline-center-mixin
  [:$ty "calc(var(--oe-y-center) - 50%)"]
  [:$arrow-ty :-50%]
  :&_.kushi-tooltip-arrow:top--50%)

(defclass ^{:kushi/chunk :kushi/kushi-ui-defclass}
  kushi-tooltip-inline-bottom-mixin
  [:$ty "calc(var(--oe-bottom) - 100%)"]
  [:$arrow-ty "calc(0px - var(--arrow-plus-radius))"]
  :&_.kushi-tooltip-arrow:bottom--0)


;; Right 
;; ------------------------------------------------
(defclass ^{:kushi/chunk :kushi/kushi-ui-defclass}
  kushi-tooltip-right-mixin
  :.kushi-tooltip-inline-arrow-offset-mixin
  :translate--$tx:$ty
  :$tx--$right-plc
  [:$arrow-tx "0.333px"]
  [:&_.kushi-tooltip-arrow {:right    :100%
                            :translate :$arrow-tx:$arrow-ty}])

(defclass ^{:kushi/chunk :kushi/kushi-ui-defclass} 
  kushi-tooltip-rt
  :.kushi-tooltip-right-mixin
  :.kushi-tooltip-inline-top-mixin
  :transform-origin--top:left)

(defclass ^{:kushi/chunk :kushi/kushi-ui-defclass} 
  kushi-tooltip-r 
  :.kushi-tooltip-right-mixin
  :.kushi-tooltip-inline-center-mixin
  :transform-origin--center:left)

(defclass ^{:kushi/chunk :kushi/kushi-ui-defclass}
  kushi-tooltip-rb
  :.kushi-tooltip-right-mixin
  :.kushi-tooltip-inline-bottom-mixin
  :transform-origin--bottom:left)

;; inline-start
;; ------------------------------------------------
(defclass ^{:kushi/chunk :kushi/kushi-ui-defclass}
  kushi-tooltip-left-mixin
  :.kushi-tooltip-inline-arrow-offset-mixin
  :translate--$tx:$ty
  :$tx--$left-plc
  [:$arrow-tx "-0.333px"]
  [:&_.kushi-tooltip-arrow {:left      :100%
                            :translate :$arrow-tx:$arrow-ty}])

(defclass ^{:kushi/chunk :kushi/kushi-ui-defclass} 
  kushi-tooltip-lt
  :transform-origin--top:right
  :.kushi-tooltip-left-mixin
  :.kushi-tooltip-inline-top-mixin )

(defclass ^{:kushi/chunk :kushi/kushi-ui-defclass} 
  kushi-tooltip-l 
  :transform-origin--center:right
  :.kushi-tooltip-left-mixin
  :.kushi-tooltip-inline-center-mixin )

(defclass ^{:kushi/chunk :kushi/kushi-ui-defclass}
  kushi-tooltip-lb
  :transform-origin--bottom:right
  :.kushi-tooltip-left-mixin
  :.kushi-tooltip-inline-bottom-mixin )

;; corner positioning 
;; ------------------------------------------------
(defclass ^{:kushi/chunk :kushi/kushi-ui-defclass}
  kushi-tooltip-top-corner-mixin 
  [:$ty "calc(var(--top-plc) + (0.5 * var(--offset)))"]
  :translate--$tx:$ty)

(defclass ^{:kushi/chunk :kushi/kushi-ui-defclass}
  kushi-tooltip-bottom-corner-mixin 
  [:$ty "calc(var(--bottom-plc) - (0.5 * var(--offset)))"]
  :translate--$tx:$ty)

(defclass ^{:kushi/chunk :kushi/kushi-ui-defclass}
  kushi-tooltip-right-corner-mixin 
  [:$tx "calc(var(--oe-right) + (0.5 * var(--offset)))"] )

(defclass ^{:kushi/chunk :kushi/kushi-ui-defclass}
  kushi-tooltip-left-corner-mixin 
  [:$tx "calc(var(--oe-left) + (0.5 * var(--offset)))"] )

;; Top left corner
(defclass ^{:kushi/chunk :kushi/kushi-ui-defclass}
  kushi-tooltip-tlc 
  :.kushi-tooltip-top-corner-mixin
  :.kushi-tooltip-left-corner-mixin
  :beer--0
  :transform-origin--bottom:right)

(defclass ^{:kushi/chunk :kushi/kushi-ui-defclass} 
  kushi-tooltip-trc
  :.kushi-tooltip-top-corner-mixin
  :.kushi-tooltip-right-corner-mixin
  :besr--0
  :transform-origin--bottom:left )

(defclass ^{:kushi/chunk :kushi/kushi-ui-defclass} 
  kushi-tooltip-brc
  :.kushi-tooltip-bottom-corner-mixin
  :.kushi-tooltip-right-corner-mixin
  :bssr--0
  :transform-origin--top:left)

(defclass ^{:kushi/chunk :kushi/kushi-ui-defclass}
  kushi-tooltip-blc
  :.kushi-tooltip-bottom-corner-mixin
  :.kushi-tooltip-left-corner-mixin
  :bser--0
  :transform-origin--top:right)


(defclass ^{:kushi/chunk :kushi/kushi-ui-defclass}
  kushi-tooltip-arrowless
  :beer--$tooltip-border-radius
  :besr--$tooltip-border-radius
  :bssr--$tooltip-border-radius
  :bser--$tooltip-border-radius
  :&>.kushi-tooltip-arrow:d--none)

(defclass ^{:kushi/chunk :kushi/kushi-ui-defclass} 
  kushi-tooltip-arrow
  :w--0
  :h--0
  :zi--10
  :.absolute
  :.transition
  :.xxfast
  :&.hidden:border--0!important
  [:translate "var(--tx) var(--ty)"])

(defclass ^{:kushi/chunk :kushi/kushi-ui-defclass}
  kushi-tooltip-arrow-pointing-down
  {:border-top      :$tooltip-arrow-depth:solid:$tooltip-background-color
   :dark:border-top :$tooltip-arrow-depth:solid:$tooltip-background-color-inverse
   :border-left     :$tooltip-arrow-depth:solid:transparent
   :border-right    :$tooltip-arrow-depth:solid:transparent})

(defclass ^{:kushi/chunk :kushi/kushi-ui-defclass}
  kushi-tooltip-arrow-pointing-up
  {:border-bottom      :$tooltip-arrow-depth:solid:$tooltip-background-color
   :dark:border-bottom :$tooltip-arrow-depth:solid:$tooltip-background-color-inverse
   :border-left        :$tooltip-arrow-depth:solid:transparent
   :border-right       :$tooltip-arrow-depth:solid:transparent})

(defclass ^{:kushi/chunk :kushi/kushi-ui-defclass}
  kushi-tooltip-arrow-pointing-left
  {:border-right      :$tooltip-arrow-depth:solid:$tooltip-background-color
   :dark:border-right :$tooltip-arrow-depth:solid:$tooltip-background-color-inverse
   :border-top        :$tooltip-arrow-depth:solid:transparent
   :border-bottom     :$tooltip-arrow-depth:solid:transparent})

(defclass ^{:kushi/chunk :kushi/kushi-ui-defclass} 
  kushi-tooltip-arrow-pointing-right
  {:border-left      :$tooltip-arrow-depth:solid:$tooltip-background-color
   :dark:border-left :$tooltip-arrow-depth:solid:$tooltip-background-color-inverse
   :border-top       :$tooltip-arrow-depth:solid:transparent
   :border-bottom    :$tooltip-arrow-depth:solid:transparent})

(defclass ^{:kushi/chunk :kushi/kushi-ui-defclass} 
  kushi-tooltip-mounting
  :.hidden)
