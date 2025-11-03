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


(defui flex-row
  {:props/shared [[:position {:default :relative}]
                  [:display {:default :flex-row}]
                  :surface
                  :colorway
                  :size
                  :weight]}
  [& args]
  (into [:div.ks-flex-row &attrs] &children))


(defui flex-row-center
  {:props/shared [[:position {:default :relative}]
                  [:display {:default :flex-row-center}]
                  :surface
                  :colorway
                  :size
                  :weight]}
  [& args]
  (into [:div.ks-flex-row &attrs] &children))


(defui flex-row-flex-end
  {:props/shared [[:position {:default :relative}]
                  [:display {:default :flex-row-flex-end}]
                  :surface
                  :colorway
                  :size
                  :weight]}
  [& args]
  (into [:div.ks-flex-row &attrs] &children))


(defui flex-row-space-around
  {:props/shared [[:position {:default :relative}]
                  [:display {:default :flex-row-space-around}]
                  :surface
                  :colorway
                  :size
                  :weight]}
  [& args]
  (into [:div.ks-flex-row &attrs] &children))


(defui flex-row-space-between
  {:props/shared [[:position {:default :relative}]
                  [:display {:default :flex-row-space-between}]
                  :surface
                  :colorway
                  :size
                  :weight]}
  [& args]
  (into [:div.ks-flex-row &attrs] &children))


(defui flex-row-space-evenly
  {:props/shared [[:position {:default :relative}]
                  [:display {:default :flex-row-space-evenly}]
                  :surface
                  :colorway
                  :size
                  :weight]}
  [& args]
  (into [:div.ks-flex-row &attrs] &children))


(defui flex-col
  {:props/shared [[:position {:default :relative}]
                  [:display {:default :flex-col}]
                  :surface
                  :colorway
                  :size
                  :weight]}
  [& args]
  (into
   [:div.ks-flex-col &attrs]
   &children))


(defui flex-col-center
  {:props/shared [[:position {:default :relative}]
                  [:display {:default :flex-col-center}]
                  :surface
                  :colorway
                  :size
                  :weight]}
  [& args]
  (into [:div.ks-flex-col &attrs] &children))


(defui flex-col-flex-end
  {:props/shared [[:position {:default :relative}]
                  [:display {:default :flex-col-flex-end}]
                  :surface
                  :colorway
                  :size
                  :weight]}
  [& args]
  (into [:div.ks-flex-col &attrs] &children))


(defui flex-col-space-around
  {:props/shared [[:position {:default :relative}]
                  [:display {:default :flex-col-space-around}]
                  :surface
                  :colorway
                  :size
                  :weight]}
  [& args]
  (into [:div.ks-flex-col &attrs] &children))


(defui flex-col-space-between
  {:props/shared [[:position {:default :relative}]
                  [:display {:default :flex-col-space-between}]
                  :surface
                  :colorway
                  :size
                  :weight]}
  [& args]
  (into [:div.ks-flex-col &attrs] &children))


(defui flex-col-space-evenly
  {:props/shared [[:position {:default :relative}]
                  [:display {:default :flex-col-space-evenly}]
                  :surface
                  :colorway
                  :size
                  :weight]}
  [& args]
  (into [:div.ks-flex-col &attrs] &children))
