(ns mvp.browser
  (:require
   [reagent.dom :as rdom]
   ;; [site.views :as views]
   [mvp.button :refer [my-button]]
  ;;  [kushi.ui.slider.core :refer [slider]]
   [kushi.css.core :refer [css sx defcss ?css css-vars css-vars-map]]
   ))




(defcss "@keyframes yspinner"
    [:0% {:transform "rotateY(0deg)"}]
    [:100% {:transform "rotateY(360deg)"}])

(defcss "@keyframes xspinner"
    [:0% {:transform "rotateX(0deg)"}]
    [:100% {:transform "rotateX(360deg)"}])

(defcss ".aliceblue-bg" :bgc--aliceblue)

(defcss
  "@layer kushi-ui-theming .kushi-slider-step-label-marker-none"
  ;; :.kushi-slider-step-label-marker
  {:before:fw :300
   :before:fs :0.8em
   :before:o  :1})

(defn main-view []
(let [step-marker-none-class (when true :.kushi-slider-step-label-marker-none)
      step-marker-content*   "foo"
      step-marker-content    (str "\"" step-marker-content* "\"")
      label-scale-factor     0.7
      label-scale-factor     (str "scale(" label-scale-factor ")")]
  [:div {:style (css-vars-map step-marker-content label-scale-factor)
         :class (css
                 step-marker-none-class
                 {:ta                                          :c
                  :w                                           0
                  :h                                           0
                  :.kushi-slider-step-label-selected:transform "scale(1)"
                  :.kushi-slider-step-label-selected:o         1
                  :.kushi-slider-step-label-selected:c         :currentColor
                  :.kushi-slider-step-label-selected>span:v    :visible
                  :transform                                   :$label-scale-factor
                  :before:content                              :$step-marker-content})}
   "hi"])

  #_[:div [:div {:class (css :.aliceblue-bg
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
