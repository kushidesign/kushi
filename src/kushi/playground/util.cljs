(ns kushi.playground.util
  (:require
   [markdown-to-hiccup.core :as md->hc]
   [clojure.string :as string]))

(def kushi-github-url  "https://github.com/kushidesign/kushi")
(def kushi-clojars-url "https://clojars.org/design.kushi/kushi")

(defn meta->fname [m*]
  (-> m* meta :name str))

(defn anon-fn-syntax [s]
  (string/replace s #"\(fn\* \[" "(fn [e"))

(defn formatted-code [s]
  [:pre
   [:code {:class :language-clojure
           :style {:white-space :pre
                   :line-height 1.5}}
    (anon-fn-syntax s)]])

(defn kushi-component-desc->md [coll]
  (string/replace (string/join " "
                               (map (fn [x] (if (= :br x) "____br____" x)) coll))
                  #"____br____"
                  "<br>" ))

(defn md->component [v]
  (-> v
      md->hc/md->hiccup
      md->hc/component))

(defn desc->hiccup [coll]
  (some->> coll
           kushi-component-desc->md
           md->component))
