(ns site.views
  (:require
   [fireworks.core :refer [? !? ?> !?>]]
   [kushi.css.core :refer [css sx token->ms merge-attrs]]
   [kushi.playground.layout :as layout]
   [kushi.playground.nav :as nav]
   [kushi.playground.components :refer [playground-components]]
   [domo.core :as domo]
   [kushi.playground.about :as about]
   [clojure.string :as string]
   [kushi.ui.tooltip.core :refer [tooltip-attrs]]
   [kushi.ui.button.core :refer [button]]
   [kushi.ui.popover.core :refer [popover-attrs dismiss-popover!]]
   [kushi.ui.toast.core :refer [toast-attrs dismiss-toast!]]
   [kushi.ui.toast.demo :refer [toast-content]]
   [reagent.dom :refer [render]]
   [kushi.ui.modal.core :refer [modal modal-close-button open-kushi-modal close-kushi-modal]]
   ))

(js/console.clear)


(defn pane-samples []
  [:div (sx :.absolute-centered :.flex-col-fs :gap--2rem)
   
   ;; toast example
   #_[button
    (toast-attrs {:-auto-dismiss? false
                  :-f             (fn [toast-el] (render toast-content toast-el))
                  ;; :-placement     :tlc
                  :-toast-class   (css [:--toast-border-width :5px]
                                       [:--toast-background-color :beige])})
    "Save for later"]


   ;; modal example
   (let
      [id "my-modal-basic"]
       [:div [button {:on-click (fn* [] (open-kushi-modal id))}
              "Click to open modal"]
        [modal (merge-attrs 
                (sx :min-width--300px
                    :_.kushi-modal-description:fs--$small
                 ;; [:--modal-border-radius :0px]
                 ;; [:--modal-backdrop-color :beige]
                    )
                {:id         id
                 :-elevation 5})
         [:div (merge-attrs
                (sx :.flex-row-c)

                ;; tooltip on top-layer
                #_(tooltip-attrs
                 {:-text          "This is a tooltip"
                  :-tooltip-class (css {:--tooltip-background-color :$red-800})})

                ;; popover on top-layer
                #_(popover-attrs {:-f (fn [popover-el]
                                      (render 
                                       (fn [] 
                                         [:div
                                          (sx :.flex-row-c
                                              :fs--$xxxlarge
                                              :padding--0.25em)
                                          "💃🏽"])
                                       popover-el))
                           :-popover-class (css [:--popover-background-color :lime])})
                )
          "Modal text"
          #_[:span "💃🏽"]]]])


   ;; popover example
   #_[button (popover-attrs {:-f             (fn [popover-el]
                                             (render 
                                              (fn [] 
                                                [:div
                                                 (sx :.flex-row-c
                                                     :fs--$xxxlarge
                                                     :padding--0.25em)
                                                 "💃🏽"])
                                              popover-el))
                           :-popover-class (css [:--popover-background-color :lime])})
      "Click me"]


   ;; tooltip example
   #_[button
      (tooltip-attrs {:-text          "This is a tooltip"
                      :-tooltip-class (css {:--tooltip-background-color :$red-800})})
      "Hover me"]] 
  )

(def routes 
  {
   ["components"] {:content layout/component-playground-content
                   :args    playground-components
                   :label   "Components Playground"}
   ["colors"]     {:content about/kushi-colors-about}
   ["typography"] {:content about/kushi-typography-about}
   ["intro"]      {:content about/kushi-about}})

(defn main-view []
  (.setAttribute (domo/el-by-id "app")
                 "data-kushi-playground-active-path"
                 "components")

  #_[pane-samples]

  (into 
   [:div (sx :.flex-col-fs)
    [nav/header]
    ;; Spinner between page transitions
    ;; Leave out for now as transitions are instant
    #_[layout/loading-spinner]
    #_[:div (sx :.wireframe 
              :m--100px
              [:before:content "\"gold\""])
     "hi"]]
    
    (for [[view {:keys [content label] :as route}] routes
          :let [label (or label (->> view last))
                path  (string/join "/" view)]
          :when content]
      [layout/generic-section (assoc route :path path :label label)])))
