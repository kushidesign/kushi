(ns kushi.playground.sidenav
  (:require
   [domo.core :as d]
   [kushi.css.core :refer (sx css merge-attrs)]
   [kushi.css.defs]
   [kushi.ui.util :refer [as-str]]
   [kushi.ui.button.core :refer [button]]
   [kushi.ui.icon.core :refer [icon]]
   [kushi.playground.component-examples :as component-examples]))


(defn sidenav-item-handler [opts e]
  (component-examples/scroll-to-playground-component!
   (merge opts
          (when-let [[p v] (some-> kushi.css.defs/media :sm first)]
            (when-not (d/matches-media? p (as-str v))
              {:scroll-y 16}))))
  (when-let [nav (some-> e
                         d/cet 
                         (d/nearest-ancestor
                          "nav[data-kushi-playground-sidenav-mobile]"))]
    (d/toggle-boolean-attribute nav "aria-expanded")))


(defn style-tag-first-intersecting [x]
  [:style {:type "text/css"}
   (str 

    "#app[data-kushi-playground-first-intersecting=\"" x "\"] "
    "[data-kushi-playground-sidenav-button=\"" x "\"]"
    "{"
    "color: var(--neutral-minimal-color-hover);"
    "background-color: var(--neutral-minimal-background-color-hover);"
    "}"

    ".dark #app[data-kushi-playground-first-intersecting=\"" x "\"] "
    "[data-kushi-playground-sidenav-button=\"" x "\"]"
    "{"
    "color: var(--neutral-minimal-color-hover-inverse);"
    "background-color: var(--neutral-minimal-background-color-hover-inverse);"
    "}"

    ;; "#app[data-kushi-playground-first-intersecting=\"" x "\"] "
    ;; "[data-kushi-playground-sidenav-button=\"" x "\"]:hover"
    ;; "{"
    ;; "color: var(--foreground-color-inverse);"
    ;; "background-color: var(--background-color-inverse);"
    ;; "}"
    
    ;; "#app[data-kushi-playground-first-intersecting=\"" x "\"] "
    ;; "[data-kushi-playground-sidenav-button=\"" x "\"]:active"
    ;; "{"
    ;; "color: var(--foreground-color-inverse);"
    ;; "background-color: var(--background-color-inverse);"
    ;; "}"
    )])


(defn all-components-sidenav
  [playground-components]
  [:nav {:class (css 
                 :.small
                 :.flex-col-fs
                 :.neutralize
                 :d--none
                 :lg:d--flex
                 ;; Tie into globals
                 [:iie       :4rem]
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
                  "-30px 0 30px var(--background-color-inverse), -30px -30px 30px var(--background-color-inverse), -30px 0 30px 10px var(--background-color-inverse), -30px -30px 30px 10px var(--background-color-inverse)"])
         :data-kushi-playground-sidenav "true"}
   [:button
    (sx :.all-components-sidenav-header
        :.flex-row-fs
        :cursor--default)
    [:span (sx :.flex-row-c
               :gap--0.5em
               :c--$neutral-secondary-foreground
               :dark:c--$neutral-secondary-foreground-inverse)
     "All Components"]]

   [:div (sx 
          :w--fit-content
          [:h "calc(100vh - (var(--navbar-height) * 2))"])
    (into [:ul (sx :.flex-col-fs
                   :short:d--grid
                   :short:gtc--1fr:1fr
                   :short:gar--40px
                   :short:ji--center
                   :bgc--transparent
                   :overflow-y--auto
                   :h--100%
                   :w--initial
                   :ai--center
                   :pb--0rem:2rem
                   :pi--0em
                   :column-gap--normal
                   :fs--$small)]
          (for [{:keys [label]} playground-components]
            [:<>
             [style-tag-first-intersecting label]
             [:li (sx :.flex-col-c
                      :tt--capitalize
                      :cursor--pointer
                      :w--fit-content
                      :pb--0.25em)
              [button
               (merge-attrs
                (sx :.pill
                    :.minimal
                    :.neutral
                    :transition-duration--$xxxfast
                    :pi--1em
                    :pb--0.5em
                    :.neutral.minimal:c--$neutral-secondary-foreground
                    :.neutral.minimal:hover:c--black
                    :dark:.neutral.minimal:c--$neutral-secondary-foreground-inverse
                    :dark:.neutral.minimal:hover:c--white)
                {:data-kushi-playground-sidenav-button label}
                (d/mouse-down-a11y sidenav-item-handler {:component-label label}))
               label]]]))]])



(defn all-components-sidenav-mobile
  [playground-components]
  ;; TODO sync 4rem and 6rem with elsewhere
  [:nav (merge-attrs
         (sx 
          :.flex-col-fs
          :.neutralize
          :fs--$small
          [:--translate-y :20px]
          :lg:d--none
          :position--sticky
          :ai--fe
          :w--100%
          :pi--1.25rem
          :md:pi--4rem
          [:ibs "calc(0px - 4rem)"]
          [:xsm:ibs "calc(0px - 6rem)"]
          :md:iie--4rem
          [:translate "0 calc(4rem + 0.25em + var(--navbar-height))"]
          [:xsm:translate "0 calc(6rem + 0.25em + var(--navbar-height))"]
          :box-shadow--none
          :zi--4
          ["&[aria-expanded=\"false\"]"
           {:bgc :transparent}]
          ["&[aria-expanded=\"true\"]" 
           {:bgc :$white-transparent-90
            :bgi "linear-gradient(to left, var(--background-color), var(--background-color) 50%, transparent)"}]
          ["dark:&[aria-expanded=\"true\"]" 
           {:bgc :$black-transparent-90
            :bgi "linear-gradient(to left, var(--background-color-inverse), var(--background-color-inverse) 50%, transparent)"}])
         {:data-kushi-playground-sidenav        true
          :data-kushi-playground-sidenav-mobile true
          :aria-expanded                        false})

   ;; Button for toggling open nav 
   [:button
    (merge-attrs 
     (sx :.all-components-sidenav-header
         :.pointer

        ;; To use softer color 
         :.neutral.minimal:c--$neutral-secondary-foreground
         :dark:.neutral.minimal:c--$neutral-secondary-foreground-inverse)
     {:on-click (fn [e] 
                  (let [nav  (some-> e
                                     d/cet
                                     (d/nearest-ancestor "nav"))
                        diff (some-> nav
                                     d/client-rect
                                     :top
                                     ;; Tie to global
                                     (- 50))]
                    (when (pos? diff) (d/scroll-by! {:y diff}))
                    (d/toggle-boolean-attribute nav "aria-expanded")))})
    [:span (sx :.flex-row-c
               :gap--0.5em
               :lg:_.kushi-icon:d--none
               :_.kushi-icon.sidenav-close-icon:d--none
               ["has-ancestor(nav[data-kushi-playground-sidenav][aria-expanded=\"true\"])"
                {:>.sidenav-menu-icon:d  :none
                 :>.sidenav-close-icon:d :inline-flex
                 :>ul:h                  "calc((100vh - (var(--navbar-height) * 2)) * 1)"
                 :h                      :fit-content
                 :o                      1}]

               ;; To use softer color 
               :c--$neutral-secondary-foreground
               :dark:c--$neutral-secondary-foreground-inverse)
     [icon (sx :.sidenav-menu-icon :.extra-light :fs--$large) :menu]
     [icon (sx :.sidenav-close-icon :.extra-light :fs--$large) :close]
     "All Components"]]
   
   
   ;; Container for list of playground components
   [:div (sx :.flex-col-fs
             :ai--fs
             :.transition
             :overflow-y--auto
             :pi--0
             :flex-wrap--wrap
             :align-content--flex-start
             :column-gap--3rem
             :w--fit-content
             :h--0
             :o--0
             ["has-ancestor(nav[data-kushi-playground-sidenav][aria-expanded=\"true\"])"
              {:>ul:h "calc((100vh - (var(--navbar-height) * 2)) * 1)"
               :>ul:o 1
               :h     :fit-content
               :o     1}])

    ;; List of playground components
    (into [:ul (sx :.mobile-sidenav-list
                   :.flex-col-fs
                   :.neutralize
                   :o--0
                   :transition-property--opacity
                   :transition-delay--$transition-duration
                   :bgc--transparent
                   :overflow-y--auto
                   :w--fit-content
                   :ai--center
                   :pb--0rem:20vh
                   :pi--0
                   :flex-wrap--wrap-reverse
                   :column-gap--0.75rem
                   :align-content--inherit
                   :fs--$small
                   :min-width--50vw
                   :xsm:min-width--unset)]
          (for [{:keys [label]} playground-components]
            [:<> 
             [style-tag-first-intersecting label]
             [:li (sx :tt--capitalize
                      :cursor--pointer
                      :.flex-col-c
                      :w--fit-content
                      :pb--0.25em)
              [button
               (merge-attrs
                (sx :.pill
                    :.minimal
                    :.neutral
                    :.xxxfast
                    :pi--1em
                    :pb--0.5em
                    :.neutral.minimal:c--$neutral-secondary-foreground
                    :.neutral.minimal:hover:c--black
                    :dark:.neutral.minimal:c--$neutral-secondary-foreground-inverse
                    :dark:.neutral.minimal:hover:c--white)
                {:data-kushi-playground-sidenav-button label}
                (d/mouse-down-a11y sidenav-item-handler {:component-label label}))
               label]]]))]])
