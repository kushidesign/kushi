(ns kushi.playground.sidenav
  (:require
   [fireworks.core :refer [? !? ?- !?- ?-- !?-- ?> !?> ?i !?i ?l !?l ?log !?log ?log- !?log- ?pp !?pp ?pp- !?pp-]]
   [domo.core :as domo]
   [kushi.core :refer (sx merge-attrs)]
   [kushi.ui.popover.core :refer [popover-attrs dismiss-popover!]]
   [kushi.ui.button.core :refer [button]]
   [kushi.ui.icon.core :refer [icon]]
   [kushi.playground.component-examples :as component-examples]
   [kushi.playground.state :as state]
   [reagent.dom :as rdom]))


(defn sidenav-item-handler [label modal? e]
  (component-examples/scroll-to-playground-component! label)
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

                 ;; :short:d--grid
                 ;; :short:gtc--max-content:max-content
                 ;; :short:ji--center

                 :bgc--transparent
                 :overflow-y--auto

                 :h--80%
                 :lg:h--100%
                 :w--100%
                 :lg:w--initial

                 ;;---------
                 :ai--fs
                 :lg:ai--c

                 :pb--0rem:2rem
                 :lg:pb--0rem:2rem

                 :pi--0
                 :lg:pi--0.5em


                 :flex-wrap--wrap
                 :lg:flex-wrap--unset

                 :column-gap--3rem
                 :lg:column-gap--normal

                 :align-content--flex-start
                 :lg:align-content--initial
                 ;;---------

                 )]
        (for [{:keys [label]} coll
              :let            [focused? (= label
                                           @state/*playground-first-intersecting)]]
          [:li (sx :.capitalize
                   :.pointer
                   :.flex-col-c

                   ;;--------------------
                   :fs--$xlarge
                   :lg:fs--$small
                   :min-height--50px
                   :lg:min-height--unset
                   :fw--$semi-bold
                   :lg:fw--inherit
                   :lg:d--list-item
                   ;;--------------------
                   
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
                 :pi--0
                 :pb--0
                 :lg:pi--1em
                 :lg:pb--0.5em

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
             (mouse-down-a11y sidenav-item-handler label modal?))
            label]])))


(defn- all-componenents-sidenav-button [attrs]
  [:button
   attrs
   [:span (sx :.flex-row-c
              :gap--0.5em
              :lg:&_.kushi-icon:d--none
              :c--$neutral-secondary-foreground
              :dark:c--$neutral-secondary-foreground-inverse)
    [icon :menu]
    "All Components"]])


(defn all-components-sidenav
  [playground-components]
  [:nav (sx 
         :.small
         :.flex-col-fs
         :.neutralize

         :position--sticky
         :ai--fe
         :w--100%
         :h--$navbar-height
         :h--fit-content
         :pi--1.25rem
         :ibs--0
         :md:iie--4rem
         [:translate "0 calc(var(--navbar-height) + 0.25em)"]

         [:lg {:position  :fixed
               :ai        :c
               :w         :fit-content
               :h         "calc(100vh - var(--navbar-height))"
               :pi        0
               :iie       :1.25rem
               :ibs       :$navbar-height
               :translate :unset
               :pb        :0:1rem
               :jc        :flex-start}]


         :box-shadow--none
        ; :background-color--transparent
         ;;when open


         :zi--4

         [:lg:box-shadow
          "-30px 0 30px var(--background-color), -30px -30px 30px var(--background-color), -30px 0 30px 10px var(--background-color), -30px -30px 30px 10px var(--background-color)"]
         [:dark:lg:box-shadow
          "-30px 0 30px var(--background-color-inverse), -30px -30px 30px var(--background-color-inverse), -30px 0 30px 10px var(--background-color-inverse), -30px -30px 30px 10px var(--background-color-inverse)"]


         ;; So that our box-shadow does not taint popup on mobile -----
         ;; maybe nix?
         ;; ["has(>button[aria-expanded]):box-shadow" :none]
         ;; ["has(>button[aria-expanded]):dark:box-shadow" :none]
         ;; -----------------------------------------------------------
         

         {:data-kushi-playground-sidenav "true"})

   ;; Button for lg and below (mobile)
   [all-componenents-sidenav-button
    (merge-attrs 
     (sx :.all-components-sidenav-header
         :.pointer
         :lg:d--none
         :&.neutral.minimal:c--$neutral-secondary-foreground
         :dark:&.neutral.minimal:c--$neutral-secondary-foreground-inverse)
     
     {:on-click #(let [sidenav (domo/qs "[data-kushi-playground-sidenav=\"true\"]") ]
                   ;; get height of navbar instead of magic num
                     (when-let [diff (- (:top (domo/client-rect sidenav))
                                        53)]
                       (domo/scroll-by! {:y diff})))}

     #_(popover-attrs
      {:-f         (fn [popover-el]
                     (let [sidenav (domo/qs "[data-kushi-playground-sidenav=\"true\"]") ]
                       ;; get height of navbar instead of magic num
                       (when-let [diff  (- (:top (domo/client-rect sidenav))
                                           53)]
                         (domo/scroll-by! {:y diff})))
                     
                     #_(js/requestAnimationFrame 
                        #(rdom/render (partial all-componenents-sidenav-items 
                                               {:coll   playground-components
                                                :modal? true})
                                      popover-el)))

         ;; why not on mobile?
       :-placement :br
       :-arrow?    false
       :class      (:class (sx 'all-components-sidenav-popover-pane
                               :.styled-scrollbars
                               :$popover-offset--0px
                               :$popover-edge-padding--0px
                               :$popover-flip-viewport-edge-threshold--0px
                               :$popover-border-color--transparent
                               :$popover-border-color--red
                               :$popover-z-index--2
                               :overflow--hidden
                               [:$popover-box-shadow 
                                "0 0 0px 100vmax var(--white-transparent-70), 0 0 50vw 30vw white"]
                               [:h '(calc :100vh - :325px)]))}))]

     ;; Button for lg and above
   [all-componenents-sidenav-button
    (sx :.all-components-sidenav-header
        :d--none
        :lg:d--flex
        :cursor--default)]

     ;; Component list for lg and above
   [:div (sx :d--none
             :lg:d--block
             :lg:w--fit-content

             ;;-------
             :d--block
             :ai--fs
             :pb--0rem:2rem
             :overflow-y--auto
             :pi--0
             :h--100%
             :w--100%
             :flex-wrap--wrap
             :jc--fs
             :align-content--fs
             :column-gap--3rem
             ;;-------
             
             [:h '(calc :100vh - :103px)])
    [all-componenents-sidenav-items 
     {:coll playground-components}]]])
