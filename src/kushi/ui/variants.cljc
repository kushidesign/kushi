(ns ^:dev/always kushi.ui.variants
  (:require 
   [fireworks.core :refer [? !? ?> !?>]]
   [kushi.ui.util :refer [keyed]]
   #?(:clj [kushi.ui.ordered :refer [ordered-set]])))

(def contours-rounded
  [:rounded-xxxsmall
   :rounded-xxsmall
   :rounded-xsmall
   :rounded-small
   :rounded-medium
   :rounded-large
   :rounded-xlarge
   :rounded-xxlarge
   :rounded-xxxlarge])

(def contours-basic
  [:pill :sharp :rounded])

(def contours-auxillary
  [:circle :squircle])

(def contours-basic+rounded
  (apply conj contours-basic contours-rounded))

(def contours
  (into [] (concat contours-basic+rounded contours-auxillary contours-rounded)))

(def strokes
  [:none :xsoft :soft :medium :hard :xhard])

(def drop-shadows
  [:xxsmall :xsmall :small :medium :large :xlarge :xxlarge])

(def icon-style
  [:rounded :outlined :sharp])

(def spinner-type
  [:donut :thinking :propeller])

(def packings
  [:xcompact :compact :default :roomy :xroomy])

;; Pull this from colors namespace?
(def colorways-named
  [:gray :purple :blue :green :lime :yellow :gold :orange :red :magenta :brown])

(def colorways-semantic
  [:neutral :accent :positive :warning :negative])

(def colorways
  (apply conj colorways-named colorways-semantic))

(def shadow-colors
  (apply conj colorways-named colorways-semantic))

(def sizings-xxsmall-xlarge
  [:xxsmall :xsmall :small :medium :large :xlarge])

(def sizings-xsmall-xxxlarge
  [:xsmall :small :medium :large :xlarge :xxlarge :xxxlarge])

(def sizings 
  [:xxxsmall :xxsmall :xsmall :small :medium :large :xlarge :xxlarge :xxxlarge])

(def weights
  [:thin :extra-light :light :normal :wee-bold :semi-bold :bold :extra-bold :heavy])

(def surfaces 
  [:solid-classic
   :solid
   :soft-classic
   :soft
   :convex
   :faint
   :minimal
   :transparent
   ])

(def positions 
  [:fixed-inline-start-inside
   :absolute-inline-end-inside
   :fixed-fill
   :absolute-block-end-inside
   :fixed-block-start-inside
   :static
   :absolute-inline-start-inside
   :absolute
   :absolute-fill
   :sticky
   :fixed-inline-end-inside
   :absolute-block-start-inside
   :fixed-block-end-inside
   :fixed-centered
   :fixed
   :absolute-centered
   :relative])

(def surfaces-tag
  [:solid :soft :faint :minimal])

(def variants*
  (keyed [contours-basic
          contours-auxillary
          contours-basic+rounded
          contours-rounded
          contours
          strokes
          drop-shadows
          shadow-colors
          icon-style
          spinner-type
          packings
          positions
          colorways-named
          colorways-semantic
          colorways
          sizings-xxsmall-xlarge
          sizings-xsmall-xxxlarge
          sizings
          weights
          surfaces
          surfaces-tag]))

(defn variant-key [k s]
  (keyword (str (name k) "/" s)))

(def variants
  (reduce-kv
   (fn [m k v]
     (assoc m
            (variant-key k "vector")
            v
            (variant-key k "set")
            #?(:cljs
               (into #{} v)
               :clj
               (into (ordered-set) v))
            (variant-key k "enum")
            (into [:enum] v)
            
            ;; (keyword (str (name k) "/" "vector-of-strs"))
            ;; (mapv name v)
            ;; (keyword (str (name k) "/" "set-of-strs"))
            ;; (into #{} (mapv name v))
            ;; (keyword (str (name k) "/" "vector-of-syms"))
            ;; (mapv symbol v)
            ;; (keyword (str (name k) "/" "set-of-syms"))
            ;; (into #{} (mapv symbol v))
            ))
   {}
   variants*))

(def variants-by-custom-opt-key
  {:weight                 (:weights/set variants)
   :sizing/xxsmall-xlarge  (:sizings-xxsmall-xlarge/set variants)
   :sizing/xsmall-xxxlarge (:sizings-xsmall-xxxlarge/set variants)
   :sizing                 (:sizings/set variants)
   :colorway               (:colorways/set variants)
   :colorway/named         (:colorways-named/set variants)
   :colorway/semantic      (:colorways-semantic/set variants)
   :surface                (:surfaces/set variants)
   :surface/tag            (:surfaces-tag/set variants)
   :packing                (:packings/set variants)
   :position               (:positions/set variants)
   :spinner-type           (:spinner-type/set variants)
   :contour                (:contours/set variants)
   :contour/basic          (:contours-basic/set variants)
   :contour/rounded        (:contours-rounded/set variants)
   :contour/basic+rounded  (:contours-basic+rounded/set variants)
   :contour/auxillary      (:contours-auxillary/set variants)
   :stroke                 (:strokes/set variants)
   :drop-shadow            (:drop-shadows/set variants)
   :shadow-color           (:shadow-colors/set variants)
   :icon-style             (:icon-style/set variants)})

(def enum-variants-by-custom-opt-key
  {:weight                 (:weights/enum variants)
   :sizing/xxsmall-xlarge  (:sizings-xxsmall-xlarge/enum variants)
   :sizing/xsmall-xxxlarge (:sizings-xsmall-xxxlarge/enum variants)
   :sizing                 (:sizings/enum variants)
   :colorway               (:colorways/enum variants)
   :colorway/named         (:colorways-named/enum variants)
   :colorway/semantic      (:colorways-semantic/enum variants)
   :surface                (:surfaces/enum variants)
   :surface/tag            (:surfaces-tag/enum variants)
   :packing                (:packings/enum variants)
   :position               (:positions/enum variants)
   :spinner-type           (:spinner-type/enum variants)
   :contour                (:contours/enum variants)
   :contour/basic          (:contours-basic/enum variants)
   :contour/rounded        (:contours-rounded/enum variants)
   :contour/basic+rounded  (:contours-basic+rounded/enum variants)
   :contour/auxillary      (:contours-auxillary/enum variants)
   :stroke                 (:strokes/enum variants)
   :drop-shadow            (:drop-shadows/enum variants)
   :shadow-color           (:shadow-colors/enum variants)
   :icon-style             (:icon-style/enum variants)})

(def ordered-variants-by-custom-opt-key
  {:weight                 (:weights/vector variants)
   :sizing/xxsmall-xlarge  (:sizings-xxsmall-xlarge/vector variants)
   :sizing/xsmall-xxxlarge (:sizings-xsmall-xxxlarge/vector variants)
   :sizing                 (:sizings/vector variants)
   :colorway               (:colorways/vector variants)
   :colorway/named         (:colorways-named/vector variants)
   :colorway/semantic      (:colorways-semantic/vector variants)
   :surface                (:surfaces/vector variants)
   :surface/tag            (:surfaces-tag/vector variants)
   :packing                (:packings/vector variants)
   :position               (:positions/vector variants)
   :spinner-type           (:spinner-type/vector variants)
   :contour                (:contours/vector variants)
   :contour/basic          (:contours-basic/vector variants)
   :contour/rounded        (:contours-rounded/vector variants)
   :contour/basic+rounded  (:contours-basic+rounded/vector variants)
   :contour/auxillary      (:contours-auxillary/vector variants)
   :stroke                 (:strokes/vector variants)
   :drop-shadow            (:drop-shadows/vector variants)
   :shadow-color           (:shadow-colors/vector variants)
   :icon-style             (:icon-style/vector variants)})



(def variants-by-custom-opt-key-set
  (into #{} (keys variants-by-custom-opt-key)))


(defn convert-opts [vc]
  (reduce 
   (fn [acc [sym m]]
     (let [prefixed (->> sym name (str "-") keyword)
           m (if (contains? variants-by-custom-opt-key prefixed)
               (dissoc m :schema)
               m)
           m (if-not (seq (:args m))
               (dissoc m :args)
               m)]
       (assoc acc prefixed m)))
   {} 
   (partition 2 (second vc))))


(defn shadows? [vc]
  (and (vector? vc)
       (every? (fn [k] 
                 (and (string? k)
                      (re-find #"^var\(--[^\)\s]+\)" k)))
               vc)))


#_[:vector [:and :string [:re #"^var\(--[^\)\s]+\)"]]]


(defn enhancer? [x]
  (or (string? x) (keyword? x) (vector? x)))


(def props
  {:sizing                    {:default nil
                               :desc    "Corresponds to the font-size based on Kushi's font-size scale."}
   :weight                    {:default :normal
                               :desc    "Corresponds to the font-weight based on Kushi's font-weight scale."}
   :colorway                  {:default :neutral
                               :desc    "Colorway of the element. Must be a named color from Kushi's design system e.g `:red` `:purple` `:gold`, `:positive`, etc." }
   :contour                   {:desc    "Shape of the element."
                               :default nil}
  ;;  :shadows                   {
  ;;                              ;; :schema        #(and (vector? %) (every? (fn [k] (and (keyword? k) (->> k name (re-find #"^--\S+|^\$\S+"))) ) %))
  ;;                              ;; TODO maybe :$myvar or "var(--myvar)" or "0 0 10px red" (legit shadow string)
  ;;                              :schema        [:vector [:and :string [:re #"^var\(--[^\)\s]+\)"]]]
  ;;                              :desc          "Vector of design tokens which are values for the CSS box-shadow property."
  ;;                              :default       nil
  ;;                              :when-not-nil  ""
  ;;                              :style-tokens? true  
  ;;                              }

   :drop-shadow               {
                               :schema   [:or :keyword :string [:vector :any]]
                               :desc     "Controls the drop shadow"
                               :default  nil
                               :data-ks? false
                               }

   :shadow-color            {
                               :desc     "Controls the drop shadow"
                               :default  nil
                               }
   :multi-stroke              {
                               :schema   [:vector [:tuple [:or :string :keyword] [:or :string :keyword]]]
                               :desc     "When you want multiple strokes, e.g. `[[:2px :$red-500] [:5px :$green-500] [:2px :$blue-500]]`."
                               :default  nil
                               :data-ks? false
                               }
   :stroke                    {
                               :schema   [:or 
                                          [:enum :none :xsoft :soft :medium :hard :xhard]
                                          [:tuple
                                           {:examples [[:1px :red]
                                                       [:2em :$accent-400]
                                                       ["4px" "rgb(0 0 0 / 0.5)"]
                                                       ["var(--my-width, 1px)" "aliceblue"]]}
                                           [:or :string :keyword] [:or :string :keyword]]
                                          [:vector 
                                           {:examples [[[:3px :$red-500]
                                                        [:3px :$green-500]
                                                        [:3px :$blue-500]]]}
                                           [:tuple [:or :string :keyword] [:or :string :keyword]]]]
                               :desc     "Can be set a number of different ways"
                               :default  nil
                               :data-ks? false
                               }
   :stroke-color              {
                               :schema   [:or :keyword :string]
                               :desc     "Controls the stroke color."
                               :default  "currentColor"
                               ;; support a pred here so you can do :faint :soft :medium :hard
                               :data-ks? false
                               }
   :stroke-align              {:schema   [:enum :inside :outside]
                               :default  nil
                               :desc     "Alignment of the stroke. Only applies to `:surface`."
                               :data-ks? false}
   :stroke-width              {:schema   [:or :string :keyword]
                               :desc     "Width of the stroke. Only applies to `:surface`. Locally sets the value of `--stroke-width`."
                               :data-ks? false}
   :packing                   {:default nil
                               :desc    "General amount of padding inside the element."}
   :end-enhancer              {:schema       [:or :string :keyword [:vector :any]]
                               :default      nil
                               :when-not-nil ""
                               :desc         "Content at the inline-end position preceding the element text. Typically an icon."}
   :start-enhancer            {:schema       [:or :string :keyword [:vector :any]]
                               :default      nil
                               :when-not-nil ""
                               :desc         "Content at the inline-start position following the element text. Typically an icon."}
   :transition                {:schema  :boolean
                               :desc    "When `true` this will enable Kushi's default css `transition-*` values on the element and the elements `:before` and `:after` pseudo-elements"
                               :default true}
   :loading                   {:schema  :boolean
                               :default false
                               :desc    "When `true` this will set the appropriate values for `aria-busy` and `aria-label`."}
   :surface                   {:desc    "Surface variant. Composition of two or more of the following characteristics: background color, foreground color, contrast, surface bevel, and stroke."
                               :default :transparent}
   :inert                     {:schema  :boolean
                               :desc    "Surface is not interactive meaning no hover or active states."
                               :default true}
   :text-transform            {:desc    "Equivalent to the css text-transform property."
                               :default nil}
   :elevation                 {:desc    "Elevation level of the element. Renders a drop-shadow."
                               :default nil}
   :convex                    {:desc    "Elevation level of the element. Renders a drop-shadow."
                               :default nil}
   :fx                        {:desc    "Surface effect such as emboss and deboss."
                               :default nil}
   :icon-enhanceable          {:schema  :boolean
                               :desc    "Element is enhanceable with an icon."
                               :default nil}
   :icon-style                {:desc    "Drawn style of icon, e.g. rounded, outlined, sharp"
                               :default :outlined}
   :icon-filled               {:desc    "Filled or not filled"
                               :schema  :boolean
                               :default false}
   :spinner-type              {:desc    "The design of the spinner"
                               :default :donut}
   :background-image-behavior {:schema  [:enum :cover :contain]
                               :desc    "The behavior of the background image."
                               :default nil}
   :position                  {:desc    "A utility class dictating the element's position."
                               :default "relative"}
   :display                   {:schema  [:or :string :keyword [:vector :keyword]]
                               :desc    "A utility class dictating the element's display properties."
                               :default "inline"}
   :gap                       {:schema  [:enum 0 [:or :string :keyword [:vector :keyword]]]
                               :desc    "A utility class dictating the element's CSS gap value"
                               :default 0}
   })

(def shared-props-enum
  (->> props keys (into [:enum])))

(def prop-families
  ;; TODO - should packing be in here?
  {:container [:sizing
               :colorway
               :contour
               :surface
               :stroke
               :stroke-weight
               :stroke-align
               :inert
               :position
               :background-image-behavior
               :fx
               :convex
               :elevation
               :shadows
               :loading
               :display
               :gap]})
