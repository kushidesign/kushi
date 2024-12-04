(ns mvp.button
  (:require 
   [kushi.css.core :refer [css ?css sx ?sx defcss ?defcss]]))

(defcss "@layer kushi-ui-theming .wttf"
         :c--gold :bgc--red)

(def class-binding "foo")

(defn my-button []
  [:button (sx #_class-binding
                :bgc--blue
                :c--white
                :p--15px
                :m--30px
                :b--1px:solid:$red-100
                :outline--1px:solid:$red-100)
   #_{:class (?css class-binding
                   :bgc--blue
                   :c--white
                   :p--15px
                   :m--30px
                   :b--1px:solid:$red-100
                   :outline--1px:solid:$red-100)}
   "Hello!"])
