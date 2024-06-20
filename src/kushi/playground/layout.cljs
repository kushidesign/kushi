(ns ^:dev/always kushi.playground.layout
  (:require [domo.core :as domo]
            [clojure.walk :as walk]
            [fireworks.core :refer [? ?-- ?- ?trace ?log]]
            [kushi.core :refer [sx merge-attrs]]
            [kushi.ui.core :refer [defcom]]
            [kushi.ui.modal.core :refer [open-kushi-modal modal close-kushi-modal]]
            [kushi.ui.modal.core :refer [modal open-kushi-modal close-kushi-modal]]
            [kushi.ui.button.core :refer [button]]
            [kushi.ui.collapse.core :refer [collapse]]
            [kushi.ui.icon.core :refer [icon]]
            [kushi.playground.component-docs :as docs]
            [kushi.playground.component-examples :as component-examples]
            [kushi.playground.state :as state]
            [kushi.playground.ui :refer [light-dark-mode-switch]]
            [kushi.playground.util :as util]
            [kushi.ui.popover.core :refer [popover-attrs dismiss-popover!]]
            [reagent.dom :as rdom]))

(defn sidenav-item-handler [label modal? e]
  (domo/scroll-into-view!
   (domo/qs-data= "kushi-playground-component" label))
  (domo/scroll-by! {:y -50})
  (when modal?
    (dismiss-popover! e)))

(defn mouse-down-a11y [f & args]
  {:on-key-down   #(when (contains? #{" " "Enter"} (.-key %))
                     (apply f (concat args [%])))
   :on-mouse-down #(when (= 0 (.-button %))
                     (apply f (concat args [%])))})

(defn all-componenents-sidenav-items
  [{:keys [coll modal?]}]
  (into [:ul (sx :.flex-col-fs
                 :.neutralize
                 :ai--c
                 :short:d--grid
                 :short:gtc--max-content:max-content
                 :short:ji--center
                 :pb--0rem:2rem
                 :pi--0.5em
                 :overflow-y--auto)]
        (for [{:keys [label]} coll
              :let            [focused? (= label
                                           @state/*playground-first-intersecting)]]
          [:li (sx :.small
                   :.capitalize
                   :.pointer
                   :w--fit-content
                   :pb--0.25em
                   [:hover>button.neutral.minimal:bgc (if focused? :$neutral-650 :$neutral-100)])
           [button
            (merge-attrs
             (sx :.pill
                 :.minimal
                 :.neutral
                 :.xxxfast
                 :pi--1em
                 :pb--0.5em
                 [:fw (when focused? :$wee-bold)]
                 [:&.neutral.minimal:bgc (when focused? :$neutral-650)]
                 [:dark:&.neutral.minimal:bgc (when focused? :$neutral-300)]
                 [:&.neutral.minimal:c (when focused? :white)]
                 [:dark:&.neutral.minimal:c (when focused? :black)])
             (mouse-down-a11y sidenav-item-handler label modal?))
            label]])))


(defn below-breakpoint? [k]
  (false? (->> (kushi.core/breakpoints)
               k
               first
               (apply domo/matches-media?))))

(defn- all-componenents-sidenav-button [attrs]
  [:button
   attrs
   [:span (sx :.flex-row-c :gap--0.5em :lg:&_.kushi-icon:d--none)
    [icon :menu]
    "All Components"]])

(defn all-components-sidenav
  [playground-components]
  (let [modal-id "mobile-sidenav-all-components"]
    [:nav (sx :.fixed
              :.small
              :.flex-col-fs
              :.neutralize
              :ai--c
              [:lg:h "calc(100vh - var(--navbar-height))"]
              :h--fit-content
              :width--fit-content
              :zi--4
              :iie--1.25rem
              :md:iie--4rem
              :ibs--$navbar-height
              :lg:pb--0:1rem)

     ;; Button for lg and below (mobile)
     [all-componenents-sidenav-button
      (merge-attrs 
       (sx :.all-components-sidenav-header
           :.pointer
           :lg:d--none)
       (popover-attrs
        {:-f         (fn [popover-el]
                       (rdom/render (partial all-componenents-sidenav-items 
                                             {:coll   playground-components
                                              :modal? true})
                                    popover-el))

         ;; why not on mobile?
         :-placement :b
         :-arrow?    false
         :class      (:class (sx 'all-components-sidenav-popover-pane
                                 :.styled-scrollbars
                                 :$popover-offset--0px
                                 :$popover-edge-padding--0px
                                 :$popover-flip-viewport-edge-threshold--0px
                                 :$popover-border-color--transparent
                                ;;  :$popover-border-color--red
                                 :$popover-z-index--2
                                 :overflow--hidden
                                 [:$popover-box-shadow 
                                  "0 0 0px 100vmax var(--white-transparent-70), 0 0 50vw 30vw white"]
                                 [:h '(calc :100vh - :125px)]))}))]

     ;; Button for lg and above
     [all-componenents-sidenav-button
      (sx :.all-components-sidenav-header
          :d--none
          :lg:d--flex
          :cursor--default)]

     ;; Component list for lg and above
     [:div (sx :d--none
               :lg:d--block
               [:h '(calc :100vh - :125px)])
      [all-componenents-sidenav-items 
       {:coll playground-components}]]]))



(defn header []
  [:div
   (sx :.kushi-playground-all-components-header
       :.fixed
       :.flex-row-sb
       :.neutralize
       :top--0
       :left--0
       :right--0
       :ai--c
       :zi--5
       :bbe--1px:solid:$neutral-150
       :dark:bbe--1px:solid:$neutral-750
       :w--100%
       :p--1rem
       :max-height--$navbar-height
       :pi--1.25rem
       :md:pi--4rem)
   [:span (sx :.semi-bold :fs--$xlarge)
    "Kushi"]
   [light-dark-mode-switch]])

(defn add-links [coll]
  (walk/postwalk #(if (and (map? %) (contains? % :href))
                    (assoc % :target :_blank :class [:kushi-link] )
                    %)
                 coll))


;; make clone of h1 section title

;; get opts section working


(defn link-button [attrs label]
  [:button (merge-attrs 
            (sx :.minimal
                :.xsmall
                :.normal
                :.xxxfast
                :.pointer
                :lh--0.8em
                :p--0
                :c--$neutral-secondary-foreground
                :hover:c--$neutral-foreground
                :hover:bgc--transparent
                :hover:td--underline
                :hover:tds--underline
                :hover:tdt--1.5px
                :hover:tup--under
                :hover:tuo--0.075em
                :active:bgc--transparent
                [:tdc "color-mix(in oklch, currentColor 10%, transparent)"]
                [:hover:tdc "color-mix(in oklch, currentColor 40%, transparent)"])
            attrs)
   label])

(defn docs-modal [modal-id label component-meta]
  [modal (sx :.fixed-block-start-inside
             ;; :$modal-backdrop-color--$white-transparent-70
             :$modal-border-radius--0
             ;; TODO pin to nav-height
             :&_.kushi-modal-inner:p--0
             :&_.kushi-modal-inner:gap--0rem
            ;;  :&.kushi-modal-open:o--0.5
             [:translate "-50% 0px"]
             ;; [:&.kushi-modal-open:translate "-50% 50px"]
             [:w "calc(100% - (2rem * 2))"]
             [:w :100vw]
             [:h :100vh]
             [:max-width :100vw]
             [:max-height :100vh]
             {:id modal-id})
   [:div (sx :.neutralize
             :position--sticky
             :zi--1
             :h--100px
             :pi--4rem
             [:top 0]
             [:w :100%])
    [:div (sx :.flex-row-sb
              :.small
              :.relative
              :h--50px
              :ai--c)
     [:div 
      [icon (sx :.medium
                :.absolute!
                :inset-inline-end--100%
                :inset-block-start--50%
                [:translate "-5px -50%"])
       :arrow-back]
      [link-button {:on-click close-kushi-modal} "Back to playground"]]
     [light-dark-mode-switch]]
    [:div (sx :.neutralize
              :.flex-row-sb
              :ai--c
              :zi--1
              :h--50px
              [:w :100%])
     [:div (sx :.flex-row-fs
               :ai--b
               :gap--1rem)
      [:h1 (sx :.semi-bold
               :.capitalize
               :lh--0.75em
               :fs--$xlarge)
       label]]
     ]]

    ;; summary
   [:div 
    (sx :.flex-col-fs
        :pi--4rem
        :gap--3rem)
    [:div 
     (-> component-meta
         :summary
         util/desc->hiccup
         add-links)]
    [:div
     (sx :max-width--660px)
     [:h2 (sx :.large :.semi-bold :mb--1rem) "Description"]
     [:p (sx :lh--1.7
             :&_code:pb--0.07em
             :&_code:pi--0.2em
             :&_code:fs--0.85rem
             :&_code:fw--$wee-bold
             :&_code:color--$accent-750
             :&_code:bgc--$accent-50
             :dark:&_code:color--$accent-100
             :dark:&_code:bgc--$accent-900
             )

      (-> component-meta :desc util/desc->hiccup add-links)]]]])


(defcom component-sections-tab
  [button (merge-attrs 
           (sx :.minimal
               :pi--0.8em
               :pb--0.4em
               ["&[aria-selected='true']:before"
                {:box-sizing :border-box
                 :content    "\"\""
                 :h          :2px
                 :position   :absolute
                 :bottom     :-0.5em
                 :left       0
                 :right      0
                 :bgc        :$accent-600
                 :dark:bgc   :$accent-300}]
               {:role     :tab
                :tab-index (if (contains? #{true "true"} (:aria-selected &attrs))
                             0
                             -1)
                :on-click #(let [el       (domo/et %)
                                 panel-id (:aria-controls &attrs)]
                             (domo/toggle-boolean-attribute-sibling el "aria-selected")
                             (domo/toggle-attribute-sibling el "tabindex" 0 -1)

                             ;; Switching the visibility of the panel
                             (when-let [panel-el (domo/el-by-id panel-id) ]
                               (when-let [parent (domo/parent panel-el)]
                                 ;; Hide the currently active panel
                                 (some-> parent
                                         (domo/qs ".playground-component-panel:not([hidden])")
                                         (.setAttribute "hidden" "hidden"))
                                 ;; Reveal the target panel
                                 (.removeAttribute panel-el "hidden")
                                 
                                 ;;Scroll to the top of the target panel
                                 (domo/scroll-into-view! panel-el)
                                 (domo/scroll-by! (-> el
                                                      (domo/nearest-ancestor ".component-section-header")
                                                      domo/client-rect
                                                      :bottom
                                                      -
                                                      (->> (hash-map :y)))))
                               ))})
           &attrs)
   &children])

                
(declare component-section)

;; Everytime there is a resize event -
;; Check if viewport height changes
;; If so, redo all the intersection observer stuff
(defn layout [_comps]
  
  [:div (sx :.flex-col-fs
            :$navbar-height--50px
            ;; :pi--4rem
            ;; [:bgi '(linear-gradient "to right" "white" "white 480px" "#f5f5f5 480px" "#f5f5f5")]
            ;; :.debug-red
            ;; :outline-width--2px
            ;; :outline-offset---2px
            )
   [header]

   [all-components-sidenav _comps]

   ;; Main section
   (into [:div
          (sx :.kushi-playground-all-components
              :>section:first-child:pbs--$navbar-height
              :.flex-col-fs
              :.grow
              :gap--5rem
              :pb--0:30vh)]
        #_[:div "hi"] 
         ;; Cycle through Collection of components  defined in playground.core
         (for [{:keys [label
                       media-matches
                       examples]
                :as component-opts}
               _comps]
           [:section
            (sx :min-height--200px
                :>*:pi--1.25rem
                :md:>*:pi--4rem
                {:data-kushi-playground-component label
                 :ref (fn [el]
                        (when el
                          (domo/observe-intersection 
                           (let [f (partial swap!
                                            state/*playground
                                            update-in
                                            [:intersecting])]
                             {:element          el
                              :not-intersecting #(f disj label)
                              :intersecting     #(f conj label)
                              ;; Incorporate into global val for header height
                              :root-margin      "51px 0px 0px 0px"}))))})
            
            [:div (sx 'component-section-header
                      :.neutralize
                      :.flex-col-fs
                      :position--sticky
                      :zi--1
                      :h--100px
                      :>div:h--50%
                      [:top :$navbar-height]
                      [:w :100%]
                      #_{:data-kushi-component-section-header label})
             [:div (sx :.flex-row-fs
                       :ai--c
                       :gap--1rem)
              [:h1 (sx :.semi-bold
                       :.capitalize
                       :lh--0.75em
                       :fs--$xlarge)
               label]]
             (let [#_component-sections-tab
                   ]
               [:div (sx :.flex-row-fs
                         :.small
                         :ai--fe
                         :gap--0.75em
                         :bbe--1px:solid:$neutral-150
                         :pbe--0.5em
                         {:role :tablist})
                [component-sections-tab
                 {:aria-selected true
                  :aria-controls (str "kushi-" label "-examples")}
                 "Examples"]
                [component-sections-tab
                 {:aria-selected false
                  :aria-controls (str "kushi-" label "-documentation")}
                 "Documentation"]
                ])]

            ;; For conditionally hiding based on device features
            ;; e.g. Do not show tooltip examples on mobile/touch
            (when (seq examples)
              (let [{:keys [matches
                            message]} media-matches
                    unsupported?      (when (and matches message)
                                        (not (some (fn [[prop val]]
                                                     (domo/matches-media? prop val))
                                                   matches)))]
                (if unsupported? 
                  [:p (sx :mbs--2rem) message]
                  [component-section component-opts])))]))])


(defn component-section
  [{:keys                     [examples label]
    {:keys [desc summary]
     custom-attributes :opts} :component-meta
    :as                       component-opts}]

  ;; TODO fireworks error :label is not ISeqable
  ;; (? (keys component-opts))

  (into [:<>
         (into [:section
                (sx :.playground-component-panel
                    {:id (str "kushi-" label "-examples")})]
               (for [
                    ;; example-opts (take 1 examples)
                    example-opts examples
                    ;; example-opts (keep-indexed (fn [idx m] (when (contains? #{3} idx) m)) examples)
                     ]
                 [component-examples/examples-section component-opts example-opts]))
         [:div (sx :.playground-component-panel
                   :max-width--660px
                   ;; TODO - tie to navbar
                   :pbs--35px
                   {
                    :hidden "hidden"
                    :id     (str "kushi-" label "-documentation")})
           (some->> summary
                    util/desc->hiccup 
                    docs/add-links
                    (into [:div (sx :.medium :.wee-bold :mb--0:2rem)]))
          [:h2 (sx :.large :.semi-bold :mb--0:1.5rem) "Usage"]
          [:div (sx :lh--1.7
                    :mb--2rem
                    :&_code:lh--1.9
                    :&_code:pb--0.07em
                    :&_code:pi--0.2em
                    :&_code:fs--0.85rem
                    :&_code:fw--$wee-bold
                    :&_code:color--$accent-750
                    :&_code:bgc--$accent-50
                    :dark:&_code:color--$accent-100
                    :dark:&_code:bgc--$accent-900)
           (some-> desc
                   util/desc->hiccup 
                   docs/add-links)]
          (when (seq custom-attributes)
            [:<>
             [:h2 (sx :.large :.semi-bold :mb--0:1.5rem) "Opts"]
             (into [:div]
                   (for [{nm      :name
                          typ     :type
                          pred    :pred
                          desc    :desc
                          default :default} (second custom-attributes)]
                     (when nm
                       [:div (sx
                              :.small
                              [:first-child:bbs "1px solid var(--gray-200)"]
                              [:dark:first-child:bbs "1px solid var(--gray-800)"]
                              [:bbe "1px solid var(--gray-200)"]
                              [:dark:bbe "1px solid var(--gray-800)"]
                              :pb--1em)
                        [:div (sx :mb--0.7rem)
                         [:span
                          (sx :.code
                              :.semi-bold
                              {:style {:pb       :0.07em
                                       :pi       :0.2em
                                       :fs       :0.85rem
                                       :c        :$accent-750
                                       :bgc      :$accent-50
                                       :dark:c   :$accent-100
                                       :dark:bgc :$accent-900}})
                          (str ":-" nm)]]
                        [:div (sx :pis--1.4em)
                         (when pred [docs/opt-detail "Pred" pred docs/kushi-opts-grid-type :pred])
                         (when typ [docs/opt-detail "Type" typ docs/kushi-opts-grid-type :type])
                         [docs/opt-detail "Default" default docs/kushi-opts-grid-default :default]
                         (when desc [docs/opt-detail "Desc." desc docs/kushi-opts-grid-desc :desc])]])))])]]))
