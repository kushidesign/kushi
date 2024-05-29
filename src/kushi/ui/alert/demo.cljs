(ns kushi.ui.alert.demo
  (:require
   [kushi.ui.icon.core :refer [icon]]
   [kushi.ui.icon.mui.svg :as mui.svg]
   [kushi.core :refer (sx)]
   [kushi.playground.component-examples :as component-examples]
   [kushi.playground.util :refer-macros [sx-call]]
   [kushi.ui.button.core :refer [button]]
   [kushi.ui.alert.core :refer [alert]]))


(declare alert-examples)

(def alert-sizes
  [:xxsmall
   :xsmall
   :small
   :medium
   :large
   :xlarge])

(defn demo
  [component-opts]
  (into [:<>]
        (for [
              ;; example-opts (take 5 alert-examples)
              example-opts alert-examples
              ;; example-opts (keep-indexed (fn [idx m] (when (contains? #{1} idx) m)) alert-examples)
              ]
          [component-examples/examples-section component-opts example-opts])))

(def alert-examples
  (let [row-attrs (sx :&_.playground-component-example-row-instance-code:w--100%
                      :flex-direction--column )]

    [{:desc      "Showing sizes from xxsmall to xlarge, in positive variant"
      :row-attrs row-attrs
      :variants- [:filled :bordered]
      :examples  (for [sz alert-sizes]
                   {:label (name sz)
                    :attrs {:-header-text "Your transaction was successful."
                            :-icon        [icon :check-circle]
                            :class        [sz "positive"]}})}

     {:desc      "With icon and dismiss button"
      :row-attrs row-attrs
      :variants- [:filled :bordered]
      :examples  [{:label "With icon and dismiss button"
                   :code  (sx-call [alert
                                    (sx
                                     :.positive
                                     {:-icon         [icon :info]
                                      :-header-text  "Alert header text goes here."
                                      :-close-button [button
                                                      (sx
                                                       :.pill
                                                       :.positive
                                                       :p--0.25em
                                                       {:on-click (fn [] (js/alert "Example close-icon click event."))})
                                                      [icon :clear]]})])}]}

     {:desc      "Neutral variant"
      :row-attrs row-attrs
      :examples  [{:label "neutral"
                   :attrs  {:-header-text "Your transaction was successful."
                            :-icon        [icon :info]
                            :class        ["neutral"]}}]}

     {:desc      "Accent variant"
      :row-attrs row-attrs
      :examples  [{:label "accent"
                   :attrs  {:-header-text "Please check out the new features."
                            :-icon        [icon :info]
                            :class        ["accent"]}}]}

     {:desc      "Positive variant"
      :row-attrs row-attrs
      :examples  [{:label "positive"
                   :attrs  {:-header-text "Please check out the new features."
                            :-icon        [icon :check-circle]
                            :class        ["positive"]}}]}

     {:desc      "Warning variant"
      :row-attrs row-attrs
      :examples  [{:label "warning"
                   :attrs  {:-header-text "Please check out the new features."
                            :-icon        [icon :warning]
                            :class        ["warning"]}}]}


     {:desc      "Negative variant"
      :row-attrs row-attrs
      :examples  [{:label "negative"
                   :attrs  {:-header-text "Please check out the new features."
                            :-icon        [icon :error]
                            :class        ["negative"]}}]}


     ]))
