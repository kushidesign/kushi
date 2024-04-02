(ns kushi.ui.dom.fune.shared
  (:require
   [clojure.string :as string]
   [goog.string]
   [kushi.ui.util :as util :refer [maybe nameable? as-str]]))


(def stock-fune-types #{:fune :tooltip :popover :toast #_:hover-board #_:context-menu})
        

(defn fune-classes
  [{:keys [fune-class 
           arrow?
           placement-kw
           new-placement-kw
           fune-type
           metrics?]}]
  (let [fune-type-class (some-> fune-type
                                (maybe #(not= :fune %))
                                (maybe stock-fune-types)
                                as-str)
        toast?          (= fune-type :toast)]
    (string/join
     " "
     (remove nil? 
             ["kushi-fune"
              (some->> fune-type-class (str "kushi-"))
              "invisible" 
              (when-not toast?
                (some->> (if metrics? placement-kw new-placement-kw)
                         name
                         (str "kushi-fune-")))
              (when-not arrow? "kushi-fune-arrowless")
              (some-> fune-class (maybe nameable?) as-str)]))))
