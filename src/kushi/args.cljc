(ns ^:dev/always kushi.args
  (:require
   [clojure.spec.alpha :as s]
   [kushi.specs2 :as specs2]
   [kushi.state2 :as state2]
   [kushi.stylesheet :as stylesheet]
   [kushi.parsed :as parsed]
   [kushi.gvecs :as gvecs]
   [kushi.styles :as styles]
   [kushi.problems :as problems]
   [kushi.printing2 :refer [kushi-expound]]
   [kushi.utils :as util :refer [keyed]]
   [kushi.config :as config :refer [user-config]] ))

(defn- data-sx-attr [form-meta]
  (when @state2/KUSHIDEBUG
    (when-let [{:keys [file line column]} form-meta]
      (when-not (some->> file (re-find #"kushi/ui/"))
        (let [v (str file ":"  line ":" column)
              k (some->> user-config
                         :data-attr-name
                         name
                         (str "data-")
                         keyword)]
          {k v})))))

;; Args clean start ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; TODO - incorporate this into pipeline ;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; This is where args to both sx and defclass get cleaned,
;; sorted into warning buckets, and conformed into a map for
;; the next step in the pipeline.
;; ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn- trailing-map [styles pred]
  (if (s/valid? pred (last styles))
    [(drop-last styles) [(-> styles count dec) (last styles)]]
    [styles nil]))


(defn- weird-entries [idx entries]
  (mapv (fn [[k v]] {:path  [idx :style k]
                     :entry [k v]}) entries))

(defn- sorted-styles [styles spec]
  (map-indexed (fn [idx %]
                 (if (s/valid? spec %)
                   %
                   {:arg  %
                    :path [idx]}))
               styles))

(defn- parts [args shared-class?]
  (let [[assigned-class
         styles]         (if (s/valid? ::specs2/assigned-class (first args))
                           [(first args) (rest args)] [nil args])
        [styles
         [attrs-idx m*]] (trailing-map styles map?)
        ;; m* is an attrs map (in the case of sx), or a just a stylemap (in the case of defclass)
        ;; In the case of defclass, we are normalizing it e.g.  {:c :red} => {:style {:c :red}}
        attrs            (when m*
                           (if shared-class? {:style m*} m*))]
    (keyed assigned-class styles attrs-idx attrs)))

(defn- clean-args-conformed
  [{:keys [conformance-spec
           assigned-class
           styles
           m]}]
  (let [cleaned   (remove nil?
                          (concat (when assigned-class
                                    [assigned-class])
                                  styles
                                  (when m [m])))
        conformed (styles/args-by-conformance cleaned conformance-spec)
        ]
    [cleaned conformed]))


(defn- pre-clean-args
  [{:keys [args :kushi/process] :as m}]
  (let [shared-class?              (util/shared-class? process)

        ;; This is a loose, initial conforming of the sequence / structure of the args
        ;; into name, tokens/tuples, and option attributes/stylemap
        {:keys [assigned-class
                styles
                attrs-idx
                attrs]
         :as   parts}             (parts args shared-class?)

        ;; This transforms the optional stylemap into a collection of tuples
        ;; And partitions it apart from a collection of bad entries that will
        ;; be feed to a warning printer from within sx-dispatch / defclass-dispatch
        [stylemap-tuples
         bad-stylemap-entries]    (some->> attrs
                                           :style
                                           (util/partition-by-pred
                                            (fn [kv]
                                              (if shared-class?
                                                (s/valid? ::specs2/style-tuple-defclass kv)
                                                (or (s/valid? ::specs2/style-tuple kv)
                                                    (s/valid? ::specs2/cssvar-tuple kv))))))

        ;; Format the bad stylemap entries for printing
        weird-entries             (weird-entries attrs-idx bad-stylemap-entries)

        ;; Prepare a clean stylemap with no bad entries for return
        clean-stylemap            (some->> stylemap-tuples (into {}))

        ;; Prepare a clean attributes map for return
        clean-attrs               (merge (dissoc attrs :style)
                                         (some->> clean-stylemap (assoc {} :style)))

        ;; This will wrap any bad style args in a map to be partitioned out in next step
        styles                    (sorted-styles styles (if shared-class?
                                                          ::specs2/valid-defclass-arg*
                                                          ::specs2/valid-sx-arg*))

        ;; Partition out bad args
        [bad-args styles]         (util/partition-by-spec ::specs2/bad-sx-or-defclass-arg styles)

        conformance-spec           (if shared-class?
                                     ::specs2/defclass-args2-normalized
                                     ::specs2/sx-args-conformance)

        ;; This assembles a clean version of the args and conforms it to its spec
        [args-pre-cleaned
         conformed]               (clean-args-conformed
                                   (merge (keyed assigned-class
                                                 styles
                                                 conformance-spec)
                                          {:m clean-attrs}))]

    (keyed
     assigned-class
     attrs
     stylemap-tuples
     bad-stylemap-entries
     weird-entries
     clean-stylemap
     clean-attrs
     styles
     bad-args
     args-pre-cleaned
     conformed)))

;; pre-clean end ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;



;; TODO - refactor in clean args from above
(defn clean-args
  [{:keys [args :kushi/process form-meta] :as m}]
  (let [shared-class?
        (util/shared-class? process)

        data-sx-attr
        (data-sx-attr form-meta)

        [validation-spec conformance-spec]
        (if shared-class?
          [::specs2/defclass-args ::specs2/defclass-args2]
          [::specs2/sx-args ::specs2/sx-args-conformance])

        expound-str (kushi-expound validation-spec args)


        bad
        (problems/problems (merge m
                                  (keyed args
                                         conformance-spec)
                                  {:validation-spec (or #_validation-spec-normalized
                                                        validation-spec)}))


        defclass-with-bad-entries?
        (and shared-class? (-> bad :bad-entries seq))

        args
        (if defclass-with-bad-entries?
          (let [ks        (keys (into {} (:bad-entries bad)))
                stylemap  (last args)
                clean-map (apply dissoc stylemap ks)]
            (concat (drop-last args) [clean-map]))
          args)

        m
        (if defclass-with-bad-entries?
          (assoc m :args args)
          m)


        {:keys [all-style-tuples
                defclass-style-tuples
                css-vars
                attrs
                selector
                classlist]
         :as   tups}
        (styles/style-tuples* (merge (keyed validation-spec
                                            conformance-spec)
                                     m

                                     ;; maybe nix this
                                     bad

                                     ;; From pre-cleaned
                                     ;; conformed
                                     ;; clean-stylemap

                                     ))

        attrs
        (merge attrs data-sx-attr)

        ;; kushi-style tuple styntax -> normalized css
        parsed
        (parsed/parsed all-style-tuples selector)

        ;; garden vectors
        garden-vecs
        (gvecs/gvecs parsed)


        ;; Nix
        bad-args
        (when (some-> bad :args)
          (let [bad (mapv (fn [x]
                            (when x {:path (:path x)
                                     :arg  (:arg x)}))
                          (:bad-args bad))]
            (when (seq bad) bad)))


        ;; Nix
        weird-entries
        (when (:bad-stylemap bad)
          (when-let [bad-entries (some-> bad :bad-stylemap-path-map)]
            (when (seq bad-entries)
              (let [path*   (ffirst bad-entries)
                    entries (-> bad-entries first second)]
                (mapv (fn [[k v]]
                        {:path  (conj path* k)
                         :entry [k v]})
                      entries)))))


        element-style-inj
        (stylesheet/garden-vecs-injection garden-vecs)]


    ;; NEW
    ;;  (merge data-sx-attr
    ;;        {:kushi/process process
    ;;         :args/bad      (:bad-args cleaned)
    ;;         :entries/weird (:weird-entries cleaned)}
    ;;        (when defclass-style-tuples
    ;;          (keyed defclass-style-tuples))
    ;;        (keyed
    ;;         ;; Leave expound str out of return map for now
    ;;         expound-str
    ;;         form-meta
    ;;         args
    ;;         css-vars
    ;;         attrs
    ;;         classlist
    ;;         selector
    ;;         element-style-inj
    ;;         garden-vecs))


    (merge data-sx-attr
           {:kushi/process process
            :args/bad      bad-args
            :entries/weird weird-entries}
           (when defclass-style-tuples
             (keyed defclass-style-tuples))
           (keyed
            ;; Leave expound str out of return map for now
            expound-str
            form-meta
            args
            css-vars
            attrs
            classlist
            selector
            element-style-inj
            garden-vecs))))
