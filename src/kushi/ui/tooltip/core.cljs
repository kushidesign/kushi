(ns kushi.ui.tooltip.core
  (:require [applied-science.js-interop :as j]
            [fireworks.core :refer [? !? ?> !?>]]
            [clojure.string :as string]
            [domo.core :as domo]
            [goog.string]
            [kushi.core :refer (token->ms register-design-tokens-by-category)]
            [kushi.ui.dom.pane.core :as pane] [kushi.ui.util :as util :refer [maybe]]
            ;; Import this styles ns to create defclasses
            [kushi.ui.dom.pane.placement :refer [user-placement]]
            [kushi.ui.dom.pane.styles]))

(register-design-tokens-by-category
 "elevation"
 "pane"
 "tooltip")

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

        (or (symbol? text) (keyword? text) (number? text))
        (str text)

        (coll? text)
        (when (valid-tooltip-text-coll? text)
          (into [] text))

        (array? text)
        (let [v (js->clj text)]
          (when (valid-tooltip-text-coll? v)
            v))))

(defn tooltip-attrs
  {:summary "Tooltips provide additional context when hovering or clicking 
             on an element."
   :desc "Tooltips are intended to be ephemeral, containing only non-interactive
          content.

          By default, tooltips will show up above the owning element.Specifying
          placement in various ways can be done with the `:placement` option.
          
          The element being tipped must receive an attributes map that is a
          result of passing a map of options to
          `kushi.ui.tooltip.core/tooltip-attrs`. You can compose this map to an
          existing elements attributes map using the pattern:
          
          `(merge-attrs (sx ...) (tooltip-attrs {...}))`
          ~ or ~
          `(merge-attrs {...} (tooltip-attrs {...}))`
          
          Tooltips can be custom styled and controlled via the following tokens
          in your theme:
          
          __Colors and images:__
          `--tooltip-color`                            
          `--tooltip-color-dark-mode`
          `--tooltip-background-color`                 
          `--tooltip-background-color-dark-mode`         
          `--tooltip-background-image`                 
          
          __Typography:__
          `--tooltip-line-height`
          `--tooltip-font-family`
          `--tooltip-font-size`
          `--tooltip-font-weight`
          `--tooltip-text-transform`
          
          __Geometry:__
          `--tooltip-padding-inline`
          `--tooltip-padding-block`
          `--tooltip-border-radius`
          `--tooltip-offset`
          `--tooltip-viewport-padding`
          `--tooltip-flip-viewport-edge-threshold`
          `--tooltip-auto-placement-y-threshold`
          
          __Choreography:__
          `--tooltip-delay-duration`            
          `--tooltip-reveal-on-click-duration`  
          `--tooltip-initial-scale`             
          `--tooltip-offset-start`              
          `--tooltip-transition-duration`       
          `--tooltip-transition-timing-function`
          
          __Arrows:__
          `--tooltip-arrow-depth`   
          `--tooltip-arrow-x-offset`
          `--tooltip-arrow-y-offset`
          
          If you want supply the value of any of the above tokens ala-carte,
          check out the \"Styling via design token at callsite\" example
          in this Examples section.
          
          `(tooltip-attrs`<br>
          ` {:text \"My text\"`<br>
          `  :tooltip-class (css [:--tooltip-font-size :34px])}))`
          
          If you would like to use a value of 0 (`px`, `ems`, `rem`, etc.) for
          `--tooltip-offset`, `--tooltip-arrow-x-offset`,
          `--tooltip-arrow-y-offset`, or `--tooltip-border-radius`, you will
          need to use an explicit unit e.g. `0px`."
          
   :opts '[{:name    text
            :schema    #(or (string? %) (keyword? %) (vector? %) (number? %))
            :default nil
            :desc    "Required. The text to display in the tooltip"}
           {:name    placement
            :schema    keyword?
            :default :auto
            :desc    "You can use single keywords to specify the exact placement 
                      of the tooltip:
                      
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
                      
                      If you care about the tooltip placement respecting writing
                      direction and/or document flow, you can use a vector of of 
                      up to 3 logical properties keywords, separated by spaces:
                      
                      `[:inline-end :block-start]`
                      `[:inline-end :block-start :corner]`
                      `[:inline-start :center]`
                      `[:inline-end :center]`
                      `[:block-start :enter]`
                      `[:block-end :center]`
                      `[:block-end :inline-start]`"}
           {:name    arrow?
            :schema    boolean?
            :default true
            :desc    "Setting to false will not render a directional arrow with 
                      the tooltip."}
           {:name    tooltip-class
            :schema    string?
            :default nil
            :desc    "A class name for a la carte application of classes on the
                      tooltip element."}
           {:name    text-on-click
            :schema    #(or (string? %) (keyword? %) (vector? %) (number? %))
            :default nil
            :desc    "The tooltip text, after the tipped element has been clicked."}
           {:name    text-on-click-tooltip-class
            :schema    string?
            :default nil
            :desc    "A class name for the la carte application of classes on
                      the tooltip element which is displaying alternate text
                      after click."}]}
  [{text                        :text
    placement                   :placement
    arrow?                      :arrow?
    tooltip-class               :tooltip-class
    text-on-click               :text-on-click
    text-on-click-tooltip-class :text-on-click-tooltip-class
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
          opts         {:tooltip-text  tooltip-text
                        :placement-kw  placement-kw
                        :arrow?        arrow?
                        :pane-type     pane-type
                        :tooltip-class tooltip-class}]
      (merge 
       {:data-kushi-ui-pane (name placement-kw)
        :on-mouse-enter     (partial pane/append-pane! opts)}
       ;; Todo use when-let to validate text-on-click and normalize if vector
       (when-let [text-on-click (pane/maybe-multiline-tooltip-text text-on-click)]
         {:on-mouse-down
          (fn [_]
            (let [duration              (token->ms :--tooltip-text-on-click-duration)
                  tt-el                 (domo/qs ".kushi-tooltip")
                  tt-el-text-wrapper    (domo/qs tt-el ".kushi-tooltip-text-wrapper")
                  tt-el-text-span       (domo/qs tt-el ".kushi-tooltip-text")
                  text-on-mouse-down-el (js/document.createElement "span")]

              (j/assoc! text-on-mouse-down-el "innerText" text-on-click)
              (domo/add-class! text-on-mouse-down-el "absolute-centered")
              (some->> text-on-click-tooltip-class (domo/add-class! tt-el))
              (.appendChild tt-el-text-wrapper text-on-mouse-down-el)
              (domo/add-class! tt-el-text-span "invisible")
              (js/setTimeout (fn [_] 
                               (.removeChild tt-el-text-wrapper
                                             text-on-mouse-down-el)
                               (some->> text-on-click-tooltip-class
                                        (domo/remove-class! tt-el))
                               (domo/remove-class! tt-el-text-span
                                                   "invisible"))
                             duration)))})))))
