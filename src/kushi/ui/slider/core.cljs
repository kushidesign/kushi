(ns kushi.ui.slider.core
  (:require
   [applied-science.js-interop :as j]
   [kushi.core :refer (sx defclass merge-attrs insert-style-tag!)]
   [kushi.ui.core :refer (opts+children)]
   [kushi.ui.slider.css]
   [kushi.ui.util :refer [range-of-floats find-index]]
   [kushi.playground.shared-styles]
   [kushi.playground.util :as util :refer-macros (keyed)]
   [kushi.ui.snippet.core :refer (copy-to-clipboard-button)]
   [domo.core :refer (copy-to-clipboard!)]
   [domo.core :as domo]))

(insert-style-tag! "kushi-slider-styles" kushi.ui.slider.css/css)

;; ----------------------------------------------------------------------------
;; Styles for marker-labels
;; ----------------------------------------------------------------------------

(defclass
  ^{:kushi/chunk :kushi/kushi-ui-defclass}
  kushi-slider-step-label-marker
  {:>span:v                                           :hidden
   :before:fw                                         :800
   :before:fs                                         :1.2rem
   :before:o                                          :0.7
   :before:position                                   :absolute
   :before:top                                        :50%
   :before:left                                       :50%
   :before:transform                                  "translate(-50%, -50%)"
   :&.kushi-slider-step-label-selected:before:content :unset})

(defclass
  ^{:kushi/chunk :kushi/kushi-ui-defclass}
  kushi-slider-step-label-marker-dot
  :.kushi-slider-step-label-marker
  {:before:fw :800
   :before:fs :1.2rem
   :before:o  :0.7})

(defclass
  ^{:kushi/chunk :kushi/kushi-ui-defclass}
  kushi-slider-step-label-marker-bar
  :.kushi-slider-step-label-marker
  {:before:fw :300
   :before:fs :0.8em
   :before:o  :1})

(defclass
  ^{:kushi/chunk :kushi/kushi-ui-defclass}
  kushi-slider-step-label-marker-none
  :.kushi-slider-step-label-marker
  {:before:fw :300
   :before:fs :0.8em
   :before:o  :1})

;; ----------------------------------------------------------------------------



(defn- slider-steps
  [steps* min max step]
  (map #(name (if (number? %) (str %) %))
       (or steps*
           (let [[min max] (when (and (number? min)
                                      (number? max)
                                      (< min max))
                             [min max])
                 ret       (if (int? step)
                             (range (or min 0) (inc (or max 10)) step)
                             (range-of-floats min max step))]
             ret))))

(defn- slider-default-index
  [supplied-idx v steps* steps]
  (let [ret* (or supplied-idx
                 (cond
                   (number? v)
                   v
                   (and steps*
                        (or (every? string? steps*)
                            (every? keyword? steps*))
                        (or (string? v) (keyword? v)))
                   (when-let [idx* (find-index #(= % (name v)) steps)]
                     idx*)
                   :else 0))
        ret (or ret* (js/Math.round (/ (-> steps count dec) 2)))]
    ret))


(defn- slider-labels
  [{:keys [steps
           supplied-steps?
           num-steps
           default-index
           label-selected-class
           label-scale-factor
           labels-attrs
           step-marker
           step-label-suffix
           display-step-labels?]}]
  (into [:div
         (merge-attrs
          (sx 'kushi-slider-step-labels
              :.flex-row-sa
              [:display (if display-step-labels? :flex :none)]
              :ai--c
              :.relative
              :w--100%
              :h--0
              {:style {:mbe "calc(10px + 0.5em)"
                       :width "100%"}})
          labels-attrs)]
        (let [ltr?     (= "ltr" (domo/writing-direction))
              last-int (dec num-steps)]
          (map-indexed
           (fn [idx step]
             (let [calcpct              (str "( (100% - var(--kushi-input-slider-thumb-width)) / " last-int ")")
                   inset-inline-start   (str "calc(" calcpct " * " idx "  + ( var(--kushi-input-slider-thumb-width) / 2)  )")
                   default-index?       (= idx default-index)
                   label-selected-class (when default-index? label-selected-class)
                   step-marker          (or step-marker :none)
                   step-marker-dot?     (= step-marker :dot)
                   step-marker-bar?     (= step-marker :bar)
                   step-marker-none?    (= step-marker :none)
                   step-marker-content  (when (contains? #{:dot :bar :none} step-marker)
                                          (case step-marker :dot "·" :bar "|" " "))
                   first?               (= idx 0)
                   last?                (= idx last-int)
                   left-most?           (or (and first? ltr?) (and last? (not ltr?)))
                   right-most?          (or (and first? (not ltr?)) (and last? ltr?))
                   ]
               [:span (sx 'kushi-slider-step-label
                          (when step-marker-dot? :.kushi-slider-step-label-marker-dot)
                          (when step-marker-bar? :.kushi-slider-step-label-marker-bar)
                          (when step-marker-none? :.kushi-slider-step-label-marker-none)
                          :.absolute
                          :.block
                          :.transition
                          :.flex-row-c
                          :ai--c
                          :c--currentColor
                          :ta--center
                          :>span:white-space--nowrap
                          {:class [label-selected-class]
                           :style {:inset-inline-start                           inset-inline-start
                                   :w                                            0
                                   :h                                            0
                                   :transform                                    (str "scale(" label-scale-factor ")")
                                   :&.kushi-slider-step-label-selected:transform "scale(1)"
                                   :&.kushi-slider-step-label-selected:o         1
                                   :&.kushi-slider-step-label-selected:c         :currentColor
                                   :&.kushi-slider-step-label-selected>span:v    :visible
                                   :before:content                               (when step-marker-content (str "\"" step-marker-content "\"")) }})
                [:span
                 (sx :.absolute-centered
                     [:translate (cond right-most?
                                       (str (if supplied-steps? "-67%" "-50%" ) " -50%")
                                       left-most?
                                       (str (if supplied-steps? "-33%" "-50%" ) " -50%")
                                       :else
                                       "-50% -50%")])
                 (str step step-label-suffix)]]))
           steps))))

(defn on-change [label-selected-class label-id e]
  (let [el                  (-> e .-target)
        val                 (js/parseInt (.-value el))
        parent              (.-parentNode el)
        previous-sibling    (.-firstChild parent)
        label-node          (.querySelector previous-sibling
                                            (str ":nth-child(" (+ 1 val) ")"))
        selected-label-node (.querySelector previous-sibling (str "." label-selected-class))]
    (.remove (.-classList selected-label-node) label-selected-class)
    (.add (.-classList label-node) label-selected-class)
    (j/assoc! (domo/el-by-id label-id) :textContent (.-textContent label-node))
    #_(domo/set-attribute! (domo/el-by-id label-id) "textContent" (.-textContent label-node))))

(defn slider
  {:desc ["A slider is a ui element which allows the user to specify a numeric value which must be no less than a given value, and no more than another given value."
          "By default, values are represented as a numeric scale with a `min` and a `max`."
          "Note that `:min`, `:max` and `:step` are passed down to the underlying `<input type=range>` element, and do not need to be written with the custom opts syntax."
          "Checkout [input range docs](https://developer.mozilla.org/en-US/docs/Web/HTML/Element/input/range) for info on how `min`, `max`, and `step` work."
          "Alternately, a scale of named, stepped values may be provided with the custom `:-steps` option."]
   :opts '[{:name    default-value
            :pred    any?
            :default (:text "The supplied `min` or the first item in the supplied `:-steps` collection.")
            :desc    "The initial, default value."}
           {:name    default-index
            :pred    int?
            :default 0
            :desc    "Use `default-index` when you want to set the default value by index. This is the index of the number in a numeric range (with a `min` and `max`), or the index of a value in a supplied `:-steps` collection"}
           {:name    steps
            :pred    vector?
            :default nil
            :desc    "Collection of step values."}
           {:name    step-marker
            :pred    #{:dot :bar :value :none}
            :default :none
            :desc    "Collection of step values."}
           {:name    label-scale-factor
            :pred    float?
            :default 0.7
            :desc    "Factor to scale down labels in range which are not selected. Must be positive float and <= 1.0."}
           {:name    wrapper-attrs
            :pred    map?
            :default nil
            :desc    "HTML attributes map applied to the outer containing div."}
           {:name    labels-attrs
            :pred    map?
            :default nil
            :desc    "HTML attributes map applied to the step labels containing div."}
           {:name    step-label-suffix
            :pred    string?
            :default nil
            :desc    "String to postpend to step value label, e.g. `\"px\"`"}
           {:name    display-step-labels?
            :pred    boolean?
            :default false
            :desc    "If set to `false`, step labels above slider will not be rendered."}
           {:name    copy-to-clipboard-fn
            :pred    fn?
            :default nil
            :desc    "This fn can be used to transform the string value when using a copy-to-clipboard button"}
           ]}
  [& args]
  (let [[opts
         attrs]
        (opts+children args)

        {:keys [defaultValue
                default-value
                min
                max
                step]}
        attrs

        {:keys  [default-index
                 label-scale-factor
                 wrapper-attrs
                 labels-attrs
                 step-marker
                 step-label-suffix
                 display-step-labels?
                 copy-to-clipboard-fn]
         steps* :steps
         :or    {display-step-labels? true}}
        opts

        supplied-steps?
        (boolean steps*)

        steps
        (slider-steps steps* min max (or step 1))

        num-steps
        (count steps)

        default-val
        (or defaultValue default-value)

        default-index
        (slider-default-index default-index default-val steps* steps)

        label-scale-factor
        (cond
          (= 1 label-scale-factor)
          1.0
          (and (float? label-scale-factor) (< label-scale-factor 1.0))
          label-scale-factor
          :else 0.6)

        label-selected-class
        "kushi-slider-step-label-selected"

        id
        (or (:id attrs) (gensym))

        label-id
        (str "label-for-" id)
        ]

    [:div (merge-attrs (sx 'kushi-slider
                           :.flex-col-c

                           ;; when not showing labels and using leading value label
                          ;;  :.flex-row-fs

                           :.relative
                          ;;  :ai--c
                           :ai--stretch
                           :w--100%
                           :pi--1em:2em)
                       wrapper-attrs)

     [slider-labels (keyed steps
                           supplied-steps?
                           num-steps
                           default-index
                           label-selected-class
                           label-scale-factor
                           labels-attrs
                           step-marker
                           step-label-suffix
                           display-step-labels?)]

     #_[:div (merge-attrs
              (sx 'kushi-slider-single-value-label-wrapper
                  :.flex-row-sb
                  :.no-shrink
                  :flex-basis--50px
                  :pie--0.75em)
              labels-attrs)
        [:label (sx 'kushi-slider-single-value-label
                    :ws--n
                    {:id  label-id
                     :for id})
         default-value]
        [copy-to-clipboard-button
         (sx 'kushi-slider-single-value-label-copy-to-clipboard-button
             :>button:h--$medium
             :m--0
             {:-placement :left
            ;; :on-click   #(copy-to-clipboard! (.-textContent (.-firstChild (domo/nearest-ancestor (domo/et %) ".kushi-slider-single-value-label-wrapper"))))
              :on-click   #(copy-to-clipboard! (copy-to-clipboard-fn %))})]]

     [:input (merge-attrs
              (sx {:style         {:w :100%}
                   :id            id
                   :data-kushi-ui :input.range
                   :type          :range
                   :on-change     (partial on-change label-selected-class label-id)})
              (assoc (or attrs {})
                     :defaultValue default-index
                     :min (str 0)
                     :max (str (-> steps count dec))
                     :step (or step 1)))]]))
