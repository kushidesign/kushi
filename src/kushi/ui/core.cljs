(ns ^:dev/always kushi.ui.core
  (:require [fireworks.core :refer [? !? ?> !?>]]
            [clojure.string :as string]
            [kushi.ui.variants :as variants]
            [kushi.ui.extract]
            [kushi.util :refer [keyed as-str maybe]]
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



;; data-ks-attribute resolution ------------------------------------------------

(defn data-ns-flex-attrs [m]
  (when-let [{:keys [display]} m]
    (cond 
      (or (keyword? display) (string? display))
      (!? {:data-ks-display (as-str display)})
      
      (vector? display)
      (!? (when-let [[css-display] (seq display)]
           (let [css-display (as-str css-display)]
             (cond
               (= "flex" css-display)
               (let [[_ flex-direction justify-content align-items] display]
                 {:data-ks-display (as-str css-display)
                  :data-ks-fd      (some-> flex-direction as-str)
                  :data-ks-jc      (some-> justify-content as-str)
                  :data-ks-ai      (some-> align-items as-str)})
               )))))))


(defn- resolve-supplied-prop [prop supplied when-not-nil]
  (if (true? (:boolean? prop))
    (if (false? supplied) nil "")
    (or when-not-nil
        (kushi.util/as-str supplied))))


(defn- resolve-default-prop [prop default]
  (if (true? (:boolean? prop))
    (case default
      false   nil
      "false" nil
      "")
    (kushi.util/as-str default)))


(defn- data-ks-attr*
  "Returns something like:
   `{:data-ks-surface \"transparent\"}`
   or
   `{:data-ks-inert \"\"}`
   
   This sorts out `data-ks-*` attrs that are boolean,
   but need to be supplied as `data-ks-foo=\"\"` (when true, appears in dom as `data-ks-foo`)
   or `data-ks-foo=nil` (if false, does not appear in dom)"

  [{:keys [when-not-nil default] :as prop} supplied data-ks-key]

  (cond 
    (not (nil? supplied))
    {data-ks-key (resolve-supplied-prop prop supplied when-not-nil)}

    default
    {data-ks-key (resolve-default-prop prop default)}))


(defn- shared-prop-destined-for-data-ks-attr? [k data-ks?]
  (and (contains? variants/props k)
       (not (false? data-ks?))))


(defn- destined-for-data-ks-attr? [k {:keys [data-ks?]}]
  (or (shared-prop-destined-for-data-ks-attr? k data-ks?)
      ;; user prop destined for data-ks-attr
      (true? data-ks?)))


(defn- data-ks-attr
  [props k prop]
  (let [supplied    (get props k)
        style       (when (:style-tokens? prop)
                      (data-ks-attrs-style-map k supplied))
        data-ks-key (keyword (str "data-ks-" (name k)))
        ret         (data-ks-attr* prop supplied data-ks-key)]
    (? {:when (= k :contour)} (keyed [supplied data-ks-key style ret]))
    (merge ret (when style {:style style}))))


(defn data-ks-attrs 
  "Creates a map of data-ks-* attributes based on defined prop schema from
   component rendering function's metadata map, which is defined in the defui
   macro. To be called at runtime from within runtime portion of defui macro.
   
   If one of the props is supplied, it will convert it to a data-ks-* attribute,
   or do something else with it, such as set a css var in the style map, or
   just ignore it, if the prop is just used for internal logic in the component
   rendering function."
  [props with-schema]
  (? 'runtime:data-ks-attrs
      (merge (? (reduce-kv 
              (fn [m k prop]
                (merge m
                       (when (destined-for-data-ks-attr? k prop)
                         (data-ks-attr props k prop))))
              {} 
              with-schema))
             (? (data-ns-flex-attrs props))
             (some->> props :at (hash-map :data-ks-at)))))


;; Extraction ------------------------------------------------------------------
(def extract kushi.ui.extract/extract)


;; Validation ------------------------------------------------------------------
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
