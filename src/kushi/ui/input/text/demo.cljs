(ns kushi.ui.input.text.demo
  (:require
   [kushi.ui.icon.core :refer [icon]]
   [kushi.ui.button.core :refer [button]]
   [kushi.ui.input.text.core :refer [input]]
   [kushi.core :refer (sx)]
   [kushi.playground.component-examples :as component-examples]
   [kushi.playground.util :refer-macros [sx-call]]
   [kushi.ui.button.core :refer [button]]
   [reagent.dom :as rdom]))

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
  (let [row-attrs
        (sx
         [:&_.playground-component-example-row-instance-code
          {:border-radius :$rounded
           :box-shadow    :$elevated-3
           :w             :fit-content
          ;;  :bgc           :$neutral-50
          ;;  :bgc           :$neutral-50
           :p             :2em:2.5em
           :b             :1px:solid:$neutral-100}]
         [:dark:&_.playground-component-example-row-instance-code
          {:w   :fit-content
          ;;  :bgc :$neutral-950
           :p   :1em
           :b   :1px:solid:$neutral-900}])]
    [{:desc     "Simple"
      ;; :row-attrs row-attrs
      :examples [{:code (sx-call [input
                                  (sx
                                   {:placeholder "Your text here"
                                    :-label      "Input label" 
                                    :-helper     "My helper text"})]
                                 )}]}
     {:desc     "All the options"
      :examples [{:code (sx-call [input
                                  (sx
                                   {:-label-placement     :inline
                                    :-label-attrs         (sx :bgc--yellow)
                                    :placeholder          "Your text here"
                                    :disabled             false
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
                                    :required             false 
                                    :-label               "Input label"})]
                                 )}]}

     {:desc     "Inline label with helper"
      :examples [{:code (sx-call [input
                                  (sx
                                   {:placeholder      "Your text here"
                                    :-label           "Input label"
                                    :-label-placement :inline 
                                    :-helper          "Your helper text here"})])}]}

     {:desc     "With textarea element"
      :examples [{:code (sx-call [input
                                  (sx
                                   {:placeholder "Your text here"
                                    :-textarea?  true 
                                    :-label      "Input label" 
                                    :-helper     "My helper text"})])}]}
     
     ]))


