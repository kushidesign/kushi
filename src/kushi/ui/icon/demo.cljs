(ns kushi.ui.icon.demo
  (:require
   [kushi.ui.icon.core :refer [icon]]
   [kushi.ui.icon.mui.svg :as mui.svg]
   [kushi.ui.button.core :refer [button]]
   [kushi.ui.text-field.core :refer [text-field]]
   [kushi.core :refer (sx merge-attrs)]
   [kushi.playground.component-examples :as component-examples]
   [kushi.playground.util :refer-macros [sx-call]]
   [kushi.ui.button.core :refer [button]]))


(def icons-without-filled-variants
  ["search"
   "playlist_add"
   "expand"
   "compress"
   "arrow_back"
   "arrow_forward"
   "sort"
   "clear"
   "keyboard_return"
   "check"
   "find_replace"
   "open_in_new"
   "fingerprint"
   "arrow-forward"
   "refresh"
   "open-in-new"
   "download"
   "menu"])

(def icons-with-filled-variants
  ["auto_awesome"
   "help"
   "info"
   "favorite"
   "settings"
   "filter_alt"
   "cloud_upload"
   "download"
   "delete"
   "cancel"
   "auto_awesome_motion"
   "archive"
   "sell"
   "visibility"
   "visibility_off"
   "report_problem"
   "check_circle"
   "error"
   "edit"
   "folder"
   "smartphone"
   "star"
   "add-circle"
   "delete"])

(def sizes
  [:xxsmall
   :xsmall
   :small
   :medium
   :large
   :xlarge
   :xxlarge
   :xxxlarge])

(def examples
 (let [container-attrs
       (sx :gtc--1fr)

       row-attrs-2
       (sx [:&_.playground-component-example-row-instance-code
            {:w              :100%
             :d              :flex
             :flex-direction :column
             :jc             :sb
             :row-gap        :1rem
             :ai             :stretch}])

       grid-row-attrs
       (sx 
        [:&_.playground-component-example-row-instance-code
         {:ai         :fe     
          :display    :grid
          :w          :100%
          :jc         :sb
          :gtc        '(repeat 6 :max-content)
          :row-gap    :2rem
          :column-gap :unset}])]

   [{:desc            "Sizes from xxsmall to xxxlarge, in weights from thin to bold"
     :variants-       [:outlined :filled]
     :container-attrs container-attrs
     :row-attrs       row-attrs-2
     :examples        [{:label "Sizes from xxsmall to xxxlarge, in weights from thin to bold"
                        :code  (sx-call 
                                (for [weight component-examples/type-weights]
                                  (into [:div (sx :.flex-row-sb :row-gap--1.25rem)]
                                        (for [sz sizes]
                                          [icon {:class [sz weight]} :star]))))}]}
    
    {:desc            "Weights from 100 to 700"
     :variants-       [:outlined :filled]
     :container-attrs container-attrs
     :row-attrs       (merge-attrs grid-row-attrs
                                   (sx :&_.kushi-icon:fs--48px
                                       :sm:&_.kushi-icon:fs--64px))
     :examples        [{:label "Weights from 100 to 700"
                        :code  (sx-call 
                                (for [weight (drop-last component-examples/type-weights)]
                                  [icon {:class [weight]}
                                   :star]))}]}

    {:desc            "All the colors"
     :variants-       [:outlined :filled]
     :container-attrs container-attrs
     :row-attrs       row-attrs-2
     :examples        [{:label "All the colors"
                        :code  (sx-call 
                                (for [color (concat component-examples/colors
                                                    component-examples/non-semantic-colors)]
                                  (into [:div (sx :.flex-row-fs :jc--sb :row-gap--1.25rem)]
                                        (for [val (range 100 1100 100)]
                                          [:div (sx :.flex-col-fs)
                                           [icon {:class [:xlarge :light]
                                                  :style {:color (str "var(--" color "-" val ")")}}
                                            :star]
                                           [icon {:class         [:xlarge :light]
                                                  :-icon-filled? true
                                                  :style         {:color (str "var(--" color "-" val ")")}}
                                            :star]]))))}]}

    {:desc            "Many icons have a filled variant"
     :variants-       [:outlined :filled]
     :container-attrs container-attrs
     :row-attrs       grid-row-attrs
     :examples        [{:label "Many icons have a filled variant"
                        :code  (sx-call 
                                (for [icon-name icons-with-filled-variants]
                                  [:div (sx :.flex-row-fs 
                                            :gap--0.25rem)
                                   [icon {:class [:xlarge :light]} icon-name]
                                   [icon {:class         [:xlarge]
                                          :-icon-filled? true}
                                    (name icon-name)]]) ) }]}

    {:desc      "Some icons do not have a filled variant"
     :variants- [:outlined :filled]
     :container-attrs container-attrs
     :row-attrs grid-row-attrs
     :examples  [{:label "Sizes from xxsmall to xxxlarge"
                  :code  (sx-call 
                          (for [icon-name icons-without-filled-variants]
                            [icon {:class [:xlarge :light]} icon-name]))}]}]))
