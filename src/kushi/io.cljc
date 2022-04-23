(ns ^:dev/always kushi.io
  (:require
   [clojure.edn :as edn]))

(defn load-edn
  "Load edn from an io/reader source (filename or io/resource)."
  [source]
  #?(:clj (use 'clojure.java.io))
  #?(:clj (try
            (with-open [r (clojure.java.io/reader source)]
              (edn/read {:default (fn [tag value] value)} (java.io.PushbackReader. r)))

            ;; TODO use warning or error panel
            ;; TODO consolidate these somewhere?
            (catch java.io.IOException e
              (printf "\nCouldn't open '%s':\n %s.\nIgnore the above warning about 'kushi.edn' if you are running tests from the source repo (kushi/test/kushi/test.clj).\n"
                      source
                      (.getMessage e)))

            (catch RuntimeException e
              (printf "Error parsing edn file '%s':\n %s\n"
                      source
                      (.getMessage e))))))
