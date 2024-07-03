(ns site.browser
  (:require
   [kushi.core :refer [inject!]]
   [site.theme]
   [reagent.dom :as rdom]
   [site.views :as views]))

(defn ^:dev/after-load mount-root []
  (let [root-el (.getElementById js/document "app")]
    (rdom/render [views/main-view] root-el)))

(defn init []
  (mount-root))


(inject!)
