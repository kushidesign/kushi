;; TODO - maybe not dedicated ns
(ns kushi.ui.dom.fune.arrow
  (:require
   [kushi.ui.util :as util :refer [ck?]]))

(defn append-arrow-el!
  [{:keys [el
           tt-pos
           new-placement-kw]}]
  (let [arrow-el                (js/document.createElement "div")
        ck?                     (partial ck? new-placement-kw)
        direction-class*        (cond (ck? #{:tl :t :tr}) "down"
                                      (ck? #{:bl :b :br}) "up"
                                      (ck? #{:lt :l :lb}) "right"
                                      (ck? #{:rt :r :rb}) "left")
        direction-class         (str "kushi-tooltip-arrow-pointing-"
                                     direction-class*)]

    ;; Set class and style on arrow element
    (doto arrow-el
      (.setAttribute "class" (str "kushi-tooltip-arrow " direction-class)))

    ;; Finally, append arrow element to tooltip element.
    (.appendChild el arrow-el)
    arrow-el))
