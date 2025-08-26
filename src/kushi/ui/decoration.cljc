(ns kushi.ui.decoration
  (:require [clojure.string :as string]
            [fireworks.core :refer [? !? ?> !?>]]
            [kushi.css.defs]))

(defn as-str [x]
  (str (if (or (keyword? x) (symbol? x)) (name x) x)))

(defn maybe [x pred]
  (when (if (set? pred)
          (contains? pred x)
          (pred x))
    x))

;;; Shadows and strokes --------------------------------------------------------
          
(defn kw->cssvar  [x] 
  (if-let [token (some-> x
                         (maybe keyword?)
                         name
                         (maybe #(string/starts-with? % "$"))
                         (subs 1))]
    (str "var(--" token ")")
    (as-str x)))

(defn accumulated-stroke-widths [strokes]
  (reduce (fn [acc [w]] 
            (let [w        (kw->cssvar w)
                  previous (peek acc)]
              (if previous
                (conj acc (into [] (concat previous [w])))
                [[w]])))
          []
          strokes))

(defn calc-stroke-widths [accumulated-stroke-widths]
  (map-indexed (fn [i vc] 
                 (if (zero? i)
                   (peek vc)
                   (str "calc("
                        (string/join " + " vc)
                        ")")))
               accumulated-stroke-widths))
         

(defn strokes-with-calc-stroke-widths [strokes]
  (mapv vector
        (-> strokes
            accumulated-stroke-widths
            calc-stroke-widths)
        (map (comp kw->cssvar peek) strokes)))

(defn css-box-shadow-for-strokes-str [coll stroke-align]
  (string/join ", "
               (mapv (fn [[w c]]
                       (str (when (= :inside stroke-align) "inset ")
                            "0 0 0 " w " " c ))
                     coll)))
          
(defn css-box-shadow-for-strokes [coll stroke-align]
  (-> coll
      strokes-with-calc-stroke-widths
      (css-box-shadow-for-strokes-str stroke-align)))
          
(defn css-box-shadow-for-shadows [shadows]
  (some->> shadows
           (mapv #(do
                    #_(println "")
                    (!? :- %)
                    (or (!? :no-file (some-> % (maybe keyword?) kw->cssvar))
                        (!? :no-file (if (vector? %)
                                      (!? :no-file 
                                         (string/join " " (mapv kw->cssvar %)))
                                      %)))))
           seq
           (string/join ", ")))
          
;; TODO maybe change to  drop-shadow and stroke
;; coerce into vector e.g. [:2px :red] => [[:2px :red]]
;; if design token, make that work

;; TODO remove code from theming with drop-shadow layers
(def stroke-presets
  {:none   [["0" "transparent"]]
   :xsoft  [[:$stroke-width "color-mix(in oklch, currentColor var(--xsoft-stroke-transparency, 15%), var(--stroke-transparency-mix-color, transparent))"]]
   :soft   [[:$stroke-width "color-mix(in oklch, currentColor var(--soft-stroke-transparency, 30%), var(--stroke-transparency-mix-color, transparent)"]]
   :medium [[:$stroke-width "color-mix(in oklch, currentColor var(--medium-stroke-transparency, 50%), var(--stroke-transparency-mix-color, transparent))"]]
   :hard   [[:$stroke-width "color-mix(in oklch, currentColor var(--hard-stroke-transparency, 70%), var(--stroke-transparency-mix-color, transparent))"]]
   :xhard  [[:$stroke-width "color-mix(in oklch, currentColor var(--xhard-stroke-transparency, 100%), var(--stroke-transparency-mix-color, transparent))"]]
   })

(def stroke-presets-key-set (->> stroke-presets keys (into #{})))

(defn strokes-vector [x]
  (cond
    (keyword? x) 
    (get stroke-presets x (get stroke-presets :none))
    (not-any? vector? x)
    [x]
    :else
    x))

(def shadow-presets
  {
   :xxsmall [:$shadow-xxsmall]
   :xsmall  [:$shadow-xsmall]
   :small   [:$shadow-small]
   :medium  [:$shadow-medium]
   :large   [:$shadow-large]
   :xlarge  [:$shadow-xlarge]
   :xxlarge [:$shadow-xxlarge]
   })

(def shadow-presets-key-set (->> shadow-presets keys (into #{})))

(defn shadows-vector [x]
  (or
   (get shadow-presets x nil) 
   (cond
     (or (string? x) (keyword? x)) 
     [x]
     :else
     x)))

(defn box-shadow [{:keys [shadows strokes stroke-align] 
                   :or   {stroke-align :inside}}]
  (let [strokes (some-> strokes
                        strokes-vector
                        (css-box-shadow-for-strokes stroke-align))
        shadows (some->> shadows
                         shadows-vector
                         css-box-shadow-for-shadows)]
    (str strokes (when (and strokes shadows) ", ") shadows)))

(def dbg (atom false))

(defn drop-shadow-and-stroke-attrs*
  [{:keys [stroke
           drop-shadow 
           stroke-align]
    :or {stroke-align :inside}}]
  (let [only-simple-stroke?      (and (contains? stroke-presets-key-set 
                                                 stroke)
                                      (not drop-shadow))
        only-simple-drop-shadow? (and (contains? shadow-presets-key-set
                                                 drop-shadow)
                                      (not stroke))]
    (!? :pp (cond 
              only-simple-stroke?
              {:data-ks-stroke       stroke
               :data-ks-stroke-align stroke-align}

              only-simple-drop-shadow?
              {:data-ks-drop-shadow drop-shadow}

              (or stroke drop-shadow)
              {:style {:box-shadow (box-shadow 
                                    {:shadows      drop-shadow
                                     :strokes      stroke
                                     :stroke-align stroke-align})}}))))

(defn drop-shadow-and-stroke-attrs [props]
  (or (:kushi.ui.core/pc props)
      (drop-shadow-and-stroke-attrs* props)))

(defn stroke-width-cssvar [stroke-width s]
  {:style {"--stroke-width" 
           (or (some-> stroke-width as-str)
               (str "var(--" s "-stroke-width, var(--element-stroke-width), 1px)"))}})

;; stepped-shadows effect
(defn- shadow-range [start end n]
  (? {:when @dbg} [start end n])
  (let [step (float (/ (? {:when @dbg} 
                        (#?(:cljs js/Math.abs :clj abs) (- end start))) (dec n)))
        op  (if (or (neg? end) (> start end)) - +)
        end (op end step)]
    (range (? {:when @dbg} start)
           (? {:when @dbg} end)
           (if (< start end) step (- step)))))

(defn float->fixed [n places]
  #?(:cljs
     (js/parseFloat (.toFixed n places))
     :clj
     (Float/parseFloat (format (str "%." places "f") n))))

(defn stepped-shadows
  "Creates a vector of css shadow settings"
  [{:keys [colors 
           blur
           spread
           start-y
           end-y  
           start-x
           end-x
           start-opacity
           end-opacity
           opacity-mix-color]
    :or {blur 0 spread 0 opacity-mix-color "transparent"}}]



  (let [coll-count    (count colors)
        range-y       (shadow-range start-y end-y coll-count)
        range-x       (shadow-range start-x end-x coll-count)
        start-opacity (when (number? start-opacity)
                        (float->fixed (* start-opacity 100) 2))
        end-opacity   (when (number? end-opacity)
                        (float->fixed (* end-opacity 100) 2))
        ;; _ (reset! dbg true)
        range-opacity (? (when (and start-opacity end-opacity)
                           (shadow-range start-opacity end-opacity coll-count)))
        ]
    
    (into []
          (map-indexed 
           (fn [i c]
             [(str (.toFixed (nth range-x i) 2) "px")
              (str (.toFixed (nth range-y i) 2) "px")
              blur
              spread
              (if (and start-opacity end-opacity)
                (str "color-mix(in oklch, "
                     (kw->cssvar c)
                     ", "
                     (kw->cssvar opacity-mix-color)
                     " "
                     (.toFixed (- 100 (nth range-opacity i)) 2) "%"  ")")
                (kw->cssvar c))])
           colors))))

;; Example call
#_(stepped-shadows {:colors  ["lime" "green" "blue" "purple"]
                 :blur    :10px
                 :start-y 10
                 :end-y   50
                 :start-x 10
                 :end-x   50
                 })

;; Working example for stepped-shadows 
#_(let [shadows       (stepped-shadows {:colors  ["lime" "green" "blue" "purple"]
                                           :blur    :10px
                                           :start-y 10
                                           :end-y   50
                                           :start-x 10
                                           :end-x   50
                                           })
      shadows-2     (stepped-shadows {:colors  ["yellow"
                                                     "orange"
                                                     "red"
                                                     "magenta"]
                                           :blur    :10px
                                           :start-y -10
                                           :end-y   -50
                                           :start-x -10
                                           :end-x   -50
                                           })
      shadows (concat shadows shadows-2)]
      [:div {:style {:w--200px   :h--200px
                     :box-shadow (box-shadow {:shadows shadows})}
             }])

;; For rainbow stepped shadows
#_{:colors            [:$red-500 :$orange-500 :$yellow-500 :$lime-500 :$green-500 :$blue-500 :$purple-500]
 :blur              :10px
                                              ;;  :spread  :10px
 :start-y           10
 :end-y             100
 :start-x           10
 :end-x             100
 :start-opacity     1
 :end-opacity       0.1
 :opacity-mix-color :white
 }
