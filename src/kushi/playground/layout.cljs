(ns ^:dev/always kushi.playground.layout
  (:require
   [kushi.core :refer [sx]]
   [kushi.ui.button.core :as kushi.button]
   [kushi.ui.button.demo :as button.demo]))

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
        :pbs--4rem
        :$padding--0.5em:1em)
    [:h2 
     (sx :.medium
         :.semi-bold
          ;;  :ta--right
         :ta--center
         :p--$padding)
     "Components"]
    (into [:ul (sx 
                :.flex-col-fs
                :ai--c
                :pbs--1rem
                :gap--0.25rem
                :overflow-y--auto)]
          (for [{:keys [label]} _comps]
            [:li (sx :.xsmall
                     :.wee-bold
                     :.capitalize
                     :.pill
                     :w--fit-content
                     :p--$padding
                     :first-child:bgc--$blue-100)
             label]))])

(defn header []
  [:div
   (sx :bgc--$gray-200
       :.fixed
       :w--100%
       :p--1rem)
   "Kushi"])

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
          (sx :.flex-col-fs
              :.grow
              ;; :bgc--$brown-00
              :mbs--50px
              :gap--2rem
              :pi--4rem
              :pb--4rem
              :mie--190px)]
         
         #_[button2 "Hello"]
         ;; Cycle through Collection of components  defined in playground.core
         (for [{:keys [label demo-component] :as opts} _comps]
           [:section
            (sx :min-height--300px)
            [:h1 (sx :.xxlarge
                     :.semi-bold
                     :.capitalize
                     :mbe--2rem)
             label]
            (when demo-component
              [demo-component opts])]))])
