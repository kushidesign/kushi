(ns ^{:kushi/layer "user-styles"}
  kushi.ui.checkbox.demo
  (:require [kushi.core :refer (sx)]
            [kushi.ui.icon :refer [icon]]
            [kushi.showcase.core
             :as showcase
             :refer [samples samples-with-variant]]
            [kushi.ui.checkbox :refer [checkbox]]
            [kushi.ui.flex :refer [flex-row]]
            [kushi.ui.label :refer [label]]))

;; TODO Make :row-attrs work

  (def demos
    (let [bounded (sx ".ks-showcase-text-field-sample-wrapper"
                      {:bgc               :transparent
                       :padding           :1.05em:1.65em:1.05em
                       :border-width      :1px
                       :border-style      :solid
                       :border-color      :$neutral-150
                       :dark:border-color :$neutral-850
                       :width             :fit-content
                       :border-radius     :$shape-rounded})]
      
      [
      #_ 
       {:label   "Different sizes",
        :samples (samples-with-variant
                  {:variant       :size,
                   :variant-scale :size/xs-3xl,
                   :attrs         {:end-enhancer   :east
                                   :defaultChecked true} })}

       {:label     "With label",
        :row-attrs bounded
        :samples   (samples
                    [[flex-row
                      [checkbox {:id "with-label"}]
                      [label {:for "with-label"} "Sign me up"]]])}

       {:label     "With label and traling icon",
        :row-attrs bounded
        :samples   (samples
                    [[flex-row
                      [checkbox]
                    ;; TODO fix red colorway on icon
                      [label "Make it shiny" [icon #_{:colorway :neutral} :auto-awesome]]]])}
       
       {:label     "Weights",
        :row-style {:flex-direction :column
                    :gap            :1.5rem}
        :samples   (samples 
                    [
                     "thin"
                     [flex-row
                      {:size   :xl
                       :weight :thin}
                      [checkbox]
                      [:label "Sign me up"]]

                     "extra-light"
                     [flex-row
                      {:size   :xl
                       :weight :extra-light}
                      [checkbox]
                      [:label "Sign me up"]]

                     "normal"
                     [flex-row
                      {:size   :xl
                       :weight :normal}
                      [checkbox]
                      [:label "Sign me up"]]

                     "wee-bold"
                     [flex-row
                      {:size   :xl
                       :weight :wee-bold}
                      [checkbox]
                      [:label "Sign me up"]]

                     "semi-bold"
                     [flex-row
                      {:size   :xl
                       :weight :semi-bold}
                      [checkbox]
                      [:label "Sign me up"]]

                     "bold"
                     [flex-row
                      {:size   :xl
                       :weight :bold}
                      [checkbox]
                      [:label "Sign me up"]]

                     "extra-bold"
                     [flex-row
                      {:size   :xl
                       :weight :extra-bold}
                      [checkbox]
                      [:label "Sign me up"]]

                     "heavy"
                     [flex-row
                      {:size   :xl
                       :weight :heavy}
                      [checkbox]
                      [:label "Sign me up"]]])

        #_(samples-with-variant
                    {:variant         :weight
                     :variant-labels? false
                     :args            ["Make it shiny" [icon :auto-awesome]]})}]))

