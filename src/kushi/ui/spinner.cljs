(ns kushi.ui.spinner
  (:require
   [fireworks.core :refer [? !? ?> !?>]]
   [kushi.core :refer (css-vars-map css defcss sx merge-attrs validate-option)]
   [kushi.ui.core :refer (defui)]))

(defcss "@keyframes spin"
  [:0% {:transform "rotate(0deg)"}]
  [:100% {:transform "rotate(360deg)"}])

(defcss "@keyframes pulsing"
  [:0% {:opacity 1}]
  [:50% {:opacity 0}])

(defcss ".ks-spinner-wrapper"
  :position--relative
  :pi--0.3em
  :d--inline-flex
  :flex-direction--row
  :jc--c
  :ta--center
  :ai--c
  :min-width--$loading-spinner-height)

;; changed vec to map
;; changed sym keys to prefixed keywords
;; changed :pred to :schema, if member of variants, elide

(defui spinner
  {:docs         "A spinner"
   :summary      "Round & round"
   :props/shared [:spinner-type 
                  :size       
                  [:colorway {:default nil}]]}
  
  [& args]
  (let [{:keys [spinner-type]} 
        &props

        more-attrs          (merge {:aria-hidden     true
                                    :class [:surface-transparent]})]
               

    (cond

      (contains? #{:propeller "propeller"} spinner-type)
      [:div {:data-ks-spinner ""
             :class              (css ".ks-propeller-wrapper"
                                      :.ks-spinner-wrapper
                                      :.transition
                                      :pi--0.5em)}
       [:div (merge-attrs
              {:class               (css ".ks-propeller"
                                         [:animation
                                          "var(--spinner-animation-duration) linear infinite spin"]
                                         [:b
                                          "max(0.055em, 1px) solid currentColor"]
                                         :h--$loading-spinner-height
                                         :w--0px)}
              more-attrs
              &attrs)]]
      

      (contains? #{:thinking "thinking"} spinner-type)
      (let [circle        [:div (merge-attrs (sx ".ks-pulsing-dot"
                                                 :w--0.3em
                                                 :h--0.3em
                                                 :bgc--currentColor
                                                 [:animation "var(--spinner-animation-duration) linear infinite pulsing"]
                                                 ["nth-child(2):animation-delay" "calc(var(--spinner-animation-duration) / 4)"]
                                                 ["nth-child(3):animation-delay" "calc(var(--spinner-animation-duration) / 2)"])
                                             {:data-ks-shape :pill})]]

        [:div {:data-ks-spinner ""
               :class              (css ".ks-thinking-wrapper"
                                        :.ks-spinner-wrapper
                                        :.transition)} 
         [:div (merge-attrs
                {:class               (css
                                       ".ks-thinking"
                                       :gap--0.333em)
                 :data-ks-display      :flex-row-center}
                more-attrs
                &attrs)
          circle
          circle
          circle]])
      
      :else
      [:div {:data-ks-spinner ""
             :class              (css ".ks-donut-wrapper"
                                      :.ks-spinner-wrapper
                                      :.transition)}
       [:div (merge-attrs
              {:class                   (css ".ks-donut"
                                             :position--relative
                                             :.before-position-absolute-fill
                                             :.after-position-absolute-fill
                                             [:animation
                                              "var(--spinner-animation-duration) linear infinite spin"]
                                             [:before:bw "max(2.5px, 0.125em)"]
                                             [:after:bw "max(2.5px, 0.125em)"]
                                             :w--$loading-spinner-height
                                             :h--$loading-spinner-height
                                             :before:border-radius--9999px
                                             :before:bs--solid
                                             :before:bc--transparent
                                             :before:bbsc--currentColor
                                             :after:border-radius--9999px
                                             :after:o--0.2
                                             :after:bs--solid
                                             :after:bc--currentColor)
               :data-ks-before-position :absolute-fill
               :data-ks-after-position  :absolute-fill}
              more-attrs
              &attrs)]])))




