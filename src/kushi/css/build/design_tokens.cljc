(ns kushi.css.build.design-tokens
  (:require
   [clojure.string :as string]
   [kushi.css.build.tokens-shared :as shared]))

;; Divisors  -------------------------------------------------------------------

(def divisor-color-scale
  "1 ~ 10
   e.g.:
   [:--divisor-color-1  :$neutral-50
    :--divisor-color-2  :$neutral-100
    :--divisor-color-3  :$neutral-150
    ...]"
  (for [n (range 1 11)]
    [(keyword (str "--divisor-color-" n))
     (keyword (str "$neutral-" (* 50 n))) ]))

(def divisor-color-scale-dark-mode
  "1 ~ 10
   e.g.:
   [:--divisor-color-1-dark-mode  :$neutral-950
    :--divisor-color-2-dark-mode  :$neutral-900
    :--divisor-color-3-dark-mode  :$neutral-850
    ...]"
  (for [n (range 1 11)]
    [(keyword (str "--divisor-color-" n "-dark-mode"))
     (keyword (str "$neutral-" (- 1000 (* 50 n))))]))

(defn divisor-compound-scale* [dark-mode?]
  (for [n (range 1 11)]
    [(keyword (str "--divisor-" n (when dark-mode? "-dark-mode")))
     (str "var(--divisor-thickness) var(--divisor-style) var(--divisor-color-"
          n
          (when dark-mode? "-dark-mode") ")") ]))

(def divisor-compound-scale
  (divisor-compound-scale* false))

(def divisor-compound-scale-dark-mode
  (divisor-compound-scale* true))

(def divisor-tokens
   (flatten
    (concat 
     [[:--divisor-thickness :1px]
      [:--divisor-style     :solid]
      [:--divisor-color-0   :transparent]]

     divisor-color-scale
     divisor-color-scale-dark-mode

     [[:--divisor-color         :$divisor-color-3]
      [:--divisor-color-dark-mode :$divisor-color-5-dark-mode]]

     [[:--divisor-0 "var(--divisor-thickness) var(--divisor-style) var(--divisor-color-0)"]]
     divisor-compound-scale

     divisor-compound-scale-dark-mode

     [[:--divisor         :$divisor-3]
      [:--divisor-dark-mode :$divisor-5-dark-mode]])))

;; Added a :ns entry to each family, populated with vector of namespaces of
;; kushi.ui components which make use of the family of tokens. These namespaces
;; 
(def design-tokens*
  ;; 
  [ 
   ;; Background grid
   ;; ------------------------------------------------------
   {:family   "Background Grid"
    :category ["background-patterns"]
    :tags     ["debugging" "development" "background" "patterns"]
    }
   [:--background-grid-size :16px
    :--background-grid-color "hsla(0 0% 90%)"
    :--background-grid-color-dark-mode "hsla(0 0% 25%)"
    :--background-grid (str "repeating-linear-gradient(to bottom, transparent, transparent var(--background-grid-size), var(--background-grid-color) var(--background-grid-size), var(--background-grid-color) calc(var(--background-grid-size) + 1px), transparent calc(var(--background-grid-size) + 1px)), "
                            "repeating-linear-gradient(to right,  transparent, transparent var(--background-grid-size), var(--background-grid-color) var(--background-grid-size), var(--background-grid-color) calc(var(--background-grid-size) + 1px), transparent calc(var(--background-grid-size) + 1px))")
    ]



   ;; Typography
   ;; ------------------------------------------------------
   

   ;; font-family
   ;; TODO should this live in basetheme?
   {:family   "Font-family"
    :category ["font-family"]
    :tags     ["typography" "fonts" "font-stack"]}
   [:--sans-serif-font-stack
    (string/join 
     ", "
     (map #(if (re-find #" " %)
             (str "\"" % "\"")
             %)
          ["Inter"
           "system-ui"
           "sans-serif"
           "-apple-system"
           "BlinkMacSystemFont"
           "Segoe UI"
           "Roboto"
           "Helvetica Neue"
           "Arial"
           "Noto Sans"
           "sans-serif"
           "Apple Color Emoji"
           "Segoe UI Emoji"
           "Segoe UI Symbol"
           "Noto Color Emoji"]))

    :--serif-font-stack                       
    "Cormorant, Times, serif"

    :--code-font-stack                        
    "\"JetBrains Mono\", \"Fira Code\", monospace"

    :--sans                                   
    :$sans-serif-font-stack

    :--serif                                  
    :$serif-font-stack
    ]


   ;; code
   {:family   "Code"
    :category ["code-blocks"]
    :desc     {:en "Styling of code blocks"}
    :ns       '[kushi.ui.code]
    :tags     ["code" "color" "typography" "block"]}
   [:--code-font-size                  :$font-size-sm
    :--code-padding-inline             :0.2em
    :--code-padding-block              :0.08em
    :--code-border-radius              :3px
    :--code-background-color           :$gray-100
    :--code-background-color-dark-mode :$gray-800
    :--code-color-dark-mode            :$gray-50
    ]


   ;; Intended for css prop `font-weight`
   {:family   "Font weight"
    :desc     {:en "Controls the weight of type"}
    :category ["font-weight"]
    :tags     ["font-weight" "typography"]}
   [:--font-weight-thin                                   100
    :--font-weight-extra-light                            200
    :--font-weight-light                                  300
    :--font-weight-normal                                 400
    :--font-weight-wee-bold                               500
    :--font-weight-semi-bold                              600
    :--font-weight-bold                                   700
    :--font-weight-extra-bold                             800
    :--font-weight-heavy                                  900
    :--font-weight-root-font-size                         :1rem
    ]


   ;; Intended for css prop `font-size`
   {:family   "Font size"
    :desc     {:en "Controls the size of type"}
    :category ["font-size"]
    :tags     ["font-size" "typography"]}
   [:--font-size-4xs    :0.64rem
    :--font-size-3xs     :0.67rem
    :--font-size-2xs      :0.71rem
    :--font-size-xs       :0.77rem
    :--font-size-sm        :0.86rem
    :--font-size-base       :1rem
    :--font-size-lg        :1.21rem
    :--font-size-xl       :1.485rem
    :--font-size-2xl      :1.86rem
    :--font-size-3xl     :2.36rem
    :--font-size-4xl    :3.03rem

    :--font-size-4xs-b  :0.655rem
    :--font-size-3xs-b   :0.685rem
    :--font-size-2xs-b    :0.733rem
    :--font-size-xs-b     :0.805rem
    :--font-size-sm-b      :0.92rem
    :--font-size-base-b     :1.1rem
    :--font-size-lg-b      :1.33rem
    :--font-size-xl-b     :1.655rem
    :--font-size-2xl-b    :2.085rem
    :--font-size-3xl-b   :2.68rem
    :--font-size-4xl-b  :3.475rem]


   ;; Intended for css prop `letterspacing`
   {:family   "Letter spacing"
    :desc     {:en "Controls the tracking (aka letter spacing) of the type"}
    :category ["letter-spacing"]
    :tags     ["font-size" "typography" "tracking" "letter-spacing"]}
   [:--letter-spacing-3xtight                               :-0.09em
    :--letter-spacing-2xtight                                :-0.06em
    :--letter-spacing-xtight                                 :-0.03em
    :--letter-spacing-tight                                  :-0.01em
    :--letter-spacing-base                                :0em
    :--letter-spacing-loose                                  :0.04em
    :--letter-spacing-xloose                                 :0.08em
    :--letter-spacing-2xloose                                :0.12em
    :--letter-spacing-3xloose                               :0.16em]


   ;; Intended for css props `border-width` for inputs
   {:family   "Input border weight"
    :desc     {:en "Controls the border-width for inputs"}
    :category ["input-border"]
    :ns       '[kushi.ui.input]
    :tags     ["border-width" "border" "input" "inputs"]}
   [:--input-border-weight-thin               :0.05em
    :--input-border-weight-extra-light        :0.07em
    :--input-border-weight-light              :0.09em
    :--input-border-weight-normal             :0.1em
    :--input-border-weight-wee-bold           :0.12em
    :--input-border-weight-semi-bold          :0.135em
    :--input-border-weight-bold               :0.165em
    :--input-border-weight-extra-bold         :0.195em
    :--input-border-weight-heavy              :0.21em
    ]


   ;; Intended for css props: border-*, general
   {:family   "Border"
    :desc     {:en "Controls general border styling"}
    :category ["border"]
    :tags     ["border" "borders"]}
   [:--border-width                           :1px
    :--border-style                           :solid
    ]

   ;; Intended for divisors and divisor-like borders
   
   {:family   "Divisors"
    :desc     {:en "Styling for border-like divisors"}
    :category ["divisor"]
    :ns       '[kushi.ui.divisor]
    :tags     ["divisors" "divisor"]}
   divisor-tokens


   ;; Intended for overlay placement
   {:family   "Overlay placement"
    :desc     {:en "Styling for overlays"}
    :category ["overlay"]
    :tags     ["overlays" "overlay"]}
   [:--overlay-placement-inline-offset        :12px
    :--overlay-placement-block-offset         :6px
    ]



   ;; Buttons
   ;; ------------------------------------------------------
   {:family   "Button padding"
    :desc     {:en "Styling for overlays"}
    :category ["button"]
    :ns       '[kushi.ui.button]
    :tags     ["button"]}
   [:--button-padding-inline              :0.9em
    :--icon-button-padding-inline         :0.57em
    :--button-padding-block               :0.55em
    :--button-with-icon-padding-inline-offset :0.75em
    ]

   {:family   "Button border styling"
    :desc     {:en "Styling borders for buttons"}
    :category ["button-border"]
    :tags     ["button"]}
   [:--button-border-width :1px]


   ;; Tags
   ;; ------------------------------------------------------
   {:family   "Tag border styling"
    :desc     {:en "Styling borders for tags"}
    :category ["tag-border"]
    :ns       '[kushi.ui.tag]
    :tags     ["tag"]}
   ;; Tags
   [:--tag-border-width :1px]


   ;; pane - floating layer abstraction
   ;; ------------------------------------------------------
   
   ;; pane colors and images
   {:family   "Floating pane colors and images"
    :desc     {:en ""}
    :category ["pane"]
    :tags     ["pane" "tooltip" "toast" "popover" "modal" "floating" "color"]}
   [:--pane-background-color                 :$background-color
    :--pane-background-color-dark-mode       :$background-color-dark-mode
    :--pane-background-image                 :white
    :--pane-box-shadow                       :$shadow-lg
    :--pane-box-shadow-dark-mode             :$shadow-lg
    :--pane-border-width                     :0px
    :--pane-border-style                     :solid
    :--pane-border-color                     :transparent
    :--pane-border-color-dark-mode           :transparent]

    ;; pane geometry
   {:family   "Floating pane geometry"
    :desc     {:en ""}
    :category ["pane"]
    :tags     ["pane" "tooltip" "toast" "popover" "modal" "floating" "geometry"]}
   [:--pane-min-width                        :70px
    :--pane-min-height                       :35px
    :--pane-padding-inline                   :1em
    :--pane-padding-block                    :0.5em
    :--pane-border-radius                    :$shape-rounded-md-absolute
    :--pane-offset                           :7px
    :--pane-viewport-padding                 :5px 
    :--pane-flip-viewport-edge-threshold     :32px 
    :--pane-auto-placement-y-threshold       :0.1]

    ;; pane choreography
   {:family   "Floating pane choreography"
    :desc     {:en ""}
    :category ["pane"]
    :tags     ["pane" "tooltip" "toast" "popover" "modal" "floating" "choreography"]}
   [:--pane-offset-start                     "calc(var(--pane-offset) + 5px)"
    :--pane-z-index                          99999
    :--pane-delay-duration                   :0ms
    :--pane-transition-duration              :$transition-xfast 
    :--pane-transition-timing-function       :$timing-ease-out-curve]

    ;; pane arrows
   {:family   "Floating pane arrow"
    :desc     {:en ""}
    :category ["pane"]
    :tags     ["pane" "tooltip" "toast" "popover" "modal" "floating" "arrows"]}
   [:--pane-arrow-inline-inset               :7px
    :--pane-arrow-block-inset                :2px
    :--pane-arrow-depth                      :7px
    ]


   ;; Tooltips
   ;; ------------------------------------------------------
   
   {:family   "Tooltip typography"
    :desc     {:en ""}
    :category ["tooltip"]
    :tags     ["pane" "tooltip" "typography" "floating"]}
   [:--tooltip-line-height    1.45
    :--tooltip-font-family    :$sans-serif-font-stack
    :--tooltip-font-size      :$font-size-xs
    :--tooltip-font-weight    :$font-weight-wee-bold
    :--tooltip-text-transform :none
    ]

   ;; tooltip colors and images
   {:family   "Tooltip colors and images"
    :desc     {:en ""}
    :category ["tooltip"]
    :ns       '[kushi.ui.tooltip]     
    :tags     ["pane" "tooltip" "color" "floating"]}
   [:--tooltip-color                            :$foreground-color-dark-mode
    :--tooltip-color-dark-mode                    :$foreground-color
    :--tooltip-background-color                 :$background-color-dark-mode
    :--tooltip-background-color-dark-mode         :$background-color
    :--tooltip-background-image                 :none
    :--tooltip-box-shadow                       :none
    :--tooltip-box-shadow-dark-mode               :none
    :--tooltip-border-width                     :$pane-border-width
    :--tooltip-border-style                     :$pane-border-style
    :--tooltip-border-color                     :$pane-border-color
    :--tooltip-border-color-dark-mode             :$pane-border-color-dark-mode
    ]
   
   ;; tooltip geometry
   {:family   "tooltip panes geometry"
    :desc     {:en ""}
    :category ["tooltip"]
    :ns       '[kushi.ui.tooltip]     
    :tags     ["pane" "tooltip" "floating" "geometry"]}
   [:--tooltip-min-width                        :1rem
    :--tooltip-min-height                       :1rem
    :--tooltip-padding-inline                   :1.2em
    :--tooltip-padding-block                    :0.65em
    :--tooltip-border-radius                    :5px
    :--tooltip-offset                           :$pane-offset
    :--tooltip-viewport-padding                 :$pane-viewport-padding 
    :--tooltip-flip-viewport-edge-threshold     :$pane-flip-viewport-edge-threshold 
    :--tooltip-auto-placement-y-threshold       :$pane-auto-placement-y-threshold 
    ]

   ;; tooltip choreography
   {:family   "tooltip panes choreography"
    :desc     {:en ""}
    :category ["tooltip"]
    :ns       '[kushi.ui.tooltip]     
    :tags     ["pane" "tooltip" "floating" "choreography"]}
   [:--tooltip-offset-start                     :$pane-offset-start
    :--tooltip-z-index                          :$pane-z-index
    :--tooltip-delay-duration                   :550ms
    :--tooltip-text-on-click-duration           :2000ms
    :--tooltip-initial-scale                    1
    :--tooltip-transition-duration              :$pane-transition-duration 
    :--tooltip-transition-timing-function       :$pane-transition-timing-function 
    ]

   ;; tooltip arrows
   {:family   "tooltip panes arrow"
    :desc     {:en ""}
    :category ["tooltip"]
    :ns       '[kushi.ui.tooltip]     
    :tags     ["pane" "tooltip" "toast" "floating" "choreography"]}
   [:--tooltip-arrow-inline-inset               :$pane-arrow-inline-inset
    :--tooltip-arrow-block-inset                :$pane-arrow-inline-inset
    :--tooltip-arrow-depth                      :5px
    ]


   ;; Popovers
   ;; ------------------------------------------------------
   
   ;; popover colors and images
   {:family   "Popover colors and images"
    :desc     {:en ""}
    :category ["popover"]
    :ns       '[kushi.ui.popover]
    :tags     ["pane" "popover" "floating"]}
   [:--popover-background-color                 :$pane-background-color
    :--popover-background-color-dark-mode       :$pane-background-color-dark-mode
    :--popover-background-image                 :none
    :--popover-box-shadow                       :$pane-box-shadow
    :--popover-box-shadow-dark-mode             :$pane-box-shadow-dark-mode
    :--popover-border-width                     :1px
    :--popover-border-style                     :solid
    :--popover-border-color                     :$neutral-200
    :--popover-border-color-dark-mode           :$neutral-700
    ]

   ;; popover geometry
   {:family   "Popover panes geometry"
    :desc     {:en ""}
    :category ["popover"]
    :ns       '[kushi.ui.popover]
    :tags     ["pane" "popover" "floating" "geometry"]}
   [:--popover-min-width                        :$pane-min-width
    :--popover-min-height                       :$pane-min-height
    :--popover-border-radius                    :$pane-border-radius
    :--popover-offset                           :$pane-offset
    :--popover-viewport-padding                 :$pane-viewport-padding 
    :--popover-flip-viewport-edge-threshold     :$pane-flip-viewport-edge-threshold 
    :--popover-auto-placement-y-threshold       :$pane-auto-placement-y-threshold 
    ]

   ;; popover choreography
   {:family   "Popover panes choreography"
    :desc     {:en ""}
    :category ["popover"]
    :ns       '[kushi.ui.popover]
    :tags     ["pane" "popover" "floating" "chreography"]}
   [:--popover-offset-start               :$pane-offset-start
    :--popover-z-index                    :$pane-z-index
    :--popover-delay-duration             :0ms
    :--popover-initial-scale              1
    :--popover-transition-duration        :$pane-transition-duration 
    :--popover-transition-timing-function :$pane-transition-timing-function 
    :--popover-auto-dismiss-duration      :5000ms
    ]

   ;; popover arrows
   {:family   "Popover panes arrow"
    :desc     {:en ""}
    :category ["popover"]
    :ns       '[kushi.ui.popover]
    :tags     ["pane" "popover" "floating" "arrow"]}
   [:--popover-arrow-inline-inset :$pane-arrow-inline-inset
    :--popover-arrow-block-inset  :$pane-arrow-inline-inset
    :--popover-arrow-depth        :7px
    ]

   
   ;; toasts - TODO breakout into more families (and same w other panes)
   ;; --------------------------------------------------------------------------
   ;; toast colors and images
   {:family   "Toast colors and images"
    :desc     {:en ""}
    :category ["toast"]
    :ns       '[kushi.ui.toast]
    :tags     ["pane" "toast" "floating" "color"]}
   [:--toast-background-color           :$pane-background-color
    :--toast-background-color-dark-mode :$pane-background-color-dark-mode
    :--toast-background-image           :none
    :--toast-box-shadow                 :$pane-box-shadow
    :--toast-box-shadow-dark-mode       :$pane-box-shadow-dark-mode
    :--toast-border-width               :1px
    :--toast-border-style               :solid
    :--toast-border-color               :$gray-150
    :--toast-border-color-dark-mode     :$gray-700
    ]

   ;; toast geometry
   {:family   "Toast panes geometry"
    :desc     {:en ""}
    :category ["toast"]
    :ns       '[kushi.ui.toast]
    :tags     ["pane" "Toast" "floating" "geometry"]}
   [:--toast-border-radius       :$pane-border-radius
    :--toast-slot-padding-inline :1rem
    :--toast-slot-padding-block  :1rem
    :--toast-slot-gap            :1rem
    :--toast-slot-z-index        100000
    ]

   ;; toast choreography
   {:family   "Toast panes choreography"
    :desc     {:en ""}
    :category ["toast"]
    :ns       '[kushi.ui.toast]
    :tags     ["pane" "Toast" "floating" "geometry"]}
   [:--toast-delay-duration             :200ms
    :--toast-initial-scale              1
    :--toast-transition-duration        :$pane-transition-duration 
    :--toast-transition-timing-function :$pane-transition-timing-function 
    :--toast-auto-dismiss-duration      :5000ms
    ]
   

   ;; Modals
   ;; ------------------------------------------------------
   {:family   "Modal styling"
    :desc     {:en ""}
    :category ["modal"]
    :ns       '[kushi.ui.toast]
    :tags     ["pane" "modal" "dialog" "floating"]}
   [
    :--modal-box-shadow             :$pane-box-shadow
    :--modal-box-shadow-dark-mode   :$pane-box-shadow-dark-mode
    :--modal-border-radius          :$shape-rounded-md-absolute
    :--modal-border-width           :0px
    :--modal-border-style           :solid
    :--modal-border-color           :$gray-150
    :--modal-border-color-dark-mode :$gray-700
    :--modal-padding                :2rem
    :--modal-padding-block          :$modal-padding
    :--modal-padding-inline         :$modal-padding
    :--modal-backdrop-color         :$transparent-black-40
    :--modal-margin                 :1rem
    :--modal-min-width              :200px
    :--modal-transition-duration    :$transition-xfast]


   ;; Material UI icons
   ;; ------------------------------------------------------
   {:family   "Mui icon size"
    :desc     {:en ""}
    :category ["icon"]
    :ns       '[kushi.ui.icon]
    :tags     ["icon" "size" "font-size"]
    }
   [:--mui-icon-relative-font-size :inherit
    ]



   ;; General icons
   ;; ------------------------------------------------------
   {:family   "Icon gap"
    :desc     {:en "Controls the width of the gap between icon and text, in labels, buttons, and tags"}
    :category ["icon"]
    :ns       '[kushi.ui.button kushi.ui.tag kushi.ui.label]
    :tags     ["icon" "size" "font-size"]}
   [:--icon-enhanceable-gap :0.25em]


   ;; Intended for css props: border-radius
   ;; TODO - should these be --border-radius-rounded-xs ?  ... instead of shape
   ;; ------------------------------------------------------
   
   ;; Absolute versions for panes, cards, etc.
   {:family   "Rounded corners, absolute"
    :desc     {:en "Controls the roundedness of corners on panes, cards, etc. Value is independent of font-size"}
    :category ["border-radius"]
    :tags     ["border-radius" "corners" "rounded"]}
   [:--shape-rounded-3xs-absolute  :0.0625rem        ;; 1px
    :--shape-rounded-2xs-absolute   :0.125rem         ;; 2px
    :--shape-rounded-xs-absolute    :0.25rem          ;; 4px
    :--shape-rounded-sm-absolute     :0.375rem         ;; 6px
    :--shape-rounded-md-absolute    :0.5rem           ;; 8px
    :--shape-rounded-lg-absolute     :0.75rem          ;; 12px
    :--shape-rounded-xl-absolute    :1rem           ;; 16px
    :--shape-rounded-2xl-absolute   :1.25rem          ;; 20px
    :--shape-rounded-3xl-absolute  :1.5625rem        ;; 25px
    :--shape-rounded-absolute           :$shape-rounded-md-absolute
    ]
   
   ;; Relative (to type size) versions for buttons, badges
   {:family   "Rounded corners, relative"
    :desc     {:en "Controls the roundedness of corners on panes, cards, etc. Value is relative to font-size"}
    :category ["border-radius"]
    :tags     ["border-radius" "corners" "rounded"]}
   [:--shape-rounded-3xs :0.04375em  
    :--shape-rounded-2xs  :0.0875em  
    :--shape-rounded-xs   :0.175em   
    :--shape-rounded-sm    :0.2625em  
    :--shape-rounded-md   :0.35em    
    :--shape-rounded-lg    :0.475em   
    :--shape-rounded-xl   :0.625em     
    :--shape-rounded-2xl  :0.775em   
    :--shape-rounded-3xl :0.925em 
    :--shape-rounded          :$shape-rounded-md
    :--border-weight          :1px
    ]


   ;; Intended for css animations and transitions
   ;; todo - should these be --transition-timing-function-linear-curve
   ;; ------------------------------------------------------
   {:family   "Animation and transition timing functions"
    :desc     {:en ""}
    :category ["transition-timing-function"]
    :tags     ["animation" "cubic-bezier" "timing" "transition-timing-function"]}
   [:--timing-linear-curve           "cubic-bezier(0, 0, 1, 1)"
    :--timing-ease-out-curve         "cubic-bezier(.2, .8, .4, 1)"
    :--timing-ease-out-curve-5       "cubic-bezier(.2, .8, .4, 1)"
    :--timing-ease-in-curve          "cubic-bezier(.8, .2, .6, 1)"
    :--timing-ease-in-out-curve      "cubic-bezier(0.4, 0, 0.2, 1)"
    :--timing-ease-out-curve-extreme "cubic-bezier(0.190, 0.510, 0.125, 0.905)"
    :--transition-timing-function    :$timing-linear-curve]


   ;; todo - should these be --transition-duration-instant
   {:family   "Animation and transition duration"
    :desc     {:en ""}
    :category ["transition-duration"]
    :tags     ["animation" "cubic-bezier" "timing" "transition-duration"]}
   [:--transition-duration         :$transition-fast
    :--transition-instant          :0ms
    :--transition-3xfast           :50ms
    :--transition-2xfast           :100ms
    :--transition-xfast            :175ms
    :--transition-fast             :250ms
    :--transition-base             :500ms
    :--transition-slow             :700ms
    :--transition-xslow            :1s
    :--transition-2xslow           :2s
    :--transition-3xslow           :4s
    :--spinner-animation-duration  :900ms
    :--loading-spinner-height      :0.8em]


   ;; Intended for styling scrollbars with the .styled-scrollbars utility-class
   ;; ------------------------------------------------------
   {:family   "Scrollbar styling"
    :desc     {:en ""}
    :category ["scrollbar"]
    :ns       '[kushi.ui.modal]
    :tags     ["scrollbar" "chrome" "browser-scrollbars"]}
   [:--scrollbar-thumb-color                  :$neutral-300
    :--scrollbar-thumb-color-dark-mode        :$neutral-700
    :--scrollbar-background-color             :$neutral-50
    :--scrollbar-background-color-dark-mode   :$neutral-900
    :--scrollbar-width                        :5px]


   ;; Kushi UI Components (move?)
   ;; ------------------------------------------------------
   {:family   "Collapse styling"
    :desc     {:en ""}
    :category ["collapse"]
    :ns       '[kushi.ui.collapse]
    :tags     ["collapse" "accordian"]}
   [:--collapse-transition-duration              :$slow]

    ;; kushi.ui.text-field/input
   
   {:family   "Text field styling"
    :desc     {:en ""}
    :category ["input"]
    :ns       '[kushi.ui.text-field]
    :tags     ["text-input" "text-field" "input"]}
   [:--text-input-helper-margin-block-start      :0.3em
    :--text-input-label-inline-margin-inline-end :0.7em
    :--text-input-label-block-margin-block-end   :0.4em

    ;; Remove wrapper from this
    :--text-input-border-intensity               :50%
    :--text-input-border-intensity-dark-mode     :55%
    :--text-input-border-radius                  :0.3em]])

(def design-tokens-by-component-usage
  (reduce 
   (fn [m
        [{ui-components-that-use-these-toks :ns
          family :family} toks]]
     (let [dbg? (= family "Icon gap")]
       (reduce 
        (fn [m component-name]
          (assoc m
                 component-name
                 (if-let [component-toks-by-family (get m component-name)]
                   (assoc component-toks-by-family
                          family
                          (if-let [existing-family-toks
                                   (get component-toks-by-family family)]
                            (apply conj existing-family-toks toks)
                            toks))
                   {family toks})))
        m
        ui-components-that-use-these-toks)))
   {}
   (partition 2 design-tokens*)))

#_(def enriched-design-tokens-ordered
  (->> design-tokens*
       (apply array-map)
       shared/enriched-tokens-ordered*))
