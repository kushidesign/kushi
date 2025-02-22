(ns kushi.playground.tweak.colorscale
  (:require
   [kushi.core :refer [css ?css sx defcss token->ms merge-attrs css-vars-map]]
   [kushi.playground.layout :as layout]
   [kushi.playground.nav :as nav]
   [kushi.playground.components :refer [playground-components]]
   [domo.core :as domo]
   [kushi.playground.about :as about]
   [clojure.string :as string]
   [kushi.ui.tooltip.core :refer [tooltip-attrs]]
   [kushi.ui.button.core :refer [button]]
   [kushi.ui.tag.core :refer [tag]]
   [kushi.ui.switch.core :refer [switch]]
   [kushi.ui.callout.core :refer [callout]]
   [kushi.ui.link.core :refer [link]]
   [kushi.ui.popover.core :refer [popover-attrs dismiss-popover!]]
   [kushi.ui.toast.core :refer [toast-attrs dismiss-toast!]]
   [kushi.ui.toast.demo :refer [toast-content]]
   [reagent.dom :refer [render]]
   [reagent.ratom]
   [kushi.ui.modal.core :refer [modal modal-close-button open-kushi-modal close-kushi-modal]]
   [kushi.ui.spinner.core :refer [spinner donut propeller thinking]]
   [kushi.ui.slider.core :refer [slider]]
   [kushi.ui.icon.core :refer [icon]]
  ;;  [kushi.colors2 :as colors2]
   [kushi.playground.ui :refer [light-dark-mode-switch]]
   [reagent.core :as r]))

(def pallette-idx 0)

(defonce okstate
  (let [levels*         (range 2.5 100 2.5)
        levels          (concat (take 27 levels*)
                                [70.4
                                 73.7
                                 76.9
                                 79.9
                                 83.3
                                 86.8
                                 90
                                 92.4
                                 94.4
                                 96.4
                                 98
                                 99.1])
        chroma-scale    (into [] (repeat (count levels) 0))
        lightness-scale (into [] levels)]
    (reagent.ratom/atom
     {:levels        levels
      :pallettes     [{:hue-name        "red"
                       :hue             22.4
                       :chroma-scale    chroma-scale
                       :lightness-scale lightness-scale}
                      {:hue-name        "blue"
                       :hue             262
                       :chroma-scale    chroma-scale
                       :lightness-scale lightness-scale}]
      :current-color {:h 262
                      :c 0.1
                      :l 54.6}})))


(defn oklch-slider-change-handler [k %]
  (let [updated-slider-value    (/ (domo/cetv %) (case k :l 10 :c 1000 :h 10))
        ;; new-state            (swap! okstate assoc-in [:current-color k] updated-slider-value)
        ;; {:keys [l c h]}      (-> new-state :current-color)
        ;; oklch                (str "oklch(" l "% " c " " h ")")
        ;; color                (js/Color. oklch)
        ;; color-in-srgb?       (.inGamut color "srgb")
        ]
    #_(swap! okstate
           assoc
           :current-color-in-srgb?
           color-in-srgb?
           :current-color-oklch-css-string
           oklch
           )))

(defn swap-new-pallette! [{:keys [pallette-idx scale-key scale-idx] :as m}
                          updated-slider-value]
  (let [pallettes     (-> okstate deref :pallettes)
        pallette      (-> pallettes (nth pallette-idx))
        scale         (-> pallette scale-key)
        new-scale     (assoc scale scale-idx updated-slider-value)
        new-pallette  (assoc pallette scale-key new-scale)
        new-pallettes (assoc pallettes pallette-idx new-pallette)]
    (swap! okstate
           assoc
           :pallettes
           new-pallettes)))

(defn updated-slider-value [v scale-key]
  (/ v
     (case scale-key
       :lightness-scale 10 
       :chroma-scale 1000
       :hue 10)))

(defn get-updated-slider-value [{:keys [scale-key current-value op]}]
  (->
   current-value
   ((or op +) (case scale-key
                :lightness-scale 0.1 
                :chroma-scale 0.001 
                :hue 0.1))
   (.toFixed 3)
   (js/parseFloat)))

(defn get-current-slider-value [{:keys [pallette-idx scale-key scale-idx]}]
  (-> okstate deref
      :pallettes
      (nth pallette-idx)
      scale-key
      (nth scale-idx)))

(defn get-current-hue [{:keys [pallette-idx]}]
  (-> okstate deref
      :pallettes
      (nth pallette-idx)
      :hue))

(defn oklch-slider-change-handler2 [{:keys [pallette-idx scale-key scale-idx] :as m} %]
  (let [updated-slider-value (updated-slider-value (domo/cetv %) scale-key)
        ;; new-state            (swap! okstate assoc-in [:current-color k] updated-slider-value)
        ;; {:keys [l c h]}      (-> new-state :current-color)
        ;; oklch                (str "oklch(" l "% " c " " h ")")
        ;; color                (js/Color. oklch)
        ;; color-in-srgb?       (.inGamut color "srgb")
        ]
    (swap-new-pallette! m updated-slider-value)))

;; [:<>
;;  [:p {:class (css :p--0.5em:1.2em :mbe--1em)
;;       :style {:color            (when-not current-color-in-srgb? "var(--negative-800)")
;;               :background-color (when-not current-color-in-srgb? "var(--negative-200)")
;;               :font-weight      :bold}}
;;   current-color-oklch-css-string]
;;  [:div {:class (css :w--300px :h--300px)
;;         :style {:background-color current-color-oklch-css-string}}]]

(defn adjust-slider* [m]
  (let [{:keys [pallette-idx scale-key op]}
        m

        c
        (get-current-slider-value (assoc m :scale-key :chroma-scale))

        l
        (get-current-slider-value (assoc m :scale-key :lightness-scale))

        hue
        (get-current-hue {:pallette-idx pallette-idx})

        oklch                
        (str "oklch(" l "% " c " " hue ")")

        color                
        (js/Color. oklch)

        color-in-srgb?       
        (.inGamut color "srgb")

        updated-value (get-updated-slider-value (assoc m
                                                       :current-value
                                                       (case scale-key
                                                         :lightness-scale l
                                                         :chroma-scale c
                                                         :hue hue)))]

    {:color-in-srgb? color-in-srgb?
     :updated-value  updated-value
     :l              l
     :c              c
     :hue            hue}

    #_(while color-in-srgb?
      (swap-new-pallette! m 
                          (get-updated-slider-value (assoc m
                                                           :current-value
                                                           (case scale-key
                                                             :lightness-scale l
                                                             :chroma-scale c
                                                             :hue hue)))))))
(defn adjust-slider! [{:keys [scale-key] :as m}]
  (loop [{:keys [color-in-srgb? updated-value l c hue]} (adjust-slider* m)]
    (if color-in-srgb?
      (do (swap-new-pallette! m updated-value)
          (recur (adjust-slider* m)))
      (swap-new-pallette! m 
                          (get-updated-slider-value (assoc m
                                                           :op
                                                           -
                                                           :current-value
                                                           (case scale-key
                                                             :lightness-scale l
                                                             :chroma-scale c
                                                             :hue hue)))))))


#_(defn adjust-slider! []
  (let [{:keys [color-in-srgb? updated-value l c h]} (adjust-slider*)]
    (while color-in-srgb?
      (swap-new-pallette! m 
                          (get-updated-slider-value (assoc m
                                                           :current-value
                                                           (case scale-key
                                                             :lightness-scale l
                                                             :chroma-scale c
                                                             :hue hue)))))))

(defcss ".inline-slider-label"
  {:position :relative
   :before   {:content :$value
              :o       :33%
              :pi      :1em
              :fs      :$xsmall}})

(defn color-sliders [factor scale-key label-attrs]
  (into [:div.flex-col-c]
        (let [color                                  
              (nth (-> okstate deref :pallettes) pallette-idx)

              {:keys [chroma-scale lightness-scale]} 
              color]
          (for [idx  (range (-> okstate deref :levels count))
                :let [scale  (if (= scale-key :lightness-scale)
                               lightness-scale
                               chroma-scale)
                      value* (* factor (nth scale idx nil))
                      value  (str "\"" (/ value* factor) "%\"")]]
            [:input.kushi-slider-input
             (merge-attrs
              {:style (css-vars-map value)}
              label-attrs
              {:data-scale :lightness
               :data-level (str (- 1000 (+ (* idx 25) 25)))
               :style      {:height :33px}
               :type       :range
               :value      value*
               :min        0
               :max        1000
               :step       1
               :on-change  (partial oklch-slider-change-handler2 
                                    {:scale-key    scale-key
                                     :scale-idx    idx
                                     :pallette-idx pallette-idx})})]))))

(defn pallette-generator []
  [:div (sx :.flex-row-fs
            :mbs--3rem
            :ff--$code-font-stack)
   (into [:div (sx :.flex-col-c :ai--c :w--400px)]
         (let [color                                      (nth (-> okstate deref :pallettes) pallette-idx)
               {:keys [hue chroma-scale lightness-scale]} color]
           (for [idx  (range (-> okstate deref :levels count))
                 :let [c                   (nth chroma-scale idx nil)
                       l                   (nth lightness-scale idx nil)
                       oklch               (str "oklch(" l "% " c " " hue ")")
                       color               (js/Color. oklch)
                       color-in-srgb?      (.inGamut color "srgb")
                       color-in-srgb-class (when-not color-in-srgb? 
                                             "out-of-srgb-gamut")
                       level               (str "\"" (- 1000
                                                        (+ (* idx 25) 25))
                                                "\"")]]
             [:div (merge-attrs
                    {:style (css-vars-map level oklch)}
                    (sx color-in-srgb-class
                        :.before-absolute-inline-start-outside
                        :.after-absolute-inline-end-outside
                        :bgc--$oklch
                        :w--200px
                        :h--33px
                        :position--relative
                        [".out-of-srgb-gamut:after:content" "\"🔴\""]
                        :after:pis--1em
                        [":before:content" :$level]
                        :before:pis--1em
                        :before:o--33%
                        :before:pi--1em
                        :before:fs--$xsmall))])))

   [:div (sx :.flex-row-fs
             :gap--4rem
             {:>div {:w :200px
                     :o 1}})

    [color-sliders
     10
     :lightness-scale
     (sx :.before-absolute-inline-start-outside
         :.inline-slider-label)]

    [color-sliders
     1000
     :chroma-scale
     (sx :.before-absolute-inline-end-outside
         :.inline-slider-label)]]])
