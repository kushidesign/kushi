(ns ^:dev/always kushi.playground.showcase.modal
  (:require
   [clojure.repl]
   [fireworks.core :refer [!? ?]]
   [kushi.core :refer [css defcss ?defcss merge-attrs sx]]
   [kushi.playground.showcase.snippets :refer [reqs-by-refers component-snippets reqs-coll]]
   [kushi.ui.tooltip.core :refer [tooltip-attrs]]
   [kushi.ui.icon.mui.svg :as mui.svg]
   [kushi.ui.button.core :refer [button icon-button]]
   [kushi.ui.divisor.core :refer [divisor]]
   [kushi.ui.icon.core :refer [icon]]
   [kushi.ui.modal.core :refer [modal open-kushi-modal modal-close-button]]
   [kushi.css.media]
   ))

(defcss ".kushi-playground-examples-modal"
  :_.kushi-modal-inner:pi--1.25em
  :xsm:_.kushi-modal-inner:pi--3em
  :_.kushi-modal-inner:pb--1.5rem:2em
  :xsm:_.kushi-modal-inner:pb--3em:3.5em
  [:--modal-min-width :200px]
  :_.kushi-modal-inner:gap--0.75rem
  [:height "min(var(--modal-max-height), calc(100vh - (2 * var(--modal-margin, 1rem))))"]
  :overflow--hidden
  :width--$playground-main-content-max-width)


(defn example-modal-trigger [modal-id]
  [button
   {:-colorway :accent
    :-shape    :pill
    :-surface  :minimal
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
    :on-click (fn* [] (open-kushi-modal modal-id))}
   [icon (sx :.kushi-playground-examples-modal-trigger-icon :.small :.extra-bold) :code]
   "Code"])


(defn- example-modal-inner
  [{:keys [modal-id label component-label]
    :as   m}]
  [:<> 
   [modal-close-button {:-modal-id modal-id}]
   [:div (sx :.kushi-playground-examples-modal-wrapper
             :.flex-row-sb :ai--fs :gap--1.5em)
    [:div
     (sx  :.kushi-playground-examples-modal-wrapper-inner
          :.flex-col-fs :ai--b :gap--1rem )
     [:h1 (sx :.kushi-playground-examples-modal-wrapper-inner-label
              :.component-section-header-label) component-label]
     label]]
   [divisor]
   [component-snippets m]])


(defn example-modal
  [{:keys [modal-id wrapper-tag]
    :as   m}]
  (!? :pp m)
    ;; pass wrapper tag of :div to just render element
    ;; good for dev 
  [(or wrapper-tag modal) 
   {:class "kushi-playground-examples-modal"
    :id    modal-id}
   [example-modal-inner m]])

