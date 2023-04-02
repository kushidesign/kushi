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
                         {:on-click #(open-kushi-modal "my-dialog")}
                         "Click to open modal"]
                        [modal
                         (sx {:-modal-title "My modal"
                              :-description "My modal description"
                              :id           "my-dialog"})
                         [modal-close-button {:-modal-id "my-dialog"}]
                         [:div [:p "My modal content"]]]])}
   {:label   "With buttons"
    :example (example2 [:div
                        [button
                         {:on-click #(open-kushi-modal "my-dialog")}
                         "Click to open modal"]
                        [modal
                         (sx {:-modal-title "My modal"
                              :-description "My modal description"
                              :id           "my-dialog"})
                         [modal-close-button {:-modal-id "my-dialog"}]
                         [:div [:p "My modal content"]]
                         [:div (sx :.flex-row-fe :gap--1em)
                          [button (sx {:on-click #(close-kushi-modal "my-dialog")}) "Cancel"]
                          [button (sx :.filled) "Submit"]]]])}
   {:label   "Rounded, bordered panel with white backdrop"
    :example (example2 [:div
                        [button
                         {:on-click #(open-kushi-modal "my-dialog")}
                         "Click to open modal"]
                        [modal
                         (sx :border-radius--24px
                             :b--2px:solid:$gray-900
                             :$modal-backdrop-color--$white-transparent-70
                             {:-modal-title "My modal"
                              :-description "My modal description"
                              :id           "my-dialog"})
                         [modal-close-button {:-modal-id "my-dialog"}]
                         [:div [:p "My modal content"]]
                         [:div (sx :.flex-row-fe :gap--1em)
                          [button (sx :.minimal :.pill {:on-click #(close-kushi-modal "my-dialog")}) "Cancel"]
                          [button (sx :.bordered :.pill) "Submit"]]]])}
   {:label   "No drop shadow"
    :example (example2 [:div
                        [button
                         {:on-click #(open-kushi-modal "my-dialog")}
                         "Click to open modal"]
                        [modal
                         (sx :border-radius--24px
                            ;;  :b--1px:solid:$gray-500
                             :$modal-backdrop-color--$light-gray-transparent-50
                             {:-modal-title "My modal"
                              :-description "My modal description"
                              :-elevation   0
                              :id           "my-dialog"})
                         [modal-close-button {:-modal-id "my-dialog"}]
                         [:div [:p "My modal content"]]]])}
   {:label   "Animate"
    :example (example2 [:div
                        [button
                         {:on-click #(open-kushi-modal "my-dialog")}
                         "Click to open modal"]
                        [modal
                         (sx
                          [:transform '(translate :-50% "calc(-50% + 30px)")]
                          [:&.open:transform '(translate :-50% :-50%)]
                          {:-modal-title "My modal"
                           :-description "My modal description"
                           :id           "my-dialog"})
                         [modal-close-button {:-modal-id "my-dialog"}]
                         [:div [:p "My modal content"]]
                         [:div (sx :.flex-row-fe :gap--1em)
                          [button (sx {:on-click #(close-kushi-modal "my-dialog")}) "Cancel"]
                          [button (sx :.filled) "Submit"]]]])}
   {:label   "From viewport top, animated"
    :example (example2 [:div
                        [button
                         {:on-click #(open-kushi-modal "my-dialog")}
                         "Click to open modal"]
                        [modal
                         (sx
                          :.fixed-block-start
                          [:transform '(translate :-50% :-50px)]
                          [:&.open:transform '(translate :-50% :50px)]
                          {:-modal-title "My modal"
                           :-description "My modal description"
                           :id           "my-dialog"})
                         [modal-close-button {:-modal-id "my-dialog"}]
                         [:div [:p "My modal content"]]
                         [:div (sx :.flex-row-fe :gap--1em)
                          [button (sx {:on-click #(close-kushi-modal "my-dialog")}) "Cancel"]
                          [button (sx :.filled) "Submit"]]]])}

   {:label   "From viewport top, animated fast"
    :example (example2 [:div
                        [button
                         {:on-click #(open-kushi-modal "my-dialog")}
                         "Click to open modal"]
                        [modal
                         (sx
                          :.fixed-block-start
                          [:transform '(translate :-50% :-50px)]
                          [:&.open:transform '(translate :-50% :50px)]
                          :transition-duration--$xxfast
                          {:-modal-title "My modal"
                           :-description "My modal description"
                           :id           "my-dialog"})
                         [modal-close-button {:-modal-id "my-dialog"}]
                         [:div [:p "My modal content"]]
                         [:div (sx :.flex-row-fe :gap--1em)
                          [button (sx {:on-click #(close-kushi-modal "my-dialog")}) "Cancel"]
                          [button (sx :.filled) "Submit"]]]])}

   {:label   "Minimal"
    :example (example2 [:div
                        [button
                         {:on-click #(open-kushi-modal "my-dialog")}
                         "Click to open modal"]
                        [modal
                         (sx {:id "my-dialog"})
                         [:div
                          [icon (sx :.xxlarge) :emoticon]]]])}])
