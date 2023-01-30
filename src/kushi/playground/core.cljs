(ns kushi.playground.core
  (:require
   [malli.dev.pretty :as pretty]
   [malli.core :as malli]
   [garden.color]
   [kushi.core :refer [sx merge-attrs]]
   [kushi.color :refer [colors->tokens]]
   [kushi.colors :as kushi.colors]
   [kushi.ui.button.core :refer [button]]
   [kushi.ui.examples :as examples]
   [kushi.playground.nav :as nav]
   [kushi.playground.about :as about]
   [kushi.playground.state :as state]
   [kushi.playground.component-section :refer [component-section]]
   [kushi.playground.sidenav :refer [mobile-subnav desktop-sidenav hidden-desktop-sidenav]]
   [kushi.playground.ui :refer [light-dark-mode-switch]]
   [kushi.playground.colors :as colors]
   [kushi.playground.util :as util :refer-macros [keyed]]
   [kushi.playground.shared-styles :as shared-styles]))


(def Example
  [:map
   [:fn fn?]
   [:meta fn?]
   [:desc {:optional true} [:or vector? string?]]
   [:stage {:optional true} [:map [:style [:map [:min-height keyword?]]]]]
   [:controls {:optional true} [:vector keyword?]]
   [:content
    [:vector
     [:map
      [:example
       [:map
        [:evaled vector?]
        [:quoted vector?]]]]]]
   [:defaults {:optional true} [:map-of keyword? any?]]])

(defn validated-components [coll]
  (filter #(let [valid? (malli/validate Example %)]
             (when-not valid? (js/console.log (with-out-str (pretty/explain Example %))))
             valid?)
          coll))

(defn filter-by-index [coll idxs]
  (keep-indexed #(when ((set idxs) %1) %2)
                coll))

(defn component-name [m]
  (-> m :meta meta :name))

(defn component-by-index [coll sym]
  (let [m (first (filter #(= sym (component-name %)) coll))]
    (.indexOf coll m)))

(defn components-to-render
  ([coll]
   (components-to-render coll []))
  ([coll syms]
   (let [idxs (map (partial component-by-index coll) syms)
        ;; idxs [0]
         ret (cond-> coll
               (seq idxs) (filter-by-index idxs)
               true validated-components)]
     ret)))

(defn main-section [s & children]
  ^{:key s}
  [:div
   (sx 'kushi-main-section-wrapper
       :mbs--155px
       :md:mbs--0
       {:class [(str s "-wrapper")]})
   (into [:section
          {:id    s
           :class [s :kushi-main-section]}]
         children)
   [:section#kushi-docs (sx :min-height--1000px :flex-grow--0)]])

(def main-view-outer-wrapper-attrs
  (sx
   'main-view-outer-wrapper
   :ff--Inter|system|sans-serif
   :.wee-bold
   {:style {:$topnav-height                                      (str (:topnav-height shared-styles/shared-values) "px")
            :$divisor                                            "4px solid var(--gray100)"
            :$divisor-dark                                       "4px solid var(--gray700)"
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
            :$kushi-tooltip-placement-inline-offset              :3px
            "dark:&_.kushi-copy-to-clipboard-button-graphic:filter" '(invert 1)
            "dark:&_a.kushi-link:after:filter"                      '(invert 1)

            :&_a.kushi-link:td                                    :underline:1px:solid:currentColor
            :&_a.kushi-link:tuo                                   :-2px
            :&_.sidenav-primary&_a.kushi-link:td                  :none
            :&_a.kushi-link:d                                     :inline-flex
            :&_a.kushi-link:after:content                         "url(\"data:image/svg+xml,%3Csvg xmlns='http://www.w3.org/2000/svg' height='12px' viewBox='0 0 24 24' width='12px' fill='%23000000'%3E%3Cpath d='M0 0h24v24H0V0z' fill='none'/%3E%3Cpath d='M19 19H5V5h7V3H5c-1.11 0-2 .9-2 2v14c0 1.1.89 2 2 2h14c1.1 0 2-.9 2-2v-7h-2v7zM14 3v2h3.59l-9.83 9.83 1.41 1.41L19 6.41V10h2V3h-7z'/%3E%3C/svg%3E\")"
            :&_a.kushi-link:after:d                               :flex
            :&_a.kushi-link:after:flex-direction                   :column
            :&_a.kushi-link:after:jc                              :center
            :&_a.kushi-link:after:va                              :middle
            :&_a.kushi-link:after:mi                              :0.2em:0.25em}}) )


(defn desktop-lightswitch []
  [:div (sx 'kushi-light-dark-switch-desktop
            :d--none
            ["md:has-ancestor(.hide-lightswitch):d" :none]
            :md:d--block
            :position--fixed
            :inset-inline--auto:0.75rem
            :inset-block--1rem:auto)
   [light-dark-mode-switch]])


(def main-view-wrapper-attrs
  (sx :.flex-col-fs
      :md:flex-direction--row
      :md:jc--fs
      :lg:jc--c
      :min-height--1500px
      {:style {:md:pbs (str "calc(var(--topnav-height) + "
                            (:main-view-wrapper-padding-block-start shared-styles/shared-values)
                            "px)")}}))

(defn info-sections [style-class]
  (into [:div.flex-row-fs]
        (for [color-class [:neutral :accent :positive :negative :warning]]
          [:p.info (sx :p--1em :m--1em {:class [color-class style-class]}) "info section"])))

(defn button-gallery []
  (let [sem   [:neutral :positive :negative :accent :warning]
        kinds [:minimal :simple :bordered :filled ]]
    [:div.dark
     (into [:div (sx :.flex-col-c :ai--c)]
           (for [styling-class [nil :bordered :filled]]
             [info-sections styling-class]))
     [:div (sx :.flex-row-c
               :>div:first-child:mie--2em
               :>div:p--3em
               :>div:flex-grow--1
               :>div:flex-shrink--0
               :&_button:mb--0.5em)
      (into [:div.flex-row-sa (sx :max-width--1100px)]
            (for [kind kinds]
              (into [:div (sx :.flex-col-c :ai--c)]
                    (for [kw sem]
                      [button {:class [kw kind :medium]} "Hello"])))) ]]))

;; TODO - just get this from kushi.colors and mix with user-supplied?
(defn color-scales2
  [{:keys [colorlist]}]
  (let [tokens (colors->tokens kushi.colors/colors {:format :css})
        coll   (keep (fn [[k v]]
                       (let [color*       (->> k name (re-find #"^--([a-zAZ-_]+)([0-9]+)$"))
                             color-name   (some-> color* second)
                             color-level  (some-> color* last js/parseInt)
                             color-token? (contains? (into #{} colorlist) (keyword color-name))]
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
    ret))

;; TODO Make this work with anything user-supplied
(def colorlist [:gray :red :orange :yellow :green :blue :purple :magenta :brown])

(defn main-view
  [{:keys [
           main-view-attrs
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
           display-kushi-links-in-mobile-nav?
           ]
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
                              :sidenav-header "About"}}
    :as   m}]
  (let [
        m                   (merge m (keyed render
                                            mobile-nav
                                            kushi-colors
                                            kushi-user-guide
                                            kushi-clojars
                                            kushi-about))

        kushi-components    (merge kushi-components
                                   {:coll (components-to-render examples/components [])})
        global-color-scales (color-scales2 {:colorlist colorlist})
        nav-opts            (keyed
                             custom-components
                             kushi-components
                             custom-colors
                             kushi-colors
                             custom-typography
                             kushi-typography
                             kushi-user-guide
                             kushi-clojars
                             kushi-about)]

    [:div
     (merge-attrs main-view-outer-wrapper-attrs
                  (when hide-lightswitch? {:class [:hide-lightswitch]})
                  main-view-attrs)

     [desktop-lightswitch]

       ;; User Desktop nav - leave out for now
     #_[desktop-nav]

     ;; print focused section for deving
     #_(when true
         [:div
          (sx :.fixed
              :bottom--0
              :right--0)
          @state/*focused-section])

     [:div
      main-view-wrapper-attrs

      [mobile-nav (keyed site-header display-kushi-links-in-mobile-nav?)]

      [mobile-subnav nav-opts]

      [desktop-sidenav (keyed site-header nav-opts)]

      ;; TODO eliminate
      [hidden-desktop-sidenav {:opts (merge
                                      {:kushi-components-sidenav-header (:sidenav-header kushi-components)}
                                      (keyed kushi-components))}]


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
         [colors/color-rows global-color-scales]]

        :custom-typography
        [main-section
         "custom-typography"
         [about/intro-section {:-header (:header custom-typography)}]]

        :kushi-typography
        [main-section
         "kushi-typography"
         [about/intro-section
          {:-header (:header kushi-typography)}
          about/kushi-typography-about]]

        :kushi-about
        [main-section
         "kushi-about"
         [about/intro-section
          {:-header (:header kushi-about)}
          about/kushi-about]])]]))


