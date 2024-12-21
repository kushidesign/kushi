(ns kushi.css.core
  (:require [clojure.string :as string])
  (:require-macros [kushi.css.core :refer [css-include]]))

(css-include "@layer css-reset build/kushi-reset.css")

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

;; move into macro-land?
(defn grid-template-areas
  "Use like this:
   (kushi/grid-template-areas
    \"brc br b  bl blc\"
    \"rt  .  .  .  lt\"
    \"r   .  .  .  l\"
    \"rb  .  .  .  lb\"
    \"trc tr t  tl tlc\")"
  [& rows]
  (string/join " " (map #(str "\"" % "\"") rows)))
