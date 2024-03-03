(defproject design.kushi/kushi "1.0.0-a.19-SNAPSHOT"
  :description         "ClojureScript UI Framework"
  :url                 "https://github.com/kushidesign/kushi"
  :license             {:name "EPL-2.0 OR GPL-2.0-or-later WITH Classpath-exception-2.0"
                        :url  "https://www.eclipse.org/legal/epl-2.0/"}
  :dependencies        [[org.clojure/clojure "1.11.1"]
                        [garden "1.3.10"]
                        [applied-science/js-interop "0.3.3"]
                        [io.aviso/pretty "1.1"]
                        [expound "0.9.0"]
                        [metosin/malli                         "0.11.0"]
                        [markdown-to-hiccup/markdown-to-hiccup "0.6.2"]]
  :repl-options        {:init-ns kushi.core}
  :deploy-repositories [["clojars" {:url           "https://clojars.org/repo"
                                    :sign-releases false}]])
