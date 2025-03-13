(ns kushi.ui.button.core
  (:require [fireworks.core :refer [? !? ?> !?>]]
            ;; [kushi.jeff :refer []]
            ))

;; (defmacro big-paw*
;;   [& args]
;;   (let [v (into ['kushi.ui.button.core/big-paw
;;                  ^:kushi.ui/form
;;                  {:form-meta (meta &form)
;;                   :form      (str &form)}]
;;                 args)]
;;     `~v))

;; (defmacro myfn
;;   {:args '[x
;;            {:pred duh-cat?}]}
;;   [x]
;;   (? (meta &form))
;;   (let [vf (? (setup (-> myfn var) (meta &form)))]
;;     (!? (-> myfn var meta))
;;     `nil))

