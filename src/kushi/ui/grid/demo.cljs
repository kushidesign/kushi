(ns kushi.ui.grid.demo
  (:require
   [kushi.core :refer (sx)]
   [kushi.playground.util :refer-macros [sx-call]]
   [kushi.ui.grid.core :refer [grid]]))

(def examples
  (let [row-attrs       (sx :&_.instance-code:w--100%)
        container-attrs (sx :gtc--1fr)]
    [(let [code (sx-call [grid
                          (sx
                           :>div:bgc--$neutral-150
                           :dark:>div:bgc--$neutral-800)
                          (for
                           [x (range 6)]
                            [:div 
                             [:div (sx :.absolute-fill
                                       :.flex-col-c :ai--c)
                              (inc x)]])])]
       {:desc            "Basic"
        :row-attrs       row-attrs
        :container-attrs container-attrs
        :snippets        [(:quoted code)]
        :examples        [{:code code} ]})
     (let [code (sx-call [grid
                          (sx :>div:bgc--$neutral-150
                              :dark:>div:bgc--$neutral-800
                              {:-column-min-width :80px
                               :-gap              :15px
                               :-aspect-ratio     :2:3})
                          (for
                           [x (range 18)]
                            [:div [:div (sx :.absolute-fill
                                            :.flex-col-c :ai--c)
                                   (inc x)]])])]
       {:desc            "With sizing options"
        :row-attrs       row-attrs
        :container-attrs container-attrs
        :snippets        [(:quoted code)]
        :examples        [{:code code}]})]))
