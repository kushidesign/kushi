(ns kushi.ui.modal.core
  (:require [par.core :refer-macros [!? ?]]
            [kushi.ui.card.core :refer (card)]
            [kushi.utils :refer (merge-with-style)]
            [kushi.ui.button.core :refer (button)]
            [kushi.core :refer-macros (sx cssfn defclass)]))

(defn close-kushi-modal [e]
  (let [modal-parent (.closest (-> e .-target) ".kushi-modal")]
    (.setAttribute modal-parent "aria-modal" "false")))

(defn open-kushi-modal [e]
  (let [modal-wrapper (.closest (-> e .-target) ".kushi-modal-wrapper")
        modal-node (aget (.-childNodes modal-wrapper) 1)]
        (.toggleAttribute modal-node "aria-modal")))

(defn dismiss-button []
  [:button
   (sx {:on-click close-kushi-modal
        :class    [:.absolute :.flex-row-c]
        :style    {:border-radius :100%
                   :w             :1.5rem
                   :h             :1.5rem
                   :lh            0
                   :p             :0.5rem
                   :b             :none
                   :fs            :1rem
                   :bgi           :none
                   :ai            :c
                   :cursor        :pointer
                   :hover:o       0.8
                   :top           :0.75rem
                   :right         :0.75rem
                   :after:content "\"✕\""
                   }})])

(defclass modal-scrim
  {:ai                           :c
   :bgc                          (cssfn :rgba 232 232 232 0.86)
   "&[aria-modal='false']:display" :none
   :z                            1})

(defn modal [{:keys [children trigger parts]}]
  (let [{[scrim-attrs] :scrim [panel-attrs] :panel} parts]
    [:span (sx :d--block {:prefix :kushi- :ident :modal-wrapper})
     trigger
     [:div
      (merge-with-style
       (sx
        :.fixed-fill
        :.flex-col-c
        :.modal-scrim
        {:aria-modal false
         :prefix     :kushi-
         :ident      :modal
         :role       :dialog})
       scrim-attrs)
      [card (merge-with-style
             (sx :.elevated :.flex-col-c :ai--c :w--600px :h--350px)
             panel-attrs)
       [:<> [dismiss-button] children]]]]))
