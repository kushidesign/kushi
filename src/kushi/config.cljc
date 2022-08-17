(ns ^:dev/always kushi.config
  (:require
   [clojure.edn :as edn]))


(def default-kushi-responsive
  [:sm {:min-width :640px}
   :md {:min-width :768px}
   :lg {:min-width :1024px}
   :xl {:min-width :1280px}
   :2xl {:min-width :1536px}])

(defn valid-responsive? [m]
  (and (map? m)
       (not-empty m)))

(defn load-edn
  "Load edn from an io/reader source (filename or io/resource)."
  [source]
  #?(:clj (do
            (use 'clojure.java.io)
            (try
              (with-open [r (clojure.java.io/reader source)]
                (edn/read (java.io.PushbackReader. r)))

              ;; TODO use warning or error panel
              ;; TODO consolidate these somewhere?
              (catch java.io.IOException e
                (printf (str "Warning in kushi.config/load-edn: "
                             "Could not open '%s': "
                             " %s.\nIgnore the above warning if you are running tests from the source repo kushi/test/kushi/test.clj.\n")
                        source
                        (.getMessage e)))

              (catch RuntimeException e
                (printf "Error parsing edn file '%s':\n %s\n"
                        source
                        (.getMessage e)))))))

(def user-config-defaults
  {:ui                   []
   :select-ns            nil
   :post-build-report?   true
   :report-cache-update? true
   :reporting-style      :simple
   :kushi-class-prefix   nil
   :prepend-to-selectors nil ;; Usually would be the id of the "app" container, e.g "#app".
   :css-vars-root        nil ;; Usually would be the id of the "app" container, e.g "#app". Defaults to ":root"
   :data-attr-name       nil
   :css-dir              nil
   :write-stylesheet?    true
   :runtime-injection?   false
   :handle-duplicates    nil
   :enable-caching?      false
   :theme                nil
   :css-reset?           true
   :ui-theming?          true

   ;; Experimental - add to docs later
   :scaling-system       nil
   :diagnose             nil
   :diagnose-idents      nil
   :log-clean!?          false})

(defn ->user-config [m]
  (let [config*         m
        user-responsive (apply array-map (:media config*))
        responsive      (if (valid-responsive? user-responsive)
                          user-responsive
                          (apply array-map default-kushi-responsive))
        ret*            (assoc config* :media responsive)
        ret             (merge user-config-defaults ret*)]
    ret))

(def user-config
  (let [config* (let [m (load-edn "kushi.edn")]
                  (if (map? m) m {}))]
    (->user-config config*)))

(defn ->user-config-args-sx-defclass
  "Takes a merged config and selects only the keys needed for hashing (cache key) for sx and defclass."
  [m]
  (select-keys m
               [:data-attr-name
                :prepend-to-selectors
                :kushi-class-prefix
                :media]))

(def user-config-args-sx-defclass
  (->user-config-args-sx-defclass user-config))

(def user-css-file-path
  (str (or (:css-dir user-config) (:static-css-dir user-config))
       "/"
       (or (:css-filename user-config) "kushi.css")))

;; ! Update kushi version here for console printing and cache file path generation
(def version* "1.0.0-a.10")

;; You can optionally unsilence the ":LOCAL" bit when developing kushi from local filesystem (for visual feedback sanity check).
(def version (str version* #_":LOCAL"))

(def kushi-cache-dir ".kushi/.cache")

(def kushi-cache-path
  (str kushi-cache-dir "/" version* ".edn"))

