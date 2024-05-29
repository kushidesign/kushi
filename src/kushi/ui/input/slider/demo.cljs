(ns kushi.ui.input.slider.demo
  (:require
   [kushi.core :refer (sx)]
   [kushi.playground.component-examples :as component-examples]
   [kushi.playground.util :refer-macros [sx-call]]
   [kushi.ui.input.slider.core :refer [slider]]))


(declare slider-examples)


(defn demo [component-opts]
  (into [:<>]
        (for [
              ;; example-opts (take 5 slider-examples)
              example-opts slider-examples
              ;; example-opts (keep-indexed (fn [idx m] (when (contains? #{1} idx) m)) slider-examples)
              ]
          [component-examples/examples-section component-opts example-opts])))


(def slider-examples
  (let [row-attrs (sx :&_.playground-component-example-row-instance-code:w--100%
                      :&_.playground-component-example-row-instance-code:max-width--500px
                      :mb--2rem:1.5rem)]
    [{:desc      "Simple"
      :row-attrs row-attrs
      :examples  [{:label "Simple"
                   :code  (sx-call [slider {:min 0
                                            :max 7}])}]}
     {:desc      "Labels"
      :row-attrs row-attrs
      :examples  [{:label "Simple"
                   :code  (sx-call [slider {:min          0
                                            :max          7
                                            :-step-marker :label}])}]}
     {:desc      "Dot markers"
      :row-attrs row-attrs
      :examples  [{:label "Simple"
                   :code  (sx-call [slider {:min          0
                                            :max          7
                                            :-step-marker :dot}])}]}
     {:desc      "Bar markers"
      :row-attrs row-attrs
      :examples  [{:label "Simple"
                   :code  (sx-call [slider {:min          0
                                            :max          7
                                            :-step-marker :bar}])}]}
     {:desc      "Fractional steps"
      :row-attrs row-attrs
      :examples  [{:label "Simple"
                   :code  (sx-call [slider {:min  0
                                            :max  1
                                            :step 0.01}])}]}
     {:desc      "Supplied step values"
      :row-attrs row-attrs
      :examples  [{:label "Simple"
                   :code  (sx-call [slider
                                    {:-steps            ["xsmall" "medium" "large" "xlarge"]
                                     :-step-marker      :label 
                                     :-label-size-class :medium}])}]}
     {:desc      "Supplied step values, custom horizontal shift for first and last values"
      :row-attrs row-attrs
      :examples  [{:label "Simple"
                   :code  (sx-call [slider
                                    {:-steps            ["First label is long"
                                                         "Second label"
                                                         "Third label"
                                                         "Last label is long"]
                                     :-step-marker      :dot
                                     :-label-size-class :small
                                     :-labels-attrs     (sx
                                                         [:&_.kushi-slider-step-label:first-child>span:translate :-25%:-50%]
                                                         [:&_.kushi-slider-step-label:last-child>span:translate :-75%:-50%])}])}]}

     






     
     ]))
