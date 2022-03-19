(ns kushi.ui.collapse.header
  (:require-macros [kushi.core :refer (sx cssfn)]
                   [kushi.gui :refer (defcom)])
  (:require [kushi.ui.icon.core :refer (bar chevron-down icon)]
            [kushi.ui.label.core :refer (label)]
            [par.core :refer-macros [!? ?]]))

(defn collapse-header-contents
  [{:keys [label-text label-text-expanded icon-type]}]
  [:<>
   ;; TODO refactor out cond once variable selector name generation is fixed
   (cond
     (= icon-type :plus)
[icon
      (sx {:style {:transition-property :transform
                   :transition-duration :500ms
                   :position :.relative
                   :width :10px
                   :mr    :10px}})
      [:div (sx :.absolute-fill
                {:style {:overflow :hidden
                         :transition-property :transform
                         :transition-duration :500ms}})
       [bar]]
      [:div (sx :.absolute-fill
                {:style {"has(ancestor([aria-expanded='false'])):transform" "rotate(-90deg)"
                         "has(ancestor([aria-expanded='true'])):display" "none"
                         :overflow :hidden
                         :transition-duration :500ms}})
       [bar]]]
     #_[icon
      (sx {:style {:transition-property :transform
                   :transition-duration :500ms
                   :position :.relative
                   :width :10px
                   :mr    :10px}})
      [:div (sx :.absolute-fill
                {:style {:overflow :hidden
                         :transition-property :transform
                         :transition-duration :500ms}})
       [bar]]
      [:div (sx :.absolute-fill
                {:style {"has(ancestor([aria-expanded='false'])):transform" "rotate(-90deg)"
                         "has(ancestor([aria-expanded='true'])):display" "none"
                         :overflow :hidden
                         :transition-duration :500ms}})
       [bar]]]
     :else
     [icon
      (sx {:style {
                   "has(parent([aria-expanded='false'])):transform" "rotate(-90deg)"
                   "has(parent([aria-expanded='true'])):transform" "rotate(0deg)"
                   :transition-property :transform
                   :transition-duration :500ms
                   :width :10px
                   :mr    :10px}})
      [chevron-down]])
   (if label-text-expanded
     [:<>
      [:span
       (sx {:style {"has(parent([aria-expanded='true'])):display" "none"}
            :prefix :kushi-
            :ident :collapse-header-label-text-collapsed})
       label-text]
      [:span
       (sx :d--none
           {:style {"has(parent([aria-expanded='true'])):display" "block"}
            :prefix :kushi-
            :ident :collapse-header-label-text-expanded})
       label-text-expanded]]
     [label
      (sx {:prefix :kushi-
           :ident :collapse-header-label})
      label-text])])
