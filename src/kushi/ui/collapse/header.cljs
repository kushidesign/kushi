(ns kushi.ui.collapse.header
  (:require-macros [kushi.core :refer (sx cssfn)]
                   [kushi.gui :refer (defcom)])
  (:require [kushi.ui.icon.core :refer (bar chevron-down icon)]
            [kushi.ui.icon.mui.core :refer (mui-icon)]
            [kushi.ui.title.core :refer (title)]
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
                   :width :14px
                   :height :14px
                   :&_path:stroke-width 1.5
                   :mr    :10px}
           :class [:kushi-custom-icon]})
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
      (sx {:style {"has(parent([aria-expanded='false'])):transform" "rotate(-90deg)"
                   "has(parent([aria-expanded='true'])):transform" "rotate(0deg)"
                   :transition-property :transform
                   :transition-duration :500ms
                   :&_path:stroke-width 1.5
                   :width :14px
                   :height :14px
                   :mr    :10px}
           :class [:kushi-custom-icon]})
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
     [title
      (sx {:prefix :kushi-
           :ident :collapse-header-label})
      label-text])])
