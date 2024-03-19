(ns ^dev-always kushi.playground.demobox.core
  (:require
   [clojure.string :as string]
   [clojure.pprint :as pprint]
   [kushi.core :refer (sx merge-attrs)]
   [domo.core :as dom]
   [kushi.ui.flex.core :as flex]
   [kushi.ui.input.slider.css]
   [kushi.ui.button.core :refer (button)]
   [kushi.ui.icon.core :refer (icon)]
   [kushi.ui.icon.mui.svg :as mui.svg]
   [kushi.ui.tag.core :refer (tag)]
   [kushi.ui.tooltip.core :refer (tooltip-attrs)]
   [kushi.ui.label.core :refer (label)]
   [kushi.ui.input.slider.core :refer (slider)]
   [kushi.ui.input.checkbox.core :refer (checkbox)]
   [domo.core :refer (copy-to-clipboard)]
   [kushi.ui.core :refer (defcom)]
   [kushi.ui.input.radio.core :refer (radio)]
   [kushi.playground.demobox.handlers :as handlers]
   [kushi.playground.demobox.decorate :as decorate]
   [kushi.playground.demobox.devmode :refer [dev-mode-view]]
   [kushi.playground.demobox.defs :refer [variants-by-category]]
   [kushi.playground.state :as state :refer [*state *dev-mode?]]
   [kushi.playground.shared-styles]
   [kushi.playground.ui :refer [light-dark-mode-switch]]
   [kushi.playground.util :as util :refer-macros (keyed)]
   [reagent.core :as r]
   [reagent.dom :as rdom]
   ["react-dom" :as react-dom]))

(defcom input-row
  (let [{:keys [group-id
                group-role
                slider?]
         text  :label}     &opts]
    [:section
     (merge-attrs
      (sx 'kushi-playground-examples-input-row-wrapper
          :fs--$xsmall
          :sm:fs--$small
          :.flex-col-c
          :color--inherit)
      &attrs)
     [:fieldset
      (sx
       'kushi-playground-examples-input-row
       :.flex-col-c
       :sm:flex-direction--row
       [:gap (if slider? :1em :0.26em)]
       :sm:gap--0
       {:id              group-id
        :role            group-role
        :aria-labelledby (str group-id "-label")})
      [label
       (sx :.kushi-playground-meta-desc-label
           :min-width--90px
           :jc--fs
           {:id (str group-id "-label")})
       (if (= "size-expanded" text) "size" text)]

      [flex/row-fs
       (sx
        'kushi-playground-examples-input-group
        :.grow
        :flex-wrap--wrap
        (when slider? :.kushi-playground-examples-input-group-slider)
        [:mbs (when slider? :$kushi-playground-examples-input-group-slider_margin-block-start||0)]
        [:sm:mbs (when slider? :$sm_kushi-playground-examples-input-group-slider_margin-block-start||2.25em)]
        :row-gap--$kushi-playground-examples-input-group_row-gap||0.25em

        :$inputs-fw--$kushi-playground-examples-input-group_font-weight||$wee-bold
        :$inputs-fs--$kushi-playground-examples-input-group_font-size||$xsmall
        :$mqsm-inputs-fs--$sm_kushi-playground-examples-input-group_font-size||$small

        :fw--$inputs-fw

        :fs--$inputs-fs
        :sm:fs--$mqsm-inputs-fs

        :&_.kushi-slider-step-label:fs--$inputs-fs
        :sm:&_.kushi-slider-step-label:fs--$mqsm-inputs-fs

        :&_.kushi-slider-step-label:fw--$inputs-fw

        ["has-ancestor(#icon):row-gap" :0.5em]
        ["has-ancestor(#icon):column-gap" :1em]
        :&_label:hover:bgc--$gray-100
        :dark:&_label:hover:bgc--$gray-800)
       &children]]]))



(defn variant-controls
  [{:keys [*demostate]
    :as m}]
  (let [{:keys [nm variants]} @*demostate]
    (into [:<>]
          (for [variant-category variants
                :let             [label       (name variant-category)
                                  flavors     (get variants-by-category variant-category)
                                  slider?     (contains? #{"size" "size-expanded" "weight"} label) ]]
            [input-row
             (sx  {:-label      label
                   :-group-id   (str nm "-" (string/lower-case label))
                   :-group-role (when-not slider? "radiogroup")
                   :-slider?    slider? })

             (cond
               slider?
               (let [slider-opts (sx {:-labels-attrs (sx {:style {:mbe "calc(10px + 0.5em)" :ws :n}})
                                      :-steps        flavors
                                      :-step-marker  :dot
                                      :on-change     (partial handlers/slider-on-change
                                                              (merge m
                                                                     (keyed variant-category
                                                                            flavors)))
                                      :default-value (variant-category (:active-controls-by-type @*demostate))})]
                 [slider slider-opts])

               :else
               (into [:<>]
                     (for [utility-class* flavors
                           :let           [utility-class   (name utility-class*)
                                           label-id        (str utility-class "-label")
                                           current-checked (variant-category (:active-controls-by-type @*demostate))
                                           checked?        (= utility-class current-checked)
                                           m+              (merge m (keyed variant-category utility-class))]]
                       [radio
                        {:id           label-id
                         :-input-attrs (do
                                         {:id              utility-class
                                          :tabIndex        0
                                          :aria-labelledby label-id
                                          :value           utility-class
                                          :name            (str nm "-example:" (name variant-category))
                                          :checked         checked?
                                          :on-change       (partial handlers/variant-change m+)})}
                        utility-class])))]))))


(defcom copy-to-clipboard-button
  [button
   (merge-attrs
    (sx :.minimal
        :p--7px
        {:on-click #(copy-to-clipboard (:text-to-copy &opts))})
    (tooltip-attrs
     {:-text                        "Click to copy"
      :-text-on-click               "Copied!"
      :-text-on-click-tooltip-class (first (:class (sx :$tooltip-background-color--$positive-filled-background-color)))
      :-placement                   "block-start inline-end"})
    &attrs)
   [icon (sx :.medium!) mui.svg/content-copy]])

(defn controls-by-type-init
  [{:keys [defaults variants init-w-defaults?]}]
  (reduce (fn [acc kw]
            (assoc acc kw (when init-w-defaults?
                            (when-let [init (kw defaults kw)]
                              (if (or (keyword? init) (string? init))
                                (name init)
                                (str init))))))
          {}
          variants))

(defn get-example [examples k]
  (->> examples
       (filter #(= k (:label %)))
       first))

(defn current-snippet [st opts]
  (r/reaction (let [{:keys [hide-default-classes? active-controls-by-type]} @st
                    st-opts (keyed hide-default-classes? active-controls-by-type)]
                (-> @st
                    :active-example
                    :example
                    :quoted
                    (decorate/utility-classes-into-snippet (merge st-opts opts))
                    pprint/pprint
                    with-out-str
                    (string/replace #"," "")))))

(defn show-utility-class-checkbox [st]
  (let [{v :hide-default-classes?} @st]
    [:div (sx :mb--0.5rem :pis--5px)
     [checkbox {:value           (str v)
                :default-checked (str v)
                :on-change       #(swap! st assoc :hide-default-classes? (not v))
                :-label-attrs    (sx :.xsmall
                                     :.normal
                                     :c--$gray-700
                                     :dark:c--$gray-300)}
      "Hide utility classes in snippet if equal to default"]]))


(defn *demostate
  [{:keys [component-id
           defaults
           variants
           init-w-defaults?
           examples
           selector
           nm]}]
  (let [controls-path [:demo component-id :active-controls-by-type]
        x (or (get-in @*state controls-path)
              (let [ret (controls-by-type-init (keyed defaults
                                                      variants
                                                      init-w-defaults?))]
                (swap! *state assoc-in controls-path ret)
                ret))]

    (r/atom {:active-controls-by-type x
             :nm                      nm
             :component-id            component-id
             :defaults                defaults
             :variants                variants
             :selector                selector
             :hide-default-classes?   true
             :examples                examples
             :active-example          (or (when-let [active (get-in @*state [:demo component-id :active-example])]
                                            (get-example examples (:label active)))
                                          (get-example examples (:examples defaults)))})))


(defn demobox2
  [{:keys      [defaults
                utility-class-target
                selector]
    examples   :examples
    variants   :variants
    m*         :meta
    stage-attr :stage
    :as        m+}]
  (let [component-id         (subs (str m*) 2)
        dev-modal-id         (str component-id "-dev-modal")
        nm                   (util/meta->fname m*)
        utility-class-target (or utility-class-target nm)
        snippet-id           (str nm "-snippet")
        init-w-defaults?     true
        opts                 (keyed nm
                                    component-id
                                    defaults
                                    variants
                                    init-w-defaults?
                                    examples
                                    selector
                                    utility-class-target)
        *demostate           (*demostate opts)
        current-snippet      (current-snippet *demostate opts)
        current-stage        (r/reaction (-> @*demostate :active-example :example :evaled))
        update-classes       (fn [_]
                               (decorate/utility-classes-into-dom
                                (merge opts
                                       {:active-controls-by-type (:active-controls-by-type @*demostate)})))]


;; (println current-stage)
;; (println (dissoc @*demostate :examples))
;; (println @*demostate)
;; (println (keyed current-snippet current-stage))


    (r/create-class
     {:display-name         "example"
      :component-did-mount  (fn [this]
                              (let [focused? (state/focused? nm)
                                    node     (rdom/dom-node this)]
                                (when (and focused? @*dev-mode?)

                                  #_(dom/add-class node "kushi-playground-demobox-dev-mode")
                                  #_(open-kushi-modal dev-modal-id)
                                  #_(js/setTimeout (fn [_]
                                                     (dom/remove-class node "kushi-playground-demobox-dev-mode"))
                                                   1000)))
                              (update-classes this))
      :component-did-update update-classes
      :reagent-render       (fn []
                              (let [dev-mode-stage-ai (or (-> @*demostate :layout :justify-content) :center)
                                    dev-mode-stage-jc (or (-> @*demostate :layout :align-items) :center)]
                                [:section (sx :.kushi-playground-demobox)

                                   ;; Component preview stage
                                   ;; ------------------------------------
                                 [:div.flex-row-c.relative
                                  (merge-attrs stage-attr
                                               (sx 'kushi-demo-stage {:id  component-id
                                                                      :key (-> @*demostate
                                                                               :active-example
                                                                               :example
                                                                               :quoted)}))
                                  @current-stage

                                  [button (merge-attrs
                                           (sx :.southeast-inside
                                               :.kushi-playground-demobox-ui-icon
                                               {:on-click (fn [_]
                                                            (dom/add-class js/document.body
                                                                           "kushi-playground-dev-mode")
                                                            (js/setTimeout (fn [_]
                                                                             (dom/add-class js/document.body
                                                                                            "kushi-playground-dev-mode-hidden")
                                                                             (reset! *dev-mode? true))
                                                                           500))})
                                           (sx :$tooltip-offset---3px)
                                           (tooltip-attrs {:-text      "Enter dev mode"
                                                           :-placement "block-start inline-start corner"}))
                                   [icon :fullscreen]]

                                  (when (and (state/focused? nm) @*dev-mode?)
                                    (react-dom/createPortal
                                     (r/as-element [dev-mode-view *demostate current-stage component-id])
                                     (.. js/document (getElementById "kushi-playground-dev-mode-portal"))))]


                                 ;; Examples radio group
                                 ;; ------------------------------------
                                 (into [input-row {:-label "Examples"
                                                   :-nm    nm}]
                                       (for [{:keys [label radio-label example]} examples
                                             :let                                [id (if (vector? label)
                                                                                       (str (last label) ":" (-> example :quoted first name))
                                                                                       label)
                                                                                  label       (name label)
                                                                                  radio-label (or radio-label label)]]
                                         [radio
                                          (sx
                                           {:-input-attrs {:id        id
                                                           :value     id
                                                           :name      (str nm "-example:content")
                                                           :checked   (= label (-> @*demostate :active-example :label))
                                                           :on-change (fn [_]
                                                                        (swap! *state
                                                                               assoc-in
                                                                               [:demo component-id :active-example]
                                                                               (get-example (:examples @*demostate) label))
                                                                        (swap! *demostate
                                                                               assoc
                                                                               :active-example
                                                                               (get-example (:examples @*demostate) label)))}})
                                          (if (keyword? radio-label) (name radio-label) radio-label)]))


                                 ;; Variant controls section, radio groups and sliders
                                 ;; ------------------------------------
                                 [variant-controls {:*demostate *demostate}]

                                 [:section
                                  (sx :margin-block--1.5rem:0.5rem)

                                  ;; Code snippet section
                                  ;; ------------------------------------
                                  [:div
                                   (sx :.relative)
                                   [:div
                                    (sx :.codebox
                                        {:id snippet-id})
                                    [util/formatted-code @current-snippet]]
                                   [:div (sx :.absolute-fill)]

                                   [copy-to-clipboard-button
                                    (sx :.northeast-inside!
                                        {:-text-to-copy @current-snippet})]]


                                    ;; Show utility class if checked = default
                                    ;; Leave this out for now
                                    ;; ------------------------------------
                                  #_[show-utility-class-checkbox *demostate] ]]))})))
