(ns ^:dev/always kushi.playground.component-examples
  (:require [fireworks.core :refer [? pprint]]
            [clojure.string :as string]
            ;; [clojure.walk :as walk]
            [domo.core :refer (copy-to-clipboard!)]
            [kushi.core :refer (sx merge-attrs keyed)]
            [kushi.playground.util :as util]
            [kushi.playground.component-docs :as docs]
            [kushi.ui.button.core :refer (button)]
            [kushi.ui.core :refer (defcom)]
            [kushi.ui.icon.core :refer (icon)]
            [kushi.ui.icon.mui.svg :as mui.svg]
            [kushi.ui.modal.core :refer [modal open-kushi-modal close-kushi-modal]]
            [kushi.ui.tooltip.core :refer (tooltip-attrs)]
            [kushi.ui.util :refer [maybe]]
            [domo.core :as domo]))

(defn- example-row-variant
  [component
   {:keys                                    [row-attrs
                                              containier-attrs
                                              variant-attrs
                                              examples]
    {sx-attrs     :evaled
     quoted-attrs :quoted}                   :sx-attrs
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
  [:p (sx 'example-section-label
          :.serif
          :.bold
          :.neutralize-secondary
          :dark:fw--$wee-bold
          :fs--16.75px
          ;; :min-width--55px
          ;; :w--fit-content
          :lh--1.7
          :&_span.code:mis--0.5ch)
   s])


(declare component-snippets)

(defn examples-section
  [{component       :component
    component-reqs  :reqs
    component-label :label
    :as             component-opts}
   {:keys             [desc container-attrs snippets-header snippets]
    example-desc      :desc
    example-reqs      :reqs
    example-component :component
    :or          {example-reqs []}
    :as          example-opts}]
  (let [component      (or example-component component)
        all-reqs       (into []
                             (concat component-reqs
                                     example-reqs))
        reqs-by-refers (reqs-by-refers all-reqs)
        label          (some-> example-desc
                               kushi.ui.util/backtics->hiccup
                               section-label)
        modal-id       (str component-label "__" example-desc)]
    [:div (sx :.playground-example-row-container
              :pb--1.5rem
              :first-of-type:pbs--2.5rem
              ;; Hack to conditionally hide things here, like if they should not be shown on mobile
              ["has([data-kushi-playground-example='popover-with-form']):display" :none]
              ["xsm:has([data-kushi-playground-example='popover-with-form']):display" :block])
     [:section (sx :.playground-example-row
                  ;; make this max-width global var
                   :max-width--605px)
      [:div (sx :.flex-row-fs
                :flex-wrap--wrap
                :mbe--1rem
                :gap--1em)
       label
       [button
        (sx :.minimal
            :.pill
            :.accent
            :.xxsmall
            :.wee-bold
            {:on-click (fn* [] (open-kushi-modal modal-id))})
        [icon (sx :.small :.extra-bold) :code]
        "Get the code"]
       [modal (sx :&_.kushi-modal-inner:pi--1.25em
                  :xsm:&_.kushi-modal-inner:pi--2.5em
                  :&_.kushi-modal-inner:pb--1.75em:2em
                  :xsm:&_.kushi-modal-inner:pb--2.25em:2.75em
                  :$modal-min-width--200px
                  :width--605px
                  {:id modal-id})
        [component-snippets component-reqs
         snippets-header
         snippets]]]

      
      (into [:div (merge-attrs
                   (sx :.grid
                       :gtc--1fr
                       :gap--1rem)
                   container-attrs)]
            (for [variant-attrs (resolve-variants-attrs component-opts
                                                        example-opts)]
              [example-row-variant
               component
               (merge example-opts
                      (keyed variant-attrs
                             reqs-by-refers))]))]]

    #_(into [:section (sx :.playground-example-row
                          :pb--1.5rem
                          :first-of-type:pbs--3.5rem)
             label]
            )))


;;; New snippet code


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
        {:on-click #(copy-to-clipboard!
                     (or (some->> &opts 
                                  :clipboard-parent-sel
                                  (domo/nearest-ancestor (domo/et %)))
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
                :first-of-type:mbe--2.5em) 
   header
   [:section 
    (sx :.relative
        :.code
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


(defn component-snippets
  []
  (fn [reqs-coll
       snippets-header
       snippets]
    [:div
     (sx :.relative
         :.flex-row-fs
         :fs--small
         :xsm:fs--medium
         :&_code:fs--0.95em
         :&_.code:fs--0.95em
         :&_code:ws--n
         :lh--1.7
         :ai--fs
         :min-width--200px
         :min-height--120px)

     [:div
      (sx 
          :.flex-col-fs
          :gap--1em
          :&_.kushi-text-input-label:min-width--7em
          :&_.kushi-input-inline:gtc--36%:64%)
      (let [formatted*    #(-> % (pprint {:max-width 50}) with-out-str) ]
        (into [:div (sx :.flex-col-fs :gap--1rem)
               [snippet-section
                {:header       (util/desc->hiccup
                                ["Paste into the `:require` section of your `:ns` form:"])
                 :preformatted (-> reqs-coll
                                   formatted*
                                   (subs 1)
                                   (drop-last)
                                   string/join
                                   formatted-code)
                 :copyable     (string/join "\n" reqs-coll)}]]

              (for [[i call] (map-indexed (fn [i call] [i call]) snippets)
                    :let [header (when (zero? i) 
                                   (some-> snippets-header
                                           util/desc->hiccup 
                                           docs/add-links))]]
                [snippet-section
                 {:header       header
                  :preformatted (-> call
                                    formatted*
                                    string/join
                                    formatted-code)
                  :copyable     (string/join "\n" call)}])))]]))


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


