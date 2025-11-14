(ns ^{:kushi/layer "user-styles"} kushi.ui.card.demo
  (:require [kushi.core :refer (sx merge-attrs)]
            [kushi.playground.util :refer-macros [sx-call]]
            [kushi.showcase.core
             :as showcase
             :refer [samples-with-variant samples]]
            [kushi.ui.card :refer [card]]
            [kushi.ui.callout :refer [callout]]
            [kushi.ui.flex :refer [flex-col flex-row]]
            [kushi.ui.icon :refer [icon]]
            [kushi.ui.icon-button :refer [icon-button]]
            [kushi.ui.link :refer [link]]
            ))


;; TODO remove section-label
;; TODO hoist reqs up to a higher level
(def sizes
  [:xxsmall
   :xsmall
   :small
   :medium
   :large])


(def demos
  [{:label     "Sizes from xxsmall to large"
    :row-attrs (sx :gtc--80px:400px)
    :desc      "Sizes from xxsmall to large"
    :samples   (samples
                [

                 "xxsmall"
                 [card 
                  {:size :xxsmall}
                  [flex-row (sx :ai--stretch :gap--0.8em)
                   [:div (merge-attrs
                          (sx :position--relative
                              :overflow--hidden
                              :.transition
                              :bgc--$neutral-200
                              :dark:bgc--$neutral-800
                              :w--3.5em
                              :h--3.5em)
                          {:data-ks-shape      :rounded
                           :data-ks-transition ""})
                    [:span (merge-attrs 
                            (sx [:transform "translate(0, 0.045em)"]
                                :display--block
                                :scale--2.55)
                            {:data-ks-position :absolute-centered})
                     "🐻‍❄"]]
                   [flex-col (sx {:jc :sa})
                    [:p (sx :fs--1.25em :fw--$weight-wee-bold) "Polar Bear"] 
                    [:p (sx :c--$secondary-foreground-color
                            :dark:c--$secondary-foreground-dark-mode)
                     "polar.bear@example.com"]]]]

                 "xsmall"
                 [card 
                  {:size :xsmall}
                  [flex-row (sx :ai--stretch :gap--0.8em)
                   [:div (sx :position--relative
                             :overflow--hidden
                             :.transition
                             :bgc--$neutral-200
                             :dark:bgc--$neutral-800
                             :w--3.5em
                             :h--3.5em)
                    [:span (merge-attrs
                            (sx [:transform "translate(0, 0.045em)"]
                                :display--block
                                :scale--2.55)
                            {:data-ks-position   :absolute-centered
                             :data-ks-shape      :rounded
                             :data-ks-transition ""})
                     "🐻‍❄"]]
                   [flex-col (sx {:jc :sa})
                    [:p (sx :fs--1.25em :fw--$weight-wee-bold) "Polar Bear"] 
                    [:p (sx :c--$secondary-foreground-color
                            :dark:c--$secondary-foreground-dark-mode)
                     "polar.bear@example.com"]]]]

                 "small"
                 [card 
                  {:size :small}
                  [flex-row (sx :ai--stretch :gap--0.8em)
                   [:div (merge-attrs
                          (sx :overflow--hidden
                              :bgc--$neutral-200
                              :dark:bgc--$neutral-800
                              :w--3.5em
                              :h--3.5em)
                          {:data-ks-position   :relative
                           :data-ks-shape      :rounded
                           :data-ks-transition ""})
                    [:span (merge-attrs
                            (sx [:transform "translate(0, 0.045em)"]
                                :display--block
                                :scale--2.55)
                            {:data-ks-position   :absolute-centered
                             :data-ks-transition ""})
                     "🐻‍❄"]]
                   [flex-col (sx {:jc :sa})
                    [:p (sx :fs--1.25em :fw--$weight-wee-bold) "Polar Bear"] 
                    [:p (sx :c--$secondary-foreground-color
                            :dark:c--$secondary-foreground-dark-mode)
                     "polar.bear@example.com"]]]]

                 "medium"
                 [card 
                  {:size :medium}
                  [flex-row (sx :ai--stretch :gap--0.8em)
                   [:div (merge-attrs
                          (sx :overflow--hidden
                              :bgc--$neutral-200
                              :dark:bgc--$neutral-800
                              :w--3.5em
                              :h--3.5em)
                          {:data-ks-position   :relative
                           :data-ks-shape      :rounded
                           :data-ks-transition ""})
                    [:span (merge-attrs
                            (sx [:transform "translate(0, 0.045em)"]
                                :scale--2.55)
                            {:data-ks-position :absolute-centered
                             :data-ks-display  :block})
                     "🐻‍❄"]]
                   [flex-col (sx {:jc :sa})
                    [:p (sx :fs--1.25em :fw--$weight-wee-bold) "Polar Bear"] 
                    [:p (sx :c--$secondary-foreground-color
                            :dark:c--$secondary-foreground-dark-mode)
                     "polar.bear@example.com"]]]]

                 "large"
                 [card 
                  {:size :large}
                  [flex-row (sx :ai--stretch :gap--0.8em)
                   [:div (sx :.rounded
                             :position--relative
                             :overflow--hidden
                             :.transition
                             :bgc--$neutral-200
                             :dark:bgc--$neutral-800
                             :w--3.5em
                             :h--3.5em)
                    [:span (merge-attrs
                            (sx [:transform "translate(0, 0.045em)"]
                                :scale--2.55)
                            {:data-ks-position :absolute-centered
                             :data-ks-display  :block})
                     "🐻‍❄"]]
                   [flex-col (sx {:jc :sa})
                    [:p (sx :fs--1.25em :fw--$weight-wee-bold) "Polar Bear"] 
                    [:p (sx :c--$secondary-foreground-color
                            :dark:c--$secondary-foreground-dark-mode)
                     "polar.bear@example.com"]]]]
                 ])}
   
   {:label     "Shadow sizes from xxsmall to xxxlarge"
    :row-attrs (sx :gtc--80px:400px
                   :row-gap--3rem
                   :_.ks-card:color--$foreground-color-secondary)
    :desc      "Shadow sizes from xxsmall to xxxlarge"
    :samples   (samples-with-variant
                {:variant       :shadow
                 :attrs         {:size :large}
                 :args          ["Card with shadow"]})}
   
   {:label     "Shadow colors"
    :row-attrs (sx :gtc--80px:400px
                   :row-gap--2rem
                   :_.ks-card:color--$foreground-color-secondary)
    :desc      "Shadow colors"
    :samples   (samples-with-variant
                {:variant       :shadow-color
                ;;  :variant-scale :size/xxsmall-large
                 :attrs         {:size :large :shadow :medium}
                 :args          ["Card with colored shadow"]})}
   ])


#_(def examples
  [
   {:desc      "Sizes from xxsmall to large"
    :row-attrs (sx 
                :ai--fs
                :flex-direction--column
                ["_.kushi-card:nth-child(5):d" :none]
                ["xsm:_.kushi-card:nth-child(5):d" :block]
                :_.kushi-card:w--fit-content
                :_.kushi-card:b--1px:solid:$neutral-200
                :dark:_.kushi-card:b--1px:solid:$neutral-700)
    :snippets-header component-examples/sizes-snippet-header*
    :snippets ['[card (sx :fs--xxlarge) "My content"]]
    :examples  (for [sz sizes]
                 {:label (name sz)
                  :attrs {:class [sz]}
                  :args  [[:div (sx :.flex-row-fs
                                    :.neutralize
                                    :ai--stretch
                                    :gap--0.8em)
                           [:div (sx :.rounded
                                     :position--relative
                                     :overflow--hidden
                                     :.transition
                                     :bgc--$neutral-200
                                     :dark:bgc--$neutral-800
                                     :w--3.5em
                                     :h--3.5em)
                            [:span (sx :.absolute-centered
                                       [:transform "translate(0, 0.045em)"]
                                       :display--block
                                       :scale--2.55)
                             "🐻‍❄"]]
                           [:section (sx :.flex-col-sa) 
                            [:p (sx :fs--1.25em :fw--$wee-bold) "Polar Bear"] 
                            [:p (sx :c--$secondary-foreground-color
                                    :dark:c--$secondary-foreground-dark-mode)
                             "polar.bear@example.com"]]]]})}
   
   {:desc      "Elevated levels from 0 to 5"
    :row-attrs (sx :.transition
                   :ai--fs
                   :flex-direction--column
                   :gap--2rem
                   :_.kushi-card:w--fit-content
                   :_.kushi-card:b--1px:solid:$neutral-200
                   :dark:_.kushi-card:b--1px:solid:$neutral-700)
    :examples  (for [sz   (range 5)
                     :let [class (str "elevated-" sz)]]
                 {:label class
                  :attrs {:class [class]}
                  :args  [[:div (sx :.flex-row-fs
                                    :ai--stretch
                                    :gap--0.8em)
                           [:div (sx :.rounded
                                     :position--relative
                                     :.transition
                                     :overflow--hidden
                                     :dark:bgc--$neutral-850
                                     :bgc--$neutral-200
                                     :w--3.5em
                                     :h--3.5em)
                            [:span (sx :.absolute-centered
                                       [:transform "translate(0, 0.045em)"]
                                       :display--block
                                       :scale--2.55)
                             "🐻‍❄"]]
                           [:section (sx :.flex-col-sa) 
                            [:p (sx :fs--1.25em :fw--$wee-bold) "Polar Bear"] 
                            [:p (sx :.foreground-color-secondary!) "polar.bear@example.com"]]]]})}
   


   {:desc      "Alien"
    :row-attrs (sx :ai--fs
                   :flex-direction--column
                   :gap--2rem
                   :_.kushi-card:w--fit-content
                   :_.kushi-card:b--1px:solid:$neutral-200
                   :dark:_.kushi-card:b--1px:solid:$neutral-800)
    :examples  [{:label "Alien"
                 :code  (sx-call [card
                                  (sx :fs--$size-xxsmall
                                      :xsm:fs--$size-small
                                      :sm:fs--$size-medium
                                      :md:fs--$size-large
                                      :lg:fs--$size-xlarge
                                      :.extra-bold
                                      :.flex-col-c
                                      :.rounded-small
                                      :p--0
                                      :height--12em
                                      :tt--u
                                      :ta--center
                                      :bgc--#313131
                                      :c--white
                                      :b--1px:solid:#9eef00
                                      :text-shadow--1px:1px:5px:#9eef00b5
                                      :box-shadow--inset:0px:0px:4em:#9eef0073
                                      :dark:box-shadow--inset:0px:0px:4em:#9eef002e)
                                  [:span (sx :pis--7ex
                                             :letter-spacing--7ex)
                                   "alien"]])}]}])
