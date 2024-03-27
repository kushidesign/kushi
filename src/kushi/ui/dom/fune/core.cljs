(ns kushi.ui.dom.fune.core
  (:require
   [fireworks.core :refer [? ?? ??? !? ?> !?> ]]
   [applied-science.js-interop :as j]
   [clojure.string :as string]
   [goog.string]
   [domo.core :as domo]
   ;; Import this to create defclasses
   [kushi.core :refer (keyed)]
   [kushi.ui.util :as util :refer [maybe nameable? as-str]]
   [kushi.ui.dom.fune.placement :refer [el-plc
                                        fune-plc
                                        updated-fune-placement
                                        og-placement
                                        placement-css-custom-property
                                        owning-el-rect-cp]]
   [goog.functions]))

(def stock-fune-types #{:fune :tooltip :popover #_:hover-board #_:context-menu})
        

(defn fune-classes
  [{:keys [fune-class 
           arrow?
           placement-kw
           new-placement-kw
           fune-type
           metrics?]}]
  (let [fune-type-class (some-> fune-type
                                (maybe #(not= :fune %))
                                (maybe stock-fune-types)
                                as-str)]
    (string/join
     " "
     ["kushi-fune"
      (some->> fune-type-class (str "kushi-"))
      "invisible" 
      (some->> (if metrics? 
                 placement-kw
                 new-placement-kw)
               name
               (str "kushi-fune-"))
      (when-not arrow? "kushi-fune-arrowless")
      (some-> fune-class (maybe nameable?) as-str)])))


(defn maybe-multiline-tooltip-text [v]
  (if (or (vector? v)
          (array? v)
          (and (not (string? v))
               (seq v)))
    (string/join "<br>" v)
    v))


(defn- txy 
  [{:keys [el
           placement-kw
           tt-pos-og
           metrics?
           translate-xy-style]
    :as   append-tt-opts}]
  (let [txy (or translate-xy-style
                (placement-css-custom-property
                 (merge append-tt-opts
                        {:corner-plc?  (:corner-plc? tt-pos-og)
                         :fune-el      el
                         :placement-kw placement-kw})))]
    (str txy
         "; "
         (when metrics? 
           (domo/css-style-string
            {:scale               :1!important
             :transition-property :none!important})))))

(defn- tooltip-text-html!
  [{:keys [el tooltip-text]}]
  (let [style (domo/css-style-string
               {:position        :relative
                :display         :flex
                :justify-content :center
                :align-items     :center
                :width           :fit-content})]
    (set! (.-innerHTML el)
          (str "<div class=\"kushi-tooltip-text-wrapper\""
                    "style=\"" style "\">"
                 "<span class=\"kushi-tooltip-text\">"
                   (maybe-multiline-tooltip-text tooltip-text)
                 "</span>"
               "</div>"))))


(defn- append-fune-el!
  [{:keys [el 
           id
           fune-type 
           user-rendering-fn
           metrics? 
           placement-kw
           new-placement-kw]
    :as append-tt-opts}]

  ;; Set the innerHTML / or tooltip text
  (cond (= fune-type :tooltip)
        (tooltip-text-html! append-tt-opts)
        ;; (= fune-type :popover)
        ;; (user-rendering-fn el)
        )
  ;; popover-close-button-html! append-tt-opts
  ;; Set the class and id of the fune el
  ;; TODO
  ;; make sure translate-xy-style is always map, then merge with below
  (let [txy          (txy append-tt-opts)
        fune-classes (fune-classes append-tt-opts)
        placement    (some->> (if metrics? 
                                placement-kw
                                new-placement-kw)
                              name)]
    (doto el
      (.setAttribute "data-kushi-ui" "fune")

      ;; TODO swap this in !
      ;; (.setAttribute "data-kushi-ui-fune-placement" placement)

      (.setAttribute "id" id)
      (.setAttribute "style" txy)
      (.setAttribute "class" fune-classes)))
  
  ;; Append fune el to the <body> 
  (.appendChild js/document.body el)
  (when (contains? #{:popover} fune-type)
    (user-rendering-fn el)))


(defn- append-fune!*
  "A fune is given an initial position based on screen-quadrant of the owning
   element, or user-supplied `-placement` attr. If it is offscreen, it is then
   given a new placement and position which may include a shift on the x or y
   axis in order to keep it wholly in the veiwport."
  [{placement-kw :placement-kw
    arrow?  :arrow?
    owning-el    :owning-el
    fune-type    :fune-type
    :as opts
    :or {fune-type :fune}}
   id]

  ;; 1) Pre-calculate and append fune
  ;; Calculate an initial placment and append a fune element to the
  ;; dom. If the owning element is beyond the edge-threshold, the fune will
  ;; be assigned a new placement, but only if the value of :-fune-placement
  ;; is something other than :auto.

  ;; TODO - optimize for auto placement
  ;; --------------------------------------------------------------------------------
  (let [fune-type       (or (maybe fune-type stock-fune-types)
                            (maybe fune-type nameable?)
                            :fune)
        arrow?     (if (false? arrow?) false true)
        opts            (assoc opts
                               :fune-type
                               fune-type
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
                                      placement-kw
                                      tt-pos-og 
                                      arrow?))]
    (append-fune-el! (merge append-tt-opts
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
    (let [
          vpp                  (el-plc viewport el 0)
          new-placement-kw     (updated-fune-placement
                                (merge tt-pos-og
                                       (keyed vpp placement-kw)))

          tt-pos               (fune-plc new-placement-kw)

          new-placement?       (not= placement-kw new-placement-kw)    

          ;;; TODO Do we really need this?
          adjust?               new-placement?

          ;;; TODO Do we really need this?
          translate-xy-style   (when new-placement?
                                 (placement-css-custom-property 
                                  ;; need to be different than append-tt-opts?
                                  (merge opts
                                         {:corner-plc?  (:corner-plc? tt-pos)
                                          :el           el
                                          :arrow?  arrow?
                                          :adjust?      true
                                          :placement-kw new-placement-kw}) ))
          
          ;; 3) Remove dummy and append new element
          ;; ----------------------------------------------------------------------
          
          _                    (.removeChild js/document.body el) ;; move down
          
          el                   (js/document.createElement "div")

          append-tt-opts-part2 (merge opts
                                      (keyed el
                                             id 
                                             arrow?
                                             
                                             ;; is this right?
                                             ;; why not new-placement-kw
                                             placement-kw 
                                             
                                             ;; is this right or tt-pos better?
                                             tt-pos-og
                                             new-placement-kw 
                                             ))
          ]

      (append-fune-el! append-tt-opts-part2)
      
      ;; leave off for now?
      #_(when adjust?
        (.setAttribute el "style" translate-xy-style))
      

      ;; 4) Display
      ;; Remove `.invisible` class, which will fade-in the fune via
      ;; css transition setting, if desired. 
      (let [arrow-el (when (and arrow?
                                (not (:corner-plc? tt-pos)))
                       (let [arrow-el (js/document.createElement "div")]
                         (doto arrow-el
                           (.setAttribute "class" "kushi-fune-arrow"))
                         (.appendChild el arrow-el)))]
        (js/window.requestAnimationFrame
         (fn [_]
           (let [t      (-> opts :fune-type as-str)
                 offset (str "max(var(--" t "-offset), 0px)")]
             (domo/remove-class! el "invisible")
             (domo/set-css-var! el "--offset" offset)
             (domo/set-style! el "scale" "1")) ))))))

(declare remove-fune!)

(defn remove-fune-if-clicked-outside!
  [owning-el fune-id fune-type e] 
  (let [skip? (let [el (domo/et e)]
                (boolean (or (domo/has-class?
                              el 
                              ".kushi-popover")
                             (domo/nearest-ancestor
                              el
                              ".kushi-popover"))))]  
    (when-not skip?
      (remove-fune! owning-el fune-id fune-type e))))

(defn update-fune-placement-class!
  [fune-el placement-class placement-kw _]
  (js/window.requestAnimationFrame
   (fn []
     (let [vpp                 (el-plc (domo/viewport) fune-el 0)
           tt-pos-og           (fune-plc placement-kw)
           new-placement-kw    (updated-fune-placement
                                (merge tt-pos-og
                                       (keyed vpp placement-kw)))
           new-placement-class (some->> new-placement-kw
                                        name
                                        (str "kushi-fune-"))]
       (when (and placement-class
                  new-placement-class
                  (not= placement-class
                        new-placement-class))
         (domo/remove-class! fune-el placement-class)
         (domo/add-class! fune-el new-placement-class))))))

;; Make sure this is getting removed properly
;; TODO maybe batch the 2 different adjustments into one
(def update-fune-placement!
  (goog.functions.debounce
   (fn [el fune-id]
       (when-let [fune-el (domo/el-by-id fune-id)]
         (let [m       
               (-> el domo/client-rect owning-el-rect-cp)

               ms      
               (domo/duration-property-ms fune-el "transition-duration")

               coll   
               (keep #(re-find #"^kushi-fune-([a-z]*)$" %)
                     (.-classList fune-el))
               [placement-class placement-kw*]
               (some-> coll (nth 0 nil))

               placement-kw
               (some->  placement-kw* keyword)]

          (doseq [[k v] m]
            (domo/set-css-var! fune-el k v))

          (js/setTimeout (partial update-fune-placement-class!
                                  fune-el
                                  placement-class
                                  placement-kw)
                         ms))))
   300))

(defn- escape-fune!
  "If Escape key is pressed when fune is active, dispatch `remove-fune`.
   to remove fune element from DOM.
   Also removes the `mouseleave` event listener on owning element."
  [owning-el fune-id fune-type e]
  ;; TODO ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
  ;; Add conditionality around fune-type for dismissal
  (when-not e.defaultPrevented
    (when (or (= e.type "scroll") 
              (= e.key "Escape"))
      (when owning-el
        (remove-fune! owning-el fune-id fune-type nil)
        (when (= :tooltip fune-type)
         (.removeEventListener owning-el
                               "mouseleave"
                               (partial remove-fune!
                                        owning-el
                                        fune-id
                                        fune-type)
                               #js {"once" true}))))))


(defn remove-fune!
  "Removes fune from dom.
   Removes :aria-describedby on owning element.
   Removes the fune instance-specific `keydown` event on window."
  [owning-el fune-id fune-type e]
  ;; TODO ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
  ;; Add conditionality around fune-type for dismissal, if necessary
  (!?> "Removing fune!")
  (some->> fune-id
           domo/el-by-id
           (.removeChild js/document.body))

  (when (= :popover fune-type)
    (.removeEventListener js/window
                          "click"
                          (partial remove-fune-if-clicked-outside!
                                   owning-el
                                   fune-id
                                   fune-type))
    (.removeEventListener js/window
                          "resize"
                          #(update-fune-placement! owning-el fune-id))

    (!?> 'removing-attributes!)
    (when-let [owning-el (domo/qs (str "[aria-controls=\"" fune-id "\"]"))]
      (domo/remove-attribute! owning-el :aria-controls)
      (domo/remove-attribute! owning-el :aria-haspopup)
      (domo/remove-attribute! owning-el :aria-expanded)))

  (when (= :tooltip fune-type)
    (domo/remove-attribute! owning-el :aria-describedby)
    (.removeEventListener owning-el
                          "mouseleave"
                          (partial remove-fune! owning-el fune-id fune-type)
                          #js {"once" true}))
  (.removeEventListener js/window
                        "keydown"
                        (partial escape-fune! owning-el fune-id fune-type)
                        #js {"once" true})
  (.removeEventListener js/window
                        "scroll"
                        (partial escape-fune! owning-el fune-id fune-type)
                        #js {"once" true}))



(defn append-fune!
  ([opts e]
  (append-fune! opts nil e))
  ([opts fune-id e]
   ;; We need to use cet here (.currentEventTarget), in order
   ;; To prevent mis-assignment of ownership of the fune to
   ;; A child element of the intended owning el. 
  ;;  (js/console.clear)
   (!?> 'appending-fune)
   (let [owning-el        (domo/cet e)
         ;; TODO - should this be "kushi-fune-*" ?
         fune-id          (or fune-id (str "kushi-" (gensym)))
         fune-type        (:fune-type opts)
         existing-popover (and (= fune-type :popover)
                               (j/get owning-el "ariaHasPopup"))
         ]

     (when-not existing-popover
       (when (= fune-type :tooltip)
         (domo/set-attribute! owning-el :aria-describedby fune-id))

       (append-fune!* (assoc opts :owning-el owning-el)
                      fune-id)

       (when (= fune-type :tooltip)
         (.addEventListener owning-el
                            "mouseleave"
                            (partial remove-fune! owning-el fune-id fune-type)
                            #js {"once" true}))

       (when (= fune-type :popover)
         (!?> 'adding-attributes!)
         (domo/set-attribute! owning-el :aria-controls fune-id)
         (domo/set-attribute! owning-el :aria-haspopup "dialog")
         (domo/set-attribute! owning-el :aria-expanded true)
         (js/window.requestAnimationFrame 
          #(.addEventListener js/window
                              "click"
                              (partial remove-fune-if-clicked-outside!
                                       owning-el
                                       fune-id
                                       fune-type))
          (.addEventListener js/window
                             "resize"
                             #(update-fune-placement! owning-el fune-id))
         ;; TODO
         ;; add window resize debouce it
          ;; update oe-left etc on popover el
          ;; setup some kind of observer so if it goes offscreen, it flips
          ;; if owning el goes beyond threshold, it flips
          
          ;; on scroll, if outside, or threshold, same as above
          ;; on if owning el is offscreen, just leave it
          
          ;; move placement from kushi-fune-b to data-kushi-ui-pane-placement="b"
          ))
       (.addEventListener js/window
                          "keydown"
                          (partial escape-fune! owning-el fune-id fune-type)
                          #js {"once" true})
       (.addEventListener js/window
                          "scroll"
                          (partial escape-fune! owning-el fune-id fune-type)
                          #js {"once" true}))
     )))

