(ns kushi.ui.input.radio.button.core
  (:require-macros
   [kushi.core :refer (sx)])
  (:require
   [kushi.core :refer (merge-with-style)]
   [kushi.ui.core :refer (opts+children)]))

(defn radio-button
  "Radio button"
  [& args]
  (let [[opts attr & children] (opts+children args)
        {:keys []}             opts]
    [:label
     (sx 'kushi-radio-button-wrapper
         :.transition
         :.fast!
         :.pointer
         :.pill
         :c--:--gray700
         :line-height--1.1
         :display--grid
         :grid-template-columns--1em:auto
         :gap--0.4em
         :padding-inline--0.5em:1em
         :padding-block--0.4em
         :hover:bgc--:--gray50
         :>*:align-self--center)
     [:input
      (merge-with-style
       (sx 'kushi-radio-button
           :.transition
           :.fast!
           :.pointer
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
           :outline--0.15em:solid:currentColor
           :outline-offset---0.15em
           :checked:outline-width--0.333em
           :checked:outline-offset---0.333em
           :checked:o--1
           :o--0.6
           :border-radius--50%
           {:type  :radio
            :style {}})
       attr)]
     children]))
