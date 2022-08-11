(ns ^:dev/always kushi.ui.icon.helper
  (:require-macros
   [kushi.core :refer (sx defclass)])
  (:require
   [kushi.parstub :refer-macros [!? ?]]
   [kushi.ui.icon.mui.core :refer (mui-icon mui-icon-outlined mui-icon-round mui-icon-two-tone mui-icon-sharp)]
  ;;  [kushi.ui.icon.mui.outlined :refer (mui-icon-outlined)]
  ;;  [kushi.ui.icon.mui.round :refer (mui-icon-round)]
  ;;  [kushi.ui.icon.mui.two-tone :refer (mui-icon-two-tone)]
  ;;  [kushi.ui.icon.mui.sharp :refer (mui-icon-sharp)]
   ))

(defclass kushi-icon-inline-start :mie--:--icon-enhancer-inline-gap-ems)
(defclass kushi-icon-inline-end :mis--:--icon-enhancer-inline-gap-ems)

(defn icon-component [{:keys [mi icon-style icon-position]}]
  (let [component-class (when mi
                          (if (= icon-position :inline-end) 'kushi-icon-inline-end 'kushi-icon-inline-start) )
        icon-component* (case icon-style
                          :outlined mui-icon-outlined
                          :round mui-icon-round
                          :two-tone mui-icon-two-tone
                          :sharp mui-icon-sharp
                          mui-icon)
        icon-component  [icon-component* {:class [component-class]} mi]]
    icon-component))
