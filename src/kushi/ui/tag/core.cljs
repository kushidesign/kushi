(ns kushi.ui.tag.core
  (:require [kushi.core :refer [sx css merge-attrs]]
            [kushi.ui.util :refer [as-str maybe nameable?]]
            [clojure.string :as string]
            [kushi.ui.core :refer (opts+children)]
            [kushi.ui.shared.theming :refer [data-kui- get-variants hue-style-map]]

            ))

(defn tag
  {:summary "A tag is typically used for concise information, often in a group
             with other tags."}
  [& args]
  (let [[opts attrs & children]
        (opts+children args)
        
        {:keys [loading?
                start-enhancer
                end-enhancer
                colorway
                stroke-align
                packing
                icon]}
        opts

        {:keys             [shape surface]
         semantic-colorway :colorway}
        (get-variants opts)

        hue-style-map                 
        (when-not semantic-colorway 
          (some-> colorway
                  hue-style-map))
        ]
    (into [:div
           (merge-attrs
            (sx ".kui-tag"
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

            {:aria-busy        loading?
             :aria-label       (when loading? "loading")

             ;; different from button
            ;;  :data-kui-ia      ""
             ;; different from button

             :data-kui-surface surface
             :data-kui-shape   shape}
            (when loading? {:data-kushi-ui-spinner true})
            (when (and (not icon) end-enhancer) (data-kui- "" :end-enhancer))
            (when (and (not icon) start-enhancer) (data-kui- "" :start-enhancer))
            (some-> stroke-align 
                    (maybe #{:outside "outside"})
                    (data-kui- :stroke-align))
            (some-> (or semantic-colorway
                        (when hue-style-map ""))
                    (data-kui- :colorway))
            (some-> packing
                    (maybe nameable?)
                    as-str
                    (maybe #{"compact" "roomy"})
                    (data-kui- :packing))
            hue-style-map
            (some-> surface (data-kui- :surface))
            attrs)]
          (cond icon           [[kushi.ui.icon.core/icon :star]]
                start-enhancer (cons start-enhancer children)
                end-enhancer   (concat children [end-enhancer])
                :else          children))))
