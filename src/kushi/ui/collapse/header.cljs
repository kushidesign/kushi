(ns kushi.ui.collapse.header
  (:require-macros [kushi.core :refer (sx)] )
  (:require [kushi.utils :as util :refer-macros (keyed)]
            [clojure.string :as string]
            [kushi.ui.icon.mui.core :refer (mui-icon)]
            [kushi.ui.title.core :refer (title)] ))

(defn readable-string? [label]
  (and (string? label) (not (string/blank? label))))


;; TODO put backin component when you get hashed vars working
;; (defclass ^:kushi-override hide-when-expanded
;;   {"has-parent([aria-expanded='true']):display" "none"})

;; (defclass ^:kushi-override show-when-expanded
;;   {"has-parent([aria-expanded='true']):display" "block"})

(defn header-title
  [{:keys [label
           icon-opposite?]
    :as   opts}]
    (js/console.log :header-title [label
                                   icon-opposite?])
    (if (string? label)
      (let [ico      [mui-icon
                      (sx 'kushi-collapse-header-title-icon
                          [:mie (when-not icon-opposite? :--icon-enhancer-inline-gap-ems)])
                      (:mui-icon opts)]
            title-sx (sx
                      'kushi-collapse-header-title-contents
                      {:style {:w                    (when icon-opposite? :100%)
                               :>span.kushi-label:w  (when icon-opposite? :100%)
                               :>span.kushi-label:jc (when icon-opposite? :space-between)}})]
        (if icon-opposite?
          [title title-sx label ico]
          [title title-sx ico label]))
      label))

(defn collapse-header-contents
  [{:keys [label label-expanded mui-icon mui-icon-expanded icon-position]
    :as   m}]
  (let [label-expanded    (or label-expanded label)
        mui-icon          (if (util/nameable? mui-icon) (name mui-icon) "add")
        mui-icon-expanded (if (util/nameable? mui-icon-expanded) (name mui-icon-expanded) "remove")
        icon-opposite?    (= :end icon-position)
        opts              (keyed label mui-icon icon-opposite?)]
    [:<>
     [:span
      (sx 'kushi-collapse-header-label-collapsed
          :.flex-row-fs
          :w--100%
          ["has-parent([aria-expanded='true']):display" :none])
      (if (string? label)
        [header-title opts]
        label)]
     [:span
      (sx 'kushi-collapse-header-label-expanded
          :.flex-row-fs
          :w--100%
          :d--none
          ["has-parent([aria-expanded='true']):display" :block])
      (if (string? label-expanded)
        [header-title (assoc opts :label label-expanded :mui-icon mui-icon-expanded)]
        label-expanded)]]))
