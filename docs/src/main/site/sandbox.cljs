;; For trying stuff out

(ns site.sandbox
  (:require
   [reagent.dom :as rdom]
   [kushi.ui.icon.mui.svg :as mui.svg ]
   [kushi.core :refer [merge-attrs]]
   [kushi.css.core :refer [css sx defcss ?css css-vars css-vars-map grid-template-areas]]
   ;; [mvp.views :as views]
   ;; [mvp.button :refer [my-button]]
   ;; [kushi.ui.slider.core :refer [slider]]
   ;; [kushi.ui.radio.core :refer [radio]]
   ;; [kushi.ui.checkbox.core :refer [checkbox]]
   ;; [kushi.ui.label.core :refer [label]]
   [kushi.ui.icon.core :refer [icon]]
   [kushi.ui.text-field.core :refer [text-field]]
   ;; [kushi.ui.spinner.core :refer [spinner donut propeller thinking]]
   ;; [kushi.ui.grid.core :refer [grid]]
   ;; [kushi.ui.callout.core :refer [callout]]
   ;; [kushi.ui.link.core :refer [link]]
   ;; [kushi.ui.collapse.core :refer [collapse]]
   ;; [kushi.ui.switch.core :refer [switch]]
   ;; [kushi.ui.card.core :refer [card]]
   ;; [kushi.ui.tag.core :refer [tag]]
  ;;  [kushi.ui.modal.core :refer [modal
  ;;                               modal-close-button
  ;;                               open-kushi-modal
  ;;                               close-kushi-modal]]
   
   [kushi.ui.toast.core :refer [toast-attrs dismiss-toast!]]
   [kushi.ui.button.core :refer [button]]
   [kushi.ui.tooltip.core :refer [tooltip-attrs]]
   [kushi.ui.button.core :refer [button]]
   [kushi.ui.popover.core :refer [popover-attrs dismiss-popover!]]
   [domo.core :as domo]
   [reagent.dom :refer [render]]))


  ;; ---------------------------------------------------------------------------
  ;; Sandbox experiements from late 2025
  ;; ---------------------------------------------------------------------------

  ;; button grid

(let [sim-ia
        (fn [e]
          (when-let [el (let [et (domo/event-target e)]
                          (if (domo/has-attribute? et "data-ks-surface2")
                            et
                            (domo/nearest-ancestor et "[data-ks-surface2]")))]
            (domo/toggle-class! el "ks-active")
            (js/setTimeout
             (fn [] 
               (domo/toggle-class! el "ks-active"))
             300)))
        en->jp 
        (!? {"ghost"         "ゴースト",
            "transparent"   "透明",
            "minimal"       "ミニマル",
            "faint"         "薄い",
            "soft"          "ソフト",
            "convex"        "凸",
            "soft-classic"  "ソフトクラシック",
            "solid-classic" "ソリッドクラシック",
            "solid"         "ソリッド"})
        div (fn [{:keys [stroke surface colorway interactive text shape id]}]
              [:div (merge-attrs 
                     {
                      ;; for the animation wipe
                      ;; :on-click sim-ia
                      :id                 id
                      :style              {:display (if (or (contains? #{
                                                                         "ghost"
                                                                         "transparent"
                                                                         "minimal"
                                                                         "faint"
                                                                         "convex"
                                                                         "soft"
                                                                         "soft-classic"
                                                                         "solid"
                                                                         "solid-classic"}
                                                                       (name surface)))
                                                      "flex"
                                                      "none")}
                      :data-ks-transition ""}
                     (when (contains? #{"soft-classic" "solid-classic"}
                                      surface)
                       {:class [:ks-button2 surface]
                        :style {:position :relative
                                "--surface" (str "\"" surface "\"")}})
                     (sx {
                          ;; :border :3px:solid:silver
                          ;; :aspect-ratio                        3
                          ;; :w                :140px
                          ;; :h                :55px
                          ;; :ff               "Bradthen"
                          ;; :tt               :u
                          ;; ":--stroke-width" :3px
                          ;; :fs               :$text-size-xxlarge
                          ;; :fw               :$text-weight-bold
                          
                          ;;;;;; buttons
                          "--stroke-transparency" "33%"
                          :fs                   :$text-size-small
                          :display              :relative
                          :cursor               :pointer
                          :gap                  :$icon-enhanceable-gap
                          :padding-inline       :$button-padding-inline
                          :padding-block        :$button-padding-block
                          :w                    :fit-content
                          :tt                   :capitalize
                          :d                    :flex
                          :jc                   :center
                          :ai                   :center
                          :border-radius        :$shape-rounded-medium
                          
                          ;; for the animation wipe
                          ;; :transition-duration :300ms
                          ;; :after:transition-duration         :300ms
                          
                          ;; "--colorway-background-opacity"        0.5
                          ;; "--colorway-background-opacity-hover"  0.5
                          ;; "--colorway-background-opacity-active" 1
                          })
                     (let [stroke stroke]
                       (merge (some->> surface (hash-map :data-ks-surface2))
                              (some->> colorway (hash-map :data-ks-colorway2))
                              (some->> interactive (hash-map :data-ks-interactive))
                              (some->> stroke (hash-map :data-ks-stroke))
                              (some->> shape (hash-map :data-ks-shape)))))
               (let [s "Next" #_(or text (name surface))]
                 s
                 #_(get en->jp s s))

               ;; buttons v
               [icon :east]
               ;; buttons ^
               
               ])
        
        button-lineup 
        (fn [colorway i]
          
          (into [flex-row
                 [:span (sx {:font-size :$text-size-xxxsmall
                             :min-width :55px})
                  (string/capitalize (name colorway))]]
                (for [surface (reverse [:ghost 
                                        :transparent
                                        :minimal
                                        :faint
                                        :soft
                                        :soft-classic
                                        :convex
                                        :solid
                                        :solid-classic])]
                  (let [id (!? :- (str (name surface) 
                                       "-" 
                                       (name colorway)))]
                    
                    (js/setTimeout
                     (fn []
                       (-> id domo/el-by-id domo/click!))
                     (+ 2000 (* i 100)))
                    [div {:surface     surface 
                          :id          id
                          :colorway    colorway
                          :interactive true
                          :shape       :rounded-xlarge
                          :stroke      (when (= surface :minimal) :medium)}]))))
        
        swatch
        (fn [i colorway]
          (when (< i 25)
            [flex-row (sx {:h :50px})
             [:span (sx {:w  :100px
                         :fs :$text-size-small})
              (-> colorway name string/capitalize)]
             [:div {:style {:background-color (str "oklch(0.49 0.18 var(--" (name colorway) "-hue-oklch))")
                            :rotate           :45deg
                            :width            :100px
                            :height           :100px}}]]))]
    
    [:<> 
     (into [flex-col (sx {:p        :3rem
                          :gap      :0.5rem
                          :>div:gap :0.5rem})
            ;; header row
            (into [flex-row
                   (sx {:gap :0.5rem
                        :mbe :1rem
                        :pis "calc(55px + 0.5rem)"})]
                  (for [s (reverse [
                                    :ghost 
                                    :transparent
                                    "Minimal"
                                    "Faint"
                                    "Soft"
                                    "Soft Classic"
                                    "Convex"
                                    "Solid"
                                    "Solid Classic"])]
                    [:span (sx {:font-size             :$text-size-xxxsmall
                                :ta                    :c
                                "not(first-child):width" :72.34px
                                :first-child:min-width :55px})
                     (name s)]))]
           (for [[i colorway] (map-indexed 
                               (fn [i v] [i v])
                               [
                                :blue
                                :sky
                                :ice
                                :teal
                                :cyan 
                                :mint
                                :emerald
                                :green
                                :grass
                                :lime
                                :acid
                                :citron
                                :yellow
                                :maize
                                :gold
                                :apricot
                                :amber
                                :orange
                                :coral
                                :red
                                :rose
                                :pink
                                :magenta
                                :plum
                                :purple
                                :violet
                                :indigo
                                :blue
                                :sky
                                :slate
                                :gray
                                :sand])]
             #_[swatch i colorway]
             [button-lineup colorway i]))

     #_[flex-row
        (sx {:p                                   :2rem
             :gap                                 :2rem
             :>div:gap                            :2rem
             ">.ks-flex-col:nth-child(odd):display" :none})

        [flex-col
         [div {:surface "ghost" 
               :stroke  :hard
               :shape   :rounded-xlarge}]
         [div {:surface "transparent"
               :stroke  :hard}]
         [div {:surface "minimal"
               :stroke  :hard}]
         [div {:surface "faint"}]
         [div {:surface "convex"}]
         [div {:surface "soft"}]
         [div {:surface "soft-classic"
               :shape   :pill }]
         [div {:surface "solid-classic"
               :shape   :pill }]
         [div {:surface "solid"
               :shape   :pill}]]

        [flex-col
         [div {:surface     "ghost"
               :interactive true
               :stroke      :hard
               :shape       :rounded-xlarge
               }]
         [div {:surface     "transparent"
               :interactive true
               :stroke      :hard}]
         [div {:surface     "minimal"
               :interactive true
               :stroke      :hard}]
         [div {:surface     "faint"
               :interactive true}]
         [div {:surface     "convex"
               :interactive true}]
         [div {:surface     "soft"
               :interactive true}]
         [div {:surface     "soft-classic"
               :shape       :pill
               :interactive true}]
         [div {:surface     "solid-classic"
               :interactive true
               :shape       :pill}]
         [div {:surface     "solid"
               :interactive true
               :shape       :pill}]]

        [flex-col
         [div {:surface  "ghost"
               :colorway :cyan
               :stroke   :hard
               :shape    :rounded-xlarge}]
         [div {:surface  "transparent"
               :colorway :cyan
               :stroke   :hard}]
         [div {:surface  "minimal"
               :colorway :cyan
               :stroke   :hard}]
         [div {:surface  "faint"
               :colorway :cyan}]
         [div {:surface  "convex"
               :colorway :cyan}]
         [div {:surface  "soft"
               :colorway :cyan}]
         [div {:surface  "soft-classic"
               :colorway :cyan
               :shape    :pill}]
         [div {:surface  "solid-classic"
               :colorway :cyan
               :shape    :pill}]
         [div {:surface  "solid"
               :colorway :cyan
               :shape    :pill}]]

        [flex-col
         [div {:surface     "ghost"
               :colorway    :cyan
               :interactive true
               :shape       :rounded-xlarge 
               :stroke      :hard}]
         [div {:surface     "transparent"
               :colorway    :cyan
               :interactive true
               :stroke      :hard}]
         [div {:surface     "minimal"
               :colorway    :cyan
               :interactive true
               :stroke      :hard}]
         [div {:surface     "faint"
               :colorway    :cyan
               :interactive true}]
         [div {:surface     "convex"
               :colorway    :cyan
               :interactive true}]
         [div {:surface     "soft"
               :colorway    :cyan
               :interactive true}]
         [div {:surface     "soft-classic"
               :colorway    :cyan
               :shape       :pill
               :interactive true}]
         [div {:surface     "solid-classic"
               :colorway    :cyan
               :shape       :pill
               :interactive true}]
         [div {:surface     "solid"
               :colorway    :cyan
               :interactive true
               :shape       :pill}] ]

        [flex-col
         [div {:surface     "ghost"
               :colorway    :gold
               :interactive true
               :shape       :rounded-xlarge 
               :stroke      :hard}]
         [div {:surface     "transparent"
               :colorway    :gold
               :interactive true
               :stroke      :hard}]
         [div {:surface     "minimal"
               :colorway    :gold
               :interactive true
               :stroke      :hard}]
         [div {:surface     "faint"
               :colorway    :gold
               :interactive true}]
         [div {:surface     "convex"
               :colorway    :gold
               :interactive true}]
         [div {:surface     "soft"
               :colorway    :gold
               :interactive true}]
         [div {:surface     "soft-classic"
               :colorway    :gold
               :shape       :pill
               :interactive true}]
         [div {:surface     "solid-classic"
               :colorway    :gold
               :shape       :pill
               :interactive true}]
         [div {:surface     "solid"
               :colorway    :gold
               :interactive true
               :shape       :pill}]]   

        [flex-col
         [div {:surface     "ghost"
               :colorway    :sand
               :interactive true
               :shape       :rounded-xlarge 
               :stroke      :hard}]
         [div {:surface     "transparent"
               :colorway    :sand
               :interactive true
               :stroke      :hard}]
         [div {:surface     "minimal"
               :colorway    :sand
               :interactive true
               :stroke      :hard}]
         [div {:surface     "faint"
               :colorway    :sand
               :interactive true}]
         [div {:surface     "convex"
               :colorway    :sand
               :interactive true}]
         [div {:surface     "soft"
               :colorway    :sand
               :interactive true}]
         [div {:surface     "soft-classic"
               :colorway    :sand
               :shape       :pill
               :interactive true}]
         [div {:surface     "solid-classic"
               :colorway    :sand
               :shape       :pill
               :interactive true}]
         [div {:surface     "solid"
               :colorway    :sand
               :interactive true
               :shape       :pill}]]
        

        [flex-col
         [div {:surface     "ghost"
               :colorway    :slate
               :interactive true
               :shape       :rounded-xlarge 
               :stroke      :hard}]
         [div {:surface     "transparent"
               :colorway    :slate
               :interactive true
               :stroke      :hard}]
         [div {:surface     "minimal"
               :colorway    :slate
               :interactive true
               :stroke      :hard}]
         [div {:surface     "faint"
               :colorway    :slate
               :interactive true}]
         [div {:surface     "convex"
               :colorway    :slate
               :interactive true}]
         [div {:surface     "soft"
               :colorway    :slate
               :interactive true}]
         [div {:surface     "soft-classic"
               :colorway    :slate
               :shape       :pill
               :interactive true}]
         [div {:surface     "solid-classic"
               :colorway    :slate
               :shape       :pill
               :interactive true}]
         [div {:surface     "solid"
               :colorway    :slate
               :interactive true
               :shape       :pill}]]
        
        [flex-col
         [div {:surface     "ghost"
               :colorway    :blue
               :interactive true
               :shape       :rounded-xlarge 
               :stroke      :hard}]
         [div {:surface     "transparent"
               :colorway    :blue
               :interactive true
               :stroke      :hard}]
         [div {:surface     "minimal"
               :colorway    :blue
               :interactive true
               :stroke      :hard}]
         [div {:surface     "faint"
               :colorway    :blue
               :interactive true}]
         [div {:surface     "convex"
               :colorway    :blue
               :interactive true}]
         [div {:surface     "soft"
               :colorway    :blue
               :interactive true}]
         [div {:surface     "soft-classic"
               :colorway    :blue
               :shape       :pill
               :interactive true}]
         [div {:surface     "solid-classic"
               :colorway    :blue
               :shape       :pill
               :interactive true}]
         [div {:surface     "solid"
               :colorway    :blue
               :interactive true
               :shape       :pill}]]
        ]
     
     #_[showcase (!? (showcase/opts kushi.ui.button/button
                                    kushi.ui.button.demo/demos))]
     
     
     ]


    ;; [div {:surface "transparent" :colorway :red :interactive true}]
    ;; [div {:surface "faint" :colorway :red :interactive true}]
    ;; [div {:surface "soft" :colorway :red :interactive true}]
    
    ;; [div {:surface :ghost}]
    
    ;; [:div (sx :p--10px :color--red) [div {:text "hi" :surface :transparent}]]
    ;; [:div (sx :p--10px :color--teal) [div {:text "hi" :surface :faint}]]
    ;; [:div (sx :p--10px :color--purple) [div {:text "hi" :surface :soft}]]
    
    
    ;; [div {:surface "solid"}]
    #_[flex-col (sx :.bones
                    {
                     :p                                   :20px
                     ;;  :gtr                                 "repeat(10, 50px)"
                     ;;  :gtc                                 :1fr:1fr:1fr
                     :gap                                 :3rem
                     :>.sidenav-close-icon:c              :cyan
                     })


       #_#_#_#_#_

                 [icon-button {:text-size :xxxlarge :surface :soft} :star]

               [icon-button {:text-size :xxxlarge :surface :transparent} :star]

             [icon-button {:text-size :xxxlarge :surface :soft :colorway :red} :star]

           [icon {:text-size :xxxlarge} :star]

         [icon {:text-size :xxxlarge :colorway :red} :star]

       #_[box {:shadow          :xxlarge
               :stroke          :xsoft
               :surface         :transparent
               :shadow-strength :50%
               :class           (css :w--300px :h--300px)}
          "hi"]

       #_[:span (sx2 {
                      ;; :shadow          :xxlarge
                      ;; :shadow-strength :50%
                      ;; :surface         :transparent
                      :class       [:sample]
                      :display     :flex-col-center
                      :ai          :center
                      :text-size   :xxlarge
                      :text-weight :wee-bold
                      :tt          :u
                      :w           :px
                      :h           :300px})
          "span"]

       #_[:span
          ;; You should be able to do stroke and shape without surface?
          (sx2 {:id                      :foo
                :name                    :wtf
                :display                 :flex-row-space-between
                :position                :absolute-center
                :colorway                :cyan
                :surface                 :transparent
                :stroke                  :hard
                :shape                   :rounded-xsmall 
                :padding                 :10px
                :shadow                  :xxlarge 
                :shadow-strength         :60%
                ;; :margin                  :*1
                ;; :gap                     :*3
                :gap                     :3*
                ;; :bgi                     :$background-grid
                :w                       :500px
                :h                       :300px
                :>.sidenav-close-icon:c  :red
                ;; ".bones &"                 {:outline        :3px:solid:lime
                ;;                             :outline-offset :10px}
                "[data-ks]:c"              :cyan
                "[data-ks=\"wtf\"]:c"        :blue
                :fs                      :$text-size-xxxlarge
                :.aa:bgc                 :pink
                " a.wtf:bgc"               :salmon
                :--background-grid-size  :16px
                :--background-grid-color :$red-500}
               )
          [:span "🍒"]
          [:span "👺"]
          [:span "🦑"]
          [:strong 
           (sx2 {:surface  :solid
                 :colorway :blue
                 :shape    :rounded})
           "COLOR"]]

       #_[:span.flex-row-start
          [radio {:name   :g
                  :id     :foo
                  :text-weight :thin}]
          [label {:for :foo} "hi"]]


       #_[:div.flex-row-start.absolute-inline-start-inside
          (sx :gap--1rem
              :p--100px
              :fs--$text-size-xxxlarge)
          ;; [switch]
          ;; [switch {:colorway :neutral}]
          
          #_[switch {:colorway :accent :size :xxlarge}]

          #_[switch {:colorway    :accent
                     :size      :xxlarge
                     :thumb-attrs (sx :bgi--$convex :dark:bgi--$convex-3)}]

          #_[thumb {:surface :soft-classic
                    ;;  :stroke  :soft
                    :size  :xxxlarge}
             ]

          [switch
           (mrj
            (sx #_[:--switch-inset-gap :2px]
             #_[:--switch-thumb-scale-factor :1.25])
            {:size            :xxxlarge
             ;; :thumb-label-on  "ON"
             ;; :thumb-label-off "OFF"
             ;; :thumb-scale-factor 1.2
             
             :track-label-on  "ON"
             :track-label-off "OFF"

             :track-inset-gap   :2px
             :thumb-attrs       (mrj {:surface     :convex
                                      ;; TODO - why not :soft working?
                                      :stroke      :medium #_[[:2px :$red-500] [:2px :$orange-300] [:2px :$yellow-300]]
                                      :shadow :large}
                                     (sx #_:bgc--red
                                      #_[:--shadow-strength :50%]
                                      ))})]

          ;; [switch {:colorway :positive}]
          ;; [switch {:colorway :warning}]
          ;; [switch {:colorway :negative}]
          ;; [button {:surface :outline :size :large} "Click"]
          ;; [button {:surface :classic :size :large} "Click"]
          
          #_[button
             (merge-attrs 
              {:at               (at)
               :surface          :soft
               :size           :xxxlarge
               :shape          :pill
               :colorway         :accent
               :stroke           :medium #_[[:2px :$sand-300] [:2px :$green-300]]
               :stroke-width     :3px

               ;;  :stroke-align     :outside
               ;;  :shadow      :large
               ;;  :shadow-color     :$blue-500
               ;;  :shadow-strength  :medium
               
               #_["5px 5px 10px currentColor"]
               :style {"--shadow-color"    "var(--red-500)"
                       "--shadow-strength" "30%"}})
             "Click"]

          #_[button
             (merge-attrs 
              {:surface      :transparent
               :size       :large
               :shape      :pill
               :colorway     :accent
               ;;  :stroke [[:4px :$sand-300] [:2px :$green-300]]
               :stroke       :soft
               ;;  :stroke-width "5px"
               :stroke-align :inside
               ;;  :shadow  ["5px 5px 10px pink"]
               })
             "Click"]
          
          #_[button
             {:at           (at)
              :surface      :solid
              :size       :large
              :shape      :pill
              :colorway     :accent
              ;; :stroke [[:4px :$sand-300] [:2px :$green-300]]
              :stroke       :medium
              :stroke-width "3px"
              :stroke-align :outside
              :shadow  ["0 10px 10px -0px pink"]
              :class        (css ["--stroke-transparency-mix-color" :$green-600])}
             "Click"]

          ;; [button {:surface :soft-classic :size :large :shape :pill :colorway :accent} "Click"]
          ;; [thumb {:surface :outline :size :xxlarge :stroke-width :1px}]
          ;; [thumb {:surface :soft-classic :size :xxlarge #_#_:stroke-width :1px}]
          ;; [thumb {:surface :solid-classic :size :xxlarge #_#_:stroke-width :1px}]
          #_(let [
                  stroke-align :inside

                  strokes      (for [c ["magenta" "red" "orange" "yellow" "lime" "green" "blue" "purple"]]
                                 [:2px (str "var(--" c "-700)")])

                  shadows       (util/stepped-shadows 
                                 {:colors            [:$red-500 :$orange-500 :$yellow-500 :$lime-500 :$green-500 :$blue-500 :$purple-500]
                                  :blur              :10px
                                  ;;  :spread  :10px
                                  :start-y           10
                                  :end-y             100
                                  :start-x           10
                                  :end-x             100
                                  :start-opacity     1
                                  :end-opacity       0.1
                                  :opacity-mix-color :white
                                  })
                  shadows-2     nil #_(util/stepped-shadows {:colors  ["yellow"
                                                                       "orange"
                                                                       "red"
                                                                       "magenta"]
                                                             :blur    :10px
                                                             :start-y -10
                                                             :end-y   -50
                                                             :start-x -10
                                                             :end-x   -50
                                                             })

                  ;; shadows      [[:-15px :15px :20px :aqua]
                  ;;               [:15px :-15px :20px :cyan]]
                  shadows (concat shadows shadows-2)]
              [:div {:style {:width      :200px
                             :height     :200px
                             :background :aliceblue
                             :box-shadow (util/box-shadow
                                          {:shadows      shadows
                                           :strokes      strokes
                                           :stroke-align stroke-align
                                           })}
                     }])]

       #_#_#_#_#_#_
                   [box {:surface  :solid
                         :colorway :accent} "hi"]
                 [card [:div (sx :.flex-row-start
                                 :ai--stretch
                                 :gap--0.8em)
                        #_[:div (sx :.rounded
                                    :position--relative
                                    :.transition
                                    :overflow--hidden
                                    :dark:bgc--$neutral-850
                                    :bgc--$neutral-200
                                    :w--3.5em
                                    :h--3.5em)
                           [:span (sx :.absolute-centered
                                      [:transform "translate(0, 0.045em)"]
                                      :display--block
                                      :scale--2.55)
                            "🐻‍❄"]]
                        [avatar {:surface :faint-outline
                                 ;;  :size  :xxlarge
                                 ;;  :src     avatar-1
                                 }
                         "🐻‍❄"]
                        [:section (sx :.flex-col-space-around
                                      :jc--sa) 
                         [:p (sx :fs--1.25em :fw--$wee-bold) "Polar Bear"] 
                         [:p (sx :.foreground-color-secondary!) "polar.bear@example.com"]]]]
               [avatar 
                {:surface :solid
                 :size  :xxlarge
                 :src     avatar-1}
                "JC"]
             [tag {:end-enhancer :east} "Bingo " [link {:href "google.com"} "& more"]]
           [:span (sx :.flex-row-start :gap--1em)
            [checkbox {:id     :bar
                       :text-weight :thin
                       :class  (css :.xxlarge)}] 
            [label {:for          :bar
                    :class        (css :.xxlarge)
                    :end-enhancer :star} "check me"]
            #_[icon {:size :xxlarge} :star]]
         

         [checkbox-group {:group-id "foo"
                          :choices  ["Yes" "No" "Maybe"]
                          :surface  :outline
                          ;; :display  [:flex :column :flex-start :center]
                          ;; :gap      0
                          :class    (css :d--grid
                                         :gtc--1fr:1fr
                                         :>div:p--1rem)}]

       #_[checkbox {:text-weight :thin}
          "Star"
          [icon {:at         (at)
                 :colorway   :red
                 :size     :xxxlarge
                 :icon-style :sharp
                 :inert      true
                 :id         :foo}
           :star]]
       
       #_[box (merge-attrs 
               {:shape      :rounded
                :display      [:flex :row :space-around :center]
                :stroke       :medium
                :stroke-width "2px"
                ;; :shadow :medium
                :at (at)}
               (sx :w--300px :h--200px))
          [:div "1"]
          [:div "2"]
          [:div "3"]]

       #_[tag
          {:start-enhancer :check-circle,
           :colorway       :accent,
           :surface        :minimal
           :stroke         :xsoft}
          "Passing"] 

       #_[callout
          {:header-icon     :check-circle
           :size          :xlarge
           :colorway        :positive
           :header-text     "Your transaction was successful."
           :close-button?   true
           :close-button-fn (fn [] [:div "hi"])}]

       #_[callout
          {:size     :xlarge
           :colorway :positive
           :surface  :soft
           :stroke   :hard}
          [flex-row (sx {:jc :sb})
           [icon :check-circle]
           "Your transaction was successful."
           [button {:colorway :positive
                    :surface  :transparent
                    :shape    :circle
                    :stroke   :hard}
            "GO"]
           [icon-button 
            {:colorway :positive
             :surface  :transparent
             :shape    :pill
             :stroke   :hard
             :packing  :compact
             :text-weight   :bold
             :size     :medium}
            :close]]]])


;; (? (:colorway kushi.ui.variants/variants-by-custom-opt-key))


 

  #_[switch
     (mrj
      (sx #_[:--switch-inset-gap :2px]
       #_[:--switch-thumb-scale-factor :1.25])
      {
       :on? true
       :size            :xxxlarger 
       ;; :thumb-label-on  "ON"
       ;; :thumb-label-off "OFF"
       ;; :thumb-scale-factor 1.2
       
       ;; :track-label-on  "ON"
       ;; :track-label-off "OFF"
       
       ;; :track-inset-gap   :2px
       
       :thumb-attrs       (mrj {
                                :surface     :minimal
                                ;; TODO - why not :soft working?
                                :stroke      :medium #_[[:2px :$red-500] [:2px :$orange-300] [:2px :$yellow-300]]
                                :shadow :large}
                               (sx #_:bgc--red
                                #_[:--shadow-strength :50%]
                                ))
       })]

  #_(into [:div.absolute-centered.flex-col-space-between (sx :gap--5rem)]
          (for [k [:xxxsmall :xxsmall :xsmall :small :medium :large :xlarge :xxlarge :xxxlarge]]
            [button (mrj {:packing         :roomy
                          :size          :xsmall
                          :shape         :rounded
                          :surface         :minimal
                          :shadow     k}
                         (sx :p--20px:40px
                             :min-width--200px
                             [:--color :$cyan-400]
                             [:--shadow-strength :40%]))
             k]))
  




#_(defn my-radio [m]
  (let [id (str (:name m) "-radio-group_" (:value m) "-choice")]
    [:div.flex-row-start
     {:class     (css :.pointer
                      :pb--0.33em
                      :pi--0.5em:0.75em
                      :w--fit-content)
      :as       :section
      :colorway :accent
      :size     :xxlarge
      :shape    :pill
      :text-weight   :extra-light
      :surface  :minimal}
     [radio (assoc m :id id)]

     ;; make this label component
     [label (merge-attrs
             (sx :pis--0.5em)
             {:text-weight         :bold
              :start-enhancer 8 #_[icon {:text-weight :light} (:icon m)]
              :for             id})
      (string/capitalize (:value m))]]))


  ;; (? (m/validate [:cat
  ;;                 [:? [:map
  ;;                      [:foo {:optional true} :int]
  ;;                      [:bar {:optional true} :string]]]
  ;;                 [:* [:not :map]]]
  ;;                [{:foo 8} 2 3 4]))
  
  #_[:div (sx :m--100px)
     #_[button {:start-enhancer :pets} "Click"]
     [icon {:ns           (at)
            :colorway     :red
            :size       :xxxlarge
            :text-weight       :bolds
            :icon-style   :sharp
            :icon-filled? true
            :inert       true
            :id           :foo}
      :star]]

  #_[:div (sx :m--100px)
     [button {:colorway       :red 
              :start-enhancer :pets
              :surface        :solid}
      "Click"]]


  ;; (? (uic-showcase-map3 kushi.ui.icon.demo/demos))
  
  ;; Take stock on conventions and see if you can use defui macro
  ;; maybe see if you can make macro to rewrite fns into macro
  ;; write outer let body in macro then insert postwalked-body?
  ;; make it easy to fall back to expanded defn if you want
  
  ;; if macro works, try conditional compiltion for another framework 
  

  ;; Change kpg to showcase
  ;; switch
  ;; slider
  ;; popover
  

  #_[button
     {
      :colorway     :accent
      :surface      :solid
      :size       :small
      :end-enhancer [spinner {:spinner-type :donut}]}
     "Play"]

  ;; This should be box or flex with flex and default inert
  ;; [box {:--border-color :cyan
  ;;       :--smile-factor :clown}]
  #_[radio-group 
     {:class    (css :w--fit-content
                     :p--1em
                     :flex-direction--column
                     :ai--flex-start)
      :surface  :solid
      ;; maybe that is good?
      :flexbox  :row:start:center
      ;;  :shape   :rounded-3 ?
      :shape  :rounded-xlarge
      :size   :large
      :colorway :accent
      :id       "foo"
      :default  "Yes"
      :choices  ["Yes" "No" "Maybe"]}]

  #_[icon {:at          (at)
           :position    :absolute-centered
           :colorway    :red
           :size      :xxxlarge
           :text-weight      :bold
           :icon-style  :sharp
           :icon-filled true
           :inert       true
           :id          :foo}
     :star]

  #_[button
     {:at           (at)
      :size       :xxlarge
      :position     :absolute-centered
      ,
      :colorway     :accent,
      :surface      :outline,
      ;;  :stroke-width :5px
      :end-enhancer [spinner {:spinner-type :donut}]
      :style        {"--outlined-button-stroke-width" :10px}}
     "Play"]

  #_[tag
     {:at           (at)
      :size       :xxlarge
      :position     :absolute-centered
      :colorway     :accent,
      :surface      :outline,
      ;;  :stroke-width :5px
      :end-enhancer [spinner {:spinner-type :donut}]
      :style        {"--outlined-button-stroke-width" :10px}}
     "Play"]

  #_[button
     {:surface  :solid
      :colorway :blue
      :id       "foo"
      
      :ns       (at)}
     "hi"]

  #_[icon-button
     {:surface  :solid
      :position :absolute-centered
      :colorway :blue
      :id       "foo"
      
      :ns       (at)}
     :east]

  
  ;; radios
  #_[:span
     [:span.flex-row-start [radio {:name "foo" :id "foo-hi"}] [label {:for "foo-hi"} "hi"]]
     [:span.flex-row-start [radio {:name "foo" :id "foo-bye"}] [label {:for "foo-bye"} "bye"]]]

  ;; Basic example call
  #_[radio-group {
                  :group-id       "foo"
                  :size         :xxxlarge
                  :choices        ["Yes" "No" "Maybe"]
                  :default-choice "Yes"}]


  ;; Basic example call, with maps
  #_[radio-group {:group-id "foo"
                  :choices  [{:label "Yes"
                              :value "12"}
                             {:label "No"
                              :value "2"}
                             {:label "Maybe"
                              :value "3"}]}]

  #_(let [id (fn [m] 
               [box (merge {:size  :xlarge
                            :surface :solid
                            :class   (css :w--100px :h--100px)}
                           m)
                [box {:position :absolute-centered}
                 "Hi"]])]
      [box {:position      :absolute-block-end-inside
            :colorway      :blue
            :surface       :solid
            :data-ks-debug :foobar
            :ns            (at)
            :class         (css :w--100px :h--100px)}
       "HI"]

      #_[box
         {:class    (css {"--foo" "0 5px 10px green"
                          :w    :500px
                          :h    :500px})

          ;; Could have both elevation and shadows, with shadows overriding
          ;; :elevated 5
          ;; :elevation 5
          
          ;; :shadow [:--elevated-5 :--my-custom-shadow]
          ;; :shadow :--elevated-5
          ;; :shadows :--elevated-5
          
          ;; These values need to be checked at runtime or it kills border and shadow, if bad value
          :shadows  ["var(--elevated-3)" "-15px -15px 10px purple"]
          :position :absolute-centered
          :surface  :faint-outline
          ;; :colorway :neutral
          }
         [id {:position :absolute-inline-start-inside
              :colorway :red}]
         [id {:position :absolute-inline-end-inside
              :colorway :green}]
         [id {:position      :absolute-block-end-inside
              :colorway      :blues
              :data-ks-debug :foobar}]
         "Child 1"])



  ;; grid
  
  ;; collapse
  ;; DONE when you do demo
  #_[collapse
     {:label-collapsed "Collapsable section label"}
     [:p "Child 1"]
     [:p "Child 2"]]

  #_[collapse
     {:label-collapsed "Click to expand"
      :label-expanded  "Click to collapse"}
     [:p "Child 1"]
     [:p "Child 2"]]

  #_[collapse
     {:label-collapsed "Collapsable section label"
      :icon-position   :end}
     [:p "Child 1"]
     [:p "Child 2"]]

  #_[collapse
     (merge-attrs (sx :bbe--1px:solid:$neutral-800
                      :dark:bbe--1px:solid:$neutral-400) 
                  {:label-collapsed "Collapsable section label "
                   :header-attrs    (sx :bbs--1px:solid:$neutral-800
                                        :dark:bbs--1px:solid:$neutral-400)})
     [:p "Child 1"]
     [:p "Child 2"]] 

  #_[collapse
     (merge-attrs (sx :bbe--1px:solid:$neutral-800
                      :dark:bbe--1px:solid:$neutral-400) 
                  {:-label        "Collapsable section label "
                   :-header-attrs (sx :bbs--1px:solid:$neutral-800 
                                      :dark:bbs--1px:solid:$neutral-400)})
     [:p "Child 1"]
     [:p "Child 2"]]


  


  



  #_[avatar {:surface :solid
             :size  :xxlarge
             :src     avatar-1}
     "JC"]



  ;; Sorting out 
  #_[flex-col
     (sx :max-width--1200px
         :p--24px
         :gap--48px
         :fs--$text-size-xxlarge
         :_p:fs--$text-size-large
         [:bgi "linear-gradient(to bottom right, silver, transparent)"])

     [callout
      {:surface :faint}
      [flex-row (sx {:jc :sb})
       [icon {:colorway :negative} :info]
       [:span "Please check out the " [link "new features"]]
       [icon-button 
        {:shape   :pill
         :surface :transparent
         :packing :xcompact}
        :close]]]
     
     [callout
      {:surface  :solid
       :colorway :accent
       ;;  :stroke   :xhard
       }
      [flex-row (sx {:jc :sb})
       [icon :info]
       [:span "Please check out the " [link "new features"]]
       [icon-button 
        {:shape    :pill
         :surface  :solid
         :colorway :accent
         :packing  :xcompact}
        :close]]]
     
     ]


  #_[flex-col
     (sx :max-width--1200px
         :p--24px
         :gap--48px
         :fs--$text-size-xxlarge
         :_p:fs--$text-size-large
         [:bgi "linear-gradient(to bottom right, silver, transparent)"])

     [:p "No colorway set"]
     [callout
      {:surface :faint}
      [flex-row (sx {:jc :sb})
       [icon {:colorway :negative} :info]
       [:span "Please check out the " [link "new features"]]
       [icon-button 
        {:shape    :pill
         :surface  :transparent
         :packing  :xcompact}
        :close]]]


     [:p "No colorway set"]
     [callout
      {:surface :faint}
      [flex-row (sx {:jc :sb})
       [icon :info]
       [:span "Please check out the " [link "new features"]]
       [icon-button 
        {:shape    :pill
         :surface  :transparent
         :packing  :xcompact}
        :close]]]


     [:p "Colorway neutral"]
     [callout
      {:surface  :faint
       :colorway :neutral}
      [flex-row (sx {:jc :sb})
       [icon :info
        ]
       [:span "Please check out the " [link "new features"]]
       [icon-button 
        {:shape    :pill
         :surface  :faint
         :packing  :xcompact}
        :close]]]
     
     [:p "Colorway accent"]
     [callout
      {:surface  :faint
       :colorway :accent}
      [flex-row (sx {:jc :sb})
       [icon :info]
       [:span "Please check out the " [link "new features"]]
       [icon-button 
        {:shape    :pill
         :surface  :faint
         :colorway :accent
         :packing  :xcompact}
        :close]]]

     [:p "Colorway accent"]
     [callout
      {:surface  :transparent
       :colorway :accent
       :stroke   :xhard}
      [flex-row (sx {:jc :sb})
       [icon :info]
       [:span "Please check out the " [link "new features"]]
       [icon-button 
        {:shape    :pill
         :surface  :transparent
         :colorway :accent
         :packing  :xcompact}
        :close]]]

     [:p "Colorway accent"]
     [callout
      {:surface  :solid
       :colorway :accent
       ;;  :stroke   :xhard
       }
      [flex-row (sx {:jc :sb})
       [icon :info]
       [:span "Please check out the " [link "new features"]]
       [icon-button 
        {:shape    :pill
         :surface  :solid
         :colorway :accent
         :packing  :xcompact}
        :close]]]

     [:p "Colorway accent, surface solid"]
     [icon-button {:colorway :accent :surface :solid :shape :pill} :east]

     [:p "No colorway"]
     [icon-button :east]

     [:p "Colorway neutral"]
     [icon-button {:colorway :neutral} :east]

     [:p "Colorway accent"]
     [icon-button {:colorway :accent} :east]

     [:p "Colorway accent, surface faint"]
     [icon-button {:colorway :accent :surface :faint :shape :pill} :east]
     
     [:p "No colorway"]
     [button {:end-enhancer :east :surface :minimal} "Next"]

     [:p "No colorway"]
     [button {:stroke :hard :end-enhancer :east :surface :transparent} "Next"]

     [:p "No colorway"]
     [button {:stroke :hard :end-enhancer :east :surface :ghost} "Next"]

     [:p "No colorway"]
     [button {:end-enhancer :east} "Next"]

     [:p "No colorway"]
     [button {:end-enhancer :east} "Next"]

     [:p "No colorway"]
     [button {:end-enhancer :east} "Next"]
     
     [:p "Colorway neutral"]
     [button {:end-enhancer :east :colorway :neutral} "Next"]

     [:p "Colorway accent"]
     [button {:end-enhancer :east :colorway :accent} "Next"]

     [:p "Colorway accent"]
     [button {:end-enhancer :east :colorway :accent :surface :solid} "Next"]]


  #_[flex-col
     (sx :gap--1rem :_div:gap--1rem :.position-absolute-centered)
     
     ;; TRANSPARENT
     [flex-row
      (into [flex-col
             (for [colorway [:green :cyan :blue :red]]
               [button {:size         :medium
                        :shape        :pill
                        :stroke       :medium
                        :stroke-align :outside
                        :stroke-color :green
                        :stroke-width :1px
                        :surface      :transparent
                        :colorway     colorway
                        :shadow       :xsmall
                        :shadow-color colorway}
                "Next"])])
      #_[flex-col
         [:button {:class (css :.ks-button
                               ["--stroke-width" :3px]
                               ["--shadow-color" :$red-500]
                               :.shadow-large
                               :.stroke-medium
                               ;;  :.stroke-align-outside
                               :.surface-transparent
                               :.colorway-positive
                               :.shadow-color-positive
                               :.shape-rounded)
                   }
          "Next"]
         [:button {:class (css :.ks-button ["--stroke-width" :1px] :.shadow-large :.stroke-medium :.surface-transparent :.colorway-yellow :.shape-rounded)} "Next"]
         [:button {:class (css :.ks-button ["--stroke-width" :1px] :.shadow-large :.stroke-medium :.surface-transparent :.colorway-blue :.shape-rounded)} "Next"]
         [:button {:class (css :.ks-button ["--stroke-width" :1px] :.shadow-large :.stroke-medium :.surface-transparent :.colorway-red :.shape-rounded)} "Next"]]]    


     
     ;; MINIMAL
     [flex-row
      (sx :ai--flex-start :gap--1rem)
      [flex-col (sx :p--20px :gap--4rem)
       [button {:size     :medium
                :stroke   :medium
                :shadow   :medium
                :surface  :minimal
                :colorway :green} "Next"]
       [button {:size     :medium
                :stroke   :medium
                :shadow   :medium
                :surface  :minimal
                :colorway :cyan} "Next"]
       [button {:size     :medium
                :stroke   :medium
                :shadow   :medium
                :surface  :minimal
                :colorway :blue} "Next"]
       [button {:size     :medium
                :stroke   :medium
                :shadow   :medium
                :surface  :minimal
                :colorway :red} "Next"]]
      [flex-col (sx :p--20px :gap--4rem)
       [:button {:class           (css :.ks-button
                                       ["--stroke-width" :1px]
                                       :.shadow-medium
                                       :.stroke-medium
                                       ;;  :.stroke-align-outside
                                       :.surface-minimal
                                       :.colorway-green
                                       :.shape-rounded)
                 :data-ks-surface ""
                 ;;  :data-ks-shadow  ""
                 }
        "Next"]
       [:button {:class (css  :.ks-button ["--stroke-width" :1px] :.shadow-medium :.stroke-medium :.surface-minimal :.colorway-yellow :.shape-rounded)} "Next"]
       [:button {:class (css  :.ks-button ["--stroke-width" :1px] :.shadow-medium :.stroke-medium :.surface-minimal :.colorway-blue :.shape-rounded)} "Next"]
       [:button {:class (css  :.ks-button ["--stroke-width" :1px] :.shadow-medium :.stroke-medium :.surface-minimal :.colorway-red :.shape-rounded)} "Next"]]]    


     #_
       ;; FAINT
       [flex-row
        (sx :ai--flex-start :gap--1rem)
        [:div (sx :.flex-col-start :p--20px :gap--1rem)
         [button {:size   :medium
                  :surface  :faint
                  :colorway :green} "Next"]
         [button {:size   :medium
                  :surface  :faint
                  :colorway :cyan} "Next"]
         [button {:size   :medium
                  :surface  :faint
                  :colorway :blue} "Next"]
         [button {:size   :medium
                  :surface  :faint
                  :colorway :red} "Next"]]
        [flex-col (sx :p--20px :gap--1rem)
         [:button (sx ".ks-button" :.surface-faint :.colorway-green :.shape-rounded) "Next"]
         [:button (sx ".ks-button" :.surface-faint :.colorway-yellow :.shape-rounded) "Next"]
         [:button (sx ".ks-button" :.surface-faint :.colorway-blue :.shape-rounded) "Next"]
         [:button (sx ".ks-button" :.surface-faint :.colorway-red :.shape-rounded) "Next"]]]


     #_
       ;; SOFT
       [:div
        (sx :.flex-row-start :ai--flex-start :gap--1rem)
        [:div (sx :.flex-col-start :p--20px :gap--1rem)

         [button {:surface  :soft
                  :size   :xxxlarge
                  :shape    :pill
                  :colorway :green
                  :stroke   :hard}
          "Next"]
         #_#_#_
               [button {:size   :medium
                        :surface  :soft
                        :colorway :cyan} "Next"]
             [button {:size   :medium
                      :surface  :soft
                      :colorway :blue} "Next"]
           [button {:size   :medium
                    :surface  :soft
                    :colorway :red} "Next"]]
        [:div (sx :.flex-col-start :p--20px :gap--1rem)
         [:button (sx :.ks-button :.surface-soft :.colorway-green :.shape-rounded) "Next"]
         #_#_#_
               [:button (sx :.ks-button :.surface-soft :.colorway-yellow :.shape-rounded) "Next"]
             [:button (sx :.ks-button :.surface-soft :.colorway-blue :.shape-rounded) "Next"]
           [:button (sx :.ks-button :.surface-soft :.colorway-red :.shape-rounded) "Next"]]]

     
     #_
       ;; SOFT CLASSIC
       [:div
        (sx :.flex-row-start :ai--flex-start :gap--1rem)
        [:div (sx :.flex-col-start :p--20px :gap--1rem)
         [button {:size   :medium
                  :surface  :soft-classic
                  :colorway :green} "Next"]
         [button {:size   :medium
                  :surface  :soft-classic
                  :colorway :cyan} "Next"]
         [button {:size   :medium
                  :surface  :soft-classic
                  :colorway :blue} "Next"]
         [button {:size   :medium
                  :surface  :soft-classic
                  :colorway :red} "Next"]]
        [:div (sx :.flex-col-start :p--20px :gap--1rem)
         [:button (sx ".ks-button" :.surface-soft-classic :.colorway-green :.shape-rounded) "Next"]
         [:button (sx ".ks-button" :.surface-soft-classic :.colorway-yellow :.shape-rounded) "Next"]
         [:button (sx ".ks-button" :.surface-soft-classic :.colorway-blue :.shape-rounded) "Next"]
         [:button (sx ".ks-button" :.surface-soft-classic :.colorway-red :.shape-rounded) "Next"]]]

     #_
       ;; SOlID
       [:div
        (sx :.flex-row-start :ai--flex-start :gap--1rem)
        [:div (sx :.flex-col-start :p--20px :gap--1rem)
         [button {:size   :medium
                  :surface  :solid
                  :colorway :green} "Next"]
         [button {:size   :medium
                  :surface  :solid
                  :colorway :cyan} "Next"]
         [button {:size   :medium
                  :surface  :solid
                  :colorway :blue} "Next"]
         [button {:size   :medium
                  :surface  :solid
                  :colorway :red} "Next"]]
        [:div (sx :.flex-col-start :p--20px :gap--1rem)
         [:button (sx ".ks-button" :.surface-solid :.colorway-green :.shape-rounded) "Next"]
         [:button (sx ".ks-button" :.surface-solid :.colorway-yellow :.shape-rounded) "Next"]
         [:button (sx ".ks-button" :.surface-solid :.colorway-blue :.shape-rounded) "Next"]
         [:button (sx ".ks-button" :.surface-solid :.colorway-red :.shape-rounded) "Next"]]]

     #_
       ;; SOLID CLASSIC
       [:div
        (sx :.flex-row-start :ai--flex-start :gap--1rem)
        [:div (sx :.flex-col-start :p--20px :gap--1rem)
         [button {:size   :medium
                  :surface  :solid-classic
                  :colorway :green} "Next"]
         [button {:size   :medium
                  :surface  :solid-classic
                  :colorway :cyan} "Next"]
         [button {:size   :medium
                  :surface  :solid-classic
                  :colorway :blue} "Next"]
         [button {:size   :medium
                  :surface  :solid-classic
                  :colorway :red} "Next"]]
        [:div (sx :.flex-col-start :p--20px :gap--1rem)
         [:button (sx ".ks-button" :.surface-solid-classic :.colorway-green :.shape-rounded) "Next"]
         [:button (sx ".ks-button" :.surface-solid-classic :.colorway-yellow :.shape-rounded) "Next"]
         [:button (sx ".ks-button" :.surface-solid-classic :.colorway-blue :.shape-rounded) "Next"]
         [:button (sx ".ks-button" :.surface-solid-classic :.colorway-red :.shape-rounded) "Next"]]]
     
     ]



  #_[:div 
     ;;  [icon
     ;;   (merge-attrs
     ;;    {:start-enhancer [icon :phone]
     ;;     :size           :xxxlarge
     ;;     :text-weight         :thin}
     ;;    (sx :fs--98px))
     ;;   "star"]
     
     [button
      {:start-enhancer 8 #_[icon :phone]
       :size           :xxlarge
       :text-weight         :bold}
      "Phone"]

     #_[flex-col-start (merge-attrs (sx :gap--1em :p--2rem)
                                    {:as :section})
        [my-radio {:name  :baz
                   :icon  :email
                   :value "email"}]
        [my-radio {:name  :baz
                   :icon  :phone
                   :value "phone"}]]]

  #_[:div
     [button
      {
       ;; 
       :end-enhancer #_[icon :east]
       [propeller]    }
      "Play"]
     
     [button
      {
       ;; 
       :end-enhancer #_[icon :east]
       [donut]        }
      "Play"]
     
     [button
      {
       ;; 
       :end-enhancer #_[icon :east]
       [thinking]     }
      "Play"]]

  ;; for pallette generation dev
  #_(js/setTimeout
     (fn []
       (dotimes [n (-> okstate deref :levels count)]
         (adjust-slider! {:pallette-idx pallette-idx 
                          :scale-key    :chroma-scale
                          :scale-idx    n}))
       #_(? (domo/qs "[data-scale='chroma'][data-level='450']"))
       )
     2000)

  ;; #_[showcase (? :pp (uic-showcase-map kushi.ui.button/button))]
  
  ;; #_[showcase (uic-showcase-map kushi.ui.spinner/spinner)]
  

  ;; #_[showcase (uic-showcase-map kushi.ui.radio/radio)]
  
  ;; ;; This will auto-generate children
  
  #_[radio-group 
     {:radio-button-attrs {:name    :baz
                           :size   :large
                           :text-weight :bold}
      :choices            ["Email" "Phone" "Mail"]}]
  

  ;; This will auto-generate...
  
  #_[:div (sx :.flex-col-fs :ai--fs :gap--0.75em)
     [:div (sx :.xxxlarge :.bold :.flex-row-fs :gap--0.5em)
      [radio-button {:name           :baz
                     :id             "baz-radio-group_email-choice"
                     :value          "email"
                     :size          :xxxlarge
                     :text-weight        :bold
                     :label-attrs   {}
                     :wrapper-attrs {}}]
      [:label {:for "baz-radio-group_email-choice"}
       "Email"]]
     

     [radio-button {:name    :baz
                    :value   "phone"
                    :label  "phonnne"
                    :size   :xxxlarge
                    :text-weight :bold}]
     [radio-button {:name    :baz
                    :value   "mail"
                    :label  "mailll"
                    :size   :xxxlarge
                    :text-weight :bold}]]


  #_[pane-samples]



;; -----------------------------------------------------------------------------
;; End of experiments from late 2025
;; -----------------------------------------------------------------------------





(defcss "@keyframes jiggle2"
  [:0% {:transform "rotate(0deg) scale(1.55)"}]
  [:18% {:transform "rotate(-5deg) scale(1.55)"}]
  [:36% {:transform "rotate(5deg) scale(1.55)"}]
  [:54% {:transform "rotate(0deg) scale(1.55)"}]
  [:72% {:transform "rotate(5deg) scale(1.55)"}]
  [:90% {:transform "rotate(-5deg) scale(1.55)"}]
  [:100% {:transform "rotate(0deg) scale(1.55)"}])

(defcss ".duh"
  :c--red)

(defn popover-content []
  [:div
   (sx :.flex-row-fs
       :.small
       :position--relative
       :ai--fs
       :pi--1.5em
       :xsm:pi--2.5em
       :pb--1.25em:1.75em
       :xsm:pb--2.25em:2.75em
       :min-width--200px
       :xsm:max-width--90vw
       :max-width--250px
       :min-height--120px)
   [:div
    (sx :.flex-col-fs
        :gap--1em
        :_.kushi-text-input-label:min-width--7em
        :_.kushi-input-inline:gtc--36%:64%
        :_.kushi-input-inline:d--grid)
    [:h2 (sx :fs--$size-medium
             :fw--$semi-bold
             :mbe--0.75em)
     "Example Popover Form"]
    [text-field
     {:placeholder      "100%"
      :-label           "Height"
      :-label-placement :inline}]
    [text-field
     {:placeholder      "335px"
      :-label           "Min Width"
      :-label-placement :inline}]
    [text-field
     {:placeholder      "75px"
      :-label           "Depth"
      :-label-placement :inline}]]
   [button
    {:class
     (css :.top-right-corner-inside
          :.neutral
          :.minimal
          :.pill
          :zi--1
          :fs--$size-small
          ["--icon-button-padding-inline"
           :0.4em]
          [:opacity                         
           :$popover-close-button-opacity]
          ["--button-padding-block"     
           :$icon-button-padding-inline]
          [:margin-inline                   
           :$popover-close-button-margin-inline||$icon-button-padding-inline]
          [:margin-block                    
           :$popover-close-button-margin-block||$icon-button-padding-inline])
     :on-click dismiss-popover!}
    [icon mui.svg/close]]])


(defn toast-content []
  [:div
   (sx ".my-toast-content"
       :.flex-row-fs
       :position--relative
       :fs--$size-medium
       :ai--c
       :gap--1.25em
       :xsm:gap--1.5em
       :pi--1.25em
       :xsm:pi--1.5em
       :pb--1em
       :xsm:pb--1.25em )
   [:div
    (sx ".my-toast-content-wrapper"
        :.flex-col-c
        :ai--fs
        :gap--0.5em
        :_.kushi-text-input-label:min-width--7em
        :_.kushi-input-inline:gtc--36%:64%)
    [:h3 (sx :fw--$bold :m--0) "Saved for later"]
    [:p (sx ".my-toast-text"
            :fs--$size-small
            :.neutral-secondary-foreground)
     (.format (new js/Intl.DateTimeFormat
                   "en-US"
                   #js{:dateStyle "full" :timeStyle "short"})
              (new js/Date))]]
   [button
    {:class (css ".toast-close-button"
                 :fw--$semi-bold
                 :.no-shrink
                 :br--$shape-rounded
                 :fs--$size-xxsmall
                 :letter-spacing--$loose
                 :zi--1
                 [:opacity  :$popover-close-button-opacity])
     :on-click dismiss-toast!}
    "Undo Save" ]])


(defn main-view []
  [:div (sx :.flex-col-c
            :p--5rem)

  ;;  (let [id "my-modal-basic"]
  ;;    [:div [button {:on-click (fn* [] (open-kushi-modal id))}
  ;;           "Click to open modal"]
  ;;     [modal {:class (css :min-width--300px
  ;;                         :_.kushi-modal-description:fs--$size-small)
  ;;             :id    id}
  ;;      [:div (sx :.xxxlarge :.flex-row-c) "💃🏽"]]])

  [button
   (toast-attrs {:-auto-dismiss? false
                 :-f             (fn [toast-el]
                                   (rdom/render toast-content toast-el))})
   "Save for later"]
   
  #_[button  
   (popover-attrs
    {:-placement     :r
     :-arrow?        false
     :-auto-dismiss? true
     :-f             (fn
                       [el]
                       (rdom/render
                        [:div
                         (sx :.flex-col-c :ai--c :min-height--100% :p--1rem)
                         [:p (sx :.small)
                          "I will close automatically,"
                          [:br]
                          "after 5000ms"]]
                        el))})
   "Open"]

  #_[button  
   (popover-attrs
    {:-placement :r
     :-arrow?    false
     :-f         (fn [popover-el]
                   (rdom/render (fn []
                                  [:div
                                   (sx :.flex-col-c
                                       :ai--c
                                       :min-height--100%
                                       :p--1rem)
                                   [button {:class "small"
                                            :on-click dismiss-popover!}
                                    "Close"]])
                                popover-el))})
   "Open"]

  #_[button  
   (popover-attrs
    {:-placement :r
     :-arrow?    :false
     :-f         (fn [popover-el]
                   (rdom/render (fn []
                                  [:div (sx :.xxxlarge 
                                            :.flex-row-c
                                            :padding--0.25em)
                                   "💃🏽"])
                                popover-el))})
   "Open"]

#_(into
   [:div
    {:style (let [gta (grid-template-areas
                       "brc br b  bl blc"
                       "rt  .  .  .  lt"
                       "r   .  .  .  l"
                       "rb  .  .  .  lb"
                       "trc tr t  tl tlc")
                  tooltip-delay-duration 0]
              (css-vars-map gta tooltip-delay-duration))
     :class (css
             :d--grid
             :gtc--1fr:1fr:1fr:1fr:1fr
             :gtr--auto
             :gap--1rem
             :w--400px
             :h--400px
             [:gta :$gta])}]

   (for [x     ["brc" "br" "b"  "bl" "blc"
                "rt"  nil  nil  nil  "lt"
                "r"   nil  nil  nil  "l"
                "rb"  nil  nil  nil  "lb"
                "trc" "tr" "t"  "tl" "tlc"]
         :when (not (nil? x))]

     [:button (merge-attrs
               {:style
                (css-vars-map x)
                :class     
                (css 'kushi-playground-tooltip-demo-button
                     :.flex-row-c
                     :.pointer
                     :.relative
                     :b--1px:solid:$neutral-600
                     :dark:b--1px:solid:$neutral-400
                     :hover:b--1px:solid:black
                     :dark:hover:b--1px:solid:white
                     :>span.placement-label:ff--$code-font-stack
                     :fs--0.9em
                     :c--$neutral-600
                     :dark:c--$neutral-400
                     :hover:c--black
                     :dark:hover:c--white
                     :_.kushi-pseudo-tooltip-revealed:bc--$accent-color
                     :dark:&.kushi-pseudo-tooltip-revealed:bc--$accent-color-inverse
                     :_.kushi-pseudo-tooltip-revealed:c--$accent-color
                     :dark:&.kushi-pseudo-tooltip-revealed:c--$accent-color-inverse
                     :_.kushi-pseudo-tooltip-revealed:bgc--$accent-background-color
                     :dark:_.kushi-pseudo-tooltip-revealed:bgc--$accent-background-color-inverse
                     [:grid-area :$x])
                :tab-index 0}
               (tooltip-attrs {:-text      [(str "`:" x "`")]#_["Tooltip Line 1" "Tooltip Line 2" ]
                               ;; :-reveal-on-click?         true
                               :-placement (keyword x)}))
      [:span.placement-label (str ":" x)]]))

   #_[button (tooltip-attrs {:-text "This is a tooltip"})
    "Hover me"]


   #_(let [id "Rounded, with white backdrop"]
     [:div
      [button
       {:on-click (fn* [] (open-kushi-modal id))}
       "Click to open modal"]
      [modal
       (merge-attrs (sx
                     :min-width--300px
                     :border-radius--24px
                     :b--2px:solid:$gray-900
                     [:--modal-backdrop-color :$white-transparent-70]
                     :_.kushi-modal-description:fs--$size-small)
                    {:-modal-title "Example modal"
                     :-description "Example modal description goes here."
                     :id           id})
       [:div
        (sx :.flex-col-fs :gap--1em)
        [text-field {:placeholder "Puffy"
                     :-label      "Screen name"}]
        [text-field {:placeholder "Executive"
                     :-label      "Occupation"}]]
       [:div
        (sx :.flex-row-fe :gap--1em)
        [button
         (merge-attrs (sx :.minimal :.pill)
                      {:on-click close-kushi-modal})
         "Cancel"]
        [button
         (merge-attrs (sx :.filled :.pill)
                      {:on-click close-kushi-modal}) 
         "Submit"]]]])


   #_(let [id "my-modal-basic"]
       [:div [button {:on-click (fn* [] (open-kushi-modal id))}
              "Click to open modal"]
        [modal {:class (css :min-width--300px
                            :_.kushi-modal-description:fs--$size-small)
                :id    id}
         [:div (sx :.xxxlarge :.flex-row-c) "💃🏽"]]])


   #_[:div 
      [:div (sx :.wireframe :p--1rem)
       [collapse
        {:-label "Collapsable section label"}
        [:p "Child 1"]
        [:p "Child 2"]]]

      [:div (sx :.wireframe :p--1rem)
       [collapse
        {:-label          "Click to expand"
         :-label-expanded "Click to collapse"}
        [:p "Child 1"]
        [:p "Child 2"]]]

      [:div (sx :.wireframe :p--1rem)
       [collapse
        {:-label         "Collapsable section label "
         :-icon-position :end}
        [:p "Child 1"]
        [:p "Child 2"]]]

      [:div (sx :.wireframe :p--1rem)
       [collapse
        {:class         (css :bbe--1px:solid:$neutral-800
                             :dark:bbe--1px:solid:$neutral-400)
         :-label        "Collapsable section label "
         :-header-attrs (sx :bbs--1px:solid:$neutral-800
                            :dark:bbs--1px:solid:$neutral-400)}
        [:p "Child 1"]
        [:p "Child 2"]]]

      [:div (sx :.wireframe :p--1rem)
       [collapse
        {:-label        "Collapsable section label "
         :-header-attrs (sx :.bold)}
        [:p "Child 1"]
        [:p "Child 2"]]]

      [:div (sx :.wireframe :p--1rem)
       [collapse
        {:-label      "Collapsable section label "
         :-body-attrs (sx :bgc--$purple-100 :dark:bgc--$purple-900 :pis--1rem)
         :-speed      1000}
        [:section (sx :pb--0.5rem)
         [:p "Child 1"]
         [:p "Child 2"]
         [:p "Child 3"]
         [:p "Child 4"] 
         [:p "Child 5"]]]]

      [:div (sx :.wireframe :p--1rem)
       [collapse
        {:-label    "Collapsable section label"
         :-on-click (fn* [] (js/alert "clicked"))}
        [:p "Child 1"]
        [:p "Child 2"]]]]
   


   #_[:div (sx :w--605px)
      [callout
       {:class        (css :.small :.warning :.filled)
        :-header-text [:span "Please check out the " 
                       [link {:class (sx :ws--n )
                              :href  "#"} 
                        "new features"]]
        :-icon        [icon :info]}]]

   #_[:div (sx :w--605px)
      [grid
       {:class             (css :>div:bgc--$neutral-150
                                :dark:>div:bgc--$neutral-800
                                )
        :-column-min-width :80px
        :-gap              :15px
        :-aspect-ratio     :2:3}
       (for [x (range 18)]
         [:div [:div (sx :.absolute-fill :.flex-col-c :ai--c) (inc x)]])]]

   #_[:div (sx :.flex-row-fs :gap--3rem)
      [donut (sx :.small)]
      [donut (sx :.medium)]
      [donut (sx :.large)]
      [donut (sx :.xlarge)]
      [donut (sx :.xxlarge :c--$green-700)]
      [donut (sx :.xxxlarge )]
      ]
   #_[:div (sx 
            {:border-radius      "var(--rounded)"
             :width              :fit-content
             :background-color   :transparent
             :padding            :1em
             :padding-inline-end :1.5em
             :border             "1px solid var(--neutral-150)"})
      [text-field
       {:-label-attrs         (sx :bgc--yellow)
        :placeholder          "Your text here"
        :disabled             false
        :-semantic            :accent
        :-end-enhancer        "🦄"
        :-helper              "Your helper text here"
        :-start-enhancer      "$"
        :-wrapper-attrs       (sx :box-shadow--4px:4px:7px:#f2baf9ab
                                  :dark:box-shadow--4px:4px:7px:#b000c66e
                                  )
        :-outer-wrapper-attrs (sx :b--1px:solid:yellow :dark:b--1px:solid:#c419b5
                                  :box-shadow--8px:8px:17px:#b000c66e
                                  :dark:box-shadow--8px:8px:17px:#b000c66e
                                  :p--1em)
        :required             false
        :-label               "Input label"}]]

   #_[:section
      (sx :d--grid
        ;; :xsm:gtc--1fr:1fr
          :row-gap--1em
          :column-gap--2em
          [:_.emoji
           {:fs                  :28px
            :mi                  :0.33em
            :filter              "grayscale(1)"
            :transition-property :transform
            :transition-duration :500ms}]
          [:_.kushi-radio-input:checked+.kushi-label>.emoji
           {:filter    :none
            :transform "scale(1.5)"
            :animation :jiggle2:0.5s}])

      [radio {:-input-attrs {:name           :demo-custom
                             :defaultChecked true}} 
       [label [:span.emoji "🦑"] "Squid"]]

      [radio {:-input-attrs {:name :demo-custom}}
       [label [:span.emoji "🐋"] "Whale"]]

      [radio {:-input-attrs {:name :demo-custom}}
       [label [:span.emoji "🦈 "] "Shark"]]

      [radio {:-input-attrs {:name :demo-custom}}
       [label [:span.emoji "🐊"] "Croc"]]]

   #_[:section
      (sx :.flex-row-fs :c--$purple-600 :dark:c--$purple-300)
      [radio {:-input-attrs {:name :demo}} "Yes"]
      [radio {:-input-attrs {:name :demo}} "No"]
      [radio {:-input-attrs {:name :demo}} "Maybe"]]

   #_[:div (sx :.flex-row-fs)
      [:div
       [radio {:class        (css :.large) 
               :-input-attrs {:name           :large-sample
                              :defaultChecked true}}]
       [radio {:class        (css :.large) 
               :-input-attrs {:name :large-sample}}]]
      [:div
       [radio {:class        (css :.xxxlarge) 
               :-input-attrs {:name           :xxxlarge-sample
                              :defaultChecked true}}]
       [radio {:class        (css :.xxxlarge) 
               :-input-attrs {:name :xxxlarge-sample}}]]
      ]
   

   #_[:div (sx :.flex-col-fs :gap--1.0rem)
      [:div (sx 
             {:border-radius      "var(--rounded)"
              :width              :fit-content
              :background-color   :transparent
              :padding            :1em
              :padding-inline-end :1.5em
              :border             "1px solid var(--neutral-150)"})
       [checkbox
        {:-label-attrs (sx :.large :.thin)}
        [label "Make it shiny" [icon :auto-awesome]]]]
      [:div (sx 
             {:border-radius      "var(--rounded)"
              :width              :fit-content
              :background-color   :transparent
              :padding            :1em
              :padding-inline-end :1.5em
              :border             "1px solid var(--neutral-150)"})
       [checkbox
        {:-label-attrs (sx :.large :.light)}
        [label "Make it shiny" [icon :auto-awesome]]]]  

      [:div (sx 
             {:border-radius      "var(--rounded)"
              :width              :fit-content
              :background-color   :transparent
              :padding            :1em
              :padding-inline-end :1.5em
              :border             "1px solid var(--neutral-150)"})
       [checkbox
        {:-label-attrs (sx :.large :.wee-bold)}
        [label "Make it shiny" [icon :auto-awesome]]]]

      [:div (sx 
             {:border-radius      "var(--rounded)"
              :width              :fit-content
              :background-color   :transparent
              :padding            :1em
              :padding-inline-end :1.5em
              :border             "1px solid var(--neutral-150)"})
       [checkbox
        {:-label-attrs (sx :.large :.heavy)}
        [label "Make it shiny" [icon :auto-awesome]]]]]

   #_[:div (sx :.flex-row-fs :gap--2rem)
      [:div (sx :.flex-col-fs :gap--1.0rem)
       [checkbox
        {:class          (css :fs--$size-small)
         :name           :xxxlarge-sample
         :defaultChecked true}]
       [checkbox
        {:class          (css :fs--$size-small)
         :name           :xxxlarge-sample
         :defaultChecked false}]]
      [:div (sx :.flex-col-fs :gap--1.0rem)
       [checkbox
        {:class          (css :fs--$size-xxxlarge)
         :name           :xxxlarge-sample
         :defaultChecked true}]
       [checkbox
        {:class          (css :fs--$size-xxxlarge)
         :name           :xxxlarge-sample
         :defaultChecked false}]]
      
      ]
   #_[:div (sx :.flex-col-fs :gap--3.0rem)
    ;; [:div (sx :w--500px)
    ;;  [slider
    ;;   {:min 0
    ;;    :max 7}]]
    ;; [:div (sx :w--500px)
    ;;  [slider
    ;;   {:min          0
    ;;    :max          7
    ;;    :-step-marker :label}]]
    ;; [:div (sx :w--500px)
    ;;  [slider
    ;;   {:min          0
    ;;    :max          7
    ;;    :-step-marker :bar}]]
    ;; [:div (sx :w--500px)
    ;;  [slider
    ;;   {:min          0
    ;;    :max          7
    ;;    :-step-marker :dot}]]
    ;; [:div (sx :w--500px)
    ;;  [slider
    ;;   {:min  0
    ;;    :max  1
    ;;    :step 0.01}]]
    ;; [:div (sx :w--500px)
    ;;  [slider
    ;;   {:-steps            ["xsmall" "medium" "large" "xlarge"]
    ;;    :-step-marker      :label
    ;;    :-label-size-class :medium}]]
      
    ;; [:div (sx :w--500px)
    ;;  [slider
    ;;   {:-steps            ["First label is long" "Second label" "Third label" "Last label is long"]
    ;;    :-step-marker      :dot
    ;;    :-label-size-class :small
    ;;    :-labels-attrs     (sx [:_.kushi-slider-step-label:first-child>span:translate :-25%:-50%]
    ;;                           [:_.kushi-slider-step-label:last-child>span:translate :-75%:-50%])}]]
      

      
      ]

   #_[:div (sx :.flex-col-fs :gap--0.5rem)
      [:div (sx :.flex-row-fs :gap--0.5rem)
       [tag (sx :.neutral :.xxsmall) "tagger"]
       [tag (sx :.neutral :.xsmall) "tagger"]
       [tag (sx :.neutral :.small) "tagger"]
       [tag (sx :.neutral :.medium) "tagger"]
       [tag (sx :.neutral :.large) "tagger"]
       [tag (sx :.neutral :.xlarge) "tagger"]
       [tag (sx :.neutral :.xxlarge) "tagger"]]

      [:div (sx :.flex-row-fs :gap--0.5rem)
       [tag (sx :.neutral :.large) "tagger"]
       [tag (sx :.accent :.large) "tagger"]
       [tag (sx :.positive :.large) "tagger"]
       [tag (sx :.warning :.large) "tagger"]
       [tag (sx :.negative :.large) "tagger"]]

      [:div (sx :.filled :.flex-row-fs :gap--0.5rem)
       [tag (sx :.filled :.neutral :.large) "tagger"]
       [tag (sx :.filled :.accent :.large) "tagger"]
       [tag (sx :.filled :.positive :.large) "tagger"]
       [tag (sx :.filled :.warning :.large) "tagger"]
       [tag (sx :.filled :.negative :.large) "tagger"]]

      [:div (sx :.flex-row-fs :gap--0.5rem)
       [tag (sx :.bordered :.neutral :.large) "tagger"]
       [tag (sx :.bordered :.accent :.large) "tagger"]
       [tag (sx :.bordered :.positive :.large) "tagger"]
       [tag (sx :.bordered :.warning :.large) "tagger"]
       [tag (sx :.bordered :.negative :.large) "tagger"]]
      
      #_[card [:div (sx :.flex-row-fs
                        :.neutralize
                        :ai--stretch
                        :gap--0.8em)
               [:div (sx :.rounded
                         :.transition
                         :position--relative
                         :overflow--hidden
                         :bgc--$neutral-200
                         :dark:bgc--$neutral-800
                         :w--3.5em
                         :h--3.5em)
                [:span (sx :.absolute-centered
                           [:transform "translate 0 0.045em"]
                           :display--block
                           :scale--2.55)
                 "🐻‍❄"]]
               [:section (sx :.flex-col-sa) 
                [:p (sx :fs--1.25em :.wee-bold) "Polar Bear"] 
                [:p (sx :c--$neutral-secondary-foreground
                        :dark:c--$neutral-secondary-foreground-inverse)
                 "polar.bear@example.com"]]]]

      #_[:div (sx :.flex-row-fs :gap--0.5rem)
         [switch (sx :.xlarge)]
         [switch (sx :.xlarge :.accent)]
         [switch (sx :.xlarge :.positive)]
         [switch (sx :.xlarge :.warning)]
         [switch (sx :.xlarge :.negative)]]

      #_[:div (sx :.flex-row-fs :gap--0.5rem)
         [switch {:class (css :.xlarge)
                  :-on?  true}]
         [switch {:class (css :.xlarge :.accent)
                  :-on?  true}]
         [switch {:class (css :.xlarge :.positive)
                  :-on?  true}]
         [switch {:class (css :.xlarge :.warning)
                  :-on?  true}]
         [switch {:class (css :.xlarge :.negative)
                  :-on?  true}]]
      
      #_[:div (sx :.flex-row-fs :gap--0.5rem)
         [switch {:class (css :.small)
                  :-on?  true}]
         [switch {:class (css :.medium)
                  :-on?  true}]
         [switch {:class (css :.large)
                  :-on?  true}]
         [switch {:class (css :.xlarge)
                  :-on?  true}]
         [switch {:class (css :.xxlarge)
                  :-on?  true}]
         [switch {:class (css :.xxxlarge)
                  :-on?  true}]
         ]
      #_[switch
         {:class              (css {:--switch-width-ratio 2.25} :.xxxlarge)
          :-track-content-on  "ON"
          :-track-content-off "OFF"
          }]

      [:div (sx :.flex-row-fs :gap--0.5rem)]]

   #_[:div (sx :.flex-row-c
               :gap--1rem
               :w--500px
               :p--10px)
      [:div (sx :.flex-col-fs :gap--0.5rem)
       [button (sx :.small :.accent)          "Pets"   [icon :pets]]
       [button (sx :.small :.filled :.accent) "Pets"   [icon :pets]]
       [button (sx :.small :.bordered :.accent) "Pets" [icon :pets]]
       [button (sx :.small :.minimal :.accent) "Pets"  [icon :pets]]]
      
      [:div (sx :.flex-col-fs :gap--0.5rem)
       [button (sx :.small :.positive) "Pets"            [icon :pets]]
       [button (sx :.small :.filled :.positive) "Pets"   [icon :pets]]
       [button (sx :.small :.bordered :.positive) "Pets" [icon :pets]]
       [button (sx :.small :.minimal :.positive) "Pets"  [icon :pets]]]

      [:div (sx :.flex-col-fs :gap--0.5rem)
       [button (sx :.small :.warning) "Pets"            [icon :pets]]
       [button (sx :.small :.filled :.warning) "Pets"   [icon :pets]]
       [button (sx :.small :.bordered :.warning) "Pets" [icon :pets]]
       [button (sx :.small :.minimal :.warning) "Pets"  [icon :pets]]]

      [:div (sx :.flex-col-fs :gap--0.5rem)
       [button (sx :.small :.negative)           "Pets"  [icon :pets]]
       [button (sx :.small :.filled :.negative) "Pets"   [icon :pets]]
       [button (sx :.small :.bordered :.negative) "Pets" [icon :pets]]
       [button (sx :.small :.minimal :.negative) "Pets"  [icon :pets]]]]


   #_[:div (sx :.flex-row-c
               :gap--1rem
               :w--500px
               :p--10px)
      [:div (sx :.flex-col-fs :gap--0.5rem)
       [button (sx :.small :.rounded) "Pets"            [icon :pets]]
       [button (sx :.small :.rounded :.filled ) "Pets"  [icon :pets]]
       [button (sx :.small :.rounded :.bordered) "Pets" [icon :pets]]
       [button (sx :.small :.rounded :.minimal) "Pets"  [icon :pets]]]

      [:div (sx :.flex-col-fs :gap--0.5rem)
       [button (sx :.small :.pill) "Pets"             [icon :pets]]
       [button (sx :.small :.pill :.filled ) "Pets"   [icon :pets]]
       [button (sx :.small :.pill :.bordered) "Pets"  [icon :pets]]
       [button (sx :.small :.pill :.minimal) "Pets"   [icon :pets]]]
      
      [:div (sx :.flex-col-fs :gap--0.5rem)
       [button (sx :.small :.sharp) "Pets"            [icon :pets]]
       [button (sx :.small :.sharp :.filled ) "Pets"  [icon :pets]]
       [button (sx :.small :.sharp :.bordered) "Pets" [icon :pets]]
       [button (sx :.small :.sharp :.minimal) "Pets"  [icon :pets]]]
      
      [:div (sx :.flex-col-fs :gap--0.5rem)
       [button (sx :.small)           [icon :pets]]
       [button (sx :.small :.filled)  [icon :pets]]
       [button (sx :.small :.bordered) [icon :pets]]
       [button (sx :.small :.minimal) [icon :pets]]]]
   
   #_[:div (sx :.flex-row-c :gap--1rem :w--500px :p--10px)
      [:div (sx :.flex-col-fs :ai--c :gap--0.5rem)
       [button
        {:-loading? true}
        [spinner [icon :play-arrow] [propeller]]
        "Play"]
    ;; [button (sx :.xsmall :.rounded  ) "Pets"  [icon :pets]]
    ;; [button (sx :.small :.rounded   ) "Pets" [icon :pets]]
    ;; [button (sx :.medium :.rounded   ) "Pets" [icon :pets]]
    ;; [button (sx :.large :.rounded   ) "Pets"  [icon :pets]]
    ;; [button (sx :.xlarge :.rounded  ) "Pets"  [icon :pets]]
    ;; [button (sx :.xxlarge :.rounded ) "Pets"  [icon :pets]]
    ;; [button (sx :.xxxlarge :.rounded) "Pets"  [icon :pets]]
       ]]
   #_[:div (sx :.flex-row-c :gap--1rem :w--500px :p--10px)
      [:div (sx :.flex-col-fs :ai--c :gap--0.5rem)
       
       [button (sx :.xxsmall :.rounded) "Pets"            [icon :pets]]
       [button (sx :.xsmall :.rounded  ) "Pets"  [icon :pets]]
       [button (sx :.small :.rounded   ) "Pets" [icon :pets]]
       [button (sx :.medium :.rounded   ) "Pets" [icon :pets]]
       [button (sx :.large :.rounded   ) "Pets"  [icon :pets]]
       [button (sx :.xlarge :.rounded  ) "Pets"  [icon :pets]]
       [button (sx :.xxlarge :.rounded ) "Pets"  [icon :pets]]
       [button (sx :.xxxlarge :.rounded) "Pets"  [icon :pets]]
       ]]
   
   ])
        

(defn ^:dev/after-load mount-root []
  (let [root-el (.getElementById js/document "app")]
  ;;  (rdom/render [views/main-view] root-el)
    (rdom/render [main-view] root-el)
    ))

(defn init []
  (mount-root))

;; (inject!)
