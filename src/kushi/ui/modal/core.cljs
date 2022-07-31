(ns kushi.ui.modal.core
  (:require [kushi.ui.card.core :refer (card)]
            [kushi.ui.card.core :refer (card)]
            [kushi.ui.core :refer (opts+children)]
            [kushi.core :refer (merge-attrs) :refer-macros (sx cssfn)]))

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
  {:desc ["Modal dialogs create a new floating layer over the current view to get user feedback or display information."]
   :opts '[{:name    trigger
            :type    :vector
            :default nil
            :desc    ["Required. The element that will trigger the modal when clicked."
                      :br
                      "Must have an `on-click` attr that calls `kushi.ui.modal.core/open-kushi-modal`."]}
           {:name    scrim-attrs
            :type    :map
            :default nil
            :desc    "html attributes map applied to the background scrim `div`."}
           {:name    panel-attrs
            :type    :map
            :default nil
            :desc    "html attributes map applied to the modal panel `div`"}]}
  [& args]
  (let [[opts attr & children]                    (opts+children args)
        {:keys [trigger scrim-attrs panel-attrs]} opts]
    [:div
     (merge-attrs
      (sx 'kushi-modal-wrapper
          :d--block)
      attr)
     trigger
     [:div
      (merge-attrs
       (sx 'kushi-modal
           :.fixed-fill
           :.flex-col-c
           {:aria-modal false
            :data-kushi-ui :modal
            :role       :dialog
            :style      {:ai                             :c
                         :bgc                            (cssfn :rgba 232 232 232 0.86)
                         "&[aria-modal='false']:display" :none
                         :z                              1}})
       scrim-attrs)
      (into [card (merge-attrs
                   (sx :.elevated
                       :.flex-col-c
                       :ai--c
                       :w--600px
                       :h--350px)
                   panel-attrs)
             [dismiss-button]]
            children)]]))
