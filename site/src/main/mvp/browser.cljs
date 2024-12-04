(ns mvp.browser
  (:require
   [reagent.dom :as rdom]
   ;; [site.views :as views]
   [mvp.button :refer [my-button]]
   [kushi.css.core :refer [css sx defcss]]
   [kushi.css.inject :as inject]))

;; required for default fonts used in kushi.ui
(inject/add-google-fonts!
 {:family "Inter"
  :styles {:normal :all #_[400 500 600]
           :italic :all #_[400 500 600]}}
 {:family "JetBrains Mono"
  :styles {:normal :all #_[400 500 600]
           :italic :all #_[400 500 600]}}
 {:family "Cormorant"
  :styles {:normal :all #_[400 500 600]
           :italic :all #_[400 500 600]}})

;; required for default icons used in kushi.ui
(inject/add-google-material-symbols!
 {:family "Material Symbols Outlined"
  :axes   {:opsz :20..48
           :wght :100..700
           :grad :-50..200
           :fill :0..1}})

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
   [:div (sx :.absolute-centered)
    [my-button]
    [:span (sx :c--red) "red"]]])

(defn ^:dev/after-load mount-root []
  (let [root-el (.getElementById js/document "app")]
    ;; (rdom/render [views/main-view] root-el)
    (rdom/render [main-view] root-el)
    ))

(defn init []
  (mount-root))

;; (inject!)
