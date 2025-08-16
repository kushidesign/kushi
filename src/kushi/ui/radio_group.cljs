(ns kushi.ui.radio-group
  (:require
   [kushi.core :refer (sx merge-attrs)]
   [kushi.ui.label :refer (label)]
   [kushi.ui.flex :refer (flex-row-start)]
   [kushi.ui.radio :refer [radio]]
   [kushi.ui.core :refer (defui)]
   [clojure.string :as string]
   [bling.util :as util]))

;; Sketch for radio-group
(defui radio-group 
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
                                   :required? true
                                   :data-ks?  false}

                  :default-choice {:schema   :string
                                   :desc     "If :choices is a vector of strings, must match one of the strings. If :choices is a vector of maps, must match one of the :label entries in one of the maps."
                                   :default  nil
                                   :data-ks? false}
                  }}

  [& args]
  (let [{:keys [group-id choices default-choice inert]} &props
        radio-group-id (str group-id "-radio-group")]
    ;; Maybe no legend
    (into
     [:div (merge-attrs (sx :.flex-row-start :gap--1.5em)
                        {:id radio-group-id}
                        &attrs)]
     (for [choice choices]
       (let [choice-label     (util/as-str (if (map? choice) (:label choice) choice))
             choice-lowercase (string/lower-case choice-label)
             choice-id        (or (when (map? choice) (:id choice))
                                  (str radio-group-id "-" choice-lowercase "-choice"))
             choice-value     (or (when (map? choice) (:value choice))
                                  choice-lowercase)]
         [flex-row-start 
          [radio
           (merge {:id    choice-id
                   :name  group-id
                   :value (or choice-value choice-lowercase)}
                  (when (= default-choice choice-label)
                    {:defaultChecked true}))]
          [label {:for choice-id} choice-label]])))))


;; Basic example call
;; [radio-group {:id      "foo"
;;               :choices ["Yes" "No" "Maybe"]}]


;; Basic example call, with maps
;; [radio-group {:id      "foo"
;;               :choices [{:label "Yes"
;;                          :value "12"}
;;                         {:label "No"
;;                          :value "2"}
;;                         {:label "Maybe"
;;                          :value "3"}]}]

;; Basic example call, with maps
;; How to apply attrs to members?
;; Maybe leave legend out of it?
;; [radio-group (merge-attrs
;;               (sx :flex-direction--column
;;                   :gap--0.5em)
;;               {:id             "foo"
;;                :choices        [{:label "Yes"
;;                                  :value "12"}
;;                                 {:label "No"
;;                                  :value "2"}
;;                                 {:label "Maybe"
;;                                  :value "3"}]})]
