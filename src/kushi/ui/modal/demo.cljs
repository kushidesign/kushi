(ns kushi.ui.modal.demo
  (:require [kushi.ui.icon.core :refer (icon)]
            [kushi.ui.modal.core :refer (modal modal-close-button close-kushi-modal open-kushi-modal)]
            [kushi.ui.button.core :refer [button]]
            [kushi.ui.text-field.core :refer [text-field]]
            [kushi.playground.util :refer-macros [sx-call]]
            [kushi.core :refer-macros (sx)]
            [kushi.playground.component-examples :as component-examples]
            ))

(def examples
  (let [row-attrs (sx :&_.kushi-button:fs--$small)]
    [(let [code (sx-call (let [id "my-modal-basic"]
                           [:div
                            [button
                             {:on-click (fn* [] (open-kushi-modal id))}
                             "Click to open modal"]
                            [modal
                             (sx :min-width--300px
                                 :&_.kushi-modal-description:fs--$small
                                 {:id id})
                             [:div
                              (sx :.xxxlarge :.flex-row-c)
                              "💃🏽"] ]]))] 
       {:desc      "Basic"
        :component button
        :reqs      '[[kushi.ui.button.core :refer [button]]]
        :row-attrs (sx :&_.kushi-button:fs--$small)
        :snippets  [(:quoted code)]
        :examples  [{:label "right"
                     :args  ["Hover me"]
                     :code  code}]})
     
     {:desc      "With modal title, description, form with fields, and close button"
      :component button
      :reqs      '[[kushi.ui.button.core :refer [button]]]
      :row-attrs row-attrs
      :examples  [{:label "Basic, form with fields."
                   :args  ["Hover me"]
                   :code  (sx-call (let [id "my-modal-with-title-description-and-forms"]
                                     [:div
                                      [button
                                       {:on-click (fn* [] (open-kushi-modal id))}
                                       "Click to open modal"]
                                      [modal
                                       (sx
                                        :min-width--300px
                                        :&_.kushi-modal-description:fs--$small
                                        {:-modal-title "Example modal"
                                         :-description "Example modal description goes here."
                                         :id           id})
                                       [modal-close-button {:-modal-id id}]
                                       [:div
                                        (sx :.flex-col-fs :gap--1em)
                                        [text-field (sx {:placeholder "Puffy"
                                                         :-label      "Screen name"})]
                                        [text-field (sx {:placeholder "Executive"
                                                         :-label      "Occupation"})]]
                                       [:div
                                        (sx :.flex-row-fe :gap--1em)
                                        [button {:on-click close-kushi-modal} "Cancel"]
                                        [button (sx :.filled) "Submit"]]]]))}]}
     
     {:desc      "Animated"
      :component button
      :reqs      '[[kushi.ui.button.core :refer [button]]]
      :row-attrs row-attrs
      :examples  [{:label "Basic, form with fields."
                   :args  ["Hover me"]
                   :code  (let [id "my-modal-with-title-description-and-forms-animated"]
                            (sx-call
                             [:div
                              [button
                               {:on-click (fn* [] (open-kushi-modal id))}
                               "Click to open modal"]
                              [modal
                               (sx
                                :min-width--300px
                                [:translate "-50% calc(-50% + 30px)"]
                                [:&.kushi-modal-open:translate "-50% -50%"]
                                {:-modal-title "Example modal"
                                 :-description "Example modal description goes here."
                                 :id           id})
                               [modal-close-button {:-modal-id id}]
                               [:div
                                (sx :.flex-col-fs :gap--1em)
                                [text-field (sx {:placeholder "Puffy"
                                                 :-label      "Screen name"})]
                                [text-field (sx {:placeholder "Executive"
                                                 :-label      "Occupation"})]]
                               [:div
                                (sx :.flex-row-fe :gap--1em)
                                [button {:on-click close-kushi-modal} "Cancel"]
                                [button (sx :.filled) "Submit"]]]]))}]}

     {:desc      "Animated from top"
      :component button
      :reqs      '[[kushi.ui.button.core :refer [button]]]
      :row-attrs row-attrs
      :examples  [{:label "Basic, form with fields."
                   :args  ["Hover me"]
                   :code  (sx-call
                           (let [id  "With modal title, description, and form with fields, animated from top."]
                             [:div
                              [button
                               {:on-click (fn* [] (open-kushi-modal id))}
                               "Click to open modal"]
                              [modal
                               (sx
                                :.fixed-block-start-inside
                                :min-width--300px
                                [:translate "-50% -50px"]
                                [:&.kushi-modal-open:translate "-50% 50px"]
                                {:-modal-title "Example modal"
                                 :-description "Example modal description goes here."
                                 :id           id})
                               [modal-close-button {:-modal-id id}]
                               [:div
                                (sx :.flex-col-fs :gap--1em)
                                [text-field (sx {:placeholder "Puffy"
                                                 :-label      "Screen name"})]
                                [text-field (sx {:placeholder "Executive"
                                                 :-label      "Occupation"})]]
                               [:div
                                (sx :.flex-row-fe :gap--1em)
                                [button {:on-click close-kushi-modal} "Cancel"]
                                [button (sx :.filled) "Submit"]]]]))}]}
     
     {:desc      "Rounded, with white backdrop"
      :component button
      :reqs      '[[kushi.ui.button.core :refer [button]]]
      :row-attrs row-attrs
      :examples  [{:label "Basic, form with fields."
                   :args  ["Hover me"]
                   :code  (sx-call
                           (let [id "Rounded, with white backdrop"]
                             [:div
                              [button
                               {:on-click (fn* [] (open-kushi-modal id))}
                               "Click to open modal"]
                              [modal
                               (sx
                                :min-width--300px
                                :border-radius--24px
                                :b--2px:solid:$gray-900
                                :$modal-backdrop-color--$white-transparent-70
                                :&_.kushi-modal-description:fs--$small
                                {:-modal-title "Example modal"
                                 :-description "Example modal description goes here."
                                 :id           id})
                               [:div
                                (sx :.flex-col-fs :gap--1em)
                                [text-field (sx {:placeholder "Puffy"
                                                 :-label      "Screen name"})]
                                [text-field (sx {:placeholder "Executive"
                                                 :-label      "Occupation"})]]
                               [:div
                                (sx :.flex-row-fe :gap--1em)
                                [button
                                 (sx :.minimal :.pill {:on-click close-kushi-modal})
                                 "Cancel"]
                                [button
                                 (sx :.filled :.pill {:on-click close-kushi-modal}) 
                                 "Submit"]]]]
                             ))}]}
  {:desc      "No drop shadow"
      :component button
      :reqs      '[[kushi.ui.button.core :refer [button]]]
      :row-attrs row-attrs
      :examples  [{:label "Basic, form with fields."
                   :args  ["Hover me"]
                   :code  (sx-call
                           (let [id "No drop shadow"]
                             [:div
                              [button
                               {:on-click (fn* [] (open-kushi-modal id))}
                               "Click to open modal"]
                              [modal
                               (sx
                                :min-width--300px
                                :border-radius--24px
                                :$modal-backdrop-color--$light-gray-transparent-50
                                {:-modal-title "Example modal"
                                 :-description "Example modal description goes here."
                                 :-elevation   0
                                 :id           id})
                               [:div
                                (sx :.flex-col-fs :gap--1em)
                                [text-field (sx {:placeholder "Puffy"
                                                 :-label      "Screen name"})]
                                [text-field (sx {:placeholder "Executive"
                                                 :-label      "Occupation"})]]
                               [:div
                                (sx :.flex-row-fe :gap--1em)
                                [button
                                 (sx :.minimal :.pill {:on-click close-kushi-modal})
                                 "Cancel"]
                                [button
                                 (sx :.filled :.pill {:on-click close-kushi-modal}) 
                                 "Submit"]]]]))}]}]))
