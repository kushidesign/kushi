(ns kushi.ui.tag
  (:require [kushi.core :refer [?sx sx css merge-attrs]]
            [kushi.ui.util :refer [as-str maybe nameable?]]
            [clojure.string :as string]
            [kushi.ui.core :refer (extract defui)]
            [kushi.ui.icon :refer [icon]]
            [kushi.ui.shared.theming :refer [get-variants]]))

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
                  :surface
                  :transition
                  :inert]}
  [& args]
  (let [{:keys [loading
                start-enhancer
                end-enhancer
                stroke-width]}
        &props
        
        enhancer
        #(if (keyword? %) [icon %] %)

        start-enhancer                                                                                             
        (enhancer start-enhancer)

        end-enhancer                                                                                               
        (enhancer end-enhancer)
        ]
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

            (when stroke-width 
              {:style {"--_stroke-width" (name stroke-width)}})
           
            {:aria-busy  loading
             :aria-label (when loading "loading")}

            &attrs)]
          (cond start-enhancer
                (concat [start-enhancer] &children)

                end-enhancer
                (concat &children [end-enhancer])

                :else
                &children))))

#_(defn tag
  {:summary "A tag is typically used for concise information, often in a group
             with other tags."

   :desc    "Tags are fundamental components that allow to organize information,
             or view organized information."
   
   :opts    [:sizing
             :end-enhancer
             :start-enhancer
             :colorway
             :packing
             :loading
             :stroke-align
             :stroke-width
             :contour
             :surface
             :weight]}
  [& args]
  (let [{:keys [opts attrs children]}
        (extract args [:loading
                       :start-enhancer
                       :end-enhancer
                       :colorway
                       :contour
                       :surface
                       :sizing
                       :weight
                       :stroke-align
                       :stroke-width
                       :packing
                       :inert])
        
        {:keys [loading
                start-enhancer
                end-enhancer
                colorway
                contour
                surface
                sizing
                weight
                stroke-align
                stroke-width
                packing
                inert]}
        opts]
    (into [:div
           (merge-attrs

            ;; base styles
            (sx "[data-ks-ui=\"tag\"]"
                :position--relative
                :d--flex
                :flex-direction--row
                :jc--c
                :ai--c
                :w--fit-content
                :gap--$icon-enhanceable-gap
                
                ;; different from button
                ;; :cursor--pointer
                
                :transition-property--all
                :transition-timing-function--$transition-timing-function
                :transition-duration--$transition-duration

                ;; different from button
                [:--_padding-block-start "calc(var(--tag-padding-block) * var(--tag-padding-block-start-reduction-ratio, 1))"]
                [:--_padding-block-end   :$tag-padding-block]
                [:--_padding-inline      :$tag-padding-inline]
                ;; different from button
                
                :pi--$_padding-inline
                :pbs--$_padding-block-start
                :pbe--$_padding-block-end)

            ;; stroke-width
            (when stroke-width 
              {:style {"--_stroke-width" (name stroke-width)}})

            ;; resolved html attributes for theming
            {:aria-busy              loading
             :aria-label             (when loading "loading")
             :data-ks-ui-spinner     (when loading "")
             :data-ks-sizing         sizing
             :data-ks-weight         weight
             :data-ks-contour        (or contour :rounded)
             :data-ks-surface        (or surface :soft)
             :data-ks-packing        packing
             :data-ks-colorway       (or colorway :neutral)
             :data-ks-stroke-align   stroke-align
             :data-ks-end-enhancer   (when end-enhancer "")
             :data-ks-start-enhancer (when start-enhancer "")
             :data-ks-inert          (when-not (false? inert) "")}
            
            ;; user attrs
            attrs)]
          (cond start-enhancer (cons [icon start-enhancer] children)
                end-enhancer   (concat children [[icon end-enhancer]])
                :else          children))))
