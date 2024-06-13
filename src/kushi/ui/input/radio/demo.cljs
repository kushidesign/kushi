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
  (let [row-attrs {:class ["playground-example-row-bounded"]} ]
    [{:desc      "Showing sizes from xsmall to xxxlarge"
      :row-attrs (sx [:md:&_.playground-component-example-row-instance-code
                      {:ai             :fe
                       :flex-direction :row}]
                     [:&_.playground-component-example-row-instance-code
                      {:ai             :fs
                       :flex-direction :column}]
                     :&_label:pbe--0
                     :&_label:pie--0.35em)
      :examples  [{:code (sx-call (into [:<>] 
                                        (for [sz switch-sizes]
                                          [:div (sx :.flex-row-fs
                                                    :md:flex-direction--column) 
                                           [radio {:class        [sz]
                                                   :-input-attrs {:name           sz
                                                                  :defaultChecked true}}]
                                           [radio {:class        [sz]
                                                   :-input-attrs {:name sz}}]])))}]}
     {:desc     "Radio group with labels"
      :row-attrs row-attrs
      :examples [{:code (sx-call [:section 
                                  (sx :.flex-row-fs)
                                  [radio (sx {:-input-attrs {:name :demo}}) "Yes"]
                                  [radio (sx {:-input-attrs {:name :demo}}) "No"]
                                  [radio (sx {:-input-attrs {:name :demo}}) "Maybe"]])}]}

     {:desc     "Radio group with labels, inherited color"
      :row-attrs row-attrs
      :examples [{:code (sx-call [:section 
                                  (sx :.flex-row-fs)
                                  [radio (sx {:-input-attrs {:name :demo-color}}) "Yes"]
                                  [radio (sx {:-input-attrs {:name :demo-color}}) "No"]
                                  [radio (sx {:-input-attrs {:name :demo-color}}) "Maybe"]])}]}
     
     {:desc     "Custom, with default checked"
      :row-attrs row-attrs
      :examples [{:code (sx-call [:section
                                  (sx
                                   :dark:c--$purple-300
                                   :d--grid
                                   :md:gtc--1fr
                                   :md:gtc--1fr:1fr
                                   :row-gap--1em
                                   :column-gap--2em
                                   [:&_.emoji
                                    {:fs                  :28px
                                     :mi                  :0.33em
                                     :filter              "grayscale(1)"
                                     :transition-property :transform
                                     :transition-duration :500ms}]
                                   [:&_.kushi-radio-input:checked+.kushi-label>.emoji
                                    {:filter    :none
                                     :transform "scale(1.5)"
                                     :animation :jiggle2:0.5s}])
                                  [radio
                                   (sx :.normal {:-input-attrs {:name :demo-custom :defaultChecked true}})
                                   [label [:span.emoji "🦑"] "Squid"]]
                                  [radio
                                   (sx :.normal {:-input-attrs {:name :demo-custom}})
                                   [label [:span.emoji "🐋"] "Whale"]]
                                  [radio
                                   (sx :.normal {:-input-attrs {:name :demo-custom}})
                                   [label [:span.emoji "🦈 "] "Shark"]]
                                  [radio
                                   (sx :.normal {:-input-attrs {:name :demo-custom}})
                                   [label [:span.emoji "🐊"] "Croc"]]])}]}]))
