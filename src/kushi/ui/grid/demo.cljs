(ns ^{:kushi/layer "user-styles"} kushi.ui.grid.demo
  (:require
   [kushi.core :refer (sx merge-attrs)]
   [kushi.showcase.core :as showcase :refer [samples]]
   [kushi.ui.grid :refer [grid]]))

(def demos
  (let [row-attrs       (sx :_.instance-code:w--100%)
        container-attrs (sx :gtc--1fr)]

    [{:label           "Basic"
      :row-attrs       row-attrs
      :container-attrs container-attrs
      :samples         (samples [[grid
                                  (sx
                                   :>div:bgc--$neutral-150
                                   :dark:>div:bgc--$neutral-800)
                                  (for [x (range 6)]
                                    [:div 
                                     [:div (merge-attrs (sx :ai--c)
                                                        {:data-ks-display  :flex-col-center
                                                         :data-ks-position :absolute-fill})
                                      (inc x)]])]])}

     {:label           "With aspect ratio"
      :row-attrs       row-attrs
      :container-attrs container-attrs
      :samples         (samples [[grid
                                  (merge-attrs
                                   (sx :>div:bgc--$neutral-150
                                       :dark:>div:bgc--$neutral-800)
                                   {:column-min-width :80px
                                    :gap              :15px
                                    :aspect-ratio     :2:3})
                                  (for [x (range 18)]
                                    [:div [:div 
                                           (merge-attrs (sx :ai--c)
                                                        {:data-ks-display  :flex-col-center
                                                         :data-ks-position :absolute-fill})
                                           (inc x)]])]])}]))
