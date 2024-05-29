(ns kushi.ui.icon.demo
  (:require
   [kushi.ui.icon.core :refer [icon]]
   [kushi.ui.icon.mui.svg :as mui.svg]
   [kushi.ui.button.core :refer [button]]
   [kushi.ui.input.text.core :refer [text-field]]
   [kushi.core :refer (sx)]
   [kushi.playground.component-examples :as component-examples]
   [kushi.playground.util :refer-macros [sx-call]]
   [kushi.ui.button.core :refer [button]]))


(declare icon-examples)


(defn demo [component-opts]
  (into [:<>]
        (for [
              ;; example-opts (take 1 icon-examples)
              example-opts icon-examples
              ;; example-opts (keep-indexed (fn [idx m] (when (contains? #{1} idx) m)) icon-examples)
              ]
          [component-examples/examples-section component-opts example-opts])))


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

(def icon-sizes
  [:xxsmall
   :xsmall
   :small
   :medium
   :large
   :xlarge
   :xxlarge
   :xxxlarge])

(def icon-examples
 [{:desc      "Sizes from xxsmall to xxxlarge, in weights from thin to bold"
   :variants- [:outlined :filled]
   :row-attrs (sx 
               [:&_.playground-component-example-row-instance-code {:flex-direction :column
                                                                    :ai             :fe     
                                                                    :gap            :1rem}])
   :examples  [{:label "Sizes from xxsmall to xxxlarge, in weights from thin to bold"
                :code  (sx-call 
                        (for [weight component-examples/type-weights]
                          (into [:div (sx :.flex-row-fs :ai--fe :gap--2rem)]
                                (for [sz icon-sizes]
                                  [icon {:class [sz weight]} :favorite]))))}]}
  
  {:desc      "Weights from 100 to 700, shown at a font-size of 64px"
   :variants- [:outlined :filled]
   :row-attrs (sx 
               [:&_.playground-component-example-row-instance-code {:ai  :fe     
                                                                    :gap :0.65rem}])
   :examples  [{:label "Weights from 100 to 700"
                :code  (sx-call 
                        (for [weight (drop-last component-examples/type-weights)]
                          [icon {:class [weight]
                                 :style {:font-size :64px}}
                           :favorite]))}]}

  {:desc      "All the colors"
   :variants- [:outlined :filled]
   :row-attrs (sx 
               [:&_.playground-component-example-row-instance-code {:flex-direction :column
                                                                    :ai             :fe     
                                                                    :gap            :1rem}])
   :examples  [{:label "All the colors"
                :code  (sx-call 
                        (for [color (concat component-examples/colors
                                            component-examples/non-semantic-colors)]
                          (into [:div (sx :.flex-row-fs :ai--fe :gap--1.25rem)]
                                (for [val (range 100 1100 100)]
                                  [:div (sx :.flex-col-fs)
                                   [icon {:class [:xlarge]
                                          :style {:color (str "var(--" color "-" val ")")}}
                                    :favorite]
                                   [icon {:class         [:xlarge]
                                          :-icon-filled? true
                                          :style         {:color (str "var(--" color "-" val ")")}}
                                    :favorite]
                                   ]))))}]}

  {:desc      "Many icons have a filled variant"
   :variants- [:outlined :filled]
   :row-attrs (sx 
               [:&_.playground-component-example-row-instance-code {:ai      :fe     
                                                                    :display :grid
                                                                    :gtc     :1fr:1fr:1fr:1fr
                                                                    :gap     :3rem
                                                                    :row-gap :2rem}])
   :examples  [{:label "Many icons have a filled variant"
                :code  (sx-call 
                        (for [icon-name icons-with-filled-variants]
                          [:div (sx :.flex-row-fs :gap--0.75em)
                           [icon {:class [:xlarge :light]} icon-name]
                           [icon {:class         [:xlarge]
                                  :-icon-filled? true}
                            (name icon-name)]]) ) }]}

  {:desc      "Some icons do not have a filled variant"
   :variants- [:outlined :filled]
   :row-attrs (sx 
               [:&_.playground-component-example-row-instance-code {:ai      :fe     
                                                                    :display :grid
                                                                    :gtc     :1fr:1fr:1fr:1fr:1fr:1fr
                                                                    :gap     :3rem
                                                                    :row-gap :2rem}])
   :examples  [{:label "Sizes from xxsmall to xxxlarge"
                :code  (sx-call 
                        (for [icon-name icons-without-filled-variants]
                          [icon {:class [:xlarge :light]} icon-name]))}]}])
