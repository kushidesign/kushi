(ns kushi.ui.tooltip.core
  (:require
   [clojure.string :as string]
   [goog.string]
   [kushi.ui.dom :as dom]
   ;; Import this to create defclasses
   [kushi.core :refer (keyed token->ms)]
   [kushi.ui.util :as util :refer [maybe nameable? as-str]]
   [kushi.ui.tooltip.arrow :as arrow]
   [kushi.ui.tooltip.placement :refer [el-positioning
                                       tooltip-positioning
                                       updated-tooltip-placement
                                       user-placement
                                       og-placement
                                      ;;  shifts
                                       ]]
   [kushi.ui.tooltip.styles]
   [kushi.ui.tooltip.translate :as translate]
   [applied-science.js-interop :as j]))

(defn tooltip-classes
  [{:keys [tooltip-class tooltip-arrow?]}]
  (string/join
   " " 
   ["kushi-floating-tooltip"
    "invisible" 
    (when-not tooltip-arrow? "kushi-floating-tooltip-arrowless")
    (some-> tooltip-class (maybe nameable?) as-str)]))

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
           opts
           placement-kw
           tt-pos-og
           metrics?
           translate-xy-style]
    :as append-tt-opts}]

  ;; Set the innerHTML / tooltip text
  (set! (.-innerHTML el)
        (str "<div class=\"kushi-floating-tooltip-text-wrapper\" style=\"position:relative;display:flex;justify-content:center;align-items:center;width:fit-content;\"><span class=\"kushi-floating-tooltip-text\">"
               (maybe-multiline-tooltip-text ttt)
             "</span></div>"))
  
  ;; Set the class and id of the tooltip el
  (doto el
    (.setAttribute "id" id)
    (.setAttribute "class" (tooltip-classes append-tt-opts)))
  
  ;; Append tooltip el to the <body> 
  (.appendChild js/document.body el)

  ;; Calculate the css :translate property and set the :style
  (let [txy (or translate-xy-style
                (translate/tooltip-translate-css
                 (assoc opts
                        :corner-positioning?
                        (:corner-positioning? tt-pos-og)
                        :tooltip-el el)
                 placement-kw))]
    (.setAttribute el
                   "style"
                   (str txy
                        (when metrics? 
                          (dom/css-style-string
                           {:scale               :1!important
                            :transition-property :none!important}))))

    ;; TODO - maybe return txy so we can use it again next time we call this fn?
    ))



(defn- append-tooltip!*
  "A tooltip is given an initial position based on screen-quadrant of the owning
   element, or user-supplied `-placment` attr. If it is offscreen, it is then
   given a new placement and position which may include a shift on the x or y
   axis in order to keep it wholly in the veiwport."
  [{ttt            :tooltip-text
    placement-kw   :placement-kw
    tooltip-class  :tooltip-class
    tooltip-arrow? :tooltip-arrow?
    owning-el      :owning-el
    :as opts}
   id]

  ;; 1) Pre-calculate and append tooltip
  ;; Calculate an initial placment and append a tooltip element to the
  ;; dom. If the owning element is beyond the edge-threshold, the tooltip will
  ;; be assigned a new placement, but only if the value of :-tooltip-placement
  ;; is something other than :auto.

  ;; TODO - optimize for auto placement
  ;; --------------------------------------------------------------------------------
  (let [tooltip-arrow?  (if (false? tooltip-arrow?) false true)
        opts            (assoc opts
                               :owning-el-rect
                               (dom/client-rect owning-el))
        viewport        (dom/viewport)
        ;; TOOD - Use some koind of dpi calc for edge-threshold.
        ;; Convert to px if user supplies ems or rems.
        edge-threshold  (some-> owning-el
                                (dom/css-custom-property-value
                                 "--tooltip-flip-viewport-edge-threshold")
                                js/parseInt)
        owning-el-vpp   (el-positioning viewport
                                        owning-el
                                        edge-threshold)
        placement-kw    (og-placement placement-kw
                                      owning-el
                                      owning-el-vpp)
        tt-pos-og       (tooltip-positioning placement-kw)
        el              (js/document.createElement "div")
        ;; TODO - maybe use existing opts instead of nesting it here?
        ;; also you are doing this in append-tooltip-el!, so maybed do it here?:
            ;; (assoc opts 
            ;;   :corner-positioning?
            ;;   (:corner-positioning? tt-pos-og)
            ;;   :tooltip-el el)
        append-tt-opts  (keyed el
                               id
                               ttt
                               opts
                               placement-kw 
                               tooltip-class
                               tooltip-arrow?
                               tt-pos-og)]
    (append-tooltip-el! (assoc append-tt-opts
                               :metrics? true
                               :id       (str "_kushi-metrics_" id)))


    ;; 2) Measure and adjust
    ;; Second, detect if the tooltip falls outside the viewport
    ;; If the tooltip needs to be "shifted" along x or y axis to move it
    ;; back inside viewport, get an updated style value and reset the style.
    
    ;; TODO - if needed, viewport-padding should be handled in css-land.
    ;; Add some kind of :tl or :br key to opts below, then use padding value
    ;; in css land.
    ;; -----------------------------------------------------------------------------
    (let [vpp               (el-positioning viewport el 0)
          new-placement-kw  (updated-tooltip-placement
                             (merge tt-pos-og
                                    (keyed vpp placement-kw)))

          tt-pos            (tooltip-positioning new-placement-kw)


          ;; Disable shifting for now and just return nil for shift-x & shift-y
          ;; {:keys [shift-x
          ;;         shift-y]} (shifts (merge tt-pos (keyed viewport vpp)))
          
          shift-x        nil 
          shift-y        nil 
          
          shift?            (boolean (or shift-x shift-y))
          new-placement?    (not= placement-kw new-placement-kw)    
          adjust?           (boolean (or shift? new-placement?))

          translate-xy-style
          (when adjust?
            (let [opts (assoc opts
                              :shift-x             shift-x
                              :shift-y             shift-y
                              :corner-positioning? (:corner-positioning? tt-pos)
                              :tooltip-el          el
                              :tooltip-arrow?      tooltip-arrow?
                              :adjust?             true) ]
              (translate/tooltip-translate-css
               opts
               new-placement-kw)))
          

          
          ;; 3) Remove dummy and append new element
          ;; If shifted, update scale
          ;; ----------------------------------------------------------------------
          _               (.removeChild js/document.body el)
          el              (js/document.createElement "div")
          append-tt-opts  (keyed el
                                 id
                                 ttt
                                 opts
                                 placement-kw 
                                 tooltip-arrow?
                                 tooltip-class
                                 ;; is this right or tt-pos better?
                                 tt-pos-og)]

       (append-tooltip-el! append-tt-opts)
       
       (when adjust?
         (.setAttribute el "style" translate-xy-style))
          

      ;;  (println :after-update (keyed shift-x shift-y adjust? new-placement-kw placement-kw))

      ;; (.setAttribute el "style" updated-translate-xy)
      

      ;; 4) Arrow
      ;; Add the class with final placement syntax "kushi-floating-tooltip-tr".
      ;; Then create an arrow element and calculate position and geometry.
      ;; ----------------------------------------------------------------------
      (dom/add-class el
                     (str "kushi-floating-tooltip-"
                          (name new-placement-kw)))

      

      ;; 5) Display
      ;; Remove `.invisible` class, which will fade-in the tooltip via
      ;; css transition setting, if desired. 
      (let [arrow-el              (when (and tooltip-arrow?
                                             (not (:corner-positioning? tt-pos)))
                                    (arrow/append-arrow-el!
                                     (keyed el
                                            tt-pos
                                            shift-x 
                                            shift-y
                                            new-placement-kw
                                            owning-el-vpp
                                            shift?)))

            ;; No shifting for now, so leaving these out
            ;; transition-duration-ms (dom/duration-property-ms el "transitionDuration")
            ;; transition-delay-ms    (dom/duration-property-ms el "transitionDelay")
            ;; compute-shift-delay    (+ transition-delay-ms transition-duration-ms)
            ]
        (js/window.requestAnimationFrame
            (fn [_]
              (dom/remove-class el "invisible")
              
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
  "If Escape key is pressed when tooltip is active, dispatch `remove-tooltip`.
   to remove tooltip element from DOM.
   Also removes the `mouseleave` event listener on owning element."
  [owning-el tt-id e]
  (when-not e.defaultPrevented
    (when (or (= e.type "scroll") 
              (= e.key "Escape"))
      (when owning-el
        (remove-tooltip! owning-el tt-id nil)))))

(defn- remove-tooltip!
  "Removes tooltip from dom.
   Removes :aria-describedby on owning element.
   Removes the tooltip instance-specific `keydown` event on window."
  [owning-el tt-id _]
  (some->> tt-id
           dom/el-by-id
           #_(.removeChild js/document.body))
  (dom/remove-attribute! owning-el :aria-describedby)
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

(defn append-tooltip!
  ([opts e]
  (append-tooltip! opts nil e))
  ([opts tt-id e]
   ;; We need to use cet here (.currentEventTarget), in order
   ;; To prevent mis-assignment of ownership of the tooltip to
   ;; A child element of the intended owning el. 
   (let [owning-el (dom/cet e)
                   ;; TODO - should this be "kushi-tooltip-*" ?
         tt-id     (or tt-id (str "kushi-" (gensym)))]
     (do 
       (dom/set-attribute! owning-el :aria-describedby tt-id)
       (append-tooltip!* (assoc opts :owning-el owning-el) tt-id)
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
                          #js {"once" true})))))

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
  {:desc ["Tooltips provide additional context when hovering or clicking on an element. They are intended to be ephemeral, containing only non-interactive content."
          :br
          :br
          "Specifying placement in various ways can be done with the `:-placement` option."
          :br
          :br
          "These tooltips are implemented with a `::after` and `::before` pseudo-elements and therefore differ from most of the other primitive component Kushi offers."
          "The element being tipped must receive an attributes map that is a result of passing a map of options to `kushi.ui.tooltip.core/tooltip-attrs`."
          "You can compose this map to an existing elements attributes map using the pattern:"
          :br
          :br "`(merge-attrs (sx ...) (tooltip-attrs {...}))`"
          :br
          :br
          "If the element that you are tipping is already using either of the `::before` or `::after` pseudo-elements, you will need to wrap it in a container (perhaps a `<span>`) and apply the tooltip attrs to that wrapper."
          :br
          :br
          "The element being tipped must also have a css `position` value such as `relative` set, so that the absolutely-positioned tooltip pseudo-element will end up with the desired placement."
          "Tooltips can be custom styled via the following tokens in your theme:"
          :br
          :br
          ;; TODO add documentation for each token
          :br "`:$tooltip-arrow-depth`"
          :br "`:$tooltip-arrow-x-offset`"
          :br "`:$tooltip-arrow-y-offset`"
          :br "`:$tooltip-padding-inline`"
          :br "`:$tooltip-padding-block`"
          :br "`:$tooltip-border-radius`"
          :br "`:$tooltip-font-size`"
          :br "`:$tooltip-font-weight`"
          :br "`:$tooltip-color`"
          :br "`:$tooltip-background-color`"
          :br "`:$tooltip-color-inverse`"
          :br "`:$tooltip-background-color-inverse`"
          :br "`:$tooltip-text-transform`"
          :br "`:$tooltip-offset`"
          :br
          :br
          "If you want supply the value of any of the above tokens ala-carte, use the following pattern."
          :br
          :br
          "`(merge-attrs (sx :$tooltip-offset--5px ...) (tooltip-attrs {...}))`"
          :br
          :br
          "If you would like to use a value of 0 (`px`, `ems`, `rem`, etc.) for `$tooltip-offset`, `$tooltip-arrow-x-offset`, `$tooltip-arrow-y-offset`, or `$tooltip-border-radius`, you will need to use an explicit unit e.g. `0px`."
          ]
   :opts '[{:name    text
            :pred    #(or (string? %) (keyword? %) (vector? %))
            :default nil
            :desc    "Required. The text to display in the tooltip"}
           {:name    placement
            :pred    keyword?
            :default :auto
            :desc    [
                      "You can use single keywords to specify the exact placement of the tooltip:"
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
                      "If you care about the tooltip placement respecting writing direction and/or document flow, you can use a string of up to 3 logical properties, separated by spaces:"
                      :br
                      "`\"inline-end block-start\"`"
                      :br
                      "`\"inline-end block-start corner\"`"
                      :br
                      "`\"inline-start center\"`"
                      :br
                      "`\"inline-end center\"`"
                      :br
                      "`\"block-start center\"`"
                      :br
                      "`\"block-end center\"`"
                      :br
                      "`\"block-end inline-start\"`"
                      :br]}
           {:name    arrow?
            :pred    boolean?
            :default true
            :desc    "Setting to false will not render a directional arrow with the tooltip."}
           {:name    tooltip-class
            :pred    string
            :default nil
            :desc    "A classname for a la carte application of a classes on the tooltip element."}
           {:name    text-on-click
            :pred    #(or (string? %) (keyword? %) (vector? %))
            :default nil
            :desc    ["The tooltip text, after the tipped element has been clicked."]}
           {:name    text-on-click-tooltip-class
            :pred    string
            :default nil
            :desc    "A classname for a la carte application of a classes on the tooltip element which is displaying alternate text after click."}
           {:name    text-on-click-duration
            :pred    int?
            :default 2000
            :desc    "When `:-text-on-click` is utilized, this milliseconds value will control the duration of the `:-text-on-click` value being used as the tooltip text."} ]}
  [{text                        :-text
    placement                   :-placement
    arrow?                      :-arrow?
    tooltip-class               :-tooltip-class
    text-on-click               :-text-on-click
    text-on-click-tooltip-class :-text-on-click-tooltip-class
    :or                         {placement :auto
                                 arrow?    true}}]

  (when-let [tooltip-text (valid-tooltip-text text)] 
    (let [tooltip-arrow? (if (false? arrow?) false true)
          placement      (if-not (or (string? placement)
                                     (keyword? placement)
                                     (vector? placement))
                           :auto
                           placement)
          placement-kw   (or (maybe placement #(= % :auto))
                             (user-placement placement))
          opts           (keyed tooltip-text
                                placement-kw
                                tooltip-arrow?
                                tooltip-class)
          tt-id          (str "kushi-" (gensym))]
      (merge 
       {:data-kushi-ui-tooltip (name placement-kw)
        :on-mouse-enter        (partial append-tooltip! opts tt-id)}

       ;; Todo use when-let to validate text-on-click and normalize if vector
       (when-let [text-on-click (maybe-multiline-tooltip-text text-on-click)]
         {:on-click (fn [_]
                      (let [duration           (token->ms :$tooltip-reveal-on-click-duration)
                            tt-el              (dom/el-by-id tt-id)
                            tt-el-text-wrapper (dom/qs tt-el ".kushi-floating-tooltip-text-wrapper")
                            tt-el-text-span    (dom/qs tt-el ".kushi-floating-tooltip-text")
                            text-on-click-el   (js/document.createElement "span")]
                        (j/assoc! text-on-click-el "innerText" text-on-click)
                        (dom/add-class text-on-click-el "absolute-centered")
                        (dom/add-class tt-el text-on-click-tooltip-class)
                        (.appendChild tt-el-text-wrapper text-on-click-el)
                        (dom/add-class tt-el-text-span "invisible")
                        (js/setTimeout (fn [_] 
                                         (.removeChild tt-el-text-wrapper text-on-click-el)
                                         (dom/remove-class tt-el text-on-click-tooltip-class)
                                         (dom/remove-class tt-el-text-span "invisible"))
                                       duration)))})))))
