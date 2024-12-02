(ns mvp.button
  (:require 
   [kushi.css.core :refer [sx]]))


(defn my-button []
  [:button (sx :bgc--blue :c--white) "Hello!"])
