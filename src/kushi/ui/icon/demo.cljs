(ns ^{:kushi/layer "user-styles"} kushi.ui.icon.demo
  (:require
   [kushi.ui.icon.core :refer [icon]]
   [kushi.ui.tooltip.core :refer [tooltip-attrs]]
   [kushi.core :refer (sx merge-attrs)]
   [kushi.playground.component-examples :as component-examples]
   [kushi.playground.util :refer-macros [sx-call]]))


(def icons-without-filled-variants
  ["search"
   "playlist-add"
   "expand"
   "compress"
   "arrow-back"
   "arrow-forward"
   "sort"
   "clear"
   "keyboard-return"
   "check"
   "find-replace"
   "open-in-new"
   "fingerprint"
   "refresh"
   "download"
   "menu"])

(def icons-with-filled-variants
  ["auto-awesome"
   "help"
   "info"
   "favorite"
   "settings"
   "filter-alt"
   "cloud-upload"
   "download"
   "delete"
   "cancel"
   "auto-awesome-motion"
   "archive"
   "sell"
   "visibility"
   "visibility-off"
   "report-problem"
   "check-circle"
   "error"
   "edit"
   "folder"
   "smartphone"
   "star"
   "add-circle"
   "expand-circle-down"])

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
       (sx [:_.playground-component-example-row-instance-code
            {:w              :100%
             :d              :flex
             :flex-direction :column
             :jc             :sb
             :row-gap        :1rem
             :ai             :stretch}])

       row-attrs
       (sx [:_.instance-code
            {:w  :100%
             :d  :flex
             :jc :sb
             :pi :0.75rem}])

       row-attrs-all
       (sx [:_.instance-code
            {:w              :100%
            ;;  :pi             :0.75rem
             :pi             :0rem
             :row-gap        :2rem
             :flex-direction :column
             :align-items    :stretch}])

       grid-row-attrs
       (sx 
        [:_.playground-component-example-row-instance-code
         {:ai         :fe     
          :display    :grid
          :w          :100%
          :jc         :sb
          :gtc        "repeat(6, :max-content)"
          :row-gap    :2rem
          :column-gap :unset}])]

   [
    {:desc            "Sizes from xxsmall to xxxlarge, in weights from extra-light to extra-bold"
     :variants-       [:outlined :filled]
     :container-attrs container-attrs
     :row-attrs       row-attrs-all
     :snippets-header ["Use the font-size utility classes `:.xxxsmall` ~ `:.xxxlarge` to control the size of the icons."
                       "You can also use something like `:fs--96px` for specific sizes."
                       :br
                       :br
                       "Use the font-weight utility classes `:.thin` ~ `:.extra-bold` to control the weight of the icons."
                       "This is only applicable to the outline variant of the icon."
                       :br
                       :br
                       "A few examples of different size/weight combos:"]
     :snippets        '[[icon (sx :.small :.extra-bold) :star]
                        [icon (sx :.xxxlarge :.light) :star]
                        [icon (sx :.medium :.normal) :star]]
     :examples        [{:label "Sizes from xxsmall to xxxlarge, in weights from extra-light to extra-bold"
                        :code  (sx-call 
                                (for [weight component-examples/type-weights]
                                  (into [:div (sx :.flex-row-sb :row-gap--1.25rem)]
                                        (for [sz sizes]
                                          [icon (merge-attrs
                                                 (sx sz
                                                     weight
                                                     :cursor--pointer
                                                     :c--$foreground-color
                                                     :dark:c--$foreground-color-dark-mode
                                                     )
                                                 (tooltip-attrs {:-text [(name sz) (name weight)]}))
                                           :star]))))}]}
    
    {:desc            "Weights from extra-light to bold"
     :variants-       [:outlined :filled]
     :container-attrs container-attrs
     :row-attrs       (merge-attrs #_grid-row-attrs
                                   row-attrs
                                   (sx 
                                    :_.kushi-icon:fs--36px
                                    :xsm:_.kushi-icon:fs--48px
                                    :sm:_.kushi-icon:fs--64px
                                    [:_.instance-code {:gap 0
                                                        :pi  0}] ))

     :snippets-header ["Use the font-weight utility classes `:.thin` ~ `:.bold` to control the weight of the icons."
                       :br
                       "This is only applicable to the outline variant of the icon."
                       :br
                       :br
                       "A few examples of different weights, at 48px:"]
     :snippets        '[[icon (sx :fs--48px :.extra-bold) :star]
                        [icon (sx :fs--48px :.light) :star]
                        [icon (sx :fs--48px :.normal) :star]]
     :examples        [{:label "Weights from extra-light to bold"
                        :code  (sx-call 
                                (for [weight (drop-last component-examples/type-weights)]
                                  [icon (sx weight
                                            :c--$foreground-color
                                            :dark:c--$foreground-color-dark-mode
                                            )
                                   :star]))}]}

    {:desc            "All the colors"
     :variants-       [:outlined :filled]
     :container-attrs container-attrs
     :row-attrs       (merge-attrs row-attrs-all  (sx [:_.instance-code {:row-gap :1rem}]))
     :snippets-header ["The css `color` property controls the color of icons."
                       "Kushi provides color tokens in value ranges from `50` ~ `1000`, in increments of `50`."
                       "E.g. `:c--$blue-50`, `:c--$blue-350`, `:c--$blue-800`, etc."
                       :br
                       "You can also use any valid css color value e.g. `$c--#8a8a8a`"
                       :br
                       :br
                       "Example row of stars, in all the colors:"]
     :snippets        '[[:div
                         [icon (sx :c--$gray-500) :star]
                         [icon (sx :c--$blue-500) :star]
                         [icon (sx :c--$green-500) :star]
                         [icon (sx :c--$yellow-500) :star]
                         [icon (sx :c--$red-500) :star]
                         [icon (sx :c--$purple-500) :star]
                         [icon (sx :c--$lime-500) :star]
                         [icon (sx :c--$brown-500) :star]
                         [icon (sx :c--$orange-500) :star]
                         [icon (sx :c--$magenta-500) :star]]]
     :examples        [{:label "All the colors"
                        :code  (sx-call 

                                (into [:div (sx :.flex-row-fs :jc--sb :row-gap--1.25rem)]
                                      (for [color (concat component-examples/colors
                                                          component-examples/non-semantic-colors)
                                            :let  [
                                                   color (get component-examples/color-lut color color)
                                                   base             "var(--foreground-color-" 
                                                   color-token      (str base color ")")
                                                   color-token-dark (str base color "-dark-mode)")
                                                   foreground-color-class (str "foreground-" color)
                                                   ]]
                                        [:div (merge-attrs
                                               (sx :.flex-col-fs 
                                                   :cursor--pointer)
                                               (tooltip-attrs {:-text color}))
                                         [icon (sx :fs--$xlarge
                                                   :.light
                                                   foreground-color-class)
                                          :star]
                                         [icon (merge-attrs
                                                (sx :fs--$xlarge
                                                    foreground-color-class)
                                                {:-icon-filled? true})
                                                         
                                          :star]])))}]}



    {:desc            "Many icons have a filled variant"
     :variants-       [:outlined :filled]
     :container-attrs container-attrs
     :row-attrs       row-attrs-all
     :snippets-header ["Use the `:-icon-filled?` custom attribute to get a filled icon."
                       :br
                       "This is only applicable to icons which have a filled variant."]
     :snippets        '[[icon {:-icon-filled? true} :star]]
     :examples        [{:label "Many icons have a filled variant"
                        :code  (sx-call 
                                (for [icon-set (partition 8 icons-with-filled-variants)]
                                  (into [:div (sx :.flex-row-fs :jc--sb :row-gap--1.25rem)]
                                        (for [icon-name icon-set]
                                          [:div (merge-attrs
                                                 (sx :.flex-col-fs 
                                                     :gap--0.25rem
                                                     :cursor--pointer)
                                                 (tooltip-attrs {:-text icon-name}))
                                           [icon (merge-attrs
                                                  (sx :.xlarge 
                                                      :.light
                                                      :cursor--pointer
                                                      :c--$foreground-color
                                                      :dark:c--$foreground-color-dark-mode))
                                            (name icon-name)]
                                           [icon 
                                            (merge-attrs
                                             (sx :.xlarge 
                                                 :cursor--pointer
                                                 :c--$foreground-color
                                                 :dark:c--$foreground-color-dark-mode)
                                             {:-icon-filled? true})
                                            (name icon-name)]]))))}]}

    {:desc            "Some icons do not have a filled variant"
     :variants-       [:outlined :filled]
     :container-attrs container-attrs
     :row-attrs       row-attrs-all
     :snippets        '[[:div
                         [icon :search]
                         [icon :playlist-add]
                         [icon :expand]
                         [icon :compress]
                         [icon :arrow-back]
                         [icon :arrow-forward]
                         [icon :sort]
                         [icon :clear]
                         [icon :keyboard-return]
                         [icon :check]
                         [icon :find-replace]
                         [icon :open-in-new]
                         [icon :fingerprint]
                         [icon :refresh]
                         [icon :download]
                         [icon :menu]]]
     :examples        [{:label "Sizes from xxsmall to xxxlarge"
                        :code  (sx-call 
                                (for [icon-set (partition 8 icons-without-filled-variants)]
                                  (into [:div (sx :.flex-row-fs :jc--sb :row-gap--1.25rem)]
                                        (for [icon-name icon-set]
                                          [:div (sx :.flex-col-fs 
                                                    :gap--0.25rem)
                                           [icon
                                            (merge-attrs (sx :.xlarge 
                                                             :.light
                                                             :cursor--pointer
                                                             :c--$foreground-color
                                                             :dark:c--$foreground-color-dark-mode)
                                                         (tooltip-attrs {:-text icon-name}))
                                            icon-name]]))))}]}]))
