(ns site.views
  (:require
   [kushi.playground.about :as about]
   [kushi.playground.components :refer [playground-components]]
   [kushi.playground.layout :as layout]
   [kushi.playground.nav :as nav]
   [clojure.string :as string]
   [domo.core :as domo]
   [kushi.playground.shared-styles]
   ))

;; (js/console.clear)

(def routes 
  {
   ["components"] {:content layout/component-playground-content
                   :args    playground-components
                   :label   "Components Playground"}
   ["colors"]     {:content about/kushi-colors-about}
   ["typography"] {:content about/kushi-typography-about}
   ["intro"]      {:content about/kushi-about}
   })


(defn main-view []
  (.setAttribute (domo/el-by-id "app")
                 "data-kushi-playground-active-path"
                 "components")


  ;; for pallette generation dev
  ;; (js/setTimeout
  ;;    (fn []
       
  ;;      )
  ;;    2000)

  (into 
     [:div.flex-col-fs
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
