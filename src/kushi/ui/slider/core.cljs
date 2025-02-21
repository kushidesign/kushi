;; TODO
;; - Add color options, similar to switch
;; - Surface variants + thin (default)
;; - Feature that colors portion of track from min to current, other portion is faded
;; - Make vertical slider work
;; - Make sure you can tweak all parts of slider


(ns ^{:kushi/layer "kushi-ui-styles"}
  kushi.ui.slider.core
  (:require
   [fireworks.core :refer [? !? ?> !?>]]
   [applied-science.js-interop :as j]
   [kushi.core :refer (sx css defcss css-vars-map merge-attrs)]
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
  ;;  :.kushi-slider-step-label-selected:before:content :unset
   })

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
   :--kushi-input-slider-thumb-width                 :1em
   :--kushi-input-slider-thumb-height                :1em
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


;; Shared style for both versions of slider
(defcss ".kushi-slider-wrapper"
  :.flex-col-c
  :position--relative
  :ff--$slider-font-family|$code-font-stack
  :ai--stretch
  :w--100%)

;; ----------------------------------------------------------------------------



;; -----------------------------------------------------------------------------
;; Slider with labels supporting fns start
;; -----------------------------------------------------------------------------

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
  [{:keys [f
           cvlp
           steps
           num-steps
           step-marker
           default-index
           supplied-steps?
           label-scale-factor
           label-selected-class
           display-step-markers?
           step-labels-wrapper-attrs
           display-current-value-label?]}]
  (into [:div
         (merge-attrs
          {:style (let [current-value-display 
                        (if display-current-value-label?
                          "block"
                          "none")
                        
                        step-marker-display 
                        (if display-step-markers?
                          "flex"
                          "none")

                        step-labels-container-display
                        (if (or display-step-markers?
                                display-current-value-label?)
                          "flex" "none")]
                    (css-vars-map step-marker-display
                                  current-value-display
                                  step-labels-container-display))

           ;; TODO - sync this class with hi-perf slider label wrapper
           :class (css ".kushi-slider-step-labels"
                       :.flex-row-sa
                       :display--$step-labels-container-display-class
                       :ai--c
                       :position--relative
                       :w--100%
                       :h--0
                       {:mbe "calc(10px + 0.5em)"})}
          
          (when (contains? #{:thumb-bottom :thumb-block-end} cvlp)
            {:style {:margin-block-end   :unset
                     :margin-block-start "calc(10px + 0.5em)"}})
          step-labels-wrapper-attrs)
         
         ]

        (let [ltr?     (= "ltr" (domo/writing-direction))
              last-int (dec num-steps)]
          (map-indexed
           (fn [idx step]
             (let [step-display         (if f (f step) step)
                   calcpct              (str "((100% - var(--kushi-input-slider-thumb-width)) / " last-int ")")
                   inset-inline-start   (str "calc(" calcpct " * " idx "  + ( var(--kushi-input-slider-thumb-width) / 2)  )")
                   default-index?       (= idx default-index)
                   label-selected-class (when default-index? label-selected-class)
                   current-value-display-class (when display-current-value-label?
                                                 "kushi-slider-step-label-display-current-value")
                   label-scale-factor   (str "scale(" label-scale-factor ")")
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
                   step-marker-content  (when step-marker-content
                                          (str "\"" step-marker-content "\""))
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
               [:span {:style (css-vars-map inset-inline-start 
                                            label-scale-factor
                                            step-marker-content
                                            current-value-display-class)
                       :class (css ".kushi-slider-step-label"
                                   current-value-display-class
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
                                   {".kushi-slider-step-label-selected.kushi-slider-step-label-display-current-value:before:content"
                                    "\"\""
                                    :.kushi-slider-step-label-selected:transform           
                                    "scale(1)"})}
                [:span
                 {:style (css-vars-map right-or-left-most)
                  :class (css ".kushi-slider-step-label-inner"
                              :.absolute-centered
                              :translate--$right-or-left-most
                              [:display :$current-value-display])}
                 step-display]]))
           steps))))

(defn on-change [label-selected-class label-id e]
  (let [el               (-> e .-target)
        val              (js/parseInt (.-value el))
        parent           (.-parentNode el)
        previous-sibling (.-firstChild parent)
        label-node       (.querySelector previous-sibling
                                         (str ":nth-child(" (+ 1 val) ")"))
        selected-label-node (.querySelector previous-sibling
                                            (str "." label-selected-class))]
    (.remove (.-classList selected-label-node) label-selected-class)
    (.add (.-classList label-node) label-selected-class)
    (j/assoc! (domo/el-by-id label-id) :textContent (.-textContent label-node))
    #_(domo/set-attribute! (domo/el-by-id label-id) "textContent" (.-textContent label-node))))

;; Slider with labels supporting fns end
;; -----------------------------------------------------------------------------



;; -----------------------------------------------------------------------------
;; Slim slider supporting fns start 
;; -----------------------------------------------------------------------------
(defn current-value-label [{:keys [dv cvlp f]}] 
  [:div
   (merge-attrs
    (sx ".kushi-slider-hi-perf-current-value-label"
        {:c        :currentColor
         :position :absolute})
    (let [thumb-block-y-shift  "calc(100% + 0.5em)"
          thumb-block-style    {:left      "var(--iis, 0%)"
                                :translate "var(--tx)"}
          track-inline-x-shift "calc(100% + 1.5ex)"
          track-inline-style   {:bottom    :unset
                                :top       :50%
                                :transform "translateY(calc(-50% - 0.1em))"}
          above-thumb          (merge thumb-block-style
                                      {:bottom thumb-block-y-shift
                                       :left   "var(--iis, 0%)" })]

      ;; We are using inline styles here to reduce complexity
      ;; This positions the label relative the thumb, or the track.
      (cond
        (contains? #{:thumb-top :thumb-block-start} cvlp)
        {:style above-thumb}

        (contains? #{:thumb-bottom :thumb-block-end} cvlp)
        {:style (merge thumb-block-style
                       {:bottom :unset
                        :top    thumb-block-y-shift})}

        (contains? #{:track-right} cvlp)
        {:style (assoc track-inline-style 
                       :left
                       track-inline-x-shift)}

        (contains? #{:track-inline-end} cvlp)
        {:style (assoc track-inline-style 
                       :inset-inline-start
                       track-inline-x-shift)}

        (contains? #{:track-left} cvlp)
        {:style (assoc track-inline-style 
                       :right
                       track-inline-x-shift)}

        (contains? #{:track-inline-start} cvlp)
        {:style (assoc track-inline-style 
                       :inset-inline-end
                       track-inline-x-shift)}
        :else
        {:style above-thumb})))
   (if f (f dv) dv)])


(defn slider-slim-change-handler [f num-steps midpoint e]
  (let [parent                   (-> e domo/cet domo/parent)
        label-el                 (-> e domo/cet domo/previous-element-sibling)
        s                        (domo/etv e)
        float?                   (re-find #"[0-9]+\.[0-9]+" s) 
        n                        (if float? (js/parseFloat s) (js/parseInt s))
        fraction                 (/ n num-steps)
        inline-inset-start-css   (str (* 100 fraction) "%")
        slider-midpoint-fraction (* 2 (js/Math.abs (- 0.5 fraction)))
        midpoint-plus-minus-op   (if (<= n midpoint) "+" "-")]
    (set! (.-textContent label-el) (if f (f n) n))

    ;; TODO - set atomically in one go - add this functionality to domo
    (domo/set-css-var! parent "--iis" inline-inset-start-css)
    (domo/set-css-var! parent "--slider-midpoint-fraction" slider-midpoint-fraction)
    (domo/set-css-var! parent "--midpoint-plus-minus-op" midpoint-plus-minus-op)))


(defn slim-slider
  [{:keys [current-value-label-display-fn
           default-val
           min
           max
           step
           wrapper-attrs
           attrs
           id
           cvlp]}]
  (let [f         current-value-label-display-fn
        dv        (or default-val 0 min)
        num-steps (js/Math.abs (- max min))
        midpoint  (/ (+ min max) 2)
        step      (or step 1)
        tx        (str "calc(-50% "
                       "var(--midpoint-plus-minus-op, +) "
                       "(var(--slider-midpoint-fraction, 1) "
                       " * "
                       "(var(--kushi-input-slider-thumb-width) / 2)))")]
    [:div (merge-attrs
           {:style (css-vars-map tx)
            :class (css ".kushi-slider-hi-perf"
                        :.kushi-slider-wrapper
                        :mi--1em:2em)}
           wrapper-attrs)
     [current-value-label {:f    f
                           :dv   dv
                           :cvlp cvlp}]
     [:input
      (merge-attrs 
       attrs
       {:class         (css ".kushi-slider-input" :w--100%)
        :id            id
        :data-kushi-ui :input.range
        :type          :range
        :on-change     (partial slider-slim-change-handler f num-steps midpoint)
        :defaultValue  dv
        :min           min
        :max           max
        :step          step})]]))

;; Slim slider supporting fns start 
;; -----------------------------------------------------------------------------




(defn slider
  ;; TODO line-break this up
  {:summary "A slider is a ui element which allows the user to specify a
             numeric value which must be no less than a given value, and no more
             than another given value." 
   :desc "By default, values are represented as a numeric scale with a `min` and
          a `max`. Note that `:min`, `:max` and `:step` are passed down to the
          underlying `<input type=range>` element, and do not need to be written
          with the custom opts syntax. Checkout
          [input range docs](https://developer.mozilla.org/en-US/docs/Web/HTML/Element/input/range)
          for info on how `min`, `max`, and `step` work. Alternately, a scale of
          named, stepped values may be provided with the custom `:-steps` option."
   :opts '[{:name    default-value
            :pred    any?
            :default (:text "The supplied `min` or the first item in the supplied `:-steps` collection.")
            :desc    "The initial, default value."}
           {:name    default-index
            :pred    int?
            :default 0
            :desc    "Use `default-index` when you want to set the default value
                      by index. This is the index of the number in a numeric
                      range (with a `min` and `max`), or the index of a value in
                      a supplied `:-steps` collection"}
           {:name    steps
            :pred    vector?
            :default nil
            :desc    "Collection of step values."}
           {:name    step-marker
            :pred    #{:dot :bar :value :none}
            :default :none
            :desc    "Collection of step values."}

           ;; Flip this to current-value-label-scale-factor (or token?)
           {:name    label-scale-factor
            :pred    float?
            :default 0.7
            :desc    "Factor to scale down labels in range which are not selected.
                      Must be positive float and <= 1.0."}

           {:name    wrapper-attrs
            :pred    map?
            :default nil
            :desc    "HTML attributes map applied to the outer containing div."}
           {:name    step-labels-wrapper-attrs
            :pred    map?
            :default nil
            :desc    "HTML attributes map applied to the step labels containing div."}
           {:name    current-value-label-display-fn
            :pred    fn?
            :default nil
            :desc    "Function which takes the current step, (usually a number),
                      and transforms the value for display."}
           {:name    display-current-value-label?
            :pred    boolean?
            :default false
            :desc    "If set to `false`, the current step value label will not
                      be displayed."}

           {:name    current-value-label-position
            :pred    #{:track-top
                       :track-right
                       :track-bottom
                       :track-left
                       :track-block-start
                       :track-block-end
                       :track-inline-start
                       :track-inline-end
                       :thumb-top
                       :thumb-right
                       :thumb-bottom
                       :thumb-left
                       :thumb-block-start
                       :thumb-block-end
                       :thumb-inline-start
                       :thumb-inline-end}
            :default false
            :desc    "The position of the current value label, relative to the
                      slider track or slider thumb."}

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
                 step-labels-wrapper-attrs
                 step-marker
                 display-current-value-label?]
         f      :current-value-label-display-fn
         cvlp   :current-value-label-position
         steps* :steps
         :or    {display-current-value-label? true
                 cvlp                         :thumb-top}}
        opts

        supplied-steps?
        (boolean steps*)

        step-marker
        (or step-marker :none)

        display-step-markers?
        (and step-marker (not= step-marker :none))

        hi-perf-slider?
        (and display-current-value-label?
             (not display-step-markers?)
             (not steps*))

        label-selected-class
        "kushi-slider-step-label-selected"

        id
        (or (:id attrs) (gensym))

        label-id
        (str "label-for-" id)

        default-val
        (or defaultValue default-value)]


    (if hi-perf-slider?   

      ;; Version of slider that is more performant when lots of steps are present
      ;; Does not feature tick marks or labels
      ;; Current value label can be put to left or right (inline) of track
      [slim-slider {:f             f
                    :default-val   default-val
                    :min           min
                    :max           max
                    :step          step
                    :wrapper-attrs wrapper-attrs
                    :attrs         attrs
                    :id            id
                    :cvlp          cvlp}]
      


      ;; Version of slider with a distinct dom element for each marker/current-value-label 
      ;; Current value label CANNOT be put to left or right (inline) of track
      (let [steps              (slider-steps steps* min max (or step 1))
            num-steps          (count steps)
            default-val        default-val
            default-index      (slider-default-index default-index
                                                     default-val
                                                     steps*
                                                     steps)
            label-scale-factor (cond
                                 (= 1 label-scale-factor)
                                 1.0
                                 (and (float? label-scale-factor)
                                      (< label-scale-factor 1.0))
                                 label-scale-factor
                                 :else 0.6)]
        
        [:div (merge-attrs 
               {:class (css ".kushi-slider"
                            :.kushi-slider-wrapper
                            :pi--1em:2em)}
               wrapper-attrs
               (cond
                 (contains? #{:thumb-bottom :thumb-block-end} cvlp)
                 {:style {:flex-direction :column-reverse}}))

         
         [slider-labels {:f                            f
                         :steps                        steps
                         :supplied-steps?              supplied-steps?
                         :num-steps                    num-steps
                         :default-index                default-index
                         :label-selected-class         label-selected-class
                         :label-scale-factor           label-scale-factor
                         :step-labels-wrapper-attrs    step-labels-wrapper-attrs
                         :step-marker                  step-marker
                         :display-current-value-label? display-current-value-label?
                         :cvlp                         cvlp
                         :default-val                  default-val
                         :display-step-markers?        display-step-markers?}]
         
         [:input (merge-attrs
                  {:class         (css ".kushi-slider-input" :w--100%)
                   :id            id
                   :data-kushi-ui :input.range
                   :type          :range
                   :on-change     (partial on-change label-selected-class label-id)}
                  (assoc (or attrs {})
                         :defaultValue default-index
                         :min (str 0)
                         :max (str (-> steps count dec))
                         :step (or step 1)))]]))))
