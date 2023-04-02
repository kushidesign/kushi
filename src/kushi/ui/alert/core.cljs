(ns kushi.ui.alert.core
  (:require [kushi.core :refer (merge-attrs) :refer-macros [sx]]
            [kushi.ui.dom :as dom]
            [kushi.ui.icon.core]
            [kushi.ui.label.core :refer [label]]
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
           {:name    close-icon?
            :pred    boolean?
            :default nil
            :desc    "Places an `✕` (dismiss) icon anchored to the inline-end area of the alert."}
           {:name    close-icon-attrs
            :pred    map?
            :default nil
            :desc    "Attributes map for close-icon. This is where you would put your click-handler for closing the alert."}
          ;; Leave this out for now
          ;;  {:name    duration
          ;;   :pred    pos-int?
          ;;   :default nil
          ;;   :desc    "When supplied, the alert will dismiss itself after the given time (in milliseconds) has passed."}
           ]}

  [& args]

  (let [[opts attrs & children]    (opts+children args)
        {:keys [icon
                close-icon?
                close-icon-attrs
                duration]}         opts
        alert-id     (str (.now js/Date))]

    ;; Leave this out for now
    ;; (when (pos-int? duration)
    ;;   (js/setTimeout #(when-let [el (dom/el-by-id alert-id)]
    ;;                     (.remove el))
    ;;                  duration))

    [:section
     (merge-attrs
      (sx 'kushi-alert
          :.info
          :p--1rem
          :w--100%
          {:id alert-id
          ;;  :data-kushi-ui          :alert
          ;;  :data-kushi-ui-alert-id alert-id
           })
      attrs)
     [:div (sx :.flex-row-fs
               :ai--c
               :.relative
               :w--100%)
      [label (sx (when-not icon :.hidden)) icon]
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
                       :role           :button})
                  close-icon-attrs)
           ;; todo use svg
           [kushi.ui.icon.core/icon :close]]))]]))
