(ns kushi.ui.tooltip.core
  (:require
   [applied-science.js-interop :as j]
   [clojure.string :as string]
   [goog.string]
   [domo.core :as domo]
   [kushi.core :refer (keyed token->ms)]
   [kushi.ui.util :as util :refer [maybe]]
   [kushi.ui.dom.pane.core :as pane]
   [kushi.ui.dom.pane.placement :refer [user-placement]]
   ;; Import this styles ns to create defclasses
   [kushi.ui.dom.pane.styles]))


(defn valid-tooltip-text-coll? [x]
  (and (seq x) 
       (every? #(or (and (string? %)
                         (not (string/blank? %)))
                    (keyword? %)
                    (number? %)
                    (symbol? %))
               x)))

(defn valid-tooltip-text [text]
  (cond (string? text)
        (when-not (string/blank? text)
          text)
        (coll? text)
        (when (valid-tooltip-text-coll? text)
          (into [] text))
        (array? text)
        (let [v (js->clj text)]
          (when (valid-tooltip-text-coll? v)
            v))))


(defn tooltip-attrs
  {:desc ["tooltips provide additional context when hovering or clicking on an"
          "element. They are intended to be ephemeral, containing only"
          "non-interactive content."
          :br
          :br
          "By default, tooltips will show up above the owning element. "
          "Specifying placement in various ways can be done with the"
          "`:-placement` option."
          :br
          :br
          "The element being tipped must receive an attributes map that is a "
          "result of passing a map of options to "
          "`kushi.ui.tooltip.core/tooltip-attrs`. You can compose this map to "
          "an existing elements attributes map using the pattern:"
          :br
          :br "`(merge-attrs (sx ...) (tooltip-attrs {...}))`"
          :br
          :br
          "tooltips can be custom styled and controlled via the following "
          "tokens in your theme:"
          :br
          ;; TODO add documentation for each token
          :br "__Colors and images:__"
              "`:$tooltip-color`"                            
          :br "`:$tooltip-color-inverse`"                    
          :br "`:$tooltip-background-color`"                 
          :br "`:$tooltip-background-color-inverse`"         
          :br "`:$tooltip-background-image`"                 
          :br
          :br "__Typography:__"
              "`:$tooltip-line-height`"
          :br "`:$tooltip-font-family`"
          :br "`:$tooltip-font-size`"
          :br "`:$tooltip-font-weight`"
          :br "`:$tooltip-text-transform`"
          :br
          :br "__Geometry:__"
              "`:$tooltip-padding-inline`"
          :br "`:$tooltip-padding-block`"
          :br "`:$tooltip-border-radius`"
          :br "`:$tooltip-offset`"
          :br "`:$tooltip-viewport-padding`"
          :br "`:$tooltip-flip-viewport-edge-threshold`"
          :br "`:$tooltip-auto-placement-y-threshold`"
          :br
          :br "__Choreography:__"
              "`:$tooltip-delay-duration`"            
          :br "`:$tooltip-reveal-on-click-duration`"  
          :br "`:$tooltip-initial-scale`"             
          :br "`:$tooltip-offset-start`"              
          :br "`:$tooltip-transition-duration`"       
          :br "`:$tooltip-transition-timing-function`"
          :br
          :br "__Arrows:__"
              "`:$tooltip-arrow-depth`"   
          :br "`:$tooltip-arrow-x-offset`"
          :br "`:$tooltip-arrow-y-offset`"

          :br
          :br
          "If you want supply the value of any of the above tokens ala-carte, "
          "use the following pattern."
          :br
          :br
          "`(merge-attrs (sx :$tooltip-offset--5px ...) (tooltip-attrs {...}))`"
          :br
          :br
          "If you would like to use a value of 0 (`px`, `ems`, `rem`, etc.) for "
          "`$tooltip-offset`, `$tooltip-arrow-x-offset`, "
          "`$tooltip-arrow-y-offset`, or `$tooltip-border-radius`, you will need "
          "to use an explicit unit e.g. `0px`."
          ]
   :opts '[{:name    text
            :pred    #(or (string? %) (keyword? %) (vector? %))
            :default nil
            :desc    "Required. The text to display in the tooltip"}
           {:name    placement
            :pred    keyword?
            :default :auto
            :desc    [
                      "You can use single keywords to specify the exact placement "
                      "of the tooltip:"
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
                      "If you care about the tooltip placement respecting writing "
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
                     "the tooltip."]}
           {:name    tooltip-class
            :pred    string?
            :default nil
            :desc    ["A class name for a la carte application of a classes on the "
                      " tooltip element."]}
           {:name    text-on-click
            :pred    #(or (string? %) (keyword? %) (vector? %))
            :default nil
            :desc    ["The tooltip text, after the tipped element has been clicked."]}
           {:name    text-on-click-tooltip-class
            :pred    string?
            :default nil
            :desc    ["A class name for the la carte application of classes on "
                      "the tooltip element which is displaying alternate text "
                      "after click."]}]}
  [{text                        :-text
    placement                   :-placement
    arrow?                      :-arrow?
    tooltip-class               :-tooltip-class
    text-on-click               :-text-on-click
    text-on-click-tooltip-class :-text-on-click-tooltip-class
    :or                         {placement :auto
                                 arrow?    true}}]
  
  (when-let [tooltip-text (valid-tooltip-text text)] 
    (let [arrow?       (if (false? arrow?) false true)
          placement    (if-not (or (string? placement)
                                   (keyword? placement)
                                   (vector? placement))
                         :auto
                         placement)
          placement-kw (or (maybe placement #(= % :auto))
                           (user-placement placement))
          pane-type    :tooltip
          opts         (keyed tooltip-text
                              placement-kw
                              arrow?
                              pane-type
                              tooltip-class)]
      (merge 
       {:data-kushi-ui-pane (name placement-kw)
        :on-mouse-enter     (partial pane/append-pane! opts)}

       ;; Todo use when-let to validate text-on-click and normalize if vector
       (when-let [text-on-click (pane/maybe-multiline-tooltip-text text-on-click)]
         {:on-click
          (fn [_]
            (let [duration           (token->ms :$tooltip-text-on-click-duration)
                  tt-el              (domo/qs ".kushi-pane")
                  tt-el-text-wrapper (domo/qs tt-el ".kushi-tooltip-text-wrapper")
                  tt-el-text-span    (domo/qs tt-el ".kushi-tooltip-text")
                  text-on-click-el   (js/document.createElement "span")]
              (j/assoc! text-on-click-el "innerText" text-on-click)
              (domo/add-class! text-on-click-el "absolute-centered")
              (some->> text-on-click-tooltip-class (domo/add-class! tt-el))
              (.appendChild tt-el-text-wrapper text-on-click-el)
              (domo/add-class! tt-el-text-span "invisible")
              (js/setTimeout (fn [_] 
                               (.removeChild tt-el-text-wrapper
                                             text-on-click-el)
                               (some->> text-on-click-tooltip-class
                                        (domo/remove-class! tt-el))
                               (domo/remove-class! tt-el-text-span
                                                   "invisible"))
                             duration)))})))))
