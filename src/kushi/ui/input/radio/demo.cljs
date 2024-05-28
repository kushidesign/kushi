(ns kushi.ui.input.radio.demo
  (:require [kushi.core :refer (sx)]
            [kushi.playground.component-examples :as component-examples]
            [kushi.playground.util :refer-macros [sx-call]]
            [kushi.ui.input.radio.core :refer [radio]]
            [kushi.ui.label.core :refer [label]]))

(declare radio-examples)

(defn demo [component-opts]
  (into [:<>]
        (for [
              ;; example-opts (take 1 radio-examples)
              example-opts radio-examples
              ;; example-opts (keep-indexed (fn [idx m] (when (contains? #{9} idx) m)) radio-examples)
              ]
          [component-examples/examples-section
           component-opts
           example-opts])))

(def switch-sizes
  [:xsmall
   :small
   :medium
   :large
   :xlarge
   :xxlarge
   :xxxlarge])

(def radio-examples
  [{:desc      "Showing sizes from `xsmall` to `xxxlarge`"
    :row-attrs (sx :ai--fe
                   :&_.playground-component-example-row-instance-code:ai--fe
                   :&_label:pbe--0
                   :&_label:pie--0.35em)
    :examples  [{:code (sx-call (into [:<>] 
                                      (for [sz switch-sizes]
                                        [:div (sx :.flex-col-fs) 
                                         [radio {:class        [sz]
                                                 :-input-attrs {:name           sz
                                                                :defaultChecked true}}]
                                         [radio {:class        [sz]
                                                 :-input-attrs {:name sz}}]])))}]}
   {:desc     "Radio group with labels"
    :examples [{:code (sx-call [:section (sx :.rounded
                                             :p--1em
                                             :b--1px:solid:$neutral-100
                                             :dark:b--1px:solid:$neutral-900
                                             :bgc--$neutral-50
                                             :dark:bgc--$neutral-950)
                                [label (sx :.bold :mbe--0.75em) "Choose an option:"]
                                [:section 
                                 (sx :.flex-row-fs)
                                 [radio (sx {:-input-attrs {:name :demo}}) "Yes"]
                                 [radio (sx {:-input-attrs {:name :demo}}) "No"]
                                 [radio (sx {:-input-attrs {:name :demo}}) "Maybe"]]])}]}

   {:desc     "Radio group with labels, inherited color"
    :examples [{:code (sx-call [:section (sx :.rounded
                                             :p--1em
                                             :b--1px:solid:$neutral-100
                                             :dark:b--1px:solid:$neutral-900
                                             :bgc--$neutral-50
                                             :dark:bgc--$neutral-950
                                             :c--$purple-500
                                             :dark:c--$purple-300)
                                [label (sx :.bold :mbe--0.75em) "Choose an option:"]
                                [:section 
                                 (sx :.flex-row-fs)
                                 [radio (sx {:-input-attrs {:name :demo}}) "Yes"]
                                 [radio (sx {:-input-attrs {:name :demo}}) "No"]
                                 [radio (sx {:-input-attrs {:name :demo}}) "Maybe"]]])}]}
   
   {:desc     "Custom, with default checked"
    :examples [{:code (sx-call [:span
                                (sx
                                 :.large
                                 :d--grid
                                 :gtc--1fr:1fr
                                 :&_.emoji:fs--28px
                                 :&_.emoji:mi--0.3em:0.6em
                                 :&_.kushi-radio:mbe--0.95em
                                 {:style {"&_.kushi-radio:nth-child(even):mis"                        :1em
                                          :&_.emoji:filter                                            "grayscale(1)"
                                          :&_.emoji:transition-property                               :transform
                                          :&_.emoji:transition-duration                               :500ms
                                          :&_.kushi-radio-input:checked+.kushi-label>.emoji:filter    :none
                                          :&_.kushi-radio-input:checked+.kushi-label>.emoji:transform "scale(1.5)"
                                          :&_.kushi-radio-input:checked+.kushi-label>.emoji:animation :jiggle2:0.5s}})
                                [radio
                                 (sx :.normal {:-input-attrs {:name           :demo
                                                              :defaultChecked true}})
                                 [label [:span.emoji "🦑"] "Squid"]]
                                [radio
                                 (sx :.normal {:-input-attrs {:name :demo}})
                                 [label [:span.emoji "🐋"] "Whale"]]
                                [radio
                                 (sx :.normal {:-input-attrs {:name :demo}})
                                 [label [:span.emoji "🦈 "] "Shark"]]
                                [radio
                                 (sx :.normal {:-input-attrs {:name :demo}})
                                 [label [:span.emoji "🐊"] "Croc"]]])}]}])
