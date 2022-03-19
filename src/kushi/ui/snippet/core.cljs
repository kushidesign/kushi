(ns kushi.ui.snippet.core
  (:require-macros [kushi.utils :refer (keyed)])
  (:require
   [kushi.core :refer (sx defclass merge-with-style) :refer-macros (sx)]
   [clojure.string :as string]
   [kushi.gui :refer (gui defcom)]
   ))

(defn copy-to-clipboard [val]
  (let [el (js/document.createElement "textarea")]
    (set! (.-value el) val)
    (.appendChild js/document.body el)
    (.select el)
    (js/document.execCommand "copy")
    (.removeChild js/document.body el)))

(defn copy-to-clipboard-button
  [opts & children*]
  (let [children (or children*
                     [:img
                      (sx :h--60%
                          :o--0.7
                          :hover:o--1
                          {:src "graphics/copy.svg"})])]
    [:button
     (merge-with-style
      (sx :.absolute
          :.flex-row-c
          :.pointer
          :.pill
          :ai--center
          :w--22px
          :h--22px
          :top--0
          :right--0
          :ta--center
          :fs--0.5rem
          :fw--600
          :letter-spacing--0.2ex
          :tt--u
          :b--none
          :m--0.4rem
          :hover:bgc--white
          :bgi--none
          :bgc--transparent
          {:type :text
           :value "copy"})
      opts)
     children]))

(defn snippet [{:keys [text-to-display text-to-copy]}]
  [:div
   (sx :.relative :.codebox)
   text-to-display
   [copy-to-clipboard-button
    {:on-click #(copy-to-clipboard text-to-copy)}]])
