(ns kushi.ui.button.core
  (:require
   [clojure.string :as string]
   [kushi.core :refer (css-vars-map css defcss sx merge-attrs validate-option)]
   [kushi.ui.core :refer (extract)]
   [kushi.ui.icon.core]
   [kushi.ui.shared.theming :refer [data-kushi- get-variants hue-style-map]]
   [kushi.ui.util :refer [as-str maybe nameable?]]
   )
  ;; (:require-macros [kushi.ui.button.core])
  )


(defn ^:public button
  {:summary "Buttons provide cues for actions and events."

   :desc    "Buttons are fundamental components that allow users to process actions or navigate an experience.
              
             They can be custom styled via a variety of tokens in your theme.

             `--button-padding-inline`<br>
             The default value is `:1.2em`
              
             `--icon-button-padding-inline`<br>
             The default value is `:0.69em`
              
             `--button-padding-block`<br>
             The default value is `:0.67em`
              
             `--button-with-icon-padding-inline-offset`<br>
             The default value is `:0.9em`<br>
              
             `--button-border-width`
             The default value is `:1px`"
   
   ;; Should this be a map, parsed with edamame?
   ;; Should we use keys like :-colorway, same as call-site
   :opts   
   '[
     colorway       
     {:pred    #{:neutral :accent :positive :negative :warning}
      :default nil
      :desc    "Colorway of the button. Can also be a named color from Kushi's design system, e.g `:red`,
                        `:purple`, `:gold`, etc."
      :demo    {:label          "Colorways / surfaces × shapes"
                :snippets-label "Colorways"
                :require        [[kushi.ui.icon.core :refer [icon]]]
                :x-variants     [surface shape]
                :rows?          true
                :attrs          {:-end-enhancer [icon :east]}
                :attrs/display  {:-size :small}
                :attrs/snippet  {:-surface :solid}
                :args           ["Next"]}}

     surface        
     {:pred    #{:classic :soft :mild :solid :outline :minimal}
      :default :round
      :desc    "Surface variant of the button."
      :demo    {:label         "Surface variants"
                :attrs         {:-end-enhancer [icon :east]
                                :-colorway     :accent}
                :attrs/display {:-size :small}
                :args          ["Next"]}}

     shape          
     {:pred    #{:sharp :rounded :pill}
      :default :round
      :desc    "Shape of the button."
      :demo    {:label         "Shape variants"
                :attrs         {:-end-enhancer [icon :east]
                                :-colorway     :accent
                                :-surface      :solid}
                :attrs/display {:-size :small}
                :args          ["Next"]}}


     packing        
     {:pred    #{:compact :roomy}
      :default nil
      :desc    "General amount of padding inside the button"
      :demo    {:label         "Packing variants"
                :attrs         {:-end-enhancer [icon :east]
                                :-colorway     :accent
                                :-surface      :solid}
                :attrs/display {:-size :small}
                :args          ["Next"]
                ;; :x-variants    [surface]
                }}

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
      :desc    "Corresponds to the font-size based on Kushi's font-size scale."
      :demo    {:label         "Size variants"
                :attrs         {:-end-enhancer [icon :east]
                                :-colorway     :accent
                                :-surface      :solid}
                :attrs/display {:-surface :solid}
                :rows?         true
                :args          ["Next"]}}

     stroke-align   
     {:pred    #{:outside :inside}
      :default nil
      :desc    "Alignment of the stroke. Only applies to `:-surface` `:outline`"
      :demo    {:label   "Stroke alignment"
                :require [[kushi.ui.icon.core :refer [icon]]]
                :samples [[button {:-end-enhancer [icon :east]
                                   :-size         :small
                                   :-colorway     :accent
                                   :-surface      :outline
                                   :-stroke-align :inside}
                           "Next"]

                          [button {:-end-enhancer [icon :east]
                                   :-size         :small
                                   :-colorway     :accent
                                   :-surface      :outline
                                   :-stroke-align :outside}
                           "Next"]

                          ;; [button {:style         {"--outlined-element-stroke-width" :3px
                          ;;                          "--stroke-color"                  :currentColor}
                          ;;          :-end-enhancer [icon :east]
                          ;;          :-size         :small
                          ;;          :-surface      :outline
                          ;;          :-stroke-align :inside}
                          ;;  "Next"]
                          ;; [button {:style         {"--outlined-element-stroke-width" :3px
                          ;;                          "--stroke-color"                  :currentColor}
                          ;;          :-end-enhancer [icon :east]
                          ;;          :-size         :small
                          ;;          :-surface      :outline
                          ;;          :-stroke-align :outside}
                          ;;  "Next"]
                          
                          ]}}

     start-enhancer 
     {:pred    #{string? keyword?}
      :default nil
      :desc    "Content at the inline-start position, following the button text. Typically an icon."
      :demo    {:label   "Start-enhancer icons"
                :require [[kushi.ui.icon.core :refer [icon]]]
                :samples [[button {:-start-enhancer [icon :pets]
                                   :-size           :small
                                   :-colorway       :accent
                                   :-surface        :solid}
                           "Pets"]
                          
                          [button {:-start-enhancer [icon :auto-awesome]
                                   :-size           :small
                                   :-colorway       :accent
                                   :-surface        :soft}
                           "Wow"]

                          [button {:-start-enhancer [icon :play-arrow]
                                   :-size           :small
                                   :-colorway       :accent
                                   :-surface        :outline}
                           "Play"]]}}

     end-enhancer   
     {:pred    #{string? keyword?}
      :default nil
      :desc    "Content at the inline-end position, preceding the button text. Typically an icon."
      :demo    {:label   "End-enhancer icons"
                :require [[kushi.ui.icon.core :refer [icon]]]
                :samples [
                          [button {:-end-enhancer [icon :pets]
                                   :-size         :small
                                   :-colorway     :accent
                                   :-surface      :solid}
                           "Pets"]
                          
                          [button {:-end-enhancer [icon :auto-awesome]
                                   :-size         :small
                                   :-colorway     :accent
                                   :-surface      :soft}
                           "Wow"]

                          [button {:-end-enhancer [icon :play-arrow]
                                   :-size         :small
                                   :-colorway     :accent
                                   :-surface      :outline}
                           "Play"]]}}
                           
     loading?       
     {:pred    boolean?
      :default false
      :desc    "When `true`, this will set the appropriate values for `aria-busy` and `aria-label`"
      :demo    {:label "Loading and disabled states"
                :require [[kushi.ui.button.core :refer [button]]
                          [kushi.ui.icon.core :refer [icon]]
                          [kushi.ui.spinner.core :refer [donut propeller thinking]]]
                :samples [[button
                           {:-loading?     true
                            :-colorway     :accent
                            :-surface      :solid
                            :-size         :small
                            :-end-enhancer [donut]}
                           "Play"]

                          [button
                           {:-loading?     true
                            :-colorway     :accent
                            :-surface      :solid
                            :-size         :small
                            :-end-enhancer [propeller]}
                           "Play"]

                          [button
                           {:-loading?     true
                            :-colorway     :accent
                            :-surface      :solid
                            :-size         :small
                            :-end-enhancer [thinking]}
                           "Play"]

                          [button
                           {:-loading?     true
                            :-colorway     :accent
                            :-surface      :solid
                            :-size         :small}
                           [:span {:style {:visibility :hidden :width :0px}} "Play"]
                           [thinking]]

                          [button
                           {:disabled      true
                            :-colorway     :accent
                            :-surface      :solid
                            :-size         :small
                            :-end-enhancer [icon :play-arrow]}
                           "Play"]]}}]

   :display  '{:docs     {:order [:summary :desc :toks]
                          :parse {:summary 'x
                                  :desc 'y
                                  :toks 'z}
                          :exclude #{:toks}}
               :showcase {:toks {:order ["tok family name" "..."]
                                 :exclude #{"..."}}
                          :order [surface shape]
                          :exclude #{start-enhancer size}}}}

  [& args]
  (let [{:keys [opts attrs children]}
        (extract args button)

        {:keys [start-enhancer
                loading?
                end-enhancer
                stroke-align
                packing
                size
                icon]}
        opts

        ;;  _ (? :pp [opts children])

        {:keys             [shape surface]
         semantic-colorway :colorway}

        (get-variants opts)
        
        styling-class
        (css ".kushi-button"
             :.transition
             :position--relative
             :d--flex
             :flex-direction--row
             :jc--c
             :ai--c
             :w--fit-content
             :h--fit-content
             :gap--$icon-enhanceable-gap
             :cursor--pointer
             [:--_padding-block :$button-padding-block]
             [:--_padding-inline :$button-padding-inline]
             ["[aria-label='loading']>.kushi-spinner-propeller:d" :revert]
             ["[aria-label='loading']>.kushi-icon:d" :none]
             :pi--$_padding-inline
             :pb--$_padding-block
             )]
    
    ;; TODO maybe use :data-kushi-ui "button"

    ;; TODO incorporate into docs
    (into 
     [:button
      (merge-attrs
       {:class                     styling-class
        :aria-busy                 loading?
        :aria-label                (when loading? "loading")
        :data-kushi-ia             ""
        :data-kushi-size           (validate-option button size)
        :data-kushi-shape          (validate-option button shape)
        :data-kushi-surface        (validate-option button surface)
        :data-kushi-packing        (validate-option button packing)
        :data-kushi-colorway       (validate-option button semantic-colorway)
        :data-kushi-stroke-align   (validate-option button stroke-align)
        :data-kushi-end-enhancer   (when (and (not icon) end-enhancer) "")
        :data-kushi-start-enhancer (when (and (not icon) start-enhancer) "")}
       attrs)]
          (cond icon           [[kushi.ui.icon.core/icon :star]]
                start-enhancer (concat [start-enhancer] children)
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
             :br "`:$icon-button-padding-inline`"
             :br "The default value is `:0.69em`"
             :br
             :br "`:$icon-button-padding-block`"
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
        {:keys [opts attrs children]}
        (extract args icon-button)

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
             :data-kushi-colorway     semantic-colorway
             :data-kushi-size         size
             :data-kushi-packing      (some-> packing
                                              (maybe nameable?)
                                              as-str
                                              (maybe #{"compact" "roomy"})
                                              (data-kushi- :packing))}
            ;; (when loading? {:data-kushi-ui-spinner ""})
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


;; Sample component built with 2-fn macro pattern 
;; (defn big-paw
;;   {:doc "Hi from big button"
;;   ;;  :opts '{size {:pred #{:small :large :xxxlarge}}}
;;    }
;;   [& args]
;;   (let [{:keys [opts attrs children]}
;;         (extract args big-paw)]
;;     (into [:div 
;;            (merge-attrs 
;;             #_(sx :c--red)
;;             {:style {"--fs" (case (:size opts)
;;                               :small    "20px"
;;                               :large    "80px"
;;                               :xxxlarge :200px
;;                               nil)}
;;              :class (css :c--red :fs--$fs)}
;;             attrs)]
;;           children)))
