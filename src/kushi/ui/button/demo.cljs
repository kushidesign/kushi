(ns ^{:kushi/layer "user-styles"} kushi.ui.button.demo
  (:require
   [kushi.css.core :refer [sx css css-vars css-vars-map merge-attrs]]
   [kushi.playground.component-examples :as component-examples :refer [section-label]]
   [kushi.playground.util :refer-macros [sx-call]]
   [kushi.ui.spinner.core :refer (spinner donut propeller thinking)]
   [kushi.ui.icon.core :refer [icon]]
   [kushi.ui.tooltip.core :refer (tooltip-attrs)]
   [kushi.ui.button.core :refer (button)]))


;; Nix
(defn info-sections [style-class]
  (into [:div.flex-row-fs]
        (for [color-class [:neutral :accent :positive :negative :warning]]
          [:p.info
           (merge-attrs 
            (sx :p--1em :m--1em)
            {:class [color-class style-class]})
           "info section"])))

;; Nix
(defn- button-grid [shape minimal?]
  (let [sem    [:neutral :positive :negative :accent :warning]
        kinds  (if minimal?
                 [:minimal :bordered :simple :filled]
                 [:bordered :simple :filled])]
    [:div (sx :.flex-row-c
              :>div:flex-grow--1
              :>div:flex-shrink--0
              :_button:mb--0.5em)
     (into [:div (sx :.flex-col-fs
                     :max-width--1100px)]
           (for [kind kinds]
             (let [kind-class (when-not (= :simple kind) kind)]
               (into [:div (sx :.flex-row-fs :gap--1em)]
                     (for [semantic sem]
                       [button
                        (merge-attrs
                         (sx [:--tooltip-offset :5px])
                         {:class [semantic kind-class :medium shape]}

                         ;; TODO - use this for above (make sure to register classes)
                         #_(sx [:--tooltip-offset :5px]
                               semantic
                               kind-class
                               :.medium
                               shape)

                         (tooltip-attrs
                          {:-placement [:block-start :inline-start]
                           :-text      (map #(str ":." (name %))
                                            (remove nil?
                                                    [kind-class
                                                     semantic
                                                     (when (not= shape :sharp)
                                                       shape)]))}))
                        "Play"])))))]))


(defn demo []
  [:div
   [:p.pointer-only "Hover (non-touch devices) to reveal the Kushi utility classes used for styling."]
   [:div
    (sx :>div:pb--1em)
    [button-grid :sharp true]]])



;; New April 2024 ----------------------------------------------------------------------------------

(def sizes
  [:xxsmall
   :xsmall
   :small
   :medium
   :large
   :xlarge])

(def examples
  (let [container-attrs
        (sx :.playground-button-rows-container
            :gtc--max-content:max-content
            :md:gtc--max-content)

        container-attrs2
        (merge-attrs container-attrs
                     {:class (css [:xsm:gtc "repeat(4, max-content)"]
                                  :md:gtc--max-content)})]
    [
     {:desc            "Surface variants"
      :sx-attrs        (sx-call (sx :.small))
      :container-attrs (merge-attrs
                        container-attrs2
                        {:class (css [:md:gtc "repeat(4, max-content)"]
                                     :color--red!important)})
      :variants+       [:minimal]
      :snippets-header ["Use the utility classes `:.filled`, `:.bordered`, and `:.minimal` to control the surface variant of the button."]
      :snippets        '[[button "Play"]
                         [button (sx :.filled) "Play"]
                         [button (sx :.bordered) "Play"]
                         [button (sx :.minimal) "Play"]]
      :examples        (for [s [:rounded]]
                         {:label (name s)
                          :args  ["Pets" [icon :pets]]
                          :attrs {:class [:accent]}})}

     {:desc            "Semantic variants"
      :sx-attrs        (sx-call (sx :.small))
      :container-attrs container-attrs2
      :variants+       [:minimal]
      :snippets-header ["Use the utility classes `:.neutral`, `:.accent`, `:.positive`, `:.warning`, and `:.negative` to control the semantic color variant."
                        :br
                        :br
                        "The default is `:.neutral`."]                        
      :snippets        '[[button "Play"]
                         [button (sx :.neutral) "Play"]
                         [button (sx :.accent) "Play"]
                         [button (sx :.positive) "Play"]
                         [button (sx :.warning) "Play"]
                         [button (sx :.negative) "Play"]]
      :examples        (for [s component-examples/colors]
                         {:label (name s)
                          :args  ["Pets" [icon :pets]]
                          :attrs {:class [s]}})}

     {:desc            "Shape variants"
      :sx-attrs        (sx-call (sx :.small))
      :container-attrs container-attrs2
      :variants+       [:minimal]
      :snippets-header ["Use the utility classes `:.pill`, `:.rounded`, and `:.sharp` to control the surface variant of the button."
                        :br
                        :br
                        "The default is `:.rounded`."]
      :snippets        '[[button "Play"]
                         [button (sx :.rounded) "Play"]
                         [button (sx :.pill) "Play"]
                         [button (sx :.sharp) "Play"]]
      :examples        (for [s [:rounded :pill :sharp]]
                         {:label (name s)
                          :args  ["Pets" [icon :pets]]
                          :attrs {:class [s]}})}

     ;; get extra reqs working
     ;; get links working

     {:desc            "With icons"
      :reqs            '[[kushi.ui.icon.core :refer [icon]]
                         [kushi.ui.label.core :refer [label]]]
      :sx-attrs        (sx-call (sx :.small))
      :container-attrs container-attrs2
      :snippets-header ["Check out the [icon component](#icon) for detailed info on icon usage"
                        :br
                        :br
                        "Various buttons with icons:"]
      :snippets        '[[button [icon :play-arrow]]
                         [button "Play" [icon :play-arrow]]
                         [button [icon :auto-awesome] "Wow" [icon :auto-awesome]]]
      :examples        [{:label "Icon button"
                         :args  [[icon :favorite]]}
                        {:label "Icon button"
                         :args  [[icon :star]]}
                        {:label "Icon button"
                         :args  [[icon :play-arrow]]}
                        {:label "Leading icon"
                         :args  [[icon :play-arrow] "Play"]}
                        {:label "Trailing icon"
                         :args  ["Play" [icon :play-arrow]]}
                        {:label "2 icons"
                         :args  [[icon :auto-awesome] "Wow" [icon :auto-awesome]]}]}


     (merge
      (component-examples/sizes-snippet-scale 'button "Play")
      {:desc            "Sizes from xxsmall to xlarge"
       :row-attrs       (sx :md:ai--fe)
       :container-attrs container-attrs
       :examples        (for [sz component-examples/sizes]
                          {:label (name sz)
                           :attrs {:class sz}
                           :args  ["Pets" [icon :pets]]})})


     {:desc            "Weight variants from light to extra-bold"
      :sx-attrs        (sx-call (sx :.small))
      :container-attrs container-attrs2
      :snippets-header ["Use the font-weight utility classes `:.thin` ~ `:.heavy` to control the weight."
                        :br
                        :br
                        "Scale of weights:"]
      :snippets        '[[:div
                          [button "Play"]
                          [button (sx :.thin) "Play"]
                          [button (sx :.extra-light) "Play"]
                          [button (sx :.light) "Play"]
                          [button (sx :.normal) "Play"]
                          [button (sx :.wee-bold) "Play"]
                          [button (sx :.semi-bold) "Play"]
                          [button (sx :.bold) "Play"]
                          [button (sx :.extra-bold) "Play"]
                          [button (sx :.heavy) "Play"]]]
      :examples        (for [s (rest component-examples/type-weights)]
                         {:label (name s)
                          :args  ["Wow" [icon :auto-awesome]]
                          :attrs {:class [s]}})}


     {:desc            "Loading and disabled states"
      :variants-       [:bordered :filled]
      :reqs            '[[kushi.ui.button.core :refer [button]]
                         [kushi.ui.icon.core :refer [icon]]
                         [kushi.ui.spinner.core :refer [spinner donut propeller thinking]]]
      ;; :sx-attrs        (sx-call (sx :.small {:-loading? true}))
      :sx-attrs        (sx-call {:-loading? true
                                 :class (css :.small)})
      :container-attrs container-attrs
      :snippets-header ["Examples:"]
      :snippets        '[[button {:-loading? true} [spinner [icon :play-arrow] [propeller]] "Play"]
                         [button {:disabled true} "Play"]]
      :examples        [{:label "Loading state, propeller"
                         :args  [[spinner [icon :play-arrow] [propeller]] "Play"]}
                        {:label "Loading state, dots"
                         :args  [[spinner "Play" [thinking]]]}
                        {:label "Loading state, donut"
                         :args  [[spinner "Play" [donut]]]}
                        {:label "Loading state, donut, fast"
                         :args  [[spinner "Play" [donut (sx :animation-duration--325ms)]]]}
                        {:label "Loading state, donut on icon"
                         :args  [[spinner [icon :play-arrow] [donut]] "Play"]}
                        {:label "Loading state, propeller on icon"
                         :attrs {:disabled true}
                         :args  [[spinner [icon :play-arrow] [donut]] "Play"]}
                        {:label "Disabled"
                         :attrs {:disabled true}
                         :args  ["Play"]}]}]))


#_(defn section-label
  "Renders a vertical label"
  [s]
  [:p (sx :.xxsmall
          :c--$neutral-secondary-foreground
          :min-width--55px
          {:style {:writing-mode :vertical-lr
                   :text-orientation :upright
                   :text-transform :uppercase
                   :font-weight :800
                   :color :#7d7d7d
                   :font-family "JetBrains Mono"
                   :text-align :center
                   :background-image "linear-gradient(90deg, #e3e3e3, #e3e3d3 1px, transparent 1px)"
                   :background-position-x :1ch}})
   [:span (sx :bgc--white :pi--0.5em) s]])

