(ns kushi.ui.button.demo
  (:require
   [kushi.core :refer (sx merge-attrs)]
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
           (sx :p--1em
               :m--1em
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
              :&_button:mb--0.5em)
     (into [:div (sx :.flex-col-fs
                     :max-width--1100px)]
           (for [kind kinds]
             (let [kind-class (when-not (= :simple kind) kind)]
               (into [:div (sx :.flex-row-fs :gap--1em)]
                     (for [semantic sem]
                       [button
                        (merge-attrs
                         (sx :$tooltip-offset--5px
                             {:class [semantic kind-class :medium shape]})
                         (tooltip-attrs
                          {:-placement [:block-start :inline-start]
                           :-text      (map #(str ":." (name %))
                                            (remove nil?
                                                    [kind-class
                                                     semantic
                                                     (when (not= shape :sharp)
                                                       shape)]))}))
                        "Hello"])))))]))


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
        (sx 'playground-button-rows-container
            :gtc--max-content:max-content
            :md:gtc--max-content)

        container-attrs2
        (merge-attrs container-attrs
                     (sx [:xsm:gtc '(repeat 4 :max-content)]
                         :md:gtc--max-content
                         ))]
    [
     {:desc            "Sizes from xxsmall to xlarge"
      :row-attrs       (sx :md:ai--fe)
      :container-attrs container-attrs
      :examples        (for [sz component-examples/sizes]
                         {:label (name sz)
                          :attrs {:class sz}
                          :args  ["Pets" [icon :pets]]})}
     
     {:desc            "Semantic variants"
      :sx-attrs        (sx-call (sx :.small))
      :container-attrs container-attrs2
      :variants+       [:minimal]
      :examples        (let [semantics #{"neutral" "accent" "positive" "warning" "negative"}]
                         (for [s component-examples/colors]
                           {:label (name s)
                            :args  ["Pets" [icon :pets]]
                            :attrs {:class [s]}}))}

     {:desc            "Shape variants"
      :sx-attrs        (sx-call (sx :.small))
      :container-attrs container-attrs2
      :variants+       [:minimal]
      :examples        (for [s [:rounded :pill :sharp]]
                         {:label (name s)
                          :args  ["Pets" [icon :pets]]
                          :attrs {:class [s]}})}

     {:desc            "With icons"
      :reqs            '[[kushi.ui.icon.core :refer [icon]]]
      :sx-attrs        (sx-call (sx :.small))
      :container-attrs container-attrs2
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

     {:desc            "Weight variants from light to extra-bold"
      :sx-attrs        (sx-call (sx :.small))
      :container-attrs container-attrs2
      :examples        (for [s (rest component-examples/type-weights)]
                         {:label (name s)
                          :args  ["Wow" [icon :auto-awesome]]
                          :attrs {:class [s]}})}

     {:desc            "Loading and disabled states"
      :variants-       [:bordered :filled]
      :reqs            '[[kushi.ui.button.core :refer [button]]
                         [kushi.ui.icon.core :refer [icon]]
                         [kushi.ui.spinner.core :refer [spinner donut propeller thinking]]]
      :sx-attrs        (sx-call (sx :.small {:-loading? true}))
      :container-attrs container-attrs
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

