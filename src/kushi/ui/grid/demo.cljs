(ns kushi.ui.grid.demo
  (:require
   [kushi.core :refer (sx)]
   [kushi.playground.component-examples :as component-examples]
   [kushi.playground.util :refer-macros [sx-call]]
   [kushi.ui.grid.core :refer [grid]]))


(declare grid-examples)

(defn demo [component-opts]
  (into [:<>]
        (for [
              ;; example-opts (take 1 grid-examples)
              example-opts grid-examples
              ;; example-opts (keep-indexed (fn [idx m] (when (contains? #{9} idx) m)) grid-examples)
              ]
          [component-examples/examples-section
           component-opts
           example-opts])))

(def grid-examples
  (let [row-attrs (sx :&_.playground-component-example-row-instance-code:w--100%)
        container-attrs (sx :gtc-1fr)]
    [{:desc            "Basic"
      :row-attrs       row-attrs
      :container-attrs container-attrs
      :examples        [{:code (sx-call [grid
                                         (sx
                                          :>div:bgc--$neutral-150
                                          :dark:>div:bgc--$neutral-800)
                                         (for
                                          [x (range 6)]
                                           [:div 
                                            [:div (sx :.absolute-fill
                                                      :.flex-col-c :ai--c)
                                             (inc x)]])])} ]}
     {:desc            "With sizing options"
      :row-attrs       row-attrs
      :container-attrs container-attrs
      :examples        [{:code (sx-call [grid
                                         (sx :>div:bgc--$neutral-150
                                             :dark:>div:bgc--$neutral-800
                                             {:-column-min-width :80px
                                              :-gap              :15px
                                              :-aspect-ratio     :2:3})
                                         (for
                                          [x (range 15)]
                                           [:div [:div (sx :.absolute-fill
                                                           :.flex-col-c :ai--c)
                                                  (inc x)]])])}]}]))
