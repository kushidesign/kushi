(ns kushi.ui.modal.core
  (:require [par.core :refer-macros [!? ?]]
            [kushi.ui.card.core :refer (card)]
            [kushi.utils :refer (merge-with-style)]
            [kushi.ui.button.core :refer (button)]
            [kushi.ui.card.core :refer (card)]
            [kushi.ui.core :refer (opts+children)]
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

(defn modal
  "Desc for"
  [& args]
  (let [[opts attr & children]  (opts+children args)
        {:keys [trigger parts]} opts]
    [:div
     (merge-with-style
      (sx 'kushi-modal-wrapper:ui
          :d--block)
      attr)
     trigger
     [:div
      (merge-with-style
       (sx 'kushi-modal:ui
           :.fixed-fill
           :.flex-col-c
           {:aria-modal false
            :role       :dialog
            :style      {:ai                             :c
                         :bgc                            (cssfn :rgba 232 232 232 0.86)
                         "&[aria-modal='false']:display" :none
                         :z                              1}})
       (:scrim parts))
      [card (merge-with-style
             (sx :.elevated
                 :.flex-col-c
                 :ai--c
                 :w--600px
                 :h--350px)
             (:panel parts))
       [:<> [dismiss-button] children]]]]))

#_(defn modal [{:keys [children trigger parts]}]
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




;; CRUFT

;; (def modal
;;   (gui
;;    [:div
;;     (sx {:class [:absolute-fill :flex-col-c]
;;          :style {:ai  :c
;;                  :bgc (cssfn :rgba 0 0 0 0.1)}})]))

;; (defclass floating-pane
;;   {:bgc :white
;;    :box-shadow "0 10px 15px -3px rgb(0 0 0 / 5%), 0 4px 6px -2px rgb(0 0 0 / 2%)"})

;; (def modal-panel-base
;;   [:div
;;    (sx {:class [:floating-pane]
;;         :style {:p :1.5rem
;;                 ;; :border-radius (theme/get-style [:panel :border-radius] 0)
;;                 ;; :border-color  (theme/get-style [:panel :border-color] :#efefef)
;;                 ;; :border-width  (theme/get-style [:panel :border-width] :1px)
;;                 ;; :border-style  (theme/get-style [:panel :border-style] :solid)
;;                 }
;;         :ident :modal-panel-base})])

;; (def modal-panel-flex
;;   (gui
;;    modal-panel-base
;;    (sx {:class [:flex-col-c :relative]
;;         :style {:ai :c
;;                 :width :600px
;;                 :h :100%
;;                 :max-height "calc(100% - 40px)"
;;                 :max-width "calc(100% - 40px)"}
;;         :ident :modal-panel-flex})))

;; (def modal-panel
;;   (gui
;;    modal-panel-base
;;    (sx {:class [:flex-col-c :relative :foo]
;;         :ident :modal-panel
;;         :style {:ai :c
;;                 :w :600px
;;                 :h :350px}})))
