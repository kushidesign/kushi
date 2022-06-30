(ns ^:dev/always kushi.utils
  #?(:clj (:require [io.aviso.ansi :as ansi]))
  (:require
  ;;  [kushi.parstub :refer [!? ?+ ?]]
   [clojure.spec.alpha :as s]
   [clojure.string :as string]
   [clojure.walk :as walk]
   [kushi.defs :as defs]
   [kushi.cssvarspecs :as cssvarspecs]
   [kushi.scales :refer [scales scaling-map]]
   [kushi.config :refer [user-config]]))

(defmacro keyed [& ks]
  #?(:clj
     `(let [keys# (quote ~ks)
            keys# (map keyword keys#)
            vals# (list ~@ks)]
        (zipmap keys# vals#))))


(defn auto-generated-selector []
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

(defn cssfn [[_ nm & args*]]
  #_(?+ "cssfn" {:nm nm :args args*})
  (let [args (map #(cond
                     (cssfn? %) (cssfn %)
                     (vector? %) (vec-in-cssfn %)
                     (keyword? %) (name %)
                     (number? %) %
                     :else (if (= nm :url)
                             (str "\"" % "\"")
                             (str %)))
                  args*)
        css-arg (string/join ", " args)]
    #_(?+ "cssfn" {:nm nm :args args* :css-arg css-arg})
    (str (name nm) "(" css-arg ")")))

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

(defn sanitize-for-css-var-name [v]
  (string/escape
   v
   {\? "_QMARK"
    \! "_BANG"
    \# "_HASH"
    \+ "_PLUS"
    \$ "_DOLLAR"
    \% "_PCT"
    \= "_EQUALS"
    \< "_LT"
    \> "_GT"
    \( "_OB"
    \) "_CB"
    \& "_AMP"
    \* "_STAR"}))

(defn css-var-string
  ([x]
   (css-var-string x nil))
  ([x suffix]
   (str "var(--" (sanitize-for-css-var-name (name x)) ")" suffix)))

(defn hashed-css-var
  [selector* css-prop]
  (let [hashed-name    (hash (str selector* css-prop))]
    (str "--" hashed-name)))

;; TODO - safe to remove?
(defn css-var-for-sexp [selector* css-prop]
  (let [hashed-name    (hash (str selector* css-prop))]
    (str "--" hashed-name))
  #_(let [sanitized-name (-> css-prop
                           (string/replace  #":" "_")
                           sanitize-for-css-var-name)
        hashed-name    (hash (str selector* css-prop))]
    (?+ hashed-name)
    (str "--"
         selector*
         "_"
         sanitized-name)))

(defn css-var-string-!important
  [x selector* prop]
  #_(?+ "css-var-string-!important" (css-var-string (second x) "!important"))
  (if (list? (second x))
    (str "var(" (hashed-css-var selector* prop) ")!important")
    (css-var-string (second x) "!important")))

(defn !important-var? [x] (and (list? x) (= '!important (first x))))

(defn wrap-css-var [x] (str "var(" (name x) ")"))

(defn process-sexp [sexp selector* css-prop]
  (walk/postwalk
   (fn [x]
     (cond
       (cssfn? x)
       (cssfn x)

       (!important-var? x)
       (css-var-string-!important x selector* css-prop)

       (s/valid? ::cssvarspecs/css-var-name x)
       (wrap-css-var x)

       :else x))
   sexp))

(defn process-value
  [v hydrated-k selector*]
  (cond
    (s/valid? ::cssvarspecs/css-var-name v)
    (wrap-css-var v)

    (and (string? v) (re-find #"[\da-z]+\*$" v))
    (if-let [scaling-system (:scaling-system user-config)]
      (let [scale-key (string/replace v #"\*$" "")
            css-val (get (some-> scales
                                 scaling-system
                                 (get (hydrated-k scaling-map)))
                         scale-key nil)]
        css-val)
      (let [warning (str "\n[kushi - WARNING][Bad value => " v " ]\nIf you trying to use a scaling system, you need to explicitly set a value for the :scaling-system entry in your kushi.edn config map.\nCurrently support values are `:tachyons` and `:tailwind`\n")]
        (println warning)
        warning))

    (or (numeric-string? v) (number? v))
    (convert-number (str v) hydrated-k)

    (cssfn? v)
    (cssfn v)

    (list? v)
    (process-sexp v selector* hydrated-k)

    (vector? v)
    (mapv #(process-value % hydrated-k selector*) v)

    :else v))

(defn deep-merge [& maps]
  (apply merge-with (fn [& args]
                      (if (every? map? args)
                        (apply deep-merge args)
                        (last args)))
         maps))

(defn into-coll [x]
  (if (coll? x) x [x]))

(defn starts-with-dot? [x]
 (-> x name (string/starts-with? ".")))

(defn reduce-by-pred [pred coll]
  (reduce (fn [acc v]
            (let [k (if (pred v) :valid :invalid)]
              (assoc acc k (conj (k acc) v))))
          {:valid [] :invalid []}
          coll))

(defn partition-by-pred [pred coll]
  (let [ret* (reduce (fn [acc v]
                       (let [k (if (pred v) :valid :invalid)]
                         (assoc acc k (conj (k acc) v))))
                     {:valid [] :invalid []}
                     coll)
        ret [(:valid ret*) (:invalid ret*)]]
    ret))

(defn partition-by-spec [pred coll]
  (let [ret* (reduce (fn [acc v]
                       (let [k (if (s/valid? pred v) :valid :invalid)]
                         (assoc acc k (conj (k acc) v))))
                     {:valid [] :invalid []}
                     coll)
        ret [(:valid ret*) (:invalid ret*)]]
    ret))

(defn filter-map [m pred]
  (select-keys m (for [[k v] m :when (pred k v)] k)))

(defn nameable? [x] (or (string? x) (keyword? x) (symbol? x)))

(defn stringify [x] (if (nameable? x) (name x) (str x)))

(defn token? [x]
  (when (nameable? x)
    (some-> x name (string/starts-with? "--"))))

(defn s->cssvar
  ([x] (s->cssvar x nil))
  ([x fallback]
    (str "var(--" (name x) (when fallback (str ", " fallback)) ")")))

(defn maybe-wrap-css-var [x]
  (if (token? x)
    (str "var(" (name x) ")")
    x))

(defn merged-attrs-map
  [{:keys [attrs-base classlist css-vars data-cljs] :as m}]
  (cond-> attrs-base
    true (assoc :class (distinct classlist) :style css-vars)
    data-cljs (assoc :data-cljs data-cljs)))
