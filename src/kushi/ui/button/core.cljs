(ns kushi.ui.button.core
  (:require-macros
   [kushi.core :refer (sx)])
  (:require
   [kushi.core :refer (merge-attrs)]
   [kushi.ui.core :refer (opts+children)]
   [kushi.ui.icon.mui.core :refer (mui-icon)]
   [kushi.ui.icon.mui.outlined :refer (mui-icon-outlined)]
   [kushi.ui.label.core :refer (label)]
   [kushi.ui.tooltip.events :refer (tooltip-mouse-leave tooltip-mouse-enter)]))

(defn button
  {:desc ["Buttons provide cues for actions and events."
          "These fundamental components allow users to process actions or navigate an experience."]
   :opts '[{:name    icon
            :type    :string
            :default nil
            :desc    "Can be either an emoji character or a string corresponding to a [mui-icon](https://fonts.google.com/icons?icon.set=Material+Icons)."}
           {:name    icon-style
            :type    #{:filled :outlined :rounded :sharp :two-tone}
            :default :filled
            :desc    "Controls the style of the [mui-icon](https://fonts.google.com/icons?icon.set=Material+Icons)."}
           {:name    icon-position
            :type    #{:inline-start :inline-end :block-start :block-end}
            :default nil
            :desc    "Setting to one of the accepted values will place the icon, relative to any text labels."}]}
  [& args]
  (let [[opts attrs & children] (opts+children args)
        {:keys [icon-position icon-style]
         mi :mui-icon
         :or {mi nil icon-style :filled icon-position :inline-start}} opts
        icon-component (when mi (if (= :outlined icon-style) [mui-icon-outlined mi] [mui-icon mi]))
        children (if mi
                   (case icon-position
                     :inline-end
                     (into (apply vector children) [icon-component])
                     :inline-start
                     (into [icon-component] children)
                     children)
                   children)
        icon-class (when mi (str "kushi-button-with-icon-" (name icon-position)))
        inline-icon? (contains? #{:inline-start :inline-end} icon-position)]
    [:button
     (merge-attrs
      (sx 'kushi-button
          :.transition
          :.pointer
          :.relative
          :>span:pi--1.2em
          :>span:pb--0.8em
          :&.kushi-button-with-icon-inline-start:>span:pi--:--icon-label-padding-inline-ems
          :&.kushi-button-with-icon-inline-end:>span:pi--:--icon-label-padding-inline-ems
          :&.kushi-button-with-icon-inline-start:&_.kushi-label-text:mi--:--icon-label-margin-inline-start-ems
          :&.kushi-button-with-icon-inline-end:&_.kushi-label-text:mi--:--icon-label-margin-inline-end-ems
          {:data-kushi-ui      :button
           :data-kushi-tooltip true
           :aria-expanded      "false"
           :on-mouse-enter     tooltip-mouse-enter
           :on-mouse-leave     tooltip-mouse-leave})
      attrs
      {:class [icon-class]})
     (case icon-position
       :block-start
       (into [:span (sx :.flex-col-c :ai--c) icon-component] children)
       :block-end
       (into [:span (sx :.flex-col-c :ai--c :flex-direction--column-reverse) icon-component] children)
       [apply
        label
        children])]))
