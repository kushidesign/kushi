(ns kushi.ui.button
  (:require
   [fireworks.core :refer [? !? ?> !?>]]
   [clojure.string :as string]
   [kushi.core :refer (css-vars-map css defcss sx merge-attrs validate-option)]
   [kushi.ui.core :refer (extract)]
   [kushi.ui.icon :refer [icon]]
   [kushi.ui.shared.theming :refer [data-ks- get-variants hue-style-map]]
   [kushi.ui.util :refer [as-str maybe nameable?]])
   ;; (:require-macros [kushi.ui.button])
  )

;; [kushi.ui.variants :refer [variants-by-custom-opt-key convert-opts]]
;; (? :pp (-> button var meta :opts convert-opts))



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
   ;; Should we use keys like :colorway, same as call-site
   :opts   
   {

    :sizing       {:default nil
                   :desc    "Corresponds to the font-size based on Kushi's font-size scale."
                   }


    :colorway     {:default nil
                   :desc    "Colorway of the button. Can also be a named color from Kushi's design system e.g `:red` `:purple` `:gold` etc."
                   }


    :contour      {:default :round
                   :desc    "Shape of the button."
                   }
    
    :stroke-align {:schema  #{:inside :outside}
                   :default nil
                   :desc    "Alignment of the stroke. Only applies to `:surface` `:outline`"
                   }
    
    :packing        {:default nil
                     :desc    "General amount of padding inside the button"
                     }
    
    :end-enhancer   {:schema    #(or (string? %) (keyword? %) (vector? %))
                      :default nil
                      :desc    "Content at the inline-end position preceding the button text. Typically an icon."
                      }
    
    :start-enhancer {:schema  [:or :string :keyword vector?]
                     :default nil
                     :desc    "Content at the inline-start position following the button text. Typically an icon."
                     }
    
    :loading?       {:schema  boolean?
                     :default false
                     :desc    "When `true` this will set the appropriate values for `aria-busy` and `aria-label`"
                     }
    
    :surface        {:default :round
                     :desc    "Surface variant of the button."
                     }
    }

   :demos    '[{:label   "Start-enhancer icons"
                :desc    "Content at the inline-start position following the button text. Typically an icon." }]

  ;;  :display  '{:docs     {:order [:summary :desc :toks]
  ;;                         :parse {:summary 'x
  ;;                                 :desc 'y
  ;;                                 :toks 'z}
  ;;                         :exclude #{:toks}}
  ;;              :showcase {:toks {:order ["tok family name" "..."]
  ;;                                :exclude #{"..."}}
  ;;                         :order [surface shape]
  ;;                         :exclude #{start-enhancer size}}}

   }

  [& args]
  (let [{:keys [opts attrs children]}
        (extract args [:loading? :stroke-align])

        {:keys [start-enhancer
                end-enhancer
                stroke-align
                loading?
                packing
                weight
                sizing
                contour
                surface
                colorway]}
        opts
         
        start-enhancer
        (if (keyword? start-enhancer) [icon start-enhancer] start-enhancer)

        end-enhancer
        (if (keyword? end-enhancer) [icon end-enhancer] end-enhancer)

        ;; {:keys [contour surface colorway]}
        ;; (get-variants opts)
        
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
             :pi--$_padding-inline
             :pb--$_padding-block
             ;; TODO what are these???
             ["[aria-label='loading']>.kushi-spinner-propeller:d" :revert]
             ["[aria-label='loading']>.kushi-icon:d" :none])]
    
    ;; TODO incorporate into docs
    (into 
     [:button
      (merge-attrs
       {:class                  styling-class
        :aria-busy              loading?
        :aria-label             (when loading? "loading")
        :data-ks-ia             ""
        :data-ks-sizing         sizing
        :data-ks-weight         weight
        :data-ks-contour        (or contour :rounded)
        :data-ks-surface        surface
        :data-ks-packing        packing
        :data-ks-colorway       colorway
        :data-ks-stroke-align   stroke-align
        :data-ks-end-enhancer   (when end-enhancer "")
        :data-ks-start-enhancer (when start-enhancer "")}
       attrs)]
          (cond start-enhancer (concat [start-enhancer] children)
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
               :schema    boolean?
               :default false
               :desc    "When `true`, this will set the appropriate values for
                         `aria-busy` and `aria-label`"}]}
  [& args]
  (let [
        {:keys [opts attrs children]}
        (extract args)

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

    ;; TODO maybe use :data-ks-name "button"
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
             :data-ks-ia           ""
             :data-ks-surface      surface
             :data-ks-contour        shape
             :data-ks-ui-spinner   (when loading? "")
             :data-ks-stroke-align (some-> stroke-align 
                                              (maybe #{:outside "outside"}))
             :data-ks-colorway     semantic-colorway
             :data-ks-sizing         size
             :data-ks-packing      (some-> packing
                                              (maybe nameable?)
                                              as-str
                                              (maybe #{"compact" "roomy"})
                                              (data-ks- :packing))}
            ;; (when loading? {:data-ks-ui-spinner ""})
            (some-> stroke-align 
                    (maybe #{:outside "outside"})
                    (data-ks- :stroke-align))
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
          (if icon [kushi.ui.icon/icon icon]
              children))))


;; Sample component built with 2-fn macro pattern 
;; (defn big-paw
;;   {:doc "Hi from big button"
;;   ;;  :opts '{size {:schema #{:small :large :xxxlarge}}}
;;    }
;;   [& args]
;;   (let [{:keys [opts attrs children]}
;;         (extract args)]
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
