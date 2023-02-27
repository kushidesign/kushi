(ns kushi.ui.examples
  (:require
   [kushi.core :refer (sx merge-attrs)]
   [kushi.ui.button.core :refer (button)]
   [kushi.ui.input.radio.core :refer (radio)]
   [kushi.ui.input.checkbox.core :refer (checkbox)]
   [kushi.ui.input.text.core :refer (input)]
   [kushi.ui.input.slider.core :refer (slider)]
   [kushi.ui.alert.core :refer (alert)]
   [kushi.ui.grid.core :refer (grid)]
   [kushi.ui.tag.core :refer (tag)]
   [kushi.ui.label.core :refer (label)]
   [kushi.ui.card.core :refer (card)]
   [kushi.ui.tooltip.core :refer (tooltip-attrs)]
   [kushi.ui.collapse.core :refer (collapse accordian)]
   [kushi.ui.modal.core :refer (modal close-kushi-modal open-kushi-modal)]
   [kushi.ui.icon.mui.examples :refer [icon-examples]]
   [kushi.ui.icon.core :refer [icon]]
   [kushi.playground.util :refer-macros (example2)]))

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
                :example (example2 [button [icon :play-arrow] "Play"])}
               {:label   "Trailing icon"
                :example (example2 [button "Play" [icon :play-arrow]])}
               {:label   "2 icons"
                :example (example2 [button [icon :auto-awesome] "Play" [icon :auto-awesome]])}
               {:label   "Icon button"
                :example (example2 [button [icon :play-arrow]])}
               {:label   "Custom"
                :example (example2 [button (sx :.heavy
                                               :.xxxloose
                                               :c--white
                                               :pb--1em
                                               :pi--5em
                                               [:transform '(skew :159deg)]
                                               [:bgi '(linear-gradient :135deg :$blue600 :$magenta500)])
                                    "YES"])}
               {:label   "On-click"
                :example (example2 [button {:on-click (fn [e] (js/alert "Clicked!"))} "Play"])}
               ]}

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
                                    [label (sx :.bold :mbe--0.75em) "Choose an option:"]
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
                                     {:style {"&_.kushi-radio:nth-child(even):mis"                        :1em
                                              :&_.emoji:filter                                            "grayscale(1)"
                                              :&_.emoji:transition-property                               :transform
                                              :&_.emoji:transition-duration                               :500ms
                                              :&_.kushi-radio-input:checked+.kushi-label>.emoji:filter    :none
                                              :&_.kushi-radio-input:checked+.kushi-label>.emoji:transform "scale(1.5)"
                                              :&_.kushi-radio-input:checked+.kushi-label>.emoji:animation :jiggle2:0.5s}})
                                    [radio
                                     {:-input-attrs {:name           :demo
                                                     :defaultChecked true}} [label [:span.emoji "🦑"] "Squid"]]
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
                :example (example2 [checkbox [label "Make it shiny" [icon :auto-awesome]]])}]}

   {:fn       input
    :meta     #'input
    :stage    {:style {:min-height :170px}}
    :controls [:size :weight]
    :defaults {:size     :medium
               :weight   :normal
               :examples "Simple"}
    :content  [{:label   "Simple"
                :example (example2 [input (sx {:placeholder "Your text here"
                                               :-label      "Input label"})])}

               {:label   "Required"
                :example (example2 [input (sx {:placeholder "Your text here"
                                               :required    true
                                               :-label      "Input label"})])}
               {:label   "Disabled"
                :example (example2 [input (sx {:placeholder "Your text here"
                                               :disabled    true
                                               :-label      "Input label"})])}
               {:label   "With helper"
                :example (example2 [input (sx {:placeholder "Your text here"
                                               :-label      "Input label"
                                               :-helper     "Your helper text here"})])}
               {:label   "With start enhancer"
                :example (example2 [input (sx {:placeholder     "Monetary value"
                                               :-start-enhancer "$"
                                               :-label          "Input label"})])}
               {:label   "With end enhancer"
                :example (example2 [input (sx {:placeholder   "Your text here"
                                               :-end-enhancer [icon :star]
                                               :-label        "Input label"})])}
               {:label   "Inline label"
                :example (example2 [input (sx {:placeholder      "Your text here"
                                               :-label           "Input label"
                                               :-label-placement :inline})])}
               {:label   "Inline label with helper"
                :example (example2 [input (sx {:placeholder      "Your text here"
                                               :-label           "Input label"
                                               :-label-placement :inline
                                               :-helper          "Your helper text here"})])}
               {:label   "With semantic class"
                :example (example2 [input (sx {:placeholder "Your text here"
                                               :-label      "Input label"
                                               :-helper     "Your helper text here"
                                               :-semantic   :negative})])}
               {:label   "All options"
                :example (example2 [input (sx {:placeholder          "Your text here"
                                               :required             false
                                               :disabled             false
                                               :-start-enhancer      "$"
                                               :-end-enhancer        "🦄"
                                               :-label               "Input label"
                                               :-label-placement     :inline
                                               :-helper              "Your helper text here"
                                               :-semantic            :accent
                                               :-outer-wrapper-attrs (sx :b--1px:solid:yellow
                                                                         :box-shadow--8px:8px:17px:#f2baf9ab
                                                                         :p--1em)
                                               :-label-attrs         (sx :bgc--yellow)
                                               :-wrapper-attrs       (sx :box-shadow--4px:4px:7px:#f2baf9ab
                                                                         {:class :my-input-wrapper-name})
                                               })])}]}

   {:fn       slider
    :meta     #'slider
    :stage    {:style {:min-height :135px}}
    :defaults {:examples "Simple"}
    :content  [{:label   "Simple"
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

   {:fn       tooltip-attrs
    :meta     #'tooltip-attrs
    :title    "Tooltip"
    :stage    {:style {:min-height      :200px
                       :justify-content :center}}
    :defaults {:examples "Auto"}
    :content  [
               {:label   "Auto"
                :example (example2 [button
                                    (tooltip-attrs {:-text "My tooltip text"})
                                    "Hover me to reveal tooltip"])}

              ;; Leave these auto placements out for now
              ;;  {:label   "block-start, auto"
              ;;   :example (example2 [button
              ;;                       (tooltip-attrs {:-text      "My tooltip text"
              ;;                                       :-placement "block-start"})
              ;;                       "Hover me to reveal tooltip"])}

              ;;  {:label   "block-end, auto"
              ;;   :example (example2 [button
              ;;                       (tooltip-attrs {:-text      "My tooltip text"
              ;;                                       :-placement "block-end"})
              ;;                       "Hover me to reveal tooltip"])}

              ;;  {:label   "inline-start, auto"
              ;;   :example (example2 [button
              ;;                       (tooltip-attrs {:-text      "My tooltip text"
              ;;                                       :-placement "inline-start"})
              ;;                       "Hover me to reveal tooltip"])}

              ;;  {:label   "inline-end, auto"
              ;;   :example (example2 [button
              ;;                       (tooltip-attrs {:-text      "My tooltip text"
              ;;                                       :-placement "inline-end"})
              ;;                       "Hover me to reveal tooltip"])}


               {:label   "top-left-corner"
                :example (example2 [button
                                    (tooltip-attrs {:-text      "My tooltip text"
                                                    :-placement :top-left-corner})
                                    "Hover me to reveal tooltip"])}

               {:label   "top-left"
                :example (example2 [button
                                    (tooltip-attrs {:-text      "My tooltip text"
                                                    :-placement :top-left})
                                    "Hover me to reveal tooltip"])}

               {:label   "top"
                :example (example2 [button
                                    (tooltip-attrs {:-text      "My tooltip text"
                                                    :-placement :top})
                                    "Hover me to reveal tooltip"])}

               {:label   "top-right"
                :example (example2 [button
                                    (tooltip-attrs {:-text      "My tooltip text"
                                                    :-placement :top-right})
                                    "Hover me to reveal tooltip"])}

               {:label   "top-right-corner"
                :example (example2 [button
                                    (tooltip-attrs {:-text      "My tooltip text"
                                                    :-placement :top-right-corner})
                                    "Hover me to reveal tooltip"])}

               {:label   "right-top-corner"
                :example (example2 [button
                                    (tooltip-attrs {:-text      "My tooltip text"
                                                    :-placement :right-top-corner})
                                    "Hover me to reveal tooltip"])}

               {:label   "right"
                :example (example2 [button
                                    (tooltip-attrs {:-text      "My tooltip text"
                                                    :-placement :right})
                                    "Hover me to reveal tooltip"])}

               {:label   "inline-end, center"
                :example (example2 [button
                                    (tooltip-attrs {:-text      "My tooltip text"
                                                    :-placement "inline-end center"})
                                    "Hover me to reveal tooltip"])}

               {:label   "block-start, inline-start"
                :example (example2 [button
                                    (tooltip-attrs {:-text      "My tooltip text"
                                                    :-placement "block-start inline-start"})
                                    "Hover me to reveal tooltip"])}

               {:label   "block-start, center"
                :example (example2 [button
                                    (tooltip-attrs {:-text      "My tooltip text"
                                                    :-placement "block-start center"})
                                    "Hover me to reveal tooltip"])}

               {:label   "block-start, inline-end"
                :example (example2 [button
                                    (tooltip-attrs {:-text      "My tooltip text"
                                                    :-placement "block-start inline-end"})
                                    "Hover me to reveal tooltip"])}

               {:label   "block-start, inline-end, corner"
                :example (example2 [button
                                    (tooltip-attrs {:-text      "My tooltip text"
                                                    :-placement "block-start inline-end corner"})
                                    "Hover me to reveal tooltip"])}
               {:label   "With forced linebreaks"
                :example (example2 [button
                                    (tooltip-attrs {:-text      ["My tooltip text line1" "My tooltip text line2"]
                                                    :-placement :right #_"inline-end"})
                                    "Hover me to reveal tooltip"])}

               {:label   "Reveal on click"
                :example (example2 [button
                                    (tooltip-attrs {:-text                     "My tooltip text!"
                                                    :-reveal-on-click?         true
                                                    :-reveal-on-click-duration 1500
                                                    :-placement                :right #_"inline-end"})
                                    "Click me to reveal tooltip"])}

               {:label   "Toggle on click"
                :example (example2 [:div.flex-row-fs
                                    [button "WTF"]
                                    [icon (merge-attrs
                                           (sx :.pill
                                               :.pointer
                                               :m--1em
                                               :hover:c--black
                                               :hover:bgc--$neutral-background-color
                                               :dark:hover:c--white
                                               :dark:hover:bgc--$neutral-background-color-inverse
                                               :&.kushi-pseudo-tooltip-revealed:c--$accent-color
                                               :&.kushi-pseudo-tooltip-revealed:bgc--$accent-background-color
                                               :dark:&.kushi-pseudo-tooltip-revealed:c--$accent-color-inverse
                                               :dark:&.kushi-pseudo-tooltip-revealed:bgc--$accent-background-color-inverse
                                              ;;  :hover:bgc--$accent-background-color
                                              ;;  :dark:hover:c--$accent-color-inverse
                                               )
                                           (tooltip-attrs {:-text                     "My tooltip text!"
                                                           :-reveal-on-click?         true
                                                           :-reveal-on-click-duration :infinite
                                                           :-placement                :right})) :info]])}

               {:label   "With custom styled span"
                :example (example2 [:span
                                    (merge-attrs
                                     (sx :.relative
                                         :.pointer
                                         :.pill
                                         :tt--u
                                         :td--u
                                         :tuo--8px
                                         :tds--dashed
                                         :tdc--$green400
                                         :tdt--4px
                                         :pi--1em
                                         :pb--0.25em)
                                     (tooltip-attrs {:-text      "My tooltip text"
                                                     :-placement :top}))
                                    "Hover me to reveal tooltip"])}]}

   {:fn       icon
    :meta     #'icon
    :title    "Icons"
    :stage    {:style {:min-height :135px}}
    :controls [:size]
    :defaults {:size :medium}

    :content  icon-examples }

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
                :example (example2 [label [icon :pets] "Pet friendly"])}
               {:label   "Trailing icon"
                :example (example2 [label "Pet friendly" [icon :pets]])}]}

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


   {:fn       alert
    :meta     #'alert
    :stage    {:style {:min-height :150px}}
    :controls [:semantic :kind :shape :size :weight]
    :defaults {:kind     :default
               :shape    :sharp
               :semantic :accent
               :size     :medium
               :weight   :wee-bold
               :examples "Default"}
    :content  [{:label   "Default"
                :example (example2 [alert
                                    (sx :.accent
                                        {:-icon             [icon :info]
                                         :-close-icon?      true
                                         :-close-icon-attrs {:on-click #(js/alert "Example close-icon click event.")}})
                                    "Your message goes here."])}
               {:label   "with fixed position"
                :example (example2 [alert
                                    (sx :.accent
                                        :.fixed
                                        :zi--100
                                        :bottom--0
                                        :left--0
                                        :right--0
                                        {:-icon             [icon :auto-awesome]
                                         :-close-icon?      true
                                         :-close-icon-attrs {:on-click #(js/alert "Example close-icon click event.")}})
                                    "Your message goes here."])}
               ]}

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
                                     :-body-attrs (sx :bgc--$accent100 :pl--1rem)}
                                    [:p "child 1"]
                                    [:p "child 2"]])}
               {:label   "Header color"
                :example (example2 [collapse
                                    (sx :bbe--3px:solid:#3d3d3d
                                        {:-label        "Collapsable section label "
                                         :-body-attrs   (sx :pl--0.5rem)
                                         :-header-attrs (sx :p--10px
                                                            :bgc--$gray1000
                                                            :c--white)})
                                    [:p "child 1"]
                                    [:p "child 2"]])}

               ;; TODO fix expanded? functionality
               #_{:label   "Expanded"
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
