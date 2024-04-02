(ns kushi.ui.toast.demo
  (:require
   [goog.string]
   [kushi.core :refer (sx)]
   [kushi.ui.button.core :refer [button]]
   [kushi.ui.toast.core :refer [close-toast!]]))

(defn toast-content []
  [:div
   (sx 'my-toast-content
       :.relative
       :.flex-row-fs
       :.medium
       :ai--c
       :gap--1.5em
       :pi--1.5em
       :pb--1.25em)
   [:div
    (sx 'my-toast-content-wrapper
        :.flex-col-c
        :ai--fs
        :gap--0.5em
        :&_.kushi-text-input-label:min-width--7em
        :&_.kushi-input-inline:gtc--36%:64%)
    [:h3 (sx :.bold :m--0) "Saved for later"]
    [:p (sx 'my-toast-text
            :.small
            :.neutral-secondary-fg)
     (.format (new js/Intl.DateTimeFormat
                   "en-US"
                   #js{:dateStyle "full" :timeStyle "short"})
              (new js/Date))]]
   [button
    (sx 'kushi-toast-close-button
        :.semi-bold
        :.rounded
        :.xxsmall
        :.loose
        :zi--1
        [:opacity  :$popover-close-button-opacity]
        {:on-click close-toast!})
    "Undo Save" ]])
