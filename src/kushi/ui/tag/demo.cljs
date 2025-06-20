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
               ;; :attrs/display  {:sizing :small},
               :label          "Colorways ... surfaces × contours",
               :rows?          true})}

   {:label   "Contour variants"
    :desc    "Contour of the tag."
    :samples (samples-with-variant
              {:variant       :contour
               :variant-scale :contour/basic
               :attrs         {:colorway     :accent
                               :surface      :solid}
               :args          ["New"]})}
   
   {:desc    "Surface variant of the tag.",
    :label   "Surface variants",
    :samples (samples-with-variant
              {:variant       :surface,
               :variant-scale :surface/tag,
               :attrs         {:colorway     :accent},
               :args          ["New"]})}
   
   {:desc    "General amount of padding inside the tag",
    :label   "Packing variants",
    :samples (samples-with-variant
              {:attrs   {:colorway     :accent
                         :surface      :solid},
               :args    ["New"],
               :variant :packing})}

   {:label   "Sizing"
    ;;  :label/modal "Colorways ..."
    :desc    "Sizes from xxxsmall to xxxlarge"
    ;; :row-style {:border "1px solid red"}
    :samples (samples-with-variant
              {:variant :sizing
              ;; :variant-labels? false
               :attrs   {:surface      :solid
                         :colorway     :accent}
               :args    ["New"]})}

   {:schema  #{:inside :outside},
    :desc    "Alignment of the stroke. Only applies to `:surface` `:outline`",
    :label   "Stroke alignment",
    :require '[[kushi.ui.icon :refer [icon]]],
    :samples (samples [[tag
                        {:sizing       :xlarge,
                         :colorway     :accent,
                         :surface      :outline,
                         :stroke-align :inside
                         :stroke-width :2px}
                        "New"]
                       [tag
                        {:sizing       :xlarge,
                         :colorway     :accent,
                         :surface      :outline,
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
                         :colorway       :accent,
                         :surface        :soft}
                        "Wow"]
                       [tag
                        {:start-enhancer :play-arrow,
                         :colorway       :accent,
                         :surface        :outline}
                        "Play"]])}
   
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
                        {:end-enhancer :play-arrow,
                         :colorway     :accent,
                         :surface      :outline}
                        "Play"]])}

   {:label   "Loading states",
    :desc    "When `true` this will set the appropriate values for `aria-busy` and `aria-label`",
    :schema  boolean?,
    :require '[[kushi.ui.tag :refer [tag]]
               [kushi.ui.icon :refer [icon]]
               [kushi.ui.spinner :refer [spinner]]],
    :samples (samples
              [[tag
                {:loading?     true,
                 :colorway     :accent,
                 :surface      :solid,
                 :end-enhancer [spinner {:spinner-type :donut}]
                 }
                "Play"]
               [tag
                {:loading?     true,
                 :colorway     :accent,
                 :surface      :solid,
                 :end-enhancer [spinner {:spinner-type :propeller}]}
                "Play"]
               [tag
                {:loading?     true,
                 :colorway     :accent,
                 :surface      :solid,
                 :end-enhancer [spinner {:spinner-type :thinking}]}
                "Play"]
               [tag
                {:loading? true
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
;;       :sx-attrs        (sx-call (sx :fs--$small))
;;       :container-attrs playground-tag-rows-container4
;;       :variants+       [:minimal]
;;       :examples        (for [colorway component-examples/colors]
;;                          {:label (name colorway)
;;                           :args  ["Done"]
;;                           :attrs {:colorway colorway}})}

;;      #_{:desc            "Shape variants"
;;       :sx-attrs        (sx-call (sx :fs--$small))
;;       :container-attrs playground-tag-rows-container4
;;       :variants+       [:minimal]
;;       :examples        (for [s [:rounded :pill :sharp]]
;;                          {:label (name s)
;;                           :args  ["Done"]
;;                           :attrs {:contour s}})}

;;      #_{:desc            "With icons"
;;       :reqs            '[[kushi.ui.icon :refer [icon]]]
;;       :sx-attrs        (sx-call (sx :fs--$small))
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

;;      #_{:desc            "Weight variants"
;;       :sx-attrs        (sx-call (sx :fs--$small))
;;       :container-attrs playground-tag-rows-container4
;;       :variants+       [:minimal]
;;       :examples        (for [s (rest component-examples/type-weights)]
;;                          {:label (name s)
;;                           :args  ["Pets" [icon :pets]]
;;                           :attrs {:class [s]}})}

;;      #_{:desc            "Max width"
;;       :reqs            '[[kushi.ui.icon :refer [icon]]]
;;       :sx-attrs        (sx-call (sx :fs--$small))
;;       :container-attrs (sx :gtc--max-content)
;;       :variants+       [:minimal]
;;       :examples        [{:label "Max width"
;;                          :args  [[:span {:class "truncate"
;;                                          :style {:max-width :130px}}
;;                                    "My tag with longer text"]]}]}]))


