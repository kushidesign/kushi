(ns kushi.ui.radio.core
  (:require
   [kushi.css.core :refer (css sx merge-attrs)]
   [kushi.ui.core :refer (opts+children)]))

(defn radio
  {:desc ["Radio buttons are used in groups of 2 or more, when only one choice may be selected from an array of options."]
   :opts '[{:name    input-attrs
            :pred    map?
            :default nil
            :desc    "HTML attributes map applied to the underlying `input` div."}]}
  [& args]
  (let [[opts attrs & children] (opts+children args)
        {:keys [input-attrs]}   opts]
    (into
     [:label
      (merge-attrs
       (sx
        ".kushi-radio"
        :.transition
        :transition-duration--$xxfast
        :cursor--pointer
        :.pill
        :color--currentColor
        :line-height--1.1
        :display--grid
        :grid-template-columns--1em:auto
        :gap--0.4em
        :padding-inline--0.5em:1em
        :padding-block--0.4em
        :>*:align-self--center)
       attrs)
      [:input
       (merge-attrs
        {:class         (css
                         ".kushi-radio-input"
                         :.transition
                         :transition-duration--$xxfast
                         :cursor--pointer
                         [:border-color
                          "color-mix(in hsl, currentColor 55%, transparent)"]
                         [:checked:border-color
                          :currentColor]
                         :display--grid
                         :place-content--center
                         :-webkit-appearance--none
                         :appearance--none
                         :bgc--white
                         :m--0
                         :font--inherit
                         :color--currentColor
                         :width--1em
                         :height--1em
                         :border-style--solid
                         :border-width--$input-border-weight-normal
                         :border-color--currentColor
                         :checked:border-width--0.333em
                         :checked:border-offset---0.333em
                         :o--1
                         :border-radius--50%)
         :data-kushi-ui :input.radio
         :type          :radio}
        input-attrs)]]
     children)))
