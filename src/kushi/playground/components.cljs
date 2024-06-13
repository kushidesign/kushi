(ns ^:dev/always kushi.playground.components
 (:require 
  [kushi.core :refer [sx]]
  [kushi.ui.callout.core :refer [callout]]
  [kushi.ui.callout.demo :as callout.demo]
  [kushi.ui.button.core :refer [button]]
  [kushi.ui.button.demo :as button.demo]
  [kushi.ui.card.core :refer [card]]
  [kushi.ui.card.demo :as card.demo]
  [kushi.ui.collapse.core :refer [accordion collapse]]
  [kushi.ui.collapse.demo :as collapse.demo]
  [kushi.ui.grid.core :refer [grid]]
  [kushi.ui.grid.demo :as grid.demo]
  [kushi.ui.icon.core :refer [icon]]
  [kushi.ui.icon.demo :as icon.demo]
  [kushi.ui.input.checkbox.core :refer [checkbox]]
  [kushi.ui.input.checkbox.demo :as checkbox.demo]
  [kushi.ui.input.radio.core :refer [radio]]
  [kushi.ui.input.radio.demo :as radio.demo]
  [kushi.ui.input.slider.core :refer [slider]]
  [kushi.ui.input.slider.demo :as slider.demo]
  [kushi.ui.input.switch.core :refer [switch]]
  [kushi.ui.input.switch.demo :as switch.demo]
  [kushi.ui.input.text.core :refer [text-field]]
  [kushi.ui.input.text.demo :as input.demo]
  [kushi.ui.modal.demo :as modal.demo]
  [kushi.ui.popover.demo :as popover.demo]
  [kushi.ui.spinner.core :refer [spinner]]
  [kushi.ui.spinner.demo :as spinner.demo]
  [kushi.ui.tag.core :refer [tag]]
  [kushi.ui.tag.demo :as tag.demo]
  [kushi.ui.toast.demo :as toast.demo]
  [kushi.ui.tooltip.demo :as tooltip-demo]))

(def playground-components 
  (filter 
  ;;  :label
   #(contains? #{
                  "slider"
                  ;; "button"
                  ;;  "callout"
                  ;;  "spinner"
                  ;;  "grid"
                  ;;  "accordian"
                  ;;  "radio"
                  ;;  "text field"
                  ;;  "modal"
                  ;;  "popover"
                  ;;  "collapse"
                  ;;  "checkbox"
                 }
                 (:label %))

   [{:label          "button"
     :demo-component button.demo/demo2
     :component      button
     :reqs           '[[kushi.ui.button.core :refer [button]]]
     :variants-base  #{:rounded :filled :bordered :minimal}
     :variants-order [:rounded :filled :bordered :minimal]
     :variants-attrs {:rounded  (sx :.rounded)
                      :filled   (sx :.rounded :.filled)
                      :bordered (sx :.rounded :.bordered)
                      :minimal  (sx :.rounded :.minimal)}}

    {:label          "spinner"
     :demo-component spinner.demo/demo
     :component      spinner
     :reqs           '[[kushi.ui.spinner.core :refer [spinner
                                                      donut
                                                      propeller
                                                      thinking]]]}
    {:label          "switch" 
     :demo-component switch.demo/demo2
     :component      switch
     :reqs           '[[kushi.ui.input.switch.core :refer [switch]]]
     :variants-base  #{:on :off}
     :variants-order [:off :on]
     :variants-attrs {:on  {:-on? true}
                      :off {}}}

    {:label          "tooltip" 
     :demo-component tooltip-demo/demo2
     :component      :span
     :reqs           '[[kushi.ui.tooltip.core :refer [tooltip-attrs]] ]
     :variants-base  #{:positions}
     :variants-attrs {:positions {}}}

    {:label          "modal" 
     :demo-component modal.demo/demo
     :component      :span
     :reqs           '[[kushi.ui.modal.core :refer [modal
                                                    modal-close-button
                                                    open-kushi-modal
                                                    close-kushi-modal]] ]
     :variants-base  #{:positions}
     :variants-attrs {:positions {}}}
    
    {:label          "popover" 
     :demo-component popover.demo/demo
     :component      :span
     :reqs           '[[kushi.ui.popover.core :refer [popover-attrs dismiss-popover!]]]
     :variants-base  #{:positions}
     :variants-attrs {:positions {}}}

    {:label          "toast" 
     :demo-component toast.demo/demo
     :component      :span
     :reqs           '[[kushi.ui.toast.core :refer [toast-attrs dismiss-toast!]]]
     :variants-base  #{:positions}
     :variants-attrs {:positions {}}}

    {:label          "radio" 
     :demo-component radio.demo/demo
     :component      radio
     :reqs           '[[kushi.ui.input.radio.core :refer [radio]]]
     :variants-base  #{:positions}
     :variants-attrs {:positions {}}
     }

    {:label          "checkbox" 
     :demo-component checkbox.demo/demo
     :component      checkbox
     :reqs           '[[kushi.ui.input.radio.core :refer [radio]]]
     :variants-base  #{:positions}
     :variants-attrs {:positions {}}}

    {:label          "text field" 
     :demo-component input.demo/demo
     :component      text-field
     :reqs           '[[kushi.ui.input.text.core :refer [text-field]]]
     :variants-base  #{:positions}
     :variants-attrs {:positions {}}}

    {:label          "tag"
     :demo-component tag.demo/demo2
     :component      tag
     :reqs           '[[kushi.ui.tag.core :refer [tag]]]
     :variants-base  #{:rounded :filled :bordered}
     :variants-order [:rounded :filled :bordered :minimal]
     :variants-attrs {:rounded  (sx :.rounded)
                      :filled   (sx :.rounded :.filled)
                      :bordered (sx :.rounded :.bordered)
                      :minimal  (sx :.rounded :.minimal)}}

    {:label          "card"
     :demo-component card.demo/demo
     :component      card
     :reqs           '[[kushi.ui.card.core :refer [card]]]
     :variants-base  #{:rounded}
     :variants-order [:rounded]
     :variants-attrs {:rounded (sx :.rounded)}
     }

    {:label          "slider"
     :demo-component slider.demo/demo
     :component      slider
     :reqs           '[[kushi.ui.input.slider.core :refer [slider]]]
     :variants-base  #{:on}
     :variants-order [:on]
     :variants-attrs {:on {}}}

    {:label          "callout"
     :demo-component callout.demo/demo
     :component      callout
     :reqs           '[[kushi.ui.callout.core :refer [callout]]]
     :variants-base  #{:default :filled :bordered}
     :variants-order [:default :filled :bordered]
     :variants-attrs {:default  {}
                      :filled   (sx :.filled)
                      :bordered (sx :.bordered)}}

    {:label          "icon"
     :demo-component icon.demo/demo
     :component      icon
     :reqs           '[[kushi.ui.icon.core :refer [icon]]]
     :variants-base  #{:outlined :filled}
     :variants-order [:outlined :filled]
     :variants-attrs {:filled   {:-icon-filled? true}
                      :outlined {}}}

    {:label          "collapse"
     :demo-component collapse.demo/demo
     :component      collapse
     :reqs           '[[kushi.ui.collapse.core :refer [collapse]]]}

    {:label          "accordian"
     :demo-component collapse.demo/accordion-demo
     :component      accordion
     :reqs           '[[kushi.ui.collapse.core :refer [accordion]]]}

    {:label          "grid"
     :demo-component grid.demo/demo
     :component      grid
     :reqs           '[[kushi.ui.grid.core :refer [grid]]]}

    ]))
