(ns ^dev-always kushi.ui.examples
  (:require
   [kushi.core :refer (sx merge-attrs)]
   [kushi.ui.button.core :refer (button)]
   [kushi.ui.spinner.core :refer (spinner donut propeller thinking)]
   [kushi.ui.button.demo :as button-demo]
   [kushi.ui.input.radio.core :refer (radio)]
   [kushi.ui.input.checkbox.core :refer (checkbox)]
   [kushi.ui.input.text.core :refer (text-field)]
   [kushi.ui.input.switch.core :refer (switch)]
   [kushi.ui.input.slider.core :refer (slider)]
   [kushi.ui.callout.core :refer (callout)]
   [kushi.ui.grid.core :refer (grid)]
   [kushi.ui.tag.core :refer (tag)]
   [kushi.ui.label.core :refer (label)]
   [kushi.ui.card.core :refer (card)]
   [kushi.ui.tooltip.core :refer (tooltip-attrs)]
   [kushi.ui.tooltip.demo :as tooltip-demo]
   [kushi.ui.popover.core :refer [popover-attrs dismiss-popover!]]
   [kushi.ui.popover.demo :refer [popover-content]]
   [kushi.ui.toast.core :refer [toast-attrs]]
   [kushi.ui.toast.demo :refer [toast-content]]
   [kushi.ui.icon.core :refer [icon]]
   [kushi.ui.collapse.core :refer (collapse accordion)]
   [kushi.ui.modal.core :refer (modal)]
   [kushi.ui.modal.examples :refer (modal-examples)]
   [kushi.ui.icon.mui.examples :refer [icon-examples]]
   [kushi.playground.util :refer-macros (feature example2)]
   [reagent.dom :as rdom]))


(def components
  [(feature
    button
    {:stage    {:style {:min-height :150px}}
     :variants [:kind :shape :semantic :size :weight]
     :demo     button-demo/demo
     :requires (str
                "            ;; Optional, for icons\n"
                "            [kushi.ui.icon.core :refer [icon]]\n"
                "            ;; Optional, for loading animations\n"
                "            [kushi.ui.spinner.core :refer [spinner\n"
                "                                            donut\n"
                "                                            propeller\n"
                "                                            thinking]]")
     :defaults {:kind     :default
                :semantic :neutral
                :size     :medium
                :shape    :sharp
                :weight   :wee-bold
                :examples "Simple"}
     :examples [{:label   "Simple"
                 :example [button "Play"]}
                {:label   "Leading icon"
                 :example [button [icon :play-arrow] "Play"]}
                {:label   "Trailing icon"
                 :example [button "Play" [icon :auto-awesome]]}
                {:label   "2 icons"
                 :example [button [icon :auto-awesome] "Play" [icon :auto-awesome]]}
                {:label   "Icon button"
                 :example [button [icon :play-arrow]]}
                {:label   "on-click"
                 :example [button {:on-click (fn [e] (js/alert "Clicked!"))} "Play"]}
                {:label   "Loading state, propeller"
                 :example [button {:-loading? true} [spinner "Play" [propeller]]]}
                {:label   "Loading state, dots"
                 :example [button {:-loading? true} [spinner "Play" [thinking]]]}
                {:label   "Loading state, donut"
                 :example [button {:-loading? true} [spinner "Play" [donut]]]}
                {:label   "Loading state, donut, fast"
                 :example [button {:-loading? true} [spinner "Play" [donut (sx :animation-duration--325ms)]]]}
                {:label   "Loading state, donut on icon"
                 :example [button {:-loading? true} [spinner [icon :play-arrow] [donut]] "Play"]}
                {:label   "Loading state, propeller on icon"
                 :example [button {:-loading? true} [spinner [icon :play-arrow] [propeller]] "Play"]}
                #_{:label   "Custom"
                   :example [button (sx :.heavy
                                        :.xxxloose
                                        :c--white
                                        :pb--1em
                                        :pi--5em
                                        [:transform '(skew :159deg)]
                                        [:bgi '(linear-gradient :135deg :$blue-600 :$magenta-500)])
                             "YES"]}
                ]})

   (feature
    radio
    {:stage    {:style {:min-height :185px}}
     :variants [:size :weight]
     :defaults {:size     :medium
                :weight   :wee-bold
                :examples "Simple"}
     :examples [{:label   "Simple"
                 :example [:section
                           [label (sx :.bold :mbe--0.75em) "Choose an option:"]
                           [radio {:-input-attrs {:name :demo}} "Yes"]
                           [radio {:-input-attrs {:name :demo}} "No"]
                           [radio {:-input-attrs {:name :demo}} "Maybe"]]}
                {:label   "Inherited color"
                 :example [:section (sx :c--$purple-500 :dark:c--$purple-300)
                           [label (sx :.bold :mbe--0.75em) "Choose an option:"]
                           [radio {:-input-attrs {:name :demo}} "Yes"]
                           [radio {:-input-attrs {:name :demo}} "No"]
                           [radio {:-input-attrs {:name :demo}} "Maybe"]]}

                {:label   "Custom, with default checked"
                 :example [:span
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
                           [radio {:-input-attrs {:name           :demo
                                                  :defaultChecked true}} [label [:span.emoji "🦑"] "Squid"]]
                           [radio {:-input-attrs {:name :demo}} [label [:span.emoji "🐋"] "Whale"]]
                           [radio {:-input-attrs {:name :demo}} [label [:span.emoji "🦈 "] "Shark"]]
                           [radio {:-input-attrs {:name :demo}} [label [:span.emoji "🐊"] "Croc"]]]}]})

   (feature
    checkbox
    {:stage    {:style {:min-height :135px}}
     :variants [#_:kind #_:shape :size :weight]
     :defaults {:kind     :secondary
                :size     :medium
                :shape    :sharp
                :weight   :wee-bold
                :examples "Simple"}
     :examples  [{:label   "Simple"
                  :example [checkbox "Sign me up"]}
                 {:label   "With trailing icon"
                  :example [checkbox [label "Make it shiny" [icon :auto-awesome]]]}]})

   (feature
    switch
    {:stage    {:style {:min-height :135px}}
     :variants [:size-expanded :semantic]
     :defaults {:examples      "Simple"
                :size-expanded :xxlarge
                :semantic      :neutral}
     :examples [{:label   "Simple"
                 :example [switch]}
                {:label   "On by default"
                 :example [switch (sx {:-on? true})]}

                {:label   "With styled thumb"
                 :example [switch (sx {:-thumb-attrs (sx :.convex)})]}

                {:label   "With oversized thumb"
                 :example [switch (sx :$switch-border-width--0px
                                      :$switch-thumb-scale-factor--1.25
                                      {:-thumb-attrs (sx :outline--1px:solid:currentColor
                                                         :outline-offset---1px)})]}
                {:label   "With labeled track"
                 :example [switch (sx :$switch-width-ratio--2.25
                                      {:-track-content-on  "ON"
                                       :-track-content-off "OFF"})]}
                {:label   "With icon track"
                 :example [switch (sx {:-track-content-on  [icon (sx :fs--0.55em
                                                                     {:-icon-filled? true})
                                                            :visibility]
                                       :-track-content-off [icon (sx :fs--0.55em
                                                                     {:-icon-filled? true})
                                                            :visibility-off]
                                       })]}
                {:label   "With labeled thumb"
                 :example [switch (sx {:-thumb-content-on  [:span (sx :.semi-bold :fs--0.325em) "ON"]
                                       :-thumb-content-off [:span (sx :.semi-bold :fs--0.325em) "OFF"]})]}

                {:label   "With icon thumb"
                 :example [switch (sx {:-thumb-content-on  [icon (sx :fs--0.55em
                                                                     {:-icon-filled? true})
                                                            :visibility]
                                       :-thumb-content-off [icon (sx :fs--0.55em
                                                                     {:-icon-filled? true})
                                                            :visibility-off]
                                       })]}
                {:label   "With outline styling"
                 :example [switch (sx :$switch-border-color--$gray-400
                                      :$switch-border-width--1.5px
                                      :$switch-off-background-color--white
                                      ["bgc" :transparent!important]
                                      ["hover:bgc" :transparent!important]
                                      ["[aria-checked='true']:bc" :currentColor]
                                      ["[aria-checked='true']:hover:bc" :currentColor]
                                      {:-thumb-attrs (sx :.elevated-0!
                                                         [:b "calc(var(--switch-border-width) * (1 / var(--switch-thumb-scale-factor))) solid transparent"]
                                                         :bgc--$switch-border-color
                                                         ["has-ancestor(.kushi-switch[aria-checked='true']):bgc" :currentColor]
                                                         )})]}]})

   (feature
    text-field
    {:stage    {:style {:min-height :170px}}
     :variants [:size :weight]
     :defaults {:size     :medium
                :weight   :normal
                :examples "Simple"}
     :examples [{:label   "Simple"
                 :example [text-field (sx {:placeholder "Your text here"
                                      :-label      "Input label"
                                      :-helper     "My helper text"})]}

                {:label   "Required"
                 :example [text-field (sx {:placeholder "Your text here"
                                      :required    true
                                      :-label      "Input label"})]}
                {:label   "Disabled"
                 :example [text-field (sx {:placeholder "Your text here"
                                      :disabled    true
                                      :-label      "Input label"})]}
                {:label   "With helper"
                 :example [text-field (sx {:placeholder "Your text here"
                                      :-label      "Input label"
                                      :-helper     "Your helper text here"})]}
                {:label   "With start enhancer"
                 :example [text-field (sx {:placeholder     "Monetary value"
                                      :-start-enhancer "$"
                                      :-label          "Input label"})]}
                {:label   "With end enhancer"
                 :example [text-field (sx {:placeholder   "Your text here"
                                      :-end-enhancer [icon :star]
                                      :-label        "Input label"})]}
                {:label   "Inline label"
                 :example [text-field (sx {:placeholder      "Your text here"
                                      :-label           "Input label"
                                      :-label-placement :inline})]}
                {:label   "Inline label with helper"
                 :example [text-field (sx {:placeholder      "Your text here"
                                      :-label           "Input label"
                                      :-label-placement :inline
                                      :-helper          "Your helper text here"})]}
                {:label   "With semantic class"
                 :example [text-field (sx {:placeholder "Your text here"
                                      :-label      "Input label"
                                      :-helper     "Your helper text here"
                                      :-semantic   :negative})]}
                {:label   "All options"
                 :example [text-field (sx {:placeholder          "Your text here"
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
                                                                {:class :my-input-wrapper-name})})]}
                {:label   "With textarea element"
                 :example [text-field (sx {:placeholder "Your text here"
                                      :-textarea?  true
                                      :-label      "Input label"
                                      :-helper     "My helper text"})]}]})

   (feature
    slider
    {:stage    {:style {:min-height :135px}}
     :defaults {:examples "Simple"}
     :examples  [{:label   "Simple"
                  :example [slider {:min 0
                                    :max 7}]}
                 {:label   "Lables"
                  :example [slider {:min          0
                                    :max          7
                                    :-step-marker :label}]}

                 {:label   "Dot markers"
                  :example [slider {:min          0
                                    :max          7
                                    :-step-marker :dot}]}
                 {:label   "Bar markers"
                  :example [slider {:min          0
                                    :max          7
                                    :-step-marker :bar}]}
                 {:label   "Fractional step"
                  :example [slider {:min  0
                                    :max  1
                                    :step 0.01}]}
                 {:label   "Supplied step values"
                  :example [slider {:-steps            ["xsmall" "medium" "large" "xlarge"]
                                    :-step-marker      :label
                                    :-label-size-class :medium}]}

                 {:label   "Supplied step values, custom horizontal shift for first and last step labels"
                  :example [slider {:-steps            ["First label is long" "Second label" "Third label" "Last label is long"]
                                    :-step-marker      :dot
                                    :-label-size-class :small
                                    :-labels-attrs     (sx [:&_.kushi-slider-step-label:first-child>span:translate :-25%:-50%]
                                                           [:&_.kushi-slider-step-label:last-child>span:translate :-75%:-50%])}]}

                 #_{:label   "Supplied step values, custom label styling"
                    :example [slider {:-steps              ["low" "guarded" "elevated" "high" "severe"]
                                      :-step-marker        :label
                                      :-label-size-class   :small
                                      :-label-scale-factor 0.8
                                      :-labels-attrs       (sx :>span>span:border-radius--9999px
                                                               :>span>span:padding--0.125em:0.75em:0.2em
                                                               {:style {">span:nth-child(1):c"         :$positive
                                                                        ">span:nth-child(1):>span:bgc" :$positive-50
                                                                        ">span:nth-child(2):c"         :$accent
                                                                        ">span:nth-child(2):>span:bgc" :$accent-50
                                                                        ">span:nth-child(3):c"         :$warning
                                                                        ">span:nth-child(3):>span:bgc" :$warning-50
                                                                        ">span:nth-child(4):c"         :$orange-700
                                                                        ">span:nth-child(4):>span:bgc" :$orange-50
                                                                        ">span:nth-child(5):c"         :$negative
                                                                        ">span:nth-child(5):>span:bgc" :$negative-50}})}]}]})

   (feature
    tooltip-attrs
    {:fn       tooltip-attrs
     :meta     #'tooltip-attrs
     :title    "Tooltip"
     :demo     tooltip-demo/demo
     :demo-attrs (sx :d--none :md:d--block)
     :stage    {:style {:min-height      :200px
                        :justify-content :center}}
     :defaults {:examples "Auto"}
     :examples  [
                 {:label   "Auto"
                  :example [button
                            (merge-attrs
                             ;; If you need to show the auto-top placement thing
                             #_(sx :$tooltip-auto-placement-y-threshold--0.1)
                             (tooltip-attrs {:-text "My tooltip text"}))
                            "Hover me to reveal tooltip"]}


                 {:label   "top-left-corner"
                  :example [button
                            (tooltip-attrs {:-text      "My tooltip text"
                                            :-placement :top-left-corner})
                            "Hover me to reveal tooltip"]}

                 {:label   "top-left"
                  :example [button
                            (tooltip-attrs {:-text      "My tooltip text"
                                            :-placement :top-left})
                            "Hover me to reveal tooltip"]}

                 {:label   "top"
                  :example [button
                            (tooltip-attrs {:-text      "My tooltip text"
                                            :-placement :top})
                            "Hover me to reveal tooltip"]}

                 {:label   "top-right"
                  :example [button
                            (tooltip-attrs {:-text      "My tooltip text"
                                            :-placement :top-right})
                            "Hover me to reveal tooltip"]}

                 {:label   "top-right-corner"
                  :example [button
                            (tooltip-attrs {:-text      "My tooltip text"
                                            :-placement :top-right-corner})
                            "Hover me to reveal tooltip"]}

                 {:label   "right-top-corner"
                  :example [button
                            (tooltip-attrs {:-text      "My tooltip text"
                                            :-placement :right-top-corner})
                            "Hover me to reveal tooltip"]}

                 {:label   "right"
                  :example [button
                            (tooltip-attrs {:-text      "My tooltip text"
                                            :-placement :right})
                            "Hover me to reveal tooltip"]}

                 {:label   "inline-end, center"
                  :example [button
                            (tooltip-attrs {:-text      "My tooltip text"
                                            :-placement [:inline-end :center]})
                            "Hover me to reveal tooltip"]}

                 {:label   "block-start, inline-start"
                  :example [button
                            (tooltip-attrs {:-text      "My tooltip text"
                                            :-placement [:block-start :inline-start]})
                            "Hover me to reveal tooltip"]}

                 {:label   "block-start, center"
                  :example [button
                            (tooltip-attrs {:-text      "My tooltip text"
                                            :-placement [:block-start :center]})
                            "Hover me to reveal tooltip"]}

                 {:label   "block-start, inline-end"
                  :example [button
                            (tooltip-attrs {:-text      "My tooltip text"
                                            :-placement [:block-start :inline-end]})
                            "Hover me to reveal tooltip"]}

                 {:label   "corner"
                  :example [button
                            (tooltip-attrs {:-text      "My tooltip text"
                                            :-placement [:block-start :inline-end :corner]})
                            "Hover me to reveal tooltip"]}

                 {:label   "With forced linebreaks"
                  :example [button
                            (tooltip-attrs {:-text      ["My tooltip text line1" "My tooltip text line2"]
                                            :-placement :right #_"inline-end"})
                            "Hover me to reveal tooltip"]}


                 {:label   "Change text on click"
                  :example [:div.flex-row-fs
                            [button
                             (tooltip-attrs {:-text          "My tooltip text!"
                                             :-text-on-click "Clicked!"
                                             :-placement     :right})
                             "Hover me to reveal tooltip"]]}

                ;; When you get sx-class working
                ;;  {:label   "Change text and color on click"
                ;;   :example [:div.flex-row-fs
                ;;             [button
                ;;              (merge-attrs 
                ;;               (tooltip-attrs {:-text                        "My tooltip text!"
                ;;                               :-text-on-click               "Success!"
                ;;                               :-text-on-click-tooltip-class (sx-class :bgc--$positive-bg :c--white) 
                ;;                               :-placement                   :right}))
                ;;              "Hover me to reveal tooltip"]]}

                 {:label   "With custom styled span"
                  :example [:span
                            (merge-attrs
                             (sx :.relative
                                 :.pointer
                                 :.pill
                                 :tt--u
                                 :td--u
                                 :tuo--8px
                                 :tds--dashed
                                 :tdc--$green-400
                                 :tdt--4px
                                 :pi--1em
                                 :pb--0.25em)
                             (tooltip-attrs {:-text      "My tooltip text"
                                             :-placement :top}))
                            "Hover me to reveal tooltip"]}]})

  (feature
    popover-attrs
    {:fn       popover-attrs
     :meta     #'popover-attrs
     :title    "Popover"
     :stage    {:style {:min-height      :200px
                        :justify-content :center}}
     :defaults {:examples "Auto"}
     :refers   '[dismiss-popover!]
     :requires (str
                "            ;; Optional, for label component shown in examples\n"
                "            [kushi.ui.icon.core :refer [icon]]\n"
                "            ;; Optional, for button component shown in examples\n"
                "            [kushi.ui.button.core :refer [button]]\n"
                "            ;; Optional, only if you are using reagent as in\n"
                "            ;; examples above. Otherwise you can bring your\n"
                "            ;; own rendering function.\n"
                "            [reagent.dom :as rdom]"
                
                )
     :examples [{:label   "Auto"
                 :example [button
                           (popover-attrs {:-f (fn [popover-el]
                                                 (rdom/render popover-content
                                                              popover-el))})
                           "Click me"]}
                {:label   "Arrowless"
                 :example [button
                           (popover-attrs {:-f (fn [popover-el]
                                                 (rdom/render popover-content
                                                              popover-el))
                                           :-arrow? false})
                           "Click me"]}
                
                {:label   "With dismiss action"
                 :example [button
                           (popover-attrs
                            {:-f (fn [el]
                                   (rdom/render [:div (sx :.flex-col-c
                                                          :ai--c
                                                          :min-height--100%
                                                          :p--1rem)
                                                 [button 
                                                  (sx :.small
                                                      {:on-click dismiss-popover!})
                                                  "Close"]]
                                                el))})
                           "Click me"]}

                {:label   "Auto-dismiss"
                 :example [button
                           (popover-attrs
                            {:-f             (fn [el]
                                               (rdom/render [:div (sx :.flex-col-c
                                                                      :ai--c
                                                                      :min-height--100%
                                                                      :p--1rem)
                                                             [label 
                                                              (sx :.small)
                                                              "I will close automatically after 5000ms"]]
                                                            el))
                             :-auto-dismiss? true})
                           "Click me"]}]})
   
   (feature
    toast-attrs
    {:fn       toast-attrs
     :meta     #'toast-attrs
     :title    "Toast"
     :stage    {:style {:min-height      :200px
                        :justify-content :center}}
     :defaults {:examples "Auto"}
     :refers   '[dismiss-toast!]
     :requires (str
                "            ;; Optional, for label component shown in examples\n"
                "            [kushi.ui.icon.core :refer [icon]]\n"
                "            ;; Optional, for button component shown in examples\n"
                "            [kushi.ui.button.core :refer [button]]\n"
                "            ;; Optional, only if you are using reagent as in\n"
                "            ;; examples above. Otherwise you can bring your\n"
                "            ;; own rendering function.\n"
                "            [reagent.dom :as rdom]")
     :examples [{:label   "Auto"
                 :example [button
                           (toast-attrs
                            {:-auto-dismiss? false
                             :-placement     :nonsense
                             :-f             (fn [toast-el]
                                               (rdom/render toast-content
                                                            toast-el))})
                           "Save for later"]}]})


   ;; Defined in kushi.ui.icon.mui.examples
   icon-examples


   {:fn       tag
    :meta     #'tag
    :stage    {:style {:min-height :135px}}
    :desc     ["A tag is a kind of label, often displayed together with other tags for categorization and context."]
    :variants [:kind :shape :semantic :size :weight]
    :defaults {:kind     :default
               :shape    :rounded
               :semantic :neutral
               :size     :xsmall
               :weight   :wee-bold
               :examples "\"my tag\""}
    :examples  [{:label   "\"my tag\""
                 :example (example2 [tag "my tag"])}
                {:label   "\"xyz\""
                 :example (example2 [tag "xyz"])}
                {:label   "\"XYZ\""
                 :example (example2 [tag "XYZ"])}
                {:label   "With icon"
                 :example (example2 [tag [icon :pets] "pet friendly"])}
                {:label   "Max-width example"
                 :example (example2 [tag [:span (sx :.truncate :max-width--130px) "My tag with longer text"]])}]}

   (feature
    label
    {:stage    {:style {:min-height :135px}}
     :variants [:size :weight]
     :defaults {:size     :medium
                :weight   :wee-bold
                :examples "Simple"}
     :examples [{:label   "Simple"
                 :example [label "my label"]}
                {:label   "Leading icon"
                 :example [label [icon :pets] "Pet Friendly"]}
                {:label   "Trailing icon"
                 :example [label "Pet friendly" [icon :pets]]}]})

   (feature
    card
    {:stage    {:style {:min-height :280px}}
     :defaults {:examples "Default, elevated"}
     :examples [{:label   "Default, elevated"
                 :example [:div [card (sx :.elevated-3) "my content"]]}
                {:label   "Elevation levels 1-5"
                 :example [:div.grid (sx :ai--c
                                         :gap--2rem
                                         :gtc--1fr:1fr:1fr
                                         :&_code:ws--n
                                         :&_code:fs--$xsmall
                                         :&_.kushi-card:pi--1.5rem)
                           [card (sx :.elevated-1) [:code ":.elevation-1"]]
                           [card (sx :.elevated-2) [:code ":.elevation-2"]]
                           [card (sx :.elevated-3) [:code ":.elevation-3"]]
                           [card (sx :.elevated-4) [:code ":.elevation-4"]]
                           [card (sx :.elevated-5) [:code ":.elevation-5"]]]}
                {:label   "sharp, bordered"
                 :example [card (sx :.sharp :.bordered) "my content"]}
                {:label   "rounded, bordered"
                 :example [card (sx :.rounded :.bordered) "my content"]}
                {:label   "Alien"
                 :example [card (sx :.large
                                    :.extra-bold
                                    :.flex-col-c
                                    :.rounded-small
                                    :p--0
                                    :height--220px
                                    :tt--u
                                    :ta--center
                                    :bgc--#313131
                                    :c--white
                                    :b--1px:solid:#9eef00
                                    :text-shadow--1px:1px:5px:#9eef00b5
                                    :box-shadow--inset:0px:0px:40px:#9eef0073)
                           [:span (sx :pis--7ex :letter-spacing--7ex) "alien"]]}]})


   (feature
    callout
    {:stage    {:style {:min-height :220px}}
     :variants [:shape :size :weight]
     :defaults {:shape    :sharp
                :size     :medium
                :weight   :wee-bold
                :examples "Default"}
     :examples [{:label   "Default"
                 :example [callout
                           (sx :.neutral
                               {:-header-text "callout header text goes here." })]}

                {:label   "With close button"
                 :example [callout
                           (sx :.neutral
                               {:-header-text  "callout header text goes here."
                                :-close-button [button (sx :.pill
                                                           :p--0.25em
                                                           {:on-click #(js/alert "Example close-icon click event.")})
                                                [icon :clear]]})]}

                {:label   "With close button and icon"
                 :example [callout
                           (sx :.neutral
                               {:-icon         [icon :info]
                                :-header-text  "callout header text goes here."
                                :-close-button [button (sx :.pill
                                                           :p--0.25em
                                                           {:on-click #(js/alert "Example close-icon click event.")})
                                                [icon :clear]]})]}
                {:label   "Accent"
                 :example [callout
                           (sx :.accent
                               {:-icon         [icon :info]
                                :-header-text  "callout header text goes here."
                                :-close-button [button (sx :.accent
                                                           :.pill
                                                           :p--0.25em
                                                           {:on-click #(js/alert "Example close-icon click event.")})
                                                [icon :clear]]})]}
                {:label   "Accented, filled"
                 :example [callout
                           (sx :.accent
                               :.filled
                               {:-icon         [icon :info]
                                :-header-text  "callout header text goes here."
                                :-close-button [button (sx :.accent
                                                           :.filled
                                                           :.pill
                                                           :p--0.25em
                                                           {:on-click #(js/alert "Example close-icon click event.")})
                                                [icon :clear]]})]}
                {:label   "Positive, filled"
                 :example [callout
                           (sx :.positive
                               {:-icon         [icon :info]
                                :-header-text  "callout header text goes here."
                                :-close-button [button (sx :.positive
                                                           :.pill
                                                           :p--0.25em
                                                           {:on-click #(js/alert "Example close-icon click event.")})
                                                [icon :clear]]})]}
                {:label   "Negative, bordered"
                 :example [callout
                           (sx :.negative
                               :.bordered
                               :bw--2px
                               {:-icon         [icon (sx :.bold) :info]
                                :-header-text  "callout header text goes here."
                                :-close-button [button (sx :.negative
                                                           :.minimal
                                                           :.pill
                                                           :p--0.25em
                                                           {:on-click #(js/alert "Example close-icon click event.")})
                                                [icon (sx :.bold) :clear]]})]}
                {:label   "Warning, with body"
                 :example [callout
                           (sx :.warning
                               {:-icon         [icon :warning]
                                :-header-text  "callout header text goes here."
                                :-close-button [button (sx :.warning
                                                           :.pill
                                                           :p--0.25em
                                                           {:on-click #(js/alert "Example close-icon click event.")})
                                                [icon :clear]]})
                           [:div (sx :.flex-col-c :ai--c)
                            "callout body"
                            [label (sx :.xxxlarge) "👻"]]]}]})

   ;; We are passing in the examples as a var, so we are not using the `kushi.playground.util/feature` macro here
   {:fn       modal
    :meta     #'modal
    :stage    {:style {:min-height :150px :justify-content :center}}
    :refers   '[modal-close-button open-kushi-modal close-kushi-modal ]
    :defaults {:examples "With title and description"}
    :examples  modal-examples}

   (feature
    collapse
    {:stage    {:style {:min-height  :190px
                        :align-items :start}}
     :defaults {:examples "Default"}
     :examples [{:label   "Default"
                 :example [collapse
                           {:-label "collapsable section label"}
                           [:p "child 1"]
                           [:p "child 2"]]}
                {:label   "Dynamic label"
                 :example [collapse
                           {:-label          "Click to expand"
                            :-label-expanded "Click to collapse"}
                           [:p "child 1"]
                           [:p "child 2"]]}
                {:label   "Icon on right"
                 :example [collapse
                           {:-label         "Collapsable section label "
                            :-icon-position :end}
                           [:p "child 1"]
                           [:p "child 2"]]}
                {:label   "Borders"
                 :example [collapse
                           (sx
                            :bbe--1px:solid:black
                            {:-label        "Collapsable section label "
                             :-header-attrs (sx :bbs--1px:solid:black)})
                           [:p "child 1"]
                           [:p "child 2"]]}
                {:label   "Label weight"
                 :example [collapse
                           {:-label        "Collapsable section label "
                            :-header-attrs (sx :.bold)}
                           [:p "child 1"]
                           [:p "child 2"]]}
                {:label   "Body color"
                 :example [collapse
                           {:-label      "Collapsable section label "
                            :-body-attrs (sx :bgc--$accent-100 :pis--1rem)
                            :-speed      1000}
                           [:section
                            (sx :pb--0.5rem)
                            [:p "child 1"]
                            [:p "child 2"]
                            [:p "child 3"]
                            [:p "child 4"]
                            [:p "child 5"]]]}
                {:label   "Header color"
                 :example [collapse
                           (sx :border-block--3px:solid:$neutral-1000
                               {:-label        "Collapsable section label "
                                :-body-attrs   (sx :pis--0.5rem)
                                :-header-attrs (sx :.semi-bold
                                                   :p--10px
                                                   :bgc--$neutral-1000
                                                   :dark:bgc--$neutral-200
                                                   :c--white
                                                   :dark:c--black)})
                           [:p "child 1"]
                           [:p "child 2"]]}

               ;; TODO fix expanded? functionality
                #_{:label   "Expanded"
                   :example [collapse
                             {:-expanded?  true
                              :-label      "Collapsable section label "
                              :-body-attrs (sx :bgc--#ffe3ac :pl--1rem)}
                             [:p "child 1"]
                             [:p "child 2"]]}

                {:label   "With click handler"
                 :example [collapse
                           {:-label    "Collapsable section label"
                            :-on-click #(js/alert "clicked")}
                           [:p "child 1"]
                           [:p "child 2"]]}]})

   (feature
    accordion
    {:stage    {:style {:min-height      :190px
                        :justify-content :flex-start
                        :align-items     :start}}
     :desc     ["cool"]
     :defaults {:examples "Simple"}
     :examples [{:label   "Simple"
                 :example [accordion
                           [collapse
                            {:-label "first section"
                             :-speed 1000}
                            [:div (sx :pis--0.5rem) [:p "child 1"] [:p "child 2"]]]
                           [collapse
                            {:-label "second section"
                             :-speed 1000}
                            [:div (sx :pis--0.5rem) [:p "child 1"] [:p "child 2"]]]]}
                {:label   "borders"
                 :example [accordion
                           (sx :>section:first-child:bbs--1px:solid:black)
                           (for [[label-text content] [["first section" "lorem ipsum"]
                                                       ["second section" "lorem ipsum2"]
                                                       ["third section" "lorem ipsum3"]]]
                             ^{:key label-text}
                             [collapse
                              (sx
                               :bbe--1px:solid:black
                               {:-label "collapsable section label" })
                              [:p content]])]}]})


   (feature
    grid
    {:stage    {:style {:min-height :135px
                        :padding    :20px}}
     :defaults {:examples "Simple"}
     :examples  [{:label   "Simple"
                  :example [grid
                            (sx [:>div:bgc '(rgba 128 128 128 0.2)])
                            (for [x (range 6)]
                              [:div
                               [:div (sx :.absolute-fill :.flex-col-c :ai--c) (inc x)]])]}
                 {:label   "With sizing options"
                  :example [grid
                            (sx [:>div:bgc '(rgba 128 128 128 0.2)]
                                {:-column-min-width :80px
                                 :-gap              :15px
                                 :-aspect-ratio     :2:3})
                            (for [x (range 15)]
                              [:div
                               [:div (sx :.absolute-fill :.flex-col-c :ai--c) (inc x)]])]}]})])
