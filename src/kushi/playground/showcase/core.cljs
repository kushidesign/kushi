(ns ^:dev/always kushi.playground.showcase.core
  (:require
   [bling.core :refer [bling callout]]
   [clojure.walk :as walk]
   [clojure.repl]
   [kushi.core :refer [css defcss ?defcss merge-attrs sx]]
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
   [clojure.string :as string]
   [kushi.ui.button.core :refer [button]]
   [kushi.ui.icon.core :refer [icon]]
   [kushi.ui.defs :as defs]
   [kushi.css.media])
  (:require-macros [kushi.playground.showcase.core :refer [fqns-sym fqns-sym+file-info]]))

(def vo defs/variants-ordered)

;; sym will be something like 'surface or 'colorway
(defn resolve-variants [sym ks]
  (or (get vo sym)
      (get vo (get ks sym))
      (get-in vo [:defaults sym])
      sym))

(defn sym->option-key [sym]
  (keyword (str "-" sym)))

(defcss ".kpg-variant-grid-1d"
  :.flex-col-fs
  :sm:flex-direction--row
  :sm:ai--c
  :gap--0.5rem)

(defcss ".kpg-variant-grid-2d"
  :.flex-row-fs
  :sm:flex-direction--column
  :sm:ai--fs
  :gap--0.5rem)



;; TODO - figure out where these should live as they may need augmentation from
(def publics
  "These are common public UI component functions that are used in component
   examples."
  {'icon   icon
   'button button})

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
           (callout
            {:type :warning}
            
            (bling [:italic (fqns-sym+file-info syms->publics)]
                   "\n\n"
                   [:italic 
                    (str "The following symbol did not map to a "
                         "publicly visible component rendering function:")] "\n"
                   [:bold.italic x]
                   "\n"
                   [:bold.red (string/join (repeat (count (str x)) "^"))]
                   "\n"
                   "\n"
                   [:italic "Component being demo'd:"] "\n"
                   [:bold (str (:fq-uic-name uic))]
                   "\n"
                   "\n"
                   [:italic "Path, in the above function's metadata map:"] "\n"
                   [:bold (str [:opts (:opt-sym opt) :demo :samples i])]
                   "\n"
                   "\n"
                   [:italic "Source snippet with problematic symbol:"] "\n"
                   [:bold (pprint-str coll 50)]))
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
         snippets          :snippets}
        demo]
   {:modal-id            (str opt-sym "-snippets")
    ;; change to uic-label
    :component-label     (str uic-name)
    :label               [section-label demo-label #_(name opt-sym)]
    :snippets-header     snippets-header
    :snippets            (or samples snippets)
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

(defn variant-grid
  "Prduces a 1 or 2d variant grid"
  [uic opt]
  (let [{:keys [label variants snippets]
         ;; vks :variants-keys
        }
        (:demo opt)

        [v-1d v-2d]
        variants
        
        vks
        nil
        
        uic-fn
        (:component-fn uic)

        modal-opts
        (when (seq snippets)
          (let [hic        (map-indexed (fn [i sample] 
                                          (syms->publics uic opt i sample))
                                        snippets)
                modal-opts (assoc (modal-opts* uic opt)
                                  :hiccup-for-examples
                                  hic
                                  :wrapper-tag
                                  (when modal-dbg? :div))]
            modal-opts))]

    [:div (sx ".kpg-variant-grid-wrapper" :.flex-col-fs :gap--1rem)
     [:div (sx :.flex-row-fs :gap--0.5em)
      [section-label label]
      (when modal-opts 
        (let [id (:modal-id modal-opts)]
          [:<>
           [example-modal-trigger id]
           [example-modal modal-opts]]))]

     (if v-2d
       (into [:div (sx :.kpg-variant-grid-2d)]
             (for [a (resolve-variants v-2d vks)] 
               (into [:div (sx :.kpg-variant-grid-1d)]
                     (for [b (resolve-variants v-1d vks)]
                       [uic-fn
                        (merge-attrs (sx :.xxxsmall)
                                     {(sym->option-key v-2d) a
                                      (sym->option-key v-1d) b})
                        "Bang"]))))
       (into [:div (sx :.kpg-variant-grid-1d)]
             (let [coll (resolve-variants v-1d vks)]
               (when (coll? coll)
                 (for [a coll]
                   [uic-fn
                    (merge-attrs (sx :.xxxsmall)
                                 {(sym->option-key v-1d) (name a)})
                    "Bang"])))))]))


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

(defn uic*
  [{uic-ns   :ns
    uic-name :name
    [_ opts] :opts 
    [_ demo] :demo
    :as m}]
  {:demo         demo
   :opts         (apply array-map opts)
   :toks         (get dtoks-by-usage uic-ns)
   :reqs-for-uic (reqs-for-uic* uic-ns)
   :uic-name     uic-name
   ;; change to uic-fn
   :component-fn button})

(defn showcase [uic]
  (into [:div (sx ".kpg-component-demos-wrapper"
                  :.flex-col-fs
                  :p--4rem 
                  :gap--3rem)
         [light-dark-mode-switch 
          (sx :.fixed-block-start-inside 
              :.light
              :.transition)]]
        (for [[opt-sym 
               {{:keys [variants samples]} :demo
                :as opt}]
              (:opts uic)
              :when
              (= opt-sym
                 #_'colorway
                 'end-enhancer)]
          (cond
            variants
            [variant-grid
             uic
             (assoc opt :opt-sym opt-sym)
             opt]

            samples
            [discrete-examples-grid2 
             uic 
             (assoc opt :opt-sym opt-sym)
             samples]))

        #_(for [demo demos]
            (cond
              (:variants demo)
              [variant-grid button demo]

              (:examples demo)
              [examples-grid button demo]
              ))))

