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
;;   {"has(parent([aria-expanded='true'])):display" "none"})

;; (defclass ^:kushi-override show-when-expanded
;;   {"has(parent([aria-expanded='true'])):display" "block"})

(defn header-title
  [{:keys [label
           icon
           icon-opposite?
           title-sx]}]
    (if (string? label)
      (let [ico [mui-icon icon]]
         (if icon-opposite?
           [title title-sx label ico]
           [title title-sx ico label]))
      label))

(defn collapse-header-contents
  [{:keys [label label-expanded icon icon-expanded icon-position]}]
  (let [label-expanded (or label-expanded label)
        icon           (if (util/nameable? icon) (name icon) "add")
        icon-expanded  (if (util/nameable? icon-expanded) (name icon-expanded) "remove")
        icon-opposite? (= :end icon-position)
        title-sx       (sx ^:no-prefix
                           'kushi-collapse-header-title-contents
                           {:style {:w        :100%
                                    :>span:jc (when icon-opposite? :space-between)}})
        opts           (keyed label icon icon-opposite? title-sx)]
   [:<>
    [:span
     (sx 'kushi-collapse-header-label-collapsed:ui
         :.flex-row-fs
         :w--100%
         {:style {"has(parent([aria-expanded='true'])):display" :none}})
     (if (string? label)
       [header-title opts]
       label)]
    [:span
     (sx 'kushi-collapse-header-label-expanded:ui
         :.flex-row-fs
         :w--100%
         :d--none
         {:style {"has(parent([aria-expanded='true'])):display" :block}
          })
     (if (string? label-expanded)
       [header-title (assoc opts :label label-expanded :icon icon-expanded)]
       label-expanded)]]))
