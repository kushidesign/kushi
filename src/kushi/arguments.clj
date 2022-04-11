(ns ^:dev/always kushi.arguments
 (:require
  [clojure.spec.alpha :as s]
  [clojure.string :as string]
  [par.core :refer [!? ? ?+ !?+]]
  [kushi.selector :as selector]
  [kushi.parse :as parse]
  [kushi.printing :as printing]
  [kushi.state :as state]
  [kushi.specs :as specs]
  [kushi.defs :as defs]
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
                  :kushi-atomic            state/atomic-declarative-classes-used
                  :defclass                state/defclasses-used
                  :defclass-kushi-override state/defclasses-used
                  :defclass-user-override  state/defclasses-used
                  nil)]
      #_(? 'register&prefix*:x {x (:__classtype__ ret)})
      #_(? 'register&prefix* (keyed x ret store))
      (when store (swap! store conj x))
      ret)))

(defn register&prefix [x]
  (if (s/valid? ::specs/conditional-sexp x)
    (map register&prefix* x)
    (register&prefix* x)))

(defn resolved-classes [x]
  (when x
    (let [class-coll             (util/into-coll x)
          [conditionals
           kw-classes]           (util/partition-by-pred #(s/valid? ::specs/conditional-sexp %) class-coll)
          from-conditionals      (flatten (map #(filter keyword? (drop 2 %)) conditionals))
          distinct-classes       (distinct (concat kw-classes from-conditionals))
          prefixed               (map register&prefix class-coll)]
      (keyed distinct-classes prefixed))))

(defn tokens+attrs [args]
  (let [[attrs* & more]  args
        [tokens attrs] (cond (map? attrs*) [nil attrs*]
                             :else       (if (map? (last args))
                                           [(seq (drop-last args)) (last args)]
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

(defn validate-args [args style-tokens attrs*]
  ;; (when (state/debug?) (?+ args))
  (let [style-map-grouped        (let [stylemap (:style attrs*)]
                                   (if (or (map? stylemap) (nil? stylemap))
                                     (group-by #(if (do
                                                      (when (state/debug?)
                                                        (?+ %)
                                                        (?+ (s/valid? ::specs/style-tuple %)))
                                                      (s/valid? ::specs/style-tuple %)) :clean :bad) stylemap)
                                     ;; TODO move this out
                                     (printing/simple-warning {:desc "Invalid value for :style entry in attributes map."
                                                               :args args
                                                               :hint "Must be a map (data-literal) or nil."})))
        style-tokens-indexed     (map-indexed (fn [idx v]
                                                (if (s/valid? ::specs/kushi-tokenized-keyword v)
                                                  v
                                                  (do
                                                    (.indexOf args v)
                                                    idx
                                                    [[(.indexOf args v)] v])))
                                              style-tokens)
        style-tokens-grouped     (group-by #(if (vector? %) :bad :clean) style-tokens-indexed)
        invalid-style-args*      (let [bad-entries* (:bad style-map-grouped)
                                       bad-tokens   (into {} (:bad style-tokens-grouped))
                                       attrs-idx*   (when (seq bad-entries*) (-> args count dec))]
                                   (if (and attrs-idx* (pos? attrs-idx*))
                                     (assoc bad-tokens [attrs-idx* :style] bad-entries*)
                                     bad-tokens))
        invalid-style-args       (not-empty invalid-style-args*)
        valid-styles-from-attrs  (into {} (:clean style-map-grouped))
        valid-styles-from-tokens (:clean style-tokens-grouped)
        styles?                  (if (seq (concat (seq valid-styles-from-attrs)
                                                  (seq valid-styles-from-tokens)))
                                   true
                                   false)]
    #_(when (state/debug?)
      (? :validate-args (keyed style-map-grouped style-tokens-indexed style-tokens-grouped invalid-style-args)))
    (when invalid-style-args (?+ invalid-style-args))
    (keyed valid-styles-from-attrs
           valid-styles-from-tokens
           invalid-style-args
           styles?)))

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

(defn prefixed-classlist
  [class-tokens*
   classlist-from-attrs
   selector*
   styles?
   {:keys [prefix ident]}]
  (let [class-tokens       (map dotkw->kw class-tokens*)
        from-attrs         (->> classlist-from-attrs (map dotkw->kw) resolved-classes)
        from-tokens        (resolved-classes class-tokens)
        from-attrs*&tokens [from-attrs from-tokens]
        distinct-classes   (distinct (apply concat (map :distinct-classes from-attrs*&tokens)))
        selector           (when (or styles? (and prefix ident)) selector*)
        prefixed-classlist (->> from-attrs*&tokens
                                (map :prefixed)
                                (map classlist)
                                (apply concat)
                                (cons selector)
                                distinct
                                (remove nil?)
                                (into []))]
    (keyed distinct-classes prefixed-classlist)))

(defn garden-vecs-with-prefix
  [coll styles? {:keys [prefix ident]}]
  (if (and (not styles?) (and prefix ident) (and (= 1 (count coll)) (nil? (-> coll first second))))
    (assoc-in coll [0 1] {})
    coll))

(defn new-args
  "Takes args and reorganizes it into internal/legacy format"
  ([args form-meta]
   (new-args args form-meta nil))
  ([args form-meta defclass-name]
   (let [[tokens attrs*]              (tokens+attrs args)
         [class-tokens* style-tokens] (util/partition-by-spec ::specs/tokenized-classes tokens)
         classlist-from-attrs         (:class attrs*)
         kushi-attr*                  (select-keys attrs* defs/meta-ks)
         kushi-attr                   (if defclass-name (assoc kushi-attr* :defclass-name defclass-name) kushi-attr*)
         {:keys [selector selector*]} (selector/selector-name kushi-attr*)
         attrs-base                   (apply dissoc attrs* (conj defs/meta-ks :class :style))
         _                            (state/set-current-macro! (merge {:macro (if defclass-name :defclass :sx)}
                                                                       (keyed args form-meta kushi-attr)))

        ;; args validation and conformance -----------------------------------------------------------
         {:keys
          [valid-styles-from-attrs
           valid-styles-from-tokens
           invalid-style-args
           styles?]}                  (validate-args args style-tokens attrs*)

         classlist-from-attrs?        (if (seq classlist-from-attrs) true false)
         classes-from-tokens?         (if (seq class-tokens*) true false)
         classes?                     (or classlist-from-attrs? classes-from-tokens?)

        ;; classlist construction ---------------------------------------------------------------------
         {:keys [prefixed-classlist
                 distinct-classes]}   (when (or classes? styles? selector*)
                                        (prefixed-classlist class-tokens* classlist-from-attrs selector* styles? kushi-attr))

        ;; element style with mqs, modifiers, & css vars ----------------------------------------------
         style-tokens-map             (style-tokens-map valid-styles-from-tokens)
         new-style                    (into [] (merge style-tokens-map valid-styles-from-attrs))
         styles-with-vars             (parse/+vars new-style selector*)
         css-vars                     (parse/css-vars new-style selector*)
         tokenized-styles             (mapv (partial parse/kushi-style->token selector*) styles-with-vars)
         grouped-by-mqs               (parse/grouped-by-mqs tokenized-styles)
         garden-vecs*                 (parse/garden-vecs grouped-by-mqs selector)
         garden-vecs                  (garden-vecs-with-prefix garden-vecs* styles? kushi-attr)

        ;; dev-time debugging info --------------------------------------------------------------------
         data-cljs-prefix             (when-let [pf (:data-cljs-prefix kushi-attr)] (str (name pf) ":"))
         data-cljs                    (let [{:keys [file line column]} form-meta]
                                        (str data-cljs-prefix file ":"  line ":" column))

         prefixed-classlist?          (if (seq prefixed-classlist) true false)
         css-vars?                    (if (seq css-vars) true false)
         attrs?                       (if (seq attrs-base) true false)
         only-attrs?                  (and (not prefixed-classlist?) (not css-vars?) attrs?)
         only-class?                  (and prefixed-classlist? (not css-vars?) (not attrs?))
         only-class+style?            (and prefixed-classlist? styles? (not attrs?))
         ret                          (keyed
                                       prefixed-classlist
                                       distinct-classes
                                       attrs-base
                                       kushi-attr
                                       css-vars
                                       garden-vecs
                                       data-cljs)]

    ;; Set invalid style-args in state!
     (reset! state/invalid-style-args invalid-style-args)

     #_(when  (state/debug?)
             (?+ :new-args:bindings
                 (keyed
            ;; args
            ;; tokens
            ;; style-tokens
            ;; style-tokens-map
            ;; class-tokens*
            ;; attrs*
            ;; distinct-classes
            ;; prefixed-classlist
            ;; invalid-style-args
            ;; styles?
            ;; classes?
            ;; css-vars
            ;; css-vars?
            ;; attrs?
            ;; prefixed-classlist
            ;; prefixed-classlist?
            ;; only-attrs?
            ;; only-class?
            ;; only-class+style?
            ;; new-style
            ;; invalid-style-args
            ;; attrs-base
            ;; defclass-name
            ;; kushi-attr
            ;; styles-with-vars
            ;; style-tokens-map
            ;; styles-with-vars
            ;; tokenized-styles
            ;; grouped-by-mqs
            ;; css-vars
            ;; selector
            ;; selector*
            ;; garden-vecs
            ;; new-args
                  ret)))
     ret)))

(defn combine-classes [coll]
  (let [f   (fn [acc v]
              (if (coll? v)
                (into [] (concat acc v)) (conj acc v)))
        ret (->> coll
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
