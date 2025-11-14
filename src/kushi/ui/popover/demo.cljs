(ns ^{:kushi/layer "user-styles"} kushi.ui.popover.demo
  (:require
   [kushi.ui.icon :refer [icon]]
   [kushi.ui.icon.mui.svg :as mui.svg ]
   [kushi.ui.text-field :refer [text-field]]
   [kushi.core :refer (sx css merge-attrs)]
   [kushi.ui.button :refer [button]]
   [kushi.ui.icon-button :refer [icon-button]]
   [kushi.ui.popover :refer [popover-attrs dismiss-popover!]]
   [kushi.showcase.core :as showcase :refer [samples]]
   [reagent.dom :as rdom]))

(defn popover-content []
  [:div
   (sx :.my-popover-content
       :position--relative
       :.flex-row-fs
       :fs--$size-small
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
             :fs--$size-medium
             :fw--$semi-bold
             :mbe--0.75em)
     "Example Popover Form"]
    [text-field
     {:placeholder      "100%"
      :label           "Height"
      :label-placement :inline}]
    [text-field
     {:placeholder      "335px"
      :label           "Min Width"
      :label-placement :inline}]
    [text-field
     {:placeholder      "75px"
      :label           "Depth"
      :label-placement :inline}]]

   [icon-button
    (merge-attrs
     {:on-click dismiss-popover!
      :shape   :pill
      :surface :minimal}
     (sx :.kushi-popover-close-button
         :.neutral
         :.top-right-corner-inside
         {:position      :absolute
          :fs            :$size-small
          :zi            1
          :opacity       :$popover-close-button-opacity
          :margin-inline :$popover-close-button-margin-inline||$icon-button-padding-inline
          :margin-block  :$popover-close-button-margin-block||$icon-button-padding-inline}))
    [icon mui.svg/close]]])


(def demos
  (let []
    [
     {:label         "Basic"
      :header/dialog "Popover"
      :require       '[[kushi.ui.popover.core :refer [popover-attrs dismiss-popover!]]
                       [kushi.ui.button :refer [button]]
                       [kushi.ui.button.core :refer [button]]                  
                       [reagent.dom :refer [render]]]
      :samples       (samples 
                      [[button
                        (merge-attrs 
                         {:size :small}
                         (popover-attrs
                          {:f (fn [popover-el]
                                (rdom/render
                                 (fn []
                                   [:div 
                                    (sx :d--flex
                                        :jc--center
                                        :fs--$size-xxxlarge 
                                        :padding--0.25em)
                                    "💃🏽"])
                                 popover-el))}))
                        "Open"]])}

     {:label         "Styling via design token at callsite"
      :header/dialog "Popover"
      :require       '[[kushi.ui.button :refer [button]]
                       [reagent.dom :as rdom :refer [render]]]
      :row-attrs     (sx :_.ks-button:fs--$size-small)
      :samples       (samples 
                      [[button
                        (merge-attrs
                         {:size :small}
                         (popover-attrs
                          {:popover-class (css {:--popover-background-color           :$purple-100
                                                :--popover-background-color-dark-mode :$purple-900})
                           :f             (fn [popover-el]
                                            (rdom/render 
                                             (fn [] 
                                               [:div
                                                (sx :.flex-row-c
                                                    :fs--$size-xxxlarge
                                                    :padding--0.25em)
                                                "💃🏽"])
                                             popover-el))}))
                        "Open"]])}

     {:label         "With manual placement"
      :header/dialog "Popover"
      :require       '[[kushi.ui.button :refer [button]]
                       [reagent.dom :as rdom :refer [render]]]
      :row-attrs     (sx :_.ks-button:fs--$size-small)
      :samples       (samples 
                      [[button
                        (merge-attrs 
                         {:size :small}
                         (popover-attrs
                          {:placement :r
                           :f         (fn [popover-el]
                                        (rdom/render 
                                         (fn [] 
                                           [:div
                                            (sx :.flex-row-c
                                                :fs--$size-xxxlarge
                                                :padding--0.25em)
                                            "💃🏽"])
                                         popover-el))}))
                        "Open"]])}

     {:label         "Arrowless"
      :header/dialog "Popover"
      :require       '[[kushi.ui.button :refer [button]]
                       [reagent.dom :as rdom :refer [render]]]
      :row-attrs     (sx :_.ks-button:fs--$size-small)
      :samples       (samples 
                      [[button
                        (merge-attrs
                         {:size :small}
                         (popover-attrs
                          {:placement :r
                           :arrow?    false
                           :f         (fn [popover-el]
                                        (rdom/render 
                                         (fn [] 
                                           [:div
                                            (sx :.flex-row-c
                                                :fs--$size-xxxlarge
                                                :padding--0.25em)
                                            "💃🏽"])
                                         popover-el))}))
                        "Open"]])}
     
     {:label         "With form"
      :header/dialog "Popover"
      :require       '[[kushi.ui.button :refer [button]]
                       [reagent.dom :as rdom :refer [render]]]
      :row-attrs     (sx :_.ks-button:fs--$size-small)
      :samples       (samples 
                      [[button
                        (merge-attrs
                         {:size :small}
                         (popover-attrs
                          {:placement :r
                           :arrow?    false
                           :f         (fn [popover-el]
                                        (rdom/render 
                                         (fn [] 
                                           [:div
                                            (sx :.my-popover-content
                                                :display--flex
                                                :position--relative
                                                :fs--$size-small
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
                                                      :display--flex
                                                      :flex-direction--column
                                                      :gap--1em
                                                      :_.ks-text-input-label:min-width--7em
                                                      :_.ks-input-inline:gtc--36%:64%)
                                             [:h2 (sx :.my-form-header
                                                      :fs--$size-medium
                                                      :fw--$weight-semi-bold
                                                      :mbe--0.75em)
                                              "Example Popover Form"]
                                             [text-field
                                              {:placeholder     "100%"
                                               :label-text      "Height"
                                               :label-placement :inline}]
                                             [text-field
                                              {:placeholder     "335px"
                                               :label-text      "Min Width"
                                               :label-placement :inline}]
                                             [text-field
                                              {:placeholder     "75px"
                                               :label-text      "Depth"
                                               :label-placement :inline}]]

                                            [icon-button
                                             (merge-attrs
                                              {:on-click         dismiss-popover!
                                               :shape            :pill
                                               :surface          :minimal
                                               :data-ks-position :top-right-corner-inside}
                                              (sx :.ks-popover-close-button
                                                  {:fs            :$size-small
                                                   :zi            1
                                                   :opacity       :$popover-close-button-opacity
                                                   :margin-inline :$popover-close-button-margin-inline||$icon-button-padding-inline
                                                   :margin-block  :$popover-close-button-margin-block||$icon-button-padding-inline}))
                                             :close]])
                                         popover-el))}))
                        "Open"]])}
     
     {:label         "With dismiss action"
      :header/dialog "Popover"
      :require       '[[kushi.ui.button :refer [button]]
                       [reagent.dom :as rdom :refer [render]]]
      :row-attrs     (sx :_.ks-button:fs--$size-small)
      :samples       (samples [[button
                                (merge-attrs
                                 {:size :small}
                                 (popover-attrs
                                  {:f (fn
                                        [el]
                                        (rdom/render
                                         [:div
                                          (sx :display--flex
                                              :flex-direction--column
                                              :jc--c
                                              :ai--c
                                              :min-height--100%
                                              :p--1rem)
                                          [button (merge-attrs 
                                                   (sx :fs--$size-small)
                                                   {:on-click dismiss-popover!})
                                           "Close"]]
                                         el))})) 
                                "Open"]])}
     
     {:label         "With dismiss action"
      :header/dialog "Popover"
      :require       '[[kushi.ui.button :refer [button]]
                       [reagent.dom :as rdom :refer [render]]]
      :row-attrs     (sx :_.ks-button:fs--$size-small)
      :samples       (samples [[button
                                (merge-attrs
                                 {:size :small}
                                 (popover-attrs
                                  {:f             (fn
                                                    [el]
                                                    (rdom/render
                                                     [:div
                                                      (merge-attrs (sx :ai--c :min-height--100% :p--1rem)
                                                                   {:data-ks-display :flex-col-center})
                                                      [:p (sx :fs--$size-small)
                                                       "I will close automatically,"
                                                       [:br]
                                                       "after 5000ms"]]
                                                     el)) 
                                   :auto-dismiss? true
                                   :placement     :r})) 
                                "Open"]])}]))
