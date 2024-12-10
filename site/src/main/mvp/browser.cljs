(ns mvp.browser
  (:require
   [reagent.dom :as rdom]
   [kushi.core :refer [merge-attrs]]
   [kushi.css.core :refer [css sx defcss ?css css-vars css-vars-map]]
   ;; [mvp.views :as views]
   ;; [mvp.button :refer [my-button]]
   ;; [kushi.ui.slider.core :refer [slider]]
   ;; [kushi.ui.radio.core :refer [radio]]
   ;; [kushi.ui.checkbox.core :refer [checkbox]]
   ;; [kushi.ui.label.core :refer [label]]
   ;; [kushi.ui.icon.core :refer [icon]]
   [kushi.ui.text-field.core :refer [text-field]]
   ;; [kushi.ui.spinner.core :refer [spinner donut propeller thinking]]
   ;; [kushi.ui.grid.core :refer [grid]]
   ;; [kushi.ui.callout.core :refer [callout]]
   ;; [kushi.ui.link.core :refer [link]]
   ;; [kushi.ui.collapse.core :refer [collapse]]
   ;; [kushi.ui.switch.core :refer [switch]]
   ;; [kushi.ui.card.core :refer [card]]
   ;; [kushi.ui.tag.core :refer [tag]]
   [kushi.ui.modal.core :refer [modal
                                modal-close-button
                                open-kushi-modal
                                close-kushi-modal]]
   [kushi.ui.tooltip.core :refer [tooltip-attrs]]
   [kushi.ui.button.core :refer [button]]
   [kushi.ui.dom.pane.styles]
   ))

(defcss "@keyframes jiggle2"
  [:0% {:transform "rotate(0deg) scale(1.55)"}]
  [:18% {:transform "rotate(-5deg) scale(1.55)"}]
  [:36% {:transform "rotate(5deg) scale(1.55)"}]
  [:54% {:transform "rotate(0deg) scale(1.55)"}]
  [:72% {:transform "rotate(5deg) scale(1.55)"}]
  [:90% {:transform "rotate(-5deg) scale(1.55)"}]
  [:100% {:transform "rotate(0deg) scale(1.55)"}])

(defn main-view []
  [:div (sx #_:.absolute-centered
            :p--5rem
            :.flex-col-c)
   

  ;;  (let [id "my-modal-basic"]
  ;;    [:div [button {:on-click (fn* [] (open-kushi-modal id))}
  ;;           "Click to open modal"]
  ;;     [modal {:class (css :min-width--300px
  ;;                         :_.kushi-modal-description:fs--$small)
  ;;             :id    id}
  ;;      [:div (sx :.xxxlarge :.flex-row-c) "💃🏽"]]])
   

   [button (tooltip-attrs {:-text "This is a tooltip"})
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
                     :_.kushi-modal-description:fs--$small)
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
                            :_.kushi-modal-description:fs--$small)
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
        {:class          (css :fs--$small)
         :name           :xxxlarge-sample
         :defaultChecked true}]
       [checkbox
        {:class          (css :fs--$small)
         :name           :xxxlarge-sample
         :defaultChecked false}]]
      [:div (sx :.flex-col-fs :gap--1.0rem)
       [checkbox
        {:class          (css :fs--$xxxlarge)
         :name           :xxxlarge-sample
         :defaultChecked true}]
       [checkbox
        {:class          (css :fs--$xxxlarge)
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
