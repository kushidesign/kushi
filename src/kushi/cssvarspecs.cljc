(ns kushi.cssvarspecs
  (:require
   [clojure.spec.alpha :as s]))

(s/def ::tokenized-style-with-css-var
 (s/and #(or (keyword? %) (string? %))
        #(->> % name (re-find #"--:--"))))

(s/def ::tokenized-style-with-js-var
 (s/and (s/or :keyword? keyword? :string? string?)
        #(->> % name (re-find #"--:--"))))

(s/def ::css-var-name-string
 (s/and string? #(re-find #"^--\S+" %)))

(s/def ::css-var-name-kw
 (s/and keyword?
        #(s/valid? ::css-var-name-string (name %))))

(s/def ::css-var-name
 (s/or :keyword? ::css-var-name-kw
       :string?  ::css-var-name-string))

(s/def ::wrapped-css-var-name
 (s/and string? #(re-find #"^var\(--\S+" %)))
