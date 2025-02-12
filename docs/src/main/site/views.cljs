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
   [kushi.ui.tag.core :refer [tag]]
   [kushi.ui.switch.core :refer [switch]]
   [kushi.ui.callout.core :refer [callout]]
   [kushi.ui.link.core :refer [link]]
   [kushi.ui.popover.core :refer [popover-attrs dismiss-popover!]]
   [kushi.ui.toast.core :refer [toast-attrs dismiss-toast!]]
   [kushi.ui.toast.demo :refer [toast-content]]
   [reagent.dom :refer [render]]
   [kushi.ui.modal.core :refer [modal modal-close-button open-kushi-modal close-kushi-modal]]
   [kushi.ui.spinner.core :refer [spinner donut propeller thinking]]
   [kushi.ui.icon.core :refer [icon]]
  ;;  [kushi.colors2 :as colors2]
   [fireworks.core :refer [? !? ?> !?>]]
   [kushi.playground.ui :refer [light-dark-mode-switch]]
   ))
(js/console.clear)

(defn switch-dev-samples []
  (defcss ".switch-dev-grid"
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
    #_(into [:div (sx :.switch-dev-grid
                    {:before:content "\"Colorways\""})]
          (for [colorway ["bonkers" "accent" "positive" "warning" "negative"]] 
            [switch 
             (merge-attrs (sx :fs--$xxxlarge)
                          {:-colorway colorway
                           :-on?      true})]))

     (into [:div (sx :.switch-dev-grid
                     {:before:content "\"Colorways\""})]
          (for [colorway ["bonkers" "accent" "positive" "warning" "negative"]] 
            [button
             (merge-attrs (sx :fs--$small :fw--$wee-bold :.loose)
                          {:-colorway colorway
                           :-surface  :solid})
             "Button"]))
   #_(into [:div (sx :.switch-dev-grid
                     {:before:content "\"Colorways\""})]
          (for [colorway ["neutral" "accent" "positive" "warning" "negative"]] 
            [button
             (merge-attrs (sx :fs--$small :fw--$wee-bold :.loose)
                          {:-colorway colorway
                           :-surface  :soft})
             "Button"]))
   ])

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


(defn tag-dev-samples []
  (defcss ".tag-dev-grid"
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
   #_(into [:div (sx :.tag-dev-grid :.flex-col-fs)]
         (for [shape ["rounded" #_"sharp" #_"pill"]] 
           (into [:div (sx :.flex-col-fs :gap--0.5em)]
                 (for [surface ["minimal" "outline" "solid" "soft"]]
                   (into [:div (sx :.flex-row-fs :gap--0.5em)]  
                         (for [colorway [nil "accent" "positive" "warning" "negative"]]
                           [tag 
                            (merge-attrs (sx :fs--$small)
                                         {:-colorway       colorway
                                          :-surface        surface
                                          :-shape          shape
                                          :-start-enhancer [icon :pets]})
                            "Dang"]))))))

   #_(into [:div (sx :.tag-dev-grid :.flex-col-fs)]
         (for [shape ["rounded" #_"sharp" #_"pill"]] 
           (into [:div (sx :.flex-col-fs :gap--0.5em)]
                 (for [surface ["minimal" "outline" "solid" "soft"]]
                   (into [:div (sx :.flex-row-fs :gap--0.5em)]  
                         (for [colorway [nil "accent" "positive" "warning" "negative"]]
                           [tag 
                            (merge-attrs (sx :fs--$small)
                                         {:-colorway colorway
                                          :-surface  surface
                                          :-shape    shape})
                            "Dang"]))))))  

   (into [:div (sx :.tag-dev-grid
                   {:before:content "\"Shape\""})]
         (for [shape ["round" "sharp" "pill" "round" "sharp" "pill"]] 
           [tag 
            (merge-attrs (sx :fs--$xxxlarge
                             ["nth-child(4):tt" :lowercase]
                             ["nth-child(5):tt" :lowercase]
                             ["nth-child(6):tt" :lowercase])
                         {:-surface        "outline"
                          :-shape          shape})
            (if (= shape "sharp") "Done" "Dang")]))

   (into [:div (sx :.tag-dev-grid
                   {:before:content "\"Packing\""})]
         (for [packing ["compact" nil "roomy" "compact" nil "roomy"]] 
           [tag 
            (merge-attrs (sx :fs--$xxxlarge
                             ["nth-child(4):tt" :lowercase]
                             ["nth-child(5):tt" :lowercase]
                             ["nth-child(6):tt" :lowercase])
                         {:-surface        "outline"
                          :-packing        packing})
            "Dang"]))

   #_(into [:div (sx :.tag-dev-grid
                   :gap--2em
                   {:before:content "\"Outline thicknesses\""})]
         (for [_stroke-width ["1px" "2px" "3px" "4px" "5px"]] 
           [tag 
            (merge-attrs {:style (css-vars-map _stroke-width)
                          :class (css :fs--$small
                                      [:--outlined-button-stroke-width :$_stroke-width]
                                      [:--outlined-button-stroke-align "center"])}
                         {:-surface        "outline"
                          :-shape          "round"})
            "Dang"]))

   #_(into [:div (sx :.tag-dev-grid
                   :gap--2em
                   {:before:content "\"Outline thicknesses with :-stroke-align set to :outside\""})]
         (for [_stroke-width ["1px" "2px" "3px" "4px" "5px"]] 
           [tag 
            (merge-attrs {:style (css-vars-map _stroke-width)
                          :class (css :fs--$small
                                      [:--outlined-button-stroke-width :$_stroke-width]
                                      [:--outlined-button-stroke-align "center"])}
                         {:-surface        "outline"
                          :-shape          "round"
                          :-stroke-align   :outside})
            "Dang"]))
   
   #_(into [:div (sx :.tag-dev-grid
                   :gap--2em
                   {:before:content "\"Outline thicknesses with :-stroke-align set to :outside, with lowercase\""})]
         (for [_stroke-width ["1px" "2px" "3px" "4px" "5px"]] 
           [tag 
            (merge-attrs {:style (css-vars-map _stroke-width)
                          :class (css :fs--$small
                                      :.lowercase
                                      [:--outlined-button-stroke-width :$_stroke-width]
                                      [:--outlined-button-stroke-align "center"])}
                         {:-surface        "outline"
                          :-shape          "round"
                          :-stroke-align   :outside})
            "Dang"]))

   #_(into [:div (sx :.tag-dev-grid
                   :gap--2em
                   {:before:content "\"Box shadow\""})]
         (for [_stroke-width ["1px" "2px" "3px" "4px" "5px"]] 
           [tag 
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
                          :-stroke-align   :inside})
            "Dang"]))])


(defn color-row [bgc]
  [:div (sx :.flex-col-c :>div:w--50px :>div:h--50px)
   [:div {:class (css :position--relative :w--100px :h--100px)
          :style {:background-color bgc}}]])


(defn hsl+oklch-color-grid2 []
   ;; Based on design-system-tokens
   (let [colors   [
                  ;;  ["gray" 0]
                   {:n "purple"
                    :h 304.9
                    :l 57.2
                    :c 0.315}
                   {:n "blue"
                    :h 262
                    :l 51
                    :c 0.2927}
                   {:n "green"
                    :h 155
                    :l 85.5
                    :c 0.2932}
                  ;;  ["lime" 129.5]
                  ;;  ["yellow" 100]
                   {:n "gold"
                    :h 86.4
                    :l 85.38
                    :c 0.2013
                    }
                  ;;  ["orange" 62.3]
                   {:n "red"
                    :h 22.4
                    :l 65.19
                    :c 0.2959}
                  ;;  ["magenta" 347.6]
                  ;;  ["brown" 46.1]
                  ;;  ["gray" 0]
                   ]
         scale [5 10 20 40 80 140 200 260 320 380 440 500 560 720 780 840]
         _ (? (count scale))
         ;; colors       (? (keys oklch-colors))
         oklch-grid   (for [{:keys [h l c]} colors]
                        (into [:div (sx :.flex-row-fs)]
                              (for [lvl [5 10 20 40 80 140 200 260 320 380 440 500 560 720 780 840 900 960 1000]
                                    :let [c     0.1 #_(* c ratio)]]
                                [color-row (str "oklch(" (- 100 (/ lvl 10)) "%"  " " c " " h ")")])))]
    [:<> 
     #_(into [:div (sx :.flex-col-fs [:bgc "rgb(127.5 127.5 127.5)"] :p--100px)]
           oklch-grid)
     #_(into [:div (sx :.flex-col-fs [:bgc "rgb(0 0 0)"] :p--100px)]
           oklch-grid)

     (into [:div (sx :.flex-col-fs [:bgc "rgb(255 255 255)"] :p--100px)]
           oklch-grid)]

    ))

(defn hsl+oklch-color-grid [{:keys [hsl?]}]

  #_[:div (sx :p--50px :bgc--$red-500) 
   [:div {:class (css :w--100px :h--100px :bgc--$transparent-dark-gray-35)}]]

  ;; Based on design-system-tokens
   (let [colors   ["gray"
                   "purple"
                   "blue"
                   "green"
                   "lime"
                   "yellow"
                   "gold"
                   "orange"
                   "red"
                   "magenta"
                   "brown"
                   "gray"]
        ;; colors       (? (keys oklch-colors))
         oklch-grid   (for [color-name colors]
                        (into [:div (sx :.flex-row-fs)]
                              (for [lvl (range 50 1050 50)]
                                [color-row (str "var(--" color-name "-" lvl ")")])))
         hsl-grid     (when hsl?
                        #_(for [[color-name {:keys [hue scale]}] colors]
                          (into [:div (sx :.flex-row-fs)]
                                (for [[lvl saturation lightness] scale]
                                  [color-row (str "hsl(var(--"
                                                  color-name
                                                  "-hue) "
                                                  saturation
                                                  " "
                                                  lightness
                                                  ")")]))))
         ]
    (into [:div (sx :.flex-col-fs :bgc--$neutral-500 :p--100px)]
          (if hsl?
            (interleave hsl-grid oklch-grid)
            oklch-grid)))


  ;; Based on raw data
  #_(let [oklch-colors (apply array-map colors2/oklch-colors)
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
        hsl-grid     (when hsl?
                       (for [[color-name {:keys [hue scale]}] hsl-colors]
                         (into [:div (sx :.flex-row-fs)]
                               (for [[lvl saturation lightness] scale]
                                 [color-row (str "hsl(var(--"
                                                 color-name
                                                 "-hue) "
                                                 saturation
                                                 " "
                                                 lightness
                                                 ")")]))))]
    (into [:div (sx :.flex-col-fs)]
          (if hsl?
            (interleave hsl-grid oklch-grid)
            oklch-grid))))

(defn callout-dev-samples []
  [callout
   (merge-attrs (sx :.large :.accent) 
                {:-header-text [:span
                                "Please check out the "
                                [link (merge-attrs
                                       (sx :ws--n)
                                       {:href "#"})
                                 "new features"]]
                 :-colorway    :accent
                 :-icon        [icon :info]})]

  #_(into [:div (sx :.button-dev-grid :.flex-col-fs)]
         (for [shape ["rounded" #_"sharp" #_"pill"]] 
           (into [:div (sx :.flex-col-fs :gap--0.5em)]
                 (for [surface ["minimal" "outline" "solid" "soft"]]
                   (into [:div (sx :.flex-row-fs :gap--0.5em)]  
                         (for [colorway [nil "accent" "positive" "warning" "negative"]]
                           [callout
                            (merge-attrs (sx :.large :.accent) 
                                         {:-header-text [:span
                                                         "Please check out the "
                                                         [link (merge-attrs
                                                                (sx :ws--n)
                                                                {:href "#"})
                                                          "new features"]]
                                          :-colorway    colorway
                                          :-icon        [icon :info]})])))))))



(defn pane-samples []
  [:<> 
   [light-dark-mode-switch (sx :.fixed-block-start-inside :.light :.transition)]
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
    
    
    ;; [hsl+oklch-color-grid {:hsl? false}]
    [hsl+oklch-color-grid2]
    

    #_[button-dev-samples]
    #_[tag-dev-samples]
    #_[switch-dev-samples]
    #_[callout-dev-samples]

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
       "Hover me"]]] 
  
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
