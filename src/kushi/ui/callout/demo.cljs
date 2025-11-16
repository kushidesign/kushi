(ns ^{:kushi/layer "user-styles"} kushi.ui.callout.demo
  (:require
   [fireworks.core :refer [? !? ?> !?>]]
   [clojure.string :as string]
   [kushi.core :refer (sx merge-attrs)]
   [kushi.playground.util :refer-macros [sx-call]]
   [kushi.showcase.core
    :as showcase
    :refer [samples-with-variant samples]]
   [kushi.ui.callout :refer [callout]]
   [kushi.ui.flex :refer [flex-row]]
   [kushi.ui.icon :refer [icon]]
   [kushi.ui.button :refer [button]]
   [kushi.ui.icon-button :refer [icon-button]]
   [kushi.ui.link :refer [link]]
   )
    )

(def sizes
  [:xxsmall
   :xsmall
   :small
   :medium
   :large])

(def demos
  (let [row-style     {:flex-direction        :column
                       :justify-content       :flex-start
                       :gap                   :1rem
                       :grid-template-columns "100px 600px"}
        require       '[[kushi.core :refer (sx merge-attrs)]
                        [kushi.ui.icon :refer [icon]]
                        [kushi.ui.link :refer [link]]]
        variant-scale [:faint :soft :solid :outline]]

    [{:label     "Text sizes from xxsmall to xxlarge"
      :row-attrs (sx :gtc--100px:600px)
      :desc      "Text sizes from xxxsmall to xxxlarge"
      :samples   (samples-with-variant
                  {:variant       :text-size
                   :variant-scale :text-size/xxsmall-large
                   :attrs         {:colorway :accent}
                   :args          [[flex-row (sx {:jc :sb})
                                    [icon :info]
                                    [:span "Please check out the " [link "new features"]]
                                    [icon-button 
                                     {:colorway :positive
                                      :surface  :transparent
                                      :shape    :pill
                                      :packing  :compact}
                                     :close]]]})}

     ;; TODO - maybe you need a new surface sub-family for non-interactive things
     #_{:label     "Surfaces"
        :row-attrs (sx :max-width--600px)
        :desc      "Surfaces"
        :samples   (samples-with-variant
                    {:variant       :surface
                     :variant-scale :text-size/xxsmall-large
                     :attrs         {:colorway :accent
                                     :stroke   :hard}
                     :args          [[flex-row (sx {:jc :sb})
                                      [icon :info]
                                      [:span "Please check out the " [link "new features"]]
                                      [icon-button 
                                       {:colorway :positive
                                        :surface  :transparent
                                        :shape    :pill
                                        :packing  :compact}
                                       :close]]]})}
     {:label     "Stroke intensity"
      :row-attrs (sx :gtc--100px:600px)
      :desc      "Stroke intensity"
      :samples   (samples-with-variant
                  {:variant       :stroke
                   :attrs         {:colorway :accent}
                   :args          [[flex-row (sx {:jc :sb})
                                    [icon :info]
                                    [:span "Please check out the " [link "new features"]]
                                    [icon-button 
                                     {:colorway :positive
                                      :surface  :transparent
                                      :shape    :pill
                                      :packing  :compact}
                                     :close]]]})}
     

     {:label     "Surfaces"
      :row-attrs (sx :gtc--100px:600px)
      :desc      "Text sizes from xxxsmall to xxxlarge"
      :samples   (samples-with-variant
                  {:variant       :surface
                   :variant-scale :surface/simple
                   :attrs         {:colorway :accent}
                   :args          [[flex-row (sx {:jc :sb})
                                    [icon :info]
                                    [:span "Please check out the " [link "new features"]]
                                    [icon-button 
                                     {:colorway :positive
                                      :surface  :transparent
                                      :shape    :pill
                                      :packing  :compact}
                                     :close]]]})}
     #_{:label     "Surfaces"
      :row-attrs (sx :flex-wrap--wrap :_.ks-callout:w--100%)
      :samples   (samples
                  [[callout
                    {:colorway :accent
                     :surface  :faint}
                    [flex-row (sx {:jc :sb})
                     [icon :info]
                     [:span "Please check out the " [link "new features"]]
                     [icon-button 
                      {:colorway :positive
                       :shape    :pill
                       :packing  :compact}
                      :close]]]

                   [callout
                    {:colorway :accent
                     :surface  :soft}
                    [flex-row (sx {:jc :sb})
                     [icon :info]
                     [:span "Please check out the " [link "new features"]]
                     [icon-button 
                      {:colorway :positive
                       :shape    :pill
                       :packing  :compact}
                      :close]]]
                   
                   [callout
                    {:colorway :accent
                     :surface  :solid}
                    [flex-row (sx {:jc :sb})
                     [icon :info]
                     [:span "Please check out the " [link "new features"]]
                     [icon-button 
                      {:colorway :positive
                       :shape    :pill
                       :packing  :compact} :close]]]])}

     {:label     "Surfaces with outline"
      :row-attrs (sx :flex-wrap--wrap :_.ks-callout:w--100%)
      :samples   (samples
                  [[callout
                    {:colorway :accent
                     :surface  :minimal
                     :stroke   :hard}
                    [flex-row (sx {:jc :sb})
                     [icon :info]
                     [:span "Please check out the " [link "new features"]]
                     [icon-button 
                      {:surface  :minimal
                       :shape    :pill
                       :packing  :compact}
                      :close]]]

                   [callout
                    {:colorway :accent
                     :surface  :faint
                     :stroke   :hard}
                    [flex-row (sx {:jc :sb})
                     [icon :info]
                     [:span "Please check out the " [link "new features"]]
                     [icon-button 
                      {:surface  :faint
                       :colorway :accent
                       :shape    :pill
                       :packing  :compact}
                      :close]]]
                   
                   [callout
                    {:colorway :accent
                     :surface  :soft
                     :stroke   :hard}
                    [flex-row (sx {:jc :sb})
                     [icon :info]
                     [:span "Please check out the " [link "new features"]]
                     [icon-button 
                      {:surface  :transparent
                       :colorway :accent
                       :shape    :pill
                       :packing  :compact}
                      :close]]]])}
    
     {:label     "Neutral"
      :row-attrs (sx :flex-wrap--wrap :_.ks-callout:w--100%)
      :samples   (samples
                  [[callout
                    {:surface :solid}
                    [flex-row (sx {:jc :sb})
                     [icon :info
                      {:colorway :neutral}]
                     [:span "Please check out the " [link "new features"]]
                     [icon-button 
                      {:surface  :solid
                       :shape    :pill
                       :packing  :compact}
                      :close]]]

                   [callout
                    {:surface :soft
                     :stroke  :hard}
                    [flex-row (sx {:jc :sb})
                     [icon :info]
                     [:span "Please check out the " [link "new features"]]
                     [icon-button 
                      {:surface  :soft
                       :shape    :pill
                       :packing  :compact}
                      :close]]]
                   
                   [callout
                    {:surface :soft}
                    [flex-row (sx {:jc :sb})
                     [icon :info]
                     [:span "Please check out the " [link "new features"]]
                     [icon-button 
                      {:surface  :soft
                       :shape    :pill
                       :packing  :compact}
                      :close]]]

                   [callout
                    {:surface :faint
                     :stroke  :hard}
                    [flex-row (sx {:jc :sb})
                     [icon :info]
                     [:span "Please check out the " [link "new features"]]
                     [icon-button 
                      {:surface  :faint
                       :shape    :pill
                       :packing  :compact}
                      :close]]]

                   [callout
                    {:surface :faint}
                    [flex-row (sx {:jc :sb})
                     [icon :info]
                     [:span "Please check out the " [link "new features"]]
                     [icon-button 
                      {:surface  :faint
                       :shape    :pill
                       :packing  :compact}
                      :close]]]

                   [callout
                    {:surface :transparent
                     :stroke  :hard}
                    [flex-row (sx {:jc :sb})
                     [icon :info]
                     [:span "Please check out the " [link "new features"]]
                     [icon-button 
                      {:colorway :positive
                       :surface  :transparent
                       :shape    :pill
                       :packing  :compact}
                      :close]]]])}

     {:label     "Positive"
      :row-attrs (sx :flex-wrap--wrap :_.ks-callout:w--100%)
      :samples   (samples
                  [[callout
                    {:surface :solid
                     :colorway :positive}
                    [flex-row (sx {:jc :sb})
                     [icon :info]
                     [:span "Please check out the " [link "new features"]]
                     [icon-button 
                      {:colorway :positive
                       :surface  :solid
                       :shape    :pill
                       :packing  :compact}
                      :close]]]

                   [callout
                    {:surface :soft
                     :colorway :positive
                     :stroke  :hard}
                    [flex-row (sx {:jc :sb})
                     [icon :info]
                     [:span "Please check out the " [link "new features"]]
                     [icon-button 
                      {:colorway :positive
                       :surface  :soft
                       :shape    :pill
                       :packing  :compact}
                      :close]]]
                   
                   [callout
                    {:surface :soft
                     :colorway :positive}
                    [flex-row (sx {:jc :sb})
                     [icon :info]
                     [:span "Please check out the " [link "new features"]]
                     [icon-button 
                      {:colorway :positive
                       :surface  :soft
                       :shape    :pill
                       :packing  :compact}
                      :close]]]

                   [callout
                    {:surface :faint
                     :colorway :positive
                     :stroke  :hard}
                    [flex-row (sx {:jc :sb})
                     [icon :info]
                     [:span "Please check out the " [link "new features"]]
                     [icon-button 
                      {:colorway :positive
                       :surface  :faint
                       :shape    :pill
                       :packing  :compact}
                      :close]]]

                   [callout
                    {:surface :faint
                     :colorway :positive}
                    [flex-row (sx {:jc :sb})
                     [icon :info]
                     [:span "Please check out the " [link "new features"]]
                     [icon-button 
                      {:colorway :positive
                       :surface  :faint
                       :shape    :pill
                       :packing  :compact}
                      :close]]]

                   [callout
                    {:surface :transparent
                     :colorway :positive
                     :stroke  :hard}
                    [flex-row (sx {:jc :sb})
                     [icon :info]
                     [:span "Please check out the " [link "new features"]]
                     [icon-button 
                      {:colorway :positive
                       :surface  :transparent
                       :shape    :pill
                       :packing  :compact}
                      :close]]]])}
     

     {:label     "Warning"
      :row-attrs (sx :flex-wrap--wrap :_.ks-callout:w--100%)
      :samples   (samples
                  [[callout
                    {:surface :solid
                     :colorway :warning}
                    [flex-row (sx {:jc :sb})
                     [icon :info]
                     [:span "Please check out the " [link "new features"]]
                     [icon-button 
                      {:colorway :warning
                       :surface  :solid
                       :shape    :pill
                       :packing  :compact}
                      :close]]]

                   [callout
                    {:surface :soft
                     :colorway :warning
                     :stroke  :hard}
                    [flex-row (sx {:jc :sb})
                     [icon :info]
                     [:span "Please check out the " [link "new features"]]
                     [icon-button 
                      {:colorway :warning
                       :surface  :soft
                       :shape    :pill
                       :packing  :compact}
                      :close]]]
                   
                   [callout
                    {:surface :soft
                     :colorway :warning}
                    [flex-row (sx {:jc :sb})
                     [icon :info]
                     [:span "Please check out the " [link "new features"]]
                     [icon-button 
                      {:colorway :warning
                       :surface  :soft
                       :shape    :pill
                       :packing  :compact}
                      :close]]]

                   [callout
                    {:surface :faint
                     :colorway :warning
                     :stroke  :hard}
                    [flex-row (sx {:jc :sb})
                     [icon :info]
                     [:span "Please check out the " [link "new features"]]
                     [icon-button 
                      {:colorway :warning
                       :surface  :faint
                       :shape    :pill
                       :packing  :compact}
                      :close]]]

                   [callout
                    {:surface :faint
                     :colorway :warning}
                    [flex-row (sx {:jc :sb})
                     [icon :info]
                     [:span "Please check out the " [link "new features"]]
                     [icon-button 
                      {:colorway :warning
                       :surface  :faint
                       :shape    :pill
                       :packing  :compact}
                      :close]]]

                   [callout
                    {:surface :transparent
                     :colorway :warning
                     :stroke  :hard}
                    [flex-row (sx {:jc :sb})
                     [icon :info]
                     [:span "Please check out the " [link "new features"]]
                     [icon-button 
                      {:colorway :warning
                       :surface  :transparent
                       :shape    :pill
                       :packing  :compact}
                      :close]]]])}
     
     {:label     "Negative"
      :row-attrs (sx :flex-wrap--wrap :_.ks-callout:w--100%)
      :samples   (samples
                  [[callout
                    {:surface :solid
                     :colorway :negative}
                    [flex-row (sx {:jc :sb})
                     [icon :info]
                     [:span "Please check out the " [link "new features"]]
                     [icon-button 
                      {:colorway :negative
                       :surface  :solid
                       :shape    :pill
                       :packing  :compact}
                      :close]]]

                   [callout
                    {:surface  :soft
                     :colorway :negative
                     :stroke   :hard}
                    [flex-row (sx {:jc :sb})
                     [icon :info]
                     [:span "Please check out the " [link "new features"]]
                     [icon-button 
                      {:colorway :negative
                       :surface  :soft
                       :shape    :pill
                       :packing  :compact}
                      :close]]]

                   [callout
                    {:surface :soft
                     :colorway :negative}
                    [flex-row (sx {:jc :sb})
                     [icon :info]
                     [:span "Please check out the " [link "new features"]]
                     [icon-button 
                      {:colorway :negative
                       :surface :soft
                       :shape    :pill
                       :packing  :compact}
                      :close]]]

                   [callout
                    {:surface :faint
                     :colorway :negative
                     :stroke  :hard}
                    [flex-row (sx {:jc :sb})
                     [icon :info]
                     [:span "Please check out the " [link "new features"]]
                     [icon-button 
                      {:colorway :negative
                       :surface  :faint
                       :shape    :pill
                       :packing  :compact}
                      :close]]]
                   
                   [callout
                    {:surface :faint
                     :colorway :negative}
                    [flex-row (sx {:jc :sb})
                     [icon :info]
                     [:span "Please check out the " [link "new features"]]
                     [icon-button 
                      {:colorway :negative
                       :surface  :faint
                       :shape    :pill
                       :packing  :compact}
                      :close]]]

                   [callout
                    {:surface  :transparent
                     :colorway :negative
                     :stroke   :hard}
                    [flex-row (sx {:jc :sb})
                     [icon :info]
                     [:span "Please check out the " [link "new features"]]
                     [icon-button 
                      {:colorway :negative
                       :surface  :transparent
                       :shape    :pill
                       :packing  :compact}
                      :close]]]])}]


    #_[{:label     "Text sizes from xxsmall to xlarge"
      :row-style row-style
      :require   require
      :samples   (samples-with-variant 
                  {:variant         :text-size
                   :variant-scale   :text-size/xxsmall-xlarge
                   :attrs           {:header-text [:span "Please check out the "
                                                   [link (merge-attrs (sx :ws--n)
                                                                      {:href "#"})
                                                    "new features"]]
                                     :header-icon :info
                                     :colorway    :accent
                                     :surface     :faint
                                     :inert      true}
                   :variant-labels? false})}
     
     {:label     "With icon and dismiss button, in positive variant"
      :row-style row-style
      :variants- [:filled :bordered]
      :samples   (samples
                  [[callout
                    {:header-icon     [icon :check-circle]
                     :colorway        :positive
                     :header-text     "Your transaction was successful."
                     :close-button?   true
                     :close-button-fn (fn [] [:div "hi"])}]])}
     
     {:label     "Neutral variant"
      :row-style row-style
      :require   require
      :samples   (samples-with-variant 
                  {:variant         :surface
                   :variant-scale   variant-scale
                   :attrs           {:header-text [:span "Please check out the "
                                                   [link (merge-attrs (sx :ws--n)
                                                                      {:href "#"})
                                                    "new features"]]
                                     :header-icon :info
                                     :colorway    :neutral
                                     :inert      true}
                   :variant-labels? false})}
     
     {:label     "Accent variant"
      :row-style row-style
      :require   require
      :samples   (samples-with-variant 
                  {:variant         :surface
                   :variant-scale   variant-scale
                   :attrs           {:header-text [:span "Please check out the "
                                                   [link (merge-attrs (sx :ws--n)
                                                                      {:href "#"})
                                                    "new features"]]
                                     :header-icon :info
                                     :colorway    :accent
                                     :inert      true}
                   :variant-labels? false})}
     
     {:label     "Positive variant"
      :row-style row-style
      :require   require
      :samples   (samples-with-variant 
                  {:variant         :surface
                   :variant-scale   variant-scale
                   :attrs           {:header-text "Your transaction was successful"
                                     :header-icon :info
                                     :colorway    :positive
                                     :inert      true}
                   :variant-labels? false})}

     {:label     "Warning variant"
      :row-style row-style
      :require   require
      :samples   (samples-with-variant 
                  {:variant         :surface
                   :variant-scale   variant-scale
                   :attrs           {:header-text [:span "Time to update. "
                                                   [link (merge-attrs (sx :ws--n)
                                                                      {:href "#"})
                                                    "Take action"]]
                                     :header-icon :info
                                     :colorway    :warning
                                     :inert      true}
                   :variant-labels? false})}
     
     {:label     "Negative variant"
      :row-style row-style
      :require   require
      :samples   (samples-with-variant 
                  {:variant         :surface
                   :variant-scale   variant-scale
                   :attrs           {:header-text [:span "Something went wrong. "
                                                   [link (merge-attrs (sx :ws--n)
                                                                      {:href "#"})
                                                    "Learn more"]]
                                     :header-icon :info
                                     :colorway    :negative
                                     :inert      true}
                   :variant-labels? false})}


            ;; TODO - maybe put in more of these
     
            ;; {:name    user-actions
            ;;  :schema  fn?
            ;;  :default nil
            ;;  :desc    "Component rendering fn for CTA interactions. Can also be
            ;;           a close button component via
            ;;           `kushi.ui.callout/close-button`. Optional."}
     
            ;; {:name    header-text
            ;;  :schema  string
            ;;  :default nil
            ;;  :desc    "The header text to render in the callout. Optional."}
     
            ;; {:name    colorway
            ;;  :schema  #{:neutral :accent :positive :negative :warning}
            ;;  :default nil
            ;;  :desc    "Colorway of the callout. Can also be a named color from
            ;;           Kushi's design system, e.g `:red`, `:purple`, `:gold`,
            ;;           etc."}
     
            ;; {:name    surface
            ;;  :schema  #{:faint :solid :minimal :outline}
            ;;  :default :round
            ;;  :desc    "Surface variant of the callout."}
     
            ;; {:name    shape
            ;;  :schema  #{:sharp :round :pill}
            ;;  :default :round
            ;;  :desc    "Shape of the callout."}
     
            ;; {:name    packing
            ;;  :schema  #{:compact :roomy}
            ;;  :default nil
            ;;  :desc    "General amount of padding inside the callout"}
     
          ;;  Leave this out for now
          ;;   {:name    duration
          ;;    :schema    pos-int?
          ;;    :default nil
          ;;    :desc    ["When supplied, the callout will dismiss itself after "
          ;;              "the given time (in milliseconds) has passed."]}
     
     ]))

#_(def examples
  (let [row-attrs         
        (sx :.fooosball
            :_.instance-code:w--100%
            :_.instance-code:w--100%
            :_.instance-code:flex-direction--column
            :md:_.instance-code:flex-direction--column
            :w--100%
            :w--100%
            :flex-direction--column
            :md:flex-direction--column)

        container-attrs   
        (sx :gtc--1fr)

        semantic-variants 
        (for [[s msg]
              [["neutral" [:span "Please check out the "
                           [link (merge-attrs (sx :ws--n) {:href "#"})
                            "new features"]]]
               ["accent" [:span "Please check out the "
                          [link (merge-attrs (sx :ws--n) {:href "#"})
                           "new features"]]]
               ["positive" "Your transaction was successful"]
               ["warning" [:span "Your subscription needs to be updated. "
                           [link (merge-attrs (sx :ws--n) {:href "#"})
                            "Take action."]]]
               ["negative" [:span "Something went wrong. "
                            [link (merge-attrs (sx :ws--nw) {:href "#"})
                             "Learn more."]]]]]
          {:desc            (str (string/capitalize s) " variant")
           :row-attrs       row-attrs
           :container-attrs container-attrs
           :examples        (for [surface #{:faint :solid :outline}]
                              {:attrs {:header-text msg
                                       :icon        [icon :info]
                                       :colorway    s
                                       :surface     surface}})})]


      (into [{:desc            "Text sizes from xxsmall to xlarge"
              :row-attrs       row-attrs #_(sx :md:ai--fe)
              :container-attrs container-attrs
              :snippets        '[[callout
                                  (merge-attrs
                                   (sx :.large)
                                   {:header-text [:span "Please check out the "
                                                   [link (merge-attrs (sx :ws--n)
                                                                      {:href "#"})
                                                    "new features"]]
                                    :colorway    :accent
                                    :icon        [icon :info]})]]
              :examples          [{:code (sx-call
                                          (for [sz sizes]
                                            [callout {:header-text [:span "Please check out the "
                                                                    [link (merge-attrs
                                                                           (sx :ws--n)
                                                                           {:href "#"})
                                                                     "new features"]]
                                                      :icon        [icon :info]
                                                      :colorway    :accent
                                                      :class       [sz]}]))}]}

             {:desc            "With icon and dismiss button, in positive variant"
              :row-attrs       row-attrs
              :container-attrs container-attrs
              :variants-       [:filled :bordered]
              :examples        [{:code (sx-call 
                                        [callout
                                         {:icon         [icon :check-circle]
                                          :colorway     :positive
                                          :header-text  "Your transaction was successful."
                                          ;; :user-actions callout-close-button
                                          }])}]}]
             semantic-variants)))

