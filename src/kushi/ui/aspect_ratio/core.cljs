;; Todo - decide whether or not to include
(ns kushi.ui.aspect-ratio.core
  (:require
   [fireworks.core :refer [?]]
   [kushi.core :refer [css merge-attrs sx]]
   [kushi.ui.core :refer (extract)]
   [kushi.ui.util :refer [maybe nameable?]]))

;; Check docs
(defn aspect-ratio 
  {:summary ""
   :desc    ""
   :opts    '[]}
  [& args]
  (let [{:keys [opts attrs children]}
        (extract args aspect-ratio)
        
        {:keys [ratio]}
        opts

        ratio-fraction
        (if-let [[_ w h] 
                 (some-> ratio
                         (maybe nameable?)
                         name
                         (->> (re-find #"^([0-9]+(?:\.[0-9]+)?)\:([0-9]+(?:\.[0-9]+)?)$"))
                         ?)]
          (/ w h)
          (do (when ratio 
                ;; Add warning here
                )
              1))]
    
    [:div
     (merge-attrs
       {:style {:padding-bottom (str "calc(100% / " ratio-fraction ")")}
        :class (css ".kushi-aspect-ratio"
                    :.relative)}
      attrs)
     (into [:div (sx ".absolute-fill")]
           children)]))
