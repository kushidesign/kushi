(ns ^dev-always kushi.ui.examplescustom
  (:require
   [kushi.core :refer (sx merge-attrs)]
   [kushi.ui.icon.core :refer [icon]]
   [kushi.playground.util :refer-macros (feature)]))

(defn button [s] [:div.my-button-example s])
(defn button2 [s] [:div.my-button2-example s])

(def mock-custom-components
  [(feature 
    button
    {:stage    {:style {:min-height :150px}}
     :variants [:kind :shape :semantic :size :weight]
     :selector ".my-button-example"
     :defaults {:kind     :default
                :semantic :neutral
                :size     :medium
                :shape    :sharp
                :weight   :wee-bold
                :examples "foo"}
     :examples [
                {:label   "foo"
                 :example [button "Pla"]}
                {:label   "Leading foo icon"
                 :example [button [icon :play-arrow] "Plays"]}
                {:label   "Trailing foo icon"
                 :example [button "Play" [icon :auto-awesome]]}]})

   (feature
    button2
    {:stage    {:style {:min-height :150px}}
     :variants [:kind :shape :semantic :size :weight]
     :selector ".my-button2-example"
     :defaults {:kind     :default
                :semantic :neutral
                :size     :medium
                :shape    :sharp
                :weight   :wee-bold
                :examples "foo"}
     :examples [
                {:label   "foo"
                 :example [button2 "Pla"]}
                {:label   "Leading foo icon"
                 :example [button2 [icon :play-arrow] "Plays"]}
                {:label   "Trailing foo icon"
                 :example [button2 "Play" [icon :auto-awesome]]}]})])
