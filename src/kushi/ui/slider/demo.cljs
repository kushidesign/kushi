(ns kushi.ui.slider.demo
  (:require [kushi.core :refer (sx keyed)]
            [kushi.playground.component-examples :as component-examples]
            [kushi.playground.util :refer-macros [sx-call]]
            [kushi.ui.slider.core :refer [slider]]))


;; (defn demo [component-opts]
;;   (into [:<>]
;;         (for [
;;               ;; example-opts (take 5 slider-examples)
;;               example-opts slider-examples
;;               ;; example-opts (keep-indexed (fn [idx m] (when (contains? #{1} idx) m)) slider-examples)
;;               ]
;;           [component-examples/examples-section component-opts example-opts])))


(def examples
  (let [row-attrs       (sx :&_.instance-code:w--100%
                            :&_.instance-code:max-width--500px
                            :mb--2rem:1.5rem)
        container-attrs (sx :gtc--1fr)
        m               (keyed row-attrs container-attrs)
        f               (fn [desc code] 
                          (let [m* {:desc     desc
                                    :examples [{:label desc
                                                :code  code}]}]
                            (merge m* m)))]
    [(f "Simple"
        (sx-call [slider {:min 0
                          :max 7}]))
     (f "Labels"
        (sx-call [slider {:min          0
                          :max          7
                          :-step-marker :label}]))
     (f "Dot markers"
        (sx-call [slider {:min          0
                          :max          7
                          :-step-marker :dot}]) )
     (f "Bar markers"
        (sx-call [slider {:min          0
                          :max          7
                          :-step-marker :bar}]) )
     (f "Fractional steps"
        (sx-call [slider {:min  0
                          :max  1
                          :step 0.01}]) )
     (f "Supplied step values"
        (sx-call [slider
                  {:-steps            ["xsmall" "medium" "large" "xlarge"]
                   :-step-marker      :label 
                   :-label-size-class :medium}]) )
     
     (f "Supplied step values, custom horizontal shift for first and last values"
        (sx-call [slider
                  {:-steps            ["First label is long"
                                       "Second label"
                                       "Third label"
                                       "Last label is long"]
                   :-step-marker      :dot
                   :-label-size-class :small
                   :-labels-attrs     (sx
                                       [:&_.kushi-slider-step-label:first-child>span:translate :-25%:-50%]
                                       [:&_.kushi-slider-step-label:last-child>span:translate :-75%:-50%])}]))]))
