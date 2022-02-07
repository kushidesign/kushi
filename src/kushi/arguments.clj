(ns ^:dev/always kushi.arguments
 (:require
  [clojure.spec.alpha :as s]
  [clojure.string :as string]
  [par.core :refer [!? ? ?+ !?+]]
  [kushi.selector :as selector]
  [kushi.parse :as parse]
  [kushi.specs :as specs]
  [kushi.state :as state]
  [kushi.utils :as util]))

;TODO move this to utils
(defmacro keyed [& ks]
  `(let [keys# (quote ~ks)
         keys# (map keyword keys#)
         vals# (list ~@ks)]
     (zipmap keys# vals#)))

(defn reduce-by-registered
  [coll state-coll]
  (util/partition-by-pred #(contains? state-coll %) coll))

(defn register-classes [coll state-coll]
  (when (seq coll)
    (doseq [k coll]
      #_(println (str "swapping " k " into " @state-coll))
      (swap! state-coll conj k))))

(defn kushi-class? [x]
  (when (keyword? x)
    (or (contains? (:defclass @state/declarations) x)
        (contains? @state/kushi-atomic-user-classes x))))

(defn register&prefix* [x]
  (if-not (keyword? x)
    x
    (let [ret (or (get @state/kushi-atomic-user-classes x)
                  (let [nm (name x)]
                    {:n             nm
                     :selector      (str "." nm)
                     :selector*     nm
                     :__classtype__ :other}))
          store (case (:__classtype__ ret)
                  :kushi-atomic state/atomic-declarative-classes-used
                  :defclass state/defclasses-used
                  nil)]
      (when store (swap! store conj x))
      ret)))

(defn register&prefix [x]
  (if (s/valid? ::specs/conditional-sexp x)
    (map register&prefix* x)
    (register&prefix* x)))

(defn resolved-classes [x]
  #_(?+ :resolved-classes x)
  (when x
    (let [class-coll             (util/into-coll x)
          [conditionals
           kw-classes]           (util/partition-by-pred #(s/valid? ::specs/conditional-sexp %) class-coll)
          from-conditionals      (flatten (map #(filter keyword? (drop 2 %)) conditionals))
          distinct-classes       (distinct (concat kw-classes from-conditionals))
          ;; grouped                (group-by
          ;;                         #(cond (contains? (:defclass @state/declarations) %)  :defclass
          ;;                                (contains? @state/kushi-atomic-user-classes %) :kushi-atomic
          ;;                                :else :other)
          ;;                         distinct-classes)
          prefixed               (map register&prefix class-coll)]
          ;; [defclasses+atomics
          ;;  non-kushi-classes]    (util/partition-by-pred kushi-class? kw-classes)
          ;; [defclasses
          ;;  kushi-atomics]        (reduce-by-registered
          ;;                         defclasses+atomics
          ;;                         (:defclass @state/declarations))
          ;; [defclasses-used
          ;;  defclasses-unused]    (reduce-by-registered
          ;;                         defclasses
          ;;                         @state/defclasses-used)
          ;; [kushi-atomics-used
          ;;  kushi-atomics-unused] (reduce-by-registered
          ;;                         kushi-atomics
          ;;                         @state/atomic-declarative-classes-used)
          

      #_(?+ :prefixed prefixed)
      #_(?+)
       :resolve-classes
       (keyed)
        ;; class-coll
        ;; conditionals
        ;; from-conditionals
        ;; kw-classes
        ;; distinct-classes
        ;; grouped
        ;; defclasses+atomics
        ;; non-kushi-classes
        ;; defclasses
        ;; kushi-atomics
        ;; defclasses-used
        ;; defclasses-unused
        ;; kushi-atomics-used
        ;; kushi-atomics-unused
        

      ;; (when true #_(some #(= :foo %) class)
      ;;       #_(? @state/declarations)
      ;;       #_(? @state/atomic-declarative-classes-used)
      ;;       #_(? (keyed kushi other defclasses kushi-atomics kushi-atomics-unused kushi-atomics-used defclasses-unused defclasses-used))
      ;;       (register-classes defclasses-unused state/defclasses-used)
      ;;       (register-classes kushi-atomics-unused state/atomic-declarative-classes-used))

      (keyed distinct-classes prefixed))))

(defn tokens+attrs [args]
  (let [[arg & more]  args
        [tokens attrs] (cond (map? arg) [nil arg]
                             more       (if (map? (last args))
                                          [(drop-last args) (last args)]
                                          [args nil]))]
    [tokens attrs]))

(defn dotkw->kw [x] (cond (s/valid? ::specs/dot-kw x) (parse/kw-subs1 x)
                          (coll? x) (map dotkw->kw x)
                          :else x))

(defn style-tokens-map [coll]
  (reduce (fn [acc v]
            (if (s/valid? ::specs/style-kw v)
              (let [[k v] (-> v name (string/split #"--"))]
                (assoc acc (keyword k) (keyword v)))
              acc))
          {}
          coll))

(defn new-class-sorted
  [classes-from-tokens+
   attrs*]
  (let [sortfn    (fn [a] (if (coll? a) (first "zzz") (first (name a))))
        new-class (distinct (concat classes-from-tokens+ (:class attrs*)))
        ret       (into [] (sort-by sortfn new-class))]
    ret))

(defn get-selector-base [x]
  (if (and (map? x) (contains? x :__classtype__))
    (:selector* x)
    x))

(defn classlist [coll]
  (map #(if (s/valid? ::specs/conditional-sexp %)
          (map get-selector-base %)
          (get-selector-base %))
       coll))

(def meta-ks [:ancestor :prefix :ident :element])

        ;; {:keys [valid invalid]}    (util/reduce-by-pred #(s/valid? ::specs/kushi-arg %) styles+classes)
        ;; {classes* :valid
        ;;  styles*  :invalid}        (util/reduce-by-pred #(s/valid? ::specs/kushi-class-like %) valid)
        ;; {classes-with-mods :valid} (util/reduce-by-pred #(s/valid? ::specs/kushi-dot-class-with-mods %) classes*)

(defn validate-args [args style-tokens attrs*]
  (let [style-map-grouped    (?+ :style-map-grouped
                                  (group-by #(if (s/valid? ::specs/style-tuple %) :clean :bad) (:style attrs*)))

        style-tokens-indexed (!?+ :indexed-tokens
                                  (map-indexed (fn [idx v]
                                                 (if (s/valid? ::specs/kushi-arg v)
                                                   v
                                                   [[idx] v]))
                                               style-tokens))
        style-tokens-grouped (!?+ :grouped-tokens
                                  (group-by #(if (vector? %) :bad :clean) style-tokens-indexed))
        invalid-style-args   (let [bad-entries* (:bad style-map-grouped)
                                   bad-tokens   (into {} (:bad style-tokens-grouped))
                                   attrs-idx*   (when (seq bad-entries*) (-> args count dec))]
                               (if (and attrs-idx* (pos? attrs-idx*))
                                 (let []#_[bad-entries (map (fn [kv] [[attrs-idx* :style] kv]) bad-entries*)]
                                   (assoc bad-tokens [attrs-idx* :style] bad-entries*))
                                 bad-tokens))]

    {:valid-styles-from-attrs  (into {} (:clean style-map-grouped))
     :valid-styles-from-tokens (:clean style-tokens-grouped)
     :invalid-style-args       invalid-style-args}
    ))

;; TODO maybe remove?
(defn class-details
  [attrs* from-attrs from-tokens]
  (let [classes-from-attrs   (map #(if (s/valid? ::specs/conditional-sexp %)
                                     (map get-selector-base %)
                                     (get-selector-base %))
                                  from-attrs)
        classes-from-tokens  (map #(if (s/valid? ::specs/conditional-sexp %)
                                     (map get-selector-base %)
                                     (get-selector-base %))
                                  from-tokens)
        classes-from-tokens+ (apply concat (vals classes-from-tokens))
        new-class            (new-class-sorted classes-from-tokens+ attrs*)]
    (keyed classes-from-attrs
           classes-from-tokens
           classes-from-tokens+
           new-class)))

(defn new-args
  "Takes args and reorganizes it into internal/legacy format"
  [args form-meta]
  (let [[tokens attrs*]              (tokens+attrs args)
        [class-tokens* style-tokens] (util/partition-by-spec ::specs/tokenized-classes tokens)
        attrs-base                   (dissoc attrs* :class :style)
        kushi-attr                   (select-keys attrs-base meta-ks)
        {:keys [selector selector*]} (selector/selector-name kushi-attr)

        ;; args validation and conformance -----------------------------------------------------------
        {:keys
         [valid-styles-from-attrs
          valid-styles-from-tokens
          invalid-style-args]}       (validate-args args style-tokens attrs*)

        ;; classlist construction ---------------------------------------------------------------------
        class-tokens                 (map dotkw->kw class-tokens*)
        from-attrs                   (resolved-classes (:class attrs*))
        from-tokens                  (resolved-classes class-tokens)
        from-attrs*&tokens                    [from-attrs from-tokens]
        distinct-classes             (distinct (apply concat (map :distinct-classes from-attrs*&tokens)))
        prefixed-classlist           (->> from-attrs*&tokens
                                          (map :prefixed)
                                          (map classlist)
                                          (apply concat)
                                          (cons selector*)
                                          distinct
                                          (into []))


        ;; element style with mqs, modifiers, & css vars ----------------------------------------------
        style-tokens-map             (style-tokens-map valid-styles-from-tokens)
        new-style                    (into [] (merge style-tokens-map valid-styles-from-attrs))
        styles-with-vars             (parse/+vars new-style selector*)
        css-vars                     (parse/css-vars new-style selector*)
        tokenized-styles             (mapv (partial parse/kushi-style->token selector*) styles-with-vars)
        grouped-by-mqs               (parse/grouped-by-mqs tokenized-styles)
        garden-vecs                  (parse/garden-vecs grouped-by-mqs selector)


        ;; dev-time debugging info --------------------------------------------------------------------
        data-cljs-prefix             (when-let [pf (:data-cljs-prefix kushi-attr)] (str (name pf) ":"))
        data-cljs                    (let [{:keys [file line column]} form-meta]
                                       (str data-cljs-prefix file ":"  line ":" column))]

    ;; Set invalid style-args in state!
    (reset! state/invalid-style-args (? invalid-style-args))

    #_(?+ (keyed
        ;; args
        ;; tokens
        ;; style-tokens
        ;; style-tokens-map
        ;; class-tokens*
        ;; class-tokens
        ;; attrs*
        ;; attrs
        ;; classes-from-attrs
        ;;  distinct-classes
        ;;  prefixed-classlist
        ;;  invalid-style-args
         new-style
         invalid-style-args
        ;;  attrs-base
        ;;  kushi-attr
        ;;  selector
        ;;  selector*
        ;;  css-vars
        ;;  garden-vecs
        ;; classes-from-tokens+
        ;; new-args
    ))
         

    (!? :bam
        (keyed
         distinct-classes
         prefixed-classlist
         invalid-style-args
         new-style
         attrs-base
         kushi-attr
         selector
         selector*
         css-vars
         garden-vecs
         data-cljs))))
         

(defn combine-classes [coll]
  (let [f     (fn [acc v] (if (coll? v) (into [] (concat acc v)) (conj acc v)))
        ret  (->> coll
                  (reduce f [])
                  (remove nil?)
                  distinct
                  (into []))]
    ret))

(defn merge-attr [kushi-map attr-map]
  #_(?+ (keyed m maps))
  (let [classes [(:. kushi-map) (:class attr-map)]
        class   (combine-classes classes)
        styles* (dissoc kushi-map :.)
        style   (merge styles* (:style attr-map))
        ret     (assoc attr-map :class class :style style)]
    #_(?+ (keyed classes class* class ret))
    ret))


(defn consolidated [args]
  (if (s/valid? ::specs/map-mode-style+attr args)
    (vector (merge-attr (first args) (second args)))
    args))
