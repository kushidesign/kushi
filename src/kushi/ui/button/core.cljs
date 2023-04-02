(ns kushi.ui.button.core
  (:require-macros
   [kushi.core :refer (sx keyed)])
  (:require
   [kushi.playground.util :as util :refer-macros (keyed)]
   [kushi.core :refer (merge-attrs)]
   [kushi.ui.core :refer (opts+children)]
   [kushi.ui.icon.core :refer (icon)]))


(defn resolve-inline-offset
  [{:keys [only-icons? icon-inline-*? bordered?]}]
  (let [base (cond only-icons?
                   "var(--icon-button-padding-inline-ems)"
                   icon-inline-*?
                   "var(--button-with-icon-padding-inline-offset)"
                   :else
                   "var(--button-padding-inline-ems)")]
    (if bordered?
      (str "calc(" base " - var(--button-border-width))")
      base)))

(defn icon-child? [x]
  (when (and (coll? x) (seq x) )
    (or (= (first x) icon)
        (some icon-child? x))))

(defn button
  {:desc ["Buttons provide cues for actions and events."
          "These fundamental components allow users to process actions or navigate an experience."]}
  [& args]
  (let [[opts attrs & children] (opts+children args)
        {:keys [loading?]}   opts
        only-icons?          (every? icon-child? children)
        icon-inline-start?   (some-> children first icon-child?)
        icon-inline-end?     (some-> children last icon-child?)
        bordered?            (some->> attrs :class seq (some #{:bordered "bordered"}))
        pi-opts              (keyed only-icons? bordered?)
        pis                  (resolve-inline-offset (assoc pi-opts :icon-inline-*? icon-inline-start?))
        pie                  (resolve-inline-offset (assoc pi-opts :icon-inline-*? icon-inline-end?))]

    (into [:button
           (merge-attrs
            (sx 'kushi-button
                :.flex-row-c
                :.transition
                :.pointer
                :.relative
                :.neutral
                :.enhanceable
                :ai--c
                [:pis pis]
                [:pie pie]
                :pb--$button-padding-block-ems
                [:&.bordered:pb "calc(var(--button-padding-block-ems) - var(--button-border-width))"]
                [:&.bordered:pb "calc(var(--button-padding-block-ems) - var(--button-border-width))"]
                {:data-kushi-ui :button
                 :aria-busy     loading?
                 :aria-label    (when loading? "loading")})
            (when loading? {:data-kushi-ui-progress true})
            attrs)]
          children)))

