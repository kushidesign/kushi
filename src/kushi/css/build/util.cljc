;; TODO eliminate this ns, redundant with kushi.utils
(ns kushi.css.build.util
  (:require
   [clojure.string :as string]
   ))

;; from kushi.utils ------------------------------------------------------------

(defn nameable? [x]
  (or (string? x) (keyword? x) (symbol? x)))

(defn stringify [x]
  (if (nameable? x) (name x) (str x)))

(defn token? [x]
  (when (nameable? x)
    (let [nm (name x)]
      (or (string/starts-with? nm "--")
          (string/starts-with? nm "$")))))

(defn kebab->shorthand [x] 
  (->> (-> x
           stringify
           (string/split #"-"))
       (map #(nth % 0 nil))
       string/join))

(defn deep-merge [& maps]
  (apply merge-with (fn [& args]
                      (if (every? map? args)
                        (apply deep-merge args)
                        (last args)))
         maps))


(defn kwargs-keys
  "Expects an even-numbered kwarg-style collection of key/values.
   Returns a coll of the keys."
  [kwargs]
  (keep-indexed (fn [idx x] (when (even? idx) x)) kwargs))
