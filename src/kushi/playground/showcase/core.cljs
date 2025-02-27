(ns ^:dev/always kushi.playground.showcase.core
  (:require
   [clojure.walk :as walk]
   [clojure.repl]
   [fireworks.core :refer [!? ?]]
   [kushi.core :refer [css defcss ?defcss merge-attrs sx $]]
   [kushi.css.build.design-tokens
    :rename {design-tokens-by-component-usage dtoks-by-usage }]
   [kushi.ui.lightswitch.core :refer [light-dark-mode-switch]]
   [kushi.playground.showcase.shared :refer [section-label]]
   [kushi.playground.showcase.modal :refer [example-modal-trigger example-modal]]
   [kushi.ui.tooltip.core :refer [tooltip-attrs]]
   [kushi.ui.icon.mui.svg :as mui.svg]
   [kushi.ui.divisor.core :refer [divisor]]
   [kushi.ui.util :refer [as-str maybe keyed]]
   [kushi.ui.modal.core :refer [modal open-kushi-modal modal-close-button]]
   [clojure.string :as string]
   [kushi.ui.button.core :refer [button icon-button big-paw big-paw*]]
   [kushi.ui.icon.core :refer [icon]]
   [kushi.ui.defs :as defs]
   [kushi.css.media]))

(def vo defs/variants-ordered)

(defn resolve-variants [sym ks]
  (or (get vo sym)
      (get vo (get ks sym))
      (get-in vo [:defaults sym])
      sym))

(defn sym->option-key [sym]
  (keyword (str "-" sym)))

(def publics
  {'icon icon
   'button button})


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

;; Test and optimize
(defn variant-grid
  "Prduces a 1 or 2d variant grid"
  [uic demo]
  (let [{:keys [label variants]
         vks :variants-keys}
        demo

        [v-1d v-2d]
        variants]

    [:div (sx ".kpg-variant-grid-wrapper"
              :.flex-col-fs
              :gap--1rem)
     [section-label label]
     (if v-2d
       (into [:div (sx :.kpg-variant-grid-2d)]
             (for [a (resolve-variants v-2d vks)] 
               (into [:div (sx :.kpg-variant-grid-1d)]
                     (for [b (resolve-variants v-1d vks)]
                       [uic
                        (merge-attrs (sx :.xxxsmall)
                                     {(sym->option-key v-2d) a
                                      (sym->option-key v-1d) b})
                        "Bang"]))))

       (into [:div (sx :.kpg-variant-grid-1d)]
             (let [coll (resolve-variants v-1d vks)]
               (when (coll? coll)
                 (for [a coll]
                   [uic
                    (merge-attrs (sx :.xxxsmall)
                                 {(sym->option-key v-1d) (name a)})
                    "Bang"])))))]))


(defn- syms->publics
  "Converts symbols to values if they are present in `publics` def.
   (syms->publics '[:div [icon :pets]])
   =>
   [:div [#Object[kushi.ui.icon.core] :pets]]]"
  [coll]
  (walk/postwalk (fn [x]
                   (if (symbol? x)
                     (get publics x :div.debug-red)
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
   {:keys [examples opt-sym snippets-header] :as opt}]
  (let [{reqs-for-examples :require}
         (meta examples)]
   {:modal-id            (str "button-" "wtf" "-variants")
    ;; change to uic-label
    :component-label     (str uic-name)
    :label               [section-label (name opt-sym)]
    :snippets-header     snippets-header
    :snippets            examples
    :reqs-for-uic        reqs-for-uic
    :reqs-for-examples   reqs-for-examples}))

(def modal-dbg? true)

(defn discrete-examples-grid2
  "Supports literal hiccup in the examples"
  [uic 
   opt
   samples]
  (when (seq samples)
    (let [hic        (for [sample samples]
                       (syms->publics sample))
          
          modal-opts (assoc (modal-opts* uic opt)
                            :hiccup-for-examples
                            hic
                            :wrapper-tag
                            (when modal-dbg? :div))]
      [:div (sx ".kpg-example-grid-wrapper"
                :.flex-col-fs :gap--1rem)
       [section-label (-> samples meta :label)]
       [:<>
        [example-modal-trigger (:modal-id modal-opts)]
        [example-modal modal-opts]]
       (into [:div (sx :.kpg-variant-grid-1d)]
             hic)])))

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
    [_ demo] :demo}]
  (!? (keyed [opts demo]))
  {:demo        demo
   :opts         (apply array-map opts)
   :toks         (get dtoks-by-usage uic-ns)
   :reqs-for-uic (reqs-for-uic* uic-ns)
   :uic-name     uic-name
   ;; change to uic-fn
   :component-fn button})

(defn showcase [ui]
  (let [uic (-> button var meta uic*)]

    [big-paw (merge-attrs (sx :c--green) {:-size :xxxlarge})
     [icon :pets]]


    #_[:div 
       [button {:-colorway :blue
                :-shape    :red
                :-size    :red
                :id        :gray
                :class     "gold"
                :on-click  #(js/console.log "hi")}
        "Hi" "Bye"]]

    #_(into [:div 
             (sx ".kpg-component-demos-wrapper"
                 :.flex-col-fs
                 :p--4rem 
                 :gap--3rem)
             [light-dark-mode-switch (sx :.fixed-block-start-inside 
                                         :.light
                                         :.transition)]]

            (for [[opt-sym 
                   {{:keys [variants samples]} :demo
                    :as opt}]
                  (:opts uic)

                  :when
                  (= opt-sym 'end-enhancer)]
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
                 samples]
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

