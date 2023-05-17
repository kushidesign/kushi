(ns kushi.ui.tooltip.demo
  (:require
   [kushi.core :refer (sx merge-attrs)]
   [kushi.ui.tooltip.core :refer [tooltip-attrs]]))

(defn demo []
  (into
   [:div
    (sx
     :.grid
     :gtc--1fr:1fr:1fr:1fr:1fr
     :gtr--auto
     :gap--1rem
     :w--400px
     :h--400px
     [:gta '(kushi/grid-template-areas
             "brc br b  bl blc"
             "rt  .  .  .  lt"
             "r   .  .  .  l"
             "rb  .  .  .  lb"
             "trc tr t  tl tlc")])]

   (for [x     ["brc" "br" "b"  "bl" "blc"
                "rt"  nil  nil  nil  "lt"
                "r"   nil  nil  nil  "l"
                "rb"  nil  nil  nil  "lb"
                "trc" "tr" "t"  "tl" "tlc"]
         :when (not (nil? x))]

     [:button (merge-attrs
               (sx 'kushi-playground-tooltip-demo-button
                   :.flex-row-c
                   :.pointer
                   :.relative
                   :b--1px:solid:$neutral-600
                   :dark:b--1px:solid:$neutral-400
                   :hover:b--1px:solid:black
                   :dark:hover:b--1px:solid:white
                   :>span.placement-label:ff--$code-font-stack
                   :fs--0.9em
                   :c--$neutral-600
                   :dark:c--$neutral-400
                   :hover:c--black
                   :dark:hover:c--white
                   :&.kushi-pseudo-tooltip-revealed:bc--$accent-color
                   :dark:&.kushi-pseudo-tooltip-revealed:bc--$accent-color-inverse
                   :&.kushi-pseudo-tooltip-revealed:c--$accent-color
                   :dark:&.kushi-pseudo-tooltip-revealed:c--$accent-color-inverse
                   :&.kushi-pseudo-tooltip-revealed:bgc--$accent-background-color
                   :dark:&.kushi-pseudo-tooltip-revealed:bgc--$accent-background-color-inverse
                   [:grid-area x]
                   {:tab-index 0})
               (tooltip-attrs {:-text      ["Tooltip Line 1" "Tooltip Line 2" ]
                                      ;; :-reveal-on-click?         true
                               :-placement (keyword x)}))
      [:span.placement-label (str ":" x)]])))
