(ns ^{:kushi/layer "user-styles"} kushi.ui.slider.demo
  (:require
   [domo.core :as domo]
   [kushi.core :refer (sx)]
   [kushi.playground.util :refer-macros [sx-call]]
   [kushi.ui.slider.core :refer [slider]]))

(def examples
  (let [row-attrs       (sx :_.instance-code:w--100%
                            :_.instance-code:max-width--500px
                            :mb--2rem:1.5rem)
        container-attrs (sx :gtc--1fr)
        m               {:row-attrs row-attrs :container-attrs container-attrs}
        f               (fn example-map 
                          ([desc code]
                           (example-map desc code nil))
                          ([desc code snippets-map] 
                           (let [m* {:desc     desc
                                     :examples [{:label desc
                                                 :code  code}]}]
                             (merge m*
                                    m
                                    (or snippets-map
                                        {:snippets [(:quoted code)]})))))]
    [

     (f "Simple, default"
        (sx-call [slider {:min 0
                          :max 100
                          :step 1
                          }]))

     (f "Simple, label at thumb bottom"
        (sx-call [slider {:min 0
                          :max 100
                          :step 1
                          :current-value-label-position :thumb-bottom
                          }]))

     (f "Simple, label at track inline start"
        (sx-call [slider {:min 0
                          :max 100
                          :step 1
                          :current-value-label-position :track-inline-start
                          ;; :current-value-label-position :track-left
                          ;; :current-value-label-position :track-inline-end
                          ;; :current-value-label-position :track-right
                          ;; :current-value-label-position :thumb-block-start
                          ;; :current-value-label-position :thumb-top
                          ;; :current-value-label-position :thumb-block-end
                          ;; :current-value-label-position :thumb-bottom
                          }]))

     (f "Simple, label at track inline end"
        (sx-call [slider {:min 0
                          :max 100
                          :step 1
                          :current-value-label-position :track-inline-end
                          }]))

     (f "Labels"
        (sx-call [slider {:min                           0
                          :max                           7
                          :step-marker                  :label
                          ;; :current-value-label-position :thumb-bottom
                          :current-value-label-position :track-left
                          }]))

     (f "Dot markers"
        (sx-call [slider {:min          0
                          :max          7
                          :step-marker :dot}]))
     (f "Bar markers"
        (sx-call [slider {:min          0
                          :max          7
                          :step-marker :bar}]))

     (f "Markers with no current value label"
        (sx-call [slider {:min                     0
                          :max                     7
                          :step-marker            :bar
                          :display-current-value? false}]))

     (f "Fractional steps"
        (sx-call [slider {:min                            0
                          :max                            100
                          :step                           1
                          :current-value-label-display-fn #(.toFixed (/ % 100) 2)}]))

     (f "Supplied step values"
        (sx-call [slider
                  {:steps            ["xsmall" "medium" "large" "xlarge"]
                   :step-marker      :label 
                   :label-size-class :medium}]) )
     
     (f "Supplied step values, custom horizontal shift for first and last values"
        (sx-call [slider
                  {:steps            
                   ["First label is long"
                    "Second label"
                    "Third label"
                    "Last label is long"]
                   :step-marker      
                   :dot
                   :label-size-class 
                   :small
                   :labels-attrs     
                   (sx [:_.kushi-slider-step-label:first-child>span:translate
                        :-25%:-50%]
                       [:_.kushi-slider-step-label:last-child>span:translate
                        :-75%:-50%])}]))
     ]))
