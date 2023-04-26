(ns site.browser
  (:require
   [kushi.ui.examplescustom :refer [mock-custom-components]]
   [kushi.ui.label.core :refer [label]]
   [kushi.playground.core :refer [main-view components-to-render]]
   [kushi.playground.util :refer [kushi-github-url kushi-clojars-url]]
   [kushi.core :refer [sx inject!]]
   [reagent.dom :as rdom]
   [site.theme]))

(defn site-header []
  [label
   (sx 'playground-header
       :.playground-title
       :fs--$kushi-playground-main-section-header_font-size|$large
       :md:fs--$kushi-playground-main-section-header_font-size|$xxlarge
       :fw--$kushi-playground-main-section-header_font-weight|$wee-bold
       :md:pbs--$vp-top-header-padding-with-offset
       :ai--baseline)
   "Kushi"
   [:span
    (sx 'playground-title-version-number
        :fs--$xxxxsmall
        :md:fs--$xxxsmall
        :.wee-bold
        :.tester
        :mis--0.5rem)
    "v1.0.0-alpha"]])

(defn ^:dev/after-load start []

  ;; (rdom/render [main-view] (.getElementById js/document "app"))

  (rdom/render
   [main-view
    {
     ;; Change to `:site-header`
     :site-header                        site-header

     :custom-components                  {:render?        false
                                          :header         "Custom Components Playground"
                                          :sidenav-header "Custom Components"
                                          :coll           (components-to-render mock-custom-components)}

     :kushi-components                   {:render?        true
                                          :header         "Components Playground"
                                          :sidenav-header "Components"}

     :kushi-colors                       {:render?        true
                                          :header         "Colors"
                                          :sidenav-header "Colors"}

     :kushi-typography                   {:render?        true
                                          :header         "Typography"
                                          :sidenav-header "Typography"}

     :kushi-user-guide                   {:render?        true
                                          :sidenav-header "User Guide"
                                          :href           kushi-github-url
                                          :target         :_blank}

     :kushi-clojars                      {:render?        true
                                          :sidenav-header "Clojars"
                                          :href           kushi-clojars-url
                                          :target         :_blank}

     :kushi-about                        {:render?        true
                                          :header         "About"
                                          :sidenav-header "About"}

     :hide-lightswitch?                  false

     :display-kushi-links-in-mobile-nav? true

     :page-wrapper-attrs                 (sx {:class ["kushi-playground-with-rainbow-keys"]})}]
   (.getElementById js/document "app")))

(defn init []
  ;; init is called ONCE when the page loads
  ;; this is called in the index.html and must be exported
  ;; so it is availle even in :advanced release builds
  ;; (js/console.log "init")
  (start))

;; (js/console.clear)

(inject!)
