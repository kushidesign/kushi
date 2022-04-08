(ns kushi.ui.util
 (:require
  [clojure.string :as string]))

(defn capitalize-words
  "Capitalize every word in a string"
  [s]
  (->> (string/split (str s) #"\b")
       (map string/capitalize)
       string/join))

;; https://gist.github.com/rotaliator/73daca2dc93c586122a0da57189ece13
(defn copy-to-clipboard [val]
  (let [el (js/document.createElement "textarea")]
    (set! (.-value el) val)
    (.appendChild js/document.body el)
    (.select el)
    (js/document.execCommand "copy")
    (.removeChild js/document.body el)))
