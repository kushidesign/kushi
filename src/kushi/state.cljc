(ns ^:dev/always kushi.state
  (:require
   [clojure.pprint :refer [pprint]]
   [kushi.io :refer [load-edn]]
   [kushi.parstub :refer [? !? ?+ !?+]]
   [kushi.atomic :as atomic]
   [kushi.config :refer [user-config kushi-cache-path user-config-args-sx-defclass]]))

(def silence-warnings? (atom false))

(def user-config-args-sx-defclass-stub (atom nil))

(def current-op (atom nil))

(def current-macro (atom nil))

(defn debug? []
  (-> @current-op :args last :kushi-debug?)
  #_(let [first-arg (-> (? @current-macro) :args first)]
      (= (? first-arg) 'my-desired-classname)
      #_(or (= first-arg :bgc--$bgc) (and (map? first-arg)  (-> first-arg :style :--wtf)))))

(def current-sx (atom nil))

(defn set-current-macro!
  [{:keys [args form-meta kushi-attr macro]}]
  (let [opts        {:form-meta  form-meta
                     :bad-mods   {}
                     :fname      (name macro)
                     :kushi-attr kushi-attr}
        opts-w-args (assoc opts :args args)]
    (reset! current-macro opts-w-args)
    (reset! current-sx opts-w-args)
    opts))

(def warnings-js (atom []))

(def warnings-terminal (atom []))

(def compilation-warnings (atom []))

(def invalid-style-args (atom nil))

(def invalid-style-warnings (atom []))

(def utility-classes (atom {}))

(def utility-classes-by-classtype (atom {}))

(def utility-classes-used (atom {}))

(def utility-classes-used-by-classtype (atom {}))

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

;; NUKE
;; ;; Used to keep track of defclasses used.
;; (def defclasses-used (atom []))

(def defclasses+atomics-used (atom {}))

(defonce garden-vecs-state-init
  (reduce (fn [acc [k mq]] (assoc acc mq []))
          {:rules []}
          (:media user-config)))

;; Used to keep track of css-global resets which are used.
(def css-reset (atom {}))

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

;; Used to keep track of all theme override styles.
(def garden-vecs-state-theme (atom garden-vecs-state-init))

;; Used to keep track of all reusable base component styles.
(def garden-vecs-state-components (atom garden-vecs-state-init))

;; Used to keep track of all the component styles.
(def garden-vecs-state (atom garden-vecs-state-init))

;; Used to keep track of all the google fonts to be added.
(def google-font-maps (atom []))

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
  (swap! used-tokens conj var))

(defn add-reset-rules! [coll]
  (?+ coll))

(defn add-styles!
  [coll type*]
  (let
   [state (case type*
            :theme garden-vecs-state-theme
            :ui garden-vecs-state-components
            :reset css-reset
            garden-vecs-state)]
    ;; TODO throw error if duplicate? Maybe do this in stylesheet at end
    #_(when-let [kushi-class (-> @current-op :kushi-attr :kushi-class)]
      (?+ :add-styles @garden-vecs-state))
    (doseq [x coll]
      (if-let [{:keys [media-queries rules]}  (when (map? x) (:value x))]
        (let [new-val (apply conj (get @state media-queries) rules)]
          (swap! state assoc media-queries new-val))
        (swap! state assoc :rules (conj (:rules @state) x))))))

;; TODO - refactor all this stuff into single atom?
(defn reset-build-states! []
  ;; user-config-stub (used only for tests)
  (reset! user-config-args-sx-defclass-stub nil)
  ;; silence warnings (used only for tests)
  (reset! silence-warnings? false)

  ;; css-sync
  (reset! kushi-css-sync nil)

  ;; css-reset
  (reset! css-reset {})

  ;; tokens
  (reset! global-tokens [])
  (reset! alias-tokens [])
  (reset! used-tokens [])

  ;; fonts, animations
  (reset! user-defined-keyframes {})
  (reset! user-defined-font-faces [])
  (reset! google-font-maps [])

  ;; defclass-related
  (reset! utility-classes {})
  (reset! utility-classes-by-classtype {})
  (reset! utility-classes-used [])
  (reset! utility-classes-used-by-classtype {})


  ;; sx-related
  (reset! garden-vecs-state garden-vecs-state-init)
  (reset! garden-vecs-state-components garden-vecs-state-init)
  (reset! garden-vecs-state-theme garden-vecs-state-init)

  ;; what does this declarations do?
  (reset! declarations declarations-init)

  ;; NIX THIS ordered-defclasses thing?
  (reset! ordered-defclasses [])

  ;; maybe NIX all below
  (reset! kushi-atomic-user-classes atomic/kushi-atomic-combo-classes)
  (reset! atomic-declarative-classes-used [])
  ;; (reset! defclasses-used [])
  (reset! defclasses+atomics-used {})
  (reset! cached-sx-rule-count 0))

(defonce styles-cache-current
  (let [styles-cache-disc (when (:enable-caching? user-config)
                            (load-edn kushi-cache-path))]
    (atom (or styles-cache-disc {}))))

(defonce styles-cache-updated
  (atom @styles-cache-current))

(defn cached
  "Assuming the following user config:
   (def user-config {:enable-caching? false
                     :kushi-class-prefix  \"hi-\"})
   (cached :sx :p--10px {:id :foo})
   =>
   {:caching?  true
    :cache-key [:sx {... :kushi-class-prefix \"hi-\" ...} :p--10px {:id :foo}]
    :cached    {...}}"
  [k & more]
  (let [caching?  (if @user-config-args-sx-defclass-stub
                    false
                    (:enable-caching? user-config))
        user-config-args-sx-defclass (or @user-config-args-sx-defclass-stub user-config-args-sx-defclass)
        cache-key (hash (apply conj [k user-config-args-sx-defclass] more))
        cached    (when caching? (get @styles-cache-updated cache-key))]
    (!?+ :sym-meta (-> more first meta))
    (!?+ :hashing-on-this (apply conj [k user-config-args-sx-defclass] more))
    (!?+ :state/cached {:k k :nm (first more) :cached (when cached "...")})
    (!?+ :state/cached (when cached "..."))
    {:caching?  caching?
     :cache-key cache-key
     :cached    cached}))

(defn update-cache! [cache-map result]
  (let [{:keys [caching? cache-key cached]} cache-map]
    (when caching?
      (when-not cached
        (swap! styles-cache-updated assoc cache-key result)))))

(defn add-utility-class! [{:keys [defclass-name classtype] :as m}]
  (swap! utility-classes assoc defclass-name m)
  (swap! utility-classes-by-classtype assoc-in [classtype defclass-name] m))

(defn add-google-font-maps!
  [coll]
  (doseq [m coll] (swap! google-font-maps conj m)))

(defn k->utility-class [k]
  (or (k @utility-classes)
      ((-> k name (subs 1) keyword) @utility-classes)))

(defn register-utility-class-usage! [coll]
  (doseq [k coll]
    (when-let [{:keys [classtype] :as m} (k->utility-class k)]
      (!?+ m)
      (swap! utility-classes-used conj m)
      (let [coll* (get :classtype @utility-classes-used-by-classtype [])
            coll (conj coll* m)]
        (swap! utility-classes-used-by-classtype assoc classtype coll)))))

;; Does user want runtime injection?
(def rt-inj? (:runtime-injection? user-config))

;; Keep track whether we are in dev or prod build
(def KUSHIDEBUG (atom true))


