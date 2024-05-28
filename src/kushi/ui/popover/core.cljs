(ns kushi.ui.popover.core
  (:require
   [fireworks.core :refer [? !?]]
   [applied-science.js-interop :as j]
   [goog.string]
   [domo.core :as domo]
   [kushi.core :refer (keyed)]
   [kushi.ui.util :as util :refer [maybe]]
   [kushi.ui.dom.pane.core :as pane]
   [kushi.ui.dom.pane.placement :refer [user-placement]]
   ;; Import this styles ns to create defclasses
   [kushi.ui.dom.pane.styles]))

(defn popover-attrs
  {:desc ["Popovers provide additional context when hovering or clicking on an"
          "element. They can be interactive and are typically dismissed"
          "manually by the user."
          :br
          :br
          "By default, popovers will show up above the owning element. "
          "Specifying placement in various ways can be done with the"
          "`:-placement` option. See the tooltip docs for details on "
          "`:-placement`."
          :br
          :br
          "The element owning the popover must receive an attributes map that "
          "is a result of passing a map of options to "
          "`kushi.ui.popover.core/popover-attrs`. You can compose this map to "
          "an existing element's attributes map with `kushi.core/merge-attrs` "
          "using the pattern:"
          :br
          :br "`(merge-attrs (sx ...) (popover-attrs {...}))`"
          :br
          :br
          "You are responsible for providing your own rendering function, which "
          "takes as a single argument the dom node of the generated popover "
          "into which you can render whatever you like."
          :br
          :br
          "You can use the `kushi.ui.popover.core/dismiss-popover!` function if "
          "you want to close the popover from an action within the popover."
          "If you are using a close button that is potitioned near the edge "
          "of the popover, it is recommended to give it a `z-index` of `1` or "
          "higher so that it does not get clipped by the arrow element. See "
          "the example above."
          :br
          :br
          "Elements and behaviors of the popover containers can be custom "
          "styled and controlled via the following tokens in your theme:"
          :br
          ;; TODO add documentation for each token
          :br 
          "__Colors and images:__"
              "`:$popover-background-color`"                 
          :br "`:$popover-background-color-inverse`"         
          :br "`:$popover-background-image`"                 
          :br "`:$popover-box-shadow`"                 
          :br "`:$popover-border-width`"                 
          :br "`:$popover-border-style`"                 
          :br "`:$popover-border-color`"                 
          :br
          :br "__Geometry:__"
              "`:$popover-min-width`"
          :br "`:$popover-min-height`"
          :br "`:$popover-border-radius`"
          :br "`:$popover-offset`"
          :br "`:$popover-viewport-padding`"
          :br "`:$popover-flip-viewport-edge-threshold`"
          :br "`:$popover-auto-placement-y-threshold`"
          :br
          :br "__Choreography:__"
              "`:$popover-offset-start`"
          :br "`:$popover-z-index`"             
          :br "`:$popover-delay-duration`"            
          :br "`:$popover-initial-scale`"             
          :br "`:$popover-offset-start`"              
          :br "`:$popover-transition-duration`"       
          :br "`:$popover-transition-timing-function`"
          :br "`:$popover-auto-dismiss-duration`"
          :br
          :br "__Arrows:__"
              "`:$popover-arrow-inline-inset`"
          :br "`:$popover-arrow-block-inset`"
          :br "`:$popover-arrow-depth`"   

          :br
          :br
          "If you want supply the value of any of the above tokens ala-carte, "
          "use the following pattern."
          :br
          :br
          "`(merge-attrs (sx :$popover-offset--5px ...) (popover-attrs {...}))`"
          :br
          :br
          "If you would like to use a value of `0` (`px`, `ems`, `rem`, etc.) for "
          "`$popover-offset`, `$popover-arrow-inline-inset`, "
          "`$popover-arrow-block-inset`, or `$popover-border-radius`, you will need "
          "to use an explicit unit e.g. `0px`."
          ]
   :opts '[{:name    f
            :pred    fn?
            :default nil
            :desc    ["A component rendering function which takes a single "
                      "argument, (the popover container dom node), and renders "
                      "content into it."
                      :br
                      :br
                      "The example above uses reagent, but you could do "
                      "something similar with another rendering library:"
                      :br
                      "`(fn [el] (rdom/render [my-popover-content] el))`"]}
           {:name    placement
            :pred    keyword?
            :default :auto
            :desc    ["You can use single keywords to specify the exact placement "
                      "of the popover:"
                      :br
                      "`:top-left-corner`"
                      :br
                      "`:top-left`"
                      :br
                      "`:top`"
                      :br
                      "`:top-right`"
                      :br
                      "`:top-right-corner`"
                      :br
                      "`:right-top-corner`"
                      :br
                      "`:right-top`"
                      :br
                      "`:right`"
                      :br
                      "`:right-bottom`"
                      :br
                      "`:right-bottom-corner`"
                      :br
                      :br
                      "You can also use shorthand versions of the single keywords:"
                      :br
                      "`:tlc`"
                      :br
                      "`:tl`"
                      :br
                      "`:t`"
                      :br
                      "`:tr`"
                      :br
                      "`:trc`"
                      :br
                      "`:rtc`"
                      :br
                      "`:rt`"
                      :br
                      "`:r`"
                      :br
                      "`:rb`"
                      :br
                      "`:rbc`"
                      :br
                      :br
                      "If you care about the popover placement respecting writing "
                      "direction and/or document flow, you can use a vector of of "
                      "up to 3 logical properties keywords, separated by spaces:"
                      :br
                      "`[:inline-end :block-start]`"
                      :br
                      "`[:inline-end :block-start :corner]`"
                      :br
                      "`[:inline-start :center]`"
                      :br
                      "`[:inline-end :center]`"
                      :br
                      "`[:block-start :enter]`"
                      :br
                      "`[:block-end :center]`"
                      :br
                      "`[:block-end :inline-start]`"
                      :br]}
           {:name    arrow?
            :pred    boolean?
            :default true
            :desc    ["Setting to false will not render a directional arrow with "
                     "the popover."]}
           {:name    auto-dismiss?
            :pred    boolean?
            :default false
            :desc    ["Setting to true will auto-dismiss the popover. "
                      "The time of display before display is controlled "
                      "by the theme token `:$popover-auto-dismiss-duration`"]}
           #_{:name    use-on-click?
            :pred    boolean?
            :default false
            :desc    ["By default, the popover is triggered on the mousedown event. "
                      "Setting to true will instead use the click event to trigger "
                      "the popover. "]}
           ]}

  [{placement                   :-placement
    arrow?                      :-arrow?
    auto-dismiss?               :-auto-dismiss?
    use-on-click?               :-use-on-click?
    user-rendering-fn           :-f
    :or                         {placement     :auto
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
          ;; !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
          ;; TODO add `popover-class` here, similar to tooltip
          ;; !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
          opts         (keyed placement-kw
                              arrow?
                              auto-dismiss?
                              pane-type
                              user-rendering-fn)]
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
