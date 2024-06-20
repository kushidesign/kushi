(ns kushi.ui.spinner.demo
  (:require 
            [kushi.core :refer (sx)]
            [kushi.playground.component-examples :as component-examples]
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
        (sx [:&_.instance-code
             {:w  :100%
              :d  :flex
              :jc :sb
              :pi :0.75rem}])

        row-attrs-all-colors
        (sx [:&_.instance-code
             {:w              :100%
              :pi             :0.75rem
              :row-gap        :2rem
              :flex-direction :column
              :align-items    :stretch}])

        f
        (fn [desc row-attrs code]
          {:desc            desc
           :container-attrs container-attrs
           :row-attrs       row-attrs
           :examples        [{:code code}]})]

    [{:desc      "Usage with a button."
      :row-attrs row-attrs
      :examples  [{:code (sx-call [button 
                                   (sx :.small
                                       {:on-click (fn [e]
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
                                   [spinner 
                                    [icon :play-arrow] [donut]]
                                   "Activate"])}]}
     
     (f "Propeller, xxsmall to xxxlarge"
        row-attrs
        (sx-call (for [sz sizes]
                   [propeller {:class [sz]}])))
     
     (f "Propeller, all the colors"
        row-attrs-all-colors
        (sx-call 
         (for [color (concat component-examples/colors
                             component-examples/non-semantic-colors)]
           (into [:div (sx :.flex-row-fs :jc--sb)]
                 (for [val (range 300 1100 100)]
                   [:div (sx :.flex-col-fs) 
                    [propeller {:class [:large]
                                :style {:color (str "var(--" color "-" val ")")}}]])))))

     (f "Spinner, xxsmall to xxxlarge"
        row-attrs (sx-call (for [sz sizes]
                             [donut {:class [sz]}])) )
     
     (f "Spinner, all the colors"
        row-attrs-all-colors 
        (sx-call 
         (for [color (concat component-examples/colors
                             component-examples/non-semantic-colors)]
           (into [:div (sx :.flex-row-fs :jc--sb)]
                 (for [val (range 300 1100 100)]
                   [:div (sx :.flex-col-fs)
                    [donut {:class [:large]
                            :style {:color (str "var(--" color "-" val ")")}}]])))) )

     (f "Thinking, small to xxxlarge"
        row-attrs
        (sx-call (for [sz (drop 2 sizes)]
                   [thinking {:class [sz]}])))

     
     (f "Thinking, all the colors"
        row-attrs-all-colors 
        (sx-call 
         (for [color (concat component-examples/colors
                             component-examples/non-semantic-colors)]
           (into [:div (sx :.flex-row-fs :jc--sb)]
                 (for [val (range 400 1000 100)]
                   [:div (sx :.flex-col-fs)
                    [thinking {:class [:medium]
                               :style {:color (str "var(--" color "-" val ")")}}]])))) )]))


