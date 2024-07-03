(ns ^:dev/always kushi.playground.components
 (:require 
  [fireworks.core :refer [? !? ?- !?- ?-- !?-- ?> !?> ?i !?i ?l !?l ?log !?log ?log- !?log- ?pp !?pp ?pp- !?pp-]]
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
  [kushi.ui.checkbox.core :refer [checkbox]]
  [kushi.ui.checkbox.demo :as checkbox.demo]
  [kushi.ui.radio.core :refer [radio]]
  [kushi.ui.radio.demo :as radio.demo]
  [kushi.ui.slider.core :refer [slider]]
  [kushi.ui.slider.demo :as slider.demo]
  [kushi.ui.switch.core :refer [switch]]
  [kushi.ui.switch.demo :as switch.demo]
  [kushi.ui.text-field.core :refer [text-field]]
  [kushi.ui.text-field.demo :as text-field.demo]
  [kushi.ui.modal.core :refer [modal]]
  [kushi.ui.modal.demo :as modal.demo]
  [kushi.ui.popover.core :refer [popover-attrs]]
  [kushi.ui.popover.demo :as popover.demo]
  [kushi.ui.spinner.core :refer [spinner]]
  [kushi.ui.spinner.demo :as spinner.demo]
  [kushi.ui.tag.core :refer [tag]]
  [kushi.ui.tag.demo :as tag.demo]
  [kushi.ui.toast.core :refer [toast-attrs]]
  [kushi.ui.toast.demo :as toast.demo]
  [kushi.ui.tooltip.core :refer [tooltip-attrs]]
  [kushi.ui.tooltip.demo :as tooltip.demo]))


        ;; for surfaces:
        ;; Create classic variant
        ;; Create surface variant
        ;; Create belcher variant
        ;; Create fantasy variant
        ;; Create sci-fi variant
        ;; Create gel variant
        

        ;; tooltips(delay):
        ;; row of buttons that show all positions with no delay
        ;; row of buttons that show various delays 
        ;; row of buttons that show various animations 
        ;; row of buttons that show various stylings (no arrow etc)


        



        ;; Fix toast on mobile


        ;; check size of app etc.


        ;; upgrade lasertag clojars
        ;; upgrade fireworks clojars
        ;; upgrade kushi quickstart       
        ;; upgrade kushi clojars
        ;; deploy new kushi site



        ;; resume


        ;; upgrade tinycolor2


        ;; switch tab back to `Examples` when component section goes offscreen (later)


        ;; Smoother transition between Examples and Docs section
        ;; - fade-in, fade-out
        ;; - hightlight blink on tab header


        ;; Activate side components mobile menu on hover (for small desktops)

        ;; Some koind of splash page? (later)

        ;; finish rest of snippets (later)

        ;; section that is scrolling up and out , fade it out (later)

        ;; jiggle animation not work on mobile safari (later)

        ;; seems fine for now  ;; For some reason modal and card do not register as intersecting

        ;; test with pulling :examples from fn meta, then merging with examples from demo (later)

        ;; Maybe try to figure out feature macro (later)


        ;; Figure out interactive modal (later)

        ;; Add link and divisor components to doc (later)

        ;; Add tablist and tabs component

        ;; Redo color section design (later)


        ;; Make a p-like component for consistent body copy maybe call it text
        ;;   - Use text sizes for this 

        ;; Add :.text-xxlarge utility scale (later)

        ;; Add :.tracking-xxloose utility scale (later)
        
        ;; Add :.leading-xxloose utility scale (later)


        ;; Add 


        ;; Add APCA-based color checks

        ;;

        

        ;; ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;


;; Sketch for features macro:
'(features {:req         [kushi.ui.$.core :refer [$]]
            :examples    $.demo/examples}
           [{:sym            button
             :variants-base  #{:rounded :filled :bordered :minimal}
             :variants-order [:rounded :filled :bordered :minimal]
             :variants-attrs {:rounded  (sx :.rounded)
                              :filled   (sx :.rounded :.filled)
                              :bordered (sx :.rounded :.bordered)
                              :minimal  (sx :.rounded :.minimal)} }
            
            {:sym          spinner
             :extra-refers [donut propeller thinking]}

            ;; Uses :refer-sym override
            ;; Uses :extra reqs
            ;; Uses :component  - maybe change that name to `render`* something
            {:sym               toast
             :refer-sym         toast-attrs
             :component         :span
             :extra-refers      [toast-attrs dismiss-toast!]
             :variants-base     #{:positions}
             :variants-attrs    {:positions {}}}])

;=>

'[{:label          "button"
   :component      button
   :examples       button.demo/examples
   :component-meta (-> button var meta)
   :reqs           '[[kushi.ui.button.core :refer [button]]]
   :variants-base  #{:rounded :filled :bordered :minimal}
   :variants-order [:rounded :filled :bordered :minimal]
   :variants-attrs {:rounded  (sx :.rounded)
                    :filled   (sx :.rounded :.filled)
                    :bordered (sx :.rounded :.bordered)
                    :minimal  (sx :.rounded :.minimal)}}

  {:label          "spinner"
   :examples       spinner.demo/examples
   :component-meta (-> spinner var meta)
   :component      spinner
   :reqs           '[[kushi.ui.spinner.core :refer [spinner donut propeller thinking ]]]}
  
  {:label          "toast" 
   :examples       toast.demo/examples
   :component-meta (-> toast var meta)
   :component      :span
   :reqs           '[[kushi.ui.toast.core :refer [toast-attrs dismiss-toast!]]]
   :variants-base  #{:positions}
   :variants-attrs {:positions {}}}

  ]


(def playground-components 
  (filter 
   :label
  #_#(contains? #{
                ;; "button"     ;; ✅
                ;; "icon"       ;; ✅
                ;; "spinner"    ;; ✅
                ;; "switch"     ;; ✅
                ;; "radio"      ;; ✅
                ;; "checkbox"   ;; ✅ 
                ;; "slider"     ;; ✅
                "text field" ;; ✅

                ;; "tooltip"    ;; ✅
                ;; "popover"    ;; PS
                ;; "modal"      ;; PS
                ;; "toast"      ;; PS
                ;; "card"       ;; PS
                ;; "callout"    ;; PS
                ;; "collapse"   ;; PS
                ;; "accordian"  ;; PS
                ;; "tag"        ;; PS
                ;; "grid"       ;; PS
                }
               (:label %))

   ;; You need to wrap this whole collection in features macro

   [{:label          "button"
     :component      button
     :examples       button.demo/examples
     :component-meta (-> button var meta)
     :reqs           '[[kushi.ui.button.core :refer [button]]]
     :variants-base  #{:rounded :filled :bordered :minimal}
     :variants-order [:rounded :filled :bordered :minimal]
     :variants-attrs {:rounded  (sx :.rounded)
                      :filled   (sx :.rounded :.filled)
                      :bordered (sx :.rounded :.bordered)
                      :minimal  (sx :.rounded :.minimal)}}

    {:label          "icon"
     :component      icon
     :examples       icon.demo/examples
     :component-meta (-> icon var meta)
     :reqs           '[[kushi.ui.icon.core :refer [icon]]]
     :variants-base  #{:outlined :filled}
     :variants-order [:outlined :filled]
     :variants-attrs {:filled   {:-icon-filled? true}
                      :outlined {}}}

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
     :variants-base  #{:rounded}
     :variants-order [:rounded]
     :variants-attrs {:rounded (sx :.rounded)}
     }

    {:label          "tag"
     :examples       tag.demo/examples
     :component-meta (-> tag var meta)
     :component      tag
     :reqs           '[[kushi.ui.tag.core :refer [tag]]]
     :variants-base  #{:rounded :filled :bordered :minimal}
     :variants-order [:rounded :filled :bordered :minimal]
     :variants-attrs {:rounded  (sx :.rounded)
                      :filled   (sx :.rounded :.filled)
                      :bordered (sx :.rounded :.bordered)
                      :minimal  (sx :.rounded :.minimal)}}

    {:label          "callout"
     :examples       callout.demo/examples
     :component-meta (-> callout var meta)
     :component      callout
     :reqs           '[[kushi.ui.callout.core :refer [callout]]]
     :variants-base  #{:default :filled :bordered}
     :variants-order [:default :filled :bordered]
     :variants-attrs {:default  {}
                      :filled   (sx :.filled)
                      :bordered (sx :.bordered)}}

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
