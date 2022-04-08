(ns kushi.ui.button.core
  (:require-macros
   [kushi.core :refer (sx cssfn defclass)]
   [kushi.gui :refer (defcom)])
  (:require
   [kushi.core :refer (merged-attrs-map)]
   [kushi.gui :refer (gui)]
   #_[par.core :refer [? !? ?+ !?+]]))

(defclass ^{:kushi true :base true} textbase :fs--48px)

(defclass ^:kushi text
  {
   :fs :1rem
   :m                          0
   :p                          0
   :line-height                1
   })

(defclass ^:kushi button-base
  {;;  :border-radius           (theme/get-style [:button :border-radius] 0)
   :min-width                  :50px
   :h                          :fit-content
   :w                          :fit-content
   :>span:padding              [[:0.8em :1.2em]]
   :bw                         0
   :bc                         :black
   :bs                         :solid
   :bgi                        :none
   :bgc                        :#eee
   :hover:bgc                  :#e2e2e2
   ;;  :bgc                        (theme/get-style [:button :background-color] :background-color)
   ;;  :hover:bgc                  (theme/get-style [:button:hover :background-color])
   :cursor                     :pointer
              ;;  :hover:o                    (theme/get-style [:button:hover :opacity] 0)
   :transition-property        :all
   :transition-timing-function (cssfn :cubic-bezier 0 0 1 1)
   :transition-duration        :200ms})

(def button-base
  [:button:!
   (kushi.core/sx
    #_:.text
    #_:.button-base
    {:prefix :kushi-
     :ident :button
     :style {;;  :border-radius           (theme/get-style [:button :border-radius] 0)
             :min-width                  :50px
             :h                          :fit-content
             :w                          :fit-content
             :fs                         :1rem
             :m                          0
             :p                          0
             :line-height                1
             :>span:padding              [[:0.8em :1.2em]]
             :bw                         0
             :bc                         :black
             :bs                         :solid
             :bgi                        :none
             :bgc                        :#eee
             :hover:bgc                  :#e2e2e2
            ;;  :bgc                        (theme/get-style [:button :background-color] :background-color)
            ;;  :hover:bgc                  (theme/get-style [:button:hover :background-color])
             :cursor                     :pointer
              ;;  :hover:o                    (theme/get-style [:button:hover :opacity] 0)
             :transition-property        :all
             :transition-timing-function (cssfn :cubic-bezier 0 0 1 1)
             :transition-duration        :200ms}})
   [:span {:style {:display :block}}]])

;; #_(def primary-button
;;   (gui button*
;;        (kushi.core/sx {:prefix :kushi- :ident :primary-button})))


(defcom button
  button-base)
