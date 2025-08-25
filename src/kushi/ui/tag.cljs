(ns kushi.ui.tag
  (:require [kushi.core :refer [sx merge-attrs]]
            [fireworks.core :refer [? !? ?> !?>]]
            [kushi.ui.core :refer (defui)]
            [kushi.ui.util :as util]
            [kushi.ui.shared :refer [add-enhancer]]))

(defui tag
  {:summary      "A tag is typically used for concise information, often in a group with other tags."
   :desc         "Tags are fundamental components that allow to organize information, or view organized information."
   :props/shared [:sizing
                  :end-enhancer
                  :start-enhancer
                  :colorway
                  :packing
                  :loading
                  :stroke-align
                  :stroke-width
                  :position
                  :contour
                  [:surface {:default :faint}]
                  :transition
                  :inert]}
  [& args]
  (let [{:keys [loading stroke-width]} &props]
    (into [:div
           (merge-attrs

            ;; base styles
            ;; TODO - how different from button?
            (sx "[data-ks-ui=\"tag\"]"
                :d--flex
                :flex-direction--row
                :jc--c
                :ai--c
                :w--fit-content
                :h--fit-content
                :gap--$icon-enhanceable-gap
                
                ;; different from button
                [:--_padding-block-start "calc(var(--tag-padding-block) * var(--tag-padding-block-start-reduction-ratio, 1))"]
                [:--_padding-block-end   :$tag-padding-block]
                [:--_padding-inline      :$tag-padding-inline]
                ;; different from button
                
                :pi--$_padding-inline
                :pbs--$_padding-block-start
                :pbe--$_padding-block-end)

            {:aria-busy  loading
             :aria-label (when loading "loading")}

            (? :pp {:style {"--stroke-width" (or (some-> stroke-width util/as-str)
                                             "var(--tag-stroke-width)")}})
            
            (util/stroke-width-cssvar stroke-width "tag")
            (util/drop-shadow-and-stroke-attrs &props)

            &attrs)]
          (add-enhancer &props &children))))
