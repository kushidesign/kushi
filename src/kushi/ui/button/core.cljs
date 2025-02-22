(ns kushi.ui.button.core
  (:require
   [kushi.core :refer (sx merge-attrs)]
   [kushi.ui.core :refer (opts+children)]
   [kushi.ui.icon.core]
   [kushi.ui.shared.theming :refer [data-kui- get-variants hue-style-map]]
   [kushi.ui.util :refer [as-str maybe nameable?]]))

(defn button
  {:summary "Buttons provide cues for actions and events."

   :desc    "Buttons are fundamental components that allow users to process
             actions or navigate an experience.
              
             They can be custom styled via a variety of tokens in your theme:

             `--button-padding-inline-ems`<br>
             The default value is `:1.2em`
              
             `--icon-button-padding-inline-ems`<br>
             The default value is `:0.69em`
              
             `--button-padding-block-ems`<br>
             The default value is `:0.67em`
              
             `--button-with-icon-padding-inline-offset`<br>
             The default value is `:0.9em`<br>
              
             `--button-border-width`
             The default value is `:1px`"
   
   :opts    '[{:name    loading?
               :pred    boolean?
               :default false
               :desc    "When `true`, this will set the appropriate values for
                        `aria-busy` and `aria-label`"}
              {:name    start-enhancer
               :pred    #{string? keyword?}
               :default nil
               :desc    "The name of a Google Material Symbol to use as an icon
                         in the inline start position"}
              {:name    end-enhancer
               :pred    #{string? keyword?}
               :default nil
               :desc    "The name of a Google Material Symbol to use as an icon
                         in the inline end position"}
              {:name    colorway
               :pred    #{:neutral :accent :positive :negative :warning}
               :default nil
               :desc    "Colorway of the button. Can also be a named color from
                         Kushi's design system, e.g `:red`, `:purple`, `:gold`,
                         etc."}
              {:name    surface
               :pred    #{:soft :solid :minimal :outline}
               :default :round
               :desc    "Surface variant of the button."}
              {:name    shape
               :pred    #{:sharp :round :pill}
               :default :round
               :desc    "Shape of the button."}
              {:name    packing
               :pred    #{:compact :roomy}
               :default nil
               :desc    "General amount of padding inside the button"}]}
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

        icon-button? (and icon (not (seq children)))]

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


(defn icon-button
  {:summary ["Icon buttons provide cues for actions and events."]
   :desc    ["Buttons are fundamental components that allow users to process
              actions or navigate an experience."
             :br
             :br
             "They can be custom styled via a variety of tokens in your theme."
             :br
             :br "`:$icon-button-padding-inline-ems`"
             :br "The default value is `:0.69em`"
             :br
             :br "`:$icon-button-padding-block-ems`"
             :br "The default value is `:0.69em`"
             :br
             :br "`:$button-border-width`"
             :br "The default value is `:1px`"]
   :opts    '[{:name    loading?
               :pred    boolean?
               :default false
               :desc    "When `true`, this will set the appropriate values for
                         `aria-busy` and `aria-label`"}]}
  [& args]
  (let [
        [opts attrs & children]
        (opts+children args)

        {:keys [loading?
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
                  hue-style-map))]

    ;; TODO maybe use :data-kui-name "button"
    (into [:button
           (merge-attrs
            (sx ".kui-icon-button"
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
                [:pb :$_padding-block]
                [:pi :$_padding-inline])
            {:aria-busy        loading?
             :aria-label       (when loading? "loading")
             :data-kui-ia      ""
             :data-kui-surface surface
             :data-kui-shape   shape}
            (when loading? {:data-kushi-ui-spinner true})
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
          (if icon [kushi.ui.icon.core/icon icon]
              children))))
