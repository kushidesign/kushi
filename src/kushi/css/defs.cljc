(ns kushi.css.defs)

(def lvfha-pseudos-order
  '{&:link    0
    &:visited 2
    &:focus   3
    &:hover   4 
    &:active  5})


(def lvfha-pseudos 
  (->> lvfha-pseudos-order keys (into #{})))

(def lvfha-pseudos-order-strs
  '{"&:link"    0
    "&:visited" 2
    "&:focus"   3
    "&:hover"   4 
    "&:active"  5})

(def lvfha-pseudos-strs
  (->> lvfha-pseudos-order-strs keys (into #{})))

(def default-kushi-responsive
  [:xsm {:min-width :480px}
   :sm {:min-width :640px}
   :md {:min-width :768px}
   :lg {:min-width :1024px}
   :xl {:min-width :1280px}
   :xxl {:min-width :1536px}])

(def media (apply array-map default-kushi-responsive))

(def index-by-media-query
  (into {}
        (map-indexed (fn [i [k _]]
                       [k i])
                     media)))

(def pseudo-elements*
  [:after
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
   :part    ;;()
   :slotted ;;()
   :spelling-error
   :-moz-progress-bar
   :-moz-range-progress
   :-moz-range-thumb
   :-moz-range-track
   :-webkit-progress-bar
   :-webkit-progress-value
   :-webkit-slider-runnable-track
   :-webkit-slider-thumb])

(def functional-pseudo-elements*
  [:part    ;;()
   :slotted ;;()
   ])

(def pseudo-elements
  (into #{} pseudo-elements*))

;; TODO - should this be defonce?
;; These are sorted roughly be most-commonly used, for speeding up for regex (alternations)
(def int-vals*
  [:opacity
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
   :counter-set])

(def int-vals
  (into #{} int-vals*))

;: left and :right (used with @page rule) have been removed from this check to avoid clash with standard css props
;; These are sorted roughly be most-commonly used, for speeding up for regex (alternations)
(def pseudo-classes*
  [:hover
   :active
   :focus
   :not ;;()
   :first-child
   :last-child
   :nth-child ;;()
   :visited
   :checked
   :disabled
   :nth-of-type ;;()
   :nth-last-child ;;()
   :nth-last-of-type ;;()
   :has ;; ()
   :is ;; ()
   :where ;; ()
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
   :dir ;;()
   :host ;;()
   :indeterminate
   :in-range
   :invalid
   :lang ;;()
   :local-link
   :only-of-type
   :optional
   :out-of-range
   :current ;;()
   :past ;;()
   :future ;;()
   :placeholder-shown
   :playing
   :paused
   :read-only
   :read-write
   :scope])

(def functional-pseudo-classes* 
  [
   :not ;;()
   :nth-child ;;()
   :nth-of-type ;;()
   :nth-last-child ;;()
   :nth-last-of-type ;;()
   :has ;; ()
   :is ;; ()
   :where ;; ()
   :dir ;;()
   :host ;;()
   :lang ;;()
   :current ;;()
   :past ;;()
   :future ;;()
   ])

;; (def pseudo-classes
;;   (into #{} pseudo-classes*))

(def at-rules
  #{"keyframes"
    "font-face"
    "media"
    "charset"
    "container"
    "supports"
    "import"
    "layer"
    "page"
    "scope"
    "property"
    "color-profile"
    "counter-style"
    "font-feature-values"
    "font-palette-values"
    "namespace"
    "position-try"
    "starting-style"
    "view-transition"  })

