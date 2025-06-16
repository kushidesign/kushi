(ns kushi.ui.shared.theming
  (:require 
   [kushi.ui.defs :as defs]
   [kushi.ui.util :refer [as-str maybe nameable?]]
   [clojure.string :as string]))

(def variants
  {:contour    #{"rounded" "sharp" "pill" "circle"}
   :surface  defs/basic-surfaces-set-of-strs
   ;; :semantic #{"neutral" "accent" "positive" "negative" "warning"}
   :colorway #{"neutral"
               "accent"
               "positive"
               "negative"
               "warning"
               "gray"
               "purple"
               "blue"
               "green"
               "lime"
               "yellow"
               "gold"
               "orange"
               "red"
               "magenta"
               "brown"}
   :weight   defs/basic-weights-set-of-strs
   :size     defs/basic-sizes-set-of-strs})

(def variant-defaults
  {:colorway "neutral"
   :surface  "faint"
   :contour    "rounded"})

(def variant-basics (into #{} (keys variants)))

;; When it needs to be {:data-ks-end-enhancer ""}
(def data-ks-blanks 
  #{:end-enhancer :start-enhancer})

(defn maybe-blank [x k]
  (if (contains? data-ks-blanks k) "" x))

(defn data-ks- [x k]
  (some-> x
          (maybe-blank k)
          as-str
          (->> (hash-map (keyword (str "data-ks-" (name k)))))))

(defn component-attrs [s opts & colls]
  (merge (data-ks- s :ui)
         (reduce
          (fn [m k]
            (merge m (data-ks- (k opts) k))) 
          {}
          (apply concat colls))))

;; (def color-mix-support? (? (.supports js/window.CSS "(color: color-mix(in oklch, red, transparent)")))
;; (def oklch-support? (? (.supports js/window.CSS "(color: oklch(40.1% 0.123 21.57))")))

(defn get-variants
  ([opts]
   (get-variants opts nil))
  ([opts defaults]
   (reduce-kv (fn [acc k v]
                (assoc acc
                       k
                       (or (some-> opts
                                   (get k)
                                   (maybe nameable?)
                                   as-str
                                   (maybe (k variants)))
                           v)))
              {}
              (merge variant-defaults defaults))))


(defn- valid-hue? [x]
  (and (number? x) (<= 0 x 360)))

;; unused for now
(defn hue-style-map [x]
  (when-let [v (or (when-let [s (some-> x (maybe nameable?) as-str)]
                     (or (when (string/starts-with? s "$")
                           (str "var(--" (subs s 1) ")"))
                         (maybe s #(re-find #"^var\(--.+\)$" %))))
                   (valid-hue? x)
                   (some-> x (maybe nameable?) js/parseInt valid-hue?))]
    {:style {"--_hue" v}}))


