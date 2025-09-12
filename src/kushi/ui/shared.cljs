(ns kushi.ui.shared
  (:require [kushi.ui.icon :refer [icon]]))
            

(defn enhancer [x] (if (keyword? x) [icon x] x))

(defn add-enhancer [{:keys [start-enhancer end-enhancer]} children]
  (let [start-enhancer                                                                                             
        (enhancer start-enhancer)

        end-enhancer                                                                                               
        (enhancer end-enhancer)]
    (cond start-enhancer (concat [start-enhancer] children)
          end-enhancer   (concat children [end-enhancer])
          :else          children)))
