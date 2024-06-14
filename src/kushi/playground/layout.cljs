(ns ^:dev/always kushi.playground.layout
  (:require [domo.core :as domo]
            [fireworks.core :refer [? ?--]]
            [kushi.core :refer [sx merge-attrs]]
            [kushi.ui.collapse.core :refer [collapse]]
            [kushi.ui.icon.core :refer [icon]]
            [kushi.playground.state :as state]
            [kushi.playground.component-examples :as component-examples]))


(defn componenent-sidenav-items [coll]
  (into [:ul (sx :.flex-col-fs
                 :ai--c
                 :pb--1rem:2rem
                 :bgc--white
                 :overflow-y--auto)]
        (for [{:keys [label]} coll
              :let            [focused? (= label
                                           @state/*playground-first-intersecting)]]
          [:li (sx :.xsmall
                   :.wee-bold
                   :.capitalize
                   :.pointer
                   :w--fit-content
                   :pb--0.25em
                   [:hover>span:bgc (if focused? :$neutral-650 :$neutral-100)])
           [:span 
            (sx :.pill
                :.block
                :p--$nav-padding
                [:fw (when focused? :$semi-bold)]
                [:bgc (when focused? :$neutral-650)]
                [:c (when focused? :white)]
                {:on-click (fn [e]
                             (domo/scroll-into-view!
                              (domo/qs-data= "kushi-playground-component" label))
                             (domo/scroll-by! {:y -50})
                             (when-let [collapse (domo/nearest-ancestor (domo/et e)
                                                                        "#playground-right-sidenav-collapse.kushi-collapse-expanded")]
                               (some-> collapse
                                       (domo/qs ".kushi-collapse-header")
                                       .click)))})
            label]])))

(defn mobile-component-sidenav-scrim []
  (when (:mobile-sidenav-expanded? @state/*playground)
    [:div (sx 'mobile-component-sidenav-scrim
              :.fixed-fill
              :zi--3
              :bgc--transparent
              ;; 160px is width of mobile side menu
              [:bgi "linear-gradient(to left, white 160px, rgba(255, 255, 255, 0.8) 320px, rgba(255, 255, 255, 0.8))"]
              :dark:bgc--black)]))

(defn mobile-component-sidenav 
  [playground-components]
  (do
    [collapse
     (merge-attrs
      (sx :.playground-right-sidenav
          :lg:display--none
          :width--160px
          :md:width--190px
          :$nav-padding--0.5em:1em
          {:id "playground-right-sidenav-collapse"})
      {:-label         "All Components"
       :-icon          [icon :menu]
       :-icon-expanded [icon :close]
       :-speed         0 
       :-header-attrs  (sx :.playground-right-sidenav-header
                           :.small!
                           :>span:jc--center
                           :pi--1em
                           :pb--0.25em:0.5em
                           {:on-click #_#(?-- 'User)
                            #(swap! state/*playground
                                    assoc-in
                                    [:mobile-sidenav-expanded?]
                                    (not (= "true"
                                            (.getAttribute (domo/cet %)
                                                           "aria-expanded"))))})}) 
     [componenent-sidenav-items playground-components]]))


(defn desktop-component-sidenav
  [playground-components]
  [:nav
   (sx :.playground-right-sidenav
       [:h "calc(100vh - 50px)"]
       :display--none
       :lg:display--block
       :width--160px
       :md:width--190px
       :$nav-padding--0.5em:1em)
   [:h2 
    (sx :.playground-right-sidenav-header
        :ta--center)
    "All Components"]
   [componenent-sidenav-items playground-components]])

(defn header []
  [:div
   (sx :.kushi-playground-all-components-header
       :.fixed
       :zi--5
       :bgc--$gray-200
       :w--100%
       :p--1rem)
   "Kushi"])


;; Everytime there is a resize event -
;; Check if viewport height changes
;; If so, redo all the intersection observer stuff

(defn layout [_comps]
  [:div (sx :.flex-col-fs
            [:bgi '(linear-gradient "to right" "white" "white 400px" "#f5f5f5 400px" "#f5f5f5")]
            ;; :.debug-red
            ;; :outline-width--2px
            ;; :outline-offset---2px
            )
   [header]
   [mobile-component-sidenav _comps]
   [mobile-component-sidenav-scrim]
   [desktop-component-sidenav _comps]
   ;; Main section
   #_[:div 
    (sx :.flex-col-fs :gap--1rem :p--100px)

    [kushi.button/button
     (sx :.xxlarge :.accent :.pill :.filled)
     "Hello"]

    [kushi.button/button2
     {:-size     :xxlarge
      :-semantic :accent
      :-shape    :pill
      :-variant  :filled}
     "Hello"]]

   (into [:div
          (sx :.kushi-playground-all-components
              :>section:first-child:pbs--3rem
              :.flex-col-fs
              :.grow
              :gap--5rem
              :pb--0:30vh
              :pi--2rem
              :md:pi--4rem)]
         
         #_[button2 "Hello"]
         ;; Cycle through Collection of components  defined in playground.core
         (for [{:keys [label demo-component] :as opts} _comps]
           [:section
            (sx :min-height--300px
                {:data-kushi-playground-component label
                 :ref (fn [el]
                        (when el
                          (domo/observe-intersection 
                           {:element          el
                            :not-intersecting #(swap! state/*playground update-in [:intersecting] disj label)
                            :intersecting     #(swap! state/*playground update-in [:intersecting] conj label)
                            ;; :intersecting     #(reset! state/*playground-focused-component-section label)
                            :root-margin      "51px 0px 0px 0px"})))})
            [:h1 (sx :.xxlarge
                     :.semi-bold
                     :.capitalize
                     :.playground-pane-box-shadow
                     :lh--0.75em
                     :position--sticky
                     [:ibs :51.5px]
                     :zi--1
                     :pbs--51.5px
                     [:w '(calc :100vw - :4rem)]
                     :bgc--white)
             label]
            (when demo-component
              [demo-component opts])]))])

