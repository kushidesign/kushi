(ns ^:dev/always kushi.state
  (:require
   [kushi.atomic :as atomic]
   [kushi.config :refer [user-config]]))

(def current-macro (atom nil))

(def current-sx (atom nil))

(def compilation-warnings (atom []))

;; Used to create user-classes.
(def kushi-atomic-user-classes
  (atom atomic/kushi-atomic-combo-classes))

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
  (reset! garden-vecs-state garden-vecs-state-init)
  (reset! kushi-atomic-user-classes atomic/kushi-atomic-combo-classes)
  (reset! atomic-declarative-classes-used #{}))

(def styles-cache (atom {}))
