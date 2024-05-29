(ns kushi.ui.callout.core
  (:require [kushi.core :refer (merge-attrs) :refer-macros [sx]]
            [kushi.ui.core :refer (opts+children)]
            [kushi.ui.icon.core]))

(defn callout
  {:desc ["callouts provide contextual feedback information for the user"
          :br
          :br
          "To position the callout at the top of the viewport, use the "
          "`:.fixed-block-start-inside` utility class, or the "
          "`:.fixed-block-end-inside` utility class for positioning "
          "at the bottom of the viewport."]
   :opts '[{:name    icon
            :pred    vector?
            :default nil
            :desc    ["An instance of a `kushi.ui.icon/icon` component"
                      "Places an icon anchored to the inline-start area "
                      "of the callout. Optional."]}
           {:name    close-button
            :pred    vector?
            :default nil
            :desc    ["Hiccup to render a close button."
                      "Optional."]}
           {:name    header-text
            :pred    string
            :default nil
            :desc    ["The header text to render in the callout."
                      "Optional."]}
          ;;  Leave this out for now
          ;;   {:name    duration
          ;;    :pred    pos-int?
          ;;    :default nil
          ;;    :desc    ["When supplied, the callout will dismiss itself after "
          ;;              "the given time (in milliseconds) has passed."]}

           ]}

  [& args]

  (let [[opts attrs & children]    (opts+children args)
        {:keys [icon
                close-button
                header-text
                ;; Leave this out for now
                duration
                ]}opts
        callout-id                   (str (.now js/Date))]

    ;; Leave this out for now
    ;; (when (pos-int? duration)
    ;;   (js/setTimeout #(when-let [el (domo/el-by-id callout-id)]
    ;;                     (.remove el))
    ;;                  duration))

    [:section
     (merge-attrs
      (sx 'kushi-callout
          :ai--c
          :.info
          :w--100%
          {:id callout-id
          ;;  :data-kushi-ui          :callout
          ;;  :data-kushi-ui-callout-id callout-id
           })
      attrs)
     [:div (sx 'kushi-callout-header-wrap
               :.flex-row-sb
               :.relative
               :gap--0.5em
               :p--0.85em:0.75em
               :w--100%)
      [:div (sx 'kushi-callout-header-icon-wrap :min-width--1em) icon]
      header-text
      [:div (sx 'kushi-callout-header-close-button-wrap :min-width--1em) close-button]]
     (when (seq children)
       (into [:div (sx :p--1rem)]
             children))]))
