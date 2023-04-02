(ns kushi.ui.input.text.core
  (:require-macros
   [kushi.core :refer (sx defclass)])
  (:require
   [kushi.core :refer (merge-attrs)]
   [kushi.ui.core :refer (opts+children)]))

(defclass kushi-text-input-enhancer
  :d--if
  :ai--center
  :jc--c
  :pi--0.375em )

(defn- enhancer [x]
  [:div
   (sx 'kushi-text-input-start-enhancer
       :d--if
       :ai--center
       :jc--c
       :pi--0.375em )
   x])

(defn- input* [& args]
  (let [[opts attrs & _]       (opts+children args)
        {:keys [wrapper-attrs
                start-enhancer
                end-enhancer
                semantic]}     opts]
    [:div
     (merge-attrs
      (sx 'kushi-text-input-wrapper
          :.flex-row-fs
          :ai--stretch
          :jc--sb
          :w--100%
          :w--auto
          :min-height--34px
          :bw--1px
          :bs--solid
          :bc--currentColor
          [:focus-within:bgc :transparent!important] ;; tmp fix for when semantic class + input is focused
          [:focus-within:c :currentColor!important] ;; ["has-ancestor(.error):bc" :$negative-600]
          [:focus-within:bc '(rgba 0 125 250 1)]
          {:class [semantic]})
      wrapper-attrs)
     (when start-enhancer [enhancer start-enhancer])
     [:div (sx 'kushi-text-input-input-wrapper :flex-grow--1)
      [:input
       (merge-attrs
        (sx 'kushi-text-input-input
            :.transition
            :h--100%
            :w--100%
            :pi--0.5em
            :pb--0.5em
            :placeholder:o--0.4
            {:type :text})
        attrs)]]
     (when end-enhancer [enhancer end-enhancer])])
  )

(defn input
  {:desc ["An input enables the entry of single lines of text."]
   :opts '[
           {:name    outer-wrapper-attrs
            :pred    map?
            :default nil
            :desc    ["HTML attributes map applied to the outermost div of the component."
                      "This div wraps the label, input-wrapper div, and the helper text span."]}

           {:name    label
            :pred    string?
            :default nil
            :desc    "The text for `:label` element associated with the input field."}

           {:name    label-attrs
            :pred    map?
            :default nil
            :desc    "HTML attributes map applied to the `:label` element that contains the `label` text."}

           {:name    label-placement
            :pred    #{:block :inline}
            :default :block-start
            :desc    "HTML attributes map applied to the `label` element associated with the `input` element, and end-enhancer div."}

           {:name    label-width
            :pred    #(or (string? %) (keyword? %))
            :default :block-start
            :desc    ["Sets the width of your label \"column\", when `:-label-placement` is set to `:inline`."
                      "Must be a valid css width value (`px`, `em` `rem`, etc)"]}

           {:name    wrapper-attrs
            :pred    map?
            :default nil
            :desc    ["HTML attributes map applied to the input wrapper div, which is bordered by default."
                      "This div wraps the `start-enhancer` div, the actual `input` element, and the `end-enhancer` div."]}

           {:name    start-enhancer
            :pred    #(or (string? %) (vector? %))
            :default nil
            :desc    "A string, hiccup vector, or child component intended to aid the user and positioned within the input field area, at the start"}

           {:name    end-enhancer
            :pred    #(or (string? %) (vector? %))
            :default nil
            :desc    "A string, hiccup vector, or child component intended to aid the user and positioned within the input field area, at the end"}

           {:name    helper
            :pred    string?
            :default nil
            :desc    ["The text for `:.kushi-text-input-helper` label."
                      "If used, this should give the user actionable information about the value of the associated input field."]}

           {:name    semantic
            :pred    #{:neutral :accent :positive :negative :warning}
            :default nil
            :desc    ["The text for `:.kushi-text-input-helper` label."
                      "If used, this should give the user actionable information about the value of the associated input field."]}
           ]}
   [& args]
  (let [[opts attrs & _]     (opts+children args)
        {:keys [
                outer-wrapper-attrs
                label
                label-placement
                label-attrs
                wrapper-attrs
                start-enhancer
                end-enhancer
                helper
                semantic]
         :or   {label " "}}         opts
        {:keys [required
                disabled]}          attrs
        input-id                    (:id attrs)
        inline?                     (= :inline label-placement)
        label-text-attrs   (sx 'kushi-text-input-label-text
                               :.minimal
                               :.info
                               :.block
                               :hover:bgc--transparent!important ;; temp fix
                               :active:bgc--transparent!important ;; temp fix
                               {:class [semantic]})

        helper-label-attrs (when helper
                             (merge-attrs
                              label-text-attrs
                              (sx 'kushi-text-input-helper
                                  :.neutral-secondary-fg
                                  :.inline-block
                                  :.normal
                                  :fs--smaller
                                  :mbs--0.5em)))

        wrapped-input [input* (merge attrs
                                     {:-wrapper-attrs  wrapper-attrs
                                      :-start-enhancer start-enhancer
                                      :-end-enhancer   end-enhancer
                                      :-semantic       semantic})]
        label-with-attrs [:label
                          (merge-attrs
                           label-text-attrs
                           (sx 'kushi-text-input-label
                               :.inline-block
                               [:after:content (when required "\"*\"")]
                               [:after:c (when required :$negative-600)]
                               :after:pis--0.15em
                               {:for input-id})
                           (if inline?
                             (sx 'kushi-text-input-label-block [:mie :0.5em])
                             (sx 'kushi-text-input-label-inline [:mbe :0.5em]))
                           label-attrs)
                          label]

        kushi-input-attrs (merge-attrs (sx 'kushi-input
                                           (when disabled :.disabled)
                                           :ai--center)
                                       (when inline?
                                         (sx 'kushi-input-inline
                                             :d--grid
                                             :gtc--auto:auto))
                                       outer-wrapper-attrs)]
    [:div
     kushi-input-attrs
     label-with-attrs
     wrapped-input
     (when helper
       [:<>
        (when inline? [:div])
        [:span helper-label-attrs helper]])]))
