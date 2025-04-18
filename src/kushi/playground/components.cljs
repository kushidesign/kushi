(ns ^:dev/always kushi.playground.components
 (:require
  [kushi.core :refer [sx]]
  [kushi.ui.avatar.core :refer [avatar]]
  [kushi.ui.button.core :refer [button]]
  [kushi.ui.button.demo :as button.demo]
  [kushi.ui.callout.core :refer [callout]]
  [kushi.ui.callout.demo :as callout.demo]
  [kushi.ui.card.core :refer [card]]
  [kushi.ui.card.demo :as card.demo]
  [kushi.ui.avatar.core :refer [avatar]]
  [kushi.ui.avatar.demo :as avatar.demo]
  [kushi.ui.checkbox.core :refer [checkbox]]
  [kushi.ui.checkbox.demo :as checkbox.demo]
  [kushi.ui.collapse.core :refer [collapse]]
  [kushi.ui.collapse.demo :as collapse.demo]
  [kushi.ui.grid.core :refer [grid]]
  [kushi.ui.grid.demo :as grid.demo]
  [kushi.ui.icon.core :refer [icon]]
  [kushi.ui.icon.demo :as icon.demo]
  [kushi.ui.modal.core :refer [modal]]
  [kushi.ui.modal.demo :as modal.demo]
  [kushi.ui.popover.core :refer [popover-attrs]]
  [kushi.ui.popover.demo :as popover.demo]
  [kushi.ui.radio.core :refer [radio]]
  [kushi.ui.radio.demo :as radio.demo]
  [kushi.ui.slider.core :refer [slider]]
  [kushi.ui.slider.demo :as slider.demo]
  [kushi.ui.spinner.core :refer [spinner]]
  [kushi.ui.spinner.demo :as spinner.demo]
  [kushi.ui.switch.core :refer [switch]]
  [kushi.ui.switch.demo :as switch.demo]
  [kushi.ui.tag.core :refer [tag]]
  [kushi.ui.tag.demo :as tag.demo]
  [kushi.ui.text-field.core :refer [text-field]]
  [kushi.ui.text-field.demo :as text-field.demo]
  [kushi.ui.toast.core :refer [toast-attrs]]
  [kushi.ui.toast.demo :as toast.demo]
  [kushi.ui.tooltip.core :refer [tooltip-attrs]]
  [kushi.ui.tooltip.demo :as tooltip.demo]))



(def playground-components 
  (filter 
  ;;  :label
   #(contains? #{
                 "button"     
                ;;  "icon"       
                ;;  "spinner"    
                ;;  "switch"     
                ;;  "radio"      
                ;;  "checkbox"   
                ;;  "slider"     
                ;;  "text field" 
                ;;  "tooltip"    
                ;;  "popover"    ;; PS
                ;;  "modal"      ;; PS
                ;;  "toast"      ;; PS
                ;;  "avatar"
                ;;  "card"       ;; PS
                ;;  "callout"    ;; PS
                ;;  "collapse"   ;; PS
                ;;  "accordian"  ;; PS
                ;;  "tag"        ;; PS
                ;;  "grid"       ;; PS
                 }
               (:label %))

   ;; You need to wrap this whole collection in features macro
   [
    {:label          "button"
     :component      button
     :examples       button.demo/examples
     :component-meta (-> button var meta)
     :reqs           '[[kushi.ui.button.core :refer [button]]]
     :variants-base  #{:soft :solid :outline :minimal}
     :variants-order [:soft :solid :outline :minimal]
     :variants-attrs {:soft    {}
                      :solid   {:-surface :solid}
                      :outline {:-surface :outline}
                      :minimal {:-surface :minimal}}}

      {:label          "icon"
       :component      icon
       :examples       icon.demo/examples
       :component-meta (-> icon var meta)
       :reqs           '[[kushi.ui.icon.core :refer [icon]]]
       :variants-base  #{:outline :solid}
       :variants-order [:outline :solid]
       :variants-attrs {:solid   {:-icon-solid? true}
                        :outline {}}}

      {:label          "spinner"
       :component      spinner
       :examples       spinner.demo/examples
       :component-meta (-> spinner var meta)
       :reqs           '[[kushi.ui.spinner.core :refer [spinner
                                                        donut
                                                        propeller
                                                        thinking]]]}

      {:label          "switch" 
       :component      switch
       :examples       switch.demo/examples
       :component-meta (-> switch var meta)
       :reqs           '[[kushi.ui.switch.core :refer [switch]]]
       :variants-base  #{:on :off}
       :variants-order [:off :on]
       :variants-attrs {:on  {:-on? true}
                        :off {}}}

      {:label          "radio" 
       :component      radio
       :examples       radio.demo/examples
       :component-meta (-> radio var meta)
       :reqs           '[[kushi.ui.radio.core :refer [radio]]]
       :variants-base  #{:positions}
       :variants-attrs {:positions {}}
       }
      
      {:label          "checkbox" 
       :component      checkbox
       :examples       checkbox.demo/examples
       :component-meta (-> checkbox var meta)
       :reqs           '[[kushi.ui.radio.core :refer [radio]]]
       :variants-base  #{:positions}
       :variants-attrs {:positions {}}}

      {:label          "slider"
       :component      slider
       :examples       slider.demo/examples
       :component-meta (-> slider var meta)
       :reqs           '[[kushi.ui.slider.core :refer [slider]]]
       :variants-base  #{:on}
       :variants-order [:on]
       :variants-attrs {:on {}}}

      {:label          "text field" 
       :component      text-field
       :examples       text-field.demo/examples
       :component-meta (-> text-field var meta)
       :reqs           '[[kushi.ui.text-field.core :refer [text-field]]]
       :variants-base  #{:positions}
       :variants-attrs {:positions {}}}


      {:label          "tooltip" 
       :examples       tooltip.demo/examples
       :component-meta (-> tooltip-attrs var meta)
       :media-matches  {:matches {"any-hover" "hover"
                                  "hover"     "hover"}
                        :message [:span
                                  "The Tooltip component is intended only for devices that support the css "
                                  [:code ":hover"]
                                  " pseudo-class. "
                                  [:br]
                                  [:br]
                                  "To view Tooltip demos, please check this page out on a device that supports this feature."] }
       :component      :span
       :reqs           '[[kushi.ui.tooltip.core :refer [tooltip-attrs]] ]
       :variants-base  #{:positions}
       :variants-attrs {:positions {}}}

      {:label          "popover" 
       :examples       popover.demo/examples
       :component-meta (-> popover-attrs var meta)
       :component      :span
       :reqs           '[[kushi.ui.popover.core :refer [popover-attrs dismiss-popover!]]]
       :variants-base  #{:positions}
       :variants-attrs {:positions {}}}

      {:label          "modal" 
       :examples       modal.demo/examples
       :component-meta (-> modal var meta)
       :component      :span
       :reqs           '[[kushi.ui.modal.core :refer [modal
                                                      modal-close-button
                                                      open-kushi-modal
                                                      close-kushi-modal]] ]
       :variants-base  #{:positions}
       :variants-attrs {:positions {}}}

      {:label          "toast" 
       :examples       toast.demo/examples
       :component-meta (-> toast-attrs var meta)
       :component      :span
       :reqs           '[[kushi.ui.toast.core :refer [toast-attrs dismiss-toast!]]]
       :variants-base  #{:positions}
       :variants-attrs {:positions {}}}


      {:label          "card"
       :examples       card.demo/examples
       :component-meta (-> card var meta)
       :component      card
       :reqs           '[[kushi.ui.card.core :refer [card]]]
       :variants-base  #{:soft}
       :variants-order [:soft]
       :variants-attrs {:rounded (sx :.rounded)}
       }

      {:label          "avatar"
       :examples       avatar.demo/examples
       :component-meta (-> avatar var meta)
       :component      avatar
       :reqs           '[[kushi.ui.avatar.core :refer [avatar]]]
       :variants-base  #{:soft :solid :outline :minimal}
       :variants-order [:soft :solid :outline :minimal]
       :variants-attrs {:soft    {}
                        :solid   {:-surface :solid}
                        :outline {:-surface :outline}
                        :minimal {:-surface :minimal}}
       }

      {:label          "tag"
       :examples       tag.demo/examples
       :component-meta (-> tag var meta)
       :component      tag
       :reqs           '[[kushi.ui.tag.core :refer [tag]]]
       :variants-base  #{:soft :solid :outline :minimal}
       :variants-order [:soft :solid :outline :minimal]
       :variants-attrs {:soft    {}
                        :solid   {:-surface :solid}
                        :outline {:-surface :outline}
                        :minimal {:-surface :minimal}}
       }

      {:label          "callout"
       :examples       callout.demo/examples
       :component-meta (-> callout var meta)
       :component      callout
       :reqs           '[[kushi.ui.callout.core :refer [callout]]]
       :variants-base  #{:soft :solid :outline}
      ;;  :variants-order [:soft :solid :outline]
      ;;  :variants-attrs {:soft    {}
      ;;                   :solid   {:-surface :solid}
      ;;                   :outline {:-surface :outline}
      ;;                   :minimal {:-surface :minimal}}
       }

      {:label          "collapse"
       :examples       collapse.demo/examples
       :component-meta (-> collapse var meta)
       :component      collapse
       :reqs           '[[kushi.ui.collapse.core :refer [collapse]]]}

    ;; TODO - make namespace for this? - move demo out of collapse
    ;; {:label          "accordian"
    ;;  :examples       accordian.demo/examples
    ;;  :component-meta (-> accordian var meta)
    ;;  :component      accordion
    ;;  :reqs           '[[kushi.ui.collapse.core :refer [accordion]]]}
      
      {:label          "grid"
       :examples       grid.demo/examples
       :component-meta (-> grid var meta)
       :component      grid
       :reqs           '[[kushi.ui.grid.core :refer [grid]]]}

      ]))
