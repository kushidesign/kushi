(ns ^{:kushi/layer "user-styles"}
  kushi.ui.checkbox.demo
  (:require [kushi.core :refer (sx)]
            [kushi.ui.icon :refer [icon]]
            [kushi.showcase.core
             :as showcase
             :refer [samples samples-with-variant]]
            [kushi.ui.checkbox :refer [checkbox]]
            [kushi.ui.label :refer [label]]))

;; TODO Make :row-attrs work

  (def demos
    (let [bounded (sx {:border-radius :$rounded
                       :w             :fit-content
                       :bgc           :transparent
                       :p             :1em
                       :pie           :1.5em
                       :b             :1px:solid:$neutral-150
                       :dark:b        :1px:solid:$neutral-850})]
      [{:label   "Different sizes",
        :samples (samples-with-variant
                  {:variant       :sizing,
                   :variant-scale :sizing/xsmall-xxxlarge,
                   :attrs         {:end-enhancer   :east
                                   :defaultChecked true} })}

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
                     :args            ["Make it shiny" [icon :auto-awesome]]})}]))

