(ns kushi.ui.input.checkbox.demo
  (:require [kushi.core :refer (sx)]
            [kushi.ui.icon.core :refer [icon]]
            [kushi.playground.component-examples :as component-examples]
            [kushi.playground.util :refer-macros [sx-call]]
            [kushi.ui.input.checkbox.core :refer [checkbox]]
            [kushi.ui.label.core :refer [label]]))


(declare checkbox-examples)

(defn demo [component-opts]
  (into [:<>]
        (for [
              ;; example-opts (take 1 checkbox-examples)
              example-opts checkbox-examples
              ;; example-opts (keep-indexed (fn [idx m] (when (contains? #{9} idx) m)) checkbox-examples)
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

(def checkbox-examples
  (let [row-attrs {:class ["playground-example-row-bounded"]}]
    [{:desc      "Showing sizes from xsmall to xxxlarge"
      :row-attrs (sx :ai--fe
                     :&_.playground-component-example-row-instance-code:ai--fe
                     ["&_label:first-child:pbe" :0.75em]
                     :&_label:pie--0.75em)
      :examples  [{:code (sx-call (into [:<>] 
                                        (for [sz switch-sizes]
                                          [:div (sx :.flex-col-fs) 
                                           [checkbox {:-label-attrs   {:class [sz]}
                                                      :defaultChecked true}]
                                           [checkbox {:-label-attrs {:class [sz]}}]])))}]}
     {:desc      "With label"
      :row-attrs row-attrs
      :examples  [{:code (sx-call [checkbox (sx :.large) [label "Sign me up"]])}]}

     {:desc      "With label and trailing icon"
      :row-attrs row-attrs
      :examples  [{:code (sx-call [checkbox (sx :.large) [label "Make it shiny" [icon :auto-awesome]]])}]}
     
     (let [weights component-examples/type-weights]
       {:desc      (str "Weight variants " (name (first weights)) " to " (name (last weights)))
        :row-attrs (sx :.flex-col-fs
                       :ai--fs
                       :gap--2rem
                       :w--fit-content
                       :border-radius--$rounded
                       :p--1em
                       :bgc--$neutral-50
                       :b--1px:solid:$neutral-100
                       :dark:bgc--$neutral-950
                       :dark:b--1px:solid:$neutral-900)
        :sx-attrs  (sx-call (sx :.large))
        :examples  (for [s weights]
                     {:label (name s)
                      :attrs {:-label-attrs {:class [s]}}
                      :args  [[label "Make it shiny" [icon :auto-awesome]]]} )})
     ]))


