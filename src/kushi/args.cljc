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
   [kushi.config :as config :refer [user-config]]))


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

(defn- trailing-map [styles pred]
  (if (s/valid? pred (last styles))
    [(drop-last styles) (last styles)]
    [styles nil]))

(defn- pre-clean-sx-args [args]
  (let [[nm styles]            (if (symbol? (first args))
                                 [(first args) (rest args)]
                                 [nil args])
        [styles tracing]       (trailing-map styles ::specs2/kushi-trace)
        [styles attrs]         (trailing-map styles map?)
        stylemap               (or (some-> attrs :style) {})
        attrs                  (dissoc attrs :style)
        styles                 (map-indexed (fn [idx %]
                                        (if (s/valid? ::specs2/valid-sx-arg %)
                                          %
                                          {:bad-value %
                                           :path      [idx]}))
                                      styles)
        [bad-styles
         styles]               (util/partition-by-spec ::specs2/bad-sx-value styles)
        [stylemap-tuples
         bad-stylemap-entries] (util/partition-by-pred
                                (fn [kv]
                                  (or (s/valid? ::specs2/style-tuple kv)
                                      (s/valid? ::specs2/cssvar-tuple kv)))
                                stylemap)
        stylemap               (into {} stylemap-tuples)]

{:nm                   nm
 :tracing              tracing
 :attrs                attrs
 :styles               styles
 :stylemap             stylemap
 :bad-styles           bad-styles
 :bad-stylemap-entries bad-stylemap-entries
 :stylemap-tuples      stylemap-tuples
 :args-pre-cleaned     (remove nil? (concat (when nm [nm]) styles (when (seq stylemap) [stylemap])))}))


(defn clean-args
  [{:keys [args :kushi/process form-meta] :as m}]
  (let [[validation-spec conformance-spec]
        (if (util/shared-class? process)
          [::specs2/defclass-args ::specs2/defclass-args2]
          [::specs2/sx-args ::specs2/sx-args-conformance])

        bad
        (problems/problems (merge m
                                  (keyed args
                                         validation-spec
                                         conformance-spec)))

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
                                     bad))
        ;; dev debuggging
        data-sx-attr
        (data-sx-attr form-meta)

        attrs
        (merge attrs data-sx-attr)

        ;; kushi-style tuple styntax -> normalized css
        parsed
        (parsed/parsed all-style-tuples selector)

        ;; garden vectors
        garden-vecs
        (gvecs/gvecs parsed)


        bad-args
        (let [bad (mapv (fn [[path arg]] {:path path
                                          :arg  arg}) (:bad-args bad))]
          (when (seq bad) bad))

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

        expound-str (kushi-expound validation-spec args)

        element-style-inj
        (stylesheet/garden-vecs-injection garden-vecs)]

    (merge data-sx-attr
           {:kushi/process process
            :args/bad bad-args
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
