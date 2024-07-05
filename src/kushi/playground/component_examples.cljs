(ns ^:dev/always kushi.playground.component-examples
  (:require [clojure.string :as string] ;; [clojure.walk :as walk]
            [domo.core :as d]
            [kushi.core :refer (sx merge-attrs keyed)]
            [kushi.playground.component-docs :as docs]
            [kushi.playground.util :as util]
            [kushi.ui.button.core :refer (button)]
            [kushi.ui.core :refer (defcom)]
            [kushi.ui.divisor.core :refer (divisor)]
            [kushi.ui.icon.core :refer (icon)]
            [kushi.ui.tooltip.core :refer (tooltip-attrs)]
            [kushi.ui.icon.mui.svg :as mui.svg]
            [kushi.ui.modal.core :refer [close-kushi-modal modal
                                         modal-close-button open-kushi-modal]]
            [kushi.ui.util :refer [as-str maybe]]
            [me.flowthing.pp :refer [pprint]]))

(defn- example-row-variant
  [component
   {:keys                                    [row-attrs
                                              containier-attrs
                                              variant-attrs
                                              examples]
    {sx-attrs     :evaled
     quoted-attrs :quoted} :sx-attrs
    :as                                      example-opts}]
  (into [:section (merge-attrs
                   (sx 'playground-component-example-row-variant-section
                       :.flex-col-fs
                       :ai--fs
                       :md:ai--fe
                       :md:flex-direction--row
                       :gap--1rem)
                   row-attrs)]
        (for [{instance-args                  :args
               instance-attrs                 :attrs
               {instance-sx-attrs     :evaled
                instance-quoted-attrs :quoted} :sx-attrs
               {instance-code        :evaled
                instance-code-quoted :quoted}  :code
               :as          m}                examples
              :let [sx-attrs      (or instance-sx-attrs sx-attrs)
                    quoted-attrs  (or instance-quoted-attrs quoted-attrs)
                    merged-attrs* (merge-attrs variant-attrs
                                               sx-attrs
                                               quoted-attrs
                                               (when-not instance-sx-attrs
                                                 instance-attrs)
                                               (when instance-sx-attrs
                                                 {:instance-sx-attrs? true})
                                               example-opts
                                               m)
                    ;; poa           (popover-attrs
                    ;;                (merge-attrs
                    ;;                 {:class "dark"
                    ;;                  :-f    (fn [popover-el]
                    ;;                           (rdom/render
                    ;;                            [component-details-popover 
                    ;;                             component
                    ;;                             merged-attrs*
                    ;;                             quoted-attrs]
                    ;;                            popover-el))}))
                    merged-attrs  (merge-attrs variant-attrs
                                               sx-attrs
                                               instance-attrs
                                              ;;  poa
                                               )]]
          (if instance-code 
            (let [attrs (sx :.instance-code
                            :.flex-row-fs
                            :gap--1rem)]
              (if (and 
                   (seq? instance-code)
                   (every? vector? instance-code))
                (into [:div attrs] instance-code)
                [:div attrs
                 instance-code
            ;;  [button
            ;;   (merge-attrs
            ;;    (sx :.accent
            ;;        :.pill
            ;;        :.xxsmall
            ;;        :.bold) 
            ;;    poa
            ;;    (tooltip-attrs {:-text "Click to view code" :-placement :r}))
            ;;   [icon :code]]
                 ]))
            (into [component merged-attrs] instance-args)))))

(defn resolve-variants-attrs
  "This creates a list of `sx` attr maps for each variant
   Ex `sx` attr map for rounded & filled button variant
   => {:class [\"rounded\" \"filled\" nil]  :style {}}"
  [{:keys [variants-base
           variants-attrs
           variants-order]}
   {:keys [variants+ variants-]}]
  (let [a (when variants-base
            (as-> variants-base $
              (apply conj $ variants+)
              (apply disj $ variants-)
              (select-keys variants-attrs $)))
        b (if variants-order
            (keep #(% a) variants-order)
            a)
        ;; If example does not use a variants-base, give it a blank one
        c (or (maybe b seq)
              '({}))]
    c))

(defn- reqs-by-refers
  "This creates a map of syms / syms representing :requires by :refers
   Used to populate popover snipped
   Ex '{button kushi.ui.button.core
   icon   kushi.ui.icon.core}"
  [all-reqs]
  (some->> (mapv (fn [vc]
                   (let [_ns (first vc)
                         m   (apply hash-map (rest vc))
                         ret (into {} (map (fn [v] [v _ns]) (:refer m)))]
                     ret))
                 all-reqs)
           seq
           (apply merge)))

(defn section-label [s]
  [:p 
   (sx 'example-section-label
       :.serif
      ;; Include this if using cormorant serif face in :$serif-font-stack
      ;;  :.cormorant-section-label
      ;; Comment fs below if using cormorant serif face in :$serif-font-stack
      ;;  :fs--$small
       :fs--$small-b
       :.oblique
       :.neutralize-secondary
       :lh--1.7
       :&_span.code:mis--0.5ch)
   s])


#_(defn section-label-vertical
  "Renders a vertical label"
  [s]
  [:p (sx :.xxsmall
          :c--$neutral-secondary-foreground
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


(declare component-snippets)
(declare reqs-coll)


(defn example-modal
  [{:keys [modal-id
           label
           component-label
           component-reqs
           snippets-header
           snippets
           example-reqs]}]
  (let [all-reqs       (into [] (concat component-reqs example-reqs))
        reqs-by-refers (reqs-by-refers all-reqs)]
    [modal (sx :&_.kushi-modal-inner:pi--1.25em
               :xsm:&_.kushi-modal-inner:pi--3em
               :&_.kushi-modal-inner:pb--1.5rem:2em
               :xsm:&_.kushi-modal-inner:pb--3em:3.5em
               :$modal-min-width--200px
               :&_.kushi-modal-inner:gap--0.75rem
               [:height "min(var(--modal-max-height), calc(100vh - (2 * var(--modal-margin, 1rem))))"]
               :overflow--hidden
               :width--$main-content-max-width
               {:id modal-id})
     [modal-close-button {:-modal-id modal-id}]
     [:div (sx :.flex-row-sb :ai--fs :gap--1.5em)
      [:div
       (sx :.flex-col-fs :ai--b :gap--1rem )
       [:h1 (sx :.component-section-header-label) component-label]
       label]
      #_[button (sx :.extra-light
                  :.xxlarge 
                  :.minimal 
                  :.pill
                  :p--0
                  :translate--0:-10px
                  {:on-click close-kushi-modal})
       [icon :close]]]
     [divisor]
     [component-snippets
      (reqs-coll reqs-by-refers)
      snippets-header
      snippets]]))

(defn example-modal-trigger [modal-id]
  [button
   (sx :.minimal
       :.accent
       :.xxsmall
       :.wee-bold
       :.pill
       :pb--0.4em
       :&.accent.minimal:hover:background-color--$accent-50
       :dark:&.accent.minimal:hover:background-color--$accent-800

       ;; Next 3 styles will give it a link-button style
       #_:p--0
       #_:hover&.accent.minimal:bgc--transparent
       #_[:hover:after {:content  "\"\""
                        :position :absolute
                        :w        :100%
                        :h        :1px
                        :o        0.5
                        :bgc      :$accent-foreground
                        :top      "calc(100% + 2px)"}]
       {:on-click (fn* [] (open-kushi-modal modal-id))})
   [icon (sx :.small :.extra-bold) :code]
   "Code"])

(defn examples-section
  [{component       :component
    component-reqs  :reqs
    component-label :label
    :as             component-opts}
   {:keys             [container-attrs 
                       snippets-header
                       snippets]
    example-desc      :desc
    example-reqs      :reqs
    example-component :component
    :or          {example-reqs []}
    :as          example-opts}]
  (let [component      (or example-component component)
        label          (some-> example-desc
                               kushi.ui.util/backtics->hiccup
                               section-label)
        modal-id       (str component-label "__" example-desc)]
    [:div (sx :.playground-example-row-container
              :pb--2.5rem
              :first-of-type:pbs--2.5rem

              ;; Hack to conditionally hide things here,
              ;; like if they should not be shown on mobile
              ["has([data-kushi-playground-example='popover-with-form']):display" :none]
              ["xsm:has([data-kushi-playground-example='popover-with-form']):display" :block])

     [:section (sx :.playground-example-row
                  ;; make this max-width global var
                   :max-width--$main-content-max-width)
      [:div (sx :.flex-row-fs
                :flex-wrap--wrap
                :ai--c
                :mbe--1rem
                :gap--1rem)
       label
       (when snippets 
         [:<> 
          [example-modal-trigger modal-id]
          [example-modal (keyed modal-id
                                component-label
                                label
                                snippets-header
                                snippets
                                component-reqs
                                example-reqs)]])]

      
      (into [:div (merge-attrs
                   (sx :.grid :gtc--1fr :gap--1rem)
                   container-attrs)]
            (for [variant-attrs (resolve-variants-attrs component-opts
                                                        example-opts)]
              [example-row-variant
               component
               (merge example-opts
                      (keyed variant-attrs
                             reqs-by-refers))]))]]))


;;; New snippet code

(defn- reqs-coll
  "Returns something like this:
   '[[kushi.ui.button.core  :refer  [button]]
     [kushi.ui.icon.core  :refer  [icon]]]"
  [reqs-by-refers]
  (some->> reqs-by-refers
           keys
           distinct
           (reduce (fn [acc v]
                     (let [_ns    (get reqs-by-refers v)
                           refers (or (get acc _ns) [])]
                       (assoc acc _ns (conj refers v))))
                   {})
           (reduce-kv (fn [acc k v]
                        (conj acc [k :refer v]))
                      [])))

(defn- formatted-code [s]
  [:pre
   [:code {:class :language-clojure
           :style {:white-space :pre
                   :line-height 1.5}}
    s]])

(defcom copy-to-clipboard-button
  [button
   (merge-attrs
    (sx :.accent
        :.minimal
        :p--7px
        {:on-click #(d/copy-to-clipboard!
                     (or (some->> &opts 
                                  :clipboard-parent-sel
                                  (d/nearest-ancestor (d/et %)))
                         js/document.body) 
                     (:text-to-copy &opts))})
    (tooltip-attrs
     {:-text                        "Click to copy"
      :-text-on-click               "Copied!"
      :-text-on-click-tooltip-class (first (:class (sx :$tooltip-background-color--$accent-filled-background-color)))
      :-placement                   [:block-start :inline-end]})
    &attrs)
   [icon (sx :.medium!) mui.svg/content-copy]])


(defn- snippet-section
  [{:keys [header
           preformatted
           copyable]}]
  [:section (sx 'snippet-section
                :.flex-col-fs
                :gap--0.5em
                ;; :first-of-type:mbe--2.5em
                ) 
   header
   [:section 
    (sx :.relative
        :.code
        :.xsmall
        :xsm:p--1.5em
        :p--1.0em
        :pie--3.5em
        :xsm:pie--2.25em
        :w--100%)
    (when-let [attrs (some->> copyable
                              (hash-map :-text-to-copy)
                              (merge-attrs 
                               (sx :.top-right-corner-inside!)
                               {:-clipboard-parent-sel ".kushi-modal"}))]
      [copy-to-clipboard-button attrs])
    preformatted]])

(defn scroll-to-playground-component! [{:keys [component-label scroll-y]}]
  (let [el (d/qs-data= "kushi-playground-component" component-label)]
    (d/scroll-into-view! el)
    ;; This is dependent on the existance of `#header-navbar`
    (when scroll-y
      (d/raf
       #(d/scroll-by!
         {:y scroll-y})))))

(defn- scroll-to-elsewhere-on-page
  [{href :href}]
  (when (string/starts-with? href "#")
    {:target   :_self
     :on-click (fn [e]
                 (.preventDefault e)
                 (close-kushi-modal e)
                 (scroll-to-playground-component!
                  {:component-label (subs href 1)}))}))

(defn component-snippets
  []
  (fn [reqs-coll
       snippets-header
       snippets]
    [:div
     (sx :.relative
         :.flex-row-fs
         :&_code:fs--0.95em
         :&_.code:fs--0.95em
         :&_code:ws--n
         :&_.code:ws--n
         :&_pre&_code:p--0
         :&_pre&_.code:p--0
         :&_pre&_code:fs--$xsmall
         :&_pre&_.code:fs--$xsmall
         :lh--1.7
         :ai--fs
         :min-width--200px
         :min-height--120px)
     [:div
      (sx :.flex-col-fs
          :w--100%
          :gap--1em
          :&_.kushi-text-input-label:min-width--7em
          :&_.kushi-input-inline:gtc--36%:64%)
      (let [max-width   (or (when-let [[p v] (some-> (kushi.core/breakpoints) :sm first)]
                              (when-not (d/matches-media? p (as-str v))
                                27))
                            50)
            formatted*  #(-> % (pprint {:max-width max-width}) with-out-str)]
        (into [:div (sx :.flex-col-fs
                        :gap--2.25rem
                        :mbs--1.5em
                        :pbe--2rem)
               [snippet-section
                {:header       (util/desc->hiccup
                                ["Paste into the `:require` section of your `:ns` form:"])
                 :preformatted (-> reqs-coll
                                   formatted*
                                   (subs 1)
                                   (drop-last)
                                   string/join
                                   (string/replace #"\n \[kushi." "\n[kushi.")
                                   formatted-code)
                 :copyable     (string/join "\n" reqs-coll)}]

               [divisor]]

              (for [[i call] (map-indexed (fn [i call] [i call]) snippets)
                    :let [header (when (zero? i) 
                                   (some-> snippets-header
                                           util/desc->hiccup 
                                           (docs/add-links scroll-to-elsewhere-on-page)))]]
                [snippet-section
                 {:header       header
                  :preformatted (-> call
                                    formatted*
                                    string/join
                                    formatted-code)
                  :copyable     (let [[ob cb] (if (list? call) ["(" ")"] ["[" "]"])]
                                  (str ob
                                       (string/join "\n" 
                                                    (map #(if (string? %)
                                                            (str "\"" % "\"")
                                                            %)
                                                         call))
                                       cb))}])))]]))


(def type-weights
  [
  ;;  :thin       
   :extra-light
   :light      
   :normal     
   :wee-bold   
   :semi-bold  
   :bold       
   :extra-bold 
  ;;  :heavy
   ])

(def colors
  [
   "neutral"
  ;;  "purple"
   "accent"
   "positive"
  ;;  "lime"
   "warning"
  ;;  "brown"
  ;;  "orange"
   "negative"
  ;;  "magenta"
   ])

(def non-semantic-colors
  [
   "purple"
   "lime"
   "brown"
   "orange"
   "magenta"
   ])

(def sizes
  [#_:xxxsmall
   :xxsmall
   :xsmall
   :small
   :medium
   :large
   :xlarge
   #_:xxlarge
   #_:xxxlarge] )



(def sizes-snippet-header*
  ["Use the font-size utility classes `:.xxxsmall` ~ `:.xxxlarge` to control the size."
   "You can also use something like `:fs--96px` for specific sizes."
   :br
   :br])
        
(defn sizes-snippet-map [sym]
  {:snippets-header (conj sizes-snippet-header* "A few examples of different sizes:")
   :snippets        [[sym '(sx :.small)]
                     [sym '(sx :.large)]
                     [sym '(sx :.xxxlarge)]]})

(def sizes-snippet-scale-header
  (conj sizes-snippet-header* "Scale of different sizes:"))

(defn sizes-snippet-scale
  ([sym]
   (sizes-snippet-scale sym nil))
  ([sym arg]
   {:snippets-header sizes-snippet-scale-header
    :snippets        [(if arg
                        [:div
                         [sym arg]
                         [sym '(sx :.xxxsmall) arg]
                         [sym '(sx :.xxsmall) arg]
                         [sym '(sx :.xsmall) arg]
                         [sym '(sx :.small) arg]
                         [sym '(sx :.medium) arg]
                         [sym '(sx :.large) arg]
                         [sym '(sx :.xlarge) arg]
                         [sym '(sx :.xxlarge) arg]
                         [sym '(sx :.xxxlarge) arg]]
                        [:div
                         [sym]
                         [sym '(sx :.xxxsmall)]
                         [sym '(sx :.xxsmall)]
                         [sym '(sx :.xsmall)]
                         [sym '(sx :.small)]
                         [sym '(sx :.medium)]
                         [sym '(sx :.large)]
                         [sym '(sx :.xlarge)]
                         [sym '(sx :.xxlarge)]
                         [sym '(sx :.xxxlarge)]])]})
  )
