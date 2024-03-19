(ns kushi.ui.tooltip.translate
  (:require
   [clojure.string :as string]
   [kushi.core :refer (keyed)]
   [domo.core :as dom]
   [kushi.ui.util :refer [ck?]]))


(def ^:public translate-xy
  {:tlc [:left   -100 :top -100 "-" "-"]
   :tl  [:left   0    :top -100 nil "-"]
   :t   [:center -50  :top -100 nil "-"]
   :tr  [:right  -100 :top -100 nil "-"]
   :trc [:right  0    :top -100 "+" "-"]

   :rtc [:right 0 :top    -100 "+" "-"]
   :rt  [:right 0 :top    0    "+" nil]
   :r   [:right 0 :middle -50  "+" nil]
   :rb  [:right 0 :bottom -100 "+" nil]
   :rbc [:right 0 :bottom 0    "+" "+"]

   :brc [:right  0    :bottom 0  "+" "+"]
   :br  [:right  -100 :bottom 0  nil "+"]
   :b   [:center -50  :bottom 0  nil "+"]
   :bl  [:left   -0   :bottom 0  nil "+"]
   :blc [:left   -100 :bottom 0  "-" "+"]

   :lbc [:left -100 :bottom 0    "-" "+"]
   :lb  [:left -100 :bottom -100 "-" nil]
   :l   [:left -100 :middle -50  "-" nil]
   :lt  [:left -100 :top    0    "-" nil]
   :ltc [:left -100 :top    -100 "-" "-"]})


;; TODO - clean up this implementation, maybe do offset start differently
(defn- tooltip-translate-xy
  [{:keys [placement-kw
           owning-el-rect
           owning-el
           corner-plc?
           shift-x
           shift-y
           tooltip-arrow?
           tooltip-el
           adjust?]
    :or {shift-x 0
         shift-y 0}}]

  (let [[hrz translate-x vrt translate-y offset-x-op offset-y-op]
        (get translate-xy placement-kw nil)

        {tooltip-offset-value        :value
         tooltip-offset-value-units  :units}
        (dom/css-custom-property-value-data
         owning-el
         "--tooltip-offset")

        tooltip-offset-value
        (or tooltip-offset-value 10)

        {tooltip-offset-start-value        :value
         tooltip-offset-start-value-units  :units}
        (dom/css-custom-property-value-data
         owning-el
         "--tooltip-offset-start")

        otd
        (cond 
          (and (seq tooltip-offset-value-units)
               (= tooltip-offset-value-units
                  tooltip-offset-start-value-units))
          (- (max 0 tooltip-offset-start-value)
             tooltip-offset-value)
          :else
          0)
        
        offset-transitions-towards?
        (pos? otd)
        
        otd-abs
        (str (abs otd) (or tooltip-offset-value-units "px"))

        offset-css   (str "calc((var(--tooltip-offset))"
                          (if corner-plc?
                            " * 0.75 "
                            (when tooltip-arrow? " + var(--tooltip-arrow-depth) "))
                          (if offset-transitions-towards? " + " " - ") otd-abs
                          ")")

        offset*      #(str " " % " " offset-css)
        offset-x     (some-> offset-x-op offset*)
        offset-y     (some-> offset-y-op offset*)
        x            (str (dom/round-by-dpr (get owning-el-rect hrz 0)) "px")
        y            (str (dom/round-by-dpr (get owning-el-rect vrt 0)) "px")
        shift-x      (str (dom/round-by-dpr shift-x) "px")
        shift-y      (str (dom/round-by-dpr shift-y) "px")

        left*        (str " calc(" (.-clientWidth tooltip-el) "px * (" translate-x " / 100)) ")
        left         (str "calc(" x " + " shift-x " + " left* offset-x ")")

        top*         (str " calc(" (.-clientHeight tooltip-el) "px * (" translate-y " / 100)) ")
        top          (str "calc(" y " + " shift-y " + " top* offset-y ")")

        ;; For offset-on-reveal shift
        ;; if otd-abs stars-with "0" and initial-scale is 1 don't calc a transform
        ck?          (partial ck? placement-kw)
        scl          1
        tx-          (str "-" otd-abs)
        tx+          otd-abs
        ty-          (str "-" otd-abs)
        ty+          otd-abs
        [tx ty]      (if corner-plc?
                       (if offset-transitions-towards?
                         (cond 
                           (ck? #{:tlc :ltc}) [tx+ ty+]
                           (ck? #{:rtc :trc}) [tx- ty+]
                           (ck? #{:rbc :brc}) [tx- ty-]
                           (ck? #{:lbc :blc}) [tx+ ty-])
                         (cond 
                           (ck? #{:tlc :ltc}) [tx- ty-]
                           (ck? #{:rtc :trc}) [tx+ ty-]
                           (ck? #{:rbc :brc}) [tx+ ty+]
                           (ck? #{:lbc :blc}) [tx- ty+]))
                       (let [c (-> placement-kw name first) ]
                         (if offset-transitions-towards?
                           (case c
                             "t" [nil ty+]
                             "r" [tx- nil]
                             "b" [nil ty-]
                             "l" [tx+ nil])
                           (case c
                             "t" [nil ty-]
                             "r" [tx+ nil]
                             "b" [nil ty+]
                             "l" [tx- nil]))))]

    #_(println (keyed placement-kw shift-x shift-y left top))

    [left top scl tx ty]))


(defn tooltip-translate-css
  ;; TODO put k in opts?
  [opts k]
  (let [opts  (assoc opts :placement-kw k)
        [left
         top
         scl
         tx
         ty]  (tooltip-translate-xy opts)]
    (str
     "left: " left "; "
     "top: " top "; "
     "scale: " scl "; "
     "--_kushi-tooltip-translate-x-base: " (or tx "0px") "; "
     "--_kushi-tooltip-translate-y-base: " (or ty "0px") ";"
     )))
