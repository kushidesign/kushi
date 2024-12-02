(ns mvp.browser
  (:require
  ;;  [site.theme]
   [reagent.dom :as rdom]
  ;;  [site.views :as views]
   [mvp.button :refer [my-button]]
   [kushi.css.core :refer [css sx defcss]]
   ))

(defcss "@keyframes yspinner"
    [:0% {:transform "rotateY(0deg)"}]
    [:100% {:transform "rotateY(360deg)"}])

(defcss "@keyframes xspinner"
    [:0% {:transform "rotateX(0deg)"}]
    [:100% {:transform "rotateX(360deg)"}])

(defcss ".aliceblue-bg" :bgc--aliceblue)

(defn main-view []
  [:div [:div {:class (css :.aliceblue-bg
                           :.divisor-block-end
                           :animation--yspinner:10s:infinite
                           :c--magenta
                           :fs--100px)}
         [:div {:class (css :animation--xspinner:5s:infinite)} "串"]]
   [:div (sx :.absolute-centered) [my-button]]])

(defn ^:dev/after-load mount-root []
  (let [root-el (.getElementById js/document "app")]
    ;; (rdom/render [views/main-view] root-el)
    (rdom/render [main-view] root-el)
    ))

(defn init []
  (mount-root))

;; (inject!)
