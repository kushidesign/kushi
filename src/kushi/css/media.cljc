(ns kushi.css.media)

(def default-kushi-responsive
  [:xsm {:min-width :480px}
   :sm {:min-width :640px}
   :md {:min-width :768px}
   :lg {:min-width :1024px}
   :xl {:min-width :1280px}
   :xxl {:min-width :1536px}])

(def media (apply array-map default-kushi-responsive))

(def media-nsqkw
  (apply array-map 
         (reduce-kv (fn [acc k v]
                      (conj acc
                            (->> k
                                 name 
                                 (str "at-media/") 
                                 keyword)
                            v))
                    [] 
                    media)))

(def media+ (merge media media-nsqkw))

(def index-by-media-query
  (into {}
        (map-indexed (fn [i [k _]]
                       [k i])
                     media)))
