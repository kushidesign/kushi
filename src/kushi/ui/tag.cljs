(ns kushi.ui.tag
  (:require [kushi.core :refer [?sx sx css merge-attrs]]
            [kushi.ui.util :refer [as-str maybe nameable?]]
            [clojure.string :as string]
            [kushi.ui.core :refer (extract)]
            [kushi.ui.icon :refer [icon]]
            [kushi.ui.shared.theming :refer [data-ks- get-variants hue-style-map]]
            ))
;; Check docs
(defn tag
  {:summary "A tag is typically used for concise information, often in a group
             with other tags."

   ;; TODO remove toks
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
   
   :opts    {:loading?       {:schema  boolean?,
                              :default false,
                              :desc    "When `true`, this will set the appropriate values for `aria-busy` and `aria-label`"},
             :start-enhancer {:schema  keyword?,
                              :default nil,
                              :desc    "The name of a Google Material Symbol to use as an icon in the inline start position"},
             :end-enhancer   {:schema  keyword?,
                              :default nil,
                              :desc    "The name of a Google Material Symbol to use as an icon in the inline end position"},
             :colorway       {:default nil,
                              :desc    "Colorway of the tag. Can also be a named color from Kushi's design system, e.g `:red`, `:purple`, `:gold`, etc."},
             :surface        {:schema  #{:solid :minimal :outline :faint},
                              :default :round,
                              :desc    "Surface variant of the tag."},
             :contour        {:default :round,
                              :desc    "Shape of the tag."},
             :sizing         {:desc    "Size of the tag"
                              :default :medium},
             :packing        {:schema  #{:compact :roomy},
                              :default nil,
                              :desc    "General amount of padding inside the tag"}}

   }
  [& args]
  (let [{:keys [opts attrs children]}
        (extract args)
        
        {:keys [loading?
                start-enhancer
                end-enhancer
                colorway
                contour
                sizing
                stroke-align
                stroke-width
                packing
                inert?]}
        opts

        {:keys             [surface]
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
                :pbe--$_padding-block-end
                )

            (when stroke-width 
              {:style {"--_stroke-width" (name stroke-width)}})

            {:aria-busy            loading?
             :aria-label           (when loading? "loading")
             :data-ks-sizing       sizing
             :data-ks-surface      surface
             :data-ks-contour      contour
             :data-ks-stroke-align stroke-align}

            ;; (some-> stroke-align 
            ;;         (maybe #{:outside "outside"})
            ;;         (data-ks- :stroke-align))
            (when-not (false? inert?) {:data-ks-inert ""})

            (when loading? {:data-ks-ui-spinner ""})
            (when end-enhancer (data-ks- "" :end-enhancer))
            (when start-enhancer (data-ks- "" :start-enhancer))
            (some-> (or semantic-colorway
                        (when hue-style-map ""))
                    (data-ks- :colorway))
            (some-> packing
                    (maybe nameable?)
                    as-str
                    (maybe #{"compact" "roomy"})
                    (data-ks- :packing))
            hue-style-map
            (some-> surface (data-ks- :surface))
            attrs)]
          (cond start-enhancer (cons [icon start-enhancer] children)
                end-enhancer   (concat children [[icon end-enhancer]])
                :else          children))))
