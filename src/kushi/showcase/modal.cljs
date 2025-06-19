(ns ^:dev/always kushi.showcase.modal
  (:require
   [clojure.repl]
   [fireworks.core :refer [? !? ?> !?>]]
   [kushi.core :refer [css defcss sx]]
   [kushi.showcase.snippets :refer [component-snippets]]
   [kushi.ui.button :refer [button]]
   [kushi.ui.divisor :refer [divisor]]
   [kushi.ui.icon :refer [icon]]
   [kushi.ui.modal :refer [modal open-kushi-modal modal-close-button]]
   [kushi.css.media]))

(defcss ".kushi-playground-examples-modal"
  :_.kushi-modal-inner:pi--1.25em:0.25em
  :xsm:_.kushi-modal-inner:pi--3em:1.5em
  :_.kushi-modal-inner:pb--1.5rem:2em
  :xsm:_.kushi-modal-inner:pb--3em:3.5em
  [:--modal-min-width :200px]
  :_.kushi-modal-inner:gap--0.75rem
  [:height "min(var(--modal-max-height), calc(100vh - (2 * var(--modal-margin, 1rem))))"]
  :overflow--hidden
  :width--$playground-main-content-max-width)


(defn example-modal-trigger [modal-id]
  [button
   {:colorway :accent
    :contour    :pill
    :surface  :minimal
    :class    
    (css :.kushi-playground-examples-modal-trigger
         :pb--0.4em
         :fw--$wee-bold
         :fs--$xxsmall
         :.accent.minimal:hover:background-color--$accent-50
         :dark:.accent.minimal:hover:background-color--$accent-800

         ;; Next 3 styles will give it a link-button style
         #_:p--0
         #_:hover&.accent.minimal:bgc--transparent
         #_[:hover:after {:content  "\"\""
                          :position :absolute
                          :w        :100%
                          :h        :1px
                          :o        0.5
                          :bgc      :$accent-foreground
                          :top      "calc(100% + 2px)"}])
    :on-click
    (fn* [] (open-kushi-modal modal-id))}
   [icon (sx :.kushi-playground-examples-modal-trigger-icon
             :.small
             :.extra-bold)
    :code]
   "Code"])

(defn- example-modal-inner
  [{:keys [modal-id label modal-label component-label]
    :as   m}]
  [:<> 
   [modal-close-button {:modal-id modal-id}]
   [:div (sx :.kushi-playground-examples-modal-wrapper
             :.flex-row-space-between
             :ai--fs
             :gap--1.5em)
    [:div (sx :.kushi-playground-examples-modal-wrapper-inner
              :.flex-col-start
              :ai--b
              :gap--1rem)
     [:h1 (sx :.kushi-playground-examples-modal-wrapper-inner-label
              :.component-section-header-label) 
      component-label]
     [:span.foreground-color-secondary (or label modal-label)]]]
   [divisor]
   [component-snippets m]])

(defn example-modal
  [{:keys [modal-id wrapper-tag]
    :as   m}]
  (!? m)
  ;; pass wrapper tag of :div to just render element
  ;; good for dev 
  [#_(or wrapper-tag modal) 
   modal
   {:class (css :.kushi-playground-examples-modal)
    :id    modal-id}
   [example-modal-inner m]])

