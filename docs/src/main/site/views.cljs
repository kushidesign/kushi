(ns site.views
  (:require

   [kushi.playground.about :as about]
   [kushi.playground.tweak.samples :refer [pane-samples]]
   [kushi.playground.components :refer [playground-components]]
   [kushi.playground.layout :as layout]
   [kushi.playground.nav :as nav]
   [kushi.playground.tweak.samples :refer [pane-samples]]
   [kushi.playground.components :refer [playground-components]]

   [clojure.string :as string]
   [clojure.walk :as walk]
   [domo.core :as domo]
   [fireworks.core :refer [!? ?]]
   [kushi.core :refer [defcss merge-attrs sx]] 
   [kushi.css.build.design-tokens :as design-tokens]
   [kushi.playground.component-examples :refer [all-colors section-label]]
   [kushi.playground.showcase.core :refer [showcase]]
   [kushi.playground.shared-styles]
   [kushi.ui.button.core :refer [button]]
   [kushi.ui.icon.core :refer [icon]]
   [kushi.ui.popover.core :refer [popover-attrs]]
   [kushi.ui.popover.core :refer [popover-attrs]]
   [kushi.ui.util :refer [as-str]]
   [kushi.playground.util :refer [interleave-all]]
   ))

(js/console.clear)

(defcss ".foo" :c--blue)

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
  #_(js/setTimeout
     (fn []
       (dotimes [n (-> okstate deref :levels count)]
         (adjust-slider! {:pallette-idx pallette-idx 
                          :scale-key    :chroma-scale
                          :scale-idx    n}))
       #_(? (domo/qs "[data-scale='chroma'][data-level='450']"))
       )
     2000)
  
  #_[showcase]

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
