(ns kushi.ui.snippet.core
  (:require
   [kushi.core :refer (sx merge-attrs) :refer-macros (sx)]
   [kushi.ui.tooltip.core :refer (tooltip add-temporary-tooltip!)]
   [kushi.ui.button.core :refer (button)]
   [kushi.ui.core :refer (opts+children)]
   [kushi.ui.dom :refer (copy-to-clipboard)]))


(defn copy-to-clipboard-button
  [opts & children*]
  (let [children (or children*
                     [[button (sx 'kushi-copy-to-clipboard-button :c--red :p--5px :.tertiary {:on-click add-temporary-tooltip!})
                       [:img
                        (sx 'kushi-copy-to-clipboard-button-graphic
                            :h--60%
                            :o--0.7
                            :hover:o--1
                            {:src "graphics/copy.svg"})]
                       [tooltip (sx 'kushi-copy-to-clipboard-tooltip :.xxxsmall :.rounded :ff--Inter {:-display-on-hover? false}) "Copied!"]]] )]
    (into [:div
           (merge-attrs
            (sx 'kushi-copy-to-clipboard-button-wrapper
                :.flex-row-c
                :.pointer
                :.pill
                :ai--center
                :w--22px
                :h--22px
                :ta--center
                :fs--0.5rem
                :fw--600
                :letter-spacing--0.1ex
                ;; :tt--u
                :b--none
                :m--0.4rem
                :hover:bgc--white
                :bgi--none
                :bgc--transparent
                {:type  :text
                 :value "copy"})
            opts)]
          children)))

(defn snippet
  "Desc for"
  [& args]
  (let [[opts attrs & children]                (opts+children args)
        {:keys [text-to-display text-to-copy on-copy-click]} opts]
    (into [:div
           (merge-attrs
            (sx 'kushi-snippet :.relative :.codebox {:data-kushi-ui :snippet})
            attrs)
           [:span text-to-display]
           [copy-to-clipboard-button
            {:on-click (or on-copy-click #(copy-to-clipboard text-to-copy))}]]
          children)))
