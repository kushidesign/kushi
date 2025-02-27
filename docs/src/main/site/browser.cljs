(ns site.browser
  (:require
   [reagent.dom :as rdom]
   ;; Toggle views/views2 here to switch between sandbox and full site
   [site.views2 :as views]))

(defn ^:dev/after-load mount-root []
  (let [root-el (.getElementById js/document "app")]
    (rdom/render [views/main-view] root-el)))

(defn init []
  (mount-root))

