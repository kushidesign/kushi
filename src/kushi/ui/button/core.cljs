(ns kushi.ui.button.core
  (:require-macros
   [kushi.core :refer (sx)])
  (:require [clojure.string :as string]
            [fireworks.core :refer [?]]
            [kushi.core :refer (merge-attrs)]
            [kushi.playground.util :as util :refer-macros (keyed)]
            [kushi.ui.core :refer (opts+children)]
            [kushi.ui.icon.core :refer (icon)]
            [kushi.ui.util :refer [as-str maybe nameable?]]))


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

(def variants
  {:shape    #{"rounded" "sharp" "pill"}
   :surface  #{"minimal" "outline" "solid" "soft"}
   :semantic #{"neutral" "accent" "positive" "negative" "warning"}})

(def variant-defaults
  {:semantic "neutral"
   :surface  "soft"
   :shape    "rounded"})

(defn data-kushi- [x k]
  (some-> x
          as-str
          (maybe (get variants k nil))
          (->> (hash-map (keyword (str "data-kushi-" (name k)))))))

(def color-mix-support? (? (.supports js/window.CSS "(color: color-mix(in oklch, red, transparent)")))
(def oklch-support? (? (.supports js/window.CSS "(color: oklch(40.1% 0.123 21.57))")))

(defn- semantics [{:keys [semantic surface ia shape]}]
 (merge (data-kushi- shape :shape)
        (data-kushi- surface :surface)
        (data-kushi- semantic :semantic)
        {:class (str "_" (string/join "_" ["kushi" semantic surface ia]))}))


(defn- get-variants [opts]
  (reduce-kv (fn [acc k v]
               (assoc acc
                      k
                      (or (some-> k
                                  opts
                                  (maybe nameable?)
                                  as-str
                                  (maybe (k variants)))
                          v)))
             {}
             variant-defaults))

(defn button
  {:summary ["Buttons provide cues for actions and events."]
   :desc ["Buttons are fundamental components allow users to process actions or navigate an experience."
          :br
          :br
          "They can be custom styled via a variety of tokens in your theme."
          :br
          :br "`:$button-padding-inline-ems`"
          :br "The default value is `:1.2em`"
          :br
          :br "`:$icon-button-padding-inline-ems`"
          :br "The default value is `:0.69em`"
          :br
          :br "`:$button-padding-block-ems`"
          :br "The default value is `:0.67em`"
          :br
          :br "`:$button-with-icon-padding-inline-offset`"
          :br "The default value is `:0.9em`"
          :br
          :br "`:$button-border-width`"
          :br "The default value is `:1px`"
          :br]
   :opts '[{:name    loading?
            :pred    boolean?
            :default false
            :desc    "When `true`, this will set the appropriate values for `aria-busy` and `aria-label`"}]}
  [& args]
  (let [[opts attrs & children] (opts+children args)
        {:keys [loading?
                shape]}         opts
        only-icons?             (every? icon-child? children)
        icon-inline-start?      (some-> children first icon-child?)
        icon-inline-end?        (some-> children last icon-child?)
        bordered?               (some->> attrs :class seq (some #{:bordered "bordered"}))
        pi-opts                 (keyed only-icons? bordered?)
        pis                     (resolve-inline-offset (assoc pi-opts :icon-inline-*? icon-inline-start?))
        pie                     (resolve-inline-offset (assoc pi-opts :icon-inline-*? icon-inline-end?))
        variants                (get-variants opts)]

    (into [:button
           (merge-attrs
            (sx 'kushi-button
                :.flex-row-c
                :.transition
                :.pointer
                :.relative
                ;; :.neutral
                :.enhanceable
                ;; :.rounded
                :ai--c
                :w--fit-content
                [:pis pis]
                [:pie pie]
                :pb--$button-padding-block-ems
                [:&.bordered:pb "calc(var(--button-padding-block-ems) - var(--button-border-width))"]
                [:&.bordered:pb "calc(var(--button-padding-block-ems) - var(--button-border-width))"]
                {:data-kushi-ui :button
                 :aria-busy     loading?
                 :aria-label    (when loading? "loading")})
            (when loading? {:data-kushi-ui-spinner true})
            {:class (str "_kushi_" (or (some-> shape as-str) "rounded"))}
            (semantics (assoc variants :ia "ia"))
            attrs)]
          children)))

;; (defn button2
;;   {:desc ["Buttons provide cues for actions and events."
;;           "These fundamental components allow users to process actions or navigate an experience."]}
;;   [& args]
;;   (let [[opts attrs & children] (opts+children args)
;;         {:keys [loading?
;;                 size
;;                 variant
;;                 shape
;;                 semantic]
;;          :or   {size     "medium"
;;                 variant  "soft"
;;                 shape    "rounded"
;;                 semantic "neutral"}}  opts
;;         only-icons?             (every? icon-child? children)
;;         icon-inline-start?      (some-> children first icon-child?)
;;         icon-inline-end?        (some-> children last icon-child?)
;;         bordered?               (some->> attrs :class seq (some #{:bordered "bordered"}))
;;         pi-opts                 (keyed only-icons? bordered?)
;;         pis                     (resolve-inline-offset (assoc pi-opts :icon-inline-*? icon-inline-start?))
;;         pie                     (resolve-inline-offset (assoc pi-opts :icon-inline-*? icon-inline-end?))]

;;     (into [:button
;;            (merge-attrs
;;             (sx 'kushi-button
;;                 :.flex-row-c
;;                 :.transition
;;                 :.pointer
;;                 :.relative
;;                 :.enhanceable
;;                 :.rounded
;;                 :ai--c
;;                 :w--fit-content
;;                 [:pis pis]
;;                 [:pie pie]
;;                 :pb--$button-padding-block-ems
;;                 [:&.bordered:pb "calc(var(--button-padding-block-ems) - var(--button-border-width))"]
;;                 [:&.bordered:pi "calc(var(--button-padding-inline-ems) - var(--button-border-width))"]
;;                 {:data-kushi-ui       :button
;;                  :data-kushi-size     size
;;                  :data-kushi-variant  variant
;;                  :data-kushi-shape    shape
;;                  :data-kushi-semantic semantic
;;                  :aria-busy           loading?
;;                  :aria-label          (when loading? "loading")
;;                  :class [size variant shape semantic]})
;;             (when loading? {:data-kushi-ui-spinner true})
;;             attrs)]
;;           children)))








