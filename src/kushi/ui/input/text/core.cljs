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
           {:name    input-attrs
            :type    :map
            :default nil
            :desc    "html attributes map applied to the underlying `input` div."}]}
  [& args]
  (let [
        [opts attrs & children] (opts+children args)
        {:keys [input-attrs
                start-enhancer
                end-enhancer]}   opts]
       [:div
        (merge-attrs
         (sx :.flex-row-fs
             :ai--stretch
             :jc--sb
             :w--100%
             :min-height--34px
             :bw--1px
             :bs--solid
             :bc--black)
         attrs)
        (when start-enhancer
          [:div
           (sx 'kushi-text-input-start-enhancer
               :d--if
               :flex-direction--column
               :jc--c
               :pis--0.5em)
           start-enhancer])
        [:div (sx :flex-grow--1)
         [:input
          (merge-attrs
           (sx 'kushi-text-input-input
               :.transition
               :h--100%
               :w--100%
               :pi--0.5em
               :placeholder:o--0.4
               {:type        :text
                :placeholder "asdfhyere"})
           input-attrs)]]
        (when end-enhancer
          [:div
           (sx 'kushi-text-input-end-enhancer
               :d--if
               :flex-direction--column
               :jc--c
               :&_.kushi-mui-icon:pi--0)
           end-enhancer])]))
