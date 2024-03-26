(ns kushi.ui.popover.core
  (:require
   [fireworks.core :refer [? ?? ??? !? ?> !?> ]]
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
  {:desc ["popovers provide additional context when hovering or clicking on an"
          "element. They can be interactive and are typically dismissed"
          "manually by the user."
          :br
          :br
          "By default, popovers will show up above the owning element. "
          "Specifying placement in various ways can be done with the"
          "`:-placement` option."
          :br
          :br
          "The element being tipped must receive an attributes map that is a "
          "result of passing a map of options to "
          "`kushi.ui.popover.core/popover-attrs`. You can compose this map to "
          "an existing elements attributes map using the pattern:"
          :br
          :br "`(merge-attrs (sx ...) (popover-attrs {...}))`"
          :br
          :br
          "popovers can be custom styled and controlled via the following "
          "tokens in your theme:"
          :br
          ;; TODO add documentation for each token
          :br "Colors and images:"
          :br "`:$popover-color`"                            
          :br "`:$popover-color-inverse`"                    
          :br "`:$popover-background-color`"                 
          :br "`:$popover-background-color-inverse`"         
          :br "`:$popover-background-image`"                 
          :br
          :br "Typography:"
          :br "`:$popover-line-height`"
          :br "`:$popover-font-family`"
          :br "`:$popover-font-size`"
          :br "`:$popover-font-weight`"
          :br "`:$popover-text-transform`"
          :br
          :br "Geometry:"
          :br "`:$popover-padding-inline`"
          :br "`:$popover-padding-block`"
          :br "`:$popover-border-radius`"
          :br "`:$popover-offset`"
          :br "`:$popover-viewport-padding`"
          :br "`:$popover-flip-viewport-edge-threshold`"
          :br "`:$popover-auto-placement-y-threshold`"
          :br
          :br "Choreography:"
          :br "`:$popover-delay-duration`"            
          :br "`:$popover-reveal-on-click-duration`"  
          :br "`:$popover-initial-scale`"             
          :br "`:$popover-offset-start`"              
          :br "`:$popover-transition-duration`"       
          :br "`:$popover-transition-timing-function`"
          :br
          :br "Arrows:"
          :br "`:$popover-arrow-depth-min-px`"
          :br "`:$popover-arrow-depth-max-px`"
          :br "`:$popover-arrow-depth-ems`"
          :br "`:$popover-arrow-depth`"   
          :br "`:$popover-arrow-x-offset`"
          :br "`:$popover-arrow-y-offset`"

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
          "`$popover-offset`, `$popover-arrow-x-offset`, "
          "`$popover-arrow-y-offset`, or `$popover-border-radius`, you will need "
          "to use an explicit unit e.g. `0px`."
          ]
   :opts '[{:name    text
            :pred    #(or (string? %) (keyword? %) (vector? %))
            :default nil
            :desc    "Required. The text to display in the popover"}
           {:name    placement
            :pred    keyword?
            :default :auto
            :desc    [
                      "You can use single keywords to specify the exact placement "
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
           {:name    popover-class
            :pred    string?
            :default nil
            :desc    ["A class name for a la carte application of a classes on the "
                      " popover element."]}]}
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

(defn remove-popover! [e]
  (let [el         (domo/et e)
        popover-el (domo/nearest-ancestor el ".kushi-popover")
        fune-id    (j/get popover-el :id)
        owning-el  (domo/qs (str "[aria-controls=\"" fune-id "\"]"))]
    (fune/remove-fune! owning-el fune-id :popover e)))
