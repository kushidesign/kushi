;; TODO use proper stubbing in par instead of this hack
(ns ^:dev/always kushi.parstub)

;; Temp stubs for par.core fns
(defn ? [& args] (last args))
(defn !? [& args] (last args))
(defn ?+ [& args] (last args))
(defn !?+ [& args] (last args))
(defn ?j [& args] (last args))
(defn !?j [& args] (last args))
