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
      :snippets-header "Use the `data-kushi-surface` attributes `:solid`, `:outline`,
                        and `:minimal` to control the surface variant of the button."
      :snippets        '[[button "Play"]
                         [button {:surface :solid} "Play"]
                         [button {:surface :outline} "Play"]
                         [button {:surface :minimal} "Play"]]
      :examples        (for [s [:rounded]]
                         {:label (name s)
                          :args  ["Pets" [icon :pets]]
                          :attrs {:colorway :accent}})}

     {:desc            "Colorway variants"
      :sx-attrs        (sx-call (sx :.small))
      :container-attrs container-attrs2
      :variants+       [:minimal]
      :snippets-header "Use the `data-kushi-colorway` attributes `:neutral`, `:accent`,
                        `:positive`, `:warning`, and `:negative` to control the
                        semantic color variant. The default is `:neutral`."                        
      :snippets        '[[button "Play"]
                         [button {:colorway :neutral} "Play"]
                         [button {:colorway :accent} "Play"]
                         [button {:colorway :positive} "Play"]
                         [button {:colorway :warning} "Play"]
                         [button {:colorway :negative} "Play"]]
      :examples        (for [s component-examples/colors]
                         {:label (name s)
                          :args  ["Pets" [icon :pets]]
                          :attrs {:colorway s}})}

     {:desc            "Shape variants"
      :sx-attrs        (sx-call (sx :.small))
      :container-attrs container-attrs2
      :variants+       [:minimal]
      :snippets-header "Use the `data-kushi-contour` attributes `:pill`, `:rounded`,
                        and `:sharp` to control the surface variant of the button.
                        The default is `:rounded`."
      :snippets        '[[button "Play"]
                         [button {:contour :rounded} "Play"]
                         [button {:contour :pill} "Play"]
                         [button {:contour :sharp} "Play"]]
      :examples        (for [s [:rounded :pill :sharp]]
                         {:label (name s)
                          :args  ["Pets" [icon :pets]]
                          :attrs {:contour s}})}

     ;; get extra reqs working
     ;; get links working
     
     {:desc            "With icons"
      :reqs            '[[kushi.ui.icon.core :refer [icon icon-button]]
                         [kushi.ui.label.core :refer [label]]]
      :sx-attrs        (sx-call (sx :.small))
      :container-attrs container-attrs2
      :snippets-header "Check out the [icon component](#icon) for detailed info
                        on icon usage Various buttons with icons:"
      :snippets        '[[button "Play" [icon :play-arrow]]
                         [button [icon :auto-awesome] "Wow" [icon :auto-awesome]]
                         #_[icon-button {:icon :play-arrow}]]
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
       :examples        (for [sz component-examples/sizes]
                          {:label (name sz)
                           :attrs {:class sz}
                           :args  ["Pets" [icon :pets]]})})


     {:desc            "Weight variants from light to extra-bold"
      :sx-attrs        (sx-call (sx :.small))
      :container-attrs container-attrs2
      :snippets-header "Use the font-weight utility classes `:.thin` ~ `:.heavy`
                        to control the weight. Scale of weights:"
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
      :variants-       [:bordered :solid]
      :reqs            '[[kushi.ui.button.core :refer [button]]
                         [kushi.ui.icon.core :refer [icon]]
                         [kushi.ui.spinner.core :refer [spinner donut propeller thinking]]]
      ;; :sx-attrs        (sx-call (sx :.small {:loading? true}))
      :sx-attrs        (sx-call {:loading? true
                                 :class (css :.small)})
      :container-attrs container-attrs
      :snippets-header "Examples:"
      :snippets        '[[button {:loading? true} [spinner [icon :play-arrow] [propeller]] "Play"]
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

