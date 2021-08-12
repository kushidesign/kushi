(defproject org.clojars.paintparty/kushi "0.1.1"
  :description "ClojureScript CSS Solution"
  :url "https://github.com/paintparty/kushi"
  :license {:name "EPL-2.0 OR GPL-2.0-or-later WITH Classpath-exception-2.0"
            :url "https://www.eclipse.org/legal/epl-2.0/"}
  :dependencies [[org.clojure/clojure "1.10.3"]
                 [garden "1.3.10"]
                 [io.aviso/pretty "1.1"]]
  :repl-options {:init-ns kushi.core}
  :deploy-repositories [["clojars" {:url "https://clojars.org/repo"
                                    :sign-releases false}]]
  )
