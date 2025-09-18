(ns kushi.ui.thumb
  (:require
   [fireworks.core :refer [? !? ?> !?>]]
   [kushi.core :refer (merge-attrs sx)]
   [kushi.ui.core :refer (defui)]
   [kushi.ui.decoration :as decoration]
   [clojure.string :as string]))


(defui thumb
  {:doc          "Thumb docstring"
   :props/shared [:size
                  :colorway
                  :packing
                  :loading
                  :stroke
                  :stroke-align
                  :stroke-width
                  :position
                  :shape
                  :surface
                  :transition
                  [:inert {:default false}]]}
  [& args]
  (let [{:keys [surface loading stroke-width]} &props
        classic-variant?                       (contains? #{:solid-classic 
                                                            :soft-classic} 
                                                          surface)]
    (into [:div
           (merge-attrs
            (sx
             ".ks-thumb"
             :d--flex
             :flex-direction--row
             :jc--c
             :ai--c
             :w--1em
             :h--1em

             ;; TODO - browser support?
            ;;  [:aspect-ratio "1 / 1"]

             :cursor--pointer
             ;; TODO - is this local/private css var necessary?
             [:--padding-block :$thumb-padding-block]
             [:--padding-inline :$thumb-padding-inline]
             :pi--$_padding-inline
             :pb--$_padding-block)
            {:aria-busy  loading
             :aria-label (when loading "loading")}

            (!? (decoration/stroke-width-cssvar stroke-width "thumb"))

            (when-not classic-variant? 
              (decoration/shadow-and-stroke-attrs &props))

            &attrs)]
          &children)))

;; {:--thumb-height "calc(var(--switch-thumb-scale-factor, 1) * (1em - (var(--switch-border-width) * 2)))"

#_(sx ".ks-switch-thumb"
      :.transition
      [:--width :$thumb-height]
      :transition-duration--$transition-xxfast
      :border-color--currentColor
      ["has-ancestor(.ks-switch[aria-checked='false']):border-color" "color-mix(in srgb, currentColor, transparent)"]
      :cursor--pointer
      :bgc--$transparent-white-100
      :box-shadow--0:2px:6px:0:$transparent-black-15
      [:transform "translate(0, -50%)"]
      ["has-ancestor(.ks-switch[aria-checked='true']):inset-inline-start"
       "calc(100% - var(--width))"]
      ["has-ancestor(.ks-switch[disabled]):cursor"
       :not-allowed]
      :position--absolute
      :top--50%
      :inset-inline-start--0
      :h--$thumb-height
      :w--$width)
