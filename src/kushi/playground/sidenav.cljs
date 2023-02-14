(ns kushi.playground.sidenav
 (:require
   [clojure.string :as string]
   [kushi.core :refer (sx merge-attrs)]
   [kushi.ui.core :refer (defcom)]
   [kushi.ui.dom :as dom]
   [kushi.playground.component-section :refer [collapse-all-component-sections
                                         collapse-all-handler
                                         scroll-menu-item-into-view]]
   [kushi.playground.state :as state]
   [kushi.playground.util :as util :refer-macros (keyed)]))

(defn nav-section-id->base-id [s]
  (string/replace s #"-nav-section$" ""))


(defn section-item-on-click [href fname e]
  (let [section            (dom/nearest-ancestor (dom/et e) ".kushi-treenav-section-level-1")
        section-id         (some->  section .-firstChild .-id)
        focused-section-id (keyword (nav-section-id->base-id section-id))
        menu*              (dom/el-by-id "kushi-components")
        custom-menu*       (dom/el-by-id "custom-components")
        ]

    #_(js/console.log (keyed section section-id focused-section-id menu*))

    (when  focused-section-id
      (when-not (state/section-focused? focused-section-id)
        (reset! state/*focused-section focused-section-id)))

    (case  section-id

      "getting-started-nav-section"
      (when href
        (let [el   (dom/el-by-id href)]
          (when el
            (scroll-menu-item-into-view el)
            (state/nav! href))))

      "custom-components-nav-section"
      (js/setTimeout
       #(let [menu (dom/el-by-id "custom-components")
              item (when (and menu fname)
                     (.querySelector menu
                                     (str "#" fname ">[role='button']")))]
          (when item
            (scroll-menu-item-into-view item)
            (let [section-is-expanded? (when item (= (.-ariaExpanded item) "true"))]
              (when-not section-is-expanded?
                (.click item)))
            (state/nav! fname)))
       (if custom-menu* 0 100))

      "kushi-components-nav-section"
      (js/setTimeout
       #(let [menu (dom/el-by-id "kushi-components")
              item (when (and menu fname)
                     (.querySelector menu
                                     (str "#" fname ">[role='button']")))]
          (when item
            (scroll-menu-item-into-view item)
            (let [section-is-expanded? (when item (= (.-ariaExpanded item) "true"))]
              (when-not section-is-expanded?
                (.click item)))
            (state/nav! fname)))
       (if menu* 0 100)))))

(defn sidenav-section-items [section-opts items]
  (into [:ul]
        (for [{:keys [fname label href focused?]} items
              :let [focused? (and (:section-focused? section-opts) focused?)
                    hashed-href (str "#" fname)]]
          [:li
           [:a (sx
                :.pointer
                :.xxfast!
                :.normal
                :c--black
                :dark:c--white
                [:hover:o (if focused? 1 0.5)]
                :d--block
                :p--9px:12px:9px:48px
                :border-left--3px:solid:transparent
                {:style    {:bgc           (if focused? "rgba(0, 0, 0, 0.07)" :transparent)
                            :dark:bgc      (if focused? :$gray800 :transparent)
                            :bisc          (if focused? :black :transparent)
                            :dark:bisc     (if focused? :white :transparent)}
                 :href     hashed-href
                 :on-click (partial section-item-on-click href fname)})
            label]])))



(defn sidenav-section-header
  [{:keys [section-focused? kw header href target sidenav-header]
    :as   m}]
  [:a (merge-attrs
       (when target {:target target})
       (sx :.kushi-treenav-section-level-1-header
           :.relative
           :.semi-bold
           (when target :.kushi-link)
           :w--fit-content
           {:id                              (when (keyword? kw) (str (name kw) "-nav-section"))
            :href                            (or href (when (keyword? kw) (str "#" (name kw))))
            :style                           {:hover:c                                           (when-not section-focused? :$gray500)
                                              :bgc                                               (when section-focused? :$gray0)
                                              :bisc                                              (if section-focused? "rgba(0, 0, 0, 0.2)" :transparent)
                                              :dark:bisc                                         (if section-focused? "rgba(255, 255, 255, 0.4)" :transparent)
                                              "has-ancestor(.mobile-subnav):fs"                  :$text-xsmall
                                              "has-ancestor(.mobile-subnav):tt"                  :capitalize
                                              "has-ancestor(.mobile-subnav):bgc"                 (if section-focused? :$gray100 :transparent)
                                              "dark:has-ancestor(.mobile-subnav):bgc"            (if section-focused? :$gray750 :transparent)
                                              "has-ancestor(.mobile-subnav):border-radius"       :9999px
                                              "has-ancestor(.mobile-subnav):border-inline-style" :solid
                                              "has-ancestor(.mobile-subnav):biw"                 :8px
                                              "has-ancestor(.mobile-subnav):bic"                 (if section-focused? :$gray100 :transparent)
                                              "dark:has-ancestor(.mobile-subnav):bic"            (if section-focused? :$gray750 :transparent)
                                              "has-ancestor(.mobile-subnav):tuo"                 :3px}
            :on-click                        #(when-not target
                                                (reset! state/*focused-section kw)
                                                (state/set-focused-component! nil)
                                                (dom/scroll-to-top))}))
   (or header
       [:span.kushi-treenav-section-header sidenav-header])])

(defn sidenav-section
  [{:keys [items kw] :as m}]
  ^{:key kw}
  (let [section-focused? (state/section-focused? kw)
        section-opts     (merge m (keyed section-focused?))]
    [:li (sx 'kushi-treenav-section-level-1
             :mbs--1em
             :>a:mbs--0.5em
             :first-child:>a:mbs--0em)
     [sidenav-section-header section-opts]
     [sidenav-section-items section-opts items]]))

(defn- sidenav-component-section-items
  [coll]
  (mapv (fn [{m*         :meta
              item-title :title}]
          (let [fname    (util/meta->fname m*)
                focused? (state/focused? fname)
                label    (string/capitalize (or item-title fname))]
            (keyed fname focused? label)))
        coll))

(defn- sidenav-component-section-opts
  [{:keys [coll kw title]}]
  {:header [:span.kushi-treenav-section-header
            (sx {:on-click (partial collapse-all-handler kw)})
            [:span (sx :mie--0.5em) title]
            ;; Leave this out for now
            ;; Maybe place next to sidenav focused sections?
            #_(when (and (state/section-focused? kw)
                       (seq @state/*expanded-sections))
              [collapse-all-component-sections])]
   :kw     kw
   :href   (str "#" (string/lower-case title))
   :items  (sidenav-component-section-items coll)})

(defn xxx [{:keys [render?] :as m} kw]
  (when render?
    [sidenav-section (merge m {:kw kw})]))

(defn xxxy [{:keys [render?] :as m} kw fallback-sidenav-header]
  (when render?
    [sidenav-section (sidenav-component-section-opts
                      (assoc m
                             :kw
                             kw
                             :title
                             (or (:sidenav-header m)
                                 fallback-sidenav-header)))]))

(defn sidenav-content
  [{:keys [custom-components
           kushi-components
           custom-colors
           kushi-colors
           custom-typography
           kushi-typography
           kushi-user-guide
           kushi-clojars
           kushi-about]}]
  (into
   [:ul
    (sx :pi--5px!important
        :md:pbe--50px!important)
    (xxxy custom-components :custom-components "Custom Components")
    (xxxy kushi-components :kushi-components "Base Kushi Components")
    (xxx custom-colors :custom-colors)
    (xxx kushi-colors :kushi-colors)
    (xxx custom-typography :custom-typography)
    (xxx kushi-typography :kushi-typography)
    (xxx kushi-user-guide :kushi-user-guide)
    (xxx kushi-clojars :kushi-clojars)
    (xxx kushi-about :kushi-about)]))


(defcom sidenav
  [:div.sidenav-wrapper
   (:wrapper-attrs &opts)
   [:nav
    (merge-attrs
     (sx 'sidenav-primary
         :.small
         :.fixed
         :.flex-col-fs
         :.wee-bold
         :&_ul:list-style-type--none
         :&_li:list-style-type--none
         :&_ul:p--0
         :&_li:p--0
         :&_ul:m--0
         :&_li:m--0
         :h--100vh

        ;; sidenav gradation
        ;;  [:bgi '(rgba 0 0 0 0.7)]
        ;;  [:bgi                      '(linear-gradient "to left" "var(--gray50)" :transparent)]
        ;;  ["has-ancestor(.dark):bgi" '(linear-gradient "to left" "hsl(0deg, 0%, 15%)" :transparent)]

         {:style {:d         :none
                  :md:d      :flex
                  :md:w      :$sidebar-width
                  :lg:w      :$sidebar-width-lg
                  :xl:w      :$sidebar-width
                  :lg:m      :0
                  :xl:m      :0:24px
                  :md:pis    :1.5rem
                  :md:ii     0
                  :max-width :$sidebar-width
                  :xl:iis    :unset
                  :xl:iie    "calc((var(--components-menu-width) + ((100vw - var(--components-menu-width)) / 2) + 44px))"}})
     &attrs)
    &children]])

(defn mobile-subnav
  [opts]
  [sidenav
   (sx 'mobile-subnav
       :d--flex
       :jc--c
       :position--relative
       :h--auto
       [:pis "calc(var(--page-padding-inline) - 12px)"]
       :pie--$page-padding-inline
       :max-width--100%

       :&_.styled-scrollbars:flex-shrink--1
       :&_.styled-scrollbars:flex-grow--0
       :&_.styled-scrollbars:overflow-y--auto
       [:&_.styled-scrollbars:max-height "calc(100vh - 114px)"]
       :&_.styled-scrollbars:max-height--unset
       :&_.styled-scrollbars:max-width--$components-menu-width
       :&_.styled-scrollbars>ul:d--flex
       :&_.styled-scrollbars>ul:flex-wrap--wrap
       :&_.styled-scrollbars>ul:pb--0.5em

       :&_.kushi-treenav-section-level-1>ul:d--none
       :&_.kushi-treenav-section-level-1>a:mb--0.25em
       :&_.kushi-treenav-section-level-1>span:mb--0.5em:0
       :&_.kushi-treenav-section-level-1>a:pi--0:0em
       :&_.kushi-treenav-section-level-1>a:mi--0:0.5em
       :&_.kushi-treenav-section-level-1>span:pi--0:1.5em
       :&_.collapse-all-control:d--none
       {:-wrapper-attrs (sx :.fixed
                            :md:d--none
                            :top--77px
                            :w--100%
                            :bgc--white
                            :dark:bgc--$gray1000
                            :zi--100
                            :bbe--1px:solid:black
                            :dark:bbe--1px:solid:white)})
   [:div (sx :.styled-scrollbars
             :flex-shrink--1
             :flex-grow--0
             :overflow-y--auto
             [:max-height "calc(100vh - 114px)"])
    [sidenav-content opts ]]])


(defn desktop-sidenav [{:keys [site-header nav-opts]}]
  [sidenav
   [:div
    (sx :.xlarge
        :.relative
        :d--none
        :md:d--block
        :w--100%
        :padding-block--$title-margin-block)
    (when site-header [site-header])]
   [:div (sx :.styled-scrollbars
             :flex-shrink--1
             :flex-grow--0
             :overflow-y--auto
             [:max-height "calc(100vh - 114px)"])
    [sidenav-content nav-opts]]])


(defn hidden-desktop-sidenav [{:keys [opts]}]
  [sidenav
   (sx :.hidden
       :position--unset
       :flex--0:0:auto
       :lg:d--none)
   [sidenav-content opts]])
