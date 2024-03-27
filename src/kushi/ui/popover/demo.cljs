(ns kushi.ui.popover.demo
  (:require
   [goog.string]
   [kushi.core :refer (sx)]
   [kushi.ui.icon.core :refer [icon]]
   [kushi.ui.icon.mui.svg :as mui.svg ]
   [kushi.ui.button.core :refer [button]]
   [kushi.ui.popover.core :refer [close-popover!]]
   [kushi.ui.input.text.core :refer [input]]))

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
     "My Popover Form"]
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
        :.northeast-inside
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
        {:on-click close-popover!})
    [icon mui.svg/close]]])
