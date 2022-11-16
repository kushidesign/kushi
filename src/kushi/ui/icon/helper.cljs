(ns ^:dev/always kushi.ui.icon.helper
  (:require-macros
   [kushi.core :refer (defclass)])
  (:require
   [kushi.ui.icon.mui.core :refer (mui-icon mui-icon-outlined mui-icon-round mui-icon-two-tone mui-icon-sharp)]))

(defclass kushi-icon-inline-start :mie--:--icon-enhancer-inline-gap-ems)
(defclass kushi-icon-inline-end :mis--:--icon-enhancer-inline-gap-ems)

(defn icon-component [{:keys [mi mui-icon-style icon-position no-margins?]}]
  (when mi
    (let [component-class (when (and mi (not no-margins?))
                            (if (= icon-position :inline-end) 'kushi-icon-inline-end 'kushi-icon-inline-start))
          icon-component* (case mui-icon-style
                            :outlined mui-icon-outlined
                            :round mui-icon-round
                            :two-tone mui-icon-two-tone
                            :sharp mui-icon-sharp
                            mui-icon)
          icon-component  [icon-component* {:class [component-class]} mi]]
      icon-component)))
