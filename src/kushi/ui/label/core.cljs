(ns kushi.ui.label.core
  (:require [par.core :refer-macros [!? ?]]
            [kushi.core :refer-macros (sx cssfn defclass)]
            [kushi.gui :refer-macros (defcom)]))

(defclass ^:kushi label :d--block)

(defcom label
  [:span (sx :.label)])
