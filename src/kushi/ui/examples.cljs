(ns kushi.ui.examples
  (:require
   [kushi.core :refer (sx defkeyframes)]
   [kushi.ui.button.core :refer (button)]
   [kushi.ui.input.radio.core :refer (radio)]
   [kushi.ui.input.checkbox.core :refer (checkbox)]
   [kushi.ui.input.text.core :refer (input)]
   [kushi.ui.input.slider.core :refer (slider)]
   [kushi.ui.grid.core :refer (grid)]
   [kushi.ui.icon.mui.core :refer (mui-icon)]
   [kushi.ui.tag.core :refer (tag)]
   [kushi.ui.title.core :refer (title)]
   [kushi.ui.label.core :refer (label)]
   [kushi.ui.card.core :refer (card)]
   [kushi.ui.tooltip.core :refer (tooltip)]
   [kushi.ui.collapse.core :refer (collapse accordian)]
   [kushi.ui.modal.core :refer (modal close-kushi-modal open-kushi-modal)]
   [kushi.ui.icon.mui.examples :refer [icon-examples]]
   [playground.util :refer-macros (example2)]))


(def components
  [{:fn       button
    :meta     #'button
    :stage    {:style {:min-height :135px}}
    :controls [:kind :shape :semantic :size :weight]
    :defaults {:kind     :default
               :semantic :neutral
               :size     :medium
               :shape    :sharp
               :weight   :wee-bold
               :examples "Simple"}
    :content  [{:label   "Simple"
                :example (example2 [button "Play"])}
               {:label   "Leading icon"
                :example (example2 [:div
                                    [button (sx {:-mui-icon "play_arrow"})
                                     "Play"]])}
               {:label   "Trailing icon"
                :example (example2 [button {:-mui-icon      "play_arrow"
                                            :-icon-position :inline-end}
                                    "Play"])}
               {:label   "Icon above"
                :example (example2 [button (sx {:-mui-icon      "play_arrow"
                                                :-icon-position :block-start})
                                    "Play"])}
               {:label   "Icon below"
                :example (example2 [button (sx {:-mui-icon      "play_arrow"
                                                :-icon-position :block-end})
                                    "Play"])}

              ;; TODO -- fix this example
              ;;  {:label   "Trailing icon, custom spacing"
              ;;   :example (example2 [button (sx :&_.kushi-icon:mis--0
              ;;                                  {:-mui-icon      "play_arrow"
              ;;                                   :-icon-position :inline-end})
              ;;                       "Play"])}

               {:label   "Icon button"
                :example (example2 [button (sx  {:-mui-icon "play_arrow"})])}

               {:label   "Custom dimensions"
                :example (example2 [button (sx :w--75px :h--75px) "YES"])}]}

   {:fn       radio
    :meta     #'radio
    :stage    {:style {:min-height :185px}}
    :controls [:size :weight]
    :defaults {:size     :medium
               :weight   :wee-bold
               :examples "Simple"}
    :content  [{:label   "Simple"
                :example (example2 [:span
                                    [radio {:-input-attrs {:name :demo}} "Yes"]
                                    [radio {:-input-attrs {:name :demo}} "No"]
                                    [radio {:-input-attrs {:name :demo}} "Maybe"]])}
               {:label   "Inherited color"
                :example (example2 [:section (sx :c--$purple400)
                                    [title (sx :.bold :mbe--0.75em) "Choose an option:"]
                                    [radio {:-input-attrs {:name :demo}} "Yes"]
                                    [radio {:-input-attrs {:name :demo}} "No"]
                                    [radio {:-input-attrs {:name :demo}} "Maybe"]])}

               {:label   "Custom, with default checked"
                :example (example2 [:span
                                    (sx
                                     :d--grid
                                     :gtc--1fr:1fr
                                     :&_.emoji:fs--28px
                                     :&_.emoji:mi--0.3em:0.6em
                                     :&_.kushi-radio:mbe--0.95em
                                     {:style {"&_.kushi-radio:nth-child(even):mis"                          :1em
                                              :&_.emoji:filter                                            "grayscale(1)"
                                              :&_.emoji:transition-property                               :transform
                                              :&_.emoji:transition-duration                               :500ms
                                              :&_.kushi-radio-input:checked+.kushi-label>.emoji:filter    :none
                                              :&_.kushi-radio-input:checked+.kushi-label>.emoji:transform "scale(1.5)"
                                              :&_.kushi-radio-input:checked+.kushi-label>.emoji:animation :jiggle2:0.5s}})
                                    [radio
                                     {:-input-attrs {:name :demo :defaultChecked true}} [label [:span.emoji "🦑"] "Squid"]]
                                    [radio {:-input-attrs {:name :demo}} [label [:span.emoji "🐋"] "Whale"]]
                                    [radio {:-input-attrs {:name :demo}} [label [:span.emoji "🦈 "] "Shark"]]
                                    [radio {:-input-attrs {:name :demo}} [label [:span.emoji "🐊"] "Croc"]]])}]}

   {:fn       checkbox
    :meta     #'checkbox
    :stage    {:style {:min-height :135px}}
    :controls [#_:kind #_:shape :size :weight]
    :defaults {:kind     :secondary
               :size     :medium
               :shape    :sharp
               :weight   :wee-bold
               :examples "Simple"}
    :content  [{:label   "Simple"
                :example (example2 [checkbox "Sign me up"])}
               {:label   "With trailing icon"
                :example (example2 [checkbox [label {:-mui-icon      "auto_awesome"
                                                     :-icon-position :inline-end}
                                              "Make it shiny" ]])}]}

   {:fn       input
    :meta     #'input
    :stage    {:style {:min-height :135px}}
    :controls [:size :weight]
    :defaults {:size     :medium
               :weight   :normal
               :examples "Simple"}
    :content  [{:label   "Simple"
                :example (example2 [input (sx {:placeholder "Your text here"
                                               :-label      "Input label"})])}
               {:label   "With start enhancer"
                :example (example2 [input (sx {:placeholder     "Monetary value"
                                               :-start-enhancer "$"
                                               :-label          "Input label"})])}
               {:label   "With end enhancer"
                :example (example2 [input (sx {:placeholder "Your text here"
                                               :-end-enhancer [mui-icon (sx :pi--0.375em) "star"]
                                               :-label      "Input label"})])}
               {:label   "Inline label"
                :example (example2 [input (sx {:placeholder      "Your text here"
                                               :-label           "Input label"
                                               :-label-placement :inline
                                               :-label-attrs     (sx :ws--n :min-width--100px)})])} ]}

   {:fn      slider
    :meta    #'slider
    :stage   {:style {:min-height :135px}}
    :defaults {:examples "Simple"}
    :content [{:label   "Simple"
               :example (example2 [slider {:min 0
                                           :max 7}])}
              {:label   "Lables"
               :example (example2 [slider {:min          0
                                           :max          7
                                           :-step-marker :label}])}
              {:label   "Dot markers"
               :example (example2 [slider {:min          0
                                           :max          7
                                           :-step-marker :dot}])}
              {:label   "Bar markers"
               :example (example2 [slider {:min          0
                                           :max          7
                                           :-step-marker :bar}])}
              {:label   "Fractional step"
               :example (example2 [slider {:min  0
                                           :max  1
                                           :step 0.01}])}
              {:label   "Supplied step values"
               :example (example2 [slider {:-steps            ["xsmall" "medium" "large" "xlarge"]
                                           :-step-marker      :label
                                           :-label-size-class :medium}])}
              #_{:label   "Supplied step values, custom label styling"
               :example (example2 [slider {:-steps              ["low" "guarded" "elevated" "high" "severe"]
                                           :-step-marker        :label
                                           :-label-size-class   :small
                                           :-label-scale-factor 0.8
                                           :-labels-attrs       (sx :>span>span:border-radius--9999px
                                                                    :>span>span:padding--0.125em:0.75em:0.2em
                                                                    {:style {">span:nth-child(1):c"         :$positive
                                                                             ">span:nth-child(1):>span:bgc" :$positive50
                                                                             ">span:nth-child(2):c"         :$accent
                                                                             ">span:nth-child(2):>span:bgc" :$accent50
                                                                             ">span:nth-child(3):c"         :$warning
                                                                             ">span:nth-child(3):>span:bgc" :$warning50
                                                                             ">span:nth-child(4):c"         :$orange700
                                                                             ">span:nth-child(4):>span:bgc" :$orange50
                                                                             ">span:nth-child(5):c"         :$negative
                                                                             ">span:nth-child(5):>span:bgc" :$negative50}})}])}]}

   {:fn       tooltip
    :meta     #'tooltip
    :stage    {:style {:min-height      :135px
                       :justify-content :center}}
    :controls [:shape]
    :defaults {:shape    :rounded
               :examples "Auto"}
    :content  [{:label   "Auto"
                :example (example2 [button
                                    "Hover me"
                                    [tooltip "This is an example tooltip"]])}
               {:label   "Auto, Start"
                :example (example2 [button
                                    "Hover me"
                                    [tooltip {:-inline-offset :start} "This is an example tooltip"]])}
               {:label   "Auto, End"
                :example (example2 [button
                                    "Hover me"
                                    [tooltip {:-inline-offset :end} "This is an example tooltip"]])}
               {:label   "Above, Start"
                :example (example2 [button
                                    "Hover me"
                                    [tooltip {:-inline-offset :start
                                              :-block-offset  :start} "This is an example tooltip"]])}
               {:label   "Above, End"
                :example (example2 [button
                                    "Hover me"
                                    [tooltip {:-inline-offset :end
                                              :-block-offset  :start} "This is an example tooltip"]])}
               {:label   "Below, End"
                :example (example2 [button
                                    "Hover me"
                                    [tooltip {:-inline-offset :end
                                              :-block-offset  :end} "This is an example tooltip"]])}
               {:label   "inline-start"
                :example (example2 [button
                                    "Hover me"
                                    [tooltip (sx {:-placement :inline-start}) "This is an example tooltip"]])}
               {:label   "inline-end"
                :example (example2 [button
                                    "Hover me"
                                    [tooltip (sx {:-placement :inline-end}) "This is an example tooltip"]])}
               {:label   "block-start"
                :example (example2 [button
                                    "Hover me"
                                    [tooltip (sx {:-placement :block-start}) "This is an example tooltip"]])}
               {:label   "block-end"
                :example (example2 [button
                                    "Hover me"
                                    [tooltip (sx {:-placement :block-end}) "This is an example tooltip"]])}
               {:label   "Custom styling"
                :example (example2 [button
                                    "Hover me"
                                    [tooltip (sx :c--black
                                                 :bgc--white
                                                 :box-shadow--1px:2px:7px:#42320035|0px:0px:1px:#42320035
                                                 :bgc--$yellow100
                                                 :p--0.3em:0.6em!important)
                                     "This is an example tooltip"]])}]}

   {:fn                   mui-icon
    :meta                 #'mui-icon
    :title                "Icons"
    :stage                {:style {:min-height :135px}}
    :controls             [:size]
    :defaults             {:size :medium}
    :desc                 ["Icons in Kushi are pulled in via [Google's Material Icons font for the web](https://developers.google.com/fonts/docs/material_icons#icon_font_for_the_web)."
                           :br
                           "Use [this page](https://fonts.google.com/icons?icon.set=Material+Icons) to explore over 1000+ different icons."
                           :br
                           :br
                           "This component expects a child argument which is a string or keyword that correspondes to the name of an existing mui icon. If a string, snake case (underscores instead of spaces) must be used. If using a keyword, kebab (hyphens instead of spaces) case must be used."]

    :content              icon-examples }

   {:fn       tag
    :meta     #'tag
    :stage    {:style {:min-height :135px}}
    :desc     ["A tag is a kind of label, often displayed together with other tags for categorization and context."]
    :controls [:kind :shape :semantic :size :weight]
    :defaults {:kind     :default
               :shape    :rounded
               :semantic :neutral
               :size     :xsmall
               :weight   :wee-bold
               :examples "\"my tag\""}
    :content  [{:label   "\"my tag\""
                :example (example2 [tag "my tag"])}
               {:label   "\"xyz\""
                :example (example2 [tag "xyz"])}
               {:label   "\"XYZ\""
                :example (example2 [tag "XYZ"])}
               {:label   "Max-width example"
                :example (example2 [tag (sx :max-width--140px) "My tag with longer text"])}]}


   {:fn       label
    :meta     #'label
    :stage    {:style {:min-height :135px}}
    :controls [:size :weight]
    :defaults {:size     :medium
               :weight   :wee-bold
               :examples "Simple"}
    :content  [{:label   "Simple"
                :example (example2 [label "my label"])}
               {:label   "Leading icon"
                :example (example2 [label (sx {:-mui-icon "pets"}) "Pet friendly"])}
               {:label   "Trailing icon"
                :example (example2 [label (sx {:-mui-icon      "pets"
                                               :-icon-position :inline-end}) "Pet friendly"])}]}

   {:fn       title
    :meta     #'title
    :stage    {:style {:min-height :135px}}
    :controls [:size :weight]
    :defaults {:size     :medium
               :weight   :wee-bold
               :examples "Simple"}
    :content  [{:label   "Simple"
                :example (example2 [title "my title"])}
               {:label   "Longer text"
                :example (example2 [title "My title with longer text"])}
               #_{:label   "Leading icon"
                  :example (example2 [title (sx {:-mui-icon "pets"}) "Pet friendly"])}
               #_{:label   "Trailing icon"
                  :example (example2 [title (sx {:-mui-icon      "pets"
                                                 :-icon-position :inline-end} "Pet friendly")])}]}

   {:fn       card
    :meta     #'card
    :stage    {:style {:min-height :280px}}
    :defaults {:examples "Default"}
    :content  [{:label   "Default"
                :example (example2 [card (sx :.elevated) "my content"])}
               {:label   "sharp, bordered"
                :example (example2 [card (sx :.sharp :.bordered) "my content"])}
               {:label   "rounded, bordered"
                :example (example2 [card (sx :.rounded :.bordered) "my content"])}
               {:label   "Alien"
                :example (example2 [card (sx :.large
                                             :.extra-bold
                                             :.flex-col-c
                                             :p--0
                                             :height--220px
                                             :tt--u
                                             :ta--center
                                             :bgc--#313131
                                             :c--white
                                             :b--1px:solid:#9eef00
                                             :text-shadow--1px:1px:5px:#9eef00b5
                                             :box-shadow--inset:0px:0px:40px:#9eef0073)
                                    [:span (sx :pis--7ex :letter-spacing--7ex) "alien"]])}]}

   {:fn       modal
    :meta     #'modal
    :stage    {:style {:min-height :100px}}
    :refers   '[open-kushi-modal close-kushi-modal]
    :defaults {:examples "Default"}
    :content  [{:label   "Default"
                :example (example2 [modal
                                    {:-trigger [button (sx {:on-click open-kushi-modal}) "click to launch modal"]}
                                    [:div (sx :.flex-col-sa :ai--c :h--100% :w--100%)
                                     [:p (sx :.xxlarge :.normal) "modal content"]
                                     [button {:on-click close-kushi-modal} "submit and dismiss"]]])}
               {:label   "with dark scrim"
                :example (example2 [modal
                                    (sx
                                     {:-trigger     [button (sx {:on-click open-kushi-modal}) "click to launch modal"]
                                      :-scrim-attrs (sx {:style {:bgc '(rgba 88 88 88 0.82)}})})
                                    [:div (sx :.flex-col-sa :ai--c :h--100% :w--100%)
                                     [:p (sx :.xxlarge :.normal) "modal content"]
                                     [button {:on-click close-kushi-modal} "submit and dismiss"]]])}
               {:label   "rounded, bordered, offset panel"
                :example (example2 [modal
                                    (sx
                                     {:-trigger     [button (sx {:on-click open-kushi-modal}) "click to launch modal"]
                                      :-panel-attrs (sx :border-radius--24px
                                                        :b--1px:solid:black
                                                        :mbs--10vh
                                                        :box-shadow--none)
                                      :-scrim-attrs (sx {:style {:bgc '(rgba 255 255 255 0.9)
                                                                 :jc  :fs}})})
                                    [:div (sx :.flex-col-sa :ai--c :h--100% :w--100%)
                                     [:p (sx :.xxlarge :.normal) "modal content"]
                                     [button {:on-click close-kushi-modal} "submit and dismiss"]]])}]}

   {:fn       collapse
    :meta     #'collapse
    :stage    {:style {:min-height  :190px
                       :align-items :start}}
    :defaults {:examples "Default"}
    :content  [{:label   "Default"
                :example (example2 [collapse
                                    {:-label "collapsable section label"}
                                    [:p "child 1"]
                                    [:p "child 2"]])}
               {:label   "Dynamic label"
                :example (example2 [collapse
                                    {:-label          "Click to expand"
                                     :-label-expanded "Click to collapse"}
                                    [:p "child 1"]
                                    [:p "child 2"]])}
               {:label   "Icon on right"
                :example (example2 [collapse
                                    {:-label         "Collapsable section label "
                                     :-icon-position :end}
                                    [:p "child 1"]
                                    [:p "child 2"]])}

               {:label   "Borders"
                :example (example2 [collapse
                                    (sx
                                     :bbe--1px:solid:black
                                     {:-label        "Collapsable section label "
                                      :-header-attrs (sx :bbs--1px:solid:black)})
                                    [:p "child 1"]
                                    [:p "child 2"]])}
               {:label   "Label weight"
                :example (example2 [collapse
                                    {:-label        "Collapsable section label "
                                     :-header-attrs (sx :.bold)}
                                    [:p "child 1"]
                                    [:p "child 2"]])}
               {:label   "Body color"
                :example (example2 [collapse
                                    {:-label      "Collapsable section label "
                                     :-body-attrs (sx :bgc--#ffe3ac :pl--1rem)}
                                    [:p "child 1"]
                                    [:p "child 2"]])}
               {:label   "Header color"
                :example (example2 [collapse
                                    (sx :bbe--3px:solid:#3d3d3d
                                        {:-label        "Collapsable section label "
                                         :-body-attrs   (sx :pl--0.5rem)
                                         :-header-attrs (sx :p--10px
                                                            :bgc--#3d3d3d
                                                            :c--white
                                                            :fw--600)})
                                    [:p "child 1"]
                                    [:p "child 2"]])}
               {:label   "Expanded"
                :example (example2 [collapse
                                    {:-expanded?  true
                                     :-label      "Collapsable section label "
                                     :-body-attrs (sx :bgc--#ffe3ac :pl--1rem)}
                                    [:p "child 1"]
                                    [:p "child 2"]])}
               {:label   "With click handler"
                :example (example2 [collapse
                                    {:-label    "Collapsable section label"
                                     :-on-click #(js/alert "clicked")}
                                    [:p "child 1"]
                                    [:p "child 2"]])}]}

   {:fn       accordian
    :meta     #'accordian
    :stage    {:style {:min-height  :190px
                       :align-items :start}}
    :desc     ["cool"]
    :defaults {:examples "Simple"}
    :content  [{:label   "Simple"
                :example (example2 [accordian
                                    [collapse {:-label "first section"} [:p "child 1"] [:p "child 2"]]
                                    [collapse {:-label "second section"} [:p "child 1"] [:p "child 2"]]])}
               {:label   "borders"
                :example (example2
                          [accordian
                           (sx :>section:first-child:bbs--1px:solid:black)
                           (for [[label-text content] [["first section" "lorem ipsum"]
                                                       ["second section" "lorem ipsum2"]
                                                       ["third section" "lorem ipsum3"]]]
                             ^{:key label-text}
                             [collapse
                              (sx
                               :bbe--1px:solid:black
                               {:-label "collapsable section label" })
                              [:p content]])])}]}

   {:fn       grid
    :meta     #'grid
    :stage    {:style {:min-height :135px
                       :padding    :20px}}
    :defaults {:examples "Simple"}
    :content  [{:label   "Simple"
                :example (example2 [grid
                                    (sx [:>div:bgc '(rgba 128 128 128 0.2)])
                                    (for [x (range 6)]
                                      [:div
                                       [:div (sx :.absolute-fill :.flex-col-c :ai--c) (inc x)]])])}
               {:label   "With sizing options"
                :example (example2 [grid
                                    (sx [:>div:bgc '(rgba 128 128 128 0.2)]
                                        {:-column-min-width :80px
                                         :-gap              :15px
                                         :-aspect-ratio     :2:3})
                                    (for [x (range 15)]
                                      [:div
                                       [:div (sx :.absolute-fill :.flex-col-c :ai--c) (inc x)]])])}]}])
