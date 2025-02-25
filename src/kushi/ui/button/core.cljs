(ns kushi.ui.button.core
  (:require
   [fireworks.core :refer [? !? ?> !?>]]
   [clojure.string :as string]
   [kushi.core :refer (css defcss sx merge-attrs validate-opt)]
   [kushi.ui.core :refer (opts+children)]
   [kushi.ui.icon.core]
   [kushi.ui.shared.theming :refer [data-kushi- get-variants hue-style-map]]
   [kushi.ui.util :refer [as-str maybe nameable?]]))


(defn ^:public button
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
   
   :opts    '{loading? 
             {:pred    boolean?
              :default false
              :desc    "When `true`, this will set the appropriate values for
                        `aria-busy` and `aria-label`"}
             start-enhancer
             {:pred    #{string? keyword?}
              :default nil
              :desc    "The name of a Google Material Symbol to use as an icon
                         in the inline start position"}
             end-enhancer 
             {:pred    #{string? keyword?}
              :default nil
              :desc    "The name of a Google Material Symbol to use as an icon
                        in the inline end position"
              :snippets-header "Who cares?"
              :examples ^{:label "Examples using end enhancer icons"
                          :require [[kushi.ui.icon.core :refer [icon]]
                                   [smth.core :refer [duh]]
                                   [clojure.string :as string]]}
                         [#_{:value [icon :auto-awesome]
                           :args  ["Wow"]
                           :attrs {:class     ["xxxsmall"]
                                   :-colorway :accent}}
                          #_{:value [icon :auto-awesome]
                           :args  ["Pets"]
                           :attrs {:class     ["xxxsmall"]
                                   :-colorway :accent
                                   :-surface  :solid}}
                          ^{:label "oneee"}
                          [button
                           {:class         ["xxxsmall"]
                            :-end-enhancer [icon :pets]
                            :-colorway     :accent
                            :-surface      :solid}
                           "Pets"]
                          
                          ^{:label "two"}
                          [button
                           {:class         ["xxxsmall"]
                            :-end-enhancer [icon :auto-awesome]
                            :-colorway     :accent
                            :-surface      :soft}
                           "Wow"]

                          ^{:label "three"}
                          [button
                           {:class         ["xxxsmall"]
                            :-end-enhancer [icon :play-arrow]
                            :-colorway     :accent
                            :-surface      :outline}
                           "Play"]
                          ]}
              colorway
              {:pred    #{:neutral :accent :positive :negative :warning}
               :default nil
               :desc    "Colorway of the button. Can also be a named color from
                         Kushi's design system, e.g `:red`, `:purple`, `:gold`,
                         etc."}
              surface
              {:pred    #{:soft :solid :outline :minimal}
               :default :round
               :desc    "Surface variant of the button."}

              shape
              {:pred    #{:sharp :rounded :pill}
               :default :round
               :desc    "Shape of the button."}

              packing
              {:pred    #{:compact :roomy}
               :default nil
               :desc    "General amount of padding inside the button"}

              stroke-align
              {:pred    #{:outside :inside}
               :default nil
               :desc    "General amount of padding inside the button"}

              size
              {:pred    #{:xxxsmall
                          :xxsmall
                          :xsmall
                          :small
                          :medium
                          :large
                          :xlarge
                          :xxlarge
                          :xxxlarge}
               :default nil
               :desc    "Corresponds to the font-size based on Kushi's font-size
                         scale."}}
   :demos   '[{:label    "Colorway + Surface variants"
               :variants [colorway surface]}
              {:label         "Sizes from xxsmall-xlarge"
               :variants      [size]
               :variants-keys {size xxsmall-xlarge}}
              {:label    "Shape variants"
               :variants [shape]}
              
              #_{:label    "Weight variants from light to extra-bold"
              ;;  :sx-attrs        (sx-call (sx :.small))
              ;;  :container-attrs container-attrs2
              ;;  :snippets-header "Use the font-weight utility classes `:.thin` ~ `:.heavy`
              ;;           to control the weight. Scale of weights:"
              ;;  :snippets        '[[:div
              ;;                      [button "Play"]
              ;;                      [button (sx :.thin) "Play"]
              ;;                      [button (sx :.extra-light) "Play"]
              ;;                      [button (sx :.light) "Play"]
              ;;                      [button (sx :.normal) "Play"]
              ;;                      [button (sx :.wee-bold) "Play"]
              ;;                      [button (sx :.semi-bold) "Play"]
              ;;                      [button (sx :.bold) "Play"]
              ;;                      [button (sx :.extra-bold) "Play"]
              ;;                      [button (sx :.heavy) "Play"]]]
               :examples (for [s (rest component-examples/type-weights)]
                           {:label (name s)
                            :args  ["Wow" #_[icon :auto-awesome]]
                            :attrs {:class [s]}})}
              
              #_{:label    "icons"
               :examples [
                        ;; {:label "Icon button"
                        ;;  :args  [[icon :favorite]]
                        ;;  :attrs {:icon true}}
                        ;; {:label "Icon button"
                        ;;  :args  [[icon :star]]}
                        ;; {:label "Icon button"
                        ;;  :args  [[icon :play-arrow]]}
                          
                          {:label "Leading icon"
                           :sx-utils (sx :.xxsmall)
                           :args  [[icon :play-arrow] "Play"]}
                          {:label "Trailing icon"
                           :sx-utils (sx :.xxsmall)
                           :args  ["Play" [icon :play-arrow]]}
                          {:label   "2 icons"
                           :sx-utils (sx :.xxsmall)
                           :args    [[icon :auto-awesome] "Wow" [icon :auto-awesome]]}]}
              ]}
  [& args]
  (let [[opts attrs & children]
        (opts+children args)

        {:keys [loading?
                start-enhancer
                end-enhancer
                stroke-align
                packing
                size
                icon]}
        opts

        {:keys             [shape surface]
         semantic-colorway :colorway}
        (get-variants opts)
        
        styling-class
        (css ".kushi-button"
             :position--relative
             :d--flex
             :flex-direction--row
             :jc--c
             :ai--c
             :w--fit-content
             :h--fit-content
             :gap--$icon-enhanceable-gap
             :cursor--pointer
             :transition-property--all
             :transition-timing-function--$transition-timing-function
             :transition-duration--$transition-duration
             [:--_padding-block :$button-padding-block-ems]
             [:--_padding-inline :$button-padding-inline-ems]
             :pi--$_padding-inline
             :pb--$_padding-block)]
    
    ;; TODO maybe use :data-kushi-ui "button"
    (into [:button
           (merge-attrs
            {:class                     styling-class
             :aria-busy                 loading?
             :aria-label                (when loading? "loading")
             :data-kushi-ia             ""
             :data-kushi-surface        (validate-opt button surface)
             :data-kushi-colorway       (validate-opt button semantic-colorway)
             :data-kushi-shape          (validate-opt button shape)
             :data-kushi-size           (validate-opt button size)
             :data-kushi-ui-spinner     (when loading? "")
             :data-kushi-end-enhancer   (when (and (not icon) end-enhancer) "")
             :data-kushi-start-enhancer (when (and (not icon) start-enhancer) "")
             :data-kushi-packing        (validate-opt button packing)
             :data-kushi-stroke-align   (validate-opt button stroke-align)
            ;;  :data-kushi-stroke-align   (some-> stroke-align 
            ;;                                     (maybe #{:outside "outside"}))
             }
            ;; hue-style-map creates a {:style {:--_hue "33"}} value - unused for now
            ;; hue-style-map
            attrs)]
          (cond icon           [[kushi.ui.icon.core/icon :star]]
                start-enhancer (cons start-enhancer children)
                end-enhancer   (concat children [end-enhancer])
                :else          children))))


(defn ^:public icon-button
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
                size
                icon]}
        opts

        {:keys             [shape surface]
         semantic-colorway :colorway}
        (get-variants opts)

        hue-style-map                 
        (when-not semantic-colorway 
          (some-> colorway
                  hue-style-map))]

    ;; TODO maybe use :data-kushi-name "button"
    (into [:button
           (merge-attrs
            {:class                   (css ".kushi-icon-button"
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
             :aria-busy               loading?
             :aria-label              (when loading? "loading")
             :data-kushi-ia           ""
             :data-kushi-surface      surface
             :data-kushi-shape        shape
             :data-kushi-ui-spinner   (when loading? "")
             :data-kushi-stroke-align (some-> stroke-align 
                                              (maybe #{:outside "outside"}))
             :data-kushi-colorway       semantic-colorway
             :data-kushi-size           size
             :data-kushi-packing        (some-> packing
                                                (maybe nameable?)
                                                as-str
                                                (maybe #{"compact" "roomy"})
                                                (data-kushi- :packing))

             }
            (when loading? {:data-kushi-ui-spinner true})
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
          (if icon [kushi.ui.icon.core/icon icon]
              children))))

