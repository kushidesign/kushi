(ns kushi.showcase.core
  (:require 
   [fireworks.core :refer [? !? ?> !?>]]
   [clojure.string :as string]
   [clojure.repl]
   [kushi.util :refer [keyed]]
   [kushi.css.build.design-tokens
    :refer [design-tokens-by-component-usage]]))


(defmacro fqns-sym+file-info [sym]
  (when (symbol? sym)
    `(let [m# (-> ~sym var meta)]
       (symbol (str (:ns m#) "/" (:name m#) "  " (:line m#) ":" (:column m#))))))


(defn ui-demo-samples-partioned [samples]
  (and (even? (count samples))
       (let [partitioned
             (partition 2 samples)]
         (when (every? (fn [[label]]
                         (string? label))
                       partitioned)
           partitioned))))


(defmacro samples [vc]
  (let [samples vc
        m*      (fn [sample label]
                  (merge {:code/evaled sample
                          :code/quoted (list 'quote sample)}
                         (when label {:label label})))
        ret     (if-let [partitioned 
                         (ui-demo-samples-partioned samples)]
                  (mapv (fn [[label sample]] (m* sample label))
                        partitioned)
                  (mapv (fn [sample] (m* sample nil))
                        samples))]
    (!? {:display-metadata? false} ret)
    `(with-meta ~ret {:kushi.ui.showcase/demo true})))


(defmacro samples-with-template
  [{:keys [attrs
           :attrs/display
           :attrs/snippets
           args]
    :as m}]
  (let [ret m]
    `(with-meta ~ret {:kushi.ui.showcase/demo-with-template true})))

(defmacro samples-with-variant
  [{:keys [variant
           variant2
           variant3
           variant-labels?
           variant-scale
           attrs
           :attrs/display
           :attrs/snippets
           args]
    :as m}]
  (let [ret m]
    `(with-meta ~ret {:kushi.ui.showcase/opt true})))

(defmacro opts
  [fq-uic-sym demos-sym]
  (let [[uic-ns-name uic-name] (-> fq-uic-sym str (clojure.string/split #"/"))
        uic-sym       (symbol uic-name)
        uic-ns-sym    (symbol uic-ns-name)
        toks          (get design-tokens-by-component-usage uic-ns-sym)
        fq-uic-name   (str fq-uic-sym)]
    
    ;; (? (keyed [toks uic-ns-name uic-name uic-ns-sym fq-uic-sym fq-uic-name]))
    
    `(let [
          ;;  opts# (->> ~demos-sym 
          ;;             (filter :opt)
          ;;             (reduce (fn [acc# m#]
          ;;                       (assoc acc# (:opt m#) (dissoc m# :opt)))
          ;;                     {}))
           opts# (->> ~demos-sym 
                       (keep (fn [m#] 
                                 (when (-> m# :samples meta :kushi.ui.showcase/opt)
                                   {:opt-key (-> m# :samples :variant)
                                    :demo (merge (dissoc (:samples m#) :variant)
                                                 (dissoc m# :samples))})))
                       (into []))
           
           demos# (->> ~demos-sym 
                        (keep (fn [m#] 
                                (cond
                                  (-> m# :samples meta :kushi.ui.showcase/demo-with-template)
                                  (merge (:samples m#) (dissoc m# :samples))
                                  (-> m# :samples meta :kushi.ui.showcase/demo)
                                  m#)))
                       (into []))
           ]
       (!? demos#)
       {:demos        (!? demos#)
        :opts         opts#
        :fq-uic-name  ~fq-uic-name
        :component-fn ~fq-uic-sym
        :uic-name     ~uic-name
        :uic-sym      (quote ~uic-sym)
        :toks         ~toks
        :reqs-for-uic (->> ~uic-ns-sym
                           clojure.repl/dir
                           with-out-str
                           clojure.string/split-lines
                           (mapv symbol)
                           (vector (quote ~uic-ns-sym) :refer)
                           vector)})))
