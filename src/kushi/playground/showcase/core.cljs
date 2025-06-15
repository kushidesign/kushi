(ns ^:dev/always kushi.playground.showcase.core
  (:require
   [fireworks.core :refer [? !? ?> !?>]]
   [bling.core :refer [bling callout]]
   [clojure.walk :as walk]
   [clojure.repl]
   [clojure.edn]
   [kushi.core :refer [css defcss merge-attrs sx css-vars-map]]
  ;;  [kushi.css.build.design-tokens
  ;;   :rename {design-tokens-by-component-usage dtoks-by-usage}]
   [kushi.ui.lightswitch.core :refer [light-dark-mode-switch]]
   [kushi.playground.showcase.shared :refer [section-label pprint-str]]
   [kushi.playground.showcase.modal :refer [example-modal-trigger example-modal]]
   [kushi.ui.tooltip.core :refer [tooltip-attrs]]
   [kushi.ui.icon.mui.svg :as mui.svg]
   [kushi.ui.divisor.core :refer [divisor]]
   [kushi.ui.util :refer [as-str maybe keyed]]
   [kushi.ui.modal.core :refer [modal open-kushi-modal modal-close-button]]
   [kushi.ui.spinner.core :refer [spinner]]
   [clojure.string :as string]
   [kushi.ui.button.core :refer [button]]
   [kushi.ui.icon.core :refer [icon]]
   [kushi.ui.radio.core :refer [radio]]
   [kushi.ui.label.core :refer [label]]
   [kushi.ui.defs :as defs]
   [kushi.util]
   [kushi.css.media]
   [kushi.util :as util])
  (:require-macros [kushi.playground.showcase.core :refer [fqns-sym+file-info]]))

(def vo defs/variants-ordered-kw)

;; sym will be something like 'surface or 'colorway

;; TODO - fix this so sym is keyword like :surface

(defn resolve-variants
  ([kw]
   (resolve-variants kw nil))
  ([kw ks]
   (or (when (-> kw name (string/ends-with? "?"))
         [false true])
       (get vo kw)
       (get vo (get ks kw))
       (get-in vo [:defaults kw])
       kw)))

(defn kw->option-key [kw]
  (keyword (str "-" (name kw))))

(defn sym->option-key [sym]
  (keyword (str "-" sym)))


(defcss "@layer kushi-playground-styles .kpg-variant-grid-1d"
  :.flex-row-start
  :gap--0.5rem)

(defcss "@layer kushi-playground-styles .kpg-variant-grid-2d"
  :.flex-col-start
  :ai--fs
  :gap--0.5rem)


(defn uic-demo-callout-warning
  [{:keys [file-info bad-value uic message opt-key snippet-message snippet]}]
  (let [snippet (and snippet-message snippet)
        indent "  "]
    (callout {:type :warning}
             (bling [:italic file-info]
                    "\n\n\n"
                    [:italic message] "\n\n"
                    [:bold.italic (str indent bad-value)]
                    "\n"
                    [:bold.red (str indent
                                    (string/join (repeat (count (str bad-value))
                                                         "^")))]
                    "\n\n\n"
                    [:italic "Component being demo'd:"] "\n\n"
                    [:bold (str indent (:fq-uic-name uic))]
                    "\n\n\n\n"
                    [:italic "Custom option being demo'd, from the component's schema map:"] "\n\n"
                    indent 
                    [:bold opt-key]
                    (when snippet "\n\n\n\n")
                    (when snippet-message [:italic snippet-message])
                    (when snippet "\n\n")
                    (when snippet (str indent 
                                       (string/join (str "\n" indent)
                                                    (string/split (pprint-str snippet 50)
                                                                  #"\n"))))))))


;; TODO - figure out where these should live as they may need augmentation from
(def publics
  "These are common public UI component functions that are used in component
   examples."
  {'icon        icon
   'button      button
   'spinner     spinner
   'radio       radio
   'label       label
   'sx          sx
   'merge-attrs merge-attrs})

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
             :opt-key         (:opt-key opt)
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
;;               :.flex-col-start
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
;;               :.flex-col-start
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
   {:keys [demo opt-sym snippets-header demo-index label] :as opt}]
  (let [{reqs-for-examples :require
         demo-label        :label
         modal-label       :label/modal
         samples           :samples
        ;;  variants          :variants
        ;;  variant-attrs     :variant-attrs
        ;;  variant-args      :variant-args
         }
        demo
        demo-label (or modal-label label demo-label)]

   {:modal-id          (str uic-name
                               "_"
                               opt-sym
                                 "_snippets"
                                 "_demo-" 
                                 demo-index)
    ;; change to uic-label
    :component-label     (str uic-name)
    :label               [section-label demo-label #_(name opt-sym)]
    :snippets-header     snippets-header
    :snippets            samples
    :reqs-for-uic        reqs-for-uic
    :reqs-for-examples   reqs-for-examples}))

(def modal-dbg? true)

(defn- icons-with-tooltips
  [{:keys [component-fn uic-name]
    :as   uic}
   opt
   samples]
  {:hic 
   (mapv 
    (fn [arg]
      [component-fn
       (merge-attrs (or (-> opt :demo :attrs/display)
                        (-> opt :demo :attrs))
                    (sx :.pointer)
                    (tooltip-attrs 
                     {:-text          arg
                      :-tooltip-class (css :ff--$code-font-stack
                                           [:--tooltip-delay-duration :$xxxfast])}))
       arg])
    samples)
   :hiccup-for-examples
   (mapv 
    (fn [arg]
      (if-let [attrs (or (-> opt :demo :attrs/snippet)
                         (-> opt :demo :attrs))]
        [component-fn attrs arg]
        [component-fn arg]))
    samples)
   :hiccup-for-snippets
   (mapv 
    (fn [arg]
      (if-let [attrs (or (-> opt :demo :attrs/snippet)
                         (-> opt :demo :attrs))]
        [uic-name attrs arg]
        [uic-name arg]))
    samples)})


(defn radio-sizes
  [{:keys [component-fn uic-name]
    :as   uic}
   opt
   samples]
  {:hic 
   (mapv 
    (fn [arg]
      [component-fn
       (merge-attrs (or (-> opt :demo :attrs/display)
                        (-> opt :demo :attrs))
                    (sx :.pointer)
                    (tooltip-attrs {:-text          arg
                                    :-tooltip-class (css :ff--$code-font-stack
                                                         [:--tooltip-delay-duration :$xxxfast])
                                    }))
       arg])
    samples)
   :hiccup-for-examples
   (mapv 
    (fn [arg]
      (if-let [attrs (or (-> opt :demo :attrs/snippet)
                         (-> opt :demo :attrs))]
        [component-fn attrs arg]
        [component-fn arg]))
    samples)
   :hiccup-for-snippets
   (mapv 
    (fn [arg]
      (if-let [attrs (or (-> opt :demo :attrs/snippet)
                         (-> opt :demo :attrs))]
        [uic-name attrs arg]
        [uic-name arg]))
    samples)})


(def showcase-fns
  {:icons-with-tooltips icons-with-tooltips
   :radio-sizes         radio-sizes})


(defn- d1-grid-with-variant-labels
  [v-1d vks uic-fn variant-attrs variant-args demo]
  (into [:div (merge-attrs 
               (sx :display--grid
                   :gtc--74px:max-content
                   :ai--c
                   :gap--0.75rem:1rem)
               {:style (:row-style demo)})]
        (let [coll (resolve-variants v-1d vks)]
          (when (coll? coll)
            (reduce
             (fn [acc a]
               (let [variant-label
                     (if (keyword? a) (name a) (str a))]
                 (conj acc
                       [:span (sx :content--$variant-label
                                  :ws--n
                                  :.foreground-color-secondary
                                  :text-shadow--none
                                  :fs--$xsmall
                                  :ff--$sans-serif-font-stack)
                        variant-label]
                       (into [uic-fn
                              (merge-attrs
                               {(kw->option-key v-1d) a
                                :-end-enhancer        [icon :arrow-forward]}
                               variant-attrs)]
                             variant-args))))
             []
             coll)))))


;; view input coming in from opts

#_(defn- d1-grid-with-sample-labels
  [samples labels]
  (? labels)
  (? samples)
  (into [:div (merge-attrs (sx :display--grid
                               :gtc--74px:max-content
                               :ai--c
                               :gap--0.75rem:1rem))]
        (reduce
         (fn [acc [label sample]]
           (? :no-file label)
           (? :pp sample)
           (let [variant-label
                 (if (keyword? label) (name label) (str label))]
             (conj
              acc
              [:span (sx :content--$variant-label
                         :ws--n
                         :.foreground-color-secondary
                         :text-shadow--none
                         :fs--$xsmall
                         :ff--$sans-serif-font-stack)
               variant-label]
              sample)))
         []
         (map vector labels samples))))
         
(defn- d1-grid-with-sample-labels
  [samples]
  (into [:div (merge-attrs (sx :display--grid
                               ;; TODO - Does this need to be more dynamic? e.g. 74px
                               :gtc--74px:max-content
                               :ai--c
                               :gap--0.75rem:1rem))]
        (reduce
         (fn [acc {:keys [label] :as sample}]
           (let [variant-label
                 (if (keyword? label) (name label) (str label))]
             (conj
              acc
              [:span (sx :content--$variant-label
                         :ws--n
                         :.foreground-color-secondary
                         :text-shadow--none
                         :fs--$xsmall
                         :ff--$sans-serif-font-stack)
               variant-label]
              (:code/evaled sample))))
         []
         samples)))

(defn- d1-grid-no-labels
  [opt samples]
  (into [:div (merge-attrs 
               (sx :.kpg-variant-grid-1d)
               {:style (or (-> opt :row-style)
                           (-> opt :demo :row-style))})]
        (map :code/evaled samples)))

(defn- discrete-example-grid-wrapper [opt modal-opts hic]
 (!? (keyed [opt modal-opts hic]))
  [:div (sx ".kpg-example-grid-wrapper" :.flex-col-start :gap--1rem)
   [:div (sx :.flex-row-start
             :gap--0.5em
             :pbe--0.5em
             :bbe--1px:solid:$neutral-200
             :dark:bbe--1px:solid:$neutral-800)
    [section-label (or (-> opt :label)
                       (-> opt :demo :label))]
    [example-modal-trigger (:modal-id modal-opts)]]
   [example-modal modal-opts]
   hic])

(defn hiccup-for-examples [uic samples]
  (let [evaled (mapv :code/evaled samples)]
    (cond (= 'radio (:uic-name uic))
          (walk/postwalk #(if-let [nm (and (map? %)
                                           (-> % :-input-attrs :name))]
                            (assoc-in % 
                                      [:-input-attrs :name]
                                      (str (util/as-str nm) "-snippets-modal"))
                            %)
                         evaled)
          :else
          evaled)))

(defn- discrete-examples-grid2
  "Supports literal hiccup in the examples"
  [uic 
   opt
   samples]
  (when (seq samples)

    (if-let [f (->> opt :demo :render-as (get showcase-fns))]

      ;; TODO - Make this work with labels too
      ;; This branch uses a specific rendering fn in this playground.showcase.core 
      (let [{:keys [hic hiccup-for-examples hiccup-for-snippets]}
            (f uic opt samples)

            modal-opts 
            (assoc (modal-opts* uic opt)
                   :hiccup-for-examples
                   hiccup-for-examples
                   :samples
                   samples
                   :snippets
                   hiccup-for-snippets
                   :wrapper-tag
                   (when modal-dbg? :div))]

        [discrete-example-grid-wrapper 
         opt
         modal-opts
         (into [:div (merge-attrs 
                      (sx :.kpg-variant-grid-1d)
                      {:style (or (-> opt :row-style)
                                  (-> opt :demo :row-style))})]
               hic)])

      ;; This branch renders a row of samples, maybe with labels 
      (let [
            ;; og-samples        samples
            ;; samples-are-maps? (every? #(and (map? %) (some-> % :code seq)) og-samples) 
            ;; samples           (if samples-are-maps? (mapv :code samples) og-samples)
            ;; labels            (when samples-are-maps? (keep :label og-samples))
            ;; labels            (when (and (= (count labels) (count samples))
            ;;                              (every? #(or (keyword? %) (string? %))
            ;;                                      labels))
            ;;                     labels)
            ;; hic               (map-indexed (fn [i sample] 
            ;;                                  (syms->publics uic opt i sample))
            ;;                                samples)
            ;; opt               (if samples-are-maps?
            ;;                     (assoc-in opt [:demo :samples] samples)
            ;;                     opt)
            labels?    (every? #(-> % :label) samples)
            modal-opts (assoc (modal-opts* uic opt)
                              :hiccup-for-examples
                              (hiccup-for-examples uic samples)
                              :snippets
                              (mapv :code/quoted samples)
                              :wrapper-tag
                              (when modal-dbg? :div))]
        
        (keyed [#_uic #_opt samples labels? modal-opts])
        [discrete-example-grid-wrapper
         opt
         modal-opts
         (if labels? 
           [d1-grid-with-sample-labels samples]
           [d1-grid-no-labels opt samples])]))))


;; :variants -> :x-variants for additional dimensions 
;; Control for variants by column or row
(defn variant-grid-snippet-modal-samples [uic opt]
  (let [{args          :args
         attrs         :attrs
         attrs-snippet :attrs/snippet
         snippets?     :snippets?}
        (:demo opt)]
    (if true ; TODO <-validate here
      (let [{:keys [opt-key opt-kw]} opt
            resolved-variants      (resolve-variants opt-kw nil)
            attrs-hydrated         (syms->publics uic opt nil attrs)
            attrs-snippet-hydrated (syms->publics uic opt nil attrs-snippet)]
        (when opt-key
          {:samples ; <- this is for snippets
           (into []
                 (for [v resolved-variants]
                   (into [(:uic-sym uic) (merge (assoc attrs opt-key v)
                                                attrs-snippet)]
                         args)))
           :hiccup-for-examples  ; <- this is for rendering preview above snippets
           (for [v resolved-variants]
             (into [(:component-fn uic) 
                    (merge (assoc attrs-hydrated opt-key v)
                           attrs-snippet-hydrated)]
                   args))}))
      (callout {:type :waring} "bad variant name"))))


(defn d2-grid [v-2d vks v-1d uic-fn variant-attrs variant-args row-style]
  (!? [v-2d vks v-1d uic-fn variant-attrs variant-args])
  (into [:div (sx :.kpg-variant-grid-2d)]
        (for [a    (resolve-variants v-2d vks)
              :let [variant-label (str "\"" a "\"")]] 
          (into [:div {:style (merge (css-vars-map variant-label)
                                     row-style)
                       :class (css :.kpg-variant-grid-1d
                                        ;; :first-child>*:after:display--block
                                        ;; :first-child>*:after:display--none
                                   #_:before:content--$variant-label
                                   )}]
                (for [b (resolve-variants v-1d vks)]
                  (into [uic-fn
                         (merge-attrs {(kw->option-key v-2d) a
                                       (kw->option-key v-1d) b}
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
  "Produces a 1d, 2d, or 3d variant grid"
  [uic opt]
  (let [{:keys [demo-index multiple-demos? schema]}
        opt
        
        {args          :args
         attrs         :attrs
         attrs-display :attrs/display
         :keys         [x-variants label rows? snippets? variant-labels? row-style]
         :or           {variant-labels? true}
         :as           demo} 
        (:demo opt)

        variants
        (into []
              (remove nil? 
                      (if rows?
                        (into [(first x-variants)
                               (:opt-kw opt)]
                              (rest x-variants))
                        (into [(:opt-kw opt)] x-variants))))

        [variants bad-variants]
        (kushi.util/partition-by-pred
         #(or (= schema boolean?)
              (= schema 'boolean?)
              (contains? defs/variants-kw-set %))
         variants)]

    (if (seq bad-variants)

      (do (uic-demo-callout-warning
           {:file-info (fqns-sym+file-info variant-grid)
            :uic       uic
            :message   "The following variants are invalid:"
            :bad-value bad-variants
            :opt-key   (:opt-key opt)})
          [:<>])
      
      (let [[v-1d v-2d v-3d]
            variants
            
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

    [:div (sx ".kpg-variant-grid-wrapper" :.flex-col-start :gap--1rem)
     [:div (sx :.flex-row-start
               :gap--0.5em
               :pbe--0.5em
               :bbe--1px:solid:$neutral-200
               :dark:bbe--1px:solid:$neutral-800
               )
      [section-label (or label (some-> opt :opt-sym name string/capitalize (str " variants")))]
      (when modal-opts 
        (let [id (:modal-id modal-opts)]
          [:<>
           [example-modal-trigger id]
           [example-modal modal-opts]]))]

     ;; Note if you want before labels, you'll need to use a grid layout
     (cond
       v-3d
       (into [:div (sx :.flex-col-start :gap--2em)]
             (for [kw (resolve-variants v-3d vks)
                   :let [variant-attrs (assoc variant-attrs (:opt-key opt) kw)]]
               [d2-grid v-2d vks v-1d uic-fn variant-attrs variant-args row-style]))

       v-2d
       [d2-grid v-2d vks v-1d uic-fn variant-attrs variant-args row-style]

       :else
       (if-not variant-labels?
         ;; d1 with no labels
         (into [:div (sx :.flex-row-space-between :ai--c :w--100%)]
               (let [coll (resolve-variants v-1d vks)]
                 (when (coll? coll)
                   (mapv (fn [a]
                           (into [uic-fn
                                  (merge-attrs {(kw->option-key v-1d) a}
                                               variant-attrs)]
                                 variant-args))
                         coll))))

         [d1-grid-with-variant-labels
          v-1d
          vks
          uic-fn
          variant-attrs
          variant-args
          demo]))]))))


(defn showcase [m]
  (into [:div (sx ".kpg-component-demos-wrapper"
                  :.flex-col-start
                  :p--4rem 
                  :gap--5rem)
         [light-dark-mode-switch 
          (sx :.fixed-block-start-inside 
              :.light
              :.transition)]]
          
         (concat
          (for [[opt-key opt] (:opts m)
                :let          [opt-name (subs (name opt-key) 1)
                               opt-sym  (symbol opt-name)
                               opt-kw   (keyword opt-name)
                               opt      (assoc opt
                                               :opt-name opt-sym
                                               :opt-sym opt-sym
                                               :opt-key opt-key
                                               :opt-kw opt-kw)]
                :when         (and (:demo opt)
                                   ;; Only for debugging
                                   #_(= opt-sym 'start-enhancer)
                                   )]

            (let [samples         (some-> opt :demo :samples)
                  demo            (:demo opt)
                  multiple-demos? (vector? demo) 
                  demos           (if multiple-demos? demo [(or demo {})])]
              (into [:<>] 
                    (map-indexed (fn [demo-index demo]
                                   [variant-grid
                                    (!? m)
                                    (!? (assoc opt
                                               :demo demo
                                               :demo-index demo-index
                                               :multiple-demos? multiple-demos?))])
                                 demos))))

          (map-indexed (fn [demo-index demo]
                         [discrete-examples-grid2 
                          m
                          {:demo            demo
                           :demo-index      demo-index
                           :multiple-demos? true}
                          (:samples demo)])
                       (:demos m)))))

#_(let [samples (some-> opt :demo :samples)]
              (cond
                  ;; This branch uses the :samples vector located in the :demo entry
                  ;; samples
                  ;; [discrete-examples-grid2 m opt samples]
                

                  ;; This branch renders variants for the specific opt, things like
                  ;; :-size, :-colorway, etc.
                  ;; It uses the :attrs map provided in the :demo entry, or 
                  ;; just an empty map if no :attrs is provided. 
                :else
                (let [demo            (:demo opt)
                      multiple-demos? (vector? demo) 
                      demos           (if multiple-demos? demo [(or demo {})])]
                  (into [:<>] 
                        (map-indexed (fn [demo-index demo]
                                       [variant-grid
                                        (? m)
                                        (? (assoc opt
                                                  :demo demo
                                                  :demo-index demo-index
                                                  :multiple-demos? multiple-demos?))])
                                     demos)))))
