(ns kushi.playground.state
  (:require
   [kushi.ui.dom :as dom]
   [applied-science.js-interop :as j]
   [reagent.core :as r]))

(defonce *state
  (r/atom {
           ;;  :init-focused-component "button"
           :kushi-components-indexes []
           :components-expanded? false
           :snippet-by-component {}
           :dev-mode?            false
           :option-radios        {}
           :controls-by-type     {}}))

(def *focused-component (r/atom nil))

(def *focused-section (r/atom :kushi-components))

(def *expanded-sections (r/atom #{}))

(def *visible-sections (r/atom {}))

(defn focused? [fname] (= @*focused-component fname))

(defn section-focused? [x] (= @*focused-section x))

#_(defn initial-focus? [fname]
  (when (= (:init-focused-component @*state) fname)
    (swap! *state assoc :focused-component fname)
    (swap! *state assoc :init-focused-component nil)
    true))

(defn set-focused-component! [x]
  #_(js/console.log :set-focused-component! x)
  (j/call js/history :pushState  #js {} "" (str "#" x))
  (reset! *focused-component x) )

(defn nav! [x]
  #_(js/console.log :nav!)
  (let [el (dom/el-by-id x)
        expanded? (dom/has-class? el "kushi-collapse-expanded")]
    (swap! *expanded-sections (if expanded? conj disj) x)
    #_(js/console.log @*expanded-sections))
  (when (when-not (focused? x) x)
    (set-focused-component! x)))


