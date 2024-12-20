(ns kushi.css.build.analyzer
  (:require
   [clojure.spec.alpha]
   [clojure.string :as str]
   [clojure.tools.reader :as reader]
   [clojure.tools.reader.reader-types :as reader-types]
   [clojure.walk :as walk]
   [fireworks.core :refer [? !?] :rename {? ff}]
   [kushi.css.specs :as kushi-specs]
   [kushi.css.build.utility-classes :as utility-classes]))

(defn ? [& args]
  (last args))

(defn reduce-> [init rfn coll]
  (reduce rfn init coll))

(defn reduce-kv-> [init rfn coll]
  (reduce-kv rfn init coll))

(defn lookup-alias [svc alias-kw]
  (get-in svc [:aliases alias-kw]))

(def plain-numeric-props
  #{:flex :order :flex-shrink :flex-grow :z-index :opacity})

(defn convert-num-val [index prop num]
  (if (contains? plain-numeric-props prop)
    (str num)
    (or (get-in index [:svc :spacing num])
        (throw
          (ex-info
            (str "invalid numeric value for prop " prop)
            {:prop prop :num num})))))

(defn add-warning [svc form warning-type warning-vals]
  ;; NEW Silence these for now ------------------------
  #_(update form :warnings conj (assoc warning-vals :warning-type warning-type))
  #_(ff warning-type)
  ;; NEW ----------------------------------------------
  form)

(declare add-part)

(defn add-alias [svc form alias-kw]
  (let [alias-val (lookup-alias svc alias-kw)]
    (cond
      (not alias-val)
      (add-warning svc form ::missing-alias {:alias alias-kw})

      (map? alias-val)
      (add-part svc form alias-val)

      (vector? alias-val)
      (reduce #(add-part svc %1 %2) form alias-val)

      :else
      (add-warning svc form ::invalid-alias-replacement {:alias alias-kw :val alias-val})
      )))

(defn add-map [svc form defs]
  (reduce-kv
    (fn [form prop val]
      (let [[form val]
            (cond
              ;; {:thing "val"}
              (string? val)
              [form val]

              ;; {:thing 4}
              (number? val)
              ;; Kushi - we don't want to do this conversion
              [form val #_(convert-num-val svc prop val)]
              

              ;; {:thing :alias}
              (keyword? val)
              (let [alias-value (lookup-alias svc val)]
                (cond
                  (nil? alias-value)
                  [(add-warning svc form ::missing-alias {:alias val})
                   nil]

                  (and (map? alias-value) (contains? alias-value prop))
                  [form (get alias-value prop)]

                  (string? alias-value)
                  [form alias-value]

                  (number? alias-value)
                  ;; Kushi - we don't want to do this conversion
                  [form alias-value #_(convert-num-val form prop alias-value)]

                  :else
                  [(add-warning svc form ::invalid-map-val {:prop prop :val val})
                   nil]
                  )))]

        (assoc-in form [:rules (:sel form) prop] val)))

    form
    defs))

(defn make-sub-rule [{:keys [stack rules] :as form}]
  (update form :sub-rules assoc stack rules))

(defn add-group* [svc form group-sel group-parts]
  (cond
    (not (string? group-sel))
    (add-warning svc form ::invalid-group-sel {:sel group-sel})

    ;; media queries
    (str/starts-with? group-sel "@media")
    (let [{:keys [rules media]} form

          new-media
          (if-not (seq media)
            group-sel

            ;; attempt to combine media queries via and
            ;; FIXME: can all @media queries combined like this?
            ;;   @media (min-width: 300) @media print
            ;; combined to
            ;;   @media (min-width: 300) and print
            ;; so there is only one media rule and no nesting?
            (str media " and " (subs group-sel 7)))]

      (-> form
          (assoc :rules {} :media new-media)
          (reduce-> #(add-part svc %1 %2) group-parts)
          ((fn [{:keys [rules] :as form}]
             (assoc-in form [:at-rules new-media] rules)))
          (assoc :rules rules :media media)))

    (str/index-of group-sel "&")
    (let [{:keys [rules sel]} form]

      (if (not= sel "&")
        (throw (ex-info "tbd, combining &" {:sel sel :group-sel group-sel}))
        (-> form
            (assoc :sel group-sel)
            (reduce-> #(add-part svc %1 %2) group-parts)
            (assoc :sel sel))))

    :else
    (add-warning svc form ::invalid-group-sel {:sel group-sel})))

(defn add-group [svc form [sel & parts]]
  (if (keyword? sel)
    (if-some [alias-value (lookup-alias svc sel)]
      (add-group* svc form alias-value parts)
      (add-warning svc form ::group-sel-alias-not-found {:alias sel}))
    (add-group* svc form sel parts)))

(defn add-part [svc form part]
  (cond
    (string? part) ;; "other-class", passthrough, ignored here, handled in macro
    (? (str part " is string,\nreturning form...") form)

    (keyword? part) ;; :px-4 alias
    (? (str part " is keyword,\nreturning (add-alias svc form part)")
       (add-alias svc form part))

    (map? part) ;; {:padding 4}
    (? (str part "\n is map, returning (add-map svc form part)")
       (add-map svc form part)) 

    (vector? part) ;; ["&:hover" :px-4] subgroup
    (? (str part " is vector, returning (add-group svc form part)")
       (add-group svc form part))

    :else
    (? (add-warning svc form ::invalid-part part))))

(defn process-form [svc {:keys [form] :as form-info}]
  (? :trace
     (-> form-info
         (assoc :sel "&" :media nil :rules {} :at-rules {} :warnings [])
         (reduce-> #(add-part svc %1 %2) form)
         (dissoc :stack :sel :media))))

(defn flatten-prefix-lists [form]
  (let [prefix-parts (take-while #(not (keyword? %)) form)
        prefix-count (count prefix-parts)
        args (drop prefix-count form)]

    (if-not (even? (count args))
      (throw (ex-info "failed to parse ns require" {:form form}))
      (let [args-map (apply array-map args)]
        (if (= 1 prefix-count)
          [(assoc args-map :require (first form))]
          (let [[prefix & suffixes] prefix-parts]
            (when (string? prefix)
              (throw (ex-info "failed to parse ns require, string requires can't have prefix lists" {:form form})))

            (loop [expanded
                   (if-not (seq args)
                     []
                     [(assoc args-map :require (first form))])
                   suffixes suffixes]

              (if-not (seq suffixes)
                expanded
                (let [[part & more] suffixes]
                  (cond
                    (symbol? part)
                    (recur
                      (conj expanded {:require (symbol (str prefix "." part))})
                      more)

                    (sequential? part)
                    (recur
                      (into expanded
                        (flatten-prefix-lists
                          (cons
                            (symbol (str prefix "." (first part)))
                            (rest part))))
                      more)

                    :else
                    (throw (ex-info "failed to parse ns form, unexpected prefix part" {:form form :part part}))))))))))))

(defn parse-ns-require-part [state part]
  (cond
    (symbol? part)
    (update state :requires conj part)

    (sequential? part)
    (reduce
      (fn [state {:keys [require as-alias as refer rename] :as opts}]
        ;; non-loading, only [some.ns :as-alias foo], do not add to :requires
        (if (and (= 2 (count opts)) as-alias)
          (update state :require-aliases assoc as-alias require)
          (-> state
              (update :requires conj require)
              (cond->
                as
                (update :require-aliases assoc as require)
                as-alias
                (update :require-aliases assoc as-alias require)
                (seq refer)
                (reduce->
                  ;; not constructing a {css kushi.css.build/css} qualified symbol here
                  ;; since require may be a string in CLJS
                  #(assoc-in %1 [:refer %2] {:require require :sym %2})
                  refer)
                (seq rename)
                (reduce-kv->
                  (fn [state from to]
                    (update state :refer
                      (fn [m]
                        (-> m
                            (cond->
                              ;; only remove from refer if it came from same form
                              ;; [a :refer (css)]
                              ;; [b :refer (css) :rename {css c}]
                              ;; FIXME: verify this is actually what clojure does?
                              (contains? refer from)
                              (dissoc from))
                            (assoc to {:require require :sym from})))))
                  rename)))))
      state
      (flatten-prefix-lists part))

    ;; ignore parts like  :reload :reload-all etc
    :else
    state))

(defn parse-ns-require-form [state [require-kw & parts]]
  (reduce parse-ns-require-part state parts))

(defn parse-ns [state form]
  (let [[_ ns maybe-meta]
        form

        ;; FIXME: should this filter kushi.css.build/* already? don't really need other meta
        ns-meta
        (merge
          ;; don't care about the reader metadata, only added stuff
          (dissoc (meta ns) :source :line :column :end-line :end-column)
          (when (map? maybe-meta)
            maybe-meta))

        ns-requires
        (->> form
             (drop 2)
             (filter #(and (list? %) (= :require (first %)))))]

    (-> state
        (assoc :ns ns :ns-meta ns-meta)
        (reduce-> parse-ns-require-form ns-requires))))

(defn find-css-calls [state form]
  (cond
    (map? form)
    (reduce find-css-calls state (vals form))

    (list? form)
    (case (first form)
      ;; (ns foo {:maybe "meta") ...)
      ns
      (parse-ns state form)

      ;; don't traverse into (comment ...)
      comment
      state

      ;; thing we actually look for
      ;; FIXME: make this use require-aliases/refers, should find aliased uses
      ;; FIXME: also make this extensible in some way so it can find
      ;; other forms that maybe expand to css via some macro
      css
      (update state 
              :css
              conj
              (-> (meta form)
                  (dissoc :source)
                  ;; want [:px-4] instead of (css :px-4)
                  ;; don't really care about the (css ...) part later
                  ;; other forms also maybe won't have this
                  (assoc :form (with-meta (vec (rest form))
                                 {:macro 'kushi.css/css}))))

      sx
      (update state 
              :css
              conj
              (-> (meta form)
                  (dissoc :source)
                  (assoc :form (with-meta (vec (rest form))
                                 {:macro 'kushi.css/sx}))))

      utilize
      (let [reqs (volatile! #{})]
        (walk/postwalk
         (fn [x]
           (when-let [cn (when (or (string? x) (keyword? x))
                           (let [nm (name x)]
                             (if (str/starts-with? nm ".")
                               nm
                               (str "." nm))))]
             (when (contains? utility-classes/utility-class-ks-set cn)
               (vswap! reqs conj (keyword cn)))))
         form)
        (update state
                :utilized
                conj
                (-> @reqs)))

      defcss
      (update state
              :defcss
              conj
              (let [[_ sel & rest-of-form] form
                    enable-css-layers?     false
                    at-layer?              (str/starts-with? sel "@layer ")
                    keyframes?             (str/starts-with? sel "@keyframes")]
                (-> (meta form)
                    (dissoc :source)
                    (assoc :sel (if enable-css-layers?
                                  sel
                                  (if at-layer?
                                    (-> sel
                                        (str/split #"[\t\n\r\s]+")
                                        last)
                                    sel)))
                    (assoc :layer (cond 
                                    keyframes?
                                    :keyframes

                                    (and at-layer?
                                         (not enable-css-layers?))
                                    (some-> sel
                                            (str/split #"[\t\n\r\s]+")
                                            second
                                            keyword)

                                    :else
                                    :defcss))
                    (assoc :form (vec rest-of-form)))))

      ;; any other list
      (reduce find-css-calls state form))

    ;; sets, vectors
    (coll? form)
    (reduce find-css-calls state form)

    :else
    state))

(defn find-css-in-source [src]
  ;; shortcut if src doesn't contain any css, faster than parsing all forms
  (let [has-css? (or (str/index-of src "(css")
                     (str/index-of src "(sx")
                     (str/index-of src "(defcss")
                     (str/index-of src "(utilize"))
        reader (reader-types/source-logging-push-back-reader src)
        eof #?(:clj (Object.) :cljs (js-obj))]

    (loop [ns-found false
           state
           {:css             []
            :defcss          []
            :utilize         []
            :ns              nil
            :ns-meta         {}
            :require-aliases {}
            :requires        []}]

      (let [form
            (binding
              [reader/*default-data-reader-fn*
               (fn [tag data] data)

               ;; used for ::alias/keywords
               reader/*alias-map*
               (fn [sym]
                 (get (:require-aliases state) sym sym))

               ;; used for ::keywords
               ;; don't know actual ns until ns form is parsed
               *ns*
               (create-ns (or (:ns state) 'user))]

              (try
                (reader/read {:eof eof :read-cond :preserve} reader)
                (catch #?(:clj Exception :cljs :default) e
                  (throw (ex-info "failed to parse ns" {:ns (:ns state)} e)))))]

        (if (identical? form eof)
          state

          (let [next-state (find-css-calls state form)]
            (cond
              (and (not ns-found) (not (:ns next-state)))
              ;; do not continue without ns form being first, just don't look for css
              next-state

              (not has-css?)
              next-state

              :else
              (recur true next-state))))))))
