(ns kushi.ui.modal.demo
  (:require [kushi.ui.icon.core :refer (icon)]
            [kushi.ui.modal.core :refer (modal modal-close-button close-kushi-modal open-kushi-modal)]
            [kushi.ui.button.core :refer [button]]
            [kushi.ui.input.text.core :refer [input]]
            [kushi.playground.util :refer-macros [sx-call]]
            [kushi.core :refer-macros (sx)]
            [kushi.playground.component-examples :as component-examples]
            ))


(declare modal-examples)

(defn demo [component-opts]
  (into [:<>]
        (for [
              ;; example-opts (take 1 modal-examples)
              example-opts modal-examples
              ;; example-opts (keep-indexed (fn [idx m] (when (contains? #{9} idx) m)) modal-examples)
              ]
          [component-examples/examples-section
           component-opts
           example-opts])))

;; code button

;; Super basic
;; form with fields
;; rest

(def modal-examples
  [{:desc      "Basic"
    :component button
    :reqs      '[[kushi.ui.button.core :refer [button]]]
    :examples  [{:label "right"
                 :args  ["Hover me"]
                 :code  (sx-call [:div
                                  [button
                                   {:on-mouse-down (fn* [] (open-kushi-modal "my-modal-basic"))}
                                   "Click to open modal"]
                                  [modal
                                   (sx
                                    :min-width--450px
                                    :&_.kushi-modal-description:fs--$small
                                    {:id "my-modal-basic"})
                                   [:div
                                    (sx :.xxxlarge :.flex-row-c)
                                    "💃"] ]])}]}
   
   {:desc      "With modal title, description, and form with fields"
    :component button
    :reqs      '[[kushi.ui.button.core :refer [button]]]
    :examples  [{:label "Basic, form with fields."
                 :args  ["Hover me"]
                 :code  (sx-call [:div
                                  [button
                                   {:on-mouse-down (fn* []
                                                        (open-kushi-modal
                                                         "my-modal-with-title-description-and-forms"))}
                                   "Click to open modal"]
                                  [modal
                                   (sx
                                    :min-width--450px
                                    :&_.kushi-modal-description:fs--$small
                                    {:-modal-title "Example modal"
                                     :-description "Example modal description goes here."
                                     :id           "my-modal-with-title-description-and-forms"})
                                   [:div
                                    (sx :.flex-col-fs :gap--1em)
                                    [input (sx {:placeholder "Puffy"
                                                :-label      "Screen name"})]
                                    [input (sx {:placeholder "Executive"
                                                :-label      "Occupation"})]]
                                   [:div
                                    (sx :.flex-row-fe :gap--1em)
                                    [button {:on-mouse-down close-kushi-modal} "Cancel"]
                                    [button (sx :.filled) "Submit"]]]])}]}
   
   {:desc      "Animated"
    :component button
    :reqs      '[[kushi.ui.button.core :refer [button]]]
    :examples  [{:label "Basic, form with fields."
                 :args  ["Hover me"]
                 :code  (sx-call
                         [:div
                          [button
                           {:on-mouse-down (fn* []
                                                (open-kushi-modal
                                                 "my-modal-with-title-description-and-forms-animated"))}
                           "Click to open modal"]
                          [modal
                           (sx
                            [:translate "-50% calc(-50% + 30px)"]
                            [:&.kushi-modal-open:translate "-50% -50%"]
                            {:-modal-title "Example modal"
                             :-description "Example modal description goes here."
                             :id           "my-modal-with-title-description-and-forms-animated"})
                           [:div
                            (sx :.flex-col-fs :gap--1em)
                            [input (sx {:placeholder "Puffy"
                                        :-label      "Screen name"})]
                            [input (sx {:placeholder "Executive"
                                        :-label      "Occupation"})]]
                           [:div
                            (sx :.flex-row-fe :gap--1em)
                            [button {:on-click close-kushi-modal} "Cancel"]
                            [button (sx :.filled) "Submit"]]]])}]}

   {:desc      "Animated from top"
    :component button
    :reqs      '[[kushi.ui.button.core :refer [button]]]
    :examples  [{:label "Basic, form with fields."
                 :args  ["Hover me"]
                 :code  (sx-call
                         [:div
                          [button
                           {:on-click (fn* [] (open-kushi-modal "With modal title, description, and form with fields, animated from top."))}
                           "Click to open modal"]
                          [modal
                           (sx
                            :.fixed-block-start-inside
                            [:translate "-50% -50px"]
                            [:&.kushi-modal-open:translate "-50% 50px"]
                            {:-modal-title "Example modal"
                             :-description "Example modal description goes here."
                             :id           "With modal title, description, and form with fields, animated from top."})
                           [:div
                            (sx :.flex-col-fs :gap--1em)
                            [input (sx {:placeholder "Puffy"
                                        :-label      "Screen name"})]
                            [input (sx {:placeholder "Executive"
                                        :-label      "Occupation"})]]
                           [:div
                            (sx :.flex-row-fe :gap--1em)
                            [button {:on-click close-kushi-modal} "Cancel"]
                            [button (sx :.filled) "Submit"]]]])}]}])
