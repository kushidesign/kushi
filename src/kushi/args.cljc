(ns ^:dev/always kushi.args
  (:require
   [clojure.spec.alpha :as s]
   [kushi.specs2 :as specs2]
   [kushi.state2 :as state2]
   [kushi.stylesheet :as stylesheet]
   [kushi.parsed :as parsed]
   [kushi.gvecs :as gvecs]
   [kushi.styles :as styles]
   [kushi.printing2 :refer [kushi-expound]]
   [kushi.utils :as util :refer [keyed]]
   [kushi.config :as config :refer [user-config]]))

(defn- data-sx-attr [form-meta]
  (when @state2/KUSHIDEBUG
    (when-let [{:keys [file line column]} form-meta]
      ;; This elides kushi's internal ui components
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

(defn unnested-styles [tups]
  (let [[nested-styles
         styles-]      (util/partition-by-spec ::specs2/style-tuple-nested tups)
        nested-styles+ (mapcat (fn [[k m]]
                                 (let [mq-keys*    (->> user-config :media keys (map name) (into #{}))
                                       mq-keys     (conj mq-keys* "dark")
                                       k-as-string (specs2/kw?->s k)
                                       k-is-mq?    (contains? mq-keys k-as-string)
                                      ;;  debug?      (= tups '(["has-ancestor(.g)" {:&_.foo:c :red}]))
                                       ]

                                   (map (fn [[prop value]]
                                          (let [nested-prop-as-string (specs2/kw?->s prop)
                                                nested-prop-has-mod?  (re-find #"\:" nested-prop-as-string)
                                                sep (if (and nested-prop-has-mod? (not k-is-mq?)) "" ":")]
                                            ;; (when debug? (? (keyed value-as-string value-has-mod? k-is-mq?)))
                                            [(str k-as-string sep (specs2/kw?->s prop)) value]))
                                        m)))
                               nested-styles)
        styles         (concat styles- nested-styles+)
        styles         (when (seq styles) styles)]
    styles))

(defn- parts [args shared-class?]
  (let [[assigned-class
         styles*]                 (if (s/valid? ::specs2/assigned-class (first args))
                                    [(first args) (rest args)]
                                    [nil args])
        assigned-class           (if (s/valid? ::specs2/quoted-symbol assigned-class)
                                   (second assigned-class)
                                   assigned-class)
        [styles
         [attrs-idx m*]]  (trailing-map styles* map?)

        ;; m* is an attrs map (in the case of sx), or a just a stylemap (in the case of defclass)
        ;; In the case of defclass, we are normalizing it e.g.  {:c :red} => {:style {:c :red}}
        attrs                    (when m*
                                   (if shared-class? {:style m*} m*))

        ;; Unnest styles
        styles-unnested          (unnested-styles styles)
        styles                   (when (seq styles-unnested) styles-unnested)
        styles-from-map-unnested (unnested-styles (some-> attrs :style))
        attrs                    (when attrs
                                   (merge attrs
                                          (when (seq styles-from-map-unnested)
                                            {:style (into {} styles-from-map-unnested)})))]

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
  [{:keys [args :kushi/process form-meta] :as m}]
  (let [shared-class?             (util/shared-class? process)

        data-sx-attr              (data-sx-attr form-meta)

        ;; This is a loose, initial conforming of the sequence / structure of the args
        ;; into name, tokens/tuples, and option attributes/stylemap
        {:keys [assigned-class
                styles
                attrs-idx
                attrs]
         :as   parts}             (parts args shared-class?)

        ;; This transforms the optional stylemap into a collection of tuples,
        ;; then partitions it apart from a collection of bad entries that will
        ;; be feed to a warning printer from within kushi.core/sx-dispatch or kushi.core/defclass-dispatch
        [stylemap-tuples
         bad-stylemap-entries]    (some->> attrs
                                           :style
                                           (util/partition-by-pred
                                            (fn [kv]
                                              (if shared-class?
                                                (or (s/valid? ::specs2/style-tuple-defclass kv)
                                                    (s/valid? ::specs2/cssvar-tuple-defclass kv))
                                                (or (s/valid? ::specs2/style-tuple kv)
                                                    (s/valid? ::specs2/cssvar-tuple kv))))))

        ;; Format the bad stylemap entries for printing
        weird-entries             (weird-entries attrs-idx bad-stylemap-entries)


        ;; Prepare a clean stylemap with no bad entries for return
        clean-stylemap            (some->> stylemap-tuples (into {}))

        ;; Prepare a clean attributes map for return
        clean-attrs               (merge (dissoc attrs :style)
                                         (some->> clean-stylemap (assoc {} :style))
                                         ;; Add this data-sx attribute for insight/debugging when in dev-mode.
                                         ;; Only gets attached to things coming from kushi.core/sx (not defclass).
                                         (when (and state2/KUSHIDEBUG (not shared-class?))
                                           data-sx-attr))

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
                                          {:m clean-attrs}))

        ;; Nilify weird/bad entries if empty
        weird-entries            (some->> weird-entries seq (into []))
        bad-args                 (some->> bad-args seq (into []))
        ret                      (keyed
                                  data-sx-attr
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
                                  conformed)]

    ret))

;; pre-clean end ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;



;; TODO - refactor in clean args from above
(defn clean-args
  [{:keys [args
           :kushi/process
           kushi-selector
           cache-key
           form-meta]
    :as m}]
  (let [shared-class?
        (util/shared-class? process)

        [validation-spec conformance-spec]
        (if shared-class?
          [::specs2/defclass-args ::specs2/defclass-args2]
          [::specs2/sx-args ::specs2/sx-args-conformance])

        ;; move this up into clean?
        expound-str (kushi-expound validation-spec args)

        {:keys [assigned-class
                clean-stylemap
                clean-attrs
                conformed
                bad-args
                weird-entries
                data-sx-attr]}
        (pre-clean-args m)


        ;; This is where we deal with cssfns, conditional sexprs, cssvars for runtime bindings.
        ;; The attrs map is reconstructed with proper.
        ;; {:all-style-tuples
        ;;  [["b" "1px:solid:$blue-500"] [:sm:dark:hover:c "var(--mybc)"]],
        ;;  :css-vars {"--mybc" mybc},
        ;;  :attrs
        ;;  {:data-sx "starter/browser.cljs:284:4",
        ;;   :class [(if true "hi" "bye") "absolute" "_1498602750"],
        ;;   :style {"--mybc" mybc}},
        ;;  :classlist [(if true "hi" "bye") "absolute" "_1498602750"],
        ;;  :selector
        ;;  {:selector* "_1498602750",
        ;;   :selector "._1498602750",
        ;;   :prefixed-name nil}}
        {:keys [all-style-tuples
                defclass-style-tuples
                css-vars
                attrs
                selector
                classlist]
         :as   tups}
        (styles/style-tuples*
         (merge (keyed
                 ;; from kushi.core/sx-dispatch or kushi.core/sx-dispatch
                 kushi-selector
                 cache-key
                 ;; from pre-cleaning
                 assigned-class
                 clean-stylemap
                 clean-attrs
                 conformed)
                {:kushi/process process}))


        ;; Kushi-style tuple syntax -> normalized css.
        ;; Kushi shorthand gets hydrated here.
        ;; This is where all the media-query, pseudo-class/element, and ancestor stuff gets pulled out
        ;; Example
        ;; '([:sm:dark:hover:c :red])
        ;; =>
        ;; '({:selector          "._88624201"
        ;;    :compound-selector ".dark ._88624201:hover"
        ;;    :mq                "sm"
        ;;    :parent            nil
        ;;    :ancestor          ".dark "
        ;;    :mods              ":hover"
        ;;    :css-prop          "color"
        ;;    :css-value         "red"})
        parsed
        (parsed/parsed all-style-tuples selector)

        ;; Create garden vectors from kushi object
        ;; Example:
        ;;'({:selector "._-1949681979",
        ;;   :compound-selector "._-1949681979",
        ;;   :mq nil,
        ;;   :parent nil,
        ;;   :ancestor nil,
        ;;   :mods nil,
        ;;   :css-prop "color",
        ;;   :css-value "var(--71500812)"}
        ;;   {:selector "._-1949681979",
        ;;   :compound-selector ".dark ._-1949681979:hover",
        ;;   :mq "sm",
        ;;   :parent nil,
        ;;   :ancestor ".dark ",
        ;;   :mods ":hover",
        ;;   :css-prop "color",
        ;;   :css-value "red"})
        ;;  =>
        ;; '(["._-1949681979" {"color" "var(--71500812)"}]
        ;;   {:identifier :media,
        ;;    :value      {:media-queries {:min-width :640px},
        ;;                 :rules         ([".dark ._-1949681979:hover" {"color" "red"}])}}) =>
        garden-vecs
        (gvecs/gvecs parsed)


        ;; Make actual to get injected on dev reload
        ;; Example:
        ;; '(["._-1949681979" {"color" "var(--71500812)"}]
        ;;   {:identifier :media,
        ;;    :value      {:media-queries {:min-width :640px},
        ;;                 :rules         ([".dark ._-1949681979:hover" {"color" "red"}])}})
        ;; =>
        ;; ["._-1949681979{color:var(--71500812)}"
        ;;  "@media(min-width:640px){.dark ._-1949681979:hover{color:red}}"]
        element-style-inj
        (stylesheet/garden-vecs-injection garden-vecs)
        ]


;; Debugging - change quoted sym to line up with the sx or defclass call (with manually assigned class) you want to observe

;; (when (= (first args) '(quote foo))
;;   (? all-style-tuples)
;;   (? parsed))




    (merge
     data-sx-attr
     {:kushi/process process
      :args/bad      bad-args
      :entries/weird weird-entries}
     (when defclass-style-tuples
       (keyed defclass-style-tuples))
     (when-not shared-class?
       {:attrs attrs})
     (keyed element-style-inj
            garden-vecs
            args
            css-vars
            classlist
            selector
            expound-str
            attrs
            form-meta
            ))))
