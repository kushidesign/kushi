(ns ^:dev/always kushi.ui.core
  (:require-macros [kushi.ui.core])
  (:require [fireworks.core :refer [? !? ?> !?>]]
            [kushi.ui.variants :as variants]
            [kushi.util :refer [keyed]]
            [bling.explain :refer [explain-malli]]))

(defn attr+children [coll]
  (when (coll? coll)
    (let [[a & xs] coll
          attr     (when (map? a) a)]
      [attr (if attr xs coll)])))

(defn unwrapped-children [children]
  (let [fc (nth children 0 nil)]
    (if (and
          (seq? children)
          (= 1 (count children))
          (seq? fc)
          (seq fc))
      fc
      children)))


;; TODO - analyze perf benefits of NOT including this in prod
(def ^:private html-attrs 
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
  #{:at :inert :end-enhancer :start-enhancer :loading :stroke-align :stroke-width})

(defn- data-ks-attrs-style-map [k supplied]
  (cond (and (= k :shadows) supplied) 
        (into {}
              (map-indexed (fn [i s]
                             [(str "--_drop-shadow"
                                   (when (pos? i) (str "-" (inc i))))
                              s])
                           (take 3 supplied)))))

(defn data-ks-attrs 
  "Attaches data-ks based on opts from defn metadata map. To be called from
   defui macro."
  [props with-schema]

  ;; Should it be data-ks instead of data?
  ;; Or concept of registry so you don't need to manually add :elide thing?
  (merge (reduce-kv 
          (fn [m k {:keys [default data when-not-nil style-tokens?] :as prop}]
            (!? {:when (= k :shadows)}
                (merge m
                       (when-not (= data :elide)
                         (let [supplied 
                               (get props k)

                               data-ks-*  
                               (keyword (str "data-ks-" (name k)))

                               style    
                               (when style-tokens?
                                 (data-ks-attrs-style-map k supplied))

                               ;; This sorts out `data-ks-*` attrs that are boolean,
                               ;; but need to be supplied as `data-ks-foo=""` (when true, appears in dom as `data-ks-foo`)
                               ;; or `data-ks-foo=nil` (if false, does not appear in dom)
                               ret 
                               (cond (!? {:when (= k :loading)} (not (nil? supplied)))
                                     {data-ks-* (if (true? (:boolean? prop))
                                                  (if (false? supplied) nil "")
                                                  (or when-not-nil
                                                      (kushi.util/as-str supplied)))}
                                     (!? {:when (= k :loading)} default)
                                     {data-ks-* (if (true? (:boolean? prop))
                                                 (case default
                                                   false   nil
                                                   "false" nil
                                                   "")
                                                 (kushi.util/as-str default))})]
                           (!? (keyed [supplied data-ks-* style ret]))
                           (merge ret (when style {:style style})))))))
          {} 
          with-schema)
          (some->> props :at (hash-map :data-ks-at))))


(defn extract
  "Extracts custom attributes from mixed map of html attributes and
   attributes/options that are specific to the ui component.
   
   Returns a map:
   {:props    <map-of-custom-attributes>
    :attrs    <html-attributes>
    :children <children>}"
  ([args]
   (extract args nil))
  ([args custom-option-ks]
   
   ;; TODO - analyze perf benefits of NOT doing this in prod
   (doseq [k custom-option-ks]
     (when (contains? html-attrs k)
       (js/console.warn 
        (str "HTML attribute name clash\n" k "\n"
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

           {:keys [attrs props]} 
           (some->> attr*
                    (group-by #(contains? user-ks (nth % 0 nil)))
                    (map (fn [[k v]]
                           {(if k :props :attrs) (into {} v)}))
                    (apply merge))
           attrs
           (apply dissoc attrs user-ks)]

       {:props    props
        :attrs    attrs
        :children (->> children (remove nil?) unwrapped-children)}))))


(defn validate*2 
  [{:keys [props
           fn-info
           malli-schema
           data-ks-ns]}]
  (when (and (seq props) malli-schema) 
    (let [user-spacing 
          :compact #_:ultra-compact

          user-malli-schema-validation-label
          nil

          callout-opts 
          {:label (str (:ns/name fn-info)
                       "/"
                       (:fn/name fn-info))}
          #_(if (= :ultra-compact user-spacing)
            {:label data-ks-ns}
            (some->> user-malli-schema-validation-label 
                     (hash-map :label)))]
      (explain-malli 
          malli-schema
          props 
          {:file-info-str                     data-ks-ns
           :display-file-info-as-side-label?  true
           :display-schema?                   false
          ;;  :success-message                   :bling.explain/explain-malli-success-verbose
          ;;  :success-message                   :bling.explain/explain-malli-success-simple
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
           }))))
