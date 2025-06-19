(ns ^:dev/always kushi.ui.core
  (:require-macros [kushi.ui.core])
  (:require [clojure.string :as string]
            [fireworks.core :refer [? !? ?> !?>]]
            [me.flowthing.pp :refer [pprint]]
            [kushi.core :refer [merge-attrs]]
            [kushi.ui.variants :as variants]
            [kushi.util :refer [keyed]]
            ))


(defn attr+children [coll]
  (when (coll? coll)
    (let [[a & xs] coll
          attr     (when (map? a) a)]
      [attr (if attr xs coll)])))

;; TODO add in docs that custom attribute names must pass this regex
(defn user-attr? [x]
  (and (keyword? x)
       (->> x name (re-find #"^-[^\s\d]+"))) )

(defn unwrapped-children [children]
  (let [fc (nth children 0 nil)]
    (if (and
          (seq? children)
          (= 1 (count children))
          (seq? fc)
          (seq fc))
      fc
      children)))

(defn children
  "For internal use by defcom macro"
  [children* f]
  (let [children (unwrapped-children children*)
        fragment? (-> children* first (= :<>))]
    (into (if fragment? [] [:<>]) (if f (map f children) children))))

(defn children2
  "For internal use by defcom macro"
  [children*]
  (let [children (unwrapped-children children*)
        fragment? (-> children* first (= :<>))]
    (into (if fragment? [] [:<>]) children)))

(defn user-ks [attr*]
  #_(some->> attr*
             keys
             (filter user-attr?)
             (into #{}))
  (some->> attr*
           (reduce-kv (fn [acc k _]
                        (if (user-attr? k)
                          (conj acc k)
                          acc))
                      #{})))
(defn grouped-attrs
  [attr* user-ks]
  #_(some->> attr*
             (group-by #(contains? user-ks (first %)))
             (map (fn [[k v]] {(if k :opts :attr) (into {} v)}))
             (apply merge))
  (some->> attr*
           (group-by #(contains? user-ks (first %)))
           (reduce-kv (fn [m k v]
                        (assoc m (if k :opts :attr) (into {} v)))
                      {})))

(defn opts-w-normal-keys
  [opts]
  #_(->> opts
                 (map (fn [[k v]] [(-> k name (subs 1) keyword) v]))
                 (into {}))
  (some->> opts
           (reduce-kv
            (fn [m k v]
              (assoc m (-> k name (subs 1) keyword) v))
            {})))

#_(defn extract
  "Reorganizes arguments to component and returns:
   [map-of-user-opts attr child1 child2 ...]"
  [coll]
  (when (coll? coll)
    (let [[attr* children]    (attr+children coll)
          user-ks             (user-ks attr*)
          {:keys [attr opts]} (grouped-attrs attr* user-ks)
          opts                (opts-w-normal-keys opts)
          ;; TODO can you eliminate this unwrapping?
          children            (->> children (remove nil?) unwrapped-children)]

      #_(into []
              (concat [opts-w-normal-keys attr]
                      (->> children (remove nil?) unwrapped-children)))
      (into [opts attr]
            children))))



;; TODO - use new optimized fns from above
;; (defn extract-1
;;   "Extracts custom attributes from mixed map of html attributes and
;;    attributes/options that are specific to the ui component.
   
;;    Returns a map:
;;    {:opts     <map-of-custom-attributes>
;;     :attrs    <html-attributes>
;;     :children <children>}"
;;   [args schema]
;;   (when (coll? args)
;;     (let [[src args]          
;;           (let [[src & rest] args]
;;             (if (some-> src meta :kushi.ui/form)
;;               [src rest]
;;               [nil args]))                

;;           [attr* children]    
;;           (attr+children args)

;;           user-ks             
;;           (some->> attr*
;;                    keys
;;                    (filter user-attr?)
;;                    (into #{}))

;;           {:keys [attr opts]} 
;;           (some->> attr*
;;                    (group-by #(contains? user-ks
;;                                          (nth % 0 nil)))
;;                    (map (fn [[k v]]
;;                           {(if k :opts :attr) (into {} v)}))
;;                    (apply merge))

;;           supplied-opts       
;;           (->> opts
;;                (map (fn [[k v]]
;;                       [(-> k name (subs 1) keyword) v]))
;;                (into {}))]


;;        (when (and ^boolean js/goog.DEBUG schema)
;;          (let [as-map
;;                (reduce  (fn [acc [prop a b]]
;;                           (let [opts (when (map? a) a)
;;                                 schema (if opts b a)
;;                                 schema (when (ifn? schema) schema)]
;;                             (assoc acc
;;                                    prop
;;                                    {:prop prop
;;                                     :opts opts
;;                                     :schema schema})))
;;                         {}
;;                         (rest schema))

;;                problems
;;                (keep (fn [[_ {:keys [prop opts schema]}]]
;;                        (when-let [problem
;;                                   (if (some-> opts :required? true?)
;;                                     (if-not (contains? attr* prop)
;;                                       :missing-key
;;                                       (when-not (schema (prop attr*)) schema))
;;                                     (when-let [v (prop attr*)]
;;                                       (when-not (schema v) schema)))]
;;                          {:in      [prop]
;;                           :prop    prop
;;                           :problem problem
;;                           :value   (prop attr*)}))
;;                      as-map)]

;;            (doseq [{:keys [in prop problem value]} problems]
;;              (let [pprint-with-indent
;;                    (fn [x]
;;                      (-> x
;;                          pprint
;;                          with-out-str
;;                          (string/split "\n")
;;                          (->> (map #(str "  " %)))
;;                          (->> (string/join "\n")) ))
                   
;;                    section-break
;;                    "\n\n\n"
                   
;;                    missing-key?
;;                    (= problem :missing-key )]

;;                (js/console.warn "")

;;                (if missing-key? 

;;                  (.apply js/console.log
;;                          js/console
;;                          #js [(str "══ Schema Validation Error ════ %ckushi.ui.icon%c"
;;                                    section-break

;;                                    "%cMissing key:%c\n\n"

;;                                    "  %c" prop "%c"

;;                                    section-break
;;                                    "%cProblem value:%c\n\n"
;;                                    (str (string/replace 
;;                                          (pprint-with-indent 
;;                                           attr*)
;;                                          #"^  " 
;;                                          "  %c")
;;                                         "%c")
                                   
;;                                    section-break
;;                                    "%cMust satisfy:%c\n\n"
;;                                    (pprint-with-indent problem)
;;                                    "\n"
;;                                    "\n")
;;                               "font-style:italic;"
;;                               "line-height:initial;"
;;                               "font-style:italic;"
;;                               "line-height:initial;"
;;                               "font-weight:bold;"
;;                               "line-height:initial;"
;;                               "font-style:italic;"
;;                               "line-height:initial;"
;;                               "font-weight:bold; text-decoration-line: underline; text-decoration-style: wavy; text-decoration-color: red; line-height: 2;"
;;                               "line-height:initial;"
;;                               "font-style:italic;"
;;                               "line-height:initial;"])

;;                  (.apply js/console.log
;;                          js/console
;;                          #js [(str "══ Schema Validation Error ════ %ckushi.ui.icon%c"
;;                                    section-break

;;                                    "%cProperty:%c\n\n"

;;                                    "  %c" prop "%c"

;;                                    section-break
;;                                    "%cProblem value:%c\n\n"
;;                                    (str (string/replace 
;;                                          (pprint-with-indent 
;;                                           value)
;;                                          #"^  " 
;;                                          "  %c")
;;                                         "%c")
                                   
;;                                    section-break
;;                                    "%cMust satisfy:%c\n\n"
;;                                    (pprint-with-indent problem)
;;                                    "\n"
;;                                    "\n")
;;                               "font-style:italic;"
;;                               "line-height:initial;"
;;                               "font-style:italic;"
;;                               "line-height:initial;"
;;                               "font-weight:bold;"
;;                               "line-height:initial;"
;;                               "font-style:italic;"
;;                               "line-height:initial;"
;;                               "font-weight:bold; text-decoration-line: underline; text-decoration-style: wavy; text-decoration-color: red; line-height: 2;"
;;                               "line-height:initial;"
;;                               "font-style:italic;"
;;                               "line-height:initial;"]))))))


;;       ;; (when (and ^boolean js/goog.DEBUG schema)
;;       ;;   (when (map? attr*)
;;       ;;     (bling.explain/explain-malli schema attr* {:display-schema? false}))

;;       ;;   #_(kushi.core/validate-options* {:uic-meta      uic-meta
;;       ;;                                    :supplied-opts supplied-opts 
;;       ;;                                    :src           src}))

;;       {:opts     supplied-opts
;;        :attrs    attr
;;        :children (->> children
;;                       (remove nil?)
;;                       unwrapped-children)})))

(def html-attrs 
  #{:bgcolor :accept :accept-charset :access-key :action :allow-full-screen :allow-transparency :alt :async :auto-complete :auto-focus :auto-play :capture
    :cell-padding :cell-spacing :challenge :char-set :checked :cite :class :class-name :cols :col-span :content :content-editable :context-menu :controls :controls-list
    :coords :cross-origin :data :date-time :default :defer :dir :disabled :download :draggable :enc-type :form :form-action :form-enc-type :form-method
    :form-no-validate :form-target :frame-border :headers :height :hidden :high :href :href-lang :html-for :http-equiv :icon :id :input-mode :integrity
    :is :key-params :key-type :kind :label :lang :list :loop :low :manifest
    :margin-height :margin-width :max :max-length :media :media-group :method :min :min-length :multiple :muted :name :no-validate :nonce :open :optimum :pattern :placeholder
    :poster :preload :profile :radio-group :read-only :rel :required :reversed :role :rows :row-span :sandbox :scope :scoped :scrolling :seamless :selected :shape :size :sizes
    :span :spell-check :src :src-doc :src-lang :src-set :start :step :style :summary :tab-index :target :title :type :use-map :value :width :wmode :wrap
    ; React specific 
    :ref :key})

(def kushi-ui-props 
  #{:ns :inert? :end-enhancer :start-enhancer})

(defn extract
  "Extracts custom attributes from mixed map of html attributes and
   attributes/options that are specific to the ui component.
   
   Returns a map:
   {:opts     <map-of-custom-attributes>
    :attrs    <html-attributes>
    :children <children>}"
  ([args]
   (extract args nil))
  ([args custom-option-ks]
   (doseq [k custom-option-ks]
     (when (contains? html-attrs k)
       (js/console.warn (str "HTML attribute name clash\n" k "\n"
                             "https://developer.mozilla.org/en-US/docs/Web/HTML/Reference/Attributes" "\n"
                             "You should probably choose a different name for your custom attribute."))))
   (when (coll? args)
     (let [[src args]          
           (let [[src & rest] args]
             (if (some-> src meta :kushi.ui/form)
               [src rest]
               [nil args]))                

           [attr* children]    
           (attr+children args)

           user-ks             
           (some->> attr*
                    keys
                    (filter #(or (contains? variants/variants-by-custom-opt-key %)
                                 (contains? (into #{} custom-option-ks) %)
                                 (contains? kushi-ui-props %)))
                    (into #{}))

           {:keys [attrs opts]} 
           (some->> attr*
                    (group-by #(contains? user-ks (nth % 0 nil)))
                    (map (fn [[k v]]
                           {(if k :opts :attrs) (into {} v)}))
                    (apply merge))
           attrs
           (apply dissoc attrs user-ks)]

      ;; (when (and ^boolean js/goog.DEBUG schema)
      ;;   (when (map? attr*)
      ;;     (bling.explain/explain-malli schema attr* {:display-schema? false}))
       
      ;;   #_(kushi.core/validate-options* {:uic-meta      uic-meta
      ;;                                    :supplied-opts supplied-opts 
      ;;                                    :src           src}))
       
       {:opts     opts
        :attrs    attrs
        :children (->> children
                       (remove nil?)
                       unwrapped-children)}))))



;; Opts schema validation ------------------------------------------------------


(defn- pprint-with-indent
  ([x]
   (pprint-with-indent x 2))
  ([x n]
   (-> x
       pprint
       with-out-str
       (string/split "\n")
       (->> (map #(str (string/join (repeat n " "))
                       %)))
       (->> (string/join "\n")))))

(defn section-with-header [header v m]
  (str (:section-break m)
       "%c" header ":%c" (:section-header-break m)
       "  %c" v "%c"))

(def warning-config
  {:opt-validation-warning/template :dense})


(defn- missing-key-warning 
  [{:keys [schema
           prop
           src-ns]
    :as   m}]

  (case (:opt-validation-warning/template warning-config)
    :dense 
    ;; Compact version
    (.apply js/console.log
            js/console
            #js 
             [(str src-ns
                   (when src-ns "\n")
                   (:fn/fq-name schema)
                   "\n"
                   "%cRequired key is missing:%c\n"
                   "%c" prop "%c")
              "font-style:italic;"
              "line-height:initial;"
              "font-weight:bold; text-decoration-line: underline; text-underline-offset: 3px;text-decoration-style: wavy; text-decoration-color: red; line-height: 2; background-color:rgba(255, 5, 5, 0.2)"
              "line-height:initial;"])

   ;; Verbose version
    #_(.apply js/console.log
              js/console
              #js [(str "══ UI Schema Validation Error ════"

                        section-break
                        "%cUI component:%c\n\n"
                        "  %c" (:fn/fq-name schema) "%c"

                        section-break
                        "%cRequired key is missing:%c\n\n"

                        "  %c" prop "%c"

                        section-break
                        "%cAttributes map with missing key:%c\n"
                        (str (string/replace 
                              (pprint-with-indent 
                               attr*)
                              #"^  " 
                              "  %c")
                             "%c")
                        "\n")
                   "font-style:italic;"
                   "line-height:initial;"
                   "font-weight:bold;"
                   "line-height:initial;"
                   "font-style:italic;"
                   "line-height:initial;"
                   "font-weight:bold;"
                   "line-height:initial;"
                   "font-style:italic;"
                   "line-height:initial;"
                   "font-weight:bold; text-decoration-line: underline; text-underline-offset: 5px;text-decoration-style: wavy; text-decoration-color: red; line-height: 3;"
                   "line-height:initial;"])))


(defn- unprefixed-key-warning 
  [{:keys [schema
           unprefixed-key
           prop
           unprefixed-key-value
           src-ns]
    :as   m}]
  (case (:opt-validation-warning/template warning-config)
    :dense
    (.apply js/console.log
            js/console
            #js 
             [(str "%c" src-ns "%c"
                   (when src-ns "\n")
                   "%c" (:fn/fq-name schema) "%c"
                   "\n"
                   "%cUnprefixed key:%c " "{%c" prop "%c" " " unprefixed-key-value "}\n"

                              
                   "%cSuggested fix:%c  " "%c" prop "%c"
                   (when (string/starts-with? (str src-ns) "kushi.showcase")
                     (str "\n\nThis is caused by a variant example or demo\nfrom "
                          (:fn/fq-name schema)))
                   )
              "line-height: 2;"
              "line-height:initial;"
              "line-height: 2;"
              "line-height:initial;"
              "font-style:italic;line-height: 2;"
              "line-height:initial;"
              "font-weight:bold; text-decoration-line: underline; text-underline-offset: 3px;text-decoration-style: wavy; text-decoration-color: red; line-height: 2; background-color:rgba(255, 5, 5, 0.2)"
              "line-height:initial;"
              "font-style:italic;line-height: 2;"
              "line-height:initial;"
              "font-weight:bold; line-height: 2; background-color:rgba(5, 197, 255, 0.18)"
              "line-height:initial;"
              ])

    (.apply js/console.log
            js/console
            (.concat #js [(str "══ Schema Validation Error ════"

                               (when src-ns
                                 (section-with-header "Source" src-ns m))

                               (section-with-header "UI component" 
                                                    (:fn/fq-name schema)
                                                    m)

                               (section-with-header "Unprefixed key" 
                                                    unprefixed-key
                                                    m)

                               (section-with-header "Suggested fix" 
                                                    prop
                                                    m)
                               "\n")]

                     (when src-ns
                       #js ["font-style:italic;"
                            "line-height:initial;"
                            "font-weight:bold;"
                            "line-height:initial;"])

                     #js ["font-style:italic;"
                          "line-height:initial;"
                          "font-weight:bold;"
                          "line-height:initial;"
                          "font-style:italic;"
                          "line-height:initial;"
                          "font-weight:bold; text-decoration-line: underline; text-underline-offset: 3px;text-decoration-style: wavy; text-decoration-color: red; line-height: 2;"
                          "line-height:initial;"
                          "font-style:italic;"
                          "line-height:initial;"
                          "font-weight:bold;"
                          "line-height:initial;"]))))


(defn mf 
  [attr*
   [prop {:keys [required? schema]}]
   [_ {quoted-schema :schema}]]
  (when-let [problem
             (if (true? required?)
               (if-not (contains? attr* prop)
                 :missing-key
                 (when-not (schema (prop attr*)) schema))
               (when-let [v (prop attr*)]
                 ;; This is where validation occurs
                 (when-not (schema v)
                   (if (fn? schema) quoted-schema schema))))]
    {:in      [prop]
     :prop    prop
     :problem problem
     :value   (prop attr*)}))


(defn validate*
  [schema args]
  (let [attr* (first args)]
    (when (and (map? attr*) (seq attr*))
      (let [problems
            (remove
             nil?  
             (map (partial mf attr*)
                  (:opts/expanded schema)
                  (:opts/quoted schema)))]

        #_(? (select-keys schema [:form/meta :fn/name :ns/name])) 
        (doseq [{:keys [in prop problem value unprefixed-key-value unprefixed-key]}
                problems]
          (let [section-break        "\n\n\n"
                section-header-break "\n\n"
                missing-key?         (= problem :missing-key )
                unprefixed-key?      (= problem :unprefixed-key)
                src-ns               (:ns attr*)
                warning-opts         (keyed [section-break
                                             section-header-break
                                             unprefixed-key
                                             unprefixed-key-value
                                             schema
                                             prop
                                             value
                                             src-ns])]

            #_(js/console.warn "")

            (cond
              unprefixed-key? 
              (unprefixed-key-warning warning-opts)

              missing-key? 
              (missing-key-warning warning-opts)

              :else
              #_(problem-value-warning warning-opts)
              ;; Compact version
              (.apply js/console.log
                      js/console
                      
                      (if src-ns 
                        #js [(str 
                              "%c══ %c"
                              "%c" src-ns "%c"
                              " %c════%c"
                              "\n"
                              (:fn/fq-name schema)
                              "\n"
                              "{%c" prop "%c" " " "%c" value "%c}"
                              "\n"
                              "%cMust satisfy:%c\n"
                              "%c" (pprint-with-indent problem 0) "%c")

                             "color:#af8700;"
                             "line-height:initial;"
                             "font-style:italic;"
                             "line-height:initial;"
                             "color:#af8700;"
                             "line-height:initial;"
                             
                             "font-weight:bold; line-height: 2; background-color:rgba(5, 197, 255, 0.18)"
                             "line-height:initial;"
                             "font-weight:bold; text-decoration-line: underline; text-underline-offset: 3px;text-decoration-style: wavy; text-decoration-color: red; line-height: 2; background-color:rgba(255, 5, 5, 0.2)"
                             "line-height:initial;"
                             "font-style:italic;"
                             "line-height:initial;"
                             "font-weight:normal;"
                             "line-height:initial;"]

                        #js [(str 
                              (:fn/fq-name schema)
                              "\n"
                              "{%c" prop "%c" " " "%c" value "%c}"
                              "\n"
                              "%cMust satisfy:%c\n"
                              "%c" (pprint-with-indent problem 0) "%c")

                             "font-weight:bold; line-height: 2; background-color:rgba(5, 197, 255, 0.18)"
                             "line-height:initial;"
                             "font-weight:bold; text-decoration-line: underline; text-underline-offset: 3px;text-decoration-style: wavy; text-decoration-color: red; line-height: 2; background-color:rgba(255, 5, 5, 0.2)"
                             "line-height:initial;"
                             "font-style:italic;"
                             "line-height:initial;"
                             "font-weight:normal;"
                             "line-height:initial;"]
                        ))

                ;; Verbose version
              #_(.apply js/console.log
                        js/console
                        #js [(str "══ Schema Validation Error ════"
                                  section-break
                                  "%cUI component:%c\n\n"
                                  "  %c" (:fn/fq-name schema) "%c"

                                  section-break
                                  "%cProperty:%c\n\n"

                                  "  %c" prop "%c"

                                  section-break
                                  "%cProblem value:%c\n"
                                  (str (string/replace 
                                        (pprint-with-indent 
                                         value)
                                        #"^  " 
                                        "  %c")
                                       "%c")
                                  
                                  section-break
                                  "%cMust satisfy:%c\n\n"
                                  (pprint-with-indent problem)
                                  "\n")
                             "font-style:italic;"
                             "line-height:initial;"
                             "font-weight:bold;"
                             "line-height:initial;"
                             "font-style:italic;"
                             "line-height:initial;"
                             "font-weight:bold;"
                             "line-height:initial;"
                             "font-style:italic;"
                             "line-height:initial;"
                             "font-weight:bold; text-decoration-line: underline; text-underline-offset: 5px;text-decoration-style: wavy; text-decoration-color: red; line-height: 3;"
                             "line-height:initial;"
                             "font-style:italic;"
                             "line-height:initial;"]))))))))
