(ns kushi.printing2
  #?(:clj (:require [io.aviso.ansi :as ansi]))
  (:require
   [clojure.string :as string]
   [clojure.edn]
   [clojure.pprint :as pp :refer [pprint]]
   [expound.alpha :as expound]
   [kushi.config :as config :refer [user-config]] ))

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
        replacement    (when match (str "\"\n" (string/join (repeat (inc nspaces) " ")) ansi/bold-font carets ansi/reset-font))
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

(defn with-line-numbers
  [{:keys [pprinted lnum]}]
  (if lnum
    (let [linenums   (->> pprinted (re-seq #"\n") count (+ lnum) (range lnum) (map inc))
          pos        (map (fn [n pos] (dissoc (assoc pos :ln n) :group)) linenums (re-seq-pos #"\n" pprinted))
          max-digits (-> linenums last str count)
          spaces     (fn [ln]
                       (let [num-digits (-> ln str count)
                             spaces     (string/join (repeat (inc (- max-digits num-digits)) " "))]
                         spaces))
          borderchar "|"  #_"│"
          numbered   (reduce (fn [acc {:keys [start ln]}]
                               (let [spaces      (spaces ln)
                                     spaces-for-squiqqly (count (str ln spaces borderchar "  "))]
                                 (replace-in-str (fn [_] (str "\n"  ln spaces borderchar "  ")) acc start 1 spaces-for-squiqqly)))
                             pprinted
                             (reverse pos))
          result*    (str lnum (spaces (first linenums)) borderchar "  " numbered)

          result     (-> result*
                         (string/replace #"\"__bold__" ansi/bold-font)
                         (string/replace #"__bold__\"" ansi/reset-font))]
      result)
    pprinted))

(defn with-line-numbers2
  [{:keys [pprinted lnum]}]
  (let [linenums   (->> pprinted (re-seq #"\n") count (+ lnum) (range lnum) (map inc))
        pos        (map (fn [n pos] (dissoc (assoc pos :ln n) :group)) linenums (re-seq-pos #"\n" pprinted))
        max-digits (-> linenums last str count)
        spaces     (fn [ln]
                     (let [num-digits (-> ln str count)
                           spaces     (string/join (repeat (inc (- max-digits num-digits)) " "))]
                       spaces))
        borderchar "|"  #_"│"
        numbered   (reduce (fn [acc {:keys [start ln]}]
                             (let [spaces      (spaces ln)
                                   spaces-for-squiqqly (count (str ln spaces borderchar "  "))]
                               (replace-in-str (fn [_] (str "\n"  ln spaces borderchar "  ")) acc start 1 spaces-for-squiqqly)))
                           pprinted
                           (reverse pos))
        result     (str #_ansi/bold-font lnum (spaces (first linenums)) borderchar "  " numbered #_ansi/reset-font)]

    result))

(def border-char "◢◤")
(defn border-str [n] (string/join (repeat n border-char)))
(def border-len 50)
(def alert-indent 4)
(def unbroken-border (border-str (/ border-len 2)))

(defn simple-alert-header-border-top [header color]
  (str (when color ansi/bold-red-font)
       border-char
       border-char
       " "
       ansi/bold-font
       header
       ansi/reset-font
       (when color ansi/bold-red-font)
       " "
       (string/join (repeat (/ (- border-len (dec alert-indent) (+ 2 (count header)) 2) 2)
                            border-char))
       ansi/reset-font))


(defn simple-alert-header2 [header file-info-str color]
  (str
   "\n"
   (when-let [user-warning-banner (:log-warning-banner user-config)]
     (when (some->> user-warning-banner seq (every? string?))
       (str
        ansi/bold-font
        "\n"
        (string/join "\n" user-warning-banner)
        "\n\n"
        ansi/reset-font
        )))
   (simple-alert-header-border-top header color)
   (when file-info-str (str "\n\n" "File: " file-info-str))))

(defn file+line+col-str
  [form-meta]
  (let [{:keys [file
                line
                column
                :printing/normal-font-weight?]} form-meta]
    (when file
      (str file
           (when line
             (if normal-font-weight?
               (str ":" line ":" column)
               (str ":" ansi/bold-font line ansi/reset-font ":" ansi/bold-font column ansi/reset-font)))))))

(defn caught-exception [{:keys [ex
                                sym
                                args
                                fname
                                form-meta
                                exception-message
                                top-of-stack-trace
                                commentary]
                         :as   m}]
  (let [file-info-str (file+line+col-str form-meta)
        args (if sym (cons sym args) args)
        {:keys [lnum pprinted]
         :as   m+}                      (pprinted (assoc m :args args))
        with-line-numbers               (if lnum
                                          (with-line-numbers2 m+)
                                          pprinted)]
    (println
     (str (simple-alert-header2 "EXCEPTION CAUGHT" file-info-str :red)
          (when with-line-numbers
            (str "\n\n" with-line-numbers))
          (when commentary
            (str "\n\n" commentary))
          (when exception-message
            (str "\n\n"
                 ansi/italic-font
                 ansi/cyan-font
                 "(.getMessage ex) =>\n"
                 ansi/reset-font
                 exception-message))

          (when top-of-stack-trace
            (str "\n\n"
                 ansi/italic-font
                 ansi/cyan-font
                 "StackTraceElement[0] =>\n"
                 ansi/reset-font
                 top-of-stack-trace))

          (when-let [stack-trace (.getStackTrace ex)]
            (str "\n\n"
                 ansi/italic-font
                 ansi/cyan-font
                 "StackTrace =>\n"
                 ansi/reset-font
                 (with-out-str (pprint stack-trace))))

          (when-not (or exception-message top-of-stack-trace)
            (str "\n\n"
                 ansi/italic-font
                 ansi/cyan-font
                 "Clojure says:\n"
                 ansi/reset-font
                 (with-out-str (pprint ex))))

          "\n\n"
          ansi/bold-red-font
          unbroken-border
          ansi/reset-font))))

(defn build-failure []
  (throw (Exception.
          (str "\n\n"
               (simple-alert-header2 "EXCEPTION" "[kushi.core/kushi-debug]")
               "The required entry `:css-dir` is missing from your kushi.edn config"
               "\n\n"
               "Its value must be a path relative to proj root e.g \"public/css\" or \"resources/public/css\"."
               "\n\n"
               "https://github.com/kushidesign/kushi#configuration-options"
               "\n\n"
               unbroken-border
               "\n\n\n"))))

(defn entries-msg [coll msg k]
  (when coll
    (let [plural? (< 1 (count coll))
          args    (->> coll
                       (map k)
                       (map #(if (string? %) (str "\"" % "\"") %))
                       (string/join "\n"))
          msg     (str msg ansi/bold-font args ansi/reset-font)
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
           :clojure.spec.alpha/problems
           :kushi/process]
    weird-entries :entries/weird
    bad-entries :entries/bad
    missing-entries :entries/missing
    bad-args :args/bad
    :as   m}]

  #_(println :simple-warning2
             (keyed
      ;;  sym
      ;;  args
      ;;  hint
      ;;  fname
      ;;  form-meta
      ;;  commentary
              problems))

  (let [file-info-str           (file+line+col-str form-meta)
        fname                   (some-> process name)
        ;; args                    (if (= fname "defclass") (cons sym args) args)
        {:keys [lnum pprinted]
         :as   m+}              (when (or fname process)
                                  (pprinted (assoc m :args args :fname fname)))
        with-line-numbers       (when m+
                                  (if lnum (with-line-numbers2 m+) pprinted))
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

    (println
     (str (simple-alert-header2 "WARNING" file-info-str nil)
          (ln commentary)
          (ln with-line-numbers)
          (ln bad-args)
          (ln missing-entries)
          (ln bad-entries)
          (ln weird-entries)
          (ln expound-str)
          (ln hint)
          (ln doc)
          "\n\n"
          unbroken-border
          "\n"))))

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
