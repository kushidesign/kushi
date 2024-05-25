(ns kushi.ui.input.radio.core
  (:require-macros
   [kushi.core :refer (sx)])
  (:require [domo.core :as domo]
            [kushi.core :refer (merge-attrs)]
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
       (sx 'kushi-radio
           :.transition
           :.xxfast!
           :.pointer
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
        (sx 'kushi-radio-input
            :.transition
            :.xxfast!
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
            :outline-style--solid
            :outline-width--$input-border-weight-normal
            :outline-color--currentColor
            :outline-offset---0.15em
            :checked:outline-width--0.333em
            :checked:outline-offset---0.333em
            :checked:o--1
            :o--0.6
            :border-radius--50%
            {:data-kushi-ui :input.radio
             :type          :radio
             :on-click      #(let [el         (domo/et %)
                                   nm         "data-kushi-radio-input-mousedown"
                                   mousedown? (domo/attribute-true? el nm)]

                               (when-not mousedown? (.preventDefault %))
                               (if mousedown?
                                 (domo/remove-attribute! el nm)
                                 (domo/set-attribute! (domo/et %) nm true)))
             :on-mouse-down #(let [el (domo/et %)
                                   nm "data-kushi-radio-input-mousedown"]
                               (domo/set-attribute! el nm true)
                               (.click el))
             })
        input-attrs)]]
     children)))
