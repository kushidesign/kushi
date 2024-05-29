(ns kushi.ui.modal.examples
  (:require [kushi.ui.icon.core :refer (icon)]
            [kushi.ui.modal.core :refer (modal modal-close-button close-kushi-modal open-kushi-modal)]
            [kushi.ui.button.core :refer [button]]
            [kushi.ui.input.text.core :refer [text-field]]
            [kushi.playground.util :refer-macros (example2)]
            [kushi.core :refer-macros (sx)]))
[:div
    (sx 'my-form
        :.flex-col-fs
        :gap--1em
        :&_.kushi-text-input-label:min-width--7em
        :&_.kushi-input-inline:gtc--36%:64%)
    [:h2 (sx 'my-form-header
             :.medium
             :.semi-bold
             :mbe--0.75em)
     "Example Popover Form"]
    [text-field
     (sx
      {:placeholder      "100%"
       :-label           "Height"
       :-label-placement :inline})]
    [text-field
     (sx
      {:placeholder      "335px"
       :-label           "Min Width"
       :-label-placement :inline})]
    [text-field
     (sx
      {:placeholder      "75px"
       :-label           "Depth"
       :-label-placement :inline})]]

(def modal-examples
  [
   {:label   "With title and description"
    :example (example2 [:div
                        [button
                         {:on-click #(open-kushi-modal "my-modal")}
                         "Click to open modal"]
                        [modal
                         (sx 
                          :min-width--450px
                          :&_.kushi-modal-description:fs--$small
                          {:-modal-title "Example modal"
                           :-description "Example modal description goes here."
                           :id           "my-modal"})
                         [:div 
                          (sx :.flex-col-fs
                              :gap--1em)
                          [text-field
                           (sx
                            {:placeholder "Puffy"
                             :-label      "Screen name"})]
                          [text-field
                           (sx
                            {:placeholder "Executive"
                             :-label      "Occupation"})]]
                         [:div (sx :.flex-row-fe :gap--1em)
                          [button {:on-click close-kushi-modal} "Cancel"]
                          [button (sx :.filled) "Submit"]]]])}
   {:label   "With additional close icon button in corner"
    :example (example2 [:div
                        [button
                         {:on-click #(open-kushi-modal "my-modal")}
                         "Click to open modal"]
                        [modal
                         (sx 
                          :&_.kushi-modal-description:fs--$small
                          {:-modal-title "Example modal"
                           :-description "Example modal description goes here."
                           :id           "my-modal"})
                         [modal-close-button {:-modal-id "my-modal"}]
                         [:div 
                          (sx :.flex-col-fs
                              :gap--1em)
                          [text-field
                           (sx
                            {:placeholder "Puffy"
                             :-label      "Screen name"})]
                          [text-field
                           (sx
                            {:placeholder "Executive"
                             :-label      "Occupation"})]]
                         [:div (sx :.flex-row-fe :gap--1em)
                          [button {:on-click close-kushi-modal} "Cancel"]
                          [button (sx :.filled {:on-click close-kushi-modal}) "Submit"]]]])}
   {:label   "Rounded, bordered panel with white backdrop"
    :example (example2 [:div
                        [button
                         {:on-click #(open-kushi-modal "my-modal")}
                         "Click to open modal"]
                        [modal
                         (sx :border-radius--24px
                             :b--2px:solid:$gray-900
                             :$modal-backdrop-color--$white-transparent-70
                             :&_.kushi-modal-description:fs--$small
                             {:-modal-title "Example modal"
                              :-description "Example modal description goes here."
                              :id           "my-modal"})
                         [:div 
                          (sx :.flex-col-fs
                              :gap--1em)
                          [text-field
                           (sx
                            {:placeholder "Puffy"
                             :-label      "Screen name"})]
                          [text-field
                           (sx
                            {:placeholder "Executive"
                             :-label      "Occupation"})]]
                         [:div (sx :.flex-row-fe :gap--1em)
                          [button (sx :.minimal :.pill {:on-click close-kushi-modal}) "Cancel"]
                          [button (sx :.filled :.pill {:on-click close-kushi-modal}) "Submit"]]
                         ]])}
   {:label   "No drop shadow"
    :example (example2 [:div
                        [button
                         {:on-click #(open-kushi-modal "my-modal")}
                         "Click to open modal"]
                        [modal
                         (sx :border-radius--24px
                             :$modal-backdrop-color--$light-gray-transparent-50
                             {:-modal-title "Example modal"
                              :-description "Example modal description goes here."
                              :-elevation   0
                              :id           "my-modal"})
                         [:div 
                          (sx :.flex-col-fs
                              :gap--1em)
                          [text-field
                           (sx
                            {:placeholder "Puffy"
                             :-label      "Screen name"})]
                          [text-field
                           (sx
                            {:placeholder "Executive"
                             :-label      "Occupation"})]]
                         [:div (sx :.flex-row-fe :gap--1em)
                          [button (sx :.minimal :.pill {:on-click close-kushi-modal}) "Cancel"]
                          [button (sx :.filled :.pill {:on-click close-kushi-modal}) "Submit"]]]])}
   {:label   "Animate"
    :example (example2 [:div
                        [button
                         {:on-click #(open-kushi-modal "my-modal")}
                         "Click to open modal"]
                        [modal
                         (sx [:translate "-50% calc(-50% + 30px)"]
                             [:&.kushi-modal-open:translate "-50% -50%"]
                             {:-modal-title "Example modal"
                              :-description "Example modal description goes here."
                              :id           "my-modal"})
                         [:div 
                          (sx :.flex-col-fs
                              :gap--1em)
                          [text-field
                           (sx
                            {:placeholder "Puffy"
                             :-label      "Screen name"})]
                          [text-field
                           (sx
                            {:placeholder "Executive"
                             :-label      "Occupation"})]]
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
                          {:-modal-title "Example modal"
                           :-description "Example modal description goes here."
                           :id           "my-modal"})
                         [:div 
                          (sx :.flex-col-fs
                              :gap--1em)
                          [text-field
                           (sx
                            {:placeholder "Puffy"
                             :-label      "Screen name"})]
                          [text-field
                           (sx
                            {:placeholder "Executive"
                             :-label      "Occupation"})]]
                         [:div (sx :.flex-row-fe :gap--1em)
                          [button {:on-click close-kushi-modal} "Cancel"]
                          [button (sx :.filled) "Submit"]]]])}
   #_{:label   "With max-height and inner overflow."
    :example (example2 [:div
                        [button
                         {:on-click #(open-kushi-modal "my-modal")}
                         "Click to open modal"]
                        [modal
                         (sx 
                          :min-width--450px
                          :max-height--400px
                          :&_.kushi-modal-description:fs--$small
                          {:-modal-title "Example modal"
                           :-description "Example modal description goes here."
                           :id           "my-modal"})
                         [:div 
                          [:p "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Integer nisi arcu, iaculis eu sollicitudin sed, aliquam eget massa. Fusce porta posuere sapien, ac laoreet ligula. Pellentesque habitant morbi tristique senectus et netus et malesuada fames ac turpis egestas. Ut ullamcorper lectus a mattis vehicula. Mauris quis dignissim urna, convallis vulputate quam. In lectus diam, suscipit id lectus sit amet, lacinia cursus tellus. Fusce sagittis, felis in pharetra condimentum, neque leo porta eros, non pharetra nulla tortor at nulla. Fusce ut elit nibh. Nulla dictum nulla congue arcu viverra, et tempus lacus ullamcorper. Nulla eleifend nibh eu velit maximus posuere. Aenean facilisis, est quis consectetur pharetra, metus libero dictum nulla, sit amet sollicitudin leo sem sed justo. Nullam tincidunt libero mauris, sit amet mattis leo vestibulum id. Duis vulputate, mauris in pretium tincidunt, arcu lectus mollis lacus, sit amet volutpat diam magna eu urna."]
                          [:p "Integer id arcu ac justo bibendum consequat nec eget neque. Pellentesque ornare enim nibh, eu accumsan libero sodales eu. In justo nulla, mollis vel imperdiet id, ultricies at quam. Cras vestibulum quam arcu, ut tristique mauris semper ac. Nulla molestie mattis felis non molestie. Duis nec felis aliquet, pellentesque mauris sed, rhoncus lorem. Sed luctus vitae mi vel sollicitudin. Nulla facilisi. Aenean euismod mauris id purus pellentesque sagittis. Sed tristique magna sagittis fermentum fermentum. In facilisis est ac ipsum vehicula cursus. Sed justo lorem, mollis a dapibus scelerisque, euismod sed dolor. Donec quis convallis lectus. Aliquam cursus sem vel lectus facilisis, vitae tempor odio gravida. Aliquam quis iaculis orci, eu suscipit ex. Aenean nunc ante, bibendum a lacus vehicula, porta cursus justo. "]]
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


{:label   "With title and description"
 :example (example2 [:div
                     [button
                      {:on-click #(open-kushi-modal "my-modal")}
                      "Click to open modal"]
                     [modal
                      (sx 
                       :min-width--450px
                       :&_.kushi-modal-description:fs--$small
                       {:-modal-title "Example modal"
                        :-description "Example modal description goes here."
                        :id           "my-modal"})
                      [:div 
                       (sx :.flex-col-fs
                           :gap--1em)
                       [text-field
                        (sx
                         {:placeholder "Puffy"
                          :-label      "Screen name"})]
                       [text-field
                        (sx
                         {:placeholder "Executive"
                          :-label      "Occupation"})]]
                      [:div (sx :.flex-row-fe :gap--1em)
                       [button {:on-click close-kushi-modal} "Cancel"]
                       [button (sx :.filled) "Submit"]]]])}


