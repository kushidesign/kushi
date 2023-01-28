(ns kushi.ui.alert.core
  (:require [kushi.core :refer (merge-attrs) :refer-macros [sx]]
            [kushi.ui.label.core :refer [label]]
            [kushi.ui.core :refer (opts+children)]))

(defn alert
  {:desc ["Alerts provide contextual feedback information for the user"]
   :opts '[{:name    mui-icon
            :type    :string
            :default nil
            :desc    ["Places an icon anchored to the inline-start area of the alert."
                      "Must be a string corresponding to a [mui-icon](https://fonts.google.com/icons?icon.set=Material+Icons)."]}
           {:name    mui-icon-style
            :type    #{:filled :outlined :rounded :sharp}
            :default :filled
            :desc    "Controls the style of the [mui-icon](https://fonts.google.com/icons?icon.set=Material+Icons)."}
           {:name    icon-svg
            :type    :boolean
            :default false
            :desc    ["Pass a `mui-icon` in `svg` (hiccup) to use in place of the Google Fonts Material Icons font."
                      "Must use `:viewBox` attribute with values such as `\"0 0 24 24\"`."
                      "The `:width` and `:height` attributes of the `svg` do not need to be set."]}
           {:name    close-icon?
            :type    :boolean
            :default nil
            :desc    "Places a close \"×\" icon anchored to the inline-end area of the alert."}
           {:name    close-icon-attrs
            :type    :map
            :default nil
            :desc    "Attributes map for close-icon. This is where you would put your click-handler for closing the alert."}]}
  [& args]
  (let [[opts attrs & children]   (opts+children args)
        {:keys [mui-icon-style
                close-icon?
                close-icon-attrs]
         mi    :mui-icon} opts]
    [:section
     (merge-attrs
      (sx 'kushi-alert
          :.info
          :p--1rem
          :w--100%
          {:data-kushi-ui :alert})
      attrs)
     [:div (sx :.flex-row-fs
               :ai--c
               :.relative
               :w--100%)
      [label (sx (when-not mi :.hidden)
                 {:-mui-icon       (or mi :info)
                  :-mui-icon-style mui-icon-style})]
      (if (every? string? children)
        [label (sx :flex-grow--1 :ta--c) children]
        (into [:span (sx :flex-grow--1 :ta--c)] children))
      (when close-icon?
        (let [hover-bgc "rgba 255 255 255 0.3"]
          [label (merge-attrs
                  (sx :.pointer
                      :.pill
                      :outline-width--3px
                      :outline-style--style
                      :outline-color--transparent
                      [:outline-color hover-bgc]
                      [:hover:bgc hover-bgc]
                      {:tab-index      0
                       :role           :button
                       :-mui-icon      :close
                       :-icon-position :inline-end})
                  close-icon-attrs)]))]]))
