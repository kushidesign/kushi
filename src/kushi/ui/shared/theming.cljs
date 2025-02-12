(ns kushi.ui.shared.theming
  (:require 
   [kushi.ui.icon.core]
   [kushi.ui.util :refer [as-str maybe nameable?]]
   [clojure.string :as string])
  )

(def variants
  {:shape    #{"rounded" "sharp" "pill"}
   :surface  #{"minimal" "outline" "solid" "soft"}
   ;; :semantic #{"neutral" "accent" "positive" "negative" "warning"}
   :colorway #{"accent" "positive" "negative" "warning"}
   })

(def variant-defaults
  {:colorway nil
   :surface  "soft"
   :shape    "rounded"})

(defn data-kui- [x k]
  (some-> x
          as-str
          #_(maybe (get variants k nil))
          (->> (hash-map (keyword (str "data-kui-" (name k)))))))

;; (def color-mix-support? (? (.supports js/window.CSS "(color: color-mix(in oklch, red, transparent)")))
;; (def oklch-support? (? (.supports js/window.CSS "(color: oklch(40.1% 0.123 21.57))")))

(defn get-variants [opts]
  (reduce-kv (fn [acc k v]
               (assoc acc
                      k
                      (or (some-> k
                                  opts
                                  (maybe nameable?)
                                  as-str
                                  (maybe (k variants)))
                          v)))
             {}
             variant-defaults))


(defn- valid-hue? [x]
  (and (number? x) (<= 0 x 360)))

(defn hue-style-map [x]
  (when-let [v (or (when-let [s (some-> x (maybe nameable?) as-str)]
                     (or (when (string/starts-with? s "$")
                           (str "var(--" (subs s 1) ")"))
                         (maybe s #(re-find #"^var\(--.+\)$" %))))
                   (valid-hue? x)
                   (some-> x (maybe nameable?) js/parseInt valid-hue?))]
    {:style {"--_hue" v}}))


