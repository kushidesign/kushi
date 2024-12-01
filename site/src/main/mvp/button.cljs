(ns mvp.button
  (:require 
   [shadow.grove :as sg :refer (css defc <<)]
   [kushi.css.core :refer [css]]))


(defc ui-root []
  (render
    (<< [:div {:class (css :bgc--blue :c--white)}
         (str "Hello!")])))
