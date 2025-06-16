(ns ^{:kushi/layer "user-styles"} kushi.ui.text-field.demo
  (:require [kushi.core :refer (sx merge-attrs)]
            [kushi.playground.util :refer-macros [sx-call]]
            [kushi.ui.icon.core :refer [icon]]
            [kushi.ui.text-field.core :refer [text-field]]))


(def examples
  (let [row-attrs (merge-attrs {:class ["playground-example-row-bounded"]}
                               (sx :_.instance-code:p--1.05em:1.65em:1.25em))
        f         (fn [desc code]
                    {:desc      desc
                     :row-attrs row-attrs
                     :snippets  [(:quoted code)]
                     :examples  [{:code code}]})]

    [
     (f "Simple"
        (sx-call [text-field
                  {:placeholder "Your text here"
                   :-label      "Input label" 
                   :-helper     "My helper text"}]))

     #_{:desc      "Simple"
        :row-attrs row-attrs
        :examples  [{:code (sx-call [text-field
                                     (sx
                                      {:placeholder "Your text here"
                                       :-label      "Input label" 
                                       :-helper     "My helper text"})]
                                    )}]}

     (f "Required"
        (sx-call [text-field
                  {:placeholder "Your text here"
                   :required    true
                   :-label      "Input label" 
                   :-helper     "My helper text"}]))

     (f "Disabled"
        (sx-call [text-field
                  {:placeholder "Your text here"
                   :disabled    true
                   :-label      "Input label" 
                   :-helper     "My helper text"}]) )

     (f "With helper"
        (sx-call [text-field
                  {:placeholder "Your text here"
                   :-label      "Input label" 
                   :-helper     "Your helper text here"}]) )

     (f "With start enhancer"
        (sx-call [text-field {:placeholder     "Your text here"
                              :-start-enhancer "$"
                              :-label          "Input label"}]) )

     (f "With end enhancer (icon)"
        (sx-call [text-field
                  {:placeholder   "Your text here"
                   :-end-enhancer [icon :star]
                   :-label        "Input label"}]) )

     (f "With textarea element"
        (sx-call [text-field
                  {:placeholder "Your text here"
                   :-textarea?  true 
                   :-label      "Input label" 
                   :-helper     "My helper text"}]) )

     (let [code (sx-call [text-field
                          {:required             false
                           :placeholder          "Your text here"
                           :disabled             false 
                           :-label-attrs         (sx :bgc--$yellow-50
                                                     :dark:bgc--$yellow-900)
                           :colorway            :accent
                           :-end-enhancer        "🦄"
                           :-helper              "Your helper text here"
                           :-start-enhancer      "$"
                           :-wrapper-attrs       (sx
                                                  :box-shadow--4px:4px:7px:#f2baf9ab
                                                  :dark:box-shadow--4px:4px:7px:#b000c66e
                                                  {:class :my-input-wrapper-name})
                           :-outer-wrapper-attrs (sx
                                                  :b--1px:solid:yellow
                                                  :dark:b--1px:solid:#c419b5
                                                  :box-shadow--8px:8px:17px:#b000c66e
                                                  :dark:box-shadow--8px:8px:17px:#b000c66e
                                                  :p--1em) 
                           :-label               "Input label"}])] 
       {:desc      "All the options"
        :row-attrs (merge-attrs row-attrs
                                (sx :max-width--280px
                                    :xsm:max-width--unset))
        :snippets [(:quoted code)]
        :examples  [{:code code}]})]))


