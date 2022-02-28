(ns ^:dev/always kushi.gui
  (:require-macros [kushi.core :refer [keyed]])
  (:require [clojure.string :as string]
            [par.core :refer [? !? ?+ !?+]]
            [kushi.utils :as util]))

(defn hiccup? [x]
  (and (vector? x) (-> x first keyword?)))

(defn split-key [k re] (-> k name (string/split re)))

(defn hiccup-tag [k]
  (let [tagstr  (name k)
        target? (some? (re-find #".+\:\!$" tagstr))
        tag     (if target?
                  (!?+ (-> tagstr (string/replace #":\!$" "") keyword))
                  k)]
    (when target? (!?+ :hiccup-tag (keyed tagstr tag target?)))
    [tag target?]))

(defn target-tag? [k]
  (when (keyword k)
    (!? :target-tag? {:k k :ret (-> k hiccup-tag second)})
    (-> k hiccup-tag second)))

(def kushi-keys
  [:css :ident :data-cljs :element :prefix :ancestor])

(defn ->hiccup [x]
  (when (hiccup? x)
    (let [[tag* attr*] x
          [tag _]      (hiccup-tag tag*)
          attr         (when (map? attr*) attr*)]
      [tag attr])))

(defn merge-hiccup [tag attr args]
  (let [user-attr   (when (map? (first args)) (first args))
        children*   (if user-attr (rest args) args)
        nested?     (some-> children* first seq?)
        children*   (if nested? (first children*) children*)
        children    (map-indexed (fn [idx x] ^{:key (str (hash x) "-" idx)} x) children*)
        merge?      (and user-attr (map? attr))
        attr-merged (if merge?
                      (util/merge-with-style attr (first args))
                      attr)]
    (!? :coll? (coll? children))
    (into [] (remove nil? (concat [tag attr-merged] children)))))

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
  ([hiccup*]
   (gui hiccup* nil) )
  ([hiccup* decorator]
   (!? (keyed hiccup* decorator))
   (let [[path target] (target-vector hiccup*)
         [tag attr*]   (->hiccup target)
         attr          (if (map? decorator)
                         (util/merge-with-style attr* decorator)
                         attr*)]
     (!? (keyed path target tag attr* attr))
     (fn [& args]
       (let [node (merge-hiccup tag attr args)
             ret  (if (seq path) (assoc-in hiccup* path node) node)]
         ret)))))

;; (def complex (gui [:section {} [:div [:span "wtf"]] [:div [:p.foo:! {:style {:color :red} :class [:wtf]} "hi"]]]))

;; (def simple (gui [:a "hi"]))

;; (!? (simple "hi" "bye"))
;; (?+ :complex (complex {:style {:color :blue :padding :10px} :class [:foo]} "hi" "bye"))

