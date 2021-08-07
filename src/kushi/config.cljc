(ns ^:dev/always kushi.config
  (:require
   [clojure.edn :as edn]
   ))

(def default-kushi-responsive
  {:sm {:min-width :640px}
   :md {:min-width :768px}
   :lg {:min-width :1024px}
   :xl {:min-width :1280px}
   :2xl {:min-width :1536px}})

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

              (catch java.io.IOException e
                (printf "\nCouldn't open '%s': %s.\nIgnore the above warning about 'kushi.edn' if you are running tests from the source repo (kushi/test/kushi/test.clj).\n" source (.getMessage e)))

              (catch RuntimeException e
                (printf "Error parsing edn file '%s': %s\n" source (.getMessage e)))))))

(def user-config
  (let [config* (let [m (load-edn "kushi.edn")]
                  (if (map? m) m {}))
        user-responsive (:media config*)
        config (if (valid-responsive? user-responsive)
                 config*
                 (assoc config* :media default-kushi-responsive))]
    config))
