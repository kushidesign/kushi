(ns ^:dev/always kushi.gui
  (:require-macros [kushi.core :refer [keyed]]
                   [kushi.gui])
  (:require [clojure.string :as string]
            [par.core :refer [? !? ?+ !?+]]
            [kushi.utils :as util]))

(defn opts+children [coll]
  (when (coll? coll)
    (let [[a* & c*] coll
          opts      (when (map? a*) a*)
          children  (if opts c* coll)]
      (keyed opts children))))

(defn hiccup? [x]
  (and (vector? x) (-> x first keyword?)))

(defn hiccup-span? [x]
  (and (hiccup? x)  (->> x first str (re-find #"^\:span[\:\.\#]?.*"))))

(defn split-key [k re] (-> k name (string/split re)))

(defn hiccup-tag [k]
  (let [tagstr  (name k)
        target? (some? (re-find #".+\:\!$" tagstr))
        tag     (if target?
                  (-> tagstr (string/replace #":\!$" "") keyword)
                  k)]
    (when target? (keyed tagstr tag target?))
    [tag target?]))

(defn target-tag? [k]
  (when (keyword k)
    {:k k :ret (-> k hiccup-tag second)}
    (-> k hiccup-tag second)))

(def kushi-keys
  [:css :ident :data-cljs :element :prefix :ancestor])

(defn ->hiccup [x]
  (when (hiccup? x)
    (let [[tag* attr*] x
          [tag _]      (hiccup-tag tag*)
          attr         (when (map? attr*) attr*)]
      [tag attr])))

(defn merge-hiccup [{:keys [tag attr args inner-span]}]
  (!? :merge-hiccup (keyed tag attr args inner-span))
  (let [user-attr   (when (map? (first args)) (first args))
        children*   (if user-attr (rest args) args)
        nested?     (some-> children* first seq?)
        children*   (if nested? (first children*) children*)
        children    (map-indexed (fn [idx x] ^{:key (str (hash x) "-" idx)} x) children*)
        merge?      (and user-attr (map? attr))
        attr-merged (if merge?
                      (util/merge-with-style attr (first args))
                      attr)]
    (into [] (if inner-span
               [tag attr-merged (apply conj inner-span children)]
               (remove nil? (concat [tag attr-merged] children))))))

(defn hiccup-path [pred coll]
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

(defn target-vector [coll]
  (let [nested? (nested-hiccup? coll)
        path    (if nested? (->> coll (hiccup-path target-tag?) drop-last) [])
        target  (if (seq path) (get-in coll path) coll)]
    [path target]))

(defn gui
  ([args hiccup*]
   (gui args hiccup* nil) )
  ([args hiccup* decorator]
   (keyed hiccup* decorator)
   (let [[path target] (target-vector hiccup*)
         [tag attr*]   (->hiccup target)
         attr          (if (map? decorator)
                         (util/merge-with-style attr* decorator)
                         attr*)]
     #_(? (keyed path target tag attr* attr))
     (let [inner-span (let [x (last target)] (when (hiccup-span? x) x))
           node (merge-hiccup (keyed tag attr args inner-span))
           ret  (if (seq path) (assoc-in hiccup* path node) node)]
       ret))))


