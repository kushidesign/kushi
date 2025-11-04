(ns site.views2
  (:require

   [fireworks.core :refer [? !? ?> !?> pprint]]
   [bling.core]
   [domo.core :as domo]
  ;;  [bling.core :as bling :refer [bling print-bling callout point-of-interest]]
  ;;  [bling.hifi :refer [print-hifi hifi]]
  ;;  [bling.explain :refer [explain-malli]]
   [kushi.core :refer [?sx sx css merge-attrs mrj at defcss]]
   [kushi.playground.shared-styles]
  ;;  [kushi.ui.variants]
   [kushi.ui.core :refer [defui data-ks-attrs #_pc ui]]

   [kushi.showcase.core :as showcase :refer [showcase]]


   [kushi.ui.flex :refer [flex-row flex-col flex-row-space-between]]

   [kushi.ui.text-field :refer [text-field]]
   [kushi.ui.text-field.demo]

   [kushi.ui.button :refer [button]]
   [kushi.ui.button.demo]

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

   [kushi.ui.switch :refer [switch]]
   [kushi.ui.switch.demo]

   [kushi.ui.thumb :refer [thumb]]

   [kushi.ui.label :refer [label]]
  ;;  [kushi.ui.radio :refer [radio]]
  ;;  [kushi.ui.spinner :refer [spinner]]
   [kushi.ui.util :as util]
   [clojure.string :as string]
   ;; [malli.core :as m]
   [kushi.ui.link :refer [link]]))


(js/console.clear)

;; Experimental macro calls

;; #_(defui bang 
;;   {:doc  "This is bang docstring"
;;    :opts {:foo {:schema number?} 
;;           :bar {:schema string?}}}
;;   (let [{:keys [foo bar]} &opts]
;;     (js/console.log &data-ks-attrs &opts foo bar)))

;; (bang {:foo 3 :bar "buzz"})

#_(pprint (? :data {1 2 3 :x}))


;; (? (:colorway kushi.ui.variants/variants-by-custom-opt-key))
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
      :weight   :extra-light
      :surface  :minimal}
     [radio (assoc m :id id)]

     ;; make this label component
     [label (merge-attrs
             (sx :pis--0.5em)
             {:weight         :bold
              :start-enhancer 8 #_[icon {:weight :light} (:icon m)]
              :for             id})
      (string/capitalize (:value m))]]))

(defn main-view []
  (.setAttribute (domo/el-by-id "app")
                 "data-ks-playground-active-path"
                 "components")

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
            :weight       :bolds
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
     {:loading     true
      :colorway     :accent
      :surface      :solid
      :size       :small
      :end-enhancer [spinner {:spinner-type :donut}]}
     "Play"]

  ;; This should be box or flex with flex and default inert
  ;; [box {:--border-color :gold
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
           :weight      :bold
           :icon-style  :sharp
           :icon-filled true
           :inert       true
           :id          :foo}
     :star]

  #_[button
     {:at           (at)
      :size       :xxlarge
      :position     :absolute-centered
      :loading      true,
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
      :loading  true
      :ns       (at)}
     "hi"]

  #_[icon-button
     {:surface  :solid
      :position :absolute-centered
      :colorway :blue
      :id       "foo"
      :loading  true
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

  


  [:div (sx :.flex-col-center :p--20px #_:.absolute-centered :gap--3rem) 


   #_[:span.flex-row-start
      [radio {:name   :g
              :id     :foo
              :weight :thin}]
      [label {:for :foo} "hi"]]


   #_[:div.flex-row-start.absolute-inline-start-inside
      (sx :gap--1rem
          :p--100px
          :fs--$size-xxxlarge)
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
           :stroke           :medium #_[[:2px :$brown-300] [:2px :$green-300]]
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
      ;;  :stroke [[:4px :$brown-300] [:2px :$green-300]]
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
        ;; :stroke [[:4px :$brown-300] [:2px :$green-300]]
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
          ;;               [:15px :-15px :20px :yellow]]
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
                   :weight :thin
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

   #_[checkbox {:weight :thin}
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
    [flex-row-space-between
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
       :weight   :bold
       :size     :medium}
      :close]]]]

  

  #_[switch
     (mrj
      (sx #_[:--switch-inset-gap :2px]
       #_[:--switch-thumb-scale-factor :1.25])
      {
       :on? true
       :size            :xxxlarge
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
                             [:--color :$gold-400]
                             [:--shadow-strength :40%]))
             k]))
  

  ;; DONE
  #_[showcase (!? (showcase/opts kushi.ui.switch/switch
                                 kushi.ui.switch.demo/demos))]

  ;; DONE
  #_[showcase (!? (showcase/opts kushi.ui.icon/icon
                               kushi.ui.icon.demo/demos))]

  ;; DONE
  #_[showcase (!? (showcase/opts kushi.ui.tag/tag
                               kushi.ui.tag.demo/demos))]

  ;; DONE
  #_[showcase (!? (showcase/opts kushi.ui.text-field/text-field
                               kushi.ui.text-field.demo/demos))]

  ;; DONE
  #_[showcase (!? (showcase/opts kushi.ui.switch/switch
                               kushi.ui.switch.demo/demos))]

  ;; DONE
  #_[showcase (!? (showcase/opts kushi.ui.icon-button/icon-button
                               kushi.ui.icon-button.demo/demos))]

  ;; DONE
  #_[showcase (!? (showcase/opts kushi.ui.button/button
                               kushi.ui.button.demo/demos))]
  ;; DONE
  [showcase (!? (showcase/opts kushi.ui.callout/callout
                               kushi.ui.callout.demo/demos))]

  ;; DONE
  #_[showcase (!? (showcase/opts kushi.ui.spinner/spinner
                               kushi.ui.spinner.demo/demos))]

  ;; DONE
  #_[showcase (!? {:display-metadata? false}
               (showcase/opts kushi.ui.checkbox/checkbox
                              kushi.ui.checkbox.demo/demos))]

  ;; DONE
  #_[showcase (showcase/opts kushi.ui.radio/radio
                           kushi.ui.radio.demo/demos)]

  ;; DONE
  #_[showcase (showcase/opts kushi.ui.avatar/avatar
                           kushi.ui.avatar.demo/demos)]

  #_[avatar {:surface :solid
             :size  :xxlarge
             :src     avatar-1}
     "JC"]



  ;; Sorting out 
   #_[flex-col
    (sx :max-width--1200px
       :p--24px
       :gap--48px
       :fs--$size-xxlarge
       :_p:fs--$size-large
       [:bgi "linear-gradient(to bottom right, silver, transparent)"])

    [callout
     {:surface :faint}
     [flex-row-space-between
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
    [flex-row-space-between
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
       :fs--$size-xxlarge
       :_p:fs--$size-large
       [:bgi "linear-gradient(to bottom right, silver, transparent)"])

   [:p "No colorway set"]
   [callout
    {:surface :faint}
    [flex-row-space-between
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
    [flex-row-space-between
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
    [flex-row-space-between
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
    [flex-row-space-between
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
    [flex-row-space-between
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
    [flex-row-space-between
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
               (for [colorway [:green :yellow :blue :red]]
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
     [:div
      (sx :.display-flex-row-start :ai--flex-start :gap--1rem)
      [:div (sx :.display-flex-col-start :p--20px :gap--4rem)
       [button {:size      :medium
                :stroke      :medium
                :shadow :medium
                :surface     :minimal
                :colorway    :green} "Next"]
       [button {:size      :medium
                :stroke      :medium
                :shadow :medium
                :surface     :minimal
                :colorway    :yellow} "Next"]
       [button {:size      :medium
                :stroke      :medium
                :shadow :medium
                :surface     :minimal
                :colorway    :blue} "Next"]
       [button {:size      :medium
                :stroke      :medium
                :shadow :medium
                :surface     :minimal
                :colorway    :red} "Next"]]
      [:div (sx :.display-flex-col-start :p--20px :gap--4rem)
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
     [:div
      (sx :.flex-row-start :ai--flex-start :gap--1rem)
      [:div (sx :.flex-col-start :p--20px :gap--1rem)
       [button {:size   :medium
                :surface  :faint
                :colorway :green} "Next"]
       [button {:size   :medium
                :surface  :faint
                :colorway :yellow} "Next"]
       [button {:size   :medium
                :surface  :faint
                :colorway :blue} "Next"]
       [button {:size   :medium
                :surface  :faint
                :colorway :red} "Next"]]
      [:div (sx :.flex-col-start :p--20px :gap--1rem)
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
                    :colorway :yellow} "Next"]
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
                :colorway :yellow} "Next"]
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
                :colorway :yellow} "Next"]
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
                :colorway :yellow} "Next"]
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
  ;;     :weight         :thin}
  ;;    (sx :fs--98px))
  ;;   "star"]
     
     [button
      {:start-enhancer 8 #_[icon :phone]
       :size           :xxlarge
       :weight         :bold}
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
    ;; :loading     true
       :end-enhancer #_[icon :east]
       [propeller]    }
      "Play"]
     
     [button
      {
    ;; :loading     true
       :end-enhancer #_[icon :east]
       [donut]        }
      "Play"]
     
     [button
      {
    ;; :loading     true
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
                           :weight :bold}
      :choices            ["Email" "Phone" "Mail"]}]
  

;; This will auto-generate...
  
  #_[:div (sx :.flex-col-fs :ai--fs :gap--0.75em)
     [:div (sx :.xxxlarge :.bold :.flex-row-fs :gap--0.5em)
      [radio-button {:name           :baz
                     :id             "baz-radio-group_email-choice"
                     :value          "email"
                     :size          :xxxlarge
                     :weight        :bold
                     :label-attrs   {}
                     :wrapper-attrs {}}]
      [:label {:for "baz-radio-group_email-choice"}
       "Email"]]
     

     [radio-button {:name    :baz
                    :value   "phone"
                    :label  "phonnne"
                    :size   :xxxlarge
                    :weight :bold}]
     [radio-button {:name    :baz
                    :value   "mail"
                    :label  "mailll"
                    :size   :xxxlarge
                    :weight :bold}]]


  #_[pane-samples]

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
;;   (sx {:font-size    :$size-small
;;        :sm:font-size :$size-large
;;        :br           :$shape-rounded-absolute
;;        :large:br     :$shape-rounded-absolute}))

;; '[tag {:class ["foo.wtf__L20_C30"
;;                "surface-solid" 
;;                "colorway-gold"]
;;        :style ""}]
