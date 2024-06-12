(ns ^:dev/always kushi.playground.layout
  (:require [domo.core :as domo]
            [fireworks.core :refer [?]]
            [kushi.core :refer [sx]]
            [kushi.playground.state :as state]))

(defn desktop-component-sidenav
  [_comps]
  [:nav
    (sx :bgc--$purple-00
        :.fixed
        [:h "calc(100vh - 50px)"]
        :iie--0
        :ibs--50px
        :min-width--190px
        :p--1rem
        :pbs--51.5px
        :$nav-padding--0.5em:1em)
    [:h2 
     (sx :.medium
         :.semi-bold
         :ta--center
         :p--$nav-padding
         :pbs--0.25em)
     "Components"]
    (into [:ul (sx :.flex-col-fs
                   :ai--c
                   :pbs--1rem
                   :overflow-y--auto)]
          (for [{:keys [label]} _comps
                :let [focused? (= label @state/*playground-first-intersecting)
                      ]]
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
                  {:on-click #(domo/scroll-into-view! (domo/qs-data= "kushi-playground-component" label))})
              label]]))])

(defn header []
  [:div
   (sx :.kushi-playground-all-components-header
       :.fixed
       :zi--2
       :bgc--$gray-200
       :w--100%
       :p--1rem)
   "Kushi"])


;; Everytime there is a resize event -
;; Check if viewport height changes
;; If so, redo all the intersection observer stuff

(defn layout [_comps]
  [:div (sx :.flex-col-fs
            ;; :.debug-red
            ;; :outline-width--2px
            ;; :outline-offset---2px
            )
   [header]
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
              :max-width--800px
              :gap--5rem
              :pi--4rem
              :pb--0:30vh
              :mie--190px)]
         
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
                     :lh--0.75em
                     :position--sticky
                     [:ibs :51.5px]
                     :zi--1
                     :pbs--51.5px
                     :w--100%
                     :bgc--white
                     :box-shadow--0:0:13px:8px:white|0:0:10px:9px:white)
             label]
            (when demo-component
              [demo-component opts])]))])

