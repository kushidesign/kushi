(ns kushi.ui.button.core
  (:require-macros
   [kushi.core :refer (sx defclass)])
  (:require
   [kushi.core :refer (merge-attrs)]
   [kushi.ui.core :refer (opts+children)]
   [kushi.ui.icon.core :refer (icon)]))

(defn resolve-inline-offset
  [{:keys [only-icons? icon-inline-*?]}]
  (cond only-icons?
        :0.8em
        icon-inline-*?
        "var(--button-with-icon-padding-inline-offset)"
        :else
        "var(--button-padding-inline-ems)"))

(defn icon-inline-start? [children]
  (and (= (ffirst children) icon)
       (not= (-> children second first) icon)))

(defn icon-inline-end? [children]
  (and (= (-> children second first) icon)
       (not= (ffirst children) icon)))

(defn icon-child? [x]
  (when (seq x)
    (= (first x) icon)))

(defn button
  {:desc ["Buttons provide cues for actions and events."
          "These fundamental components allow users to process actions or navigate an experience."]}
  [& args]
  (let [[_ attrs & children] (opts+children args)
        only-icons?             (every? icon-child? children)
        icons+others?           (and (not only-icons?)
                                     (some icon-child? children))
        two-children?           (= 2 (count children))
        icon-inline-start?      (when two-children? (icon-inline-start? children))
        icon-inline-end?        (when (and (not icon-inline-start?)
                                           two-children?)
                                  (icon-inline-end? children))
        pis                     (resolve-inline-offset
                                 {:only-icons?    only-icons?
                                  :icon-inline-*? icon-inline-start?})
        pie                     (resolve-inline-offset
                                 {:only-icons?    only-icons?
                                  :icon-inline-*? icon-inline-end?})]
    (into [:button
           (merge-attrs
            (sx 'kushi-button
                :.flex-row-c
                :.transition
                :.pointer
                :.relative
                :.neutral
                :ai--c
                [:pis pis]
                [:pie pie]
                :pb--0.8em
                [:gap (when (or icons+others?
                                icon-inline-start?
                                icon-inline-end?)
                        "var(--icon-enhancer-inline-gap-ems)")]
                {:data-kushi-ui :button})
            attrs)]
           children)))

