(ns kushi.ui.tooltip.placement
  (:require
   [clojure.string :as string]
   [goog.string]
   [kushi.core :refer (keyed)]
   [domo.core :as dom]
   [kushi.ui.util :refer [ck? maybe as-str]]
   [kushi.ui.tooltip.translate :as translate]))

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
      ;; Always auto-place tooltip on top, unless the owning element falls
      ;; within the top fraction of the viewport, as defined by the
      ;; :$tooltip-auto-placement-y-threshold css custom property.
      (if (< (:y-fraction vpp)
             (some-> owning-el
                     (dom/css-custom-property-value
                      "--tooltip-auto-placement-y-threshold")
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
  {:rtc                 :trc
   :top-right-corner    :trc
   :right-top-corner    :trc
   :rbc                 :brc
   :bottom-right-corner :brc
   :right-bottom-corner :brc
   :lbc                 :blc
   :left-bottom-corner  :blc
   :bottom-left-corner  :blc
   :ltc                 :ltc
   :top-left-corner     :ltc
   :left-top-corner     :ltc})


;; TODO Add some safety here for bad inputs
;; Make the logic more efficient if arg is a string or vector (for logic placement)
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
     (when (contains? translate/translate-xy kw) kw)
     (let [parts (string/split s #"-")]
       (when (every? #(contains? non-logicals %) parts)
         (let [kw (some->> parts
                           (map first)
                           string/join
                           keyword)]
           (when (contains? translate/translate-xy kw)
             kw))))
     (logical-placement {:ltr?      (= (dom/writing-direction) "ltr")
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

(defn updated-tooltip-placement
  [{:keys [corner-positioning?
           block-positioning?
           inline-positioning?
           placement-kw]
    {:keys [sw? nw? ne? se? n? s? e? w?]} :vpp}]
  (or (cond
        sw?          
        (if corner-positioning? 
          :trc
          (case placement-kw
            :lt :rb
            :l  :rb
            :lb :rb
            :bl :tl
            :b  :t
            :br :tr))

        nw?
        (if corner-positioning?
          :brc
          (case placement-kw
            :lt :rt
            :l  :rt
            :lb :rt
            :tl :bl
            :t  :b
            :tr :bt))

        ne?
        (if corner-positioning?
          :blc
          (case placement-kw
            :rt :lt
            :r  :lt
            :rb :lt
            :tr :br
            :t  :b
            :tl :bl))

        se?
        (if corner-positioning? 
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
          ;; Branch for non-shifting scenario
          ;; Comment out/remove if you want to support shift
          inline-positioning?
          (case placement-kw
            :l   (if n? :lt :lb)
            :lt  :lb
            :lb  :lt
            :r   (if n? :rt :rb)
            :rt  :rb
            :rb  :rt)

          block-positioning?
          (v-flip placement-kw)

          corner-positioning?
          (case placement-kw
            ;; version where tooltip is basically shifted from intended corner
            ;; :ltc :lt
            ;; :tlc :lt
            ;; :trc :rt
            ;; :rtc :rt
            ;; :blc :lb
            ;; :lbc :lb
            ;; :brc :rb
            ;; :rbc :rb
            
            ;; version where no shifing occurs
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
          ;; Branch for non-shifting scenario
          ;; Comment out/remove if you want to support shift
          block-positioning?
          (case placement-kw
            :t   (if w? :tl :tr)
            :tr  :tl
            :tl  :tr
            :b   (if w? :bl :br)
            :br  :bl
            :bl  :br)
          
          inline-positioning?
          (h-flip placement-kw)

          corner-positioning?
          (case placement-kw
            ;; version where tooltip is basically shifted from intended corner
            ;; :ltc :tr
            ;; :tlc :tr
            ;; :trc :tl
            ;; :rtc :tl
            ;; :blc :br
            ;; :lbc :br
            ;; :brc :bl
            ;; :rbc :bl
            
            ;; version where no shifing occurs
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



;; Functions below deal with calculating the position of the tooltip based
;; on its placement keyword and the position of the owning element relative
;; to the viewport.


(defn tooltip-positioning [k]
  ;; TODO use let-map macro here
  ;; change f to ck?
  (let [f                       (partial ck? k)
        block-start?            (f #{:tl :t :tr})
        block-end?              (f #{:bl :b :br})
        block-positioning?      (or block-start? block-end?)
        inline-start?           (f #{:lt :l :lb})
        inline-end?             (f #{:rt :r :rb})
        inline-positioning?     (or inline-start? inline-end?)
        corner-positioning?     (f #{:tlc :trc
                                     :ltc :rtc
                                     :blc :brc
                                     :lbc :rbc})
        ]
    (keyed block-start?       
           block-end?         
           block-positioning? 
           inline-start?      
           inline-end?        
           inline-positioning?
           corner-positioning?)))

(defn el-positioning
  [viewport el edge-threshold]
  ;; TODO - use let-map here
  (let [{:keys [top
                bottom
                left
                right
                middle
                center
                x-fraction
                y-fraction]} (dom/client-rect el)
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
               middle
               center
               x-fraction
               y-fraction)))
 

(defn shifts
  [{:keys [inline-positioning?
           corner-positioning?
           block-positioning?
           viewport
           vpp]}]
  (let [
        ;; TODO - This should be a css-var called :$tooltip-viewport-padding
        viewport-padding 5

        ;; TODO - if needed, viewport-padding should be handled in css-land
        tl-shift #(+ (js/Math.abs %) viewport-padding)
        br-shift (fn [x k]
                   (- (- (if (= k :right)
                           (:inner-width-without-scrollbars viewport) 
                           (:inner-height-without-scrollbars viewport))
                         x)
                      viewport-padding))
        c-on-c?   (and corner-positioning? (:on-corner? vpp))
        shift-x   (when-not (or inline-positioning?
                                c-on-c?)
                    (cond
                      (:e? vpp) (br-shift (:right vpp) :right)
                      (:w? vpp) (tl-shift (:left vpp))))
        shift-y   (when-not (or block-positioning?
                                c-on-c?)
                    (cond
                      (:n? vpp) (tl-shift (:top vpp))
                      (:s? vpp) (br-shift (:bottom vpp) :left))) ]
    (keyed shift-x shift-y)))
