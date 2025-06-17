(ns kushi.ui.checkbox
  (:require
   [kushi.core :refer (css sx merge-attrs)]
   [kushi.ui.core :refer (extract)]))

;; TODO outlines for ally
(defn checkbox
  {:summary "Checkboxes are used to provide multiple options for selection."
   :opts    '[{:name    label-attrs
               :schema    map?
               :default nil
               :desc    "HTML attributes map applied to the wrapping `label` div."}]}
  [& args]
  (let [{:keys [opts attrs children]} (extract args)
        {:keys [label-attrs]}   opts]
    [:label
     (merge-attrs
      {:class (css
               ".kushi-checkbox"
               :.transition
               :transition-duration--$xxfast
               :cursor--pointer
               :d--grid
               :gtc--1em:auto
               :gap--0.4em
               :line-height--1.1
               :+.form-control:mbs--1em)}
      label-attrs)
     [:input
      (merge-attrs
       {:class (css
                ".kushi-checkbox-input"
                :.transition
                :transition-duration--$xxfast
                :cursor--pointer
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
                :bgc--transparent
                :checked:bgc--currentColor
                :checked:o--1
                :o--0.6
                {:before:box-shadow        "inset 1em 1em white"
                 :before:clip-path         "polygon(14% 44%, 0 65%, 50% 100%, 100% 16%, 80% 0%, 43% 62%)"
                 :before:content           "\"\""
                 :before:height            :0.65em
                 :before:transform         "scale(0) rotate(15deg)"
                 :before:transform-origin  :center:center
                 :before:transition        :120ms:transform:ease-in-out
                 :before:width             :0.65em
                 :checked:before:transform "scale(1) rotate(15deg)"})
        :type  :checkbox}
       attrs)]
     (into [:span] children)]))
