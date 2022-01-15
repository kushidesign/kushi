(ns example.core
  (:require

   ;; Require various functions and macros from kushi.core
   [kushi.core :refer (sx cssfn inject-stylesheet add-font-face add-system-font-stack defkeyframes cssfn clean!)]

   ;; IMPORTANT - If you are using defclasses to share styles, it is good practice to defined them all
   ;;   in a dedicated namespace. To ensure all of these defclasses will be available globally,
   ;;   you must require them (as we are doing here) in the ns that corresponds to your main module.
   ;;   This require must come before the requires of any other namespaces which contain ui code that
   ;;   uses one of your defclasses.
   [example.shared-styles]

   ;; This example uses reagent
   [reagent.dom :as rdom]))


;; kushi.core/clean! removes all existing styles that were injected into #_kushi-dev_ style tag.
;; These styles are only injected in development builds, for instant preview of changes.
;; You only need to call this once, from your project's main/core ns.
(clean!)


;; Main component
(defn main-view []
  [:<>
   [:div
      (sx
       :fs--36px
       :c--midnightblue)
      "Hi"]])



;; Below is boilerplate code from https://github.com/shadow-cljs/quickstart-browser

;; start is called by init and after code reloading finishes
(defn ^:dev/after-load start []
  (rdom/render [main-view] (.getElementById js/document "app")))

(defn init []
  ;; init is called ONCE when the page loads
  ;; this is called in the index.html and must be exported
  ;; so it is available even in :advanced release builds
  (start))

;; this is called before any code is reloaded
(defn ^:dev/before-load stop [])
