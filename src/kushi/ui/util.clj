(ns kushi.ui.util
  (:require [kushi.css.defs]))

(let [transforms {:keys keyword
                  :strs str
                  :syms identity}]
  (defmacro ^:public keyed
    "Create a map in which, for each symbol S in vars, (keyword S) is a
       key mapping to the value of S in the current scope. If passed an optional
     :strs or :syms first argument, use strings or symbols as the keys."
    ([vars] `(keyed :keys ~vars))
    ([key-type vars]
     (let [transform (comp (partial list `quote)
                           (transforms key-type))]
       (into {} (map (juxt transform identity) vars))))))
