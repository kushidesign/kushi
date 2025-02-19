(ns ^:dev/always kushi.playground.layout
  (:require [domo.core :as domo]
            [clojure.string :as string]
            [kushi.ui.divisor.core :refer (divisor)]
            [kushi.core :refer [sx css merge-attrs defcss]]
            [kushi.ui.core :refer [defcom]]
            [kushi.ui.button.core :refer [button]]
            [kushi.ui.spinner.core :refer [propeller]]
            [kushi.playground.about :as about]
            [kushi.playground.component-docs :as docs]
            [kushi.playground.component-examples :as component-examples]
            [kushi.playground.sidenav :as sidenav]
            [kushi.playground.state :as state]
            [kushi.playground.util :as util]))

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


(defcss "@layer kushi-playground-shared .component-section-header"
  :.neutralize
  :.flex-col-fs
  :position--sticky
  :zi--1
  :h--100px
  :>div:h--50%
  :top--$navbar-height
  :w--100%
  :bgc--$background-color
  :c--$foreground-color)


(defn style-tag-active-path [path]
  [:style {:type "text/css"}
   (str "#app[data-kushi-playground-active-path=\"" path "\"]"
        " [data-kushi-path=\"" path "\"]{display: flex}")])


(defn generic-section
  ;; content is a component
  [{:keys [path label content args]}]
  [:<> 
   [style-tag-active-path path]
   [:div 
    {:class           (css
                       :.transition
                       :.flex-col-fs
                       :.grow
                       :d--none
                       :gap--5rem
                       :pb--0:30vh)
     :data-kushi-path path
     :ref             (fn [el]
                        (when el
                          (js/requestAnimationFrame
                           #(domo/remove-class! (domo/el-by-id "app")
                                                "path-transitioning"))))}
    
    [:section 
     {:class
      (css :min-height--200px
           [">*:not([data-kushi-playground-sidenav]):pi" :1.25rem]
           ["md:>*:not([data-kushi-playground-sidenav]):pi" "4rem"]
           :>section>p:max-width--605px)
      :data-kushi-playground-section
      "about"}
     [:div (sx :.component-section-header
              ;;  :d--none
               :position--relative
               :top--unset
               :mbs--$navbar-height
               ;; TODO - make sure this works
               ["+*:pbs" :1.5rem]
               ;; Maybe smaller division for mobile?
               ;; ["~section[data-kushi-playground-component]:pbs" :6rem]
               )
      [:div (sx :.flex-col-c :mbs--50px :h--50px)
       [:h1 (sx :.component-section-header-label)
        (string/capitalize label)]]]
     (if args
       [content args]
       [content])]]])



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
   [:div
    [about/component-playground-about]
    [:div [divisor]]]

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
                 :fw--$semi-bold
                 :pb--0.07em
                 :pi--0.2em
                 :fs--0.85rem
                 ;; TODO use neutralize utilities here
                 :c--$accent-750
                 :bgc--$accent-50
                 :dark:c--$accent-100
                 :dark:bgc--$accent-900)
                (str ":-" nm)]]
              [:div (sx
                     :.flex-col-fs
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
          {:class  (css :.playground-component-panel
                        :>div:max-width--$main-content-max-width
                        :pbs--35px)
           :hidden "hidden"
           :id     (str "kushi-" label "-documentation")}

          (when summary
            [:div 
             (sx :fs--$medium
                 :fw--$wee-bold
                 :mb--0:2rem
                 :_p:lh--1.7)
             (->> summary
                  util/desc->hiccup 
                  docs/add-links)])

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
             [:div (sx :lh--1.7
                       :mb--0:2rem
                       :_code:lh--1.9
                       :_code:pb--0.07em
                       :_code:pi--0.2em
                       [:_p_b {:fw      :$wee-bold
                               :mbe     :0.4em
                               :display :block}])

              (-> desc
                  util/desc->hiccup 
                  docs/add-links)]])

          (when (seq custom-attributes)
            [custom-attributes-section custom-attributes])]]))



 



