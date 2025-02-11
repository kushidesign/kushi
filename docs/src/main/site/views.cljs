(ns site.views
  (:require
   [kushi.core :refer [css sx defcss token->ms merge-attrs css-vars-map]]
   [kushi.playground.layout :as layout]
   [kushi.playground.nav :as nav]
   [kushi.playground.components :refer [playground-components]]
   [domo.core :as domo]
   [kushi.playground.about :as about]
   [clojure.string :as string]
   [kushi.ui.tooltip.core :refer [tooltip-attrs]]
   [kushi.ui.button.core :refer [button]]
   [kushi.ui.popover.core :refer [popover-attrs dismiss-popover!]]
   [kushi.ui.toast.core :refer [toast-attrs dismiss-toast!]]
   [kushi.ui.toast.demo :refer [toast-content]]
   [reagent.dom :refer [render]]
   [kushi.ui.modal.core :refer [modal modal-close-button open-kushi-modal close-kushi-modal]]
   [kushi.ui.spinner.core :refer [spinner donut propeller thinking]]
   [kushi.ui.icon.core :refer [icon]]
   [kushi.colors2 :as colors2]
   [fireworks.core :refer [? !? ?> !?>]]
   ))

(js/console.clear)

(defn button-dev-samples []
  (defcss ".button-dev-grid"
    :.flex-row-fs
    :gap--0.5em
    :mbs--3rem
    :position--relative
    {:before {:content     "\"All colorways, all surfaces\""
              :position    :absolute
              :bottom      "calc(100% + 1em)"
              :fs          :$xsmall
              :font-family :$serif-font-stack
              :font-style  :italic}})
  [:<> 
   (into [:div (sx :.button-dev-grid :.flex-col-fs)]
         (for [shape ["rounded" #_"sharp" #_"pill"]] 
           (into [:div (sx :.flex-col-fs :gap--0.5em)]
                 (for [surface ["minimal" "outline" "solid" "soft"]]
                   (into [:div (sx :.flex-row-fs :gap--0.5em)]  
                         (for [colorway [nil "accent" "positive" "warning" "negative"]]
                           [button 
                            (merge-attrs (sx :fs--$small)
                                         {:-colorway       colorway
                                          :-surface        surface
                                          :-shape          shape
                                          :-start-enhancer [icon :pets]})
                            "Button"]))))))

   (into [:div (sx :.button-dev-grid
                   {:before:content "\"Shape\""})]
         (for [shape ["round" "sharp" "pill"]] 
           [button 
            (merge-attrs (sx :fs--$small)
                         {:-surface        "outline"
                          :-shape          shape
                          :-start-enhancer [icon :pets]})
            "Button"]))

   (into [:div (sx :.button-dev-grid
                   {:before:content "\"Packing\""})]
         (for [packing ["compact" nil "roomy"]] 
           [button 
            (merge-attrs (sx :fs--$small)
                         {:-surface        "outline"
                          :-packing        packing
                          :-start-enhancer [icon :pets]})
            "Button"]))

   (into [:div (sx :.button-dev-grid
                   :gap--2em
                   {:before:content "\"Outline thicknesses\""})]
         (for [_stroke-width ["1px" "2px" "3px" "4px" "5px"]] 
           [button 
            (merge-attrs {:style (css-vars-map _stroke-width)
                          :class (css :fs--$small
                                      [:--outlined-button-stroke-width :$_stroke-width]
                                      [:--outlined-button-stroke-align "center"])}
                         {:-surface        "outline"
                          :-shape          "round"
                          :-start-enhancer [icon :pets]})
            "Button"]))

   (into [:div (sx :.button-dev-grid
                   :gap--2em
                   {:before:content "\"Outline thicknesses with :-stroke-align set to :outside\""})]
         (for [_stroke-width ["1px" "2px" "3px" "4px" "5px"]] 
           [button 
            (merge-attrs {:style (css-vars-map _stroke-width)
                          :class (css :fs--$small
                                      [:--outlined-button-stroke-width :$_stroke-width]
                                      [:--outlined-button-stroke-align "center"])}
                         {:-surface        "outline"
                          :-shape          "round"
                          :-stroke-align   :outside
                          :-start-enhancer [icon :pets]})
            "Button"]))

   (into [:div (sx :.button-dev-grid
                   :gap--2em
                   {:before:content "\"Box shadow\""})]
         (for [_stroke-width ["1px" "2px" "3px" "4px" "5px"]] 
           [button 
            (merge-attrs {:class (css :fs--$small
                                      [:--box-shadow-strength "20%"]
                                      [:--box-shadow-blur-radius "5px"]
                                      [:--box-shadow-offset-x "5px"]
                                      [:--box-shadow-offset-y "5px"]
                                      [:--box-shadow-color "blue"]
                                      [:--box-shadow-2-offset-x "0"]
                                      [:--box-shadow-2-offset-y "0"]
                                      [:--box-shadow-2-strength "50%"]
                                      [:--box-shadow-2-blur-radius "5px"]
                                      [:--box-shadow-2-color "red"]
                                      )}
                         {:-surface        "outline"
                          :-shape          "round"
                          :-stroke-align   :inside
                          :-start-enhancer [icon :pets]})
            "Button"]))])

(defn color-row [bgc]
  [:div (sx :.flex-col-c :>div:w--50px :>div:h--50px)
   [:div {:class (css :position--relative :w--100px :h--100px)
          :style {:background-color bgc}}]])

(defn hsl+oklch-color-grid []
  (let [oklch-colors (apply array-map colors2/oklch-colors)
        hsl-colors   (apply array-map colors2/colors)
        ;; colors       (? (keys oklch-colors))
        oklch-grid   (for [[color-name {:keys [hue scale]}] oklch-colors]
                       (into [:div (sx :.flex-row-fs)]
                             (for [[lvl lightness chroma] scale]
                               [color-row (str "oklch("
                                               lightness
                                               " "
                                               chroma
                                               " "
                                               hue
                                               ")")])))
        hsl-grid     (for [[color-name {:keys [hue scale]}] hsl-colors]
                       (into [:div (sx :.flex-row-fs)]
                             (for [[lvl saturation lightness] scale]
                               [color-row (str "hsl(var(--"
                                               color-name
                                               "-hue) "
                                               saturation
                                               " "
                                               lightness
                                               ")")])))]
    (into [:div (sx :.flex-col-fs)]
          (interleave hsl-grid oklch-grid))))

(defn pane-samples []
  [:div (sx :.absolute-centered 
            :.flex-col-fs
            :gap--2rem
            ;; :.debug-red
            ;; :outline-offset--0px
            )
   

  ;;  [button 
  ;;   (merge-attrs (sx [:--button-border-width :5px]
  ;;                    :border-color--pink!important)
  ;;                {:class [:bordered]})
  ;;   "hello"]
  ;;  [button "hello"]
   
   
   [hsl+oklch-color-grid]
   

   #_[button-dev-samples]


;; [lvl sat-hsl lightness-hsl] (get-in hsl-colors [color-name :scale])
;; [hue-oklch                   (get-in oklch-colors [color-name :hue])
;;                                                  hue-hsl (get-in hsl-colors  [color-name :hue])]

   ;; button with spinner example
   #_[button
      (merge-attrs
       (sx :fs--$xxxlarge)
       {:on-click (fn [e]
                    (let [el       (-> e .-target)
                          loading? (= "loading" (.-ariaLabel el))]
                      (if loading?
                        (do (.removeAttribute el "aria-label")
                            (.removeAttribute el "data-kushi-ui-spinner")) 
                        (do (.setAttribute el "aria-label" "loading")
                            (.setAttribute el "data-kushi-ui-spinner" true)))))})
      [icon (sx ["[aria-label='loading'] &:display" :none]) :play-arrow]

    ;; TODO - Need to use some kind of aspect ration thing based on height
      [:span (sx :d--none ["[aria-label='loading'] &:display" :block]) [donut]]
      "Activate"]
   

   ;; toast example
   #_[button
      (toast-attrs {:-auto-dismiss? false
                    :-f             (fn [toast-el] (render toast-content toast-el))
                  ;; :-placement     :tlc
                    :-toast-class   (css [:--toast-border-width :5px]
                                         [:--toast-background-color :beige])})
      "Save for later"]


   ;; modal example
   #_(let
      [id "my-modal-basic"]
       [:div [button {:on-click (fn* [] (open-kushi-modal id))}
              "Click to open modal"]
        [modal (merge-attrs 
                (sx :min-width--300px
                    :_.kushi-modal-description:fs--$small
                 ;; [:--modal-border-radius :0px]
                 ;; [:--modal-backdrop-color :beige]
                    )
                {:id         id
                 :-elevation 5})
         [:div (merge-attrs
                (sx :.flex-row-c)

                ;; tooltip on top-layer
                #_(tooltip-attrs
                   {:-text          "This is a tooltip"
                    :-tooltip-class (css {:--tooltip-background-color :$red-800})})

                ;; popover on top-layer
                #_(popover-attrs {:-f (fn [popover-el]
                                        (render 
                                         (fn [] 
                                           [:div
                                            (sx :.flex-row-c
                                                :fs--$xxxlarge
                                                :padding--0.25em)
                                            "💃🏽"])
                                         popover-el))
                                  :-popover-class (css [:--popover-background-color :lime])})
                )
          "Modal text"
          #_[:span "💃🏽"]]]])


   ;; popover example
   #_[button (popover-attrs {:-f             (fn [popover-el]
                                               (render 
                                                (fn [] 
                                                  [:div
                                                   (sx :.flex-row-c
                                                       :fs--$xxxlarge
                                                       :padding--0.25em)
                                                   "💃🏽"])
                                                popover-el))
                             :-popover-class nil #_(css [:--popover-background-color :lime])})
      "Click me"]


   ;; tooltip example
   #_[button
      (tooltip-attrs {:-text          "This is a tooltip"
                      :-tooltip-class (css {:--tooltip-background-color :$red-800})})
      "Hover me"]] 
  
  )

(def routes 
  {["components"] {:content layout/component-playground-content
                   :args    playground-components
                   :label   "Components Playground"}
   ["colors"]     {:content about/kushi-colors-about}
   ["typography"] {:content about/kushi-typography-about}
   ["intro"]      {:content about/kushi-about}})

(defn main-view []
  (.setAttribute (domo/el-by-id "app")
                 "data-kushi-playground-active-path"
                 "components")

  [pane-samples]

  #_(into 
   [:div (sx :.flex-col-fs)
    [nav/header]
    ;; Spinner between page transitions
    ;; Leave out for now as transitions are instant
    #_[layout/loading-spinner]
    #_[:div (sx :.wireframe 
              :m--100px
              [:before:content "\"gold\""])
     "hi"]]
    
    (for [[view {:keys [content label] :as route}] routes
          :let [label (or label (->> view last))
                path  (string/join "/" view)]
          :when content]
      [layout/generic-section (assoc route :path path :label label)])))
