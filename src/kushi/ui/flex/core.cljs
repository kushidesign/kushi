;; TODO - use :-as option to provide alternate tag

(ns kushi.ui.flex.core
  (:require
   [kushi.css.core :refer (merge-attrs)]
   [kushi.ui.core :refer (opts+children)] ))

(defn row-fs
  {:desc ["Flex container, a div with `:.flex-row-fs` class"]}
  [& args]
  (let [[_ attrs & children]  (opts+children args)]
    (into
     [:div.flex-row-fs
      (merge-attrs
       {:data-kushi-ui "flex/row-fs"}
       attrs)]
     children)))

(defn row-c
  {:desc ["Flex container, a div with `:.flex-row-c` class"]}
  [& args]
  (let [[_ attrs & children]  (opts+children args)]
    (into
     [:div.flex-row-c
      (merge-attrs
       {:data-kushi-ui "flex/row-c"}
       attrs)]
     children)))
