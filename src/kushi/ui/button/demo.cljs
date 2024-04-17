(ns kushi.ui.button.demo
  (:require-macros
   [kushi.core :refer (sx keyed)])
  (:require
   [clojure.pprint :refer [pprint]]
   [clojure.string :as string]
   [clojure.walk :as walk]
   [kushi.core :refer (merge-attrs)]
   [kushi.ui.icon.core :refer [icon]]
   [kushi.ui.progress.core :refer (progress spinner propeller thinking)]
   [kushi.ui.tooltip.core :refer (tooltip-attrs)]
   [kushi.ui.popover.core :refer [popover-attrs dismiss-popover!]]
   [kushi.ui.button.core :refer (button)]
   [kushi.playground.demobox.core :refer (copy-to-clipboard-button)]
   [kushi.ui.icon.mui.svg :as mui.svg ]
   [goog.string :as gstr]
   [reagent.dom :as rdom]))


   

;; (defn- edo-panel-pp-nilable
;;   ([label x]
;;    (edo-panel-pp-nilable label x nil))
;;   ([label x f]
;;    [:div.edo-panel
;;     {:style {:opacity (when-not x 0.5)}}
;;     [:p label]
;;     (when x 
;;       [:span.pp
;;        {:style {:white-space :pre-line}}
;;        (string/replace 
;;         (with-out-str (pp/pprint x {:max-width 60}))
;;         #"\n"
;;         (str "\n " (gstr/unescapeEntities "&nbsp;")))])]))





(defn info-sections [style-class]
  (into [:div.flex-row-fs]
        (for [color-class [:neutral :accent :positive :negative :warning]]
          [:p.info (sx :p--1em :m--1em {:class [color-class style-class]}) "info section"])))

(defn- button-grid [shape minimal?]
  (let [sem    [:neutral :positive :negative :accent :warning]
        kinds  (if minimal?
                 [:minimal :bordered :simple :filled]
                 [:bordered :simple :filled])]
    [:div (sx :.flex-row-c
              :>div:flex-grow--1
              :>div:flex-shrink--0
              :&_button:mb--0.5em)
     (into [:div (sx :.flex-col-fs
                     :max-width--1100px)]
           (for [kind kinds]
             (let [kind-class (when-not (= :simple kind) kind)]
               (into [:div (sx :.flex-row-fs :gap--1em)]
                     (for [semantic sem]
                       [button
                        (merge-attrs
                         (sx :$tooltip-offset--5px
                             {:class [semantic kind-class :medium shape]})
                         (tooltip-attrs
                          {:-placement [:block-start :inline-start]
                           :-text      (map #(str ":." (name %))
                                            (remove nil?
                                                    [kind-class
                                                     semantic
                                                     (when (not= shape :sharp) shape)]))}))
                        "Hello"])))))]))


(defn demo []
  [:div
   [:p.pointer-only "Hover (non-touch devices) to reveal the Kushi utility classes used for styling."]
   [:div
    (sx :>div:pb--1em)
    [button-grid :sharp true]]])




;; New April 2024 ----------------------------------------------------------------------------------
(def type-weights
  [:thin       
   :extra-light
   :light      
   :normal     
   :wee-bold   
   :semi-bold  
   :bold       
   :extra-bold 
   :heavy])

(def button-colors
  ["neutral"
   "purple"
   "accent"
   "positive"
   "lime"
   "warning"
   "brown"
   "orange"
   "negative"
   "magenta"])

(def button-sizes
  [#_:xxxsmall
   :xxsmall
   :xsmall
   :small
   :medium
   :large
   :xlarge
   #_:xxlarge
   #_:xxxlarge] )

(defn component->sym [f]
  (-> f
      pprint
      with-out-str
      (string/split #"\$")
      last
      string/trim
      drop-last
      string/join
      symbol))

(defn formatted-code [s]
  [:pre
   [:code {:class :language-clojure
           :style {:white-space :pre
                   :line-height 1.5}}
    s]])

(defn snippet-section-header [header]
  [:h3 (sx 'snippet-section-header
           :.small
           :&_code:fs--$xsmall
           :&_.code:fs--$xsmall
           :&_.code:fw--$normal
           :mbe--0.75em)
   header])

(defn snippets
  [{:keys [args class component] :as m}]
  (let [sx-args        (concat '(sx) (keep #(some->> % name (str ".") keyword) class))
        component      (component->sym component)
        syms           (volatile! [component])
        args           (walk/postwalk (fn [v]
                                        (if (fn? v)
                                          (let [ret (component->sym v)]
                                            (vswap! syms conj ret)
                                            ret)
                                          v))
                                      args)
        distinct-refs  (seq (distinct @syms))
        code-snippet*  (str (into [component sx-args] args))
        code-snippet   (formatted-code
                        (with-out-str (pprint (into [component sx-args] args))))
        reqs-snippet** (some->> distinct-refs
                                (reduce (fn [acc v]
                                          (let [_ns    (get (:reqs-by-refers m) v)
                                                refers (or (get acc _ns) [])]
                                            (assoc acc _ns (conj refers v))))
                                        {})
                                (reduce-kv (fn [acc k v]
                                             (conj acc [k :refer v]))
                                           []))
        reqs-snippet*  (string/join "\n" reqs-snippet**)
        reqs-snippet   (map #(formatted-code (with-out-str (pprint %)))
                            reqs-snippet**)]
    (keyed code-snippet*
           code-snippet
           reqs-snippet*
           reqs-snippet)))


;; Abstract this into own ns
(defn component-details-popover [m]
  (fn [m]
    [:div
     (sx 'component-details-popover
         :.relative
         :.flex-row-fs
         :.small
         :ai--fs
         :pi--2.5em
         :pb--2.25em:2.75em
         :min-width--200px
         :min-height--120px)

     [:div
      (sx 'my-form
          :.flex-col-fs
          :gap--1em
          :&_.kushi-text-input-label:min-width--7em
          :&_.kushi-input-inline:gtc--36%:64%)
      (let [snippets              (snippets m)
            snippet-section-attrs (sx :.relative
                                      :.code
                                      :&_code:fs--$xsmall
                                      :&_.code:fs--$xsmall
                                      :p--1.5em
                                      :pie--4.5em
                                      :w--100%)]
        [:div (sx :.flex-col-fs :gap--1rem)
         [:section 
          [snippet-section-header 
           [:span
            (str "Paste into the ")
            [:span.code ":require"]
            " section of your "
            [:span.code :ns]
            " form:"]]
          (into [:section snippet-section-attrs
                 [copy-to-clipboard-button
                  (sx :.top-right-corner-inside!
                      {:-text-to-copy (:reqs-snippet* snippets)})]]
                (:reqs-snippet snippets))]

         [:section 
          [snippet-section-header
           "Component snippet:"]
          [:section snippet-section-attrs
           [copy-to-clipboard-button
            (sx :.top-right-corner-inside!
                {:-text-to-copy (:code-snippet* snippets)})]
           (:code-snippet snippets)]]])]

     #_[button
      (sx 'kushi-popover-close-button
          :.top-right-corner-inside
          :.neutral
          :.minimal
          :.small
          :.pill
          :zi--1
          [:$icon-button-padding-inline-ems :0.4em]
          [:opacity                         :$popover-close-button-opacity]
          [:$button-padding-block-ems       :$icon-button-padding-inline-ems]
          [:margin-inline                   :$popover-close-button-margin-inline||$icon-button-padding-inline-ems]
          [:margin-block                    :$popover-close-button-margin-block||$icon-button-padding-inline-ems]
          {:on-click dismiss-popover!})
      [icon mui.svg/close]]]))


(defn section-label-2 [s]
  [:p (sx :.small
          :c--$neutral-secondary-fg
          :min-width--55px
          :w--fit-content
          ;; :color--$magenta-500
          ;; :bgc--$magenta-50
          :mbe--0.5rem)
   [:span s]])



(defn example-row-variant
  [{:keys [component
           row-attrs
           extra-attrs
           variant-attrs
           examples]
    :as opts}]
  (into [:section (merge-attrs
                   (sx :.flex-row-fs
                       :gap--1rem
                       :pb--0.5rem)
                   row-attrs)]
        (for [{instance-args  :args
               instance-attrs :attrs
               :as            m} examples
              :let [merged-attrs* (merge-attrs variant-attrs
                                               extra-attrs
                                               instance-attrs
                                               opts
                                               m)
                    poa (popover-attrs {:class "dark"
                                        :-f    (fn [popover-el]
                                                 (rdom/render
                                                  [component-details-popover merged-attrs*]
                                                  popover-el))})
                    merged-attrs  (merge-attrs variant-attrs
                                               extra-attrs
                                               instance-attrs
                                               poa)]]
          (into [component merged-attrs] instance-args))))


(def variant-order-within-section
  [:rounded :filled :bordered :minimal])


(def variant-attrs
  {:rounded  (sx :.rounded)
   :filled   (sx :.rounded :.filled)
   :bordered (sx :.rounded :.bordered)
   :minimal  (sx :.rounded :.minimal)})

(def variant-attrs-base-ks
  #{:rounded :filled :bordered})

(defn example-section
  [{:keys [label variants+ variants-]
    :as   m}]
  (let [variant-attrs (as-> variant-attrs-base-ks $
                        (apply conj $ variants+)
                        (apply disj $ variants-)
                        (select-keys variant-attrs $)
                        (keep #(% $) variant-order-within-section))
        reqs-by-refers (some->> (mapv (fn [vc]
                                        (let [_ns (first vc)
                                              m   (apply hash-map (rest vc))
                                              ret (into {} (map (fn [v] [v _ns]) (:refer m)))]
                                          ret))
                                      (:reqs m))
                                seq
                                (apply merge))]
    (into [:section (sx :pb--1.5rem)
           label]
          (for [va variant-attrs]
            [example-row-variant (assoc m
                                        :variant-attrs
                                        va
                                        :reqs-by-refers
                                        reqs-by-refers)]))))


(declare example-sections)


(defn demo2 []
  (into [:<>]
        (for [m example-sections]
          [example-section m])))


;; TODO remove section-label-2
;; TODO hoist reqs up to a higher level

(def example-sections
  [{:title      "Sizes from xxxsmall to xxxlarge"
    :label      [section-label-2 [:span "Sizes from " [:span.code :xxxsmall] " ~ " [:span.code :xxxlarge]]]
    :component  button
    :row-attrs  (sx :ai--fe)
    :examples   (for [sz button-sizes]
                  {:label (name sz)
                   :attrs {:class sz}
                   :args  ["Play"]})}
   

   {:title       "With icons"
    :label       [section-label-2 "With icons"]
    :component   button
    :reqs        '[[kushi.ui.icon.core :refer [icon]]]
    :extra-attrs (sx :.small)
    :examples    [{:label "Icon button"
                   :args  [[icon :favorite]]}
                  {:label "Icon button"
                   :args  [[icon :star]]}
                  {:label "Icon button"
                   :args  [[icon :play-arrow]]}
                  {:label "Leading icon"
                   :args  [[icon :play-arrow] "Play"]}
                  {:label "Trailing icon"
                   :args  [[icon :auto-awesome]]}
                  {:label "2 icons"
                   :args  [[icon :auto-awesome] "Play" [icon :auto-awesome]]}]}


   {:title       "Loading and disabled states"
    :label       [section-label-2 "Loading and disabled states"]
    :component   button
    :reqs        '[[kushi.ui.button.core :refer [button]]
                   [kushi.ui.icon.core :refer [icon]]
                   [kushi.ui.progress.core :refer [progress spinner propeller thinking]]]
    :extra-attrs (sx :.small {:-loading? true})
    :examples    [{:label "Loading state, propeller"
                   :args  [[progress "Play" [propeller]]]}
                  {:label "Loading state, dots"
                   :args  [[progress "Play" [thinking]]]}
                  {:label "Loading state, spinner"
                   :args  [[progress "Play" [spinner]]]}
                  {:label "Loading state, spinner, fast"
                   :args  [[progress "Play" [spinner (sx :animation-duration--325ms)]]]}
                  {:label "Loading state, spinner on icon"
                   :args  [[progress [icon :play-arrow] [spinner]] "Play"]}
                  {:label "Loading state, propeller on icon"
                   :args  [[progress [icon :play-arrow] [spinner]] "Play"]}
                  {:label "Loading state, propeller on icon"
                   :attrs {:disabled true}
                   :args  [[progress [icon :play-arrow] [spinner]] "Play"]}
                  {:label "Loading state, propeller on icon"
                   :attrs {:disabled true}
                   :args  ["Play"]}]}


   {:title       "kind variants and colors"
    :label       [section-label-2 [:span "" [:span.code "kind"] " variants and colors"]]
    :component   button
    :extra-attrs (sx :.small)
    :variants+   [:minimal]
    :examples    (let [semantics #{"neutral" "accent" "positive" "warning" "negative"}]
                   (for [s button-colors]
                     {:label (name s)
                      :args  ["Play"]
                      :attrs (merge-attrs  {:class [s]}
                                           (when-not  (contains? semantics s)
                                             (sx [:color (str "var(--" s "-600)")]
                                                 [:background-color (str "var(--" s "-100)")])))}))}
   
   {:title       "weight variants"
    :label       [section-label-2 [:span "" [:span.code "weight"] " variants"]]
    :component   button
    :extra-attrs (sx :.small)
    :examples    (for [s (rest type-weights)]
                   {:label (name s)
                    :args  ["Play" [icon :auto-awesome]]
                    :attrs {:class [s]}})}
   {:title       "shape variants and colors"
    :label       [section-label-2 [:span "" [:span.code "shape"] " variants"]]
    :component   button
    :extra-attrs (sx :.small)
    :variants+   [:minimal]
    :examples    (for [s [:rounded :pill :sharp]]
                   {:label (name s)
                    :args  ["Play"]
                    :attrs {:class [s]}})}
   ])


#_(defn section-label
  "Renders a vertical label"
  [s]
  [:p (sx :.xxsmall
          :c--$neutral-secondary-fg
          :min-width--55px
          {:style {:writing-mode :vertical-lr
                   :text-orientation :upright
                   :text-transform :uppercase
                   :font-weight :800
                   :color :#7d7d7d
                   :font-family "JetBrains Mono"
                   :text-align :center
                   :background-image "linear-gradient(90deg, #e3e3e3, #e3e3d3 1px, transparent 1px)"
                   :background-position-x :1ch}})
   [:span (sx :bgc--white :pi--0.5em) s]])

