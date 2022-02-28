(ns kushi.test
  (:require
   [clojure.string :as string]
   [clojure.test :as test :refer [is testing deftest]]
   [garden.core :as garden]
   [kushi.core :refer (sx*)]
   [kushi.shorthand :as shorthand]
   [kushi.specs :as specs]
   [kushi.config]))

;; TESTING ::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
;; tests for styles
;; tests for styles with meta
;; tests for defclasses
;; Regex for tests
;; @media\(min-width:640px\){\._[a-z]\d{5}{color:red}}


;;combos here
(def kushi-gen-selector-re "\\._[a-z]\\d{4,7}")

(defn css-rules-re [css-rules]
  (let [rules (-> css-rules
                  (string/replace "(" "\\(")
                  (string/replace ")" "\\)")
                  (string/replace "." "\\.")
                  (string/replace "/" "\\/")
                  (string/replace ", " ","))]
    #_(prn rules)
    (str "\\{" rules "\\}")))

(defn mq-re [responsive-re]
  (str "@media\\((?:" responsive-re ")\\)"))

(def mods-re
  {:pe (str ":+" specs/css-pseudo-element-re)
   :pc (str ":" specs/css-pseudo-class-re)
   :combo "(?:\\*?[ _>~\\+][a-z]*)+\\*?"})

(defn kushi-css [args]
  (garden/css {:pretty-print? false} (:garden-vecs (sx* args))))

(defn responsive-re* [m]
  (string/join
   "|"
   (map (fn [[_ v]]
          (str (-> v ffirst name)
               ":"
               (-> v first second name)))
        m)))

(def responsive-re (-> kushi.config/user-config :media responsive-re*))


(defn css-re
  [output & mods]
  (let [mq? (contains?
             (->> kushi.config/default-kushi-responsive keys (into #{}))
             (first mods))
        mods (if mq? (rest mods) mods)]
    (re-pattern
     (str "^"
          (when mq?
            (mq-re responsive-re))
          (when mq? "\\{")
          kushi-gen-selector-re
          (when mods
            (string/join (map #(% mods-re) mods)))
          (css-rules-re output)
          (when mq? "\\}")
          "$"))))

(def mod-labels
  {:combo "combo-selector" :pe "pseudo-element" :pc "pseudo-class"})

(def all-mq-tests
  (->> kushi.config/default-kushi-responsive keys
       (map (fn [mq-kw]
              [[mq-kw]
               [(keyword (str (name mq-kw) ":" (name :color--red)))]
               "color:red"]))))

(defn fmt-val-inner [x]
  (if (vector? x)
    (string/join " "
                 (map (fn [v] (if (number? v) (str v) (name v))) x))
    (name x)))


(defn fmt-val [example-val]
  (cond
    (keyword? example-val)
    example-val

    (vector? example-val)
    (string/join ", " (mapv fmt-val-inner example-val))

    :else example-val))


(def all-shorthand-kw-tests
  (keep (fn [[k {hydrated-prop :name example-val :example-val}]]
          (when example-val
            (let [fmt-val (fmt-val example-val)
                  args (if (keyword? fmt-val)
                         [(keyword (str (name k) "--" (if (keyword? fmt-val) (name fmt-val) fmt-val)))]
                         [[k example-val]])]
              [[]
               args
               (str (name hydrated-prop) ":" (if (keyword? fmt-val) (name fmt-val) fmt-val))])))
        #_(select-keys shorthand/css-sh [:bg])
        shorthand/css-sh
        #_(let [as-vec (filter (fn [[_ {:keys [example-val]}]] (not (nil? example-val)))
                               (into [] shorthand/css-sh))]
            as-vec)))

(def all-shorthand-enum-kw-tests
  (apply concat
         (keep (fn [[k {hydrated-prop :name enum-vals :vals}]]
                 (when enum-vals
                   (mapv
                    (fn [[enum-val-sh enum-val-hydrated]]
                      [[]
                       [(keyword (str (name k) "--" (name enum-val-sh)))]
                       (str (name hydrated-prop) ":" (name enum-val-hydrated))])
                    enum-vals)))
               shorthand/css-sh)))

(def css-fn-tests
  [[[]
    [[:bg '(cssfn :linear-gradient "to bottom right" :red :blue)]]
    "background:linear-gradient(to bottom right,red,blue)"]
   [[]
    [[:bg '(cssfn :linear-gradient "to bottom right" [:red :10%] [:blue :20%])]]
    "background:linear-gradient(to bottom right,red 10%,blue 20%)"]])

(def mods-tests
  [[[:sm :combo :pe]
    [["sm: > a~div *:before:c" :red]]
    "color:red"]
   [[:sm :combo :pc]
    [["sm:>a:hover:c" :red]]
    "color:red"]
   [[:sm :pe]
    [["sm:before:c" :red]]
    "color:red"]
   [[:sm :pc]
    [["sm:first-child:c" :red]]
    "color:red"]])

(defn kushi-assertion [mods args output]
  (testing (str "\nTesting -> " (string/join ", " (map #(or (% mod-labels) %) mods)) "\n" args "\n")
    (is (re-find (apply (partial css-re output) mods) (kushi-css args)))))

(deftest media-queries
  (doseq [[mods args output] all-mq-tests]
    (testing (string/join ", " (map #(% mod-labels) mods))
      (is (re-find (apply (partial css-re output) mods) (kushi-css args))))))

(deftest shorthand-props-enum
  (doseq [[mods args output] all-shorthand-enum-kw-tests]
    (testing (string/join ", " (map #(% mod-labels) mods))
      (is (re-find (apply (partial css-re output) mods) (kushi-css args))))))

(deftest shorthand-props-arbitrary
  (doseq [[mods args output] all-shorthand-kw-tests]
    (testing (string/join ", " (map #(% mod-labels) mods))
      (is (re-find (apply (partial css-re output) mods) (kushi-css args))))))

(deftest css-fn
  (doseq [[mods args output] css-fn-tests]
    (testing (string/join ", " (map #(% mod-labels) mods))
      (is (re-find (apply (partial css-re output) mods) (kushi-css args))))))

(deftest mods
  (doseq [[mods args output] mods-tests]
    (kushi-assertion mods args output)))

