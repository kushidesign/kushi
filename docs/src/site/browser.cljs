(ns site.browser
  (:require
   [kushi.core :refer [inject! sx]]
   [kushi.ui.button.core :refer [button]]
   [site.theme]
   [reagent.dom :as rdom]
   [site.views :as views]
   [clojure.string :as string]))

(def labels [[:accent "play ▸"]
             [:positive "confirm"]
             [:warning "proceed"]
             [:negative "cancel"]])

(defn button-group [{:keys [surface]}]
  (into [:div (sx :.flex-row-c :gap--1rem)] 
        (for [[colorway label] labels] 
          [button
           {:data-kui-colorway colorway
            :data-kui-surface  surface}
           (string/capitalize (name label))])))

;; POC for new theming system
(defn new-theming-system-button-example []
  (into [:div (sx :.flex-col-c :.absolute-centered :gap--1rem)]
        (for [surface [:outline :solid :soft :minimal]]
          [button-group {:surface surface}])))

(defn ^:dev/after-load mount-root []
  (let [root-el (.getElementById js/document "app")]
    (rdom/render [new-theming-system-button-example]
                 #_[views/main-view]
                 root-el)))

(defn init []
  (mount-root))


(inject!)
