(ns kushi.ui.label.core
  (:require
   [fireworks.core :refer [? !? ?> !?>]]
   [kushi.ui.core :refer (extract)]
   [kushi.ui.shared.theming :refer [component-attrs variant-basics]]
   [kushi.core :refer (css merge-attrs)]))


(defn label
  {:desc "A label is typically used for providing titles to sections of content."}
  [& args]
  (let [{:keys [opts attrs children]} (extract args)
        {:keys [start-enhancer end-enhancer]}
        opts]
    (into [:label
           (merge-attrs
            {:class (css
                     :.flex-row-start
                     :.transition
                     :d--inline-flex
                     :w--fit-content
                     :gap--$icon-enhanceable-gap)}
            (component-attrs "label"
                             opts
                             variant-basics
                             [:end-enhancer :start-enhancer])
            attrs)]
          (cond start-enhancer (concat [start-enhancer] children)
                end-enhancer   (concat children [end-enhancer])
                :else          children))))


