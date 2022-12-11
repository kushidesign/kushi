(ns kushi.ui.tooltip.events
  (:require
   [kushi.ui.tooltip.core :refer (tooltip+parent remove-tooltip! expand-tooltip!)]
   [kushi.ui.dom :refer (conditional-display?)]))

(defn tooltip-mouse-enter [%]
  (when-let [[tooltip parent] (tooltip+parent %)]
    (when-not (conditional-display? tooltip)
      (expand-tooltip! tooltip parent))))

(defn tooltip-mouse-leave [%]
  (when-let [[tooltip parent] (tooltip+parent %)]
    (when-not (conditional-display? tooltip)
      (remove-tooltip! parent))))
