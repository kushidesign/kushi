(ns ^{:kushi/layer "user-styles"} kushi.ui.radio.demo
  (:require [kushi.core :refer (sx merge-attrs)]
            [kushi.showcase.core
             :as showcase
             :refer [samples samples-with-variant]]
            [kushi.ui.radio :refer [radio]]
            [kushi.ui.label :refer [label]]))



;; TODO - Consider using Flex containers
(def demos
  [{:label   "Radio group with labels"
    :samples (samples 
              [[:div (sx :.flex-row-start
                         :gap--1.5rem
                         :p--1.5rem
                         :br--5px
                         :b--$divisor-4
                         :dark:b--$divisor-4-dark-mode)
                [:div.flex-row-start 
                 [radio {:id             :foo-yes
                         :name           :foo
                         :defaultChecked true}]
                 [:label.pointer (merge-attrs (sx :.pointer :padding-inline-start--0.5em)
                                              {:for :foo-yes}) "Yes"]]
                [:div.flex-row-start 
                 [radio {:id   :foo-no
                         :name :foo}]
                 [:label
                  (merge-attrs (sx :.pointer :padding-inline-start--0.5em)
                               {:for :foo-no}) "No"]]
                [:div.flex-row-start 
                 [radio {:id   :foo-maybe
                         :name :foo}]
                 [:label
                  (merge-attrs (sx :.pointer :padding-inline-start--0.5em)
                               {:for :foo-maybe}) "Maybe"]]]])}
   
   {:label   "Radio group with labels, inherited color"
    :samples (samples 
              [[:div (sx :.flex-row-start
                         :gap--1.5rem
                         :p--1.5rem
                         :br--5px
                         :b--$divisor-4
                         :dark:b--$divisor-4-dark-mode)
                [:div.flex-row-start 
                 {:data-ks-colorway :magenta
                  :data-ks-surface  :transparent }
                 [radio {:id             :bar-yes
                         :name           :bar
                         :defaultChecked true}]
                 [:label.pointer (merge-attrs (sx :.pointer :padding-inline-start--0.5em)
                                              {:for :bar-yes}) "Yes"]]
                [:div.flex-row-start 
                 {:data-ks-colorway :magenta
                  :data-ks-surface  :transparent }
                 [radio {:id   :bar-no
                         :name :bar}]
                 [:label
                  (merge-attrs (sx :.pointer :padding-inline-start--0.5em)
                               {:for :bar-no}) "No"]]

                [:div.flex-row-start 
                 {:data-ks-colorway :magenta
                  :data-ks-surface  :transparent }
                 [radio {:id   :bar-maybe
                         :name :bar}]
                 [:label
                  (merge-attrs (sx :.pointer :padding-inline-start--0.5em)
                               {:for :bar-maybe}) "Maybe"]]]])}
   
    {:label   "Radio sizes"
     :samples (samples-with-variant 
               {:variant :sizing
                :attrs {:defaultChecked :true}})}

    {:label   "Radio colorways"
     :samples (samples-with-variant 
               {:variant       :colorway
                :variant-scale :colorway/named
                :attrs/display {:sizing :xxxlarge}
                :attrs         {:defaultChecked :true}})}
   
   #_{:label  "Custom"
    :samples (samples
              [[:section
                (sx
                 :d--grid
                 :xsm:gtc--1fr
                 :xsm:gtc--1fr:1fr
                 :row-gap--1em
                 :column-gap--2em
                 [:_.emoji
                  {:fs                  :28px
                   :mi                  :0.33em
                   :filter              "grayscale(1)"
                   :transition-property :transform
                   :transition-duration :500ms}]
                 [:_.kushi-radio-input:checked+.kushi-label>.emoji
                  {:filter    :none
                   :transform "scale(1.5)"
                   :animation :jiggle2:0.5s}])
                [radio
                 (merge-attrs (sx :.normal)
                              {:input-attrs {:name           :demo-custom
                                             :defaultChecked true}})
                 [:label [:span.emoji "🦑"] "Squid"]]
                [radio
                 (merge-attrs (sx :.normal) {:input-attrs {:name :demo-custom}})
                 [:label [:span.emoji "🐋"] "Whale"]]
                [radio
                 (merge-attrs (sx :.normal) {:input-attrs {:name :demo-custom}})
                 [:label [:span.emoji "🦈 "] "Shark"]]
                [radio
                 (merge-attrs (sx :.normal) {:input-attrs {:name :demo-custom}})
                 [:label [:span.emoji "🐊"] "Croc"]]]]
              )
    
    }

   
   
   ])

;; (def sizes
;;   [:xsmall
;;    :small
;;    :medium
;;    :large
;;    :xlarge
;;    :xxlarge
;;    :xxxlarge])

;; (def examples
;;   (let [row-attrs {:class ["playground-example-row-bounded"]}]
;;     [(merge
;;       #_(component-examples/sizes-snippet-scale 'radio)
;;       {:desc      "Showing sizes from xsmall to xxxlarge"
;;        :row-attrs (sx [:_.instance-code
;;                        {:ai                 :fs
;;                         :xsm:ai             :fe
;;                         :flex-direction     :column
;;                         :xsm:flex-direction :row
;;                         :w                  :100%
;;                         :jc                 :sb
;;                         :max-width          :400px}]
;;                       :_label:pbe--0
;;                       :_label:pie--0.35em)
;;        :snippets-header component-examples/sizes-snippet-header*
;;        :snippets '[[:div 
;;                     [radio (merge-attrs
;;                             (sx :.xxxlarge)
;;                             {:input-attrs {:name           :xxxlarge-sample
;;                                             :defaultChecked true}})]
;;                     [radio (merge-attrs
;;                             (sx :.xxxlarge)
;;                             {:input-attrs {:name :xxxlarge-sample}})]]]
;;        :examples  [{:code (sx-call (into [:<>] 
;;                                          (for [sz sizes]
;;                                            [:div (sx :.flex-row-fs
;;                                                      :xsm:flex-direction--column) 
;;                                             [radio {:class        [sz]
;;                                                     :input-attrs {:name           sz
;;                                                                    :defaultChecked true}}]
;;                                             [radio {:class        [sz]
;;                                                     :input-attrs {:name sz}}]])))}]})
;;      {:desc     "Radio group with labels"
;;       :row-attrs row-attrs
;;       :snippets  '[[:section 
;;                     (sx :.flex-row-fs)
;;                     [radio {:input-attrs {:name :demo}} "Yes"]
;;                     [radio {:input-attrs {:name :demo}} "No"]
;;                     [radio {:input-attrs {:name :demo}} "Maybe"]]]
;;       :examples [{:code (sx-call [:section 
;;                                   (sx :.flex-row-fs)
;;                                   [radio {:input-attrs {:name :demo}} "Yes"]
;;                                   [radio {:input-attrs {:name :demo}} "No"]
;;                                   [radio {:input-attrs {:name :demo}} "Maybe"]])}]}

;;      {:desc     "Radio group with labels, inherited color"
;;       :row-attrs row-attrs
;;       :snippets  '[[:section 
;;                     (sx :.flex-row-fs
;;                         :c--$purple-600
;;                         :dark:c--$purple-300)
;;                     [radio {:input-attrs {:name :demo}} "Yes"]
;;                     [radio {:input-attrs {:name :demo}} "No"]
;;                     [radio {:input-attrs {:name :demo}} "Maybe"]]]
;;       :examples [{:code (sx-call [:section 
;;                                   (sx :.flex-row-fs
;;                                       :c--$purple-600
;;                                       :dark:c--$purple-300
;;                                       )
;;                                   [radio {:input-attrs {:name :demo-color}} "Yes"]
;;                                   [radio {:input-attrs {:name :demo-color}} "No"]
;;                                   [radio {:input-attrs {:name :demo-color}} "Maybe"]])}]}
     
;;      (let [code (sx-call [:section
;;                           (sx
;;                            :d--grid
;;                            :xsm:gtc--1fr
;;                            :xsm:gtc--1fr:1fr
;;                            :row-gap--1em
;;                            :column-gap--2em
;;                            [:_.emoji
;;                             {:fs                  :28px
;;                              :mi                  :0.33em
;;                              :filter              "grayscale(1)"
;;                              :transition-property :transform
;;                              :transition-duration :500ms}]
;;                            [:_.kushi-radio-input:checked+.kushi-label>.emoji
;;                             {:filter    :none
;;                              :transform "scale(1.5)"
;;                              :animation :jiggle2:0.5s}])
;;                           [radio
;;                            (merge-attrs (sx :.normal)
;;                                         {:input-attrs {:name           :demo-custom
;;                                                         :defaultChecked true}})
;;                            [label [:span.emoji "🦑"] "Squid"]]
;;                           [radio
;;                            (merge-attrs (sx :.normal) {:input-attrs {:name :demo-custom}})
;;                            [label [:span.emoji "🐋"] "Whale"]]
;;                           [radio
;;                            (merge-attrs (sx :.normal) {:input-attrs {:name :demo-custom}})
;;                            [label [:span.emoji "🦈 "] "Shark"]]
;;                           [radio
;;                            (merge-attrs (sx :.normal) {:input-attrs {:name :demo-custom}})
;;                            [label [:span.emoji "🐊"] "Croc"]]])] 
;;        {:desc      "Custom, with default checked"
;;         :row-attrs row-attrs
;;         :snippets  [(:quoted code)]
;;         :examples  [{:code code}]})]))
