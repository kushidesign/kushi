(ns kushi.ui.button.core
  (:require-macros
   [kushi.core :refer (sx)]
   [kushi.ui.core :refer (defcom+)])
  (:require
   [kushi.core :refer (merge-with-style)]
   [kushi.ui.core :refer (opts+children)]
   [kushi.ui.label.core :refer (label)]
   [kushi.ui.tooltip.events :refer (tooltip-mouse-leave tooltip-mouse-enter)]))

(defn button
  [& args]
  (let [[_ attrs & children] (opts+children args)]
    [:button
     (merge-with-style
      (sx 'kushi-button
          :.transition
          :.pointer
          :>span:p--0.8em:1.2em
          {:data-kushi-ui :button})
      attrs)
     [apply
      label
      {:data-kushi-tooltip true
       :aria-expanded      "false"
       :on-mouse-enter     tooltip-mouse-enter
       :on-mouse-leave     tooltip-mouse-leave}
      children]]))

#_(defcom+ button
  [:button
   (merge-with-style
    ;; label-base-sx
    (sx
     'kushi-button
     :.transition
     {:style {:>span:padding [[:0.8em :1.2em]]
              :cursor        :pointer}
      :id    {:data-kushi-ui    :button}})
    &attrs)
   [apply label &children]
   #_[:span &children]]
  ;; fn for processing children
  #(if (string? %) [:span.kushi-label-text %] %))
