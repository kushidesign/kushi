(ns site.views
  (:require
   [clojure.string :as string]
   [domo.core :as domo]
   [kushi.core :refer [sx]]
   [kushi.playground.about :as about]
   ;  [kushi.playground.tweak.samples :refer [pane-samples]]
   [kushi.playground.components :refer [playground-components]]
   [kushi.playground.layout :as layout]
   [kushi.playground.nav :as nav]
   [secretary.core :as secretary :refer [defroute]]
   [accountant.core :as accountant]
   [reagent.core :as r]
   ))

(js/console.clear)

;; Define some pages -----------------------------------------------------------
(defn page-not-found []
  [:div "404 | Page not found"])

;; Define each route -----------------------------------------------------------
(declare route-state)

(defroute intro-path "/" []
  (swap! route-state assoc :active-route "/"))

(defroute components-path "/components" []
  (swap! route-state assoc :active-route "/components"))

(defroute colors-path "/colors" []
  (do
    #_(js/console.log (swap! route-state assoc :active-route "/typography"))
    (swap! route-state assoc :active-route "/colors")))

(defroute typography-path "/typography" []
  (do
    #_(js/console.log (swap! route-state assoc :active-route "/typography"))
    (swap! route-state assoc :active-route "/typography")))


(defroute component-path #"/components(\#[a-z]+)" [hash-id]
  (do (swap! route-state assoc :active-route "/components")
      (let [el (domo/el-by-id (subs hash-id 1))]
        (domo/scroll-into-view! el))))


;; Define state ----------------------------------------------------------------

(defn active-route []
  (let [s js/window.location.pathname]
    (when-not (secretary/locate-route s))
    s))

(defonce route-state
  (r/atom {:active-route (active-route) 
           :pages        {"/"           {:title   :no-title
                                         :content layout/component-playground-content-3col
                                         :args    playground-components
                                         :label   "Splash"}
                          "/components" {:title   :no-title
                                         :content layout/component-playground-content
                                         :args    playground-components}
                          "/colors"     {:content about/kushi-colors-about}
                          "/typography" {:content about/kushi-typography-about}
                          "/404"        {:content page-not-found}}}))


(accountant/configure-navigation!
 {:nav-handler  (fn [path] (secretary/dispatch! path))
  :path-exists? (fn [path] (secretary/locate-route path))})

(def routes 
  {["components"] {:content layout/component-playground-content
                   :args    playground-components}
   ["colors"]     {:content about/kushi-colors-about}
   ["typography"] {:content about/kushi-typography-about}
   ["intro"]      {:content layout/component-playground-content-3col
                   :args    playground-components
                   :label   "Splash"}})

(defn active-route->display-name [s]
  (-> s
      (string/replace #"^/" "")
      (string/replace #"-" " ")))

(defn main-view []
  (.setAttribute (domo/el-by-id "app")
                 "data-kushi-playground-active-path"
                ;;  "typography"
                ;;  "intro"
                 "components"
                 )

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
  
  ;; [pane-samples]
 
  (let [{:keys [active-route pages]} @route-state
        real-path?                   (boolean (secretary/locate-route
                                               active-route))              
        page                         (or (get pages active-route)
                                         (get pages "/404"))
        {:keys [title]}              page
        title                        (when real-path?
                                       (when-not (= :no-title title)
                                         (or title
                                             (active-route->display-name
                                              active-route))))
        path                         (subs active-route 1)
        opts                         (assoc page
                                            :path
                                            path
                                            :active-route
                                            active-route
                                            :label
                                            title)]
    [:div (sx :.flex-col-fs
              {" .prose:font-weight"                                                :$light
               "dark: .prose:color"                                                 :$neutral-500
               " .prose code:font-weight"                                           :$normal
               " .kushi-playground-component-usage span:font-weight"                :$light
               " .kushi-playground-component-usage code:font-weight"                :$normal
               " .kushi-opt-detail-value:font-weight"                               :$light
               " .kushi-opt-detail-value code:font-weight"                          :$normal
               " .kushi-opt-detail-value .code:font-weight"                         :$normal
               " .kushi-opt-detail-value span>code+code:display"                    :block
               " .kushi-opt-detail-value span>code+code:mb"                         :0.5
               " .kushi-opt-detail-value span>code+code:line-height"                1.5
               " #kushi-icon-examples>div>section>div>section:nth-child(2):display" :none
               "dark: .codebox:bgc"                                                 :#222630})
     [nav/header active-route]
     [layout/generic-section opts]]))
