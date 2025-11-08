(ns ^{:kushi/layer "user-styles"} kushi.ui.tooltip.demo
  (:require 
   [kushi.core :refer (sx css merge-attrs css-vars-map grid-template-areas)]
   [kushi.ui.button :refer [button]]
   [kushi.showcase.core :as showcase :refer [samples]]
   [kushi.ui.tooltip :refer [tooltip-attrs]]))


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
               (tooltip-attrs {:text      ["Tooltip Line 1" "Tooltip Line 2" ]
                               :placement (keyword x)}))
      [:span.placement-label (str ":" x)]])))


(def demos
  [{:label         "Basic"
    :header/dialog "Tooltip"
    :require       '[[kushi.ui.button :refer [button]]]
    :samples       (samples [[button (merge-attrs
                                      {:size :small}
                                      (tooltip-attrs {:text "This is a tooltip"}))
                              "Hover me"]])}
   
   {:label         "Styling via design token at callsite."
    :header/dialog "Tooltip"
    :require       '[[kushi.ui.button :refer [button]]]
    :samples       (samples [[button
                              (merge-attrs
                               {:size :small}
                               (tooltip-attrs
                                {:text          "This is a tooltip"
                                 :tooltip-class (css {:--tooltip-font-size                  :34px
                                                      :--tooltip-background-color           :$red-800
                                                      :--tooltip-background-color-dark-mode :$red-300})}))
                              "Hover me"]])}

   {:label         "Tooltips with specific placements"      
    :header/dialog "Tooltip"
    :row-attrs     (let [gta (grid-template-areas
                              "brc br b  bl blc"
                              "rt  .  .  .  lt"
                              "r   .  .  .  l"
                              "rb  .  .  .  lb"
                              "trc tr t  tl tlc")]
                     {:style (css-vars-map gta)
                      :class (css
                              [:--tooltip-delay-duration :0ms]
                              :display--grid
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
                                       :fs             :$size-xsmall
                                       :fw             :$weight-wee-bold
                                       :cursor         :pointer
                                       :bgc            :$neutral-100
                                       :d              :flex
                                       :border-radius  :$shape-rounded
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
                                            :border-radius  :$shape-rounded
                                            :border         :1px:dashed:$neutral-500
                                            :hover:border   :1px:dashed:$neutral-400
                                            :flex-direction :column
                                            :jc             :c
                                            :h              :100%}]
                              [:dark:>span:hover {:border :1px:dashed:$neutral-400
                                                  :bgc    :$neutral-700}])})
    :samples       (samples [
                             [:span
                              (merge-attrs
                               (tooltip-attrs
                                {:text      ["`:bottom-right-corner`"],
                                 :placement :brc})
                               {:style {:grid-area "brc"}})
                              ":brc"]

                             [:span
                              (merge-attrs
                               (tooltip-attrs
                                {:text      ["`:bottom-right`"],
                                 :placement :br})
                               {:style {:grid-area "br"}})
                              ":br"]
                             
                             [:span
                              (merge-attrs
                               (tooltip-attrs
                                {:text      ["`:bottom`"],
                                 :placement :b})
                               {:style {:grid-area "b"}})
                              ":b"]

                             [:span
                              (merge-attrs
                               (tooltip-attrs
                                {:text      ["`:bottom-left`"],
                                 :placement :bl})
                               {:style {:grid-area "bl"}})
                              ":bl"]

                             [:span
                              (merge-attrs
                               (tooltip-attrs
                                {:text      ["`:bottom-left-corner`"],
                                 :placement :blc})
                               {:style {:grid-area "blc"}})
                              ":blc"]

                             [:span
                              (merge-attrs
                               (tooltip-attrs
                                {:text      ["`:right-top`"],
                                 :placement :rt})
                               {:style {:grid-area "rt"}})
                              ":rt"]

                             [:span
                              (merge-attrs
                               (tooltip-attrs
                                {:text      ["`:left-top`"],
                                 :placement :lt})
                               {:style {:grid-area "lt"}})
                              ":lt"]

                             [:span
                              (merge-attrs
                               (tooltip-attrs
                                {:text      ["`:right`"],
                                 :placement :r})
                               {:style {:grid-area "r"}})
                              ":r"]

                             [:span
                              (merge-attrs
                               (tooltip-attrs
                                {:text      ["`:left`"],
                                 :placement :l})
                               {:style {:grid-area "l"}})
                              ":l"]

                             [:span
                              (merge-attrs
                               (tooltip-attrs
                                {:text      ["`:right-bottom`"],
                                 :placement :rb})
                               {:style {:grid-area "rb"}})
                              ":rb"]

                             [:span
                              (merge-attrs
                               (tooltip-attrs
                                {:text      ["`:left-bottom`"],
                                 :placement :lb})
                               {:style {:grid-area "lb"}})
                              ":lb"]

                             [:span
                              (merge-attrs
                               (tooltip-attrs
                                {:text      ["`:top-right-corner`"],
                                 :placement :trc})
                               {:style {:grid-area "trc"}})
                              ":trc"]

                             [:span
                              (merge-attrs
                               (tooltip-attrs
                                {:text      ["`:tr`"],
                                 :placement :tr})
                               {:style {:grid-area "tr"}})
                              ":tr"]

                             [:span
                              (merge-attrs
                               (tooltip-attrs
                                {:text      ["`:t`"],
                                 :placement :t})
                               {:style {:grid-area "t"}})
                              ":t"]

                             [:span
                              (merge-attrs
                               (tooltip-attrs
                                {:text      ["`:tl`"],
                                 :placement :tl})
                               {:style {:grid-area "tl"}})
                              ":tl"]

                             [:span
                              (merge-attrs
                               (tooltip-attrs
                                {:text      ["`:tlc`"],
                                 :placement :tlc})
                               {:style {:grid-area "tlc"}})
                              ":tlc"]])}])


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
;;                                        {:text      [(str "`:" hydrated "`")
;;                                                      "This is a tooltip"]
;;                                         :placement x})
;;                                  {:style  {:grid-area (name x)}})
;;                                 )}))))
