(ns kushi.ui.progress.core
  (:require-macros
   [kushi.core :refer (sx defkeyframes)])
  (:require
   [kushi.core :refer (merge-attrs)]
   [kushi.ui.core :refer (opts+children)]))

(defkeyframes spin
  [:0% {:transform "rotate(0deg)"}]
  [:100% {:transform "rotate(360deg)"}])

(defkeyframes pulsing
  [:0% {:opacity 1}]
  [:50% {:opacity 0}])

(defn thinking [& args]
  (let [[_ attrs & _] (opts+children args)
        circle [:div (sx 'kushi-pulsing-dot
                         :.pill
                         :w--0.29em
                         :h--0.29em
                         [:animation [["var(--progress-animation-duration)" :linear :infinite :pulsing]]]
                         ["nth-child(2):animation-delay" "calc(var(--progress-animation-duration) / 4)"]
                         ["nth-child(3):animation-delay" "calc(var(--progress-animation-duration) / 2)"]
                         :bgc--currentColor)]]
    [:div (merge-attrs
           (sx 'kushi-progress-thinking
               :.flex-row-c
               :gap--0.333em
               {:aria-hidden true})
           attrs)
     circle
     circle
     circle]))

(defn propeller [& args]
  (let [[_ attrs & _] (opts+children args)]
    [:div (merge-attrs
           (sx 'kushi-progress-propeller
               [:animation [["var(--progress-animation-duration)" :linear :infinite :spin]]]
               :h--$loading-spinner-height
               :w--0px
               [:b "max(0.055em, 1px) solid currentColor"]
               {:aria-hidden true})
           attrs)]))

(defn donut [& args]
  (let [[_ attrs & _] (opts+children args)]
    [:div (merge-attrs
           (sx 'kushi-progress-donut
               :.relative
               [:animation [["var(--progress-animation-duration)" :linear :infinite :spin]]]
               :w--$loading-spinner-height
               :h--$loading-spinner-height

               :.before-absolute-fill
               :before:border-radius--9999px
               [:before:bw "max(2.5px, 0.125em)"]
               :before:bs--solid
               :before:bc--transparent
               :before:bbsc--currentColor

               :.after-absolute-fill
               :after:border-radius--9999px
               :after:o--0.2
               [:after:bw "max(2.5px, 0.125em)"]
               :after:bs--solid
               :after:bc--currentColor
               {:aria-hidden true})
           attrs)]))

(defn progress
  [& args]
  (let [[_ attrs & children] (opts+children args)
        [content component] children]
    [:span
     (merge-attrs
      (sx 'kushi-progress-wrapper :.relative)
      attrs)
     [:span
      (sx :.kushi-progress-content
          ["has-ancestor([data-kushi-ui-progress='true']):visibility" :hidden])
      content]
     [:div
      (sx 'kushi-progress
          :.absolute-centered
          :d--none
          ["has-ancestor([data-kushi-ui-progress='true']):d" :block])
      (cond
        (fn? component)
        [component]
        (coll? component)
        component)]]))
