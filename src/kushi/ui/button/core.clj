(ns kushi.ui.button.core)

(defmacro buttonx [& args]
  (let [v (into ['kushi.ui.button.core/buttonx*
                 {:form-meta (meta &form)
                  :form      (str &form)}]
                args)]
    `~v))
