 (ns ^:dev/always kushi.color
   (:require
    [kushi.colors :refer [colors]]
    [clojure.string :as string]))



(defn parse-int [n]
  #?(:clj (Integer/parseInt n)
     :cljs (js/parseInt n)))

(defn parse-color
  "Parses a map entry from a css kushi-specific color-tokens map into data.

   Expects the map to be structured like the following example,
   with css hsl value where the hue is expressed as a var:
   {:--red50  \"hsl(var(--red-hue), 100%, 98%)\"
    :--red100 \"hsl(var(--red-hue), 95%, 94%)\"
    :--red200 \"hsl(var(--red-hue), 90%, 87%)\"}

   Example input:
   [:--red50  \"hsl(var(--red-hue), 100%, 98%)\"]

   Example output:
   {:color red :level 50 :saturation 100 :value 98}"

  [[k v]]

  (let [[_ color level*] (re-find #"^--([a-z]+)([0-9]+)$" (name k))
        [_ saturation** value**] (string/split v #",")
        [[_ saturation*] [_ value*]] (map #(->> % string/trim (re-find #"^([0-9]+).*$" )) [saturation** value**])
        saturation (* 0.01 (parse-int saturation*))
        value (* 0.01 (parse-int value*))
        level (* 0.001 (parse-int level*))]
    {:color color :level level :saturation saturation :value value}))

(def semantic-aliases
  {:blue :accent
   :gray :neutral
   :green :positive
   :yellow :warning
   :red :negative})


(defn intermediary-hsl
  [cur nxt]
  (let [ret*       (* (+ cur nxt) 0.5)
        num-string #?(:clj (Float/toString ret*) :cljs (.toString ret*))
        ret        (if (re-find #"\.0$" num-string)
                     (parse-int (string/replace num-string #"\.0$" ""))
                     ret*)
        ;; ret        (if (re-find #"\.0$" (do (!? :intermediary-hsl (string? num-string)) num-string))
        ;;              (parse-int (string/replace num-string #"\.0$" ""))
        ;;              ret*)
        ]
    ret))

(defn hsl-values
  [{:keys [data? color h s l]}]
  (if data?
    {:h h :s s :l l}
    (str "hsl(var(--" color "-hue), " s "%, " l "%)")))

(defn color-pair [{:keys [data? color level] :as m}]
  (let [hsl-values (hsl-values m)
        ret        [(keyword (str (when-not data? "--") color level))
                    hsl-values]]
    ret))

(defn colors->tokens-inner
  [opts [color {:keys [hue scale]}]]
  (let [data?  (= (:format opts) :data)
        h      [(keyword (str (when-not data? "--") color "-hue")) hue]
        scale+ (map-indexed
                (fn [i [level saturation lightness]]
                  (let [first?       (zero? i)
                        last?        (= i (dec (count scale)))
                        inter?       (and (not (false? (:expanded? opts)))
                                          (not (or first? last?)))
                        opts         {:data? data?
                                      :color color
                                      :h     hue}
                        current-tup  (color-pair (merge opts
                                                        {:level level
                                                         :s     saturation
                                                         :l     lightness}))
                        intermed-tup (when inter?
                                       (let [[level+ saturation+ lightness+] (map intermediary-hsl
                                                                                  [level saturation lightness]
                                                                                  (nth scale (inc i)))]
                                         (color-pair (merge opts
                                                            {:level level+
                                                             :s     saturation+
                                                             :l     lightness+}))))]
                    (into [] (remove nil? [current-tup intermed-tup]))))
                scale)]
    (into [] (concat [h] (apply concat scale+)))))




(defn colors->tokens

  "Intended to turn pure edn data representation of a color scale
   into a data structure that kushi can use internally to create
   a system of design tokens (css custom properties).

   The intent is to provide a simple data structure that can be
   easily tuned by hand with tweaks the :hue value and the saturation
   and lightness members of the tuples in each :scale collection.

   The resulting scale is optionally (via the :expanded? opts)
   hydrated to double the number of values of the authored scale,
   when a more granular scale is desired.

   Example input:

   [\"gray\" {:alias nil,
              :hue   0,
              :scale [[50 0 98]
                      [100 0 95]
                      [200 0 91]
                      [300 0 85]
                      [400 0 77]
                      [500 0 68]
                      [600 0 57]
                      [700 0 44]
                      [800 0 31]
                      [900 0 20]
                      [1000 0 8]]}
    \"blue\" {:alias \"accent\"
              :hue   212,
              :scale [[50 100 97]
                      [100 98 93]
                      [200 97 87]
                      [300 96 78]
                      [400 94 68]
                      [500 92 59]
                      [600 90 45]
                      [700 92 37]
                      [800 94 29]
                      [900 97 20]
                      [1000 100 11]]}]


   Example output (with {:format :data} as opts arg):

   [[:--gray-hue 0]
    [:--gray50 {:h 0, :s 0, :l 98}]
    [:--gray100 {:h 0, :s 0, :l 95}]
    [:--gray150 {:h 0, :s 0, :l 93}]
    [:--gray200 {:h 0, :s 0, :l 91}]
    [:--gray250 {:h 0, :s 0, :l 88}]
    [:--gray300 {:h 0, :s 0, :l 85}]
    [:--gray350 {:h 0, :s 0, :l 81}]
    [:--gray400 {:h 0, :s 0, :l 77}]
    [:--gray450 {:h 0, :s 0, :l 72.5}]
    [:--gray500 {:h 0, :s 0, :l 68}]
    [:--gray550 {:h 0, :s 0, :l 62.5}]
    [:--gray600 {:h 0, :s 0, :l 57}]
    [:--gray650 {:h 0, :s 0, :l 50.5}]
    [:--gray700 {:h 0, :s 0, :l 44}]
    [:--gray750 {:h 0, :s 0, :l 37.5}]
    [:--gray800 {:h 0, :s 0, :l 31}]
    [:--gray850 {:h 0, :s 0, :l 25.5}]
    [:--gray900 {:h 0, :s 0, :l 20}]
    [:--gray950 {:h 0, :s 0, :l 14}]
    [:--gray1000 {:h 0, :s 0, :l 8}]
    [:--blue-hue 212]
    [:--blue50 {:h 212, :s 100, :l 97}]
    [:--blue100 {:h 212, :s 98, :l 93}]
    [:--blue150 {:h 212, :s 97.5, :l 90}]
    [:--blue200 {:h 212, :s 97, :l 87}]
    [:--blue250 {:h 212, :s 96.5, :l 82.5}]
    [:--blue300 {:h 212, :s 96, :l 78}]
    [:--blue350 {:h 212, :s 95, :l 73}]
    [:--blue400 {:h 212, :s 94, :l 68}]
    [:--blue450 {:h 212, :s 93, :l 63.5}]
    [:--blue500 {:h 212, :s 92, :l 59}]
    [:--blue550 {:h 212, :s 91, :l 52}]
    [:--blue600 {:h 212, :s 90, :l 45}]
    [:--blue650 {:h 212, :s 91, :l 41}]
    [:--blue700 {:h 212, :s 92, :l 37}]
    [:--blue750 {:h 212, :s 93, :l 33}]
    [:--blue800 {:h 212, :s 94, :l 29}]
    [:--blue850 {:h 212, :s 95.5, :l 24.5}]
    [:--blue900 {:h 212, :s 97, :l 20}]
    [:--blue950 {:h 212, :s 98.5, :l 15.5}]
    [:--blue1000 {:h 212, :s 100, :l 11}]


    Passing {:format :css} will yield map entries like this:

    [:blue400 \"hsl(var(--blue-hue), 94%, 68%)\"]"

  [colors opts]

  (let [color-pairs (partition 2 colors)
        ret (into
             []
             (flatten (apply
                       concat
                       (mapv
                        (partial colors->tokens-inner opts)
                        color-pairs))))]
    ret))

(defn alias-color-tokens
  [opts [color alias]]
  (mapv (fn [n]
          [(keyword (str "--" (name alias) n))
           (keyword (str "--" (name color) n))])
        (if (false? (:expanded? opts))
          (concat [50] (range 100 1100 100))
          (range 50 1050 50))))

(defn colors->alias-tokens [semantic-aliases opts]
  (into []
        (flatten
         (apply concat
                (mapv (fn [pair]
                        (alias-color-tokens opts pair))
                      semantic-aliases)))) )

(def base-color-map
  (apply hash-map (colors->tokens colors {:format :css :expanded? false})))
