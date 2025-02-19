(ns ^{:kushi/layer "kushi-ui-styles"}
  kushi.ui.slider.core
  (:require
   [applied-science.js-interop :as j]
   [kushi.core :refer (css defcss css-vars-map merge-attrs)]
   [kushi.ui.core :refer (opts+children)]
   [kushi.ui.slider.css]
   [kushi.ui.util :refer [range-of-floats find-index]]
   ;; have bearing on slider?
   [kushi.playground.shared-styles]
   [domo.core :as domo]))


;; ----------------------------------------------------------------------------
;; Styles for marker-labels
;; ----------------------------------------------------------------------------

(defcss ".kushi-slider-step-label-marker"
  {:>span:v                                           :hidden
   :before:fw                                         :800
   :before:fs                                         :1.2rem
   :before:o                                          :0.7
   :before:position                                   :absolute
   :before:top                                        :50%
   :before:left                                       :50%
   :before:transform                                  "translate(-50%, -50%)"
   :.kushi-slider-step-label-selected:before:content :unset})

(defcss
  ".kushi-slider-step-label-marker-dot"
  :.kushi-slider-step-label-marker
  {:before:fw :800
   :before:fs :1.2rem
   :before:o  :0.7})

(defcss
  ".kushi-slider-step-label-marker-bar"
  :.kushi-slider-step-label-marker
  {:before:fw :300
   :before:fs :0.8em
   :before:o  :1})

(defcss
  ".kushi-slider-step-label-marker-none"
  :.kushi-slider-step-label-marker
  {:before:fw :300
   :before:fs :0.8em
   :before:o  :1})


;; ----------------------------------------------------------------------------
;; Cross-browser styles for slider track, thumb, outline, background etc.
;; ----------------------------------------------------------------------------

(defcss ":root" 
  {:--kushi-input-slider-track-background-color      :silver
   :--kushi-input-slider-thumb-width                 :48px
   :--kushi-input-slider-thumb-height                :48px
   :--kushi-input-slider-thumb-border-radius         :$kushi-input-slider-thumb-width
   :--kushi-input-slider-thumb-margin-top            "calc( var(--kushi-input-slider-thumb-height) / -2)"
   :--kushi-input-slider-thumb-outline-width-ratio   :3
   :--kushi-input-slider-thumb-outline-color         :#000
   :--kushi-input-slider-thumb-outline-color-dark    :#fff
   :--kushi-input-slider-thumb-background-color      :#fff
   :--kushi-input-slider-thumb-background-color-dark :#000
   :--kushi-input-slider-thumb-outline-style         :solid
   :--kushi-input-slider-thumb-outline-width         "calc( var(--kushi-input-slider-thumb-width) / var(--kushi-input-slider-thumb-outline-width-ratio))"
   :--kushi-input-slider-thumb-outline-offset        "calc( var(--kushi-input-slider-thumb-width) / (0 - var(--kushi-input-slider-thumb-outline-width-ratio)))"
   :--kushi-input-slider-thumb-outline               "var(--kushi-input-slider-thumb-outline-width) var(--kushi-input-slider-thumb-outline-style, solid) var(--kushi-input-slider-thumb-outline-color)"
   :--kushi-input-slider-thumb-outline-dark          "var(--kushi-input-slider-thumb-outline-width) var(--kushi-input-slider-thumb-outline-style, solid) var(--kushi-input-slider-thumb-outline-color-dark)"})

(defcss
  "input.kushi-slider-input[type=range]"
  :height--$kushi-input-slider-thumb-height
  ["-webkit-appearance" :none]
  :width--100%)

(defcss 
  "input.kushi-slider-input[type=range]:focus"
  :outline--none)

(defcss 
  "input.kushi-slider-input[type=range]::-webkit-slider-runnable-track"
  :width--100%
  :height--1px
  :cursor--pointer
  :animate--0.2s
  :box-shadow--0px:0px:0px:#000000
  :background--$kushi-input-slider-track-background-color
  :border-radius--1px
  :border--0px:solid:#000000)

(defcss
  "input.kushi-slider-input[type=\"range\"]::-webkit-slider-thumb"
  [:margin-top "calc(var(--kushi-input-slider-thumb-height) / -2)"]
  :box-shadow--0px:0px:0px:#000000
  :outline--$kushi-input-slider-thumb-outline
  :outline-offset--$kushi-input-slider-thumb-outline-offset
  :height--$kushi-input-slider-thumb-height
  :width--$kushi-input-slider-thumb-width
  :border-radius--$kushi-input-slider-thumb-border-radius
  :background--$kushi-input-slider-thumb-background-color
  :cursor--pointer
  :-webkit-appearance--none
  :border-radius--50%)

(defcss 
  ".dark input.kushi-slider-input[type=range]::-webkit-slider-thumb"
  :outline--$kushi-input-slider-thumb-outline-dark
  :background--$kushi-input-slider-thumb-background-color-dark)

(defcss
  "input.kushi-slider-input[type=range]:focus::-webkit-slider-runnable-track"
  :background--#000)

(defcss
  ".dark input.kushi-slider-input[type=range]:focus::-webkit-slider-runnable-track"
 :background--#fff)

(defcss
  "input.kushi-slider-input[type=range]::-moz-range-track"
  :width--100%
  :height--1px
  :cursor--pointer
  :animate--0.2s
  :box-shadow--0px:0px:0px:#000000
  :background--$kushi-input-slider-track-background-color
  :border-radius--1px
  :border--0px:solid:#000000)

(defcss
  ".dark input.kushi-slider-input[type=range]::-moz-range-track"
  :b--0px:solid:#fff)

(defcss
  "input.kushi-slider-input[type=range]::-moz-range-thumb"
  :box-shadow--0px:0px:0px:#000000
  :outline--5px:solid:#000
  :outline-offset---5px
  :height--15px
  :width--15px
  :border-radius--15px
  :background--#FFFFFF
  :cursor--pointer)

(defcss
  ".dark input.kushi-slider-input[type=range]::-moz-range-thumb" 
  :outline--5px:solid:#fff
  :background--#000)

(defcss
  "input.kushi-slider-input[type=range]::-ms-track" 
  :width--100%
  :height--1px
  :cursor--pointer
  :animate--0.2s
  :background--transparent
  :border-color--transparent
  :color--transparent)

(defcss 
  "input.kushi-slider-input[type=range]::-ms-fill-lower" 
  :background--$kushi-input-slider-track-background-color
  :border--0px:solid:#000000
  :border-radius--2px
  :box-shadow--0px:0px:0px:#000000)

(defcss
  ".dark input.kushi-slider-input[type=range]::-ms-fill-lower" 
  :border--0px:solid:#fff)

(defcss
  "input.kushi-slider-input[type=range]::-ms-fill-upper" 
  :background--$kushi-input-slider-track-background-color
  :border--0px:solid:#000000
  :border-radius--2px
  :box-shadow--0px:0px:0px:#000000)

(defcss
  ".dark input.kushi-slider-input[type=range]::-ms-fill-upper" 
  :border--0px:solid:#fff)

(defcss
  "input.kushi-slider-input[type=range]::-ms-thumb" 
  :margin-top--1px
  :box-shadow--0px:0px:0px:#000000
  :outline--5px:solid:#000
  :outline-offset---5px
  :height--15px
  :width--15px
  :border-radius--15px
  :background--#FFFFFF
  :cursor--pointer)

(defcss
  ".dark input.kushi-slider-input[type=range]::-ms-thumb" 
  :margin-top--1px
  :outline--5px:solid:#fff
  :background--#000)

(defcss
  "input.kushi-slider-input[type=range]:focus::-ms-fill-lower" 
  :background--#000)

(defcss
  ".dark input.kushi-slider-input[type=range]:focus::-ms-fill-lower" 
  :background--#fff)

(defcss
  "input.kushi-slider-input[type=range]:focus::-ms-fill-upper" 
  :background--#000)

(defcss
  ".dark input.kushi-slider-input[type=range]:focus::-ms-fill-upper" 
  :background--#fff)

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
           display-step-labels?
           step-label-display-fn]}]
  (into [:div
         (merge-attrs
          {:style (let [step-label-display
                        (if display-step-labels? "flex" "none")]
                    (css-vars-map step-label-display))
           :class (css ".kushi-slider-step-labels"
                       :.flex-row-sa
                       :display--$step-label-display
                       :ai--c
                       :position--relative
                       :w--100%
                       :h--0
                       {:mbe "calc(10px + 0.5em)"})}
          labels-attrs)]
        (let [ltr?     (= "ltr" (domo/writing-direction))
              last-int (dec num-steps)]
          (map-indexed
           (fn [idx step]
             (let [step-display         (if step-label-display-fn
                                          (step-label-display-fn step)
                                          step)
                   calcpct              (str "( (100% - var(--kushi-input-slider-thumb-width)) / " last-int ")")
                   inset-inline-start   (str "calc(" calcpct " * " idx "  + ( var(--kushi-input-slider-thumb-width) / 2)  )")
                   default-index?       (= idx default-index)
                   label-selected-class (when default-index? label-selected-class)
                   label-scale-factor   (str "scale(" label-scale-factor ")")
                   step-marker          (or step-marker :none)
                   step-marker-class    (case step-marker
                                          :dot
                                          :.kushi-slider-step-label-marker-dot
                                          :bar
                                          :.kushi-slider-step-label-marker-bar
                                          :none
                                          :.kushi-slider-step-label-marker-none
                                          nil)
                   step-marker-content  (when (contains? #{:dot :bar :none} step-marker)
                                          (case step-marker :dot "·" :bar "|" " "))
                   step-marker-content  (when step-marker-content (str "\"" step-marker-content "\""))
                   first?               (= idx 0)
                   last?                (= idx last-int)
                   left-most?           (or (and first? ltr?) (and last? (not ltr?)))
                   right-most?          (or (and first? (not ltr?)) (and last? ltr?))
                   right-or-left-most   (cond right-most?
                                              (str (if supplied-steps? "-67%" "-50%" ) " -50%")
                                              left-most?
                                              (str (if supplied-steps? "-33%" "-50%" ) " -50%")
                                              :else
                                              "-50% -50%")]
               [:span {:style (css-vars-map inset-inline-start label-scale-factor step-marker-content)
                       :class (css ".kushi-slider-step-label"
                                   step-marker-class
                                   label-selected-class
                                   :position--absolute
                                   :d--block
                                   :.transition
                                   :.flex-row-c
                                   :ai--c
                                   :c--currentColor
                                   :ta--center
                                   :>span:white-space--nowrap
                                   :inset-inline-start--$inset-inline-start
                                   :w--0
                                   :h--0
                                   :transform--$label-scale-factor 
                                   :.kushi-slider-step-label-selected:o--1
                                   :.kushi-slider-step-label-selected:c--currentColor
                                   :.kushi-slider-step-label-selected>span:v--visible
                                   :before:content--$step-marker-content
                                   {:.kushi-slider-step-label-selected:transform "scale(1)"})}
                [:span
                 {:style (css-vars-map right-or-left-most)
                  :class (css ".kushi-slider-step-label-inner"
                              :.absolute-centered
                              :translate--$right-or-left-most)}
                 (str step-display step-label-suffix)]]))
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
  ;; TODO line-break this up
  {:summary ["A slider is a ui element which allows the user to specify a numeric value which must be no less than a given value, and no more than another given value." ]
   :desc ["By default, values are represented as a numeric scale with a `min` and a `max`."
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
           {:name    step-label-display-fn
            :pred    fn?
            :default nil
            :desc    "Function which takes the current step, (usually a number), and transforms the value for display."}
           {:name    display-step-labels?
            :pred    boolean?
            :default false
            :desc    "If set to `false`, step labels above slider will not be rendered."}
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
                 step-label-display-fn
                 display-step-labels?]
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

    [:div (merge-attrs {:class (css ".kushi-slider"
                                    :.flex-col-c
                                    ;; when not showing labels and using leading value label
                                    ;;  :.flex-row-fs
                                    :position--relative
                                    :ai--stretch
                                    :w--100%
                                    :pi--1em:2em)}
                       wrapper-attrs)

     [slider-labels {:steps                steps
                     :supplied-steps?      supplied-steps?
                     :num-steps            num-steps
                     :default-index        default-index
                     :label-selected-class label-selected-class
                     :label-scale-factor   label-scale-factor
                     :labels-attrs         labels-attrs
                     :step-marker          step-marker
                     :step-label-suffix    step-label-suffix
                     :step-label-display-fn step-label-display-fn
                     :display-step-labels? display-step-labels?
                     }]

     [:input (merge-attrs
              {:class         (css ".kushi-slider-input" :w--100%)
               :id            id
               :data-kushi-ui :input.range
               :type          :range
               :on-change     (partial on-change label-selected-class label-id) }
              (assoc (or attrs {})
                     :defaultValue default-index
                     :min (str 0)
                     :max (str (-> steps count dec))
                     :step (or step 1)))]]))
