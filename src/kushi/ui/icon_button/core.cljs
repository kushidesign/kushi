(ns kushi.ui.icon-button.core
  (:require [kushi.ui.button.core :refer [button]]))

;; TODO maybe use :data-kui-name "button"
(defn icon-button
  {:summary ["Icons buttons provide cues for actions and events."]
   :desc    ["Buttons are fundamental components allow users to process actions or navigate an experience."]
   :opts    '[{:name    loading?
               :pred    boolean?
               :default false
               :desc    "When `true`, this will set the appropriate values for `aria-busy` and `aria-label`"}]}
  ([icon]
   [button {:data-kui-icon-button ""} icon])
  ([opts icon]
   [button
    (assoc opts :data-kui-icon-button "") 
    icon]))
