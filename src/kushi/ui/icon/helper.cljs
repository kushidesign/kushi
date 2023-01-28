(ns ^:dev/always kushi.ui.icon.helper
  (:require
   [kushi.ui.icon.mui.core :refer (mui-icon mui-icon-outlined mui-icon-round mui-icon-two-tone mui-icon-sharp)]))


(defn icon-component
  [{:keys [mi mui-icon-style icon-svg]}]
  (when mi
    (let [icon-component* (case mui-icon-style
                            :outlined mui-icon-outlined
                            :round mui-icon-round
                            :two-tone mui-icon-two-tone
                            :sharp mui-icon-sharp
                            mui-icon)
          icon-component  [icon-component*
                           {:-icon-svg icon-svg}
                           mi]]
      icon-component)))
