;; 1) Figure out stroke independant of shadow
;;    Just a stroke
;;    Just a shadow
;;    Stroke and shadow



;; 2) malli warning at comptime for sx2

;; 3) gap, spacing, padding, maybe use spacing numbers if number is supplied

;; 4) Figure out how to do custom props with new paradigm, for ui components

;; 5) Figure out list of elements that cannot take props and maybe add :not rules to the data-ks-* prop utility classes

(ns ^{:kushi/layer "kushi-ui-theming"} kushi.css.build.kushi-ui-component-theming
  (:require
   [kushi.colors]
   [cuerdas.core]
   [bling.hifi]
   [kushi.css.build.macros :refer [defcolorway defcss css-string]]))

(defcss "body"
  {"font-family"                "var(--sans-serif-font-stack)"
   "font-weight"                "var(--body-font-weight)"               
   "color"                      "var(--foreground-color)"
   "background-color"           "var(--background-color)"
   "transition-property"        "background-color, color"
   "transition-duration"        "var(--transition-fast)"
   "transition-timing-function" "var(--timing-linear-curve)"
   "overflow-y"                 "scroll"})

(defcss ".dark, body.dark, .dark body"
  {"background-color" "var(--background-color-dark-mode)",
   "color"            "var(--foreground-color-dark-mode)"})

(defcss "code"
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
   "border-radius"              "var(--code-border-radius, var(--rounded-sm-absolute))",
  ;;  "border-width"               "var(--code-border-width, 1px)",
  ;;  "border-color"               "var(--code-border-color, var(--neutral-200))",
  ;;  "border-style"               "var(--code-border-style, solid)",
   "color"                      "var(--code-color)"})

(defcss "pre>code"
  {"background-color" :transparent
   "border-width"     0})

(defcss ".dark code"
  {"background-color" "var(--code-background-color-dark-mode)",
   "color"            "var(--code-color-dark-mode)"
  ;;  "border-color"     "var(--code-border-color-dark-mode, var(--neutral-800))",
   })

(defcss ".dark pre>code"
  {"background-color" :transparent
   "border-width"     0})

(defcss ".styled-scrollbars"
  {"scrollbar-color" "var(--scrollbar-thumb-color) var(--scrollbar-background-color)",
   "scrollbar-width" "thin"})

(defcss ".dark .styled-scrollbars"
  {"scrollbar-color" "var(--scrollbar-thumb-color-dark-mode) var(--scrollbar-background-color-dark-mode)"})

(defcss ".styled-scrollbars::-webkit-scrollbar"
  {"width" "var(--scrollbar-width)", "height" "var(--scrollbar-width)"})

(defcss ".styled-scrollbars::-webkit-scrollbar-thumb"
  {"background" "var(--scrollbar-thumb-color)",
   "border-radius" "9999px",
   "border" "0px solid var(--scrollbar-background-color)"})

(defcss ".dark .styled-scrollbars::-webkit-scrollbar-thumb"
  {"background" "var(--scrollbar-thumb-color-dark-mode)",
   "border" "0px solid var(--scrollbar-background-color-dark-mode)"})

(defcss ".styled-scrollbars::-webkit-scrollbar-track"
  {"background" "var(--scrollbar-background-color)"})

(defcss ".dark .styled-scrollbars::-webkit-scrollbar-track"
  {"background" "var(--scrollbar-background-color-dark-mode)"})

(defcss "*:focus-visible"
  {"outline" "4px solid rgba(0, 125, 250, 0.6)",
   "outline-offset" "1px"})

(defcss "*:disabled"
  {"opacity" "45%!important", "cursor" "not-allowed!important"})

(defcss ".ks-radio-input:focus-visible"
  {"box-shadow" "0 0 0 4px rgba(0, 125, 250, 0.6)"})

(defcss ".ks-tag" 
  {"font-family" "var(--primary-font-family)"})

(defcss ".dark .ks-radio-input"
  {"background-color" "black"})

(defcss ".dark .ks-checkbox-input" 
  {"background-color" "black"})

(defcss ".dark .ks-checkbox-input:before"
  {"box-shadow" "inset 1em 1em black"})

(defcss ".dark .ks-slider-step-label.ks-slider-step-label-selected"
  {"color" "white"})

(defcss ".dark .ks-slider-step-label"
  {"color" "var(--gray-300)"})


;; New theming

;; TODO - should these live in design-tokens?
(defcss ":root"
  {"--background-grid-size"                           "107px",
   "--background-grid-color"                          "#81818122",
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

   "--stroked-element-stroke-opacity"           "30%"
   "--stroked-element-stroke-opacity-dark-mode" "30%"

   ;; TODO - add these scales for legacy browser support of colored shadows
   ;; legacy
   ;; :--shadow-color-red-h-s-l         "10 100 50" ; <- this would be in :root
   
   ;; super legacy, no support for shadow strength
   ;; :--shadow-color-red-hex         "#f908244d" ; <- this would be in :root
   
   ;; Convex
   :--convex-shadow-opacity                        "25%"

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
;; (defcolorway "sand")


;; Move to button ns?
(defcss ".ks-button"
  {"--padding-inline"           :$button-padding-inline
   "--padding-block"            :$button-padding-block
   "[data-ks-packing=xcompact]" {:--padding-inline :$button-padding-inline-xcompact
                                 :--padding-block  :$button-padding-block-xcompact}
   "[data-ks-packing=compact]"  {:--padding-inline :$button-padding-inline-compact
                                 :--padding-block  :$button-padding-block-compact}
   "[data-ks-packing=roomy]"    {:--padding-inline :$button-padding-inline-roomy
                                 :--padding-block  :$button-padding-block-roomy}
   "[data-ks-packing=xroomy]"   {:--padding-inline :$button-padding-inline-xroomy
                                 :--padding-block  :$button-padding-block-xroomy}
   ".ks-icon-button"            {:--padding-inline            :$padding-block
                                 :_.material-symbols-outlined {:min-width   :1.2ch
                                                               :line-height :normal}}})

(defcss ".ks-icon-button"
  {"--padding-block"              :$icon-button-padding-block||$button-padding-block
   "--padding-inline"             :$icon-button-padding-inline||$button-padding-inline
   :_.material-symbols-outlined {:min-width   :1.2ch
                                 :line-height :normal}
   "[data-ks-packing=xcompact]"   {:--padding-inline :$icon-button-padding-block-compact||$button-padding-block-xcompact
                                   :--padding-block  :$icon-button-padding-block-compact||$button-padding-block-xcompact}
   "[data-ks-packing=compact]"    {:--padding-inline :$icon-button-padding-block-compact||$button-padding-block-compact
                                   :--padding-block  :$icon-button-padding-block-compact||$button-padding-block-compact}
   "[data-ks-packing=roomy]"      {:--padding-inline :$icon-button-padding-block-roomy||$button-padding-block-roomy
                                   :--padding-block  :$icon-button-padding-block-roomy||$button-padding-block-roomy}
   "[data-ks-packing=xroomy]"     {:--padding-inline :$icon-button-padding-block-roomy||$button-padding-block-xroomy
                                   :--padding-block  :$icon-button-padding-block-roomy||$button-padding-block-xroomy}})


;; Move to tag ns
(defcss ".ks-tag"
  {"[data-ks-packing=xcompact]" {:--padding-inline :$tag-padding-inline-xcompact
                                 :--padding-block  :$tag-padding-block-xcompact}
   "[data-ks-packing=compact]"  {:--padding-inline :$tag-padding-inline-compact
                                 :--padding-block  :$tag-padding-block-compact}
   "[data-ks-packing=roomy]"    {:--padding-inline :$tag-padding-inline-roomy
                                 :--padding-block  :$tag-padding-block-roomy}
   "[data-ks-packing=xroomy]"   {:--padding-inline :$tag-padding-inline-xroomy
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
   :--switch-off-background-color-hover-dark-mode :$background-color-neutral-soft2-dark-mode})



;; Colorway hues   ---------------------------------------------------------------------------------------------------------
;; TODO - decouple semantics

(defcss "[data-ks-colorway=blue]"     {:--colorway-hue :$blue-hue-oklch})
(defcss "[data-ks-colorway=sky]"      {:--colorway-hue :$sky-hue-oklch})
(defcss "[data-ks-colorway=azure]"    {:--colorway-hue :$azure-hue-oklch})
(defcss "[data-ks-colorway=aqua]"     {:--colorway-hue :$aqua-hue-oklch})
(defcss "[data-ks-colorway=teal]"     {:--colorway-hue :$teal-hue-oklch})
(defcss "[data-ks-colorway=cyan]"     {:--colorway-hue :$cyan-hue-oklch})
(defcss "[data-ks-colorway=mint]"     {:--colorway-hue :$mint-hue-oklch})
(defcss "[data-ks-colorway=emerald]"  {:--colorway-hue :$emerald-hue-oklch})
(defcss "[data-ks-colorway=jade]"     {:--colorway-hue :$jade-hue-oklch})
(defcss "[data-ks-colorway=green]"    {:--colorway-hue :$green-hue-oklch})
(defcss "[data-ks-colorway=matcha]"   {:--colorway-hue :$matcha-hue-oklch})
(defcss "[data-ks-colorway=lime]"     {:--colorway-hue :$lime-hue-oklch})
(defcss "[data-ks-colorway=acid]"     {:--colorway-hue :$acid-hue-oklch})
(defcss "[data-ks-colorway=citron]"   {:--colorway-hue :$citron-hue-oklch})
(defcss "[data-ks-colorway=yellow]"   {:--colorway-hue :$yellow-hue-oklch})
(defcss "[data-ks-colorway=banana]"   {:--colorway-hue :$banana-hue-oklch})
(defcss "[data-ks-colorway=gold]"     {:--colorway-hue :$gold-hue-oklch})
(defcss "[data-ks-colorway=apricot]"  {:--colorway-hue :$apricot-hue-oklch})
(defcss "[data-ks-colorway=amber]"    {:--colorway-hue :$amber-hue-oklch})
(defcss "[data-ks-colorway=orange]"   {:--colorway-hue :$orange-hue-oklch})
(defcss "[data-ks-colorway=coral]"    {:--colorway-hue :$coral-hue-oklch})
(defcss "[data-ks-colorway=red]"      {:--colorway-hue :$red-hue-oklch})
(defcss "[data-ks-colorway=ruby]"     {:--colorway-hue :$ruby-hue-oklch})
(defcss "[data-ks-colorway=rose]"     {:--colorway-hue :$rose-hue-oklch})
(defcss "[data-ks-colorway=crimson]"  {:--colorway-hue :$crimson-hue-oklch})
(defcss "[data-ks-colorway=pink]"     {:--colorway-hue :$pink-hue-oklch})
(defcss "[data-ks-colorway=magenta]"  {:--colorway-hue :$magenta-hue-oklch})
(defcss "[data-ks-colorway=plum]"     {:--colorway-hue :$plum-hue-oklch})
(defcss "[data-ks-colorway=purple]"   {:--colorway-hue :$purple-hue-oklch})
(defcss "[data-ks-colorway=violet]"   {:--colorway-hue :$violet-hue-oklch})
(defcss "[data-ks-colorway=indigo]"   {:--colorway-hue :$indigo-hue-oklch})
(defcss "[data-ks-colorway=lapis]"    {:--colorway-hue :$lapis-hue-oklch})

(defcss "[data-ks-colorway=gray]"     {:--colorway-hue :$gray-hue-oklch})
(defcss "[data-ks-colorway=sand]"     {:--colorway-hue :$sand-hue-oklch})
(defcss "[data-ks-colorway=slate]"    {:--colorway-hue :$slate-hue-oklch})
(defcss "[data-ks-colorway=neutral]"  {:--colorway-hue :$gray-hue-oklch})
(defcss "[data-ks-colorway=warning]"  {:--colorway-hue "var(--yellow-hue-oklch)"})
(defcss "[data-ks-colorway=accent]"   {:--colorway-hue "var(--blue-hue-oklch)"})
(defcss "[data-ks-colorway=negative]" {:--colorway-hue "var(--red-hue-oklch)"})
(defcss "[data-ks-colorway=positive]" {:--colorway-hue "var(--green-hue-oklch)"})


(css-string "kushi-ui-theming"
            (:data-ks-surface-str 
             {:surface       "soft"
              :lightness-fgc "30%"
              :lightness-bgc "94.25%"
              :chroma-fgc    "32.5%"
              :chroma-bgc    "15.75%"
              :chroma-fgc-muted "12.5%"
              :chroma-bgc-muted "2424242"
              :lightness-bgc-dark-mode "39%"
              :chroma-bgc-dark-mode "35.6%"
              :lightness-fgc-dark-mode "95.7%"
              :chroma-fgc-dark-mode "8.8%"}))


(css-string "kushi-ui-theming"
"[data-ks-surface2],
[data-ks-surface2]&[data-ks-interactive]:hover, 
[data-ks-surface2]&[data-ks-interactive]:active {
  &[data-ks-colorway2=gray], &[data-ks-colorway2=neutral], &:not([data-ks-colorway2]) {
    --chroma-shift:   0%;
    --colorway-hue:   var(--neutral-hue-oklch);
    --chroma-bgc:     0%;
    --chroma-fgc:     0%;
  }
  &[data-ks-colorway2=sand], &[data-ks-colorway2=slate] {
    --chroma-shift: 1%;
    .dark & {
      --chroma-shift: 1%;
    }
  }
}"
)

;; Surface styles  ---------------------------------------------------------------------------------------------------------

;; neutralize chroma shift
(defcss "[data-ks-surface][data-ks-colorway=neutral], [data-ks-surface][data-ks-colorway=gray]" 
  {:--chroma-bgc   :0%
   :--chroma-fgc   :0%
   :--chroma-shift :0%
   :dark           {:--chroma-bgc   :0%
                    :--chroma-fgc   :0%
                    :--chroma-shift :0%}})

(defcss "[data-ks-surface=solid], [data-ks-surface=solid-classic], [data-ks-surface=soft], [data-ks-surface=soft-classic], [data-ks-surface=faint], [data-ks-surface=convex], [data-ks-surface=minimal], [data-ks-surface=transparent], [data-ks-surface=minimal-light-mode], [data-ks-surface=convex-light-mode]"
  {:--hover-bgc    "oklch(calc(var(--lightness-bgc) var(--lightness-shift-op, -) var(--lightness-shift)) calc(var(--chroma-bgc) var(--chroma-shift-op, +) var(--chroma-shift)) var(--colorway-hue))"
   :--active-bgc   "oklch(calc(var(--lightness-bgc) var(--lightness-shift-op, -) (2 * var(--lightness-shift))) calc(var(--chroma-bgc) var(--chroma-shift-op, +) calc(2 * var(--chroma-shift))) var(--colorway-hue))"
   :--bgc          "oklch(var(--lightness-bgc) var(--chroma-bgc) var(--colorway-hue))"
   :--fgc          "oklch(var(--lightness-fgc) var(--chroma-fgc) var(--colorway-hue))" 
   "[data-ks-inert]" {:bgc          :$bgc
                      :--hover-bgc  :$bgc
                      :--active-bgc :$bgc}})

(defcss "[data-ks-surface=transparent]"
  {:--fgc "oklch(var(--lightness-fgc) var(--chroma-fgc) var(--colorway-hue))"})

;; Solids
(defcss "[data-ks-surface=solid], [data-ks-surface=solid-classic]"
  {:--lightness-bgc           :54%
   :--chroma-bgc              :55%
   :--chroma-shift            :6.25%
   :--lightness-shift         :6%
   :--classic-trim-color      "oklch(62% 42.5% var(--colorway-hue))" 
   :bgc                       :$bgc
   :hover:bgc                 :$hover-bgc
   :active:bgc                :$active-bgc
   :color                     :white
   "[data-ks-colorway=sand]" {:--chroma-bgc :20%}
   :dark                      {:--lightness-bgc           :69%
                               :--chroma-bgc              :52%
                               :color                     :black
                               :--chroma-shift-op         "-"
                               :--lightness-shift-op      "+"
                               :--classic-trim-color      "oklch(75% 48.5% var(--colorway-hue))" 
                               "[data-ks-colorway=sand]" {:--chroma-bgc :20%}}})

(defcss "[data-ks-surface=solid-classic][data-ks-colorway=neutral], [data-ks-surface=solid-classic][data-ks-colorway=gray]"
  {:--classic-trim-color "oklch(62% 0% var(--colorway-hue))"})

(defcss "[data-ks-surface=soft], [data-ks-surface=soft-classic], [data-ks-surface=faint], [data-ks-surface=convex], [data-ks-surface=minimal], [data-ks-surface=transparent]"
  {
   :--chroma-shift                :6.25% 
   :--lightness-shift             :4% 
   :color                         :$fgc
   :hover:bgc                     :$hover-bgc
   :active:bgc                    :$active-bgc
   "[data-ks-colorway=sand]"     {:--chroma-shift :2% }
   "[data-ks-colorway=secondary]" {:color :$foreground-color-secondary
                                       :dark  :$foreground-color-secondary-dark-mode}
  ;;  :dark                       {:--chroma-shift-op    "-"
  ;;                               :--lightness-shift-op "+"
  ;;                               :hover:bgc            :$hover-bgc
  ;;                               :active:bgc           :$active-bgc}
   
   }) 

;; Softs
(defcss "[data-ks-surface=soft], [data-ks-surface=soft-classic]" 
  {:--lightness-bgc           :94.25%
   :--chroma-bgc              :15.75%
   :--lightness-fgc           :30%
   :--chroma-fgc              :32.5%
   :--classic-trim-color      "oklch(91% 10% var(--colorway-hue))" 
      ;;  :color                :$fgc
   :bgc                       :$bgc
   "[data-ks-colorway=sand]" {:--chroma-bgc :6%}
   :dark                      {:--lightness-bgc           :39%
                               :--chroma-bgc              :35.6%
                               :--lightness-fgc           :95.7%
                               :--chroma-fgc              :8.8%
                               :--classic-trim-color      "oklch(36% 30% var(--colorway-hue))" 
                               "[data-ks-colorway=sand]" {:--chroma-bgc :16%}
                               }})

;; Convex, dark
 (defcss "[data-ks-surface=convex]"
   {:dark {:--lightness-bgc           :39%
           :--chroma-bgc              :33%
           :--lightness-fgc           :95.7%
           :--chroma-fgc              :8.8%
           "[data-ks-colorway=sand]" {:--chroma-bgc    :17%
                                           :--lightness-bgc :39%}
          ;;  :color           :$fgc
           :bgc                       :$bgc
           :hover:bgc                 :$hover-bgc
           :active:bgc                :$active-bgc
           }})

;; Neutral
(defcss "[data-ks-surface=soft-classic][data-ks-colorway=neutral], [data-ks-surface=soft-classic][data-ks-colorway=gray]"
  {:--classic-trim-color "oklch(91% 0% var(--colorway-hue))"})

;; Faint
(defcss "[data-ks-surface=faint], [data-ks-surface=convex]" 
  {
   :--lightness-bgc :98%
   :--lightness-fgc :44%
   :--chroma-bgc    :6.25%
   :--chroma-fgc    :46.25%
   ;;  :color           :$fgc
   :bgc             :$bgc
   "[data-ks-colorway=sand]"  {:--chroma-bgc    :3%
                                    :--lightness-bgc :97%}
   ":dark"            {:--lightness-bgc           :26%
                       :--chroma-bgc              :26%
                       :--lightness-fgc           :92%
                       :--chroma-fgc              :25%
                       "[data-ks-colorway=sand]" {:--chroma-bgc    :14%
                                                       :--lightness-bgc :28%}}})

;; Convex, dark
(defcss "[data-ks-surface=convex]" 
  {:dark {:--lightness-bgc :37%
          :--chroma-bgc    :34.6%
          :--lightness-fgc :95.7%
          :--chroma-fgc    :8.8%
          "[data-ks-colorway=sand]"  {:--chroma-bgc :14%
                              :--lightness-bgc :39%}}})

;; Transparent
(defcss "[data-ks-surface=transparent], [data-ks-surface=minimal]" 
  {:--chroma-fgc    :46.25%
   :--lightness-fgc :44%
   :dark            {:--lightness-fgc :88%
                     :--chroma-fgc    :44%}})

;; Minimal, dark
(defcss "[data-ks-surface=minimal]" 
  {:dark {:--lightness-bgc :26%
          :--chroma-bgc    :26%
          "[data-ks-colorway=sand]"  {:--chroma-bgc    :14%
                              :--lightness-bgc :28%}}})

;; Minimal
(defcss "[data-ks-surface=minimal], [data-ks-surface=minimal-light-mode], [data-ks-surface=convex-light-mode]" 
  {:--lightness-bgc :100% 
   :--lightness-fgc :44%
   :--chroma-bgc    :0%
   :color           :$fgc
   :bgc             :$background-color ; <- body background color (change name globablly?)
   })

(defcss "[data-ks-surface=minimal]" 
  {:dark:bgc :$background-color-dark-mode ; <- body background color (change name globablly?)
   })

(defcss "[data-ks-surface=transparent]"
  {:bgc        :transparent
   :hover:bgc  :transparent
   :active:bgc :transparent
   :dark       {:bgc        :transparent
                :hover:bgc  :transparent
                :active:bgc :transparent}})

;; Classic details
(defcss "[data-ks-surface=solid-classic], [data-ks-surface=soft-classic]"
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

(defcss "[data-ks-surface=soft-classic]"
  {:dark:text-shadow "0 0px 2px var(--transparent-black-40), 0 -0.5px 0px var(--transparent-black-70)"
   :text-shadow      "0 0.5px 0px var(--transparent-white-100)"
   :box-shadow       "inset 0 0 0 1px var(--transparent-black-05), inset 0 -2px 1px var(--transparent-black-10), inset 0 0 0 1px var(--classic-trim-color), inset 0 4px 2px -2px var(--transparent-white-100), inset 0 2px 1px -1px var(--transparent-white-100)"
   :dark:box-shadow  "inset 0 0 0 1px var(--transparent-black-05), inset 0 -2px 1px var(--transparent-black-10), inset 0 0 0 1px var(--classic-trim-color-dark), inset 0 4px 2px -2px var(--transparent-white-60), inset 0 2px 1px -1px var(--transparent-white-60)"
   :bgi              "linear-gradient(to bottom,#0000 50%,var(--transparent-black-09)),linear-gradient(to bottom,#0000 50%, var(--classic-trim-color) 80%)"
   :dark:bgi         "linear-gradient(to bottom,#0000 50%,var(--transparent-black-09)),linear-gradient(to bottom,#0000 50%, var(--classic-trim-color-dark) 80%)"})

(defcss "[data-ks-surface=solid-classic]"
  {:text-shadow "0 0px 2px var(--transparent-black-30)"
   :dark        {:--top-rim-highlight-color :$transparent-white-50
                 :text-shadow               "0 0.5px 0px var(--transparent-white-40)"
                 :after:bgi                 "linear-gradient(var(--transparent-black-15), #0000, var(--transparent-white-20))"}})

;; Convex
(defcss "[data-ks-surface=convex], [data-ks-surface=convex-light-mode]"
  {:--convex-shadow-opacity      "20%"
   :dark:--convex-shadow-opacity "30%"
   :--convex-shadow-lightness-bgc "calc(var(--lightness-bgc) - (var(--convex-shadow-opacity) / 3))"
   :--convex-shadow-chroma        "calc(var(--chroma-bgc) + (var(--convex-shadow-opacity) / 6))"
   :--convex-shadow-chroma-hover  "calc(var(--convex-shadow-chroma) var(--chroma-shift-op, +) calc(2 * var(--chroma-shift)))"
   :--convex-shadow-chroma-active "calc(var(--convex-shadow-chroma) var(--chroma-shift-op, +) calc(3 * var(--chroma-shift)))"
   "[data-ks-colorway=neutral]"   {:--convex-shadow-chroma        "var(--chroma-bgc)"
                                       :--convex-shadow-chroma-hover  "var(--chroma-bgc)"
                                       :--convex-shadow-chroma-active "var(--chroma-bgc)"}
   :background-image              "linear-gradient(180deg, transparent, transparent 15%, oklch(var(--convex-shadow-lightness-bgc) calc(var(--convex-shadow-chroma) + calc(1 * var(--chroma-shift))) var(--colorway-hue)))"
   :hover:background-image        "linear-gradient(180deg, transparent, transparent 15%, oklch(calc(var(--convex-shadow-lightness-bgc) var(--chroma-shift-op, -) 4%) var(--convex-shadow-chroma-hover) var(--colorway-hue)))"
   :active:background-image       "linear-gradient(180deg, transparent, transparent 15%, oklch(calc(var(--convex-shadow-lightness-bgc) var(--chroma-shift-op, -) 8%) var(--convex-shadow-chroma-active) var(--colorway-hue)))"
   :dark                          {:--chroma-shift-op "+"}})


; Surface stroking ---------------------------------------------------------------------------------------------------------
(defcss "[data-ks-surface][data-ks-stroke]"
  {"[data-ks-stroke-align=outside]" {"--stroke-inset" ""}
   :--box-shadow-for-stroke         "var(--stroke-inset, inset) 0 0 0 var(--stroke-width, var(--element-stroke-width, 1px)) var(--transparent-stroke-color)" 
   :--transparent-stroke-color      "color-mix(in oklch, var(--stroke-color, currentColor) var(--stroke-opacity, 60%), var(--stroke-opacity-mix-color, transparent))"
   :dark                            {:--transparent-stroke-color "color-mix(in oklch, var(--stroke-color-dark-mode, currentColor) var(--stroke-opacity, 60%), var(--stroke-opacity-mix-color, transparent))"}

  ;; maybe use this in the future?
  ;;  :box-shadow                      "var(--box-shadow-for-stroke, 0 0 0 transparent), var(--shadow, 0 0 0 transparent)"
   })


;; Surface shadowing ---------------------------------------------------------------------------------------------------------
(defcss "[data-ks-surface][data-ks-shadow-size]" 
  {
   :--transparent-shadow-color "color-mix(in oklch, var(--shadow-color, black) var(--shadow-opacity, 15%), transparent)"
  ;;  :--shadow-3xs               "0 1px 3px -1px var(--transparent-shadow-color)"
  ;;  :--shadow-2xs               "0 3px 6px -2px var(--transparent-shadow-color)"
  ;;  :--shadow-xs                "0 5px 12px -4px var(--transparent-shadow-color), 0 2px 6px -4px var(--transparent-shadow-color)"
  ;;  :--shadow-sm                "0 7px 13px -3px var(--transparent-shadow-color), 0 2px 3px -3px var(--transparent-shadow-color)"
  ;;  :--shadow-md                "0 11px 21px -6px var(--transparent-shadow-color), 0 3px 7px -5px var(--transparent-shadow-color)"
  ;;  :--shadow-lg                "0 16px 26px -8px var(--transparent-shadow-color), 0 4px 10px -8px var(--transparent-shadow-color)"
  ;;  :--shadow-xl                "0 22px 36px -12px var(--transparent-shadow-color), 0 8px 10px -9px var(--transparent-shadow-color)"
  ;;  :--shadow-2xl               "0 25px 52px -11px var(--transparent-shadow-color), 0 9px 10px -10px var(--transparent-shadow-color)"
  ;;  :--shadow-3xl               "0 27px 60px -14px var(--transparent-shadow-color), 0 10px 10px -12px var(--transparent-shadow-color)"
   :--box-shadow-for-shadow    "0 0 0 0 transparent"
   ;;  :--box-shadow-for-stroke        "0 0 0 0 transparent"
   :--shadow-color             :black
   :dark                       {:--transparent-shadow-color "color-mix(in oklch, var(--shadow-color-dark-mode, white) var(--shadow-opacity, 15%), transparent)"}

   "[data-ks-shadow-size=3xs]"   {:--box-shadow-for-shadow :$shadow-3xs
                                  :--shadow-3xs            "0 1px 3px -1px var(--transparent-shadow-color)"}
   "[data-ks-shadow-size=2xs]"   {:--box-shadow-for-shadow :$shadow-2xs
                                  :--shadow-2xs            "0 3px 6px -2px var(--transparent-shadow-color)"}
   "[data-ks-shadow-size=xs]"    {:--box-shadow-for-shadow :$shadow-xs
                                  :--shadow-xs             "0 5px 12px -4px var(--transparent-shadow-color), 0 2px 6px -4px var(--transparent-shadow-color)"}
   "[data-ks-shadow-size=sm]"    {:--box-shadow-for-shadow :$shadow-sm
                                  :--shadow-md             "0 11px 21px -6px var(--transparent-shadow-color), 0 3px 7px -5px var(--transparent-shadow-color)"}
   "[data-ks-shadow-size=md]"    {:--box-shadow-for-shadow :$shadow-md
                                  :--shadow-md             "0 11px 21px -6px var(--transparent-shadow-color), 0 3px 7px -5px var(--transparent-shadow-color)"}
   "[data-ks-shadow-size=lg]"    {:--box-shadow-for-shadow :$shadow-lg
                                  :--shadow-lg             "0 16px 26px -8px var(--transparent-shadow-color), 0 4px 10px -8px var(--transparent-shadow-color)"}
   "[data-ks-shadow-size=xl]"    {:--box-shadow-for-shadow :$shadow-xl
                                  :--shadow-xl             "0 22px 36px -12px var(--transparent-shadow-color), 0 8px 10px -9px var(--transparent-shadow-color)"}
   "[data-ks-shadow-size=2xl]"   {:--box-shadow-for-shadow :$shadow-2xl
                                  :--shadow-2xl            "0 25px 52px -11px var(--transparent-shadow-color), 0 9px 10px -10px var(--transparent-shadow-color)"}
   "[data-ks-shadow-size=3xl]"   {:--box-shadow-for-shadow :$shadow-3xl
                                  :--shadow-3xl               "0 27px 60px -14px var(--transparent-shadow-color), 0 10px 10px -12px var(--transparent-shadow-color)"}

   ;;  "[data-ks-shadow-color=positive]" {:--shadow-color :$positive-500},
   ;;  "[data-ks-shadow-color=neutral]"  {:--shadow-color :$neutral-500},
   ;;  "[data-ks-shadow-color=negative]" {:--shadow-color :$negative-500},
   ;;  "[data-ks-shadow-color=purple]"   {:--shadow-color :$purple-500},
   ;;  "[data-ks-shadow-color=magenta]"  {:--shadow-color :$magenta-500},
   ;;  "[data-ks-shadow-color=accent]"   {:--shadow-color :$accent-500},
   ;;  "[data-ks-shadow-color=sand]"     {:--shadow-color :$sand-500},
   ;;  "[data-ks-shadow-color=slate]"    {:--shadow-color :$slate-500},
   ;;  "[data-ks-shadow-color=blue]"     {:--shadow-color :$blue-500},
   ;;  "[data-ks-shadow-color=orange]"   {:--shadow-color :$orange-500},
   ;;  "[data-ks-shadow-color=gray]"     {:--shadow-color :$gray-500},
   ;;  "[data-ks-shadow-color=warning]"  {:--shadow-color :$warning-500},
   ;;  "[data-ks-shadow-color=green]"    {:--shadow-color :$green-500},
   ;;  "[data-ks-shadow-color=gold]"     {:--shadow-color :$gold-500},
   ;;  "[data-ks-shadow-color=lime]"     {:--shadow-color :$lime-500},
   ;;  "[data-ks-shadow-color=yellow]"   {:--shadow-color :$yellow-500},
   ;;  "[data-ks-shadow-color=red]"      {:--shadow-color :$red-500}
   })


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
      (list 'defcss "[data-ks-surface][data-ks-shadow]")))
 

;; legacy-version
;; (defcss "[data-ks-surface][data-ks-shadow][data-ks-shadow-color=red]" 
;;   {
;;    :--shadow-color-red-h-s-l         "10 100 50" ; <- this would be in :root
;;    :--shadow-color-h-s-l             :$shadow-color-red-h-s-l})

;; super legacy-version, no support for shadow-opacity
;; (defcss "[data-ks-surface][data-ks-shadow][data-ks-shadow-color=red]" 
;;   {
;;    :--shadow-color-red-hex         "#f908244d" ; <- this would be in :root
;;    :--shadow-color-hex             :$shadow-color-red-hex})


;; modern version
#_(defcss "[data-ks-surface][data-ks-shadow]" 
  {
   :--transparent-shadow-color "color-mix(in oklch, var(--shadow-color, black) var(--shadow-opacity, 20%), transparent)"
  ;;  :--transparent-shadow-color-1 "color-mix(in oklch, var(--shadow-color, black) var(--shadow-opacity, 2%), transparent)"
  ;;  :--transparent-shadow-color-2 "color-mix(in oklch, var(--shadow-color, black) var(--shadow-opacity, 12%), transparent)"
   })

;; legacy version
;; (defcss "[data-ks-surface][data-ks-shadow]" 
;;   {:--shadow-color-h-s-l     "0 0 0"
;;    :--shadow-opacity      "20%"
;;    ;; TODO - figure out whether the slash "/" syntax works in older browsers
;;    :--transparent-shadow-color "hsl(var(--shadow-color-h-s-l) / var(--shadow-opacity))"})

;; super-legacy version
;; (defcss "[data-ks-surface][data-ks-shadow]" 
;;   {;; TODO - figure out whether the slash "/" syntax works in older browsers
;;    :--transparent-shadow-color "var(--shadow-color-hex)"})


;; (defcss "[data-ks-surface][data-ks-shadow]" 
;;   {:box-shadow "var(--box-shadow-for-stroke, 0 0 0 transparent), var(--shadow, 0 0 0 transparent)"})

   