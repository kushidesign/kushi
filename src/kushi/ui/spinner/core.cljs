(ns kushi.ui.spinner.core
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
                         [:animation [["var(--spinner-animation-duration)" :linear :infinite :pulsing]]]
                         ["nth-child(2):animation-delay" "calc(var(--spinner-animation-duration) / 4)"]
                         ["nth-child(3):animation-delay" "calc(var(--spinner-animation-duration) / 2)"]
                         :bgc--currentColor)]]
    [:div (merge-attrs
           (sx 'kushi-spinner-thinking
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
           (sx 'kushi-spinner-propeller
               [:animation [["var(--spinner-animation-duration)" :linear :infinite :spin]]]
               :h--$loading-spinner-height
               :w--0px
               [:b "max(0.055em, 1px) solid currentColor"]
               {:aria-hidden true})
           attrs)]))

(defn donut [& args]
  (let [[_ attrs & _] (opts+children args)]
    [:div (merge-attrs
           (sx 'kushi-spinner-donut
               :.relative
               [:animation [["var(--spinner-animation-duration)" :linear :infinite :spin]]]
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

(defn spinner
  {:summary ["Spinners indicate the in-progress status of an operation such as loading or processing."]
   :desc ["The spinner is component is meant to be used in conjuction with a"
          " component such as `spinner.core/donut`,  `spinner.core/propeller`,"
          " or `spinner.core/propeller`. See the \"Usage with a button\" in the Spinner > Examples section"]}
  [& args]
  (let [[_ attrs & children] (opts+children args)
        [content component] children]
    [:span
     (merge-attrs
      (sx 'kushi-spinner-wrapper :.relative)
      attrs)
     [:span
      (sx :.kushi-spinner-content
          ["has-ancestor([data-kushi-ui-spinner='true']):visibility" :hidden])
      content]
     [:div
      (sx 'kushi-spinner
          :.absolute-centered
          :d--none
          ["has-ancestor([data-kushi-ui-spinner='true']):d" :block])
      (cond
        (fn? component)
        [component]
        (coll? component)
        component)]]))
