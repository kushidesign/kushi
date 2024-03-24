(ns kushi.ui.tooltip.core
  (:require
   [fireworks.core :refer [? ?? ??? !? ?> !?> ]]
   [clojure.string :as string]
   [clojure.data :as data]
   [goog.string]
   [domo.core :as domo]
   ;; Import this to create defclasses
   [kushi.core :refer (keyed token->ms)]
   [kushi.ui.util :as util :refer [maybe nameable? as-str]]
   [kushi.ui.dom.fune.arrow :as arrow]
   [kushi.ui.dom.fune.placement :refer [el-plc
                                        fune-plc
                                        updated-fune-placement
                                        user-placement
                                        og-placement
                                        placement-css-custom-property]]
   [kushi.ui.dom.fune.styles]
   [applied-science.js-interop :as j]))

(defn fune-classes
  [{:keys [fune-class 
           fune-arrow?
           placement-kw
           new-placement-kw
           metrics?]}]
  (string/join
   " " 
   ["kushi-fune"
    "invisible" 
    (some->> (if metrics? 
               placement-kw
               new-placement-kw)
             name
             (str "kushi-fune-"))
    (when-not fune-arrow? "kushi-fune-arrowless")
    (some-> fune-class (maybe nameable?) as-str)]))

(defn maybe-multiline-tooltip-text [ttt]
  (if (or (vector? ttt)
          (array? ttt)
          (and (not (string? ttt))
               (seq ttt)))
    (string/join "<br>" ttt)
    ttt))

(defn- append-tooltip-el!
  [{:keys [el
           ttt
           id
           placement-kw
           tt-pos-og
           metrics?
           translate-xy-style]
    :as append-tt-opts}]
(!? append-tt-opts)

  ;; Set the innerHTML / fune text
  (let [style (domo/css-style-string
               {:position        :relative
                :display         :flex
                :justify-content :center
                :align-items     :center
                :width           :fit-content})
        ]
    (set! (.-innerHTML el)
          (str "<div class=\"kushi-tooltip-text-wrapper\""
               "style=\"" style  "\">"
               "<span class=\"kushi-tooltip-text\">"
               (? (maybe-multiline-tooltip-text (? ttt)))
               "</span></div>")))
  
  ;; TODO
  ;; Set the class and id of the fune el
  ;; make sure translate-xy-style is always map, then merge with below
  (let [txy     (or translate-xy-style
                    (placement-css-custom-property
                     (assoc append-tt-opts
                            :corner-plc?
                            (:corner-plc? tt-pos-og)
                            :fune-el el
                            :placement-kw placement-kw)
                     #_placement-kw))
        
        txy (str txy
                 "; "
                 (when metrics? 
                   (domo/css-style-string
                    {:scale               :1!important
                     :transition-property :none!important})))]
    (doto el
      (.setAttribute "id" id)
      (.setAttribute "style" txy)
      (.setAttribute "class" (fune-classes append-tt-opts))))
  
  ;; Append fune el to the <body> 
  (.appendChild js/document.body el))



(defn- append-tooltip!*
  "A fune is given an initial position based on screen-quadrant of the owning
   element, or user-supplied `-placement` attr. If it is offscreen, it is then
   given a new placement and position which may include a shift on the x or y
   axis in order to keep it wholly in the veiwport."
  [{ttt          :tooltip-text
    placement-kw :placement-kw
    fune-arrow?  :fune-arrow?
    owning-el    :owning-el
    :as opts}
   id]

  ;; 1) Pre-calculate and append fune
  ;; Calculate an initial placment and append a fune element to the
  ;; dom. If the owning element is beyond the edge-threshold, the fune will
  ;; be assigned a new placement, but only if the value of :-fune-placement
  ;; is something other than :auto.

  ;; TODO - optimize for auto placement
  ;; --------------------------------------------------------------------------------
  (let [fune-arrow?  (if (false? fune-arrow?) false true)
        opts            (assoc opts
                               :owning-el-rect
                               (domo/client-rect owning-el))
        viewport        (domo/viewport)
        ;; TOOD - Use some koind of dpi calc for edge-threshold.
        ;; Convert to px if user supplies ems or rems.
        edge-threshold  (some-> owning-el
                                (domo/css-custom-property-value
                                 "--fune-flip-viewport-edge-threshold")
                                js/parseInt)
        owning-el-vpp   (el-plc viewport
                                owning-el
                                edge-threshold)
        placement-kw    (og-placement placement-kw
                                      owning-el
                                      owning-el-vpp)
        tt-pos-og       (!? :tt-pos-og (fune-plc placement-kw))
        el              (js/document.createElement "div")
        append-tt-opts  (merge opts
                               (keyed el
                                      id
                                      ttt
                                      placement-kw
                                      tt-pos-og 
                                      fune-arrow?))]
    (append-tooltip-el! (merge append-tt-opts
                               {:metrics? true
                                :id       (str "_kushi-metrics_" id)}))


    ;; 2) Measure and adjust
    ;; Second, detect if the fune falls outside the viewport
    ;; If the fune needs to be "shifted" along x or y axis to move it
    ;; back inside viewport, get an updated style value and reset the style.
    
    ;; TODO - if needed, viewport-padding should be handled in css-land.
    ;; Add some kind of :tl or :br key to opts below, then use padding value
    ;; in css land.
    ;; -----------------------------------------------------------------------------
    (let [vpp                  (!? {:label 'vpp :coll-limit 24} (el-plc viewport el 0))
          new-placement-kw     (!? 'npkw (updated-fune-placement
                                (merge tt-pos-og
                                       (keyed vpp placement-kw))))

          tt-pos               (fune-plc new-placement-kw)


          ;; Disable shifting for now and just return nil for shift-x & shift-y
          ;; {:keys [shift-x
          ;;         shift-y]} (shifts (merge tt-pos (keyed viewport vpp)))
          
          shift-x              nil 
          shift-y              nil 
          
          shift?               (boolean (or shift-x shift-y))
          new-placement?       (not= placement-kw new-placement-kw)    
          adjust?              (boolean (or shift? new-placement?))

          translate-xy-style   (when adjust?
            ;; (??? new-placement-kw)
            ;; (? (data/diff opts append-tt-opts))
                                 (let [opts (assoc opts ;; need to be different than append-tt-opts?
                                                   :shift-x        shift-x
                                                   :shift-y        shift-y
                                                   :corner-plc?    (:corner-plc? tt-pos)
                                                   :el             el
                                                   :fune-arrow? fune-arrow?
                                                   :adjust?        true
                                                   :placement-kw   new-placement-kw)]
                                   (placement-css-custom-property
                                    opts
                                    #_new-placement-kw)))
          

          
          ;; 3) Remove dummy and append new element
          ;; If shifted, update scale
          ;; ----------------------------------------------------------------------

          _                    (.removeChild js/document.body el) ;; move down
          
          el                   (js/document.createElement "div")

          append-tt-opts-part2 (merge opts
                                      (keyed el
                                             id 
                                             ttt
                                             fune-arrow?
                                             
                                             ;; is this right? why not new-placement-kw
                                             placement-kw ;;exists in atto 
                                             
                                             ;; is this right or tt-pos better?
                                             tt-pos-og
                                             ;; experimental
                                             new-placement-kw 
                                             ))
          ]

      ;; temp :11:09
      (append-tooltip-el! append-tt-opts-part2)
      
      ;; temp :11:09
      (when adjust?
        (.setAttribute el "style" translate-xy-style))
      

      ;; (println :after-update (keyed shift-x shift-y adjust? new-placement-kw placement-kw))
      
      
      ;; (.setAttribute el "style" updated-translate-xy)
      

      ;; 4) Arrow
      ;; Add the class with final placement syntax "kushi-fune-tr".
      ;; Then create an arrow element and calculate position and geometry.
      ;; ----------------------------------------------------------------------

      ;; Trying this in append-fune-el! for now
      #_(domo/add-class! el
                       (str "kushi-fune-"
                            (name new-placement-kw)))
      #_(domo/set-attribute! el "data-kushi-fune-placement" (name new-placement-kw))
      

      ;; 5) Display
      ;; Remove `.invisible` class, which will fade-in the fune via
      ;; css transition setting, if desired. 
      (let [arrow-el (when (and fune-arrow?
                                (not (:corner-plc? tt-pos)))
                       (arrow/append-arrow-el!
                        (keyed el
                               tt-pos
                               shift-x 
                               shift-y
                               new-placement-kw
                               owning-el-vpp
                               shift?)))

            ;; No shifting for now, so leaving these out
            ;; transition-duration-ms (domo/duration-property-ms el "transitionDuration")
            ;; transition-delay-ms    (domo/duration-property-ms el "transitionDelay")
            ;; compute-shift-delay    (+ transition-delay-ms transition-duration-ms)
            ]
        (js/window.requestAnimationFrame
         (fn [_]
           (domo/remove-class! el "invisible")
           (domo/set-css-var! el "--tt-offset" "max(var(--fune-offset), 0px)")
           (domo/set-style! el "scale" "1")
           
              ;; Shifts are disabled for now so commenting this expression out
           #_(when shift?
                ;; TODO - we could also try doing a recusive setInterval loop here
                ;; to progressively move arrow. Or maybe just introduce a second
                ;; metrics dummy to get the exact postion, taking into accont for
                ;; the border-radius.
               (js/setTimeout
                #(arrow/shift-arrow! (keyed owning-el-vpp arrow-el tt-pos))
                compute-shift-delay))
           
           ))))))

(declare remove-tooltip!)

(defn- escape-tooltip!
  "If Escape key is pressed when fune is active, dispatch `remove-tooltip`.
   to remove fune element from DOM.
   Also removes the `mouseleave` event listener on owning element."
  [owning-el tt-id e]
  (when-not e.defaultPrevented
    (when (or (= e.type "scroll") 
              (= e.key "Escape"))
      (when owning-el
        (remove-tooltip! owning-el tt-id nil)
        (.removeEventListener owning-el
                              "mouseleave"
                              (partial remove-tooltip!
                                       owning-el
                                       tt-id)
                              #js {"once" true})))))

(defn- remove-tooltip!
  "Removes fune from dom.
   Removes :aria-describedby on owning element.
   Removes the fune instance-specific `keydown` event on window."
  [owning-el tt-id e]
  #_(some->> tt-id
           domo/el-by-id
           (.removeChild js/document.body))
  (domo/remove-attribute! owning-el :aria-describedby)
  (.removeEventListener owning-el
                        "mouseleave"
                        (partial remove-tooltip! owning-el tt-id)
                        #js {"once" true})
  (.removeEventListener js/window
                        "keydown"
                        (partial escape-tooltip! owning-el tt-id)
                        #js {"once" true})
  (.removeEventListener js/window
                        "scroll"
                        (partial escape-tooltip! owning-el tt-id)
                        #js {"once" true}))

(defn- remove-tooltip2!
  "Removes fune from dom.
   Removes :aria-describedby on owning element.
   Removes the fune instance-specific `keydown` event on window."
  [el]
  (.remove el)
  (let [owning-el (domo/qs (str "[aria-describedby='" (.-id el) "']")) ]
    (domo/remove-attribute! owning-el :aria-describedby)))

(defn append-tooltip!
  ([opts e]
  (append-tooltip! opts nil e))
  ([opts tt-id e]
   ;; We need to use cet here (.currentEventTarget), in order
   ;; To prevent mis-assignment of ownership of the fune to
   ;; A child element of the intended owning el. 
  ;;  (js/console.clear)
   (let [owning-el (domo/cet e)
                   ;; TODO - should this be "kushi-fune-*" ?
         tt-id     (or tt-id (str "kushi-" (gensym)))]
     (do 
       (domo/set-attribute! owning-el :aria-describedby tt-id)
       (append-tooltip!* (assoc opts :owning-el owning-el)
                         tt-id)
       (.addEventListener owning-el
                          "mouseleave"
                          (partial remove-tooltip! owning-el tt-id)
                          #js {"once" true})
       (.addEventListener js/window
                          "keydown"
                          (partial escape-tooltip! owning-el tt-id)
                          #js {"once" true})
       (.addEventListener js/window
                          "scroll"
                          (partial escape-tooltip! owning-el tt-id)
                          #js {"once" true})
       ))))

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
          :br "Colors and images:"
          :br "`:$tooltip-color`"                            
          :br "`:$tooltip-color-inverse`"                    
          :br "`:$tooltip-background-color`"                 
          :br "`:$tooltip-background-color-inverse`"         
          :br "`:$tooltip-background-image`"                 
          :br
          :br "Typography:"
          :br "`:$tooltip-line-height`"
          :br "`:$tooltip-font-family`"
          :br "`:$tooltip-font-size`"
          :br "`:$tooltip-font-weight`"
          :br "`:$tooltip-text-transform`"
          :br
          :br "Geometry:"
          :br "`:$tooltip-padding-inline`"
          :br "`:$tooltip-padding-block`"
          :br "`:$tooltip-border-radius`"
          :br "`:$tooltip-offset`"
          :br "`:$tooltip-viewport-padding`"
          :br "`:$tooltip-flip-viewport-edge-threshold`"
          :br "`:$tooltip-auto-placement-y-threshold`"
          :br
          :br "Choreography:"
          :br "`:$tooltip-delay-duration`"            
          :br "`:$tooltip-reveal-on-click-duration`"  
          :br "`:$tooltip-initial-scale`"             
          :br "`:$tooltip-offset-start`"              
          :br "`:$tooltip-transition-duration`"       
          :br "`:$tooltip-transition-timing-function`"
          :br
          :br "Arrows:"
          :br "`:$tooltip-arrow-depth-min-px`"
          :br "`:$tooltip-arrow-depth-max-px`"
          :br "`:$tooltip-arrow-depth-ems`"
          :br "`:$tooltip-arrow-depth`"   
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
    (let [fune-arrow? (if (false? arrow?) false true)
          placement      (if-not (or (string? placement)
                                     (keyword? placement)
                                     (vector? placement))
                           :auto
                           placement)
          placement-kw   (or (maybe placement #(= % :auto))
                             (user-placement placement))
          opts           (keyed tooltip-text
                                placement-kw
                                fune-arrow?
                                tooltip-class)]
      (merge 
       {:data-kushi-ui-fune (name placement-kw)
        :on-mouse-enter     (partial append-tooltip! opts)}

       ;; Todo use when-let to validate text-on-click and normalize if vector
       (when-let [text-on-click (maybe-multiline-tooltip-text text-on-click)]
         {:on-click (fn [_]
                      (let [duration           (token->ms :$tooltip-reveal-on-click-duration)
                            tt-el              (domo/qs ".kushi-fune")
                            tt-el-text-wrapper (domo/qs tt-el ".kushi-tooltip-text-wrapper")
                            tt-el-text-span    (domo/qs tt-el ".kushi-tooltip-text")
                            text-on-click-el   (js/document.createElement "span")]
                        (j/assoc! text-on-click-el "innerText" text-on-click)
                        (domo/add-class! text-on-click-el "absolute-centered")
                        (domo/add-class! tt-el text-on-click-tooltip-class)
                        (.appendChild tt-el-text-wrapper text-on-click-el)
                        (domo/add-class! tt-el-text-span "invisible")
                        (js/setTimeout (fn [_] 
                                         (.removeChild tt-el-text-wrapper text-on-click-el)
                                         (domo/remove-class! tt-el text-on-click-tooltip-class)
                                         (domo/remove-class! tt-el-text-span "invisible"))
                                       duration)))})))))
