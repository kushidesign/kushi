(ns ^:dev/always kushi.playground.showcase.shared 
  (:require
   [clojure.repl]
   [kushi.core :refer [sx]]
   [kushi.css.media]))

(defn section-label [s]
  [:p 
   (sx :.example-section-label
       :ff--$serif-font-stack
       :font-style--oblique
      ;; Include this if using cormorant serif face in :$serif-font-stack
      ;;  :.cormorant-section-label
      ;; Comment fs below if using cormorant serif face in :$serif-font-stack
      ;;  :fs--$small
       :fs--$small-b
       :.oblique
       :font-weight--$normal
       :.neutralize-secondary
       :lh--1.7
       :_span.code:mis--0.5ch)
   s])

