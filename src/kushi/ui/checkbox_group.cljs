(ns kushi.ui.checkbox-group
  (:require
   [kushi.core :refer (sx merge-attrs)]
   [kushi.ui.label :refer (label)]
   [kushi.ui.checkbox :refer [checkbox]]
   [kushi.ui.core :refer (defui)]
   [clojure.string :as string]
   [bling.util :as util]))

;; Sketch for radio-group
(defui checkbox-group 
  {:doc          "This is radio docstring"
   :props/family [:container]
   :props/shared []
   :props        {:group-id       {:schema    :string
                                   :required? true}

                  :choices        {:schema    [:or
                                               [:vector [:map
                                                         [:label :string]
                                                         [:value :any]]]
                                               [:vector :string]]
                                   :required? true}

                  :default-choice {:schema   :string
                                   :desc     "If :choices is a vector of strings, must match one of the strings. If :choices is a vector of maps, must match one of the :label entries in one of the maps."
                                   :default  nil
                                   :data-ks? false}}}

  [& args]
  (let [{:keys [group-id choices default-choice]} &props
        radio-group-id (str group-id "-radio-group")]
    (into
     [:div (merge-attrs (sx ".ks-checkbox-group"
                            :.flex-row-start)
                        {:id radio-group-id}
                        &attrs)]
     (for [choice choices]
       (let [choice-label     (util/as-str (if (map? choice) (:label choice) choice))
             choice-lowercase (string/lower-case choice-label)
             choice-id        (or (when (map? choice) (:id choice))
                                  (str radio-group-id "-" choice-lowercase "-choice"))
             choice-value     (or (when (map? choice) (:value choice))
                                  choice-lowercase)]
         [:div (sx ".ks-checkbox-with-label" :.flex-row-start)
          [checkbox
           (merge {:id    choice-id
                   :value (or choice-value choice-lowercase)}
                  (when (= default-choice choice-label)
                    {:defaultChecked true}))]
          [label {:for choice-id} choice-label]])))))
