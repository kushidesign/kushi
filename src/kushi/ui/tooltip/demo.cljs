(ns ^{:kushi/layer "user-styles"} kushi.ui.tooltip.demo
  (:require 
   [kushi.core :refer (sx css merge-attrs css-vars-map grid-template-areas)]
   [kushi.playground.util :refer-macros [sx-call]]
   [kushi.ui.button.core :refer [button]]
   [kushi.ui.tooltip.core :refer [tooltip-attrs]]))

(defn demo []
  (into
   [:div
    (let [gta (grid-template-areas
               "brc br b  bl blc"
               "rt  .  .  .  lt"
               "r   .  .  .  l"
               "rb  .  .  .  lb"
               "trc tr t  tl tlc")]
      {:class (css :.grid
                   :gtc--1fr:1fr:1fr:1fr:1fr
                   :gtr--auto
                   :gap--1rem
                   :w--400px
                   :h--400px
                   :gta--$gta)
          :style (css-vars-map gta)})]

   (for [x     ["brc" "br" "b"  "bl" "blc"
                "rt"  nil  nil  nil  "lt"
                "r"   nil  nil  nil  "l"
                "rb"  nil  nil  nil  "lb"
                "trc" "tr" "t"  "tl" "tlc"]
         :when (not (nil? x))]

     [:button (merge-attrs
               {:style (css-vars-map x)
                :class (css :.kushi-playground-tooltip-demo-button
                            :.flex-row-c
                            :cursor--pointer
                            :position--relative
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
                            :.kushi-pseudo-tooltip-revealed:bc--$accent-color
                            :dark:.kushi-pseudo-tooltip-revealed:bc--$accent-color-dark-mode
                            :.kushi-pseudo-tooltip-revealed:c--$accent-color
                            :dark:.kushi-pseudo-tooltip-revealed:c--$accent-color-dark-mode
                            :.kushi-pseudo-tooltip-revealed:bgc--$accent-background-color
                            :dark:.kushi-pseudo-tooltip-revealed:bgc--$accent-background-color-dark-mode
                            :grid-area--$x)
                :tab-index 0}
               (tooltip-attrs {:-text      ["Tooltip Line 1" "Tooltip Line 2" ]
                               ;; :-reveal-on-click?         true
                               :-placement (keyword x)}))
      [:span.placement-label (str ":" x)]])))


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
;;                                                      "This is a tooltip"]
;;                                         :-placement x})
;;                                  {:style  {:grid-area (name x)}})
;;                                 )}))))


(def examples
  [{:desc      "Basic, auto-placement."
    :component button
    :reqs      '[[kushi.ui.button.core :refer [button]]]
    :row-attrs (sx :_.kui-button:fs--$small)
    :snippets  '[[button
                  (tooltip-attrs {:-text "This is a tooltip"})
                  "Hover me"]]
    :examples  [{:label    "right"
                 :args     ["Hover me"]
                 :sx-attrs (sx-call (tooltip-attrs {:-text "This is a tooltip"}))}]}
   
   {:desc      "Styling via design token at callsite."
    :component button
    :reqs      '[[kushi.ui.button.core :refer [button]]]
    :row-attrs (sx :_.kui-button:fs--$small)
    :snippets  '[[button
                  (tooltip-attrs
                   {:-text          
                    "This is a tooltip"
                    :-tooltip-class
                    (css 
                     {:--tooltip-font-size                  :34px
                      :--tooltip-background-color           :$red-800
                      :--tooltip-background-color-dark-mode :$red-300}
                     )})
                  "Hover me"]]
    :examples  [{:label    "right"
                 :args     ["Hover me"]
                 :sx-attrs (sx-call
                            (tooltip-attrs
                             {:-text          "This is a tooltip"
                              :-tooltip-class (css 
                                               {:--tooltip-font-size                  :34px
                                                :--tooltip-background-color           :$red-800
                                                :--tooltip-background-color-dark-mode :$red-300}
                                               )}))}]}

   {:desc     "Tooltips with specific placements"      
    :row-attrs (let [gta (grid-template-areas
                          "brc br b  bl blc"
                          "rt  .  .  .  lt"
                          "r   .  .  .  l"
                          "rb  .  .  .  lb"
                          "trc tr t  tl tlc")]
                 {:style
                  (css-vars-map gta)
                  
                  :class
                  (css
                   [:--tooltip-delay-duration :0ms]
                   :.grid
                   :gtc--1fr:1fr:1fr:1fr:1fr
                   :gtr--auto
                   :gap--0.75rem
                   :xsm:w--333px
                   :xsm:h--333px
                   :w--300px
                   :h--300px
                   :_span.kushi-tooltip-text:ta--c
                   :gta--$gta
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
                                       :bgc    :$neutral-700}])})
    :examples [{:label    "bottom-right-corner",
                :args     [":brc"],
                :sx-attrs (sx-call
                           (merge-attrs
                            (tooltip-attrs
                             {:-text      ["`:bottom-right-corner`" ],
                              :-placement :brc})
                            {:style {:grid-area "brc"}}))}
               {:label    "bottom-right",
                :args     [":br"],
                :sx-attrs (sx-call
                           (merge-attrs
                            (tooltip-attrs
                             {:-text      ["`:bottom-right`" ]
                              :-placement :br})
                            {:style {:grid-area "br"}}))}
               {:label    "bottom",
                :args     [":b"],
                :sx-attrs (sx-call
                           (merge-attrs
                            (tooltip-attrs
                             {:-text      ["`:bottom`" ]
                              :-placement :b})
                            {:style {:grid-area "b"}}))}
               {:label    "bottom-left",
                :args     [":bl"],
                :sx-attrs (sx-call
                           (merge-attrs
                            (tooltip-attrs
                             {:-text      ["`:bottom-left`" ]
                              :-placement :bl})
                            {:style {:grid-area "bl"}}))}
               {:label    "bottom-left-corner",
                :args     [":blc"],
                :sx-attrs (sx-call
                           (merge-attrs
                            (tooltip-attrs
                             {:-text      ["`:bottom-left-corner`" ],
                              :-placement :blc})
                            {:style {:grid-area "blc"}}))}
               {:label    "right-top",
                :args     [":rt"],
                :sx-attrs (sx-call
                           (merge-attrs
                            (tooltip-attrs
                             {:-text      ["`:right-top`" ]
                              :-placement :rt})
                            {:style {:grid-area "rt"}}))}
               {:label    "left-top",
                :args     [":lt"],
                :sx-attrs (sx-call
                           (merge-attrs
                            (tooltip-attrs
                             {:-text      ["`:left-top`" ]
                              :-placement :lt})
                            {:style {:grid-area "lt"}}))}
               {:label    "right",
                :args     [":r"],
                :sx-attrs (sx-call
                           (merge-attrs
                            (tooltip-attrs
                             {:-text      ["`:right`" ]
                              :-placement :r})
                            {:style {:grid-area "r"}}))}
               {:label    "left",
                :args     [":l"],
                :sx-attrs (sx-call
                           (merge-attrs
                            (tooltip-attrs
                             {:-text      ["`:left`" ]
                              :-placement :l})
                            {:style {:grid-area "l"}}))}
               {:label    "right-bottom",
                :args     [":rb"],
                :sx-attrs (sx-call
                           (merge-attrs
                            (tooltip-attrs
                             {:-text      ["`:right-bottom`" ]
                              :-placement :rb})
                            {:style {:grid-area "rb"}}))}
               {:label    "left-bottom",
                :args     [":lb"],
                :sx-attrs (sx-call
                           (merge-attrs
                            (tooltip-attrs
                             {:-text      ["`:left-bottom`" ]
                              :-placement :lb})
                            {:style {:grid-area "lb"}}))}
               {:label    "top-right-corner",
                :args     [":trc"],
                :sx-attrs (sx-call
                           (merge-attrs
                            (tooltip-attrs
                             {:-text      ["`:top-right-corner`" ],
                              :-placement :trc})
                            {:style {:grid-area "trc"}}))}
               {:label    "top-right",
                :args     [":tr"],
                :sx-attrs (sx-call
                           (merge-attrs
                            (tooltip-attrs
                             {:-text      ["`:top-right`" ]
                              :-placement :tr})
                            {:style {:grid-area "tr"}}))}
               {:label    "top",
                :args     [":t"],
                :sx-attrs (sx-call
                           (merge-attrs
                            (tooltip-attrs
                             {:-text      ["`:top`" ]
                              :-placement :t})
                            {:style {:grid-area "t"}}))}
               {:label    "top-left",
                :args     [":tl"],
                :sx-attrs (sx-call
                           (merge-attrs
                            (tooltip-attrs
                             {:-text      ["`:top-left`" ]
                              :-placement :tl})
                            {:style {:grid-area "tl"}}))}
               {:label    "top-left-corner",
                :args     [":tlc"],
                :sx-attrs (sx-call
                           (merge-attrs
                            (tooltip-attrs
                             {:-text      ["`:top-left-corner`" ],
                              :-placement :tlc})
                            {:style {:grid-area "tlc"}}))}]}])

