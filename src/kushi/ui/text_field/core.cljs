(ns kushi.ui.text-field.core
  (:require
   [kushi.core :refer (sx css defcss css-vars-map merge-attrs)]
   [kushi.ui.core :refer (extract)]))

(defcss "@layer kushi-ui-component kushi-text-input-enhancer"
  :d--if
  :ai--center
  :jc--c
  :pi--0.375em )

(defn- enhancer [x]
  [:div
   (sx ".kushi-text-input-start-enhancer"
       :d--if
       :ai--center
       :jc--c
       :pi--0.375em )
   x])

(defn- text-field* [& args]
  (let [[opts attrs & _]       
        (extract args text-field*)

        {:keys [wrapper-attrs
                start-enhancer
                end-enhancer
                ;; TODO - change this out when new theming comes in
                colorway
                textarea?]}     
        opts]
    [:div
     (merge-attrs
      {:data-kushi-colorway colorway}
      (sx
       ".kushi-text-input-wrapper"
       :.flex-row-fs
       [:bc
        "color-mix(in srgb, currentColor var(--text-input-border-intensity, 75%), transparent)"]
       [:dark:bc
        "color-mix(in srgb, currentColor var(--text-input-border-intensity-dark-mode, 55%), transparent)"]
       ["focus-within:bgc"
        "var(--transparent-white-70)!important"]
       ["dark:focus-within:bgc"
        "var(--transparent-black-20)!important"]
       [:focus-within:c
        :currentColor!important]
       [:focus-within:bc
        "rgba(0, 125, 250, 1)"]
       :ai--stretch
       :jc--sb
       :w--100%
       :w--auto
       :min-height--34px
       :bw--1px
       :bs--solid
       :border-radius--$text-input-border-radius
       :bgc--$transparent-white-70
       :dark:bgc--$transparent-black-20
       :_textarea:border-radius--$text-input-border-radius
       :_input:border-radius--$text-input-border-radius)
      wrapper-attrs)
     (when (and start-enhancer
                (not textarea?)) 
       [enhancer start-enhancer])
     [:div (sx ".kushi-text-input-input-wrapper" :flex-grow--1)
      (if textarea?
        [:textarea
         (merge-attrs
          (sx
           ".kushi-text-input-input"
           :.transition
           :h--100%
           :w--100%
           :pi--0.5em
           :pb--0.5em
           :placeholder:o--0.4)
          attrs)]
        [:input
         (merge-attrs
          {:class (css
                   ".kushi-text-input-input"
                   :.transition
                   :h--100%
                   :w--100%
                   :pi--0.5em
                   :pb--0.5em
                   :placeholder:o--0.4)
           :type  :text}
          attrs)])]
     (when (and end-enhancer
                (not textarea?))
       [enhancer end-enhancer])]))

(defn text-field
  {:summary "A text-field enables the entry of text."
   :desc "An input enables the entry of text. By default, this component will
          use an `<input>` element of type `text`. If the option `:-textarea?`
          is set to `true`, a `<textarea>` element will be used instead."
   :opts '[{:name    textarea?
            :schema    boolean?
            :default false
            :desc    "Setting to `true` will render an html `<textarea/>`
                      element, instead of a <input type='text'/> element."}

           {:name    outer-wrapper-attrs
            :schema    map?
            :default nil
            :desc    "HTML attributes map applied to the outermost div of the
                      component. This div wraps the label, input-wrapper div,
                      and the helper text span."}

           {:name    label
            :schema    string?
            :default nil
            :desc    "The text for `:label` element associated with the input field."}

           {:name    label-attrs
            :schema    map?
            :default nil
            :desc    "HTML attributes map applied to the `:label` element that
                      contains the `label` text."}

           {:name    label-placement
            :schema    #{:block :inline}
            :default :block-start
            :desc    "HTML attributes map applied to the `label` element
                      associated with the `input` element, and end-enhancer div."}

           {:name    label-width
            :schema    #(or (string? %) (keyword? %))
            :default :block-start
            :desc    "Sets the width of your label \"column\", when
                      `:-label-placement` is set to `:inline`. Must be a valid
                      css width value (`px`, `em` `rem`, etc)"}

           {:name    wrapper-attrs
            :schema    map?
            :default nil
            :desc    "HTML attributes map applied to the input wrapper div,
                      which is bordered by default. This div wraps the
                      `start-enhancer` div, the actual `input` element, and the
                      `end-enhancer` div."}

           {:name    start-enhancer
            :schema    #(or (string? %) (vector? %))
            :default nil
            :desc    "A string, hiccup vector, or child component intended to
                      aid the user and positioned within the input field area,
                      at the start"}

           {:name    end-enhancer
            :schema    #(or (string? %) (vector? %))
            :default nil
            :desc    "A string, hiccup vector, or child component intended to
                      aid the user and positioned within the input field area,
                      at the end"}

           {:name    helper
            :schema    string?
            :default nil
            :desc    ["The text for `:.kushi-text-input-helper` label."
                      "If used, this should give the user actionable information
                       about the value of the associated input field."]}

           {:name    colorway
            :schema    #{:neutral :accent :positive :negative :warning}
            :default nil
            :desc    ["The text for `:.kushi-text-input-helper` label."
                      "If used, this should give the user actionable information
                       about the value of the associated input field."]}
           ]}
  [& args]
  (let [[opts attrs & _]
        (extract args text-field)

        {:keys [
                outer-wrapper-attrs
                label
                label-placement
                label-attrs
                wrapper-attrs
                start-enhancer
                end-enhancer
                helper
                colorway
                textarea?]
         :or   {label " "}}         
        opts

        {:keys [required
                disabled]}          
        attrs

        input-id                    
        (:id attrs)

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
         :data-kushi-colorway colorway}

        helper-label-attrs
        (when helper
          (merge-attrs
           label-text-attrs
           (sx ".kushi-text-input-helper"
               :.foreground-color-secondary
               :.inline-block
               :fw--$normal
               :mbs--$text-input-helper-margin-block-start||0.3em)
           (when disabled {:class (css ".kushi-text-input-helper-disabled"
                                       :.disabled)})))

        wrapped-input
        [text-field* (merge attrs
                            {:-wrapper-attrs    wrapper-attrs
                             :-start-enhancer   start-enhancer
                             :-end-enhancer     end-enhancer
                             :data-kushi-colorway colorway
                             :-textarea?        textarea?})]

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
         label]


        kushi-input-attrs 
        (merge-attrs (sx ".kushi-input" :ai--c)
                     (when inline?
                       (sx ".kushi-input-inline"
                           :d--grid
                           [:gtc "auto minmax(0, 1fr)"]))
                     outer-wrapper-attrs)]
    [:div
     kushi-input-attrs
     label-with-attrs
     wrapped-input
     (when helper
       [:<>
        (when inline? [:div])
        [:span helper-label-attrs helper]])]))
