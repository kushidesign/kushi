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
                   [:hover>span:bgc (if focused? 
                                      :$neutral-650
                                      :$neutral-100)])
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
       :bgc--white
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
       :.flex-row-sb
       :top--0
       :left--0
       :right--0
       :ai--c
       :zi--5
       :bgc--white
       :bbe--1px:solid:$neutral-150
       :w--100%
       :p--1rem
       :max-height--41.5px
       :md:max-height--51.5px
       :pi--1.25rem
       :md:pi--4rem
       )
   [:span (sx :.semi-bold
              :fs--$xlarge
              ;; :md:fs--$xxlarge
              :o--0.5)
    "Kushi"]])


;; Everytime there is a resize event -
;; Check if viewport height changes
;; If so, redo all the intersection observer stuff

(defn layout [_comps]
  
  [:div (sx :.flex-col-fs
            ;; :pi--4rem
            ;; [:bgi '(linear-gradient "to right" "white" "white 480px" "#f5f5f5 480px" "#f5f5f5")]
            ;; :.debug-red
            ;; :outline-width--2px
            ;; :outline-offset---2px
            )
   [header]
   [mobile-component-sidenav _comps]
   [mobile-component-sidenav-scrim]
   [desktop-component-sidenav _comps]

   ;; Main section
   (into [:div
          (sx :.kushi-playground-all-components
              :>section:first-child:pbs--41.5px
              :md:>section:first-child:pbs--51.5px
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
                  ;; :.playground-pane-box-shadow
                  :.flex-col-c
                  :ai--fs
                  :fs--$xlarge
                  ;; :md:fs--$xxlarge
                  :lh--0.75em
                  :position--sticky
                  :zi--1
                  :h--41px
                  :md:h--51px
                  [:top :41px]
                  [:md:top :51px]
                  ;; [:pbs "calc(41.5px / 3)"]
                  ;; [:md:pbs "calc(51.5px / 3)"]
                  [:w :100%]
                  :bgc--white)
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
