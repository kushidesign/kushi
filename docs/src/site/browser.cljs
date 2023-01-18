(ns site.browser
  (:require
   [kushi.ui.title.core :refer [title]]
   [playground.core :refer [main-view]]
   [kushi.core :refer [sx inject!]]
   [reagent.dom :as rdom]))

(defn site-header []
  [title
   (sx 'playground-header
       :.playground-title
       :.xxlarge
       :>span:ai--baseline
       :md:pbs--$vp-top-header-padding-with-offset)
   "Kushi"
   [:span
    (sx 'playground-title-version-number :.xxxsmall :.tester :mis--0.5rem)
    "v1.0.0-alpha"]])

(defn ^:dev/after-load start []

  ;; (rdom/render [main-view] (.getElementById js/document "app"))

  (rdom/render [main-view {
                           ;; change to `:site-header`
                           :site-header       site-header

                           :kushi-components  {:render?        true
                                               :header         "Components Playground"
                                               :sidenav-header "Components"}

                           :kushi-colors      {:render?        true
                                               :header         "Colors"
                                               :sidenav-header "Colors"}

                           :kushi-typography  {:render?        true
                                               :header         "Typography"
                                               :sidenav-header "Typography"}

                           :kushi-user-guide  {:render?        true
                                               :sidenav-header "User Guide"
                                               :href           "https://github.com/paintparty/kushi"
                                               :target         :_blank}

                           :kushi-clojars     {:render?        true
                                               :sidenav-header "Clojars"
                                               :href           "https://clojars.org/org.clojars.paintparty/kushi"
                                               :target         :_blank}

                           :kushi-about       {:render?        true
                                               :header         "About"
                                               :sidenav-header "About"}

                           :hide-lightswitch?                  false
                           :display-kushi-links-in-mobile-nav? true
                           }]
               (.getElementById js/document "app")))

(defn init []
  ;; init is called ONCE when the page loads
  ;; this is called in the index.html and must be exported
  ;; so it is availle even in :advanced release builds
  ;; (js/console.log "init")
  (start))

;; (js/console.clear)

(inject!)
