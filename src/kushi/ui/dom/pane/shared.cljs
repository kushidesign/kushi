(ns kushi.ui.dom.pane.shared
  (:require
   [clojure.string :as string]
   [goog.string]
   [kushi.ui.util :as util :refer [maybe nameable? as-str]]))


(def stock-pane-types
  #{:pane :tooltip :popover :toast #_:hover-board #_:context-menu})
        

(defn pane-classes
  [{:keys [pane-class 
           arrow?
           placement-kw
           new-placement-kw
           pane-type
           metrics?
           user-pane-class]
    :as opts}]
  (let [pane-type-class
        (some-> pane-type
                (maybe #(not= :pane %))
                (maybe stock-pane-types)
                as-str)

        toast?          
        (= pane-type :toast)

        user-pane-class
        (cond (string? user-pane-class)
              (string/split user-pane-class #" ")
              
              (util/class-coll? user-pane-class)
              user-pane-class)
        ret             
        (string/join
         " "
         (remove nil? 
                 (concat user-pane-class
                         ["kushi-pane"
                          (some->> pane-type-class (str "kushi-"))
                          "invisible" 
                          (when-not toast?
                            (some->> (if metrics? placement-kw new-placement-kw)
                                     name
                                     (str "kushi-pane-")))
                          (when-not arrow? "kushi-pane-arrowless")
                          (some-> pane-class (maybe nameable?) as-str)])))]
    ret))
