(ns kushi.playground.state
  (:require
   [kushi.css.media :refer [media]]
   [kushi.playground.components :refer [playground-components]]
   [domo.core :as domo]
   [applied-science.js-interop :as j]
   [reagent.core :as r]
   [reagent.ratom]))

;; (?trace (let [a 1 b 3]
;;           a))

(defonce *state
  (r/atom {
           ;;  :init-focused-component "button"
           :kushi-components-indexes []
          ;;  :focused-component    "button"
           :snippet-by-component {}
           :option-radios        {}
           :controls-by-type     {}}))

(defonce *md-or-smaller?
  (r/atom
   (not (let [md-breakpoints    (some-> media :md)
              [md-key md-value] (some-> md-breakpoints first)
              mql               (when-let
                                 [[md-key md-value]
                                  (when (and (or (string? md-key)
                                                 (keyword? md-key))
                                             (or (string? md-value)
                                                 (keyword? md-value)))
                                    [(name md-key) (name md-value)])]
                                  (let [mql (js/window.matchMedia (str "("
                                                                       md-key
                                                                       ": "
                                                                       md-value
                                                                       ")"))]
                                    (j/assoc!
                                     mql 
                                     :onchange
                                     #(do (reset! *md-or-smaller?
                                                  (not (.-matches %)))))))]
          (.-matches mql)))))

(defonce *dev-mode? (r/atom false))

(defonce *components-expanded? (r/atom false))

(defonce *focused-component (r/atom nil))

(def *focused-section (r/atom :kushi-components))

(def *expanded-sections (r/atom #{}))

(def *visible-sections (r/atom {}))

(defn focused? [fname] (= @*focused-component fname))

(defn section-focused? [x] (= @*focused-section x))

(defn set-focused-component! [x]
  #_(js/console.log :set-focused-component! x)
  (j/call js/history :pushState  #js {} "" (str "#" x))
  (reset! *focused-component x))

(defn nav! [x]
  #_(js/console.log :nav! x)
  (let [el (domo/el-by-id x)
        expanded? (domo/has-class? el "kushi-collapse-expanded")]
    (swap! *expanded-sections (if expanded? conj disj) x)
    #_(js/console.log @*expanded-sections))
  (when (when-not (focused? x) x)
    (set-focused-component! x)))


;; New June 2024 -----------------------------------------

(def ordered-playground-components-labels
  (mapv :label playground-components))

(def *playground (r/atom {:intersecting                   #{}
                          :playground-intro-intersecting? true
                          :mobile-sidenav-expanded?       false}))

(def playground-intro-intersecting?
  (reagent.ratom/reaction
   (:playground-intro-intersecting? @*playground)))

(defn set-first-intersecting! [m]
  (some->> m
           :intersecting
           (sort-by #(.indexOf ordered-playground-components-labels %))
           first
           (.setAttribute (domo/el-by-id "app") "data-kushi-playground-first-intersecting")))

#_(def *playground-first-intersecting
  (reagent.ratom/reaction
   (let [_ (?-- :HIII)
         ret (first (sort-by #(.indexOf ordered-playground-components-labels %)
                             (:intersecting @*playground)))]

     (? "first intersecting is " ret)

     (.setAttribute (domo/el-by-id "app")
                    "data-kushi-playground-first-intersecting"
                    ret)
     ret)))


(def *focused-path (r/atom ["components"]))
(defn set-focused-path! [s] (reset! *focused-path s))
(defn get-focused-path [] @*focused-path)
