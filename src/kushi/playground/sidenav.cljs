(ns kushi.playground.sidenav
  (:require
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
                   [:hover>button.neutral.minimal:bgc
                    (if focused? :$neutral-650 :$neutral-100)])
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
  [:nav (sx :.fixed
            :.small
            :.flex-col-fs
            :.neutralize
            :ai--c
            [:box-shadow
             "-30px 0 30px var(--background-color), -30px -30px 30px var(--background-color), -30px 0 30px 10px var(--background-color), -30px -30px 30px 10px var(--background-color)"]
            [:dark:box-shadow
             "-30px 0 30px var(--background-color-inverse), -30px -30px 30px var(--background-color-inverse), -30px 0 30px 10px var(--background-color-inverse), -30px -30px 30px 10px var(--background-color-inverse)"]

            ;; So that our box-shadow does not taint popup on mobile -----
            ["has(>button[aria-expanded]):box-shadow" :none]
            ["has(>button[aria-expanded]):dark:box-shadow" :none]
            ;; -----------------------------------------------------------

            [:lg:h "calc(100vh - var(--navbar-height))"]
            :h--fit-content
            :width--fit-content
            :zi--4
            :iie--1.25rem
            :md:iie--4rem
            :ibs--$navbar-height
            :lg:pb--0:1rem

            {:data-kushi-playground-sidenav "true"})

   ;; Button for lg and below (mobile)
   [all-componenents-sidenav-button
    (merge-attrs 
     (sx :.all-components-sidenav-header
         :.pointer
         :lg:d--none
         :&.neutral.minimal:c--$neutral-secondary-foreground
         :dark:&.neutral.minimal:c--$neutral-secondary-foreground-inverse)
  
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
                                 ;; :$popover-border-color--red
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
     {:coll playground-components}]]])
