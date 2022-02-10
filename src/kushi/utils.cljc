(ns ^:dev/always kushi.utils
  #?(:clj (:require [io.aviso.ansi :as ansi]
                    [kushi.ansiformat :as ansiformat]))
  (:require
   [clojure.spec.alpha :as s]
   [clojure.string :as string]
   [clojure.walk :as walk]
   [clojure.pprint :refer [pprint]]
   [kushi.defs :as defs]
   [kushi.scales :refer [scales scaling-map]]
   [kushi.config :refer [user-config]]))

(defmacro keyed [& ks]
  #?(:clj
     `(let [keys# (quote ~ks)
            keys# (map keyword keys#)
            vals# (list ~@ks)]
        (zipmap keys# vals#))))

(defn pprint+
  ([v]
   (pprint+ nil v))
  ([title v]
   #?(:cljs (do (if title
                  (println "\n" title "\n=>")
                  (println "\n\n"))
                (cljs.pprint/pprint v)
                (println "\n"))
      :clj (do (if title
                 (do (println "\n")
                      (println (ansi/red (str "; " title)) (str "\n" (ansi/bold-cyan "=>"))))
                 (println "\n\n"))
               (clojure.pprint/pprint v)
               (println "\n")))))

(defn ?*
  ([opts desc]
   (?* opts nil desc))
  ([opts desc val]
   #?(:cljs (do (if desc
                  (do (println "\n")
                      (println desc " \n=>"))
                  (println "\n"))
                (cljs.pprint/pprint val)
                (println "\n"))
      :clj (let [comment?      (= desc :comment)
                 opts          (merge (keyed desc val comment?) opts)
                 [desc v]      (ansiformat/format-desc+val opts)]
             (when desc
               (println desc))
             (when-not comment? (println v))))))

(defn ?
  ([val]
   (? nil val))
  ([desc val]
   (?* {:bottom-margin 1} desc val)))

(defn ?? [& args] nil)
(defn ??b [& args] nil)
(defn ??t [& args] nil)

(defn debug [{nm :name :as m debug? :debug?}]
  #_(? :debug m)
  (if-not debug?
   [?? ??t]
   (let [fn-namespace (some-> m :ns ns-name name)
         header       (str fn-namespace "/" nm)
         sep       (ansi/white "│")
        ;; comment-color ansi/cyan
         ]
     [(partial ?* {:border? true :sep sep :indent 2 :bottom-margin 1})
      #(println (str
                 "\n\n"
                 (ansi/white "┌─ ") (ansi/bold header) (ansi/white " ───────────────────────") "\n"
                 sep))
      (fn [] nil)])))

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

(defn cssfn [[_ nm & args*]]
  #_(pprint+ "cssfn" {:nm nm :args args*})
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
    #_(pprint+ "cssfn" {:nm nm :args args* :css-arg css-arg})
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

(defn css-var-for-sexp [selector* css-prop]
  (let [sanitized-name (-> css-prop
                           (string/replace  #":" "_")
                           sanitize-for-css-var-name)]
    (str "--"
         selector*
         "_"
         sanitized-name)))

(defn css-var-string-!important
  [x selector* prop]
  #_(pprint+ "css-var-string-!important" (css-var-string (second x) "!important"))
  (if (list? (second x))
    (str "var(" (css-var-for-sexp selector* prop) ")!important")
    (css-var-string (second x) "!important")))

(defn !important-var? [x] (and (list? x) (= '!important (first x))))

(defn process-sexp [sexp selector* css-prop]
  (walk/postwalk
   (fn [x]
     (cond (cssfn? x) (cssfn x)
           (!important-var? x) (css-var-string-!important x selector* css-prop)
           :else x))
   sexp))

(defn process-value
  [v hydrated-k selector*]
   #_(? :process-value v)
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


(defn filter-map [m pred]
  (select-keys m (for [[k v] m :when (pred k v)] k)))

(defn positions
  [pred coll]
  (keep-indexed (fn [idx x]
                  (when (pred x)
                    idx))
                coll))
