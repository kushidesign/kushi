(ns ^:dev/always kushi.ui.extract
  (:require [fireworks.core :refer [? !? ?> !?>]]
            [clojure.string :as string]
            [kushi.ui.variants :as variants]
            [kushi.util :refer [keyed]]
            [bling.core :refer [callout bling]]
            [bling.hifi :refer [hifi]]
            [bling.explain :refer [explain-malli]]
            [malli.core :as m]))

(def kushi-ui-props 
  #{:at :inert :end-enhancer :start-enhancer :loading :stroke-align :stroke-width})

(defn attr+children [coll]
  (when (coll? coll)
    (let [[a & xs] coll
          attr     (when (map? a) a)]
      [attr (if attr xs coll)])))

(defn unwrapped-children [children]
  (let [fc (nth children 0 nil)]
    (if (and
          (seq? children)
          (= 1 (count children))
          (seq? fc)
          (seq fc))
      fc
      children)))

(defn extract
  "Extracts custom attributes from mixed map of html attributes and
   attributes/options that are specific to the ui component.
   
   Returns a map:
   {:props    <map-of-custom-attributes>
    :attrs    <html-attributes>
    :children <children>}"
  ([args]
   (extract args nil))
  ([args custom-option-ks]
   (extract args custom-option-ks nil))
  ([args custom-option-ks fn-info]
   (when (coll? args)
     (let [[attr* children]      (attr+children args)

           user-ks               (some->> attr*
                                          keys
                                          (filter #(or (contains? variants/variants-by-custom-opt-key %)
                                                       (contains? (into #{} custom-option-ks) %)
                                                       (contains? kushi-ui-props %)))
                                          (into #{}))

           {:keys [attrs props]} (some->> attr*
                                          (group-by #(contains? user-ks (nth % 0 nil)))
                                          (map (fn [[k v]]
                                                 {(if k :props :attrs) (into {} v)}))
                                          (apply merge))
           attrs                 (apply dissoc attrs (!? user-ks))]

       {:props    props
        :attrs    attrs
        :children (->> children (remove nil?) unwrapped-children)}))))
