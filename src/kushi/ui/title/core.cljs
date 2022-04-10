(ns kushi.ui.title.core
  (:require [par.core :refer-macros [!? ?]]
            [kushi.core :refer-macros (sx cssfn defclass)]
            [kushi.gui :refer-macros (defcom)] ))


(defcom title
  [:span (sx :.block {:prefix :kushi- :ident :title})])
