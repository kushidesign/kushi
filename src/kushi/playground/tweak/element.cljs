;; TODO rewrite this ns with reframe

(ns kushi.playground.tweak.element
  (:require [clojure.string :as string]
            [clojure.edn :as edn]
            [clojure.spec.alpha :as s]
            [kushi.core :refer [sx keyed defclass merge-attrs]]
            [kushi.specs2 :as specs2]
            [kushi.shorthand :as shorthand]
            [kushi.ui.button.core :refer [button]]
            [kushi.ui.icon.core :refer [icon]]
            [kushi.ui.tooltip.core :refer [tooltip-attrs]]
            [kushi.ui.dom :as dom]
            [kushi.ui.core :refer [defcom]]
            [kushi.ui.input.slider.core]
            [kushi.ui.input.switch.core :refer [switch]]
            [kushi.ui.util :refer [find-index]]
            [kushi.playground.demobox.defs :refer [variants-by-category]]
            [kushi.ui.snippet.core :refer (copy-to-clipboard-button)]
            [kushi.ui.dom :refer (copy-to-clipboard)]
            [applied-science.js-interop :as j]
            [reagent.dom :as rdom]
            ))

(def utility-family-label-by-key
  {:flex      "Flexbox"      
   :display   "Display"   
   :size      "Type size"      
   :weight    "Type weight"    
   :semantic  "Semantic"  
   :kind      "Kind"      
   :tracking  "Type tracking"  
   :elevation "Elevation" 
   :shape     "Shape"})

(defn highlight-tweaked-label!
  [e a b]
  (let [control-el (dom/nearest-ancestor (dom/et e) ".tweaker-control-row")
        a (if (s/valid? ::specs2/s|kw a) (name a) a)
        b (if (s/valid? ::specs2/s|kw b) (name b) b)]
    (if (= a b)
      (dom/remove-class control-el "highlight-tweaked" "info")
      (dom/add-class control-el "highlight-tweaked" "info"))))

(defn profile-from-el [e]
  (some-> e
          dom/et
          (dom/nearest-ancestor "[data-kushi-tweak]")
          .-dataset
          (j/get :kushiTweak)
          edn/read-string))

(defn copy-to-clipboard-fn [e] 
  (let [text (some-> e
                     dom/et
                     (dom/nearest-ancestor ".kushi-slider-single-value-label-wrapper")
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
        control        (dom/nearest-ancestor node ".tweaker-control-row")
        siblings       (.querySelectorAll control ".flex-option-thumb")
        select-target  (.querySelector control (str ".flex-option-thumb." (name flex-class)))]

    (doseq [sibling siblings]
      (dom/set-attribute! sibling "aria-selected" "false") ) 
    (dom/set-attribute! select-target "aria-selected" "true")))

(defcom flex-options
  (let [{:keys [og-flex-class target-els family-classes starts-with]} &opts]
    (into [:div (sx :.flex-row-sa  :p--2px :gap--1rem)]
          (for [flex-class (filter #(string/starts-with? (name %) starts-with) (:flex variants-by-category))
                :let [og? (= (name flex-class) (name og-flex-class))]]
            [:div (merge-attrs 
                   (sx 'flex-option-thumb
                       :.relative
                       (when og? :.og-value)
                       :w--30px
                       :h--30px
                       :border--1px:solid:silver
                       :.neutral-bg
                       :.pointer
                       :gap--2px
                       ["[aria-selected='true']:bgc" :$accent-100]
                       ["[aria-selected='true']>div:bgc" :$accent-800]
                       {:class         [flex-class]
                        :role          :button
                        :tab-index     0
                        :aria-selected og?
                        :on-click      (fn [e]
                                         (let [node    (.-currentTarget e)
                                               control (dom/nearest-ancestor (dom/et e) ".tweaker-control-row")
                                               label   (.querySelector control ".kushi-slider-single-value-label")]

                                           (select-flex-thumb e flex-class)
                                           (highlight-tweaked-label! e og-flex-class flex-class)
                                           (j/assoc! label :textContent (name flex-class))

                                           (doseq [el target-els]
                                             (apply dom/remove-class el family-classes)
                                             (apply dom/remove-class el (:display variants-by-category))
                                             (dom/add-class el flex-class))))})
                   (tooltip-attrs {:-text      (str ":." (name flex-class))
                                   :-placement :top}))
             &children]))))


(defn flex-picker
  [{:keys [target-els profile] :as opts}]
  (let [{:keys         [utility-family]
         og-flex-class :classname}        profile
        family-classes                    (->> variants-by-category utility-family (map name))
        opts                              {:-og-flex-class  og-flex-class
                                           :-target-els     target-els
                                           :-family-classes family-classes}]
    [:div.flex-row-fs 
     (sx :pi--1em:2em)
     [:div (merge-attrs
            (sx 'kushi-slider-single-value-label-wrapper
                :.tweakable-label
                :.flex-row-sb
                :.no-shrink
                :flex-basis--50px
                :pie--0.75em)
            #_labels-attrs)
      [:label (sx 'kushi-slider-single-value-label
                  :ws--n
                  #_{:id  label-id
                   :for id})
       og-flex-class]
      [copy-to-clipboard-button
       (sx 'kushi-slider-single-value-label-copy-to-clipboard-button
           :>button:h--$medium
           :m--0
           {:-placement :left
            ;; :on-click   #(copy-to-clipboard (.-textContent (.-firstChild (dom/nearest-ancestor (dom/et %) ".kushi-slider-single-value-label-wrapper"))))
            :on-click   #(copy-to-clipboard (copy-to-clipboard-fn %))})]]

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
                                        (let [mock-el        (js/document.createElement "div")
                                              _              (doto mock-el
                                                               (.setAttribute "class" class)
                                                               (.setAttribute "style" "display: none;"))
                                              _              (js/document.body.appendChild mock-el)
                                              resolved-value (.getPropertyValue (js/window.getComputedStyle mock-el) css-property)
                                              _              (.remove mock-el)
                                              ]
                                          [class resolved-value])) 
                                      family-classes))
        current-computed  (.getPropertyValue (js/window.getComputedStyle (first target-els)) css-property)
        og-class          (or (first (filter #(contains? (into #{} family-classes) %) classes))
                              (first (keep (fn [[k v]]
                                             (when (= current-computed v) k))
                                           family-rules))
                              default-value)
        og-class-idx      (find-index #(= % og-class) family-classes)
        og-class          og-class]

    [kushi.ui.input.slider.core/slider
     {:-copy-to-clipboard-fn copy-to-clipboard-fn
      :-steps                family-classes
      :-step-marker          :dot 
      :-label-size-class     :medium
      :-labels-attrs         (sx :.tweakable-label)
      :-display-step-labels? false
      :-default-index        og-class-idx
      :default-value         og-class
      :data-kushi-tweak-og   (str {:og-value og-class :og-idx og-class-idx :family-classes family-classes})
      :on-change             (fn [e]
                               (let [updated-class-idx   (dom/etv->int e)]
                                 (highlight-tweaked-label! e og-class-idx updated-class-idx)
                                 (doseq [el target-els]
                                   (apply dom/remove-class el family-classes)
                                   (dom/add-class el (nth family-classes (js/parseInt (dom/etv e)))))))}]))




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
   [kushi.ui.input.slider.core/slider
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
                                  (let [idx   (dom/etv->int e)] 
                                    (highlight-tweaked-label! e og-idx idx)
                                    (doseq [el target-els]
                                      (let [value (* idx step)] 
                                        (dom/set-style! el css-prop (str value (name unit-type)))))))
         :class                 [og-class]})]))

(defclass tweakable-css-prop-label 
  [:after:content "\":\""]
  [:after:color :$neutral-fg]
  [:dark:after:color :$neutral-fg-inverse])

(defclass tweakable-label 
  :.xsmall
  :.wee-bold
  :>label:padding--0.25em:0.5em
  :>label:border-radius--$rounded
  :ff--$code-font-stack)

(defclass highlight-tweaked 
  [:&_.tweakable-label>label:bgi '(linear-gradient "to right" "var(--magenta-100)" "var(--blue-100)")])

(defcom control
  (let [{:keys [profile]}            &opts
        {:keys [css-prop
                utility-family
                control-type
                classname]}          profile
        utility-family-control-label (when utility-family
                                       (utility-family utility-family-label-by-key))
        tweakables (js->clj (.from js/Array (js/document.querySelectorAll "[data-sx-tweak]")))]

    [:li (sx 'tweaker-control-row
             :.relative
             :.flex-row-fs
             (when utility-family :.utility-family-control)
             :&_.kushi-slider:pie--1rem
             :&_.kushi-slider-single-value-label-wrapper:flex-basis--180px
             :&.tweak-off&_.tweakable-label>label:td--line-through
             :&.tweak-off&_.tweak-off-mask:d--block
             {:data-kushi-tweak (str &opts)})
     [:div (sx 'tweak-off-mask
               :.absolute-fill
               :zi--1000
               :d--none
               :cursor--not-allowed
               [:w "calc(100% - 27px)"]
               :bgc--$white-transparent-50)]
     [:label (sx 'tweaker-control-label
                 (when css-prop :.tweakable-css-prop-label)
                 (when-not css-prop :.italic)
                 :min-width--180px
                 [:ff (when css-prop :$code-font-stack)] 
                 (if utility-family :.xsmall :.xxsmall)
                 :.wee-bold)
      (or css-prop utility-family-control-label)]
     [:div (sx :.grow) &children]
     [button (sx 'kushi-reset-tweakable
                 :.minimal
                 :.pill
                 :p--0
                 :outline--3px:solid:transparent
                 :hover:outline-color--$neutral-background-color
                 :dark:hover:outline-color--$neutral-background-color-inverse)
      [icon {:on-click #(cond

                          (contains? #{flex-picker} control-type)
                          (let [control       (dom/nearest-ancestor (dom/et %) ".tweaker-control-row")
                                label         (.querySelector control ".tweakable-label>label")
                                cur-classname (j/get label :textContent)
                                tweakables    (js->clj (.from js/Array (js/document.querySelectorAll "[data-sx-tweak]")))]

                            (highlight-tweaked-label! %  classname classname)
                            (j/assoc! label :textContent (name classname))
                            (select-flex-thumb % classname))

                          :else
                          (let [control    (dom/nearest-ancestor (dom/et %) ".tweaker-control-row")
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
                              (.stepUp input (* multiplier (abs diff)))
                              (.stepDown input (* multiplier (abs diff))))
                            (highlight-tweaked-label! % og-idx og-idx)
                            (j/assoc! label :textContent (:og-value og))
                            (cond 

                              (contains? #{:tokenized-style :style-tuple} (:category profile))
                              (dom/set-style! tweakables (:css-prop profile) (:single-value profile))

                              (contains? #{:class} (:category profile))
                              (doseq [el tweakables]
                                (apply dom/remove-class el (:family-classes og))
                                (dom/add-class el (:og-value og))))))}
       :refresh]]
       [switch (sx :.small
                   :mis--1rem
                   {:-on?     true
                    :on-click (fn [e]

                                (let [el          (dom/cet e)
                                      checked?    (= "true" (.getAttribute el "aria-checked"))
                                      control     (dom/nearest-ancestor (dom/et e) ".tweaker-control-row")
                                      label       (.querySelector control ".tweakable-label>label")
                                      current-val (.-textContent label)] 

                                  ((if checked? dom/remove-class dom/add-class) control :tweak-off)

                                  (cond 

                                    (contains? #{:class} (:category profile))
                                    (do (doseq [el tweakables]
                                          ((if checked? dom/add-class dom/remove-class) el current-val)))

                                    (contains? #{:tokenized-style :style-tuple} (:category profile))
                                    (do (dom/set-style! tweakables 
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
   [:shape     (:shape variants-by-category)]])

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

      (dom/set-style! node "display" "block")

      (rdom/render
       [:form#kushi-playground-tweaker
        (sx :.fixed-block-start
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
                    [slider (merge (keyed css-prop css-value target-els unit-type))]]

                   (= category :class)
                   [control 
                    {:-css-prop css-prop
                     :-profile  profile}
                    [(:control-type profile) (keyed target-els classes profile)]]
                   
                   :else
                   nil))

               #_(let [profile {:category :class}]
                   [
                    [control 
                     {:-utility-family "Semantic"}
                     [semantic-slider (assoc (keyed target-els classes)
                                             :css-property
                                             "color"
                                             :default-value
                                             "neutral"
                                             :category     
                                             :semantic )]]

                    [control 
                     {:-utility-family "Flexbox"
                      :-profile        profile}
                     [semantic-slider (assoc (keyed target-els classes)
                                             :css-property
                                             "flex"
                                             :default-value
                                             "flex-row-fs"
                                             :category     
                                             :flex)]]

                    [control 
                     {:-utility-family "Type size"
                      :-profile        profile}
                     [semantic-slider (assoc (keyed target-els classes)
                                             :css-property
                                             "font-size"
                                             :default-value
                                             "medium"
                                             :category     
                                             :size-expanded)]]

                    [control 
                     {:-utility-family "Type weight"
                      :-profile        profile}
                     [semantic-slider (assoc (keyed target-els classes)
                                             :css-property
                                             "font-weight"
                                             :default-value
                                             "normal"
                                             :category     
                                             :weight)]]
                    [control 
                     {:-utility-family "Elevation"
                      :-profile        profile}
                     [semantic-slider (assoc (keyed target-els classes)
                                             :css-property
                                             "box-shadow"
                                             :default-value
                                             "elevated-0"
                                             :category     
                                             :elevation)]]
                    [control 
                     {:-utility-family "Type tracking"
                      :-profile        profile}
                     [semantic-slider (assoc (keyed target-els classes)
                                             :css-property
                                             "letter-spacing"
                                             :default-value
                                             "default-tracking"
                                             :category     
                                             :tracking)]]
                    
                    ])

               ;; Close Modal, Reset, and copy code controls
               [
                [button
                 (sx :.minimal
                     :.pill
                     :.northeast-inside
                     {:on-click #(dom/set-style! (dom/el-by-id "tweaker") "display" "none")})
                 [icon :close]]

                #_[button 
                 (sx :.xsmall
                     :w--fit-content
                     {:on-click (fn [_]
                                  (let [
                                        ;; reset-buttons (js->clj (.from js/Array 
                                        ;;                               (.querySelectorAll (dom/el-by-id "tweaker")
                                        ;;                                                  ".kushi-reset-tweakable")))

                                        reset-button (.querySelector (dom/el-by-id "tweaker")
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
                                      ;;     (dom/set-attribute! el "class" og)))
                                      ;; (doseq [[el og] og-target-els-styles]
                                      ;;   (do 
                                      ;;     (dom/set-attribute! el "style" og)))

                                  )})
                 [icon :refresh]
                 "Reset styles"]]
               ))]
       node))))
