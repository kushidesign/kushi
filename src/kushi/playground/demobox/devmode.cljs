(ns ^dev-always kushi.playground.demobox.devmode
  (:require
   [kushi.core :refer (sx merge-attrs)]
   [kushi.ui.dom :as dom]
   [kushi.ui.input.slider.css]
   [kushi.ui.button.core :refer (button)]
   [kushi.ui.icon.core :refer (icon)]
   [kushi.ui.icon.mui.svg :as mui.svg]
   [kushi.ui.tag.core :refer (tag)]
   [kushi.ui.tooltip.core :refer (tooltip-attrs)]
   [kushi.ui.core :refer (defcom)]
   [kushi.playground.state :as state :refer [*state *dev-mode?]]
   [kushi.playground.shared-styles]
   [kushi.playground.ui :refer [light-dark-mode-switch]]))


(defcom exit-dev-mode-button
  [button (merge-attrs
           (sx
            :.kushi-playground-demobox-ui-icon)
           &attrs
           (tooltip-attrs {:-text      "Exit dev mode"
                           :-placement (:tooltip-placement &opts)}))
   [icon mui.svg/fullscreen-exit]])


(defn- stage-control
  [active? tooltip-text icon-name [prop value]]
  [button
   (merge-attrs
    (sx
     :.kushi-playground-demobox-ui-icon
     :.kushi-playground-demobox-ui-icon-stage-control
     {:on-click #(let [clicked (dom/et %)]
                   (when-let [stage-settings (dom/nearest-ancestor clicked ".dev-mode-stage-settings") ]
                     (let [stage           (.-previousSibling stage-settings)
                           cls             ".kushi-playground-demobox-ui-icon-stage-control"
                           button-group    (dom/nearest-ancestor clicked ".stage-control-button-group")
                           buttons-checked (.querySelectorAll button-group (str cls "[aria-selected='true']"))
                           ctrl-button     (if (dom/has-class? clicked cls)
                                             clicked
                                             (dom/nearest-ancestor clicked cls))]
                       (doseq [el buttons-checked]
                         (dom/set-attribute! el "aria-selected" false))
                       (dom/set-attribute! ctrl-button "aria-selected" true)
                       (dom/set-style! stage (name prop) (name value)))))
      :aria-selected (str active?)})
    (tooltip-attrs {:-text      tooltip-text
                    :-placement :top}))
   [icon icon-name]])


(defn dev-mode-view
  [*demostate current-stage component-id]
  [:div
   (sx :.flex-col-sb :ai--c :w--100% :h--100%)
   [:div (sx :.flex-row-sb
             :ai--c
             :gap--1rem
             :pb--0.75em
             :pi--3rem:1rem
             :w--100%)

    [:section.flex-row-fs
     (sx :gap--2rem)
     [tag (sx :fs--$xxxsmall!important
              :fw--$normal!important
              :&_.kushi-icon:text-shadow--0:0:4px:#00ffe0
              :&_.kushi-icon:c--#0a79ff
              :dark:&_.kushi-icon:c--$accent--300
              :.uppercase
              :.rounded)
      [icon {:-icon-filled? false} :bolt]
      "Playground Dev Mode"
      [icon {:-icon-filled? false} :bolt]]

     [:span.flex-row-fs
      (sx :gap--0.5em)
      [:code.semi-bold.xsmall! component-id]
      [:span.small.italic.small! (-> @*demostate :active-example :label)]]]

    [:div (sx :>button:pb--0.4rem!important)
     ;; todo put dark mode switch here
     [light-dark-mode-switch]]]

   [:section (sx 'dev-mode-stage
                 :$dev-mode-stage-margin-inline--3rem
                 :.relative
                 :.flex-col-c
                 :.grow
                 :p--2rem
                 :ai--c
                 [:width "calc(100% - (2 * var(--dev-mode-stage-margin-inline)))"]
                 :outline--1px:solid:$neutral-200
                 :dark:outline--1px:solid:$neutral-600
                 {:id :dev-mode-stage})
    @current-stage]

   [:div (sx 'dev-mode-stage-settings
             :.flex-row-sb
             :pb--0.75em
             :pi--1rem
             :w--100%
             :&_.kushi-icon:fs--large
             :&_.kushi-button:bgc--transparent
             :&_.kushi-button:hover:bgc--$gray-150
             :&_.kushi-button:p--7px
             :&_.kushi-button:border-radius--999px)
    [:div (sx :w--32px)]
    [:div (sx 'dev-mode-stage-settings-alignment
              :.flex-row-c
              :.pill!
              :gap--4.5em
              :&_.kushi-icon:fs--large
              :&_.kushi-button:bgc--transparent
              :&_.kushi-button:p--7px)
     [:div (sx :.flex-row-fs :.stage-control-button-group :gap--0.5em)
      [stage-control false "Justify left" :align-horizontal-left [:align-items :flex-start]]
      [stage-control true "Justify center" :align-horizontal-center [:align-items :center]]
      [stage-control false "Justify right" :align-horizontal-right [:align-items :flex-end]]]
     [:div (sx :.flex-row-fs :.stage-control-button-group :gap--0.5em)
      [stage-control false "Justify top" :vertical-align-top [:justify-content :flex-start]]
      [stage-control true "Justify middle" :vertical-align-center [:justify-content :center]]
      [stage-control false "Justify bottom" :vertical-align-bottom [:justify-content :flex-end]]]]

    [exit-dev-mode-button
     {:on-click           (fn [_]
                            (dom/remove-class js/document.body "kushi-playground-dev-mode-hidden")
                            (reset! *dev-mode? false)
                            (dom/remove-class js/document.body "kushi-playground-dev-mode"))
      :-tooltip-placement "inline-start"}]]])
