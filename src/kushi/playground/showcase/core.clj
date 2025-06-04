(ns kushi.playground.showcase.core
  (:require 
   [clojure.string]
   [clojure.repl]
   [kushi.css.build.design-tokens
    :refer [design-tokens-by-component-usage]]))

(defmacro fqns-sym [sym]
  (when (symbol? sym)
    `(let [m# (-> ~sym var meta)]
       (symbol (str (:ns m#) "/" (:name m#))))))

(defmacro fqns-sym+file-info [sym]
  (when (symbol? sym)
    `(let [m# (-> ~sym var meta)]
       (symbol (str (:ns m#) "/" (:name m#) "  " (:line m#) ":" (:column m#) )))))


(defmacro uic-showcase-map
  [sym]
  (let [uic-ns (-> sym str (clojure.string/split #"/") first symbol)
        toks   (get design-tokens-by-component-usage uic-ns)]
    `(let [{uic-name#   :name
            [_# opts#]  :opts 
            [_# demos#] :demos
            :as         m#}
           (-> ~sym var meta)]
      ;;  (fireworks.core/? m#)
       {:demos        demos#
        :opts         (apply array-map opts#)
        :toks         ~toks
        :reqs-for-uic (->> ~uic-ns
                           clojure.repl/dir
                           with-out-str
                           clojure.string/split-lines
                           (mapv symbol)
                           (vector (quote ~uic-ns) :refer)
                           vector)
        :fq-uic-name  (quote ~sym)
        :uic-name     uic-name#
        ;; change to uic-fn
        :component-fn ~sym})))
