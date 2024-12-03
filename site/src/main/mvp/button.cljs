(ns mvp.button
  (:require 
   [kushi.css.core :refer [sx defcss]]))

(defcss "@layer kushi-ui-theming .wttf"
  :c--gold :bgc--red)

(defn my-button []
  [:button (sx :bgc--blue :c--white) "Hello!"])
