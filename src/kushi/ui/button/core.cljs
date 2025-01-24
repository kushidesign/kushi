(ns kushi.ui.button.core
  (:require
   [kushi.css.core :refer (css
                           css-vars-map
                           merge-attrs
                           register-design-tokens)]
   [kushi.ui.core :refer (opts+children keyed)]
   [kushi.ui.icon.core :refer (icon)]))

(register-design-tokens
 :--icon-button-padding-inline-ems
 :--button-with-icon-padding-inline-offset
 :--button-padding-inline-ems
 :--button-border-width)

(defn resolve-inline-offset
  [{:keys [only-icons? icon-inline-*? bordered?]}]
  (let [base (cond only-icons?
                   "var(--icon-button-padding-inline-ems)"
                   icon-inline-*?
                   "var(--button-with-icon-padding-inline-offset)"
                   :else
                   "var(--button-padding-inline-ems)")]
    (if bordered?
      (str "calc(" base " - " "var(--button-border-width)" ")")
      base)))

(defn icon-child? [x]
  (when (and (coll? x) (seq x) )
    (or (= (first x) icon)
        (some icon-child? x))))

(defn button
  {:summary ["Buttons provide cues for actions and events."]
   :desc ["Buttons are fundamental components allow users to process actions or navigate an experience."
          :br
          :br
          "They can be custom styled via a variety of tokens in your theme."
          :br
          :br "`--button-padding-inline-ems`"
          :br "The default value is `:1.2em`"
          :br
          :br "`--icon-button-padding-inline-ems`"
          :br "The default value is `:0.69em`"
          :br
          :br "`--button-padding-block-ems`"
          :br "The default value is `:0.67em`"
          :br
          :br "`--button-with-icon-padding-inline-offset`"
          :br "The default value is `:0.9em`"
          :br
          :br "`--button-border-width`"
          :br "The default value is `:1px`"
          :br]
   :opts (quote [{:name    loading?
                  :pred    boolean?
                  :default false
                  :desc    "When `true`, this will set the appropriate values for `aria-busy` and `aria-label`"}])}
  [& args]
  (let [[opts attrs & children] (opts+children args)
        {:keys [loading?]}      opts
        only-icons?             (every? icon-child? children)
        icon-inline-start?      (some-> children first icon-child?)
        icon-inline-end?        (some-> children last icon-child?)
        bordered?               (some->> attrs :class seq (some #{:bordered "bordered"}))
        pi-opts                 (keyed only-icons? bordered?)
        pis                     (resolve-inline-offset (assoc pi-opts :icon-inline-*? icon-inline-start?))
        pie                     (resolve-inline-offset (assoc pi-opts :icon-inline-*? icon-inline-end?))]

    (into [:button
           (merge-attrs
            {:style         (css-vars-map pis pie)
             :class         (css
                             ".kushi-button"
                             :.neutral
                             :.flex-row-c
                             :.transition
                             :cursor--pointer
                             :position--relative
                             :.enhanceable-with-icon
                             [:pis :$pis]
                             [:pie :$pie]
                             [:.bordered:pb
                              "calc(var(--button-padding-block-ems) - var(--button-border-width))"]
                             [:.bordered:pb
                              "calc(var(--button-padding-block-ems) - var(--button-border-width))"]
                             :ai--c
                             :w--fit-content
                             :pb--$button-padding-block-ems)
             :data-kushi-ui :button
             :aria-busy     loading?
             :aria-label    (when loading? "loading")}
            (when loading? {:data-kushi-ui-spinner true})
            attrs)]
          children)))
