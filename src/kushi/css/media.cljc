(ns kushi.css.media)

(def default-kushi-responsive
  [:xsm {:min-width :480px}
   :sm {:min-width :640px}
   :md {:min-width :768px}
   :lg {:min-width :1024px}
   :xl {:min-width :1280px}
   :xxl {:min-width :1536px}])

(def media (apply array-map default-kushi-responsive))

(def index-by-media-query
  (into {}
        (map-indexed (fn [i [k _]]
                       [k i])
                     media)))

(def index-by-css-media-query
  "{\"@media(min-width: 480px)\" 0
    \"@media(min-width: 640px)\" 1
    ...}"
  (into {}
        (map-indexed (fn [i [_ m]]
                       (let [[k v] (first m)]
                         [(str "@media(" (name k) ": " (name v) ")") i]))
                     media)))

(def css-media-queries
  (into #{} (keys index-by-css-media-query)))
