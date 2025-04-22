(ns ^:dev/always kushi.playground.showcase.shared 
  (:require
   [clojure.repl]
   [clojure.string :as string]
   [me.flowthing.pp :refer [pprint]]
   [kushi.core :refer [sx]]
   [kushi.css.media]))

(defn section-label [s]
  [:p 
   (sx :.example-section-label
       :ff--$serif-font-stack
      ;;  :font-style--oblique
      ;; Include this if using cormorant serif face in :$serif-font-stack
      ;;  :.cormorant-section-label
      ;; Comment fs below if using cormorant serif face in :$serif-font-stack
       :fs--$small
       :fs--$small-b
      ;;  :.oblique
      ;;  :font-weight--$normal
       :font-weight--$wee-bold
      ;;  :font-weight--$light
      ;;  :.neutralize-secondary
       :lh--1.7
       :_span.code:mis--0.5ch
       
       :ff--$sans-serif-font-stack
      ;;  :font-style--normal
       )
   s])

(defn pprint-str [x max-width]
  (-> x
      (pprint {:max-width max-width})
      with-out-str
      (string/replace #"\n$" "")
      (string/replace #",\n" "\n")
      (string/replace #", :" " :")))
