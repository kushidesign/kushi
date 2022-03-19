(ns kushi.ui.button.core
  (:require-macros
   [kushi.core :refer (sx cssfn defclass)]
   [kushi.gui :refer (defcom)])
  (:require
   [kushi.core :refer (merged-attrs-map)]
   [kushi.gui :refer (gui)]
   #_[par.core :refer [? !? ?+ !?+]]))


(def button-base
  [:button:!
   (kushi.core/sx
    {:style  {;;  :border-radius           (theme/get-style [:button :border-radius] 0)
              :min-width                  :50px
              :m                          0
              :p                          0
              :h                          :fit-content
              :w                          :fit-content
              :>span:p                    [[:0.8em :1.2em]]
              :bw                         0
              :bc                         :black
              :bs                         :solid
              :line-height                1
              :fs                         :1rem
              :bgi                        :none
              :bgc                        :#eee
              :hover:bgc                  :#e2e2e2
              ;;  :bgc                        (theme/get-style [:button :background-color] :background-color)
              ;;  :hover:bgc                  (theme/get-style [:button:hover :background-color])
              :cursor                     :pointer
              ;;  :hover:o                    (theme/get-style [:button:hover :opacity] 0)
              :transition-property        :all
              :transition-timing-function (cssfn :cubic-bezier 0 0 1 1)
              :transition-duration        :200ms}
     :prefix :kushi-
     :ident  :button})
   [:span {:style {:display :block}}]])

;; #_(def primary-button
;;   (gui button*
;;        (kushi.core/sx {:prefix :kushi- :ident :primary-button})))


(defcom button
  button-base)
