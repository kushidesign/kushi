(ns kushi.ui.dom.pane.toast
  (:require-macros
   [kushi.core :refer [utilize]])
  (:require
   [clojure.string :as string]
   [goog.string]
   [domo.core :as domo]
   [kushi.ui.util :as util :refer [as-str]]
   [kushi.ui.dom.pane.placement :refer [placement-kws-hydrated]]
   [kushi.ui.dom.pane.shared :refer [pane-classes]]
   [goog.functions]))

(defn- toast-slot-calc [op x?]
  (str "calc(0px "
       (name op)
       " (100% + var(--_p" (if x? "i" "b") ")))"))

(defn toast-slot-tx [k]
  (cond
    (contains? #{:trc :rt :r :rb :brc} k) (toast-slot-calc :+ true)
    (contains? #{:tlc :lt :l :lb :blc} k) (toast-slot-calc :- true)
    :else
    "0px"))

(defn toast-slot-ty [k]
  (cond
    (contains? #{:tlc :tl :t :tr :trc} k) (toast-slot-calc :- false)
    (contains? #{:blc :bl :b :br :brc} k) (toast-slot-calc :+ false)
    :else
    0))

(defn toast-slot-flex-direction [k]
  (if (contains? #{:lt :tlc :tl :t :tr :trc :rt} k)
    "column-reverse"
    "column"))

(defn toast-slot-padding [k]
  (let [pb "var(--_pb)"
        pi "var(--_pi)"
        p  (cond
             (contains? #{:rb :br :brc} k) [0 pi pb 0]
             (contains? #{:rt :tr :trc} k) [pb pi 0 0]
             (contains? #{:lt :tl :tlc} k) [pb 0 0 pi]
             (contains? #{:lb :bl :blc} k) [0 0 pb pi]
             :else
             (case k
               :t   [pb 0 0 0]
               :b   [0 0 pb 0]
               :r   [0 pi 0 0]
               :l   [0 0 0 pi]))]
    (some->> p
             (string/join " "))))

(def toast-slot-placement-classes
  {:lt  "top-left-corner-inside"
   :tlc "top-left-corner-inside"
   :tl  "top-left-corner-inside"
   :t   "top-inside"
   :tr  "top-right-corner-inside"
   :trc "top-right-corner-inside"
   :rt  "top-right-corner-inside"
   :r   "right-inside"
   :rb  "bottom-right-corner-inside"
   :brc "bottom-right-corner-inside"
   :br  "bottom-right-corner-inside"
   :b   "bottom-inside"
   :bl  "bottom-left-corner-inside"
   :blc "bottom-left-corner-inside"
   :lb  "bottom-left-corner-inside"
   :l   "left-inside"})




(defn update-toast-slot-dimensions! [toast-slot-el]
  (let [toasts            (.-children toast-slot-el)
        toasts-sum-height (str (apply + (for [t toasts] (.-clientHeight t)))
                               "px")
        num-toasts        (.-length toasts)
        sum-gap-height    (str "(var(--toast-slot-gap, 1rem) * "
                               (dec num-toasts)
                               ")")
        padding-top       (domo/computed-style toast-slot-el "padding-top")
        padding-bottom    (domo/computed-style toast-slot-el "padding-bottom")
        padding-block-max (str "max(" padding-top ", " padding-bottom ")")]
    (domo/set-style! toast-slot-el
                     "height"
                     (str "calc("
                          toasts-sum-height
                          " + "
                          padding-block-max
                          " + "
                          sum-gap-height
                          ")"))))

(declare dismiss-toast!)

(defn toast-slot-cleanup!
  "If Escape key is pressed when a toast slot is active, remove the most
   recent toast. If most recent toast is the only toast, remove the toast-slot
   and remove the associated event listener."
  [slot]
  (if (some-> slot .-children .-length zero?)
    (do (.remove slot)
        (.removeEventListener js/window
                              "keydown"
                              dismiss-toast!))
    (update-toast-slot-dimensions! slot)))

(defn dismiss-toast!
  "If Escape key is pressed when a toast slot is active, remove the most
   recent toast. If most recent toast is the only toast, remove the toast-slot
   and remove the associated event listener."
  [e]
  (when-not e.defaultPrevented
    (when (= e.key "Escape")
      (when-let [slot (domo/qs ".kushi-toast-slot")]
        (.remove (.-firstChild slot))
        (toast-slot-cleanup! slot)))))


(defn toast-slot-el 
  [{:keys [placement-kw reduced-motion?]} 
   placement-as-str]
  (let [toast-slot-el    (js/document.createElement "ol")
        placement-class  (get toast-slot-placement-classes placement-kw)]
    (doto toast-slot-el
      (.setAttribute "data-kushi-ui-toast-slot" placement-as-str)
      (.setAttribute 
       "style"
       (domo/css-style-string 
        (merge {:padding        (toast-slot-padding placement-kw)
                :position       "fixed"
                :flex-direction (toast-slot-flex-direction placement-kw)}
               (when reduced-motion?
                 {:scale 1}))))
      (.setAttribute
       "class"
       (string/join " "
                    [placement-class
                     "kushi-toast-slot"])))
    (.appendChild js/document.body toast-slot-el)))


(defn append-toast!
  [{:keys [user-rendering-fn
           placement-kw
           slide-in?]
    :as   opts} 
   id]

  ;; TODO move kushi.ui.dom.pane.placement/placement-kws-hydrated into this ns?
  ;; It is not used anywhere else
  (let [placement-as-str (as-str (get placement-kws-hydrated
                                      placement-kw
                                      nil))
        existing         (domo/qs-data= "kushi-ui-toast-slot" 
                                        placement-as-str)
        toast-slot-el    (or existing 
                             (toast-slot-el opts placement-as-str))
        toast-el         (js/document.createElement "li")
        pane-classes     (pane-classes opts)]

    (when-not existing
      (.addEventListener js/window
                         "keydown"
                         dismiss-toast!))

    (doto toast-el
      (.setAttribute "data-kushi-ui" "toast")

      ;; TODO swap this in once defcss is ready
      ;; (.setAttribute "data-kushi-ui-pane-placement" placement)
      (.setAttribute "id" id)
      (.setAttribute "style"
                     (domo/css-style-string
                      {:--_tx (if slide-in? 
                                (toast-slot-tx placement-kw)
                                "0px")
                       :--_ty (if slide-in?
                                (toast-slot-ty placement-kw)
                                "0px")}))
      (.setAttribute "class" pane-classes))

    (.appendChild toast-slot-el toast-el)
    
    (user-rendering-fn toast-el)
    
    (js/window.requestAnimationFrame
     (fn [_]
       (let []
         (domo/remove-class! toast-el "invisible")
         (domo/set-style! toast-el "scale" "1")
         (domo/set-css-var! toast-el "--_tx" "0px")
         (domo/set-css-var! toast-el "--_ty" "0px")
         (update-toast-slot-dimensions! toast-slot-el))))))
