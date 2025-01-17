(ns mvp.views
  (:require
   [fireworks.core :refer [? !? ?> !?>]]
   [kushi.css.core :refer [sx token->ms]]
   [kushi.playground.layout :as layout]
   [kushi.playground.nav :as nav]
  ;;  [kushi.playground.components :refer [playground-components]]
   [domo.core :as domo]
   [kushi.playground.about :as about]
   [clojure.string :as string]
   ))

(js/console.clear)

(def routes 
  {
  ;;  ["components"] {:content layout/component-playground-content
  ;;                  :args    playground-components
  ;;                  :label   "Components Playground"}
  ;;  ["colors"]     {:content about/kushi-colors-about}
   ["typography"] {:content about/kushi-typography-about}
  ;;  ["intro"]      {:content about/kushi-about}
   })

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
    ]
    (for [[view {:keys [content label] :as route}] routes
          :let [label (or label (->> view last))
                path  (string/join "/" view)]
          :when content]
      [layout/generic-section (assoc route :path path :label label)])))
