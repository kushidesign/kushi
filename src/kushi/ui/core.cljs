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


(defn opts+children
  "Reorganizes arguments to component and returns:
   [map-of-user-opts attr child1 child2 ...]"
  [coll]
  (when (coll? coll)
    (let [[attr* children]    
          (attr+children coll)

          user-ks             
          #_(some->> attr*
                     keys
                     (filter user-attr?)
                     (into #{}))
          (some->> attr*
                   (reduce-kv (fn [acc k _]
                                (if (user-attr? k)
                                  (conj acc k)
                                  acc))
                              #{}))

          {:keys [attr opts]
           :as   b} 
          #_(some->> attr*
                   (group-by #(contains? user-ks (first %)))
                   (map (fn [[k v]] {(if k :opts :attr) (into {} v)}))
                   (apply merge))
          (some->> attr*
                   (group-by #(contains? user-ks (first %)))
                   (reduce-kv (fn [m k v]
                                (assoc m (if k :opts :attr) (into {} v)))
                              {}))

          opts-w-normal-keys  
          #_(->> opts
                 (map (fn [[k v]] [(-> k name (subs 1) keyword) v]))
                 (into {}))

          (some->> opts
                   (reduce-kv
                    (fn [m k v]
                      (assoc m (-> k name (subs 1) keyword) v))
                    {}))

          ;; TODO can you eliminate this unwrapping?
          unwrapped-children
          (->> children (remove nil?) unwrapped-children)]

       #_(into []
               (concat [opts-w-normal-keys attr]
                       (->> children (remove nil?) unwrapped-children)))
       (into [opts-w-normal-keys attr]
             unwrapped-children))))


(defn opts+children2*
  "Reorganizes arguments to component and returns:
   [map-of-user-opts attr child1 child2 ...]"
  [coll f fvar-meta]
  ;; (? f)
  ;; (? {:display-metadata? true} coll)
  (when (coll? coll)
    (let [[src coll]          (let [[src & rest] coll]
                                ;; (? {:display-metadata? true} src)
                                ;; (? {:display-metadata? true} rest)
                                (if (some-> src meta :kushi.ui/form)
                                  [src rest]
                                  [nil coll]))                
          [attr* children]    (attr+children coll)
          user-ks             (some->> attr*
                                       keys
                                       (filter user-attr?)
                                       (into #{}))
          {:keys [attr opts]} (some->> attr*
                                       (group-by #(contains? user-ks (nth % 0 nil)))
                                       (map (fn [[k v]] {(if k :opts :attr) (into {} v)}))
                                       (apply merge))
          opts-w-normal-keys  (->> opts
                                   (map (fn [[k v]] [(-> k name (subs 1) keyword) v]))
                                   (into {}))]
      (when src
        (kushi.core/validate-options* fvar-meta opts-w-normal-keys src))
      {:opts     opts-w-normal-keys
       :attrs    attr
       :children (->> children (remove nil?) unwrapped-children)})))


