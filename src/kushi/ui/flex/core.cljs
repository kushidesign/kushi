;; TODO - use :-as option to provide alternate tag

(ns kushi.ui.flex.core
  (:require
   [fireworks.core :refer [? !? ?> !?>]]
   [kushi.core :refer (merge-attrs)]
   [kushi.ui.shared.theming :refer [component-attrs variant-basics]]
   [kushi.ui.core :refer (extract)] ))


(defn- flex-container [m s]
  (into
   [(or (some-> m :opts :as) :div)
    (merge-attrs {:class         [s "relative"]} ;; use relative class so that soft-classic and solid-classic ::after styling works
                 (component-attrs s (:opts m) variant-basics)
                 (:attrs m))]
   (:children m)))


(defn flex-row-start
  [& args]
  (flex-container (extract args flex-row-start) "flex-row-start"))

(defn flex-row-center
  [& args]
  (flex-container (extract args flex-row-center) "flex-row-center"))

(defn flex-row-end
  [& args]
  (flex-container (extract args flex-row-end) "flex-row-end"))

(defn flex-row-space-around
  [& args]
  (flex-container (extract args flex-row-space-around) "flex-row-space-around"))

(defn flex-row-space-between
  [& args]
  (flex-container (extract args flex-row-space-between) "flex-row-space-between"))

(defn flex-row-space-evenly
  [& args]
  (flex-container (extract args flex-row-space-evenly) "flex-row-space-evenly"))



(defn flex-col-start
  [& args]
  (flex-container (extract args flex-col-start) "flex-col-start"))

(defn flex-col-center
  [& args]
  (flex-container (extract args flex-col-center) "flex-col-center"))

(defn flex-col-end
  [& args]
  (flex-container (extract args flex-col-end) "flex-col-end"))

(defn flex-col-space-around
  [& args]
  (flex-container (extract args flex-col-space-around) "flex-col-space-around"))

(defn flex-col-space-between
  [& args]
  (flex-container (extract args flex-col-space-between) "flex-col-space-between"))

(defn flex-col-space-evenly
  [& args]
  (flex-container (extract args flex-col-space-evenly) "flex-col-space-evenly"))

