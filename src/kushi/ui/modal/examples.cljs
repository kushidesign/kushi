(ns kushi.ui.modal.examples
  (:require [kushi.ui.icon.core :refer (icon)]
            [kushi.ui.modal.core :refer (modal modal-close-button close-kushi-modal open-kushi-modal)]
            [kushi.ui.button.core :refer [button]]
            [kushi.playground.util :refer-macros (example2)]
            [kushi.core :refer-macros (sx)]))

(def modal-examples
  [
   {:label   "With title and description"
    :example (example2 [:div
                        [button
                         {:on-click #(open-kushi-modal "my-modal")}
                         "Click to open modal"]
                        [modal
                         {:-modal-title "My modal"
                          :-description "My modal description"
                          :id           "my-modal"}
                         [modal-close-button {:-modal-id "my-modal"}]
                         [:div [:p "My modal content"]]]])}
   {:label   "With buttons"
    :example (example2 [:div
                        [button
                         {:on-click #(open-kushi-modal "my-modal")}
                         "Click to open modal"]
                        [modal
                         {:-modal-title "My modal"
                          :-description "My modal description"
                          :id           "my-modal"}
                         [modal-close-button {:-modal-id "my-modal"}]
                         [:div [:p "My modal content"]]
                         [:div (sx :.flex-row-fe :gap--1em)
                          [button {:on-click close-kushi-modal} "Cancel"]
                          [button (sx :.filled) "Submit"]]]])}
   {:label   "Rounded, bordered panel with white backdrop"
    :example (example2 [:div
                        [button
                         {:on-click #(open-kushi-modal "my-modal")}
                         "Click to open modal"]
                        [modal
                         (sx :border-radius--24px
                             :b--2px:solid:$gray-900
                             :$modal-backdrop-color--$white-transparent-70
                             {:-modal-title "My modal"
                              :-description "My modal description"
                              :id           "my-modal"})
                         [modal-close-button {:-modal-id "my-modal"}]
                         [:div [:p "My modal content"]]
                         [:div (sx :.flex-row-fe :gap--1em)
                          [button (sx :.minimal :.pill {:on-click close-kushi-modal}) "Cancel"]
                          [button (sx :.bordered :.pill) "Submit"]]]])}
   {:label   "No drop shadow"
    :example (example2 [:div
                        [button
                         {:on-click #(open-kushi-modal "my-modal")}
                         "Click to open modal"]
                        [modal
                         (sx :border-radius--24px
                             :$modal-backdrop-color--$light-gray-transparent-50
                             {:-modal-title "My modal"
                              :-description "My modal description"
                              :-elevation   0
                              :id           "my-modal"})
                         [modal-close-button {:-modal-id "my-modal"}]
                         [:div [:p "My modal content"]]]])}
   {:label   "Animate"
    :example (example2 [:div
                        [button
                         {:on-click #(open-kushi-modal "my-modal")}
                         "Click to open modal"]
                        [modal
                         (sx [:translate "-50% calc(-50% + 30px)"]
                             [:&.kushi-modal-open:translate "-50% -50%"]
                             {:-modal-title "My modal"
                              :-description "My modal description"
                              :id           "my-modal"})
                         [modal-close-button {:-modal-id "my-modal"}]
                         [:div [:p "My modal content"]]
                         [:div (sx :.flex-row-fe :gap--1em)
                          [button {:on-click close-kushi-modal} "Cancel"]
                          [button (sx :.filled) "Submit"]]]])}
   {:label   "From viewport top, animated"
    :example (example2 [:div
                        [button
                         {:on-click #(open-kushi-modal "my-modal")}
                         "Click to open modal"]
                        [modal
                         (sx
                          :.fixed-block-start-inside
                          [:translate "-50% -50px"]
                          [:&.kushi-modal-open:translate "-50% 50px"]
                          {:-modal-title "My modal"
                           :-description "My modal description"
                           :id           "my-modal"})
                         [modal-close-button {:-modal-id "my-modal"}]
                         [:div [:p "My modal content"]]
                         [:div (sx :.flex-row-fe :gap--1em)
                          [button {:on-click close-kushi-modal} "Cancel"]
                          [button (sx :.filled) "Submit"]]]])}
   {:label   "Minimal"
    :example (example2 [:div
                        [button
                         {:on-click #(open-kushi-modal "my-modal")}
                         "Click to open modal"]
                        [modal
                         {:id "my-modal"}
                         [:div
                          [icon (sx :.xxlarge) :emoticon]]]])}])
