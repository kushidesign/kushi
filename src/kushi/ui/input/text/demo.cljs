(ns kushi.ui.input.text.demo
  (:require [kushi.core :refer (sx merge-attrs)]
            [kushi.playground.component-examples :as component-examples]
            [kushi.playground.util :refer-macros [sx-call]]
            [kushi.ui.icon.core :refer [icon]]
            [kushi.ui.input.text.core :refer [text-field]]))

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

(def input-sizes
  [:xsmall
   :small
   :medium
   :large
   :xlarge
   :xxlarge
   :xxxlarge])


(def checkbox-examples
  (let [row-attrs (merge-attrs {:class ["playground-example-row-bounded"]}
                               (sx [:&_.playground-component-example-row-instance-code 
                                    {:p                               :1.05em:1.65em:1.25em
                                     :&_.kushi-text-input-wrapper:bgc :$body-background-color}]))]
    [{:desc      "Simple"
      :row-attrs row-attrs
      :examples  [{:code (sx-call [text-field
                                   (sx
                                    {:placeholder "Your text here"
                                     :-label      "Input label" 
                                     :-helper     "My helper text"})]
                                  )}]}

     {:desc      "Required"
      :row-attrs row-attrs
      :examples  [{:code (sx-call [text-field
                                   (sx
                                    {:placeholder "Your text here"
                                     :required    true
                                     :-label      "Input label" 
                                     :-helper     "My helper text"})]
                                  )}]}

     {:desc      "Disabled"
      :row-attrs row-attrs
      :examples  [{:code (sx-call [text-field
                                   (sx
                                    {:placeholder "Your text here"
                                     :disabled    true
                                     :-label      "Input label" 
                                     :-helper     "My helper text"})])}]}

     {:desc      "With helper"
      :row-attrs row-attrs
      :examples  [{:code (sx-call [text-field
                                   (sx
                                    {:placeholder "Your text here"
                                     :-label      "Input label" 
                                     :-helper     "Your helper text here"})])}]}

     {:desc      "With start enhancer"
      :row-attrs row-attrs
      :examples  [{:code (sx-call [text-field
                                   (sx
                                    {:placeholder     "Your text here"
                                     :-start-enhancer "$"
                                     :-label          "Input label"})])}]}

     {:desc      "With end enhancer (icon)"
      :row-attrs row-attrs
      :examples  [{:code (sx-call [text-field
                                   (sx
                                    {:placeholder     "Your text here"
                                     :-end-enhancer   [icon :star]
                                     :-label          "Input label"})])}]}

     {:desc      "With textarea element"
      :row-attrs row-attrs
      :examples  [{:code (sx-call [text-field
                                   (sx
                                    {:placeholder "Your text here"
                                     :-textarea?  true 
                                     :-label      "Input label" 
                                     :-helper     "My helper text"})])}]}

     {:desc      "All the options"
      :row-attrs (merge-attrs row-attrs
                              (sx :max-width--280px
                                  :xsm:max-width--unset))
      :examples  [{:code (sx-call [text-field
                                   (sx
                                    {:required             false
                                     :placeholder          "Your text here"
                                     :disabled             false 
                                     :-label-attrs         (sx :bgc--yellow)
                                     :-semantic            :accent
                                     :-end-enhancer        "🦄"
                                     :-helper              "Your helper text here"
                                     :-start-enhancer      "$"
                                     :-wrapper-attrs       (sx
                                                            :box-shadow--4px:4px:7px:#f2baf9ab
                                                            {:class :my-input-wrapper-name})
                                     :-outer-wrapper-attrs (sx
                                                            :b--1px:solid:yellow
                                                            :box-shadow--8px:8px:17px:#f2baf9ab
                                                            :p--1em) 
                                     :-label               "Input label"})]
                                  )}]}
     ]))


