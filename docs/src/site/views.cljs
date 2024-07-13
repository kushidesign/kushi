(ns site.views
  (:require [domo.core :as domo]
            [kushi.core :refer [sx]]
            [kushi.playground.about :as about]
            [kushi.playground.components :refer [playground-components]]
            [kushi.playground.layout :as layout]
            [kushi.playground.nav :as nav]
            [kushi.ui.button.core :refer [button]]
            [kushi.ui.icon.core :refer [icon]]))

(def routes 
  {["components"] {:content layout/component-playground-content
                   :args    playground-components
                   :label   "Components Playground"}
   ["colors"]     {:content about/kushi-colors-about}
   ["typography"] {:content about/kushi-typography-about}
   ["intro"]      {:content about/kushi-about}})


(defn main-view []
  (.setAttribute (domo/el-by-id "app")
                 "data-kushi-playground-active-path"
                 "components")
  (into 
     [:div (sx :.flex-col-fs)
      #_[nav/header]
      ;; Spinner between page transitions
      ;; Leave out for now as transitions are instant
      #_[layout/loading-spinner]

      [:div (sx :mbs--100px :&_button:m--20px)

      ;;  [button (sx {:-semantic :accent
      ;;               :-surface  :minimal})
      ;;   "Accent"]

      ;;  [button (sx {:-semantic :warning
      ;;               :-surface  :minimal})
      ;;   "Accent"]

      ;;  [button (sx {:-semantic :negative
      ;;               :-surface  :minimal})
      ;;   "Accent"]

      ;;  [button (sx {:-semantic :positive
      ;;               :-surface  :minimal})
      ;;   "Accent"]

      ;;  [button (sx {:-semantic :positive
      ;;               :-surface  :minimal})
      ;;   "Accent"]



      ;;  [button (sx 
      ;;           {:-semantic :accent
      ;;            :-surface  :outline})
      ;;   "Accent"]

       [button (sx {:-semantic :accent
                    :-surface  :solid
                    :-start-enhancer [icon {:-icon-filled? true} :star]})
        "Accent"]

       [button (sx 
              {:-semantic :accent
               :-surface  :soft})
        "Accent"]

       [button (sx 
              {:-semantic :neutral
               :-surface  :soft})
        "Accent"]

       [button (sx 
                {:-surface :outline
                 :-hue     :$green-hue})
        "Accent"]
       
       ]]

     #_(for [[view {:keys [content label]
                  :as   route}] routes
           :let                                      [label (or label (->> view last))
                                                      path  (string/join "/" view)]
           :when                                     content]
       [layout/generic-section (assoc route :path path :label label)])))
