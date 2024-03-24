(ns kushi.ui.dom.fune.styles
  (:require
   [goog.string]
   [kushi.core :refer (defclass)]))

(defclass ^{:kushi/chunk :kushi/kushi-ui-defclass}
  kushi-fune
  :.fixed
  :fs--$fune-font-size
  ;; :fs--100px
  :border-radius--$fune-border-radius
  ;; toggle this to none to get metrics
  :transition-property--opacity|translate|scale|transform
  ;; :transition-property--none
  :.transition
  :transition-duration--$fune-transition-duration
  :transition-timing-function--$fune-transition-timing-function
  ;; :scale--1
  :scale--$fune-initial-scale
  ;; [:$_kushi-fune-translate-y-shift :0px]
  ;; [:$_kushi-fune-translate-x-shift :0px]
  ;; [:$_kushi-fune-translate-y-base :0px]
  ;; [:$_kushi-fune-translate-x-base :0px]
  ;; [:$_kushi-fune-translate-x "calc(var(--_kushi-fune-translate-x-base) + var(--_kushi-fune-translate-x-shift))"]
  ;; [:$_kushi-fune-translate-y  "calc(var(--_kushi-fune-translate-y-base) + var(--_kushi-fune-translate-y-shift))"]
  ;; :translate--$_kushi-fune-translate-x:$_kushi-fune-translate-y
  :transition-delay--$fune-delay-duration
  :zi--auto
  :top--0
  :left--0
  :c--$fune-color
  :bgc--red
  :bgc--$fune-background-color
  :w--max-content
  :p--0.5em:1em)


;; block mixins
;; ------------------------------------------------

(defclass ^{:kushi/chunk :kushi/kushi-ui-defclass}
  kushi-fune-block-arrow-offset-mixin
  [:$arrow-plus-radius "calc(var(--fune-arrow-x-offset) + var(--fune-border-radius))"])

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
  [:$arrow-ty "-0.333px"]
  [:&_.kushi-fune-arrow {:top       :100%
                            :translate :$arrow-tx:$arrow-ty}])
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
  [:$arrow-ty "0.333px"]
  [:&_.kushi-fune-arrow {:bottom    :100%
                            :translate :$arrow-tx:$arrow-ty}])

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
  [:$arrow-plus-radius "calc(var(--fune-arrow-y-offset) + var(--fune-border-radius))"])

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
  [:$arrow-tx "0.333px"]
  [:&_.kushi-fune-arrow {:right    :100%
                            :translate :$arrow-tx:$arrow-ty}])

(defclass ^{:kushi/chunk :kushi/kushi-ui-defclass} 
  kushi-fune-rt
  :.kushi-fune-right-mixin
  :.kushi-fune-inline-top-mixin
  :transform-origin--top:left)

(defclass ^{:kushi/chunk :kushi/kushi-ui-defclass} 
  kushi-fune-r 
  :.kushi-fune-right-mixin
  :.kushi-fune-inline-center-mixin
  :transform-origin--center:left)

(defclass ^{:kushi/chunk :kushi/kushi-ui-defclass}
  kushi-fune-rb
  :.kushi-fune-right-mixin
  :.kushi-fune-inline-bottom-mixin
  :transform-origin--bottom:left)

;; inline-start
;; ------------------------------------------------
(defclass ^{:kushi/chunk :kushi/kushi-ui-defclass}
  kushi-fune-left-mixin
  :.kushi-fune-inline-arrow-offset-mixin
  :translate--$tx:$ty
  :$tx--$left-plc
  [:$arrow-tx "-0.333px"]
  [:&_.kushi-fune-arrow {:left      :100%
                            :translate :$arrow-tx:$arrow-ty}])

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

(defclass ^{:kushi/chunk :kushi/kushi-ui-defclass} 
  kushi-fune-arrow
  :w--0
  :h--0
  :zi--10
  :.absolute
  :.transition
  :.xxfast
  :&.hidden:border--0!important
  [:translate "var(--tx) var(--ty)"])

(defclass ^{:kushi/chunk :kushi/kushi-ui-defclass}
  kushi-fune-arrow-pointing-down
  {:border-top      :$fune-arrow-depth:solid:$fune-background-color
   :dark:border-top :$fune-arrow-depth:solid:$fune-background-color-inverse
   :border-left     :$fune-arrow-depth:solid:transparent
   :border-right    :$fune-arrow-depth:solid:transparent})

(defclass ^{:kushi/chunk :kushi/kushi-ui-defclass}
  kushi-fune-arrow-pointing-up
  {:border-bottom      :$fune-arrow-depth:solid:$fune-background-color
   :dark:border-bottom :$fune-arrow-depth:solid:$fune-background-color-inverse
   :border-left        :$fune-arrow-depth:solid:transparent
   :border-right       :$fune-arrow-depth:solid:transparent})

(defclass ^{:kushi/chunk :kushi/kushi-ui-defclass}
  kushi-fune-arrow-pointing-left
  {:border-right      :$fune-arrow-depth:solid:$fune-background-color
   :dark:border-right :$fune-arrow-depth:solid:$fune-background-color-inverse
   :border-top        :$fune-arrow-depth:solid:transparent
   :border-bottom     :$fune-arrow-depth:solid:transparent})

(defclass ^{:kushi/chunk :kushi/kushi-ui-defclass} 
  kushi-fune-arrow-pointing-right
  {:border-left      :$fune-arrow-depth:solid:$fune-background-color
   :dark:border-left :$fune-arrow-depth:solid:$fune-background-color-inverse
   :border-top       :$fune-arrow-depth:solid:transparent
   :border-bottom    :$fune-arrow-depth:solid:transparent})

(defclass ^{:kushi/chunk :kushi/kushi-ui-defclass} 
  kushi-fune-mounting
  :.hidden)
