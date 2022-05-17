(ns kushi.ui.input.slider.core
  (:require
   [par.core :refer-macros [!? ?]]
   [kushi.core :refer (sx merge-with-style insert-style-tag!)]
   [kushi.ui.core :refer (opts+children)]
   [kushi.ui.input.slider.css]
   [playground.shared-styles]
   [playground.util :as util :refer-macros (keyed)] ))

(insert-style-tag! "kushi-slider-styles" kushi.ui.input.slider.css/css)

(defn find-index [pred coll]
  (first
   (keep-indexed
    (fn [i x]
      (when (pred x) i))
    coll)))

(defn slider-steps [steps* min max]
  (map #(name (if (number? %) (str %) %))
       (or steps*
           (let [[min max] (when (and (number? min)
                                      (number? max)
                                      (< min max))
                             [min max])]
             (range (or min 0) (inc (or max 100)))))))

(defn slider-default-index
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

(defn slider-labels
  [{:keys [steps
           num-steps
           default-index
           label-selected-class
           label-size-class
           label-block-offset
           thumb-size]
    :or   {thumb-size "15px"}}]
  (into [:div
         (sx 'kushi-slider-step-labels
             :.flex-row-sa
             :ai--c
             :.relative
             :w--100%
             :h--0
             {:style {:transform (str "translateY(calc(10px + " label-block-offset " ))")}})]
        (map-indexed
         (fn [idx step]
           (let [
                 calcpct              (str "( (100% - var(--kushi-input-slider-thumb-width)) / " (dec num-steps) ")")
                 inset-inline-start   (str "calc(" calcpct " * " idx "  + ( var(--kushi-input-slider-thumb-width) / 2)  )")
                ;;  calcpct              (str "( (100% - " thumb-size ") / " (dec num-steps) ")")
                ;;  inset-inline-start   (str "calc(" calcpct " * " idx "  + (" thumb-size  " / 2)  )")
                 default-index?       (= idx default-index)
                 label-selected-class (when default-index? label-selected-class)]
             [:span (sx 'kushi-slider-step-label
                        :.absolute
                        :.block
                        :.transition
                        :.flex-row-c
                        :ai--c
                        :c--:--gray600
                        :ta--center
                        {:class [label-size-class label-selected-class]
                         :style {:inset-inline-start                           inset-inline-start
                                 :w                                            0
                                 :h                                            0
                                 :transform                                    "scale(0.8)"
                                 :&.kushi-slider-step-label-selected:transform :none
                                 :&.kushi-slider-step-label-selected:o         1
                                 :&.kushi-slider-step-label-selected:c         :--primary}})
              step]))
         steps)))

(defn slider
  [& args]
  (let [[opts
         attr]                   (opts+children args)
        {:keys [defaultValue
                default-value
                min
                max
                step]}  attr
        {:keys  [default-index
                 label-size-class
                 label-block-offset
                 parts]
         steps* :steps}          opts
        {wrapper-attrs :wrapper} parts
        steps                    (slider-steps steps* min max)
        num-steps                (count steps)
        default-val              (or defaultValue default-value)
        default-index            (slider-default-index default-index default-val steps* steps)
        label-size-class         (or label-size-class :xsmall)
        label-selected-class     "kushi-slider-step-label-selected"
        label-block-offset       (or (name label-block-offset) (str "calc(- 1em * 2)"))]

    #_(? (keyed steps
                num-steps
                default-index
                label-size-class
                label-selected-class))

    [:div (merge-with-style (sx 'kushi-slider
                                :.flex-col-c
                                :.relative
                                :ai--c
                                :w--100%
                                :padding-inline--0.75em)
                            wrapper-attrs)

     [slider-labels (keyed steps
                           num-steps
                           default-index
                           label-selected-class
                           label-size-class
                           label-block-offset)]
     [:input (merge-with-style
              (sx {:class     [label-size-class]
                   :style     {
                              ;;  :--kushi-slider-thumb-sz "21px"
                              ;;  "-webkit-slider-thumb:width" "var(--kushi-slider-thumb-sz)!important"
                              ;;  "-webkit-slider-thumb:height"  "var(--kushi-slider-thumb-sz)!important"
                              ;;  "-webkit-slider-thumb:outline" "calc(var(--kushi-slider-thumb-sz) / 3) solid #000!important"
                              ;;  "-webkit-slider-thumb:outline-offset" "- calc(var(--kushi-slider-thumb-sz) / 3)!important"
                              ;;  "-webkit-slider-thumb:margin-top" "calc(0 - var(--kushi-slider-thumb-sz))!important"
                               :w :100%
                              ;;  :z 1000
                               }

                   :type      :range
                  ;;  :min       (str 0)
                  ;;  :max       (str (-> steps count dec))
                  ;;  :step      (or step 1)
                   :on-change #(let [el                  (-> % .-target)
                                     val                 (js/parseInt (.-value el))
                                     parent              (.-parentNode el)
                                     previous-sibling    (.-firstChild parent)
                                     label-node          (.querySelector previous-sibling
                                                                         (str ":nth-child(" (+ 1 val) ")"))
                                     selected-label-node (.querySelector previous-sibling (str "." label-selected-class))
                                    ;;  step                (name (nth steps val))

                                     ]

                                  ;;  (js/console.log "ln: " label-node)
                                  ;;  (js/console.log "sln: " selected-label-node)
                                 (.remove (.-classList selected-label-node) label-selected-class)
                                  ;;  (js/console.log "selected class: " label-selected-class)
                                  ;;  (js/console.log label-node)
                                 (.add (.-classList label-node) label-selected-class))})
              (assoc (or attr {})
                     :defaultValue default-index
                     :min (str 0)
                     :max (str (-> steps count dec))
                     :step (or step 1)))]]))
