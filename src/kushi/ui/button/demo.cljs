(ns kushi.ui.button.demo
  (:require
   [kushi.core :refer (sx merge-attrs)]
   [kushi.playground.component-examples :as component-examples :refer [section-label]]
   [kushi.ui.progress.core :refer (progress spinner propeller thinking)]
   [kushi.ui.icon.core :refer [icon]]
   [kushi.ui.tooltip.core :refer (tooltip-attrs)]
   [kushi.ui.button.core :refer (button)]
   ))


(defn info-sections [style-class]
  (into [:div.flex-row-fs]
        (for [color-class [:neutral :accent :positive :negative :warning]]
          [:p.info
           (sx :p--1em
               :m--1em
               {:class [color-class style-class]})
           "info section"])))

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


(declare button-examples)


(defn demo2 [component-opts]
  (into [:<>]
        (for [
              ;; example-opts (take 1 button-examples)
              example-opts button-examples
              ;; example-opts (keep-indexed (fn [idx m] (when (contains? #{3} idx) m)) button-examples)
              ]
          [component-examples/examples-section component-opts example-opts])))


;; TODO remove section-label
;; TODO hoist reqs up to a higher level
(def button-sizes
  [:xxsmall
   :xsmall
   :small
   :medium
   :large
   :xlarge])

(def button-examples
  [
   {:desc      "Sizes from `xxsmall` to `xlarge`"
    :row-attrs  (sx :ai--fe)
    :examples   (for [sz component-examples/sizes]
                  {:label (name sz)
                   :attrs {:class sz}
                   :args  ["Play"]})}
   
   {:desc       "semantic variants"
    :sx-attrs (sx :.small)
    :variants+   [:minimal]
    :examples    (let [semantics #{"neutral" "accent" "positive" "warning" "negative"}]
                   (for [s component-examples/colors]
                     {:label (name s)
                      :args  ["Play"]
                      :attrs (merge-attrs  {:class [s]}
                                           (when-not  (contains? semantics s)
                                             (sx [:color (str "var(--" s "-600)")]
                                                 [:background-color (str "var(--" s "-100)")])))}))}

   {:desc       "shape variants"
    :sx-attrs (sx :.small)
    :variants+   [:minimal]
    :examples    (for [s [:rounded :pill :sharp]]
                   {:label (name s)
                    :args  ["Play"]
                    :attrs {:class [s]}})}

   {:desc       "With `icons`"
    :reqs        '[[kushi.ui.icon.core :refer [icon]]]
    :sx-attrs (sx :.small)
    :examples    [{:label "Icon button"
                   :args  [[icon :favorite]]}
                  {:label "Icon button"
                   :args  [[icon :star]]}
                  {:label "Icon button"
                   :args  [[icon :play-arrow]]}
                  {:label "Leading icon"
                   :args  [[icon :play-arrow] "Play"]}
                  {:label "Trailing icon"
                   :args  [[icon :auto-awesome]]}
                  {:label "2 icons"
                   :args  [[icon :auto-awesome] "Play" [icon :auto-awesome]]}]}

   {:desc       "weight variants"
    :sx-attrs    (sx :.small)
    :examples    (for [s (rest component-examples/type-weights)]
                   {:label (name s)
                    :args  ["Play" [icon :auto-awesome]]
                    :attrs {:class [s]}})}

   {:desc       "Loading and disabled states"
    :label       [section-label "Loading and disabled states"]
    :reqs        '[[kushi.ui.button.core :refer [button]]
                   [kushi.ui.icon.core :refer [icon]]
                   [kushi.ui.progress.core :refer [progress spinner propeller thinking]]]
    :sx-attrs (sx :.small {:-loading? true})
    :examples    [{:label "Loading state, propeller"
                   :args  [[progress "Play" [propeller]]]}
                  {:label "Loading state, dots"
                   :args  [[progress "Play" [thinking]]]}
                  {:label "Loading state, spinner"
                   :args  [[progress "Play" [spinner]]]}
                  {:label "Loading state, spinner, fast"
                   :args  [[progress "Play" [spinner (sx :animation-duration--325ms)]]]}
                  {:label "Loading state, spinner on icon"
                   :args  [[progress [icon :play-arrow] [spinner]] "Play"]}
                  {:label "Loading state, propeller on icon"
                   :args  [[progress [icon :play-arrow] [spinner]] "Play"]}
                  {:label "Loading state, propeller on icon"
                   :attrs {:disabled true}
                   :args  [[progress [icon :play-arrow] [spinner]] "Play"]}
                  {:label "Loading state, propeller on icon"
                   :attrs {:disabled true}
                   :args  ["Play"]}]}])


#_(defn section-label
  "Renders a vertical label"
  [s]
  [:p (sx :.xxsmall
          :c--$neutral-secondary-fg
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

