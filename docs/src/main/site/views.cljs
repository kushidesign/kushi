(ns site.views
  (:require
   [kushi.playground.about :as about]
  ;;  [kushi.playground.components :refer [playground-components]]
  ;;  [kushi.playground.layout :as layout]
   [fireworks.core :refer [? !? ?> !?>]]
   [kushi.playground.nav :as nav]
   [clojure.string :as string]
   [domo.core :as domo]
   [kushi.ui.flex :as flex :refer [flex-row flex-col]]
   [kushi.core :refer [sx sx2] :rename {merge-attrs ma}]
   [kushi.playground.shared-styles]))

;; (js/console.clear)

(def routes 
  {
  ;;  ["components"] {:content layout/component-playground-content
  ;;                  :args    playground-components
  ;;                  :label   "Components Playground"}
   ["colors"]     {:content about/kushi-colors-about}
   ["typography"] {:content about/kushi-typography-about}
  ;;  ["intro"]      {:content about/kushi-about}
   })


(defn main-view []
  (js/console.clear)
  (.setAttribute (domo/el-by-id "app")
                 "data-kushi-playground-active-path"
                 "components")


  ;; for pallette generation dev
  ;; (js/setTimeout
  ;;    (fn []
       
  ;;      )
  ;;    2000)

  #_(into 
   [flex/flex-col
    (sx :>*:p--1rem:4rem)
    [nav/header2]

    ;; Spinner between page transitions
    ;; Leave out for now as transitions are instant
    #_[layout/loading-spinner]
    #_[:div (sx :.wireframe 
                :m--100px
                [:before:content "\"gold\""])
       "hi"]]
   
   #_(for [[view {:keys [content label]
                  :as   route}] routes
           :let                                      [label (or label (->> view last))
                                                      path  (string/join "/" view)]
           :when                                     content]
       [layout/generic-section (assoc route :path path :label label)])
   )

   [:div 
    (? (sx2 {:display    :flex-col
             :id         :foo
             :color      :red
             :>*:padding :1rem:4rem}
            {:color :blue}))
    [:div "hi"]]
   #_[:div
    (? (sx2 {:display    :flex-col
             :>*:padding :1rem:4rem}))
    [nav/header2]


    #_[flex-row 
     (ma {:tag :main} (sx :pbs--1rem))
     
     (into [flex-col]
           (for [x (range 100)]
             [:div x]))]])
