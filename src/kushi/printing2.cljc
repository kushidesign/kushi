;; TODO - Confirm bling fns not being pulled into cljs
(ns kushi.printing2
  #?(:clj (:require [bling.core :refer [stack-trace-preview]]))
  (:require
   [bling.core :refer [callout bling]]
   [clojure.string :as string]
   [clojure.edn]
   [clojure.pprint :as pp :refer [pprint]]
   [expound.alpha :as expound]
   [kushi.config :as config :refer [user-config]]))

(defn re-seq-pos [pattern string]
  (let [m (re-matcher pattern string)]
    ((fn step []
       (when (. m find)
         (cons {:start (. m start) :end (. m end) :group (. m group)}
               (lazy-seq (step))))))))

(defn replace-in-str [f in from len nspaces]
  (let [before*         (subs in 0 from)
        [match
         carets]       (re-find #":____(\^+)( \")?$" before*)
        replacement    (when match
                         (bling "\"\n"
                                (string/join (repeat (inc nspaces) " "))
                                [:bold carets]))
        before         (if match (string/replace before* #":____\^+ \"?$" replacement) before*)
        after          (subs in (+ from len))
        being-replaced (subs in from (+ from len))
        replaced       (f being-replaced)
        result         (str before replaced after )]
    result))

(defn pprinted
  [{:keys [form-meta fname args :kushi/process] :as m}]
  (let [fname    (or fname (some-> process name symbol))
        sexp     (when fname (cons (symbol fname) args))
        pprinted (string/replace (with-out-str (pprint sexp)) #"\n$" "")
        pprinted (if (= fname "defclass")
                   (string/replace-first pprinted #"\n" "")
                   pprinted)
        lnum     (:line form-meta)]
    {:pprinted pprinted :lnum lnum}))


(defn with-line-numbers3
  [{:keys [pprinted lnum]}]
  (let [linenums   (->> pprinted
                        (re-seq #"\n")
                        count
                        (+ lnum)
                        (range lnum)
                        (map inc))
        pos        (map (fn [n pos] (dissoc (assoc pos :ln n) :group))
                        linenums
                        (re-seq-pos #"\n" pprinted))
        max-digits (-> linenums last str count)
        spaces     (fn [ln]
                     (let [num-digits (-> ln str count)
                           spaces     (string/join (repeat (inc (- max-digits
                                                                   num-digits))
                                                           " "))]
                       spaces))
        borderchar (bling [:subtle "|"])  #_"│"
        numbered   (reduce (fn [acc {:keys [start ln]}]
                             (let [spaces      (spaces ln)
                                   spaces-for-squiqqly (count (str ln
                                                                   spaces
                                                                   borderchar
                                                                   "  "))]
                               (replace-in-str (fn [_] (str "\n"  
                                                            (bling [:subtle ln])
                                                            spaces
                                                            borderchar
                                                            "  "))
                                               acc
                                               start
                                               1
                                               spaces-for-squiqqly)))
                           pprinted
                           (reverse pos))
        result     (str (bling [:subtle lnum])
                        (spaces (first linenums))
                        borderchar
                        "  "
                        numbered)]

    result))


(defn file+line+col-str
  [form-meta]
  (let [{:keys [file
                line
                column
                :printing/normal-font-weight?]} form-meta]
    (when file
      (bling [:italic (str file (when line (str ":" line ":" column)))]))))


#?(:clj
   (defn caught-exception2
     [{:keys [ex
              re
              sym
              args
              form-meta
              commentary]
       :as   m}]

     (let [file-info-str                   (file+line+col-str form-meta)
           args                            (if sym (cons sym args) args)
           {:keys [lnum pprinted]
            :as   m+}                      (pprinted (assoc m :args args))
           with-line-numbers               (if lnum
                                             (with-line-numbers3 m+)
                                             pprinted)]

       (callout {:type          :error
                 :label         "EXCEPTION (CAUGHT)"}

                (str file-info-str
                     "\n\n"
                     (when with-line-numbers
                       (str with-line-numbers))
                     (when commentary
                       (str "\n\n" commentary))

                     (some->
                      (.getMessage ex)
                      (string/replace #"\(" "\n(")
                      (->> (str "\n\n"
                                (bling [:italic.bold
                                        "Message from Clojure:"])
                                "\n")))

                     "\n\n"
                     (some->> (stack-trace-preview
                               {:error  ex
                                :regex  re
                                :depth  7
                                :header (bling [:italic.bold
                                                "StackTrace preview:"])})
                              #_(str "\n\n"
                                   (bling [:subtle.bold.italic
                                           "StackTrace preview:"])
                                   "\n" )))))))


(defn build-failure []
  (throw (Exception.
          (callout {:type :error}
                   (bling  "[kushi.core/kushi-debug]"
                           "\n\n"
                           "The required entry:\n"
                           [:bold ":css-dir"]
                           "\n"
                           "is missing from your project's " [:bold "kushi.edn"] " config."
                           "\n\n"
                           "Its value must be a path relative to proj root e.g:"
                           "\n"
                           [:bold "\"public/css\""]
                           " or "
                           [:bold "\"resources/public/css\"."]
                           "\n\n"
                           "https://github.com/kushidesign/kushi#configuration-options"
                           )))))

(defn entries-msg [coll msg k]
  (when coll
    (let [plural? (< 1 (count coll))
          args    (->> coll
                       (map k)
                       (map #(if (string? %) (str "\"" % "\"") %))
                       (string/join "\n"))
          msg     (apply bling
                         (cons msg 
                               (interpose 
                                "\n"
                                (map (fn [s]
                                       [:bold s])
                                     (string/split args #"\n")))))
          matches (re-seq #"\b([a-zA-Z]*)\|([a-zA-Z]*)\b" msg)
          ret     (reduce (fn [acc [match plural singular]]
                            (string/replace acc match (if plural? plural singular)))
                          msg
                          matches)]
      ret)))

(defn simple-warning2
  [{:keys [sym
           args
           hint
           form-meta
           commentary
           expound-str
           doc
           :clojure.spec.alpha/problem
           :kushi/process]
    weird-entries :entries/weird
    bad-entries :entries/bad
    missing-entries :entries/missing
    bad-args :args/bad
    :as   m}]


  (let [file-info-str           (file+line+col-str form-meta)
        fname                   (some-> process name)
        ;; args                    (if (= fname "defclass") (cons sym args) args)
        {:keys [lnum pprinted]
         :as   m+}              (when (or fname process)
                                  (pprinted (assoc m :args args :fname fname)))
        with-line-numbers       (when m+
                                  (if lnum (with-line-numbers3 m+) pprinted))
        doc                     (when doc
                                  (str (subs (str process) 1)
                                       " docs:\n\n"
                                       (string/replace doc #"\n   " "\n")))
        weird-entries           (entries-msg weird-entries
                                             "The following map entries|entry are|is not recognized:\n"
                                             :entry)
        bad-entries             (entries-msg bad-entries
                                             "The supplied values|value for the following map entries|entry are|is invalid:\n"
                                             :entry)
        missing-entries         (entries-msg missing-entries
                                             "The following required map entries|entry are|is missing:\n"
                                             :key)
        bad-args                (entries-msg bad-args
                                             "Invalid args:\n"
                                             :arg)
        ln                      #(when % (str "\n\n" %))]
    (callout {:type :warning}
          (str file-info-str
               commentary
               (ln with-line-numbers)
               (ln bad-args)
               (ln missing-entries)
               (ln bad-entries)
               (ln weird-entries)
               (ln expound-str)
               (ln hint)
               (ln doc)))    
    ))

(defn simple-bad-global-selector-key-warning
  [m*]
  (when-let [invalid-args (:invalid-args m*)]
    (when (seq invalid-args)
      (when-not (and (map? invalid-args)
                     (= (-> invalid-args first second first)
                        [:kushi-debug? true]))
        (simple-warning2
         (assoc m*
                :commentary
                (str "Discarding the global \"*\" selector from theme :ui entry.\n"
                     "Use something like :body or :#my-app-id instead.")))))))

(defn kushi-expound [spec x]
  (expound/expound-str spec
                       x
                       (:warnings-and-errors user-config)))
