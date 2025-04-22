(ns site.views2
  (:require
   [domo.core :as domo]
   [kushi.playground.shared-styles]
   [kushi.ui.button.core :refer [button]]
   [kushi.playground.showcase.core :refer [showcase uic-showcase-map]]
   [kushi.ui.button.core :refer [button]]
   [kushi.ui.icon.core :refer [icon]]
   [kushi.ui.spinner.core :refer [donut propeller thinking]]
   ))

(js/console.clear)

(defn main-view []
  (.setAttribute (domo/el-by-id "app")
                 "data-kushi-playground-active-path"
                 "components")


  #_[:div
   [button
    {
    ;; :-loading?     true
     :-end-enhancer #_[icon :east] [propeller]
     }
    "Play"]
   
   [button
    {
    ;; :-loading?     true
     :-end-enhancer #_[icon :east] [donut]
     }
    "Play"]
   
   [button
    {
    ;; :-loading?     true
     :-end-enhancer #_[icon :east] [thinking]
     }
    "Play"]]


  ;; for pallette generation dev
  #_(js/setTimeout
     (fn []
       (dotimes [n (-> okstate deref :levels count)]
         (adjust-slider! {:pallette-idx pallette-idx 
                          :scale-key    :chroma-scale
                          :scale-idx    n}))
       #_(? (domo/qs "[data-scale='chroma'][data-level='450']"))
       )
     2000)
  #_[:div "Hi"]

  [showcase (uic-showcase-map kushi.ui.button.core/button)]

  #_[pane-samples]
  )
