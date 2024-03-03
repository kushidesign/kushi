(ns kushi.ui.alert.core
  (:require [kushi.core :refer (merge-attrs) :refer-macros [sx]]
            [kushi.ui.icon.core]
            [kushi.ui.core :refer (opts+children)]))

(defn alert
  {:desc ["Alerts provide contextual feedback information for the user"
          :br
          :br
          "To position the alert at the top of the viewport, use the `:.fixed-block-start` utility class, or the `:.fixed-block-end` utility class for positioning at the bottom of the viewport."]
   :opts '[{:name    icon
            :pred    vector?
            :default nil
            :desc    ["An instance of a `kushi.ui.icon/icon` component"
                      "Places an icon anchored to the inline-start area of the alert."
                      "Optional."]}
           {:name    close-button
            :pred    vector?
            :default nil
            :desc    ["Hiccup to render a close button."
                      "Optional."]}

           ;; Leave this out for now
           ;;  {:name    duration
           ;;   :pred    pos-int?
           ;;   :default nil
           ;;   :desc    "When supplied, the alert will dismiss itself after the given time (in milliseconds) has passed."}

           ]}

  [& args]

  (let [[opts attrs & children]    (opts+children args)
        {:keys [icon
                close-button
                header-text
                ;; Leave this out for now
                duration
                ]}opts
        alert-id                   (str (.now js/Date))]

    ;; Leave this out for now
    ;; (when (pos-int? duration)
    ;;   (js/setTimeout #(when-let [el (dom/el-by-id alert-id)]
    ;;                     (.remove el))
    ;;                  duration))

    [:section
     (merge-attrs
      (sx 'kushi-alert
          :ai--c
          :.info
          :w--100%
          {:id alert-id
          ;;  :data-kushi-ui          :alert
          ;;  :data-kushi-ui-alert-id alert-id
           })
      attrs)
     [:div (sx 'kushi-alert-header-wrap
               :.flex-row-sb
               :.relative
               :p--0.75rem:1rem
               :w--100%)
      [:div (sx 'kushi-alert-header-icon-wrap :min-width--1em) icon]
      header-text
      [:div (sx 'kushi-alert-header-close-button-wrap :min-width--1em) close-button]]
     (when (seq children)
       (into [:div (sx :p--1rem)]
             children))]))
