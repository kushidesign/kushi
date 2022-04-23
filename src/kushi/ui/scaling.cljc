(ns kushi.ui.scaling)

(defn icon-margin [k]
 (str "calc(var(--text-" (name k) ") / var(--mui-icon-spacing-factor))"))

(defn icon-font-size [k]
 (str "calc(var(--text-" (name k) ") * var(--mui-icon-scaling-factor))"))

