(ns kushi.ui.collapse.header
  (:require 
   [kushi.core :refer [css sx css-vars-map]]
   [clojure.string :as string]
   [kushi.ui.icon.core]
   [kushi.ui.label.core]))

(defn readable-string? [label]
  (and (string? label) (not (string/blank? label))))

;; TODO put back in component when you get hashed vars working
;; (defclass ^:kushi-override hide-when-expanded
;;   {"has-parent([aria-expanded='true']):display" "none"})

;; (defclass ^:kushi-override show-when-expanded
;;   {"has-parent([aria-expanded='true']):display" "block"})

(defn header-title
  [{:keys [label
           icon-opposite?
           icon]
    :as   opts}]
  (if (string? label)
    (let [w (when icon-opposite? "100%")
          jc (when icon-opposite? "space-between")
          attrs
          {:style (css-vars-map w jc)
           :class (css
                   ".kushi-collapse-header-title-contents"
                   :w--$w
                   :jc--$jc)}]
      (if icon-opposite?
        [kushi.ui.label.core/label attrs label icon]
        [kushi.ui.label.core/label attrs icon label]))
    label))

(defn collapse-header-contents
  [{:keys [label
           label-expanded
           icon
           icon-expanded
           icon-position]
    :or {icon          [kushi.ui.icon.core/icon :add]
         icon-expanded [kushi.ui.icon.core/icon :remove]}}]
  (let [label-expanded    (or label-expanded label)
        icon-opposite?    (= :end icon-position)
        opts              {:label          label
                           :icon           icon
                           :icon-opposite? icon-opposite?}]
    [:<>
     [:span
      (sx ".kushi-collapse-header-label-collapsed"
          :.flex-row-fs
          :w--100% )
      (if (string? label)
        [header-title opts]
        label)]
     [:span
      (sx ".kushi-collapse-header-label-expanded"
          :.flex-row-fs
          :w--100%
          :d--none)
      (if (string? label-expanded)
        [header-title (assoc opts :label label-expanded :icon icon-expanded)]
        label-expanded)]]))
