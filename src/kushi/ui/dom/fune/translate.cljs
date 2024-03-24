(ns kushi.ui.dom.fune.translate
  (:require
   [fireworks.core :refer [??? ?? ? !? ?> !?>]]
   [clojure.string :as string]
   [kushi.core :refer (keyed)]
   [domo.core :as domo]
   [kushi.ui.util :refer [ck? calc]]))


(def ^:public translate-xy
  {:tlc [:left   -100 :top -100 "-" "-"]
   :tl  [:left   0    :top -100 nil "-"]
   :t   [:x-center -50  :top -100 nil "-"]
   :tr  [:right  -100 :top -100 nil "-"]
   :trc [:right  0    :top -100 "+" "-"]

   :rtc [:right 0 :top    -100 "+" "-"]
   :rt  [:right 0 :top    0    "+" nil]
   :r   [:right 0 :y-center -50  "+" nil]
   :rb  [:right 0 :bottom -100 "+" nil]
   :rbc [:right 0 :bottom 0    "+" "+"]

   :brc [:right  0    :bottom 0  "+" "+"]
   :br  [:right  -100 :bottom 0  nil "+"]
   :b   [:x-center -50  :bottom 0  nil "+"]
   :bl  [:left   -0   :bottom 0  nil "+"]
   :blc [:left   -100 :bottom 0  "-" "+"]

   :lbc [:left -100 :bottom 0    "-" "+"]
   :lb  [:left -100 :bottom -100 "-" nil]
   :l   [:left -100 :y-center -50  "-" nil]
   :lt  [:left -100 :top    0    "-" nil]
   :ltc [:left -100 :top    -100 "-" "-"]})


;; TODO - clean up this implementation, maybe do offset start differently
(defn- fune-translate-xy
  [{:keys [placement-kw
           owning-el-rect
           owning-el
           corner-plc?
           arrow?
           metrics?]
    fune-el :el
    }]

  (let [[hrz translate-x vrt translate-y offset-x-op offset-y-op]
        (get translate-xy placement-kw nil)

        {fune-offset-value        :value
         fune-offset-value-units  :units}
        (domo/css-custom-property-value-data
         owning-el
         "--tooltip-offset")

        fune-offset-value
        (or fune-offset-value 10)

        {fune-offset-start-value        :value
         fune-offset-start-value-units  :units}
        (domo/css-custom-property-value-data
         owning-el
         "--tooltip-offset-start")

        otd
        (cond 
          (and (seq fune-offset-value-units)
               (= fune-offset-value-units
                  fune-offset-start-value-units))
          (- (max 0 fune-offset-start-value)
             fune-offset-value)
          :else
          0)
        
        offset-transitions-towards?
        (pos? otd)
        
        otd-abs
        (str (abs otd) (or fune-offset-value-units "px"))

        offset-css   (str "calc(var(--tooltip-offset)"
                          (if corner-plc?
                            " * 0.75 "
                            (when arrow? " + var(--tooltip-arrow-depth) "))
                          (if offset-transitions-towards? " + " " - ") otd-abs
                          ")")

        offset*      #(str " " % " " offset-css)
        offset-x     (some-> offset-x-op offset*)
        offset-y     (some-> offset-y-op offset*)
        x            (str (domo/round-by-dpr (get owning-el-rect hrz 0)) "px")
        y            (str (domo/round-by-dpr (get owning-el-rect vrt 0)) "px")

        left*        (calc (.-clientWidth fune-el) "px * (" translate-x " / 100)")
        left         (calc x " + " left* " " offset-x)

        top*         (calc (.-clientHeight fune-el) "px * (" translate-y " / 100)")
        top          (calc y " + " top* " " offset-y)


        _ (when-not metrics?
            (!? (keyed fune-offset-value
                      fune-offset-value-units
                      fune-offset-start-value
                      fune-offset-start-value-units
                      offset-transitions-towards?
                      otd
                      otd-abs
                      )))
        _ (!? :top top)
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
                       (let [c (-> placement-kw name first)]
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


(defn fune-translate-css
  ;; TODO put k in opts?
  [opts]
  (let [
        ;; [left
        ;;  top
        ;;  scl
        ;;  tx
        ;;  ty]  (fune-translate-xy opts)
        {oe-top      :top
         oe-left     :left
         oe-right    :right
         oe-bottom   :bottom
         oe-x-center :x-center
         oe-y-center :y-center} (:owning-el-rect opts)]

     (domo/css-style-string
      {"--oe-top"      (str oe-top "px")
       "--oe-left"     (str oe-left "px")
       "--oe-right"    (str oe-right "px")
       "--oe-bottom"   (str oe-bottom "px")
       "--oe-x-center" (str oe-x-center "px")
       "--oe-y-center" (str oe-y-center "px")
       "--tt-offset"   "max(var(--tooltip-offset-start), 0px)"
       "--offset"      (calc "(var(--tt-offset) + var(--tooltip-arrow-depth))")
       "--top-plc"     (calc "(var(--oe-top) - 100%) - var(--offset)")
       "--bottom-plc"  (calc "var(--oe-bottom) + var(--offset)")
       "--right-plc"   (calc "var(--oe-right) + var(--offset)")
       "--left-plc"    (calc "(var(--oe-left) - 100%) - var(--offset)")})))
