(ns kushi.jeff
  (:require
   [kushi.util :refer [maybe]]))

(defn- tuple?
  [x & preds]
  (boolean (and (coll? x)
                (= (count x) (count preds))
                (every? true?
                        (map (fn [x pred] (pred x))
                             x
                             preds)))))

(defn- or?
  [x & preds]
  (boolean (some #(% x) preds)))

(defn- not?
  [x pred]
  (not (pred x)))

(defn- and?
  [x & preds]
  (every? #(% x) preds))

(defn valid-count?
  [n* x f]
  (if-let [n (some-> n* (maybe pos-int?))]
    (f n (count x))
    true))

(defn opt-pred?
  [opt opt-type-pred x]
  (if opt
    (when (opt-type-pred opt)
      (opt x))
    true))

(defn every-opts?
  "Validate all the optional opts.
   Used for coll-of?, map-of?, every?, and every-kv?"
  [x opts]
  (and
   (valid-count? (:count opts) x =)
   (valid-count? (:min-count opts) x <=)
   (valid-count? (:max-count opts) x >=)
   (opt-pred? (:kind opts) fn? x)
   (opt-pred? (:distinct opts) boolean? x)))

(defn- coll-of?
  [x pred & {:as opts}]
  (boolean (when (and (coll? x) (every-opts? x opts))
             (every? pred x))))

(defn- map-of?
  [x pred & {:as opts}]
  (boolean (when (every-opts? x (assoc opts :kind map?))
             (every? pred x))))

(defn- _every?
  [x pred & {:as opts}]
  (boolean (when (and (coll? x) (every-opts? x opts))
             (every? pred x))))

(defn cat?
  {:specoli/id :cat}
  [mm x & kpreds]
  (boolean (when
            ;; checks for conformance of kpreds shape
            (and (coll? x)
                 (some-> kpreds
                         (maybe coll?)
                         (maybe #(even? (count %)))
                         (maybe #(every? fn? %))))
             ;; not sure you need to do this checking until
             ;; have an `explain` or a `conform` where
             ;; the keys are actually used for something

             (and
              (if-let [pass? (= (!? (count x))
                                (!? (count kpreds)))]
                (do
                  true)
                (do
                  (!? :comment
                     mm
                     #_{:pred-name (symbol (:specoli/pred-name mm))
                        :schema    (walk/postwalk #(if (string? %) (symbol %) %)
                                                  (:specoli/def mm))})
                  ;; (? kpreds)
                  ;; (? (-> cat? var meta :specoli/id))
                  false))

              (some->> (partition 2 kpreds)
                       (map-indexed
                        (fn [i [_ pred]]
                          (some-> (!? (nth x i))
                                  (!? pred))))
                       (every? true?))))))

;; s/keys 
;; s/keys*
;; s/merge
;; s/every-kv
;; s/conform ;; maybe no conformance?
;; s/alt
;; s/*
;; s/+
;; s/?
;; s/describe
;; s/valid

;;count 1?
;; 

;; All runtime (no macro) performance
;; Maybe macros should expand for perf reasons?
;; With macro performance
;; All runtime vs macro bundle size 


(def specoli-fns-by-key
  {:coll-of coll-of?
   :map-of  map-of?
   :every   _every?
   :and     and?
   :tuple   tuple?
   :or      or?
   :not     not?
   :cat     cat?})


(def specoli-keys
  (->> specoli-fns-by-key
       keys
       (into #{})))


(def specoli-fns
  (->> specoli-fns-by-key
       vals
       (into #{})))


(defn- specoli-key->sym [x]
  (-> x
      name
      (str "?")
      symbol))


(def specoli-syms-by-key
  '{:coll-of coll-of?
    :map-of  map-of?
    :every   _every?
    :and     and?
    :tuple   tuple?
    :or      or?
    :not     not?
    :cat     cat?})

(def specoli-keys-by-sym
  '{coll-of? :coll-of
    map-of?  :map-of
    _every?  :every
    and?     :and
    tuple?   :tuple
    or?      :or
    not?     :not
    cat?     :cat})

(def specoli-syms
  (->> specoli-syms-by-key
       vals
       (into #{})))


(defn- fn-call [x {:keys [schema pred-name] :as mm}]
  (when-let [sym (some-> x
                         (maybe vector?)
                         (nth 0 nil)
                         (maybe specoli-syms))]
    (let [spec-vec (into [(get specoli-keys-by-sym sym nil)]
                         (map str (rest x)))
          mm       (conj mm {:spec-vec spec-vec})]
      (!? (with-meta
           (list 'fn
                 ['x]
                 (concat [sym mm 'x] (rest x)))
           mm)))))

;; (defrecord Fname [fname])

;; (defmacro specoli
;;   [coll]
;;   (let [specdef (walk/postwalk 
;;                 ;;  #(if (symbol? %) (->Fname (str %)) %)
;;                  #(if (symbol? %) (str %) %)
;;                  coll)
;;         f (walk/postwalk
;;            (fn [x]
;;              (or
;;                (get specoli-syms-by-key x nil)
;;                (fn-call x specdef)
;;                x))
;;            coll)
;;         f (with-meta f {:specoli/def specdef})]
;;     `~f))

(defmacro defpred
  [sym coll]
  (let [pred-name (str sym)
        mm        {:specoli/schema    (walk/postwalk
                                       #(if (symbol? %) (str %) %)
                                       coll)
                   :specoli/pred-name pred-name}
        f         (walk/postwalk
                   (fn [x]
                     (or
                      (get specoli-syms-by-key x nil)
                      (fn-call x mm)
                      x))
                   coll)
        f         (with-meta f mm)]
    `(def ~sym ~f)))

;; runtime version
(defn specoli2 [coll]
  (walk/postwalk
   (fn [x]
     (cond
       ;; Use an `or` thing like macro version?
       (contains? specoli-keys x)
       (get specoli-fns-by-key x)

       (and (vector? x)
            (contains? specoli-fns (nth x 0 nil)))
       (fn [z] (apply (nth x 0 nil) (cons z (rest x))))

       :else
       x))
   coll))

(defpred duh-cat? [:cat even? keyword?])

(!? duh-cat?)

(def order ["first"
            "second"
            "third"
            "fourth"
            "fifth"
            "sixth"
            "seventh"
            "eighth"
            "ninth"
            "tenth"
            "eleventh"
            "twelvth"])

(def bling-tag-for-value :neutral)
(def missing-file-str "<filename/missing>")

(def strj string/join)

(defn invalid-opt-warning!
  [{:keys [point-of-interest-opts callout-opts]}]
  (let [message      (point-of-interest point-of-interest-opts)
        callout-opts (merge callout-opts {:padding-top 1})]
    (callout callout-opts message)))

(defn file-line-column
  [{:keys [file line column]}]
  (bling (if (= missing-file-str file) file [:bold line])
         ":"
         [:bold line]
         ":"
         [:bold column]))

(defn invalid-arg-warning!
  [m]
  (let [message
        (point-of-interest
         (merge m
               { :header (str "Invalid"
                              (str " " (nth order (:arg-index m) nil))
                              " arg to "
                              (str (:fn-ns m) "/" (:fn-name m)))
                 :text-decoration-color :orange
                 :text-decoration-style :dotted
                 :text-decoration-index 1
                 :file                 (bling [:bold (:file m)])
                 :file-line-column     (file-line-column m)}))

        callout-opts
        {:type           :warning
         :label-theme    :marquee
         :padding-top    1
         :padding-bottom 1
         :padding-left   2
         :margin-bottom  0
         ;; :border-weight :medium
         :label         "Warning: Invalid arg value"
         }]
    (callout callout-opts message)))

  (defn fqns-sym
    [{:keys [fn-ns fn-name fn-name-replace-re] :as m}]
    (symbol (str fn-ns
                 "/"
                 (if fn-name-replace-re
                   (string/replace (name fn-name)
                                   fn-name-replace-re
                                   "")
                   (name fn-name)))))

(defn pprint-max [v n]
  (-> v
      (pprint {:max-width n})
      with-out-str
      (string/replace #"\n$" "")))

(defn str++ [n s]
 (strj (repeat n s)))

(defn bad-value-lines-multi
  [{:keys [ret* label-col-len *sq bv-lns*]}]
  (let [closing-char    (re-find #".$" ret*)
        label-col-spc+1 (str++ label-col-len " ")
        truncated-str   (vreset! *sq
                                 (str label-col-spc+1  " ..." closing-char))]
    (concat (take 1 bv-lns*)
            (map (fn [%] (bling [bling-tag-for-value
                                 (str (strj label-col-spc+1) %)]))
                 (drop 1 (take 5 bv-lns*)))
            [(bling [:neutral truncated-str])])))

(defn bad-value-lines*
  [bad-value-numlines
   label-col-len
   bv-lns*
   ret*]
  (let [*sq      (volatile! "")
        lns      (if (<= 4 bad-value-numlines)
                   (bad-value-lines-multi
                    (keyed [ret*
                            label-col-len
                            *sq
                            bv-lns*]))
                   bv-lns*)
        squiggly (str++ (if (< 1 bad-value-numlines)
                          (count (string/trim @*sq))
                          (count (strj "\n" lns)))
                        "^")]
    [lns squiggly]))

(defn bad-value-display [x label-col-len]
  (let [ret*
        (pprint-max x 33)

        bv-lns*
        (string/split ret* #"\n")

        bad-value-numlines
        (or (some->> bv-lns* count) 1)

        [bad-value-lines squiggly]
        (bad-value-lines* bad-value-numlines
                          label-col-len
                          bv-lns*
                          ret*)]
    {:bad-value-display (strj "\n" bad-value-lines)
    ;;  :squiggly          squiggly
     }))

(defn label-column-length [header-labels]
  (->> header-labels
       keys
       (map count)
       (apply max)
       (+ 2))) ; <- + 2 for ": "

(declare header-labels-by-label)


(defn squiggly-label-column [n]
  (or (-> (maybe n pos-int?)
          inc
          (repeat " ")
          (strj))
      "             "))
(defn label-column
  ([header-labels n s]
   (label-column header-labels n s nil))
  ([header-labels n s {:keys [fn-name fn-ns]}]
   (into []
         (concat
          [[:italic (str s
                         ":"
                         (strj (repeat (- n (count (str s ":"))) " ")))]]
          (if (and fn-name fn-ns)
            #_[[:bold fn-name] " (" fn-ns "/" fn-name ")"]
            [fn-ns "/" fn-name]
            [[bling-tag-for-value (get header-labels s)]])))))

;; TODO
;; get name of arg
;; maybe show args vector with squig



(defn header-labels-by-label
  [{:keys [form file line column arg-index arg-name argslist pred pred-name] :as m}]
  (array-map
   "expected" pred-name
   "schema"  (let [{specoli-pred-name :specoli/pred-name
                      specoli-schema    :specoli/schema}
                     (-> pred meta)]
                 (if specoli-schema
                   (cond (string? specoli-schema)
                         (symbol specoli-schema)
                         (vector? specoli-schema)
                         (mapv #(if (string? %) (symbol %) %) specoli-schema))
                   pred-name))
   "received*"  (str (nth form (inc arg-index) nil))
   "received"  (nth form arg-index nil)
   "call site" (bling [:bold (str file ":" line ":" column)])
   "function"  (fqns-sym m)
   "arg index" arg-index

  ;; TODO - arg-index, arg-name, & argslist, do some koind of vector
  ;; representation of argslist:
  ;; [n coll]
  ;;    ^^^^

  ;; OR do a diagram of fn definition, just first line
  ;; (defn myfn
  ;;   [n coll] ...)
  ;;      ^^^^

  ;;  "argument"  (str "[" arg-name "]")
  ))

(defn summary-table-2
  [{:keys [x]
    :as   m}]
  (? m)
  (let [header-labels
        (header-labels-by-label m)

        label-col-len
        (label-column-length header-labels)

        {:keys [bad-value-display squiggly]}
        (bad-value-display x label-col-len)

        header-labels
        (assoc header-labels "received" bad-value-display)

        squiggly-label-column
        (squiggly-label-column label-col-len)

        label-column
        (partial label-column header-labels label-col-len)

        received-sym
        (label-column "received*")

        received-val
        (label-column "received")

        val-is-bound?
        (not= (-> (:form m) rest (nth (:arg-index m)))
              x)]
   (apply bling
          (apply
           concat
           (interpose
            "\n\n"
            (remove
             nil?
             [
              (when-not val-is-bound?
                received-val)
              (when val-is-bound?
                received-sym)
              (when val-is-bound?
                received-val)
              (label-column "expected")
              (label-column "schema")
              (when squiggly
                ["\n" squiggly-label-column [:bold.orange squiggly]])
              (label-column "function" m)
              (label-column "arg index")]))))))

  (defn validation-info
    [{:keys [opt-sym pred]}]
    (bling "\n"
           "Value for the "
           [:bold (str ":-" opt-sym)]
           " option must pass this predicate:"
           "\n\n"
           [:bold (if (set? pred) (str pred) pred)]))

  ;; hiccup version
  ;; '[[:br]
  ;;   [:p "Value for the "
  ;;    [:bold (str ":-" opt-sym)]
  ;;    " option must pass this predicate:" [:br]
  ;;    [:bold (if (set? pred) (str pred) pred)]]
  ;;   [:br]
  ;;   [:p "Check out the docs for more info:" [:br]
  ;;    [:a {:href "https://kushi.design"} "https://kushi.design"]]]

(defn validation-map
  [args
   i
   {:keys [pred pred-name] :as validation-map} ]
  (let [v (nth args i :not-supplied)]
    (when-not (pred v)
      (assoc validation-map
             :pred      pred
             :pred-name pred-name
             :value     v
             :arg-index i))))

;; TODO get schema info 
(defn validate-args
  [{:keys [validate fvar file line column]
    :as   src-form-meta}
   form
   & args]
  (let [
        ;; src-form-meta (assoc src-form-meta :form form)
        fails         (keep-indexed (partial validation-map args) validate)]
    (doseq [{:keys [arg-index
                    fn-name
                    fn-ns
                    arg-name
                    pred
                    pred-name]
             x :value}         fails
            :let [file (or file missing-file-str)
                  arglists (-> fvar meta :arglists)
                  m (keyed [fn-name
                            fn-ns
                            arglists
                            arg-index
                            arg-name
                            pred
                            pred-name
                            form
                            file
                            line
                            column
                            x
                            #_src-form-meta
                            ])
                  body (summary-table-2 m)]]


     #_(invalid-arg-warning!
      {:point-of-interest-opts
       (merge
        {:header (summary-table-2 m)
         :body   (warning-body m)
         :file   (call-site src-form-meta)}
        src-form-meta)})

     ;; version 2
     (invalid-arg-warning!
      (assoc m :body body)))))

#_(defn setup [var]
  (let [{fname :name
         fns   :ns
         :keys [arglists args]
         :as   var-meta} (meta var)]
    ;; TODO check if arglists and args line up
    (into []
          (for [[arg m] (partition 2 args)]
            #_m
           (assoc m
                  :fn-name
                  (str fname)
                  :fn-ns
                  fns)))))


(defn validation-fns
  [{fname :name
    fns   :ns
    :keys [arglists args] ;; <- TODO check if arglists and args line up
    :as   macrovar-meta}]
  (into []
        (for [[arg m] (partition 2 args)]
          (assoc m
                 :pred-name (str (:pred m))
                 :fn-name
                 (str fname)
                 :fn-ns
                 fns
                 :arg-name
                 (str arg)))))


;; (defmacro src* [fn-sym &form]
;;   `(list 'setup2 (list 'var ~fn-sym) ~&form))

(defn src* [macrovar &form]
  (let [{fname :name
         fns   :ns
         :keys [arglists args] ;; <- TODO check if arglists and args line up
         :as   macrovar-meta}  (meta macrovar)
        fvar                   (list 'var (symbol (str fname "*")))
        validation-fns         (validation-fns macrovar-meta)
        src                    (meta &form)]
    (merge src
           {:fvar     fvar
            :validate validation-fns})))


;; Make this work with a spec, or malli schema?
(defmacro myfn
  {:args '[x
           {:pred duh-cat?}]}
  [x]
  (let [src (src* (var myfn) &form)]
    `(do
       (validate-args ~src (quote ~&form) ~x)
       (myfn* ~x))))


(defn myfn*
  [#_src x]
  #_(? (validate-args src x))
  (!? (type x)))

(def foos 333)

(myfn #_[2 3 4]
      #_[2 :k] #_foos [2
       :33333333333333
       :4444444444444444
       :5555555555555555
       :4444444444444444
       :6666666666644444
       ])

#_(defmacro myfn
  {:args '[x
           {:pred duh-cat?}]}
  [x]
  (let [src (meta &form)
        vf (setup (-> myfn var) (meta &form))
        ;; f* (? (list 'fn [] (list 'validate-args 'src (list 'var 'myfn*) 'x)))
        fvar (list 'var 'myfn*)]
    `(do
       (validate-args (assoc ~src
                             :form (quote ~&form)
                             :validate ~vf
                             :fvar ~fvar
                             :x ~x)
                      ~x)
       (myfn* #_(assoc ~src
                     :form (quote ~&form)
                     :validate ~vf
                  ;;  :args [~x]
                  ;;  :f* ~f*
                     :fvar ~fvar
                     :x ~x)
              ~x))))


;; (? (macroexpand-1 '(specoli [:cat even? odd?])))

;; (jef et-vec?
;;   [:and
;;    vector?
;;    [:tuple
;;     [:or keyword? map?]
;;     [:not coll?]]])



;; (def et-vec?
;;   (specoli [:and
;;             vector?
;;             [:tuple
;;              [:or keyword? map?]
;;              [:not coll?]]]))


;; (def et-vec2?
;;   (specoli2 [:and
;;              vector?
;;              [:tuple
;;               [:or
;;                 keyword?
;;                 map?]
;;               [:not coll?]]]))


;; (def abc? (specoli [:coll-of keyword? :kind vector? :max-count 2]) )
;; (def duh-map? (specoli [:map-of (fn [[k v]] (and (keyword? k) (int? v))) :count 1]) )


;; (def duh-every? (specoli [:every [:or number? string?] :count 4]) )


;; (? (et-vec2? [{:color :red} "Hi"]))
;; (? (et-vec? [{:color :red} "Hi"]))


;; hiccup version
