(ns kushi.ui.button.core
  (:require
   [fireworks.core :refer [? !? ?> !?>]]
   [clojure.string :as string]
   [kushi.core :refer (css defcss sx merge-attrs validate-opt)]
   [kushi.ui.core :refer (opts+children)]
   [kushi.ui.icon.core]
   [kushi.ui.shared.theming :refer [data-kushi- get-variants hue-style-map]]
   [kushi.ui.util :refer [as-str maybe nameable?]])
  (:require-macros [kushi.ui.button.core]))


(defn ^:public button
  {:summary "Buttons provide cues for actions and events."

   :desc    "Buttons are fundamental components that allow users to process actions or navigate an experience.
              
             They can be custom styled via a variety of tokens in your theme.

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
   
   :opts   '[colorway       
             {:pred     #{:neutral :accent :positive :negative :warning}
              :default  nil
              :desc     "Colorway of the button. Can also be a named color from Kushi's design system, e.g `:red`,
                        `:purple`, `:gold`, etc."
              :demo   {:label    "Colorway + Surface variants"
                       :variants [colorway surface]}}
             surface        
             {:pred    #{:soft :solid :outline :minimal}
              :default :round
              :desc    "Surface variant of the button."}

             shape          
             {:pred    #{:sharp :rounded :pill}
              :default :round
              :desc    "Shape of the button."
              :demo    {:label    "Shape variants"
                        :variants [shape]}}

             loading?       
             {:pred    boolean?
              :default false
              :desc    "When `true`, this will set the appropriate values for `aria-busy` and `aria-label`"}

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
              :desc    "Corresponds to the font-size based on Kushi's font-size scale."}

             start-enhancer 
             {:pred    #{string? keyword?}
              :default nil
              :desc    "Content at the inline-start position, following the button text. Typically an icon."}

             end-enhancer   
             {:pred            #{string? keyword?}
              :default         nil
              :desc            "Content at the inline-end position, preceding the button text. Typically an icon."
              :snippets-header "Fix me"
              :demo            {:label   "Examples using end enhancer icons"
                                :require [[kushi.ui.icon.core :refer [icon]]
                                          [smth.core :refer [duh]]
                                          [clojure.string :as string]]
                                :samples [^{:label "oneee"}
                                          [button {:class         ["xxxsmall"]
                                                   :-end-enhancer [icon :pets]
                                                   :-colorway     :accent
                                                   :-surface      :solid}
                                           "Pets"]
                                          
                                          ^{:label "two"}
                                          [button {:class         ["xxxsmall"]
                                                   :-end-enhancer [icon :auto-awesome]
                                                   :-colorway     :accent
                                                   :-surface      :soft}
                                           "Wow"]

                                          ^{:label "three"}
                                          [button {:class         ["xxxsmall"]
                                                   :-end-enhancer [icon :play-arrow]
                                                   :-colorway     :accent
                                                   :-surface      :outline}
                                           "Play"]
                                          ]}}]

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
    (into 
     [:button
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
        ;; :data-kushi-stroke-align   (some-> stroke-align 
        ;;                                    (maybe #{:outside "outside"}))
        }
       ;; hue-style-map -> {:style {:--_hue "33"}} value - unused for now
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

(def args-fake
  {:opts     {:go? true}
   :attrs    {:id "hey"}
   :children [[:div "child 1"]]})

(def debug-kushi-ui? true)

(defn buttonx*
  {:desc "Hi from buttonx*"
   :opts '{size {:pred #{:small :large :xxxlarge}}}}
  [src & args]
  (let [[opts attrs & children]
        (opts+children args)]
    
    ;; macro here
    ;; (validate-opts buttonx* optional-derefed-state)
    ;; =>
    (when (and ^boolean js/goog.DEBUG debug-kushi-ui?)
      (kushi.core/validate-opts (-> buttonx* var meta) opts src))

    ;; (? opts)
    ;; (? attrs)

    (into [:div
           (merge-attrs 
            (sx :c--red
                :fs--100px)
            attrs)]
          (? children))))
