(ns ^:dev/always kushi.state2
  (:require
[par.core :refer [!? ?]]
   [clojure.data :as data]
   [kushi.io :refer [load-edn]]
   [clojure.java.io :refer [make-parents]]
   [kushi.reporting :as reporting]
   [kushi.defs :as defs]
   [kushi.config :refer [user-config
                         version*
                         user-config-args-sx-defclass]]))

;; Keep track whether we are in dev or prod build
(def KUSHIDEBUG (atom true))
(def shadow-build-id (atom nil))
(def initial-build? (atom true))
(def trace?* (atom false))
(def *trace-mode? (atom false))

(def css (atom []))
(def ->css (atom nil))
(def shared-classes (atom {}))
(def user-defined-keyframes (atom {}))
(def user-defined-font-faces (atom []))
(def google-font-maps (atom []))
(def design-tokens (atom []))
(def theming-tokens (atom []))
(def used-tokens (atom []))
(def ->css-to-be-printed-previously (atom nil))
(def ->css-to-be-printed (atom (into {}
                                     (map (fn [k] [k nil])
                                          defs/rule-type-report-order))))

(defn reset-build-states! []
  (reset! shadow-build-id nil)
  (reset! css [])
  (reset! ->css nil)
  (reset! shared-classes {})
  (reset! user-defined-keyframes {})
  (reset! user-defined-font-faces [])
  (reset! google-font-maps [])
  (reset! design-tokens [])
  (reset! theming-tokens [])
  (reset! used-tokens [])
  (reset! initial-build? false))

(defn add-design-token! [var]
  (swap! design-tokens conj var))

(defn add-used-token! [var]
  (swap! used-tokens conj var))

(defn add-theming-token! [var]
  (swap! theming-tokens conj var))

(defn add-google-font-maps!
  [coll]
  (doseq [m coll] (swap! google-font-maps conj m)))


;; caching ----------------------------------------------------------------

;; for testing, maybe not needed
(def user-config-args-sx-defclass-stub (atom nil))

(def kushi-cache-dir ".kushi/.cache")

(def kushi-cache-path
  (str kushi-cache-dir "/" version* ".edn"))

(def kushi-counts-path
  (str kushi-cache-dir "/counts.edn"))

(defonce styles-cache-current
  (let [styles-cache-disc (when (true? (:caching? user-config))
                            (load-edn kushi-cache-path))]
    (atom (or styles-cache-disc {}))))

(defonce styles-cache-updated
  (atom @styles-cache-current))

(defn cache-is-equal? []
  (let [[only-in-a only-in-b _] (data/diff @styles-cache-current @styles-cache-updated)
        equal?                  (and (nil? only-in-a) (nil? only-in-b))]
    #_(println {:only-in-a only-in-a
                :only-in-b only-in-b})
    #_(println (mapv (fn [[cache-key {:keys [form-meta args]}]] [cache-key form-meta args]) only-in-b))
    {:equal?         equal?
     :diff-count     (some-> only-in-b count)
     :diff-callsites (mapv (fn [[_ {:keys [form-meta]}]] form-meta) only-in-b)}))

(defn write-cache! [cache-is-equal?]
  (when-not cache-is-equal?
    (do
      (let [created-cache-dir? (make-parents kushi-cache-path)
            {fname :name
             ns*   :ns}        (meta #'write-cache!)
            ]
        (when created-cache-dir?
          (reporting/report! @shadow-build-id
                             (str " Created cache dir -> " kushi-cache-dir))))
      (spit kushi-cache-path
            @styles-cache-updated
            :append
            false)))
  (reset! styles-cache-current
          @styles-cache-updated))


;; Tracing ---------------------------------------------------------------
(defn trace! [args target] (reset! trace?* (= args (rest target))) )
(defn trace? [] @trace?*)

(defn trace-mode! [args] (reset! *trace-mode? (= (last args) :kushi/trace)))
(defn trace-mode? [] @*trace-mode?)

;; Caching and hashing ---------------------------------------------------
(defn cached
  "Example below assumes these entries are present in the user's kushi.edn config:
   {:caching? false}

   (cached :sx :p--10px {:id :foo})
   =>
   {:caching?  false
    :cache-key -345599837
    :cached    {...}}"
  [{:keys [process sym args]}]
  (let [caching?                     (if @user-config-args-sx-defclass-stub
                                       false
                                       (:caching? user-config))
        user-config-args-sx-defclass (or @user-config-args-sx-defclass-stub user-config-args-sx-defclass)
        args                         (let [m* (when (seq args) (last args))]
                                       (if (map? m*)
                                         (let [hd        (drop-last args)
                                               clean-map (select-keys m* [:style :class])]
                                           (concat hd [clean-map]))
                                         args))
        base                         (into [] (remove nil? (apply conj [process user-config-args-sx-defclass sym] args)))
        cache-key                    (hash base)
        cached                       (when caching? (get @styles-cache-updated cache-key))]
    {:caching?  caching?
     :cache-key cache-key
     :cached    cached}))

(def silence-warnings? (atom false))

(def user-config-args-sx-defclass-stub (atom nil))

