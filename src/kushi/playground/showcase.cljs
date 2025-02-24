(ns ^:dev/always kushi.playground.showcase
  (:require
   [clojure.walk :as walk]
   [fireworks.core :refer [!? ?]]
   [kushi.core :refer [css defcss merge-attrs sx]]
   [kushi.css.build.design-tokens :as design-tokens]
   [kushi.playground.md2hiccup :refer [desc->hiccup]]
   [kushi.ui.button.core :refer [button]]
   [kushi.ui.divisor.core :refer [divisor]]
   [kushi.ui.icon.core :refer [icon]]
   [kushi.ui.spinner.core :refer [propeller]]
   [kushi.ui.util :refer [as-str maybe]]))

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


(defn discrete-examples-grid2 [uic m]
  (let [{:keys [label examples]
         option-name :name}
        m]
    [:div (sx ".kushi-playground-example-grid-wrapper"
              :.flex-col-fs
              :gap--1rem)
     [section-label label]
     (into [:div (sx :.kushi-playground-variant-grid-1d)]
           (for [example examples]
             (!? (syms->publics example))))]))


;; implement working modal with snippets generated from docs meta
(defn showcase [ui]
  (let [{ui-ns :ns
         :keys [opts desc demos]}
        (-> button var meta)

        [_ demos]
        demos

        [_ opts]
        opts

        toks    
        (!? (get (!? :pp design-tokens/design-tokens-by-component-usage)
                 ui-ns))]

    (into [:div (sx ".kushi-playground-component-demos-wrapper"
                    :.flex-col-fs
                    :p--4rem 
                    :gap--3rem) 
           
           [button {:-start-enhancer [icon :pets]} "Pets"]
           ]


           (for [opt opts
                 :when (= (:name opt) 'end-enhancer)]
             (cond
               (:variants opt)
               [variant-grid button opt]

               (:examples opt)
               [discrete-examples-grid2 button opt]
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

