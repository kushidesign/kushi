(ns kushi.ui.spinner.core
  (:require
   [fireworks.core :refer [? !? ?> !?>]]
   [kushi.core :refer (css-vars-map css defcss sx merge-attrs validate-option)]
   [kushi.ui.shared.theming :refer [data-kushi- get-variants hue-style-map]]
   [kushi.ui.core :refer (extract)]))

(defcss "@keyframes spin"
  [:0% {:transform "rotate(0deg)"}]
  [:100% {:transform "rotate(360deg)"}])

(defcss "@keyframes pulsing"
  [:0% {:opacity 1}]
  [:50% {:opacity 0}])

(defcss "kushi-spinner-wrapper"
  :position--relative
  :pi--0.3em
  :d--inline-flex
  :flex-direction--row
  :jc--c
  :ta--center
  :ai--c
  :min-width--$loading-spinner-height)



(defn spinner
  {:docs "A spinner"
   :summary "Round & round"
   :opts
   '[
     spinner-type
     {:pred    #{:propeller :donut :thinking}
      :default :donut
      :desc    "The variety of spinner"
      :demo    {:label         "Spinner type variants"
                :attrs/display {:-size :large}
                ;; :x-variants      [colorway]
                ;; :rows?           true
                ;; :variant-labels? false
                :args          []}}

     size           
     {:pred    #{:xxxsmall
                 :xxsmall
                 :xsmall
                 :small
                 :medium
                 :large
                 :xlarge
                 :xxlarge
                 :xxxlarge}
      :default nil
      :desc    "Corresponds to the font-size based on Kushi's font-size scale."
      :demo    [{:label           "Propeller, sizes"
                 :attrs           {:-spinner-type :propeller}
                 :variant-labels? false
                 :args            []}
                {:label           "Donut, sizes"
                 :attrs           {:-spinner-type :donut}
                 :variant-labels? false
                 :args            []}
                #_{:label           "Thinking, size variants"
                 :attrs           {:-spinner-type :thinking}
                 :variant-labels? false
                 :args            []}]}
                
                
     colorway       
     {:pred    #{:neutral :accent :positive :negative :warning}
      :default nil
      :desc    "Colorway of the spinner. Can also be a named color from Kushi's design system, e.g `:red`, `:purple`, `:gold`, etc."
      :demo    [{:label           "Propeller, colorways"
                 :attrs           {:-spinner-type :propeller}
                 :attrs/display   {:-size :xlarge}
                 :variant-labels? false
                 :args            []}
                {:label           "Donut, colorways"
                 :attrs           {:-spinner-type :donut}
                 :attrs/display   {:-size :xlarge}
                 :variant-labels? false
                 :args            []}
                {:label           "Thinking, colorways"
                 :attrs           {:-spinner-type :thinking}
                 :attrs/display   {:-size :xxsmall}
                 :variant-labels? false
                 :args            []}]}
                
                ]}
  
  [& args]
  (let [{:keys [opts attrs]} 
        (extract args spinner)

        {:keys [size spinner-type]
         :or   {spinner-type :donut}}
        opts
        
        ;; Why the rename?
        {semantic-colorway :colorway}
        (get-variants opts)
        
        
        data-kushi-colorway (validate-option spinner semantic-colorway)
        data-kushi-size     (validate-option spinner size)
        more-attrs          (merge {:aria-hidden         true
                                    :data-kushi-size     data-kushi-size}
                                   (when-not (contains? #{"neutral" :neutral}
                                                    data-kushi-colorway)
                                     {:data-kushi-colorway data-kushi-colorway}))
        ]
               

    (cond

      (contains? #{:propeller "propeller"} spinner-type)
      [:div {:data-kushi-spinner ""
             :class              (css ".kushi-propeller-wrapper"
                                      :.kushi-spinner-wrapper
                                      :.transition
                                      :pi--0.5em)}
       [:div (merge-attrs
              {:class               (css ".kushi-propeller"
                                         [:animation
                                          "var(--spinner-animation-duration) linear infinite spin"]
                                         [:b
                                          "max(0.055em, 1px) solid currentColor"]
                                         :h--$loading-spinner-height
                                         :w--0px)}
              more-attrs
              attrs)]]
      

      (contains? #{:thinking "thinking"} spinner-type)
      (let [circle        [:div (sx ".kushi-pulsing-dot"
                                    :.pill
                                    :w--0.3em
                                    :h--0.3em
                                    :bgc--currentColor
                                    [:animation "var(--spinner-animation-duration) linear infinite pulsing"]
                                    ["nth-child(2):animation-delay" "calc(var(--spinner-animation-duration) / 4)"]
                                    ["nth-child(3):animation-delay" "calc(var(--spinner-animation-duration) / 2)"])]]

        [:div {:data-kushi-spinner ""
               :class              (css ".kushi-thinking-wrapper"
                                        :.kushi-spinner-wrapper
                                        :.transition)} 
         [:div (merge-attrs
                {:class               (css
                                       ".kushi-thinking"
                                       :.flex-row-c
                                       :gap--0.333em)}
                more-attrs
                attrs)
          circle
          circle
          circle]])
      
      :else
      [:div {:data-kushi-spinner ""
             :class              (css ".kushi-donut-wrapper"
                                      :.kushi-spinner-wrapper
                                      :.transition)}
       [:div (merge-attrs
              {:class               (css
                                     ".kushi-donut"
                                     :position--relative
                                     :.before-absolute-fill
                                     :.after-absolute-fill
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
                                     :after:bc--currentColor)}
              more-attrs
              attrs)]] )))


