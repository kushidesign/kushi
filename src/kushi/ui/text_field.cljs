(ns kushi.ui.text-field
  (:require
   [kushi.core :refer (sx css defcss css-vars-map merge-attrs)]
   [kushi.ui.icon :refer (icon)]
   [kushi.ui.shared :refer [enhancer]]
   [kushi.ui.core :refer (extract defui)]))

(defui text-field
  {:summary      "A text-field enables the entry of text."
   :desc         "An input enables the entry of text. By default, this component will
          use an `<input>` element of type `text`. If the option `:textarea?`
          is set to `true`, a `<textarea>` element will be used instead."
   :props/shared [:end-enhancer :start-enhancer :colorway]
   :props        {:helper-text         {:schema  :string
                                        :default nil
                                        :desc    ["The text for `:.kushi-text-input-helper` label."
                                                  "If used, this should give the user actionable information about the value of the associated input field."]}
                  :label-text          {:schema  :string
                                        :default nil
                                        :desc    "The text for `:label` element associated with the input field."}
                  :label-attrs         {:schema  :map
                                        :default nil
                                        :desc    "HTML attributes map applied to the `:label` element that contains the `label` text."}
                  :label-placement     {:schema  [:enum :block :inline :block-start]
                                        :default :block-start
                                        :desc    "HTML attributes map applied to the `label` element associated with the `input` element, and end-enhancer div."}
                  :label-width         {:schema  [:or :string :keyword]
                                        :default :block-start
                                        :desc    "Sets the width of your label \"column\", when `:label-placement` is set to `:inline`. Must be a valid css width value (`px`, `em` `rem`, etc)"}
                  :outer-wrapper-attrs {:schema  :map
                                        :default nil
                                        :desc    "HTML attributes map applied to the outermost div of the component. This div wraps the label, input-wrapper div, and the helper text span."}
                  :textarea?           {:schema  :boolean
                                        :default false
                                        :desc    "Setting to `true` will render an html `<textarea/>` element, instead of a <input type='text'/> element."}
                  :wrapper-attrs       {:schema  :map
                                        :default nil
                                        :desc    "HTML attributes map applied to the input wrapper div, which is bordered by default. This div wraps the `start-enhancer` div, the actual `input` element, and the `end-enhancer` div."}}}
  [& args]
  (let [{:keys [outer-wrapper-attrs
                label-text
                label-placement
                label-attrs
                wrapper-attrs
                start-enhancer
                end-enhancer
                helper-text
                colorway
                textarea?]
         :or   {label-text " "}}         
        &props

        {:keys [required
                disabled]}          
        &attrs

        input-id                    
        (:id &attrs)

        inline?                     
        (= :inline label-placement)

        label-text-attrs   
        {:class              (css ".kushi-text-input-label-text"
                                  :.minimal
                                  :.info
                                  :d--block
                                  :.small
                                  :fw--$wee-bold
                                  :hover:bgc--transparent!important
                                  :active:bgc--transparent!important)
         :data-ks-colorway colorway}

        helper-label-attrs
        (when helper-text
          (merge-attrs
           label-text-attrs
           (sx ".kushi-text-input-helper"
               :.foreground-color-secondary
               :.inline-block
               :fw--$normal
               :mbs--$text-input-helper-margin-block-start||0.3em)
           (when disabled {:class (css ".kushi-text-input-helper-disabled"
                                       :.disabled)})))

        label-with-attrs
        [:label
         (merge-attrs
          label-text-attrs
          (let [after-content (when required "\"*\"")
                after-color (when required "var(--negative-600)")]
            {:style (css-vars-map after-content after-color)
             :class (css ".kushi-text-input-label"
                         :.inline-block
                         [:after:content :$after-content]
                         [:after:c :$after-color]
                         :after:pis--0.15em)
             :for   input-id})
          (when disabled {:class (css ".kushi-text-input-label-disabled" :.disabled)})
          (if inline?
            (sx ".kushi-text-input-label-inline"
                [:mie :$text-input-label-inline-margin-inline-end||0.7em])
            (sx ".kushi-text-input-label-block"
                [:mbe :$text-input-label-block-margin-block-end||0.4em]))
          label-attrs)
         label-text]


        kushi-input-attrs 
        (merge-attrs (sx ".kushi-input" :ai--c)
                     (when inline?
                       (sx ".kushi-input-inline"
                           :d--grid
                           [:gtc "auto minmax(0, 1fr)"]))
                     outer-wrapper-attrs)
        
        wrapped-input
        [:div
         (merge-attrs
          {:data-ks-colorway colorway}
          (sx
           ".kushi-text-input-wrapper"
           :.flex-row-fs
           {:display                                         :flex
            :flex-direction                                  :row
            :justify-content                                 :space-between
            :align-items                                     :stretch
            ;; :w                     :100%
            :width                                           :auto
            :min-height                                      :34px
            :bgc                                             :$transparent-white-70
            :dark:bgc                                        :$transparent-black-20
            :focus-within:bgc                                "var(--transparent-white-70)!important"
            :dark:focus-within:bgc                           "var(--transparent-black-20)!important"
            :focus-within:c                                  :currentColor!important
            :focus-within:bc                                 "rgba(0, 125, 250, 1)"
            :_textarea:border-radius                         :$text-input-border-radius
            :_input:border-radius                            :$text-input-border-radius
            :border-width                                    :1px
            :border-style                                    :solid
            :border-radius                                   :$text-input-border-radius
            :border-color                                    :currentColor
            :dark:border-color                               :currentColor
            "@supports (color: color-mix(in oklch, red, red))" {:border-color      "color-mix(in srgb, currentColor var(--text-input-border-intensity, 75%), transparent)"
                                                                :dark:border-color "color-mix(in srgb, currentColor var(--text-input-border-intensity-dark-mode, 55%), transparent)"}
            :_.kushi-text-input-enhancer                     {:d  :inline-flex
                                                              :ai :center
                                                              :jc :c
                                                              :pi :0.375em}
            })
          wrapper-attrs)
         (when (and start-enhancer (not textarea?)) 
           [:div
            {:class [:kushi-text-input-enhancer 
                     :kushi-text-input-start-enhancer 
                     (when disabled :disabled)]}
            [enhancer start-enhancer]])
         [:div (sx ".kushi-text-input-input-wrapper" :flex-grow--1)
          (if textarea?
            [:textarea
             (merge-attrs
              (sx ".kushi-text-input-input"
                  :.transition
                  :h--100%
                  :w--100%
                  :pi--0.5em
                  :pb--0.5em
                  :placeholder:o--0.4)
              &attrs)]
            [:input
             (merge-attrs
              {:class (css ".kushi-text-input-input"
                           :.transition
                           :h--100%
                           :w--100%
                           :pi--0.5em
                           :pb--0.5em
                           :placeholder:o--0.4)
               :type  :text}
              &attrs)])]
         (when (and end-enhancer (not textarea?)) 
           [:div
            {:class [:kushi-text-input-enhancer
                     :kushi-text-input-end-enhancer 
                     (when disabled :disabled)]}
            [enhancer end-enhancer ]])]
        ]
    [:div 
     kushi-input-attrs
     label-with-attrs
     wrapped-input
     (when helper-text
       [:<>
        (when inline? [:div])
        [:span helper-label-attrs helper-text]])]))
