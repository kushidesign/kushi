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
   [(or (some-> m :opts :as) :div)
    (merge-attrs {:class [s "relative"]
                  :style {:gap (-> m :opts :gap)}} ;; use relative class so that soft-classic and solid-classic ::after styling works
                 (component-attrs s (:opts m) variant-basics)
                 (:attrs m))]
   (:children m)))

(defui box
  {:doc  "This is radio docstring"
   :props/family [:container]
  ;;  :opts {:sizing   {:schema  keyword?
  ;;                    :desc    "Blah blah blah"
  ;;                    :default nil} 
  ;;         :colorway {:schema  keyword?
  ;;                    :desc    "Blah blah blah"
  ;;                    :default nil}
  ;;         :surface  {:schema  keyword?
  ;;                    :desc    "Blah blah blah"
  ;;                    :default nil}
  ;;         :inert?   {:schema  boolean
  ;;                    :desc    "Blah blah blah"
  ;;                    :default nil}
  ;;         :position {:schema  keyword
  ;;                    :desc    "Blah blah blah"
  ;;                    :default nil}}
   }
  [& args]
  (let [{:keys [inert?]} &opts]
    ;; Maybe no legend
    (into
     [:div (merge-attrs 
            (sx ".kushi-box" :.relative)
            &data-ks-attrs
            &attrs
            ;; Pull this from in data-ks-attrs so you don't have to manualize schema?
            {:data-ks-inert (when-not (false? inert?) "")}
            (when-let [n (:data-ks-elevated &data-ks-attrs)]
              {:style {"--_drop-shadow" (str "var(--elevated" 
                                          (when-not (string/blank? n) (str "-" n))
                                          ")")}}))]
     &children)))

(defui elevated
  {:doc          "elevated"
   :props/shared [:elevated :position]
   }
  [& args]
  (let [{:keys [inert?]} &opts]
    ;; Maybe no legend
    (into
     [:div (merge-attrs 
            (sx ".kushi-elevated"
                :.relative
                :w--fit-content
                :h--fit-content)
            &data-ks-attrs
            &attrs
            ;; Pull this from in data-ks-attrs so you don't have to manualize schema?
            )]
     &children)))

(defn flex-row-start
  [& args]
  (flex-container (extract args [:gap]) "flex-row-start"))

(defn flex-row-center
  [& args]
  (flex-container (extract args [:gap]) "flex-row-center"))

(defn flex-row-end
  [& args]
  (flex-container (extract args [:gap]) "flex-row-end"))

(defn flex-row-space-around
  [& args]
  (flex-container (extract args [:gap]) "flex-row-space-around"))

(defn flex-row-space-between
  [& args]
  (flex-container (extract args [:gap]) "flex-row-space-between"))

(defn flex-row-space-evenly
  [& args]
  (flex-container (extract args [:gap]) "flex-row-space-evenly"))



(defn flex-col-start
  [& args]
  (flex-container (extract args [:gap]) "flex-col-start"))

(defn flex-col-center
  [& args]
  (flex-container (extract args [:gap]) "flex-col-center"))

(defn flex-col-end
  [& args]
  (flex-container (extract args [:gap]) "flex-col-end"))

(defn flex-col-space-around
  [& args]
  (flex-container (extract args [:gap]) "flex-col-space-around"))

(defn flex-col-space-between
  [& args]
  (flex-container (extract args [:gap]) "flex-col-space-between"))

(defn flex-col-space-evenly
  [& args]
  (flex-container (extract args [:gap]) "flex-col-space-evenly"))

