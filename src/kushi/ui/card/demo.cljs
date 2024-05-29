(ns kushi.ui.card.demo
  (:require [kushi.core :refer (sx)]
            [kushi.playground.component-examples :as component-examples]
            [kushi.playground.util :refer-macros [sx-call]]
            [kushi.ui.card.core :refer [card]]))



(declare card-examples)


(defn demo [component-opts]
  (into [:<>]
        (for [
              ;; example-opts (take 1 card-examples)
              example-opts card-examples
              ;; example-opts (keep-indexed (fn [idx m] (when (contains? #{1} idx) m)) card-examples)
              ]
          [component-examples/examples-section component-opts example-opts])))


;; TODO remove section-label
;; TODO hoist reqs up to a higher level
(def card-sizes
  [:xxsmall
   :xsmall
   :small
   :medium
   :large])

(def card-examples
  [
   {:desc      "Sizes from xxsmall to large"
    :row-attrs (sx :ai--fs
                   :flex-direction--column
                   :&_.kushi-card:w--fit-content
                   :&_.kushi-card:b--1px:solid:$neutral-200
                   :dark&_.kushi-card:b--1px:solid:$neutral-800)
    :examples  (for [sz card-sizes]
                 {:label (name sz)
                  :attrs {:class [sz]}
                  :args  [[:div (sx :.flex-row-fs
                                    :ai--stretch
                                    :gap--0.8em)
                           [:div (sx :.rounded
                                     :.relative
                                     :overflow--hidden
                                     :bgc--$neutral-200
                                     :w--3.5em
                                     :h--3.5em)
                            [:span (sx :.absolute-centered
                                       [:transform '(translate 0 :0.045em)]
                                       :display--block
                                       :scale--2.55)
                             "🐻‍❄"]]
                           [:section (sx :.flex-col-sa) 
                            [:p (sx :fs--1.25em :.wee-bold) "Polar Bear"] 
                            [:p (sx :c--$neutral-secondary-fg) "polar.bear@example.com"]]]]})}
   
   {:desc      "Elevated levels from 0 to 5"
    :row-attrs (sx :ai--fs
                   :flex-direction--column
                   :gap--2rem
                   :&_.kushi-card:w--fit-content
                   :&_.kushi-card:b--1px:solid:$neutral-200
                   :dark&_.kushi-card:b--1px:solid:$neutral-800)
    :examples  (for [sz   (range 5)
                     :let [class (str "elevated-" sz)]]
                 {:label class
                  :attrs {:class [class]}
                  :args  [[:div (sx :.flex-row-fs
                                    :ai--stretch
                                    :gap--0.8em)
                           [:div (sx :.rounded
                                     :.relative
                                     :overflow--hidden
                                     :bgc--$neutral-200
                                     :w--3.5em
                                     :h--3.5em)
                            [:span (sx :.absolute-centered
                                       [:transform '(translate 0 :0.045em)]
                                       :display--block
                                       :scale--2.55)
                             "🐻‍❄"]]
                           [:section (sx :.flex-col-sa) 
                            [:p (sx :fs--1.25em :.wee-bold) "Polar Bear"] 
                            [:p (sx :c--$neutral-secondary-fg) "polar.bear@example.com"]]]]})}
   
   {:desc      "Alien"
    :row-attrs (sx :ai--fs
                   :flex-direction--column
                   :gap--2rem
                   :&_.kushi-card:w--fit-content
                   :&_.kushi-card:b--1px:solid:$neutral-200
                   :dark&_.kushi-card:b--1px:solid:$neutral-800)
    :examples  [{:label "Alien"
                 :code  (sx-call [card
                                  (sx
                                   :.large
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
                                 [:span (sx :pis--7ex :letter-spacing--7ex) "alien"]])}]}])



