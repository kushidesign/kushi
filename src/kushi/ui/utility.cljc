(ns kushi.ui.utility
  (:require
   
   [clojure.string :as string]
   [kushi.colors :refer [color-names]]
   [kushi.utils :as util :refer [deep-merge]]))


;; Helper fns
;; ------------------------------------------------------
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

(defn scale-of-utility-defs
  ([coll ks]
   (scale-of-utility-defs coll ks {}))
  ([coll ks {:keys [key-prefix val-prefix acc-f]}]
   (mapcatv (fn [k]
              (let [pf #(some-> % (str "-"))]
                [(keyword (str (pf key-prefix)
                               (util/stringify k)))
                 (let [v (->> k
                              util/stringify
                              (str "$" (pf val-prefix))
                              keyword)]
                   (reduce (fn [acc k] (assoc acc k v))
                           (if acc-f
                             (acc-f k)
                             {})
                           ks))]))
            coll)) )


;; Scale defs
;; ------------------------------------------------------

(def type-sizes
  [:xxxxsmall
   :xxxsmall
   :xxsmall
   :xsmall
   :small
   :medium
   :large
   :xlarge
   :xxlarge
   :xxxlarge
   :xxxxlarge])

(def radius-sizes
  (trim-vec 1 type-sizes))

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




;; Disabled
;; ------------------------------------------------------
(def disabled {:opacity :45%!important
               :cursor  :not-allowed!important})


;; Combinatorial flexbox utilities
;; ------------------------------------------------------

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

(def flex-col-base {:flex-direction  :column
                    :display         :flex})

(def flex-justify-content-options 
  ["flex-start"
   "center"
   "flex-end"
   "space-between"
   "space-around"
   "space-evenly"
   "left"
   "right"
   "normal"
   "stretch"])

(def combo-flex-utility-classes
  (mapcatv (fn [fd]
             (mapcat 
              (fn [jc]
                [(->> jc
                      util/kebab->shorthand
                      (conj ["flex" fd])
                      (string/join "-")
                      keyword)
                 (merge (if (= fd "row") flex-row-base flex-col-base)
                        {:justify-content (keyword jc)})])
              flex-justify-content-options))
           ["row" "col"]))



   ;; TODO - after string-based selector is working, use something like this instead
  ;;  "[class^='debug-']" {:outline-color  :silver
  ;;                       :outline-style  :solid
  ;;                       :outline-width  :1px
  ;;                       :outline-offset :-1px}
  ;;  :debug-red {:outline-color :$red-500}
   
(def debug-outline-classes
  (mapcatv 
   (fn [c]
     [(->> c (str "debug-") keyword)
      (assoc {:outline-style  :solid
              :outline-offset :-1px
              :outline-width  :1px}
             :outline-color
             (keyword (str "$" c "-500||" c)))])
   color-names))

(def base-classes
  [
   ;; Visual debugging utilities
   ;; ------------------------------------------------------
   :debug-grid            {:background-image (str "repeating-linear-gradient(to bottom, transparent, transparent var(--debug-grid-size), var(--debug-grid-color) var(--debug-grid-size), var(--debug-grid-color) calc(var(--debug-grid-size) + 1px), transparent calc(var(--debug-grid-size) + 1px)), "
                                                  "repeating-linear-gradient(to right,  transparent, transparent var(--debug-grid-size), var(--debug-grid-color) var(--debug-grid-size), var(--debug-grid-color) calc(var(--debug-grid-size) + 1px), transparent calc(var(--debug-grid-size) + 1px))")}
   :debug-grid-8          {:background-image      (str "repeating-linear-gradient(to bottom, transparent, transparent 8px, var(--debug-grid-color) 8px, var(--debug-grid-color) calc(8px + 1px), transparent calc(8px + 1px)), "
                                                       "repeating-linear-gradient(to right,  transparent, transparent 8px, var(--debug-grid-color) 8px, var(--debug-grid-color) calc(8px + 1px), transparent calc(8px + 1px))")
                           :dark:background-image (str "repeating-linear-gradient(to bottom, transparent, transparent 8px, var(--debug-grid-color-inverse) 8px, var(--debug-grid-color-inverse) calc(8px + 1px), transparent calc(8px + 1px)), "
                                                       "repeating-linear-gradient(to right,  transparent, transparent 8px, var(--debug-grid-color-inverse) 8px, var(--debug-grid-color-inverse) calc(8px + 1px), transparent calc(8px + 1px))")}
   :debug-grid-16         {:background-image      (str "repeating-linear-gradient(to bottom, transparent, transparent 16px, var(--debug-grid-color) 16px, var(--debug-grid-color) calc(16px + 1px), transparent calc(16px + 1px)), "
                                                       "repeating-linear-gradient(to right,  transparent, transparent 16px, var(--debug-grid-color) 16px, var(--debug-grid-color) calc(16px + 1px), transparent calc(16px + 1px))")
                           :dark:background-image (str "repeating-linear-gradient(to bottom, transparent, transparent 16px, var(--debug-grid-color-inverse) 16px, var(--debug-grid-color-inverse) calc(16px + 1px), transparent calc(16px + 1px)), "
                                                       "repeating-linear-gradient(to right,  transparent, transparent 16px, var(--debug-grid-color-inverse) 16px, var(--debug-grid-color-inverse) calc(16px + 1px), transparent calc(16px + 1px))")}

   :wireframe         {:outline-color  :silver
                       :outline-style  :solid
                       :outline-width  :1px
                       :outline-offset :-1px}
   ;; End debugging utils 
   

   ;; Borders
   :outlined              {:outline-color  :currentColor
                           :outline-style  :solid
                           :outline-width  :1px
                           :outline-offset :-1px}
   :bordered              {:border-color :currentColor
                           :border-style :solid
                           :border-width :1px}
   

   ;; TODO - use scale-of-utility-defs
   ;; Divisors
   ;; need defclass-like merging here - maybe with metadata on map?
   ;; TODO -really need transition property on these?
   :divisor-block-start  {:border-block-start         :$divisor
                          :dark:border-block-start    :$divisor-inverse
                          :transition-property        :all
                          :transition-timing-function :$transition-timing-function
                          :transition-duration        :$transition-duration}

   :divisor-block-end    {:border-block-end           :$divisor
                          :dark:border-block-end      :$divisor-inverse
                          :transition-property        :all
                          :transition-timing-function :$transition-timing-function
                          :transition-duration        :$transition-duration}

   :divisor-inline-start {:border-inline-start        :$divisor
                          :dark:border-inline-start   :$divisor-inverse
                          :transition-property        :all
                          :transition-timing-function :$transition-timing-function
                          :transition-duration        :$transition-duration}

   :divisor-inline-end  {:border-inline-end          :$divisor
                         :dark:border-inline-end     :$divisor-inverse
                         :transition-property        :all
                         :transition-timing-function :$transition-timing-function
                         :transition-duration        :$transition-duration}


    ;; Non-combo flex utility classes
   :shrink               {:flex-shrink 1}
   :no-shrink            {:flex-shrink 0}
   :grow                 {:flex-grow 1}
   :no-grow              {:flex-grow 0}

   ;; Position
   ;; ------------------------------------------------------
   :relative      {:position :relative}
   :absolute      {:position :absolute}
   :fixed         {:position :fixed}
   :sticky        {:position :sticky}

    ;; Combinatorial absolute and fixed positioning utilities
    ;; ------------------------------------------------------
   :absolute-centered            {:position           :absolute
                                  :inset-inline-start "50%"
                                  :inset-block-start  "50%"
                                  :translate          "-50% -50%"}

   :absolute-fill                {:position :absolute
                                  :top      0
                                  :right    0
                                  :bottom   0
                                  :left     0}

   :after-absolute-fill         {:after:content  "\"\""
                                 :after:position :absolute
                                 :after:top      0
                                 :after:right    0
                                 :after:bottom   0
                                 :after:left     0}

   :before-absolute-fill         {:before:content  "\"\""
                                  :before:position :absolute
                                  :before:top      0
                                  :before:right    0
                                  :before:bottom   0
                                  :before:left     0}

   :absolute-inline-start-inside {:position           :absolute
                                  :inset-inline-start "0%"
                                  :inset-inline-end   :unset
                                  :inset-block-start  "50%"
                                  :translate          "0px -50%"}

   :absolute-inline-end-inside   {:position           :absolute
                                  :inset-inline-start :unset
                                  :inset-inline-end   "0%"
                                  :inset-block-start  "50%"
                                  :translate          "0px -50%"}

   :absolute-block-start-inside  {:position           :absolute
                                  :inset-block-start  "0%"
                                  :inset-block-end    :unset
                                  :inset-inline-start "50%"
                                  :translate          "-50% 0px"}

   :absolute-block-end-inside    {:position           :absolute
                                  :inset-block-start  :unset
                                  :inset-block-end    "0%"
                                  :inset-inline-start "50%"
                                  :translate          "-50% 0px"}

   :fixed-fill                   {:position :fixed
                                  :top      0
                                  :right    0
                                  :bottom   0
                                  :left     0}

   :fixed-centered            {:position           :fixed
                               :inset-inline-start "50%"
                               :inset-block-start  "50%"
                               :translate          "-50% -50%"}

   :fixed-inline-start-inside {:position           :fixed
                               :inset-inline-start "0%"
                               :inset-inline-end   :unset
                               :inset-block-start  "50%"
                               :translate          "0px -50%"}

   :fixed-inline-end-inside   {:position           :fixed
                               :inset-inline-end   "0%"
                               :inset-inline-start :unset
                               :inset-block-start  "50%"
                               :translate          "0px -50%"}

   :fixed-block-start-inside  {:position           :fixed
                               :inset-block-start  "0%"
                               :inset-block-end    :unset
                               :inset-inline-start "50%"
                               :translate          "-50%"}

   :fixed-block-end-inside    {:position           :fixed
                               :inset-block-end    "0%"
                               :inset-block-start  :unset
                               :inset-inline-start "50%"
                               :translate          "-50%"}

   ;; Surfaces, buttons, containers
   ;; ------------------------------------------------------
   :bgi-cover                 {:background-position "center center"
                               :background-repeat   :no-repeat
                               :width               "100%"}

   :bgi-contain               {:background-position "center center"
                               :background-size     :contain
                               :background-repeat   :no-repeat
                               :width               "100%"
                               :height              "100%"}


   ;; Combinatorial transition utility
   ;; ------------------------------------------------------
   :transition            {:transition-property        :all
                           :transition-timing-function :$transition-timing-function
                           :transition-duration        :$transition-duration}])

(def type-weight-override-classes
  "Creates an ordered vector of pairs, thin ~ heavy (100 ~ 900):
   [:thin 
    {:font-weight                           :$thin
     :&_.kushi-icon:font-variation-settings \"'wght' 100\"
     :&.kushi-icon:font-variation-settings  \"'wght' 100\"}
   ...]"
  (mapcatv
   (fn [[k weight]]
     [k
      (let [v (str "'wght' " weight)]
        {:font-weight                           (->> k util/stringify (str "$"))
         :&_.kushi-icon:font-variation-settings v
         :&.kushi-icon:font-variation-settings  v})])
   type-weights-by-name))

(def override-classes
  [;; General
   ;; ------------------------------------------------------
   :invisible {:opacity 0}
   :hidden    {:visibility :hidden}
   :visible   {:visibility :visible}
   :collapse  {:visibility :collapse}
   :offscreen {:position :absolute
               :left     :-10000px
               :top      :auto
               :width    :1px
               :height   :1px
               :overflow :hidden}

   
   ;; Cursor
   ;; ------------------------------------------------------
   :pointer       {:cursor :pointer}


   ;; TODO - use scale-of-utility-defs
   ;; TODO - Maybe Nix these?
   ;; Display
   ;; ------------------------------------------------------
   :block         {:display :block}
   :inline        {:display :inline}
   :inline-block  {:display :inline-block}
   :flex          {:display :flex}
   :inline-flex   {:display :inline-flex}
   :grid          {:display :grid}
   :inline-grid   {:display :inline-grid}
   :flow-root     {:display :flow-root}
   :contents      {:display :contents}

   ;; Type styling
   ;; ------------------------------------------------------
   :sans          {:font-family :$sans-serif-font-stack}
   :serif         {:font-family :$serif-font-stack}
   :fantasy       {:font-family :fantasy}
   :cursive       {:font-family :cursive}
   :italic        {:font-style :italic}
   :oblique       {:font-style :oblique}

   ;; Text capitalization
   ;; ------------------------------------------------------
   :uppercase     {:text-transform :uppercase}
   :lowercase     {:text-transform :lowercase}
   :capitalize    {:text-transform :capitalize}

   ;; Icon enhancement
   ;; ------------------------------------------------------
   :enhanceable   {:gap :$icon-enhanceable-gap}


   ;; TODO - use scale-of-utility-defs
   ;; TODO - Maybe Nix this?
   ;; Tracking (aka letter-spacing)
   ;; ------------------------------------------------------
   :xxxtight      {:letter-spacing :$xxxtight}
   :xxtight       {:letter-spacing :$xxtight}
   :xtight        {:letter-spacing :$xtight}
   :tight         {:letter-spacing :$tight}

   ;; TODO - lose this default-tracking?
   :default-tracking {:letter-spacing 0}

   :loose         {:letter-spacing :$loose}
   :xloose        {:letter-spacing :$xloose}
   :xxloose       {:letter-spacing :$xxloose}
   :xxxloose      {:letter-spacing :$xxxloose}



   ;; TODO - use scale-of-utility-defs
   ;; Animations
   ;; ------------------------------------------------------
   :instant       {:transition-duration :$instant}
   :xxxfast       {:transition-duration :$xxxfast}
   :xxfast        {:transition-duration :$xxfast}
   :xfast         {:transition-duration :$xfast}
   :fast          {:transition-duration :$fast}
   :moderate      {:transition-duration :$moderate}
   :slow          {:transition-duration :$slow}
   :xslow         {:transition-duration :$xslow}
   :xxslow        {:transition-duration :$xxslow}
   :xxxslow       {:transition-duration :$xxxslow}



   ;; This one is used for buttons, tags etc ... The roundedness is always relative to font-size
   :rounded-absolute {:border-radius :$rounded-absolute-medium}
   :rounded          {:border-radius :$rounded}


   :sharp         {:border-radius 0}
   :pill          {:border-radius :9999px}


   ;; Surfaces, buttons, containers 3D
   ;; ------------------------------------------------------
   :debossed      {:text-shadow "0 1px 2px hsl(0deg 0% 100% / 55%), 0 -1px 2px hsl(0deg 0% 0% / 27%)"}
   :embossed      {:text-shadow "0 -1px 2px hsl(0deg 0% 100% / 55%), 0 1px 2px hsl(0deg 0% 0% / 27%)"}


   ;; TODO - use scale-of-utility-defs
   ;; TODO convex 0-5 plus inverse
   :convex        {:background-image :$convex-1}
   :convex-0      {:background-image :$convex-0}
   :convex-1      {:background-image :$convex-1}
   :convex-2      {:background-image :$convex-2}
   :convex-3      {:background-image :$convex-3}
   :convex-4      {:background-image :$convex-4}
   :convex-5      {:background-image :$convex-5}

   ;; TODO - use scale-of-utility-defs
   :elevated-0    {:box-shadow :$elevated-0}

   :elevated-1    {:box-shadow      :$elevated-1
                   :dark:box-shadow :$elevated-1-inverse}
   :elevated-2    {:box-shadow      :$elevated-2
                   :dark:box-shadow :$elevated-2-inverse}
   :elevated-3    {:box-shadow      :$elevated-3
                   :dark:box-shadow :$elevated-3-inverse}
   :elevated-4    {:box-shadow      :$elevated-4
                   :dark:box-shadow :$elevated-4-inverse}
   :elevated-5    {:box-shadow      :$elevated-5
                   :dark:box-shadow :$elevated-5-inverse}

   :elevated      {:box-shadow      :$elevated-4
                   :dark:box-shadow :$elevated-4-inverse}

   ;; Controls
   ;; ------------------------------------------------------
   :disabled      disabled])


;; Buttons, badges 2D 
;; ------------------------------------------------------
(def rounded-absolute-radius-classes
  (scale-of-utility-defs
   radius-sizes
   [:border-radius]
   {:key-prefix "rounded-absolute"
    :val-prefix "rounded-absolute"}))


;; Surfaces, panes, cards, containers 2D
;; ------------------------------------------------------
(def rounded-radius-classes
  (scale-of-utility-defs
   radius-sizes
   [:border-radius]
   {:key-prefix "rounded"
    :val-prefix "rounded"}))


;; Type sizes
;; ------------------------------------------------------
(def type-size-classes
  (scale-of-utility-defs
   type-sizes
   [:font-size]))


  ;; Border weights for radios and checkbox sync with type weight
  ;; ------------------------------------------------------
 (def ui-theming-classes
   (scale-of-utility-defs
    type-weights
    [">.kushi-radio-input:outline-width"
     ">.kushi-checkbox-input:bw"]
    {:val-prefix "input-border-weight"
     :acc-f      (fn [k]
                   {:font-weight (->> k
                                      util/stringify
                                      (str "$")
                                      keyword)})}))

(def fixed-geometries? true)

(defn geometries [coll m]
  (mapcatv (fn [[k v]]
             (let [m+    (assoc m :translate v)
                   k-str (util/stringify k)]
               (concat [k m+]
                       (when (and fixed-geometries?
                                  (re-find #"-inside$" k-str))
                         [(-> k-str
                              (str "-fixed")
                              keyword)
                          (assoc m+ :position :fixed)]))))
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


(def all-classes 
  [combo-flex-utility-classes
   debug-outline-classes
   base-classes
   geom-top-left-corners
   geom-top-right-corners
   geom-bottom-left-corners
   geom-bottom-right-corners
   geom-left-side
   geom-right-side
   geom-top-side
   geom-bottom-side

   type-weight-override-classes
   override-classes
   rounded-absolute-radius-classes
   rounded-radius-classes
   type-size-classes

   ui-theming-classes])

(def utility-class-ks
  (mapcat util/kwargs-keys all-classes))

(def utility-classes
  (apply deep-merge
         (map #(apply hash-map %) all-classes)))
