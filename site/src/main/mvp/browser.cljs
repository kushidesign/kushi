(ns mvp.browser
  (:require
  ;;  [site.theme]
   [reagent.dom :as rdom]
  ;;  [site.views :as views]
   [kushi.css.core :refer [css defcss]]
   ))

(defcss "@keyframes yspinner"
    [:0% {:transform "rotateY(0deg)"}]
    [:100% {:transform "rotateY(360deg)"}])

(defcss ".aliceblue-bg" :bgc--aliceblue)

(defn main-view []
  [:div {:class (css :.aliceblue-bg
                     :.divisor-block-end
                     :animation--yspinner:10s:infinite
                     :c--magenta
                     :fs--$xxxlarge)}
   "hi"])

(defn ^:dev/after-load mount-root []
  (let [root-el (.getElementById js/document "app")]
    ;; (rdom/render [views/main-view] root-el)
    (rdom/render [main-view] root-el)
    ))

(defn init []
  (mount-root))

;; (inject!)
