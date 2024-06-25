(ns kushi.ui.toast.demo
  (:require
   [goog.string]
   [kushi.core :refer (sx)]
   [kushi.ui.button.core :refer [button]]
   [kushi.playground.util :refer-macros [sx-call]]
   [kushi.ui.toast.core :refer [toast-attrs dismiss-toast!]]
   [reagent.dom :as rdom]))

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
            :.neutral-secondary-foreground)
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
        {:on-click dismiss-toast!})
    "Undo Save" ]])



(def examples
  [#_{:desc      "Basic, auto-dismissing"
    :component button
    :reqs      '[[kushi.ui.button.core :refer [button]]]
    :examples  [{:label    "Basic"
                 :args     ["Pop toast"]
                 :sx-attrs (sx-call (toast-attrs
                                     {:-f (fn [popover-el]
                                            (rdom/render (fn []
                                                           [:div
                                                            (sx :.xxxlarge
                                                                :.flex-row-c
                                                                :padding--0.25em)
                                                            "🍞"])
                                                         popover-el))}))}]}

   (let [code (sx-call (toast-attrs
                        {:-auto-dismiss? false
                         :-f             (fn [toast-el]
                                           (rdom/render toast-content
                                                        toast-el))}))]
     {:desc      "With notification with manual dismiss cta"
      :component button
      :reqs      '[[kushi.ui.button.core :refer [button]]]
      :row-attrs (sx :&_.kushi-button:fs--$small)
      :snippets  [['button (:quoted code) "Save for later"]]
      :examples  [{:label    "Basic"
                   :args     ["Save for later"]
                   :sx-attrs code}]})
   
   ])
