(ns kushi.ui.label.core
  (:require-macros
   [kushi.core :refer (sx)]
   [kushi.ui.core :refer (defcom2)])
  (:require
   [kushi.ui.icon.helper :refer (icon-component)]
   [kushi.ui.core :refer (opts+children)]
   [kushi.core :refer (merge-attrs)]))

(defcom2 label

  {:desc ["A label is typically used for providing titles to sections of content."]
   :opts '[{:name    mui-icon
            :type    :string
            :default nil
            :desc    "Must be a string corresponding to a [mui-icon](https://fonts.google.com/icons?icon.set=Material+Icons)."}
           {:name    icon-style
            :type    #{:filled :outlined :rounded :sharp :two-tone}
            :default :filled
            :desc    "Controls the style of the [mui-icon](https://fonts.google.com/icons?icon.set=Material+Icons)."}
           {:name    icon-position
            :type    #{:inline-start :inline-end :block-start :block-end}
            :default nil
            :desc    "Setting to one of the accepted values will place the icon, relative to any text labels."}]}

  (let [{:keys [icon-position icon-style]
         mi    :mui-icon
         :or   {mi            nil
                icon-style    :filled
                icon-position :inline-start}} &opts
        icon-component (icon-component {:mi mi :icon-position icon-position :icon-style icon-style})
        icon-class (when mi (str "kushi-label-with-icon-" (name icon-position))) ]

    [:span
     (merge-attrs
      (sx 'kushi-label
          :.flex-row-c
          :.transition
          :ai--c
          :d--inline-flex
          :w--fit-content
          {:data-kushi-ui :label})
      &attrs
      {:class icon-class})
     (cond
       (and mi (= icon-position :inline-end))
       (do
         #_(js/console.log
          {:mi mi :ip icon-position :ic icon-component :children &children :result (conj &children icon-component)})
         (conj &children icon-component))
       (and mi (= icon-position :inline-start))
       (into [:<> icon-component] (rest &children))
       :else &children)])
  #(if (string? %) [:span.kushi-label-text %] %))
