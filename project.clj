(defproject design.kushi/kushi "1.0.0-a.25"
  :description         "ClojureScript UI Framework"
  :url                 "https://github.com/kushidesign/kushi"
  :license             {:name "EPL-2.0 OR GPL-2.0-or-later WITH Classpath-exception-2.0"
                        :url  "https://www.eclipse.org/legal/epl-2.0/"}
  :source-paths        [
                        ;; "../domo/src"
                        ;; "../../cljc/fireworks/src"
                        ;; "../../cljc/bling/src"
                        "src"]
  :dependencies        [[design.kushi/domo "0.3.0"]
                        [org.clojure/clojure "1.11.1"]
                        [applied-science/js-interop "0.3.3"]
                        [expound "0.9.0"]
                        [io.github.paintparty/bling "0.4.2"]
                        [io.github.paintparty/fireworks "0.10.4"]
                        [me.flowthing/pp "2024-01-04.60"]
                        [babashka/process "0.5.22"]
                        [borkdude/edamame "1.4.27"]
                        ;; for testing & profiling
                        ;; [com.taoensso/tufte "2.6.3"]
                        ]
  :repl-options        {:init-ns kushi.core}
  :deploy-repositories [["clojars" {:url           "https://clojars.org/repo"
                                    :sign-releases false}]])
