(ns kushi.ui.thumb
  (:require
   [fireworks.core :refer [? !? ?> !?>]]
   [kushi.core :refer (merge-attrs sx)]
   [kushi.ui.core :refer (defui)]
   [kushi.ui.util :refer [as-str]]
   [clojure.string :as string]))


(defui thumb
  {:doc          "This is thumb docstring"
   :props/family [:container]
   :props/shared [:transition
                  [:sizing {:default :medium}]
                  [:contour {:default :pill}]]}
  [& args]
  (let [{:keys [stroke-width sizing elevated]} (? &props)] 
    (into
     [:div (merge-attrs 
            (sx "[data-ks-ui=\"thumb\"]"
                :.relative
                :transition-duration--$xxfast
                :border-color--currentColor
                :cursor--pointer
                [:--width :$thumb-height]
                :h--$thumb-height
                :w--$thumb-height)
            {:style {"--thumb-height" (str "var(--" (as-str sizing) ")")}}
            (when stroke-width 
              {:style {"--_stroke-width" (as-str stroke-width)}})
            #_(when-let [n elevated]
              {:style {"--_drop-shadow" (str "var(--elevated" 
                                             (when-not (string/blank? n)
                                               (str "-" n))
                                             ")")}})
            &attrs)]
            
     &children)))

;; {:--thumb-height "calc(var(--switch-thumb-scale-factor, 1) * (1em - (var(--switch-border-width) * 2)))"

#_(sx ".kushi-switch-thumb"
      :.transition
      [:--width :$thumb-height]
      :transition-duration--$xxfast
      :border-color--currentColor
      ["has-ancestor(.kushi-switch[aria-checked='false']):border-color" "color-mix(in srgb, currentColor, transparent)"]
      :cursor--pointer
      :bgc--$transparent-white-100
      :box-shadow--0:2px:6px:0:$transparent-black-15
      [:transform "translate(0, -50%)"]
      ["has-ancestor(.kushi-switch[aria-checked='true']):inset-inline-start"
       "calc(100% - var(--width))"]
      ["has-ancestor(.kushi-switch[disabled]):cursor"
       :not-allowed]
      :position--absolute
      :top--50%
      :inset-inline-start--0
      :h--$thumb-height
      :w--$width)
