(ns ^{:kushi/layer "user-styles"}
  kushi.ui.switch.demo
  (:require [kushi.core :refer (sx)]
            [kushi.ui.icon :refer [icon]]
            [kushi.showcase.core
             :as showcase
             :refer [samples samples-with-variant]]
            [kushi.ui.switch :refer [switch]]))

(def demos
  [{:label   "Basic"
    :desc    "Basic"
    :samples (samples [[:div (sx :.flex-row-start :gap--1rem)
                        [switch {:colorway :neutral
                                 :sizing   :xxlarge}]
                        [switch {:colorway :neutral
                                 :sizing   :xxlarge
                                 :on?      true}]]])}

   {:samples (samples-with-variant
              {:variant         :colorway,
               :variant-labels? false
               :variant-scale   :colorway/semantic,
               :label           "Colorway",
               :row-style       {:justify-content :flex-start
                                 :gap             :1rem}
               :attrs           {:sizing :xxlarge
                                 :on?    true}})}

   {:samples (samples-with-variant
              {:variant :sizing,
               :label   "Sizing",
               :attrs   {:on? true}})}

   ;; why rows not working?
   {:samples (samples-with-variant
              {:variant         :sizing
               :variant-labels? false
               :variant-scale   :sizing/xsmall-xxlarge,
               :row-style       {:justify-content :flex-start
                                 :gap             :1rem}
               :label           "Convex thumb",
               :attrs           {:on?         true
                                 :thumb-attrs {:surface :convex}}})}

   {:samples (samples-with-variant
              {:variant         :sizing
               :variant-labels? false
               :variant-scale   :sizing/xsmall-xxlarge,
               :row-style       {:justify-content :flex-start
                                 :gap             :1rem}
               :label           "Oversized thumb",
               :attrs           {:on?                true
                                 :thumb-scale-factor 1.25
                                 :thumb-attrs        {:surface :minimal
                                                      :stroke  :medium}}})}

   {:samples (samples-with-variant
              {:variant         :sizing
               :variant-labels? false
               :variant-scale   :sizing/xsmall-xxlarge,
               :row-style       {:justify-content :flex-start
                                 :gap             :1rem}
               :label           "Labeled track",
               :attrs           {:on?                true
                                 :thumb-scale-factor 1.25
                                 :thumb-content-on   "ON"
                                 :thumb-content-off  "OFF"
                                 :thumb-attrs        {:surface :minimal
                                                      :stroke  :medium}}})}
   
   {:samples (samples-with-variant
              {:variant         :sizing
               :variant-labels? false
               :variant-scale   :sizing/xsmall-xxlarge,
               :row-style       {:justify-content :flex-start
                                 :gap             :1rem}
               :label           "Labeled track",
               :attrs           {:on?                true
                                 :thumb-scale-factor 1.25
                                 :thumb-content-on   "ON"
                                 :thumb-content-off  "OFF"
                                 :thumb-attrs        {:surface :minimal
                                                      :stroke  :medium}}})}
   
   {:samples (samples-with-variant
              {:variant         :sizing
               :variant-labels? false
               :variant-scale   :sizing/xsmall-xxlarge,
               :row-style       {:justify-content :flex-start
                                 :gap             :1rem}
               :label           "Labeled track",
               :attrs           {:on?                true
                                 :switch-width-ratio 2.25
                                 :track-content-on   "ON"
                                 :track-content-off  "OFF"
                                 :thumb-attrs        {:surface :minimal
                                                      :stroke  :medium}}})}
   #_#_#_#_

           {:samples (samples-with-variant
                      {:args          ["Next"],
                       :variant       :contour,
                       :variant-scale :contour/basic,
                       :x-variants    [:surface],
                       :snippets?     false
              ;;  :attrs/snippet  {:surface :solid},
               ;; :attrs/display  {:sizing :small},
              ;;  :label          "Colorways, surfaces × contours",
                       :label         "Contour × surfaces",
                       :attrs         {:end-enhancer :east
                                       :colorway     :accent},
                       :rows?         true})}


         {:label   "With label",
          :samples (samples
                    [#_[:div (sx {:border-radius :$rounded
                                  :w             :fit-content
                                  :bgc           :transparent
                                  :p             :1em
                                  :pie           :1.5em
                                  :b             :1px:solid:$neutral-150
                                  :dark:b        :1px:solid:$neutral-850})
                        [checkbox "Sign me up"]]
                     [checkbox "Sign me up"]])}

       {:label   "With label and traling icon",
        :samples (samples
                  [#_[:div (sx {:border-radius :$rounded
                                :w             :fit-content
                                :bgc           :transparent
                                :p             :1em
                                :pie           :1.5em
                                :b             :1px:solid:$neutral-150
                                :dark:b        :1px:solid:$neutral-850})
                      [checkbox "Sign me up"]]
                   [checkbox "Make it shiny" [icon :auto-awesome]]])}
     
     {:label     "Weight variants extra-light to extra-bold",
      :row-style {:flex-direction :column
                  :align-items    :flex-start
                  :gap            :2rem}
      :samples   (samples-with-variant
                  {:variant         :weight
                   :variant-labels? false
                   :args            ["Make it shiny" [icon :auto-awesome]]})}])

#_(def examples
  (let [row-attrs       (sx :.kushi-playground-switch-example-row
                            :xsm:ai--fe
                            :xsm:flex-direction--row )
        container-attrs (sx :.playground-switch-rows-container
                            :gtc--max-content:max-content
                            :xsm:gtc--max-content
                            )]
    [{:desc            "Colorway variants"
      :sx-attrs        (sx-call (sx :.xxlarge))
      :row-attrs       row-attrs
      :container-attrs container-attrs
      :snippets-header ["Use the `data-ks-colorway` attributes `:neutral`, `:accent`, `:positive`, `:warning`, and `:negative` to control the semantic color variant."
                        :br
                        :br
                        "The default is `:.neutral`."]                        
      :snippets        '[[:div
                          [switch]
                          [switch {:colorway :neutral}]
                          [switch {:colorway :accent}]
                          [switch {:colorway :positive}]
                          [switch {:colorway :warning}]
                          [switch {:colorway :negative}]]]
      :examples        (for [s component-examples/colors]
                         {:label (name s)
                          :attrs {:colorway s}})}
     
     ;; Leave this off until you figure out dark theme styling w/new color paradigm
     #_{:desc            "Semantic variants, outline styling"
        :sx-attrs        (sx-call (sx
                                   :.xxlarge
                                   :--switch-border-color--$gray-400
                                   :--switch-border-width--1.5px
                                   :--switch-off-background-color--white
                                   ["bgc" :transparent!important]
                                   ["hover:bgc" :transparent!important]
                                   ["[aria-checked='true']:bc" :currentColor]
                                   ["[aria-checked='true']:hover:bc" :currentColor]
                                   {:themb-attrs (sx
                                                   :.elevated-0!
                                                   [:border
                                                    "calc(var(--switch-border-width) * (1 / var(--switch-thumb-scale-factor))) solid transparent"]
                                                   [:bgc
                                                    :$switch-border-color]
                                                   ["has-ancestor(.kushi-switch[aria-checked='true']):bgc"
                                                    :currentColor])}))

        :row-attrs       row-attrs
        :container-attrs container-attrs
        :examples        (for [s component-examples/colors]
                           {:label (name s)
                            :attrs {:class [s]}})}

     (merge 
      (component-examples/sizes-snippet-scale 'switch)
      {:desc            "Different sizes from small to xxxlarge"
       :row-attrs       row-attrs
       :container-attrs container-attrs
       :examples        (for [sz sizes]
                          {:label (name sz)
                           :attrs {:class sz}})})

     {:desc            "With convex-styled thumb control"
      :row-attrs       row-attrs
      :container-attrs container-attrs
      :sx-attrs        (sx-call {:themb-attrs (sx :bgi--$convex
                                                   :dark:bgi--$convex-3)})
      :snippets        '[[switch {:themb-attrs (sx :bgi--$convex
                                                    :dark:bgi--$convex-3)}]]
      :examples        (for [sz sizes]
                         {:label (name sz)
                          :attrs {:class [sz]}})}

     {:desc            "With oversized thumb control"
      :row-attrs       row-attrs
      :container-attrs container-attrs
      :sx-attrs        (sx-call (merge-attrs
                                 (sx
                                  [:--switch-border-width :0px]
                                  [:--switch-thumb-scale-factor :1.25])
                                 {:themb-attrs (sx :border--1px:solid:currentColor)}))
      :snippets        '[[switch (merge-attrs
                                  (sx
                                   [:--switch-border-width :0px]
                                   [:--switch-thumb-scale-factor :1.25])
                                  {:themb-attrs (sx :border--1px:solid:currentColor)})]]
      :examples        (for [sz sizes]
                         {:label (name sz)
                          :attrs {:class [sz]}})}

     {:desc            "With labeled track"
      :row-attrs       row-attrs
      :container-attrs container-attrs
      :sx-attrs        (sx-call (merge-attrs (sx
                                              [:--switch-width-ratio :2.25])
                                             {:track-content-on  "ON"
                                              :track-content-off "OFF"}))
      :snippets        '[[switch (merge-attrs (sx
                                               [:--switch-width-ratio :2.25])
                                              {:track-content-on  "ON"
                                               :track-content-off "OFF"})]]
      :examples        (for [sz sizes]
                         {:label (name sz)
                          :attrs {:class [sz]}})}

     {:desc            "With labeled thumb"
      :row-attrs       row-attrs
      :container-attrs container-attrs
      :sx-attrs        (sx-call {:thumb-content-on  [:span (sx :.semi-bold :fs--0.3em) "ON"]
                                 :thumb-content-off [:span (sx :.semi-bold :fs--0.3em) "OFF"]})

      :snippets        '[[switch {:thumb-content-on  [:span (sx :.semi-bold :fs--0.3em) "ON"]
                                  :thumb-content-off [:span (sx :.semi-bold :fs--0.3em) "OFF"]}]]
      :examples        (for [sz (drop 2 sizes)]
                         {:label (name sz)
                          :attrs {:class [sz]}})}
     
     {:desc            "With icon track"
      :reqs            '[[kushi.ui.icon :refer [icon]]]
      :row-attrs       row-attrs
      :container-attrs container-attrs
      :sx-attrs        (sx-call {:track-content-on  [icon (merge-attrs (sx :fs--0.55em)
                                                                        {:icon-filled? true})
                                                      :visibility]
                                 :track-content-off [icon (merge-attrs (sx :fs--0.55em)
                                                                        {:icon-filled? true})
                                                      :visibility-off]})

      :snippets        '[[switch {:track-content-on  [icon (merge-attrs (sx :fs--0.55em)
                                                                         {:icon-filled? true})
                                                       :visibility]
                                  :track-content-off [icon (merge-attrs (sx :fs--0.55em)
                                                                         {:icon-filled? true})
                                                       :visibility-off]}]]
      :examples        (for [sz sizes]
                         {:label (name sz)
                          :attrs {:class [sz]}})}

     {:desc            "With icon thumb"
      :reqs            '[[kushi.ui.icon :refer [icon]]]
      :row-attrs       row-attrs
      :container-attrs container-attrs
      :sx-attrs        (sx-call {:thumb-content-on  [icon (merge-attrs (sx :fs--0.55em)
                                                                        {:icon-filled? true})
                                                      :visibility]
                                 :thumb-content-off [icon (merge-attrs (sx :fs--0.55em)
                                                                        {:icon-filled? true})
                                                      :visibility-off]})

      :snippets        '[[switch {:thumb-content-on  [icon (merge-attrs (sx :fs--0.55em)
                                                                         {:icon-filled? true})
                                                       :visibility]
                                  :thumb-content-off [icon (merge-attrs (sx :fs--0.55em)
                                                                         {:icon-filled? true})
                                                       :visibility-off]}]]
      :examples        (for [sz sizes]
                         {:label (name sz)
                          :attrs {:class [sz]}})}

     {:desc            "Disabled state"
      :row-attrs       row-attrs
      :container-attrs container-attrs
      :snippets        '[[switch {:disabled true}]]
      :examples        (for [sz sizes]

                         {:label (name sz)
                          :attrs {:disabled true
                                  :class    [sz]}})}]))
