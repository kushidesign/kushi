(ns kushi.ui.button.core)

(defmacro big-paw* [& args]
  (let [v (into ['kushi.ui.button.core/big-paw
                 ^:kushi.ui/form
                 {:form-meta (meta &form)
                  :form      (str &form)}]
                args)]
    `~v))
