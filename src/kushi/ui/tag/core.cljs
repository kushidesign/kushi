(ns kushi.ui.tag.core
  (:require [kushi.core :refer [sx css merge-attrs]]
            [kushi.ui.util :refer [as-str maybe nameable?]]
            [clojure.string :as string]
            [kushi.ui.core :refer (extract)]
            [kushi.ui.icon.core]
            [kushi.ui.shared.theming :refer [data-kushi- get-variants hue-style-map]]
            ))
;; Check docs
(defn tag
  {:summary "A tag is typically used for concise information, often in a group
             with other tags."

   :desc    "Tags are fundamental components that allow to organize information,
             or view organized information.
              
             They can be custom styled via a variety of tokens in your theme:

             `--tag-padding-inline`<br>
             The default value is `:1.2em`
              
             `--icon-tag-padding-inline`<br>
             The default value is `:0.69em`
              
             `--tag-padding-block`<br>
             The default value is `:0.67em`
              
             `--tag-with-icon-padding-inline-offset`<br>
             The default value is `:0.9em`<br>
              
             `--tag-border-width`
             The default value is `:1px`"
   
   :opts    '[{:name    loading?
               :schema    boolean?
               :default false
               :desc    "When `true`, this will set the appropriate values for
                        `aria-busy` and `aria-label`"}
              {:name    start-enhancer
               :schema    #{string? keyword?}
               :default nil
               :desc    "The name of a Google Material Symbol to use as an icon
                         in the inline start position"}
              {:name    end-enhancer
               :schema    #{string? keyword?}
               :default nil
               :desc    "The name of a Google Material Symbol to use as an icon
                         in the inline end position"}
              {:name    colorway
               :schema    #{:neutral :accent :positive :negative :warning}
               :default nil
               :desc    "Colorway of the tag. Can also be a named color from
                         Kushi's design system, e.g `:red`, `:purple`, `:gold`,
                         etc."}
              {:name    surface
               :schema    #{:faint :solid :minimal :outline}
               :default :round
               :desc    "Surface variant of the tag."}
              {:name    shape
               :schema    #{:sharp :round :pill}
               :default :round
               :desc    "Shape of the tag."}
              {:name    packing
               :schema    #{:compact :roomy}
               :default nil
               :desc    "General amount of padding inside the tag"}
              ]
   }
  [& args]
  (let [{:keys [opts attrs children]}
        (extract args tag)
        
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
            (sx ".kushi-tag"
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
            ;;  :data-kushi-ia      ""
             ;; different from button

             :data-kushi-surface surface
             :data-kushi-shape   shape}
            (when loading? {:data-kushi-ui-spinner true})
            (when (and (not icon) end-enhancer) (data-kushi- "" :end-enhancer))
            (when (and (not icon) start-enhancer) (data-kushi- "" :start-enhancer))
            (some-> stroke-align 
                    (maybe #{:outside "outside"})
                    (data-kushi- :stroke-align))
            (some-> (or semantic-colorway
                        (when hue-style-map ""))
                    (data-kushi- :colorway))
            (some-> packing
                    (maybe nameable?)
                    as-str
                    (maybe #{"compact" "roomy"})
                    (data-kushi- :packing))
            hue-style-map
            (some-> surface (data-kushi- :surface))
            attrs)]
          (cond icon           [[kushi.ui.icon.core/icon :star]]
                start-enhancer (cons start-enhancer children)
                end-enhancer   (concat children [end-enhancer])
                :else          children))))
