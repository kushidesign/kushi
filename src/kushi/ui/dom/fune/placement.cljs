(ns kushi.ui.dom.fune.placement
  (:require
   [fireworks.core :refer [? !? ?> !?>]]
   [clojure.string :as string]
   [goog.string]
   [kushi.core :refer (keyed)]
   [domo.core :as domo]
   [kushi.ui.util :refer [ck? maybe as-str calc]]))

(defn og-placement
  "Returns a keyword such as :t or :blc"
  [placement-kw owning-el vpp]
  (if (or (= placement-kw :auto)
          (:on-edge? vpp))
    (cond
      (:on-corner? vpp)
      (cond (:ne? vpp) :blc
            (:se? vpp) :tlc
            (:sw? vpp) :trc
            (:nw? vpp) :brc)  

      (:on-edge? vpp)
      (cond (:n? vpp) :b
            (:e? vpp) :l
            (:s? vpp) :t
            (:w? vpp) :r)

      :else
      ;; Always auto-place fune on top, unless the owning element falls
      ;; within the top fraction of the viewport, as defined by the
      ;; :$fune-auto-placement-y-threshold css custom property.
      (if (< (:y-fraction vpp)
             (some-> owning-el
                     (domo/css-custom-property-value
                      "--fune-auto-placement-y-threshold")
                     js/parseFloat))
        :b
        :t))
    placement-kw))

(def ^:private non-logicals
  #{"bottom" "right" "top" "left" "corner"})

(def placement-values
  {:inline #{"inline-end" "inline-start"}
   :block #{"block-end" "block-start"}})

(defn- p2? [s kw]
  (or (= "center" s)
      (contains? (kw placement-values) s)))

(defn- p1? [s kw]
  (contains? (kw placement-values) s))


(defn- valid-placement [[p1 p2 corner]]
  (let [p1-inline?         (p1? p1 :inline)
        p1-block?          (p1? p1 :block)
        p2-inline?         (p2? p2 :inline)
        p2-block?          (p2? p2 :block)
        inline-then-block? (and p1-inline? p2-block?)
        block-then-inline? (and p1-block? p2-inline?)
        p1+p2              (or inline-then-block? block-then-inline?)]
    (keyed p1-inline?         
           p1-block?          
           p2-inline?         
           p2-block?          
           inline-then-block? 
           block-then-inline? 
           p1+p2)
    (if (or (and (= corner "corner") p1+p2)
            p1+p2
            (and (nil? corner)
                 (nil? p2)
                 (or p1-inline? p1-block?)))
      [p1 p2 corner]
      ["block-start"])))

(def ^:private by-logic
  (let [m {"block-start"  "t"
           "inline-end"   "r"
           "block-end"    "b"
           "inline-start" "l"
           "center"       nil
           true         "c"
           false        nil}]
    {:ltr m
     :rtl (assoc m "inline-end" "l" "inline-start" "r")}))

(defn- logical-placement
  [{:keys [ltr? placement]}]
  (let [placement      (some-> placement
                               (string/trim)
                               (string/split #" "))
        [p1 p2 corner] (valid-placement placement)
        corner?        (= "corner" corner)
        p1             (or p1 "block-start")
        p2             (or p2 "center")
        placement      [p1 p2 corner?]
        ret            (->> placement
                            (map #(get ((if ltr? :ltr :rtl) by-logic) %))
                            string/join
                            keyword)]
    ret))


(def ^:private non-corner-placements
  {:top-left     :tl  
   :top          :t   
   :top-right    :tr  
   :right-top    :rt  
   :right        :r   
   :right-bottom :rb  
   :bottom-right :br  
   :bottom       :b   
   :bottom-left  :bl  
   :left-bottom  :lb  
   :left         :l   
   :left-top     :lt})


(def ^:private corner-placements
  {:top-right-corner    :trc
   :right-top-corner    :trc
   :rtc                 :trc
   :bottom-right-corner :brc
   :right-bottom-corner :brc
   :rbc                 :brc
   :bottom-left-corner  :blc
   :left-bottom-corner  :blc
   :lbc                 :blc
   :top-left-corner     :tlc
   :left-top-corner     :tlc
   :ltc                 :tlc})

(def ^:private translate-xy
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

(defn- fune-prop [t prop]
  (str "var(--" t "-" prop ")"))

(defn placement-css-custom-property
  [opts]
  (let [{oe-top      :top
         oe-left     :left
         oe-right    :right
         oe-bottom   :bottom
         oe-x-center :x-center
         oe-y-center :y-center} (:owning-el-rect opts)
        t (-> opts :fune-type as-str)]
    (domo/css-style-string
     (let [tot-off "var(--total-offset)"]
       {"--oe-top"             (str oe-top "px")
        "--oe-left"            (str oe-left "px")
        "--oe-right"           (str oe-right "px")
        "--oe-bottom"          (str oe-bottom "px")
        "--oe-x-center"        (str oe-x-center "px")
        "--oe-y-center"        (str oe-y-center "px")
        "--border-width"       (fune-prop t "border-width")
        "--border-style"       (fune-prop t "border-style")
        "--border-color"       (fune-prop t "border-color")
        "--arrow-depth"        (fune-prop t "arrow-depth")
        "--offset"             (str "max(var(--" t "-offset-start), 0px)")
        "--total-offset"       (calc "(var(--offset) + var(--arrow-depth))")
        "--border-radius"      (fune-prop t "border-radius") 
        "--arrow-inline-inset" (fune-prop t "arrow-inline-inset") 
        "--arrow-block-inset"  (fune-prop t "arrow-block-inset") 
        "--top-plc"            (calc "(var(--oe-top) - 100%) - " tot-off)
        "--bottom-plc"         (calc "var(--oe-bottom) + " tot-off)
        "--right-plc"          (calc "var(--oe-right) + " tot-off)
        "--left-plc"           (calc "(var(--oe-left) - 100%) - " tot-off)}))))


;; TODO Add some safety here for bad inputs
;; Make the logic more efficient if arg is a
;; string or vector (for logic placement)
(defn user-placement
  "Expects a string, keyword, or vector of strings or keywords"
  [x]
  (let [kw* (some-> x
                    (maybe #(or (string? x) (keyword? x)))
                    name
                    keyword)
        kw  (when kw*
              (or (kw* non-corner-placements)
                  (kw* corner-placements)
                  kw*))
        s   (if (vector? x)
              (string/join " " (map #(as-str %) x))
              x)]
    (or
     (when (contains? translate-xy kw) kw)
     (let [parts (string/split s #"-")]
       (when (every? #(contains? non-logicals %) parts)
         (let [kw (some->> parts
                           (map first)
                           string/join
                           keyword)]
           (when (contains? translate-xy kw)
             kw))))
     (logical-placement {:ltr?      (= (domo/writing-direction) "ltr")
                         :placement s}))) )


;; for re-assigning placement
;; maybe use 1 or 2 separate hash-maps for each of these instead of `case`?
(defn- v-flip [k]
  (case k
    :tl :bl
    :t  :b
    :tr :br
    :bl :tl
    :b  :t
    :br :tr
    :tlc :blc
    :ltc :blc
    :blc :tlc
    :lbc :ltc

    :trc :brc
    :rtc :rbc
    :brc :trc
    :rbc :rtc))

(defn- h-flip [k]
  (case k
    :lt :rt
    :l  :r
    :lb :rb
    :rt :lt
    :r  :l
    :rb :lb
    :tlc :trc
    :ltc :rtc
    :blc :brc
    :lbc :rbc

    :trc :tlc
    :rtc :ltc
    :brc :blc
    :rbc :lbc))

(defn updated-fune-placement
  [{:keys [corner-plc?
           block-plc?
           inline-plc?
           placement-kw]
    {:keys [sw? nw? ne? se? n? s? e? w?]} :vpp}]
  (or (cond
        sw?          
        (if corner-plc? 
          :trc
          (case placement-kw
            :lt :rb
            :l  :rb
            :lb :rb
            :bl :tl
            :b  :t
            :br :tr))

        nw?
        (if corner-plc?
          :brc
          (case placement-kw
            :lt :rt
            :l  :rt
            :lb :rt
            :tl :bl
            :t  :b
            :tr :bt))

        ne?
        (if corner-plc?
          :blc
          (case placement-kw
            :rt :lt
            :r  :lt
            :rb :lt
            :tr :br
            :t  :b
            :tl :bl))

        se?
        (if corner-plc? 
          :tlc
          (case placement-kw
            :rt :lb
            :r  :lb
            :rb :lb
            :bl :tl
            :b  :t
            :br :tr))

        (or n? s?)
        (cond
          inline-plc?
          (case placement-kw
            :l   (if n? :lt :lb)
            :lt  :lb
            :lb  :lt
            :r   (if n? :rt :rb)
            :rt  :rb
            :rb  :rt)

          block-plc?
          (v-flip placement-kw)

          corner-plc?
          (case placement-kw
            :ltc :lt
            :tlc :lt
            :trc :rt
            :rtc :rt
            :blc :lb
            :lbc :lb
            :brc :rb
            :rbc :rb
            ))

        (or w? e?)
        (cond
          block-plc?
          (case placement-kw
            :t   (if w? :tl :tr)
            :tr  :tl
            :tl  :tr
            :b   (if w? :bl :br)
            :br  :bl
            :bl  :br)
          
          inline-plc?
          (h-flip placement-kw)

          corner-plc?
          (case placement-kw
            :ltc :tl
            :tlc :tl
            :trc :tr
            :rtc :tr
            :blc :bl
            :lbc :bl
            :brc :br
            :rbc :br
            )))

      ;; fallback to existing placement kw
      placement-kw))



;; Functions below deal with calculating the position of the fune based
;; on its placement keyword and the position of the owning element relative
;; to the viewport.

(defn fune-plc [k]
  ;; TODO use let-map macro here
  ;; change f to ck?
  (let [f                       (partial ck? k)
        block-start?            (f #{:tl :t :tr})
        block-end?              (f #{:bl :b :br})
        block-plc?      (or block-start? block-end?)
        inline-start?           (f #{:lt :l :lb})
        inline-end?             (f #{:rt :r :rb})
        inline-plc?     (or inline-start? inline-end?)
        corner-plc?     (f #{:tlc :trc
                                     :ltc :rtc
                                     :blc :brc
                                     :lbc :rbc})
        ]
    (keyed block-start?       
           block-end?         
           block-plc? 
           inline-start?      
           inline-end?        
           inline-plc?
           corner-plc?)))

(defn el-plc
  [viewport el edge-threshold]
  (let [{:keys [top
                bottom
                left
                right
                y-center
                center
                x-fraction
                y-fraction]} (domo/client-rect el)
        s?         (< (- (:inner-height-without-scrollbars viewport)
                         edge-threshold)
                           bottom)
        n?         (< top edge-threshold)
        w?         (< left edge-threshold)
        e?         (< (- (:inner-width-without-scrollbars viewport)
                         edge-threshold)
                           right)
        on-edge?        (or n? s? w? e?)
        sw?        (and s? w?)
        nw?        (and n? w?)
        ne?        (and n? e?)
        se?        (and s? e?)
        on-corner?      (or ne? se? sw? nw?)]
        (keyed n?  
               ne?  
               e?  
               se?  
               s?  
               sw?  
               w?  
               nw?  
               on-corner?
               on-edge?
               top
               bottom
               left
               right
               y-center
               center
               x-fraction
               y-fraction)))
 
