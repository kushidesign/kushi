(ns kushi.ui.spinner.core
  (:require
   [kushi.core :refer (sx css defcss merge-attrs)]
   [kushi.ui.core :refer (extract)]))

(defcss "@keyframes spin"
  [:0% {:transform "rotate(0deg)"}]
  [:100% {:transform "rotate(360deg)"}])

(defcss "@keyframes pulsing"
  [:0% {:opacity 1}]
  [:50% {:opacity 0}])

(defcss "kushi-spinner-wrapper"
  :position--relative
  :pi--0.3em
  :d--inline-flex
  :flex-direction--row
  :jc--c
  :ta--center
  :ai--c
  :min-width--$loading-spinner-height)

(defn thinking [& args]
  (let [[_ attrs & _]
        (extract args thinking)

        circle
        [:div (sx ".kushi-pulsing-dot"
                  :.pill
                  :w--0.3em
                  :h--0.3em
                  :bgc--currentColor
                  [:animation "var(--spinner-animation-duration) linear infinite pulsing"]
                  ["nth-child(2):animation-delay" "calc(var(--spinner-animation-duration) / 4)"]
                  ["nth-child(3):animation-delay" "calc(var(--spinner-animation-duration) / 2)"])]]

    [:div {:data-kushi-spinner ""
           :class (css ".kushi-thinking-wrapper"
                       :.kushi-spinner-wrapper
                       :.transition)} 
     [:div (merge-attrs
            {:class       (css
                           ".kushi-thinking"
                           :.flex-row-c
                           :gap--0.333em)
             :aria-hidden true}
            attrs)
      circle
      circle
      circle]]))

(defn propeller [& args]
  (let [[_ attrs & _] (extract args propeller)]
    [:div {:data-kushi-spinner ""
           :class (css ".kushi-propeller-wrapper"
                       :.kushi-spinner-wrapper
                       :.transition
                       :pi--0.5em)}
     [:div (merge-attrs
            {:class       (css ".kushi-propeller"
                               [:animation
                                "var(--spinner-animation-duration) linear infinite spin"]
                               [:b
                                "max(0.055em, 1px) solid currentColor"]
                               :h--$loading-spinner-height
                               :w--0px)
             :aria-hidden true}
            attrs)]]))

(defn donut [& args]
  (let [[_ attrs & _] (extract args donut)]
    [:div {:data-kushi-spinner ""
           :class (css ".kushi-donut-wrapper"
                       :.kushi-spinner-wrapper
                       :.transition)}
     [:div (merge-attrs
            {:class       (css
                           ".kushi-donut"
                           :position--relative
                           :.before-absolute-fill
                           :.after-absolute-fill
                           [:animation
                            "var(--spinner-animation-duration) linear infinite spin"]
                           [:before:bw "max(2.5px, 0.125em)"]
                           [:after:bw "max(2.5px, 0.125em)"]
                           :w--$loading-spinner-height
                           :h--$loading-spinner-height
                           :before:border-radius--9999px
                           :before:bs--solid
                           :before:bc--transparent
                           :before:bbsc--currentColor
                           :after:border-radius--9999px
                           :after:o--0.2
                           :after:bs--solid
                           :after:bc--currentColor)
             :aria-hidden true}
            attrs)]]))
