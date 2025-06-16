;; Experimental, unused for now
;; TODO - rewrite this ns with reframe

(ns kushi.playground.tweak.element
  (:require [clojure.string :as string]
            [clojure.edn :as edn]
            [clojure.spec.alpha :as s]
            [kushi.core :refer [sx defcss merge-attrs]]
            [kushi.specs2 :as specs2]
            [kushi.css.shorthand :as shorthand]
            [kushi.ui.button.core :refer [button]]
            [kushi.ui.icon.core :refer [icon]]
            [kushi.ui.tooltip.core :refer [tooltip-attrs]]
            [domo.core :as domo]
            [kushi.ui.core :refer [extract]]
            [kushi.ui.slider.core]
            [kushi.ui.switch.core :refer [switch]]
            [kushi.ui.util :refer [find-index]]
            [kushi.ui.snippet.core :refer (copy-to-clipboard-button)]
            [domo.core :refer (copy-to-clipboard!)]
            ;; [kushi.ui.modal.core :refer (modal close-kushi-modal open-kushi-modal)]
            [applied-science.js-interop :as j]
            [reagent.dom :as rdom]))


(def variants-by-category
  {
   :size          [:xxxsmall :xxsmall :xsmall :small :medium :large :xlarge #_:xxlarge #_:xxxlarge]
   :size-expanded [:xxxsmall :xxsmall :xsmall :small :medium :large :xlarge :xxlarge :xxxlarge :xxxxlarge]
   :tracking      [:xxxtight
                   :xxtight
                   :xtight
                   :tight
                   :default-tracking
                   :loose
                   :xloose
                   :xxloose
                   :xxxloose]
   :elevation     [:elevated-0
                   :elevated-1
                   :elevated-2
                   :elevated-3
                   :elevated-4
                   :elevated-5]
   :kind          [:default :minimal :bordered :filled]
   :kind2         [:default :bordered :filled]
  ;;  :status   [:disabled]
   :contour         [:pill :rounded :sharp]
   :weight        [:thin
                   :extra-light
                   :light
                   :normal
                   :wee-bold
                   :semi-bold
                   :bold
                   :extra-bold]
   :semantic      [:neutral
                   :accent
                   :positive
                   :negative
                   :warning]

   :flex          [:flex-row-fs
                   :flex-row-c
                   :flex-row-fe
                   :flex-row-sa
                   :flex-row-se
                   :flex-row-sb
                   :flex-col-fs
                   :flex-col-c
                   :flex-col-fe
                   :flex-col-sa
                   :flex-col-se
                   :flex-col-sb]

   :display       [:block
                   :inline
                   :inline-block
                   :flex
                   :inline-flex
                   :grid
                   :inline-grid
                   :flow-root
                   :contents]
   })

;; (defclass kushi-devtools-guide
;;   :outline--2px:dashed:$purple-500
;;   :outline-offset---2px)

;; (defclass kushi-devtools-guide-outline
;;   :transition--outline:0s:linear)




;; Event listeners for tweaking

;; (js/document.body.addEventListener
;;  "mousemove"
;;  #(let [el             (js/document.elementFromPoint (.-clientX %) (.-clientY %))
;;         devtools-class "kushi-devtools-guide"]
;;     (when el
;;       (domo/add-class! el devtools-class "kushi-devtools-guide-outline")
;;       (el.addEventListener "mouseout"
;;                            (fn [_]
;;                              (domo/remove-class! el
;;                                                devtools-class))))
;;     #_(js/console.log el)))


;; (js/document.body.addEventListener
;;  "contextmenu"
;;  #(let [el          (domo/et %)
;;         rect        (.getBoundingClientRect el)
;;         [tb lr]     (domo/screen-quadrant-from-point (.-x rect) (.-y rect))
;;         ibs         (if (= tb :top)
;;                       (str (+ (.-y rect) (.-height rect)) "px")
;;                       (str (.-y rect) "px"))
;;         iis         (if (= lr :left)
;;                       (str (.-x rect) "px")
;;                       (str (+ (.-x rect) (.-width rect)) "px"))
;;         modal-el    (domo/el-by-id   "kushi-tweak-contextmenu")
;;         translate-x (if-not (= lr :left) "-100%" 0)
;;         translate-y (if-not (= tb :top) "-100%" 0)
;;         data-sx     el.dataset.sx]
;;     ;; TODO el.nodeValue set to data-sx
;;     (when data-sx
;;       (domo/set-style! modal-el "content" ibs))
;;     (.preventDefault %)
;;     (domo/set-style! modal-el "inset-block-start" ibs)
;;     (domo/set-style! modal-el "inset-inline-start" iis)
;;     (domo/set-style! modal-el "transform" (str "translate(" translate-x ", " translate-y ")"))
;;     (open-kushi-modal  "kushi-tweak-contextmenu")
;;     false))




;; Temp context menu for debugging
;; This would not live here but rather get injected dynamically
;; [modal (sx :.elevated-4!
;;           :border--1px:solid:$neutral-300
;;           :border-radius--3px
;;           :box-shadow--none
;;           {:id             "kushi-tweak-contextmenu"

;;            ;; Heads up this :-context-menu? option was removed 1.0.0-a.20

;;            :-context-menu? true})

;; [:ul
;;   [:li [button (sx :.minimal :.pill)
;;         [icon :tune]
;;         "Tweak"]]
;;   [:li [button (sx :.minimal :.pill {:on-click close-kushi-modal})
;;         [icon :close]
;;         "Cancel"]]]]



(def utility-family-label-by-key
  {:flex      "Flexbox"
   :display   "Display"
   :size      "Type size"
   :weight    "Type weight"
   :semantic  "Semantic"
   :kind      "Kind"
   :tracking  "Type tracking"
   :elevation "Elevation"
   :contour     "Shape"})

(defn highlight-tweaked-label!
  [e a b]
  (let [control-el (domo/nearest-ancestor (domo/et e) ".tweaker-control-row")
        a (if (s/valid? ::specs2/s|kw a) (name a) a)
        b (if (s/valid? ::specs2/s|kw b) (name b) b)]
    (if (= a b)
      (domo/remove-class! control-el "highlight-tweaked" "info")
      (domo/add-class! control-el "highlight-tweaked" "info"))))

(defn profile-from-el [e]
  (some-> e
          domo/et
          (domo/nearest-ancestor "[data-kushi-tweak]")
          .-dataset
          (j/get :kushiTweak)
          edn/read-string))

(defn copy-to-clipboard-fn [e]
  (let [text (some-> e
                     domo/et
                     (domo/nearest-ancestor ".kushi-slider-single-value-label-wrapper")
                     .-firstChild
                     .-textContent)
        profile (profile-from-el e)
        {:keys [category
                og-prop
                og-single-value]} profile]

    (case category

      :class
      (str ":." text)

      :tokenized-style
      (keyword (str og-prop "--" text))

      :style-tuple
      (let [val (cond
                  (keyword? og-single-value)
                  (keyword text)
                  :else
                  text)]
        [og-prop val])
      nil
      )))


(defn flex-row-icon-inner []
  [:div (sx :h--100% :w--3px :bgc--$neutral-800)])

(defn flex-col-icon-inner []
  [:div (sx :w--100% :h--3px :bgc--$neutral-800)])

(defn select-flex-thumb [e flex-class]
  (let [node           (.-currentTarget e)
        control        (domo/nearest-ancestor node ".tweaker-control-row")
        siblings       (.querySelectorAll control ".flex-option-thumb")
        select-target  (.querySelector control (str ".flex-option-thumb." (name flex-class)))]

    (doseq [sibling siblings]
      (domo/set-attribute! sibling "aria-selected" "false") )
    (domo/set-attribute! select-target "aria-selected" "true")))

(defn flex-options-click [e]
  (let [node    (.-currentTarget e)
        control (domo/nearest-ancestor (domo/et e) ".tweaker-control-row")
        label   (.querySelector control ".kushi-slider-single-value-label")]

    (select-flex-thumb e flex-class)
    (highlight-tweaked-label! e og-flex-class flex-class)
    (j/assoc! label :textContent (name flex-class))

    (doseq [el target-els]
      (apply domo/remove-class! el family-classes)
      (apply domo/remove-class! el (:display variants-by-category))
      (domo/add-class! el flex-class))))

(defn flex-options
  (let [{:keys [opts attrs children]} (extract args label)
        {:keys [og-flex-class target-els family-classes starts-with]} opts]
    (into [:div (sx :.flex-row-sa  :p--2px :gap--1rem)]
          (for [flex-class (filter #(string/starts-with? (name %)
                                                         starts-with)
                                   (:flex variants-by-category))
                :let [og? (= (name flex-class) (name og-flex-class))
                      og-class (when og? "og-value")]]
            [:div (merge-attrs
                   (sx :.flex-option-thumb
                       :p--relative
                       flex-class
                       og-class
                       :w--30px
                       :h--30px
                       :border--1px:solid:silver
                       :.neutral-bg
                       :cursor--pointer
                       :gap--2px
                       ["[aria-selected='true']:bgc" :$accent-100]
                       ["[aria-selected='true']>div:bgc" :$accent-800])
                   {:role          :button
                    :tab-index     0
                    :aria-selected og?
                    :on-click      flex-options-click}
                   (tooltip-attrs {:text      (str ":." (name flex-class))
                                   :placement :top}))
             children]))))


(defn flex-picker
  [{:keys [target-els profile] :as opts}]
  (let [{:keys         [utility-family]
         og-flex-class :classname}        profile
        family-classes                    (->> variants-by-category
                                               utility-family
                                               (map name))
        opts                              {:-og-flex-class  og-flex-class
                                           :-target-els     target-els
                                           :-family-classes family-classes}]
    [:div.flex-row-fs
     (sx :pi--1em:2em)
     [:div (merge-attrs
            (sx :.kushi-slider-single-value-label-wrapper
                :.tweakable-label
                :.flex-row-sb
                :.no-shrink
                :flex-basis--50px
                :pie--0.75em)
            #_labels-attrs)
      [:label (sx :.kushi-slider-single-value-label
                  :ws--n)
       og-flex-class]
      [copy-to-clipboard-button
       (merge-attrs
        (sx :.kushi-slider-single-value-label-copy-to-clipboard-button
            :>button:h--$medium
            :m--0)
        {:placement :left
         ;; :on-click   #(copy-to-clipboard! (.-textContent (.-firstChild (domo/nearest-ancestor (domo/et %) ".kushi-slider-single-value-label-wrapper"))))
         :on-click   #(copy-to-clipboard! (copy-to-clipboard-fn %))})]]

     [:div.flex-row-sa.grow.flex-option-thumbs
      [flex-options
       (assoc opts :-starts-with "flex-row")
       [flex-row-icon-inner]
       [flex-row-icon-inner]
       [flex-row-icon-inner]]

      [flex-options
       (assoc opts :-starts-with "flex-col")
       [flex-col-icon-inner]
       [flex-col-icon-inner]
       [flex-col-icon-inner]]]]))


(defn semantic-slider
  [{:keys [target-els classes css-property default-value profile] :as opts}]
  (let [{:keys [utility-family]} profile
        family-classes    (->> variants-by-category utility-family (map name))
        family-rules      (into {}
                                (mapv (fn [class]
                                        (let [mock-el        
                                              (js/document.createElement "div")

                                              _              
                                              (doto mock-el
                                                (.setAttribute "class" class)
                                                (.setAttribute "style" "display: none;"))

                                              _              
                                              (js/document.body.appendChild mock-el)

                                              resolved-value 
                                              (.getPropertyValue
                                               (js/window.getComputedStyle mock-el)
                                               css-property)

                                              _              
                                              (.remove mock-el)]
                                          [class resolved-value]))
                                      family-classes))
        current-computed  (.getPropertyValue
                           (js/window.getComputedStyle (first target-els))
                           css-property)
        og-class          (or (first (filter #(contains? (into #{}
                                                               family-classes)
                                                         %)
                                             classes))
                              (first (keep (fn [[k v]]
                                             (when (= current-computed v) k))
                                           family-rules))
                              default-value)
        og-class-idx      (find-index #(= % og-class) family-classes)
        og-class          og-class]

    [kushi.ui.slider.core/slider
     {:-copy-to-clipboard-fn copy-to-clipboard-fn
      :-steps                family-classes
      :-step-marker          :dot
      :-label-size-class     :medium
      :-labels-attrs         (sx :.tweakable-label)
      :-display-step-labels? false
      :-default-index        og-class-idx
      :default-value         og-class
      :data-kushi-tweak-og   (str {:og-value og-class
                                   :og-idx og-class-idx
                                   :family-classes family-classes})
      :on-change             (fn [e]
                               (let [updated-class-idx   (domo/etv->int e)]
                                 (highlight-tweaked-label! e
                                                           og-class-idx
                                                           updated-class-idx)
                                 (doseq [el target-els]
                                   (apply domo/remove-class! el family-classes)
                                   (domo/add-class! el 
                                                    (nth family-classes
                                                         (js/parseInt (domo/etv e)))))))}]))




(defn slider [{:keys [css-prop css-value target-els unit-type]
               :as   m}]
  (let [max (case unit-type
              :px 1000
              :em 7
              :rem 7
              :%  100)
        step (case unit-type
               :em 0.05
               :rem 0.05
               1)
        og-idx  (cond (contains? #{:rem :em} unit-type)
                      (* (/ (js/parseFloat css-value) 7)
                         (/ max step))
                      :else
                      (js/parseInt css-value))
        og-class  "original-tweakable-value"
        tweaked-class  "tweaked-value-row"
        ]
    [kushi.ui.slider.core/slider
     (sx :w--100%
         {:-copy-to-clipboard-fn copy-to-clipboard-fn
          :-step-label-suffix    (name unit-type)
          :-label-size-class     :xxsmall
          :-labels-attrs         (sx :.tweakable-label)
          :-default-index        og-idx
          :default-value         css-value
          :step                  step
          :min                   0
          :max                   max
          :data-kushi-tweak-og   (str {:og-value css-value :og-idx og-idx})
          :on-change             (fn [e]
                                   (let [idx   (domo/etv->int e)]
                                     (highlight-tweaked-label! e og-idx idx)
                                     (doseq [el target-els]
                                       (let [value (* idx step)]
                                         (domo/set-style! el
                                                          css-prop
                                                          (str value
                                                               (name unit-type)))))))
          :class                 [og-class]})]))

(defcss ".tweakable-css-prop-label"
  [:after:content "\":\""]
  [:after:color :$neutral-foreground]
  [:dark:after:color :$neutral-foreground-dark-mode])

(defcss ".tweakable-label"
  :.xsmall
  :.wee-bold
  :>label:padding--0.25em:0.5em
  :>label:border-radius--$rounded
  :ff--$code-font-stack)

(defcss ".highlight-tweaked"
  [:_.tweakable-label>label:bgi "linear-gradient(to right, var(--magenta-100), var(--blue-100))"])

(defn control
(let [{:keys [opts attrs children]} (extract args label)
  (let [{:keys [profile]}            
        opts

        {:keys [css-prop
                utility-family
                control-type
                classname]}          
        profile

        utility-family-control-label
        (when utility-family
          (utility-family utility-family-label-by-key))

        tweakables
        (js->clj (.from js/Array
                        (js/document.querySelectorAll "[data-sx-tweak]")))

        utility-family-class
        (when utility-family "utility-family-control")
        
        tweakable-css-prop-label-class
        (when css-prop :.tweakable-css-prop-label)

        italic-class
        (when-not css-prop :.italic)
        
        utility-family-class
        (if utility-family :.xsmall :.xxsmall)]

    [:li (merge-attrs 
          (sx :.tweaker-control-row
              :position--relative
              :.flex-row-fs
              utility-family-class
              :_.kushi-slider:pie--1rem
              :_.kushi-slider-single-value-label-wrapper:flex-basis--180px
              :.tweak-off_.tweakable-label>label:td--line-through
              :.tweak-off_.tweak-off-mask:d--block)
          {:data-kushi-tweak (str opts)})
     [:div (sx :.tweak-off-mask
               :.absolute-fill
               :zi--1000
               :d--none
               :cursor--not-allowed
               [:w "calc(100% - 27px)"]
               :bgc--$transparent-white-50)]
     [:label (sx :.tweaker-control-label
                 :min-width--180px
                 [:ff (when css-prop :$code-font-stack)]
                 tweakable-css-prop-label
                 italic-class
                 utility-family-class
                 :fw--$wee-bold)
      (or css-prop utility-family-control-label)]
     [:div (sx :.grow) children]
     [button (sx :.kushi-reset-tweakable
                 :.minimal
                 :.pill
                 :p--0
                 :outline--3px:solid:transparent
                 :hover:outline-color--$neutral-background-color
                 :dark:hover:outline-color--$neutral-background-color-dark-mode)
      [icon {:on-click #(cond

                          (contains? #{flex-picker} control-type)
                          (let [control       (domo/nearest-ancestor (domo/et %) ".tweaker-control-row")
                                label         (.querySelector control ".tweakable-label>label")
                                cur-classname (j/get label :textContent)
                                tweakables    (js->clj (.from js/Array (js/document.querySelectorAll "[data-sx-tweak]")))]

                            (highlight-tweaked-label! %  classname classname)
                            (j/assoc! label :textContent (name classname))
                            (select-flex-thumb % classname))

                          :else
                          (let [control    (domo/nearest-ancestor (domo/et %) ".tweaker-control-row")
                                slider     (.querySelector control ".kushi-slider")
                                input      (.querySelector control ".kushi-slider input")
                                step       (js/parseFloat (.-step input))
                                multiplier (if (< 0 step 1) (/ 1 step) 1)
                                label      (.querySelector control ".kushi-slider .kushi-slider-single-value-label")
                                og         (some-> input
                                                   .-dataset
                                                   (j/get :kushiTweakOg)
                                                   edn/read-string)
                                cur-idx    (js/parseInt (.-value input))
                                og-idx     (js/parseInt (:og-idx og))
                                diff       (- cur-idx og-idx)]
                            (if (neg? diff)
                              (.stepUp input (* multiplier (js/Math.abs diff)))
                              (.stepDown input (* multiplier (js/Math.abs diff))))
                            (highlight-tweaked-label! % og-idx og-idx)
                            (j/assoc! label :textContent (:og-value og))
                            (cond

                              (contains? #{:tokenized-style :style-tuple} (:category profile))
                              (domo/set-style! tweakables (:css-prop profile) (:single-value profile))

                              (contains? #{:class} (:category profile))
                              (doseq [el tweakables]
                                (apply domo/remove-class! el (:family-classes og))
                                (domo/add-class! el (:og-value og))))))}
       :refresh]]
     [switch (sx :.small
                 :mis--1rem
                 {:on?     true
                  :on-click (fn [e]

                              (let [el          (domo/cet e)
                                    checked?    (= "true" (.getAttribute el "aria-checked"))
                                    control     (domo/nearest-ancestor (domo/et e) ".tweaker-control-row")
                                    label       (.querySelector control ".tweakable-label>label")
                                    current-val (.-textContent label)]

                                ((if checked? domo/remove-class! domo/add-class!) control :tweak-off)

                                (cond

                                  (contains? #{:class} (:category profile))
                                  (do (doseq [el tweakables]
                                        ((if checked? domo/add-class! domo/remove-class!) el current-val)))

                                  (contains? #{:tokenized-style :style-tuple} (:category profile))
                                  (do (domo/set-style! tweakables
                                                       (:css-prop profile)
                                                       (if checked? current-val "unset"))))))})]]))



(defn unit-type [x]
  (cond  (string/ends-with? x "px")
         :px
         (string/ends-with? x "rem")
         :rem
         (string/ends-with? x "em")
         :em
         (string/ends-with? x "%")
         :%)
  )

(defn- style-profile
  "For args that conform to ::specs2/style-tuple or :specs2/tokenized-keyword"
  [[p v]]
  (let [css-prop          (-> p keyword shorthand/key-sh name)
        css-value         (cond
                            (number? v)
                            [v]
                            (or (string? v) (keyword? v))
                            (map #(let [ret* (shorthand/val-sh (keyword %) (keyword css-prop))]
                                    (if (keyword? ret*) (name ret*) ret*))
                                 (string/split (name v) #":"))
                            :else
                            v)
        css-sh?           (< 1 (count css-value))
        single-value      (when-not css-sh? (first css-value))
        og-single-value   (when-not css-sh? v)
        kushi-prop-sh?    (when single-value (when (s/valid? ::specs2/s|kw p) (not= (name p) css-prop)))
        kushi-val-sh?     (when single-value (when (s/valid? ::specs2/s|kw v) (not= (name v) single-value)))
        value-category    (s/conform ::specs2/style-tuple-value v)
        css-value-scalar? (when (vector? value-category) (= (first value-category) :css-value-scalar))
        unit-type         (when css-value-scalar? (unit-type single-value))
        ]
    (merge
     (when kushi-prop-sh? {:kushi-prop-sh? kushi-prop-sh?})
     (when kushi-val-sh? {:kushi-val-sh v})
     {:css-prop          (-> p keyword shorthand/key-sh name)
      :kushi-prop-sh?    kushi-prop-sh?
      :kushi-val-sh?     kushi-val-sh?
      :og-prop           p
      :og-single-value   og-single-value
      :css-values        v
      :css-shorthand?    css-sh?
      :single-value      single-value
      :value-category    value-category
      :css-value-scalar? css-value-scalar?
      :unit-type         unit-type
      }))
  )

(def families
  [[:flex      (:flex variants-by-category)]
   [:display   (:display variants-by-category)]
   [:size      (:size-expanded variants-by-category)]
   [:weight    (:weight variants-by-category)]
   [:semantic  (:semantic variants-by-category)]
   [:kind      (:kind variants-by-category)]
   [:tracking  (:tracking variants-by-category)]
   [:elevation (:elevation variants-by-category)]
   [:contour     (:contour variants-by-category)]])

(defn class-arg-profile [arg]
  (let [kw             (-> arg name (subs 1) keyword)
        utility-family (some (fn [[utility-family coll]]
                               (when (contains? (into #{} coll) kw)
                                 utility-family))
                             families)]
    {:classname     (let [nm (-> arg name (subs 1) keyword)]
                      nm)
     :selector-text  (name arg)
     :utility-family utility-family
     :control-type   (case utility-family
                       :flex
                       flex-picker
                       semantic-slider)}))

;; Experimental
(defn ^:public element-tweaker! []
  (let [node        (or (.getElementById js/document "tweaker")
                        (js/document.createElement "div"))
        tweakable   (js/document.querySelector "[data-sx-tweak]")
        classes     (js->clj (js/Array.from tweakable.classList))
        args*       (edn/read-string (.getAttribute tweakable "data-sx-tweak"))
        conformed*  (s/conform ::specs2/sx-args-conformance args*)
        conformed   (into [] (map-indexed (fn [idx [category arg]]
                                            (let [ret* (case category

                                                         :class
                                                         (class-arg-profile arg)

                                                         :tokenized-style
                                                         (style-profile (-> arg name (string/split #"--")))

                                                         :style-tuple
                                                         (style-profile arg)

                                                         {})
                                                  ret  (assoc ret*
                                                              :arg
                                                              arg
                                                              :category
                                                              category
                                                              :og-idx
                                                              idx
                                                              :tweakable?
                                                              (or (= :class category)
                                                                  (and (contains? #{:tokenized-style :style-tuple} category)
                                                                       (:css-value-scalar? ret*))))]
                                              ret))
                                          conformed*))

        ;; target-class (some->>  classes
        ;;                        (filter #(string/starts-with? % "_"))
        ;;                        first)
        ;; target-els   (when target-class 
        ;;                (js->clj (.from js/Array (js/document.querySelectorAll (str "." target-class))) ))

        target-els              (js->clj (.from js/Array (js/document.querySelectorAll "[data-sx-tweak]")))
        og-target-els-styles    (map (fn [el] [el (.-cssText (.-style el))]) target-els)
        og-target-els-classname (mapv (fn [el] [el (.-className el)]) target-els)
        ]

    ;; (js/console.log target-rule.style)
    ;; (.setProperty target-rule.style "color" "yellow")

    (when target-els

      (doto node
        (.setAttribute "id" "tweaker") )

      (js/document.body.appendChild node)

      (domo/set-style! node "display" "block")

      (rdom/render
       [:form#kushi-playground-tweaker
        (sx :.fixed-block-start-inside
            :.elevated-3
            :bgc--white
            :p--3rem
            :b--1px:solid:silver
            :w--100%)
        (into [:ul
               (sx :.flex-col-fs
                   :gap--2em)]
              (concat
               (for [{:keys [tweakable?
                             css-prop
                             single-value
                             unit-type
                             category
                             enum-val-type?]
                      :as   profile} conformed
                     :when                         tweakable?
                     :let                          [css-value single-value]]
                 (cond

                   unit-type
                   [control
                    {:-css-prop css-prop
                     :-profile  profile}
                    [slider (merge {:css-prop   css-prop
                                    :css-value  css-value
                                    :target-els target-els
                                    :unit-type  unit-type})]]

                   (= category :class)
                   [control
                    {:-css-prop css-prop
                     :-profile  profile}
                    [(:control-type profile) {:target-els target-els
                                              :classes classes
                                              :profile profile}]]

                   :else
                   nil))

              ;;  (let [profile {:category :class}]
              ;;      [
              ;;       [control
              ;;        {:-utility-family "Semantic"}
              ;;        [semantic-slider (assoc (keyed target-els classes)
              ;;                                :css-property
              ;;                                "color"
              ;;                                :default-value
              ;;                                "neutral"
              ;;                                :category
              ;;                                :semantic )]]

              ;;       [control
              ;;        {:-utility-family "Flexbox"
              ;;         :-profile        profile}
              ;;        [semantic-slider (assoc (keyed target-els classes)
              ;;                                :css-property
              ;;                                "flex"
              ;;                                :default-value
              ;;                                "flex-row-fs"
              ;;                                :category
              ;;                                :flex)]]

              ;;       [control
              ;;        {:-utility-family "Type size"
              ;;         :-profile        profile}
              ;;        [semantic-slider (assoc (keyed target-els classes)
              ;;                                :css-property
              ;;                                "font-size"
              ;;                                :default-value
              ;;                                "medium"
              ;;                                :category
              ;;                                :size-expanded)]]

              ;;       [control
              ;;        {:-utility-family "Type weight"
              ;;         :-profile        profile}
              ;;        [semantic-slider (assoc (keyed target-els classes)
              ;;                                :css-property
              ;;                                "font-weight"
              ;;                                :default-value
              ;;                                "normal"
              ;;                                :category
              ;;                                :weight)]]
              ;;       [control
              ;;        {:-utility-family "Elevation"
              ;;         :-profile        profile}
              ;;        [semantic-slider (assoc (keyed target-els classes)
              ;;                                :css-property
              ;;                                "box-shadow"
              ;;                                :default-value
              ;;                                "elevated-0"
              ;;                                :category
              ;;                                :elevation)]]
              ;;       [control
              ;;        {:-utility-family "Type tracking"
              ;;         :-profile        profile}
              ;;        [semantic-slider (assoc (keyed target-els classes)
              ;;                                :css-property
              ;;                                "letter-spacing"
              ;;                                :default-value
              ;;                                "default-tracking"
              ;;                                :category
              ;;                                :tracking)]]

              ;;       ])

               ;; Close Modal, Reset, and copy code controls
               [
                [button
                 (sx :.minimal
                     :.pill
                     :.top-right-corner-inside)
                 {:on-click #(domo/set-style! (domo/el-by-id "tweaker")
                                              "display"
                                              "none")}
                 [icon :close]]

                #_[button
                   (sx :.xsmall
                       :w--fit-content
                       {:on-click (fn [_]
                                    (let [
                                        ;; reset-buttons (js->clj (.from js/Array 
                                        ;;                               (.querySelectorAll (domo/el-by-id "tweaker")
                                        ;;                                                  ".kushi-reset-tweakable")))

                                          reset-button (.querySelector (domo/el-by-id "tweaker")
                                                                       ".kushi-reset-tweakable")
                                          ]

                                      (js/console.log reset-button)
                                      (reset-button.click)

                                    ;; (js/console.log  reset-buttons)
                                    ;; (doseq [btn reset-buttons]
                                    ;;   (.click btn))
                                      )

                                      ;; (doseq [[el og] og-target-els-classname]
                                      ;;   (do 
                                      ;;     (domo/set-attribute! el "class" og)))
                                      ;; (doseq [[el og] og-target-els-styles]
                                      ;;   (do 
                                      ;;     (domo/set-attribute! el "style" og)))

                                    )})
                   [icon :refresh]
                   "Reset styles"]]
               ))]
       node))))
