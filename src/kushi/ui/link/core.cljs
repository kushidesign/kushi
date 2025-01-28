(ns kushi.ui.link.core
  (:require [kushi.core :refer (css merge-attrs)]
            [kushi.ui.core :refer (opts+children)]))

(defn link
  "Desc for"
  [& args]
  (let [[opts attrs & children] (opts+children args)
        {:keys []}              opts]
    (into [:a
           (merge-attrs
            {:class (css ".kushi-link"
                         {:cursor    :pointer
                          :td        :underline
                          ;; TODO - On older browsers, (older iPhones) this won't work.
                          ;; See if lightning css can fix this...
                          ;; Otherwise, it defaults to currentColor, so NBD.
                          :tup       :under
                          :tdc       "color-mix(in oklch, currentColor 40%, transparent)"
                          :hover:tdc :currentColor}
                         )
             :data-kushi-ui :link}
            attrs)]
          children)))
