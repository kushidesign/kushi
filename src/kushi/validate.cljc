(ns kushi.validate
  (:require 
   [fireworks.core :refer [? !? ?> !?>]]
   [bling.explain :refer [explain-malli explain-malli*]]
   [kushi.util :as util :refer [keyed when->]]
   [malli.core :as m :refer [validate]]
   [kushi.ui.variants :as variants]))

;; TOOD - Use new bling.core/file-info-str
(defn- file+line+col-map [{:keys [file line column]}]
  {:file (or file "[unresolved ns]") :line line :column column})

(defn ^:public ^:no-doc validate-sx2 [&form schema {:keys [form-meta] :as opts}]
  (when-not (validate schema &form)
    (let [problems    
          (explain-malli* 
           schema
           &form
           (merge {:display-schema?    false
                   :form               &form
                   :spacing            :compact
                   ;; `:fallbacks-by-entry` will alert the user that a fallback
                   ;; will be used for an invalid value on a specific entry
                   :fallbacks-by-entry variants/defaults-on-invalid 
                   :callout-opts       (assoc
                                        (file+line+col-map form-meta)
                                        :border-shape
                                        :round
                                        :border-weight
                                        :normal
                                        :label-theme
                                        :tab)}
                  (file+line+col-map form-meta)
                  opts))



          bad-entries 
          (reduce (fn [acc {:keys [in]}]
                    (if-let [[i k]
                             (some-> in
                                     (when-> #(and (= 2 (count %))
                                                   #_(keyword? (nth % 1)))))]
                      (update-in acc [i] conj k )
                      acc))
                  {}
                  problems)
          
          fallbacks-for-invalid-entries
          (reduce-kv (fn [m i v]
                         (assoc m
                                (dec i)
                                (select-keys variants/defaults-on-invalid 
                                             (vec v))))
                       {}
                       bad-entries)

          ;; stripped    (reduce-kv
          ;;              (fn [vc i bad-keys]
          ;;                (assoc-in vc
          ;;                          [i]
          ;;                          (apply dissoc (nth vc i) bad-keys)))
          ;;              (vec args)
          ;;              bad-entries)
          ]
      (keyed [bad-entries fallbacks-for-invalid-entries #_stripped])
      )))
