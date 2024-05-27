(ns kushi.ui.popover.demo
  (:require
   [goog.string]
   [kushi.ui.icon.core :refer [icon]]
   [kushi.ui.icon.mui.svg :as mui.svg ]
   [kushi.ui.button.core :refer [button]]
   [kushi.ui.popover.core :refer [dismiss-popover!]]
   [kushi.ui.input.text.core :refer [input]]
   [kushi.core :refer (sx merge-attrs)]
   [kushi.playground.component-examples :as component-examples]
   [kushi.playground.util :refer-macros [sx-call]]
   [kushi.ui.button.core :refer [button]]
   [kushi.ui.popover.core :refer [popover-attrs]]
   
   [reagent.dom :as rdom]))

(defn popover-content []
  [:div
   (sx 'my-popover-content
       :.relative
       :.flex-row-fs
       :.small
       :ai--fs
       :pi--2.5em
       :pb--2.25em:2.75em
       :min-width--200px
       :min-height--120px)

   [:div
    (sx 'my-form
        :.flex-col-fs
        :gap--1em
        :&_.kushi-text-input-label:min-width--7em
        :&_.kushi-input-inline:gtc--36%:64%)
    [:h2 (sx 'my-form-header
             :.medium
             :.semi-bold
             :mbe--0.75em)
     "Example Popover Form"]
    [input
     (sx
      {:placeholder      "100%"
       :-label           "Height"
       :-label-placement :inline})]
    [input
     (sx
      {:placeholder      "335px"
       :-label           "Min Width"
       :-label-placement :inline})]
    [input
     (sx
      {:placeholder      "75px"
       :-label           "Depth"
       :-label-placement :inline})]]

   [button
    (sx 'kushi-popover-close-button
        :.top-right-corner-inside
        :.neutral
        :.minimal
        :.small
        :.pill
        :zi--1
        [:$icon-button-padding-inline-ems :0.4em]
        [:opacity                         :$popover-close-button-opacity]
        [:$button-padding-block-ems       :$icon-button-padding-inline-ems]
        [:margin-inline                   :$popover-close-button-margin-inline||$icon-button-padding-inline-ems]
        [:margin-block                    :$popover-close-button-margin-block||$icon-button-padding-inline-ems]
        {:on-click dismiss-popover!})
    [icon mui.svg/close]]])


(declare popover-examples)

(defn demo [component-opts]
  (into [:<>]
        (for [
              ;; example-opts (take 1 popover-examples)
              example-opts popover-examples
              ;; example-opts (keep-indexed (fn [idx m] (when (contains? #{9} idx) m)) popover-examples)
              ]
          [component-examples/examples-section
           component-opts
           example-opts])))

(def popover-examples
  [{:desc      "Basic"
    :component button
    :reqs      '[[kushi.ui.button.core :refer [button]]]
    :examples  [{:label    "Basic"
                 :args     ["Click to open popover"]
                 :sx-attrs (sx-call (popover-attrs
                                     {:-f (fn [popover-el]
                                            (rdom/render (fn [] [:div
                                                                 (sx :.xxxlarge
                                                                     :.flex-row-c
                                                                     :padding--0.25em)
                                                                 "💃"])
                                                         popover-el))}))}]}
   
   {:desc      "With manual placement"
    :component button
    :reqs      '[[kushi.ui.button.core :refer [button]]]
    :examples  [{:label    "Basic"
                 :args     ["Click to open popover"]
                 :sx-attrs (sx-call (popover-attrs
                                     {:-f         (fn [popover-el]
                                                    (rdom/render (fn [] [:div
                                                                         (sx :.xxxlarge
                                                                             :.flex-row-c
                                                                             :padding--0.25em)
                                                                         "💃"])
                                                                 popover-el))
                                      :-placement :r}))}]}
   {:desc      "With form"
    :component button
    :reqs      '[[kushi.ui.button.core :refer [button]]]
    :examples  [{:label    "With form"
                 :args     ["Click to open popover"]
                 :sx-attrs (sx-call (popover-attrs
                                     {:-f (fn [popover-el]
                                            (rdom/render popover-content
                                                         popover-el))}))}]}
   ])


