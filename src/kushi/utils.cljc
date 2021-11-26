(ns ^:dev/always kushi.utils
  (:require
   [clojure.string :as string]
   [clojure.walk :as walk]
   [clojure.pprint]
   [kushi.defs :as defs]
   [kushi.scales :refer [scales scaling-map]]
   [kushi.config :refer [user-config]]))

(defn pprint+
  ([v]
   (pprint+ nil v))
  ([title v]
   #?(:cljs (do (if title
                  (println "\n" title " =>")
                  (println "\n\n"))
                (cljs.pprint/pprint v)
                (println "\n"))
      :clj (do (if title
                 (println "\n" title " =>")
                 (println "\n\n"))
               (clojure.pprint/pprint v)
               (println "\n")))))

(defn auto-generated-hash []
  (let [rando-a-z (char (+ (rand-int 25) 97))
        hash (string/replace (str (gensym)) #"^G__" (str "_" (str rando-a-z)))]
    hash))

(defn cssfn? [x]
  (and (list? x)
       (= (first x) 'cssfn)
       (keyword? (second x))))

(declare cssfn)

(defn vec-in-cssfn [v]
  (string/join " " (map #(cond
                           (cssfn? %) (cssfn %)
                           (vector? %) (vec-in-cssfn %)
                           (keyword? %) (name %)
                           :else (str %))
                        v)))

(defn cssfn [[_ nm & args]]
  (str (name nm)
       "("
       (string/join
        ", "
        (map #(cond
                (cssfn? %) (cssfn %)
                (vector? %) (vec-in-cssfn %)
                (keyword? %) (name %)
                :else (if (nm = :url)
                        (str "\"" % "\"")
                        (str %)))
             args))
       ")"))

(defn num->pxstr-maybe
  [prop-hydrated n]
  (let [prop-hydrated-kw (keyword prop-hydrated)]
   (if (contains? defs/int-vals prop-hydrated-kw)
    n
    (str n "px"))))

(defn convert-number
  [s hydrated-k]
  (let [float? (not (nil? (re-find #"[0-9]\.[0-9]" s)))
        s #?(:clj (if float? (. Double parseDouble s) (. Integer parseInt s))
             :cljs (if float? (js/parseFloat s) (js/parseInt s)))]
    (num->pxstr-maybe hydrated-k s)))

(defn numeric-string?
  "String representation of float or int?"
  [s]
  (when (and (string? s) (not (string/blank? s)))
    (if (re-find  #"^[-+]?[0-9]*\.?[0-9]*$" s) true false)))

(defn parse-int [s]
  #?(:clj (if float? (. Double parseDouble s) (. Integer parseInt s))
     :cljs (if float? (js/parseFloat s) (js/parseInt s))))

(defn process-sexp [sexp]
  (walk/postwalk
   (fn [x]
     (if (cssfn? x) (cssfn x) x))
   sexp))

(defn process-value
  [v hydrated-k]
   (cond
     (symbol? v)
     (str "var(--" (name v) ")")

     (and (string? v) (re-find #"[\da-z]+\*$" v))
     (let [scale-system (or (:scaling-mode user-config) :tachyons)
           scale-key (string/replace v #"\*$" "")
           css-val (get (some-> scales
                                scale-system
                                (get (hydrated-k scaling-map)))
                        scale-key nil)]
       (when css-val css-val))

     (or (numeric-string? v) (number? v))
     (convert-number (str v) hydrated-k)

     (cssfn? v)
     (cssfn v)

     (list? v)
     (process-sexp v)

     (vector? v)
     (mapv #(process-value % hydrated-k) v)

     :else v))

(defn deep-merge [& maps]
  (apply merge-with (fn [& args]
                      (if (every? map? args)
                        (apply deep-merge args)
                        (last args)))
         maps))

(defn starts-with-dot? [x]
 (-> x name (string/starts-with? ".")))

(defn reduce-by-pred [pred coll]
  (reduce (fn [acc v]
            (let [k (if (pred v) :valid :invalid)]
              (assoc acc k (conj (k acc) v))))
          {:valid [] :invalid []}
          coll))

(defn normalized-class-kw [x]
  (if (keyword? x)
    (if (-> x name starts-with-dot?)
      (-> x name (subs 1) keyword)
      x)
    x))

(defn values? [x]
  (boolean
   (when x
     (or (string? x)
         (keyword? x)
         (when (coll? x)
           (not-empty x))))))

(defn analyze-attr
  [{:keys [conditional-class-sexprs attr css-vars]}]
  (let
   [conditional-classes? (values? conditional-class-sexprs)
    existing-classes? (values? (:class attr))
    css-vars? (values? css-vars)
    style? (values? (:style attr))
    attr? (values? attr)
    class-like? (boolean (some true? [conditional-classes? existing-classes?]))
    style-like? (boolean (some true? [css-vars? style?]))]

    #_(pprint+ {:conditional-classes? conditional-classes?
              :conditional-class-sexprs conditional-class-sexprs
              :existing-classes? existing-classes?
              :existing-classes (:class attr)
              :css-vars? css-vars?
              :css-vars css-vars
              :style? style?
              :style (:style attr)})

    {:only-class? (and (not attr?) (not-any? true? [class-like? style-like?]))
     :only-class+style? (and (not attr?) (and style-like? (not class-like?)))}))
