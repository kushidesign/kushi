(ns ^:dev/always kushi.ui.core
  (:require [fireworks.core :refer [? !? ?> !?>]]
            [clojure.string :as string]
            [kushi.ui.variants :as variants]
            [kushi.ui.extract]
            [kushi.util :refer [keyed]]
            [bling.core :refer [callout bling]]
            [bling.hifi :refer [hifi]]
            [bling.explain :refer [explain-malli]]
            [malli.core :as m])
  (:require-macros [kushi.ui.core]))


(defn- data-ks-attrs-style-map [k supplied]
  (cond (and (= k :shadows) supplied) 
        (into {}
              (map-indexed (fn [i s]
                             [(str "--_drop-shadow"
                                   (when (pos? i) (str "-" (inc i))))
                              s])
                           (take 3 supplied)))))
(def debug? (atom false))

(defn data-ks-attrs 
  "Attaches data-ks based on opts from defn metadata map. To be called from
   defui macro."
  [props with-schema]

  (reset! debug? (= props
                    {:stroke       [[:2px :$brown-300]                [:2px :$green-300]]
                     :colorway     :accent
                     :drop-shadow  ["5px 5px 10px currentColor"]
                     :stroke-align :outside
                     :contour      :pill
                     :surface      :transparent}))

  ;; Or concept of registry so you don't need to manually add :elide thing?
  (!? {:when (contains? with-schema :choices)}
      (merge (reduce-kv 
              (fn [m k {:keys [default data-ks? when-not-nil style-tokens?]
                        :as   prop}]
                (!? {:when (and @debug? (= k :drop-shadow))}
                    (merge m
                           (when (or (and (contains? variants/props k)
                                          (not (false? data-ks?)))
                                     (true? data-ks?))
                             (let [supplied  (case k
                                               :display
                                               (let [v (get props k)]
                                                 (cond
                                                   (vector? v)
                                                   (string/join " " (mapv name v))
                                                   :else
                                                   v))
                                               (get props k))

                                   data-ks-* (keyword (str "data-ks-" (name k)))

                                   style     (when style-tokens?
                                               (data-ks-attrs-style-map k supplied))

                                   ;; This sorts out `data-ks-*` attrs that are boolean,
                                   ;; but need to be supplied as `data-ks-foo=""` (when true, appears in dom as `data-ks-foo`)
                                   ;; or `data-ks-foo=nil` (if false, does not appear in dom)
                                   ret       (cond (!? {:when (= k :choices)} (not (nil? supplied)))
                                                   {data-ks-* (if (true? (:boolean? prop))
                                                                (if (false? supplied) nil "")
                                                                (or when-not-nil
                                                                    (kushi.util/as-str supplied)))}
                                                   (!? {:when (= k :choices)} default)
                                                   {data-ks-* (if (true? (:boolean? prop))
                                                                (case default
                                                                  false   nil
                                                                  "false" nil
                                                                  "")
                                                                (kushi.util/as-str default))})]
                               (!? {:when (= k :choices)} (keyed [supplied data-ks-* style ret]))
                               (merge ret (when style {:style style})))))))
              {} 
              with-schema)
             (some->> props :at (hash-map :data-ks-at)))))

(def extract kushi.ui.extract/extract)

(defn validate*2 
  [{:keys          [fn-info
                    malli-schema
                    data-ks-at]
    custom-props   :props/custom
    supplied-props :props
    :as            m}]
  ;; (? m)
  (when (and supplied-props malli-schema) 
    (let [user-spacing 
          :compact #_:ultra-compact

          user-malli-schema-validation-label
          nil

          callout-opts 
          {:label          (str (:ns/name fn-info)
                                "/"
                                (:fn/name fn-info))
           :padding-bottom 1}

          #_(if (= :ultra-compact user-spacing)
            {:label data-ks-ns}
            (some->> user-malli-schema-validation-label 
                     (hash-map :label)))]
      (explain-malli 
       malli-schema
       supplied-props 
       {:file-info-str                     data-ks-at
        :display-file-info-as-side-label?  true
        :display-schema?                   false
        :display-explain-data?             false
        ;;  :success-message                   :bling.explain/explain-malli-success-verbose
        ;;  :success-message                   :bling.explain/explain-malli-success-simple

        ;; TODO should be :narrow-ancestor-keys ...?
        ;; maybe don't highlight keys with yellow
        :select-keys-in-problem-path?      true     
        :highlight-missing-keys?           true     
        :section-body-indentation          0
        :spacing                           user-spacing
        :omit-sections                     [:problem-value]
        :omit-section-labels               ["UI component:" "Supplied props:"]
        :highlighted-problem-section-label "Supplied props:"
        :preamble-section-label            "UI component:"
        ;; :preamble-section-body             (str (:ns/name fn-info)
        ;;                                          "/"
        ;;                                          (:fn/name fn-info))
        :callout-opts                      callout-opts

        ;; This is done in defui macro, so probably don't need this at runtime
        ;; :error-handler                     (partial
        ;;                                     check-each-schema-at-runtime
        ;;                                     malli-schema
        ;;                                     custom-props
        ;;                                     fn-info)

        }))))

;; This is done in defui macro, so probably don't need this at runtime


;; (defn- contains-malli-error? [e]
;;   (contains? #{":malli.core/invalid-schema"
;;                ":malli.core/child-error"}
;;              (.-message e)))


;; (defn check-each-schema-at-runtime
;;   [malli-schema custom-props fn-info e]
;;   (when (contains-malli-error? e)
;;     (doseq [vc   (some-> malli-schema rest)
;;             :let [k (first vc)]]
;;       (try (malli.core/validate [:map vc] 42)
;;            (catch js/Object
;;                   err
;;              (when (contains-malli-error? err)
;;                (let [custom-prop-schema (or (some-> custom-props k :schema)
;;                                             (last vc))]
;;                  (callout {:label       (.-message e)
;;                            :type        :error
;;                            :padding-top 1
;;                            :side-label  (str (:ns/name fn-info) 
;;                                              ":" 
;;                                              (:line fn-info) 
;;                                              ":" 
;;                                              (:column fn-info))
;;                            :label-theme :pipe}
;;                           (bling [:italic "Component:"])
;;                           "\n\n"
;;                           (hifi (symbol (:fn/name fn-info)) {:margin-inline-start 2})
;;                           "\n\n\n"
;;                           (bling [:italic "Prop:"])
;;                           "\n\n"
;;                           (hifi k {:margin-inline-start 2})
;;                           (when custom-prop-schema "\n\n\n")
;;                           (when custom-prop-schema (bling [:italic "Schema:"]))
;;                           (when custom-prop-schema "\n\n")
;;                           (when custom-prop-schema (hifi custom-prop-schema
;;                                                          {:margin-inline-start 2}))
;;                           (when true "\n\n\n")
;;                           (when true (bling [:italic "Component props map schema:"]))
;;                           (when true "\n\n")
;;                           (when true (hifi malli-schema {:margin-inline-start 2}))
;;                           ))))))))
