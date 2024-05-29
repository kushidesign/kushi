;; TODO - test Malli validation
;; TODO - type-tweaker require commented out

(ns ^:dev/always kushi.playground.core
  (:require [garden.color]
            [kushi.color :refer [colors->tokens]]
            [kushi.colors :as kushi.colors]
            [kushi.core :refer [sx]]
            [kushi.playground.layout :as layout]
            [kushi.playground.nav :as nav]
            [kushi.playground.shared-styles :as shared-styles]
            [kushi.playground.state :as state :refer [*state]]
            [kushi.playground.ui :refer [light-dark-mode-switch]]
            [kushi.playground.util
             :as util
             :refer-macros [keyed]]
            [kushi.ui.alert.core :refer [alert]]
            [kushi.ui.alert.demo :as alert.demo]
            [kushi.ui.button.core :refer [button]]
            [kushi.ui.button.demo :as button.demo]
            [kushi.ui.card.core :refer [card]]
            [kushi.ui.card.demo :as card.demo]
            [kushi.ui.collapse.core :refer [accordion collapse]]
            [kushi.ui.collapse.demo :as collapse.demo]
            [kushi.ui.examples :as examples]
            [kushi.ui.grid.core :refer [grid]]
            [kushi.ui.grid.demo :as grid.demo]
            [kushi.ui.icon.core :refer [icon]]
            [kushi.ui.icon.demo :as icon.demo]
            [kushi.ui.input.checkbox.core :refer [checkbox]]
            [kushi.ui.input.checkbox.demo :as checkbox.demo]
            [kushi.ui.input.radio.core :refer [radio]]
            [kushi.ui.input.radio.demo :as radio.demo]
            [kushi.ui.input.slider.core :refer [slider]]
            [kushi.ui.input.slider.demo :as slider.demo]
            [kushi.ui.input.switch.core :refer [switch]]
            [kushi.ui.input.switch.demo :as switch.demo]
            [kushi.ui.input.text.core :refer [input]]
            [kushi.ui.input.text.demo :as input.demo]
            [kushi.ui.modal.demo :as modal.demo]
            [kushi.ui.popover.demo :as popover.demo]
            [kushi.ui.progress.core :refer [progress]]
            [kushi.ui.progress.demo :as progress.demo]
            [kushi.ui.tag.core :refer [tag]]
            [kushi.ui.tag.demo :as tag.demo]
            [kushi.ui.toast.demo :as toast.demo]
            [kushi.ui.tooltip.demo :as tooltip-demo]
            [malli.core :as malli]
            [malli.dev.pretty :as pretty]))

(def Example
  [:map
   [:fn fn?]
   [:meta fn?]
   [:desc {:optional true} [:or vector? string?]]
   [:stage {:optional true} [:map [:style [:map [:min-height keyword?]]]]]
   [:variants {:optional true} [:vector keyword?]]
   [:examples
    [:vector
     [:map
      [:example
       [:map
        [:evaled vector?]
        [:quoted vector?]]]]]]
   [:defaults {:optional true} [:map-of keyword? any?]]])


;; Prod 
(defn validated-components [coll]
  coll)

;; Dev
;; (defn validated-components [coll]
;;   (filter #(let [valid? (malli/validate Example %)]
;;              (when-not valid? (js/console.log (with-out-str (pretty/explain Example %))))
;;              valid?)
;;           coll))
;; --------------------------------------------------------------------------------

(defn filter-by-index [coll idxs]
  (keep-indexed #(when ((set idxs) %1) %2)
                coll))

(defn validated-playground-examples
  [idxs coll]
  (cond-> coll
    (seq idxs) (filter-by-index idxs)
    true validated-components))

(defn component-name [m]
  (-> m :meta meta :name))

(defn component-by-index [coll sym]
  (let [m (first (filter #(= sym (component-name %)) coll))]
    (.indexOf coll m)))

(defn components-to-render
  ([coll]
   (components-to-render coll []))
  ([coll syms]
   (let [idxs* (:kushi-component-indexes @*state)
         idxs  (if (and (seq (:kushi-components-indexes @*state))
                        (every? int? idxs*))
                 idxs*
                 (map (partial component-by-index coll) syms))
         ;; idxs [0 1]
         ;; idxs [13 14]
         
         ;; This just keeps calling validated-playground-examples when switching
         ;; between components - fix this, for now only use during dev when you
         ;; are adding new examples.
        ;;  ret   (validated-playground-examples idxs coll)

         ret  coll]
     ret)))

(defn main-section [s & children]
  ^{:key s}
  [:div
   (sx 'kushi-playground-main-section
       [:mbs "calc(2 * var(--kushi-playground-mobile-header-height-fallback))"]
       :md:mbs--0
       {:class [(str s "-wrapper")]})
   (into [:section
          {:id    s
           :class [s]}]
         children)
   #_[:section#kushi-docs (sx :min-height--1000px :flex-grow--0)]])


(def kushi-playground-page-wrapper-attrs
  "Default styling class for kushi playground page wrapper, only child of #app div.
   This can be augmented by the user-provied :page-wrapper-attrs map."
  (sx
   'kushi-playground-page-wrapper-attrs
   :.flex-row-fs
   :ai--fs

   ;; :ff--Inter|system-ui|sans-serif
   ;; :.wee-bold
   ;; TODO wire most of these up into theming
   {:style {:$topnav-height                                      (str (:topnav-height shared-styles/shared-values) "px")
            :$divisor                                            "var(--kushi-playground-main-section-divisor, 4px solid var(--gray-100))"
            :$divisor-dark                                       "var(--kushi-playground-main-section-divisor-inverse, 4px solid var(--gray-700))"
            :$title-margin-block                                 :0.0em:3.5rem
            :$body-copy-line-height                              :1.5em
            :$sidebar-width                                      :225px
            :$sidebar-width-lg                                   :21vw
            :$components-menu-width                              :660px
            :$page-padding-inline                                :1.5rem
            :$vp-top-header-padding                              :0.7em
            :$vp-top-header-padding-with-offset                  (str "calc( var(--vp-top-header-padding) - "
                                                                      (:scroll-window-by-px shared-styles/shared-values)
                                                                      "px)")
            :$kushi-playground-sidenav-max-width                 :250px
            :$kushi-playground-mobile-header-height-fallback     :$kushi-playground-mobile-header-height||46px

            "dark:&_.kushi-copy-to-clipboard-button-graphic:filter" '(invert 1)
            "dark:&_a.kushi-link:after:filter"                      '(invert 1)

            :&_a.kushi-link:td                                    :underline:1px:solid:currentColor
            :&_a.kushi-link:tuo                                   :-2px
            :&_.sidenav-primary&_a.kushi-link:td                  :none
            :&_a.kushi-link:d                                     :inline-flex
            :&_a.kushi-link:after:content                         "url(\"data:image/svg+xml,%3Csvg xmlns='http://www.w3.org/2000/svg' height='12px' viewBox='0 0 24 24' width='12px' fill='%23000000'%3E%3Cpath d='M0 0h24v24H0V0z' fill='none'/%3E%3Cpath d='M19 19H5V5h7V3H5c-1.11 0-2 .9-2 2v14c0 1.1.89 2 2 2h14c1.1 0 2-.9 2-2v-7h-2v7zM14 3v2h3.59l-9.83 9.83 1.41 1.41L19 6.41V10h2V3h-7z'/%3E%3C/svg%3E\")"
            :&_a.kushi-link:after:d                               :flex
            :&_a.kushi-link:after:flex-direction                  :column
            :&_a.kushi-link:after:jc                              :center
            :&_a.kushi-link:after:va                              :middle
            :&_a.kushi-link:after:mi                              :0.2em:0.25em

            ;; For dev-mode isolation
            "&_.kushi-playground-dev-mode>section:not(.kushi-playground-dev-mode-target):display" :none
            
            }}))


(defn desktop-lightswitch []
  [:div (sx 'kushi-light-dark-switch-desktop
            :d--none
            ["md:has-ancestor(.hide-lightswitch):d" :none]
            :md:d--block
            :position--fixed
            :inset-inline--auto:0.75rem
            :inset-block--1rem:auto)
   [light-dark-mode-switch]])


(defn info-sections [style-class]
  (into [:div.flex-row-fs]
        (for [color-class [:neutral :accent :positive :negative :warning]]
          [:p.info (sx :p--1em :m--1em {:class [color-class style-class]}) "info section"])))


;; TODO - just get this from kushi.colors and mix with user-supplied?
(defn color-scales2
  [{:keys [colorlist]}]
  (let [tokens (colors->tokens kushi.colors/colors {:format :css})
        coll   (keep (fn [[k v]]
                       (let [color*       (or (->> k name (re-find #"^--([a-zAZ-_]+)-([0-9]+)$"))
                                              (->> k name (re-find #"^\$([a-zAZ-_]+)-([0-9]+)$")))
                             color-name   (some-> color* second)
                             color-level  (some-> color* last js/parseInt)
                             color-token? (contains? (into #{} colorlist) (keyword color-name))]
                         (name k) #_(keyed color*)
                         (when color-token?
                           {:color*      color*
                            :color-name  color-name
                            :color-level color-level
                            :value       v
                            :token       k})))
                     (partition 2 tokens))
        ret    (mapv #(let [scale (into []
                                        (keep (fn [{:keys [color-name token value color-level]}]
                                                (when (= color-name (name %))
                                                  [token value color-level]))
                                              coll))]
                        {:color-name %
                         :scale      scale})
                     colorlist)]
    (keyed coll ret)
    ret))




(defn main-view
  [{:keys [
           site-header
          ;; desktop-nav ; disable for now
           mobile-nav
           custom-components
           kushi-components
           custom-colors
           kushi-colors
           custom-typography
           kushi-typography
           kushi-user-guide
           kushi-clojars
           kushi-about
           render
          ;;  theme
           hide-lightswitch?
           use-low-x-type-scale?
           display-kushi-links-in-mobile-nav?
           colorlist
           page-wrapper-attrs]
    :or   {render            []
           mobile-nav        nav/kushi-mobile-nav
           custom-components nil
           kushi-components  {:render?        true
                              :header         "Base Kushi Components"
                              :sidenav-header "Base Kushi Components"}
           custom-colors     nil
           kushi-colors      {:render?        true
                              :header         "Base Kushi Colors"
                              :sidenav-header "Base Kushi Colors"}
           kushi-typography  {:render?        true
                              :header         "Base Kushi Typography"
                              :sidenav-header "Base Kushi Typography"}
           custom-typography nil
           kushi-user-guide  {:render?        true
                              :sidenav-header "User Guide"}
           kushi-clojars     {:render?        true
                              :sidenav-header "Clojars"}
           kushi-about       {:render?        true
                              :header         "About"
                              :sidenav-header "About"}
           colorlist         [:gray :red :orange :gold :yellow :green :blue :purple :magenta :brown]}
    :as   m}]
  (let [m                            (merge m (keyed render
                                                     mobile-nav
                                                     kushi-colors
                                                     kushi-user-guide
                                                     kushi-clojars
                                                     kushi-about))

        kushi-components             (merge kushi-components
                                            {:coll (let [coll  examples/components 
                                                         idxs* (:kushi-component-indexes @*state)
                                                         idxs  (if (and (seq (:kushi-components-indexes @*state))
                                                                        (every? int? idxs*))
                                                                 idxs*
                                                                 (map (partial component-by-index coll) []))

                            ;; This just keeps calling validated-playground-examples when switching
                            ;; between components - fix this, for now only use during dev when you
                            ;; are adding new examples.
                            ;; ret   (validated-playground-examples idxs coll)
                                                         ret   coll
                                                         ]
                                                     ret)})

        global-color-scales          (color-scales2 {:colorlist colorlist})

        nav-opts                     (keyed
                                      custom-components
                                      kushi-components
                                      custom-colors
                                      kushi-colors
                                      custom-typography
                                      kushi-typography
                                      kushi-user-guide
                                      kushi-clojars
                                      kushi-about)

        page-wrapper-attrs-from-user page-wrapper-attrs
        
        ;; ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
        

        ;; for surfaces:
        ;; Create classic variant
        ;; Create surface variant
        ;; Create belcher variant
        ;; Create fantasy variant
        ;; Create sci-fi variant
        ;; Create gel variant
        
        ;; Finish components last 3 components
        
        ;; Rename alert -> callout
        ;; finish input text examples
        ;; finish modal examples
        ;; finish popover examples
        ;; finish toast positioning
        ;; tag max-width example


        ;; tooltips(delay):
        ;; row of buttons that show all positions with no delay
        ;; row of buttons that show various delays 
        ;; row of buttons that show various animations 
        ;; row of buttons that show various stylings (no arrow etc)

        ;; TODO UI
        ;; use modal instead of popover
        ;; code button right of label
        ;; chose serif font for labels

        ;; TODO layout
        ;; sticky header
        ;; add cta to sticky header
        ;; hide sidebar on mobile


        ;; TODO 
        ;; Make sidenav work

        ;; TODO 
        ;; mobile layouts
        ;; ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
        _comps                       (filter 
                                      #(contains? #{
                                                    "progress"
                                                    ;; "grid"
                                                    ;; "accordian"
                                                    ;; "radio"
                                                    ;; "input"
                                                    ;; "collapse"
                                                    ;; "checkbox"
                                                    } (:label %))
                                      [
                                       {:label          "button"
                                        :demo-component button.demo/demo2
                                        :component      button
                                        :reqs           '[[kushi.ui.button.core :refer [button]]]
                                        :variants-base  #{:rounded :filled :bordered}
                                        :variants-order [:rounded :filled :bordered :minimal]
                                        :variants-attrs {:rounded  (sx :.rounded)
                                                         :filled   (sx :.rounded :.filled)
                                                         :bordered (sx :.rounded :.bordered)
                                                         :minimal  (sx :.rounded :.minimal)}
                                        }
                                       {:label          "switch" 
                                        :demo-component switch.demo/demo2
                                        :component      switch
                                        :reqs           '[[kushi.ui.input.switch.core :refer [switch]]]
                                        :variants-base  #{:on :off}
                                        :variants-order [:off :on]
                                        :variants-attrs {:on  {:-on? true}
                                                         :off {}}
                                        }
                                       {:label          "tooltip" 
                                        :demo-component tooltip-demo/demo2
                                        :component      :span
                                        :reqs           '[[kushi.ui.tooltip.core :refer [tooltip-attrs]] ]
                                        :variants-base  #{:positions}
                                        :variants-attrs {:positions {}}}

                                       {:label          "modal" 
                                        :demo-component modal.demo/demo
                                        :component      :span
                                        :reqs           '[[kushi.ui.modal.core :refer [modal
                                                                                       modal-close-button
                                                                                       open-kushi-modal
                                                                                       close-kushi-modal]] ]
                                        :variants-base  #{:positions}
                                        :variants-attrs {:positions {}}}
                                       
                                       {:label          "popover" 
                                        :demo-component popover.demo/demo
                                        :component      :span
                                        :reqs           '[[kushi.ui.popover.core :refer [popover-attrs dismiss-popover!]]]
                                        :variants-base  #{:positions}
                                        :variants-attrs {:positions {}}}

                                       {:label          "toast" 
                                        :demo-component toast.demo/demo
                                        :component      :span
                                        :reqs           '[[kushi.ui.toast.core :refer [toast-attrs dismiss-toast!]]]
                                        :variants-base  #{:positions}
                                        :variants-attrs {:positions {}}}

                                       {:label          "radio" 
                                        :demo-component radio.demo/demo
                                        :component      radio
                                        :reqs           '[[kushi.ui.input.radio.core :refer [radio]]]
                                        :variants-base  #{:positions}
                                        :variants-attrs {:positions {}}
                                        }

                                       {:label          "checkbox" 
                                        :demo-component checkbox.demo/demo
                                        :component      checkbox
                                        :reqs           '[[kushi.ui.input.radio.core :refer [radio]]]
                                        :variants-base  #{:positions}
                                        :variants-attrs {:positions {}}}

                                       {:label          "input" 
                                        :demo-component input.demo/demo
                                        :component      input
                                        :reqs           '[[kushi.ui.input.text.core :refer [input]]]
                                        :variants-base  #{:positions}
                                        :variants-attrs {:positions {}}}

                                       {:label          "tag"
                                        :demo-component tag.demo/demo2
                                        :component      tag
                                        :reqs           '[[kushi.ui.tag.core :refer [tag]]]
                                        :variants-base  #{:rounded :filled :bordered}
                                        :variants-order [:rounded :filled :bordered :minimal]
                                        :variants-attrs {:rounded  (sx :.rounded)
                                                         :filled   (sx :.rounded :.filled)
                                                         :bordered (sx :.rounded :.bordered)
                                                         :minimal  (sx :.rounded :.minimal)}}

                                       {:label          "card"
                                        :demo-component card.demo/demo
                                        :component      card
                                        :reqs           '[[kushi.ui.card.core :refer [card]]]
                                        :variants-base  #{:rounded}
                                        :variants-order [:rounded]
                                        :variants-attrs {:rounded (sx :.rounded)}
                                        }

                                       {:label          "slider"
                                        :demo-component slider.demo/demo
                                        :component      slider
                                        :reqs           '[[kushi.ui.input.slider.core :refer [slider]]]
                                        :variants-base  #{:on}
                                        :variants-order [:on]
                                        :variants-attrs {:on {}}}

                                       {:label          "alert"
                                        :demo-component alert.demo/demo
                                        :component      alert
                                        :reqs           '[[kushi.ui.alert.core :refer [alert]]]
                                        :variants-base  #{:default :filled :bordered}
                                        :variants-order [:default :filled :bordered]
                                        :variants-attrs {:default  {}
                                                         :filled   (sx :.filled)
                                                         :bordered (sx :.bordered)}}

                                       {:label          "icon"
                                        :demo-component icon.demo/demo
                                        :component      icon
                                        :reqs           '[[kushi.ui.icon.core :refer [icon]]]
                                        :variants-base  #{:outlined :filled}
                                        :variants-order [:outlined :filled]
                                        :variants-attrs {:filled   {:-icon-filled? true}
                                                         :outlined {}}}

                                       {:label          "collapse"
                                        :demo-component collapse.demo/demo
                                        :component      collapse
                                        :reqs           '[[kushi.ui.collapse.core :refer [collapse]]]}

                                       {:label          "accordian"
                                        :demo-component collapse.demo/accordion-demo
                                        :component      accordion
                                        :reqs           '[[kushi.ui.collapse.core :refer [accordion]]]}

                                       {:label          "grid"
                                        :demo-component grid.demo/demo
                                        :component      grid
                                        :reqs           '[[kushi.ui.grid.core :refer [grid]]]}

                                       {:label          "progress"
                                        :demo-component progress.demo/demo
                                        :component      progress
                                        :reqs           '[[kushi.ui.progress.core :refer [progress
                                                                                          spinner
                                                                                          propeller
                                                                                          thinking]]]}
                                       
                                       [kushi.ui.button.core :refer [button]]
                                       ;; dropdown
                                       ;; kbd
                                       ;; coming soon
                                       ;; avz
                                       ;; quote
                                       ;; code
                                       ;; select
                                       ;; skeleton
                                       ;; table
                                      ;; "progress bar"
                                       ;; tabs
                                       



                                       ])
        
        popover-content              (fn [] [:div.absolute-centered "hi"])]


#_[button
 (tooltip-attrs
  {:-text "My tooltip text" :-placement :right-top-corner})
 "Hover me to reveal tooltip"]

    ;; Page layout -------------------------------------------------------------------------------
    

    ;; loading-example 
    #_[:div.absolute-centered
       [button
        (sx {:-loading? true})
        [progress [icon :play-arrow] [propeller]] 
        "Play"]]
    

    ;; popover example
    #_[:div.absolute-centered
       [button
        (merge-attrs 
         (popover-attrs
          {:-f (fn [popover-el]
                 (rdom/render popover-content
                              popover-el))})
         (tooltip-attrs
          {:-text "My tooltip text"}))
        "click 4 deets"]]

    ;; radio example
    #_[:div.absolute-centered
       [:section
        [label (sx :.bold :mbe--0.75em) "Choose an option:"]
        [radio (sx {:-input-attrs {:name :demo}}) "Yes"]
        [radio (sx {:-input-attrs {:name :demo}}) "No"]
        [radio (sx {:-input-attrs {:name :demo}}) "Maybe"]]]

    ;; radio example
    #_[:div.absolute-centered [checkbox "wtf"]]
    
    ;; modal example
    #_[:div.absolute-centered
     [button
      {:on-mouse-down (fn* [] (open-kushi-modal "my-modal"))}
      "Click to open modal"]
     [modal
      (sx
       :min-width--450px
       :&_.kushi-modal-description:fs--$small
       {:-modal-title "Example modal"
        :-description "Example modal description goes here."
        :id           "my-modal"})
      [:div
       (sx :.flex-col-fs :gap--1em)
       [input (sx {:placeholder "Puffy"
                   :-label      "Screen name"})]
       [input (sx {:placeholder "Executive"
                   :-label      "Occupation"})]]
      [:div
       (sx :.flex-row-fe :gap--1em)
       [button {:on-mouse-down close-kushi-modal} "Cancel"]
       [button (sx :.filled) "Submit"]]]]


    [layout/layout _comps]

    #_[:div
       (merge-attrs kushi-playground-page-wrapper-attrs
                    (when hide-lightswitch? {:class [:hide-lightswitch :one-more-thing]})
                    page-wrapper-attrs-from-user)

     ;; Auxillary fixed controls
     ;; [type-tweaker]
       #_[:div.fixed-inline-end
          [:button {:on-click #(element-tweaker!)} [icon :tune]]]
       [desktop-lightswitch]


     ;; Mobile nav
       [mobile-nav (keyed site-header display-kushi-links-in-mobile-nav?)]
       [mobile-subnav nav-opts]

     ;; Sidenav
       [desktop-sidenav (keyed site-header nav-opts)]

     ;; Main Section
       [:div
        (let [md-pbs (str "calc(var(--topnav-height) + "
                          (:main-view-wrapper-padding-block-start shared-styles/shared-values)
                          "px)")]
          (sx
           'kushi-playground-main-section-wrapper
           :d--none!important
           :.flex-col-fs
           :.grow
           :.no-shrink
           :.fast
           :ai--c
           :&_p:ff--$kushi-playground-main-section-wrapper_font-family||$sans-serif-font-stack
           :&_p:fs--$kushi-playground-main-section-wrapper_font-size||$medium
           :fs--$kushi-playground-main-section-wrapper_font-size||$medium
           :transition-property--opacity
           :md:flex-direction--row
           :md:jc--fs
           :md:pie--05vw
           :lg:jc--c
           :lg:pis--4rem
           :pi--$page-padding-inline
           :pbe--5rem
           :w--100%
           [:md:pbs md-pbs]
           {:id    "#kushi-playground-main-section-wrapper"
            :style {:md:pbs (str "calc(var(--topnav-height) + "
                                 (:main-view-wrapper-padding-block-start shared-styles/shared-values)
                                 "px)")}}))

        (case @state/*focused-section

          :custom-components
          [apply main-section
           (into ["custom-components"
                  [about/component-playground-about
                   {:header (:header custom-components)}]]
                 (do
                   (swap! state/*state assoc :custom-components custom-components)
                   (for [m (:coll custom-components)]
                     [component-section m])))]


          :kushi-components
          [apply main-section
           (into ["kushi-components"
                  [about/component-playground-about
                   {:header (:header kushi-components)}]]
                 (do
                   (swap! state/*state assoc :components (:coll kushi-components))
                   (for [m (:coll kushi-components)]
                     [component-section m])))]

          :custom-colors
          [main-section
           "custom-colors"
           [about/intro-section {:-header (:header custom-colors)}]]

          :kushi-colors
          [main-section
           "kushi-colors"
           [about/intro-section {:-header (:header kushi-colors)}
            about/kushi-colors-about]
           [playground.colors/color-rows global-color-scales]]

          :custom-typography
          [main-section
           "custom-typography"
           [about/intro-section {:-header (:header custom-typography)}]]

          :kushi-typography
          [main-section
           "kushi-typography"
           [about/intro-section
            {:-header (:header kushi-typography)}
            [about/kushi-typography-about (keyed use-low-x-type-scale?)]]]

          :kushi-about
          [main-section
           "kushi-about"
           [about/intro-section
            {:-header (:header kushi-about)}
            about/kushi-about]])]

     ;; Placeholder for secondary nav, necessary for symmetrical layout on desktop
       [:div
        (sx 'kushi-playground-desktop-secondary-nav-wrapper
            :.kushi-playground-sidenav-wrapper
            :h--100vh)]]))


