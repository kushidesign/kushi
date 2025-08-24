(ns kushi.ui.box
  (:require
   [kushi.core :refer (merge-attrs sx)]
   [kushi.ui.core :refer (defui)]
   [clojure.string :as string]))


(defui box
  {:doc          "This is box docstring"
   :props/family [:container]}
  [& args]
  (do 
    #_'(do (let [[state set-state!] (uix.core/use-state 0)]
           ($ :<>
              ($ button {:on-click #(set-state! dec)} "-")
              ($ :span state)
              ($ button {:on-click #(set-state! inc)} "+"))))
    (into
     [:div (merge-attrs 
            (sx "[data-ks-ui=\"box\"]"
                :.relative)
            &attrs
            (when-let [n (:data-ks-elevated &attrs)]
              {:style {"--_drop-shadow" (str "var(--elevated" 
                                             (when-not (string/blank? n)
                                               (str "-" n))
                                             ")")}}))]
     &children)))
