(ns ^{:kushi/layer "user-styles"} kushi.ui.text-field.demo
  (:require [kushi.core :refer (sx)]
            [kushi.showcase.core :as showcase :refer [samples]]
            [kushi.ui.text-field :refer [text-field]]))


(def demos
  (let [row-attrs (sx ".ks-showcase-text-field-sample-wrapper"
                      {:padding           :1.05em:1.65em:1.25em
                       :border-width      :1px
                       :border-style      :solid
                       :border-color      :$neutral-150
                       :dark:border-color :$neutral-850
                       :width             :fit-content
                       :border-radius     :$shape-rounded})]
    [{:label     "Basic"
      :desc      "Basic"
      :row-attrs row-attrs
      :samples   (samples [[text-field
                            {:placeholder "Your text here"
                             :label-text  "Input label"
                             :helper-text "My helper text"}]])}
     {:label   "Required"
      :desc    "Required"
      :row-attrs row-attrs
      :samples (samples [[text-field
                          {:placeholder "Your text here"
                           :required    true
                           :label-text  "Input label"
                           :helper-text "My helper text"}]])}

     {:label   "Disabled"
      :desc    "Disabled"
      :row-attrs row-attrs
      :samples (samples [[text-field
                          {:placeholder "Your text here"
                           :disabled    true
                           :label-text  "Input label"
                           :helper-text "My helper text"}]])}

     {:label   "Start enhancer(text)"
      :desc    "Start enhancer(text)"
      :row-attrs row-attrs
      :samples (samples [[text-field
                          {:placeholder    "Enter a dollar amount"
                           :start-enhancer "$"
                           :label-text     "Input label"
                           :helper-text    "My helper text"}]])}

     {:label   "End enhancer (icon)"
      :desc    "End enhancer (icon)"
      :row-attrs row-attrs
      :samples (samples [[text-field
                          {:placeholder  "Enter a dollar amount"
                           :end-enhancer :star
                           :label-text   "Input label"
                           :helper-text  "My helper text"}]])}

     {:label   "Textarea"
      :desc    "Textarea"
      :row-attrs row-attrs
      :samples (samples [[text-field
                          {:placeholder    "Enter a dollar amount"
                           :required       true
                           :label-text     "Input label"
                           :helper-text    "My helper text"
                           :start-enhancer "$"
                           :end-enhancer   :star
                           :textarea?      true}]])}

     {:label   "All the options"
      :desc    "All the options"
      :samples (samples [[text-field
                          {:colorway            :accent
                           :label-attrs         (sx :bgc--$yellow-50 :dark:bgc--$yellow-900)
                           :placeholder         "Your text here"
                           :disabled            false
                           :end-enhancer        "🦄"
                           :helper-text         "Your helper text here"
                           :start-enhancer      "$"
                           :wrapper-attrs       (sx :box-shadow--4px:4px:7px:#f2baf9ab
                                                    :dark:box-shadow--4px:4px:7px:#b000c66e
                                                    {:class :my-input-wrapper-name})
                           :outer-wrapper-attrs (sx :b--1px:solid:yellow
                                                    :dark:b--1px:solid:#c419b5
                                                    :box-shadow--8px:8px:17px:#b000c66e
                                                    :dark:box-shadow--8px:8px:17px:#b000c66e
                                                    :p--1.05em:1.65em:1.25em)
                           :required            false
                           :label-text          "Input label"}]])}]))
