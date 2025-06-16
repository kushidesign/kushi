(ns kushi.ui.snippet.core
  (:require
   [kushi.core :refer (sx css merge-attrs)]
   [kushi.ui.tooltip.core :refer (tooltip-attrs)]
   [kushi.ui.button.core :refer (button)]
   [kushi.ui.icon.core :refer (icon)]
   [kushi.ui.icon.mui.svg :as mui.svg]
   [kushi.ui.core :refer (extract)]
   [domo.core :refer (copy-to-clipboard!)]))

(def copy-content-svg
  "data:image/svg+xml,<svg xmlns='http://www.w3.org/2000/svg' aria-hidden='true' height='16' viewBox='0 0 16 16' version='1.1' width='16' data-view-component='true'>
      <path fill-rule='evenodd' d='M0 6.75C0 5.784.784 5 1.75 5h1.5a.75.75 0 010 1.5h-1.5a.25.25 0 00-.25.25v7.5c0 .138.112.25.25.25h7.5a.25.25 0 00.25-.25v-1.5a.75.75 0 011.5 0v1.5A1.75 1.75 0 019.25 16h-7.5A1.75 1.75 0 010 14.25v-7.5z'></path><path fill-rule='evenodd' d='M5 1.75C5 .784 5.784 0 6.75 0h7.5C15.216 0 16 .784 16 1.75v7.5A1.75 1.75 0 0114.25 11h-7.5A1.75 1.75 0 015 9.25v-7.5zm1.75-.25a.25.25 0 00-.25.25v7.5c0 .138.112.25.25.25h7.5a.25.25 0 00.25-.25v-7.5a.25.25 0 00-.25-.25h-7.5z'></path>
  </svg>")

(defn copy-to-clipboard-button
  [opts & children*]
  (let [children (or children*
                     [[button
                       (merge-attrs
                        (sx ".kushi-copy-to-clipboard-button"
                            :.minimal
                            :.accent
                            :p--0px
                            :fs--$small)
                        (tooltip-attrs
                         {:text                        "Click to copy"
                          :text-on-click               "Copied!"
                          :text-on-click-tooltip-class (css [:--tooltip-background-color :$accent-filled-background-color])
                          :placement                   :r}))
                       [icon mui.svg/content-copy]]])]
    (into [:div
           (merge-attrs
            (sx ".kushi-copy-to-clipboard-button-wrapper"
                :.flex-row-c
                :.pill
                :cursor--pointer
                :ai--center
                :w--22px
                :h--22px
                :ta--center
                :fs--0.5rem
                :fw--600
                :letter-spacing--0.1ex
                :b--none
                :m--0.4rem
                :hover:bgc--white
                :bgi--none
                :bgc--transparent)
            {:type  :text
             :value "copy"}
            (dissoc opts :placement))]
          children)))

(defn snippet
  "Desc for"
  [& args]
  (let [{:keys [opts attrs children]}                
        (extract args [:text-to-display :text-to-copy :on-copy-click])
        {:keys [text-to-display text-to-copy on-copy-click]}
        opts]
    (into [:div
           (merge-attrs
            (sx ".kushi-snippet"
                :.codebox
                :position--relative)
            {:data-ks-ui :snippet}
            attrs)
           [:span text-to-display]
           [copy-to-clipboard-button
            {:on-click (or on-copy-click
                           #(copy-to-clipboard! text-to-copy))}]]
          children)))
