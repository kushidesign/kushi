(ns kushi.colors2
  (:require [fireworks.core :refer [? !? ?> !?>]]
            [kushi.util :refer [maybe keyed]]))


            
(defn round2
  "Round a double to the given precision (number of significant digits)"
  [precision d]
  (let [factor (Math/pow 10 precision)]
    (/ (Math/round (* d factor)) factor)))

(def transparent-neutrals-oklch
  (->> (for [[lightness color-name]
             [["100%" "white"]
              ["80%" "light-gray"]
              ["50%" "gray"]
              ["25%" "dark-gray"]
              ["0%" "black"]]]
         (let [kw           #(keyword (str "--transparent-" color-name %1 %2))
               oklch        #(str "oklch(" lightness " 0 0 / "  % ")")
               oughts       (reduce (fn [acc n]
                                      (conj acc (kw "-0" n) (oklch (* 0.01 n))))
                                    []
                                    (range 1 10))
               ten-to-hundo (reduce (fn [acc n]
                                      (conj acc
                                            (kw "-" n)
                                            (oklch (round2 2 (* 0.01 n)))))
                                    []
                                    (range 10 105 5))]
           (concat oughts ten-to-hundo)))
       (apply concat)))


(def colors
  ["gray"
   {:alias "neutral",
    :hue   0,
    :scale [[50 0 98]
            [100 0 95]
            [200 0 91]
            [300 0 85]
            [400 0 77]
            [500 0 68]
            [600 0 57]
            [700 0 44]
            [800 0 28]
            [900 0 18]
            [1000 0 8]]},
   "purple"
   {:alias nil,
    :hue   273,
    :scale
    [[50 100 97]
     [100 90 93]
     [200 90 85]
     [300 90 77]
     [400 91 69]
     [500 93 58]
     [600 91 45]
     [700 90 38]
     [800 90 30]
     [900 90 22]
     [1000 100 11]]},
   "blue"
   {:alias "accent",
    :hue   220,
    :scale [[50 100 98]
            [100 96 95]
            [200 93 88]
            [300 90 80]
            [400 85 70]

            [500 85 59]

            [600 80 49]
            [700 85 38.5]
            [800 90 30]
            [900 96 22]
            [1000 100 11]]},
   "green"
   {:alias "positive",
    :hue   148,
    :scale
    [
     [50 95 97]
     [100 86 93]
     [200 75 81]
     [300 70 65]
     [400 74 47]
     [500 95 35]
     [600 96 30]
     [700 99 24]
     [800 100 19]
     [900 100 14]
     [1000 100 10]]
    },
   "lime"
   {:hue   83,
    :scale
    [[50  100 96]
     [100 100 89]
     [200 100 69]
     [300 100 49]
     [400 98 45]
     [500 96 40]
     [600 94 35]
     [700 96 30]
     [800 96 22]
     [900 96 16]
     [1000 100 10]]},
   "yellow"
   {:alias nil,
    :hue   59,
    :scale [[50 96 95]
            [100 94 90]
            [200 93 75]
            [300 92 49]
            [400 91 46]
            [500 91 42]
            [600 91 36]
            [700 89 30]
            [800 89 22]
            [900 90 16]
            [1000 91 11]]},
   "gold"
   {:alias "warning",
    :hue   44,
    :scale [[50 98 96]
            [100 98 92]
            [200 98 81]
            [300 98 68]
            [400 96 52]
            [500 96 45]
            [600 98 38]
            [700 98 32]
            [800 98 24]
            [900 98 17]
            [1000 98 11]]},

   "orange"
   {:alias nil,
    :hue   31,
    :scale
    [[50  98 97]
     [100 98 93]
     [200 97 84]
     [300 97 73]
     [400 97 60]
     [500 97 48]
     [600 97 41]
     [700 97 35]
     [800 97 27]
     [900 97 18]
     [1000 97 11]]},
   "red"
   {:alias "negative",
    :hue   358,
    :scale
    [
     [50 100 98]
     [100 100 95]
     [200 96 88]
     [300 87 78]
     [400 78 65]
     [500 76 51.5]
     [600 70 45]
     [700 79 36.5]
     [800 93 27.5]
     [900 100 20]
     [1000 100 11]]},
   "magenta"
   {:alias nil,
    :hue   324,
    :scale [[50 100 98]
            [100 100 95.5]
            [200 95 88.5]
            [300 92 79]
            [400 88 68]
            [500 83 51]
            [600 82 45]
            [700 85 38]
            [800 88 30]
            [900 95 21]
            [1000 100 11]]},
   "brown"
   {:alias nil,
    :hue   19,
    :scale [[50 40 97]
            [100 37 93]
            [200 35 87]
            [300 32 78]
            [400 29 68]
            [500 27 59]
            [600 29 50]
            [700 32 42]
            [800 35 33]
            [900 37 24]
            [1000 40 11]]}
   ])
;; (defn average [coll] 
;;   (/ (reduce + coll) (count coll)))

;; (defn js-round-to [n places]
;;   (let [factor (js/Math.pow 10 places)]
;;     (/ (js/Math.round (* n factor)) factor)))

;; (defn create-color-pallette [m color-name]
;;   (let [{:keys [hue scale]
;;          :as   color}      (get m color-name) 
;;         oklch-coords       (for [[level
;;                                   saturation
;;                                   lightness] scale
;;                                  :let        [hsl-color (new (.-Color js/window)
;;                                                              "hsl"
;;                                                              (clj->js [hue saturation lightness]))
;;                                               oklch-color (.to hsl-color "oklch")
;;                                               ;;  _ (when (= "gray" color-name)
;;                                               ;;      (?> oklch-color))
;;                                               ]]
;;                              (->> oklch-color 
;;                                   .-coords 
;;                                   js->clj
;;                                   (concat [level])))
;;         average-ok-lch-hue* (->> oklch-coords (map last) average)
;;         average-ok-lch-hue  (js-round-to average-ok-lch-hue* 1)
;;         oklch-coords        (mapv #(into []
;;                                          (map-indexed (fn [i n]
;;                                                         (if (zero? i)
;;                                                           n
;;                                                           (js-round-to n 3)))
;;                                                       (drop-last %)))
;;                                   oklch-coords)]
;;     [color-name {:hue (if (js/isNaN average-ok-lch-hue) 0 average-ok-lch-hue)
;;                  :scale oklch-coords}]))


;; (defn pallettes []
;;   (let [colors-map      (apply hash-map colors)
;;         color-names (take-nth 2 colors)]
;;     (mapv (partial create-color-pallette colors-map) color-names)))


;; generated from functions above using original kushi.colors/colors hsl scale.
(def oklch-colors
  (apply
   array-map
   ["gray"
    {:hue   0,
     :alias "neutral",
     :scale [[50 0.985 0]
             [100 0.962 0]
             [200 0.931 0]
             [300 0.885 0]
             [400 0.821 0]
             [500 0.749 0]
             [600 0.658 0]
             [700 0.546 0]
             [800 0.399 0]
             [900 0.301 0]
             [1000 0.193 0]]}
    "purple"
    {:hue   304.9,
     :alias nil,
     :scale [[50 0.975 0.022]
             [100 0.932 0.047]
             [200 0.844 0.102]
             [300 0.758 0.158]
             [400 0.679 0.212]
             [500 0.589 0.269]
             [600 0.505 0.26]
             [700 0.447 0.228]
             [800 0.38 0.19]
             [900 0.302 0.151]
             [1000 0.193 0.101]]}
    "blue"
    {:hue   262.7,
     :alias "accent",
     :scale [[50 0.979 0.01]
             [100 0.948 0.024]
             [200 0.877 0.057]
             [300 0.797 0.094]
             [400 0.702 0.136]
             [500 0.601 0.189]
             [600 0.521 0.213]
             [700 0.439 0.185]
             [800 0.37 0.159]
             [900 0.301 0.131]
             [1000 0.198 0.078]]}
    "green"
    {:hue   155.1,
     :alias "positive",
     :scale [[50 0.986 0.018]
             [100 0.966 0.039]
             [200 0.905 0.09]
             [300 0.831 0.147]
             [400 0.756 0.189]
             [500 0.657 0.177]
             [600 0.589 0.158]
             [700 0.506 0.136]
             [800 0.431 0.115]
             [900 0.35 0.091]
             [1000 0.282 0.071]]}
    "lime"
    {:hue   129.5,
     :alias nil,
     :scale [[50 0.989 0.028]
             [100 0.97 0.076]
             [200 0.929 0.195]
             [300 0.89 0.247]
             [400 0.829 0.229]
             [500 0.754 0.207]
             [600 0.678 0.185]
             [700 0.61 0.166]
             [800 0.489 0.132]
             [900 0.392 0.105]
             [1000 0.293 0.078]]}
    "yellow"
    {:hue   108.1,
     :alias nil,
     :scale [[50 0.991 0.031]
             [100 0.982 0.061]
             [200 0.959 0.139]
             [300 0.916 0.197]
             [400 0.87 0.187]
             [500 0.813 0.175]
             [600 0.725 0.155]
             [700 0.629 0.134]
             [800 0.504 0.107]
             [900 0.406 0.086]
             [1000 0.319 0.067]]}
    "gold"
    {:hue   86.4,
     :alias "warning",
     :scale [[50 0.984 0.02]
             [100 0.968 0.04]
             [200 0.926 0.092]
             [300 0.881 0.142]
             [400 0.829 0.168]
             [500 0.761 0.156]
             [600 0.675 0.138]
             [700 0.596 0.122]
             [800 0.485 0.099]
             [900 0.383 0.078]
             [1000 0.29 0.059]]}
    "orange"
    {:hue   62.3,
     :alias nil,
     :scale [[50 0.981 0.013]
             [100 0.957 0.03]
             [200 0.903 0.068]
             [300 0.841 0.114]
             [400 0.776 0.158]
             [500 0.71 0.174]
             [600 0.632 0.154]
             [700 0.564 0.136]
             [800 0.469 0.112]
             [900 0.355 0.082]
             [1000 0.261 0.058]]}
    "red"
    {:hue   22.4,
     :alias "negative",
     :scale [[50 0.978 0.011]
             [100 0.944 0.028]
             [200 0.87 0.067]
             [300 0.775 0.118]
             [400 0.668 0.173]
             [500 0.587 0.22]
             [600 0.529 0.195]
             [700 0.465 0.179]
             [800 0.395 0.158]
             [900 0.321 0.131]
             [1000 0.215 0.087]]}
    "magenta"
    {:hue   347.6,
     :alias nil,
     :scale [[50 0.979 0.013]
             [100 0.953 0.031]
             [200 0.885 0.076]
             [300 0.799 0.137]
             [400 0.713 0.198]
             [500 0.622 0.248]
             [600 0.571 0.228]
             [700 0.508 0.204]
             [800 0.432 0.173]
             [900 0.342 0.139]
             [1000 0.225 0.094]]}
    "brown"
    {:hue   46.1,
     :alias nil,
     :scale [[50 0.977 0.005]
             [100 0.945 0.011]
             [200 0.898 0.02]
             [300 0.826 0.032]
             [400 0.746 0.044]
             [500 0.672 0.054]
             [600 0.598 0.073]
             [700 0.529 0.07]
             [800 0.448 0.063]
             [900 0.362 0.051]
             [1000 0.227 0.028]]}]))

(def oklch-colors-expanded
  (apply
   array-map 
   ["gray"
    {:hue   0,
     :alias "neutral",
     :scale [[50 0.985 0]
             [100 0.962 0]
             [150 0.95 0.0]
             [200 0.931 0]
             [250 0.91 0.0]
             [300 0.885 0]
             [350 0.85 0.0]
             [400 0.821 0]
             [450 0.78 0.0]
             [500 0.749 0]
             [550 0.7 0.0]
             [600 0.658 0]
             [650 0.6 0.0]
             [700 0.546 0]
             [750 0.47 0.0]
             [800 0.399 0]
             [850 0.35 0.0]
             [900 0.301 0]
             [950 0.25 0.0]
             [1000 0.193 0]]}
    "purple"
    {:hue   304.9,
     :alias nil,
     :scale [[50 0.975 0.022]
             [100 0.932 0.047]
             [150 0.89 0.07]
             [200 0.844 0.102]
             [250 0.8 0.13]
             [300 0.758 0.158]
             [350 0.72 0.19]
             [400 0.679 0.212]
             [450 0.63 0.24]
             [500 0.589 0.269]
             [550 0.55 0.26]
             [600 0.505 0.26]
             [650 0.48 0.24]
             [700 0.447 0.228]
             [750 0.41 0.21]
             [800 0.38 0.19]
             [850 0.34 0.17]
             [900 0.302 0.151]
             [950 0.25 0.13]
             [1000 0.193 0.101]]}
    "blue"
    {:hue   262.7,
     :alias "accent",
     :scale [[50 0.979 0.01]
             [100 0.948 0.024]
             [150 0.91 0.04]
             [200 0.877 0.057]
             [250 0.84 0.08]
             [300 0.797 0.094]
             [350 0.75 0.12]
             [400 0.702 0.136]
             [450 0.65 0.16]
             [500 0.601 0.189]
             [550 0.56 0.2]
             [600 0.521 0.213]
             [650 0.48 0.2]
             [700 0.439 0.185]
             [750 0.4 0.17]
             [800 0.37 0.159]
             [850 0.34 0.15]
             [900 0.301 0.131]
             [950 0.25 0.1]
             [1000 0.198 0.078]]}
    "green"
    {:hue   155.1,
     :alias "positive",
     :scale [[50 0.986 0.018]
             [100 0.966 0.039]
             [150 0.94 0.06]
             [200 0.905 0.09]
             [250 0.87 0.12]
             [300 0.831 0.147]
             [350 0.79 0.17]
             [400 0.756 0.189]
             [450 0.71 0.18]
             [500 0.657 0.177]
             [550 0.62 0.17]
             [600 0.589 0.158]
             [650 0.55 0.15]
             [700 0.506 0.136]
             [750 0.47 0.13]
             [800 0.431 0.115]
             [850 0.39 0.1]
             [900 0.35 0.091]
             [950 0.32 0.08]
             [1000 0.282 0.071]]}
    "lime"
    {:hue   129.5,
     :alias nil,
     :scale [[50 0.989 0.028]
             [100 0.97 0.076]
             [150 0.95 0.14]
             [200 0.929 0.195]
             [250 0.91 0.22]
             [300 0.89 0.247]
             [350 0.86 0.24]
             [400 0.829 0.229]
             [450 0.79 0.22]
             [500 0.754 0.207]
             [550 0.72 0.2]
             [600 0.678 0.185]
             [650 0.64 0.18]
             [700 0.61 0.166]
             [750 0.55 0.15]
             [800 0.489 0.132]
             [850 0.44 0.12]
             [900 0.392 0.105]
             [950 0.34 0.09]
             [1000 0.293 0.078]]}
    "yellow"
    {:hue   108.1,
     :alias nil,
     :scale [[50 0.991 0.031]
             [100 0.982 0.061]
             [150 0.97 0.1]
             [200 0.959 0.139]
             [250 0.94 0.17]
             [300 0.916 0.197]
             [350 0.89 0.19]
             [400 0.87 0.187]
             [450 0.84 0.18]
             [500 0.813 0.175]
             [550 0.77 0.16]
             [600 0.725 0.155]
             [650 0.68 0.14]
             [700 0.629 0.134]
             [750 0.57 0.12]
             [800 0.504 0.107]
             [850 0.46 0.1]
             [900 0.406 0.086]
             [950 0.36 0.08]
             [1000 0.319 0.067]]}
    "gold"
    {:hue   86.4,
     :alias "warning",
     :scale [[50 0.984 0.02]
             [100 0.968 0.04]
             [150 0.95 0.07]
             [200 0.926 0.092]
             [250 0.9 0.12]
             [300 0.881 0.142]
             [350 0.86 0.16]
             [400 0.829 0.168]
             [450 0.8 0.16]
             [500 0.761 0.156]
             [550 0.72 0.15]
             [600 0.675 0.138]
             [650 0.64 0.13]
             [700 0.596 0.122]
             [750 0.54 0.11]
             [800 0.485 0.099]
             [850 0.43 0.09]
             [900 0.383 0.078]
             [950 0.34 0.07]
             [1000 0.29 0.059]]}
    "orange"
    {:hue   62.3,
     :alias nil,
     :scale [[50 0.981 0.013]
             [100 0.957 0.03]
             [150 0.93 0.05]
             [200 0.903 0.068]
             [250 0.87 0.09]
             [300 0.841 0.114]
             [350 0.81 0.14]
             [400 0.776 0.158]
             [450 0.74 0.17]
             [500 0.71 0.174]
             [550 0.67 0.16]
             [600 0.632 0.154]
             [650 0.6 0.15]
             [700 0.564 0.136]
             [750 0.52 0.12]
             [800 0.469 0.112]
             [850 0.41 0.1]
             [900 0.355 0.082]
             [950 0.31 0.07]
             [1000 0.261 0.058]]}
    "red"
    {:hue   22.4,
     :alias "negative",
     :scale [[50 0.978 0.011]
             [100 0.944 0.028]
             [150 0.91 0.05]
             [200 0.87 0.067]
             [250 0.82 0.09]
             [300 0.775 0.118]
             [350 0.72 0.15]
             [400 0.668 0.173]
             [450 0.63 0.2]
             [500 0.587 0.22]
             [550 0.56 0.21]
             [600 0.529 0.195]
             [650 0.5 0.19]
             [700 0.465 0.179]
             [750 0.43 0.17]
             [800 0.395 0.158]
             [850 0.36 0.14]
             [900 0.321 0.131]
             [950 0.27 0.11]
             [1000 0.215 0.087]]}
    "magenta"
    {:hue   347.6,
     :alias nil,
     :scale [[50 0.979 0.013]
             [100 0.953 0.031]
             [150 0.92 0.05]
             [200 0.885 0.076]
             [250 0.84 0.11]
             [300 0.799 0.137]
             [350 0.76 0.17]
             [400 0.713 0.198]
             [450 0.67 0.22]
             [500 0.622 0.248]
             [550 0.6 0.24]
             [600 0.571 0.228]
             [650 0.54 0.22]
             [700 0.508 0.204]
             [750 0.47 0.19]
             [800 0.432 0.173]
             [850 0.39 0.16]
             [900 0.342 0.139]
             [950 0.28 0.12]
             [1000 0.225 0.094]]}
    "brown"
    {:hue   46.1,
     :alias nil,
     :scale [[50 0.977 0.005]
             [100 0.945 0.011]
             [150 0.92 0.02]
             [200 0.898 0.02]
             [250 0.86 0.03]
             [300 0.826 0.032]
             [350 0.79 0.04]
             [400 0.746 0.044]
             [450 0.71 0.05]
             [500 0.672 0.054]
             [550 0.64 0.06]
             [600 0.598 0.073]
             [650 0.56 0.07]
             [700 0.529 0.07]
             [750 0.49 0.07]
             [800 0.448 0.063]
             [850 0.41 0.06]
             [900 0.362 0.051]
             [950 0.29 0.04]
             [1000 0.227 0.028]]}]))


(def dev-sample-proj-dir "docs")


;; TODO - test this
(def colorways
  (let [user-config
        (try (-> "./kushi.edn" slurp read-string)
             (catch Exception e
               (try (-> (str "./" dev-sample-proj-dir "/kushi.edn") 
                        slurp
                        read-string)
                    (catch Exception e
                      {}))))
        user-colorways                             
        (-> user-config :theme :colorways)

        {:keys [accent positive negative warning neutral]} 
        user-colorways

        valid-color-names                          
        (into #{} (keys oklch-colors))

        f
        (fn [v s] (or (when (contains? valid-color-names v) v) s))]
    {:accent   (f accent "blue")
     :positive (f positive "green")
     :negative (f negative "red")
     :warning  (f warning "gold")
     :neutral  (f neutral "gray")}))



(defn color-token-scale
  "Color token scale"
  [{:keys [hue scale color-name alias]}]
  (into [[(keyword (str "--" (or alias color-name) "-hue-oklch" )) hue]]
        (for [[lvl lightness chroma] scale]
          [(keyword (str "--" (or alias color-name) "-" lvl)) 
           #_{:l lightness
            :c chroma
            :h hue}
           (if alias 
             (str "var(--" color-name "-" lvl ")")
             (str "oklch("
                  lightness
                  " "
                  chroma
                  " "
                  hue
                  ")"))])))

(def oklch-colors-flattened
  (!? :pp
     (->> (for [[color-name {:keys [alias] :as m}]
                oklch-colors-expanded]
            (let [m+ (assoc m :color-name color-name)
                  ret       (color-token-scale (dissoc m+ :alias))
                  alias-ret (when alias (color-token-scale m+))]
              (if (seq alias-ret)
                (concat ret alias-ret)
                ret)))
          (apply concat)
          (apply concat))))


;; This version resolves colorways from user theme
;; If used, take out :alias entries from oklch-colors and oklch-colors-expanded
(def oklch-colors-flattened2
  (!? :pp
     (let [colorways-by-colorname (!? (reduce-kv (fn [m k v]
                                               (assoc m (name v) (name k)))
                                             {} 
                                             colorways))]
       (->> (for [[color-name m]
                  oklch-colors-expanded]
              (do (!? color-name)
                  (let [m+        (-> m 
                                      (assoc :color-name color-name)
                                      (dissoc :alias) ;; <- remove legacy alias
                                      )
                        ret       (color-token-scale m+)
                        alias     (get colorways-by-colorname color-name)
                        alias-ret (!? color-name
                                     (when alias
                                       (color-token-scale (assoc m+ :alias alias))))]
                    (if (seq alias-ret)
                      (concat ret alias-ret)
                      ret))))
            (apply concat)
            (apply concat)))))


;; to be used at repl to expand scales of oklch colors to create the
;; oklch-colors-expanded def in this ns
(defn oklch-colors-expanded* []
  (!? :pp
     (->> (for [[color-name {:keys [scale] :as m}]
                oklch-colors

                :let                                  
                [
                 dbg?     (= color-name "red")
                 fifty    (first scale)
                 thousand (last scale)
                 expanded (keep-indexed
                           (fn [i [lvl chroma lightness]]
                             (when (< 1 i)
                               (let [[_ chroma-p lightness-p] (nth scale (dec i))]
                                 [(- lvl 50)
                                  (round2 2 (/ (+ chroma chroma-p) 2))
                                  (round2 2 (/ (+ lightness lightness-p) 2))])))
                           scale)
                 expanded (into []
                                (concat [fifty]
                                        (interleave (rest scale) expanded)
                                        [thousand]))]]
            [color-name (assoc m :scale expanded)])
          (apply concat)
          (into []))))

;; (!? :pp (oklch-colors-expanded*))

(def foreground-colors-oklch-flattened 
  (let [l    650
        d    350
        coll ["accent"   [l d]
              "warning"  [l d]
              "positive" [l d]
              "negative" [l d]
              "gray"     [l d]
              "purple"   [l d]
              "blue"     [l d]
              "green"    [l d]
              "lime"     [l d]
              "yellow"   [l d]
              "gold"     [l d]
              "orange"   [l d]
              "red"      [l d]
              "magenta"  [l d]
              "brown"    [l d]]]
    (reduce-kv (fn [acc k [l d]]
                 (conj acc
                       (str "--foreground-color-" k)
                       (str "--" k "-" l)
                       (str "--foreground-color-" k "-dark-mode")
                       (str "--" k "-" d)))
               [] 
               (apply array-map coll))))
