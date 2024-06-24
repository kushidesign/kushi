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
   "refresh"
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
   "expand_circle_down"])

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

       row-attrs
       (sx [:&_.instance-code
            {:w  :100%
             :d  :flex
             :jc :sb
             :pi :0.75rem}])

       row-attrs-all
       (sx [:&_.instance-code
            {:w              :100%
             :pi             :0.75rem
             :row-gap        :2rem
             :flex-direction :column
             :align-items    :stretch}])

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

   [{:desc            "Sizes from xxsmall to xxxlarge, in weights from extra-light to extra-bold"
     :variants-       [:outlined :filled]
     :container-attrs container-attrs
     :row-attrs       row-attrs-all
     :snippets-header ["Use the font-size utility classes `:.xxxsmall` ~ `:.xxxlarge` to control the size of the icons."
                       "You can also use something like `:fs--96px` for specific sizes."
                       :br
                       :br
                       "Use the font-weight utility classes `:.extra-light` ~ `:.extra-bold` to control the weight of the icons."
                       "This is only applicable to the outline variant of the icon."
                       :br
                       :br
                       "A few examples of different size/weight combos:"]
     :snippets        '[[icon (sx :.small :.extra-bold)]
                        [icon (sx :.xxxlarge :.light)]
                        [icon (sx :.medium :.normal)]]
     :examples        [{:label "Sizes from xxsmall to xxxlarge, in weights from extra-light to extra-bold"
                        :code  (sx-call 
                                (for [weight component-examples/type-weights]
                                  (into [:div (sx :.flex-row-sb :row-gap--1.25rem)]
                                        (for [sz sizes]
                                          [icon {:class [sz weight]} :star]))))}]}
    
    {:desc            "Weights from extra-light to bold"
     :variants-       [:outlined :filled]
     :container-attrs container-attrs
     :row-attrs       (merge-attrs #_grid-row-attrs
                                   row-attrs
                                   (sx :&_.kushi-icon:fs--48px
                                       :sm:&_.kushi-icon:fs--64px))
     :examples        [{:label "Weights from extra-light to bold"
                        :code  (sx-call 
                                (for [weight (drop-last component-examples/type-weights)]
                                  [icon {:class [weight]}
                                   :star]))}]}

    {:desc            "All the colors"
     :variants-       [:outlined :filled]
     :container-attrs container-attrs
     :row-attrs       (merge-attrs row-attrs-all  (sx [:&_.instance-code {:row-gap :1rem}]))
     :examples        [{:label "All the colors"
                        :code  (sx-call 
                                (for [color (concat component-examples/colors
                                                    component-examples/non-semantic-colors)]
                                  (into [:div (sx :.flex-row-fs :jc--sb :row-gap--1.25rem)]
                                        (for [val (range 200 1000 100)]
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
     :row-attrs       row-attrs-all
     :examples        [{:label "Many icons have a filled variant"
                        :code  (sx-call 
                                (for [icon-set (partition 8 icons-with-filled-variants)]
                                  (into [:div (sx :.flex-row-fs :jc--sb :row-gap--1.25rem)]
                                        (for [icon-name icon-set]
                                          [:div (sx :.flex-col-fs 
                                                    :gap--0.25rem)
                                           [icon {:class [:xlarge :light]} icon-name]
                                           [icon {:class         [:xlarge]
                                                  :-icon-filled? true}
                                            (name icon-name)]]))))}]}

    {:desc            "Some icons do not have a filled variant"
     :variants-       [:outlined :filled]
     :container-attrs container-attrs
     :row-attrs       row-attrs-all
     :examples        [{:label "Sizes from xxsmall to xxxlarge"
                        :code  (sx-call 
                                (for [icon-set (partition 8 icons-without-filled-variants)]
                                  (into [:div (sx :.flex-row-fs :jc--sb :row-gap--1.25rem)]
                                        (for [icon-name icon-set]
                                          [:div (sx :.flex-col-fs 
                                                    :gap--0.25rem)
                                           [icon {:class [:xlarge :light]} icon-name]
                                           #_[icon {:class         [:xlarge]
                                                  :-icon-filled? true}
                                            (name icon-name)]]))))}]}]))
