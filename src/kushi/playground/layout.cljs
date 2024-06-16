(ns ^:dev/always kushi.playground.layout
  (:require [domo.core :as domo]
            [fireworks.core :refer [? ?-- ?-]]
            [kushi.core :refer [sx merge-attrs]]
            [kushi.ui.modal.core :refer [open-kushi-modal modal close-kushi-modal]]
            [kushi.ui.button.core :refer [button]]
            [kushi.ui.collapse.core :refer [collapse]]
            [kushi.ui.icon.core :refer [icon]]
            [kushi.playground.state :as state]
            [kushi.playground.ui :refer [light-dark-mode-switch]]
            [kushi.playground.component-examples :as component-examples]
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
                   [:hover>button:bgc (if focused? :$neutral-650 :$neutral-100)])
           [button
            (merge-attrs
             (sx :.pill
                 :.minimal
                 :.neutral
                 :.xxxfast
                 :pi--1em
                 :pb--0.5em
                 [:fw (when focused? :$wee-bold)]
                 [:bgc (when focused? :$neutral-650)]
                 [:c (when focused? :white)])
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
       :w--100%
       :p--1rem
       :max-height--$navbar-height
       :pi--1.25rem
       :md:pi--4rem)
   [:span (sx :.semi-bold :fs--$xlarge)
    "Kushi"]
   [light-dark-mode-switch]])

      ;;  :bgc--$body-background-color-inverse
      ;;  :sm:bgc--$body-background-color
      ;;  :c--$body-color-inverse
      ;;  :sm:c--$body-color

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
         (for [{:keys [label demo-component media-matches] :as opts} _comps]
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
                              :root-margin      "51px 0px 0px 0px"})))
                        
                        
                        )})
            [:h1 (sx 
                  :.semi-bold
                  :.capitalize
                  :.neutralize
                  :.flex-col-c
                  :ai--fs
                  :fs--$xlarge
                  :lh--0.75em
                  :position--sticky
                  :zi--1
                  :h--50
                  [:top :$navbar-height]
                  [:w :100%])
             label]
            (when demo-component
              (let [{:keys [matches
                            message]} media-matches
                    unsupported?      (when (and matches message)
                                        (not (some (fn [[prop val]]
                                                     (domo/matches-media? prop val))
                                                   matches)))]
                (if unsupported? 
                  [:p (sx :mbs--2rem) message]
                  [demo-component opts])))]))])
