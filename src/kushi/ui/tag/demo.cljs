(ns kushi.ui.tag.demo
  (:require
   [kushi.ui.icon.core :refer [icon]]
   [kushi.ui.text-field.core :refer [text-field]]
   [kushi.core :refer (sx)]
   [kushi.playground.component-examples :as component-examples]
   [kushi.playground.util :refer-macros [sx-call]]
   [kushi.ui.button.core :refer [button]]
   [kushi.ui.tag.core :refer [tag]]
   [reagent.dom :as rdom]))


;; TODO remove section-label
;; TODO hoist reqs up to a higher level
(def sizes
  [:xxsmall
   :xsmall
   :small
   :medium
   :large
   :xlarge])

(def examples
  (let [playground-tag-rows-container
        (sx 'playground-tag-rows-container
            :md:gtc--max-content
            :gtc--max-content:max-content)
        playground-tag-rows-container4
        (sx 'playground-tag-rows-container4
            :md:gtc--max-content
            :gtc--max-content:max-content:max-content:max-content)
        playground-tag-rows-container24
        (sx 'playground-tag-rows-container24
            :md:gtc--max-content
            :xsm:gtc--max-content:max-content:max-content:max-content
            :gtc--max-content:max-content)]
    [{:desc            "Sizes from xxsmall to xlarge"
      :row-attrs       (sx :md:ai--fe)
      :container-attrs playground-tag-rows-container24
      :examples        (for [sz sizes]
                         {:label (name sz)
                          :attrs {:class sz}
                          :args  ["Play"]})}
     
     {:desc            "Semantic variants"
      :sx-attrs        (sx-call (sx :.small))
      :container-attrs playground-tag-rows-container4
      :variants+       [:minimal]
      :examples        (let [semantics #{"neutral" "accent" "positive" "warning" "negative"}]
                         (for [s component-examples/colors]
                           {:label (name s)
                            :args  ["Play"]
                            :attrs {:class [s]}}))}

     {:desc            "Shape variants"
      :sx-attrs        (sx-call (sx :.small))
      :container-attrs playground-tag-rows-container4
      :variants+       [:minimal]
      :examples        (for [s [:rounded :pill :sharp]]
                         {:label (name s)
                          :args  ["Play"]
                          :attrs {:class [s]}})}

     {:desc            "With icons"
      :reqs            '[[kushi.ui.icon.core :refer [icon]]]
      :sx-attrs        (sx-call (sx :.small))
      :container-attrs playground-tag-rows-container4
      :variants+       [:minimal]
      :examples        [{:label "Icon tag"
                         :args  [[icon :favorite]]}
                        {:label "Icon tag"
                         :args  [[icon :star]]}
                        {:label "Icon tag"
                         :args  [[icon :pets]]}
                        {:label "Leading icon"
                         :args  [[icon :pets] "Pets"]}]}

     {:desc            "Weight variants"
      :sx-attrs        (sx-call (sx :.small))
      :container-attrs playground-tag-rows-container4
      :variants+       [:minimal]
      :examples        (for [s (rest component-examples/type-weights)]
                         {:label (name s)
                          :args  ["Pets" [icon :pets]]
                          :attrs {:class [s]}})}

     {:desc            "Max width"
      :reqs            '[[kushi.ui.icon.core :refer [icon]]]
      :sx-attrs        (sx-call (sx :.small))
      :container-attrs playground-tag-rows-container4
      :variants+       [:minimal]
      :examples        [{:label "Max width"
                         :args  [[:span {:class "truncate" :style {:max-width :130px}}
                                   "My tag with longer text"]]}]}]))


