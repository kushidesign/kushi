(ns ^{:kushi/layer "user-styles"} kushi.ui.popover.demo
  (:require
   [kushi.ui.icon.core :refer [icon]]
   [kushi.ui.icon.mui.svg :as mui.svg ]
   [kushi.ui.popover.core :refer [dismiss-popover!]]
   [kushi.ui.text-field.core :refer [text-field]]
   [kushi.core :refer (sx merge-attrs)]
   [kushi.playground.util :refer-macros [sx-call]]
   [kushi.ui.button.core :refer [button]]
   [kushi.ui.popover.core :refer [popover-attrs]]
   [reagent.dom :as rdom]))

(defn popover-content []
  [:div
   (sx :.my-popover-content
       :position--relative
       :.flex-row-fs
       :fs--$small
       :ai--fs
       :pi--1.5em
       :xsm:pi--2.5em
       :pb--1.25em:1.75em
       :xsm:pb--2.25em:2.75em
       :min-width--200px
       :xsm:max-width--90vw
       :max-width--250px
       :min-height--120px)

   [:div (sx :.my-form
             :.flex-col-fs
             :gap--1em
             :_.kushi-text-input-label:min-width--7em
             :_.kushi-input-inline:gtc--36%:64%)
    [:h2 (sx :.my-form-header
             :fs--$medium
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
    (merge-attrs
     (sx :.kushi-popover-close-button
         :.neutral
         :.minimal
         :.pill
         :.top-right-corner-inside
         :position--absolute
         :fs--$small
         :zi--1
         [:--icon-button-padding-inline-ems :0.4em]
         [:opacity                         :$popover-close-button-opacity]
         [:--button-padding-block-ems       :$icon-button-padding-inline-ems]
         [:margin-inline                   :$popover-close-button-margin-inline||$icon-button-padding-inline-ems]
         [:margin-block                    :$popover-close-button-margin-block||$icon-button-padding-inline-ems])
     {:on-click dismiss-popover!})
    [icon mui.svg/close]]])



(def examples
  (let [row-attrs (sx :_.kushi-button:fs--$small)]
    [(let [code (sx-call (popover-attrs
                          {:-f (fn [popover-el]
                                 (rdom/render 
                                  (fn [] 
                                    [:div
                                     (sx :.flex-row-c
                                         :fs--$xxxlarge
                                         :padding--0.25em)
                                     "💃🏽"])
                                  popover-el))}))]
       {:desc      "Basic"
        :component button
        :reqs      '[[kushi.ui.button.core :refer [button]]
                     [reagent.dom :as rdom :refer [render]]]
        :row-attrs row-attrs
        :snippets  [(:quoted code)]
        :examples  [{:args     ["Open"]
                     :sx-attrs code}]})
     
     {:desc      "With manual placement"
      :component button
      :reqs      '[[kushi.ui.button.core :refer [button]]]
      :row-attrs row-attrs
      :examples  [{:args     ["Open"]
                   :sx-attrs (sx-call (popover-attrs
                                       {:-f         (fn [popover-el]
                                                      (rdom/render 
                                                       (fn [] 
                                                         [:div
                                                          (sx :fs--$xxxlarge
                                                              :.flex-row-c
                                                              :padding--0.25em)
                                                          "💃🏽"])
                                                       popover-el))
                                        :-placement :r}))}]}


     {:desc      "Arrowless"
      :component button
      :reqs      '[[kushi.ui.button.core :refer [button]]]
      :row-attrs row-attrs
      :examples  [{:args     ["Open"]
                   :sx-attrs (sx-call (popover-attrs
                                       {:-f         (fn [popover-el]
                                                      (rdom/render 
                                                       (fn [] 
                                                         [:div
                                                          (sx :fs--$xxxlarge
                                                              :.flex-row-c
                                                              :padding--0.25em)
                                                          "💃🏽"])
                                                       popover-el))
                                        :-arrow?    false
                                        :-placement :r}))}]}

     {:desc            "With form"
      :component       button
      :reqs            '[[kushi.ui.button.core :refer [button]]]
      :container-attrs (merge-attrs
                        (sx :d--none :xsm:d--block)
                        {:data-kushi-playground-example "popover-with-form"})
      :row-attrs       row-attrs
      :examples        [{:args     ["Open"]
                         :sx-attrs (sx-call
                                    (popover-attrs
                                     {:-f (fn [popover-el]
                                            (rdom/render popover-content
                                                         popover-el))
                                        ;; :class (:class (sx :max-width--200px))
                                      }))}]}
     
     
     {:desc      "With dismiss action"
      :component button
      :reqs      '[[kushi.ui.button.core :refer [button]]]
      :row-attrs row-attrs
      :examples  [{:code (sx-call 
                          [button
                           (popover-attrs
                            {:-f
                             (fn
                               [el]
                               (rdom/render
                                [:div
                                 (sx :.flex-col-c
                                     :ai--c
                                     :min-height--100%
                                     :p--1rem)
                                 [button (merge-attrs (sx :fs--$small)
                                                      {:on-click dismiss-popover!})
                                  "Close"]]
                                el))}) 
                           "Open"])}]}
     
     {:desc      "Auto-dismissing, with manual placement"
      :component button
      :reqs      '[[kushi.ui.button.core :refer [button]]]
      :row-attrs row-attrs
      :examples  [{:code (sx-call 
                          [button
                           (popover-attrs
                            {:-f             
                             (fn
                               [el]
                               (rdom/render
                                [:div
                                 (sx :.flex-col-c :ai--c :min-height--100% :p--1rem)
                                 [:p (sx :fs--$small)
                                  "I will close automatically,"
                                  [:br]
                                  "after 5000ms"]]
                                el)) 

                             :-auto-dismiss?
                             true

                             :-placement     
                             :r}) 
                           "Open"])}]}]))


