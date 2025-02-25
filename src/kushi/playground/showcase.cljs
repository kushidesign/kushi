(ns ^:dev/always kushi.playground.showcase
  (:require
   [clojure.walk :as walk]
   [clojure.repl]
   [fireworks.core :refer [!? ?]]
   [kushi.core :refer [css defcss ?defcss merge-attrs sx]]
   [kushi.css.build.design-tokens :as design-tokens]
   [kushi.playground.md2hiccup :refer [desc->hiccup]]
   [kushi.playground.ui :refer [light-dark-mode-switch]]
   [kushi.ui.tooltip.core :refer [tooltip-attrs]]
   [kushi.ui.icon.mui.svg :as mui.svg]
   [kushi.ui.button.core :refer [button]]
   [kushi.ui.divisor.core :refer [divisor]]
   [kushi.ui.icon.core :refer [icon]]
   [kushi.ui.spinner.core :refer [propeller]]
   [kushi.ui.util :refer [as-str maybe keyed]]
   [kushi.ui.modal.core :refer [modal open-kushi-modal modal-close-button]]
   [clojure.string :as string]
   [kushi.ui.button.core :refer [button icon-button]]
   [kushi.ui.icon.core :refer [icon]]
   [kushi.ui.core :refer (opts+children)]
   [kushi.css.media]
   [me.flowthing.pp :refer [pprint]]
   [domo.core :as d]
   ))

(defcss ".kushi-code-block"
  {:border-width         :$code-border-width||1px
   :border-color         :$code-border-color||$neutral-150
   :border-style         :$code-border-style||solid
   :background-color     :$code-background-color
   :--code-bracket-color :$neutral-600   
   :_.parent-open:c      :$code-bracket-color
   :_.bracket-open:c     :$code-bracket-color
   :_.brace-open:c       :$code-bracket-color
   :_.parent-close:c     :$code-bracket-color
   :_.bracket-close:c    :$code-bracket-color
   :_.brace-close:c      :$code-bracket-color
   :_.language-clojure   {:.symbol:c  :#4d6dba
                          :.keyword:c :#7A3E9D
                          :.string:c  :#4488C27}})

(defcss ".dark .kushi-code-block"
  {:border-color                   :$code-border-color-dark-mode||$neutral-800
   :--code-bracket-color-dark-mode :$neutral-500   
   :background-color               :$code-background-color-dark-mode
   :_.parent-open:c                :$code-bracket-color-dark-mode
   :_.bracket-open:c               :$code-bracket-color-dark-mode
   :_.brace-open:c                 :$code-bracket-color-dark-mode
   :_.parent-close:c               :$code-bracket-color-dark-mode
   :_.bracket-close:c              :$code-bracket-color-dark-mode
   :_.brace-close:c                :$code-bracket-color-dark-mode
   :_.language-clojure             {:.symbol:c  "#71ADE7"
                                    :.keyword:c "#b696b5"
                                    :.string:c  "#8cbd7a"}})


(defn- formatted-code [s]
  [:pre
   [:code {:class :language-clojure
           :style {:white-space :pre
                   :line-height 1.5}}
    s]])

(defn copy-to-clipboard-button [& args]
  (let [[opts attrs] (opts+children args)]
    [button
     (merge-attrs
      {:-colorway :accent
       :-surface :minimal
       :class    (css :.kushi-playground-copy-to-clipboard-button
                      :p--7px)
       :on-click #(d/copy-to-clipboard!
                   (or (some->> opts 
                                :clipboard-parent-sel
                                (d/nearest-ancestor (d/et %)))
                       js/document.body) 
                   (:text-to-copy opts))}
      (tooltip-attrs
       {:-text                        
        "Click to copy"

        :-text-on-click               
        "Copied!"

        :-text-on-click-tooltip-class 
        (css :.kushi-playground-copy-to-clipboard-button-tooltip-class
              [:--tooltip-background-color :$background-color-accent-hard])

        :-placement                  
        [:block-start :inline-end]})
      attrs)
     [icon (sx :.kushi-playground-copy-to-clipboard-button-icon
               :fs--medium) mui.svg/content-copy]]))


(defn- snippet-section
  [{:keys [header
           preformatted
           quoted-source-code
           copyable
           bottom-half?]}]
  [:section (sx :.kushi-playground-snippet-section
                :.snippet-section
                :.flex-col-fs
                ;; :gap--0.5em
                ;; :first-of-type:mbe--2.5em
                ) 
   header
   [:section 
    (merge-attrs 
     (sx :.kushi-playground-code-snippet
         :.kushi-code-block
         :.code
         :.xsmall
         :xsm:p--1.5em
         :position--relative
         :p--1.0em
         :pie--3.5em
         :xsm:pie--2.25em
         :w--100%
         :lh--1.2
         :fs--$xsmall-b
         {"border-radius"               :$rounded-absolute-medium,
          "border-width"                :1px,
          "border-color"                :$neutral-150,
          "border-style"                :solid
          ">*:nth-child(2):line-height" "revert"
          :p                          :1rem})
    (when bottom-half? (sx :bser--0 :bssr--0)))
    (when-let [attrs (some->> copyable
                              (hash-map :-text-to-copy)
                              (merge-attrs 
                               ;; TODO - can this be done without :.top-right-corner-inside!
                               ;; TODO - can this be done without :.top-right-corner-inside!
                               (sx :.top-right-corner-inside
                                   :position--absolute)
                               {:-clipboard-parent-sel ".kushi-modal"}))]
      [copy-to-clipboard-button attrs])
    preformatted
    ]])

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

(defn example-modal-trigger [modal-id]
  [button
   {:-colorway :accent
    :-shape    :pill
    :-surface  :minimal
    :class    
    (css :.kushi-playground-examples-modal-trigger
         :pb--0.4em
         :fw--$wee-bold
         :fs--$xxsmall
         :.accent.minimal:hover:background-color--$accent-50
         :dark:.accent.minimal:hover:background-color--$accent-800

         ;; Next 3 styles will give it a link-button style
         #_:p--0
         #_:hover&.accent.minimal:bgc--transparent
         #_[:hover:after {:content  "\"\""
                          :position :absolute
                          :w        :100%
                          :h        :1px
                          :o        0.5
                          :bgc      :$accent-foreground
                          :top      "calc(100% + 2px)"}])
    :on-click (fn* [] (open-kushi-modal modal-id))}
   [icon (sx :.kushi-playground-examples-modal-trigger-icon :.small :.extra-bold) :code]
   "Code"])


(defn component-snippets
  []
  (fn [{:keys [reqs-coll
               reqs-for-component
               snippets-header
               snippets
               hiccup-for-examples]}]
    [:div
     (sx :.relative
         :.flex-row-fs
         :_code:ws--n
         :_.code:ws--n
         :_pre_code:p--0
         :_pre_.code:p--0
         :min-height--120px
         :lh--1.7
         :ai--fs
         :min-width--200px
         :min-height--120px)
     [:div
      (sx :.flex-col-fs
          :w--100%
          :gap--1em
          :_.kushi-text-input-label:min-width--7em
          :_.kushi-input-inline:gtc--36%:64%)
      (let [max-width  (or (when-let [[p v] (some-> kushi.css.media/media
                                                    :sm
                                                    first)]
                             (when-not (d/matches-media? p (as-str v))
                               27))
                           50)
            formatted* #(-> % (pprint {:max-width max-width}) with-out-str)]
        (into [:div (sx ".kushi-playground-snippets-modal-requires"
                        :.flex-col-fs
                        :gap--2.25rem
                        :mbs--1.5em
                        #_:pbe--2rem)
               [snippet-section
                {:header       (into [:div (sx :.small :.wee-bold :mbe--1em)]
                                     (desc->hiccup
                                      "Paste into the `:require` section of your `:ns` form:"))
                 :preformatted (-> reqs-for-component
                                   formatted*
                                   (subs 1)
                                   (drop-last)
                                   string/join
                                   (string/replace #"\n \[kushi." "\n[kushi.")
                                   formatted-code)
                 :quoted-source-code reqs-for-component
                 :copyable     (string/join "\n" reqs-coll)}]]

              ;; This produces a snippet section for each of the examples 
              (for [[i call] (map-indexed (fn [i call] [i call]) snippets)
                    :let [
                          ;; header (when (zero? i) 
                          ;;          (when (string? snippets-header)
                          ;;            (some->> snippets-header
                          ;;                     desc->hiccup 
                          ;;                     (into [:div])
                          ;;                     #_(docs/add-links scroll-to-elsewhere-on-page))))
                          header [:div 
                                  (sx :.transition
                                      {:beer                       0
                                       :besr                       0
                                       :bser                       :$rounded-absolute-medium,
                                       :bssr                       :$rounded-absolute-medium,
                                       :border-width               :1px,
                                       :border-color               :$neutral-150,
                                       :dark:border-color          :$neutral-800,
                                       :border-style               :solid
                                       :bbew                       0
                                       ">*:nth-child(2):line-height" "revert"
                                       :p                          :1rem})
                                  #_header
                                  (nth hiccup-for-examples i)]]]
                [snippet-section
                 {:header       header

                  :bottom-half? true

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


(defcss ".kushi-playground-examples-modal"
  :_.kushi-modal-inner:pi--1.25em
  :xsm:_.kushi-modal-inner:pi--3em
  :_.kushi-modal-inner:pb--1.5rem:2em
  :xsm:_.kushi-modal-inner:pb--3em:3.5em
  [:--modal-min-width :200px]
  :_.kushi-modal-inner:gap--0.75rem
  [:height "min(var(--modal-max-height), calc(100vh - (2 * var(--modal-margin, 1rem))))"]
  :overflow--hidden
  :width--$playground-main-content-max-width)


(defn- example-modal-inner
  [{:keys [modal-id
           label
           component-label
           reqs-for-component
           snippets-header
           snippets
           reqs-for-examples
           hiccup-for-examples]
    :as   m}]

  (let [all-reqs       (? (into [] (concat reqs-for-component reqs-for-examples)))
        reqs-by-refers (? (reqs-by-refers all-reqs))]
    [:<> 
     [modal-close-button {:-modal-id modal-id}]
     [:div (sx :.kushi-playground-examples-modal-wrapper
               :.flex-row-sb :ai--fs :gap--1.5em)
      [:div
       (sx  :.kushi-playground-examples-modal-wrapper-inner
            :.flex-col-fs :ai--b :gap--1rem )
       [:h1 (sx :.kushi-playground-examples-modal-wrapper-inner-label
                :.component-section-header-label) component-label]
       label]]
     [divisor]
     [component-snippets
      (assoc (keyed [snippets-header
                     snippets
                     hiccup-for-examples
                     reqs-for-component])
             :reqs-col
             (reqs-coll reqs-by-refers))]
     ]) )


(defn example-modal
  [{:keys [modal-id
           label
           component-label
           reqs-for-component
           snippets-header
           snippets
           reqs-for-examples
           hiccup-for-examples
           wrapper-tag]
    :as m}]
  (!? :pp m)
    ;; pass wrapper tag of :div to just render element
    ;; good for dev 
    [(or wrapper-tag modal) 
     {:class "kushi-playground-examples-modal"
      :id    modal-id}
     [example-modal-inner m]

    ;;  [modal-close-button {:-modal-id modal-id}]
    ;;  [:div (sx :.kushi-playground-examples-modal-wrapper
    ;;            :.flex-row-sb :ai--fs :gap--1.5em)
    ;;   [:div
    ;;    (sx  :.kushi-playground-examples-modal-wrapper-inner
    ;;         :.flex-col-fs :ai--b :gap--1rem )
    ;;    [:h1 (sx :.kushi-playground-examples-modal-wrapper-inner-label
    ;;             :.component-section-header-label) component-label]
    ;;    label]]
    ;;  [divisor]
     
    ;;  [component-snippets
    ;;   (assoc (keyed [snippets-header
    ;;                  snippets
    ;;                  hiccup-for-examples
    ;;                  reqs-for-component])
    ;;          :reqs-col
    ;;          (reqs-coll reqs-by-refers))]
     ])

(defn section-label [s]
  [:p 
   (sx :.example-section-label
       :ff--$serif-font-stack
       :font-style--oblique
      ;; Include this if using cormorant serif face in :$serif-font-stack
      ;;  :.cormorant-section-label
      ;; Comment fs below if using cormorant serif face in :$serif-font-stack
      ;;  :fs--$small
       :fs--$small-b
       :.oblique
       :.neutralize-secondary
       :lh--1.7
       :_span.code:mis--0.5ch)
   s])

(def all-shapes
  [:rounded :pill :circle :sharp :squircle])

(def all-colors
  [
   "neutral"
   "purple"
   "blue"
   "green"
   "lime"
   "yellow"
   "gold"
   "orange"
   "red"
   "magenta"
   "brown"
   ])

(def basic-shapes
  [:rounded :pill :sharp])

(def xxsmall-xlarge
  [:xxsmall
   :xsmall
   :small
   :medium
   :large
   :xlarge])

(def xxxsmall-xxxlarge
  [:xxxsmall
   :xxsmall
   :xsmall
   :small
   :medium
   :large
   :xlarge
   :xxlarge
   :xxxlarge])

(def basic-surfaces
  [:soft :solid :outline :minimal])

(def variants-ordered 
  {'xxsmall-xlarge  
   xxsmall-xlarge

   'xxxsmall-xxxlarge 
   xxxsmall-xxxlarge 
   
   :defaults
   {'size    xxxsmall-xxxlarge
    'shape   basic-shapes
    'surface basic-surfaces
    'colorway all-colors}})

(def vo variants-ordered)

(defn resolve-variants [sym ks]
  (or (get vo sym)
      (get vo (get ks sym))
      (get-in vo [:defaults sym])
      sym))

(defcss ".kushi-playground-variant-grid-1d"
  :.flex-col-fs
  :sm:flex-direction--row
  :sm:ai--c
  :gap--0.5rem)

(defcss ".kushi-playground-variant-grid-2d"
  :.flex-row-fs
  :sm:flex-direction--column
  :sm:ai--fs
  :gap--0.5rem)

(defn sym->option-key [sym]
  (keyword (str "-" sym)))

(def publics
  {'icon icon
   'button button})


(defn variant-grid [uic demo]
  (let [{:keys [label variants]
         vks :variants-keys}
        demo

        [v-1d v-2d]
        variants]

    [:div (sx ".kushi-playground-variant-grid-wrapper"
              :.flex-col-fs
              :gap--1rem)
     [section-label label]
     (if v-2d
       (into [:div (sx :.kushi-playground-variant-grid-2d)]
             (for [a (resolve-variants v-2d vks)] 
               (into [:div (sx :.kushi-playground-variant-grid-1d)]
                     (for [b (resolve-variants v-1d vks)]
                       [uic
                        (merge-attrs (sx :.xxxsmall)
                                     {(sym->option-key v-2d) a
                                      (sym->option-key v-1d) b})
                        "Bang"]))))

       (into [:div (sx :.kushi-playground-variant-grid-1d)]
             (let [coll (resolve-variants v-1d vks)]
               (when (coll? coll)
                 (for [a coll]
                   [uic
                    (merge-attrs (sx :.xxxsmall)
                                 {(sym->option-key v-1d) (name a)})
                    "Bang"])))))]))




(defn- sx-utils->class-vector [sx-utils]
  (into [] (->> sx-utils
                rest 
                (map (comp #(subs % 1) as-str)))))

(defn- syms->publics [coll]
  (walk/postwalk (fn [x]
                   (if (symbol? x)
                     (get publics x :div.debug-red)
                     x))
                 coll))


(defn examples-grid [uic demo]
  (let [{:keys [label examples]}
        demo]
    [:div (sx ".kushi-playground-example-grid-wrapper"
              :.flex-col-fs
              :gap--1rem)
     [section-label label]
     (into [:div (sx :.kushi-playground-variant-grid-1d)]
           (for [{:keys [label args sx-utils]} examples]
             (into [uic {:class (sx-utils->class-vector sx-utils)}]
                   (syms->publics args))))]))


 (defn discrete-examples-grid [uic m]
  (let [{:keys [label examples]
         option-name :name}
        m]
    [:div (sx ".kushi-playground-example-grid-wrapper"
              :.flex-col-fs
              :gap--1rem)
     [section-label label]
     (into [:div (sx :.kushi-playground-variant-grid-1d)]
           (for [{:keys [label args attrs value]} examples]
             (into [uic (merge 
                         {(sym->option-key option-name) (syms->publics value)}
                         attrs)]
                   (syms->publics args))))]))


  #_(!? (let [ret (volatile! {})]
       (walk/postwalk (fn [x]
                        (when (get publics x)
                          
                          (vswap! ret conj )) 
                        x)
                      examples)
       (some-> ret deref (dissoc ))))
 

(defn discrete-examples-grid2
  "Supports just literal hiccup in the examples"
  [{:keys [component-fn component-name component-ns reqs]
    :as   uic}
   {:keys [label examples opt-sym snippets-header]
    :as opt}]

  (!? (find-ns component-ns))



    (let [{reqs-for-examples :require
           examples-label    :label}
          (meta examples)

          hiccup-for-examples
          (for [example examples]
            (syms->publics example))

          modal-opts
          {:modal-id            (str "button-" "wtf" "-variants"),
           :component-label     (str component-name),
           :label               [:p
                                 {:class "example-section-label oblique neutralize-secondary kushi_playground_component-examples__L139_C4"}
                                 (name opt-sym)],
           :snippets-header     "snippets-header"
           :snippets            examples
           :reqs-for-component  reqs,
           :reqs-for-examples   reqs-for-examples
           :hiccup-for-examples hiccup-for-examples}]
      [:div (sx ".kushi-playground-example-grid-wrapper"
                :.flex-col-fs
                :gap--1rem)
       [section-label examples-label]
       (when (seq examples)
         [:<>
          [example-modal-trigger (:modal-id modal-opts)]

          ;; For dev
          (let [modal-dbg? true]
            [example-modal (assoc modal-opts
                                  :wrapper-tag
                                  (when modal-dbg? :div))])
          
          ])
       (into [:div (sx :.kushi-playground-variant-grid-1d)]
             hiccup-for-examples)]))



#_{:modal-id        "button__Surface variants",
   :component-label "button",
   :label           [:p
                     {:class "example-section-label oblique neutralize-secondary kushi_playground_component-examples__L139_C4"}
                     "Surface variants"],
   :snippets-header "Use the `data-kushi-surface` attributes `:solid`, `:outline`,\n                        and `:minimal` to control the surface variant of the button.",
   :snippets        [[button "Play"]
                     [button {:-surface :solid} "Play"]
                     [button {:-surface :outline} "Play"]
                     [button {:-surface :minimal} "Play"]],
   :component-reqs  [[kushi.ui.button.core :refer [button]]],
   :example-reqs    []}



(defn showcase [ui]
  (let [{component-ns   :ns
         component-name :name
         :keys          [opts desc demos]
         :as            uic-meta}
        (-> button var meta)

        [_ demos]
        demos

        [_ opts]
        opts

        toks    
        (!? (get (!? :pp design-tokens/design-tokens-by-component-usage)
                 component-ns))

        reqs
        (->> kushi.ui.button.core
             clojure.repl/dir
             with-out-str
             string/split-lines
             (mapv symbol)
             (vector component-ns :refer)
             vector)

        uic (assoc uic-meta
                   :component-fn button
                   :component-ns component-ns
                   :component-name component-name
                   :reqs reqs)]

   (into [:div 
          (sx ".kushi-playground-component-demos-wrapper"
              :.flex-col-fs
              :p--4rem 
              :gap--3rem)
          [light-dark-mode-switch (sx :.fixed-block-start-inside 
                                      :.light
                                      :.transition)]
          ]

         (for [[opt-sym {:keys [variants examples] :as opt}]
               opts

               :when
               (= opt-sym 'end-enhancer)]
           (cond
             variants
             [variant-grid button opt]

             examples
             [discrete-examples-grid2 uic (assoc opt :opt-sym opt-sym)]
             ))
         #_(for [demo demos]
             (cond
               (:variants demo)
               [variant-grid button demo]

               (:examples demo)
               [examples-grid button demo]
               )))

      #_(into [:div (sx :p--3rem)] 
              [variant-grid demos]

              #_(for [{option-name :name
                       :keys       [pred default desc samples]} 
                      (? (second opts))

                      :when
                      (= option-name 'surface)]
                  (into [:div (sx :.flex-col-fs :gap--0.5rem)]
                        (for [surface surfaces-ordered] 
                          (into [:div (sx :.flex-row-fs :gap--0.5rem)]
                                (for [colorway all-colors]
                                  [button
                                   (merge-attrs (sx :.xxxsmall)
                                                {:-colorway colorway
                                                 :-surface  surface})
                                   "Bang"]))))))))

