(ns kushi.playground.shared-styles
  (:require
   [kushi.playground.util :as util :refer-macros (let-map)]
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
  :bc--$gray-200
  :&_p:margin-block--0
  :&_.kushi-ui-opt-desc&_p:fs--0.775rem
  :&_a:td--underline)

(defclass codebox
  :.transition
  {:p                         :20px:50px:20px:20px
   :bgc                       :$accent-50
   "has-ancestor(.dark):bgc"  :$accent-900
   :c                         :$accent-750
   "has-ancestor(.dark):c"    :$accent-100
   :mbs                       :7px
   :fs                        :$kushi-playground-codebox-snippet_font-size||$xsmall
   :&_.code:fs                :$kushi-playground-codebox-snippet_font-size||$xsmall
   :&_code:fs                 :$kushi-playground-codebox-snippet_font-size||$xsmall
   :&_.code:bgc               :transparent
   :&_code:bgc                :transparent
   :fw                        400
   :bisw                      :5px
   :biss                      :solid
   :bisc                      :$accent-200
   "has-ancestor(.dark):bisc" :$accent-750})

(defclass kushi-treenav-section-level-1
  :mbs--1em
  :>span:mbs--0.5em
  :first-child:>span:mbs--0em)

(defclass kushi-treenav-section-level-1-header
  :.pointer
  :fs--$kushi-playground-sidenav-section-header_font-size
  :d--block
  :line-height--$body-copy-line-height
  :pb--0.4em
  :lh--1.25
  :pi--1.8em:0.9em
  :bisw--3px
  :biss--solid
  :bisc--transparent)

(defclass kushi-playground-main-section
  {:flex-grow 0
   :bgc       :transparent
   :w         :100%
   :max-width :$components-menu-width
   :md:w      "calc(var(--components-menu-width) - 80px)"
   :xl:w      :$components-menu-width})

(defclass kushi-playground-meta-desc-label
  {:fs         :$kushi-playground-meta-desc-label_font-size||$xsmall
   :fw         :$kushi-playground-meta-desc-label_font-weight||$wee-bold
   :font-style :$kushi-playground-meta-desc-label_font-style||italic
   :tt         :$kushi-playground-meta-desc-label_text-transform||uppercase
   :c          :$neutral-secondary-foreground
   :dark:c     :$neutral-secondary-foreground-inverse })

(defclass kushi-treenav-section-header
  :.relative
  :.flex-row-c
  :md:jc--fs)

(defclass hover-trailing-fade-out
  :transition-duration--350ms
  :hover:transition-duration--0ms)

(defclass kushi-playground-with-rainbow-keys

  ;trailing fade out rainbow keys
  ["&_.hover-trailing-fade-out-wrapper:nth-child(8n+1)>.hover-trailing-fade-out:hover" {:bgc :$gold-50 :color :$gold-1000}]
  ["&_.hover-trailing-fade-out-wrapper:nth-child(8n+1)>.hover-trailing-fade-out[aria-expanded='true']:hover" {:bgc :$neutral-50 :color :$neutral-1000}]
  ["&_.hover-trailing-fade-out-wrapper:nth-child(8n+2)>.hover-trailing-fade-out:hover" {:bgc :$orange-50 :c :$orange-1000}]
  ["&_.hover-trailing-fade-out-wrapper:nth-child(8n+2)>.hover-trailing-fade-out[aria-expanded='true']:hover" {:bgc :$neutral-50 :c :$neutral-1000}]
  ["&_.hover-trailing-fade-out-wrapper:nth-child(8n+3)>.hover-trailing-fade-out:hover" {:bgc :$red-50 :c :$red-1000}]
  ["&_.hover-trailing-fade-out-wrapper:nth-child(8n+3)>.hover-trailing-fade-out[aria-expanded='true']:hover" {:bgc :$neutral-50 :c :$neutral-1000}]
  ["&_.hover-trailing-fade-out-wrapper:nth-child(8n+4)>.hover-trailing-fade-out:hover" {:bgc :$magenta-50 :c :$magenta-1000}]
  ["&_.hover-trailing-fade-out-wrapper:nth-child(8n+4)>.hover-trailing-fade-out[aria-expanded='true']:hover" {:bgc :$neutral-50 :c :$neutral-1000}]
  ["&_.hover-trailing-fade-out-wrapper:nth-child(8n+5)>.hover-trailing-fade-out:hover" {:bgc :$purple-50 :c :$purple-1000}]
  ["&_.hover-trailing-fade-out-wrapper:nth-child(8n+5)>.hover-trailing-fade-out[aria-expanded='true']:hover" {:bgc :$neutral-50 :c :$neutral-1000}]
  ["&_.hover-trailing-fade-out-wrapper:nth-child(8n+6)>.hover-trailing-fade-out:hover" {:bgc :$blue-50 :c :$blue-1000}]
  ["&_.hover-trailing-fade-out-wrapper:nth-child(8n+6)>.hover-trailing-fade-out[aria-expanded='true']:hover" {:bgc :$neutral-50 :c :$neutral-1000}]
  ["&_.hover-trailing-fade-out-wrapper:nth-child(8n+7)>.hover-trailing-fade-out:hover" {:bgc :$green-50 :c :$green-1000}]
  ["&_.hover-trailing-fade-out-wrapper:nth-child(8n+7)>.hover-trailing-fade-out[aria-expanded='true']:hover" {:bgc :$neutral-50 :c :$neutral-1000}]
  ["&_.hover-trailing-fade-out-wrapper:nth-child(8n+8)>.hover-trailing-fade-out:hover" {:bgc :$yellow-50 :c :$yellow-1000}]
  ["&_.hover-trailing-fade-out-wrapper:nth-child(8n+8)>.hover-trailing-fade-out[aria-expanded='true']:hover" {:bgc :$neutral-50 :c :$neutral-1000}]

  ;; Inverse (dark-theme) trailing fade out rainbow keys
  ["dark:&_.hover-trailing-fade-out-wrapper:nth-child(8n+1)>.hover-trailing-fade-out:hover" {:bgc :$gold-950 :color :$gold-50}]
  ["dark:&_.hover-trailing-fade-out-wrapper:nth-child(8n+2)>.hover-trailing-fade-out:hover" {:bgc :$orange-950 :c :$orange-50}]
  ["dark:&_.hover-trailing-fade-out-wrapper:nth-child(8n+3)>.hover-trailing-fade-out:hover" {:bgc :$red-950 :c :$red-50}]
  ["dark:&_.hover-trailing-fade-out-wrapper:nth-child(8n+4)>.hover-trailing-fade-out:hover" {:bgc :$magenta-950 :c :$magenta-50}]
  ["dark:&_.hover-trailing-fade-out-wrapper:nth-child(8n+5)>.hover-trailing-fade-out:hover" {:bgc :$purple-950 :c :$purple-50}]
  ["dark:&_.hover-trailing-fade-out-wrapper:nth-child(8n+6)>.hover-trailing-fade-out:hover" {:bgc :$blue-950 :c :$blue-50}]
  ["dark:&_.hover-trailing-fade-out-wrapper:nth-child(8n+7)>.hover-trailing-fade-out:hover" {:bgc :$green-950 :c :$green-50}]
  ["dark:&_.hover-trailing-fade-out-wrapper:nth-child(8n+8)>.hover-trailing-fade-out:hover" {:bgc :$yellow-950 :c :$yellow-50}]
  )

(defclass no-hover-bgc :bgc--transparent!important)

(defclass kushi-demo-stage
  :min-height--135px
  :p--30px:15px
  :bw--1px
  :bs--solid
  :bc--$gray-300
  :dark:bc--$gray-700
  :mb--10px
  :&_.kushi-input:min-width--220px)

(defclass kushi-playground-demobox
  :&_.kushi-playground-examples-input-row-wrapper:bbe--0px:solid:#eee
  :&_.kushi-playground-examples-input-row-wrapper:min-height--50px
  :&_.kushi-playground-examples-input-row-wrapper:padding-block--0.75em
  :md:&_.kushi-playground-examples-input-row-wrapper:padding-block--0.5em
  :&_.kushi-radio-button-wrapper:margin-inline--0:0.666em
  :&_.kushi-radio-button-wrapper:margin-block--0.125em)

(defclass kushi-playground-demobox-ui-icon
  :fs--1rem!important
  :>div.kushi-icon>span:fs--1rem!important
  :b--none!important
  :bw--0!important
  :p--0.425em!important
  :c--$neutral-minimal-color!important
  :dark:c--$neutral-minimal-color-inverse!important
  :bgc--transparent!important
  :hover:bgc--transparent!important
  :active:bgc--transparent!important)

(defclass kushi-playground-demobox-ui-icon-stage-control
  :p--0.5em!important
  :hover:bgc--$neutral-200!important
  :dark:hover:bgc--$neutral-750!important
  :active:bgc--$neutral-300!important
  :dark:active:bgc--$neutral-650!important
  ["&[aria-selected='true']:bgc" :$neutral-100!important]
  ["dark:&[aria-selected='true']:bgc" :$neutral-750!important]
  )

(defclass truncate
  :overflow--hidden
  :text-overflow--ellipsis
  :white-space--nowrap)

(defclass kushi-playground-dev-mode-portal
  :.fixed
  :.transition
  :.xxslow!
  :transition-property--opacity
  :w--0
  :h--0
  :o--0
  :dark:bgc--$gray-1000
  :bgc--white
  :zi--10000)


(defclass kushi-playground-dev-mode-hidden
  :overflow--hidden)


(defclass kushi-playground-dev-mode
  :>#app:o--0
  :>#app:transition--all
  :>#app:transition-duration--500ms
  :>#kushi-playground-dev-mode-portal:o--1
  :>#kushi-playground-dev-mode-portal:w--100vw
  :>#kushi-playground-dev-mode-portal:h--100vh)


(defclass kushi-playground-sidenav-wrapper
  :h--100vh
  :.grow
  :d--none
  :xl:d--flex
  :xl:jc--c
  :ai--fs
  [:w "calc(100% - (708px / 2))"]
  [:max-width :$kushi-playground-sidenav-max-width]
  [:xl:max-width :unset])


;; New May 2024
(defclass playground-example-row-bounded
  [:&_.instance-code
   {:border-radius :$rounded
    :w             :fit-content
    :bgc           :transparent
    :p             :1em
    :pie           :1.5em
    :b             :1px:solid:$neutral-150
    }]
  [:dark:&_.instance-code
   {:bgc :transparent
    :b   :1px:solid:$neutral-850}])

(defclass playground-example-row-bounded-parent
  {:border-radius :$rounded
   :w             :fit-content
   :bgc           :transparent
   :p             :1em
   :pie           :1.5em
   :b             :1px:solid:$neutral-150
   :dark:b        :1px:solid:$neutral-850})




(defclass playground-pane-box-shadow
  :box-shadow--0:0:13px:8px:white|0:0:10px:9px:white)

(defclass all-components-sidenav-header
  :.flex-col-c
  :.semi-bold
  :.neutralize
  :ai--c
  :height--$navbar-height)

(defclass neutralize
  :.transition
  :bgc--$background-color
  :dark:bgc--$background-color-inverse
  :c--$foreground-color
  :dark:c--$foreground-color-inverse)

(defclass neutralize-secondary
  :.transition
  :bgc--$background-color
  :dark:bgc--$background-color-inverse
  :c--$neutral-secondary-foreground
  :dark:c--$neutral-secondary-foreground-inverse)
