(ns ^:dev/always kushi.playground.layout
  (:require [domo.core :as domo]
            [clojure.string :as string]
            [fireworks.core :refer [? ?-- ?- ?trace ?log]]
            [kushi.core :refer [sx merge-attrs defclass]]
            [kushi.ui.link.core :refer [link]]
            [kushi.ui.core :refer [defcom]]
            [kushi.ui.modal.core :refer [modal open-kushi-modal close-kushi-modal]]
            [kushi.ui.button.core :refer [button]]
            [kushi.ui.icon.core :refer [icon]]
            [kushi.playground.about :as about]
            [kushi.playground.component-docs :as docs]
            [kushi.playground.component-examples :as component-examples]
            [kushi.playground.state :as state]
            [kushi.playground.ui :refer [light-dark-mode-switch]]
            [kushi.playground.util :as util]
            [kushi.ui.popover.core :refer [popover-attrs dismiss-popover!]]
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
                   [:hover>button.neutral.minimal:bgc (if focused? :$neutral-650 :$neutral-100)])
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
   [:span (sx :.flex-row-c :gap--0.5em :lg:&_.kushi-icon:d--none)
    [icon :menu]
    "Components"]])

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


(defcom header-nav-button
  [button 
   (let [focused? (:focused? &opts)]
     (merge-attrs 
      (sx :.xlarge
          :.minimal
          :.pill
          :.capitalize
          :pi--0.7em
          :pb--0.3em
          :&.neutral.minimal:c--$neutral-secondary-foreground
          :&.neutral.minimal:hover:c--$neutral-950
          :&.neutral.minimal:active:c--$neutral-1000
          :&.neutral.minimal:hover:bgc--$neutral-100
          :&.neutral.minimal:active:bgc--$neutral-0

          :dark:&.neutral.minimal:c--$neutral-secondary-foreground-inverse
          :dark:&.neutral.minimal:hover:c--$neutral-50
          :dark:&.neutral.minimal:active:c--$neutral-0
          :dark:&.neutral.minimal:hover:bgc--$neutral-850
          :dark:&.neutral.minimal:active:bgc--$neutral-900

          ["&.neutral.minimal[aria-selected='true']:c" :black]
          ["dark:&.neutral.minimal[aria-selected='true']:c" :white])
      &attrs))
   &children])


(defn header-menu []
  (into [:nav (sx :.flex-col-c
                  :.semi-bold
                  :ai--c
                  :lg:flex-direction--row
                  :gap--1.5rem
                  :lg:gap--2rem
                  :mbs--2rem
                  :lg:mbs--3rem
                  :.transition)]
         (for [label ["intro" "components" "colors" "typography" "guide"]
               :let [guide? (= label "guide")
                     href (if guide?
                            "https://github.com/kushidesign/kushi"
                            (str "/" label))
                     target (if guide? :_blank :_self)]]
           [:a {:href href :target target}
            [header-nav-button
             (sx [:translate (when guide? "0.66ch")]
                 {:aria-selected (= label @state/*focused-page)})
             label
             (when guide?
               [icon (sx :fs--0.75em) :open-in-new])]] )))


(defn header []
  [:div#header-navbar
   (sx 
    [:$overlay-width "calc(100vw + 40px)"]
    :.fixed
    :.flex-row-sb
    :.neutralize
    :.divisor-block-end
    :top--0
    :left--0
    :right--0
    :ai--c
    :zi--5
    :w--100%
    :p--1rem
    :max-height--$navbar-height
    :pi--1.25rem
    :md:pi--4rem )
   [:span (sx :.semi-bold :fs--$xlarge)
    "Kushi"]


   [:div.relative
    (sx :.relative
        :hover>div.explore-menu-container:h--500px
        :lg:hover>div.explore-menu-container:h--300px
        :hover&_nav:mbs--4rem
        :lg:hover&_nav:mbs--6rem
        :hover>div.explore-menu-container:o--1
        ["hover+div.bg-scrim-gradient:height" :100vh]
        ["hover+div.bg-scrim-gradient:o" 1]
        :zi--1
        :translate---30px)
    [button 
     (sx 'kushi-explore
         :.pill
         :.minimal
         :.small
         :pi--0.8em
         :pb--0.4em
         :&.neutral.minimal:c--$neutral-secondary-foreground
         :dark:&.neutral.minimal:c--$neutral-secondary-foreground-inverse)
     [icon :keyboard-arrow-down]
     "Explore"]
    [:div (sx 'explore-menu-container
              :.bottom-outside
              :.flex-col-fs
              :.transition
              :.neutralize
              :bgc--$background-color
              :o--0
              :w--$overlay-width
              [:transform "translateX(7px)"]
              :h--0
              :overflow--hidden
              
              ;; :h--200px
              ;; :mbs--6rem
              ;; :o--1
              ;; ["height" :100vh]
              ;; ["o" 1]
              )
     [header-menu]]]
   [:div (sx :.bg-scrim-gradient
             :.bottom-outside
             :.transition
             :w--$overlay-width
             :o--0
             :h--0)]

   
   [light-dark-mode-switch]])




(defn link-button [attrs label]
  [:button (merge-attrs 
            (sx :.minimal
                :.xsmall
                :.normal
                :.xxxfast
                :.pointer
                :lh--0.8em
                :p--0
                :c--$neutral-secondary-foreground
                :hover:c--$neutral-foreground
                :hover:bgc--transparent
                :hover:td--underline
                :hover:tds--underline
                :hover:tdt--1.5px
                :hover:tup--under
                :hover:tuo--0.075em
                :active:bgc--transparent
                [:tdc "color-mix(in oklch, currentColor 10%, transparent)"]
                [:hover:tdc "color-mix(in oklch, currentColor 40%, transparent)"])
            attrs)
   label])

(defcom tab
  [button (merge-attrs 
           (sx :.minimal
               :.small
               :pi--0.8em
               :pb--0.4em
               {:role        :tab
                :tab-index   (if (contains? #{true "true"}
                                            (:aria-selected &attrs))
                               0
                               -1)
                :on-key-down domo/on-key-down-tab-navigation})
           &attrs)
   &children])


(declare component-section)

(defn tab-click-handler
  [panel-id e]
  (let [el       (domo/et e)]
    (domo/toggle-boolean-attribute-sibling el "aria-selected")
    (domo/toggle-attribute-sibling el "tabindex" 0 -1)

                       ;; Switching the visibility of the panel
    (when-let [panel-el (domo/el-by-id panel-id) ]
      (when-let [parent (domo/parent panel-el)]
                           ;; Hide the currently active panel
        (some-> parent
                (domo/qs ".playground-component-panel:not([hidden])")
                (.setAttribute "hidden" "hidden"))
                           ;; Reveal the target panel
        (.removeAttribute panel-el "hidden")
        
                           ;;Scroll to the top of the target panel
        (domo/scroll-into-view! panel-el)
        (domo/scroll-by! (-> el
                             (domo/nearest-ancestor ".component-section-header")
                             domo/client-rect
                             :bottom
                             -
                             (->> (hash-map :y))))))))

(defn component-section-tab
  [{:keys [aria-selected
           component-label
           tab-label]}]
  [tab (let [panel-id (str "kushi-" component-label "-" tab-label)]
         (sx :.pill
             :&.neutral.minimal:c--$neutral-secondary-foreground
             :dark:&.neutral.minimal:c--$neutral-secondary-foreground-inverse
             {:name          (str "kushi-" component-label "-tab-group")
              :aria-selected aria-selected
              :aria-controls panel-id
              :on-click      (partial tab-click-handler panel-id)}))
   (string/capitalize tab-label)])


(declare kushi-about)

(defclass playground-section
  :min-height--200px
  :>*:pi--1.25rem
  :md:>*:pi--4rem)

(defclass component-section-header
  :.neutralize
  :.flex-col-fs
  :position--sticky
  :zi--1
  :h--100px
  :>div:h--50%
  [:top :$navbar-height]
  [:w :100%])

(defn generic-section
  [label content-component]
  [:div (sx :>section:first-child:pbs--$navbar-height
            :.flex-col-fs
            :.grow
            :gap--5rem
            :pb--0:30vh)
   [:section 
    (sx :min-height--200px
        :>*:pi--1.25rem
        :md:>*:pi--4rem
        :>section>p:max-width--605px
        :>section:pbs--1rem
        {:data-kushi-playground-section "about"})
    [:div (sx :.component-section-header
              :h--50px
              :>div:h--$navbar-height)
     [:div (sx :.flex-row-fs :ai--c :gap--1rem)
      [:h1 (sx :.component-section-header-label)
       label]]]
    [content-component]]])


;; Everytime there is a resize event -
;; Check if viewport height changes
;; If so, redo all the intersection observer stuff
(defn layout [_comps]

  [:div (sx :.flex-col-fs) [header]

   [all-components-sidenav _comps]

   ;; Uncomment to try what's inside
   #_[:div (sx :.debug-blue
               :.flex-col-fs
               :gap--6rem
               :mbs--100px
               :p--2rem)

      ;; Just for trying stuff out - paste here
      [:div "hi"]]

   ;; Main section
   ;; [generic-section "About" kushi-about]

   ;; Main section
  ;;  [generic-section "Typography" about/kushi-typography-about]
   #_[generic-section "Colors" about/kushi-colors-about]

   (into [:div
          (sx :.kushi-playground-all-components
              :>section:first-child:pbs--$navbar-height
              :.flex-col-fs
              :.grow
              :gap--5rem
              :pb--0:30vh)
          [:section
           (sx :.playground-section)
           [:div (sx :.component-section-header)
            [:div (sx :mbs--50px :h--50px)
             [:h1 (sx :.component-section-header-label)
              "Components Playground"]]]
           [:section
            (sx :>p:lh--1.7)
            [:p
             "Kushi is a base for building web UI with "
             [link (sx {:href   "https://clojurescript.org/"
                        :target :_blank}) "ClojureScript"] "."]
            [:br]
            [:p "For detailed docs, check out the "
             [link {:href   "https://github.com/kushidesign/kushi"
                    :target :_blank} "Readme"]
             " and the "
             [link {:href   "https://github.com/kushidesign/kushi-quickstart"
                    :target :_blank}
              "Quickstart repo"] "."]
            [:br]
            [:p
             "In addition to providing a css-in-cljs solution, Kushi offers "
             "a basic suite of themeable, headless UI components for free. "
             "This set of building blocks consitutes a base for rolling "
             "your own design system."]
            [:br]
            [:p
             (str "The components menu on this site provides interactive documentation, "
                  "detailed usage options, and snippet generation for easy inclusion of "
                  "Kushi UI components in your own project.")]]]]

         ;; Cycle through collection of components defined in playground.core
         (for [{:keys [label
                       media-matches
                       examples]
                :as   component-opts}
               _comps]
           [:section
            (sx :.playground-section
                {:data-kushi-playground-component label
                 :ref                             (fn [el]
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
                                                          :root-margin      "51px 0px 0px 0px"}))))})
            
            [:div (sx :.component-section-header)
             [:div (sx :.flex-row-fs :ai--c :gap--1rem)
              [:h1 (sx :.component-section-header-label) label]]
             [:div (sx 
                    :$tablist-selected-tab-underline-color--$accent-600
                    :$tablist-selected-tab-underline-color-inverse--$accent-300
                    :$tablist-selected-tab-underline-thickness--2px
                    :$tablist-border-end-color--$divisor-color
                    :$tablist-border-end-color-inverse--$divisor-color-inverse
                    :$tablist-border-end-width--$divisor-thickness
                    :$tablist-border-end-style--$divisor-style
                    :$tablist-padding-end--0.5rem
                    :.flex-row-fs
                    :.transition
                    :bbew--$tablist-border-end-width
                    :bbec--$tablist-border-end-color
                    :bbes--$tablist-border-end-style
                    :dark:bbec--$tablist-border-end-color-inverse
                    :ai--fe
                    :gap--0.75em
                    :pbe--$tablist-padding-end
                    ;; TODO - data-orientation
                    [">[role='tab'][aria-selected='true']:before"
                     {:box-sizing :border-box
                      :content    "\"\""
                      :h          :$tablist-selected-tab-underline-thickness
                      :position   :absolute
                      :bottom     "calc(0px - (var(--tablist-padding-end) + 1px))"
                      :left       0
                      :right      0
                      :bgc        :$tablist-selected-tab-underline-color}]
                    ["dark:>[role='tab'][aria-selected='true']:before"
                     {:bgc :$tablist-selected-tab-underline-color-inverse}]              
                    {:role             :tablist
                     :aria-orientation "horizontal"})

              [component-section-tab {:aria-selected   true
                                      :component-label label
                                      :tab-label       "examples"}]
              [component-section-tab {:component-label label
                                      :tab-label       "documentation"}]]]

            ;; For conditionally hiding based on device features
            ;; e.g. Do not show tooltip examples on mobile/touch
            (when (seq examples)
              (let [{:keys [matches
                            message]} media-matches
                    unsupported?              (when (and matches message)
                                                (not (some (fn [[prop val]]
                                                             (domo/matches-media? prop val))
                                                           matches)))]
                (if unsupported? 
                  [:p (sx :mbs--2rem) message]
                  [component-section component-opts])))]))])


(defn custom-attributes-section
  [custom-attributes]
  [:<>
   [:h2 (sx :.large :.semi-bold :mb--0:1.5rem) "Opts"]
   (into [:div]
         (for [{nm      :name
                typ     :type
                pred    :pred
                desc    :desc
                default :default} (second custom-attributes)]
           (when nm
             [:div (sx
                    :first-child:bbs--1px:solid:$gray-200
                    :dark:first-child:bbs--1px:solid:$gray-800
                    :bbe--1px:solid:$gray-200
                    :dark:bbe--1px:solid:$gray-800
                    :pb--1em)
              [:div (sx :mb--0.7rem)
               [:span
                (sx :.code
                    :.semi-bold
                    :pb--0.07em
                    :pi--0.2em
                    :fs--0.85rem

                    ;; TODO use neutralize utilities here
                    :c--$accent-750
                    :bgc--$accent-50
                    :dark:c--$accent-100
                    :dark:bgc--$accent-900)
                (str ":-" nm)]]
              [:div (sx :.flex-col-fs
                        :pb--0.5em
                        :gap--1.25em
                        :pis--1.4em)
               (when pred [docs/opt-detail "Pred" pred docs/kushi-opts-grid-type :pred])
               (when typ [docs/opt-detail "Type" typ docs/kushi-opts-grid-type :type])
               [docs/opt-detail "Default" default docs/kushi-opts-grid-default :default]
               (when desc [docs/opt-detail "Desc." desc docs/kushi-opts-grid-desc :desc])]])))])


(defn component-section
  [{:keys                     [examples label]
    {:keys [desc summary]
     custom-attributes :opts} :component-meta
    :as                       component-opts}]

  (into [:<>
         (into [:section
                (sx :.playground-component-panel
                    {:id (str "kushi-" label "-examples")})]
               (for [
                    ;; example-opts (take 2 examples)
                     example-opts examples
                    ;;  example-opts (keep-indexed (fn [idx m] (when (contains? #{3} idx) m)) examples)
                     ]
                 [component-examples/examples-section component-opts example-opts]))
         [:div (sx :.playground-component-panel
                   :>div:max-width--$main-content-max-width
                   :pbs--35px
                   {
                    :hidden "hidden"
                    :id     (str "kushi-" label "-documentation")})
          (when summary
            [:div (sx :.medium :.wee-bold :mb--0:2rem)
             (->> summary
                  util/desc->hiccup 
                  docs/add-links)])
          [:h2 (sx :.large :.semi-bold :mb--0:1.5rem) "Usage"]
          [:div (sx :lh--1.7
                    :mb--2rem
                    :&_code:lh--1.9
                    :&_code:pb--0.07em
                    :&_code:pi--0.2em
                    ;; :&_code:fs--0.85rem
                    ;; :&_code:fw--$wee-bold
                    ;; :&_code:color--$accent-850
                    ;; :&_code:bgc--$accent-50
                    ;; :dark:&_code:color--$accent-100
                    ;; :dark:&_code:bgc--$accent-900
                    [:&_p&_b {:fw      :$wee-bold
                              :mbe     :0.4em
                              :display :block}])

           ;; TODO why is this not converting underscores to bold
           ;; Contrast with component-section/component-section L277
           (some-> desc
                   util/desc->hiccup 
                   docs/add-links)]

          (when (seq custom-attributes)
            [custom-attributes-section custom-attributes])]]))



 (defn kushi-about []
  [:section
   (sx :>*:max-width--$main-content-max-width
       :>p:first-child:mbs--0
       :>p:mb--2em
       :>p:lh--1.7)
   [:p
    "Kushi is a base for building web UI with "
    [link (sx {:href   "https://clojurescript.org/"
               :target :_blank}) "ClojureScript"] "."]
   [:p "For detailed docs, check out the "
    [link {:href   "https://github.com/kushidesign/kushi"
           :target :_blank} "Readme"]
    " and the "
    [link {:href   "https://github.com/kushidesign/kushi-quickstart"
           :target :_blank}
     "Quickstart repo"] "."]
   [:p
    "In addition to providing a css-in-cljs solution, Kushi offers a basic suite of themeable, headless UI components for free. "
    "This set of building blocks consitutes a base for rolling your own design system."]
   [:p
    "The components menu on this site provides interactive documentation, detailed usage options, and snippet generation for easy inclusion of Kushi UI components in your own project."]])  



