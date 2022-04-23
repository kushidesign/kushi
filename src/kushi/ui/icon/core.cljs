(ns kushi.ui.icon.core
  (:require
   [kushi.core  :refer (sx cssfn defclass)]
   [kushi.gui   :refer (gui defcom)]))


;; #_(def label*
;;   (gui [:section
;;         (sx
;;          :fs--1rem
;;          :sm:fs--1.2rem
;;          :md:fs--1.4rem
;;          :lg:fs--1.75rem)]))

;; #_(defn right-chevron
;;     [{:keys [size stroke-width color]
;;       :or   {color "black" size 14 stroke-width 1}}]
;;     (let [width          (/ size 2)
;;           width-adjusted (+ 2 width)]
;;       [:svg {:height  :100%
;;              :width   :100%
;;              :viewBox (str "0 0 " width-adjusted " " size)}
;;        [:path {:d            (str "M 0, 0"  " L " width ", " (/ size 2) "L 0, " size)
;;                :stroke       color
;;                :stroke-width stroke-width
;;                :fill         "none"}]]))

(defn bar
  [{:keys [width
           height
           stroke-width
           color]
    :or   {color        :black
           width        100
           height       5
           stroke-width 1}}]
  (let [size          (max width height)
        factor        (/ 100 size)
        width         (* factor width)
        height*       (max height stroke-width)
        height        (* factor height*)
        overflow-factor 4
        y-offset        (/ (- (* height overflow-factor) height) 2)
        x-offset        (/ (- (* width overflow-factor) width) 2)]
    [:svg (sx :.absolute-centered
              :.absolute-fill
              {:style {:w :400%}
               ;;  :height  :100%
          ;;  :width   :100%
               :viewBox (str "0 0 " (* overflow-factor width) " " (* overflow-factor height))})
     [:path {:d             (str "M " x-offset "," y-offset
                                 " L " (+ x-offset width) " , " y-offset)
             :stroke        color
             :stroke-width  stroke-width
             :fill          :none
             :vector-effect :non-scaling-stroke}]]))

(defn chevron-down
  [{:keys [width
           height
           stroke-width
           color]
    :or   {color        :black
           width        100
           height       50
           stroke-width 1}}]
  (let [size          (max width height)
        factor        (/ 100 size)
        width         (* factor width)
        height        (* factor height)
        overflow-factor 4
        y-offset        (/ (- (* height overflow-factor) height) 2)
        x-offset        (/ (- (* width overflow-factor) width) 2)]
    [:svg (sx :.absolute-centered
              :.absolute-fill
              {:style {:w :400%}
               ;;  :height  :100%
          ;;  :width   :100%
               :viewBox (str "0 0 " (* overflow-factor width) " " (* overflow-factor height))})
     [:path {:d             (str "M " x-offset "," y-offset
                                 " L " (+ (/ width 2) x-offset) ", " (+ height y-offset)
                                 "L " (+ x-offset width) " , " y-offset)
             :stroke        color
             :stroke-width  stroke-width
             :fill          :none
             :vector-effect :non-scaling-stroke}]]))

(def icon-base
  [:div
   (sx
    'kushi-icon:ui
    :.relative
    :.transition
    :.flex-row-c
    :ta--center
    :d--ib
    :ai--c)])

(defcom icon
  icon-base)

;; (def icon-button-base
;;   [:button
;;    (sx {:class [:flex-row-c]
;;         :style {:border-radius :100%
;;                 :w     :1.5rem
;;                 :h     :1.5rem
;;                 :lh    0
;;                 :p     :0.5rem
;;                 :b     :none
;;                 :fs    :1rem
;;                 :bgi   :none
;;                 :ai    :c
;;                 :cursor :pointer
;;                 :hover:o 0.8}})])

;; (def dismiss-button
;;   (gui icon-button-base
;;        (sx {:class [:absolute]
;;             :style {:top           :0.75rem
;;                     :right         :0.75rem
;;                     :after:content "\"✕\""}})))

;; (def icon-button
;;   (gui icon-button-base))


;; (def modal
;;   (gui
;;    [:div
;;     (sx {:class [:absolute-fill :flex-col-c]
;;          :style {:ai  :c
;;                  :bgc (cssfn :rgba 0 0 0 0.1)}})]))

;; (defclass floating-pane
;;   {:bgc :white
;;    :box-shadow "0 10px 15px -3px rgb(0 0 0 / 5%), 0 4px 6px -2px rgb(0 0 0 / 2%)"})

;; (def modal-panel-base
;;   [:div
;;    (sx {:class [:floating-pane]
;;         :style {:p :1.5rem
;;                 ;; :border-radius (theme/get-style [:panel :border-radius] 0)
;;                 ;; :border-color  (theme/get-style [:panel :border-color] :#efefef)
;;                 ;; :border-width  (theme/get-style [:panel :border-width] :1px)
;;                 ;; :border-style  (theme/get-style [:panel :border-style] :solid)
;;                 }
;;         :ident :modal-panel-base})])

;; (def modal-panel-flex
;;   (gui
;;    modal-panel-base
;;    (sx {:class [:flex-col-c :relative]
;;         :style {:ai :c
;;                 :width :600px
;;                 :h :100%
;;                 :max-height "calc(100% - 40px)"
;;                 :max-width "calc(100% - 40px)"}
;;         :ident :modal-panel-flex})))

;; (def modal-panel
;;   (gui
;;    modal-panel-base
;;    (sx {:class [:flex-col-c :relative :foo]
;;         :ident :modal-panel
;;         :style {:ai :c
;;                 :w :600px
;;                 :h :350px}})))

;; (def button-group
;;   (gui
;;    [:div
;;     (sx {:class [:flex-row-fs]
;;          :style  {
;;                   ;; ">button:nth-child(2n):margin" (theme/get-style [:button-group :>button :margin] 0)
;;                   :>button:first-child:m 0
;;                   :>button:last-child:m 0}})]))
