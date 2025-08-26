(ns ^:dev/always kushi.ui.extract
  (:require [fireworks.core :refer [? !? ?> !?>]]
            [clojure.string :as string]
            [kushi.ui.variants :as variants]
            [kushi.util :refer [keyed as-str maybe]]
            [bling.core :refer [callout bling]]
            [bling.hifi :refer [hifi]]
            [bling.explain :refer [explain-malli]]
            [malli.core :as m])) 


;; data-ks-attribute resolution ------------------------------------------------

(defn data-ns-flex-attrs [m]
  (when-let [{:keys [display]} m]
    (cond 
      (or (keyword? display) (string? display))
      (!? {:data-ks-display (as-str display)})
      
      (vector? display)
      (!? (when-let [[css-display] (seq display)]
           (let [css-display (as-str css-display)]
             (cond
               (= "flex" css-display)
               (let [[_ flex-direction justify-content align-items] display]
                 {:data-ks-display (as-str css-display)
                  :data-ks-fd      (some-> flex-direction as-str)
                  :data-ks-jc      (some-> justify-content as-str)
                  :data-ks-ai      (some-> align-items as-str)})
               )))))))


(defn- resolve-supplied-prop [prop supplied when-not-nil]
  (if (true? (:boolean? prop))
    (if (false? supplied) nil "")
    (or when-not-nil
        (kushi.util/as-str supplied))))


(defn- resolve-default-prop [prop default]
  (if (true? (:boolean? prop))
    (case default
      false   nil
      "false" nil
      "")
    (kushi.util/as-str default)))


(defn- data-ks-attr*
  "Returns something like:
   `{:data-ks-surface \"transparent\"}`
   or
   `{:data-ks-inert \"\"}`
   
   This sorts out `data-ks-*` attrs that are boolean,
   but need to be supplied as `data-ks-foo=\"\"` (when true, appears in dom as `data-ks-foo`)
   or `data-ks-foo=nil` (if false, does not appear in dom)"

  [{:keys [when-not-nil default] :as prop} supplied data-ks-key]

  (cond 
    (not (nil? supplied))
    {data-ks-key (resolve-supplied-prop prop supplied when-not-nil)}

    default
    {data-ks-key (resolve-default-prop prop default)}))


(defn- shared-prop-destined-for-data-ks-attr? [k data-ks?]
  (and (contains? variants/props k)
       (not (false? data-ks?))))


(defn- destined-for-data-ks-attr? [k {:keys [data-ks?]}]
  (or (shared-prop-destined-for-data-ks-attr? k data-ks?)
      ;; user prop destined for data-ks-attr
      (true? data-ks?)))


(defn- data-ks-attr
  [props k prop]
  (let [supplied    (get props k)
        data-ks-key (keyword (str "data-ks-" (name k)))
        ret         (data-ks-attr* prop supplied data-ks-key)]
    (!? {:when (= k :contour)} (keyed [supplied data-ks-key ret]))
    ret))



(defn data-ks-attrs 
  "Creates a map of data-ks-* attributes based on defined prop schema from
   component rendering function's metadata map, which is defined in the defui
   macro. To be called at runtime from within runtime portion of defui macro.
   
   If one of the props is supplied, it will convert it to a data-ks-* attribute,
   or do something else with it, such as set a css var in the style map, or
   just ignore it, if the prop is just used for internal logic in the component
   rendering function."
  [props defaults-by-prop flag]
  (!? (symbol (str flag ":data-ks-attrs"))
      (merge (!? (reduce-kv 
              (fn [m k prop]
                (merge m
                       (when (destined-for-data-ks-attr? k prop)
                         (data-ks-attr props k prop))))
              {} 
              (!? defaults-by-prop)))
             (!? (data-ns-flex-attrs props))
             (some->> props :at (hash-map :data-ks-at)))))


#_(defn user-supplied-props->data-ks-attrs 
  "wtf"
  [props defaults-by-prop]
  (!? 'runtime:user-supplied-props->data-ks-attrs 
      (merge (!? (reduce-kv 
              (fn [m k prop]
                (merge m
                       (when (destined-for-data-ks-attr? k prop)
                         (data-ks-attr props k prop))))
              {} 
              (!? (select-keys defaults-by-prop (keys props)))))
             (!? (data-ns-flex-attrs props))
             (some->> props :at (hash-map :data-ks-at)))))


;; extraction of props  ------------------------------------------------

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
