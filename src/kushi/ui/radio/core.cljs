(ns kushi.ui.radio.core
  (:require
   [fireworks.core :refer [? !? ?> !?>]]
   [kushi.core :refer (css sx merge-attrs)]
   [kushi.playground.util :refer-macros [sx-call]]
   [kushi.ui.label.core :refer (label)]
   [kushi.ui.core :refer (extract)]
   [clojure.string :as string]))


(defn radio-old
  {:desc "Input elments of type radio buttons are used in groups of 2 or more,
          when only one choice may be selected from a set of related options."
   }
  [& args]
  (let [{:keys [opts attrs children]} (extract args radio-old)
        {:keys [input-attrs size]}    opts]
    (into
     [:label
      (merge-attrs
       (sx
        ".kushi-radio"
        :.transition
        :transition-duration--$xxfast
        :cursor--pointer
        :.pill
        :color--currentColor
        :line-height--1.1
        :display--grid
        :grid-template-columns--1em:auto
        :gap--0.4em
        :padding-inline--0.5em:1em
        :padding-block--0.4em
        :>*:align-self--center)
       {:data-kushi-size size}
       attrs)
      [:input
       (merge-attrs
        {:class         (css
                         ".kushi-radio-input"
                         :.transition
                         :transition-duration--$xxfast
                         :cursor--pointer
                         [:border-color
                          "color-mix(in hsl, currentColor 55%, transparent)"]
                         [:checked:border-color
                          :currentColor]
                         :display--grid
                         :place-content--center
                         :-webkit-appearance--none
                         :appearance--none
                         :bgc--white
                         :m--0
                         :font--inherit
                         :color--currentColor
                         :width--1em
                         :height--1em
                         :border-style--solid
                         :border-width--$input-border-weight-normal
                         :border-color--currentColor
                         :checked:border-width--0.333em
                         :checked:border-offset---0.333em
                         :o--1
                         :border-radius--50%)
         :data-kushi-ui :input.radio
         :type          :radio}
        input-attrs)]]
     children)))



(defn radio [& args]
  (let [{:keys [opts attrs]} (extract args radio)]
    [:input
     (merge-attrs
      {:class               (css
                             ".kushi-radio-button"
                             :.transition
                             :transition-duration--$xxfast
                             :cursor--pointer
                             [:border-color
                              "color-mix(in hsl, currentColor 55%, transparent)"]
                             [:checked:border-color
                              :currentColor]
                             :display--grid
                             :place-content--center
                             :-webkit-appearance--none
                             :appearance--none
                             :bgc--transparent
                             :m--0
                             :color--currentColor
                             :width--1em
                             :height--1em
                             :border-style--solid
                             :border-width--$input-border-weight-normal
                             :border-color--currentColor
                             :checked:border-width--0.333em
                             :checked:border-offset---0.333em
                             :o--1
                             :border-radius--50%)
       :data-kushi-ui       :radio
       :type                :radio}
      attrs)]))

(def demos
  [{:label   "Basic group with labels"
    :samples [(sx-call [:section.flex-row-fs 
                        [radio {:-input-attrs {:name :demo}} "Yes"]
                        [radio {:-input-attrs {:name :demo}} "No"]
                        [radio {:-input-attrs {:name :demo}} "Maybe"]])]}
   
   {:label   "Basic group with labels, inherited color"
    :samples [(sx-call [:section
                        {:class [:foreground-purple :flex-row-fs]}
                        [radio {:-input-attrs {:name :demo}} "Yes"]
                        [radio {:-input-attrs {:name :demo}} "No"]
                        [radio {:-input-attrs {:name :demo}} "Maybe"]])]}
   
   {:label   "Custom size, xxlarge"
    :samples [(sx-call [:div.flex-row-fs
                        [radio {:-size        :xxlarge
                                :-input-attrs {:name           :custom-size-xxlarge-sample
                                               :defaultChecked true}}
                         "Yes"]
                        [radio {:-size        :xxlarge
                                :-input-attrs {:name :custom-size-xxlarge-sample}}
                         "No"]] )]}

   {:label   "Showing-sizes from xsmall to xxxlarge"
    ;;  :render-as :radio-sizes
    ;; :row-style {:flex-direction :column :align-items :flex-start}
    :samples #_[:xsmall
                :small
                :medium
                :large
                :xlarge
                :xxlarge
                :xxxlarge]

    [(sx-call (into [:div (sx :.flex-col-fs :ai--fs :gap--1.5rem)]
                    (for [size [:xsmall
                                :small
                                :medium
                                :large
                                :xlarge
                                :xxlarge
                                :xxxlarge]
                          :let [nm (str size "-sample")]]
                      [:div.flex-row-fs
                       [radio {:-size        size
                               :-input-attrs {:name           nm 
                                              :defaultChecked true}}
                        "Yes"]
                       [radio {:-size        size
                               :-input-attrs {:name nm}}
                        "No"]])))]}

   {:label   "Custom, with default checked"
    ;; :row-attrs row-attrs
      
      :samples [(sx-call
                 [:section
                  (sx
                   :d--grid
                   :xsm:gtc--1fr
                   :xsm:gtc--1fr:1fr
                   :row-gap--1em
                   :column-gap--2em
                   [:_.emoji
                    {:fs                  :28px
                     :mi                  :0.33em
                     :filter              "grayscale(1)"
                     :transition-property :transform
                     :transition-duration :500ms}]
                   [:_.kushi-radio-input:checked+.kushi-label>.emoji
                    {:filter    :none
                     :transform "scale(1.5)"
                     :animation :jiggle2:0.5s}])
                  [radio
                   (merge-attrs (sx :.normal)
                                {:-input-attrs {:name           :demo-custom
                                                :defaultChecked true}})
                   [label [:span.emoji "🦑"] "Squid"]]
                  [radio
                   (merge-attrs (sx :.normal) {:-input-attrs {:name :demo-custom}})
                   [label [:span.emoji "🐋"] "Whale"]]
                  [radio
                   (merge-attrs (sx :.normal) {:-input-attrs {:name :demo-custom}})
                   [label [:span.emoji "🦈 "] "Shark"]]
                  [radio
                   (merge-attrs (sx :.normal) {:-input-attrs {:name :demo-custom}})
                   [label [:span.emoji "🐊"] "Croc"]]])]}
   ])

  ;;  :demos '[
  ;;           {:label   "Basic group with labels"
  ;;            :samples [[:section.flex-row-fs 
  ;;                       [radio {:-input-attrs {:name :demo}} "Yes"]
  ;;                       [radio {:-input-attrs {:name :demo}} "No"]
  ;;                       [radio {:-input-attrs {:name :demo}} "Maybe"]]]}
            
  ;;           {:label   "Basic group with labels, inherited color"
  ;;            :samples [[:section
  ;;                       {:class [:foreground-purple :flex-row-fs]}
  ;;                       [radio {:-input-attrs {:name :demo}} "Yes"]
  ;;                       [radio {:-input-attrs {:name :demo}} "No"]
  ;;                       [radio {:-input-attrs {:name :demo}} "Maybe"]]]
  ;;            }


  ;; ;;           {:label   "Custom, with default checked"
  ;;            ;; TODO - need a thing that does 'evaled and 'quoted
  ;;            :samples [[:section
  ;;                       (sx :d--grid
  ;;                           :xsm:gtc--1fr
  ;;                           :xsm:gtc--1fr:1fr
  ;;                           :row-gap--1em
  ;;                           :column-gap--2em 
  ;;                           [:_.emoji {:fs                  :28px
  ;;                                      :mi                  :0.33em
  ;;                                      :filter              "grayscale(1)"
  ;;                                      :transition-property :transform
  ;;                                      :transition-duration :500ms}]
  ;;                           [:_.kushi-radio-input:checked+.kushi-label>.emoji {:filter    :none
  ;;                                                                              :transform "scale(1.5)"
  ;;                                                                              :animation :jiggle2:0.5s}])
  ;;                       [radio (merge-attrs (sx :.normal) {:-input-attrs {:name           :demo-custom
  ;;                                                                         :defaultChecked true}}) [label [:span.emoji "🦑"] "Squid"]]
  ;;                       [radio (merge-attrs (sx :.normal) {:-input-attrs {:name :demo-custom}}) [label [:span.emoji "🐋"] "Whale"]]
  ;;                       [radio (merge-attrs (sx :.normal) {:-input-attrs {:name :demo-custom}}) [label [:span.emoji "🦈 "] "Shark"]]
  ;;                       [radio (merge-attrs (sx :.normal) {:-input-attrs {:name :demo-custom}}) [label [:span.emoji "🐊"] "Croc"]]]]}


  ;;           {:label   "Showing-sizes from xsmall to xxxlarge"
  ;;           ;;  :render-as :radio-sizes
  ;;            :samples #_[:xsmall
  ;;                        :small
  ;;                        :medium
  ;;                        :large
  ;;                        :xlarge]
  ;;            [{:label "xsmall"
  ;;              :code  [:div.flex-row-fs
  ;;                      [radio {:-size        :xsmall
  ;;                              :-input-attrs {:name           :xsmall-sample
  ;;                                             :defaultChecked true}}]
  ;;                      [radio {:-size        :xsmall
  ;;                              :-input-attrs {:name :xsmall-sample}}]]}

  ;;             {:label "small"
  ;;              :code  [:div.flex-row-fs
  ;;                      [radio {:-size        :small
  ;;                              :-input-attrs {:name           :small-sample
  ;;                                             :defaultChecked true}}]
  ;;                      [radio {:-size        :small
  ;;                              :-input-attrs {:name :small-sample}}]]}

  ;;             {:label "medium"
  ;;              :code  [:div.flex-row-fs
  ;;                      [radio {:-size        :medium
  ;;                              :-input-attrs {:name           :medium-sample
  ;;                                             :defaultChecked true}}]
  ;;                      [radio {:-size        :medium
  ;;                              :-input-attrs {:name :medium-sample}}]]}

  ;;             {:label "large"
  ;;              :code  [:div.flex-row-fs
  ;;                      [radio {:-size        :large
  ;;                              :-input-attrs {:name           :large-sample
  ;;                                             :defaultChecked true}}]
  ;;                      [radio {:-size        :large
  ;;                              :-input-attrs {:name :large-sample}}]]}
  ;;             {:label "xlarge"
  ;;              :code  [:div.flex-row-fs
  ;;                      [radio {:-size        :xlarge
  ;;                              :-input-attrs {:name           :xlarge-sample
  ;;                                             :defaultChecked true}}]
  ;;                      [radio {:-size        :xlarge
  ;;                              :-input-attrs {:name :xlarge-sample}}]]}

  ;;             {:label "xxlarge"
  ;;              :code  [:div.flex-row-fs
  ;;                      [radio {:-size        :xxlarge
  ;;                              :-input-attrs {:name           :xxlarge-sample
  ;;                                             :defaultChecked true}}]
  ;;                      [radio {:-size        :xxlarge
  ;;                              :-input-attrs {:name :xxlarge-sample}}]]}

  ;;             {:label "xxxlarge"
  ;;              :code  [:div.flex-row-fs
  ;;                      [radio {:-size        :xxxlarge
  ;;                              :-input-attrs {:name           :xxxlarge-sample
  ;;                                             :defaultChecked true}}]
  ;;                      [radio {:-size        :xxxlarge
  ;;                              :-input-attrs {:name :xxxlarge-sample}}]]}]}]
