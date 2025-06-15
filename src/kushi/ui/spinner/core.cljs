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

;; changed vec to map
;; changed sym keys to prefixed keywords
;; changed :pred to :schema, if member of variants, elide




(defn spinner
  {:docs    "A spinner"
   :summary "Round & round"
   :opts    {:-spinner-type {:default   :donut
                             :desc      "The variety of spinner"
                             :demo      {:label         "Spinner type variants"
                                         :attrs/display {:-size :xxxlarge}
                                         :row-style     {:gap :2rem}
                                         }}
             :-size         {:default nil
                             :desc    "Corresponds to the font-size based on Kushi's font-size scale."
                             :demo    [{:label           "Propeller, sizes"
                                        :attrs           {:-spinner-type :propeller}
                                        :row-style       {:border "1px solid red"}
                                        :variant-labels? false} 
                                       {:label           "Donut, sizes"
                                        :attrs           {:-spinner-type :donut}
                                        :variant-labels? false}]}
             :-colorway     {:default nil
                             :desc    "Colorway of the spinner. Can also be a named color from Kushi's design system, e.g `:red`, `:purple`, `:gold`, etc."
                             :demo    [{:label           "Propeller, colorways"
                                        :attrs           {:-spinner-type :propeller}
                                        :attrs/display   {:-size :xlarge}
                                        :variant-labels? false}
                                       {:label           "Donut, colorways"
                                        :attrs           {:-spinner-type :donut}
                                        :attrs/display   {:-size :xlarge}
                                        :variant-labels? false}
                                       {:label           "Thinking, colorways"
                                        :attrs           {:-spinner-type :thinking}
                                        :attrs/display   {:-size :xxsmall}
                                        :variant-labels? false}]}}}
  
  [& args]
  (let [{:keys [opts attrs]} 
        (extract args spinner)

        {:keys [size spinner-type inert?]
         :or   {spinner-type :donut
                inert?       true}}
        opts
        
        ;; Why the rename?
        {colorway :colorway}
        (get-variants opts)
        
        
        ;; data-kushi-colorway (validate-option spinner semantic-colorway)
        ;; data-kushi-size     (validate-option spinner size)
        more-attrs          (merge {:aria-hidden         true
                                    :data-kushi-surface  :transparent
                                    :data-kushi-size     size}
                                   (when (true? inert?) 
                                     {:data-kushi-inert ""})
                                   (when-not (contains? #{"neutral" :neutral}
                                                    colorway)
                                     {:data-kushi-colorway colorway}))]
               

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
                                       :.flex-row-center
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




