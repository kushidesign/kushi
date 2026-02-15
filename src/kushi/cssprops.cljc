(ns kushi.cssprops
  (:require [clojure.string :as string]
            [clojure.set :as set]
            [fireworks.core :refer [? !? ?> !?>]])
  #?(:cljs
     (:require-macros [kushi.util])))

;; cherries are props picked out for importance based on frequency of usage
;; hands are marking props for short-handing
(def by-alphabetical-index
 (apply
  array-map
  '["A"
    [
     accent-color
     align-content
     align-items 🍒
     align-self
     alignment-baseline
     all 🍒 🤘
     anchor-name
     animation-composition
     animation-delay 🍒
     animation-direction
     animation-duration 🍒
     animation-fill-mode
     animation-iteration-count
     animation-name
     animation-play-state
     animation-range-end
     animation-range-start
     animation-range 🍒 🤘
     animation-timeline
     animation-timing-function
     animation 🍒 🤘
     appearance
     aspect-ratio 🍒
     ]
    "B"
    [
     backdrop-filter
     backface-visibility
     background-attachment
     background-blend-mode
     background-clip
     background-color 🍒
     background-image 🍒
     background-origin
     background-position-x
     background-position-y
     background-position 🍒
     background-repeat 🍒
     background-size 🍒
     background 🍒 🤘
     block-size]

    "Border-*"
    [
     border-block-color
     border-block-end-color
     border-block-end-style
     border-block-end-width
     border-block-end 🍒 🤘
     border-block-start-color
     border-block-start-style
     border-block-start-width
     border-block-start 🍒 🤘
     border-block-style
     border-block-width
     border-block 🍒 🤘
     border-bottom-color
     border-bottom-left-radius
     border-bottom-right-radius
     border-bottom-style
     border-bottom-width
     border-bottom 🍒 🤘
     border-collapse
     border-color 🍒 🤘
     border-end-end-radius
     border-end-start-radius
     border-image-outset
     border-image-repeat
     border-image-slice
     border-image-source
     border-image-width
     border-image 🍒 🤘
     border-inline-color
     border-inline-end-color
     border-inline-end-style
     border-inline-end-width
     border-inline-end 🍒 🤘
     border-inline-start-color
     border-inline-start-style
     border-inline-start-width
     border-inline-start 🍒 🤘
     border-inline-style
     border-inline-width
     border-inline 🍒 🤘
     border-left-color
     border-left-style
     border-left-width
     border-left 🍒 🤘
     border-radius 🍒 🤘
     border-right-color
     border-right-style
     border-right-width
     border-right 🍒 🤘
     border-spacing
     border-start-end-radius
     border-start-start-radius
     border-style 🍒 🤘
     border-top-color
     border-top-left-radius
     border-top-right-radius
     border-top-style
     border-top-width
     border-top 🍒 🤘
     border-width 🍒 🤘
     border 🍒 🤘
     ]

    "B - C"
    [
     bottom 🍒
     box-align
     box-decoration-break
     box-direction
     box-flex-group
     box-flex
     box-lines
     box-ordinal-group
     box-orient
     box-pack
     box-shadow 🍒
     box-sizing 🍒
     break-after
     break-before
     break-inside
     caption-side
     caret-color
     clear 🍒
     clip-path
     clip-rule
     clip
     color-interpolation-filters
     color-interpolation
     color-scheme
     color 🍒
     column-count
     column-fill
     column-gap 🍒
     column-rule-color
     column-rule-style
     column-rule-width
     column-rule 🍒 🤘
     column-span
     column-width
     columns 🍒 🤘
     contain-intrinsic-block-size
     contain-intrinsic-height
     contain-intrinsic-inline-size
     contain-intrinsic-size 🍒 🤘
     contain-intrinsic-width
     contain
     container-name
     container-type
     container 🍒 🤘
     content-visibility
     content 🍒
     counter-increment
     counter-reset
     counter-set
     cursor 🍒
     cx 🍒
     cy  🍒
     ]
    "D - F"
    [
     d 🍒
     direction 🍒
     display 🍒
     dominant-baseline
     empty-cells
     field-sizing
     fill-opacity
     fill-rule
     fill 🍒
     filter 🍒
     flex-basis 🍒
     flex-direction 🍒
     flex-flow 🍒 🤘
     flex-grow 🍒
     flex-shrink 🍒
     flex-wrap 🍒
     flex 🍒 🤘
     float 🍒 
     flood-color
     flood-opacity
     font-family 🍒
     font-feature-settings
     font-kerning
     font-language-override
     font-optical-sizing
     font-palette
     font-size-adjust
     font-size 🍒
     font-smooth 🍒
     font-stretch 🍒
     font-style 🍒
     font-synthesis-position
     font-synthesis-small-caps
     font-synthesis-style
     font-synthesis-weight
     font-synthesis
     font-variant-alternates
     font-variant-caps
     font-variant-east-asian
     font-variant-emoji
     font-variant-ligatures
     font-variant-numeric
     font-variant-position
     font-variant 🍒 🤘
     font-variation-settings 🍒
     font-weight 🍒
     font 🍒 🤘
     forced-color-adjust]

    "G - I"
    [gap 🍒 🤘
     grid-area 🍒 🤘
     grid-auto-columns
     grid-auto-flow
     grid-auto-rows
     grid-column-end
     grid-column-start
     grid-column 🍒 🤘
     grid-row-end 🍒
     grid-row-start 🍒
     grid-row 🍒 🤘
     grid-template-areas 🍒
     grid-template-columns 🍒
     grid-template-rows 🍒
     grid-template 🍒 🤘
     grid 🍒 🤘
     hanging-punctuation
     height 🍒 
     hyphenate-character
     hyphenate-limit-chars
     hyphens
     image-orientation
     image-rendering
     image-resolution
     initial-letter
     inline-size
     inset-block-end 🍒
     inset-block-start 🍒
     inset-block 🍒 🤘
     inset-block 🍒 🤘
     inset-inline-end 🍒
     inset-inline-start 🍒
     inset-inline 🍒 🤘
     inset-inline 🍒 🤘
     inset 🍒 🤘
     interpolate-size
     isolation]
    "J - M"
    [
     justify-content 🍒
     justify-items 🍒
     justify-self 🍒
     left 🍒
     letter-spacing 🍒
     lighting-color
     line-break
     line-clamp
     line-height-step
     line-height 🍒
     list-style-image 🍒
     list-style-position 🍒
     list-style-type 🍒
     list-style 🍒 🤘
     margin-block-end 🍒
     margin-block-start 🍒
     margin-block 🍒 🤘
     margin-bottom 🍒
     margin-inline-end 🍒
     margin-inline-start 🍒
     margin-inline 🍒 🤘
     margin-left 🍒
     margin-right 🍒
     margin-top 🍒
     margin-trim 🍒
     margin 🍒 🤘
     marker-end
     marker-mid
     marker-start
     marker
     mask-border-mode
     mask-border-outset
     mask-border-repeat
     mask-border-slice
     mask-border-source
     mask-border-width
     mask-border 🍒 🤘
     mask-clip 🍒
     mask-composite 🍒
     mask-image 🍒
     mask-mode 🍒
     mask-origin 🍒
     mask-position 🍒
     mask-repeat 🍒
     mask-size 🍒
     mask-type 🍒
     mask 🍒 🤘
     math-depth
     math-shift
     math-style
     max-block-size
     max-height 🍒
     max-inline-size
     max-width 🍒
     min-block-size
     min-height 🍒
     min-inline-size
     min-width 🍒
     mix-blend-mode]

    "O - P"
    [
     object-fit 🍒
     object-position 🍒
     offset-anchor
     offset-distance
     offset-path
     offset-position
     offset-rotate
     offset 🍒 🤘
     opacity 🍒
     order 🍒
     orphans
     outline-color 🍒
     outline-offset 🍒
     outline-style 🍒
     outline-width 🍒
     outline 🍒 🤘
     overflow-anchor
     overflow-block 🍒 🤘
     overflow-clip-margin
     overflow-inline
     overflow-wrap
     overflow-x 🍒
     overflow-y 🍒
     overflow 🍒 🤘
     overlay
     overscroll-behavior-block
     overscroll-behavior-inline
     overscroll-behavior-x
     overscroll-behavior-y
     overscroll-behavior 🍒 🤘
     padding-block-end 🍒
     padding-block-start 🍒
     padding-block 🍒 🤘
     padding-bottom 🍒
     padding-inline-end 🍒
     padding-inline-start 🍒
     padding-inline 🍒 🤘
     padding-left 🍒
     padding-right 🍒
     padding-top 🍒
     padding 🍒 🤘
     page-break-after
     page-break-before
     page-break-inside
     page
     paint-order
     perspective-origin
     perspective
     place-content 🍒 🤘
     place-items 🍒 🤘
     place-self 🍒 🤘
     pointer-events
     position-anchor
     position-area
     position-try-fallbacks
     position-try-order
     position-try 🍒 🤘
     position-visibility
     position 🍒
     print-color-adjust]
    "Q - S"
    [quotes
     r 🍒
     reading-flow
     reading-order
     resize
     right 🍒
     rotate 🍒
     row-gap 🍒
     ruby-align
     ruby-position
     rx 🍒
     ry 🍒
     scale 🍒
     scroll-behavior 🍒
     scroll-margin-block-end
     scroll-margin-block-start
     scroll-margin-block 🍒 🤘
     scroll-margin-bottom
     scroll-margin-inline-end
     scroll-margin-inline-start
     scroll-margin-inline 🍒 🤘
     scroll-margin-left
     scroll-margin-right
     scroll-margin-top
     scroll-margin 🍒 🤘
     scroll-marker-group
     scroll-padding-block-end
     scroll-padding-block-start
     scroll-padding-block 🍒 🤘
     scroll-padding-bottom
     scroll-padding-inline-end
     scroll-padding-inline-start
     scroll-padding-inline 🍒 🤘
     scroll-padding-left
     scroll-padding-right
     scroll-padding-top
     scroll-padding 🍒 🤘
     scroll-snap-align
     scroll-snap-stop
     scroll-snap-type
     scroll-timeline-axis
     scroll-timeline-name
     scroll-timeline 🍒 🤘
     scrollbar-color 
     scrollbar-gutter
     scrollbar-width
     shape-image-threshold
     shape-margin
     shape-outside
     shape-rendering
     speak-as
     stop-color
     stop-opacity
     stroke-dasharray
     stroke-dashoffset
     stroke-linecap
     stroke-linejoin
     stroke-miterlimit
     stroke-opacity 🍒
     stroke-width 🍒
     stroke 🍒
     ]
    "T - Z"
    [tab-size
     table-layout
     text-align-last
     text-align 🍒
     text-anchor
     text-box-edge
     text-box-trim
     text-box 🍒 🤘
     text-combine-upright
     text-decoration-color 🍒
     text-decoration-line 🍒
     text-decoration-skip-ink
     text-decoration-skip
     text-decoration-style 🍒
     text-decoration-thickness 🍒
     text-decoration 🍒 🤘
     text-emphasis-color
     text-emphasis-position
     text-emphasis-style
     text-emphasis 🍒 🤘
     text-indent
     text-justify
     text-orientation
     text-overflow
     text-rendering
     text-shadow 🍒
     text-size-adjust
     text-spacing-trim
     text-transform 🍒
     text-underline-offset 🍒
     text-underline-position 🍒
     text-wrap-mode
     text-wrap-style
     text-wrap 🍒 🤘
     timeline-scope
     top 🍒
     touch-action
     transform-box
     transform-origin 🍒
     transform-style 🍒
     transform 🍒
     transition-behavior 🍒
     transition-delay 🍒
     transition-duration 🍒
     transition-property 🍒
     transition-timing-function 🍒
     transition 🍒 🤘
     translate 🍒
     unicode-bidi
     user-modify
     user-select
     vector-effect
     vertical-align 🍒
     view-timeline-axis
     view-timeline-inset
     view-timeline-name
     view-timeline 🍒 🤘
     view-transition-class
     view-transition-name
     visibility 🍒
     white-space-collapse
     white-space 🍒
     widows
     width 🍒
     will-change
     word-break
     word-spacing
     writing-mode
     x 🍒
     y 🍒
     z-index 🍒
     zoom 🍒
                         ]

    "Non-standard, vendor-prefixed properties"
    [-moz-float-edge
     -moz-force-broken-image-icon
     -moz-orient
     -moz-user-focus
     -moz-user-input
     -webkit-box-reflect
     -webkit-border-before 🍒 🤘
     -webkit-mask-box-image 🍒 🤘
     -webkit-mask-composite
     -webkit-mask-position-x
     -webkit-mask-position-y
     -webkit-mask-repeat-x
     -webkit-mask-repeat-y
     -webkit-tap-highlight-color
     -webkit-text-fill-color
     -webkit-text-security
     -webkit-text-stroke 🍒 🤘
     -webkit-text-stroke-color
     -webkit-text-stroke-width
     -webkit-touch-callout]
    ]))

(def with-badges
   (->> by-alphabetical-index
        vals
        (apply concat)
        (mapv keyword))) 

 (def all 
   (reduce
    (fn [acc v] 
      (if (and (not= v :🤘) (not= v :🍒))
        (conj acc v)
        acc))
    []
    with-badges))

(def all-set
   (into #{} all))

(def cherries
  (->> with-badges
       (keep-indexed 
        (fn [idx x]
          (when (= (nth with-badges (+ 1 idx) nil) :🍒) x)))
       (into [])))

(def shorthands
  (->> with-badges
       (keep-indexed 
        (fn [idx x]
          (when (= (nth with-badges (+ 2 idx) nil) :🤘) x)))
       (into [])))

(def shorthands-set
   (into #{} shorthands))

(def cherries-set
   (into #{} cherries))

(def non-cherries-set
  (set/difference all-set cherries-set))
