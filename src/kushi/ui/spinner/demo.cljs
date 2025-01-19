(ns ^{:kushi/layer "user-styles"} kushi.ui.spinner.demo
  (:require 
   [fireworks.core :refer [? !? ?> !?>]]
   [kushi.css.core :refer [sx css merge-attrs css-vars-map]]
   [kushi.playground.component-examples :as component-examples :refer [sizes-snippet-map]]
   [kushi.playground.util :refer-macros [sx-call]]
   [kushi.ui.button.core :refer [button]]
   [kushi.ui.icon.core :refer [icon]]
   [kushi.ui.spinner.core :refer [spinner
                                  propeller
                                  donut
                                  thinking]]))

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

        row-attrs
        (sx [:_.instance-code {:w  :100%
                               :d  :flex
                               :jc :sb
                               :pi :0.75rem}])

        row-attrs-all-colors
        (sx [:_.instance-code {:w              :100%
                               :pi             :0.75rem
                               :row-gap        :2rem
                               :flex-direction :column
                               :align-items    :stretch}])

        button-example-code
        (sx-call [button 
                  (merge-attrs 
                   (sx :fs--$small)
                   {:on-click
                    (fn [e]
                      (let [el       (-> e .-target)
                            loading? (= "loading" (.-ariaLabel el))] 
                        (if loading?
                          (do (.removeAttribute el "aria-label")
                              (.removeAttribute el "data-kushi-ui-spinner"))
                          (do (.setAttribute el
                                             "aria-label"
                                             "loading")
                              (.setAttribute el
                                             "data-kushi-ui-spinner"
                                             true)))))})
                  [spinner [icon :play-arrow] [donut]]
                  "Activate"])

        f
        (fn [desc row-attrs code snippets-map]
          (merge {:desc            desc
                  :container-attrs container-attrs
                  :row-attrs       row-attrs
                  :examples        [{:code code}]}
                 snippets-map))

        all-the-colors-snippet-map
        (fn [sym]
          {:snippets-header
           ["The css `color` property controls the color."
            "Kushi provides color tokens in value ranges from `50` ~ `1000`, in increments of `50`."
            "E.g. `:c--$blue-50`, `:c--$blue-350`, `:c--$blue-800`, etc."
            :br
            "You can also use any valid css color value e.g. `$c--#8a8a8a`"
            :br
            :br
            "Example rows below, in all the colors."
            :br
            :br
            "The first example sets the `:color` property directly."
            :br
            :br
            "The second example sets a `:.foreground-<color>` class, which provides a different shade of color depending on light or dark mode."
            ]

           :snippets        
           [[:div
             [sym '(sx :c--$gray-500)]
             [sym '(sx :c--$blue-500)]
             [sym '(sx :c--$green-500)]
             [sym '(sx :c--$yellow-500)]
             [sym '(sx :c--$red-500)]
             [sym '(sx :c--$purple-500)]
             [sym '(sx :c--$lime-500)]
             [sym '(sx :c--$brown-500)]
             [sym '(sx :c--$orange-500)]
             [sym '(sx :c--$magenta-500)]]

            [:div
             [sym '(sx :.foreground-gray)]
             [sym '(sx :.foreground-blue)]
             [sym '(sx :.foreground-green)]
             [sym '(sx :.foreground-yellow)]
             [sym '(sx :.foreground-red)]
             [sym '(sx :.foreground-purple)]
             [sym '(sx :.foreground-lime)]
             [sym '(sx :.foreground-brown)]
             [sym '(sx :.foreground-orange)]
             [sym '(sx :.foreground-magenta)]]
            
            ]})

         example-colors
         (map #(->> (get component-examples/color-lut % %)
                    (str "foreground-"))
              (concat component-examples/colors
                      component-examples/non-semantic-colors))]


    [{:desc      "Usage with a button."
      :row-attrs row-attrs
      ;; :reqs     '[[kushi.ui.button.core :refer [button]]]
      :snippets  [(:quoted button-example-code)]
      :examples  [{:code button-example-code}]}
     
     (f "Propeller, xxsmall to xxxlarge"
        row-attrs
        (sx-call (for [sz sizes]
                   [propeller {:class [sz]}]))
        (sizes-snippet-map 'propeller))
     
     (f "Propeller, all the colors"
        row-attrs-all-colors
        (sx-call 
         (into
          [:div (sx :.flex-row-fs :jc--sb)]
          (for [fg-color-class example-colors]
            [propeller (sx :fs--$large fg-color-class)])))

        (all-the-colors-snippet-map 'propeller))

     (f "Donut, xxsmall to xxxlarge"
        row-attrs 
        (sx-call (for [sz sizes]
                   [donut {:class [sz]}]))
        (sizes-snippet-map 'donut))
     
     (f "Donut, all the colors"
        row-attrs-all-colors 
        (sx-call 
         (into [:div (sx :.flex-row-fs :jc--sb)]
               (for [fg-color-class example-colors]
                 [donut (sx :fs--$large fg-color-class)])))
        (all-the-colors-snippet-map 'donut))

     (f "Thinking, small to xxxlarge"
        row-attrs
        (sx-call (for [sz (drop 2 sizes)]
                   [thinking {:class [sz]}]))
        (sizes-snippet-map 'thinking))

     
     (f "Thinking, all the colors"
        row-attrs-all-colors 
        (sx-call 
         (into [:div (sx :.flex-row-fs :jc--sb)]
               (for [fg-color-class example-colors]
                 [thinking (sx :fs--$medium fg-color-class)])))
        (all-the-colors-snippet-map 'thinking))]))


