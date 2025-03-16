(ns ^{:kushi/layer "user-styles"} kushi.ui.button.demo
  (:require
   [kushi.core :refer [sx css merge-attrs]]
   [kushi.playground.component-examples :as component-examples]
   [kushi.playground.util :refer-macros [sx-call]]
   [kushi.ui.spinner.core :refer (spinner donut propeller thinking)]
   [kushi.ui.icon.core :refer [icon]]
   [kushi.ui.tooltip.core :refer (tooltip-attrs)]
   [kushi.ui.button.core :refer (button)]))



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
      :snippets-header "Use the `data-kui-surface` attributes `:solid`, `:outline`,
                        and `:minimal` to control the surface variant of the button."
      :snippets        '[[button "Button"]
                         [button {:-surface :solid} "Button"]
                         [button {:-surface :outline} "Button"]
                         [button {:-surface :minimal} "Button"]]
      :examples        (for [s [:rounded]]
                         {:label (name s)
                          :args  ["Button" #_[icon :play-arrow]]
                          :attrs {:-colorway :accent}})}

     {:desc            "Colorway variants"
      :sx-attrs        (sx-call (sx :.small))
      :container-attrs container-attrs2
      :variants+       [:minimal]
      :snippets-header "Use the `data-kui-colorway` attributes `:neutral`, `:accent`,
                        `:positive`, `:warning`, and `:negative` to control the
                        semantic color variant. The default is `:neutral`."                        
      :snippets        '[[button "Button"]
                         [button {:-colorway :neutral} "Button"]
                         [button {:-colorway :accent} "Button"]
                         [button {:-colorway :positive} "Button"]
                         [button {:-colorway :warning} "Button"]
                         [button {:-colorway :negative} "Button"]]
      :examples        (for [s component-examples/colors]
                         {:label (name s)
                          :args  ["Button" #_[icon :play-arrow]]
                          :attrs {:-colorway s}})}

     {:desc            "Shape variants"
      :sx-attrs        (sx-call (sx :.small))
      :container-attrs container-attrs2
      :variants+       [:minimal]
      :snippets-header "Use the `data-kui-shape` attributes `:pill`, `:rounded`,
                        and `:sharp` to control the surface variant of the button.
                        The default is `:rounded`."
      :snippets        '[[button "Button"]
                         [button {:-shape :rounded} "Button"]
                         [button {:-shape :pill} "Button"]
                         [button {:-shape :sharp} "Button"]]
      :examples        (for [s [:rounded :pill :sharp]]
                         {:label (name s)
                          :args  ["Button"] #_["Play" [icon :play-arrow]]
                          :attrs {:-shape s}})}

     ;; get extra reqs working
     ;; get links working

     {:desc            "With icons"
      :reqs            '[[kushi.ui.icon.core :refer [icon icon-button]]
                         [kushi.ui.label.core :refer [label]]]
      :sx-attrs        (sx-call (sx :.small))
      :container-attrs container-attrs2
      :snippets-header "Check out the [icon component](#icon) for detailed info
                        on icon usage Various buttons with icons:"
      :snippets        '[[button "Button" [icon :play-arrow]]
                         [button [icon :auto-awesome] "Wow" [icon :auto-awesome]]
                         #_[icon-button {:-icon :play-arrow}]]
      :examples        [
                        ;; {:label "Icon button"
                        ;;  :args  [[icon :favorite]]
                        ;;  :attrs {:icon true}}
                        ;; {:label "Icon button"
                        ;;  :args  [[icon :star]]}
                        ;; {:label "Icon button"
                        ;;  :args  [[icon :play-arrow]]}

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
       :examples        (for [sz (drop-last component-examples/sizes)]
                          {:label (name sz)
                           :attrs {:class sz}
                           :args  ["Button" #_[icon :play-arrow]]})})


     {:desc            "Weight variants from light to extra-bold"
      :sx-attrs        (sx-call (sx :.small))
      :container-attrs container-attrs2
      :snippets-header "Use the font-weight utility classes `:.thin` ~ `:.heavy`
                        to control the weight. Scale of weights:"
      :snippets        '[[:div
                          [button "Wow"]
                          [button (sx :.thin) "Wow"]
                          [button (sx :.extra-light) "Wow"]
                          [button (sx :.light) "Wow"]
                          [button (sx :.normal) "Wow"]
                          [button (sx :.wee-bold) "Wow"]
                          [button (sx :.semi-bold) "Wow"]
                          [button (sx :.bold) "Wow"]
                          [button (sx :.extra-bold) "Wow"]
                          [button (sx :.heavy) "Wow"]]]
      :examples        (for [s (drop-last (rest component-examples/type-weights))]
                         {:label (name s)
                          :args  ["Wow" [icon :auto-awesome]]
                          :attrs {:class [s]}})}


     {:desc            "Loading and disabled states"
      :variants-       [:bordered :solid]
      :reqs            '[[kushi.ui.button.core :refer [button]]
                         [kushi.ui.icon.core :refer [icon]]
                         [kushi.ui.spinner.core :refer [spinner donut propeller thinking]]]
      ;; :sx-attrs        (sx-call (sx :.small {:-loading? true}))
      :sx-attrs        (sx-call {:-loading? true
                                 :class (css :.small)})
      :container-attrs container-attrs
      :snippets-header "Examples:"
      :snippets        '[[button {:-loading? true}
                          [spinner [icon :play-arrow] [propeller]] "Play"]
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
                        #_{:label "Loading state, propeller on icon"
                         :attrs {:disabled true}
                         :args  [[spinner [icon :play-arrow] [donut]] "Play"]}
                        {:label "Disabled"
                         :attrs {:disabled true}
                         :args  ["Play"]}]}]))

