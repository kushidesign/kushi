(ns ^:dev/always kushi.playground.showcase.core
  (:require
   [bling.core :refer [bling callout]]
   [clojure.walk :as walk]
   [clojure.repl]
   [kushi.core :refer [css defcss merge-attrs sx css-vars-map]]
   [kushi.css.build.design-tokens
    :rename {design-tokens-by-component-usage dtoks-by-usage }]
   [kushi.ui.lightswitch.core :refer [light-dark-mode-switch]]
   [kushi.playground.showcase.shared :refer [section-label pprint-str]]
   [kushi.playground.showcase.modal :refer [example-modal-trigger example-modal]]
   [kushi.ui.tooltip.core :refer [tooltip-attrs]]
   [kushi.ui.icon.mui.svg :as mui.svg]
   [kushi.ui.divisor.core :refer [divisor]]
   [kushi.ui.util :refer [as-str maybe keyed]]
   [kushi.ui.modal.core :refer [modal open-kushi-modal modal-close-button]]
   [kushi.ui.spinner.core :refer [donut propeller thinking]]
   [clojure.string :as string]
   [kushi.ui.button.core :refer [button]]
   [kushi.ui.icon.core :refer [icon]]
   [kushi.ui.defs :as defs]
   [kushi.util]
   [kushi.css.media])
  (:require-macros [kushi.playground.showcase.core :refer [fqns-sym+file-info]]))

(def vo defs/variants-ordered)

;; sym will be something like 'surface or 'colorway
(defn resolve-variants
  ([sym]
   (resolve-variants sym nil))
  ([sym ks]
   (or (get vo sym)
       (get vo (get ks sym))
       (get-in vo [:defaults sym])
       sym)))

(defn sym->option-key [sym]
  (keyword (str "-" sym)))

(defcss "@layer kushi-playground-styles .kpg-variant-grid-1d"
  ;; :.flex-col-fs
  ;; :sm:flex-direction--row
  ;; :sm:ai--c
  :.flex-row-fs
  :gap--0.5rem)

(defcss "@layer kushi-playground-styles .kpg-variant-grid-2d"
  ;; :.flex-row-fs
  ;; :sm:flex-direction--column
  ;; :sm:ai--fs
  :.flex-col-fs
  :ai--fs
  :gap--0.5rem)


(defn uic-demo-callout-warning
  [{:keys [file-info bad-value uic message path snippet-message snippet]}]
  (let [snippet (and snippet-message snippet)]
    (callout {:type :warning}
             (bling [:italic file-info]
                    "\n\n"
                    [:italic message] "\n"
                    [:bold.italic (str bad-value)]
                    "\n"
                    [:bold.red (string/join (repeat (count (str bad-value)) "^"))]
                    "\n"
                    "\n"
                    [:italic "Component being demo'd:"] "\n"
                    [:bold (str (:fq-uic-name uic))]
                    "\n"
                    "\n"
                    [:italic "Path, in the above function's metadata map:"] "\n"
                    [:bold (->> path
                                (remove nil?)
                                (into [])
                                str)]
                    (when snippet "\n")
                    (when snippet "\n")
                    (when snippet-message [:italic snippet-message])
                    (when snippet "\n")
                    (when snippet [:bold (pprint-str snippet 50)])
                    ))))


;; TODO - figure out where these should live as they may need augmentation from
(def publics
  "These are common public UI component functions that are used in component
   examples."
  {'icon      icon
   'button    button
   'propeller propeller
   'donut     donut
   'thinking  thinking})

(defn- syms->publics
  "Converts symbols to values if they are present in `publics` def.
   (syms->publics '[:div [icon :pets]])
   =>
   [:div [#Object[kushi.ui.icon.core] :pets]]]"
  [uic opt i coll]
  (walk/postwalk 
   (fn [x]
     (if (symbol? x)
       (if-let [f (get publics x)]
         f
         (do 
           (uic-demo-callout-warning
            {:file-info       (fqns-sym+file-info syms->publics)
             :message         (str "The following symbol did not map to a publicly\n"
                                   "visible component rendering function:")
             :bad-value       x
             :uic             uic
             :path            [:opts (:opt-sym opt) :demo :samples i]
             :snippet-message "Source snippet with problematic symbol:"
             :snippet         coll})
           :div.debug-red))
       x))
   coll))


;; Trash if not needed

;; (defn- sx-utils->class-vector [sx-utils]
;;   (into [] (->> sx-utils
;;                 rest 
;;                 (map (comp #(subs % 1) as-str)))))


;; (defn examples-grid [uic demo]
;;   (let [{:keys [label examples]}
;;         demo]
;;     [:div (sx ".kpg-example-grid-wrapper"
;;               :.flex-col-fs
;;               :gap--1rem)
;;      [section-label label]
;;      (into [:div (sx :.kpg-variant-grid-1d)]
;;            (for [{:keys [label args sx-utils]} examples]
;;              (into [uic {:class (sx-utils->class-vector sx-utils)}]
;;                    (syms->publics args))))]))


;;  (defn discrete-examples-grid [uic m]
;;   (let [{:keys [label examples]
;;          option-name :name}
;;         m]
;;     [:div (sx ".kpg-example-grid-wrapper"
;;               :.flex-col-fs
;;               :gap--1rem)
;;      [section-label label]
;;      (into [:div (sx :.kpg-variant-grid-1d)]
;;            (for [{:keys [label args attrs value]} examples]
;;              (into [uic (merge 
;;                          {(sym->option-key option-name) (syms->publics value)}
;;                          attrs)]
;;                    (syms->publics args))))]))

(defn- modal-opts*
  "Creates opts for a modal within the component's showcase"
  [{:keys [uic-name reqs-for-uic] :as uic}
   {:keys [demo opt-sym snippets-header] :as opt}]
  (let [{reqs-for-examples :require
         demo-label        :label
         samples           :samples
        ;;  variants          :variants
        ;;  variant-attrs     :variant-attrs
        ;;  variant-args      :variant-args
         }
        demo]

   #_(when (= "Surface variants" 
            (-> opt :demo :label))
     (let [sym   (first variants)
           coll  (resolve-variants sym)
           k     (some->> sym name (str "-") keyword)
          ;;  m+   (syms->publics uic opt i m)
           attrs (syms->publics uic opt 0 variant-attrs)
           args  (syms->publics uic opt 0 variant-args)
           ]
       ;; TODO generate the snippets and samples at once
       (? (into []
                (for [variant coll]
                  (into [button (assoc attrs k variant)] args))))))

   {:modal-id            (str opt-sym "-snippets")
    ;; change to uic-label
    :component-label     (str uic-name)
    :label               [section-label demo-label #_(name opt-sym)]
    :snippets-header     snippets-header
    :snippets            samples
    :reqs-for-uic        reqs-for-uic
    :reqs-for-examples   reqs-for-examples}))

(def modal-dbg? true)

(defn discrete-examples-grid2
  "Supports literal hiccup in the examples"
  [uic 
   opt
   samples]
  (when (seq samples)
    (let [hic        (map-indexed (fn [i sample] 
                                    (syms->publics uic opt i sample))
                                  samples)
          modal-opts (assoc (modal-opts* uic opt)
                            :hiccup-for-examples
                            hic
                            :wrapper-tag
                            (when modal-dbg? :div))]
      [:div (sx ".kpg-example-grid-wrapper" :.flex-col-fs :gap--1rem)
       [:div (sx :.flex-row-fs :gap--0.5em)
        [section-label (-> opt :demo :label)]
        [example-modal-trigger (:modal-id modal-opts)]]
       [example-modal modal-opts]
       (into [:div (sx :.kpg-variant-grid-1d)]
             hic)])))

;; :variants -> :x-variants for additional dimensions 
;; Control for variants by column or row
(defn variant-grid-snippet-modal-samples [uic opt]
  (let [{args          :args
         attrs         :attrs
         attrs-snippet :attrs/snippet
         snippets?     :snippets?}
        (:demo opt)]
    (if true ; <-validate here
      (let [sym                    (:opt-sym opt)
            k                      (some->> sym name (str "-") keyword)
            resolved-variants      (resolve-variants sym nil)
            attrs-hydrated         (syms->publics uic opt nil attrs)
            attrs-snippet-hydrated (syms->publics uic opt nil attrs-snippet)]
        (when sym
          {:samples ; <- this is for snippets
           (into []
                 (for [v resolved-variants]
                   (into [(:uic-name uic) (merge (assoc attrs k v)
                                                 attrs-snippet)]
                         args)))
           :hiccup-for-examples  ; <- this is for rendering preview above snippets
           (for [v resolved-variants]
             (into [(:component-fn uic) 
                    (merge (assoc attrs-hydrated k v)
                           attrs-snippet-hydrated)]
                   args))}))
      (callout {:type :waring} "bad variant name"))))

(defn d2-grid [v-2d vks v-1d uic-fn variant-attrs variant-args]
  (into [:div (sx :.kpg-variant-grid-2d)]
        (for [a    (resolve-variants v-2d vks)
              :let [variant-label (str "\"" a "\"")]] 
          (into [:div {:style (css-vars-map variant-label)
                       :class (css :.kpg-variant-grid-1d
                                        ;; :first-child>*:after:display--block
                                        ;; :first-child>*:after:display--none
                                   #_:before:content--$variant-label
                                   )}]
                (for [b (resolve-variants v-1d vks)]
                  (into [uic-fn
                         (merge-attrs {(sym->option-key v-2d) a
                                       (sym->option-key v-1d) b}
                                      variant-attrs
                                      #_{:style (css-vars-map variant-label)
                                         :class (css :.kpg-variant-grid-1d
                                                     :before:content--$variant-label
                                                     :before:position--absolute
                                                     :before:bottom--125%
                                                     :before:left--50%
                                                     :before:translate---50%
                                                     :before:fs--$xxxsmall
                                                     :before:font-style--italic
                                                     :before:color--$foreground-color
                                                     )})]
                        variant-args))))))

(defn variant-grid
  "Prduces a 1 or 2d variant grid"
  [uic opt]
  (let [{args          :args
         attrs         :attrs
         attrs-display :attrs/display
         :keys         [x-variants label rows? snippets?]}
        (:demo opt)

        variants
        (into []
              (remove nil? 
                      (if rows?
                        (into [(first x-variants)
                               (:opt-sym opt)]
                              (rest x-variants))
                        (into [(:opt-sym opt)] x-variants))))

        [v-1d v-2d v-3d]
        (let [[variants bad-variants]
              (kushi.util/partition-by-pred
               #(contains? defs/variants-syms-set %)
               variants)]
          (when (seq bad-variants)
            (uic-demo-callout-warning
             {:file-info (fqns-sym+file-info variant-grid)
              :uic       uic
              :message   "The following variants are invalid:"
              :bad-value bad-variants
              :path      [:opts (:opt-sym opt) :demo :variants]}))
          variants)
        
        vks
        nil
        
        uic-fn
        (:component-fn uic)

        {:keys [samples hiccup-for-examples]}
        (variant-grid-snippet-modal-samples uic opt)

        opt
        (assoc-in opt [:demo :samples] samples)

        modal-opts
        (when (and (seq samples)
                   (not (false? snippets?)))
          (assoc (modal-opts* uic opt)
                 :hiccup-for-examples
                 hiccup-for-examples
                 :wrapper-tag
                 (when modal-dbg? :div)))

        variant-args
        (syms->publics uic opt nil args)

        variant-attrs
        (merge (syms->publics uic opt nil attrs)
               (syms->publics uic opt nil attrs-display))]

    [:div (sx ".kpg-variant-grid-wrapper" :.flex-col-fs :gap--1rem)
     [:div (sx :.flex-row-fs
               :gap--0.5em
               :pbe--0.5em
               :bbe--1px:solid:$neutral-200
               :dark:bbe--1px:solid:$neutral-800
               )
      [section-label label]
      (when modal-opts 
        (let [id (:modal-id modal-opts)]
          [:<>
           [example-modal-trigger id]
           [example-modal modal-opts]]))]

     ;; Note if you want before labels, you'll need to use a grid layout
     (cond
       v-3d
       (into [:div (sx :.flex-col-fs :gap--2em)]
             (for [kw (resolve-variants v-3d vks)
                   :let [variant-attrs (assoc variant-attrs
                                              (keyword (str "-" (name v-3d)))
                                              kw)]]
               [d2-grid v-2d vks v-1d uic-fn variant-attrs variant-args]))

       v-2d
       [d2-grid v-2d vks v-1d uic-fn variant-attrs variant-args]

       :else
       (into [:div (merge-attrs (sx :display--grid
                                    :gtc--74px:max-content
                                    :ai--c
                                    :gap--0.75rem:1rem)
                                #_(when rows? (sx :flex-direction--column
                                                :mbs--0)))]
             (let [coll (resolve-variants v-1d vks)]
               (when (coll? coll)
                 (reduce
                  (fn [acc a]
                    (let [variant-label a #_(-> a
                                            name
                                            (string/replace #"-" " ")
                                            (string/replace #"^:" ""))]
                      (conj acc
                            [:span (sx :content--$variant-label
                                       :ws--n
                                       :color--$foreground-color-secondary
                                       :text-shadow--none
                                       :fs--$xsmall
                                       :ff--$sans-serif-font-stack)
                             variant-label]
                            [uic-fn
                             (merge-attrs {(sym->option-key v-1d) (name a)
                                           :-end-enhancer         [icon :arrow-forward]}
                                          variant-attrs
                                          (when rows?
                                            (sx [:before:bottom "calc(50%)"]
                                                :before:left--50%
                                                :before:translate---50%)))
                             "Next"])))
                  []
                  coll))))

       #_(into [:div (merge-attrs (sx :.kpg-variant-grid-1d
                                    :mbs--1.5rem
                                    :ai--fs)
                                (when rows? 
                                  (sx :flex-direction--column
                                      :mbs--0)))]
             (let [coll (resolve-variants v-1d vks)]
               (when (coll? coll)
                 (for [a coll
                       :let [variant-label (-> a
                                               name
                                               (string/replace #"-" " ")
                                               (string/replace #"^:" ""))]]
                   [uic-fn
                    (merge-attrs {(sym->option-key v-1d) (name a)
                                  :-end-enhancer         [icon :arrow-forward]}
                                 variant-attrs
                                 {:style {"--variant-label" 
                                          (str "\"" variant-label "\"")}}
                                 (sx :before:content--$variant-label
                                     :before:position--absolute
                                     [:before:bottom "calc(100% + 15px)"]
                                     :before:left--50%
                                     :before:translate---50%
                                     :before:ws--n
                                     :before:color--$foreground-color-secondary
                                     :before:text-shadow--none
                                     :before:fs--10px
                                     :before:tt--capitalize
                                     :before:ff--$sans-serif-font-stack)
                                 (when rows?
                                   (sx 
                                     [:before:bottom "calc(50%)"]
                                     :before:left--50%
                                     :before:translate---50%
                                    )))
                    "Next"])))))]))


(defn reqs-for-uic* 
  "Constructs a vector representing a ns :require vector for the supplied ns.
   All public vars in the ns are present in the :refer vector.
   
   Example:
   (reqs-for-uic* kushi.ui.button.core)
   =>
   [kushi.ui.button.core :refer [button]]"
  [uic-ns]
  (->> #_kushi.ui.button.core
       uic-ns
       clojure.repl/dir
       with-out-str
       string/split-lines
       (mapv symbol)
       (vector uic-ns :refer)
       vector))

;; (defn uic*
;;   [{uic-ns   :ns
;;     uic-name :name
;;     [_ opts] :opts 
;;     [_ demo] :demo
;;     :as m}]
;;   (? m)
;;   {:demo         demo
;;    :opts         (apply array-map opts)
;;    :toks         (get dtoks-by-usage uic-ns)
;;    :reqs-for-uic (reqs-for-uic* uic-ns)
;;    :uic-name     uic-name
;;    ;; change to uic-fn
;;    :component-fn button})

(defn showcase [m]
  (into [:div (sx ".kpg-component-demos-wrapper"
                  :.flex-col-fs
                  :p--4rem 
                  :gap--5rem)
         [light-dark-mode-switch 
          (sx :.fixed-block-start-inside 
              :.light
              :.transition)]]
        (for [[opt-sym 
               {{:keys [attrs samples]} :demo
                :as                     opt}]
              (:opts m)
              ;; :when
              ;; (= opt-sym
              ;;    #_'colorway
              ;;    'end-enhancer)
              ]
          (cond
            attrs
            [variant-grid
             m
             (assoc opt :opt-sym opt-sym)
             opt]

            samples
            [discrete-examples-grid2 
             m 
             (assoc opt :opt-sym opt-sym)
             samples]))

        #_(for [demo demos]
            (cond
              (:variants demo)
              [variant-grid button demo]

              (:examples demo)
              [examples-grid button demo]
              ))))
