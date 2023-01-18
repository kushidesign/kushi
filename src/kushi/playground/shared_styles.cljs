(ns kushi.playground.shared-styles
  (:require
   [kushi.playground.util :as util :refer-macros (keyed let-map)]
   [kushi.core :refer (defclass defkeyframes)]))

(defkeyframes jiggle
  [:0% {:transform "translate(1px, 1px) rotate(0deg) scale(1.55)"}]
  [:10% {:transform "translate(-1px, -2px) rotate(-1deg) scale(1.55)"}]
  [:20% {:transform "translate(-3px, 0px) rotate(1deg) scale(1.55)"}]
  [:30% {:transform "translate(3px, 2px) rotate(0deg) scale(1.55)"}]
  [:40% {:transform "translate(1px, -1px) rotate(1deg) scale(1.55)"}]
  [:50% {:transform "translate(-1px, 2px) rotate(-1deg) scale(1.55)"}]
  [:60% {:transform "translate(-3px, 1px) rotate(0deg) scale(1.55)"}]
  [:70% {:transform "translate(3px, 1px) rotate(-1deg) scale(1.55)"}]
  [:80% {:transform "translate(-1px, -1px) rotate(1deg) scale(1.55)"}]
  [:90% {:transform "translate(1px, 2px) rotate(0deg) scale(1.55)"}]
  [:100% {:transform "translate(1px, -2px) rotate(-1deg) scale(1.55)"}])

 (defkeyframes jiggle2
  [:0% {:transform "rotate(0deg) scale(1.55)"}]
  [:18% {:transform "rotate(-5deg) scale(1.55)"}]
  [:36% {:transform "rotate(5deg) scale(1.55)"}]
  [:54% {:transform "rotate(0deg) scale(1.55)"}]
  [:72% {:transform "rotate(5deg) scale(1.55)"}]
  [:90% {:transform "rotate(-5deg) scale(1.55)"}]
  [:100% {:transform "rotate(0deg) scale(1.55)"}])


(def shared-values
  (let-map [topnav-height                         0
            main-view-wrapper-padding-block-start 0
            scroll-to-component-menu-item-y       (- (+ topnav-height main-view-wrapper-padding-block-start))
            scroll-window-by-px                   2]))

(defclass twirl
  :animation--y-axis-spinner:12s:linear:infinite)

(defclass twirl2x
  :animation--y-axis-spinner:12s:linear:infinite)

(defclass twirl4x
  :animation--y-axis-spinner:12s:linear:infinite)

(defclass section-focused
  :bgc--#fafafa)

(defclass kushi-opts-grid-row-item
  :padding-block--1.5em
  :bbe--1px:solid:#efefef
  :bc--:$gray200
  :&_p:margin-block--0
  :&_.kushi-ui-opt-desc&_p:fs--0.775rem
  :&_a:td--underline)

(defclass codebox
  :.small
  :.transition
  {:p                       :20px:50px:20px:20px
   :bgc                     :$gray50
   "has-ancestor(.dark):bgc" :$gray900
   :mbs                     :7px
   :&_.cm-line:fs           :0.8rem!important
   :&_.code:bgc             :transparent
   :&_code:bgc              :transparent
   :fw                      400
   :bisw                    :5px
   :biss                    :solid
   :bisc                    :$gray300
   "has-ancestor(.dark):bisc" :$gray700})

(defclass kushi-treenav-section-level-1
  :mbs--1em
  :>span:mbs--0.5em
  :first-child:>span:mbs--0em)

(defclass kushi-treenav-section-level-1-header
  :.pointer
  :.medium
  :d--block
  :tt--u
  :line-height--$body-copy-line-height
  :pb--0.4em
  :lh--1.25
  :pi--1.8em:0.9em
  :bisw--3px
  :biss--solid
  :bisc--transparent)

(defclass kushi-main-section-wrapper
  :.flex-col-fs
  :ai--c
  :width--100%
  :md:width--auto
  :flex-shrink--0
  :md:flex-grow--1
  :lg:flex-grow--0
  :padding-inline--$page-padding-inline)

(defclass kushi-main-section
  {:flex-grow 0
   :bgc       :transparent
   :w         :100%
   :max-width :$components-menu-width
   :md:w      "calc(var(--components-menu-width) - 80px)"
   :xl:w      :$components-menu-width})

(defclass meta-desc-label
  :.italic
  :.uppercase
  :.xsmall
  :.wee-bold
  :c--$gray700
  ["has-ancestor(.dark):c" :$gray300])

(defclass kushi-treenav-section-header
  :.relative
  :.flex-row-fs
  :ai--c)



