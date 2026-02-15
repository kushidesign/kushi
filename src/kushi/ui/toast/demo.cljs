(ns ^{:kushi/layer "user-styles"} kushi.ui.toast.demo
  (:require
   [goog.string]
   [kushi.core :refer (sx css merge-attrs)]
   [kushi.ui.button :refer [button]]
   [kushi.ui.card :refer [card]]
   [kushi.playground.util :refer-macros [sx-call]]
   [kushi.ui.toast :refer [toast-attrs dismiss-toast!]]
   [kushi.showcase.core :as showcase :refer [samples]]
   [reagent.dom :as rdom]))

(defn toast-content []
  [:div
   (merge-attrs (sx :.my-toast-content
                    :.text-base
                    :position--relative
                    :ai--c
                    :gap--1.25em
                    :xsm:gap--1.5em
                    :pi--1.25em
                    :xsm:pi--1.5em
                    :pb--1em
                    :xsm:pb--1.25em)
                {:data-ks-display :flex-row})
   [:div
    (merge-attrs (sx :.my-toast-content-wrapper
                     :ai--fs
                     :gap--0.5em
                     :_.kushi-text-input-label:min-width--7em
                     :_.kushi-input-inline:gtc--36%:64%)
                 {:data-ks-display :flex-col-center})
    [:h3 (sx :.weight-bold :m--0) "Saved for later"]
    [:p (sx :.my-toast-text
            :fs--$text-sm
            :.foreground-color-secondary!)
     (.format (new js/Intl.DateTimeFormat
                   "en-US"
                   #js{:dateStyle "full" :timeStyle "short"})
              (new js/Date))]]
   [button
    (merge-attrs (sx :.kushi-toast-close-button
                     :flex-shrink--0
                     :fw--$semi-bold
                     :fs--$text-2xs
                     :letter-spacing--$loose
                     :zi--1
                     [:opacity  :$popover-close-button-opacity])
                 {:on-click      dismiss-toast!
                  :data-ks-shape :rounded})
    "Undo Save"]])


(def examples
  [(let [code (sx-call (toast-attrs
                        {:auto-dismiss? false
                         :f             (fn [toast-el]
                                           (rdom/render toast-content
                                                        toast-el))}))]
     {:desc      "With notification, and manual dismiss cta"
      :component button
      :reqs      '[[kushi.ui.button :refer [button]]]
      :row-attrs (sx :_.kushi-button:fs--$text-sm)
      :snippets  [['button (:quoted code) "Save for later"]]
      :examples  [{:label    "Basic"
                   :args     ["Save for later"]
                   :sx-attrs code}]})])

(def demos 
  [{:label   "With notification and manual dismiss CTA"
    :require '[[kushi.ui.button :refer [button]]
               [kushi.ui.card :refer [card]]]
    :samples (samples 
              [[button
                (toast-attrs
                 {:auto-dismiss? false
                  :f             (fn [toast-el]
                                   (rdom/render 
                                    [:div
                                     {
                                      ;; :shadow          :base
                                      ;; :shadow-opacity :15%
                                      ;; :stroke          :xsoft
                                      :class           (css :.my-toast-content
                                                            :.text-base
                                                            :position--relative
                                                            :ai--c
                                                            :gap--1.25em
                                                            :xsm:gap--1.5em
                                                            :pi--1.25em
                                                            :xsm:pi--1.5em
                                                            :pb--1em
                                                            :xsm:pb--1.25em)
                                      :data-ks-display      :flex-row}
                                     [:div
                                      (merge-attrs (sx :.my-toast-content-wrapper
                                                       :ai--fs
                                                       :gap--0.5em
                                                       :_.kushi-text-input-label:min-width--7em
                                                       :_.kushi-input-inline:gtc--36%:64%)
                                                   {:data-ks-display :flex-col-center})
                                      [:h3 (sx :.weight-bold :m--0) "Saved for later"]
                                      [:p (sx :.my-toast-text
                                              :fs--$text-sm
                                              :.foreground-color-secondary!)
                                       (.format (new js/Intl.DateTimeFormat
                                                     "en-US"
                                                     #js{:dateStyle "full"
                                                         :timeStyle "short"})
                                                (new js/Date))]]
                                     [button
                                      (merge-attrs (sx :.kushi-toast-close-button
                                                       :flex-shrink--0
                                                       :fw--$semi-bold
                                                       :fs--$text-2xs
                                                       :letter-spacing--$loose
                                                       :zi--1
                                                       [:opacity :$popover-close-button-opacity])
                                                   {:on-click      dismiss-toast!
                                                    :data-ks-shape :rounded})
                                      "Undo Save"]]
                                    toast-el))})
                "Save for Later"]])}])
