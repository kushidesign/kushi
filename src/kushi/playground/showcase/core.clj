(ns kushi.playground.showcase.core
  (:require 
   [fireworks.core :refer [? !? ?> !?>]]
   [clojure.string]
   [clojure.repl]
   [edamame.core :as e]
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
        toks   (get design-tokens-by-component-usage uic-ns)
        demos  (symbol (str uic-ns "/demos"))]
    `(let [{uic-name#   :name
            [_# opts#]  :opts 
            ;; [_# demos#] :demos
            ;; demos#      (symbol (str uic-ns "/" demo))
            :as         m#}
           (-> ~sym var meta)]
       {
        ;; :demos        ~demos
        :opts         (apply array-map opts#)
        ;; :toks gets design tokens by component usage, these toks are used for
        ;; constructing the docs for each component
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


(defmacro uic-showcase-map2
  ([f]
   (let [[uic-ns-name uic-name] (-> f str (clojure.string/split #"/"))
         uic-ns-sym             (symbol uic-ns-name)
         toks                   (get design-tokens-by-component-usage
                                     uic-ns-sym)]
     `(do 
        {
         ;;  :demos        ~demos
         :component-fn ~f
         :opts         (-> ~f var meta :opts)
         :uic-name     ~uic-name
         :uic-sym      (symbol ~uic-name)
         :toks         ~toks
         :fq-uic-name  (str ~uic-ns-name "/" ~uic-name)

         ;; reqs-for-uic is a snippet for reqs consisting of all the public
         ;; vars in the component namespaces. If you don't want `def` forms
         ;; from the component namespace to be included, they must be defined
         ;; like this:
         ;; (def ^{:private true} foo 1)
         
         :reqs-for-uic (->> ~uic-ns-sym
                            clojure.repl/dir
                            with-out-str
                            clojure.string/split-lines
                            (mapv symbol)
                            (vector (quote ~uic-ns-sym) :refer)
                            vector)})))
  ([f demos]
   (let [[uic-ns-name uic-name] (-> f str (clojure.string/split #"/"))
         uic-ns-sym             (symbol uic-ns-name)
         toks                   (get design-tokens-by-component-usage
                                     uic-ns-sym)]
     `(do 
        {:demos        ~demos
         :component-fn ~f
         :opts         (-> ~f var meta :opts)
         :uic-name     ~uic-name
         :uic-sym      (symbol ~uic-name)
         :toks         ~toks
         :fq-uic-name  (str ~uic-ns-name "/" ~uic-name)

         ;; reqs-for-uic is a snippet for reqs consisting of all the public
         ;; vars in the component namespaces. If you don't want `def` forms
         ;; from the component namespace to be included, they must be defined
         ;; like this:
         ;; (def ^{:private true} foo 1)
         
         :reqs-for-uic (->> ~uic-ns-sym
                            clojure.repl/dir
                            with-out-str
                            clojure.string/split-lines
                            (mapv symbol)
                            (vector (quote ~uic-ns-sym) :refer)
                            vector)}))))
