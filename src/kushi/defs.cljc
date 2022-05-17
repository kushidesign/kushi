(ns kushi.defs)

;; TODO use :kushi/* ns kws ?
(def meta-ks
  [:kushi-class
   :kushi-class-prefix
   :kushi-class-prepend
   :kushi-selector
   :kushi-theme?
   :ui?
   :kushi/sheet])

;; These are sorted roughly be most-commonly used, for speeding up for regex (alternations)
(def pseudo-elements
  #{:after
    :before
    :selection
    :backdrop
    :placeholder
    :cue
    :cue-region
    :first-letter
    :first-line
    :grammar-error
    :marker
    :part
    :slotted
    :spelling-error
    :-moz-progress-bar
    :-moz-range-progress
    :-moz-range-thumb
    :-moz-range-track
    :-webkit-progress-bar
    :-webkit-progress-value
    :-webkit-slider-runnable-track
    :-webkit-slider-thumb})

; TODO - should this be defonce?
;; These are sorted roughly be most-commonly used, for speeding up for regex (alternations)
(def int-vals
  #{:opacity
    :font-weight
    :stroke-width
    :line-height
    :order
    :flex-grow
    :flex-shrink
    :z-index
    :grid-row
    :grid-row-start
    :grid-row-end
    :grid-column
    :grid-column-start
    :grid-column-end
    :columns
    :column-count
    :counter-increment
    :counter-reset
    :counter-set})

;: left and :right (used with @page rule) have been removed from this check to avoid clash with standard css props
;; These are sorted roughly be most-commonly used, for speeding up for regex (alternations)
(def pseudo-classes
   #{:hover
     :active
     :focus
     :not
     :first-child
     :last-child
     :nth-child
     :visited
     :checked
     :disabled
     :nth-of-type
     :nth-last-child
     :nth-last-of-type
     :first-of-type
     :last-of-type
     :only-child
     :required
     :default
     :empty
     :enabled
     :link
     :root
     :valid
     :target
     :first
     :focus-visible
     :focus-within
     :any-link
     :blank
     :current
     :dir
     :future
     :indeterminate
     :in-range
     :invalid
     :lang
     :local-link
     :is
     :only-of-type
     :optional
     :out-of-range
     :past
     :placeholder-shown
     :playing
     :paused
     :read-only
     :read-write
     :scope
     })
