(ns kushi.ui.radio
  (:require
   [fireworks.core :refer [? !? ?> !?>]]
   [kushi.core :refer (css sx merge-attrs)]
   [kushi.playground.util :refer-macros [sx-call]]
   [kushi.ui.label :refer (label)]
   [kushi.ui.flex :refer (flex-row-start)]
   [kushi.ui.core :refer (extract defui)]
   [clojure.string :as string]))





(defn radio-old
  {:desc "Input elments of type radio buttons are used in groups of 2 or more,
          when only one choice may be selected from a set of related options."
   }
  [& args]
  (let [{:keys [opts attrs children]} (extract args [:input-attrs])
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
       {:data-ks-sizing size}
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
         :data-ks-ui :input.radio
         :type          :radio}
        input-attrs)]]
     children)))



#_(defn radio
  {:doc "Input elments of type radio buttons are used in groups of 2 or more,
         when only one choice may be selected from a set of related options."
   :opts {:sizing   {:desc    "Size"
                     :default nil}
          :colorway {:desc    "Size"
                     :default nil}}}
  [& args]
  (let [{:keys [opts attrs]}      (extract args)
        {:keys [sizing colorway]} opts]
    [:input
     (merge-attrs
      {:class            (css
                          ".kushi-radio-button"
                          :.transition
                          :transition-duration--$xxfast
                          :cursor--pointer
                          :+label:cursor--pointer
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
       :data-ks-ui       :radio
       :data-ks-sizing   sizing
       :data-ks-colorway colorway
       :data-ks-surface  :transparent
       :type             :radio}
      attrs)]))

(defui radio 
  {:doc   "This is radio docstring"
   :props {:sizing   {:schema  keyword?
                      :desc    "Blah blah blah"
                      :default nil} 
           :colorway {:schema keyword?
                      :desc   "Blah blah blah"
                      :defaul nil}}}
  [& args]
  (let [{:keys [colorway sizing]} &props]
    [:input
     (merge-attrs
      {:class            (css
                          ".kushi-radio-button"
                          :.transition
                          :transition-duration--$xxfast
                          :cursor--pointer
                          :+label:cursor--pointer
                          :+label:pis--0.369em
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
       :data-ks-ui       :radio
       :data-ks-surface  :transparent
       :type             :radio}
      &data-ks-attrs
      &attrs)]))


(def demos
  [{:label   "Basic group with labels"
    :samples [(sx-call [:section.flex-row-fs 
                        [radio {:input-attrs {:name :demo}} "Yes"]
                        [radio {:input-attrs {:name :demo}} "No"]
                        [radio {:input-attrs {:name :demo}} "Maybe"]])]}
   
   {:label   "Basic group with labels, inherited color"
    :samples [(sx-call [:section
                        {:class [:foreground-purple :flex-row-start]}
                        [radio {:input-attrs {:name :demo}} "Yes"]
                        [radio {:input-attrs {:name :demo}} "No"]
                        [radio {:input-attrs {:name :demo}} "Maybe"]])]}
   
   {:label   "Custom size, xxlarge"
    :samples [(sx-call [:div.flex-row-fs
                        [radio {:sizing        :xxlarge
                                :input-attrs {:name           :custom-size-xxlarge-sample
                                               :defaultChecked true}}
                         "Yes"]
                        [radio {:sizing        :xxlarge
                                :input-attrs {:name :custom-size-xxlarge-sample}}
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
                       [radio {:sizing        size
                               :input-attrs {:name           nm 
                                              :defaultChecked true}}
                        "Yes"]
                       [radio {:sizing        size
                               :input-attrs {:name nm}}
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
                                {:input-attrs {:name           :demo-custom
                                                :defaultChecked true}})
                   [label [:span.emoji "🦑"] "Squid"]]
                  [radio
                   (merge-attrs (sx :.normal) {:input-attrs {:name :demo-custom}})
                   [label [:span.emoji "🐋"] "Whale"]]
                  [radio
                   (merge-attrs (sx :.normal) {:input-attrs {:name :demo-custom}})
                   [label [:span.emoji "🦈 "] "Shark"]]
                  [radio
                   (merge-attrs (sx :.normal) {:input-attrs {:name :demo-custom}})
                   [label [:span.emoji "🐊"] "Croc"]]])]}
   ])

  ;;  :demos '[
  ;;           {:label   "Basic group with labels"
  ;;            :samples [[:section.flex-row-fs 
  ;;                       [radio {:input-attrs {:name :demo}} "Yes"]
  ;;                       [radio {:input-attrs {:name :demo}} "No"]
  ;;                       [radio {:input-attrs {:name :demo}} "Maybe"]]]}
            
  ;;           {:label   "Basic group with labels, inherited color"
  ;;            :samples [[:section
  ;;                       {:class [:foreground-purple :flex-row-start]}
  ;;                       [radio {:input-attrs {:name :demo}} "Yes"]
  ;;                       [radio {:input-attrs {:name :demo}} "No"]
  ;;                       [radio {:input-attrs {:name :demo}} "Maybe"]]]
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
  ;;                       [radio (merge-attrs (sx :.normal) {:input-attrs {:name           :demo-custom
  ;;                                                                         :defaultChecked true}}) [label [:span.emoji "🦑"] "Squid"]]
  ;;                       [radio (merge-attrs (sx :.normal) {:input-attrs {:name :demo-custom}}) [label [:span.emoji "🐋"] "Whale"]]
  ;;                       [radio (merge-attrs (sx :.normal) {:input-attrs {:name :demo-custom}}) [label [:span.emoji "🦈 "] "Shark"]]
  ;;                       [radio (merge-attrs (sx :.normal) {:input-attrs {:name :demo-custom}}) [label [:span.emoji "🐊"] "Croc"]]]]}


  ;;           {:label   "Showing-sizes from xsmall to xxxlarge"
  ;;           ;;  :render-as :radio-sizes
  ;;            :samples #_[:xsmall
  ;;                        :small
  ;;                        :medium
  ;;                        :large
  ;;                        :xlarge]
  ;;            [{:label "xsmall"
  ;;              :code  [:div.flex-row-fs
  ;;                      [radio {:sizing        :xsmall
  ;;                              :input-attrs {:name           :xsmall-sample
  ;;                                             :defaultChecked true}}]
  ;;                      [radio {:sizing        :xsmall
  ;;                              :input-attrs {:name :xsmall-sample}}]]}

  ;;             {:label "small"
  ;;              :code  [:div.flex-row-fs
  ;;                      [radio {:sizing        :small
  ;;                              :input-attrs {:name           :small-sample
  ;;                                             :defaultChecked true}}]
  ;;                      [radio {:sizing        :small
  ;;                              :input-attrs {:name :small-sample}}]]}

  ;;             {:label "medium"
  ;;              :code  [:div.flex-row-fs
  ;;                      [radio {:sizing        :medium
  ;;                              :input-attrs {:name           :medium-sample
  ;;                                             :defaultChecked true}}]
  ;;                      [radio {:sizing        :medium
  ;;                              :input-attrs {:name :medium-sample}}]]}

  ;;             {:label "large"
  ;;              :code  [:div.flex-row-fs
  ;;                      [radio {:sizing        :large
  ;;                              :input-attrs {:name           :large-sample
  ;;                                             :defaultChecked true}}]
  ;;                      [radio {:sizing        :large
  ;;                              :input-attrs {:name :large-sample}}]]}
  ;;             {:label "xlarge"
  ;;              :code  [:div.flex-row-fs
  ;;                      [radio {:sizing        :xlarge
  ;;                              :input-attrs {:name           :xlarge-sample
  ;;                                             :defaultChecked true}}]
  ;;                      [radio {:sizing        :xlarge
  ;;                              :input-attrs {:name :xlarge-sample}}]]}

  ;;             {:label "xxlarge"
  ;;              :code  [:div.flex-row-fs
  ;;                      [radio {:sizing        :xxlarge
  ;;                              :input-attrs {:name           :xxlarge-sample
  ;;                                             :defaultChecked true}}]
  ;;                      [radio {:sizing        :xxlarge
  ;;                              :input-attrs {:name :xxlarge-sample}}]]}

  ;;             {:label "xxxlarge"
  ;;              :code  [:div.flex-row-fs
  ;;                      [radio {:sizing        :xxxlarge
  ;;                              :input-attrs {:name           :xxxlarge-sample
  ;;                                             :defaultChecked true}}]
  ;;                      [radio {:sizing        :xxxlarge
  ;;                              :input-attrs {:name :xxxlarge-sample}}]]}]}]

;; Sketch for radio-group
(defui radio-group 
  {:doc          "This is radio docstring"
   :props/family [:element]
   :props/shared [
                ;;  :sizing 
                ;;  :colorway
                  ;; :element
                  ]
   :props        {
          ;; :sizing   {:schema  keyword?
          ;;            :desc    "Blah blah blah"
          ;;            :default nil} 
                  
          ;; :colorway {:schema  keyword?
          ;;            :desc    "Blah blah blah"
          ;;            :default nil}
                  
                  :surface  {:schema  keyword?
                             :desc    "Blah blah blah"
                             :default nil}

                  :inert?   {:schema  boolean?
                             :desc    "Surface is not interative meaning no hover or active states."
                             :default nil}

          ;; TODO group-id?
                  :group-id {:schema    keyword?
                             :required? true}

                  :choices  {:schema    vector?
                             :required? true
                             :data-ks?  false}

                  :legend   {:schema   string?
                             :default  nil
                             :data-ks? false}

                  :default  {:schema   string?
                             :desc     "Must match the Choice label string value"
                             :default  nil
                             :data-ks? false}}}


  ;; Is `data-ks?` needed? or use a registry?
  ;; Pull in from data-ks-attrs
  ;; Incorporate validation in defui
  ;; for :props entry, make kushi.ui.props
  ;; or kushi.props entry work


  ;; {:kushi.ui.props [:surface :convex]
  ;;  :kushi.props    [:surface :convex]
  ;;  :props          {:legend {:schema   string?
  ;;                            :default  nil
  ;;                            :data-ks? false}}}


  ;; {:doc   "HIHIihihihh"
  ;;    :props (merge surface-props
  ;;                  {:id      {:schema    keyword?
  ;;                             :required? true}
  ;;                   :choices {:schema    vector?
  ;;                             :required? true
  ;;                             :data-ks?  false}
  ;;                   :legend  {:schema  string?
  ;;                             :default nil
  ;;                             :data-ks?  false}
  ;;                   :default {:schema   string?
  ;;                             :desc     "Must match the Choice label string value"
  ;;                             :default  nil
  ;;                             :data-ks? false}})}
  [& args]
  (let [{:keys [group-id choices default inert?]} &props]
    ;; Maybe no legend
    (let [rg-id (str group-id "-radio-group")]
      (into
       [:div (merge-attrs (sx :.flex-row-start :gap--1.5em)
                          {:id rg-id}
                          &data-ks-attrs
                          &attrs

                          ;; Pull this from in data-ks-attrs so you don't have to manualize schema?
                          {:data-ks-inert (when-not (false? inert?) "")})]
       (for [choice choices]
         (let [choice-label (if (map? choice) (:label choice) choice)
               choice-lc    (string/lower-case choice-label)
               choice-id    (or (when (map? choice) (:id choice))
                                (str rg-id "-" choice-lc "-choice"))
               choice-value (or (when (map? choice) (:value choice))
                                choice-lc)]
           [flex-row-start 
            [radio (merge {:id    choice-id
                           :name  group-id
                           :value (or choice-value choice-lc)}
                          (when (= default choice-label)
                            {:defaultChecked true}))
             choice-label]
            [label {:for choice-id} choice-label]]))))))


;; Basic example call
;; [radio-group {:id      "foo"
;;               :choices ["Yes" "No" "Maybe"]}]


;; Basic example call, with maps
;; [radio-group {:id      "foo"
;;               :choices [{:label "Yes"
;;                          :value "12"}
;;                         {:label "No"
;;                          :value "2"}
;;                         {:label "Maybe"
;;                          :value "3"}]}]

;; Basic example call, with maps
;; How to apply attrs to members?
;; Maybe leave legend out of it?
;; [radio-group (merge-attrs
;;               (sx :flex-direction--column
;;                   :gap--0.5em)
;;               {:id             "foo"
;;                :choices        [{:label "Yes"
;;                                  :value "12"}
;;                                 {:label "No"
;;                                  :value "2"}
;;                                 {:label "Maybe"
;;                                  :value "3"}]})]
