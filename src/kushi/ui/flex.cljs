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

(defui elevated
  {:doc          "elevated"
   :props/shared [:elevated :position]
   }
  [& args]
  (let [{:keys [inert]} &props]
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
