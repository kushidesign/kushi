(ns kushi.css.build.utility-classes
  (:require
   [fireworks.core :refer [? !? ?> !?>]]
   [kushi.ui.variants :as variants]
   [kushi.util :as util :refer [keyed maybe]]
   [clojure.string :as string]))

;; From kushi.colors/colornames ------------------------------------------------
(def color-names
  ["gray"
   "purple"
   "blue"
   "green"
   "lime"
   "yellow"
   "gold"
   "orange"
   "red"
   "magenta"
   "sand"
   "slate"
   ])


;; Helper fns
;; -----------------------------------------------------------------------------

(defn as-classname [x]
  (str "." x)
  #_keyword
  )

(defn trim-vec
  "Trims n number of things of both sides of vector.
   Safety checks first, if bad will just return collection."
  [n v]
  (if (vector? v)
    (let [cnt (count v)]
      (if (and (pos-int? n)
               (<= (* n 2) cnt))
        (subvec v n (- cnt n))
        v))
    v))

(defn mapcatv [f coll]
  (into [] (mapcat f coll)))

(defn maybe-data-attr-css-selector
  ([s]
   (maybe-data-attr-css-selector s nil))
  ([s data-attr]
   (if data-attr
     (str "[data-" data-attr "=\"" s "\"]")
     s)))

(defn scale-of-utility-defs
  ([coll ks]
   (scale-of-utility-defs coll ks {}))
  ([coll ks {:keys [key-prefix val-prefix acc-f data-attr]}]
   (mapcatv (fn [k]
              (let [pf
                    #(some-> % (str "-"))

                    css-selector*
                    (->> k
                         util/stringify
                         (str (pf key-prefix))
                         keyword)

                    css-selector
                    (maybe-data-attr-css-selector css-selector* data-attr)]

                [css-selector
                 (let [v (->> k
                              util/stringify
                              (str "$" (pf val-prefix))
                              keyword)]
                   (reduce (fn [acc k] (assoc acc k v))
                           (if acc-f (acc-f k) {})
                           ks))]))
            coll)))

(defn utility-class-scale
  {:examples [{:desc   "Generating an ordered scale of font-size utility classes"
               :call   '(utility-class-scale [:xxxsmall :xxsmall :small]
                                             :font-size
                                             "text-size")
               :result [:xxxsmall
                        {:font-size :$text-size-xxxsmall}
                        :xxsmall
                        {:font-size :$text-size-xxsmall}
                        :xsmall
                        {:font-size :$text-size-xsmall}]}]}
  ([coll css-prop]
   (utility-class-scale coll css-prop nil))
  ([coll css-prop token-prefix]
   (mapcatv 
    (fn [x]
      [(keyword x)
       {css-prop (keyword (str "$"
                               (some-> token-prefix util/as-str (str "-"))
                               (util/as-str x)))}])
    coll)))


(defn class-sels
  "(class-sels [\"foo\"
                {:color :red}
                \"bar\"
                {:color :blue}]
                \"debug\")
   =>
   [\".debug-foo\"]
    {:color :red}
    \".debug-bar\"]
    {:color :blue}]"
  ([coll]
   (class-sels coll nil))
  ([coll prefix]
   (into []
         (map-indexed (fn [i x] 
                        (if (odd? i) 
                          x
                          (->> x
                               name
                               (str "." 
                                    prefix 
                                    (when prefix "-")))))
                      coll))))


(defn data-ks-sels
  "(data-ks-sels [\"foo\"
                  {:color :red}
                  \"bar\"
                  {:color :blue}]
                  \"debug\")
   =>
   [\".debug-foo\"
    {:color :red}
    \".debug-bar\"
    {:color :blue}]"
  ([coll]
   (data-ks-sels coll nil))
  ([coll s]
   (reduce (fn [acc [k v]] 
             (conj acc
                   (str "[data-ks-"
                        s
                        "=\"" 
                        (name k)
                        "\"]")
                   v))
           []
           (partition 2 coll))))


(def sel-fn
  "The function used to generate either:

   a) utilility class selectors
   ~OR~
   b) data-attr selectors
   
   e.g.
   "
  data-ks-sels
  #_class-sels)

;; Scale defs
;; -----------------------------------------------------------------------------
(def type-weights-by-name
  (array-map
   :thin 100
   :extra-light 200
   :light 300
   :normal 400
   :wee-bold 500
   :semi-bold 600
   :bold 700
   :extra-bold 800
   :heavy 900))


(def type-weights (keys type-weights-by-name))

;;

;; DISPLAY
;; -----------------------------------------------------------------------------
(def display-classes
  [:block        {:display :block}
   :inline       {:display :inline}
   :inline-block {:display :inline-block}
   :flex         {:display :flex}
   :inline-flex  {:display :inline-flex}
   :grid         {:display :grid}
   :inline-grid  {:display :inline-grid}
   :flow-root    {:display :flow-root}
   :contents     {:display :contents}])


;; Combinatorial flexbox utilities
;; -----------------------------------------------------------------------------

;; TODO - Analyze performance tradeoffs with writing selectors like these:
;; first need to fix compiler to not prepend a "." in front of selector.
;; "[class^='flex-row-']" {:flex-direction  :row
;;                         :align-items     :center
;;                         :display         :flex}
;; "[class^='flex-row-']" {:flex-direction  :col
;;                         :display         :flex}
;; "[class$='c']"     {:justify-content :center}
;; "[class$='fs']"    {:justify-content :flex-start}
;; "[class$='fe']"    {:justify-content :flex-end}


(def flex-row-base {:flex-direction  :row
                    :align-items     :center
                    :display         :flex})

(def flex-column-base {:flex-direction :column
                       :display        :flex})

(def flex-justify-content-options 
  ["flex-start"
   "start"
   "center"
   "flex-end"
   "end"
   "space-between"
   "space-around"
   "space-evenly"
   "left"
   "right"
   "normal"
   "stretch"])

(def base-flex-classes
  [:flex-row flex-row-base
   :flex-col flex-column-base])

(def combo-flex-utility-classes
  (mapcatv (fn [fd]
             (mapcat 
              (fn [jc]
                [(->> #_(string/replace jc #"^flex-" "")
                      jc
                      (conj ["flex" fd])
                      (string/join "-")
                      #_as-classname)
                 (merge (if (= fd "row") flex-row-base flex-column-base)
                        {:justify-content (keyword jc)})])
              flex-justify-content-options))
           ["row" "col"]))

(def foreground-color-classes
  (mapcatv 
   (fn [c]
     [(keyword c)
      {:c      (keyword (str "$" c "-650"))
       :dark:c (keyword (str "$" c "-350"))}])
   color-names))

(def colored-wireframe-classes
  (mapcatv 
   (fn [c]
     [(keyword c)
      (assoc {:outline-style  :solid
              :outline-offset :-1px
              :outline-width  :1px}
             :outline-color
             (keyword (str "$" c "-500||" c)))])
   color-names))

(def wireframe-classes
  [
   ;; Visual debugging utilities
   ;; --------------------------------------------------------------------------
   ".wireframe"   {:outline-color  :silver
                   :outline-style  :solid
                   :outline-width  :1px
                   :outline-offset :-1px}])

(def font-family-classes
  [
   ;; font-family
   :sans       {:font-family :$sans-serif-font-family}
   :serif      {:font-family :$serif-font-family}
   :italic     {:font-style :italic}
   :oblique    {:font-style :oblique}])


;; TODO - Ok to take these out?
#_(def divisor-classes
  [:block-start  {:border-block-start         :$divisor
                  :dark:border-block-start    :$divisor-dark-mode
                  :transition-property        :all
                  :transition-timing-function :$transition-timing-function
                  :transition-duration        :$transition-duration}

   :block-end    {:border-block-end           :$divisor
                  :dark:border-block-end      :$divisor-dark-mode
                  :transition-property        :all
                  :transition-timing-function :$transition-timing-function
                  :transition-duration        :$transition-duration}

   :inline-start {:border-inline-start        :$divisor
                  :dark:border-inline-start   :$divisor-dark-mode
                  :transition-property        :all
                  :transition-timing-function :$transition-timing-function
                  :transition-duration        :$transition-duration}

   :inline-end  {:border-inline-end          :$divisor
                 :dark:border-inline-end     :$divisor-dark-mode
                 :transition-property        :all
                 :transition-timing-function :$transition-timing-function
                 :transition-duration        :$transition-duration}])

(def position-classes 
   ;; Combinatorial absolute and fixed positioning utilities
   ;; --------------------------------------------------------------------------
  [:static               {:position :static}
   :relative             {:position :relative}
   :absolute             {:position :absolute}
   :fixed                {:position :fixed}
   :sticky               {:position :sticky}

   :absolute-center            {:position           :absolute
                                :inset-inline-start "50%"
                                :inset-inline-end   :unset
                                :inset-block-start  "50%"
                                :inset-block-end    :unset
                                :translate          "-50% -50%"}

   :absolute-fill                {:position :absolute
                                  :top      0
                                  :right    0
                                  :bottom   0
                                  :left     0}

   :absolute-inline-start-inside {:position           :absolute
                                  :inset-inline-start "0%"
                                  :inset-inline-end   :unset
                                  :inset-block-start  "50%"
                                  :inset-block-end    :unset
                                  :translate          "0px -50%"}

   :absolute-inline-end-inside   {:position           :absolute
                                  :inset-inline-end   "0%"
                                  :inset-inline-start :unset
                                  :inset-block-start  "50%"
                                  :inset-block-end    :unset
                                  :translate          "0px -50%"}

   :absolute-block-start-inside  {:position           :absolute
                                  :inset-block-start  "0%"
                                  :inset-block-end    :unset
                                  :inset-inline-start "50%"
                                  :inset-inline-end   :unset
                                  :translate          "-50% 0px"}

   :absolute-block-end-inside    {:position           :absolute
                                  :inset-block-start  :unset
                                  :inset-block-end    "0%"
                                  :inset-inline-start "50%"
                                  :inset-inline-end   :unset
                                  :translate          "-50% 0px"}

   :fixed-fill                   {:position :fixed
                                  :top      0
                                  :right    0
                                  :bottom   0
                                  :left     0}

   :fixed-center            {:position           :fixed
                             :inset-inline-start "50%"
                             :inset-inline-end   :unset
                             :inset-block-start  "50%"
                             :inset-block-end    :unset
                             :translate          "-50% -50%"}

   :fixed-inline-start-inside {:position           :fixed
                               :inset-inline-start "0%"
                               :inset-inline-end   :unset
                               :inset-block-start  "50%"
                               :inset-block-end    :unset
                               :translate          "0px -50%"}

   :fixed-inline-end-inside   {:position           :fixed
                               :inset-inline-end   "0%"
                               :inset-inline-start :unset
                               :inset-block-start  "50%"
                               :inset-block-end    :unset
                               :translate          "0px -50%"}

   :fixed-block-start-inside  {:position           :fixed
                               :inset-block-start  "0%"
                               :inset-block-end    :unset
                               :inset-inline-start "50%"
                               :inset-inline-end   :unset
                               :translate          "-50%"}

   :fixed-block-end-inside    {:position           :fixed
                               :inset-block-end    "0%"
                               :inset-block-start  :unset
                               :inset-inline-start "50%"
                               :inset-inline-end   :unset
                               :translate          "-50%"}]
  )

(def pseudo-element-before-position-classes  
  [:absolute-fill         {:before:content  "\"\""
                           :before:position :absolute
                           :before:top      0
                           :before:right    0
                           :before:bottom   0
                           :before:left     0}

   :absolute-inline-end-outside    {:before:position           :absolute
                                    :before:top                :50%
                                    :before:bottom             :unset
                                    :before:inset-inline-start :100%
                                    :after:inset-inline-end    :unset
                                    :before:translate          :0:-50%}

   :absolute-inline-start-outside  {:before:position          :absolute
                                    :before:top               :50%
                                    :before:bottom            :unset
                                    :before:inset-inline-end  :100%
                                    :after:inset-inline-start :unset
                                    :before:translate         :0:-50%}])

(def pseudo-element-after-position-classes  
  [:absolute-fill         {:after:content  "\"\""
                           :after:position :absolute
                           :after:top      0
                           :after:right    0
                           :after:bottom   0
                           :after:left     0}

   :absolute-inline-end-outside     {:after:position           :absolute
                                     :after:top                :50%
                                     :after:bottom             :unset
                                     :after:inset-inline-start :100%
                                     :after:inset-inline-end   :unset
                                     :after:translate          :0:-50%}

   :absolute-inline-start-outside   {:after:position           :absolute
                                     :after:top                :50%
                                     :after:inset-inline-end   :100%
                                     :after:inset-inline-start :unset
                                     :after:translate          :0:-50%}])

(def background-image-behavior-classes
  [:bg-image-cover {:background-position "center center"
                    :background-repeat   :no-repeat
                    :width               "100%"}

   :bg-image-contain {:background-position "center center"
                      :background-repeat   :no-repeat
                      :width               "100%"
                      :height              "100%"
                      :background-size     :contain}])


(def icon-synced-weights
  "Creates an ordered vector of pairs, thin ~ heavy (100 ~ 900):
   [:thin 
    {:font-weight                             :$text-weight-thin
     \" .ks-icon:font-variation-settings\" \"'wght' 100\"
     \".ks-icon:font-variation-settings\"  \"'wght' 100\"}
   ...]"
  (mapcatv
   (fn [[k weight]]
     [k
      (let [v        (str "'wght' " weight)
            sel      (if (= sel-fn data-ks-sels) ".ks-icon" ".ks-icon")
            ancestor (if (= sel-fn data-ks-sels) "[data-ks-weight]" "[class*=\"weight-\"]")]
        {:font-weight                           
         (->> k util/stringify (str "$text-weight-") keyword)

         (str " " sel ":font-variation-settings")
         v

         (str sel ":font-variation-settings") 
         v
         
         (str sel ":has-ancestor(" ancestor "):font-variation-settings")
         v})])
   type-weights-by-name))

(def global-classes
  ["*:disabled"
   {:opacity :45%!important ;; <-make a token $disabled-opacity
    :cursor  :not-allowed!important}
   ])

(def transition 
  {:transition-property        :all
   :transition-timing-function :$transition-timing-function
   :transition-duration        :$transition-duration
   :after                      {:transition-property        :all
                                :transition-timing-function :$transition-timing-function
                                :transition-duration        :$transition-duration}
   :before                     {:transition-property        :all
                                :transition-timing-function :$transition-timing-function
                                :transition-duration        :$transition-duration}})
(def transition-classes
  ;; class-based selector
  #_[:transition transition]
  ;; data-ks selector
  ["[data-ks-transition]" transition])


;; TODO maybe just do in css?
#_(def transition-duration-classes
  (utility-class-scale
   (variants/tshirt-sizes [:slow :moderate :fast]
                          {:number-of-sizes 3
                           :cast-fn         keyword})
   :transition-duration))



(def offscreen-classes 
  [:offscreen {:position :absolute
               :left     :-10000px
               :top      :auto
               :width    :1px
               :height   :1px
               :overflow :hidden}])


(def icon-enhanceable-classes 
   ;; Icon enhancement - maybe you don't need if you make a 
   ;; label component that has this built-in?
  [:enhanceable-with-icon {:gap :$icon-enhanceable-gap}])


(def relief-effects-classes
   ;; Surfaces, buttons, containers 3D
   ;; TODO - make $debossed and $embossed tokens
   ;;      - Maybe make scale like convex and elevation 0-5?
   ;; TODO - consider using data-ks-debossed-text-level
   ;;        and maybe also :debossed-level on lib components
   ;; --------------------------------------------------------------------------

   [:debossed-text {:text-shadow "0 1px 2px hsl(0deg 0% 100% / 55%), 0 -1px 2px hsl(0deg 0% 0% / 27%)"}
    :embossed-text {:text-shadow "0 -1px 2px hsl(0deg 0% 100% / 55%), 0 1px 2px hsl(0deg 0% 0% / 27%)"}])



(def text-transform-classes 
 [:capitalize     {:text-transform :capitalize}
  :uppercase      {:text-transform :uppercase}
  :lowercase      {:text-transform :lowercase}
  :full-width     {:text-transform :full-width}
  :full-size-kana {:text-transform :full-size-kana}
  :math-auto      {:text-transform :math-auto}])

(def text-size-classes
  (utility-class-scale variants/xxxsmall-xxxlarge :font-size :text-size))

(def text-tracking-classes
  (utility-class-scale
   (variants/tshirt-sizes [:tight :default :loose]
                          {:number-of-sizes 3
                           :cast-fn         keyword})
   :letter-spacing))



(def shape-classes-rounded
  (utility-class-scale variants/shapes-rounded+rounded-absolute :border-radius "shape"))

(def shape-classes-non-rounded
  [:pill {:border-radius :9999px}
   :sharp {:border-radius :0px}
  ;;  :squircle {}
  ;;  :notched {}
   ])

;; Border weights for radios and checkbox sync with type weight
;; -----------------------------------------------------------------------------
;; TODO - sort out "checkbox-input" vs "checkbox"
(def radio-and-checkbox-synced-border-weights
  (scale-of-utility-defs
   type-weights
   (let [sel-checkbox
         ".ks-checkbox"
         #_(if (= sel-fn data-ks-sels)
             "[data-ks-ui=\"checkbox\"]" 
             ".ks-checkbox")
         
         sel-radio
         ".ks-radio"
         #_(if (= sel-fn data-ks-sels)
             "[data-ks-ui=\"checkbox\"]" 
             ".ks-checkbox")]
     [
      (str sel-checkbox ":outline-width")
      (str sel-checkbox ":border-width")
      (str " " sel-checkbox ":outline-width")
      (str " " sel-checkbox ":border-width")
      
      (str sel-radio ":outline-width")
      (str sel-radio ":border-width")
      (str " " sel-radio ":outline-width")
      (str " " sel-radio ":border-width")
      ])
   {:val-prefix "input-border-weight"
    ;; :data-attr  "ks-weight"
    :acc-f      (fn [k]
                  {:font-weight (->> k
                                     util/stringify
                                     (str "$")
                                     keyword)})}))

;; -----------------------------------------------------------------------------

(def fixed-geometries? true)

(defn geometries [coll m]
  (mapcatv (fn [[k v]]
             (let [m+     (assoc m :translate v)
                   k-str  (util/stringify k)
                   sel    (-> k
                              #_name
                              #_as-classname)
                   -fixed (when (and fixed-geometries?
                                     (re-find #"-inside$" k-str))
                            [(-> k-str
                                 (str "-fixed")
                                 #_as-classname
                                 keyword)
                             (assoc m+ :position :fixed)])]
               (!? {:when (= k :bottom-inside)} (keyed [m+ k-str sel -fixed]))
               (concat [sel m+]
                       -fixed)))
           coll))

(def geom-top-base 
  {:position :absolute
   :top      "0%"
   :bottom   :unset})


(def geom-top-right-corners
  (geometries
    [[:top-right-outside "0% -100%"]
     [:top-right "0% -50%"]
     [:top-right-corner-outside "100% -100%"]
     [:top-right-corner "50% -50%"]
     [:top-right-corner-inside "0% 0%"]
     [:right-top-outside "100% 0%"]
     [:right-top "50% 0%"]]
    (merge geom-top-base
           {:left  :unset
            :right "0%"})))

(def geom-top-left-corners
  (geometries
   [[:top-left-corner-outside "-100% -100%"]
    [:top-left-corner "-50% -50%"]
    [:top-left-corner-inside "0% 0%"]
    [:top-left-outside "0% -100%"]
    [:top-left "0% -50%"]
    [:left-top-outside "-100% 0%"]
    [:left-top "-50% 0%"]]
   (merge geom-top-base
          {:left  "0%"
           :right :unset})))

(def geom-bottom-left-corners
  (geometries
   [[:bottom-left-outside "0% 100%"]
    [:bottom-left "0% 50%"]
    [:bottom-left-corner-outside "-100% 100%"]
    [:bottom-left-corner "-50% 50%"]
    [:bottom-left-corner-inside "0% 0%"]
    [:left-bottom-outside "-100% 0%"]
    [:left-bottom "-50% 0%"]]
   {:position :absolute
    :top      :unset
    :bottom   "0%"
    :left     "0%"
    :right    :unset}))

(def geom-bottom-right-corners
  (geometries
   [[:right-bottom-outside "100% 0%"]
    [:right-bottom "50% 0%"]
    [:bottom-right-corner-outside
     "100% 100%"]
    [:bottom-right-corner "50% 50%"]
    [:bottom-right-corner-inside
     "0% 0%"]
    [:bottom-right-outside "0% 100%"]
    [:bottom-right "0% 50%"]]
   {:position :absolute
    :top      :unset
    :bottom   "0%"
    :left     :unset
    :right    "0%"}))

(def geom-right-left-side-base 
  {:position :absolute,
   :top      "50%"
   :bottom   :unset})

(def geom-right-side
  (geometries
   [[:right-inside "0% -50%"]
    [:right "50% -50%"]
    [:right-outside "100% -50%"]]
   (merge geom-right-left-side-base
          {:left  :unset
           :right "0%"})))

 (def geom-left-side
  (geometries
   [[:left-inside "0% -50%"]
    [:left "-50% -50%"]
    [:left-outside "-100% -50%"]]
   (merge geom-right-left-side-base
          {:right :unset
           :left  "0%"})))  

(def geom-top-bottom-side-base 
  {:position  :absolute
   :left      "50%"
   :right     :unset})

 
 (def geom-top-side
   (geometries 
    [[:top-outside "-50% -100%"]
     [:top "-50% -50%"]
     [:top-inside "-50% 0%"]]
    (merge geom-top-bottom-side-base
           {:bottom :unset
            :top    "0%"})))

(def geom-bottom-side
   (geometries 
    [[:bottom-inside "-50% 0%"]
     [:bottom "-50% 50%"]
     [:bottom-outside "-50% 100%"]]
    (merge geom-top-bottom-side-base
           {:top    :unset
            :bottom "0%"})))

(def text-weight-synced-classes 
  ;; TODO fix this docstring
  "[\".weight-light\"
    {:font-weight                :$text-weight-light
     \" >.ks-radio-i \"...    :$input-border-weight-light
     \" >.ks-checkbo \"...    :$input-border-weight-light
     \" .ks-icon:fo \"... \"  'wght' 300 \"
     \" .ks-icon:fon \"... \" 'wght' 300 \"}]"
  (let [sels (take-nth 2 radio-and-checkbox-synced-border-weights)
        m1   (apply hash-map radio-and-checkbox-synced-border-weights)
        m2   (apply hash-map icon-synced-weights)]
    (reduce
     (fn [acc sel]
       (conj acc sel (merge (get m1 sel) (get m2 sel))))
     []
     sels)))


(def all-classes
  "All the classes"
  [

   ;; base and global
   global-classes

   ;; display utility classes e.g. [data-ks-display="inline"]
   (sel-fn display-classes "display")

   ;; combo flex-utility classes e.g. [data-ks-display="flex-row-flex-end"]
   (sel-fn base-flex-classes "display")
   (sel-fn combo-flex-utility-classes "display")

   ;; data-ks-background-image-behavior="cover"
   ;; - bg image help   ->   :.bg-image-cover, :.bg-image-contain
   (sel-fn background-image-behavior-classes "bg-image")

   ;; foreground color
   ;; TODO - Remove? maybe redundant with colorway
   #_(sel-fn foreground-color-classes "foreground")

   ;; outline helpers  :.wireframe-red
   (class-sels colored-wireframe-classes "wireframe")

   ;; - debugging   e.g. :.wireframe
   wireframe-classes
   
   ;; TODO - Maybe take out?
   (sel-fn font-family-classes "font-family")

   ;; - divisors        e.g. :.divisor-block-start
   ;; TODO - remove
   #_(sel-fn divisor-classes "divisor")

   ;; These are combinatorial classes dealing with:
   ;; - abs fixed pos   e.g. :.absolute-block-end-inside 
   (sel-fn position-classes "position")

   ;; These are geometry-based absolute and fixed positioning utilities 
   ;; e.g. :.top-left-outside :.top-left-corner-outside etc.
   ;; data-ks-placement="top-left-outside"
   ;; data-ks-placement="absolute-block-end-inside "
   (sel-fn geom-top-left-corners "position")
   (sel-fn geom-top-right-corners "position")
   (sel-fn geom-bottom-left-corners "position")
   (sel-fn geom-bottom-right-corners "position")
   (sel-fn geom-left-side "position")
   (sel-fn geom-right-side "position")
   (sel-fn geom-top-side "position")
   (sel-fn geom-bottom-side "position")

   ;; offscreen positioning
   (sel-fn offscreen-classes "position")

   ;; - abs fixed pos for pseudo   e.g. :.after-absolute-block-end-inside 
   (sel-fn pseudo-element-after-position-classes "after-position")
   (sel-fn pseudo-element-before-position-classes "before-position")

   ;; transitions, animations
   transition-classes

   ;; TODO maybe remove
   #_(sel-fn transition-duration-classes "transition")

   ;; text weight
   (sel-fn text-weight-synced-classes "text-weight")

   ;; text size
   (sel-fn text-size-classes "text-size")

   ;; text tracking
   (sel-fn text-tracking-classes "tracking")

   ;; surface shapes
   (sel-fn shape-classes-non-rounded "shape")
   (sel-fn shape-classes-rounded "shape")

   (sel-fn shape-classes-rounded "shape")
   ])

   ;; A scale of selectors like ".text-weight-thin"
   ;;
   ;; TODO - maybe you don't need this if you can figure out how to add a
   ;; setting to the css compiler to do:
   ;;
   ;; (css-block {:fw $text-weight-thin})
   ;; =>
   ;; {:font-weight                           var(--text-weight-thin)
   ;;  ">.ks-radio-input:border-weight"    $input-border-weight-thin
   ;;  " .ks-icon:font-variation-settings" "'wght' 100"}
   ;;
   ;; It would have to be a config that maps a props to fns e.g.
   ;; {:font-weight (fn [x]
   ;;                 (if x-is-on-scale-of-type-weights?
   ;;                   (let [s (subs 1 (name x))] ; <- stringify it
   ;;                     {:font-weight                        
   ;;                      x
   ;;                      ">.ks-radio-input:border-weight"
   ;;                      (keyword (str "$input-border-weight-" s))
   ;;                      ...})
   ;;                   x)})

(!? all-classes)

;; OTHERS
;; data-ks-convex-level="5"
;; data-ks-elevation-level="5"
;; data-ks-text-effect="deboss"
;; data-ks-enhanceable-with-icon=""

(def utility-class-ks
  (!? :pp (mapcat util/kwargs-keys all-classes)))

;; (? :pp (filter #(string/starts-with? % "[" ) utility-class-ks))
;; (? :pp (filter #(string/starts-with? % "." ) utility-class-ks))

(def utility-class-ks-set
  (into #{} utility-class-ks))
;; #{".no-shrink"
;;   ".divisor-block-start"
;;   ".top-left-corner"
;;   ".left-inside-fixed"
;;   ".bottom-outside"
;;   ".flex-col-se"
;;   ...}

(def utility-classes
  (apply util/deep-merge
         (map #(apply hash-map %) all-classes)))
