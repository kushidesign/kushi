(ns mvp.views
  (:require
   [fireworks.core :refer [? !? ?> !?>]]
   [kushi.css.core :refer [sx token->ms]]
  ;;  [kushi.playground.layout :as layout]
   [kushi.playground.nav :as nav]
  ;;  [kushi.playground.components :refer [playground-components]]
   [domo.core :as domo]
  ;;  [kushi.playground.about :as about]
  ;;  [clojure.string :as string]
   ))

;; (? (domo/as-css-custom-property-name "var(--xxfast)"))
;; (? (domo/as-css-custom-property-name :$xxfast))
(js/console.clear)
;; (? (token->ms "--xxfast"))
;; (? (token->ms :--xxfast))
;; (? (token->ms 9))

#_(def routes 
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
    ;; Spinner between page transitions
    ;; Leave out for now as transitions are instant
    #_[layout/loading-spinner]
    ]
    #_(for [[view {:keys [content label] :as route}] routes
          :let [label (or label (->> view last))
                path  (string/join "/" view)]
          :when content]
      [layout/generic-section (assoc route :path path :label label)])))
