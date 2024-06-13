(ns kushi.ui.callout.demo
  (:require
   [kushi.ui.icon.core :refer [icon]]
   [kushi.ui.icon.mui.svg :as mui.svg]
   [kushi.core :refer (sx)]
   [kushi.playground.component-examples :as component-examples]
   [kushi.playground.util :refer-macros [sx-call]]
   [kushi.ui.button.core :refer [button]]
   [kushi.ui.callout.core :refer [callout]]))


(declare callout-examples)

(def callout-sizes
  [:xxsmall
   :xsmall
   :small
   :medium
   :large])

(defn demo
  [component-opts]
  (into [:<>]
        (for [
              ;; example-opts (take 5 callout-examples)
              example-opts callout-examples
              ;; example-opts (keep-indexed (fn [idx m] (when (contains? #{1} idx) m)) callout-examples)
              ]
          [component-examples/examples-section component-opts example-opts])))

(def callout-examples
  (let [row-attrs (sx :&_.playground-component-example-row-instance-code:w--100%
                      :flex-direction--column)]

    [{:desc      "Showing sizes from xxsmall to large, in positive variant"
      :row-attrs row-attrs
      :variants- [:filled :bordered]
      :examples  (for [sz callout-sizes]
                   {:label (name sz)
                    :attrs {:-header-text "Your transaction was successful."
                            :-icon        [icon :check-circle]
                            :class        [sz "positive"]}})}

     {:desc      "With icon and dismiss button"
      :row-attrs row-attrs
      :variants- [:filled :bordered]
      :examples  [{
                   :code  (sx-call [callout
                                    (sx
                                     :.positive
                                     {:-icon         [icon :info]
                                      :-header-text  "Your transaction was successful."
                                      :-close-button [button
                                                      (sx
                                                       :.pill
                                                       :.positive
                                                       :p--0.25em
                                                       {:on-click (fn [] (js/alert "Example close-icon click event."))})
                                                      [icon :clear]]})])}]}

     {:desc      "Neutral variant"
      :row-attrs row-attrs
      :examples  [{
                   :attrs  {:-header-text "Your transaction was successful."
                            :-icon        [icon :info]
                            :class        ["neutral"]}}]}

     {:desc      "Accent variant"
      :row-attrs row-attrs
      :examples  [{
                   :attrs  {:-header-text "Please check out the new features."
                            :-icon        [icon :info]
                            :class        ["accent"]}}]}

     {:desc      "Positive variant"
      :row-attrs row-attrs
      :examples  [{
                   :attrs  {:-header-text "Please check out the new features."
                            :-icon        [icon :check-circle]
                            :class        ["positive"]}}]}

     {:desc      "Warning variant"
      :row-attrs row-attrs
      :examples  [{
                   :attrs  {:-header-text "Please check out the new features."
                            :-icon        [icon :warning]
                            :class        ["warning"]}}]}


     {:desc      "Negative variant"
      :row-attrs row-attrs
      :examples  [{
                   :attrs  {:-header-text "Please check out the new features."
                            :-icon        [icon :error]
                            :class        ["negative"]}}]}


     ]))