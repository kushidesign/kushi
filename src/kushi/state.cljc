(ns ^:dev/always kushi.state
  (:require
   [kushi.io :refer [load-edn]]
   [garden.core :as garden]
   [kushi.defs :as defs]
   [par.core :refer [? !? ?+ !?+]]
   [kushi.atomic :as atomic]
   [kushi.config :refer [user-config kushi-cache-path user-config-args-sx-defclass]]))

(def current-macro (atom nil))

(defn debug? [] (-> @current-macro :args first (= :p--2000px)))

(def current-sx (atom nil))

(defn set-current-macro!
  [{:keys [args form-meta kushi-attr macro]}]
  (let [opts        {:form-meta  form-meta
                     :bad-mods   {}
                     :fname      (name macro)
                     :kushi-attr kushi-attr
                     :ident      (:ident kushi-attr)}
        opts-w-args (assoc opts :args args)]
    (reset! current-macro opts-w-args)
    (reset! current-sx opts-w-args)
    opts))

(def warnings-js (atom []))

(def warnings-terminal (atom []))

(def compilation-warnings (atom []))

(def invalid-style-args (atom nil))

(def invalid-style-warnings (atom []))

;; Used to create user-classes.
(def kushi-atomic-user-classes
  (atom atomic/kushi-atomic-combo-classes))

(def ordered-defclasses
  (atom []))

(def declarations-init {:sx {}
                        :defkeyframes {}
                        :defclass {}})

(def declarations (atom declarations-init))

;; Used to keep track of unique prefix + ident combos.
(def defkeyframes-selectors (atom {}))

;; Used to keep track of atomic declarative classes which are used.
(def atomic-declarative-classes-used (atom []))

;; Used to keep track of defclasses used.
(def defclasses-used (atom []))

(def defclasses+atomics-used (atom {}))

;; Used to keep track of keyframe definitions which are used.
(def user-defined-keyframes (atom {}))

;; Used to keep track of @font-face declarations which are used.
(def user-defined-font-faces (atom []))

;; Used to keep track of global design tokens (css custom properties) which are added by themes or ala carte.
(def global-tokens (atom []))

;; Used to keep track of alias design tokens (css custom properties) which are added by themes or ala carte.
(def alias-tokens (atom []))

;; Used to keep track of design tokens (css custom properties) which are actually used.
(def used-tokens (atom []))

(defonce garden-vecs-state-init
  (reduce (fn [acc [k mq]] (assoc acc mq {}))
          {:rules {}}
          (:media user-config)))

;; Used to keep track of all theme override styles.
(def garden-vecs-state-theme (atom garden-vecs-state-init))

;; Used to keep track of all reusable base component styles.
(def garden-vecs-state-components (atom garden-vecs-state-init))

;; Used to keep track of all the component styles.
(def garden-vecs-state (atom garden-vecs-state-init))

;; Used to store output of stylesheet which will be inected at dev (and maybe prod) runtime
(def kushi-css-sync (atom nil))

;; Used to store the rules "to be printed"
(def kushi-css-sync-to-be-printed (atom nil))

(def cached-sx-rule-count (atom 0))

(defn add-global-token! [var]
  (swap! global-tokens conj var))

(defn add-alias-token! [var]
  (swap! alias-tokens conj var))

(defn add-used-token! [var]
  (swap! alias-tokens conj var))

(defn add-styles!
  ([coll type*]
   (let
    [state (case type*
             :theme garden-vecs-state-theme
             :ui garden-vecs-state-components
             garden-vecs-state)]
     (doseq [x coll]
       (if-let [{:keys [media-queries rules]}  (when (map? x) (:value x))]
         (let [new-val (apply conj (get @state media-queries) rules)]
           (swap! state assoc media-queries new-val))
         (swap! state assoc :rules (conj (:rules @state) x)))))))

;; TODO - refactor all this stuff into single atom?
(defn reset-build-states! []
  (reset! user-defined-keyframes {})
  (reset! user-defined-font-faces [])
  (reset! declarations declarations-init)
  (reset! garden-vecs-state garden-vecs-state-init)
  (reset! garden-vecs-state-components garden-vecs-state-init)
  (reset! garden-vecs-state-theme garden-vecs-state-init)
  (reset! global-tokens [])
  (reset! alias-tokens [])
  (reset! used-tokens [])
  (reset! kushi-atomic-user-classes atomic/kushi-atomic-combo-classes)
  (reset! ordered-defclasses [])
  (reset! atomic-declarative-classes-used [])
  (reset! defclasses-used [])
  (reset! defclasses+atomics-used {})
  (reset! cached-sx-rule-count 0))

(defonce styles-cache-current
  (let [styles-cache-disc (when (:__enable-caching?__ user-config)
                            (load-edn kushi-cache-path))]
    (atom (or styles-cache-disc {}))))

(defonce styles-cache-updated
  (atom @styles-cache-current))

(defn cached
  "Assuming the following user config:
   (def user-config {:__enable-caching__? true
                     :prefix              \"hi-\"})
   (cached :sx :p--10px {:id :foo})
   =>
   {:caching?  true
    :cache-key [:sx {... :prefix \"hi-\" ...} :p--10px {:id :foo}]
    :cached    {...}}"
  [k & more]
  (let [caching?  (:__enable-caching?__ user-config)
        cache-key (hash (apply conj [k user-config-args-sx-defclass] more))
        cached    (when caching? (get @styles-cache-updated cache-key))]
    {:caching?  caching?
     :cache-key cache-key
     :cached    cached}))


;; Does user want runtime injection?
(def rt-inj? (:runtime-injection? user-config))

;; Keep track whether we are in dev or prod build
(def KUSHIDEBUG (atom true))

(defn kushi-debug
  {:shadow.build/stage :compile-prepare}
  [build-state]
  #_(?+ {:shadow.build/stage :compile-prepare} "preparing to reset build states...")
  (reset-build-states!)
  #_(?+ "After reset...kushi-debug:garden-vecs-state" @state/garden-vecs-state)
  #_(?+ "After reset...kushi-debug:atomic-user-classes" @state/kushi-atomic-user-classes)
  #_(?+ "After reset...kushi-debug:atomic-declarative-classes-used" @state/atomic-declarative-classes-used)
  #_(?+ "After reset...kushi-debug:state/user-defined-keyframes" @state/user-defined-keyframes)
  #_(?+ "After reset...kushi-debug:state/user-defined-font-faces" @state/user-defined-font-faces)

  (let [mode (:shadow.build/mode build-state)]
    #_(when mode
        (?+ (? "(:shadow.build/mode build-state)") mode))
    (when (not= mode :dev)
      (reset! KUSHIDEBUG false)))
  build-state)
