(ns kushi.ui.input.checkbox.core
  (:require-macros
   [kushi.core :refer (sx)])
  (:require
   [kushi.core :refer (merge-with-style)]
   [kushi.ui.core :refer (opts+children)]))

;; TODO outlines for ally
(defn checkbox
  {:desc ["Checkboxes are used to provide multiple options for selection. One or more checkboxes can be checked at a time."]
   :opts '[{:name    input-attrs
            :type    :map
            :default nil
            :desc    "html attributes map applied to the underlying `input` div."}]}
  [& args]
  (let [[opts attrs & children] (opts+children args)
        {:keys [label-attrs]}   opts]
    [:label
     (merge-with-style
      (sx
       'kushi-checkbox
       :.transition
       :.fast!
       :.pointer
       :.grid
       :gtc--1em:auto
       :gap--0.4em
       :font-family--system-ui|sans-serif
       :line-height--1.1
       :+.form-control:mbs--1em)
      label-attrs)
     [:input
      (merge-with-style
       (sx
        'kushi-checkbox-input
        :.transition
        :.fast!
        :.pointer
        :-webkit-appearance--none
        :appearance--none
        :m--0
        :font--inherit
        :color--currentColor
        :width--1em
        :height--1em
        :border--0.15em:solid:currentColor
        :border-radius--0em
        :display--inline-grid
        :place-content--center
        :bgc--white
        :checked:bgc--black
        :checked:o--1
        :o--0.6
        {:style {:before:content           "\"\""
                 :before:width             :0.65em
                 :before:height            :0.65em
                 :before:transform         "scale(0) rotate(15deg)"
                 :checked:before:transform "scale(1) rotate(15deg)"
                 :before:transition        :120ms:transform:ease-in-out
                 :before:box-shadow        "inset 1em 1em white"
                 :before:transform-origin  :center:center
                 :before:clip-path         "polygon(14% 44%, 0 65%, 50% 100%, 100% 16%, 80% 0%, 43% 62%)"}
         :type  :checkbox})
       attrs)]
     (into [:span] children)]))
