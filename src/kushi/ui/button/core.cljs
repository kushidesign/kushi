(ns kushi.ui.button.core
  (:require-macros
   [kushi.core :refer (sx)])
  (:require
   [kushi.core :refer (merge-attrs)]
   [kushi.ui.core :refer (opts+children)]
   [kushi.ui.icon.helper :refer (icon-component)]
   [kushi.ui.label.core :refer (label)]
   [kushi.ui.tooltip.events :refer (tooltip-mouse-leave tooltip-mouse-enter)]
   [clojure.string :as string]))

(defn resolve-inline-offset
  [{:keys [offset?]}]
  (if offset?
    "var(--button-with-icon-padding-inline-offset)"
    "var(--button-padding-inline-ems)"))

(defn button
  {:desc ["Buttons provide cues for actions and events."
          "These fundamental components allow users to process actions or navigate an experience."]
   :opts '[{:name    mui-icon
            :type    :string
            :default nil
            :desc    "Must be a string corresponding to a [mui-icon](https://fonts.google.com/icons?icon.set=Material+Icons)."}
           {:name    mui-icon-style
            :type    #{:filled :outlined :rounded :sharp :two-tone}
            :default :filled
            :desc    "Controls the style of the [mui-icon](https://fonts.google.com/icons?icon.set=Material+Icons)."}
           {:name    icon-position
            :type    #{:inline-start :inline-end :block-start :block-end}
            :default nil
            :desc    "Setting to one of the accepted values will place the icon, relative to any text labels."}]}
  [& args]
  (let [[opts attrs & children] (opts+children args)
        {:keys [icon-position mui-icon-style background]
         mi    :mui-icon
         :or   {mi             nil}} opts

        icon-position  (or icon-position :inline-start)
        mui-icon-style (or mui-icon-style :filled)
        text-node-label? (and (seq children)
                              (some string? children))
        legit-icon? (and mi (string? mi) (not (string/blank? mi)) )
        only-icons? (and legit-icon? (not text-node-label?))
        icon-component (icon-component {:mi             mi
                                        :icon-position  icon-position
                                        :mui-icon-style mui-icon-style
                                        :no-margins?    only-icons?})
        icon-class (when icon-component (str "kushi-button-with-icon-" (name icon-position)))
        pis (if only-icons?
              :0.8em
              (resolve-inline-offset {:offset? (and mi (= icon-position :inline-start))}))
        pie (if only-icons?
              :0.8em
              (resolve-inline-offset {:offset? (and mi (= icon-position :inline-end))}))]
    [:button
     (merge-attrs
      (sx 'kushi-button
          :.flex-row-c
          :.transition
          :.pointer
          :.relative
          :.neutral
          [:pis pis]
          [:pie pie]
          :pb--0.8em
          {:data-kushi-ui      :button
           :data-kushi-tooltip true
           :aria-expanded      "false"
           :on-mouse-enter     tooltip-mouse-enter
           :on-mouse-leave     tooltip-mouse-leave})
      attrs
      {:class [icon-class]})
     (if icon-component
       (case icon-position
         :block-start
         (into [:span (sx :.flex-col-c :ai--c) icon-component] children)
         :block-end
         (into [:span (sx :.flex-col-c
                          :ai--c
                          :flex-direction--column-reverse)
                icon-component] children)
         :inline-start
         (into [label icon-component] children)
         (into [label] (concat children [icon-component])))
       (into [label] children))]))
