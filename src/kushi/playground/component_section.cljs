(ns ^:dev-always kushi.playground.component-section
  (:require
   [clojure.edn :refer [read-string]]
   [clojure.walk :as walk]
   [markdown-to-hiccup.core :as md->hc]
   [applied-science.js-interop :as j]
   [clojure.string :as string]
   [kushi.core :refer (sx merge-attrs)]
   [kushi.ui.collapse.core :refer (collapse)]
   [kushi.ui.core :refer (defcom)]
   [kushi.ui.icon.core :refer (icon)]
   [kushi.ui.icon.mui.svg :as mui.svg]
   [kushi.ui.label.core :refer (label)]
   [domo.core :as domo]
   [kushi.ui.button.core :refer (button)]
   [kushi.playground.shared-styles :as shared-styles]
   [kushi.playground.state :as state :refer [*components-expanded?]]
   [kushi.playground.util :as util :refer (capitalize-words)]
   [kushi.playground.demobox.core :refer (demobox2 copy-to-clipboard-button)]))

(defn scroll-window-by-px []
  (domo/scroll-by! {:y (:scroll-window-by-px shared-styles/shared-values)}))

(defn scroll-window-by-nav-height []
  (domo/scroll-by! {:y (:scroll-to-component-menu-item-y shared-styles/shared-values)}))

(defn scroll-menu-item-into-view [el]
  (domo/scroll-into-view! el)
  (scroll-window-by-px)
  ;; Only use if you have a fixed top-nav
  #_(scroll-window-by-nav-height)
  )

(defn collapse-all-handler [id e]
  (when-let [menu  (js/document.getElementById "kushi-components")]
    (.forEach (.querySelectorAll menu "[aria-expanded='true'].kushi-collapse-header")
              (fn [node] () (.click node)))
    (scroll-menu-item-into-view menu)
    (j/call js/history :pushState  #js {} "" (str "#" "kushi-components"))
    (reset! state/*focused-section id)
    (reset! state/*focused-component nil)))

(defn collapse-all-component-sections
  ([]
   [collapse-all-component-sections nil])
  ([s]
   [:div (sx 'collapse-all-control :.flex-row-fs)
    ;style this ... display only on mobile at call site
    [button
     (sx :p--0
         :.minimal
         :hover:bgc--transparent!important
         :&>.kushi-label>span:td--none!important)
     [icon {:title "Collapse All Component Demos"} :compress]
     [:span (sx :.xxsmall :mie--0.5em :td--none) s]]]))

(defn require-snippet
  [m* refers requires]
  (let [text (util/require-snippet-text m* refers)]
    {:hiccup    [:div (sx :line-height--1.2rem)
                 (str "(ns your.namespace")
                 [:br]
                 [:span (sx :pis--1rem) (str "(:require " text "))")]]
     :text      text
     :full-text (str "(ns your.namespace\n  (:require " text ")" (when requires  (str "\n" requires)) ")")
     :form      (read-string (str "(ns your.namespace  (:require " text "))"))}))

(defcom subsection
  (let [{:keys [title]} &opts]
    [:section
     (merge-attrs
      (sx
       'kushi-playground-subsection
       :pb--4.5em:1em
       :&.description&_p:fs--$kushi-playground-main-section-wrapper_font-size||$medium
       [:&.description&_p&_b {:fw      :$wee-bold
                              :mbe     :0.4em
                              :display :block}]
       :&_p:fs--$kushi-playground-main-section-wrapper_font-size||$medium
       :&_p:ff--$kushi-playground-main-section-wrapper_font-family||$sans-serif-font-stack
       :&_p:fw--$kushi-playground-main-section-wrapper_font-weight||$normal
       :&_p:lh--$kushi-playground-main-section-wrapper_line-height||1.7
       [:&_p&_code {:pb  :0.07em
                    :pi  :0.2em
                    :fs  :0.85rem
                    :c   :$accent-750
                    :bgc :$accent-50}]
       [:dark:&_p&_code {:c   :$accent-100
                         :bgc :$accent-900}]
       :&_.kushi-opt-detail-label:lh--2.05
       :&_.code.opt-type:bgc--transparent)
      &attrs)
     [:h3 (sx 'kushi-playground-subsection-header
              :fs--$kushi-playground-main-section-subsection-header_font-size||$xlarge
              :fw--$kushi-playground-main-section-subsection-header_font-weight||$wee-bold
              :margin-block--0:1.25rem)
      title]
     &children]))


(defn subsection-title [title s]
  [:span s [:span (sx :o--0.55) [:span (sx :.xxsmall :pi--0.5em) "for"] title]] )


(defn maps->header-row-data [maps]
  (->> maps (map keys) (sort-by count) reverse first))

(defn add-links [coll]
  (walk/postwalk #(if (and (map? %) (contains? % :href))
                    (assoc % :target :_blank :class [:kushi-link] )
                    %)
                 coll))

(defn kushi-opts-grid-desc [v m]
  [:span
   (sx :.kushi-ui-opt-desc
       :.normal
       :&_p:m--0
       :&_p:fs--$medium
       )
   (let [ret* (cond
                (string? v)
                (->> v md->hc/md->hiccup md->hc/component)

                (coll? v)
                (some->> v util/desc->hiccup)

                :else
                [:span])]
     (add-links ret*))])

(defn kushi-opts-grid-default [v m]
  (if (and (list? v) (= :text (first v)) (string? (second v)))
    [kushi-opts-grid-desc (second v) m]
    [:span.code
     (str
      (cond (nil? v)
            "nil"
            (string? v)
            (str "\"" v "\"")
            :else
            v))]))

(defn kushi-opts-grid-type [v]
  (when v
    (cond
      (and (list? v) (= (first v) 'fn*))
      (let [anon-fn-display* (nth v 2)
            anon-fn-display (walk/postwalk #(if (re-find #"^p[0-9]+__[0-9]+\#$" (str %))
                                              (symbol "%")
                                              %)
                                           anon-fn-display*)]
        [:span.code (str "#" anon-fn-display)])
      (set? v)
      [:span.code (str v)]
      (symbol? v)
      [:span.code (name v)])))


(defn opt-detail [text v f kw]
  [:div
   (sx :.flex-row-fs
       :pb--0.5em
       {:style {:ai (if (= text "Desc.") :flex-start :center)}})
   [:div
    (sx 'kushi-opt-detail-label :min-width--75px)
    [label (sx :.kushi-playground-meta-desc-label :.normal) text]]
   [:div (sx 'kushi-opt-detail-value
             [:&_.code {:pb       :0.07em
                        :pi       :0.2em
                        :fs       :0.85rem
                        :c        :$accent-750
                        :bgc      :$accent-50
                        :dark:c   :$accent-100
                        :dark:bgc :$accent-900}])
    [f v]]])

(defn component-section-body
  [{:keys [fname m* m refers requires demo title demo-attrs doc-hiccup opts]}]
  ^{:key fname}
  [:section
   (sx :mbe--40px )
   [:section
    [demobox2 m]
    [:section (sx :padding-block--1rem)
     [:div
      (sx 'kushi-snippet
          :.relative
          :.codebox
          :&_pre:max-width--250px
          {:data-kushi-ui :snippet})
      (let [{:keys [full-text text]} (require-snippet m* refers requires)]
        [:<>
         [util/formatted-code full-text]
         [copy-to-clipboard-button
          (sx :.top-right-corner-inside! {:-text-to-copy text})]])]]

    [:div
     (sx :d--none :sm:d--block)
     (when demo
       [subsection
        (merge-attrs
         (sx :.description
             {:-title [subsection-title title "Examples"]})
         demo-attrs)
        [demo]])]

    [subsection
     (sx :.description
         {:-title [subsection-title title "Description"]})
     doc-hiccup]

    (when opts
      [subsection
       {:-title [subsection-title title "Opts"]}
       (into [:div]
             (for [{nm      :name
                    typ     :type
                    pred    :pred
                    desc    :desc
                    default :default} (second opts)]
               (when nm
                 [:div (sx
                        :.small
                        [:first-child:bbs "1px solid var(--gray-200)"]
                        [:dark:first-child:bbs "1px solid var(--gray-800)"]
                        [:bbe "1px solid var(--gray-200)"]
                        [:dark:bbe "1px solid var(--gray-800)"]
                        :pb--1em)
                  [:div (sx :mb--0.7rem)
                   [:span
                    (sx :.code
                        :.semi-bold
                        {:style {:pb       :0.07em
                                 :pi       :0.2em
                                 :fs       :0.85rem
                                 :c        :$accent-750
                                 :bgc      :$accent-50
                                 :dark:c   :$accent-100
                                 :dark:bgc :$accent-900}})
                    (str ":-" nm)]]
                  [:div (sx :pis--1.4em)
                   (when pred [opt-detail "Pred" pred kushi-opts-grid-type :pred])
                   (when typ [opt-detail "Type" typ kushi-opts-grid-type :type])
                   [opt-detail "Default" default kushi-opts-grid-default :default]
                   (when desc [opt-detail "Desc." desc kushi-opts-grid-desc :desc])]])))])]]
  )

(defn component-section
  [{m*         :meta
    demo       :demo
    demo-attrs :demo-attrs
    desc       :desc
    refers     :refers
    requires   :requires
    title      :title
    opts       :opts
    :as        m}]

  (let [fname                      (util/meta->fname m*)
        no-components-are-focused? (not @state/*focused-component)
        focused?                   (state/focused? fname)
        render-collapses?          @state/*md-or-smaller?
        header-attrs               (sx :fs--$kushi-playground-main-section-header_font-size||$xxlarge
                                       :.wee-bold
                                       :.hover-trailing-fade-out
                                       (when focused? :.no-hover-bgc)
                                       :pb--$vp-top-header-padding
                                       :hover:bgc--$gray-100
                                       :dark:hover:bgc--$gray-800
                                       ["&[aria-expanded='true']:hover:bgc" :transparent])
        ]
    (if render-collapses?
      (let [{kushi-desc :desc
             kushi-opts :opts} (meta m*)
            opts                                 (or kushi-opts opts)
            title                                (or title (-> fname (string/replace #"-" " ") capitalize-words))
            doc-hiccup                           (-> (or kushi-desc desc) util/desc->hiccup add-links)
            component-section-body-opts          (util/keyed fname
                                                             m*
                                                             m
                                                             refers
                                                             requires
                                                             title
                                                             demo
                                                             demo-attrs
                                                             doc-hiccup
                                                             opts)]
        ^{:key title}
        [collapse
         (sx
          :.hover-trailing-fade-out-wrapper
          :bbew--4px
          :bbes--solid
          :bbec--$gray-100
          :dark:bbec--$gray-700
          :&.kushi-collapse-expanded:bbec--black
          :dark:&.kushi-collapse-expanded:bbec--$gray-50
          :transition--border-block-end-color:200ms:linear
          {:id             fname
           :on-click       #(state/nav! fname)
           :-label         title
           :-icon          [icon mui.svg/add]
           :-icon-expanded [:span (sx :.flex-row-sb
                                      :gap--2rem)
                            [icon mui.svg/remove]]
           :-icon-position :end
           :-expanded?     (or @*components-expanded? (state/focused? fname))
           :-header-attrs  header-attrs})
         [component-section-body component-section-body-opts]])


      (when (or no-components-are-focused?
                focused?)
        (let [{kushi-desc :desc
               kushi-opts :opts} (meta m*)
              opts                                 (or kushi-opts opts)
              title                                (or title (-> fname (string/replace #"-" " ") capitalize-words))
              doc-hiccup                           (-> (or kushi-desc desc) util/desc->hiccup add-links)
              component-section-body-opts          (util/keyed fname
                                                               m*
                                                               m
                                                               refers
                                                               requires
                                                               title
                                                               demo
                                                               demo-attrs
                                                               doc-hiccup
                                                               opts)]
          [:div.hover-trailing-fade-out-wrapper
           [:header
            (merge-attrs
             (sx :.pointer
                 :bbe--$divisor
                 [:bbec      (if no-components-are-focused? :$neutral-100 :transparent)]
                 [:dark:bbec (if no-components-are-focused? :$neutral-750 :transparent)]
                 {:role      :button
                  :on-click  #(reset! state/*focused-component fname)})
             header-attrs
             (when focused? {:aria-expanded true}))
            title]
           (when focused?
             [component-section-body
              component-section-body-opts])])))))
