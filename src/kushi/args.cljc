(ns ^:dev/always kushi.args
  (:require
   [kushi.specs2 :as specs2]
   [kushi.state2 :as state2]
   [kushi.stylesheet :as stylesheet]
   [kushi.parsed :as parsed]
   [kushi.gvecs :as gvecs]
   [kushi.styles :as styles]
   [kushi.problems :as problems]
   [kushi.utils :as util :refer [keyed]]
   [kushi.config :as config :refer [user-config]]
   [expound.alpha :as expound]))


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

(defn clean-args
  [{:keys [args :kushi/process form-meta] :as m}]
  (let [
        test? (reset! state2/trace? (= '(quote wtfx) (some-> args first)))
        [validation-spec conformance-spec]
        (if (util/shared-class? process)
          [::specs2/defclass-args ::specs2/defclass-args2]
          [::specs2/sx-args ::specs2/sx-args-conformance])

        bad
        (problems/problems args
                           validation-spec
                           conformance-spec)

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

        expound-str
        (expound/expound-str validation-spec args)

        element-style-inj
        (stylesheet/garden-vecs-injection garden-vecs)]

    #_(when test?
      (? :clean
                    (keyed
        ;;  args
        ;;  process
        ;; bad
        ;;  bad-args
        ;;  bad-stylemap
        ;;  all-style-tuples
        ;;  parts*
        ;;  grouped-by-mq2
        ;;  no-mq
        ;;  mqs
        tups
        ;; parsed
                    ;;  garden-vecs
        ;;  gv*
        ;;  grouped
        ;;  css-vars
        ;;  data-sx-attr
        ;;  attrs
                     )))

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
