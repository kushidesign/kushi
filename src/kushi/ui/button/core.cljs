(ns kushi.ui.button.core
  (:require-macros
   [kushi.core :refer (sx cssfn defclass)]
   [kushi.gui :refer (defcom)])
  (:require
   [kushi.core :refer (merged-attrs-map)]
   [kushi.gui :refer (gui)]
   [kushi.ui.tooltip.core :refer (tooltip+parent remove-tooltip! expand-tooltip!)]
   [kushi.ui.util :refer (conditional-display?)]
   [par.core :refer [? !? ?+ !?+]]))

(defclass ^{:kushi true :base true} textbase :fs--48px)

(defclass ^:kushi text
  {:fs :1rem
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
   :cursor                     :pointer
   :transition-property        :all
   :transition-timing-function (cssfn :cubic-bezier 0 0 1 1)
   :transition-duration        :200ms})


(def button-base
  [:button:!
   (kushi.core/sx
    #_:.text
    #_:.button-base
    :.relative
    {:prefix :kushi-
     :ident :button
     :data-kushi-tooltip :true
     :aria-expanded "false"
     :on-mouse-enter #(when-let [[tooltip parent] (tooltip+parent %)]
                        (when-not (conditional-display? tooltip)
                          (expand-tooltip! tooltip parent)))
     :on-mouse-leave #(when-let [[tooltip parent] (tooltip+parent %)]
                        (when-not (conditional-display? tooltip)
                          (remove-tooltip! parent)))

     :style {:min-width                  :50px
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
             :cursor                     :pointer
             :transition-property        :all
             :transition-timing-function (cssfn :cubic-bezier 0 0 1 1)
             :transition-duration        :200ms}})
   [:span {:style {:display :block}}]])


(defcom button
  button-base)
