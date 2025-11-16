;; TODO - use :-as option to provide alternate tag
(ns kushi.ui.flex
  (:require
   [fireworks.core :refer [? !? ?> !?>]]
   [kushi.core :refer (merge-attrs sx)]
   [kushi.ui.shared.theming :refer [component-attrs variant-basics]]
   [kushi.ui.core :refer (extract defui)]
   [clojure.string :as string]))

(defn- flex-container [m s]
  (into
   [(or (some-> m :props :as) :div)
    (merge-attrs {:class [s "relative"]
                  :style {:gap (-> m :props :gap)}} ;; use relative class so that soft-classic and solid-classic ::after styling works
                 (component-attrs s (:props m) variant-basics)
                 (:attrs m))]
   (:children m)))


;; TODO maybe should not have surface?

(defui flex-row
  {:props/shared [[:position {:default :relative}]
                  [:display {:default :flex-row}]
                  :inert
                  :surface
                  :colorway
                  :text-size
                  :text-weight]
   :props        {:tag {:schema  :keyword
                        :desc    "HTML tag to use."
                        :default :div}}}
  [& args]
  (into
   [(or (:as &props) :div)
    (merge-attrs {:class "ks-flex-row"} &attrs)]
   &children))


(defui flex-col
  {:props/shared [[:position {:default :relative}]
                  [:display {:default :flex-col}]
                  :inert
                  :surface
                  :colorway
                  :text-size
                  :text-weight]
   :props        {:tag {:schema  :keyword
                        :desc    "HTML tag to use."
                        :default :div}}}
  [& args]
  (into
   [(or (:as &props) :div)
    (merge-attrs {:class "ks-flex-col"} &attrs)]
   &children))


