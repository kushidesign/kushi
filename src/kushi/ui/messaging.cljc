;; WIP namespace for bundling errors and consolidating them
(ns kushi.ui.messaging)

(def state
  (atom {:ui-schema/problems {}}))

(defn hook
  {:shadow.build/stage :compile-finish}
  [{:keys [:shadow.build/build-id
           :shadow.build/build-info
           :build-sources
           :sources
           :output]
    :as build-state}]

  ;; TODO maybe deleted? and added? should be seqs or nils
  ;; @kushi.css.build.state/initial-build?  
  (when (-> @state :ui-schema/problems seq)
    #_(doseq [_ ]
      
      )))
