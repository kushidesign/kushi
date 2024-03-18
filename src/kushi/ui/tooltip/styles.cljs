(ns kushi.ui.tooltip.styles
  (:require
   [goog.string]
   [kushi.core :refer (defclass)]))

(defclass ^{:kushi/chunk :kushi/kushi-ui-defclass}
  kushi-floating-tooltip
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
  [:$_kushi-floating-tooltip-translate-y-shift :0px]
  [:$_kushi-floating-tooltip-translate-x-shift :0px]
  [:$_kushi-floating-tooltip-translate-y-base :0px]
  [:$_kushi-floating-tooltip-translate-x-base :0px]
  [:$_kushi-floating-tooltip-translate-x "calc(var(--_kushi-floating-tooltip-translate-x-base) + var(--_kushi-floating-tooltip-translate-x-shift))"]
  [:$_kushi-floating-tooltip-translate-y  "calc(var(--_kushi-floating-tooltip-translate-y-base) + var(--_kushi-floating-tooltip-translate-y-shift))"]
  :translate--$_kushi-floating-tooltip-translate-x:$_kushi-floating-tooltip-translate-y
  :transition-delay--$tooltip-delay-duration
  :zi--auto
  :top--0
  :left--0
  :c--$tooltip-color
  :bgc--red
  :bgc--$tooltip-background-color
  :w--max-content
  :p--0.5em:1em)


;; block start
(defclass ^{:kushi/chunk :kushi/kushi-ui-defclass}
  kushi-floating-tooltip-tl 
  :transform-origin--bottom:left)

(defclass ^{:kushi/chunk :kushi/kushi-ui-defclass} 
  kushi-floating-tooltip-t 
  :transform-origin--bottom:center)

(defclass ^{:kushi/chunk :kushi/kushi-ui-defclass} 
  kushi-floating-tooltip-tr
  :transform-origin--bottom:right)


;; inline-end
(defclass ^{:kushi/chunk :kushi/kushi-ui-defclass} 
  kushi-floating-tooltip-rt
  :transform-origin--top:left)

(defclass ^{:kushi/chunk :kushi/kushi-ui-defclass} 
  kushi-floating-tooltip-r 
  :transform-origin--center:left)

(defclass ^{:kushi/chunk :kushi/kushi-ui-defclass}
  kushi-floating-tooltip-rb
  :transform-origin--bottom:left)


;; block-end
(defclass ^{:kushi/chunk :kushi/kushi-ui-defclass}
  kushi-floating-tooltip-br
  :transform-origin--top:right)

(defclass ^{:kushi/chunk :kushi/kushi-ui-defclass}
  kushi-floating-tooltip-b
  :transform-origin--top:center)

(defclass ^{:kushi/chunk :kushi/kushi-ui-defclass}
  kushi-floating-tooltip-bl
  :transform-origin--top:left)


;; inline-start
(defclass ^{:kushi/chunk :kushi/kushi-ui-defclass}
  kushi-floating-tooltip-lt
  :transform-origin--top:right)

(defclass ^{:kushi/chunk :kushi/kushi-ui-defclass} 
  kushi-floating-tooltip-l 
  :transform-origin--center:right)

(defclass ^{:kushi/chunk :kushi/kushi-ui-defclass} 
  kushi-floating-tooltip-lb
  :transform-origin--bottom:right)


;; corner positioning 
(defclass ^{:kushi/chunk :kushi/kushi-ui-defclass}
  kushi-floating-tooltip-tlc 
  :beer--0
  :transform-origin--bottom:right)

(defclass ^{:kushi/chunk :kushi/kushi-ui-defclass}
  kushi-floating-tooltip-ltc
  :beer--0
  :transform-origin--bottom:right )

(defclass ^{:kushi/chunk :kushi/kushi-ui-defclass} 
  kushi-floating-tooltip-trc
  :besr--0
  :transform-origin--bottom:left)

(defclass ^{:kushi/chunk :kushi/kushi-ui-defclass}
  kushi-floating-tooltip-rtc
  :besr--0
  :transform-origin--bottom:left)

(defclass ^{:kushi/chunk :kushi/kushi-ui-defclass} 
  kushi-floating-tooltip-brc
  :bssr--0
  :transform-origin--top:left)

(defclass ^{:kushi/chunk :kushi/kushi-ui-defclass} 
  kushi-floating-tooltip-rbc
  :bssr--0
  :transform-origin--top:left)

(defclass ^{:kushi/chunk :kushi/kushi-ui-defclass}
  kushi-floating-tooltip-blc
 :bser--0
 :transform-origin--top:right)

(defclass ^{:kushi/chunk :kushi/kushi-ui-defclass}
  kushi-floating-tooltip-lbc
  :bser--0
  :transform-origin--top:right)

(defclass ^{:kushi/chunk :kushi/kushi-ui-defclass}
  kushi-floating-tooltip-arrowless
  :beer--$tooltip-border-radius
  :besr--$tooltip-border-radius
  :bssr--$tooltip-border-radius
  :bser--$tooltip-border-radius
  :&>.kushi-floating-tooltip-arrow:d--none)

(defclass ^{:kushi/chunk :kushi/kushi-ui-defclass} 
  kushi-floating-tooltip-arrow
  :w--0
  :h--0
  :zi--10
  :.absolute
  :.transition
  :.xxfast
  :&.hidden:border--0!important
  :transition-property--translate
  :transition-timing-function--$tooltip-arrow-transition-timing-function
  :transition-duration--$tooltip-arrow-transition-duration
  [:translate "calc(var(--__ktt-x, 0px) + var(--__ktt-shift-x, 0px)) calc(var(--__ktt-y, 0px) + var(--__ktt-shift-y, 0px))"])

(defclass ^{:kushi/chunk :kushi/kushi-ui-defclass}
  kushi-floating-tooltip-arrow-pointing-down
  {:border-top      :$tooltip-arrow-depth:solid:$tooltip-background-color
   :dark:border-top :$tooltip-arrow-depth:solid:$tooltip-background-color-inverse
   :border-left     :$tooltip-arrow-depth:solid:transparent
   :border-right    :$tooltip-arrow-depth:solid:transparent})

(defclass ^{:kushi/chunk :kushi/kushi-ui-defclass}
  kushi-floating-tooltip-arrow-pointing-up
  {:border-bottom      :$tooltip-arrow-depth:solid:$tooltip-background-color
   :dark:border-bottom :$tooltip-arrow-depth:solid:$tooltip-background-color-inverse
   :border-left        :$tooltip-arrow-depth:solid:transparent
   :border-right       :$tooltip-arrow-depth:solid:transparent})

(defclass ^{:kushi/chunk :kushi/kushi-ui-defclass}
  kushi-floating-tooltip-arrow-pointing-left
  {:border-right      :$tooltip-arrow-depth:solid:$tooltip-background-color
   :dark:border-right :$tooltip-arrow-depth:solid:$tooltip-background-color-inverse
   :border-top        :$tooltip-arrow-depth:solid:transparent
   :border-bottom     :$tooltip-arrow-depth:solid:transparent})

(defclass ^{:kushi/chunk :kushi/kushi-ui-defclass} 
  kushi-floating-tooltip-arrow-pointing-right
  {:border-left      :$tooltip-arrow-depth:solid:$tooltip-background-color
   :dark:border-left :$tooltip-arrow-depth:solid:$tooltip-background-color-inverse
   :border-top       :$tooltip-arrow-depth:solid:transparent
   :border-bottom    :$tooltip-arrow-depth:solid:transparent})

(defclass ^{:kushi/chunk :kushi/kushi-ui-defclass} 
  kushi-floating-tooltip-mounting
  :.hidden)
