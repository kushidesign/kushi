(ns ^{:kushi/layer "kushi-ui-theming"} kushi.css.build.kushi-ui-component-theming
  (:require
   [kushi.colors]
   [bling.hifi]
   [kushi.css.build.macros :refer [defcolorway defcss]]))

(defcss
  "body"
  {"font-family"                "var(--sans-serif-font-stack)"
   "font-weight"                "var(--body-font-weight)"               
   "color"                      "var(--foreground-color)"
   "background-color"           "var(--background-color)"
   "transition-property"        "background-color, color"
   "transition-duration"        "var(--transition-fast)"
   "transition-timing-function" "var(--timing-linear-curve)"
   "overflow-y"                 "scroll"})

(defcss
  ".dark, body.dark"
  {"background-color" "var(--background-color-dark-mode)",
   "color" "var(--foreground-color-dark-mode)"})

(defcss
  "code"
  {"width"                      "fit-content",
   "transition-duration"        "var(--transition-fast)",
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
   "--callout-stroke-width"                           :$element-stroke-width
   "--avatar-stroke-width"                            :$element-stroke-width
   "--card-stroke-width"                              :$element-stroke-width
   "--tag-stroke-width"                               :$element-stroke-width
   "--thumb-stroke-width"                             :$element-stroke-width
   "--stroke-width-nearest-pixel"                     "round(nearest, var(--stroke-width, 1px), 1px)"

   "--outlined-tag-stroke-width"                      "var(--outlined-element-stroke-width)"
   "--outlined-callout-stroke-width"                  "var(--outlined-element-stroke-width)"

   "--button-padding-inline-xcompact"                 "0.55em",
   "--button-padding-inline-compact"                  "0.7em",
   "--button-padding-inline"                          "0.9em",
   "--button-padding-inline-roomy"                    "1.2em",
   "--button-padding-inline-xroomy"                   "1.5em",

   "--button-padding-block-xcompact"                  "0.275em",
   "--button-padding-block-compact"                   "0.35em",
   "--button-padding-block"                           "0.55em",
   "--button-padding-block-roomy"                     "0.75em"
   "--button-padding-block-xroomy"                    "1em"

   "--icon-button-padding-inline"                     :$button-padding-block,
   "--icon-button-padding-block"                      :$button-padding-block,
   ;; Use or not use -ems ?
   "--tag-padding-block-start-reduction-ratio"        "0.9",
   "--tag-padding-block-start"                        "0.27em",

   "--tag-padding-inline-xcompact"                    "0.275em",
   "--tag-padding-inline-compact"                     "0.45em",
   "--tag-padding-inline"                             "0.6em",
   "--tag-padding-inline-roomy"                       "0.75em",
   "--tag-padding-inline-xroomy"                      "1.0em",

   "--tag-padding-block-xcompact"                     "0.15em",
   "--tag-padding-block-compact"                      "0.20em",
   "--tag-padding-block"                              "0.3em",
   "--tag-padding-block-roomy"                        "0.45em"
   "--tag-padding-block-xroomy"                       "0.6em"

   "--transition-duration"                            :$transition-xxxfast
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


;; Remove?
(defcolorway "neutral")
;; (defcolorway "accent")
;; (defcolorway "positive")
;; (defcolorway "negative")
;; (defcolorway "warning")
;; (defcolorway "gray")
;; (defcolorway "purple")
;; (defcolorway "blue")
;; (defcolorway "green")
;; (defcolorway "lime")
;; (defcolorway "yellow")
;; (defcolorway "gold")
;; (defcolorway "orange")
;; (defcolorway "red")
;; (defcolorway "magenta")
;; (defcolorway "brown")




;; Move to button ns?
(defcss ".ks-button"
  {:--padding-inline :$button-padding-inline
   :--padding-block  :$button-padding-block
   ".packing-xcompact" {:--padding-inline :$button-padding-inline-xcompact
                        :--padding-block  :$button-padding-block-xcompact}
   ".packing-compact"  {:--padding-inline :$button-padding-inline-compact
                        :--padding-block  :$button-padding-block-compact}
   ".packing-roomy"    {:--padding-inline :$button-padding-inline-roomy
                        :--padding-block  :$button-padding-block-roomy}
   ".packing-xroomy"   {:--padding-inline :$button-padding-inline-xroomy
                        :--padding-block  :$button-padding-block-xroomy}
   ".ks-icon-button"   {:--padding-inline            :$padding-block
                        :_.material-symbols-outlined {:min-width   :1.2ch
                                                      :line-height :normal}}})
(defcss ".ks-icon-button"
  {:--padding-block             :$icon-button-padding-block||$button-padding-block
   :--padding-inline            :$icon-button-padding-inline||$button-padding-inline
   :_.material-symbols-outlined {:min-width   :1.2ch
                                 :line-height :normal}

   ".packing-xcompact"            {:--padding-inline :$icon-button-padding-block-compact||$button-padding-block-xcompact
                                   :--padding-block  :$icon-button-padding-block-compact||$button-padding-block-xcompact}
   ".packing-compact"             {:--padding-inline :$icon-button-padding-block-compact||$button-padding-block-compact
                                   :--padding-block  :$icon-button-padding-block-compact||$button-padding-block-compact}
   ".packing-roomy"               {:--padding-inline :$icon-button-padding-block-roomy||$button-padding-block-roomy
                                   :--padding-block  :$icon-button-padding-block-roomy||$button-padding-block-roomy}
   ".packing-xroomy"              {:--padding-inline :$icon-button-padding-block-roomy||$button-padding-block-xroomy
                                   :--padding-block  :$icon-button-padding-block-roomy||$button-padding-block-xroomy}
   
   })


;; Move to tag ns
(defcss ".ks-tag"
  {".packing-xcompact" {:--padding-inline :$tag-padding-inline-xcompact
                        :--padding-block  :$tag-padding-block-xcompact}
   ".packing-compact"  {:--padding-inline :$tag-padding-inline-compact
                        :--padding-block  :$tag-padding-block-compact}
   ".packing-roomy"    {:--padding-inline :$tag-padding-inline-roomy
                        :--padding-block  :$tag-padding-block-roomy}
   ".packing-xroomy"   {:--padding-inline :$tag-padding-inline-xroomy
                        :--padding-block  :$tag-padding-block-xroomy}})

;; Move to switch ns
(defcss ".ks-switch"
  {:--switch-thumb-scale-factor                   1
   :--switch-width-ratio                          1.85
   :--switch-border-color                         :transparent
   :--switch-border-width                         :2px
   :--switch-off-background-color                 :$background-color-neutral-soft-5
   :--switch-off-background-color-hover           :$background-color-neutral-soft-6
   :--switch-off-background-color-dark-mode       :$background-color-neutral-soft-dark-mode
   :--switch-off-background-color-hover-dark-mode :$background-color-neutral-soft2-dark-mode
   })



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
    (defcss "[class*=\"surface-\"].colorway-neutral, [class*=\"surface-\"].colorway-gray" 
      {:--chroma-bgc   :0%
       :--chroma-fgc   :0%
       :--chroma-shift :0%
       :dark           {:--chroma-bgc   :0%
                        :--chroma-fgc   :0%
                        :--chroma-shift :0%}})

    (defcss ".surface-solid, .surface-solid-classic, .surface-soft, .surface-soft-classic, .surface-faint, .surface-convex, .surface-minimal, .surface-transparent, .surface-minimal-light-mode, .surface-convex-light-mode"
      {:--hover-bgc  "oklch(calc(var(--lightness-bgc) var(--lightness-shift-op, -) var(--lightness-shift)) calc(var(--chroma-bgc) var(--chroma-shift-op, +) var(--chroma-shift)) var(--colorway-hue))"
       :--active-bgc "oklch(calc(var(--lightness-bgc) var(--lightness-shift-op, -) (2 * var(--lightness-shift))) calc(var(--chroma-bgc) var(--chroma-shift-op, +) calc(2 * var(--chroma-shift))) var(--colorway-hue))"
       :--bgc        "oklch(var(--lightness-bgc) var(--chroma-bgc) var(--colorway-hue))"
       :--fgc        "oklch(var(--lightness-fgc) var(--chroma-fgc) var(--colorway-hue))" 
       ".inert"        {:bgc          :$bgc
                        :--hover-bgc  :$bgc
                        :--active-bgc :$bgc}})

    (defcss ".surface-transparent"
      {:--fgc "oklch(var(--lightness-fgc) var(--chroma-fgc) var(--colorway-hue))"})

    ;; Solids
    (defcss ".surface-solid, .surface-solid-classic"
      {:--lightness-bgc      :54%
       :--chroma-bgc         :55%
       :--chroma-shift       :6.25%
       :--lightness-shift    :6%
       :--classic-trim-color "oklch(62% 42.5% var(--colorway-hue))" 
       :bgc                  :$bgc
       :hover:bgc            :$hover-bgc
       :active:bgc           :$active-bgc
       :color                :white
       ".colorway-brown"     {:--chroma-bgc :20%}
       :dark                 {:--lightness-bgc      :69%
                              :--chroma-bgc         :52%
                              :color                :black
                              :--chroma-shift-op    "-"
                              :--lightness-shift-op "+"
                              :--classic-trim-color "oklch(75% 48.5% var(--colorway-hue))" 
                              ".colorway-brown"     {:--chroma-bgc :20%}}})

    (defcss ".surface-solid-classic.colorway-neutral, .surface-solid-classic.colorway-gray"
      {:--classic-trim-color "oklch(62% 0% var(--colorway-hue))"})

    (defcss ".surface-soft, .surface-soft-classic, .surface-faint, .surface-convex, .surface-minimal, .surface-transparent"
      {:--chroma-shift    :6.25% 
       :--lightness-shift :4% 
       :color             :$fgc
       :hover:bgc         :$hover-bgc
       :active:bgc        :$active-bgc
       ".colorway-brown"    {:--chroma-shift :2% }
       ".foreground-color-secondary" {:color :$foreground-color-secondary
                                      :dark  :$foreground-color-secondary-dark-mode}
       :dark              {:--chroma-shift-op    "-"
                           :--lightness-shift-op "+"
                           :hover:bgc            :$hover-bgc
                           :active:bgc           :$active-bgc}}) 


    ;; Softs
    (defcss ".surface-soft, .surface-soft-classic" 
      {:--lightness-bgc      :94.25%
       :--chroma-bgc         :15.75%
       :--lightness-fgc      :30%
       :--chroma-fgc         :32.5%
       :--classic-trim-color "oklch(91% 10% var(--colorway-hue))" 
      ;;  :color                :$fgc
       :bgc                  :$bgc
       ".colorway-brown"       {:--chroma-bgc :6%}
       :dark                 {:--lightness-bgc      :39%
                              :--chroma-bgc         :35.6%
                              :--lightness-fgc      :95.7%
                              :--chroma-fgc         :8.8%
                              :--classic-trim-color "oklch(36% 30% var(--colorway-hue))" 
                              ".colorway-brown"       {:--chroma-bgc :16%}
                              }})
    ;; Convex, dark
     (defcss ".surface-convex"
       {:dark {:--lightness-bgc :39%
               :--chroma-bgc    :33%
               :--lightness-fgc :95.7%
               :--chroma-fgc    :8.8%
               ".colorway-brown"  {:--chroma-bgc    :17%
                                   :--lightness-bgc :39%}
              ;;  :color           :$fgc
               :bgc             :$bgc
               :hover:bgc       :$hover-bgc
               :active:bgc      :$active-bgc
               }})

    ;; Neutral
    (defcss ".surface-soft-classic.colorway-neutral, .surface-soft-classic.colorway-gray"
      {:--classic-trim-color "oklch(91% 0% var(--colorway-hue))"})

    ;; Faint
    (defcss ".surface-faint, .surface-convex" 
      {:--lightness-bgc :98%
       :--lightness-fgc :44%
       :--chroma-bgc    :6.25%
       :--chroma-fgc    :46.25%
      ;;  :color           :$fgc
       :bgc             :$bgc
       ".colorway-brown"  {:--chroma-bgc    :3%
                           :--lightness-bgc :97%}
       ":dark"            {:--lightness-bgc :26%
                           :--chroma-bgc    :26%
                           :--lightness-fgc :92%
                           :--chroma-fgc    :25%
                           ".colorway-brown"  {:--chroma-bgc    :14%
                                               :--lightness-bgc :28%}}})

    ;; Convex, dark
    (defcss ".surface-convex" 
      {:dark {:--lightness-bgc :37%
              :--chroma-bgc    :34.6%
              :--lightness-fgc :95.7%
              :--chroma-fgc    :8.8%
              ".colorway-brown"  {:--chroma-bgc :14%
                                  :--lightness-bgc :39%}}})

    ;; Transparent
    (defcss ".surface-transparent, .surface-minimal" 
      {:--chroma-fgc    :46.25%
       :--lightness-fgc :44%
       :dark            {:--lightness-fgc :88%
                         :--chroma-fgc    :44%}})

    ;; Minimal, dark
    (defcss ".surface-minimal" 
      {:dark {:--lightness-bgc :26%
              :--chroma-bgc    :26%
              ".colorway-brown"  {:--chroma-bgc    :14%
                                  :--lightness-bgc :28%}}})

    ;; Minimal
    (defcss ".surface-minimal, .surface-minimal-light-mode, .surface-convex-light-mode" 
      {:--lightness-bgc :100% 
       :--lightness-fgc :44%
       :--chroma-bgc    :0%
       :color           :$fgc
       :bgc             :$background-color ; <- body background color (change name globablly?)
       })

    (defcss ".surface-minimal" 
      {:dark:bgc :$background-color-dark-mode ; <- body background color (change name globablly?)
       })

    (defcss ".surface-transparent"
      {:bgc        :transparent
       :bgc:hover  :transparent
       :bgc:active :transparent
       :dark {:bgc        :transparent
              :bgc:hover  :transparent
              :bgc:active :transparent}}) 

    ;; Classic details
    (defcss ".surface-solid-classic, .surface-soft-classic"
      {:box-shadow "inset 0 0 0 1px var(--transparent-black-10), inset 0 -2px 1px var(--transparent-black-20), inset 0 0 0 1px var(--classic-trim-color), inset 0 4px 2px -2px var(--top-rim-highlight-color, var(--transparent-white-80)), inset 0 2px 1px -1px var(--transparent-white-80)"
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
       :dark:bgi         "linear-gradient(to bottom,#0000 50%,var(--transparent-black-09)),linear-gradient(to bottom,#0000 50%, var(--classic-trim-color-dark) 80%)"})

    (defcss ".surface-solid-classic"
      {:text-shadow "0 0px 2px var(--transparent-black-30)"
       :dark        {:--top-rim-highlight-color :$transparent-white-50
                     :text-shadow               "0 0.5px 0px var(--transparent-white-40)"
                     :after:bgi                 "linear-gradient(var(--transparent-black-15), #0000, var(--transparent-white-20))"}})
    
    ;; Convex
    (defcss ".surface-convex, .surface-convex-light-mode"
      {:--convex-shadow-strength      "20%"
       :dark:--convex-shadow-strength "30%"
       :--convex-shadow-lightness-bgc "calc(var(--lightness-bgc) - (var(--convex-shadow-strength) / 3))"
       :--convex-shadow-chroma        "calc(var(--chroma-bgc) + (var(--convex-shadow-strength) / 6))"
       :--convex-shadow-chroma-hover  "calc(var(--convex-shadow-chroma) var(--chroma-shift-op, +) calc(2 * var(--chroma-shift)))"
       :--convex-shadow-chroma-active "calc(var(--convex-shadow-chroma) var(--chroma-shift-op, +) calc(3 * var(--chroma-shift)))"
       ".colorway-neutral"              {:--convex-shadow-chroma        "var(--chroma-bgc)"
                                         :--convex-shadow-chroma-hover  "var(--chroma-bgc)"
                                         :--convex-shadow-chroma-active "var(--chroma-bgc)"}
       :background-image              "linear-gradient(180deg, transparent, transparent 15%, oklch(var(--convex-shadow-lightness-bgc) calc(var(--convex-shadow-chroma) + calc(1 * var(--chroma-shift))) var(--colorway-hue)))"
       :hover:background-image        "linear-gradient(180deg, transparent, transparent 15%, oklch(calc(var(--convex-shadow-lightness-bgc) var(--chroma-shift-op, -) 4%) var(--convex-shadow-chroma-hover) var(--colorway-hue)))"
       :active:background-image       "linear-gradient(180deg, transparent, transparent 15%, oklch(calc(var(--convex-shadow-lightness-bgc) var(--chroma-shift-op, -) 8%) var(--convex-shadow-chroma-active) var(--colorway-hue)))"
       :dark                          {:--chroma-shift-op "+"}
       })

    
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


;; Shadows experimentation cruft ------------------------------------------------------

;; TODO - selector should be
;; (defcss {:data-ks-surface      ""
;;          :data-ks-shadow  ""
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
      (list 'defcss "[class*=\"surface-\"][class*=\"shadow-\"]")))
 

;; legacy-version
;; (defcss "[class*=\"surface-\"][class*=\"shadow-\"].shadow-color-red" 
;;   {
;;    :--shadow-color-red-h-s-l         "10 100 50" ; <- this would be in :root
;;    :--shadow-color-h-s-l             :$shadow-color-red-h-s-l})

;; super legacy-version, no support for shadow-strength
;; (defcss "[class*=\"surface-\"][class*=\"shadow-\"].shadow-color-red" 
;;   {
;;    :--shadow-color-red-hex         "#f908244d" ; <- this would be in :root
;;    :--shadow-color-hex             :$shadow-color-red-hex})


;; modern version
#_(defcss "[class*=\"surface-\"][class*=\"shadow-\"]" 
  {
   :--transparent-shadow-color "color-mix(in oklch, var(--shadow-color, black) var(--shadow-strength, 20%), transparent)"
  ;;  :--transparent-shadow-color-1 "color-mix(in oklch, var(--shadow-color, black) var(--shadow-strength, 2%), transparent)"
  ;;  :--transparent-shadow-color-2 "color-mix(in oklch, var(--shadow-color, black) var(--shadow-strength, 12%), transparent)"
   })

;; legacy version
;; (defcss "[class*=\"surface-\"][class*=\"shadow-\"]" 
;;   {:--shadow-color-h-s-l     "0 0 0"
;;    :--shadow-strength      "20%"
;;    ;; TODO - figure out whether the slash "/" syntax works in older browsers
;;    :--transparent-shadow-color "hsl(var(--shadow-color-h-s-l) / var(--shadow-strength))"})

;; super-legacy version
;; (defcss "[class*=\"surface-\"][class*=\"shadow-\"]" 
;;   {;; TODO - figure out whether the slash "/" syntax works in older browsers
;;    :--transparent-shadow-color "var(--shadow-color-hex)"})


;; (defcss "[class*=\"surface-\"][class*=\"shadow-\"]" 
;;   {:box-shadow "var(--box-shadow-for-stroke, 0 0 0 transparent), var(--shadow, 0 0 0 transparent)"})

