(ns kushi.css.media)

(def default-kushi-responsive
  [:xsm {:min-width :480px}
   :sm {:min-width :640px}
   :md {:min-width :768px}
   :lg {:min-width :1024px}
   :xl {:min-width :1280px}
   :xxl {:min-width :1536px}])

(defn media* [vc] (apply array-map vc))

(def media (media* default-kushi-responsive))

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

(def breakpoints (atom media))

(defn m->hydrated-mq [m]
  (let [[k v] (first m)]
    (str "@media(" (name k) ": " (name v) ")")))

(defn hydrated-breakpoints* [m]
  (->> media
       (mapv (fn [[_ m]]
               (m->hydrated-mq m)))))

(def hydrated-breakpoints
  (atom (hydrated-breakpoints* media)))


;; This is how the user resets the breakpoints
(defn reset-breakpoints! [vc]
  (let [m (media* vc)]
    (reset! breakpoints m)
    (reset! hydrated-breakpoints (hydrated-breakpoints* m))))
