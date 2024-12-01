(ns repl
  (:require
    [clojure.java.io :as io]
    [build]
    [shadow.cljs.devtools.api :as shadow]
    [shadow.cljs.devtools.server.fs-watch :as fs-watch]))

(defonce css-watch-ref (atom nil))

(defn start
  {:shadow/requires-server true}
  []

  ;; optional, could also do this from the UI
  (shadow/watch :app)

  ;; build css once on start
  (build/css-release)

  ;; the watcher that rebuilds css everything on change
  (reset! css-watch-ref
    (fs-watch/start
      {}
      [(io/file "src" "main")]
      ["cljs" "cljc" "clj"]
      (fn [_]
        (try
          (build/css-release)
          (catch Exception e
            (prn [:css-failed e]))))))

  ::started)

(defn stop []
  (when-some [css-watch @css-watch-ref]
    (fs-watch/stop css-watch))

  ::stopped)

(defn go []
  (stop)
  (start))
