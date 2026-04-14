(ns site.views2
  (:require
   [fireworks.core :refer [? !? ?> !?> pprint]]
   [bling.core]
   [domo.core :as domo]
  ;;  [bling.core :as bling :refer [bling print-bling callout point-of-interest]]
  ;;  [bling.hifi :refer [print-hifi hifi]]
  ;;  [bling.explain :refer [explain-malli]]
   [kushi.core :refer [?sx sx sx2 css merge-attrs at defcss]]
   [kushi.playground.shared-styles]
  ;;  [kushi.ui.variants]
   [kushi.ui.core :refer [defui data-ks-attrs #_pc ui]]

   [kushi.showcase.core :as showcase :refer [showcase]]


   [kushi.ui.flex :refer [flex-row flex-col]]

   [kushi.ui.text-field :refer [text-field]]
   [kushi.ui.text-field.demo]

   [kushi.ui.button :refer [button]]
   [kushi.ui.button.demo]

   [kushi.ui.collapse :refer [collapse]]
   [kushi.ui.collapse.demo]

   [kushi.ui.tooltip :refer [tooltip-attrs]]
   [kushi.ui.tooltip.demo]

   [kushi.ui.popover :refer [popover-attrs]]
   [kushi.ui.popover.demo]

   [kushi.ui.grid :refer [grid]]
   [kushi.ui.grid.demo]

   [kushi.ui.toast]
   [kushi.ui.toast.demo]

   [kushi.ui.icon-button :refer [icon-button]]
   [kushi.ui.icon-button.demo]

   [kushi.ui.box :refer [box]]
  ;;  [kushi.ui.layout :refer [layout]]

   [kushi.ui.icon :refer [icon]]
   [kushi.ui.icon.demo]

   [kushi.ui.spinner :refer [spinner]]
   [kushi.ui.spinner.demo]

   [kushi.ui.callout :refer [callout]]
   [kushi.ui.callout.demo]

   [kushi.ui.tag :refer [tag]]
   [kushi.ui.tag.demo]

   [kushi.ui.checkbox :refer [checkbox]]
   [kushi.ui.checkbox.demo]

   [kushi.ui.radio :refer [radio]]
   [kushi.ui.radio.demo]

   [kushi.ui.radio-group :refer [radio-group]]
  ;;  [kushi.ui.radio-group.demo]

   [kushi.ui.checkbox-group :refer [checkbox-group]]
  ;;  [kushi.ui.checkbox-group.demo]

   [kushi.playground.assets.graphics.avatars :refer [avatar-1]]
   [kushi.ui.avatar :refer [avatar]]
   [kushi.ui.avatar.demo]

   
   [kushi.ui.card :refer [card]]
   [kushi.ui.card.demo]

   [kushi.ui.switch :refer [switch]]
   [kushi.ui.switch.demo]

   [kushi.ui.thumb :refer [thumb]]

   [kushi.ui.label :refer [label]]
  ;;  [kushi.ui.radio :refer [radio]]
  ;;  [kushi.ui.spinner :refer [spinner]]
   [kushi.ui.util :as util]
   [kushi.util :refer [keyed]]
   [clojure.string :as string]
   ;; [malli.core :as m]
   [kushi.ui.link :refer [link]]

   [kushi.css.defs]

   [lasertag.core :as lasertag :refer [tag tag-map]]
   
   [kushi.ui.defs :as defs]))

   (js/console.clear)

(defn new-button-lineup []
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
  )   

(defn circle-edge-points
  "Calculate points around the edge of a circle inscribed in a square.
  
  The circle has a diameter equal to the square's width/height.
  Returns coordinates as percentages (0-100) relative to the square.
  
  Parameters:
    points - number of points to generate around the circle
    margin - percentage to increase the circle's radius (e.g., 10 = 10% larger)
    shift  - number of positions to shift points (e.g., 1 shifts by one point position)
  
  Returns:
    A vector of [x y angle] tuples where:
      - x, y are percentages (0-100)
      - angle is in degrees (0-359)
  
  Examples:
    (circle-edge-points 4 0 0)
    => [[50.0 0.0 0] [100.0 50.0 90] [50.0 100.0 180] [0.0 50.0 270]]
    
    (circle-edge-points 4 0 1)
    => [[100.0 50.0 90] [50.0 100.0 180] [0.0 50.0 270] [50.0 0.0 0]]
    
    (circle-edge-points 8 10 2)
    => points around a circle 10% larger, shifted by 2 positions"
  [points margin shift]
  (let [;; Center of square is at 50%, 50%
        center-x 50.0
        center-y 50.0
        ;; Base radius is 50% (half the square's width)
        base-radius 50.0
        ;; Apply margin: if margin is 10, radius becomes 50 * 1.10 = 55
        radius (* base-radius (+ 1.0 (/ margin 100.0)))
        ;; Angular step between points
        angle-step (/ (* 2 js/Math.PI) points)
        ;; Shift offset in radians
        shift-offset (* shift angle-step)
        ;; Rotate by -90 degrees to make 0° at the top instead of right
        top-offset (- (/ js/Math.PI 2))]
    (mapv (fn [i]
            (let [angle (+ (* i angle-step) shift-offset top-offset)
                  ;; Calculate position relative to center
                  x (+ center-x (* radius (.cos js/Math angle)))
                  y (+ center-y (* radius (.sin js/Math angle)))
                  ;; Convert radians to degrees and normalize to 0-359
                  angle-degrees (int (mod (.round js/Math (* angle (/ 180 js/Math.PI))) 360))]
              [x y angle-degrees]))
          (range points))))

;; exponential progression
(defn deformed-scale-2 [number-of-indices base-index shift]
  (let [shifted-index (+ base-index shift)
        ;; Invert the power so max effect is at shifted-index
        power-below (if (> shifted-index 0)
                      (/ 1.0 (+ 1.0 (/ (Math/abs (double shift)) 5.0)))
                      1.0)
        power-above (if (< shifted-index number-of-indices)
                      (/ 1.0 (+ 1.0 (/ (Math/abs (double shift)) 5.0)))
                      1.0)]
    (for [i (range number-of-indices)]
      (cond
        ;; Below or at the shifted index
        (<= i shifted-index)
        (let [t (if (> shifted-index 0)
                  (/ i (double shifted-index))
                  0.0)
              ;; Invert: 1 - curve gives max compression near shifted-index
              deformed-t (- 1.0 (Math/pow (- 1.0 t) power-below))]
          [i (* deformed-t base-index)])
        
        ;; Above the shifted index
        :else
        (let [distance (- i shifted-index)
              remaining (- (dec number-of-indices) shifted-index)
              t (/ distance (double remaining))
              ;; Start with max stretch, diminish toward edge
              deformed-t (- 1.0 (Math/pow (- 1.0 t) power-above))
              value-range (- (dec number-of-indices) base-index)]
          [i (+ base-index (* deformed-t value-range))])))))


;; linear progression
(defn deformed-scale-linear [number-of-indices base-index shift]
  (let [shifted-index (+ base-index shift)]
    (for [i (range number-of-indices)]
      (cond
        ;; Below or at the shifted index
        (<= i shifted-index)
        (let [t (if (> shifted-index 0)
                  (/ i (double shifted-index))
                  0.0)]
          [i (* t base-index)])
        
        ;; Above the shifted index
        :else
        (let [distance (- i shifted-index)
              remaining (- (dec number-of-indices) shifted-index)
              t (/ distance (double remaining))
              value-range (- (dec number-of-indices) base-index)]
          [i (+ base-index (* t value-range))])))))

(defn deformed-range [start end n]
  (let [range-size (- end start)
        num-points 16  ; or make this a parameter
        ;; Negative n = compress at start, positive n = compress at end
        power (+ 1.0 (/ (double n) 10.0))]
    (for [i (range num-points)]
      (let [t (/ i (double (dec num-points)))  ; normalize to [0, 1]
            ;; Apply exponential curve
            deformed-t (Math/pow t power)
            value (+ start (* deformed-t range-size))]
        [i value]))))

(defn deformed-range2 [start end n]
  (let [range-size (- end start)
        num-points (inc range-size)
        strength (/ (double n) 100.0)]
    (for [i (range num-points)]
      (let [index (+ start i)
            t (/ i (double (dec num-points)))
            ;; Corrected: use a formula that preserves endpoints
            ;; deformed-t must be 0 when t=0 and 1 when t=1
            deformed-t (+ t (* strength t (- 1 t)))
            value (+ start (* deformed-t range-size))]
        [index value]))))
   


(defn circular-rotate
  "Rotate a collection to start from the given index, wrapping around once."
  [coll start-idx]
  (let [v (vec coll)
        n (count v)]
    (when (pos? n)
      (mapv #(nth v (mod % n))
            (range start-idx (+ start-idx n))))))

(defn rotated-colors-from-0 [coll]
  (->> coll
       (keep-indexed (fn [i [_ v]] (when (= v 0) i)))
       first
       (circular-rotate defs/basic-colors*)))

(defn pie-slices-oklch [coll]
  (vec (reverse (assoc-in coll
                          [0 1]
                          (* 100 (/ 360 100))))))

(defn uniform-pie-slices-oklch [coll]
  ;; For uniform slices
  (map-indexed (fn [i [nm hue]] 
                 (let [fr     (->> coll count (/ 100))
                       start* (* i fr)
                       start  (if (zero? i) 0 start*)
                       end    (+ start fr)]
                   [(name nm) hue start end]))
               (pie-slices-oklch coll))

  #_(map-indexed (fn [i [nm hue]] 
                   (let [fr        (/ 100 360)
                         start     (* fr
                                      (if (zero? i)
                                        0
                                        (second (nth reversed (dec i)))))
                         hue-as-fr (* fr hue)]
                     [(name nm) hue start hue-as-fr]))
                 reversed)
  )

(defn vec-range-replace
  [v start-index coll]
  (let [end-index (+ start-index (count coll))]
    (vec (concat (subvec v 0 start-index)
                 coll
                 (subvec v end-index)))))

#_(defn replace-range-with-tweaked
  [coll]
  (vec-range-replace coll 0 (deformed-range2 0 4 -55)))

;; Tuning for lightness 0.43 / chroma 0.12


;; Tuning for lightness 0.23 / chroma 0.12
(def tuning-023-012
  [[0 4 -55]
   [6 12 45]
   [12 18 -53]
   [18 23 30]
   [23 31 20]])

(def tuning-043-012
  [[0 13 15]
   [13 23 43]
   [23 31 -10]])


(defn uniform-math-pie-slices-oklch
  "Calculate points around the edge of a circle inscribed in a square.
  
  The circle has a diameter equal to the square's width/height.
  Returns coordinates as percentages (0-100) relative to the square.
  
  Options:
    coll - vector of color names. First color is associated with `0` hue value in oklch represenation
    tuning-coll - vector of triples [tweak-idx-start tweak-idx-end strength] 
  
  Returns:
    A vector of  maps where"
  [{:keys [coll tuning-coll]}]
  (let [fr                      (->> coll count (/ 100))
        hue-fr                  (->> coll count (/ 360))
        indexes-and-shifts-base (vec (map-indexed (fn [i _] [i i]) coll))]
    (map (fn [[i i-shifted] color-name]
           (let [start*      (* i fr)
                 slice-start (if (zero? i) 0 start*)
                 slice-end   (+ slice-start fr)
                 shifted-hue (* i-shifted hue-fr)
                 color-name  (name color-name)]
             {:color-name  color-name
              :shifted-hue shifted-hue
              :slice-start slice-start
              :slice-end   slice-end}))

         (if tuning-coll
           (reduce (fn [coll [start-tweak-idx end-tweak-idx strength]]
                     (vec-range-replace coll
                                        start-tweak-idx
                                        (deformed-range2 start-tweak-idx
                                          end-tweak-idx strength)))
                   indexes-and-shifts-base
                   tuning-coll)
           indexes-and-shifts-base)
         coll)))


#_(def oklch-l 0.43)
(def oklch-l 0.77)
(def oklch-c 0.1252)
;; (def oklch-c 0.0727)
;; (def oklch-c 0.12)
;; (def oklch-c 0.048)

(defn- oklch-color-css [l c h]
  (str "oklch(" l " " c " " h ")"))

(defn- conic-gradient-step [l c h slice-start slice-end]
  (str (oklch-color-css l c h) " " slice-start "% " slice-end "% "))

;; TODO 
;; For each lightness level, go thru each hue point and find the limits for srgb, p3, and rec2020
;; 



(defn main-view []
  (.setAttribute (domo/el-by-id "app")
                 "data-ks-playground-active-path"
                 "components")

  
  #_(into [:div (sx {:w :100% :m :100px})]
        (for [[i v] (? (deformed-range2 0 16 30))]
          [:div (sx2 {:position :absolute
                      :width    :1px
                      :bgc      :white
                      :height   :10px
                      :style    {:left (str (* 50 v) "px")}})]))

  ;; BUTTON
  (let [pie-slices (uniform-math-pie-slices-oklch {:coll        defs/colors-from-0
                                                  ;;  :tuning-coll tuning-023-012
                                                  ;;  :tuning-coll tuning-043-012
                                                   })]
      [:div (sx2 {:position :fixed-fill}) 
       [:div (sx2 {:style         {:--bgi (let [stops (string/join 
                                                       ", "
                                                       (mapv (fn [{:keys [shifted-hue slice-start slice-end]}]
                                                               (conic-gradient-step oklch-l oklch-c shifted-hue slice-start slice-end))
                                                             pie-slices))]
                                            (str "conic-gradient(" stops ")"))}
                   :bgi           :$bgi
                   :width         :700px 
                   :position      :absolute-center
                   :height        :700px
                   :border-radius :100%
                   :>.axis-label  {:opacity     0.5
                                   :text-weight :light
                                   :size        :xsmall}})
        
        (into [:div (sx2 {:position :absolute
                          :width    :100%
                          :height   :100%})]
              (map-indexed 
               (fn [i [x y deg]]
                 (let [{:keys [shifted-hue color-name]} (nth pie-slices i)
                       start-idx?                       (or (= i 12) (= i 6))
                       end-idx?                         (= i 12)
                       deformation                      (if (= i 6)
                                                          #{:stretch-from-start}
                                                          #{:compress-towards-end
                                                            :stretch-from-start})]
                   [:span.wireframe
                    (sx2 {:position  :absolute
                          :text-size :xxsmall
                          :width     :0px
                          :height    :0px
                          :style     {:left (str x "%")
                                      :top  (str y "%")}})
                    [:span (sx2 {:position    :absolute
                                 :display     :flex
                                 :jc          :sb
                                 :ai          :center
                                 :_span:d     :inline-block
                                 :gap         :1em
                                 :line-height 0
                                 :ws          :n
                                 :style       {:transform-origin "center left"
                                               :flex-direction   (when (< i 16) "row-reverse")
                                               :transform        (cond 
                                                                   (< 90 deg 270)
                                                                   (str "rotate(" (+ deg 180) "deg) " "translate(-100%, -50%)")
                                                                   :else
                                                                   (str "rotate(" deg "deg) " "translate(0%, -50%)"))}})
                     (when (or start-idx? end-idx?)
                       [:div (sx2 {:position       :absolute-inline-end-outside
                                   :padding-left   :10px
                                   :gap            :30px
                                   :>div:w         :20px
                                   :>div:h         :20px
                                   :display        :flex
                                   :flex-direction :column
                                   :jc             :c
                                   :ai             :c
                                   :>.arrow        {:o     :0.5
                                                    :scale 1.5}})
                        [:div.arrow
                         (sx2 {:ta     :c
                               :rotate :180deg
                               :style  {:visibility (when-not end-idx? :hidden)}})
                         (if (contains? deformation :compress-towards-end?) 
                           "⭣"
                           "⭡")]
                        [:div (sx2 {:shape :pill
                                    :bgi   "radial-gradient(rgb(255 255 255 / 30%) 3px, transparent 3px, transparent)"
                                    :style {:background-color (oklch-color-css oklch-l oklch-c shifted-hue)}})]
                        [:div.arrow 
                         (sx2 {:ta    :c
                               :style {:visibility (when-not start-idx? :hidden)}})
                         (contains? deformation :stretch-from-start) "⭣"]])

                     [:span (sx2 {:style {:flex-direction (when (>= i 16) "row-reverse")
                                          :text-align     (when (>= i 16) "end")
                                          :opacity        :0.5
                                          :display        :flex
                                          :gap            :1em
                                          :wtf            []}})
                      [:span i]
                      [:span (sx2 {:min-width :44px
                                   :shrink    0
                                   :grow      1}) 
                       (-> color-name string/capitalize)]]]]))
               (circle-edge-points (count defs/basic-colors*)
                                   7
                                   0.5)))

        #_#_#_#_
        [:div.axis-label
         (sx2 {:position       :top-outside
               :padding-bottom :20px}) 
         0]

        [:div.axis-label
         (sx2 {:position     :right-outside
               :padding-left :20px}) 
         90]

        [:div.axis-label
         (sx2 {:position    :bottom-outside
               :padding-top :20px}) 
         180]

        [:div.axis-label
         (sx2 {:position      :left-outside
               :padding-right :20px}) 
         270]]
       


       #_[:div {:style {:width            :900px 
                        :height           :900px
                        :border-radius    :100%
                        :margin           :80px
                        :rotate           :-90deg
                        :background-image (let [stops (string/join ", "
                                                                   (mapv (fn [[hue-name hue start end]]
                                                                           (str "oklch(" oklch-l " " oklch-c " " hue ") " start "% " end "% "))
                                                                         (uniform-pie-slices-oklch defs/basic-colors*)))]
                                            (str "conic-gradient(" stops ")"))}}]


       #_[showcase (showcase/opts kushi.ui.button/button
                                  kushi.ui.button.demo/demos)]
       #_[new-button-lineup]])
  
  ;; ICON
  #_[showcase (!? (showcase/opts kushi.ui.icon/icon
                                 kushi.ui.icon.demo/demos))]

  ;; ICON-BUTTON
  #_[showcase (!? (showcase/opts kushi.ui.icon-button/icon-button
                                 kushi.ui.icon-button.demo/demos))]

  ;; SPINNER
  #_[showcase (!? (showcase/opts kushi.ui.spinner/spinner
                                 kushi.ui.spinner.demo/demos))]

  
  ;; RADIO
  #_[showcase (showcase/opts kushi.ui.radio/radio
                             kushi.ui.radio.demo/demos)]

  ;; AVATAR
  #_[showcase (showcase/opts kushi.ui.avatar/avatar
                             kushi.ui.avatar.demo/demos)]

  ;; SWITCH
  #_[showcase (!? (showcase/opts kushi.ui.switch/switch
                                 kushi.ui.switch.demo/demos))]

  ;; CHECKBOX
  #_[showcase (showcase/opts kushi.ui.checkbox/checkbox
                             kushi.ui.checkbox.demo/demos)]
  

  ;; SLIDER
  #_[showcase (showcase/opts kushi.ui.checkbox/slider
                             kushi.ui.slider.demo/demos)]

  ;; TEXT FIELD
  #_[showcase (!? (showcase/opts kushi.ui.text-field/text-field
                                 kushi.ui.text-field.demo/demos))]



  ;; [showcase (!? (showcase/opts kushi.ui.collapse/collapse
  ;;                              kushi.ui.collapse.demo/collapse))]
  ;; tooltip
  ;; popover
  ;; modal
  ;; toast
  
  ;; CARD
  #_[showcase (showcase/opts kushi.ui.card/card
                             kushi.ui.card.demo/demos)]

  ;; TAG
  #_[showcase (!? (showcase/opts kushi.ui.tag/tag
                                 kushi.ui.tag.demo/demos))]

  ;; CALLOUT
  #_[showcase (!? (showcase/opts kushi.ui.callout/callout
                                 kushi.ui.callout.demo/demos))]

  ;; COLLAPSE
  #_[showcase (showcase/opts kushi.ui.collapse/collapse
                             kushi.ui.collapse.demo/demos)]

  ;; TOOLTIP ATTRS
  #_[showcase (showcase/opts kushi.ui.tooltip/tooltip-attrs
                             kushi.ui.tooltip.demo/demos)]

  ;; POPOVER Attrs
  #_[showcase (showcase/opts kushi.ui.popover/popover-attrs
                             kushi.ui.popover.demo/demos)]

  ;; GRID
  #_[showcase (showcase/opts kushi.ui.grid/grid
                             kushi.ui.grid.demo/demos)]


  ;; TOAST
  #_[showcase (showcase/opts kushi.ui.toast/toast-attrs
                             kushi.ui.toast.demo/demos)]
  
  )


;; '(ui 
;;   [tag
;;    {:surface     wtf
;;     :display     :flex
;;     :size        :xlarge
;;     ;; :shape       {:=           :rounded-xxlarge-absolute
;;     ;;               :media/large :rounded-xxxlarge-absolute}
;;     :at-media/lg {:size  :small
;;                   :shape :rounded-xxxlarge-absolute}
;;     :style       {:color                          :red
;;                   :at-supports/color-mix-in-oklch {:color :blue}}
;;     }])

;; '=>

;; ;; feeds to css-rule during analyzation
;; '(merge-attrs 
;;   {:class [(resolve-kushi-prop :surface wtf)
;;            ]}
;;   (sx {:font-size    :$text-size-small
;;        :sm:font-size :$text-size-large
;;        :br           :$shape-rounded-absolute
;;        :large:br     :$shape-rounded-absolute}))

;; '[tag {:class ["foo.wtf__L20_C30"
;;                "surface-solid" 
;;                "colorway-cyan"]
;;        :style ""}]

#_(? (clj->js (map #(-> % second :surface)
                 '[[div {:surface "ghost" 
                         :stroke  :hard
                         :shape   :rounded-xlarge}]
                   [div {:surface "transparent"
                         :stroke  :hard}]
                   [div {:surface "minimal"
                         :stroke  :hard}]
                   [div {:surface "faint"}]
                   [div {:surface "soft"}]
                   [div {:surface "convex"}]
                   [div {:surface "soft-classic"
                         :shape   :pill }]
                   [div {:surface "solid-classic"
                         :shape   :pill }]
                   [div {:surface "solid"
                         :shape   :pill}]])))
