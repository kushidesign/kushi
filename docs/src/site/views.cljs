(ns site.views
  (:require [domo.core :as domo]
            [kushi.core :refer [sx]]
            [kushi.playground.about :as about]
            [kushi.playground.components :refer [playground-components]]
            [kushi.playground.layout :as layout]
            [kushi.playground.nav :as nav]
            [kushi.ui.button.core :refer [button]]
            [kushi.ui.icon-button.core :refer [icon-button]]
            [kushi.ui.icon.core :refer [icon]]
            [clojure.string :as string]))

(def routes 
  {["components"] {:content layout/component-playground-content
                   :args    playground-components
                   :label   "Components Playground"}
   ["colors"]     {:content about/kushi-colors-about}
   ["typography"] {:content about/kushi-typography-about}
   ["intro"]      {:content about/kushi-about}})

(defn button-row [coll colorway]
  (into [:div.flex-row-fs]
        (for [x coll]
          [button
           (sx {:-colorway colorway
                :-surface  x})
           "Button"])))

(defn main-view []
  (.setAttribute (domo/el-by-id "app")
                 "data-kushi-playground-active-path"
                 "components")
  (into 
     [:div (sx :.flex-col-fs)
      [nav/header]
      ;; Spinner between page transitions
      ;; Leave out for now as transitions are instant
      #_[layout/loading-spinner]

      #_[:div (sx :mbs--100px :&_button:m--20px)

      ;; [button-row [:minimal :outline :solid :soft] :accent]
      ;; [button-row [:minimal :outline :solid :soft] :warning]
      ;; [button-row [:minimal :outline :solid :soft] :negative]
      ;; [button-row [:minimal :outline :solid :soft] :positive]
      ;; [button-row [:minimal :outline :solid :soft] :neutral]
      ;; [button (sx {:-surface        :solid
      ;;              :-start-enhancer [icon {:-icon-filled? true} :star]})
      ;;  "Neutral"]
      ;; [button [icon {:-icon-filled? true} :star]]

       #_[:div.flex-row-fs
        #_[button (sx {:-colorway     :accent
                       :-surface      :solid
                       :-packing      :compact
                       :-end-enhancer [icon :arrow-forward]})
         "Next"]

        #_[button (sx {:-colorway       :accent
                       :-surface        :solid
                       :-packing        :default
                       :-start-enhancer [icon {:-icon-filled? true} :star]})
         "Next"]

        [button (sx {:-colorway     :accent
                     :-surface      :solid
                     :-packing      :compact
                     :-end-enhancer [icon :arrow-forward]})
         "Nexgt"]
        
        [icon-button (sx {:-colorway :accent
                          :-surface  :solid
                          :-packing  :compact})
         [icon {:-icon-filled? true} :star]]
        
        [icon-button (sx {:-colorway :accent
                          :-surface  :solid})
         [icon {:-icon-filled? true} :star]]

        [icon-button (sx {:-colorway :accent
                          :-surface  :solid
                          :-packing  :roomy})
         [icon {:-icon-filled? true} :star]]

        ]

      ;;  [button (sx {:-surface  :soft})
      ;;   "Accent"]


       #_[button (sx
                ;; :color--magenta
                :$stroke-width--1px
                ;; :$stroke-color--teal
                :border-radius--5px
                {:-surface      :outline
                 :-stroke-align :outside
                 :-colorway :$green-hue})
        "Accent"]]]

     (for [[view
            {:keys [content label]
             :as   route}] routes

           :let                         
           [label (or label (->> view last))
            path  (string/join "/" view)]

           :when                                     
           content]
       [layout/generic-section (assoc route :path path :label label)])))
