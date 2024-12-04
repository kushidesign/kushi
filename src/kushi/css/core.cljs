(ns kushi.css.core
  (:require [clojure.string :as string])
  (:require-macros [kushi.css.core]))


(defn ^:public class-str
  "Takes a coll of class strings or keywords, at least one of which is a
   dynamic runtime binding, and joins them into a string with each class
   separated by a space. Converts keywords to string and strips off any leading
   dot chars."
  [classes]
  (string/join " "
               (keep #(when (or (keyword? %) (string? %))
                        (let [s (name %)]
                          (if (string/starts-with? s ".")
                            (subs s 1)
                            s)))
                     classes)))
