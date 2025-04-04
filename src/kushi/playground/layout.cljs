(ns ^:dev/always kushi.playground.layout
  (:require
   [clojure.string :as string]
   [domo.core :as domo]
   [kushi.core :refer [css defcss merge-attrs sx css-vars-map]]
   [kushi.css.media]
   [kushi.playground.about :as about]
   [kushi.playground.component-docs :as docs]
   [kushi.playground.component-examples :as component-examples]
   [kushi.playground.components :as kpg-components]
   [kushi.playground.sidenav :as sidenav]
   [kushi.playground.state :as state]
   [kushi.playground.md2hiccup :refer [desc->hiccup]]
   [kushi.ui.util :refer [keyed as-str]]
   [kushi.ui.button.core :refer [button]]
   [kushi.ui.core :refer [defcom]]
   [kushi.ui.divisor.core :refer (divisor)]
   [kushi.ui.spinner.core :refer [propeller]]
   [kushi.ui.slider.core :refer [slider]]
   [kushi.ui.link.core :refer [link]]
   [kushi.ui.icon.core :refer [icon]]
   [clojure.walk :as walk]))

(defcss "@layers design-tokens :root"
  {:--playground-main-content-max-width :605px})

(defn loading-spinner []
  [:div (sx
         :.flex-col-c
         :fs--$xxlarge
         ["has-ancestor(.path-transitioning):display"
          :flex]
         :position--fixed
         :pi--1.25rem
         :pi--4rem
         :h--50px
         :d--none
         :top--$navbar-height)
   [propeller (sx :translate--0.5em
                  [:--spinner-animation-duration :700ms])]])

(def tab-attrs
  (sx :fs--$small
      :pis--0.799em
      :pie--0.8em
      :pbs--0.4em
      :pbe--0.399em))

(defcom tab
  [button (merge-attrs 
           tab-attrs
           {:role        :tab
            :tab-index   (if (contains? #{true "true"}
                                        (:aria-selected &attrs))
                           0
                           -1)
            :on-key-down domo/on-key-down-tab-navigation}
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
        
        ;; Scroll to the top of the target panel
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
  [button
   (let [panel-id (str "kushi-" component-label "-" tab-label)]
     {:-surface      :minimal
      :-shape        :pill
      :class         (css :.foreground-color-secondary!
                          :fs--$small
                          :pis--0.799em
                          :pie--0.8em
                          :pbs--0.4em
                          :pbe--0.399em)
      :name          (str "kushi-" component-label "-tab-group")
      :aria-selected aria-selected
      :aria-controls panel-id
      :on-click      (partial tab-click-handler panel-id)
      :role          :tab
      :tab-index     (if (contains? #{true "true"} aria-selected) 0 -1)
      :on-key-down   domo/on-key-down-tab-navigation})
    (string/capitalize tab-label)])


;; TODO - put this in component
(defcss "@layer kushi-playground-shared .component-section-header"
  :.neutralize
  :.flex-col-fs
  :position--sticky
  :zi--1
  :>div:h--50%
  :top--$navbar-height
  :w--100%
  :h--100px
  :c--$foreground-color
  :bgc--$background-color
  ["has-ancestor(.kpg-generic-section-splash):bgc" :transparent])


(defn style-tag-active-path [path]
  [:style {:type "text/css"}
   (str "#app[data-kushi-playground-active-path=\"" path "\"]"
        " [data-kushi-path=\"" path "\"]{display: flex}")])

(defn splash-cta-button [s]
  [button (merge-attrs
           (sx :.xxsmall
               :.bold
               :transition-duration--$xxxfast
               [:translate "calc(0px - var(--button-padding-inline-ems))"]
               :dark:bgc--black!important
               [:dark:hover:bgc "var(--neutral-850)!important"])
           {:-shape        :pill
            :-surface      :minimal
            :-end-enhancer [icon :arrow-forward]})
   s])

(declare splash-isometric-grid-controls)

(defn generic-section
  ;; content is a component
  [{:keys [path active-route label content args]}]
  ;; change this logic to intro?
  (let [splash?       (= active-route "/")
        bgc           (when splash? "var(--neutral-50)")
        bgc-dark-mode (when splash? "black")]
    [:<> 
     [style-tag-active-path path]
     [:div 
      (merge-attrs
       (sx :.flex-col-fs
           :.grow
          ;;[:transition "all var(--transition-timing-function) var(--transition-duration), height 0s"]
           :gap--5rem
           :pbe--30vh
           :mbs--$navbar-height
           [:pbs "calc(var(--navbar-height) - 4px)"])
       (when-not (= path "components")
         (sx [:pbs "calc(var(--navbar-height) + 12px)"]))
       (when splash?
         (sx :.kpg-generic-section-splash
             :padding--0!important
             :padding-block--0!important
             :width--100vw
             :height--100vh
             :overflow--hidden
             :gap--0

             :scale--1
             :overflow--hidden

            ;; :scale--0.5
            ;; :overflow--visible
             ))
       {:data-kushi-path path
        :ref             (fn [el]
                           (when el
                             (js/requestAnimationFrame
                              #(domo/remove-class! (domo/el-by-id "app")
                                                   "path-transitioning"))))})
      [:section 
       {:style                         (css-vars-map bgc bgc-dark-mode)
        :class                         (css :min-height--200px
                                            :bgc--$bgc
                                            :dark:bgc--$bgc-dark-mode
                                            [">*:not([data-kushi-playground-sidenav]):pi" :1.25rem]
                                            ["md:>*:not([data-kushi-playground-sidenav]):pi" "4rem"]
                                            :>section>p:max-width--605px)
        :data-kushi-playground-section "about"}
       (if splash?
         [:div (sx #_:.component-section-header
                   :bgc--transparent
                   :dark:bgc--transparent
                   :position--absolute
                   :top--$navbar-height
                   :z-index--1)
          [:div (sx :.flex-col-c
                    ;; :mbs--48px 
                    ;; :sm:mbs--75px 
                    :h--auto!important
                    :>p:line-height--1.7
                    :>p:max-width--400px)
           [:h1 (sx :.component-section-header-label
                    :sm:fs--$xxlarge
                    :fs--$xlarge
                    :lh--1.2
                    :tt--revert)
            "A Readymade Foundation for UI"]
           [:p.prose (sx :sm:mb--1.6rem:1rem
                         :mb--0.6rem:2rem
                         :sm:fs--$medium
                         :fs--$small)
            [:span
             (sx :bgc--white
                 :dark:bgc--black
                 :dark:color--$neutral-300)
             "Kushi is a base for building web UI with "
             [link {:href   "https://clojurescript.org/"
                    :target :_blank}
              "ClojureScript"]]
            "."]
           [:div 
            (sx :.flex-col-fs
                :gap--1rem
                {"--stroke-color"      :$neutral-200
                 "dark:--stroke-color" :$neutral-900})
            [:a {:href "/components"}
             [splash-cta-button "Explore Kushi UI Components"]]
            [:a {:href "https://github.com/kushidesign/kushi" 
                 :target "_blank"}
             [splash-cta-button "View Documentation"]]
            [:a {:href "https://github.com/kushidesign/kushi-quickstart" 
                 :target "_blank"}
             [splash-cta-button "Quickstart"]]]]]

         [:div (sx #_:.component-section-header
                  ;; :d--none
                   :position--relative
                   :top--unset
                  ;; :mbs--$navbar-height
                   
                  ;; TODO - make sure this works - or move this
                   ["+*:pbs" :1.5rem]

                  ;; ["has-ancestor([data-kushi-playground-active-path='components']):d" :none]
                  ;; Maybe smaller division for mobile?
                  ;; ["~section[data-kushi-playground-component]:pbs" :6rem]
                   )
          [:div (sx :.flex-col-c #_:mbs--50px #_:h--50px)
           (when label
             [:h1 (sx :.component-section-header-label)
              (string/capitalize label)])]])
       (if args [content args] [content])]

     ;; gradients
      (when splash?
        [:div (sx :.absolute-fill
                  [:box-shadow "0 0 50vw 50vw rgba(0 0 0 / 0.5)"]
                  [:bgi "linear-gradient(164deg, var(--background) 275px, transparent 575px, transparent)"]
                  [:dark:bgi "linear-gradient(164deg, var(--background-color-dark-mode) 275px, transparent 575px, transparent)"]
                  [:sm:bgi "linear-gradient(to bottom right, white 420px, transparent 950px, transparent), linear-gradient(to bottom left, var(--background-color) 161px, transparent 450px, transparent), linear-gradient(to bottom, transparent, transparent 700px, var(--background-color) 1200px, var(--background-color))"]
                  [:sm:dark:bgi "linear-gradient(to bottom right, var(--background-color-dark-mode) 350px, transparent 850px, transparent), linear-gradient(to bottom left, var(--background-color-dark-mode) 161px, transparent 450px, transparent), linear-gradient(to bottom, transparent, transparent 700px, var(--background-color-dark-mode) 1200px, var(--background-color-dark-mode))"])])

     ;; sliders
      #_(when splash?
          [splash-isometric-grid-controls])]]))


(defn style-tag-first-intersecting [x]
  [:style {:type "text/css"}
   (str "#app[data-kushi-playground-first-intersecting=\"" x "\"]"
        " [data-kushi-playground-component=\"" x "\"]{opacity: 1; filter: none}")])


;; Everytime there is a resize event
;; Check if viewport height changes
;; If so, redo all the intersection observer stuff
(defn component-playground-content
  [playground-components]
  [:<> 
   [sidenav/all-components-sidenav playground-components]
   [sidenav/all-components-sidenav-mobile playground-components]

   ;; Cycle through collection of components defined in playground.core
   (into [:section] 
         (for [{:keys [label
                       media-matches
                       examples]
                :as   component-opts}
               playground-components]
           [:<> 
            ;; Injects a style tag to turn opacity to 1 when section is intersecting
            ;; Leave off for now
            #_[style-tag-first-intersecting label]
            [:section
             {:class  
              (css :min-height--200px
                   :pbs--4rem
                   :xsm:pbs--6rem
                   :first-child:pbs--0rem
                  ;;  :lg:first-child:pbs--5.2rem
                   
                   ;; De-emphasizing unfocused --------------------------------
                   ;; Leave off for now til you figure out intersection -------
                   ;;  :o--0.3
                   ;;  [:filter "blur(0px)"]
                   ;; ---------------------------------------------------------
                   )

              :data-kushi-playground-component 
              label

              :ref                             
              (fn [el]
                (when el
                  (domo/observe-intersection 
                   (let [f (partial swap!
                                    state/*playground
                                    update-in
                                    [:intersecting])]
                     {:element          el
                      :not-intersecting #(->> label
                                              (f disj)
                                              state/set-first-intersecting!)
                      :intersecting     #(->> label
                                              (f conj)
                                              state/set-first-intersecting!)

                          ;; Incorporate into global val for header height
                      :root-margin      "51px 0px 0px 0px"}))))}
             
             [:div (sx :.component-section-header
                       [:box-shadow
                        "-20px -20px 0px 20px var(--background-color), -10px 10px 20px 1px var(--background-color)"]
                       [:dark:box-shadow
                        "-20px -20px 0px 20px var(--background-color-dark-mode), -10px 10px 20px 1px var(--background-color-dark-mode)"] )
              [:div (sx :.flex-row-fs :ai--c :gap--1rem)
               [:h1 (sx :.component-section-header-label) 
                [:a 
                 {:class    (css :cursor--pointer)
                  :href     (str
                             "#"
                             label)
                  :on-click (fn [e]
                              (.preventDefault e)
                              (component-examples/scroll-to-playground-component!
                               {:component-label label
                                :scroll-y        16}))}
                 #_(trans (sx :.pointer 
                              {:href     (str "#" label)
                               :on-click (fn [e] 
                                           (.preventDefault e)
                                     ;; TODO - try a fast smooth transition here
                                           (component-examples/scroll-to-playground-component!
                                            {:component-label label
                                             :scroll-y        16}))})) 
                 label]]]

              ;; TODO - break this out into tabs component
              [:div 
               {:style            
                ;; TODO - move to global vars
                {"--tablist-selected-tab-underline-thickness" "2px"
                 "--tablist-padding-end"                      "0.5rem"}

                :class            
                (css
                 :.flex-row-fs
                 :.transition
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
                  {:bgc :$tablist-selected-tab-underline-color-dark-mode}]
                 [:--tablist-selected-tab-underline-color
                  :$accent-600]
                 [:--tablist-selected-tab-underline-color-dark-mode
                  :$accent-300]
                 [:--tablist-border-end-color
                  :$divisor-color]
                 [:--tablist-border-end-color-dark-mode
                  :$divisor-color-dark-mode]
                 [:--tablist-border-end-width
                  :$divisor-thickness]
                 [:--tablist-border-end-style
                  :$divisor-style]
                 :bbew--$tablist-border-end-width
                 :bbec--$tablist-border-end-color
                 :bbes--$tablist-border-end-style
                 :dark:bbec--$tablist-border-end-color-dark-mode
                 :ai--fe
                 :gap--0.75em
                 :pbe--$tablist-padding-end)

                :role
                :tablist

                :aria-orientation 
                "horizontal"}

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
                   [:p (sx :mbs--2rem :lh--1.7) message]
                   [component-section component-opts])))]]))])


(defcss ":root"
  {
   "--splash-col-1-y"            "1970px"
   "--splash-col-1-y-mobile"     "1770px"
   "--splash-col-2-y"            "-400px"
   "--splash-col-2-y-mobile"     "-800px"
   "--splash-col-3-y"            "1520px"
   "--splash-col-3-y-mobile"     "400px"
   "--splash-col-4-y"            "200px"
   "--splash-col-4-y-mobile"     "200px"
   "--splash-col-shift"          "730px"
   "--splash-col-shift-duration" "20s"}
  )

(defcss "@keyframes slide-col-1"
  [:0% {:transform "translate(0, calc(var(--splash-col-1-y) - var(--splash-col-shift)))"}]
  [:100% {:transform "translate(0, var(--splash-col-1-y))"}] )

(defcss "@keyframes slide-col-2"
  [:0% {:transform "translate(0, calc(var(--splash-col-2-y) + var(--splash-col-shift)))"}]
  [:100% {:transform "translate(0, var(--splash-col-2-y))"}] )

(defcss "@keyframes slide-col-3"
  [:0% {:transform "translate(0, calc(var(--splash-col-3-y) - var(--splash-col-shift)))"}]
  [:100% {:transform "translate(0, var(--splash-col-3-y))"}] )

(defcss "@keyframes slide-col-4"
  [:0% {:transform "translate(0, calc(var(--splash-col-4-y) + var(--splash-col-shift)))"}]
  [:100% {:transform "translate(0, var(--splash-col-4-y))"}] )


(defcss "@keyframes slide-col-1-mobile"
  [:0% {:transform "translate(0, calc(var(--splash-col-1-y-mobile) - var(--splash-col-shift)))"}]
  [:100% {:transform "translate(0, var(--splash-col-1-y-mobile))"}] )

(defcss "@keyframes slide-col-2-mobile"
  [:0% {:transform "translate(0, calc(var(--splash-col-2-y-mobile) + var(--splash-col-shift)))"}]
  [:100% {:transform "translate(0, var(--splash-col-2-y-mobile))"}] )

(defcss "@keyframes slide-col-3-mobile"
  [:0% {:transform "translate(0, calc(var(--splash-col-3-y-mobile) - var(--splash-col-shift)))"}]
  [:100% {:transform "translate(0, var(--splash-col-3-y-mobile))"}] )

(defcss "@keyframes slide-col-4-mobile"
  [:0% {:transform "translate(0, calc(var(--splash-col-4-y-mobile) + var(--splash-col-shift)))"}]
  [:100% {:transform "translate(0, var(--splash-col-4-y-mobile))"}] )


(declare component-playground-content-col)


;; What to show for splash image
(def col-filter-lists
  [[{:label "grid"}
    {:label "slider"
     :indices [3 4 7 9 10]}
    {:label "tooltip"
     :indices [2]}]

   [{:label "tag"}
    {:label "spinner"}
    {:label "icon"}
    {:label "card"
     :indices [0 1]}]

   [{:label "callout"}
    {:label "button"}
    {:label "collapse"}]

   [{:label "radio"
     :indices [1 2 3]}
    {:label "switch"}
    {:label "checkbox"}]])


(defn component-playground-content-3col 
  [playground-components]
  (into 
   [:div (sx :.kpg-splash-4col
             :.flex-row-fs
             :ai--fs
             :gap--2rem
             [:w "calc(3 * var(--playground-main-content-max-width))"]
             :transform-origin--center:center
             :transform-style--preserve-3d
             [:sm:transform "translate(-10%, -35.5%) scale(0.8) rotateX(55deg) rotateY(0deg) rotate(-45deg)"]
             [:transform "translate(-15.5%, -38.33%) scale(0.4) rotateX(55deg) rotateY(0deg) rotate(-45deg)"]
             [" .component-section-header:position" :static])]
   (for [n (range 4)]
     [component-playground-content-col 
      (mapv (fn [{:keys [label indices]}]
              ;; indices is for narrowing down examples
              (let [ret (first (filter (fn [m] (= label (:label m)))
                                       playground-components))]
                (if indices
                  (assoc ret
                         :examples
                         (into [] 
                               (keep (fn [i] (nth (:examples ret) i nil))
                                     indices)))
                  ret)))
            (nth col-filter-lists n))])))

(defn component-playground-content-col
  [playground-components]
   ;; Cycle through collection of components defined in playground.core
   (let [col-1 playground-components]
     (into [:section (merge-attrs 
                      (if (let [[p v] (some-> kushi.css.media/media :sm first)]
                            (domo/matches-media? p (as-str v))) 
                        (sx :.temp-3col-banner
                            ["nth-child(1):animation" "slide-col-1 ease-in-out var(--splash-col-shift-duration) 1"]
                            ["nth-child(2):animation" "slide-col-2 ease-in-out var(--splash-col-shift-duration) 1"]
                            ["nth-child(3):animation" "slide-col-3 ease-in-out var(--splash-col-shift-duration) 1"]
                            ["nth-child(4):animation" "slide-col-4 ease-in-out var(--splash-col-shift-duration) 1"]
                            ["nth-child(1):transform" "translateY(var(--splash-col-1-y))"]
                            ["nth-child(2):transform" "translateY(var(--splash-col-2-y))"]
                            ["nth-child(3):transform" "translateY(var(--splash-col-3-y))"]
                            ["nth-child(4):transform" "translateY(var(--splash-col-4-y))"]
                            :bgc--$background-color
                            :dark:bgc--$background-color-dark-mode
                            :pi--2.25rem!important
                            :min-width--$playground-main-content-max-width)
                        (sx :.temp-3col-banner
                            ["nth-child(1):animation" "slide-col-1-mobile ease-in-out var(--splash-col-shift-duration) 1"]
                            ["nth-child(2):animation" "slide-col-2-mobile ease-in-out var(--splash-col-shift-duration) 1"]
                            ["nth-child(3):animation" "slide-col-3-mobile ease-in-out var(--splash-col-shift-duration) 1"]
                            ["nth-child(4):animation" "slide-col-4-mobile ease-in-out var(--splash-col-shift-duration) 1"]
                            ["nth-child(1):transform" "translateY(var(--splash-col-1-y-mobile))"]
                            ["nth-child(2):transform" "translateY(var(--splash-col-2-y-mobile))"]
                            ["nth-child(3):transform" "translateY(var(--splash-col-3-y-mobile))"]
                            ["nth-child(4):transform" "translateY(var(--splash-col-4-y-mobile))"]
                            :bgc--$background-color
                            :dark:bgc--$background-color-dark-mode
                            :pi--2.25rem!important
                            :min-width--$playground-main-content-max-width)))] 
              (for [{:keys [label
                            media-matches
                            examples]
                     :as   component-opts}
                    col-1]
                [:<> 
            ;; Injects a style tag to turn opacity to 1 when section is intersecting
            ;; Leave off for now
                 #_[style-tag-first-intersecting label]
                 [:section
                  {:class  
                   (css :min-height--200px
                        :pbs--4rem
                        :xsm:pbs--6rem
                   ;;  :first-child:pbs--0rem
                        
                   ;; De-emphasizing unfocused --------------------------------
                   ;; Leave off for now til you figure out intersection -------
                   ;;  :o--0.3
                   ;;  [:filter "blur(0px)"]
                   ;; ---------------------------------------------------------
                        )

                   :data-kushi-playground-component 
                   label

                   :ref                             
                   (fn [el]
                     (when el
                       (domo/observe-intersection 
                        (let [f (partial swap!
                                         state/*playground
                                         update-in
                                         [:intersecting])]
                          {:element          el
                           :not-intersecting #(->> label
                                                   (f disj)
                                                   state/set-first-intersecting!)
                           :intersecting     #(->> label
                                                   (f conj)
                                                   state/set-first-intersecting!)

                          ;; Incorporate into global val for header height
                           :root-margin      "51px 0px 0px 0px"}))))}
                  
                  [:div (sx :.component-section-header)
                   [:div (sx :.flex-row-fs :ai--c :gap--1rem)
                    [:h1 (sx :.component-section-header-label) 
                     [:a 
                      {:class    (css :cursor--pointer)
                       :href     (str
                                  "#"
                                  label)
                       :on-click (fn [e]
                                   (.preventDefault e)
                                   (component-examples/scroll-to-playground-component!
                                    {:component-label label
                                     :scroll-y        16}))}
                      label]]]

                   ;; TODO - break this out into tabs component
                   [:div 
                    {:style            
                     ;; TODO - move to global vars
                     {"--tablist-selected-tab-underline-thickness" "2px"
                      "--tablist-padding-end"                      "0.5rem"}

                     :class            
                     (css
                      :.flex-row-fs
                      :.transition
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
                       {:bgc :$tablist-selected-tab-underline-color-dark-mode}]
                      [:--tablist-selected-tab-underline-color
                       :$accent-600]
                      [:--tablist-selected-tab-underline-color-dark-mode
                       :$accent-300]
                      [:--tablist-border-end-color
                       :$divisor-color]
                      [:--tablist-border-end-color-dark-mode
                       :$divisor-color-dark-mode]
                      [:--tablist-border-end-width
                       :$divisor-thickness]
                      [:--tablist-border-end-style
                       :$divisor-style]
                      :bbew--$tablist-border-end-width
                      :bbec--$tablist-border-end-color
                      :bbes--$tablist-border-end-style
                      :dark:bbec--$tablist-border-end-color-dark-mode
                      :ai--fe
                      :gap--0.75em
                      :pbe--$tablist-padding-end)

                     :role
                     :tablist

                     :aria-orientation 
                     "horizontal"}

                    [component-section-tab {:aria-selected   true
                                            :component-label label
                                            :tab-label       "examples"}]
                    [component-section-tab {:component-label label
                                            :tab-label       "documentation"}]]]

                  ;; For conditionally hiding based on device features
                  ;; e.g. Do not show tooltip examples on mobile/touch
                  (when (seq examples)
                    (let [{:keys [matches
                                  message]}
                          media-matches

                          unsupported?      
                          (when (and matches message)
                            (not (some (fn [[prop val]]
                                         (domo/matches-media? prop val))
                                       matches)))]
                      (if unsupported? 
                        [:p (sx :mbs--2rem :lh--1.7) message]
                        [component-section component-opts])))]]))))


(defn custom-attributes-section
  [custom-attributes]
  [:<>
   [:h2 (sx
         :fs--$large
         :fw--$semi-bold
         :mb--0:0.5rem)
    "Opts"]
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
                (sx
                 :.code
                 :fw--$bold
                 :pb--0.07em
                 :pi--0.2em
                 :fs--0.85rem)
                (str ":-" nm)]]
              [:div (sx :.flex-col-fs
                        :pb--0.5em
                        :gap--1.25em
                        :pis--1.4em
                        :overflow-x--auto)
               (when pred [docs/opt-detail "Pred" pred docs/kushi-opts-grid-type :pred])
               (when typ [docs/opt-detail "Type" typ docs/kushi-opts-grid-type :type])
               [docs/opt-detail "Defaults" default docs/kushi-opts-grid-default :default]
               (when desc [docs/opt-detail "Desc." desc docs/kushi-opts-grid-desc :desc])]])))])


(defn component-section
  [{:keys                     [examples label]
    {:keys [desc summary]
     custom-attributes :opts} :component-meta
    :as                       component-opts}]

  (into [:<>
         (into [:section
                {:class (css :.playground-component-panel)
                 :id    (str "kushi-" label "-examples")}]
               (for [
                     ;; example-opts (take 2 examples)
                     example-opts examples
                     ;;  example-opts (keep-indexed (fn [idx m] (when (contains? #{3} idx) m)) examples)
                     ]
                 [component-examples/examples-section
                  component-opts
                  example-opts]))
         [:div 
          {:class (css :.playground-component-panel
                       :>div:max-width--$playground-main-content-max-width
                       :pbs--35px)
           :hidden "hidden"
           :id    (str "kushi-" label "-documentation")}

          (when summary
            (into [:div.summary 
                   (sx :fs--$medium
                       :.summary>.prose:fw--$wee-bold
                       :mb--0:2rem
                       :>span:lh--1.7)]
                  (desc->hiccup summary)))

          (when desc
            [:<> 
             [:h2 
              (sx :fs--$large
                  :fw--$semi-bold
                  :pbe--0.5rem
                  :bbe--1px:solid:$gray-200
                  :dark:bbe--1px:solid:$gray-800
                  :mb--0:1.5rem)
              "Usage"]
             (into [:div (sx 
                          :.kushi-playground-component-usage
                          :lh--1.7
                          :mb--0:2rem
                          :_code:lh--1.9
                          :_code:pb--0.07em
                          :_code:pi--0.2em
                          :>span:d--block
                          [:_b {:fw      :$wee-bold
                                :mbe     :0.4em
                                :display :block}])]
                   (desc->hiccup desc))])

          (when (seq custom-attributes)
            [custom-attributes-section custom-attributes])]]))


;; TODO - set proper defaults
(defn splash-isometric-grid-controls []
  (let [col-translate-y (fn [n e]
                          (let [v  (domo/etv e)
                                el (domo/qs (str ".temp-3col-banner:nth-child(" n ")"))]
                            (domo/set-style! el
                                             "transform"
                                             (str "translateY(" v "px)"))))
        translate-xy (fn [k e]
                       (let [v     (domo/etv e)
                             el    (domo/qs ".kpg-splash-4col")
                             [x y] (string/split (domo/computed-style el
                                                                      "translate") 
                                                 #" ")]
                         (domo/set-style! el
                                          "translate" 
                                          (if (= k :x)
                                            (str v "% " y )
                                            (str x " " v "%")))))]
         [:div (sx :.flex-col-c
                   :.absolute
                   :bottom--0
                   :left--20px
                   :right--20px)

          [:div (sx :d--grid
                    :gap--1em
                    :height--100px
                    :gtc--1fr:1fr:1fr:1fr

                    [" input.kushi-slider-input[type=range]:w" "calc(100% - 3em)"])
           [slider {:min                           -50
                    :max                           50
                    :-current-value-label-position :track-inline-start
                    :id                            "4cols-translate-x"
                    :on-change                     (partial translate-xy :x)}]
           [slider {:min                           -45
                    :max                           -25
                    :step                          0.5
                    :-current-value-label-position :track-inline-start
                    :id                            "4cols-translate-y"
                    :on-change                     (partial translate-xy :y)}]
           [slider {:min                           0.5
                    :max                           1
                    :step                          0.05
                    :-current-value-label-position :track-inline-start
                    :id                            "4cols-scale"
                    :on-change                     (fn [e]
                                                     (let [v  (domo/etv e)
                                                           el (domo/qs ".kpg-generic-section-splash")]
                                                       (domo/set-style! el "scale" v)))}]]
          (into [:div (sx :.flex-row-fs
                          :height--100px
                          :gap--1em)]
                (for [n (range 1 5)]
                  [slider {:min                           -2500
                           :max                           2500
                           :step                          10
                           :-current-value-label-position :track-inline-start
                           :on-change                     (partial col-translate-y n)}]))])
  )
 



