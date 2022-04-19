(ns kushi.ui.button.core
  (:require-macros
   [kushi.core :refer (sx cssfn defclass)]
   [kushi.gui :refer (defcom)])
  (:require
   [kushi.core :refer (merged-attrs-map merge-with-style)]
   [kushi.gui :refer (gui)]
   [kushi.ui.tooltip.core :refer (tooltip-mouse-leave tooltip-mouse-enter)]
   [par.core :refer [? !? ?+ !?+]]))


(def label-base-sx
 (kushi.core/sx
    'kushi-label-base:ui
    :.relative
    ;; :.normal
    {:data-kushi-tooltip true
     :aria-expanded "false"
     :on-mouse-enter tooltip-mouse-enter
     :on-mouse-leave tooltip-mouse-leave
     :style {:min-width                  :1rem
             :h                          :fit-content
             :w                          :fit-content
            ;;  :fs                         "var(--text-medium)"
             :&_.kushi-mui-icon-font:fs  "var(--text-medium)"
             :m                          0
             :p                          0
             :line-height                1
             :>span:d                    :flex
             :>span:jc                   :center
             :>span:ai                   :center
             :&_.kushi-label-text+.kushi-icon:mis "calc(var(--text-medium) / 5)"
             :&_.kushi-icon+.kushi-label-text:mis "calc(var(--text-medium) / 5)"
             :bgi                        :none
             }}) )


(defcom button
  [:button:!
   (merge-with-style
     label-base-sx
     (kushi.core/sx
      'kushi-button:ui
      :.transition
      {:style {:bw                         0
               :>span:padding              [[:0.8em :1.2em]]
               :bc                         :black
               :bs                         :solid
               :bgi                        :none
               :cursor                     :pointer}}))
   [:span]]
   nil
  #(if (string? %) [:span.kushi-label-text %] %))

;; CRUFT

;; (def icon-button-base
;;   [:button
;;    (sx {:class [:flex-row-c]
;;         :style {:border-radius :100%
;;                 :w     :1.5rem
;;                 :h     :1.5rem
;;                 :lh    0
;;                 :p     :0.5rem
;;                 :b     :none
;;                 :fs    :1rem
;;                 :bgi   :none
;;                 :ai    :c
;;                 :cursor :pointer
;;                 :hover:o 0.8}})])

;; (def dismiss-button
;;   (gui icon-button-base
;;        (sx {:class [:absolute]
;;             :style {:top           :0.75rem
;;                     :right         :0.75rem
;;                     :after:content "\"✕\""}})))

;; (def icon-button
;;   (gui icon-button-base))



;; (def button-group
;;   (gui
;;    [:div
;;     (sx {:class [:flex-row-fs]
;;          :style  {
;;                   ;; ">button:nth-child(2n):margin" (theme/get-style [:button-group :>button :margin] 0)
;;                   :>button:first-child:m 0
;;                   :>button:last-child:m 0}})]))
