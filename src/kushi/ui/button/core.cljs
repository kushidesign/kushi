(ns kushi.ui.button.core
  (:require 
   [kushi.core :refer (sx merge-attrs)]
   [kushi.ui.core :refer (opts+children)]
   [kushi.ui.icon.core]
   [kushi.ui.util :refer [as-str maybe nameable?]]
   [kushi.ui.shared.theming :refer [data-kui- get-variants hue-style-map]]))

(defn button
  {:summary ["Buttons provide cues for actions and events."]
   :desc    ["Buttons are fundamental components allow users to process actions or navigate an experience."
             :br
             :br
             "They can be custom styled via a variety of tokens in your theme."
             :br
             :br "`:$button-padding-inline-ems`"
             :br "The default value is `:1.2em`"
             :br
             :br "`:$icon-button-padding-inline-ems`"
             :br "The default value is `:0.69em`"
             :br
             :br "`:$button-padding-block-ems`"
             :br "The default value is `:0.67em`"
             :br
             :br "`:$button-with-icon-padding-inline-offset`"
             :br "The default value is `:0.9em`"
             :br
             :br "`:$button-border-width`"
             :br "The default value is `:1px`"
             :br]
   :opts    '[{:name    loading?
               :pred    boolean?
               :default false
               :desc    "When `true`, this will set the appropriate values for `aria-busy` and `aria-label`"}]}
  [& args]
  (let [
        [opts attrs & children]
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

    ;; TODO maybe use :data-kui-name "button"
    (into [:button
           (merge-attrs
            (sx ".kui-button"
                :position--relative
                :d--flex
                :flex-direction--row
                :jc--c
                :ai--c
                :w--fit-content
                :gap--$icon-enhanceable-gap
                :cursor--pointer
                :transition-property--all
                :transition-timing-function--$transition-timing-function
                :transition-duration--$transition-duration
                [:--_padding-block :$button-padding-block-ems]
                [:--_padding-inline :$button-padding-inline-ems]
                :pi--$_padding-inline
                :pb--$_padding-block)
            {:aria-busy        loading?
             :aria-label       (when loading? "loading")
             :data-kui-ia      ""
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
