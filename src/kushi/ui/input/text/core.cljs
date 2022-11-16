(ns kushi.ui.input.text.core
  (:require-macros
   [kushi.core :refer (sx)])
  (:require
   [kushi.core :refer (merge-attrs)]
   [kushi.ui.core :refer (opts+children)]))

(defn input
  {:desc ["An input enables the entry of single lines of text."]
   :opts '[{:name    start-enhancer
            :type    #{string vector}
            :default nil
            :desc    "A string, hiccup vector, or child component intended to aid the user and positioned within the input field area, at the start"}
           {:name    end-enhancer
            :type    #{string vector}
            :default nil
            :desc    "A string, hiccup vector, or child component intended to aid the user and positioned within the input field area, at the end"}
           {:name    outer-wrapper-attrs
            :type    :map
            :default nil
            :desc    ["html attributes map applied to the div that wraps the outermost div of the component."
                      "This div wraps the label, input-wrapper div, and the helper text span."]}
           {:name    input-wrapper-attrs
            :type    :map
            :default nil
            :desc    "html attributes map applied to the div that wraps the start-enhancer div, `input` element, and end-enhancer div."}]}
  [& args]
  (let [[opts attrs & children] (opts+children args)
        {:keys [label
                helper
                attrs
                outer-wrapper-attrs
                wrapper-attrs
                start-enhancer
                end-enhancer]}   opts
        input-id (:id attrs)]
    [:div
     (merge-attrs
      (sx 'kushi-text-input-outer-wrapper
          :ai--center)
      outer-wrapper-attrs)
     (when label [:label
                  (sx 'kushi-text-input-label
                      :.small
                      :mbe--0.5em
                      :d--block
                      {:for input-id})
                  label])
     [:div
      (merge-attrs
       (sx 'kushi-text-input-wrapper
           :.flex-row-fs
           :ai--stretch
           :jc--sb
           :w--100%
           :min-height--34px
           :bw--1px
           :bs--solid
           :bc--currentColor
           [:focus-within:bc '(rgba 0 125 250 1)])
       wrapper-attrs)
      (when start-enhancer
        [:div
         (sx 'kushi-text-input-start-enhancer
             :d--if
             :ai--center
             :jc--c
             [:pi (when (string? start-enhancer) :0.375em)])
         start-enhancer])
      [:div (sx 'kushi-text-input-input-wrapper :flex-grow--1)
       [:input
        (merge-attrs
         (sx 'kushi-text-input-input
             :.transition
             :h--100%
             :w--100%
             :pi--0.5em
             :placeholder:o--0.4
             {:type :text})
         attrs)]]
      (when end-enhancer
        [:div
         (sx 'kushi-text-input-end-enhancer
             :d--if
             :ai--center
             :jc--c
             [:pi (when (string? end-enhancer) :0.375em)])
         end-enhancer])]
     (when helper
       [:span
        (sx 'kushi-text-input-helper
            :d--block
            :.small
            :o--0.6
            :mbs--0.5em)
        helper])]))
