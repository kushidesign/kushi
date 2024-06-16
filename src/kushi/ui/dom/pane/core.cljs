(ns kushi.ui.dom.pane.core
  (:require [applied-science.js-interop :as j]
            [fireworks.core :refer [? !? ?- !?- ?-- !?-- ?> !?> ?i !?i ?l !?l ?let ?let ?trace ?log !?log ?log- !?log- ?pp !?pp ?pp- !?pp-]]
            [clojure.string :as string]
            [domo.core :as domo] ;; Import this to create defclasses
            [goog.functions]
            [goog.string]
            [kushi.core :refer (keyed)]
            [kushi.ui.dom.pane.placement :refer [el-plc og-placement
                                                 owning-el-rect-cp pane-plc
                                                 placement-css-custom-property
                                                 updated-pane-placement]]
            [kushi.ui.dom.pane.shared :refer [pane-classes stock-pane-types]]
            [kushi.ui.dom.pane.toast :refer [append-toast!]]
            [kushi.ui.util :as util :refer [as-str maybe nameable?]]))


(defn maybe-multiline-tooltip-text [v]
  (let [ret (if (or (vector? v)
                    (array? v)
                    (and (not (string? v))
                         (seq v)))
              (string/join "<br>" v)
              v)]
    ret))


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
                         :pane-el      el
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
          (util/backtics->stringified-html
           (str "<div class=\"kushi-tooltip-text-wrapper\""
                "style=\"" style "\">"
                "<span class=\"kushi-tooltip-text\">"
                (maybe-multiline-tooltip-text tooltip-text)
                "</span>"
                "</div>")))))

(defn- adjust-client-rect
  [{:keys [top bottom left right width height] :as owning-el-rect}
   dialog-el]
  (let [{dtop  :top
         dleft :left} (domo/client-rect dialog-el)
        top           (- top dtop)
        bottom        (- bottom dtop)
        left          (- left dleft)
        right         (- right dleft)
        x-center   (domo/round-by-dpr (- right (/ width 2)))
        y-center   (domo/round-by-dpr (- bottom (/ height 2)))]
    (merge owning-el-rect
           (keyed top left bottom right x-center y-center))))


(defn- append-pane-el!
  [{:keys [el 
           id
           pane-type 
           user-rendering-fn
           owning-el
           dialog-el
           metrics? 
           placement-kw
           new-placement-kw]
    :as append-tt-opts}]

  ;; Set the innerHTML / or tooltip text
  (cond (= pane-type :tooltip)
        (tooltip-text-html! append-tt-opts))

  (let [txy          (txy append-tt-opts)
        pane-classes (pane-classes append-tt-opts)]
    (doto el
      (.setAttribute "data-kushi-ui" "pane")

      ;; TODO swap this in once kushi.core/defcss is ready
      ;; (.setAttribute "data-kushi-ui-pane-placement" placement)

      (.setAttribute "id" id)
      (.setAttribute "style" txy)
      (.setAttribute "class" pane-classes)))
  
  ;; Append pane el to the <body> 
  (.appendChild (or dialog-el js/document.body) el)

  ;; Render contents if popover
  (when (contains? #{:popover} pane-type)
    (user-rendering-fn el)))

(defn- edge-threshold 
  [{:keys [pane-type user-pane-class user-pane-style]}]
        ;; TOOD - Use some koind of dpi calc for edge-threshold.
        ;; Convert to px if user supplies ems or rems.
  (let [cp (str "--"
                (case pane-type
                  :popover "popover"
                  :tooltip "tooltip"
                  "pane")
                "-flip-viewport-edge-threshold")
        el (doto (js/document.createElement "div")
             (.setAttribute "style" user-pane-style)
             (.setAttribute "class" (str (when-let [cls user-pane-class]
                                           (cond (string? cls)
                                                 cls
                                                 (util/class-coll? cls)
                                                 (string/join " " cls)))
                                         " invisible absolute offscreen")))]
    (.appendChild js/document.body el)
    (let [ret (domo/css-custom-property-value el cp)]
      (.removeChild js/document.body el)
      (some-> ret js/parseInt))))

(defn- append-pane!*
  "A pane is given an initial position based on screen-quadrant of the owning
   element, or user-supplied `-placement` attr. If it is offscreen, it is then
   given a new placement and position which may include a shift on the x or y
   axis in order to keep it wholly in the veiwport."
  ;; use :keys here ?
  [{placement-kw :placement-kw
    arrow?       :arrow?
    owning-el    :owning-el
    dialog-el    :dialog-el
    pane-type    :pane-type
    :or          {pane-type :pane}
    :as          opts}
   id]

;; Fireworks error - cannot convert symbol to string
;; (? opts)
  
;; Fireworks error - 1 is not ISeqable
;; (?pp (seq [1 2 3]))


  ;; 1) Pre-calculate and append pane
  ;; Calculate an initial placement and append a pane element to the dom.
  ;; If the owning element is beyond the edge-threshold, the pane will
  ;; be assigned a new placement, but only if the value of :-pane-placement
  ;; is something other than :auto.

  ;; TODO - optimize for auto placement
  ;; --------------------------------------------------------------------------------
  (let [pane-type       (or (maybe pane-type stock-pane-types)
                            (maybe pane-type nameable?)
                            :pane)
        arrow?          (if (false? arrow?) false true)
        owning-el-rect* (domo/client-rect owning-el)
        owning-el-rect  (or (some->> dialog-el
                                     (adjust-client-rect owning-el-rect*))
                            owning-el-rect*)
        opts            (assoc opts
                               :pane-type
                               pane-type
                               :owning-el-rect
                               owning-el-rect)
        viewport        (domo/viewport)
        edge-threshold  (edge-threshold opts)
        owning-el-vpp   (el-plc viewport
                                owning-el
                                edge-threshold)
        placement-kw    (og-placement placement-kw
                                      owning-el
                                      owning-el-vpp)
        tt-pos-og       (pane-plc placement-kw)
        el              (js/document.createElement "div")
        append-tt-opts  (merge opts
                               (keyed el
                                      id
                                      placement-kw
                                      tt-pos-og 
                                      arrow?))]
    (append-pane-el! (merge append-tt-opts
                            {:metrics? true
                             :id       (str "_kushi-metrics_" id)}))

    ;; 2) Measure and adjust
    ;; Second, detect if the pane falls outside the viewport
    ;; If the pane needs to be "shifted" along x or y axis to move it
    ;; back inside viewport, get an updated style value and reset the style.
    
    ;; TODO - if needed, viewport-padding should be handled in css-land.
    ;; Add some kind of :tl or :br key to opts below, then use padding value
    ;; in css land.
    ;; -----------------------------------------------------------------------------
    (let [
          vpp                  (el-plc viewport el 0)
          new-placement-kw     (updated-pane-placement
                                (merge tt-pos-og
                                       (keyed vpp placement-kw)))

          tt-pos               (pane-plc new-placement-kw)

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
          
          _                    (.removeChild (or dialog-el js/document.body) el)
          
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

      (append-pane-el! append-tt-opts-part2)
      
      ;; leave off for now?
      #_(when adjust?
        (.setAttribute el "style" translate-xy-style))
      

      ;; 4) Display
      ;; Remove `.invisible` class, which will fade-in the pane via
      ;; css transition setting, if desired. 
      (let [arrow-el (when (and arrow?
                                (not (:corner-plc? tt-pos)))
                       (let [arrow-el (js/document.createElement "div")]
                         (doto arrow-el
                           (.setAttribute "class" "kushi-pane-arrow"))
                         (.appendChild el arrow-el)))]
        (js/window.requestAnimationFrame
         (fn [_]
           (let [t      (-> opts :pane-type as-str)
                 offset (str "max(var(--" t "-offset), 0px)")]
             el
             (domo/remove-class! el "invisible")
             (domo/set-css-var! el "--offset" offset)
             (domo/set-style! el "scale" "1")) ))))))

(declare remove-pane!)

(defn remove-pane-if-clicked-outside!
  [owning-el pane-id pane-type e] 
  (let [skip? (let [el (domo/et e)]
                (boolean (or (domo/has-class?
                              el 
                              ".kushi-popover")
                             (domo/nearest-ancestor
                              el
                              ".kushi-popover"))))]  
    (when-not skip?
      (remove-pane! owning-el pane-id pane-type e))))

(defn update-pane-placement-class!
  [pane-el placement-class placement-kw _]
  (js/window.requestAnimationFrame
   (fn []
     (let [vpp                 (el-plc (domo/viewport) pane-el 0)
           tt-pos-og           (pane-plc placement-kw)
           new-placement-kw    (updated-pane-placement
                                (merge tt-pos-og
                                       (keyed vpp placement-kw)))
           new-placement-class (some->> new-placement-kw
                                        name
                                        (str "kushi-pane-"))]
       (when (and placement-class
                  new-placement-class
                  (not= placement-class
                        new-placement-class))
         (domo/remove-class! pane-el placement-class)
         (domo/add-class! pane-el new-placement-class))))))


;; TODO  - Make sure this is getting removed properly
(def update-pane-placement!
  (goog.functions.debounce
   (fn [owning-el pane-id dialog-el]
      (when-let [pane-el (domo/el-by-id pane-id)]
       (if-not (domo/qs (str "[aria-controls=\"" pane-id "\"]")) 
         (domo/set-style! pane-el "display" "none")
         (let [
               owning-el-rect* (domo/client-rect owning-el)
               owning-el-rect  (or (some->> dialog-el
                                            (adjust-client-rect owning-el-rect*))
                                   owning-el-rect*)
               m       
               (owning-el-rect-cp owning-el-rect)

               ms      
               (domo/duration-property-ms pane-el "transition-duration")

               coll   
               (keep #(re-find #"^kushi-pane-([a-z]*)$" %)
                     (.-classList pane-el))
               [placement-class placement-kw*]
               (some-> coll (nth 0 nil))

               placement-kw
               (some->  placement-kw* keyword)]

           (doseq [[k v] m]
             (domo/set-css-var! pane-el k v))

           (js/setTimeout (partial update-pane-placement-class!
                                   pane-el
                                   placement-class
                                   placement-kw)
                          ms)))))
   100))

(defn- escape-pane!
  "If Escape key is pressed when pane is active, dispatch `remove-pane`.
   to remove pane element from DOM.
   Also removes the `mouseleave` event listener on owning element."
  [owning-el pane-id pane-type e]
  ;; TODO ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
  ;; Add conditionality around pane-type for dismissal
  (when-not e.defaultPrevented
    (when (or (and (= :tooltip pane-type)
                   (= e.type "scroll")) 
              (= e.key "Escape"))
      (when owning-el
        (remove-pane! owning-el pane-id pane-type nil)
        (when (= :tooltip pane-type)
         (.removeEventListener owning-el
                               "mouseleave"
                               (partial remove-pane!
                                        owning-el
                                        pane-id
                                        pane-type)
                               #js {"once" true}))))))


(defn remove-pane!
  "Removes pane from dom.

   For tooltips, removes :aria-describedby on owning element

   For tooltips, removes the pane instance-specific `keydown`
   and scroll events on window.
   
   For popovers,  removes the pane instance-specific `click`,
   `resize`, and `scroll` events on window."
  [owning-el pane-id pane-type e]
  ;; TODO ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
  ;; Add conditionality around pane-type for dismissal, if necessary

  (let [dialog-el        (domo/nearest-ancestor owning-el "dialog")
        el-to-be-removed (some->> pane-id domo/el-by-id)
        toast?           (= pane-type :toast)
        toast-slot       (when toast? (.-parentNode el-to-be-removed))]

    ;; TODO ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    ;; Document the use case for removing child from dialog-el

    ;; TODO - this warning should fire if event listener has not been properly removed
    ;; Leave this commented out:
    ;; (when-not el-to-be-removed
    ;;   (js/console.warn "[kushi.ui.dom.pane.core/remove-pane!]\nAttempt to .removeChild with a non-existing child element."))

    (some->> el-to-be-removed
             (.removeChild  (or toast-slot 
                                (or dialog-el
                                    js/document.body))
                            el-to-be-removed))

    ;; Popovers
    (when (= :popover pane-type)
      (let [update-placement-fn #(update-pane-placement!
                                  owning-el
                                  pane-id
                                  dialog-el)]
        (do
          (.removeEventListener js/window
                                "click"
                                (partial remove-pane-if-clicked-outside!
                                            owning-el
                                            pane-id
                                            pane-type)
                                #js {:once true}))
        (.removeEventListener js/window "resize" update-placement-fn)
        (.removeEventListener js/window "scroll" update-placement-fn))

      (when-let [owning-el (domo/qs (str "[aria-controls=\"" pane-id "\"]"))]
        (domo/remove-attribute! owning-el :aria-controls)
        (domo/remove-attribute! owning-el :aria-haspopup)
        (domo/remove-attribute! owning-el :aria-expanded)))


    ;; Tooltips
    (when (= :tooltip pane-type)
      (domo/remove-attribute! owning-el :aria-describedby)
      (.removeEventListener owning-el
                            "mouseleave"
                            (partial remove-pane! owning-el pane-id pane-type)
                            #js {"once" true}))
    (.removeEventListener js/window
                          "keydown"
                          (partial escape-pane! owning-el pane-id pane-type)
                          #js {"once" true})
    (.removeEventListener js/window
                          "scroll"
                          (partial escape-pane! owning-el pane-id pane-type)
                          #js {"once" true})))


(defn observe-pane! [pane-id]
  (goog.functions.debounce
   (fn [_ observer]
     (when-not (domo/qs (str "[aria-controls=\"" pane-id "\"]"))
       (.disconnect observer)
       (.remove (domo/el-by-id pane-id))))
   30))

(defn set-popover-focus! [pane-id]
  (let [focusables*     "button, [href], input, select, textarea, [tabindex]:not([tabindex=\"-1\"])"
        pane            (domo/el-by-id pane-id)
        focusables      (.querySelectorAll pane focusables*)
        focusables-len  (.-length focusables)
        first-focusable (j/get focusables 0)
        last-focusable  (j/get focusables (dec focusables-len))
        second-to-last-focusable  (when (> focusables-len 2)
                                    (j/get focusables (- (.-length focusables) 2)))]
    (when (pos? focusables-len)
      (.addEventListener pane
                         "keydown"
                         (fn [e]
                           (when (or (= "Tab" e.key) (= 9 e.keyCode))
                             (cond
                               (and e.shiftKey
                                    (= js/document.activeElement first-focusable))
                               (do (.focus last-focusable)
                                   (.preventDefault e))

                               (and e.shiftKey
                                    (= js/document.activeElement last-focusable)
                                    second-to-last-focusable)
                               (do (.focus second-to-last-focusable)
                                   (.preventDefault e))

                               (= js/document.activeElement last-focusable)
                               (do (.focus first-focusable)
                                   (.preventDefault e))))))
      (.focus first-focusable))))

(defn append-pane!
  ([opts e]
  (append-pane! opts nil e))
  ([opts pane-id e]
   ;; We need to use cet here (.currentEventTarget), in order
   ;; To prevent mis-assignment of ownership of the pane to
   ;; A child element of the intended owning el. 
   (when true
     #_(or (and (= "mousedown" (.-type e))
                (= 0 (.-button e)))
           #_(= "click" (.-type e)))
     (let [owning-el           (domo/cet e)
           dialog-el           (domo/nearest-ancestor owning-el "dialog")
         ;; TODO - should this be "kushi-pane-*" ?
           pane-id             (or pane-id (str "kushi-" (gensym)))
           pane-type           (:pane-type opts)
           existing-popover    (j/get owning-el "ariaHasPopup")
           existing-tooltip-id (.getAttribute owning-el "aria-describedby")
           existing-tooltip?   (domo/has-class? (domo/el-by-id existing-tooltip-id)
                                                "kushi-tooltip")
           opts                (merge opts (keyed owning-el dialog-el))]

       (when-not (or (and (= pane-type :tooltip)
                          existing-popover)
                     (and (= pane-type :popover)
                          existing-popover))
         
       ;; Adding `aria-describedby` for tooltips
         (when (= pane-type :tooltip)
           (domo/set-attribute! owning-el :aria-describedby pane-id))

       ;; Appending pane
         (if (= pane-type :toast)
           (append-toast! (assoc opts :owning-el owning-el)
                          pane-id)
           (append-pane!* (assoc opts :owning-el owning-el)
                          pane-id))


       ;; Tooltip-specific mouseleave
         (when (= pane-type :tooltip)
           (.addEventListener owning-el
                              "mouseleave"
                              (partial remove-pane! owning-el pane-id pane-type)
                              #js {"once" true}))

       ;; Popover-specific aria attributes, listeners, and focus-trap
         (when (= pane-type :popover)
           (when existing-tooltip?
             (remove-pane! owning-el existing-tooltip-id :tooltip nil))
           (domo/set-attribute! owning-el :aria-controls pane-id)
           (domo/set-attribute! owning-el :aria-haspopup "dialog")
           (domo/set-attribute! owning-el :aria-expanded true)
           (js/window.requestAnimationFrame 
            #(.addEventListener js/window
                                "click"
                                (partial remove-pane-if-clicked-outside!
                                            owning-el
                                            pane-id
                                            pane-type)
                                #js {:once true})
            (.addEventListener js/window
                               "scroll"
                               #(update-pane-placement! owning-el pane-id dialog-el))
            (.addEventListener js/window
                               "resize"
                               #(update-pane-placement! owning-el pane-id dialog-el))


          ;; This will set focus on first focusable element within pane
            (set-popover-focus! pane-id)

          ;; This will remove pane from dom if owning element goes away
            (let [mo (new js/MutationObserver (observe-pane! pane-id))]
              (.observe mo
                        (domo/el-by-id pane-id)
                        #js{:attributes true}))))

       ;; This will auto-dismiss pane, for toasts (default) and popovers (opt-in).
         (js/window.requestAnimationFrame 
          #(when (:auto-dismiss? opts)
             (js/setTimeout (partial remove-pane! owning-el pane-id pane-type)
                            #_(domo/duration-property-ms  "popover-auto-dismiss-duration")
                            5000)))

       ;; Additional listeners for escaping panes
         (when-not (= pane-type :toast)
           (.addEventListener js/window
                              "keydown"
                              (partial escape-pane! owning-el pane-id pane-type)
                              #js {"once" true})
           (.addEventListener js/window
                              "scroll"
                              (partial escape-pane! owning-el pane-id pane-type)
                              #js {"once" true})))))))

