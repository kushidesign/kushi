(ns kushi.css.build.tokens
  (:require [clojure.string :as string]
            [fireworks.core :refer [? !? ?> !?>]]
            [kushi.css.build.tokens-legacy]
            [kushi.colors2 :as colors2]))


;; Elevations ------------------------------------------------------------------

(defn- box-shadows->str [coll level suffix]
  (->> coll
       (map-indexed (fn [idx settings]
                      (string/join
                       " "
                       (conj
                        (mapv #(str % "px") settings)
                        (str "var(--elevation-shadow-layer-"
                             (inc idx)
                             "-color"
                             suffix
                             ")")))))
       (string/join ", ")))

(defn- elevation-scale* [elevations]
  (reduce
   (fn [acc [level box-shadows]]
     (-> acc
         (conj (keyword (str "--elevated-" level "")))
         (conj (box-shadows->str box-shadows level ""))
         (conj (keyword (str "--elevated-" level "-dark-mode")))
         (conj (box-shadows->str box-shadows level "-dark-mode"))))
   []
   elevations))


;; These are arguments to css box-shadow
(def elevation-scale
 (elevation-scale*
   (array-map
    1
    [[0 3 3 -2]
     [0 3 4 0]
     [0 1 8 0]]
    2
    [[0 3 3 -2]
     [0 3 4 0]
     [0 1 8 0]]
    3
    [[0 3 5 -1]
     [0 6 10 0]
     [0 1 18 0]]
    4
    [[0 5 5 -3]
     [0 8 18 1]
     [0 6 20 2]]
    5
    [[0 7 14 -2]
     [0 6 26 0]
     [0 8 27 0]])))


(def elevation-shadow-layer-colors 
(flatten
 (map-indexed 
  (fn [i n]
    (let [cf (fn [n color]
               (keyword (str "$transparent-" color "-" (subs (str n) 2))))
          nm (fn [i s] 
               (keyword (str "--elevation-shadow-layer-" (inc i) "-" s)))]
      [(nm i "color")
       (cf n "black")
       (nm i "color-dark-mode")
       (cf n "white")
       ]))

    ;; These control the level of opacity of the shadow layer
  [0.08 0.05 0.03])))


;; Convex surfaces -------------------------------------------------------------

;; Change these to manipulate convex scale
;; gradient-start lightness and alpha, then gradient-end lightness + alpha 
(def convex-scale-grds-l+a
  [[["100%" "20%"] ["0%" "15%"]]
   [["100%" "25%"] ["0%" "25%"]]
   [["0%" "30%"] ["100%" "35%"]]
   [["0%" "35%"] ["100%" "45%"]]
   [["0%" "40%"] ["100%" "50%"]]])

(def convex-scale 
  (flatten (map-indexed
            (fn [i
                 [[l1 a1] 
                  [l2 a2]]]
              [(keyword (str "--convex-" (inc i)))
               (str "linear-gradient(180deg, hsl(0deg 0% " l1 " / " a1 "),"
                    "transparent, hsl(0deg 0% " l2 " / " a2 "))")])
            convex-scale-grds-l+a)))



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



;; All tokens  -----------------------------------------------------------------

(def design-tokens
  (array-map 

   ;; Colors
   ;; ------------------------------------------------------

   {:family   "Transparent neutrals colors"
    :category ["transparent-colors"]
    :tags     ["colors" "oklch" "neutrals" "transparent"]}
   colors2/transparent-neutrals-oklch

   {:family   "Colors"
    :category ["colors"]
    :tags     ["colors" "oklch"]}
   colors2/oklch-colors-flattened2

   {:family   "Foreground colors"
    :category ["foreground-colors"]
    :tags     ["colors" "oklch" "foreground"]}
   colors2/theming-colors-oklch-flattened

   ;; TODO - make sure these always get written
   {:family   "Global colors"
    :category ["global-colors"]
    :tags     ["colors" "oklch" "global"]
    }
   [
    :--foreground-color                     :$neutral-950
    :--background-color                     :white
    :--foreground-color-dark-mode           :$neutral-50
    :--background-color-dark-mode           :$neutral-1000
    :--foreground-color-secondary           :$neutral-700
    :--foreground-color-secondary-dark-mode :$neutral-350
    ]


   ;; Debugging grid
   ;; ------------------------------------------------------
   {:family   "Debugging grid"
    :category ["debugging-grid"]
    :tags     ["debugging" "development" "backgrounds"]
    }
   [:--debug-grid-size :16px
    :--debug-grid-color "hsla(0 0% 90%)"
    :--debug-grid-color-dark-mode "hsla(0 0% 25%)"
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
    :tags     ["code" "color" "typography" "block"]}
   [
    :--code-font-size                         :$small
    :--code-padding-inline                    :0.2em
    :--code-padding-block                     :0.08em
    :--code-border-radius                     :3px
    :--code-background-color                  :$gray-100
    :--code-background-color-dark-mode          :$gray-800
    :--code-color-dark-mode                     :$gray-50
    ]


   ;; Intended for css prop `font-weight`
   {:family   "Font weight"
    :desc     {:en "Controls the weight of type"}
    :category ["font-weight"]
    :tags     ["font-weight" "typography"]
    }
   [:--thin                                   100
    :--extra-light                            200
    :--light                                  300
    :--normal                                 400
    :--wee-bold                               500
    :--semi-bold                              600
    :--bold                                   700
    :--extra-bold                             800
    :--heavy                                  900
    :--root-font-size                         :1rem
    ]


   ;; Intended for css prop `font-size`
   {:family   "Font size"
    :desc     {:en "Controls the size of type"}
    :category ["font-size"]
    :tags     ["font-size" "typography"]
    }
   [:--xxxxsmall                              :0.64rem
    :--xxxsmall                               :0.67rem
    :--xxsmall                                :0.71rem
    :--xsmall                                 :0.77rem
    :--small                                  :0.86rem
    :--medium                                 :1rem
    :--large                                  :1.21rem
    :--xlarge                                 :1.485rem
    :--xxlarge                                :1.86rem
    :--xxxlarge                               :2.36rem
    :--xxxxlarge                              :3.03rem

    :--xxxxsmall-b                            :0.655rem
    :--xxxsmall-b                             :0.685rem
    :--xxsmall-b                              :0.733rem
    :--xsmall-b                               :0.805rem
    :--small-b                                :0.92rem
    :--medium-b                               :1.1rem
    :--large-b                                :1.33rem
    :--xlarge-b                               :1.655rem
    :--xxlarge-b                              :2.085rem
    :--xxxlarge-b                             :2.68rem
    :--xxxxlarge-b                            :3.475rem]


   ;; Intended for css prop `letterspacing`
   {:family   "Letter spacing"
    :desc     {:en "Controls the tracking of the type"}
    :category ["letter-spacing"]
    :tags     ["font-size" "typography" "tracking"]
    }
   [:--xxxtight                               :-0.09em
    :--xxtight                                :-0.06em
    :--xtight                                 :-0.03em
    :--tight                                  :-0.01em
    :--loose                                  :0.04em
    :--xloose                                 :0.08em
    :--xxloose                                :0.12em
    :--xxxloose                               :0.16em]


   ;; Intended for css props `border-width` for inputs
   {:family   "Input border weight"
    :desc     {:en "Controls the border-width for inputs"}
    :category ["input-border"]
    :tags     ["border-width" "border" "input" "inputs"]
    }
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
    :tags     ["border" "borders"]
    }
   [:--border-width                           :1px
    :--border-style                           :solid
    ]

   ;; Intended for divisors and divisor-like borders
   
   {:family   "Divisors"
    :desc     {:en "Styling for border-like divisors"}
    :category ["divisor"]
    :tags     ["divisors" "divisor"]
    }
   divisor-tokens


   ;; Intended for overlay placement
   {:family   "Overlay placement"
    :desc     {:en "Styling for overlays"}
    :category ["overlay"]
    :tags     ["overlays" "overlay"]
    }
   [:--overlay-placement-inline-offset        :12px
    :--overlay-placement-block-offset         :6px
    ]



   ;; Buttons
   ;; ------------------------------------------------------
   {:family   "Button padding"
    :desc     {:en "Styling for overlays"}
    :category ["button-padding"]
    :tags     ["button"]
    }
   [:--button-padding-inline-ems              :1.2em
    :--icon-button-padding-inline-ems         :0.69em
    :--button-padding-block-ems               :0.67em
    :--button-with-icon-padding-inline-offset :0.9em
    ]

   {:family   "Button border styling"
    :desc     {:en "Styling borders for buttons"}
    :category ["button-border"]
    :tags     ["button"]
    }
   [:--button-border-width :1px]



   {:family   "Tag border styling"
    :desc     {:en "Styling borders for tags"}
    :category ["tag-border"]
    :tags     ["tag"]
    }
   ;; Tags
   [:--tag-border-width                       :1px
    ]


   ;; pane - floating layer abstraction
   ;; ------------------------------------------------------
   
   ;; pane colors and images
   {:family   "Floating pane colors and images"
    :desc     {:en ""}
    :category ["pane"]
    :tags     ["pane" "tooltip" "toast" "popover" "modal" "floating" "color"]
    }
   [:--pane-background-color                 :$background-color
    :--pane-background-color-dark-mode         :$background-color-dark-mode
    :--pane-background-image                 :white
    :--pane-box-shadow                       :$elevated-5
    :--pane-box-shadow-dark-mode               :$elevated-5-dark-mode
    :--pane-border-width                     :0px
    :--pane-border-style                     :solid
    :--pane-border-color                     :transparent
    :--pane-border-color-dark-mode             :transparent]

    ;; pane geometry
   {:family   "Floating pane geometry"
    :desc     {:en ""}
    :category ["pane"]
    :tags     ["pane" "tooltip" "toast" "popover" "modal" "floating" "geometry"]
    }
   [:--pane-min-width                        :70px
    :--pane-min-height                       :35px
    :--pane-padding-inline                   :1em
    :--pane-padding-block                    :0.5em
    :--pane-border-radius                    :$rounded-absolute-large
    :--pane-offset                           :7px
    :--pane-viewport-padding                 :5px 
    :--pane-flip-viewport-edge-threshold     :32px 
    :--pane-auto-placement-y-threshold       :0.1]

    ;; pane choreography
   {:family   "Floating pane choreography"
    :desc     {:en ""}
    :category ["pane"]
    :tags     ["pane" "tooltip" "toast" "popover" "modal" "floating" "choreography"]
    }
   [:--pane-offset-start                     "calc(var(--pane-offset) + 5px)"
    :--pane-z-index                          99999
    :--pane-delay-duration                   :0ms
    :--pane-transition-duration              :$xfast 
    :--pane-transition-timing-function       :$timing-ease-out-curve]

    ;; pane arrows
   {:family   "Floating pane arrow"
    :desc     {:en ""}
    :category ["pane"]
    :tags     ["pane" "tooltip" "toast" "popover" "modal" "floating" "arrows"]
    }
   [:--pane-arrow-inline-inset               :7px
    :--pane-arrow-block-inset                :2px
    :--pane-arrow-depth                      :7px
    ]


   ;; Tooltips
   ;; ------------------------------------------------------
   
   {:family   "Tooltip typography"
    :desc     {:en ""}
    :category ["tooltip"]
    :tags     ["pane" "tooltip" "typography" "floating"]
    }
   [:--tooltip-line-height    1.45
    :--tooltip-font-family    :$sans-serif-font-stack
    :--tooltip-font-size      :$xsmall
    :--tooltip-font-weight    :$wee-bold
    :--tooltip-text-transform :none
    ]

   ;; tooltip colors and images
   {:family   "Tooltip colors and images"
    :desc     {:en ""}
    :category ["tooltip"]
    :tags     ["pane" "tooltip" "color" "floating"]
    }
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
    :tags     ["pane" "tooltip" "floating" "geometry"]
    }
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
    :tags     ["pane" "tooltip" "floating" "choreography"]
    }
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
    :tags     ["pane" "tooltip" "toast" "floating" "choreography"]
    }
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
    :tags     ["pane" "popover" "floating"]
    }
   [:--popover-background-color                 :$pane-background-color
    :--popover-background-color-dark-mode         :$pane-background-color-dark-mode
    :--popover-background-image                 :none
    :--popover-box-shadow                       :$pane-box-shadow
    :--popover-box-shadow-dark-mode               :$pane-box-shadow-dark-mode
    :--popover-border-width                     :1px
    :--popover-border-style                     :solid
    :--popover-border-color                     :$neutral-200
    :--popover-border-color-dark-mode             :$neutral-700
    ]

   ;; popover geometry
   {:family   "Popover panes geometry"
    :desc     {:en ""}
    :category ["popover"]
    :tags     ["pane" "popover" "floating" "geometry"]
    }
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
    :tags     ["pane" "popover" "floating" "chreography"]
    }
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
    :tags     ["pane" "popover" "floating" "arrow"]
    }
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
    :tags     ["pane" "toast" "floating" "color"]
    }
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
    :tags     ["pane" "Toast" "floating" "geometry"]
    }
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
    :tags     ["pane" "Toast" "floating" "geometry"]
    }
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
    :tags     ["pane" "modal" "dialog" "floating"]
    }
   [
    :--modal-box-shadow             :$pane-box-shadow
    :--modal-box-shadow-dark-mode   :$pane-box-shadow-dark-mode
    :--modal-border-radius          :$rounded-absolute-large
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
    :--modal-transition-duration    :$xfast]


   ;; Material UI icons
   ;; ------------------------------------------------------
   {:family   "Mui icon size"
    :desc     {:en ""}
    :category ["icon"]
    :tags     ["icon" "size" "font-size"]
    }
   [:--mui-icon-relative-font-size :inherit
    ]



   ;; General icons
   ;; ------------------------------------------------------
   {:family   "Icon gap"
    :desc     {:en "Controls the width of the gap between icon and text, in labels, buttons, and tags"}
    :category ["icon"]
    :tags     ["icon" "size" "font-size"]
    }
   [:--icon-enhanceable-gap :0.25em]


   ;; Intended for css props: border-radius
   ;; ------------------------------------------------------
   
   ;; Absolute versions for panes, cards, etc.
   {:family   "Rounded corners, absolute"
    :desc     {:en "Controls the roundedness of corners on panes, cards, etc. Value is independent of font-size"}
    :category ["border-radius"]
    :tags     ["border-radius" "corners" "rounded"]
    }
   [:--rounded-absolute-xxxsmall  :0.0625rem        ;; 1px
    :--rounded-absolute-xxsmall   :0.125rem         ;; 2px
    :--rounded-absolute-xsmall    :0.25rem          ;; 4px
    :--rounded-absolute-small     :0.375rem         ;; 6px
    :--rounded-absolute-medium    :0.5rem           ;; 8px
    :--rounded-absolute-large     :0.75rem          ;; 12px
    :--rounded-absolute-xlarge    :0.1rem           ;; 16px
    :--rounded-absolute-xxlarge   :1.25rem          ;; 20px
    :--rounded-absolute-xxxlarge  :1.5625rem        ;; 25px
    ]
   
   ;; Relative (to type size) versions for buttons, badges
   {:family   "Rounded corners, relative"
    :desc     {:en "Controls the roundedness of corners on panes, cards, etc. Value is relative to font-size"}
    :category ["border-radius"]
    :tags     ["border-radius" "corners" "rounded"]
    }
   [:--rounded-xxxsmall :0.04375em  
    :--rounded-xxsmall  :0.0875em  
    :--rounded-xsmall   :0.175em   
    :--rounded-small    :0.2625em  
    :--rounded-medium   :0.35em    
    :--rounded-large    :0.525em   
    :--rounded-xlarge   :0.7em     
    :--rounded-xxlarge  :0.875em   
    :--rounded-xxxlarge :1.09375em 
    :--rounded          :$rounded-medium
    :--border-weight    :1px
    ]


   ;; Intended for css props: background-image
   ;; ------------------------------------------------------
   {:family   "Convex surface"
    :desc     {:en ""}
    :category ["convex"]
    :tags     ["convex" "concave" "surfaces"]
    }
   (concat
    convex-scale
    [:--convex-0 :none
     :--convex   :$convex-1
     ])
   
    ;; Intended for css props: box-shadow
    ;; ------------------------------------------------------
   {:family   "Elevation shadow colors"
    :desc     {:en ""}
    :category ["elevation"]
    :tags     ["shadow" "elevation" "surfaces"]
    }
   elevation-shadow-layer-colors


   ;; maps to MUI2 level 1
   {:family   "Elevation levels"
    :desc     {:en ""}
    :category ["elevation"]
    :tags     ["shadow" "elevation" "surfaces"]
    }
   elevation-scale

   {:family   "Elevation levels general"
    :desc     {:en ""}
    :category ["elevation"]
    :tags     ["shadow" "elevation" "surfaces"]
    }
   [:--elevated-0       :none
    :--elevated         :$elevated-4
    :--elevated-dark-mode :$elevated-4-dark-mode]

   ;; Intended for css animations and transitions
   ;; ------------------------------------------------------
   {:family   "Animation and transition timing functions"
    :desc     {:en ""}
    :category ["transition-timing-function"]
    :tags     ["animation" "cubic-bezier" "timing" "transition-timing-function"]
    }
   [:--timing-linear-curve           "cubic-bezier(0, 0, 1, 1)"
    :--timing-ease-out-curve         "cubic-bezier(.2, .8, .4, 1)"
    :--timing-ease-out-curve-5       "cubic-bezier(.2, .8, .4, 1)"
    :--timing-ease-in-curve          "cubic-bezier(.8, .2, .6, 1)"
    :--timing-ease-in-out-curve      "cubic-bezier(0.4, 0, 0.2, 1)"
    :--timing-ease-out-curve-extreme "cubic-bezier(0.190, 0.510, 0.125, 0.905)"
    :--transition-timing-function    :$timing-linear-curve]

   {:family   "Animation and transition duration"
    :desc     {:en ""}
    :category ["transition-duration"]
    :tags     ["animation" "cubic-bezier" "timing" "transition-duration"]
    }
   [:--transition-duration           :$fast
    :--instant                       :0ms
    :--xxxfast                       :50ms
    :--xxfast                        :100ms
    :--xfast                         :175ms
    :--fast                          :250ms
    :--moderate                      :500ms
    :--slow                          :700ms
    :--xslow                         :1s
    :--xxslow                        :2s
    :--xxxslow                       :4s
    :--spinner-animation-duration   :900ms
    :--loading-spinner-height        :0.8em]


   ;; Intended for styling scrollbars with the .styled-scrollbars utility-class
   ;; ------------------------------------------------------
   {:family   "Scrollbar styling"
    :desc     {:en ""}
    :category ["scrollbar"]
    :tags     ["scrollbar" "chrome" "browser-scrollbars"]
    }
   [:--scrollbar-thumb-color                  :$neutral-300
    :--scrollbar-thumb-color-dark-mode          :$neutral-700
    :--scrollbar-background-color             :$neutral-50
    :--scrollbar-background-color-dark-mode     :$neutral-900
    :--scrollbar-width                        :5px]


   ;; Kushi UI Components (move?)
   ;; ------------------------------------------------------
   {:family   "Collapse styling"
    :desc     {:en ""}
    :category ["collapse"]
    :tags     ["collapse" "accordian"]
    }
   [:--collapse-transition-duration              :$slow]

    ;; kushi.ui.text-field.core/input
   
   {:family   "Text field styling"
    :desc     {:en ""}
    :category ["input"]
    :tags     ["text-input" "text-field" "input"]
    }
   [:--text-input-helper-margin-block-start      :0.3em
    :--text-input-label-inline-margin-inline-end :0.7em
    :--text-input-label-block-margin-block-end   :0.4em

    ;; Remove wrapper from this
    :--text-input-border-intensity               :50%
    :--text-input-border-intensity-dark-mode       :55%
    :--text-input-border-radius                  :0.3em]


   ;; Switches
   ;; ------------------------------------------------------
   {:family   "Legacy Color tokens"
    :desc     {:en ""}
    :category ["legacy-switch-theming"]
    :tags     ["theming" "colors" "switch"]
    }
   kushi.css.build.tokens-legacy/legacy-switch-theming
   ))

 
(defn- css-vars-re-seq [s]
  (when (string? s)
    (re-seq #"var\((--[^_][^\)\, ]+)" s)))

(defn- css-var-string? [s]
  (boolean (when (string? s)
             (re-find #"^var\(--[^_][^\)\, ]+\)$" s))))

(defn- css-var-kw? [x]
  (and (keyword? x) (string/starts-with? (name x) "$")))

(defn- css-var-str [v]
  (when (css-var-kw? v)
    (str "var(--" (subs (name v) 1) ")")))

(def enriched-tokens-ordered 
 (mapcat (fn [[{:keys [desc category tags family added]
                :or   {desc  "Fix me"
                       added "1.0" ;; <- get version?
                       }}
               toks]]
           (let [toks (apply array-map toks)]
             (for [[tok v] toks
                   :let    [alias-token? (boolean (or (css-var-kw? v)
                                                      (css-var-string? v)))
                            value (or (css-var-str v)
                                      (when (keyword? v) (name v))
                                      v)]]
               {:name         (name tok)
                :value        value
                :desc         desc
                :category     category
                :tags         tags
                :family       family
                :added        "1.0"
                :alias-token? alias-token?
                :dep-toks     (some->> (or (css-var-str v) v)
                                       css-vars-re-seq
                                       (mapv second))
                })))
         design-tokens))



(def enriched-tokens-array-map
  (apply array-map
         (reduce (fn [acc m]
                   (conj acc (:name m) m))
                 []
                 enriched-tokens-ordered)))

(!? (get enriched-tokens-array-map "--accent-500"))

 #_(def design-tokens-by-token
   (->> design-tokens
        vals
        (apply concat)
        (apply hash-map)))

;; This version has css-var-keywords cast to strs e.g. :$bold -> "var(--bold)"
(def design-tokens-by-token-array-map
  (apply array-map
         (reduce-kv (fn [acc k v]
                      (conj acc k (:value v)))
                    []
                    enriched-tokens-array-map)))

(filter #(string/starts-with? % "--elevated") 
        (keys design-tokens-by-token-array-map))

(def design-tokens-by-token
  (reduce-kv (fn [m k v]
               (assoc m k (:value v)))
             {}
             enriched-tokens-array-map))

(def design-tokens-by-category 
  (reduce (fn [acc category]
            (assoc acc
                   category
                   (reduce (fn [acc {tokens-category :category
                                     tokens-name     :name}]
                             (if (contains? (into #{} tokens-category) category)
                               (conj acc tokens-name)
                               acc))
                           []
                           enriched-tokens-ordered)))
          {}
          ["pane"
           "elevation"
           "modal"
           "popover"
           "tooltip"
           "toast"
           "colors"
           "global-colors"]))

(!? :pp design-tokens-by-token-array-map)


#_{:name         "divisor-dark-mode",
 :value        :$divisor-5-dark-mode,
 :desc         {:en "Fix me"},
 :category   ["Surface" "Borders" "Color"],
 :tags         ["divisor"],
 :family       nil,
 :added        "1.0",
 :alias-token? true,
 :provenance   {:namespace 'kushi.ui.tokens
                :added     "1.0"}}

;; OTHER NOTES
 

;; TODO
;; Use a vector of maps like this map:
;; {:name         "neutral-secondary-fg"
;;  :desc         {:en "Foreground text that is slightly de-emphasized (such as text input field helper text)."}
;;  :value        :$gray-700
;;  :added        "1.0"
;;  :category   ["Color" "Typography"]
;;  :tags         ["label" "de-emphasis"]
;;  :family       :font-weight  ;; :code :font-weight :font-size :letter-spacing etc (could be nil)
;;  :alias-token? <reactive based on :value>
;;  :provenance   {:namespace 'kushi.ui.tokens :added "1.0"}

;;  TODO need some kind of curve fn which creates a scale of values,
;;  e.g. more precise at beginning with larger jumps towards end
;;  -- we would only use this :suggested for like padding or similar
;;  -- or infer it in general based on css-prop?
;;  :suggested    {"em" [{:min 0 :max 4 :step 0.05}
;;                       {:min 0 :max 12 :step 0.5}]
;;                 "px" {:min 0 :max 30}}}



;; TODO what about a children concept
;; {:name         “fune-background-color”
;;  :desc         {:en “Background color for fune floating layer primitive”}
;;  :value        :transparent
;;  :added        "1.0"
;;  :category   ["Color" “Layers” “Contextual”]
;;  :tags         [“color”]
;;  :family       :fune  ;; :code :font-weight :font-size :letter-spacing etc (could be nil)
;;  :name-sub     “fune”
;;  :children     [“tooltip” “popover” “context-menu” “toast” “hover-board”]
;; REACTIVE additions
;;  :desc         <{:en "Foreground text for fune floating layer abstraction”}>
;;  :inherits     <reactive based on if :value points to another token list of inherits>
;;  :provenance   <{:namespace 'kushi.ui.tokens
;;                  :added “1.0”}>
