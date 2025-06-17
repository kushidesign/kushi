(ns ^{:kushi/layer "user-styles"} kushi.ui.card.demo
  (:require [kushi.core :refer (sx)]
            [kushi.playground.component-examples :as component-examples]
            [kushi.playground.util :refer-macros [sx-call]]
            [kushi.ui.card :refer [card]]))


;; TODO remove section-label
;; TODO hoist reqs up to a higher level
(def sizes
  [:xxsmall
   :xsmall
   :small
   :medium
   :large])

(def examples
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
                                  (sx :fs--$xxsmall
                                      :xsm:fs--$small
                                      :sm:fs--$medium
                                      :md:fs--$large
                                      :lg:fs--$xlarge
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
