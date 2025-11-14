(ns kushi.ui.tag
  (:require [kushi.core :refer [sx merge-attrs]]
            [fireworks.core :refer [? !? ?> !?>]]
            [kushi.ui.core :refer (defui)]
            [kushi.ui.util :as util]
            [kushi.ui.flex :refer [flex-row]]
            [kushi.ui.shared :refer [add-enhancer]]))

(defui tag
  {:summary      "A tag is typically used for concise information, often in a group with other tags."
   :desc         "Tags are fundamental components that allow to organize information, or view organized information."
   :props/shared [:size
                  :end-enhancer
                  :start-enhancer
                  :colorway
                  :packing
                  :loading
                  :stroke
                  :stroke-align
                  :stroke-width
                  :position
                  :shape
                  [:surface {:default :faint}]
                  :transition
                  :inert]}
  [& args]
  (let [{:keys [loading stroke-width]} &props]
    (into [flex-row
           (merge-attrs

            ;; base styles
            ;; TODO - how different from button?
            (sx ".ks-tag"
                {:jc              :center
                 :w               :fit-content
                 :h               :fit-content
                 :gap             :$icon-enhanceable-gap
                 :pi              :$padding-inline||$tag-padding-inline
                 :pb              :$padding-block||$tag-padding-block
                 :.start-enhancer {:pis  "calc(var(--padding-inline, var(--tag-padding-inline)) * 0.7666)"}
                 :.end-enhancer   {:pie  "calc(var(--padding-inline, var(--tag-padding-inline)) * 0.7666)"}
                 })

            {:aria-busy  loading
             :aria-label (when loading "loading")}
            
            (? :pp {:style {"--stroke-width" (or (some-> stroke-width util/as-str)
                                                 "var(--tag-stroke-width)")}})
            
            (util/stroke-width-cssvar stroke-width "tag")
            (util/shadow-and-stroke-attrs &props)

            &attrs)]
          (add-enhancer &props &children))))


;; Keep here?

#_(defcss ".ks-button, .ks-tag"
  {".start-enhancer"      {:padding-inline-start "calc(var(--padding-inline) * 0.7666)"}
   ".end-enhancer"        {:padding-inline-end "calc(var(--padding-inline) * 0.7666)"}})
