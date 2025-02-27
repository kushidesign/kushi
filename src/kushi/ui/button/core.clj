(ns kushi.ui.button.core
  (:require [fireworks.core :refer [? !? ?> !?>]]))

(defmacro big-paw*
  [& args]
  (let [v (into ['kushi.ui.button.core/big-paw
                 ^:kushi.ui/form
                 {:form-meta (meta &form)
                  :form      (str &form)}]
                args)]
    `~v))
