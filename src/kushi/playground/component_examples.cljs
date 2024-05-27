(ns ^:dev/always kushi.playground.component-examples
  (:require [kushi.core :refer (sx merge-attrs keyed)]
            [kushi.playground.snippet :refer (component-details-popover)]
            [kushi.ui.button.core :refer [button]]
            [kushi.ui.icon.core :refer [icon]]
            [kushi.ui.popover.core :refer [popover-attrs]]
            [kushi.ui.tooltip.core :refer [tooltip-attrs]]
            [kushi.ui.util :refer [maybe]]
            [reagent.dom :as rdom]))

(defn- example-row-variant
  [component
   {:keys                                    [row-attrs
                                              variant-attrs
                                              examples]
    {sx-attrs     :evaled
     quoted-attrs :quoted}                   :sx-attrs
    :as                                      example-opts}]
  (into [:section (merge-attrs
                   (sx :.flex-row-fs
                       :gap--1rem
                       :pb--0.5rem)
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
                    poa           (popover-attrs
                                   (merge-attrs
                                    {:class "dark"
                                     :-f    (fn [popover-el]
                                              (rdom/render
                                               [component-details-popover 
                                                component
                                                merged-attrs*
                                                quoted-attrs]
                                               popover-el))}))
                    merged-attrs  (merge-attrs variant-attrs
                                               sx-attrs
                                               instance-attrs
                                               poa)]]
          (if instance-code 
            [:div 
             (sx :.flex-row-fs :gap--1rem)
             instance-code
             [button
              (merge-attrs
               (sx :.accent
                   :.pill
                   :.xxsmall
                   :.bold) 
               poa
               (tooltip-attrs {:-text "Click to view code" :-placement :r}))
              [icon :code]]]
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
  [:p (sx :.small
          :c--$neutral-secondary-fg
          :min-width--55px
          :w--fit-content
          :lh--1.7
          :mbe--0.5rem
          :&_span.code:mis--0.5ch)
   s])

(defn examples-section
  [{component      :component
    component-reqs :reqs
    :as   component-opts}
   {:keys        [desc]
    example-reqs :reqs
    example-component :component
    :or          {example-reqs []}
    :as          example-opts}]
  (let [component      (or example-component component)
        all-reqs       (into []
                             (concat component-reqs
                                     example-reqs))
        reqs-by-refers (reqs-by-refers all-reqs)
        label          (some-> desc
                               kushi.ui.util/backtics->hiccup
                               section-label)]
    (into [:section (sx :pb--1.5rem)
           label]
          (for [variant-attrs (resolve-variants-attrs component-opts
                                                      example-opts)]
            [example-row-variant
             component
             (merge example-opts
                    (keyed variant-attrs
                           reqs-by-refers))]))))


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


