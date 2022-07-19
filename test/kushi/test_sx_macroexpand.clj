(ns kushi.test-sx-macroexpand
  (:require
   [clojure.string :as string]
   [clojure.test :as test :refer [is testing deftest]]
   [garden.core :as garden]
   [clojure.pprint :refer [pprint]]
   [kushi.core :refer (sx-dispatch sx cssfn)]
   [kushi.state :as state]
   [kushi.config :as config]))


(deftest dynamic-values
  (let [mycolor :red]
    (is
     (= (sx :c--$mycolor)
        {:data-cljs ":15:9" ;; IF YOU MOVE THIS TEST, CHANGE THIS :data-cljs TO MATCH LINE + COL
         :class     '("_2008909213")
         :style     {"--mycolor" :red}}))))

(deftest with-multiple-properties
  (let [wtf  "10px"
        wtf2 "20px"]
    (is (= (sx :.absolute
               :c--black
               :ta--c
               :pis--$wtf
               :pie--:--my-pie
               {:class [:dull]
                :style {:margin-bottom wtf2}
                :id    :foo})
           {:id        :foo,
            :data-cljs ":23:12", ;; IF YOU MOVE THIS TEST, CHANGE THIS :data-cljs TO MATCH LINE + COL
            :class     '("_72952793" "dull" "absolute"),
            :style     {"--wtf"  "10px"
                        "--wtf2" "20px"}}))))
