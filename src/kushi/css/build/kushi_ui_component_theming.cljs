(ns ^{:kushi/layer "kushi-ui-theming"} kushi.css.build.kushi-ui-component-theming
  (:require
   [kushi.colors]
   [bling.hifi]
   [fireworks.core :refer [? !? ?> !?>]]
   [kushi.css.build.macros :refer [defcolorway defcss]]))

(defcss
  "body"
  {"font-family"                "var(--sans-serif-font-stack)"
   "font-weight"                "var(--body-font-weight)"               
   "color"                      "var(--foreground-color)"
   "background-color"           "var(--background-color)"
   "transition-property"        "background-color, color"
   "transition-duration"        "var(--fast)"
   "transition-timing-function" "var(--timing-linear-curve)"
   "overflow-y"                 "scroll"})

(defcss
  ".dark, body.dark"
  {"background-color" "var(--background-color-dark-mode)",
   "color" "var(--foreground-color-dark-mode)"})

(defcss
  "code"
  {"width"                      "fit-content",
   "transition-duration"        "var(--fast)",
   "transition-property"        "all",
   "font-family"                "var(--code-font-stack)",
   "font-weight"                "var(--code-font-weight)",
   "height"                     "fit-content",
   "white-space"                "nowrap",
   "padding-inline"             "var(--code-padding-inline)",
   "transition-timing-function" "cubic-bezier(0, 0, 1, 1)",
   "font-size"                  "var(--code-font-size)",
   "background-color"           "var(--code-background-color)",
   "padding-block"              "var(--code-padding-block)",
   "border-radius"              "var(--code-border-radius, var(--rounded-small-absolute))",
  ;;  "border-width"               "var(--code-border-width, 1px)",
  ;;  "border-color"               "var(--code-border-color, var(--neutral-200))",
  ;;  "border-style"               "var(--code-border-style, solid)",
   "color"                      "var(--code-color)"})

(defcss
  "pre>code"
  {"background-color" :transparent
   "border-width"     0
   })

(defcss
  ".dark code"
  {"background-color" "var(--code-background-color-dark-mode)",
   "color"            "var(--code-color-dark-mode)"
  ;;  "border-color"     "var(--code-border-color-dark-mode, var(--neutral-800))",
   })

(defcss
  ".dark pre>code"
  {"background-color" :transparent
   "border-width"     0
   })

(defcss
  ".styled-scrollbars"
  {"scrollbar-color" "var(--scrollbar-thumb-color) var(--scrollbar-background-color)",
   "scrollbar-width" "thin"})

(defcss
  ".dark .styled-scrollbars"
  {"scrollbar-color" "var(--scrollbar-thumb-color-dark-mode) var(--scrollbar-background-color-dark-mode)"})

(defcss
  ".styled-scrollbars::-webkit-scrollbar"
  {"width" "var(--scrollbar-width)", "height" "var(--scrollbar-width)"})

(defcss
  ".styled-scrollbars::-webkit-scrollbar-thumb"
  {"background" "var(--scrollbar-thumb-color)",
   "border-radius" "9999px",
   "border" "0px solid var(--scrollbar-background-color)"})

(defcss
  ".dark .styled-scrollbars::-webkit-scrollbar-thumb"
  {"background" "var(--scrollbar-thumb-color-dark-mode)",
   "border" "0px solid var(--scrollbar-background-color-dark-mode)"})

(defcss
  ".styled-scrollbars::-webkit-scrollbar-track"
  {"background" "var(--scrollbar-background-color)"})

(defcss
  ".dark .styled-scrollbars::-webkit-scrollbar-track"
  {"background" "var(--scrollbar-background-color-dark-mode)"})

(defcss
  "*:focus-visible"
  {"outline" "4px solid rgba(0, 125, 250, 0.6)",
   "outline-offset" "1px"})

(defcss
  "*:disabled"
  {"opacity" "45%!important", "cursor" "not-allowed!important"})

(defcss
  ".kushi-radio-input:focus-visible"
  {"box-shadow" "0 0 0 4px rgba(0, 125, 250, 0.6)"})

(defcss ".kushi-tag" {"font-family" "var(--primary-font-family)"})

(defcss ".dark .kushi-radio-input" {"background-color" "black"})

(defcss ".dark .kushi-checkbox-input" {"background-color" "black"})

(defcss
  ".dark .ks-checkbox-input:before"
  {"box-shadow" "inset 1em 1em black"})

(defcss
  ".dark .kushi-slider-step-label.kushi-slider-step-label-selected"
  {"color" "white"})

(defcss ".dark .kushi-slider-step-label" {"color" "var(--gray-300)"})

;; (defcss ".invisible" {"opacity" "0"})
;; (defcss ".hidden" {"visibility" "hidden"})
;; (defcss ".visible" {"visibility" "visible"})
;; (defcss ".collapse" {"visibility" "collapse"})


(defcss ".pointer" {"cursor" "pointer"})

;; DISPLAY
(defcss ".display-block" {"display" "block"})
(defcss ".display-inline" {"display" "inline"})
(defcss ".display-inline-block" {"display" "inline-block"})
(defcss ".display-flex" {"display" "flex"})
(defcss ".display-inline-flex" {"display" "inline-flex"})
(defcss ".display-grid" {"display" "grid"})
(defcss ".display-inline-grid" {"display" "inline-grid"})
(defcss ".display-flow-root" {"display" "flow-root"})
(defcss ".display-contents" {"display" "contents"})

;; TRACKING
(defcss ".tracking-xxxtight" {"letter-spacing" "var(--xxxtight)"})
(defcss ".tracking-xxtight" {"letter-spacing" "var(--xxtight)"})
(defcss ".tracking-xtight" {"letter-spacing" "var(--xtight)"})
(defcss ".tracking-tight" {"letter-spacing" "var(--tight)"})
(defcss ".tracking-default-tracking" {"letter-spacing" "0"})
(defcss ".tracking-loose" {"letter-spacing" "var(--loose)"})
(defcss ".tracking-xloose" {"letter-spacing" "var(--xloose)"})
(defcss ".tracking-xxloose" {"letter-spacing" "var(--xxloose)"})
(defcss ".tracking-xxxloose" {"letter-spacing" "var(--xxxloose)"})

;; TRANSITION SPEED
(defcss ".transition-instant" {"transition-duration" "var(--instant)"})
(defcss ".transition-xxxfast" {"transition-duration" "var(--xxxfast)"})
(defcss ".transition-xxfast" {"transition-duration" "var(--xxfast)"})
(defcss ".transition-xfast" {"transition-duration" "var(--xfast)"})
(defcss ".transition-fast" {"transition-duration" "var(--fast)"})
(defcss ".transition-moderate" {"transition-duration" "var(--moderate)"})
(defcss ".transition-slow" {"transition-duration" "var(--slow)"})
(defcss ".transition-xslow" {"transition-duration" "var(--xslow)"})
(defcss ".transition-xxslow" {"transition-duration" "var(--xxslow)"})
(defcss ".transition-xxxslow" {"transition-duration" "var(--xxxslow)"})

;; SHAPE
(defcss ".shape-rounded-absolute" {"border-radius" "var(--rounded-medium-absolute)"})
(defcss ".shape-rounded" {"border-radius" "var(--rounded)"})
(defcss ".shape-sharp" {"border-radius" "0"})
(defcss ".shape-pill" {"border-radius" "9999px"})
(defcss ".shape-circle" {"border-radius" "9999px" "aspect-ratio" "1 / 1"})
(defcss ".shape-rounded-xxsmall-absolute" {"border-radius" "var(--rounded-xxsmall-absolute)"})
(defcss ".shape-rounded-xsmall-absolute" {"border-radius" "var(--rounded-xsmall-absolute)"})
(defcss ".shape-rounded-small-absolute" {"border-radius" "var(--rounded-small-absolute)"})
(defcss ".shape-rounded-medium-absolute" {"border-radius" "var(--rounded-medium-absolute)"})
(defcss ".shape-rounded-medium-absolute" {"border-radius" "var(--rounded-medium-absolute)"})
(defcss ".shape-rounded-xlarge-absolute" {"border-radius" "var(--rounded-xlarge-absolute)"})
(defcss ".shape-rounded-xxlarge-absolute" {"border-radius" "var(--rounded-xxlarge-absolute)"})
(defcss ".shape-rounded-xxxlarge-absolute" {"border-radius" "var(--rounded-xxxlarge-absolute)"})
(defcss ".shape-rounded-xxxsmall" {"border-radius" "var(--rounded-xxxsmall)"})
(defcss ".shape-rounded-xxsmall" {"border-radius" "var(--rounded-xxsmall)"})
(defcss ".shape-rounded-xsmall" {"border-radius" "var(--rounded-xsmall)"})
(defcss ".shape-rounded-small" {"border-radius" "var(--rounded-small)"})
(defcss ".shape-rounded-medium" {"border-radius" "var(--rounded-medium)"})
(defcss ".shape-rounded-large" {"border-radius" "var(--rounded-large)"})
(defcss ".shape-rounded-xlarge" {"border-radius" "var(--rounded-xlarge)"})
(defcss ".shape-rounded-xxlarge" {"border-radius" "var(--rounded-xxlarge)"})
(defcss ".shape-rounded-xxxlarge" {"border-radius" "var(--rounded-xxxlarge)"})

;; New Sizing
(defcss ".size-xxxxsmall" {"font-size" "var(--xxxxsmall)"})
(defcss ".size-xxxsmall" {"font-size" "var(--xxxsmall)"})
(defcss ".size-xxsmall" {"font-size" "var(--xxsmall)"})
(defcss ".size-xsmall" {"font-size" "var(--xsmall)"})
(defcss ".size-small" {"font-size" "var(--small)"})
(defcss ".size-medium" {"font-size" "var(--medium)"})
(defcss ".size-large" {"font-size" "var(--large)"})
(defcss ".size-xlarge" {"font-size" "var(--xlarge)"})
(defcss ".size-xxlarge" {"font-size" "var(--xxlarge)"})
(defcss ".size-xxxlarge" {"font-size" "var(--xxxlarge)"})
(defcss ".size-xxxxlarge" {"font-size" "var(--xxxxlarge)"})


;; thin
(defcss ".weight-thin" 
  {"font-weight" "var(--thin)"})

(defcss ".weight-thin .ks-icon, .weight-thin.ks-icon" 
  {"font-variation-settings" "'wght' 100"})

(defcss
  ".weight-thin>.ks-radio-input"
  {"outline-width" "var(--input-border-weight-thin)"})

(defcss
  ".weight-thin>.ks-checkbox-input"
  {"border-width" "var(--input-border-weight-thin)"})

(defcss ".weight-thin .ks-icon, .weight-thin.ks-icon" 
  {"font-variation-settings" "'wght' 100"})

(defcss
  ".weight-thin.ks-radio, .weight-thin .ks-radio"
  {"border-width" "var(--input-border-weight-thin)"})

(defcss
  ".weight-thin.ks-checkbox, .weight-thin .ks-checkbox"
  {"border-width" "var(--input-border-weight-thin)"})


;; extra-light
(defcss ".weight-extra-light" 
  {"font-weight" "var(--extra-light)"})

(defcss ".weight-extra-light .ks-icon, .weight-extra-light.ks-icon" 
  {"font-variation-settings" "'wght' 200"})

(defcss
  ".weight-extra-light>.ks-radio-input"
  {"outline-width" "var(--input-border-weight-extra-light)"})

(defcss
  ".weight-extra-light>.ks-checkbox-input"
  {"border-width" "var(--input-border-weight-extra-light)"})


;; light
(defcss ".light" {"font-weight" "var(--light)"})

(defcss ".light .kushi-icon" {"font-variation-settings" "'wght' 300"})

(defcss ".light.kushi-icon" {"font-variation-settings" "'wght' 300"})

(defcss
  ".light>.kushi-radio-input"
  {"outline-width" "var(--input-border-weight-light)"})

(defcss
  ".light>.kushi-checkbox-input"
  {"border-width" "var(--input-border-weight-light)"})
(defcss
  ".light>.ks-checkbox-input"
  {"border-width" "var(--input-border-weight-light)"})
(defcss
  ".weight-light>.ks-checkbox-input"
  {"border-width" "var(--input-border-weight-light)"})


(defcss ".normal" {"font-weight" "var(--normal)"})

(defcss ".normal .kushi-icon" {"font-variation-settings" "'wght' 400"})

(defcss ".normal.kushi-icon" {"font-variation-settings" "'wght' 400"})

(defcss
  ".normal>.kushi-radio-input"
  {"outline-width" "var(--input-border-weight-normal)"})

(defcss
  ".normal>.kushi-checkbox-input"
  {"border-width" "var(--input-border-weight-normal)"})
(defcss
  ".normal>.ks-checkbox-input"
  {"border-width" "var(--input-border-weight-normal)"})
(defcss
  ".weight-normal>.ks-checkbox-input"
  {"border-width" "var(--input-border-weight-normal)"})

(defcss ".wee-bold" {"font-weight" "var(--wee-bold)"})

(defcss
  ".wee-bold .kushi-icon"
  {"font-variation-settings" "'wght' 500"})

(defcss
  ".wee-bold.kushi-icon"
  {"font-variation-settings" "'wght' 500"})

(defcss
  ".wee-bold>.kushi-radio-input"
  {"outline-width" "var(--input-border-weight-wee-bold)"})

(defcss
  ".wee-bold>.kushi-checkbox-input"
  {"border-width" "var(--input-border-weight-wee-bold)"})
(defcss
  ".wee-bold>.ks-checkbox-input"
  {"border-width" "var(--input-border-weight-wee-bold)"})
(defcss
  ".weight-wee-bold>.ks-checkbox-input"
  {"border-width" "var(--input-border-weight-wee-bold)"})


(defcss ".semi-bold" {"font-weight" "var(--semi-bold)"})

(defcss
  ".semi-bold .kushi-icon"
  {"font-variation-settings" "'wght' 600"})

(defcss
  ".semi-bold.kushi-icon"
  {"font-variation-settings" "'wght' 600"})

(defcss
  ".semi-bold>.kushi-radio-input"
  {"outline-width" "var(--input-border-weight-semi-bold)"})

(defcss
  ".semi-bold>.kushi-checkbox-input"
  {"border-width" "var(--input-border-weight-semi-bold)"})
(defcss
  ".semi-bold>.ks-checkbox-input"
  {"border-width" "var(--input-border-weight-semi-bold)"})
(defcss
  ".weight-semi-bold>.ks-checkbox-input"
  {"border-width" "var(--input-border-weight-semi-bold)"})

(defcss ".bold" {"font-weight" "var(--bold)"})

;; TODO need to make this support linebreaks
(defcss ".bold .ks-radio[type=\"radio\"]:not(:checked)"
  {"border-width" "0.2em"})

(defcss ".weight-bold .ks-radio[type=\"radio\"]:not(:checked)"
  {"border-width" "0.2em"})

(defcss ".bold.ks-radio[type=\"radio\"]:not(:checked)"
  {"border-width" "0.2em"})

(defcss ".weight-bold.ks-radio[type=\"radio\"]:not(:checked)"
  {"border-width" "0.2em"})


(defcss ".bold .ks-checkbox[type=\"checkbox\"]:not(:checked)"
  {"border-width" "0.2em"})

(defcss ".weight-bold .ks-checkbox[type=\"checkbox\"]:not(:checked)"
  {"border-width" "0.2em"})

(defcss ".bold.ks-checkbox[type=\"checkbox\"]:not(:checked)"
  {"border-width" "0.2em"})

(defcss ".weight-bold.ks-checkbox[type=\"checkbox\"]:not(:checked)"
  {"border-width" "0.2em"})


(defcss ".bold .kushi-icon" {"font-variation-settings" "'wght' 700"})

(defcss ".bold.kushi-icon" {"font-variation-settings" "'wght' 700"})

(defcss
  ".bold>.kushi-radio-input, .bold>.kushi-radio-button-input, .weight-bold>.kushi-radio-button-input"
  {"border-width" "var(--input-border-weight-bold)"})

(defcss
  ".bold>.kushi-checkbox-input"
  {"border-width" "var(--input-border-weight-bold)"})
(defcss
  ".bold>.ks-checkbox-input"
  {"border-width" "var(--input-border-weight-bold)"})
(defcss
  ".weight-bold>.ks-checkbox-input"
  {"border-width" "var(--input-border-weight-bold)"})

(defcss ".extra-bold" {"font-weight" "var(--extra-bold)"})

(defcss
  ".extra-bold .kushi-icon"
  {"font-variation-settings" "'wght' 800"})

(defcss
  ".extra-bold.kushi-icon"
  {"font-variation-settings" "'wght' 800"})

(defcss
  ".extra-bold>.kushi-radio-input"
  {"outline-width" "var(--input-border-weight-extra-bold)"})

(defcss
  ".extra-bold>.kushi-checkbox-input"
  {"border-width" "var(--input-border-weight-extra-bold)"})
(defcss
  ".extra-bold>.ks-checkbox-input"
  {"border-width" "var(--input-border-weight-extra-bold)"})
(defcss
  ".weight-extra-bold>.ks-checkbox-input"
  {"border-width" "var(--input-border-weight-extra-bold)"})

(defcss ".heavy" {"font-weight" "var(--heavy)"})

(defcss ".heavy .kushi-icon" {"font-variation-settings" "'wght' 900"})

(defcss ".heavy.kushi-icon" {"font-variation-settings" "'wght' 900"})

(defcss
  ".heavy>.kushi-radio-input"
  {"outline-width" "var(--input-border-weight-heavy)"})

(defcss
  ".heavy>.kushi-checkbox-input"
  {"border-width" "var(--input-border-weight-heavy)"})
(defcss
  ".heavy>.ks-checkbox-input"
  {"border-width" "var(--input-border-weight-heavy)"})
(defcss
  ".weight-heavy>.ks-checkbox-input"
  {"border-width" "var(--input-border-weight-heavy)"})


;; (defcss
;;   ".bgi-cover"
;;   {"background-position" "center center",
;;    "background-repeat" "no-repeat",
;;    "width" "100%"})

;; (defcss
;;   ".bgi-contain"
;;   {"background-position" "center center",
;;    "background-size" "contain",
;;    "background-repeat" "no-repeat",
;;    "width" "100%",
;;    "height" "100%"})

;; (defcss
;;   ".transition"
;;   {"transition-property" "all",
;;    "transition-timing-function" "var(--transition-timing-function)",
;;    "transition-duration" "var(--transition-duration)"})



;; New theming

;; TODO - should these live in design-tokens?
(defcss
  ":root"
  {"--debug-grid-size"                                "107px",
   "--debug-grid-color"                               "#eee",
   "--outlined-element-stroke-width"                  "0.075em"
   "--outlined-button-stroke-width"                   "var(--outlined-element-stroke-width)"

   "--element-stroke-width"                           "1px"
   "--button-stroke-width"                            :$element-stroke-width
   "--tag-stroke-width"                               :$element-stroke-width
   "--thumb-stroke-width"                             :$element-stroke-width
   "--stroke-width-nearest-pixel"                     "round(nearest, var(--stroke-width, 1px), 1px)"

   "--outlined-tag-stroke-width"                      "var(--outlined-element-stroke-width)"
   "--outlined-callout-stroke-width"                  "var(--outlined-element-stroke-width)"
   "--button-padding-inline-compact"                  "0.7em",
   "--button-padding-inline"                          "0.9em",
   "--button-padding-inline-roomy"                    "1.2em",
   "--button-padding-block-compact"                   "0.35em",
   "--button-padding-block"                           "0.55em",
   "--button-padding-block-roomy"                     "0.75em"
   "--icon-button-padding-inline"                     :$button-padding-block,
   "--icon-button-padding-block"                      :$button-padding-block,
   ;; Use or not use -ems ?
   "--tag-padding-block-start-reduction-ratio"        "0.9",
   "--tag-padding-block-start"                        "0.27em",
   "--tag-padding-block"                              "0.3em",
   "--tag-padding-inline"                             "0.6em",
   "--tag-padding-block-compact"                      "0.20em",
   "--tag-padding-inline-compact"                     "0.45em",
   "--tag-padding-block-roomy"                        "0.45em",
   "--tag-padding-inline-roomy"                       "0.9em"
   "--transition-duration"                            :$xxxfast
   ;; data-ks-surface=outline
   "--outlined-element-stroke-transparency"           "30%"
   "--outlined-element-stroke-transparency-dark-mode" "30%"

   "--xsoft-stroke-transparency"                      "15%"
   "--soft-stroke-transparency"                       "30%"
   "--medium-stroke-transparency"                     "50%"
   "--hard-stroke-transparency"                       "70%"
   "--xhard-stroke-transparency"                      "100%"

   ;; Shadows
   :--shadow-color                                  "black"
   :dark:--shadow-color                             "white"

   ;; TODO - add these scales for legacy browser support of colored shadows
   ;; legacy
   ;; :--shadow-color-red-h-s-l         "10 100 50" ; <- this would be in :root
   
   ;; super legacy, no support for shadow strength
   ;; :--shadow-color-red-hex         "#f908244d" ; <- this would be in :root
   
   ;; Convex
   :--convex-shadow-strength                        "25%"

   })


(defcolorway "neutral")
(defcolorway "accent")
(defcolorway "positive")
(defcolorway "negative")
(defcolorway "warning")
(defcolorway "gray")
(defcolorway "purple")
(defcolorway "blue")
(defcolorway "green")
(defcolorway "lime")
(defcolorway "yellow")
(defcolorway "gold")
(defcolorway "orange")
(defcolorway "red")
(defcolorway "magenta")
(defcolorway "brown")


;; Strokes
(defcss "[data-ks-surface][data-ks-stroke][data-ks-stroke-align=\"inside\"]"
  {:box-shadow "inset 0 0 0 var(--stroke-width, var(--element-stroke-width), 1px) color-mix(in oklch, currentColor var(--stroke-transparency, 50%), var(--stroke-transparency-mix-color, transparent))"
   })

(defcss "[data-ks-surface][data-ks-stroke][data-ks-stroke-align=\"outside\"]"
  {:box-shadow "0 0 0 var(--stroke-width, var(--element-stroke-width), 1px) color-mix(in oklch, currentColor var(--stroke-transparency, 50%), var(--stroke-transparency-mix-color, transparent))" })



(defcss "[data-ks-surface][data-ks-stroke]"
  {"[data-ks-stroke=\"none\"]"   {:--stroke-transparency :0%}
   "[data-ks-stroke=\"xsoft\"]"  {:--stroke-transparency :$xsoft-stroke-transparency}
   "[data-ks-stroke=\"soft\"]"   {:--stroke-transparency :$soft-stroke-transparency}
   "[data-ks-stroke=\"medium\"]" {:--stroke-transparency :$medium-stroke-transparency}
   "[data-ks-stroke=\"hard\"]"   {:--stroke-transparency :$hard-stroke-transparency}
   "[data-ks-stroke=\"xhard\"]"  {:--stroke-transparency :$xhard-stroke-transparency}})

(defcss "[class*=\"surface-\"][class*=\"stroke-\"]"
  {".stroke-align-outside"    {"--stroke-inset" ""}
   :--box-shadow-for-stroke   "var(--stroke-inset, inset) 0 0 0 var(--stroke-width, var(--element-stroke-width), 1px) color-mix(in oklch, currentColor var(--stroke-transparency, 50%), var(--stroke-transparency-mix-color, transparent))" 
   :box-shadow                "var(--box-shadow-for-stroke, 0 0 0 transparent), var(--shadow, 0 0 0 transparent)" 
   ".stroke-none"             {:--stroke-transparency :0%}
   ".stroke-xsoft"            {:--stroke-transparency :$xsoft-stroke-transparency}
   ".stroke-soft"             {:--stroke-transparency :$soft-stroke-transparency}
   ".stroke-medium"           {:--stroke-transparency :$medium-stroke-transparency}
   ".stroke-hard"             {:--stroke-transparency :$hard-stroke-transparency}
   ".stroke-xhard"            {:--stroke-transparency :$xhard-stroke-transparency}})

;; (defcss "[data-ks-surface][data-ks-drop-shadow=\"xsmall\"]"
;;   {:box-shadow      :$shadow-xsmall
;;    :dark:box-shadow :$shadow-xsmall-dark-mode})

;; (defcss "[data-ks-surface][data-ks-drop-shadow=\"small\"]"
;;   {:box-shadow      :$shadow-small
;;    :dark:box-shadow :$shadow-small-dark-mode})

;; (defcss "[data-ks-surface][data-ks-drop-shadow=\"medium\"]"
;;   {:box-shadow      :$shadow-medium
;;    :dark:box-shadow :$shadow-medium-dark-mode})

;; (defcss "[data-ks-surface][data-ks-drop-shadow=\"large\"]"
;;   {:box-shadow      :$shadow-large
;;    :dark:box-shadow :$shadow-large-dark-mode
;;    })

;; (defcss "[data-ks-surface][data-ks-drop-shadow=\"xlarge\"]"
;;   {:box-shadow      :$shadow-xlarge
;;    :dark:box-shadow :$shadow-xlarge-dark-mode})

(defcss "[data-ks-surface=\"solid\"], [data-ks-surface=\"solid-classic\"]"
  {:color             :white
   :dark:color        :black
   :hover:color       :white
   :dark:hover:color  :black
   :active:color      :white
   :dark:active:color :black})
                                     

;; Classics
(defcss "[data-ks-surface=\"solid-classic\"], [data-ks-surface=\"soft-classic\"], .surface-solid-classic"
  {:box-shadow       "inset 0 0 0 1px var(--transparent-black-10), inset 0 -2px 1px var(--transparent-black-20), inset 0 0 0 1px var(--classic-trim-color), inset 0 4px 2px -2px var(--transparent-white-80), inset 0 2px 1px -1px var(--transparent-white-80)"
   :bgi              "linear-gradient(to bottom,#0000 50%,var(--transparent-black-09)),linear-gradient(to bottom,#0000 50%, var(--classic-trim-color) 80%)"
   :z-index          0
   :after            {:content          "\"\""
                      :position         :absolute
                      :border-radius    :inherit
                      :pointer-events   :none
                      :inset            0
                      :z-index          -1
                      :border           :2px:solid:#0000
                      :background-clip  :content-box
                      :background-color :inherit
                      :background-image "linear-gradient(var(--transparent-black-05), #0000, var(--transparent-white-10))"
                      :box-shadow       "inset 0 2px 3px -1px var(--transparent-white-30)"}})

(defcss "[data-ks-surface=\"soft-classic\"]"
  {:dark:text-shadow "0 0px 2px var(--transparent-black-40), 0 -0.5px 0px var(--transparent-black-70)"
   :text-shadow      "0 0.5px 0px var(--transparent-white-100)"
   :box-shadow       "inset 0 0 0 1px var(--transparent-black-05), inset 0 -2px 1px var(--transparent-black-10), inset 0 0 0 1px var(--classic-trim-color), inset 0 4px 2px -2px var(--transparent-white-100), inset 0 2px 1px -1px var(--transparent-white-100)"
   :dark:box-shadow  "inset 0 0 0 1px var(--transparent-black-05), inset 0 -2px 1px var(--transparent-black-10), inset 0 0 0 1px var(--classic-trim-color-dark), inset 0 4px 2px -2px var(--transparent-white-60), inset 0 2px 1px -1px var(--transparent-white-60)"
   :bgi              "linear-gradient(to bottom,#0000 50%,var(--transparent-black-09)),linear-gradient(to bottom,#0000 50%, var(--classic-trim-color) 80%)"
   :dark:bgi         "linear-gradient(to bottom,#0000 50%,var(--transparent-black-09)),linear-gradient(to bottom,#0000 50%, var(--classic-trim-color-dark) 80%)"
   })

(defcss "[data-ks-surface=\"solid-classic\"]"
  {:text-shadow      "0 0px 2px var(--transparent-black-30)"
   :dark:text-shadow "0 0.5px 0px var(--transparent-white-40)"})

(defcss ".dark [data-ks-surface=\"solid-classic\"]"
  {:after {:background-image "linear-gradient(var(--transparent-black-02), #0000, var(--transparent-white-20))"}})

(defcss "[data-ks-shape=\"rounded\"]" {:border-radius :$rounded-medium})
(defcss "[data-ks-shape=\"rounded-xxxsmall\"]" {:border-radius :$rounded-xxxsmall})
(defcss "[data-ks-shape=\"rounded-xxsmall\"]" {:border-radius :$rounded-xxsmall})
(defcss "[data-ks-shape=\"rounded-xsmall\"]" {:border-radius :$rounded-xsmall})
(defcss "[data-ks-shape=\"rounded-small\"]" {:border-radius :$rounded-small})
(defcss "[data-ks-shape=\"rounded-medium\"]" {:border-radius :$rounded-medium})
(defcss "[data-ks-shape=\"rounded-large\"]" {:border-radius :$rounded-large})
(defcss "[data-ks-shape=\"rounded-xlarge\"]" {:border-radius :$rounded-xlarge})
(defcss "[data-ks-shape=\"rounded-xxlarge\"]" {:border-radius :$rounded-xxlarge})
(defcss "[data-ks-shape=\"rounded-xxxlarge\"]" {:border-radius :$rounded-xxxlarge})

(defcss "[data-ks-shape=\"rounded-absolute\"]" {:border-radius :$rounded-medium-absolute})
(defcss "[data-ks-shape=\"rounded-xxxsmall-absolute\"]" {:border-radius :$rounded-xxxsmall-absolute})
(defcss "[data-ks-shape=\"rounded-xxsmall-absolute\"]" {:border-radius :$rounded-xxsmall-absolute})
(defcss "[data-ks-shape=\"rounded-xsmall-absolute\"]" {:border-radius :$rounded-xsmall-absolute})
(defcss "[data-ks-shape=\"rounded-small-absolute\"]" {:border-radius :$rounded-small-absolute})
(defcss "[data-ks-shape=\"rounded-medium-absolute\"]" {:border-radius :$rounded-medium-absolute})
(defcss "[data-ks-shape=\"rounded-large-absolute\"]" {:border-radius :$rounded-large-absolute})
(defcss "[data-ks-shape=\"rounded-xlarge-absolute\"]" {:border-radius :$rounded-xlarge-absolute})
(defcss "[data-ks-shape=\"rounded-xxlarge-absolute\"]" {:border-radius :$rounded-xxlarge-absolute})
(defcss "[data-ks-shape=\"rounded-xxxlarge-absolute\"]" {:border-radius :$rounded-xxxlarge-absolute})

(defcss "[data-ks-shape=\"pill\"], [data-ks-shape=\"circle\"]"
  {:border-radius :9999px})

;; For generating code
#_(println
   (string/join "\n\n "
                (for [sz [:xxxsmall
                            :xxsmall
                            :xsmall
                            :small
                            :medium
                            :large
                            :xlarge
                            :xxlarge
                            :xxxlarge]] 

                  (list 'defcss
                        (str "[data-ks-shape=\"" (name sz) "\"]")
                        {:font-size (keyword (str "$" (name sz)))}))))


;; Move to button ns?
(defcss "[data-ks-ui=\"button\"]"
  {:--_padding-inline               "var(--button-padding-inline)"
   :--_padding-block                "var(--button-padding-block)"
   "[data-ks-packing=\"compact\"]" {:--_padding-inline "var(--button-padding-inline-compact)"
                                     :--_padding-block  "var(--button-padding-block-compact)"}
   
   "[data-ks-packing=\"roomy\"]"   {:--_padding-inline "var(--button-padding-inline-roomy)"
                                     :--_padding-block  "var(--button-padding-block-roomy)"}
   
   "[data-ks-icon-button]"         {:--_padding-inline           "var(--_padding-block)"
                                     :_.material-symbols-outlined {:min-width   :1.2ch
                                                                   :line-height :normal}}})
(defcss "[data-ks-ui=\"icon-button\"]"
  {:--_padding-block            :$icon-button-padding-block||$button-padding-block
   :--_padding-inline           :$icon-button-padding-inline||$button-padding-inline
   :_.material-symbols-outlined {:min-width   :1.2ch
                                 :line-height :normal}
   "[data-ks-packing=\"compact\"]" {:--_padding-inline :$icon-button-padding-block-compact||$button-padding-block-compact
                                     :--_padding-block  :$icon-button-padding-block-compact||$button-padding-block-compact}
   "[data-ks-packing=\"roomy\"]"   {:--_padding-inline :$icon-button-padding-block-roomy||$button-padding-block-roomy
                                     :--_padding-block  :$icon-button-padding-block-roomy||$button-padding-block-roomy}})


;; Keep here?

;; TODO  (defcss {:data-ks-ui :button :data-ks-color :accent} ...) => "[data-ks-ui=\"button\"][data-ks-ui=\"accent\"]" ;;
;; TODO  (defcss [{:data-ks-ui :button} {:data-ks-ui :tag}] ...) => "[data-ks-ui=\"button\"], [data-ks-ui=\"tag\"]" ;;
(defcss "[data-ks-ui=\"button\"], [data-ks-ui=\"tag\"]"
  {"[data-ks-start-enhancer]"      {:padding-inline-start "calc(var(--_padding-inline) * 0.7666)"}
   "[data-ks-end-enhancer]"        {:padding-inline-end "calc(var(--_padding-inline) * 0.7666)"}})



;; Move to tag ns
(defcss "[data-ks-ui=\"tag\"]"
  {"[data-ks-packing=\"compact\"]"
   {:--_padding-inline      "var(--tag-padding-inline-compact)"
    :--_padding-block-start "calc(var(--tag-padding-block-compact) * var(--tag-padding-block-start-reduction-ratio, 1))"
    :--_padding-block-end   "calc(var(--tag-padding-block-compact))"
    }

   "[data-ks-packing=\"roomy\"]"
   {:--_padding-inline      "var(--tag-padding-inline-roomy)"
    :--_padding-block-start "calc(var(--tag-padding-block-roomy) * var(--tag-padding-block-start-reduction-ratio, 1))"
    :--_padding-block-end   "calc(var(--tag-padding-block-roomy))"
    }})

;; Move to switch ns
(defcss ".kushi-switch"
  {:--switch-thumb-scale-factor                   1
   :--switch-width-ratio                          1.85
   :--switch-border-color                         :transparent
   :--switch-border-width                         :2px
   :--switch-off-background-color                 :$background-color-neutral-soft-5
   :--switch-off-background-color-hover           :$background-color-neutral-soft-6
   :--switch-off-background-color-dark-mode       :$background-color-neutral-soft-dark-mode
   :--switch-off-background-color-hover-dark-mode :$background-color-neutral-soft2-dark-mode
   })

;; display
(defcss "[data-ks-display=\"flex\"]" :d--flex)
(defcss "[data-ks-display=\"grid\"]" :d--grid)
(defcss "[data-ks-display=\"none\"]" :d--grid)

(defcss "[data-ks-fd=\"row\"]" :flex-direction--row)
(defcss "[data-ks-fd=\"row-reverse\"]" :flex-direction--row-reverse)
(defcss "[data-ks-fd=\"column\"]" :flex-direction--column)
(defcss "[data-ks-fd=\"column-reverse\"]" :flex-direction--column-reverse)

(defcss "[data-ks-jc=\"center\"]" :jc--center)
(defcss "[data-ks-jc=\"flex-start\"]" :jc--flex-start)
(defcss "[data-ks-jc=\"space-around\"]" :jc--space-around)
(defcss "[data-ks-jc=\"space-between\"]" :jc--space-between)

(defcss "[data-ks-ai=\"center\"]" :ai--center)
(defcss "[data-ks-ai=\"flex-start\"]" :ai--flex-start)
(defcss "[data-ks-ai=\"flex-end\"]" :ai--flex-end)


;; Shadows experimentation ------------------------------------------------------

;; TODO - selector should be
;; (defcss {:data-ks-surface      ""
;;          :data-ks-drop-shadow  ""
;;          :data-ks-shadow-color :blue} 
;;   {:--shadow-color             :$blue-500})


;; For generating code
#_(?
 :pp
 {:non-coll-mapkey-length-limit 50}
 (->> kushi.colors/colors 
      (apply array-map) 
      (reduce-kv (fn [acc k v] (conj acc k (:alias v))) [])
      (remove nil?)
      (reduce (fn [acc color] 
                (assoc acc
                       (str "&[data-ks-shadow-color=\"" color "\"]" )
                       {:--shadow-color (keyword (str "$" color "-500"))}))
              {})
      (list 'defcss "[data-ks-surface][data-ks-drop-shadow]")))
 

;; modern version
(defcss
  "[data-ks-surface][data-ks-drop-shadow]"
  {
   "&[data-ks-shadow-color=\"positive\"]" {:--shadow-color :$positive-500},
   "&[data-ks-shadow-color=\"neutral\"]"  {:--shadow-color :$neutral-500},
   "&[data-ks-shadow-color=\"negative\"]" {:--shadow-color :$negative-500},
   "&[data-ks-shadow-color=\"purple\"]"   {:--shadow-color :$purple-500},
   "&[data-ks-shadow-color=\"magenta\"]"  {:--shadow-color :$magenta-500},
   "&[data-ks-shadow-color=\"accent\"]"   {:--shadow-color :$accent-500},
   "&[data-ks-shadow-color=\"brown\"]"    {:--shadow-color :$brown-500},
   "&[data-ks-shadow-color=\"blue\"]"     {:--shadow-color :$blue-500},
   "&[data-ks-shadow-color=\"orange\"]"   {:--shadow-color :$orange-500},
   "&[data-ks-shadow-color=\"gray\"]"     {:--shadow-color :$gray-500},
   "&[data-ks-shadow-color=\"warning\"]"  {:--shadow-color :$warning-500},
   "&[data-ks-shadow-color=\"green\"]"    {:--shadow-color :$green-500},
   "&[data-ks-shadow-color=\"gold\"]"     {:--shadow-color :$gold-500},
   "&[data-ks-shadow-color=\"lime\"]"     {:--shadow-color :$lime-500},
   "&[data-ks-shadow-color=\"yellow\"]"   {:--shadow-color :$yellow-500},
   "&[data-ks-shadow-color=\"red\"]"      {:--shadow-color :$red-500}
   })

;; legacy-version
;; (defcss "[data-ks-surface][data-ks-drop-shadow][data-ks-shadow-color=\"red\"]" 
;;   {
;;    :--shadow-color-red-h-s-l         "10 100 50" ; <- this would be in :root
;;    :--shadow-color-h-s-l             :$shadow-color-red-h-s-l})

;; super legacy-version, no support for shadow-strength
;; (defcss "[data-ks-surface][data-ks-drop-shadow][data-ks-shadow-color=\"red\"]" 
;;   {
;;    :--shadow-color-red-hex         "#f908244d" ; <- this would be in :root
;;    :--shadow-color-hex             :$shadow-color-red-hex})



;; modern version
(defcss "[data-ks-surface][data-ks-drop-shadow], [class*=\"surface-\"][class*=\"shadow-\"], [data-ks-surface][data-ks-shadow]" 
  {
   :--transparent-shadow-color "color-mix(in oklch, var(--shadow-color, black) var(--shadow-strength, 20%), transparent)"
  ;;  :--transparent-shadow-color-1 "color-mix(in oklch, var(--shadow-color, black) var(--shadow-strength, 2%), transparent)"
  ;;  :--transparent-shadow-color-2 "color-mix(in oklch, var(--shadow-color, black) var(--shadow-strength, 12%), transparent)"
   })

;; legacy version
;; (defcss "[data-ks-surface][data-ks-drop-shadow]" 
;;   {:--shadow-color-h-s-l     "0 0 0"
;;    :--shadow-strength      "20%"
;;    ;; TODO - figure out whether the slash "/" syntax works in older browsers
;;    :--transparent-shadow-color "hsl(var(--shadow-color-h-s-l) / var(--shadow-strength))"})

;; super-legacy version
;; (defcss "[data-ks-surface][data-ks-drop-shadow]" 
;;   {;; TODO - figure out whether the slash "/" syntax works in older browsers
;;    :--transparent-shadow-color "var(--shadow-color-hex)"})


(defcss "[class*=\"surface-\"][class*=\"shadow-\"]" 
  {:box-shadow "var(--box-shadow-for-stroke, 0 0 0 transparent), var(--shadow, 0 0 0 transparent)"})

(defcss "[data-ks-surface][data-ks-drop-shadow=\"xxxsmall\"]"
  [:box-shadow "0 1px 3px -1px var(--transparent-shadow-color)"])

(defcss "[data-ks-surface][data-ks-drop-shadow=\"xxsmall\"]"
  [:box-shadow "0 3px 6px -2px var(--transparent-shadow-color)"])

(defcss "[data-ks-surface][data-ks-drop-shadow=\"xsmall\"]"
  [:box-shadow "0 5px 12px -4px var(--transparent-shadow-color), 0 2px 6px -4px var(--transparent-shadow-color)"])

(defcss "[data-ks-surface][data-ks-drop-shadow=\"small\"]"
  [:box-shadow "0 7px 13px -3px var(--transparent-shadow-color), 0 2px 3px -3px var(--transparent-shadow-color)"])

(defcss "[data-ks-surface][data-ks-drop-shadow=\"medium\"]"
  {:box-shadow "0 11px 21px -6px var(--transparent-shadow-color), 0 3px 7px -5px var(--transparent-shadow-color)"})

(defcss "[data-ks-surface][data-ks-drop-shadow=\"large\"]"
  [:box-shadow "0 16px 26px -8px var(--transparent-shadow-color), 0 4px 10px -8px var(--transparent-shadow-color)"])

(defcss "[data-ks-surface][data-ks-drop-shadow=\"xlarge\"]"
  [:box-shadow "0 22px 36px -12px var(--transparent-shadow-color), 0 8px 10px -9px var(--transparent-shadow-color)"])

(defcss "[data-ks-surface][data-ks-drop-shadow=\"xxlarge\"]"
  [:box-shadow "0 25px 52px -11px var(--transparent-shadow-color), 0 9px 10px -10px var(--transparent-shadow-color)"])

(defcss "[data-ks-surface][data-ks-drop-shadow=\"xxxlarge\"]"
  [:box-shadow "0 27px 60px -14px var(--transparent-shadow-color), 0 10px 10px -12px var(--transparent-shadow-color)"])

(defcss "[class*=\"surface-\"]" 
  {".shadow-xxxsmall" {:--shadow :$shadow-xxxsmall}
   ".shadow-xxsmall" {:--shadow :$shadow-xxsmall}
   ".shadow-xsmall" {:--shadow :$shadow-xsmall}
   ".shadow-small" {:--shadow :$shadow-small}
   ".shadow-medium" {:--shadow :$shadow-medium}
   ".shadow-large" {:--shadow :$shadow-large}
   ".shadow-xlarge" {:--shadow :$shadow-xlarge}
   ".shadow-xxlarge" {:--shadow :$shadow-xxlarge}
   ".shadow-xxxlarge" {:--shadow :$shadow-xxxlarge}})

;; (defcss "[data-ks-surface][data-ks-drop-shadow=\"xxsmall\"], [class*=\"surface-\"].shadow-xxsmall" 
;;   [:box-shadow "0 3px 6px -2px var(--transparent-shadow-color)"])

;; (defcss "[data-ks-surface][data-ks-drop-shadow=\"xsmall\"], [class*=\"surface-\"].shadow-xsmall"  
;;   [:box-shadow "0 5px 12px -4px var(--transparent-shadow-color), 0 2px 6px -4px var(--transparent-shadow-color)"])

;; (defcss "[data-ks-surface][data-ks-drop-shadow=\"small\"], [class*=\"surface-\"].shadow-small"  
;;   [:box-shadow "0 7px 13px -3px var(--transparent-shadow-color), 0 2px 3px -3px var(--transparent-shadow-color)"])

;; (defcss "[data-ks-surface][data-ks-drop-shadow=\"medium\"], [class*=\"surface-\"].shadow-medium"  
;;   {:box-shadow "0 11px 21px -6px var(--transparent-shadow-color), 0 3px 7px -5px var(--transparent-shadow-color)"})

;; (defcss "[data-ks-surface][data-ks-drop-shadow=\"large\"], [class*=\"surface-\"].shadow-large"  
;;   [:box-shadow "0 16px 26px -8px var(--transparent-shadow-color), 0 4px 10px -8px var(--transparent-shadow-color)"])

;; (defcss "[data-ks-surface][data-ks-drop-shadow=\"xlarge\"], [class*=\"surface-\"].shadow-xlarge"  
;;   [:box-shadow "0 22px 36px -12px var(--transparent-shadow-color), 0 8px 10px -9px var(--transparent-shadow-color)"])

;; (defcss "[data-ks-surface][data-ks-drop-shadow=\"xxlarge\"], [class*=\"surface-\"].shadow-xxlarge" 
;;   [:box-shadow "0 25px 52px -11px var(--transparent-shadow-color), 0 9px 10px -10px var(--transparent-shadow-color)"])

;; (defcss "[data-ks-surface][data-ks-drop-shadow=\"xxxlarge\"], [class*=\"surface-\"].shadow-xxxlarge" 
;;   [:box-shadow "0 27px 60px -14px var(--transparent-shadow-color), 0 10px 10px -12px var(--transparent-shadow-color)"])



;;  ---------------------------------------------------------------------------------------------------------
;;  ---------------------------------------------------------------------------------------------------------
;;  NEW STUFF Sept 14 2025
;;  
    ;; Color notes
    ;; '{:solid {
    ;;           :yellow {:chroma    [0.134 0.12 0.1]
    ;;                    :lightness [0.629 0.57 0.46]}
    ;;           :blue   {:chroma    [0.2 0.185 0.17]
    ;;                    :lightness [0.48 0.439 0.4]}
    ;;           :green  {:chroma    [0.15 0.136 0.13]
    ;;                    :lightness [0.55 0.502 0.47]}
    ;;           :red    {:chroma    [0.19 0.179 0.17]
    ;;                    :lightness [0.5 0.465 0.43]}}
    ;;   :soft  {
    ;;           :yellow {:chroma    [0.134 0.12 0.1]
    ;;                    :lightness [0.629 0.57 0.46]}
    ;;           :blue   {:chroma    [0.2 0.185 0.17]
    ;;                    :lightness [0.48 0.439 0.4]}

    ;;           :green  {:chroma    [0.06 0.09 0.12]
    ;;                    :lightness [0.94 0.905 0.87]}
    ;;           :red    {:chroma    [0.05 0.067 0.09]
    ;;                    :lightness [0.91 0.87 0.82]}

    ;;           }}
;;  ---------------------------------------------------------------------------------------------------------
;;  ---------------------------------------------------------------------------------------------------------

    ;; Colorway hues   ---------------------------------------------------------------------------------------------------------
    (defcss ".colorway-green, .colorway-positive" {:--colorway-hue "var(--green-hue-oklch)"})
    (defcss ".colorway-yellow, .colorway-warning" {:--colorway-hue "var(--yellow-hue-oklch)"})
    (defcss ".colorway-blue, .colorway-accent" {:--colorway-hue "var(--blue-hue-oklch)"})
    (defcss ".colorway-red, .colorway-negative" {:--colorway-hue "var(--red-hue-oklch)"})
    (defcss ".colorway-neutral" {:--colorway-hue :$gray-hue-oklch})
    (defcss ".colorway-gray"    {:--colorway-hue :$gray-hue-oklch})
    (defcss ".colorway-purple"  {:--colorway-hue :$purple-hue-oklch})
    (defcss ".colorway-magenta" {:--colorway-hue :$magenta-hue-oklch})
    (defcss ".colorway-brown"   {:--colorway-hue :$brown-hue-oklch})
    (defcss ".colorway-orange"  {:--colorway-hue :$orange-hue-oklch})
    (defcss ".colorway-gold"    {:--colorway-hue :$gold-hue-oklch})
    (defcss ".colorway-lime"    {:--colorway-hue :$lime-hue-oklch})


    ;; Surface styles  ---------------------------------------------------------------------------------------------------------
    
    ;; neutralize chroma shift
    (defcss "[class*=\"surface-\"].colorway-neutral" 
      {:--chroma       0
       :--fgc-chroma   0
       :--chroma-shift 0})

    ;; Solids
    (defcss ".surface-solid, .surface-solid-classic"
      {:--lightness          0.53
       :--chroma             0.2
       :--classic-trim-color "oklch(0.62 0.17 var(--colorway-hue))" 
       :bgc                  "oklch(var(--lightness) var(--chroma) var(--colorway-hue))" 
       :hover:bgc            "oklch(calc(var(--lightness) - 0.04) calc(var(--chroma) - 0.01) var(--colorway-hue))" 
       :active:bgc           "oklch(calc(var(--lightness) - 0.08) calc(var(--chroma) - 0.02) var(--colorway-hue))" 
       :color                :white})

    (defcss ".surface-soft, .surface-soft-classic, .surface-faint, .surface-minimal, .surface-transparent"
      {:--chroma-shift 0.025
       :hover:bgc      "oklch(calc(var(--lightness) - 0.025) calc(var(--chroma) + var(--chroma-shift)) var(--colorway-hue))" 
       :active:bgc     "oklch(calc(var(--lightness) - 0.05) calc(var(--chroma) + calc(2 * var(--chroma-shift))) var(--colorway-hue))"}) 

    ;; Softs
    (defcss ".surface-soft, .surface-soft-classic" 
      {:--lightness          0.93
       :--chroma             0.065
       :--fgc-chroma         0.13
       :--classic-trim-color "oklch(0.91 0.04 var(--colorway-hue))" 
       :color                "oklch(0.3 var(--fgc-chroma) var(--colorway-hue))" 
       :bgc                  "oklch(var(--lightness) var(--chroma) var(--colorway-hue))"})


    ;; Faint
    (defcss ".surface-faint" 
      {:--lightness  0.980
       :--chroma     0.025
       :--fgc-chroma 0.185
       :color        "oklch(0.439 var(--fgc-chroma) var(--colorway-hue))" 
       :bgc          "oklch(var(--lightness) var(--chroma) var(--colorway-hue))"})


    ;; Minimal
    (defcss ".surface-minimal" 
      {:--lightness  1 
       :--chroma     0
       :--fgc-chroma 0.185
       :color        "oklch(0.439 var(--fgc-chroma) var(--colorway-hue))" 
       :bgc          :$background-color})

    ;; Transparent
    (defcss ".surface-transparent" 
      {:color "oklch(0.439 0.185 var(--colorway-hue))"})


    ;; Classic details
    (defcss ".surface-solid-classic, .surface-soft-classic"
      {:box-shadow "inset 0 0 0 1px var(--transparent-black-10), inset 0 -2px 1px var(--transparent-black-20), inset 0 0 0 1px var(--classic-trim-color), inset 0 4px 2px -2px var(--transparent-white-80), inset 0 2px 1px -1px var(--transparent-white-80)"
       :bgi        "linear-gradient(to bottom,#0000 50%,var(--transparent-black-09)),linear-gradient(to bottom,#0000 50%, var(--classic-trim-color) 80%)"
       :z-index    0
       :after      {:content          "\"\""
                    :position         :absolute
                    :border-radius    :inherit
                    :pointer-events   :none
                    :inset            0
                    :z-index          -1
                    :border           :2px:solid:#0000
                    :background-clip  :content-box
                    :background-color :inherit
                    :background-image "linear-gradient(var(--transparent-black-05), #0000, var(--transparent-white-10))"
                    :box-shadow       "inset 0 2px 3px -1px var(--transparent-white-30)"}})

    (defcss ".surface-soft-classic"
      {:dark:text-shadow "0 0px 2px var(--transparent-black-40), 0 -0.5px 0px var(--transparent-black-70)"
       :text-shadow      "0 0.5px 0px var(--transparent-white-100)"
       :box-shadow       "inset 0 0 0 1px var(--transparent-black-05), inset 0 -2px 1px var(--transparent-black-10), inset 0 0 0 1px var(--classic-trim-color), inset 0 4px 2px -2px var(--transparent-white-100), inset 0 2px 1px -1px var(--transparent-white-100)"
       :dark:box-shadow  "inset 0 0 0 1px var(--transparent-black-05), inset 0 -2px 1px var(--transparent-black-10), inset 0 0 0 1px var(--classic-trim-color-dark), inset 0 4px 2px -2px var(--transparent-white-60), inset 0 2px 1px -1px var(--transparent-white-60)"
       :bgi              "linear-gradient(to bottom,#0000 50%,var(--transparent-black-09)),linear-gradient(to bottom,#0000 50%, var(--classic-trim-color) 80%)"
       :dark:bgi         "linear-gradient(to bottom,#0000 50%,var(--transparent-black-09)),linear-gradient(to bottom,#0000 50%, var(--classic-trim-color-dark) 80%)"
       })

    (defcss ".surface-solid-classic"
      {:text-shadow      "0 0px 2px var(--transparent-black-30)"
       :dark:text-shadow "0 0.5px 0px var(--transparent-white-40)"})

    (defcss ".dark .surface-solid-classic"
      {:after {:background-image "linear-gradient(var(--transparent-black-02), #0000, var(--transparent-white-20))"}})


    ;; Surface shaping ---------------------------------------------------------------------------------------------------------
    (defcss "[class*=\"surface-\"]"
      {".shape-pill"                      {:br :9999px}     
       ".shape-rounded"                   {:br :$rounded-medium}
       ".shape-rounded-xxxsmall"          {:br :$rounded-xxxsmall}
       ".shape-rounded-xxsmall"           {:br :$rounded-xxsmall}
       ".shape-rounded-xsmall"            {:br :$rounded-xsmall}
       ".shape-rounded-small"             {:br :$rounded-small}
       ".shape-rounded-medium"            {:br :$rounded-medium}
       ".shape-rounded-large"             {:br :$rounded-large}
       ".shape-rounded-xlarge"            {:br :$rounded-xlarge}
       ".shape-rounded-xxlarge"           {:br :$rounded-xxlarge}
       ".shape-rounded-xxxlarge"          {:br :$rounded-xxxlarge}
       ".shape-rounded-absolute"          {:br :$rounded-medium-absolute}
       ".shape-rounded-xxxsmall-absolute" {:br :$rounded-xxxsmall-absolute}
       ".shape-rounded-xxsmall-absolute"  {:br :$rounded-xxsmall-absolute}
       ".shape-rounded-xsmall-absolute"   {:br :$rounded-xsmall-absolute}
       ".shape-rounded-small-absolute"    {:br :$rounded-small-absolute}
       ".shape-rounded-medium-absolute"   {:br :$rounded-medium-absolute}
       ".shape-rounded-large-absolute"    {:br :$rounded-large-absolute}
       ".shape-rounded-xlarge-absolute"   {:br :$rounded-xlarge-absolute}
       ".shape-rounded-xxlarge-absolute"  {:br :$rounded-xxlarge-absolute}
       ".shape-rounded-xxxlarge-absolute" {:br :$rounded-xxxlarge-absolute}}
      
      )

    
   ;; Surface stroking ---------------------------------------------------------------------------------------------------------
    (defcss "[class*=\"surface-\"][class*=\"stroke-\"]"
      {".stroke-align-outside"    {"--stroke-inset" ""}
       :--box-shadow-for-stroke "var(--stroke-inset, inset) 0 0 0 var(--stroke-width, var(--element-stroke-width), 1px) color-mix(in oklch, currentColor var(--stroke-transparency, 50%), var(--stroke-transparency-mix-color, transparent))" 
       :box-shadow              "var(--box-shadow-for-stroke, 0 0 0 transparent), var(--shadow, 0 0 0 transparent)" 
       ".stroke-none"             {:--stroke-transparency :0%}
       ".stroke-xsoft"            {:--stroke-transparency :$xsoft-stroke-transparency}
       ".stroke-soft"             {:--stroke-transparency :$soft-stroke-transparency}
       ".stroke-medium"           {:--stroke-transparency :$medium-stroke-transparency}
       ".stroke-hard"             {:--stroke-transparency :$hard-stroke-transparency}
       ".stroke-xhard"            {:--stroke-transparency :$xhard-stroke-transparency}})


    ;; Surface shadowing ---------------------------------------------------------------------------------------------------------
    (defcss "[class*=\"surface-\"][class*=\"shadow-\"]" 
      {:--transparent-shadow-color "color-mix(in oklch, var(--shadow-color, black) var(--shadow-strength, 20%), transparent)"
       :--shadow-xxxsmall          "0 1px 3px -1px var(--transparent-shadow-color)"
       :--shadow-xxsmall           "0 3px 6px -2px var(--transparent-shadow-color)"
       :--shadow-xsmall            "0 5px 12px -4px var(--transparent-shadow-color), 0 2px 6px -4px var(--transparent-shadow-color)"
       :--shadow-small             "0 7px 13px -3px var(--transparent-shadow-color), 0 2px 3px -3px var(--transparent-shadow-color)"
       :--shadow-medium            "0 11px 21px -6px var(--transparent-shadow-color), 0 3px 7px -5px var(--transparent-shadow-color)"
       :--shadow-large             "0 16px 26px -8px var(--transparent-shadow-color), 0 4px 10px -8px var(--transparent-shadow-color)"
       :--shadow-xlarge            "0 22px 36px -12px var(--transparent-shadow-color), 0 8px 10px -9px var(--transparent-shadow-color)"
       :--shadow-xxlarge           "0 25px 52px -11px var(--transparent-shadow-color), 0 9px 10px -10px var(--transparent-shadow-color)"
       :--shadow-xxxlarge          "0 27px 60px -14px var(--transparent-shadow-color), 0 10px 10px -12px var(--transparent-shadow-color)"
       ".shadow-xxxsmall"            {:--shadow :$shadow-xxxsmall}
       ".shadow-xxsmall"             {:--shadow :$shadow-xxsmall}
       ".shadow-xsmall"              {:--shadow :$shadow-xsmall}
       ".shadow-small"               {:--shadow :$shadow-small}
       ".shadow-medium"              {:--shadow :$shadow-medium}
       ".shadow-large"               {:--shadow :$shadow-large}
       ".shadow-xlarge"              {:--shadow :$shadow-xlarge}
       ".shadow-xxlarge"             {:--shadow :$shadow-xxlarge}
       ".shadow-xxxlarge"            {:--shadow :$shadow-xxxlarge}
       ".shadow-color-positive"      {:--shadow-color :$positive-500},
       ".shadow-color-neutral"       {:--shadow-color :$neutral-500},
       ".shadow-color-negative"      {:--shadow-color :$negative-500},
       ".shadow-color-purple"        {:--shadow-color :$purple-500},
       ".shadow-color-magenta"       {:--shadow-color :$magenta-500},
       ".shadow-color-accent"        {:--shadow-color :$accent-500},
       ".shadow-color-brown"         {:--shadow-color :$brown-500},
       ".shadow-color-blue"          {:--shadow-color :$blue-500},
       ".shadow-color-orange"        {:--shadow-color :$orange-500},
       ".shadow-color-gray"          {:--shadow-color :$gray-500},
       ".shadow-color-warning"       {:--shadow-color :$warning-500},
       ".shadow-color-green"         {:--shadow-color :$green-500},
       ".shadow-color-gold"          {:--shadow-color :$gold-500},
       ".shadow-color-lime"          {:--shadow-color :$lime-500},
       ".shadow-color-yellow"        {:--shadow-color :$yellow-500},
       ".shadow-color-red"           {:--shadow-color :$red-500}})
