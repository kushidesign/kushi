(ns kushi.ui.input.switch.demo
  (:require
   [kushi.core :refer (sx)]
   [kushi.ui.icon.core :refer [icon]]
   [kushi.ui.input.switch.core :refer [switch]]
   [kushi.playground.util :refer-macros [sx-call]]
   [kushi.playground.component-examples :as component-examples :refer [section-label]]))

(defn switch-demo [{dark? :-dark? :or {dark? false}}]
  (into [:div
         (sx :.flex-col-fs
             :gap--0.5em
             :padding--1rem
             :.xxxlarge
             {:class [(when dark? :dark)]})]
        (for [semantic [:foo
                        :neutral :accent
                        :positive :warning :negative]]
          [:div (sx :.flex-row-fs :gap--0.5em)
           [switch (sx {:-disabled? false
                        :class      [semantic]})]
           [switch (sx {:-disabled? false
                        :-on?       true
                        :class      [semantic]})]])))

(defn switch-demo-light+dark []
  [:div.flex-row-fs
   [switch-demo]
   [switch-demo {:-dark? true}]])



;; NEW April -----------------------------------------------------
(declare switch-examples)


(defn demo2 [component-opts]
  (into [:<>]
        (for [
              ;; example-opts (take 1 switch-examples)
              example-opts switch-examples
              ;; example-opts (keep-indexed (fn [idx m] (when (contains? #{9} idx) m)) switch-examples)
              ]
          [component-examples/examples-section component-opts example-opts])))


;; TODO remove section-label-2
;; TODO hoist reqs up to a higher level

(def switch-sizes
  [:small
   :medium
   :large
   :xlarge
   :xxlarge
   :xxxlarge])

(def switch-examples
  [
   {:desc      "Sizes from `xxsmall` to `xlarge`"      
    :sx-attrs  (sx-call (sx :.xxlarge))
    :examples  (for [s (take 4 component-examples/colors)]
                 {:label (name s)
                  :attrs {:class [s]}})}

   {:desc        "`semantic` variants, outline styling"
    :sx-attrs     (sx-call (sx
                            :.xxlarge
                            :$switch-border-color--$gray-400
                            :$switch-border-width--1.5px
                            :$switch-off-background-color--white
                            ["bgc" :transparent!important]
                            ["hover:bgc" :transparent!important]
                            ["[aria-checked='true']:bc" :currentColor]
                            ["[aria-checked='true']:hover:bc" :currentColor]
                            {:-thumb-attrs (sx
                                            :.elevated-0!
                                            [:border
                                             "calc(var(--switch-border-width) * (1 / var(--switch-thumb-scale-factor))) solid transparent"]
                                            [:bgc
                                             :$switch-border-color]
                                            ["has-ancestor(.kushi-switch[aria-checked='true']):bgc"
                                             :currentColor])}))
    :examples     (for [s component-examples/colors]
                    {:label (name s)
                     :attrs {:class [s]}})}

   {:desc     "Showing sizes from `small` to `xxxlarge`"
    :row-attrs (sx :ai--fe)
    :examples  (for [sz switch-sizes]
                 {:label (name sz)
                  :attrs {:class sz}})}

   {:desc         "With convex-styled thumb control"
    :row-attrs    (sx :ai--fe)
    :sx-attrs     (sx-call (sx {:-thumb-attrs (sx :.convex)}))
    :examples     (for [sz switch-sizes]
                    {:label (name sz)
                     :attrs {:class [sz]}})}

   {:desc         "With oversized thumb control"
    :row-attrs    (sx :ai--fe)
    :sx-attrs     (sx-call (sx
                            :$switch-border-width--0px
                            :$switch-thumb-scale-factor--1.25
                            {:-thumb-attrs (sx :outline--1px:solid:currentColor :outline-offset---1px)}))
    :examples     (for [sz switch-sizes]
                    {:label (name sz)
                     :attrs {:class [sz]}})}

   {:desc         "With labeled track"
    :row-attrs    (sx :ai--fe)
    :sx-attrs     (sx-call (sx
                            :$switch-width-ratio--2.25
                            {:-track-content-on  "ON"
                             :-track-content-off "OFF"}))
    :examples     (for [sz switch-sizes]
                    {:label (name sz)
                     :attrs {:class [sz]}})}

   {:desc        "With labeled thumb"
    :row-attrs    (sx :ai--fe)
    :sx-attrs     (sx-call (sx {:-thumb-content-on  [:span (sx :.semi-bold :fs--0.325em) "ON"]
                                :-thumb-content-off [:span (sx :.semi-bold :fs--0.325em) "OFF"]}))
    :examples     (for [sz switch-sizes]
                    {:label (name sz)
                     :attrs {:class [sz]}})}
   
   {:desc        "With icon track"
    :reqs         '[[kushi.ui.icon.core :refer [icon]]]
    :row-attrs    (sx :ai--fe)
    :sx-attrs     (sx-call (sx {:-track-content-on  [icon (sx :fs--0.55em
                                                              {:-icon-filled? true})
                                                     :visibility]
                                :-track-content-off [icon (sx :fs--0.55em
                                                              {:-icon-filled? true})
                                                     :visibility-off]}))
    :examples     (for [sz switch-sizes]
                    {:label (name sz)
                     :attrs {:class [sz]}})}

   {:desc         "With icon thumb"
    :reqs         '[[kushi.ui.icon.core :refer [icon]]]
    :row-attrs    (sx :ai--fe)
    :sx-attrs     (sx-call (sx {:-thumb-content-on  [icon (sx :fs--0.55em
                                                              {:-icon-filled? true})
                                                     :visibility]
                                :-thumb-content-off [icon (sx :fs--0.55em
                                                              {:-icon-filled? true})
                                                     :visibility-off]}))
    :examples     (for [sz switch-sizes]
                    {:label (name sz)
                     :attrs {:class [sz]}})}

   {:desc         "Disabled states"
    :row-attrs    (sx :ai--fe)
    :examples     (for [sz switch-sizes]
                    {:label (name sz)
                     :attrs {:disabled true
                             :class    [sz]}})}


  ;;  {:title       "shape variants"
  ;;   :label       [section-label-2 [:span "" [:span.code "shape"] " variants"]]
  ;;   :component   button
  ;;   :sx-attrs (sx :.small)
  ;;   :variants+   [:minimal]
  ;;   :examples    (for [s [:rounded :pill :sharp]]
  ;;                  {:label (name s)
  ;;                   :args  ["Play"]
  ;;                   :attrs {:class [s]}})}
   
  ;;  {:title       "With icons"
  ;;   :label       [section-label-2 "With icons"]
  ;;   :component   button
  ;;   :reqs        '[[kushi.ui.icon.core :refer [icon]]]
  ;;   :sx-attrs (sx :.small)
  ;;   :examples    [{:label "Icon button"
  ;;                  :args  [[icon :favorite]]}
  ;;                 {:label "Icon button"
  ;;                  :args  [[icon :star]]}
  ;;                 {:label "Icon button"
  ;;                  :args  [[icon :play-arrow]]}
  ;;                 {:label "Leading icon"
  ;;                  :args  [[icon :play-arrow] "Play"]}
  ;;                 {:label "Trailing icon"
  ;;                  :args  [[icon :auto-awesome]]}
  ;;                 {:label "2 icons"
  ;;                  :args  [[icon :auto-awesome] "Play" [icon :auto-awesome]]}]}
   
  ;;  {:title       "weight variants"
  ;;   :label       [section-label-2 [:span "" [:span.code "weight"] " variants"]]
  ;;   :component   button
  ;;   :sx-attrs (sx :.small)
  ;;   :examples    (for [s (rest component-examples/type-weights)]
  ;;                  {:label (name s)
  ;;                   :args  ["Play" [icon :auto-awesome]]
  ;;                   :attrs {:class [s]}})}
   
  ;;  {:title       "Loading and disabled states"
  ;;   :label       [section-label-2 "Loading and disabled states"]
  ;;   :component   button
  ;;   :reqs        '[[kushi.ui.button.core :refer [button]]
  ;;                  [kushi.ui.icon.core :refer [icon]]
  ;;                  [kushi.ui.progress.core :refer [progress spinner propeller thinking]]]
  ;;   :sx-attrs (sx :.small {:-loading? true})
  ;;   :examples    [{:label "Loading state, propeller"
  ;;                  :args  [[progress "Play" [propeller]]]}
  ;;                 {:label "Loading state, dots"
  ;;                  :args  [[progress "Play" [thinking]]]}
  ;;                 {:label "Loading state, spinner"
  ;;                  :args  [[progress "Play" [spinner]]]}
  ;;                 {:label "Loading state, spinner, fast"
  ;;                  :args  [[progress "Play" [spinner (sx :animation-duration--325ms)]]]}
  ;;                 {:label "Loading state, spinner on icon"
  ;;                  :args  [[progress [icon :play-arrow] [spinner]] "Play"]}
  ;;                 {:label "Loading state, propeller on icon"
  ;;                  :args  [[progress [icon :play-arrow] [spinner]] "Play"]}
  ;;                 {:label "Loading state, propeller on icon"
  ;;                  :attrs {:disabled true}
  ;;                  :args  [[progress [icon :play-arrow] [spinner]] "Play"]}
  ;;                 {:label "Loading state, propeller on icon"
  ;;                  :attrs {:disabled true}
  ;;                  :args  ["Play"]}]}
   ])


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

