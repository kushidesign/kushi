(ns ^{:kushi/layer "user-styles"} kushi.ui.tag.demo
  (:require
   [kushi.ui.icon :refer [icon]]
   [kushi.ui.button :refer [button]]
   [kushi.ui.spinner :refer [spinner]]
   [kushi.showcase.core
    :as showcase
    :refer [samples-with-variant samples]]
   [kushi.ui.tag :refer [tag]]))


(def demos
  [{:samples (samples-with-variant
              {:args           ["New"],
               :desc           "Colorway of the tag. Can also be a named color from Kushi's design system e.g `:red` `:purple` `:gold` etc.",
               :variant        :colorway,
               :variant-scale  :colorway/named,
               ;; TODO make work with surface/tag
               :x-variants     [:surface/tag],
               :snippets-label "Colorways",
               :attrs/snippet  {:surface :solid},
               ;; :attrs/display  {:size :small},
               :label          "Colorways ... surfaces × shapes",
               :rows?          true})}

   {:label   "Contour"
    :desc    "Contour of the tag."
    :samples (samples-with-variant
              {:variant       :shape
               :variant-scale :shape/basic
               :attrs         {:colorway :accent
                               :surface  :solid}
               :args          ["New"]})}
   
   {:desc    "Surface variant of the tag.",
    :label   "Surface",
    :samples (samples-with-variant
              {:variant       :surface,
               :variant-scale :surface/simple,
               :attrs         {:colorway     :accent},
               :args          ["New"]})}
   
   {:desc    "General amount of padding inside the tag",
    :label   "Packing",
    :samples (samples-with-variant
              {:attrs   {:colorway :accent
                         :surface  :solid},
               :args    ["New"],
               :variant :packing})}

   {:label   "size"
    ;;  :label/modal "Colorways ..."
    :desc    "Sizes from xxxsmall to xxxlarge"
    ;; :row-style {:border "1px solid red"}
    :samples (samples-with-variant
              {:variant :size
              ;; :variant-labels? false
               :attrs   {:surface  :solid
                         :colorway :accent}
               :args    ["New"]})}

   
   {:label   "Stroke"
    :desc    "Preset stroke styles."
    :samples (samples-with-variant
              {:variant :stroke
              ;;  :variant-scale :shape/basic+rounded
               :attrs   {:colorway :accent
                         :surface  :minimal}
               :args    ["New"]})}

   {:label   "Stroke width"
    :desc    "Custom stroke widths."
    :samples (samples [[tag
                        {:colorway     :accent,
                         :surface      :minimal,
                         :stroke       :soft
                         :stroke-width :1px
                         :stroke-align :inside}
                        "New"]
                       [tag
                        {:colorway     :accent,
                         :surface      :minimal,
                         :stroke       :soft
                         :stroke-width :2px
                         :stroke-align :inside}
                        "New"]
                       [tag
                        {:colorway     :accent,
                         :surface      :minimal,
                         :stroke       :soft
                         :stroke-width :3px
                         :stroke-align :inside}
                        "New"]
                       [tag
                        {:colorway     :accent,
                         :surface      :minimal,
                         :stroke       :soft
                         :stroke-width :4px
                         :stroke-align :inside}
                        "New"]])}


   {:schema  #{:inside :outside},
    :desc    "Alignment of the stroke. Only applies to `:surface` `:outline`",
    :label   "Stroke alignment",
    :require '[[kushi.ui.icon :refer [icon]]],
    :samples (samples [[tag
                        {:colorway     :accent,
                        ;;  :surface      :minimal,
                         :stroke       :medium
                         :stroke-align :inside
                         :stroke-width :2px}
                        "New"]
                       [tag
                        {:colorway     :accent,
                        ;;  :surface      :outline,
                         :stroke       :medium
                         :stroke-align :outside
                         :stroke-width :2px}
                        "New"]])}

   {:desc    "Content at the inline-start position following the tag text. Typically an icon.",
    :label   "Start-enhancer icons",
    :samples (samples [[tag
                        {:start-enhancer :pets,
                         :colorway       :accent,
                         :surface        :solid}
                        "Pets"]
                       [tag
                        {:start-enhancer :auto-awesome,
                         :colorway     :accent,
                         :surface      :soft}
                        "Wow"]
                       [tag
                        {:start-enhancer :check-circle,
                         :colorway     :accent,
                         :surface      :faint
                         :stroke       :soft}
                        "Passing"]
                       [tag
                        {:start-enhancer :check-circle,
                         :colorway     :accent,
                         :surface      :minimal
                         :stroke       :xsoft}
                        "Passing"]])}
   
   {:desc    "Content at the inline-end position preceding the tag text. Typically an icon.",
    :label   "End-enhancer icons",
    :require '[[kushi.ui.icon :refer [icon]]],
    :samples (samples [[tag
                        {:end-enhancer :pets,
                         :colorway     :accent,
                         :surface      :solid}
                        "Pets"]
                       [tag
                        {:end-enhancer :auto-awesome,
                         :colorway     :accent,
                         :surface      :soft}
                        "Wow"]
                       [tag
                        {:end-enhancer :check-circle,
                         :colorway     :accent,
                         :surface      :faint
                         :stroke       :soft}
                        "Passing"]
                       [tag
                        {:end-enhancer :check-circle,
                         :colorway     :accent,
                         :surface      :minimal
                         :stroke       :xsoft}
                        "Passing"]
                       ])}

   #_{:label   "Loading states",
    :desc    "When `true` this will set the appropriate values for `aria-busy` and `aria-label`",
    :schema  boolean?,
    :require '[[kushi.ui.tag :refer [tag]]
               [kushi.ui.icon :refer [icon]]
               [kushi.ui.spinner :refer [spinner]]],
    :samples (samples
              [[tag
                {:loading     true,
                 :colorway     :accent,
                 :surface      :solid,
                 :end-enhancer [spinner {:spinner-type :donut}]
                 }
                "Play"]
               [tag
                {:loading     true,
                 :colorway     :accent,
                 :surface      :solid,
                 :end-enhancer [spinner {:spinner-type :propeller}]}
                "Play"]
               [tag
                {:loading     true,
                 :colorway     :accent,
                 :surface      :solid,
                 :end-enhancer [spinner {:spinner-type :thinking}]}
                "Play"]
               [tag
                {:loading true
                 :colorway :accent
                 :surface  :solid}
                [:span {:style {:visibility :hidden
                                :width      :0px}} 
                 "Play"]
                [spinner {:spinner-type :thinking}]] ])}
   
   ])


;; TODO remove section-label
;; TODO hoist reqs up to a higher level
;; (def sizes
;;   [:xxsmall
;;    :xsmall
;;    :small
;;    :medium
;;    :large
;;    :xlarge])

;; (def examples
;;   (let [playground-tag-rows-container
;;         (sx :.playground-tag-rows-container
;;             :md:gtc--max-content
;;             :gtc--max-content:max-content)
;;         playground-tag-rows-container4
;;         (sx :.playground-tag-rows-container4
;;             :md:gtc--max-content
;;             :gtc--max-content:max-content:max-content:max-content)
;;         playground-tag-rows-container24
;;         (sx :.playground-tag-rows-container24
;;             :md:gtc--max-content
;;             :xsm:gtc--max-content:max-content:max-content:max-content
;;             :gtc--max-content:max-content)]
;;     [(merge
;;       (component-examples/sizes-snippet-scale 'tag "Done")
;;       {:desc            "Sizes from xxsmall to xlarge"
;;        :row-attrs       (sx :md:ai--fe)
;;        :container-attrs playground-tag-rows-container24
;;        :examples        (for [sz sizes]
;;                           {:label (name sz)
;;                            :attrs {:class sz}
;;                            :args  ["Done"]})})
     
;;      {:desc            "Colorway variant"
;;       :sx-attrs        (sx-call (sx :fs--$size-small))
;;       :container-attrs playground-tag-rows-container4
;;       :variants+       [:minimal]
;;       :examples        (for [colorway component-examples/colors]
;;                          {:label (name colorway)
;;                           :args  ["Done"]
;;                           :attrs {:colorway colorway}})}

;;      #_{:desc            "Shape"
;;       :sx-attrs        (sx-call (sx :fs--$size-small))
;;       :container-attrs playground-tag-rows-container4
;;       :variants+       [:minimal]
;;       :examples        (for [s [:rounded :pill :sharp]]
;;                          {:label (name s)
;;                           :args  ["Done"]
;;                           :attrs {:shape s}})}

;;      #_{:desc            "With icons"
;;       :reqs            '[[kushi.ui.icon :refer [icon]]]
;;       :sx-attrs        (sx-call (sx :fs--$size-small))
;;       :container-attrs playground-tag-rows-container4
;;       :variants+       [:minimal]
;;       :examples        [{:label "Icon tag"
;;                          :args  [[icon :favorite]]}
;;                         {:label "Icon tag"
;;                          :args  [[icon :star]]}
;;                         {:label "Icon tag"
;;                          :args  [[icon :pets]]}
;;                         {:label "Leading icon"
;;                          :args  [[icon :pets] "Pets"]}]}

;;      #_{:desc            "Weight"
;;       :sx-attrs        (sx-call (sx :fs--$size-small))
;;       :container-attrs playground-tag-rows-container4
;;       :variants+       [:minimal]
;;       :examples        (for [s (rest component-examples/type-weights)]
;;                          {:label (name s)
;;                           :args  ["Pets" [icon :pets]]
;;                           :attrs {:class [s]}})}

;;      #_{:desc            "Max width"
;;       :reqs            '[[kushi.ui.icon :refer [icon]]]
;;       :sx-attrs        (sx-call (sx :fs--$size-small))
;;       :container-attrs (sx :gtc--max-content)
;;       :variants+       [:minimal]
;;       :examples        [{:label "Max width"
;;                          :args  [[:span {:class "truncate"
;;                                          :style {:max-width :130px}}
;;                                    "My tag with longer text"]]}]}]))


