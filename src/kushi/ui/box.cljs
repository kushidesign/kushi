(ns kushi.ui.box
  (:require
   [fireworks.core :refer [? !? ?> !?>]]
   [kushi.core :refer (merge-attrs sx)]
   [kushi.ui.core :refer (defui)]
   [kushi.ui.decoration :as decoration]
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
    (!? &attrs)
    (into
     [:div (merge-attrs 
            (sx "[data-ks-ui=\"box\"]"
                :.relative)

            (!? :pp (decoration/stroke-width-cssvar (:stroke-width &props) "button"))

            ;; no classics
            (!? (decoration/shadow-and-stroke-attrs &props))
            &attrs
            )]
     &children)))
