(ns kushi.ui.link
  (:require [kushi.core :refer (sx merge-attrs)]
            [kushi.ui.core :refer (defui)]))

(defui link
  {:desc         "A link is a wrapper for an anchor tag."
   :props/shared [:size :position :transition]}
  [& args]
  (into [:a
         (merge-attrs
          {:data-ks-surface :transparent}
          (sx "[data-ks-ui=\"link\"]"
              {:td        :underline
              ;; TODO - On older browsers, (older iPhones) this won't work.
              ;; See if lightning css can fix this...
              ;; Otherwise, it defaults to currentColor, so NBD.
               :tup       :under
               :tdc       "color-mix(in oklch, currentColor 40%, transparent)"
               :hover:tdc :currentColor})
          &attrs)]
        &children))
