(ns kushi.playground.component-docs
  (:require
   [clojure.walk :as walk]
   [kushi.playground.util :as util]
   [kushi.core :refer (sx)]
   [kushi.ui.label.core :refer [label]]
   [markdown-to-hiccup.core :as md->hc]))

(defn add-links [coll]
  (walk/postwalk #(if (and (map? %) (contains? % :href))
                    (assoc % :target :_blank :class [:kushi-link])
                    %)
                 coll))

(defn kushi-opts-grid-desc [v m]
  [:span
   (sx :.kushi-ui-opt-desc
       :.normal
       :&_p:m--0
       :&_p:fs--$medium
       :&_p:lh--1.5)
   (let [ret* (cond
                (string? v)
                (->> v md->hc/md->hiccup md->hc/component)

                (coll? v)
                (some->> v util/desc->hiccup)

                :else
                [:span])]
     (add-links ret*))])

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
      [:span.code (str v)]
      (symbol? v)
      [:span.code (name v)])))

(defn opt-detail [text v f kw]
  [:div
   (sx :.flex-row-fs
       :pb--0.5em
       {:style {:ai (if (= text "Desc.") :flex-start :center)}})
   [:div
    (sx 'kushi-opt-detail-label :min-width--75px)
    [label (sx :.kushi-playground-meta-desc-label
               :.normal
               :&>.kushi-label:lh--2.05) 
     text]]
   [:div (sx 'kushi-opt-detail-value
             [:&_.code {:pb       :0.07em
                        :pi       :0.2em
                        :fs       :0.85rem
                        :c        :$accent-750
                        :bgc      :$accent-50
                        :dark:c   :$accent-100
                        :dark:bgc :$accent-900}])
    [f v]]])