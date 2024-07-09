(ns ^:dev/always kushi.playground.layout
  (:require [domo.core :as domo]
            [clojure.string :as string]
            [kushi.ui.divisor.core :refer (divisor)]
            [kushi.core :refer [sx merge-attrs defclass]]
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
  [:div (sx :.xxlarge
            :.flex-col-c
            :position--fixed
            :pi--1.25rem
            :pi--4rem
            :h--50px
            :d--none
            ["has-ancestor(.path-transitioning):display" :flex]
            :top--$navbar-height)
   [propeller (sx :translate--0.5em
                  :$spinner-animation-duration--700ms)]])


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
  [tab (let [panel-id (str "kushi-" component-label "-" tab-label)]
         (sx :.pill
             :&.neutral.minimal:c--$neutral-secondary-foreground
             :dark:&.neutral.minimal:c--$neutral-secondary-foreground-inverse
             {:name          (str "kushi-" component-label "-tab-group")
              :aria-selected aria-selected
              :aria-controls panel-id
              :on-click      (partial tab-click-handler panel-id)}))
   (string/capitalize tab-label)])


(defclass component-section-header
  :.neutralize
  :.flex-col-fs
  :position--sticky
  :zi--1
  :h--100px
  :>div:h--50%
  [:top :$navbar-height]
  [:w :100%])


(defn style-tag-active-path [path]
  [:style {:type "text/css"}
   (str "#app[data-kushi-playground-active-path=\"" path "\"]"
        " [data-kushi-path=\"" path "\"]{display: flex}")])



(defn generic-section
  ;; content is a component
  [{:keys [path label content args]}]
  [:<> 
   [style-tag-active-path path]
   [:div (sx 
          :.transition
          :.flex-col-fs
          :.grow
          :d--none
          :gap--5rem
          ;; ["has-ancestor(.path-transitioning):opacity" 0]
          :pb--0:30vh
          {:data-kushi-path path
           :ref             (fn [el]
                              (when el
                                (js/requestAnimationFrame
                                 #(domo/remove-class! (domo/el-by-id "app")
                                                      "path-transitioning"))))})
    [:section 
     (sx :min-height--200px
         [">*:not([data-kushi-playground-sidenav])" {:pi  :1.25rem}]
         ["md:>*:not([data-kushi-playground-sidenav]):pi" "4rem"]
         :>section>p:max-width--605px
         ;; why this data-attr?
         {:data-kushi-playground-section "about"})
     [:div (sx :.component-section-header
              ;;  :d--none
               :position--relative
               :top--unset
               :mbs--$navbar-height
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
    #_{:ref (fn [el]
            (when (? el)
              (domo/observe-intersection 
               (let [f (partial swap!
                                state/*playground
                                assoc
                                :playground-intro-intersecting?)]
                 {:element          el
                  :not-intersecting #(f false)
                  :intersecting     #(f true)}))))}
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
             (sx :min-height--200px
                 :pbs--4rem
                 :xsm:pbs--6rem
                ;;  :first-child:pbs--0rem

                ;; De-emphasizing unfocused ---------------------------------------
                ;; Leave off for now til you figure out intersection --------------
                ;;  :o--0.3
                ;;  [:filter "blur(0px)"]
                ;; ---------------------------------------
                 
                 {:data-kushi-playground-component
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
                          :root-margin      "51px 0px 0px 0px"}))))})
             
             [:div (sx :.component-section-header
                       [:box-shadow "-20px -20px 0px 20px var(--background-color), -10px 10px 20px 1px var(--background-color)"]
                       [:dark:box-shadow "-20px -20px 0px 20px var(--background-color-inverse), -10px 10px 20px 1px var(--background-color-inverse)"] )
              [:div (sx :.flex-row-fs :ai--c :gap--1rem)
               [:h1 (sx :.component-section-header-label) 
                [:a (sx :.pointer 
                        {:href     (str "#" label)
                         :on-click (fn [e] 
                                     (.preventDefault e)
                                     ;; TODO - try a fast smooth transition here
                                     (component-examples/scroll-to-playground-component!
                                      {:component-label label
                                       :scroll-y        16}))}) 
                 label]]]
              ;; TODO - break this out into tabs component
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
                   [:p (sx :mbs--2rem :lh--1.7) message]
                   [component-section component-opts])))]]))])


(defn custom-attributes-section
  [custom-attributes]
  [:<>
   [:h2 (sx :.large :.semi-bold :mb--0:0.5rem) "Opts"]
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
                    {:id (str "kushi-" label "-examples")
                    ;;  :hidden "hidden"
                     })]
               (for [
                    ;; example-opts (take 2 examples)
                     example-opts examples
                    ;;  example-opts (keep-indexed (fn [idx m] (when (contains? #{3} idx) m)) examples)
                     ]
                 [component-examples/examples-section component-opts example-opts]))
         [:div (sx :.playground-component-panel
                   :>div:max-width--$main-content-max-width
                   :pbs--35px
                   {:hidden "hidden"
                    :id     (str "kushi-" label "-documentation")})
          (when summary
            [:div (sx :.medium :.wee-bold :mb--0:2rem :&_p:lh--1.7)
             (->> summary
                  util/desc->hiccup 
                  docs/add-links)])
          (when desc
            [:<> 
             [:h2 (sx :.large 
                      :.semi-bold
                      :pbe--0.5rem
                      :bbe--1px:solid:$gray-200
                      :dark:bbe--1px:solid:$gray-800
                      :mb--0:1.5rem)
              "Usage"]
             [:div (sx :lh--1.7
                       :mb--0:2rem
                       :&_code:lh--1.9
                       :&_code:pb--0.07em
                       :&_code:pi--0.2em
                       [:&_p&_b {:fw      :$wee-bold
                                 :mbe     :0.4em
                                 :display :block}])

              (-> desc
                  util/desc->hiccup 
                  docs/add-links)]])

          (when (seq custom-attributes)
            [custom-attributes-section custom-attributes])]]))



 



