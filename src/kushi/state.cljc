(ns ^:dev/always kushi.state
  (:require
   [kushi.io :refer [load-edn]]
   [kushi.atomic :as atomic]
   [kushi.config :refer [user-config kushi-cache-path user-config-args-sx-defclass]]))

(def current-macro (atom nil))

(def current-sx (atom nil))

(def compilation-warnings (atom []))

;; Used to create user-classes.
(def kushi-atomic-user-classes
  (atom atomic/kushi-atomic-combo-classes))

;; Used to keep track of unique prefix + ident combos.
(def prefixed-selectors (atom {}))

;; Used to keep track of atomic declarative classes which are used.
(def atomic-declarative-classes-used (atom #{}))

;; Used to keep track of keyframe definitions which are used.
(def user-defined-keyframes (atom {}))

;; Used to keep track of @font-face declarations which are used.
(def user-defined-font-faces (atom []))

;; Used to keep track of all the component styles.
(defonce garden-vecs-state-init
  (reduce (fn [acc [k mq]] (assoc acc mq {}))
          {:rules {}}
          (:media user-config)))

(def garden-vecs-state (atom garden-vecs-state-init))

(defn add-styles! [coll]
  (let [state garden-vecs-state]
    (doseq [x coll]
      (if-let [{:keys [media-queries rules]}  (when (map? x) (:value x))]
        (let [new-val (apply conj (get @state media-queries) rules)]
          (swap! state assoc media-queries new-val))
        (swap! state assoc :rules (conj (:rules @state) x))))))

(defn reset-build-states! []
  (reset! user-defined-keyframes {})
  (reset! user-defined-font-faces [])
  (reset! prefixed-selectors {})
  (reset! garden-vecs-state garden-vecs-state-init)
  (reset! kushi-atomic-user-classes atomic/kushi-atomic-combo-classes)
  (reset! atomic-declarative-classes-used #{}))

(defonce styles-cache-current
  (let [styles-cache-disc (when (:__enable-caching?__ user-config)
                            (load-edn kushi-cache-path))]
    (atom (or styles-cache-disc {}))))

(defonce styles-cache-updated
  (atom @styles-cache-current))

(defn cached [k & more]
  (let [caching?  (:__enable-caching?__ user-config)
        cache-key (when caching? (apply conj [k user-config-args-sx-defclass] more))]
    {:caching?  caching?
     :cache-key cache-key
     :cached    (when caching? (get @styles-cache-updated cache-key))}))
