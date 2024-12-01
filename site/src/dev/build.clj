(ns build
  (:require
    [fireworks.core :refer [? !? ?> !?>]]
    [kushi.css.build.build :as cb]
    [shadow.cljs.devtools.api :as shadow]
    [clojure.edn :as edn]
    [clojure.java.io :as io]))


(defn css-release [& args]
  (let [user-config
        (with-open [r (clojure.java.io/reader "kushi.edn")]
          (edn/read {:default (fn [tag value] value)} (java.io.PushbackReader. r)))

        build-state
        (-> (cb/start)
            (cb/index-path (io/file "src" "main") {})
            (assoc :user-config user-config)
            (cb/generate
             '{:main {:entries [mvp.browser]}})
            #_(cb/minify)
            (cb/write-outputs-to (io/file "public" "css")))]

    (!? (:req-utility-classes build-state))
    (!? {:non-coll-result-length-limit 999} (:utility-classes-css build-state))
    #_(? (:chunks build-state))

    (doseq [mod                                 (:outputs build-state)
            {:keys [warning-type]
             :as   warning} (:warnings mod)]
      (prn [:CSS (name warning-type) (dissoc warning :warning-type)]))))

(defn js-release []
  (shadow/release! :app))

(defn all []
  (css-release)
  (js-release)

  :done)

(comment
  (css-release))
