(ns site.views
  (:require
   [fireworks.core :refer [? !? ?- !?- ?-- !?-- ?> !?> ?i !?i ?l !?l ?log !?log ?log- !?log- ?pp !?pp ?pp- !?pp-]]
   [kushi.core :refer [sx inject!]]
   [kushi.playground.layout :as layout]
   [kushi.playground.nav :as nav]
   [kushi.playground.components :refer [playground-components]]
   [domo.core :as domo]
   [kushi.playground.about :as about]
   [clojure.string :as string]))

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
    [nav/header]
    #_[layout/loading-spinner]]
    (for [[view {:keys [content label] :as route}] routes
          :let [label (or label (->> view last))
                path  (string/join "/" view)]
          :when content]
      [layout/generic-section (assoc route :path path :label label)])))
