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
  :bc--$gray-200
  :&_p:margin-block--0
  :&_.kushi-ui-opt-desc&_p:fs--0.775rem
  :&_a:td--underline)


(defclass codebox
  :.transition
  ;; :&.codebox-flashing.bisc--$purple-600
  {:p                         :20px:50px:20px:20px
   :bgc                       :$gray-50
   "has-ancestor(.dark):bgc"  :$gray-900
   :mbs                       :7px
   :fs                        :$kushi-playground-codebox-snippet_font-size|$xsmall
   :&_.code:fs                :$kushi-playground-codebox-snippet_font-size|$xsmall
   :&_code:fs                 :$kushi-playground-codebox-snippet_font-size|$xsmall
   :&_.code:bgc               :transparent
   :&_code:bgc                :transparent
   :fw                        400
   :bisw                      :5px
   :biss                      :solid
   :bisc                      :$gray-300
   "has-ancestor(.dark):bisc" :$gray-700}
   )

#_(sx 'faaaar
    :c--red
    :bgc--gold
    {12 :gold
     :c :blue}
    {:kushi/tracing? true})

(defclass foo
  :c--red
  :bgc--gold
  #_{12 :gold
   :c :red}
  {:kushi/tracing? true})

(defclass kushi-treenav-section-level-1
  :mbs--1em
  :>span:mbs--0.5em
  :first-child:>span:mbs--0em)

(defclass kushi-treenav-section-level-1-header
  :.pointer
  :fs--$kushi-playground-sidenav-section-header_font-size
  :d--block
  :tt--u
  :line-height--$body-copy-line-height
  :pb--0.4em
  :lh--1.25
  :pi--1.8em:0.9em
  :bisw--3px
  :biss--solid
  :bisc--transparent)

(defclass kushi-playground-main-section-wrapper
  :.flex-col-fs
  :ai--c
  :width--100%
  :md:width--auto
  :flex-shrink--0
  :md:flex-grow--1
  :lg:flex-grow--0
  :padding-inline--$page-padding-inline)

(defclass kushi-playground-main-section
  {:flex-grow 0
   :bgc       :transparent
   :w         :100%
   :max-width :$components-menu-width
   :md:w      "calc(var(--components-menu-width) - 80px)"
   :xl:w      :$components-menu-width})

(defclass kushi-playground-meta-desc-label
  {:fs         :$kushi-playground-meta-desc-label_font-size|$xsmall
   :fw         :$kushi-playground-meta-desc-label_font-weight|$wee-bold
   :font-style :$kushi-playground-meta-desc-label_font-style|italic
   :tt         :$kushi-playground-meta-desc-label_text-transform|uppercase
   :c          :$neutral-secondary-fg
   :dark:c     :$neutral-secondary-fg-inverse })

(defclass kushi-treenav-section-header
  :.relative
  :.flex-row-fs)

(defclass hover-trailing-fade-out
  :transition-duration--350ms
  :hover:transition-duration--0ms)

(defclass kushi-playground-with-rainbow-keys

  ;; Main sections rainbow keys
  ["&_.kushi-collapse:nth-child(8n+1)>.kushi-collapse-header:hover:bgc" :$gold-50]
  ["&_.kushi-collapse:nth-child(8n+1)>.kushi-collapse-header[aria-expanded='true']:hover:bgc" :$neutral-50]
  ["&_.kushi-collapse:nth-child(8n+2)>.kushi-collapse-header:hover:bgc" :$orange-50]
  ["&_.kushi-collapse:nth-child(8n+2)>.kushi-collapse-header[aria-expanded='true']:hover:bgc" :$neutral-50]
  ["&_.kushi-collapse:nth-child(8n+3)>.kushi-collapse-header:hover:bgc" :$red-50]
  ["&_.kushi-collapse:nth-child(8n+3)>.kushi-collapse-header[aria-expanded='true']:hover:bgc" :$neutral-50]
  ["&_.kushi-collapse:nth-child(8n+4)>.kushi-collapse-header:hover:bgc" :$magenta-50]
  ["&_.kushi-collapse:nth-child(8n+4)>.kushi-collapse-header[aria-expanded='true']:hover:bgc" :$neutral-50]
  ["&_.kushi-collapse:nth-child(8n+5)>.kushi-collapse-header:hover:bgc" :$purple-50]
  ["&_.kushi-collapse:nth-child(8n+5)>.kushi-collapse-header[aria-expanded='true']:hover:bgc" :$neutral-50]
  ["&_.kushi-collapse:nth-child(8n+6)>.kushi-collapse-header:hover:bgc" :$blue-50]
  ["&_.kushi-collapse:nth-child(8n+6)>.kushi-collapse-header[aria-expanded='true']:hover:bgc" :$neutral-50]
  ["&_.kushi-collapse:nth-child(8n+7)>.kushi-collapse-header:hover:bgc" :$green-50]
  ["&_.kushi-collapse:nth-child(8n+7)>.kushi-collapse-header[aria-expanded='true']:hover:bgc" :$neutral-50]
  ["&_.kushi-collapse:nth-child(8n+8)>.kushi-collapse-header:hover:bgc" :$yellow-50]
  ["&_.kushi-collapse:nth-child(8n+8)>.kushi-collapse-header[aria-expanded='true']:hover:bgc" :$neutral-50]

  ;; Main sections rainbow keys inverse
  ["dark:&_.kushi-collapse:nth-child(8n+1)>.kushi-collapse-header:hover:bgc" :$gold-950]
  ["dark:&_.kushi-collapse:nth-child(8n+1)>.kushi-collapse-header[aria-expanded='true']:hover:bgc" :$neutral-900]
  ["dark:&_.kushi-collapse:nth-child(8n+2)>.kushi-collapse-header:hover:bgc" :$orange-950]
  ["dark:&_.kushi-collapse:nth-child(8n+2)>.kushi-collapse-header[aria-expanded='true']:hover:bgc" :$neutral-900]
  ["dark:&_.kushi-collapse:nth-child(8n+3)>.kushi-collapse-header:hover:bgc" :$red-950]
  ["dark:&_.kushi-collapse:nth-child(8n+3)>.kushi-collapse-header[aria-expanded='true']:hover:bgc" :$neutral-900]
  ["dark:&_.kushi-collapse:nth-child(8n+4)>.kushi-collapse-header:hover:bgc" :$magenta-950]
  ["dark:&_.kushi-collapse:nth-child(8n+4)>.kushi-collapse-header[aria-expanded='true']:hover:bgc" :$neutral-900]
  ["dark:&_.kushi-collapse:nth-child(8n+5)>.kushi-collapse-header:hover:bgc" :$purple-950]
  ["dark:&_.kushi-collapse:nth-child(8n+5)>.kushi-collapse-header[aria-expanded='true']:hover:bgc" :$neutral-900]
  ["dark:&_.kushi-collapse:nth-child(8n+6)>.kushi-collapse-header:hover:bgc" :$blue-950]
  ["dark:&_.kushi-collapse:nth-child(8n+6)>.kushi-collapse-header[aria-expanded='true']:hover:bgc" :$neutral-900]
  ["dark:&_.kushi-collapse:nth-child(8n+7)>.kushi-collapse-header:hover:bgc" :$green-950]
  ["dark:&_.kushi-collapse:nth-child(8n+7)>.kushi-collapse-header[aria-expanded='true']:hover:bgc" :$neutral-900]
  ["dark:&_.kushi-collapse:nth-child(8n+8)>.kushi-collapse-header:hover:bgc" :$yellow-950]
  ["dark:&_.kushi-collapse:nth-child(8n+8)>.kushi-collapse-header[aria-expanded='true']:hover:bgc" :$neutral-900]

  ;; Sidenav rainbow keys
  ["&_.kushi-playground-sidenav-section-item-wrapper:nth-child(8n+1)>a:hover:c" :$gold-1000]
  ["&_.kushi-playground-sidenav-section-item-wrapper:nth-child(8n+2)>a:hover:c" :$orange-1000]
  ["&_.kushi-playground-sidenav-section-item-wrapper:nth-child(8n+3)>a:hover:c" :$red-1000]
  ["&_.kushi-playground-sidenav-section-item-wrapper:nth-child(8n+4)>a:hover:c" :$magenta-1000]
  ["&_.kushi-playground-sidenav-section-item-wrapper:nth-child(8n+5)>a:hover:c" :$purple-1000]
  ["&_.kushi-playground-sidenav-section-item-wrapper:nth-child(8n+6)>a:hover:c" :$blue-1000]
  ["&_.kushi-playground-sidenav-section-item-wrapper:nth-child(8n+7)>a:hover:c" :$green-1000]
  ["&_.kushi-playground-sidenav-section-item-wrapper:nth-child(8n+8)>a:hover:c" :$yellow-1000]

  ["&_.kushi-playground-sidenav-section-item-wrapper:nth-child(8n+1)>a:hover:bgc" :$gold-50]
  ["&_.kushi-playground-sidenav-section-item-wrapper:nth-child(8n+2)>a:hover:bgc" :$orange-50]
  ["&_.kushi-playground-sidenav-section-item-wrapper:nth-child(8n+3)>a:hover:bgc" :$red-50]
  ["&_.kushi-playground-sidenav-section-item-wrapper:nth-child(8n+4)>a:hover:bgc" :$magenta-50]
  ["&_.kushi-playground-sidenav-section-item-wrapper:nth-child(8n+5)>a:hover:bgc" :$purple-50]
  ["&_.kushi-playground-sidenav-section-item-wrapper:nth-child(8n+6)>a:hover:bgc" :$blue-50]
  ["&_.kushi-playground-sidenav-section-item-wrapper:nth-child(8n+7)>a:hover:bgc" :$green-50]
  ["&_.kushi-playground-sidenav-section-item-wrapper:nth-child(8n+8)>a:hover:bgc" :$yellow-50]

  ;; Sidenav rainbow keys inverse
  ["dark:&_.kushi-playground-sidenav-section-item-wrapper:nth-child(8n+1)>a:hover:c" :$gold-100]
  ["dark:&_.kushi-playground-sidenav-section-item-wrapper:nth-child(8n+2)>a:hover:c" :$orange-100]
  ["dark:&_.kushi-playground-sidenav-section-item-wrapper:nth-child(8n+3)>a:hover:c" :$red-100]
  ["dark:&_.kushi-playground-sidenav-section-item-wrapper:nth-child(8n+4)>a:hover:c" :$magenta-100]
  ["dark:&_.kushi-playground-sidenav-section-item-wrapper:nth-child(8n+5)>a:hover:c" :$purple-100]
  ["dark:&_.kushi-playground-sidenav-section-item-wrapper:nth-child(8n+6)>a:hover:c" :$blue-100]
  ["dark:&_.kushi-playground-sidenav-section-item-wrapper:nth-child(8n+7)>a:hover:c" :$green-100]
  ["dark:&_.kushi-playground-sidenav-section-item-wrapper:nth-child(8n+8)>a:hover:c" :$yellow-100]

  ["dark:&_.kushi-playground-sidenav-section-item-wrapper:nth-child(8n+1)>a:hover:bgc" :$gold-950]
  ["dark:&_.kushi-playground-sidenav-section-item-wrapper:nth-child(8n+2)>a:hover:bgc" :$orange-950]
  ["dark:&_.kushi-playground-sidenav-section-item-wrapper:nth-child(8n+3)>a:hover:bgc" :$red-950]
  ["dark:&_.kushi-playground-sidenav-section-item-wrapper:nth-child(8n+4)>a:hover:bgc" :$magenta-950]
  ["dark:&_.kushi-playground-sidenav-section-item-wrapper:nth-child(8n+5)>a:hover:bgc" :$purple-950]
  ["dark:&_.kushi-playground-sidenav-section-item-wrapper:nth-child(8n+6)>a:hover:bgc" :$blue-950]
  ["dark:&_.kushi-playground-sidenav-section-item-wrapper:nth-child(8n+7)>a:hover:bgc" :$green-950]
  ["dark:&_.kushi-playground-sidenav-section-item-wrapper:nth-child(8n+8)>a:hover:bgc" :$yellow-950]
)

(defclass kushi-demo-stage
  :min-height--135px
  :p--30px:15px
  :bw--1px
  :bs--solid
  :bc--$gray-300
  :dark:bc--$gray-800
  :mb--10px)

(defclass kushi-playground-demobox
  :&_.kushi-playground-examples-input-row-wrapper:bbe--0px:solid:#eee
  :&_.kushi-playground-examples-input-row-wrapper:min-height--50px
  :&_.kushi-playground-examples-input-row-wrapper:padding-block--0.75em
  :md:&_.kushi-playground-examples-input-row-wrapper:padding-block--0.5em
  :&_.kushi-radio-button-wrapper:margin-inline--0:0.666em
  :&_.kushi-radio-button-wrapper:margin-block--0.125em)
