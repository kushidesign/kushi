(ns kushi.playground.util
  (:require
   [clojure.string :as string]
   [clojure.walk :as walk]
   [fireworks.core :refer [? !?]]
   [goog.string :as gstring]))

(def kushi-github-url  "https://github.com/kushidesign/kushi")
(def kushi-clojars-url "https://clojars.org/design.kushi/kushi")


(defn interleave-all
  "Returns a lazy seq of the first item in each coll, then the second, etc.
  Unlike `clojure.core/interleave`, the returned seq contains all items in the
  supplied collections, even if the collections are different sizes."
  {:arglists '([& colls])}
  ([] ())
  ([c1] (lazy-seq c1))
  ([c1 c2]
   (lazy-seq
    (let [s1 (seq c1), s2 (seq c2)]
      (if (and s1 s2)
        (cons (first s1) (cons (first s2) (interleave-all (rest s1) (rest s2))))
        (or s1 s2)))))
  ([c1 c2 & colls]
   (lazy-seq
    (let [ss (keep seq (conj colls c2 c1))]
      (when (seq ss)
        (concat (map first ss) (apply interleave-all (map rest ss))))))))

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


(defn desc->hiccup [desc]
  (let [s (if (string? desc)
            desc
            (->> desc
                 (!? {:print-with prn})
                 (map #(if (= % :br) "\n" %))
                 (!? {:print-with prn})
                 (string/join "\n")
                 (!? {:print-with prn})))
        ;; TODO fix this hack
        s (string/replace s #"\n\n\n" "\n\n")
        s (string/replace s #"\n\n\n" "\n\n")
        s (string/replace s #"\n\n\n" "\n\n")
        ]
(!? {:print-with prn} s)
    (-> s
        (string/replace #"\\\n" "")
        (string/replace #"\n *" "\n")
        (string/split #"\n")
        (->> (reduce 
              (fn [acc s]
                (let [x (if (contains-url? s) (hiccupize-url s) s)
                      x (if (string? x)
                          (cond
                            (contains-codes? x)
                            (hiccupize-codes x)

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
                          [:span (if (= x "") (gstring/unescapeEntities "&nbsp;") x)]
                          (into [:span] x)))))
              [])))))
