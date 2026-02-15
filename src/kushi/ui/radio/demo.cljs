(ns ^{:kushi/layer "user-styles"} kushi.ui.radio.demo
  (:require [kushi.core :refer (sx css merge-attrs)]
            [fireworks.core :refer [? !? ?> !?>]]
            [kushi.showcase.core
             :as showcase
             :refer [samples samples-with-variant]]
            [kushi.ui.radio :refer [radio]]
            [kushi.ui.flex :refer [flex-row]]
            [kushi.ui.label :refer [label]]
            [kushi.ui.variants :as variants]))



;; TODO - Consider using Flex containers
(def demos
  [

   {:label   "Radio sizes"
    :samples (samples-with-variant 
              {:variant :size
               :attrs   {:defaultChecked :true
                         :name           :radio-sizes-demo
                         }})}

   {:label   "Radio weights"
    :samples (samples-with-variant 
              {:variant :weight
               :attrs   {:defaultChecked false
                         :name           :radio-weights-demo
                         :size      :3xl}})}
   ]

  #_[
   {:label   "Radio group with labels"
    :samples (samples 
              [
               [flex-row (sx :gap--1.5rem
                             :p--1.5rem
                             :br--5px
                             :b--$divisor-4
                             :dark:b--$divisor-4-dark-mode)
                [flex-row 
                 [radio {:id             :foo-yes
                         :name           :foo
                         :defaultChecked true}]
                 [:label.pointer (merge-attrs (sx :.pointer :padding-inline-start--0.5em)
                                              {:for :foo-yes}) "Yes"]]
                [flex-row 
                 [radio {:id   :foo-no
                         :name :foo}]
                 [:label
                  (merge-attrs (sx :.pointer :padding-inline-start--0.5em)
                               {:for :foo-no}) "No"]]
                [flex-row 
                 [radio {:id   :foo-maybe
                         :name :foo}]
                 [:label
                  (merge-attrs (sx :.pointer :padding-inline-start--0.5em)
                               {:for :foo-maybe}) "Maybe"]]]])}
   


   ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
   ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
   ;;
   ;;         Fix inherited colorways with flex-row div
   ;;
   ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
   ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;



   {:label   "Radio group with labels, inherited color"
    :samples (samples 
              [[flex-row {:colorway :magenta
                          :surface  :transparent
                          :class    (css :gap--1.5rem
                                         :p--1.5rem
                                         :br--5px
                                         :b--$divisor-4
                                         :dark:b--$divisor-4-dark-mode)}
                [flex-row 
                 [radio {:id             :bar-yes
                         :surface        :transparent
                         :name           :bar
                         :defaultChecked true}]
                 [:label.pointer (merge-attrs (sx :.pointer :padding-inline-start--0.5em)
                                              {:for :bar-yes}) "Yes"]]
                [flex-row 
                 [radio {:id      :bar-no
                         :surface :transparent
                         :name    :bar}]
                 [:label
                  (merge-attrs (sx :.pointer :padding-inline-start--0.5em)
                               {:for :bar-no}) "No"]]

                [flex-row 
                 [radio {:id      :bar-maybe
                         :surface :transparent
                         :name    :bar}]
                 [:label
                  (merge-attrs (sx :.pointer :padding-inline-start--0.5em)
                               {:for :bar-maybe}) "Maybe"]]]
               
               
               ;; sample code with data-ks-centric approach
               #_[:div 
                  (merge-attrs
                   (sx :_label:padding-inline-start--0.5em)
                   (data-ks {:flexbox   :row-start
                             :boundary* 1
                             :radius*   5}))
                  [flex-row 
                   {:colorway :magenta
                    :gap*     1      
                    :surface  :transparent} 
                   [radio {:id             :bar-yes
                           :name           :bar
                           :defaultChecked true}]
                   [:label {:for :bar-yes}
                    "Yes"]]
                  [flex-row 
                   {:colorway :magenta
                    :gap*     1      
                    :surface  :transparent}
                   [radio {:id   :bar-no
                           :name :bar}]
                   [:label
                    (merge-attrs {:for :bar-no})
                    "No"]]

                  [flex-row 
                   {:colorway :magenta
                    :gap*     1      
                    :surface  :transparent}
                   [radio {:id   :bar-maybe
                           :name :bar}]
                   [:label
                    (merge-attrs {:for :bar-maybe})
                    "Maybe"]]]
               
               ])}
   
   {:label   "Radio sizes"
    :samples (samples-with-variant 
              {:variant :size
               :attrs   {:defaultChecked :true
                         :name           :radio-sizes-demo
                         }})}

   {:label   "Radio weights"
    :samples (samples-with-variant 
              {:variant :weight
               :attrs   {:defaultChecked false
                         :name           :radio-weights-demo
                         :size      :3xl}})}

  ;;  {:label   "Radio colorways"
  ;;   :samples (samples-with-variant 
  ;;             {:variant       :colorway
  ;;              :variant-scale :colorway/named
  ;;              :attrs/display {:size :3xl}
  ;;              :attrs         {:defaultChecked :true}})}
   
   #_(? :pp
      (reduce (fn [acc k]
                (conj acc
                      (name k)
                      ['flex-row-start 
                       {:gap :0.5em}
                       ['radio {:colorway       k
                                :size         :3xl
                                :name           k
                                :defaultChecked true}]
                       ['radio {:colorway k     
                                :size   :3xl
                                :name     k}]]) )
              []
              variants/colorways-named))

   {:label     "Radio colorways"
    :row-attrs (sx {:_.ks-flex-row:gap :0.5em})
    ;; This recognizes the even/odd structure of the vector, and uses the strings
    ;; as labels for the samples
    :samples   (samples
                ["gray"
                 [flex-row
                  {:surface :transparent :colorway :neutral}
                  [radio
                   {:size           :3xl,
                    :name           :gray,
                    :defaultChecked true}]
                  [radio {:size :3xl
                          :name :gray}]]

                 "purple"
                 [flex-row
                  {:surface :transparent :colorway :purple}
                  [radio
                   {:size           :3xl,
                    :name           :purple,
                    :defaultChecked true}]
                  [radio {:size :3xl
                          :name :purple}]]

                 "blue"
                 [flex-row
                  {:surface :transparent :colorway :blue}
                  [radio
                   {:size           :3xl,
                    :name           :blue,
                    :defaultChecked true}]
                  [radio {:size :3xl
                          :name :blue}]]

                 "green"
                 [flex-row
                  {:surface :transparent :colorway :green}
                  [radio
                   {:colorway       :green,
                    :size           :3xl,
                    :name           :green,
                    :defaultChecked true}]
                  [radio {:colorway :green
                          :size     :3xl
                          :name     :green}]]
                 
                 "lime"
                 [flex-row
                  {:surface :transparent :colorway :lime}
                  [radio
                   {:colorway       :lime,
                    :size           :3xl,
                    :name           :lime,
                    :defaultChecked true}]
                  [radio {:colorway :lime
                          :size     :3xl
                          :name     :lime}]]

                 "yellow"
                 [flex-row
                  {:surface :transparent :colorway :yellow}
                  [radio
                   {:colorway       :yellow,
                    :size           :3xl,
                    :name           :yellow,
                    :defaultChecked true}]
                  [radio {:colorway :yellow
                          :size     :3xl
                          :name     :yellow}]]

                 "gold"
                 [flex-row
                  {:surface :transparent :colorway :gold}
                  [radio
                   {:colorway       :gold,
                    :size           :3xl,
                    :name           :gold,
                    :defaultChecked true}]
                  [radio {:colorway :gold
                          :size     :3xl
                          :name     :gold}]]

                 "orange"
                 [flex-row
                  {:surface :transparent :colorway :gold}
                  [radio
                   {:colorway       :orange,
                    :size           :3xl,
                    :name           :orange,
                    :defaultChecked true}]
                  [radio {:colorway :orange
                          :size     :3xl
                          :name     :orange}]]

                 "red"
                 [flex-row
                  {:surface :transparent :colorway :red}
                  [radio
                   {:colorway       :red,
                    :size           :3xl,
                    :name           :red,
                    :defaultChecked true}]
                  [radio {:colorway :red
                          :size     :3xl
                          :name     :red}]]

                 "magenta"
                 [flex-row
                  {:surface :transparent :colorway :magenta}
                  [radio
                   {:colorway       :magenta,
                    :size           :3xl,
                    :name           :magenta,
                    :defaultChecked true}]
                  [radio {:colorway :magenta
                          :size     :3xl
                          :name     :magenta}]]

                 "sand"
                 [flex-row
                  {:surface :transparent :colorway :sand}
                  [radio
                   {:colorway       :sand,
                    :size           :3xl,
                    :name           :sand,
                    :defaultChecked true}]
                  [radio {:colorway :sand
                          :size     :3xl
                          :name     :sand}]]

                 "slate"
                 [flex-row
                  {:surface :transparent :colorway :slate}
                  [radio
                   {:colorway       :slate,
                    :size      :3xl,
                    :name           :slate,
                    :defaultChecked true}]
                  [radio {:colorway  :slate
                          :size :3xl
                          :name      :slate}]]
                 
                 
                 ])}
   
   #_{:label   "Custom"
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
;;   [:xs
;;    :sm
;;    :base
;;    :lg
;;    :xl
;;    :2xl
;;    :3xl])

;; (def examples
;;   (let [row-attrs {:class ["playground-example-row-bounded"]}]
;;     [(merge
;;       #_(component-examples/sizes-snippet-scale 'radio)
;;       {:desc      "Showing sizes from xs to 3xl"
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
;;                             (sx :.3xl)
;;                             {:input-attrs {:name           :3xl-sample
;;                                             :defaultChecked true}})]
;;                     [radio (merge-attrs
;;                             (sx :.3xl)
;;                             {:input-attrs {:name :3xl-sample}})]]]
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
