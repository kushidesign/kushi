(defproject design.kushi/kushi "1.0.0-a.22"
  :description         "ClojureScript UI Framework"
  :url                 "https://github.com/kushidesign/kushi"
  :license             {:name "EPL-2.0 OR GPL-2.0-or-later WITH Classpath-exception-2.0"
                        :url  "https://www.eclipse.org/legal/epl-2.0/"}
  ;; :source-paths        ["../domo/src"]
  :dependencies        [[design.kushi/domo "0.3.0"]
                        [org.clojure/clojure "1.11.1"]
                        [garden "1.3.10"]
                        [applied-science/js-interop "0.3.3"]
                        [expound "0.9.0"]
                        [markdown-to-hiccup/markdown-to-hiccup "0.6.2"]
                        [io.github.paintparty/bling "0.1.1"]
                        [me.flowthing/pp "2024-01-04.60"]]
  :repl-options        {:init-ns kushi.core}
  :deploy-repositories [["clojars" {:url           "https://clojars.org/repo"
                                    :sign-releases false}]])
