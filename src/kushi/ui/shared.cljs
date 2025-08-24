(ns kushi.ui.shared
  (:require [kushi.ui.icon :refer [icon]]))
            
(defn add-enhancer [{:keys [start-enhancer end-enhancer]} children]
  (let [enhancer
        #(if (keyword? %) [icon %] %)

        start-enhancer                                                                                             
        (enhancer start-enhancer)

        end-enhancer                                                                                               
        (enhancer end-enhancer)]
    (cond start-enhancer (concat [start-enhancer] children)
          end-enhancer   (concat children [end-enhancer])
          :else          children)))
