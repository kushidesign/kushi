(ns kushi.ui.layout.core
  (:require
   [fireworks.core :refer [? !? ?> !?>]]
   [kushi.core :refer (merge-attrs)]
   [kushi.ui.shared.theming :refer [component-attrs variant-basics]]
   [kushi.ui.core :refer (extract)]))


(defn layout
  {:doc  "A generic layout container."
   #_:opts #_[:map {:shape    #{"rounded" "sharp" "pill" "circle"}
                :surface  defs/basic-surfaces-set-of-strs
                :colorway #{"neutral"
                            "accent"
                            "positive"
                            "negative"
                            "warning"
                            "gray"
                            "purple"
                            "blue"
                            "green"
                            "lime"
                            "yellow"
                            "gold"
                            "orange"
                            "red"
                            "magenta"
                            "brown"}
                :weight   defs/basic-weights-set-of-strs
                :size     defs/basic-sizes-set-of-strs}]}
  [& args]
  (let [{:keys [opts attrs children]} (extract args layout)]
    (into
     ;; TODO validate layout tag?
     [(or (:as opts) :div)
      (merge-attrs
       {:class ["relative"]} ;; use relative class so that soft-classic and solid-classic ::after styling works
       (component-attrs "layout" opts variant-basics)
       attrs)]
     children)))
