(ns kushi.ui.popover.core
  (:require
   [applied-science.js-interop :as j]
   [goog.string]
   [domo.core :as domo]
   [kushi.ui.util :as util :refer [maybe]]
   [kushi.ui.dom.pane.core :as pane]
   [kushi.ui.dom.pane.placement :refer [user-placement]]
   ;; Import this styles ns to create defclasses
   [kushi.ui.dom.pane.styles]
   [kushi.core :refer [register-design-tokens-by-category]]))

(register-design-tokens-by-category
 "elevation"
 "popover"
 "pane")
   

(defn popover-attrs
  {:summary "Popovers provide additional context when clicking on an element."
   :desc "They can be interactive and are typically dismissed\\
          manually by the user.
           
          By default, popovers will show up above the owning element. \\
          Specifying placement in various ways can be done with the\\
          `:-placement` option. See the tooltip docs for details on \\
          `:-placement`.
           
          The element owning the popover must receive an attributes map that \\
          is a result of passing a map of options to \\
          `kushi.ui.popover.core/popover-attrs`. You can compose this map to \\
          an existing element's attributes map with `kushi.core/merge-attrs` \\
          using the pattern:
           
          `(merge-attrs (sx ...) (popover-attrs {...}))`
           
          You are responsible for providing your own rendering function, which \\
          takes as a single argument the dom node of the generated popover \\
          into which you can render whatever you like.
           
          You can use the `kushi.ui.popover.core/dismiss-popover!` function if \\
          you want to close the popover from an action within the popover.\\
          If you are using a close button that is potitioned near the edge \\
          of the popover, it is recommended to give it a `z-index` of `1` or \\
          higher so that it does not get clipped by the arrow element. See \\
          the example above.
           
           
          Elements and behaviors of the popover containers can be custom \\
          styled and controlled via the following tokens in your theme:\\
           
            
          __Colors and images:__
          `--popover-background-color`                 
          `--popover-background-color-dark-mode`         
          `--popover-background-image`                 
          `--popover-box-shadow`                 
          `--popover-border-width`                 
          `--popover-border-style`                 
          `--popover-border-color`                 
          __Geometry:__
          `--popover-min-width`
          `--popover-min-height`
          `--popover-border-radius`
          `--popover-offset`
          `--popover-viewport-padding`
          `--popover-flip-viewport-edge-threshold`
          `--popover-auto-placement-y-threshold`
          __Choreography:__
          `--popover-offset-start`
          `--popover-z-index`             
          `--popover-delay-duration`            
          `--popover-initial-scale`             
          `--popover-offset-start`              
          `--popover-transition-duration`       
          `--popover-transition-timing-function`
          `--popover-auto-dismiss-duration`
          __Arrows:__
          `--popover-arrow-inline-inset`
          `--popover-arrow-block-inset`
          `--popover-arrow-depth`   

          If you want supply the value of any of the above tokens ala-carte, 
          use the following pattern.

          `(popover-attrs {:-popover-class (css [:--popover-background-color :beige])}))`

          If you would like to use a value of `0` (`px`, `ems`, `rem`, etc.) for \\
          `--popover-offset`, `--popover-arrow-inline-inset`, \\
          `--popover-arrow-block-inset`, or `--popover-border-radius`, you will need \\
          to use an explicit unit e.g. `0px`."
          
   :opts '[{:name    f
            :pred    fn?
            :default nil
            :desc    "A component rendering function which takes a single\\ 
                      argument, (the popover container dom node), and renders\\
                      content into it.

                      The example above uses reagent, but you could do\\
                      something similar with another rendering library:

                      `(fn [el] (rdom/render [my-popover-content] el))`"}
           {:name    placement
            :pred    keyword?
            :default :auto
            :desc    "You can use single keywords to specify the exact placement 
                      of the popover:
                       
                      `:top-left-corner`
                      `:top-left`
                      `:top`
                      `:top-right`
                      `:top-right-corner`
                      `:right-top-corner`
                      `:right-top`
                      `:right`
                      `:right-bottom`
                      `:right-bottom-corner`
                       
                      You can also use shorthand versions of the single keywords:
                       
                      `:tlc`
                      `:tl`
                      `:t`
                      `:tr`
                      `:trc`
                      `:rtc`
                      `:rt`
                      `:r`
                      `:rb`
                      `:rbc`
                       
                      If you care about the popover placement respecting writing\\
                      direction and/or document flow, you can use a vector of of\\ 
                      up to 3 logical properties keywords, separated by spaces:
                       
                      `[:inline-end :block-start]`
                      `[:inline-end :block-start :corner]`
                      `[:inline-start :center]`
                      `[:inline-end :center]`
                      `[:block-start :enter]`
                      `[:block-end :center]`
                      `[:block-end :inline-start]`"}
           {:name    arrow?
            :pred    boolean?
            :default true
            :desc    "Setting to false will not render a directional arrow with\\ 
                      the popover."}
           {:name    auto-dismiss?
            :pred    boolean?
            :default false
            :desc    "Setting to true will auto-dismiss the popover. The time of\\
                      display before display is controlled by the theme token\\
                      `--popover-auto-dismiss-duration`"}
           {:name    popover-class
            :pred    string?
            :default nil
            :desc    "A class name for a la carte application of classes on the\\ 
                      popover element."}
           #_{:name    use-on-click?
            :pred    boolean?
            :default false
            :desc    "By default, the popover is triggered on the mousedown event.\\ 
                      Setting to true will instead use the click event to trigger\\ 
                      the popover."}
           ]}

  [{user-pane-class    :class
    user-pane-style    :style
    placement          :-placement
    arrow?             :-arrow?
    auto-dismiss?      :-auto-dismiss?
    use-on-click?      :-use-on-click?
    popover-class      :-popover-class
    user-rendering-fn  :-f
    :or                {placement     :auto
                        arrow?        true
                        auto-dismiss? false
                        use-on-click? false}}]
  
  (when user-rendering-fn 
    (let [arrow?       (if (false? arrow?) false true)
          placement    (if-not (or (string? placement)
                                   (keyword? placement)
                                   (vector? placement))
                         :auto
                         placement)
          placement-kw (or (maybe placement #(= % :auto))
                           (user-placement placement))
          pane-type    :popover
          opts         {:placement-kw      placement-kw
                        :arrow?            arrow?
                        :auto-dismiss?     auto-dismiss?
                        :pane-type         pane-type
                        :user-rendering-fn user-rendering-fn
                        :user-pane-class   user-pane-class
                        :user-pane-style   user-pane-style
                        :popover-class     popover-class}]
      (merge 
       {:data-kushi-ui-pane (name placement-kw)}
       {:on-click (partial pane/append-pane! opts)}
       #_{(if use-on-click? :on-click :on-mouse-down)
        (partial pane/append-pane! opts)}))))

(defn dismiss-popover! [e]
  (let [el         (domo/et e)
        popover-el (domo/nearest-ancestor el ".kushi-popover")
        pane-id    (j/get popover-el :id)
        owning-el  (domo/qs (str "[aria-controls=\"" pane-id "\"]"))]
    (pane/remove-pane! owning-el pane-id :popover e)))
