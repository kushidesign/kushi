(ns ^:dev/always kushi.ui.core
  (:require-macros [kushi.ui.core])
  (:require [clojure.string :as string]
            [fireworks.core :refer [? !? ?> !?>]]
            [kushi.core :refer [merge-attrs]]))
      

(defn attr+children [coll]
  (when (coll? coll)
    (let [[a & xs] coll
          attr     (when (map? a) a)]
      [attr (if attr xs coll)])))

;; TODO add in docs that custom attribute names must pass this regex
(defn user-attr? [x]
  (and (keyword? x)
       (->> x name (re-find #"^-[^\s\d]+"))) )

(defn unwrapped-children [children]
  (let [fc (nth children 0 nil)]
    (if (and
          (seq? children)
          (= 1 (count children))
          (seq? fc)
          (seq fc))
      fc
      children)))


(defn children
  "For internal use by defcom macro"
  [children* f]
  (let [children (unwrapped-children children*)
        fragment? (-> children* first (= :<>))]
    (into (if fragment? [] [:<>]) (if f (map f children) children))))

(defn children2
  "For internal use by defcom macro"
  [children*]
  (let [children (unwrapped-children children*)
        fragment? (-> children* first (= :<>))]
    (into (if fragment? [] [:<>]) children)))

(defn user-ks [attr*]
  #_(some->> attr*
             keys
             (filter user-attr?)
             (into #{}))
  (some->> attr*
           (reduce-kv (fn [acc k _]
                        (if (user-attr? k)
                          (conj acc k)
                          acc))
                      #{})))
(defn grouped-attrs
  [attr* user-ks]
  #_(some->> attr*
             (group-by #(contains? user-ks (first %)))
             (map (fn [[k v]] {(if k :opts :attr) (into {} v)}))
             (apply merge))
  (some->> attr*
           (group-by #(contains? user-ks (first %)))
           (reduce-kv (fn [m k v]
                        (assoc m (if k :opts :attr) (into {} v)))
                      {})))

(defn opts-w-normal-keys
  [opts]
  #_(->> opts
                 (map (fn [[k v]] [(-> k name (subs 1) keyword) v]))
                 (into {}))
  (some->> opts
           (reduce-kv
            (fn [m k v]
              (assoc m (-> k name (subs 1) keyword) v))
            {})))

(defn extract
  "Reorganizes arguments to component and returns:
   [map-of-user-opts attr child1 child2 ...]"
  [coll]
  (when (coll? coll)
    (let [[attr* children]    (attr+children coll)
          user-ks             (user-ks attr*)
          {:keys [attr opts]} (grouped-attrs attr* user-ks)
          opts                (opts-w-normal-keys opts)
          ;; TODO can you eliminate this unwrapping?
          children            (->> children (remove nil?) unwrapped-children)]

      #_(into []
              (concat [opts-w-normal-keys attr]
                      (->> children (remove nil?) unwrapped-children)))
      (into [opts attr]
            children))))


;; TODO - use new optimized fns from above
(defn extract* 
  "Extracts custom attributes from mixed map of html attributes and
   attributes/options that are specific to the ui component.
   
   Returns a map:
   {:opts     <map-of-custom-attributes>
    :attrs    <html-attributes>
    :children <children>}"
  [args uic-meta]
  (when (coll? args)
    (let [[src args]          (let [[src & rest] args]
                                (if (some-> src meta :kushi.ui/form)
                                  [src rest]
                                  [nil args]))                
          [attr* children]    (attr+children args)
          user-ks             (some->> attr*
                                       keys
                                       (filter user-attr?)
                                       (into #{}))
          {:keys [attr opts]} (some->> attr*
                                       (group-by #(contains? user-ks
                                                             (nth % 0 nil)))
                                       (map (fn [[k v]]
                                              {(if k :opts :attr) (into {} v)}))
                                       (apply merge))
          supplied-opts       (->> opts
                                   (map (fn [[k v]]
                                          [(-> k name (subs 1) keyword) v]))
                                   (into {}))]
      (when (and ^boolean js/goog.DEBUG
                 (:opts uic-meta))
        (kushi.core/validate-options* {:uic-meta      uic-meta
                                       :supplied-opts supplied-opts 
                                       :src           src}))
      {:opts     supplied-opts
       :attrs    attr
       :children (->> children
                      (remove nil?)
                      unwrapped-children)})))


