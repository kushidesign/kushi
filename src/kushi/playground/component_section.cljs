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
   [kushi.ui.icon.mui.svg :refer (mui-icon-svgs)]
   [kushi.ui.snippet.core :refer (copy-to-clipboard-button)]
   [kushi.ui.label.core :refer (label)]
   [kushi.ui.dom :as dom]
   [kushi.ui.button.core :refer (button)]
   [kushi.playground.shared-styles :as shared-styles]
   [kushi.playground.state :as state]
   [kushi.playground.util :as util :refer (capitalize-words )]
   [kushi.playground.demobox :refer (demobox2)]))

(defn scroll-window-by-px []
  (dom/scroll-by {:y (:scroll-window-by-px shared-styles/shared-values)}))

(defn scroll-window-by-nav-height []
  (dom/scroll-by {:y (:scroll-to-component-menu-item-y shared-styles/shared-values)}))

(defn scroll-menu-item-into-view [el]
  (dom/scroll-into-view el)
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
  [m* refers]
  (let [text (util/require-snippet-text m* refers)]
    {:hiccup    [:div (sx :line-height--1.2rem)
                 (str "(ns your.namespace")
                 [:br]
                 [:span (sx :pis--1rem) (str "(:require " text "))")]]
     :text      text
     :full-text (str "(ns your.namespace\n  (:require " text "))")
     :form      (read-string (str "(ns your.namespace  (:require " text "))"))}))

 (defcom subsection
   (let [{:keys [title]} &opts]
     [:section
      (merge-attrs
       (sx
        'kushi-playground-subsection
        :pb--4.5em:1em
        :&_p:fs--$text-small
        :&_p:fw--$text-normal
        :&_p:lh--1.70
        :&_p&_code:pb--0.07em
        :&_p&_code:pi--0.2em
        :&_p&_code:fs--0.9em
        :&_.kushi-opt-detail-label:lh--2.05
        :&_.code.opt-type:bgc--transparent
        :&.description&_p:fs--$text-medium)
       &attrs)
      [:h3 (sx 'kushi-playground-subsection-title :.xlarge :.wee-bold :margin-block--0:1.25rem) title]
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
  [:div
   [:span
    (sx :.kushi-ui-opt-desc
        :.normal
        :&_p:m--0
        :&_p:fs--$text-medium
        )
    (let [ret* (cond
                 (string? v)
                 (->> v md->hc/md->hiccup md->hc/component)

                 (coll? v)
                 (some->> v util/desc->hiccup)

                 :else
                 [:span])]
      (add-links ret*))]])

(defn kushi-opts-grid-default [v m]
  (if (and (list? v) (= :text (first v)) (string? (second v)))
    [kushi-opts-grid-desc (second v) m]
    [:div
     [:span.code
      (str
       (cond (nil? v)
             "nil"
             (string? v)
             (str "\"" v "\"")
             :else
             v))]]))

(defn kushi-opts-grid-type [v]
  [:div
   (when v
     (cond
       (or (set? v) (vector? v))
       (into [:span] (map (fn [x] [:span.code (sx :mie--0.5em) (str x)]) v))
       (keyword? v)
       [:span.code (name v)]))])


(defn kushi-opts-grid-name [v]
  [:div
   (sx #_:.kushi-opts-grid-row-item)
   [:span.code (str ":-" v)]])

(defn kushi-opts-grid-items [maps]
  (reduce
   (fn [acc m]
     (concat acc
             (let [{nm :name typ :type default :default desc :desc} m]
               (when nm
                 [(kushi-opts-grid-name nm)
                  (kushi-opts-grid-type typ)
                  (kushi-opts-grid-default default m)
                  (kushi-opts-grid-desc desc m)]))))
   []
   maps) )


(defn opt-detail [text v f]
  [:div
   (sx :.flex-row-fs
       :pb--0.5em
       {:style {:ai (if (= text "Desc.") :flex-start :center)}})
   [:div
    (sx 'kushi-opt-detail-label :min-width--75px)
    [label (sx :.meta-desc-label :.normal)
     (if (= f kushi-opts-grid-type)
       (if (or (set? v) (vector? v))
         "Enum"
         text)
       text)]]
   [f v]])


(defn component-section
  [{m*            :meta
    desc          :desc
    refers        :refers
    section-title :title
    opts          :opts
    :as           m}]
  (let [{kushi-desc :desc
         kushi-opts :opts} (meta m*)
        opts                                 (or kushi-opts opts)
        fname                                (util/meta->fname m*)
        title                                (or section-title (-> fname (string/replace #"-" " ") capitalize-words))
        doc-hiccup                           (-> (or kushi-desc desc) util/desc->hiccup add-links)]
    ^{:key title}
    [collapse
     (sx
      :bbew--4px
      :bbes--solid
      :bbec--$gray100
      :dark:bbec--$gray700
      :&.kushi-collapse-expanded:bbec--black
      :dark:&.kushi-collapse-expanded:bbec--$gray50
      :transition--border-block-end-color:200ms:linear
      {
          ;; :ref            (fn [el]
          ;;                   (dom/observe-intersection
          ;;                    {:element          el
          ;;                     :intersecting     #(swap! state/*visible-sections assoc (.-id el) (dom/el-idx el))
          ;;                     :not-intersecting #(swap! state/*visible-sections dissoc (.-id el))
          ;;                     :f                #(let [focused-component-is-visible?  (contains? @state/*visible-sections @state/*focused-component)]
          ;;                                          (when (or (not @state/*focused-component)
          ;;                                                    (not focused-component-is-visible?))
          ;;                                            (state/set-focused-component! (ffirst (sort-by val < @state/*visible-sections)))))}))
       :id             fname
       :on-click       #(state/nav! fname)
       :-label         title
       :-icon          [icon {:-icon-svg (get mui-icon-svgs "add")}]
       :-icon-expanded [icon :remove]
       :-icon-position :end
       :-expanded?     (:components-expanded? @state/*state)
       :-header-attrs  (sx
                        :.xxlarge
                        :.wee-bold
                        :pb--$vp-top-header-padding
                        :hover:bgc--$gray50
                        :dark:hover:bgc--$gray800
                        ["&[aria-expanded='true']:hover:bgc" :transparent])})
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
             {:data-kushi-ui :snippet})
         (let [{:keys [full-text text]} (require-snippet m* refers)]
           [:<>
            [util/formatted-code full-text]
            [copy-to-clipboard-button
             (sx :.absolute
                 :inset-block-start--0
                 :inset-inline-end--0
                 {:on-click #(dom/copy-to-clipboard text)})]])]]

       [subsection
        (sx :.description {:-title [subsection-title title "Description"]})
        doc-hiccup]

       (when opts
         [subsection
          {:-title [subsection-title title "Opts"]}
          (into [:div]
                (for [{nm      :name
                       typ     :type
                       desc    :desc
                       default :default} (second opts)]
                  (when nm
                    [:div (sx
                           :.small
                           [:first-child:bbs "1px solid var(--gray200)"]
                           [:dark:first-child:bbs "1px solid var(--gray800)"]
                           [:bbe "1px solid var(--gray200)"]
                           [:dark:bbe "1px solid var(--gray800)"]
                           :pb--1em)
                     [:div (sx :mb--0.7rem)
                      [:span (sx :.code :.wee-bold) (str ":-" nm)]]
                     [:div (sx :pis--1.4em)
                      (when typ [opt-detail "Type" typ kushi-opts-grid-type])
                      [opt-detail "Default" default kushi-opts-grid-default]
                      (when desc [opt-detail "Desc." desc kushi-opts-grid-desc])]])))])]]]))
