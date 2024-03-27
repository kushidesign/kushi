(ns kushi.ui.popover.core
  (:require
   [applied-science.js-interop :as j]
   [clojure.string :as string]
   [goog.string]
   [domo.core :as domo]
   [kushi.core :refer (keyed token->ms)]
   [kushi.ui.util :as util :refer [maybe]]
   [kushi.ui.dom.fune.core :as fune]
   [kushi.ui.dom.fune.placement :refer [user-placement]]
   ;; Import this styles ns to create defclasses
   [kushi.ui.dom.fune.styles]
   [reagent.dom :as rdom]
   ))



(defn valid-popover-text-coll? [x]
  (and (seq x) 
       (every? #(or (and (string? %)
                         (not (string/blank? %)))
                    (keyword? %)
                    (number? %)
                    (symbol? %))
               x)))

(defn valid-popover-text [text]
  (cond (string? text)
        (when-not (string/blank? text)
          text)
        (coll? text)
        (when (valid-popover-text-coll? text)
          (into [] text))
        (array? text)
        (let [v (js->clj text)]
          (when (valid-popover-text-coll? v)
            v))))


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
          "You can use the `kushi.ui.popover.core/close-popover!` function if "
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
          :br "Colors and images:"
          :br "`:$popover-background-color`"                 
          :br "`:$popover-background-color-inverse`"         
          :br "`:$popover-background-image`"                 
          :br "`:$popover-box-shadow`"                 
          :br "`:$popover-border-width`"                 
          :br "`:$popover-border-style`"                 
          :br "`:$popover-border-color`"                 
          :br
          :br "Geometry:"
          :br "`:$popover-min-width`"
          :br "`:$popover-min-height`"
          :br "`:$popover-border-radius`"
          :br "`:$popover-offset`"
          :br "`:$popover-viewport-padding`"
          :br "`:$popover-flip-viewport-edge-threshold`"
          :br "`:$popover-auto-placement-y-threshold`"
          :br
          :br "Choreography:"
          :br "`:$popover-offset-start`"
          :br "`:$popover-z-index`"             
          :br "`:$popover-delay-duration`"            
          :br "`:$popover-initial-scale`"             
          :br "`:$popover-offset-start`"              
          :br "`:$popover-transition-duration`"       
          :br "`:$popover-transition-timing-function`"
          :br
          :br "Arrows:"
          :br "`:$popover-arrow-inline-inset`"
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
          "If you would like to use a value of 0 (`px`, `ems`, `rem`, etc.) for "
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
                     "the popover."]}]}

  [{placement                   :-placement
    arrow?                      :-arrow?
    user-rendering-fn           :-f
    :or                         {placement :auto
                                 arrow?    true}}]
  
  (when user-rendering-fn 
    (let [arrow?       (if (false? arrow?) false true)
          placement    (if-not (or (string? placement)
                                   (keyword? placement)
                                   (vector? placement))
                         :auto
                         placement)
          placement-kw (or (maybe placement #(= % :auto))
                           (user-placement placement))
          fune-type    :popover
          opts         (keyed placement-kw
                              arrow?
                              fune-type
                              user-rendering-fn)]
      (merge 
       {:data-kushi-ui-fune (name placement-kw)
        :on-click           (partial fune/append-fune! opts)}))))

(defn close-popover! [e]
  (let [el         (domo/et e)
        popover-el (domo/nearest-ancestor el ".kushi-popover")
        fune-id    (j/get popover-el :id)
        owning-el  (domo/qs (str "[aria-controls=\"" fune-id "\"]"))]
    (fune/remove-fune! owning-el fune-id :popover e)))
