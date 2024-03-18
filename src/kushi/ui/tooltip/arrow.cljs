(ns kushi.ui.tooltip.arrow
  (:require
   [kushi.core :refer (keyed)]
   [kushi.ui.dom :as dom]
   [kushi.ui.util :as util :refer [ck?]] ))


(defn- translate
  [direction-class* axis op shift]
  (let [op             (str " " op " ")
        axis-a         axis
        axis-b         (if (= axis-a "y") "x" "y")
        arrow-offset   (str "var(--tooltip-arrow-" axis-a "-offset)")
        tt-shft-down? (and (= axis "y") (pos? shift))
        tt-shft-up?   (and (= axis "y") (neg? shift))
        tt-shft-right? (and (= axis "x") (pos? shift))
        tt-shft-left? (and (= axis "x") (pos? shift))
        ;; _ (keyed [direction-class*
        ;;           axis
        ;;           op
        ;;           shift
        ;;           tt-shft-down?
        ;;           tt-shft-up?  
        ;;           tt-shft-right?
        ;;           tt-shft-left?
        ;;           ])
        op+arrow-shift (str (cond
                              tt-shft-down? " - "
                              tt-shft-up? " + "
                              tt-shft-right? " - "
                              tt-shft-left? " + "
                              :else
                              op)
                            (or shift 0)
                            "px")
        border-radius  "var(--tooltip-border-radius)"
        subpixel-shift (str (when (contains? #{"down" "right"} direction-class*) "-")
                            "0.333px")
        a              (str "(calc(0px"
                            op
                            arrow-offset
                            op+arrow-shift
                            op
                            border-radius
                            "))")
        b              (str "(" subpixel-shift ")")]
        (if (= axis-a "x") [a b] [b a])
        ;; return the internal css custom properties
       ))

(defn- translate-xy
  [{:keys [f
           k
           block-positioning?
           inline-positioning?
           shift-x
           shift-y]}]
  (let [ck?   (partial ck? k) 
        [x y] (cond 
                (neg? shift-x)      (f "x" "-" shift-x)
                (pos? shift-x)      (f "x" "+" shift-x)
                (neg? shift-y)      (f "x" "-" shift-y)
                (pos? shift-y)      (f "x" "+" shift-y)
                (ck? #{:br :tr})    (f "x" "-" shift-x)
                (ck? #{:bl :tl})    (f "x" "+" shift-x)
                block-positioning?  ["-50%" "0px"]
                (ck? #{:rt :lt})    (f "y" "+" shift-y)
                (ck? #{:rb :lb})    (f "y" "-" shift-y)
                inline-positioning? ["0px" "-50%"])]
    {:--__ktt-x x
     :--__ktt-y y}))


(defn- arrow-style-css2
  [opts]
  (str (dom/css-style-string 
        (merge (:arrow-position-stylemap opts)
               (translate-xy opts)))))


(defn shift-arrow! 
  [{:keys [owning-el-vpp arrow-el tt-pos]}]
  (let [diff     (str (- (:center owning-el-vpp)
                         (:center (dom/client-rect arrow-el)))
                      "px")
        css-var! #(dom/set-css-var! arrow-el
                                    (str "--__ktt-shift-" %)
                                    diff)]
    (cond (:block-positioning? tt-pos)
          (css-var! "x")
          (:inline-positioning? tt-pos)
          (css-var! "y")) ))

(defn append-arrow-el!
  [{:keys [el
           tt-pos
           shift?
           shift-x
           shift-y
           owning-el-vpp
           new-placement-kw]}]
  (let [arrow-el                (js/document.createElement "div")
        ck?                     (partial ck? new-placement-kw)
        {:keys
         [block-start?          
          block-end?            
          block-positioning?    
          inline-start?         
          inline-end?           
          inline-positioning?]} tt-pos
        arrow-top-bottom        (cond block-start?        {:top :100%}
                                      block-end?          {:bottom :100%}
                                      (ck? #{:lt :rt})    {:top :0}
                                      (ck? #{:lb :rb})    {:bottom :0}
                                      inline-positioning? {:top :50%})

        arrow-left-right        (cond inline-start?      {:left :100%}
                                      inline-end?        {:right :100%}
                                      (ck? #{:tr :br})   {:right :0}
                                      (ck? #{:tl :bl})   {:left :0}
                                      block-positioning? {:left :50%})

        direction-class*        (cond (ck? #{:tl :t :tr}) "down"
                                      (ck? #{:bl :b :br}) "up"
                                      (ck? #{:lt :l :lb}) "right"
                                      (ck? #{:rt :r :rb}) "left")

        direction-class         (str "kushi-floating-tooltip-arrow-pointing-"
                                     direction-class*)

        f                       (partial translate direction-class*)
        
        arrow-position-stylemap (merge arrow-top-bottom arrow-left-right)]

    ;; Set class and style on arrow element
    (doto arrow-el
      (.setAttribute "class"
                     (str "kushi-floating-tooltip-arrow "
                          direction-class 
                          #_(when (or shift-x shift-y)
                            " hidden")))

      (.setAttribute "style"
                     (arrow-style-css2
                      (merge {:f f
                              :k new-placement-kw}
                             (keyed block-positioning?
                                    inline-positioning?
                                    arrow-position-stylemap
                                    shift-x
                                    shift-y)))))

    ;; Finally, append arrow element to tooltip element.
    (.appendChild el arrow-el)
    #_(when shift?
     (shift-arrow! (keyed owning-el-vpp arrow-el tt-pos)))
    arrow-el))
