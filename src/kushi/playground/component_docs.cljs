(ns kushi.playground.component-docs
  (:require
   [clojure.string :as string]
   [clojure.walk :as walk]
   [kushi.core :refer (sx ?sx css-vars-map css ?css)]
   [kushi.playground.md2hiccup :refer [desc->hiccup]]
   [kushi.ui.label.core :refer [label]]
   [me.flowthing.pp :as pp]))


(defn add-links
  ([coll]
   (add-links coll nil))
  ([coll f]
   (walk/postwalk #(if (and (map? %) (contains? % :href))
                     (merge (assoc %
                                   :target 
                                   (if (string/starts-with? (:href %) "#")
                                     :_self
                                     :_blank)
                                   :class [:kushi-link])
                            (when f (f %)))
                     %)
                  coll)))


(defn kushi-opts-grid-desc [v m]
  (into [:span
         ;; TODO - This should be extracted for styles
         (sx :.kushi-ui-opt-desc
             :.normal
             :m--0
             :fs--$medium
             :lh--1.55
             :_code:lh--1.9
             :_code:pb--0.07em
             :_code:pi--0.2em
             :>span:d--block)]
        (desc->hiccup v)))



(defn kushi-opts-grid-default [v m]
  (if (and (list? v) (= :text (first v)) (string? (second v)))
    [kushi-opts-grid-desc (second v) m]
    [:span.code
     (str
      (cond (nil? v)
            "nil"
            (string? v)
            (str "\"" v "\"")
            :else
            v))]))

(defn kushi-opts-grid-type [v]
  (when v
    (cond
      (and (list? v) (= (first v) 'fn*))
      (let [anon-fn-display* (nth v 2)
            anon-fn-display (walk/postwalk
                             #(if (re-find #"^p[0-9]+__[0-9]+\#$"
                                           (str %))
                                (symbol "%")
                                %)
                             anon-fn-display*)]
        [:span.code (str "#" anon-fn-display)])
      (set? v)
      ;; TODO need to format sets here
      [:span.code v #_(with-out-str (pp/pprint v {:max-width 20}))]
      (symbol? v)
      [:span.code (name v)])))


(defn opt-detail [text v f kw]
  [:div
   (let [ai (if (= text "Desc.") :flex-start :center)]
     {:style (css-vars-map ai)
      :class (css :.flex-row-fs :align-items--$ai)})
   [:div
    (sx :.kushi-opt-detail-label :min-width--75px)
    [label (sx :.kushi-playground-meta-desc-label
               :.normal
               :>.kushi-label:lh--2.05) 
     text]]
   [:div (sx :.kushi-opt-detail-value
             [:_.code {:pb :0.07em
                       :pi :0.2em}])
    [f v]]])
