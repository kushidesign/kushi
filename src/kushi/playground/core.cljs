;; TODO - test Malli validation

(ns ^:dev/always kushi.playground.core
  (:require
   [garden.color]
   [kushi.core :refer [sx merge-attrs #_breakpoints]]
   [kushi.color :refer [colors->tokens]]
   [kushi.colors :as kushi.colors]
   [kushi.ui.examples :as examples]
   [kushi.playground.nav :as nav]
   [kushi.playground.about :as about]
   [kushi.playground.state :as state :refer [*state]]
   [kushi.playground.component-section :refer [component-section]]
   [kushi.playground.sidenav :refer [mobile-subnav desktop-sidenav]]
   [kushi.playground.ui :refer [light-dark-mode-switch]]
   [kushi.playground.colors :as playground.colors]
   [kushi.playground.shared-styles :as shared-styles]
   [kushi.playground.util
    :as util
    :refer-macros [keyed]]
   
  ;; leave in, comment out when tweaking typescale
  ;; [kushi.playground.tweak.typescale :refer [type-tweaker]]

  ;; ------------------------------------------------------
  ;; TODO figure out how to use as dev-only instrumentation 
  [malli.dev.pretty :as pretty]
  [malli.core :as malli]
  ;; ------------------------------------------------------

   ))


;; --------------------------------------------------------------------------------
;; TODO figure how to instrument this for dev-only
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
  (let [m                  
        (merge m (keyed render
                        mobile-nav
                        kushi-colors
                        kushi-user-guide
                        kushi-clojars
                        kushi-about))

        kushi-components    
        (merge kushi-components
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

        global-color-scales
        (color-scales2 {:colorlist colorlist})

        nav-opts            
        (keyed
         custom-components
         kushi-components
         custom-colors
         kushi-colors
         custom-typography
         kushi-typography
         kushi-user-guide
         kushi-clojars
         kushi-about)

        page-wrapper-attrs-from-user
        page-wrapper-attrs]



    ;; Page layout -------------------------------------------------------------------------------

    [:div
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
         :md:jc--fe
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


