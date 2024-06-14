(ns kushi.ui.spinner.demo
  (:require [fireworks.core :refer [?]]
            [kushi.core :refer (sx merge-attrs)]
            [kushi.playground.component-examples :as component-examples]
            [kushi.playground.util :refer-macros [sx-call]]
            [kushi.ui.button.core :refer [button]]
            [kushi.ui.icon.core :refer [icon]]
            [kushi.ui.spinner.core :refer [spinner
                                            propeller
                                            donut
                                            thinking]]))


(declare spinner-examples)

(defn demo [component-opts]
  (into [:<>]
        (for [
              ;; example-opts (take 1 spinner-examples)
              example-opts spinner-examples
              ;; example-opts (keep-indexed (fn [idx m] (when (contains? #{9} idx) m)) spinner-examples)
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

(def spinner-examples
  (let [row-attrs
        (sx [:&_.playground-component-example-row-instance-code
             {:pis        :0.5em
              :column-gap :5.15rem
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
                                  (for [val (range 300 1100 100)]
                                    [:div (sx :.flex-col-fs) 
                                     [propeller {:class [:large]
                                                 :style {:color (str "var(--" color "-" val ")")}}]]))))}]}

     {:desc      "Spinner, xxsmall to xxxlarge"
      :row-attrs (merge-attrs row-attrs
                              (sx :&_.playground-component-example-row-instance-code:column-gap--3.25rem))
      :examples  [{:code (sx-call (for [sz spinner-sizes]
                                    [donut {:class [sz]}]))}]}
     
     
     {:desc      "Spinner, all the colors"
      :row-attrs row-attrs-all-colors 
      :examples  [{:code (sx-call 
                          (for [color (concat component-examples/colors
                                              component-examples/non-semantic-colors)]
                            (into [:div (sx :.flex-row-fs
                                            :ai--fe
                                            :gap--1.35rem)]
                                  (for [val (range 300 1100 100)]
                                    [:div (sx :.flex-col-fs)
                                     [donut {:class [:large]
                                               :style {:color (str "var(--" color "-" val ")")}}]]))))}]}

     {:desc      "Thinking, xxsmall to xxxlarge"
      :row-attrs (merge-attrs row-attrs
                              (sx :&_.playground-component-example-row-instance-code:column-gap--1.25rem
                                  :&_.playground-component-example-row-instance-code:translate---1rem:0))
      :examples  [{:code (sx-call (for [sz spinner-sizes]
                                    [thinking {:class [sz]}]))}]}

     
     {:desc      "Thinking, all the colors"
      :row-attrs (merge-attrs row-attrs-all-colors
                              (sx :&_.playground-component-example-row-instance-code:row-gap--1.75rem)) 
      :examples  [{:code (sx-call 
                          (for [color (concat component-examples/colors
                                              component-examples/non-semantic-colors)]
                            (into [:div (sx :.flex-row-fs
                                            :ai--fe
                                            :gap--1.6rem)]
                                  (for [val (range 400 1000 100)]
                                    [:div (sx :.flex-col-fs)
                                     [thinking {:class [:medium]
                                               :style {:color (str "var(--" color "-" val ")")}}]]))))}]}]))


