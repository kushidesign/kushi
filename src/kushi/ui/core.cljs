(ns ^:dev/always kushi.ui.core
  (:require-macros [kushi.core :refer [keyed add-system-font-stack]]
                   [kushi.ui.core])
  (:require [clojure.string :as string]
            [kushi.core :refer [merge-attrs]]))

(defn with-hashed-keys [coll]
  (map-indexed (fn [idx x] ^{:key (str (hash x) "-" idx)} x) coll))

(defn attr+children [coll]
  (when (coll? coll)
    (let [[a & xs] coll
          attr     (when (map? a) a)]
      [attr (if attr xs coll)])))

(defn user-attr? [x]
  (and (keyword? x)
       (->> x name (re-find #"^-[^\s\d]+"))) )

(defn unwrapped-children [children]
  (let [fc (first children)]
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
    (let [[attr* children]    (attr+children coll)
          user-ks             (some->> attr*
                                       keys
                                       (filter user-attr?)
                                       (into #{}))
          {:keys [attr opts]} (some->> attr*
                                       (group-by #(contains? user-ks (first %)))
                                       (map (fn [[k v]] {(if k :opts :attr) (into {} v)}))
                                       (apply merge))
          opts-w-normal-keys  (->> opts
                                   (map (fn [[k v]] [(-> k name (subs 1) keyword) v]))
                                   (into {}))]
      (into []
            (concat [opts-w-normal-keys attr]
                    (->> children (remove nil?) unwrapped-children))))))

(defn hiccup? [x]
  (and (vector? x) (-> x first keyword?)))

(defn hiccup-tag
  [{:keys [k re-str]}]
   (let [re-str (or re-str "!")
         tagstr  (name k)
         target? (some? (re-find (re-pattern (str ".+\\:\\" re-str "$")) tagstr))
         tag     (if (keyword? k)
                   (-> tagstr
                       (string/replace #":\!$|:\!children$|:\!attr$" "")
                       keyword)
                   k)]
     (when target? (keyed tagstr tag target?))
     [tag target?]))

(defn target-tag? [re-str k]
  (when (keyword k)
    {:k k :ret (-> {:k k :re-str re-str} hiccup-tag second)}
    (-> {:k k :re-str re-str} hiccup-tag second)))

(defn ->hiccup [x]
  (when (hiccup? x)
    (let [[tag* & more]   x
          [tag _]         (hiccup-tag {:k tag*})
          [attr children] (attr+children more)]
      [tag attr children])))

(defn hiccup-path
  "Finds the "
  [pred coll]
  (letfn [(path-in [x]
            (cond
              (pred x) '()
              (hiccup? x) (let [[failures [success & _]]
                              (->> x
                                   (map path-in)
                                   (split-with not))]
                          (when success (cons (count failures) success)))))]
    (path-in coll)))

(defn nested-hiccup? [coll]
  (and (hiccup? coll)
       (some hiccup? coll)))

(defn get-path [coll nested? re-str]
  (if nested? (->> coll (hiccup-path (partial target-tag? re-str)) drop-last) []))

(defn target-vector [coll]
  (let [nested?         (nested-hiccup? coll)
        path            (get-path coll nested? "!")
        [children-path
         attr-path]     (map #(if (seq path) path (get-path coll nested? %)) ["!children" "!attr"])
        target          (get-in coll path)
        children-target (get-in coll children-path)
        attr-target     (get-in coll attr-path)
        single-target?  (= children-path attr-path)]
    (keyed children-path children-target attr-path attr-target single-target?)))

(defn merge-attr2
  [a b]
  (if (and (map? a) (map? b))
    (merge-attrs a b)
    (if (map? a) a (when (map? b) b))))

(defn user-attr+children [args]
  (let [args                  (remove nil? args)
        [user-attr children*] (attr+children args)
        nested?               (some-> children* first seq?)
        children              (if nested? (first children*) children*)]
    [user-attr children]))

(defn merge-children
  [{:keys [user-children
           hiccup*
           children-target
           children-path]}]
  (let [[tag
         attr
         existing-children]  (->hiccup children-target)
        merged-children      (->> user-children
                                  (concat existing-children)
                                  with-hashed-keys)
        node-w-children      (into [] (concat [tag attr] merged-children))
        hiccup-w-children     (if (seq children-path)
                                   (assoc-in hiccup* children-path node-w-children)
                                   node-w-children)]
    (keyed merged-children hiccup-w-children)))

(defn merge-attributes
  [{:keys [decorator
           user-attr
           single-target?
           merged-children
           hiccup-w-children
           attr-target
           attr-path]}]
  (let [[tag attr _]   (->hiccup attr-target)
        component-attr (if (map? decorator)
                         (merge-attrs attr decorator)
                         attr)
        merged-attr    (merge-attr2 component-attr user-attr)
        node-w-attr    (if single-target?
                         (into [] (concat [tag merged-attr] merged-children))
                         (let [node*            (get-in hiccup-w-children attr-path)
                               [tag _ children] (->hiccup node*)]
                           (into [] (concat [tag merged-attr] children))))
        hiccup         (if (seq attr-path)
                         (assoc-in hiccup-w-children attr-path node-w-attr)
                         node-w-attr)]
    hiccup))

(defn gui
  ([args hiccup*]
   (gui args hiccup* nil))
  ([args hiccup* decorator]
   (let [targets          (target-vector hiccup*)
         [user-attr
          user-children]  (user-attr+children args)
         m                (merge targets (keyed user-attr user-children hiccup* decorator))
         m+               (-> m merge-children (merge m))
         hiccup-with-attr (merge-attributes m+)]
     hiccup-with-attr)))
