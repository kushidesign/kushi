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
   [kushi.ui.icon :refer [icon]]
   [kushi.ui.link :refer [link]]))

(def sizes
  [:xxsmall
   :xsmall
   :small
   :medium
   :large])

(def demos
  (let [row-style     {:flex-direction  :column
                       :justify-content :flex-start
                       :gap             :1rem
                       :max-width       :605px}
        require       '[[kushi.core :refer (sx merge-attrs)]
                        [kushi.ui.icon :refer [icon]]
                        [kushi.ui.link :refer [link]]]
        variant-scale [:faint :soft :solid :outline]]

    [{:label     "Sizes from xxsmall to xlarge"
      :row-style row-style
      :require   require
      :samples   (samples-with-variant 
                  {:variant         :sizing
                   :variant-scale   :sizing/xxsmall-xlarge
                   :attrs           {:header-text [:span "Please check out the "
                                                   [link (merge-attrs (sx :ws--n)
                                                                      {:href "#"})
                                                    "new features"]]
                                     :header-icon :info
                                     :colorway    :accent
                                     :surface     :faint
                                     :inert?      true}
                   :variant-labels? false})}
     
     {:label     "With icon and dismiss button, in positive variant"
      :row-style row-style
      :variants- [:filled :bordered]
      :samples   (samples
                  [[callout
                    {:header-icon   [icon :check-circle]
                     :colorway      :positive
                     :header-text   "Your transaction was successful."
                     :close-button? true
                     :user-actions  (fn [] [:div "hi"])}]])}
     
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
                                     :inert?      true}
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
                                     :inert?      true}
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
                                     :inert?      true}
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
                                     :inert?      true}
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
                                     :inert?      true}
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

(def examples
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


      (into [{:desc            "Sizes from xxsmall to xlarge"
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

