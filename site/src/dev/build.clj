(ns build
  (:require
    [fireworks.core :refer [? !? ?> !?>]]
    [kushi.css.build.build :as cb]
    [shadow.cljs.devtools.api :as shadow]
    [clojure.edn :as edn]
    [clojure.java.io :as io]))


;; TODO 
;;
;; 1) Use :font-loading from kushi.edn to create appropriate tags
;; 2) Parse the existing index.html with something like hickory
;; 3) Transform that data structure to include font <script> tags, if needed
;; 4) Rewrite the index.html file. (would this require hard refresh for dev?)
;;
;; Alternate convention would be to have something like:
;; <script defer src="/js/font-loading.js"></script>
;; then rewrite that font-loading.js file with vanilla js
;; 
;; Or somehow include the injection logic in main.js bundle
;; maybe with shadow-cljs build-hooks?

(defn css-release [& args]
  (let [user-config
        (with-open [r (clojure.java.io/reader "kushi.edn")]
          (edn/read {:default (fn [tag value] value)}
                    (java.io.PushbackReader. r)))

        build-state
        (-> (cb/start)
            (cb/index-path (io/file "src" "main") {})
            (assoc :user-config user-config)
            (cb/generate
             '{:main {:entries [mvp.browser]}})

            ;; Maybe don't need this if using lightningcss
            #_(cb/minify)

            (cb/write-outputs-to (io/file "public" "css")))]

    (doseq [mod                   (:outputs build-state)
            {:keys [warning-type]
             :as   warning}       (:warnings mod)]
      (prn [:CSS (name warning-type) (dissoc warning :warning-type)]))))

(defn js-release []
  (shadow/release! :app))

(defn all []
  (css-release)
  (js-release)

  :done)

(comment
  (css-release))
