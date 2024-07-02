(ns kushi.playground.sidenav
  (:require
   [fireworks.core :refer [? !? ?- !?- ?-- !?-- ?> !?> ?i !?i ?l !?l ?log !?log ?log- !?log- ?pp !?pp ?pp- !?pp- ?trace]]
   [domo.core :as d]
   [kushi.core :refer (sx merge-attrs)]
   [kushi.ui.popover.core :refer [popover-attrs dismiss-popover!]]
   [kushi.ui.button.core :refer [button]]
   [kushi.ui.icon.core :refer [icon]]
   [kushi.playground.component-examples :as component-examples]
   [kushi.playground.state :as state]
   [reagent.dom :as rdom]))


(defn sidenav-item-handler [label e]
  (component-examples/scroll-to-playground-component! label)
  (let [nav (some-> e d/cet (d/nearest-ancestor "nav"))]
    (d/toggle-boolean-attribute nav "aria-expanded")))


(defn mouse-down-a11y [f & args]
  {:on-key-down   #(when (contains? #{" " "Enter"} (.-key %))
                     (apply f (concat args [%])))
   :on-mouse-down #(when (= 0 (.-button %))
                     (apply f (concat args [%])))})

(defn all-componenents-sidenav-items
  [{:keys [coll modal?]}]
  (into [:ul (sx :.flex-col-fs
                 :.neutralize
                 :short:d--grid
                 :short:gtc--max-content:max-content
                 :short:ji--center
                 :bgc--transparent
                 :overflow-y--auto
                 :h--100%
                 :w--initial
                 :ai--c
                 :pb--0rem:2rem
                 :pi--0.5em
                 :column-gap--normal
                 )]
        (for [{:keys [label]} coll
              :let            [focused? (= label
                                           @state/*playground-first-intersecting)]]
          [:li (sx :.capitalize
                   :.pointer
                   :.flex-col-c
                   :fs--$small
                   :w--fit-content
                   :pb--0.25em

                   #_[:hover>button.neutral.minimal:bgc
                      (if focused? :$neutral-650 :$neutral-100)]
                   
                   [:hover>button.neutral.minimal:bgc :transparent])
           [button
            (merge-attrs
             (sx :.pill
                 :.minimal
                 :.neutral
                 :.xxxfast
                 :pi--1em
                 :pb--0.5em

                 :hover:td--u
                 :hover:tuo--0.1em

                 [:&.neutral.minimal:bgc :transparent]
                 [:&.neutral.minimal:bgc:hover :transparent]
                 [:&.neutral.minimal:bgc:active :transparent]
                 [:dark:&.neutral.minimal:bgc :transparent]
                 [:dark:&.neutral.minimal:hover:bgc :transparent]
                 [:dark:&.neutral.minimal:active:bgc :transparent]

                 ;; [:fw (when focused? :$wee-bold)]
                 ;; [:&.neutral.minimal:bgc (when focused? :$neutral-650)]
                 ;; [:dark:&.neutral.minimal:bgc (when focused? :$neutral-300)]
                 ;; [:&.neutral.minimal:c (when focused? :white)]
                 ;; [:dark:&.neutral.minimal:c (when focused? :black)]
                 )
             (mouse-down-a11y sidenav-item-handler label))
            label]])))


(defn all-componenents-sidenav-items-mobile
  [{:keys [coll modal?]}]
  (into [:ul (sx :.flex-col-fs
                 :.neutralize
                 :bgc--transparent
                 :overflow-y--auto
                 :h--80%
                 :w--100%
                 :ai--fs
                 :pb--0rem:12rem
                 :pi--0
                 :flex-wrap--wrap
                 :column-gap--2.75rem
                 :align-content--flex-start)]
        (for [{:keys [label]} coll
              :let            [focused? (= label
                                           @state/*playground-first-intersecting)]]
          [:li (sx :.capitalize
                   :.pointer
                   :.flex-col-c
                   :fs--$xlarge
                   :min-height--50px
                   :fw--$semi-bold
                   :w--fit-content
                   :pb--0.25em
                   [:hover>button.neutral.minimal:bgc :transparent])
           [button
            (merge-attrs
             (sx :.pill
                 :.minimal
                 :.neutral
                 :.xxxfast
                 :pi--0
                 :pb--0
                 :hover:td--u
                 :hover:tuo--0.1em
                 [:&.neutral.minimal:bgc :transparent]
                 [:&.neutral.minimal:bgc:hover :transparent]
                 [:&.neutral.minimal:bgc:active :transparent]
                 [:dark:&.neutral.minimal:bgc :transparent]
                 [:dark:&.neutral.minimal:hover:bgc :transparent]
                 [:dark:&.neutral.minimal:active:bgc :transparent]
                 ;; [:fw (when focused? :$wee-bold)]
                 ;; [:&.neutral.minimal:bgc (when focused? :$neutral-650)]
                 ;; [:dark:&.neutral.minimal:bgc (when focused? :$neutral-300)]
                 ;; [:&.neutral.minimal:c (when focused? :white)]
                 ;; [:dark:&.neutral.minimal:c (when focused? :black)]
                 )
             (mouse-down-a11y sidenav-item-handler label))
            label]])))


(defn- all-componenents-sidenav-button [attrs]
  [:button
   attrs
   [:span (sx :.flex-row-c
              :gap--0.5em
              :lg:&_.kushi-icon:d--none
              :&_.kushi-icon.sidenav-close-icon:d--none
              ["has-ancestor(nav[data-kushi-playground-sidenav][aria-expanded=\"true\"])"
               {:>.sidenav-menu-icon:d :none
                :>.sidenav-close-icon:d :inline-flex
                :>ul:h                 "calc((100vh - (var(--navbar-height) * 2)) * 1)"
                :h                     :fit-content
                :o                     1}]
              :c--$neutral-secondary-foreground
              :dark:c--$neutral-secondary-foreground-inverse)
    [icon (sx :.sidenav-menu-icon) :menu]
    [icon (sx :.sidenav-close-icon) :close]
    "All Components"]])

(defn all-components-sidenav
  [playground-components]
  [:nav (sx 
         :.small
         :.flex-col-fs
         :.neutralize
         :d--none
         :lg:d--flex
         ;;  :iie--4rem
         [:iie    :1.25rem]
         [:position  :fixed]
         [:ai        :c]
         [:w         :fit-content]
         [:h         "calc(100vh - var(--navbar-height))"]
         [:pi        0]
         [:ibs       :$navbar-height]
         [:translate :unset]
         [:pb        :0:1rem]
         [:jc        :flex-start]
         :zi--4
         [:box-shadow
          "-30px 0 30px var(--background-color), -30px -30px 30px var(--background-color), -30px 0 30px 10px var(--background-color), -30px -30px 30px 10px var(--background-color)"]
         [:dark:box-shadow
          "-30px 0 30px var(--background-color-inverse), -30px -30px 30px var(--background-color-inverse), -30px 0 30px 10px var(--background-color-inverse), -30px -30px 30px 10px var(--background-color-inverse)"]

         ;; So that our box-shadow does not taint popup on mobile -----
         ;; maybe nix?
         ;; ["has(>button[aria-expanded]):box-shadow" :none]
         ;; ["has(>button[aria-expanded]):dark:box-shadow" :none]
         ;; -----------------------------------------------------------
         
         {:data-kushi-playground-sidenav "true"})

   [all-componenents-sidenav-button
    (sx :.all-components-sidenav-header
        :.flex-row-fs
        :cursor--default
        )]

   [:div (sx 
          :w--fit-content
          [:h "calc(100vh - (var(--navbar-height) * 2))"])
    [all-componenents-sidenav-items 
     {:coll playground-components}]]])


(defn all-components-sidenav-mobile
  [playground-components]
  [:nav (sx 
         :.small
         :.flex-col-fs
         :.neutralize
         :lg:d--none
         :position--sticky
         :ai--fe
         :w--100%
         :h--$navbar-height
         :h--fit-content
         :pi--1.25rem
         :ibs--0
         :md:iie--4rem
         [:translate "0 calc(var(--navbar-height) + 0em)"] ;; was 0.25
         :box-shadow--none
         :zi--4
         ["&[aria-expanded=\"false\"]:bgc" :transparent]
         {:data-kushi-playground-sidenav "true"
          :aria-expanded                 false})

   [all-componenents-sidenav-button
    (merge-attrs 
     (sx :.all-components-sidenav-header
         :.pointer
         :&.neutral.minimal:c--$neutral-secondary-foreground
         :dark:&.neutral.minimal:c--$neutral-secondary-foreground-inverse)
     {:on-click (fn [e] 
                  (let [nav  (some-> e
                                     d/cet
                                     (d/nearest-ancestor "nav"))
                        diff (some-> nav
                                     d/client-rect
                                     :top
                                     (- 50))]
                    (when (pos? diff) (d/scroll-by! {:y diff}))
                    (d/toggle-boolean-attribute nav "aria-expanded")))})]

   [:div (sx :.flex-col-fs
             :ai--fs
             :.transition
             :overflow-y--auto
             :pi--0
             :flex-wrap--wrap
             :align-content--flex-start
             :column-gap--3rem
             :w--100%
             :h--0
             :o--0
             ["has-ancestor(nav[data-kushi-playground-sidenav][aria-expanded=\"true\"])"
              {:>ul:h "calc((100vh - (var(--navbar-height) * 2)) * 1)"
               :h :fit-content
               :o 1}])
    [all-componenents-sidenav-items-mobile 
     {:coll playground-components}]]])
