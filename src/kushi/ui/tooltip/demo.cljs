(ns kushi.ui.tooltip.demo
  (:require 
   [kushi.core :refer (sx merge-attrs)]
   [kushi.playground.component-examples :as component-examples]
   [kushi.playground.util :refer-macros [sx-call]]
   [kushi.ui.button.core :refer [button]]
   [kushi.ui.tooltip.core :refer [tooltip-attrs]]))

(defn demo []
  (into
   [:div
    (sx
    :.grid
     :gtc--1fr:1fr:1fr:1fr:1fr
     :gtr--auto
     :gap--1rem
     :w--400px
     :h--400px
     [:gta '(kushi/grid-template-areas
             "brc br b  bl blc"
             "rt  .  .  .  lt"
             "r   .  .  .  l"
             "rb  .  .  .  lb"
             "trc tr t  tl tlc")])]

   (for [x     ["brc" "br" "b"  "bl" "blc"
                "rt"  nil  nil  nil  "lt"
                "r"   nil  nil  nil  "l"
                "rb"  nil  nil  nil  "lb"
                "trc" "tr" "t"  "tl" "tlc"]
         :when (not (nil? x))]

     [:button (merge-attrs
               (sx 'kushi-playground-tooltip-demo-button
                   :.flex-row-c
                   :.pointer
                   :.relative
                   :b--1px:solid:$neutral-600
                   :dark:b--1px:solid:$neutral-400
                   :hover:b--1px:solid:black
                   :dark:hover:b--1px:solid:white
                   :>span.placement-label:ff--$code-font-stack
                   :fs--0.9em
                   :c--$neutral-600
                   :dark:c--$neutral-400
                   :hover:c--black
                   :dark:hover:c--white
                   :&.kushi-pseudo-tooltip-revealed:bc--$accent-color
                   :dark:&.kushi-pseudo-tooltip-revealed:bc--$accent-color-inverse
                   :&.kushi-pseudo-tooltip-revealed:c--$accent-color
                   :dark:&.kushi-pseudo-tooltip-revealed:c--$accent-color-inverse
                   :&.kushi-pseudo-tooltip-revealed:bgc--$accent-background-color
                   :dark:&.kushi-pseudo-tooltip-revealed:bgc--$accent-background-color-inverse
                   [:grid-area x]
                   {:tab-index 0})
               (tooltip-attrs {:-text      ["Tooltip Line 1" "Tooltip Line 2" ]
                               ;; :-reveal-on-click?         true
                               :-placement (keyword x)}))
      [:span.placement-label (str ":" x)]])))



(declare tooltip-examples)


(defn demo2 [component-opts]
  (into [:<>]
        (for [
              ;; example-opts (take 1 switch-examples)
              example-opts tooltip-examples
              ;; example-opts (keep-indexed (fn [idx m] (when (contains? #{9} idx) m)) switch-examples)
              ]
          [component-examples/examples-section component-opts example-opts])))


;; Code for generating tooltip placement examples at repl
;; (?pp (into []
;;            (for [x     [:brc :br  :b   :bl  :blc
;;                         :rt  nil  nil  nil  :lt
;;                         :r   nil  nil  nil  :l
;;                         :rb  nil  nil  nil  :lb
;;                         :trc :tr  :t   :tl  :tlc]
;;                  :when (not (nil? x))]
;;              (let [hydrated (name (get placement-kws-hydrated x))]
;;                {:label    hydrated
;;                 :args     [(str x)]
;;                 :sx-attrs (list 'sx-call 
;;                                 (list
;;                                  'merge-attrs
;;                                  (list 'tooltip-attrs
;;                                        {:-text      [(str "`:" hydrated "`")
;;                                                      "Click to view code"]
;;                                         :-placement x})
;;                                  {:style  {:grid-area (name x)}})
;;                                 )}))))

(def tooltip-examples
  [{:desc      "Basic, auto-placement."
    :component button
    :reqs      '[[kushi.ui.button.core :refer [button]]]
    :row-attrs (sx :&_.kushi-button:fs--$small)
    :examples  [{:label    "right"
                 :args     ["Hover me"]
                 :sx-attrs (sx-call (tooltip-attrs {:-text "Click to view code"}))}]}

   {:desc     "Tooltips with specific placements"      
    :row-attrs (sx
                :$tooltip-delay-duration--0ms
                :.grid
                :gtc--1fr:1fr:1fr:1fr:1fr
                :gtr--auto
                :gap--1rem
                :w--333px
                :h--333px
                :&_span.kushi-tooltip-text:ta--c
                [:>span {:ta             :c
                         :ff             :$code-font-stack
                         :fs             :$xsmall
                         :fw             :$wee-bold
                         :cursor         :pointer
                         :bgc            :$neutral-100
                         :d              :flex
                         :border-radius  :$rounded
                         :border         :1px:dashed:$neutral-400
                         :hover:border   :1px:dashed:$neutral-600
                         :flex-direction :column
                         :jc             :c
                         :h              :100%}]
                [:>span:hover {:bgc    :$neutral-200
                               :border :1px:dashed:$neutral-600}]
                [:dark:>span {:bgc            :$neutral-800
                              :hover:bgc      :$neutral-750
                              :d              :flex
                              :border-radius  :$rounded
                              :border         :1px:dashed:$neutral-500
                              :hover:border   :1px:dashed:$neutral-400
                              :flex-direction :column
                              :jc             :c
                              :h              :100%}]
                [:dark:>span:hover {:border :1px:dashed:$neutral-400
                                    :bgc    :$neutral-700}]
                [:gta '(kushi/grid-template-areas
                        "brc br b  bl blc"
                        "rt  .  .  .  lt"
                        "r   .  .  .  l"
                        "rb  .  .  .  lb"
                        "trc tr t  tl tlc")])
    :examples [{:label    "bottom-right-corner",
                :args     [":brc"],
                :sx-attrs (sx-call
                           (merge-attrs
                            (tooltip-attrs
                             {:-text      ["`:bottom-right-corner`" "Click to view code"],
                              :-placement :brc})
                            {:style {:grid-area "brc"}}))}
               {:label    "bottom-right",
                :args     [":br"],
                :sx-attrs (sx-call
                           (merge-attrs
                            (tooltip-attrs
                             {:-text      ["`:bottom-right`" "Click to view code"]
                              :-placement :br})
                            {:style {:grid-area "br"}}))}
               {:label    "bottom",
                :args     [":b"],
                :sx-attrs (sx-call
                           (merge-attrs
                            (tooltip-attrs
                             {:-text      ["`:bottom`" "Click to view code"]
                              :-placement :b})
                            {:style {:grid-area "b"}}))}
               {:label    "bottom-left",
                :args     [":bl"],
                :sx-attrs (sx-call
                           (merge-attrs
                            (tooltip-attrs
                             {:-text      ["`:bottom-left`" "Click to view code"]
                              :-placement :bl})
                            {:style {:grid-area "bl"}}))}
               {:label    "bottom-left-corner",
                :args     [":blc"],
                :sx-attrs (sx-call
                           (merge-attrs
                            (tooltip-attrs
                             {:-text      ["`:bottom-left-corner`" "Click to view code"],
                              :-placement :blc})
                            {:style {:grid-area "blc"}}))}
               {:label    "right-top",
                :args     [":rt"],
                :sx-attrs (sx-call
                           (merge-attrs
                            (tooltip-attrs
                             {:-text      ["`:right-top`" "Click to view code"]
                              :-placement :rt})
                            {:style {:grid-area "rt"}}))}
               {:label    "left-top",
                :args     [":lt"],
                :sx-attrs (sx-call
                           (merge-attrs
                            (tooltip-attrs
                             {:-text      ["`:left-top`" "Click to view code"]
                              :-placement :lt})
                            {:style {:grid-area "lt"}}))}
               {:label    "right",
                :args     [":r"],
                :sx-attrs (sx-call
                           (merge-attrs
                            (tooltip-attrs
                             {:-text      ["`:right`" "Click to view code"]
                              :-placement :r})
                            {:style {:grid-area "r"}}))}
               {:label    "left",
                :args     [":l"],
                :sx-attrs (sx-call
                           (merge-attrs
                            (tooltip-attrs
                             {:-text      ["`:left`" "Click to view code"]
                              :-placement :l})
                            {:style {:grid-area "l"}}))}
               {:label    "right-bottom",
                :args     [":rb"],
                :sx-attrs (sx-call
                           (merge-attrs
                            (tooltip-attrs
                             {:-text      ["`:right-bottom`" "Click to view code"]
                              :-placement :rb})
                            {:style {:grid-area "rb"}}))}
               {:label    "left-bottom",
                :args     [":lb"],
                :sx-attrs (sx-call
                           (merge-attrs
                            (tooltip-attrs
                             {:-text      ["`:left-bottom`" "Click to view code"]
                              :-placement :lb})
                            {:style {:grid-area "lb"}}))}
               {:label    "top-right-corner",
                :args     [":trc"],
                :sx-attrs (sx-call
                           (merge-attrs
                            (tooltip-attrs
                             {:-text      ["`:top-right-corner`" "Click to view code"],
                              :-placement :trc})
                            {:style {:grid-area "trc"}}))}
               {:label    "top-right",
                :args     [":tr"],
                :sx-attrs (sx-call
                           (merge-attrs
                            (tooltip-attrs
                             {:-text      ["`:top-right`" "Click to view code"]
                              :-placement :tr})
                            {:style {:grid-area "tr"}}))}
               {:label    "top",
                :args     [":t"],
                :sx-attrs (sx-call
                           (merge-attrs
                            (tooltip-attrs
                             {:-text      ["`:top`" "Click to view code"]
                              :-placement :t})
                            {:style {:grid-area "t"}}))}
               {:label    "top-left",
                :args     [":tl"],
                :sx-attrs (sx-call
                           (merge-attrs
                            (tooltip-attrs
                             {:-text      ["`:top-left`" "Click to view code"]
                              :-placement :tl})
                            {:style {:grid-area "tl"}}))}
               {:label    "top-left-corner",
                :args     [":tlc"],
                :sx-attrs (sx-call
                           (merge-attrs
                            (tooltip-attrs
                             {:-text      ["`:top-left-corner`" "Click to view code"],
                              :-placement :tlc})
                            {:style {:grid-area "tlc"}}))}]
    #_[{:label "right"
                :args  [(str :r)]
                :attrs (tooltip-attrs {:-text      ["Tooltip with right placement"
                                                    "Click to view code"]
                                       :-placement :r})}
               {:label "bottom left"
                :args  [(str :bl)]
                :attrs (tooltip-attrs {:-text      ["Tooltip with bottom-left placement"
                                                    "Click to view code"]
                                       :-placement :bl})}

                ;; {:label "Loading state, spinner"
                ;;  :args  [[progress "Play" [spinner]]]}
                ;; {:label "Loading state, spinner, fast"
                ;;  :args  [[progress "Play" [spinner (sx :animation-duration--325ms)]]]}
                ;; {:label "Loading state, spinner on icon"
                ;;  :args  [[progress [icon :play-arrow] [spinner]] "Play"]}
                ;; {:label "Loading state, propeller on icon"
                ;;  :attrs {:disabled true}
                ;;  :args  [[progress [icon :play-arrow] [spinner]] "Play"]}
                ;; {:label "Disabled"
                ;;  :attrs {:disabled true}
                ;;  :args  ["Play"]}
               ]}

  ;;  {:desc      "Tooltips in various positions"      
  ;;   :sx-attrs  (sx-call (sx :.xxlarge))
  ;;   :examples  (for [s (take 4 component-examples/colors)]
  ;;                {:label (name s)
  ;;                 :attrs {:class [s]}})}
   
  ;;  {:desc        "`semantic` variants, outline styling"
  ;;   :sx-attrs     (sx-call (sx
  ;;                           :.xxlarge
  ;;                           :$switch-border-color--$gray-400
  ;;                           :$switch-border-width--1.5px
  ;;                           :$switch-off-background-color--white
  ;;                           ["bgc" :transparent!important]
  ;;                           ["hover:bgc" :transparent!important]
  ;;                           ["[aria-checked='true']:bc" :currentColor]
  ;;                           ["[aria-checked='true']:hover:bc" :currentColor]
  ;;                           {:-thumb-attrs (sx
  ;;                                           :.elevated-0!
  ;;                                           [:border
  ;;                                            "calc(var(--switch-border-width) * (1 / var(--switch-thumb-scale-factor))) solid transparent"]
  ;;                                           [:bgc
  ;;                                            :$switch-border-color]
  ;;                                           ["has-ancestor(.kushi-switch[aria-checked='true']):bgc"
  ;;                                            :currentColor])}))
  ;;   :examples     (for [s component-examples/colors]
  ;;                   {:label (name s)
  ;;                    :attrs {:class [s]}})}
   
  ;;  {:desc     "Showing sizes from `small` to `xxxlarge`"
  ;;   :row-attrs (sx :ai--fe)
  ;;   :examples  (for [sz switch-sizes]
  ;;                {:label (name sz)
  ;;                 :attrs {:class sz}})}
   
  ;;  {:desc         "With convex-styled thumb control"
  ;;   :row-attrs    (sx :ai--fe)
  ;;   :sx-attrs     (sx-call (sx {:-thumb-attrs (sx :.convex)}))
  ;;   :examples     (for [sz switch-sizes]
  ;;                   {:label (name sz)
  ;;                    :attrs {:class [sz]}})}
   
  ;;  {:desc         "With oversized thumb control"
  ;;   :row-attrs    (sx :ai--fe)
  ;;   :sx-attrs     (sx-call (sx
  ;;                           :$switch-border-width--0px
  ;;                           :$switch-thumb-scale-factor--1.25
  ;;                           {:-thumb-attrs (sx :outline--1px:solid:currentColor :outline-offset---1px)}))
  ;;   :examples     (for [sz switch-sizes]
  ;;                   {:label (name sz)
  ;;                    :attrs {:class [sz]}})}
   
  ;;  {:desc         "With labeled track"
  ;;   :row-attrs    (sx :ai--fe)
  ;;   :sx-attrs     (sx-call (sx
  ;;                           :$switch-width-ratio--2.25
  ;;                           {:-track-content-on  "ON"
  ;;                            :-track-content-off "OFF"}))
  ;;   :examples     (for [sz switch-sizes]
  ;;                   {:label (name sz)
  ;;                    :attrs {:class [sz]}})}
   
  ;;  {:desc        "With labeled thumb"
  ;;   :row-attrs    (sx :ai--fe)
  ;;   :sx-attrs     (sx-call (sx {:-thumb-content-on  [:span (sx :.semi-bold :fs--0.325em) "ON"]
  ;;                               :-thumb-content-off [:span (sx :.semi-bold :fs--0.325em) "OFF"]}))
  ;;   :examples     (for [sz switch-sizes]
  ;;                   {:label (name sz)
  ;;                    :attrs {:class [sz]}})}
   
  ;;  {:desc        "With icon track"
  ;;   :reqs         '[[kushi.ui.icon.core :refer [icon]]]
  ;;   :row-attrs    (sx :ai--fe)
  ;;   :sx-attrs     (sx-call (sx {:-track-content-on  [icon (sx :fs--0.55em
  ;;                                                             {:-icon-filled? true})
  ;;                                                    :visibility]
  ;;                               :-track-content-off [icon (sx :fs--0.55em
  ;;                                                             {:-icon-filled? true})
  ;;                                                    :visibility-off]}))
  ;;   :examples     (for [sz switch-sizes]
  ;;                   {:label (name sz)
  ;;                    :attrs {:class [sz]}})}
   
  ;;  {:desc         "With icon thumb"
  ;;   :reqs         '[[kushi.ui.icon.core :refer [icon]]]
  ;;   :row-attrs    (sx :ai--fe)
  ;;   :sx-attrs     (sx-call (sx {:-thumb-content-on  [icon (sx :fs--0.55em
  ;;                                                             {:-icon-filled? true})
  ;;                                                    :visibility]
  ;;                               :-thumb-content-off [icon (sx :fs--0.55em
  ;;                                                             {:-icon-filled? true})
  ;;                                                    :visibility-off]}))
  ;;   :examples     (for [sz switch-sizes]
  ;;                   {:label (name sz)
  ;;                    :attrs {:class [sz]}})}
   
  ;;  {:desc         "Disabled states"
  ;;   :row-attrs    (sx :ai--fe)
  ;;   :examples     (for [sz switch-sizes]
  ;;                   {:label (name sz)
  ;;                    :attrs {:disabled true
  ;;                            :class    [sz]}})}
   

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

