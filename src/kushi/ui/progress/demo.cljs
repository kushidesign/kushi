(ns kushi.ui.progress.demo
  (:require [fireworks.core :refer [?]]
            [kushi.core :refer (sx merge-attrs)]
            [kushi.playground.component-examples :as component-examples]
            [kushi.playground.util :refer-macros [sx-call]]
            [kushi.ui.button.core :refer [button]]
            [kushi.ui.icon.core :refer [icon]]
            [kushi.ui.progress.core :refer [progress
                                            propeller
                                            donut
                                            thinking]]))


(declare progress-examples)

(defn demo [component-opts]
  (into [:<>]
        (for [
              ;; example-opts (take 1 progress-examples)
              example-opts progress-examples
              ;; example-opts (keep-indexed (fn [idx m] (when (contains? #{9} idx) m)) progress-examples)
              ]
          [component-examples/examples-section
           component-opts
           example-opts])))

(def spinner-sizes
  [:xxsmall
   :xsmall
   :small
   :medium
   :large
   :xlarge
   :xxlarge
   :xxxlarge])

(def progress-examples
  (let [row-attrs
        (sx [:&_.playground-component-example-row-instance-code
             {:pis        :0.5em
              :column-gap :5rem
              :row-gap    :2rem
              :display    :grid
              :gtc        :1fr:1fr:1fr:1fr}])
        row-attrs-all-colors
        (sx [:&_.playground-component-example-row-instance-code
             {:pis            :0.5em
              :gap            :1rem
              :flex-direction :column}])]

    [{:desc      "Usage with a button."
      :row-attrs row-attrs
      :examples  [{:code (sx-call [button 
                                   (sx :.small
                                       {:on-click (fn [e]
                                                    (let [el       (-> e .-target)
                                                          loading? (= "loading" (.-ariaLabel el))] 
                                                      (if loading?
                                                        (do (.removeAttribute el "aria-label")
                                                            (.removeAttribute el "data-kushi-ui-progress"))
                                                        (do (.setAttribute el
                                                                           "aria-label"
                                                                           "loading")
                                                            (.setAttribute el
                                                                           "data-kushi-ui-progress"
                                                                           true)))))})
                                   [progress 
                                    [icon :play-arrow] [donut]]
                                   "Activate"])}]}
     
     {:desc      "Propeller, xxsmall to xxxlarge"
      :row-attrs row-attrs
      :examples  [{:code (sx-call (for [sz spinner-sizes]
                                    [propeller {:class [sz]}]))}]}
     
     {:desc      "Propeller, all the colors"
      :row-attrs row-attrs-all-colors 
      :examples  [{:code (sx-call 
                          (for [color (concat component-examples/colors
                                              component-examples/non-semantic-colors)]
                            (into [:div (sx :.flex-row-fs
                                            :ai--fe
                                            :gap--2.25rem)]
                                  (for [val (range 100 1100 100)]
                                    [:div (sx :.flex-col-fs) 
                                     [propeller {:class [:large]
                                                 :style {:color (str "var(--" color "-" val ")")}}]]))))}]}

     {:desc      "Spinner, xxsmall to xxxlarge"
      :row-attrs (merge-attrs row-attrs
                              (sx :&_.playground-component-example-row-instance-code:column-gap--4.5rem))
      :examples  [{:code (sx-call (for [sz spinner-sizes]
                                    [donut {:class [sz]}]))}]}
     
     
     {:desc      "Spinner, all the colors"
      :row-attrs row-attrs-all-colors 
      :examples  [{:code (sx-call 
                          (for [color (concat component-examples/colors
                                              component-examples/non-semantic-colors)]
                            (into [:div (sx :.flex-row-fs
                                            :ai--fe
                                            :gap--1.33rem)]
                                  (for [val (range 100 1100 100)]
                                    [:div (sx :.flex-col-fs)
                                     [donut {:class [:large]
                                               :style {:color (str "var(--" color "-" val ")")}}]]))))}]}

     {:desc      "Thinking, xxsmall to xxxlarge"
      :row-attrs (merge-attrs row-attrs
                              (sx :&_.playground-component-example-row-instance-code:column-gap--2.25rem))
      :examples  [{:code (sx-call (for [sz spinner-sizes]
                                    [thinking {:class [sz]}]))}]}

     
     {:desc      "Thinking, all the colors"
      :row-attrs row-attrs-all-colors 
      :examples  [{:code (sx-call 
                          (for [color (concat component-examples/colors
                                              component-examples/non-semantic-colors)]
                            (into [:div (sx :.flex-row-fs
                                            :ai--fe
                                            :gap--1.4rem)]
                                  (for [val (range 200 1000 100)]
                                    [:div (sx :.flex-col-fs)
                                     [thinking {:class [:medium]
                                               :style {:color (str "var(--" color "-" val ")")}}]]))))}]}]))


