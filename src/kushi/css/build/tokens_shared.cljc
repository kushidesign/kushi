(ns kushi.css.build.tokens-shared
  (:require [clojure.string :as string]))

;; shared code

(defn- css-vars-re-seq [s]
  (when (string? s)
    (re-seq #"var\((--[^_][^\)\, ]+)" s)))

(defn- css-var-string? [s]
  (boolean (when (string? s)
             (re-find #"^var\(--[^_][^\)\, ]+\)$" s))))

(defn- css-var-kw? [x]
  (and (keyword? x) (string/starts-with? (name x) "$")))

(defn- css-var-str [v]
  (when (css-var-kw? v)
    (str "var(--" (subs (name v) 1) ")")))

(defn enriched-tokens-ordered* [tokens]
  (mapcat (fn [[{:keys [desc category tags family added]
                 :or   {desc  "Fix me"
                        added "1.0" ;; <- get version?
                        }}
                toks]]
            (let [toks (apply array-map toks)]
              (for [[tok v] toks
                    :let    [alias-token? (boolean (or (css-var-kw? v)
                                                       (css-var-string? v)))
                             value (or (css-var-str v)
                                       (when (keyword? v) (name v))
                                       v)]]
                {:name         (name tok)
                 :value        value
                 :desc         desc
                 :category     category
                 :tags         tags
                 :family       family
                 :added        "1.0"
                 :alias-token? alias-token?
                 :dep-toks     (some->> (or (css-var-str v) v)
                                        css-vars-re-seq
                                        (mapv second))
                 })))
          tokens))

(defn enriched-tokens-array-map*
  [tokens-ordered]
  (apply array-map
         (reduce (fn [acc m]
                   (conj acc (:name m) m))
                 []
                 tokens-ordered)))

(defn tokens-by-token* [enriched-tokens-array-map]
  (reduce-kv (fn [m k v]
               (assoc m k (:value v)))
             {}
             enriched-tokens-array-map))

(defn tokens-by-token-array-map* [enriched-tokens-array-map]
  (apply array-map
         (reduce-kv (fn [acc k v]
                      (conj acc k (:value v)))
                    []
                    enriched-tokens-array-map)))

(defn tokens-by-category* [enriched-tokens-ordered categories]
  (reduce (fn [acc category]
            (assoc acc
                   category
                   (reduce (fn [acc {tokens-category :category
                                     tokens-name     :name}]
                             (if (contains? (into #{} tokens-category) category)
                               (conj acc tokens-name)
                               acc))
                           []
                           enriched-tokens-ordered)))
          {}
          ;; pass this in, make fn
          categories))
