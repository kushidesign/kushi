(ns kushi.ui.tag.demo
  (:require
   [kushi.ui.icon.core :refer [icon]]
   [kushi.ui.input.text.core :refer [input]]
   [kushi.core :refer (sx)]
   [kushi.playground.component-examples :as component-examples]
   [kushi.playground.util :refer-macros [sx-call]]
   [kushi.ui.button.core :refer [button]]
   [kushi.ui.tag.core :refer [tag]]
   [reagent.dom :as rdom]))

(declare tag-examples)


(defn demo2 [component-opts]
  (into [:<>]
        (for [
              example-opts (take 5 tag-examples)
              ;; example-opts tag-examples
              ;; example-opts (keep-indexed (fn [idx m] (when (contains? #{1} idx) m)) tag-examples)
              ]
          [component-examples/examples-section component-opts example-opts])))


;; TODO remove section-label
;; TODO hoist reqs up to a higher level
(def tag-sizes
  [:xxsmall
   :xsmall
   :small
   :medium
   :large
   :xlarge])

(def tag-examples
  [{:desc      "Sizes from xxsmall to xlarge"
    :row-attrs (sx :ai--fe)
    :examples  (for [sz tag-sizes]
                 {:label (name sz)
                  :attrs {:class sz}
                  :args  ["Play"]})}
   
   {:desc      "Semantic variants"
    :sx-attrs  (sx-call (sx :.small))
    :variants+ [:minimal]
    :examples  (let [semantics #{"neutral" "accent" "positive" "warning" "negative"}]
                 (for [s component-examples/colors]
                   {:label (name s)
                    :args  ["Play"]
                    :attrs {:class [s]}}))}

   {:desc      "Shape variants"
    :sx-attrs  (sx-call (sx :.small))
    :variants+ [:minimal]
    :examples  (for [s [:rounded :pill :sharp]]
                 {:label (name s)
                  :args  ["Play"]
                  :attrs {:class [s]}})}

   {:desc     "With icons"
    :reqs     '[[kushi.ui.icon.core :refer [icon]]]
    :sx-attrs (sx-call (sx :.small))
    :variants+ [:minimal]
    :examples [{:label "Icon tag"
                :args  [[icon :favorite]]}
               {:label "Icon tag"
                :args  [[icon :star]]}
               {:label "Icon tag"
                :args  [[icon :pets]]}
               {:label "Leading icon"
                :args  [[icon :pets] "Play"]}
               {:label "Trailing icon"
                :args  [[icon :pets] "Pet friendly"]} ]}

   {:desc     "Weight variants"
    :sx-attrs (sx-call (sx :.small))
    :variants+ [:minimal]
    :examples (for [s (rest component-examples/type-weights)]
                {:label (name s)
                 :args  ["Pets" [icon :pets]]
                 :attrs {:class [s]}})}])


