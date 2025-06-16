(ns ^{:kushi/layer "user-styles"} kushi.ui.tag.demo
  (:require
   [kushi.ui.icon.core :refer [icon]]
   [kushi.core :refer (sx)]
   [kushi.playground.component-examples :as component-examples]
   [kushi.playground.util :refer-macros [sx-call]]
   [kushi.ui.tag.core :refer [tag]]))


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
        (sx :.playground-tag-rows-container
            :md:gtc--max-content
            :gtc--max-content:max-content)
        playground-tag-rows-container4
        (sx :.playground-tag-rows-container4
            :md:gtc--max-content
            :gtc--max-content:max-content:max-content:max-content)
        playground-tag-rows-container24
        (sx :.playground-tag-rows-container24
            :md:gtc--max-content
            :xsm:gtc--max-content:max-content:max-content:max-content
            :gtc--max-content:max-content)]
    [(merge
      (component-examples/sizes-snippet-scale 'tag "Done")
      {:desc            "Sizes from xxsmall to xlarge"
       :row-attrs       (sx :md:ai--fe)
       :container-attrs playground-tag-rows-container24
       :examples        (for [sz sizes]
                          {:label (name sz)
                           :attrs {:class sz}
                           :args  ["Done"]})})
     
     {:desc            "Colorway variant"
      :sx-attrs        (sx-call (sx :fs--$small))
      :container-attrs playground-tag-rows-container4
      :variants+       [:minimal]
      :examples        (for [colorway component-examples/colors]
                         {:label (name colorway)
                          :args  ["Done"]
                          :attrs {:colorway colorway}})}

     #_{:desc            "Shape variants"
      :sx-attrs        (sx-call (sx :fs--$small))
      :container-attrs playground-tag-rows-container4
      :variants+       [:minimal]
      :examples        (for [s [:rounded :pill :sharp]]
                         {:label (name s)
                          :args  ["Done"]
                          :attrs {:-contour s}})}

     #_{:desc            "With icons"
      :reqs            '[[kushi.ui.icon.core :refer [icon]]]
      :sx-attrs        (sx-call (sx :fs--$small))
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

     #_{:desc            "Weight variants"
      :sx-attrs        (sx-call (sx :fs--$small))
      :container-attrs playground-tag-rows-container4
      :variants+       [:minimal]
      :examples        (for [s (rest component-examples/type-weights)]
                         {:label (name s)
                          :args  ["Pets" [icon :pets]]
                          :attrs {:class [s]}})}

     #_{:desc            "Max width"
      :reqs            '[[kushi.ui.icon.core :refer [icon]]]
      :sx-attrs        (sx-call (sx :fs--$small))
      :container-attrs (sx :gtc--max-content)
      :variants+       [:minimal]
      :examples        [{:label "Max width"
                         :args  [[:span {:class "truncate"
                                         :style {:max-width :130px}}
                                   "My tag with longer text"]]}]}]))


