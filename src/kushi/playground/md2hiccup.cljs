;; TODO
;; Support a ``` concept where formatting is preserved

(ns kushi.playground.md2hiccup
  (:require
   [clojure.string :as string]
   [clojure.walk :as walk]
   [goog.string :as gstring]
   [kushi.playground.util :refer [interleave-all]]))

(defn hiccupize-bits [s re-cap re f]
  (let [matches (re-seq re-cap s)
        matches (map #(-> % second f) matches)
        coll    (string/split s re)]
    (interleave-all coll matches)))

(defn contains-codes? [x]
  (when (string? x)
    (some-> (re-seq #"`" x) count even?)))

(defn hiccupize-codes [s]
  (hiccupize-bits s
                  #"`([^`]+)`"
                  #"`[^`]+`"
                  #(vector :code %)))

(defn contains-bolded? [x]
  (when (string? x)
    (re-find #"__[^_]+__" x)))

(defn hiccupize-bolded [s]
  (hiccupize-bits s
                  #"__([^_]+)__"
                  #"__[^_]+__"
                  #(vector :b %)))

(def md-url-re #"\[[^\]]+\]\([^\)]+\)")

(def md-url-capturing-re #"\[([^\]]+)\]\(([^\)]+)\)")

(defn contains-url? [s]
  (re-find md-url-re s))

(defn hiccupize-url [s]
  (let [matches (re-seq md-url-re s)
        matches (mapv #(let [[_ link href] (re-find md-url-capturing-re %)
                             target (if (string/starts-with? href "#")
                                      "_self"
                                      "_blank")]
                        [:a {:href href :target target :class "kushi-link"} link])
                     matches)
        coll    (string/split s md-url-re)]
    (interleave-all coll matches)))


;; Preformatting for html <br> chars -------------------------------------------

(def sr string/replace)

(def ssplit string/split)

(defn line-is-only-tabs-or-spaces? [s]
  (boolean (re-find #"^[\t ]+$" s)))

(defn replace-leading-tabs-or-spaces-with-stub
  [leading-spaces s]
  (sr s #"^[\t ]+" (sr leading-spaces #"[\t ]" "·")))

(defn find-leading-spaces [s]
  (some->> s (re-find #"^([\t ]+)[^\t ]*")))

(defn several-nl->2-nl [s] (sr s #"\n{3,}" "\n\n"))

(defn remove-user-formatting-nl [s] 
  (sr s #"([^\n·])\n·+" #(str (nth % 1 nil) " ")))

(defn remove-remaining-tabs-or-spaces-markers [s]
  (sr s #"·" ""))

(defn user-forced-linebreaks->nl [s]
  (-> s
      (sr #"<br> +" "\n")
      (sr #"<br>(\n+)" (fn [%] (str (second %))))
      (sr #"<br>$" "")))

(defn leading-tabs-or-spaces->markers [s]
  (->> (ssplit s #"\n")
       (map #(if-let [[match leading-spaces]
                      (find-leading-spaces %)]
               (if (line-is-only-tabs-or-spaces? match)
                 ""
                 (replace-leading-tabs-or-spaces-with-stub 
                  leading-spaces
                  %))
               %))
       (string/join "\n")) )

(defn preformat [s]
  (-> s
      leading-tabs-or-spaces->markers
      several-nl->2-nl
      remove-user-formatting-nl
      remove-remaining-tabs-or-spaces-markers
      user-forced-linebreaks->nl))


;; Preformatting for html <br> chars end ---------------------------------------

(defn desc->hiccup [desc]
  (let [s (if (string? desc)
            desc
            (->> desc
                 (map #(if (= % :br) "\n" %))
                 (string/join "\n")))
        ;; TODO fix this hack
        s (string/replace s #"\n\n\n" "\n\n")
        s (string/replace s #"\n\n\n" "\n\n")
        s (string/replace s #"\n\n\n" "\n\n")]
    ;; (!? {:print-with prn} s)
    (-> s
        preformat
        #_(->> (? {:print-with println
                 :when #(string/starts-with? % "tooltips")}))
        (string/split #"\n")

        (->> (reduce 
              (fn [acc s]
                (let [x (if (contains-url? s) (hiccupize-url s) s)
                      x (if (string? x)
                          (cond
                            (contains-codes? x)
                            (let [ret (hiccupize-codes x)]
                              (reduce (fn [acc %]
                                        (if (contains-bolded? %)
                                          (apply conj
                                                 acc
                                                 (hiccupize-bolded %))
                                          (conj acc %)))
                                      []
                                      ret))

                            (contains-bolded? x)
                            (hiccupize-bolded x)

                            :else
                            x)
                          (walk/postwalk #(cond
                                            (contains-codes? %)
                                            (into [:<>] (hiccupize-codes %))

                                            (contains-bolded? %)
                                            (into [:<>] (hiccupize-bolded %))

                                            :else
                                            %)
                                         x))]
                  (conj acc
                        (if (string? x)
                          [:span.prose (if (= x "")
                                   (gstring/unescapeEntities "&nbsp;")
                                   x)]
                          (into [:span.prose] x)))))
              [])))))
