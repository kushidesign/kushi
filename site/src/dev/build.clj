(ns build
  (:require
    [fireworks.core :refer [? !? ?> !?> pprint]]
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
  #_(let [bs (cb/index-path {:namespaces   {}
                           :alias-groups {}
                           :aliases      {}
                           :spacing      {}}
                          (io/file "../src" "kushi" "ui" "slider") {})]
    (println "BS")
    (pprint (get (-> bs :namespaces) 'kushi.ui.slider.core)))
  (let [user-config
        (with-open [r (clojure.java.io/reader "kushi.edn")]
          (edn/read {:default (fn [tag value] value)}
                    (java.io.PushbackReader. r)))

        build-state
        (-> (cb/start)

            ;; (cb/index-path (io/file "../src" "kushi" "ui" "slider") {})
            ;; (cb/index-path (io/file "../src" "kushi" "ui" "text_field") {})
            ;; (cb/index-path (io/file "../src" "kushi" "ui" "radio") {})
            ;; (cb/index-path (io/file "../src" "kushi" "ui" "checkbox") {})
            ;; (cb/index-path (io/file "../src" "kushi" "ui" "label") {})
            (cb/index-path (io/file "../src" "kushi" "ui" "button") {})
            ;; (cb/index-path (io/file "../src" "kushi" "ui" "icon") {})
            ;; (cb/index-path (io/file "../src" "kushi" "ui" "spinner") {})
            ;; (cb/index-path (io/file "../src" "kushi" "ui" "switch") {})
            ;; (cb/index-path (io/file "../src" "kushi" "ui" "card") {})
            ;; (cb/index-path (io/file "../src" "kushi" "ui" "tag") {})
            ;; (cb/index-path (io/file "../src" "kushi" "ui" "spinner") {})
            ;; (cb/index-path (io/file "../src" "kushi" "ui" "grid") {})
            ;; (cb/index-path (io/file "../src" "kushi" "ui" "callout") {})
            ;; (cb/index-path (io/file "../src" "kushi" "ui" "link") {})
            ;; (cb/index-path (io/file "../src" "kushi" "ui" "collapse") {})
            ;; (cb/index-path (io/file "../src" "kushi" "ui" "modal") {})
            ;; (cb/index-path (io/file "../src" "kushi" "ui" "tooltip") {})
            ;; (cb/index-path (io/file "../src" "kushi" "ui" "popover") {})

            (cb/index-path (io/file "../src" "kushi" "ui" "label") {})
            ;; Check popover with form ^^^

            (cb/index-path (io/file "../src" "kushi" "ui" "toast") {})

            ;; (cb/index-path (io/file "../src" "kushi" "ui" "dom" "pane" "toast") {})
            ;; Done!

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
