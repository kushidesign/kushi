(ns ^:dev/always kushi.ui.variants
  (:require 
   [clojure.string :as string]
   [fireworks.core :refer [? !? ?> !?>]]
   [kushi.ui.util :refer [keyed]]
   #?(:clj [kushi.ui.ordered :refer [ordered-set]])
   [bling.util :as util]))

;; TODO - make a subvec utility for generating scales like :medium-xxxlarge
(defn- tshirt-size-with-prefix [])

(defn- tshirt-size* [prefix postfix cast-fn with-size-str]
  (-> prefix
      util/as-str 
      (str (when prefix "-")
           with-size-str
           (when postfix "-")
           postfix)
      cast-fn))

(defn tshirt-sizes
  {:examples [{:call   '(tshirt-sizes [:small :medium :large]
                                      {:number-of-sizes 3
                                       :prefix          :rounded
                                       :cast-fn         keyword})
               :result [:rounded-xxxsmall
                        :rounded-xxsmall
                        :rounded-xsmall
                        :rounded-medium
                        :rounded-xlarge
                        :rounded-xxlarge
                        :rounded-xxxlarge]}]}
  [[a default b]
   {:keys [prefix postfix cast-fn]
    n     :number-of-sizes
    :or   {cast-fn util/as-str}}]
  (into [] 
        (let [rng           (range (inc n))
              f             (partial tshirt-size* prefix postfix cast-fn)
              with-size-str #(str (string/join (repeat %1 "x"))
                                  (util/as-str %2))]
          (concat (for [i (reverse rng)] (f (with-size-str i a)))
                  [(f (util/as-str default))]
                  (for [i rng] (f (with-size-str i b)))))))

(def xxxsmall-xxxlarge
 [:xxxsmall :xxsmall :xsmall :small :medium :large :xlarge :xxlarge :xxxlarge])

(def shapes-rounded
  [:rounded
   :rounded-xxxsmall
   :rounded-xxsmall
   :rounded-xsmall
   :rounded-small
   :rounded-medium
   :rounded-large
   :rounded-xlarge
   :rounded-xxlarge
   :rounded-xxxlarge])

(def shapes-rounded-absolute
  [:rounded-absolute
   :rounded-xxxsmall-absolute
   :rounded-xxsmall-absolute
   :rounded-xsmall-absolute
   :rounded-small-absolute
   :rounded-medium-absolute
   :rounded-large-absolute
   :rounded-xlarge-absolute
   :rounded-xxlarge-absolute
   :rounded-xxxlarge-absolute])

(def shapes-rounded-medium-xxxlarge
  [:rounded-medium
   :rounded-large
   :rounded-xlarge
   :rounded-xxlarge
   :rounded-xxxlarge])

(def shapes-rounded-medium-xxxlarge-absolute
  [:rounded-medium-absolute
   :rounded-large-absolute
   :rounded-xlarge-absolute
   :rounded-xxlarge-absolute
   :rounded-xxxlarge-absolute])

(def shapes-basic
  [:pill :rounded :sharp])

(def shapes-auxillary
  [:circle :squircle])

(def shapes-basic+rounded
  (into [] (distinct (apply conj shapes-basic shapes-rounded))))

(def shapes-rounded+rounded-absolute
  (apply conj shapes-rounded shapes-rounded-absolute))

(def shapes
  (into [] 
        (distinct (concat shapes-basic
                          shapes-rounded
                          shapes-rounded-absolute
                          shapes-auxillary))))

(def strokes
  [:none :xsoft :soft :medium :hard :xhard])

(def shadows xxxsmall-xxxlarge)

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

(def sizes-xxsmall-xlarge
  [:xxsmall :xsmall :small :medium :large :xlarge])

(def sizes-xsmall-xxlarge
  [:xsmall :small :medium :large :xlarge :xxlarge])

(def sizes-xsmall-xxxlarge
  [:xsmall :small :medium :large :xlarge :xxlarge :xxxlarge])

(def sizes-large-xxxlarge
  [:large :xlarge :xxlarge :xxxlarge])
          
(def sizes xxxsmall-xxxlarge)

(def weights
  [:thin :extra-light :light :normal :wee-bold :semi-bold :bold :extra-bold :heavy])

(def surfaces-basic
  [:solid-classic
   :solid
   :soft-classic
   :soft
   :convex
   :faint
   :minimal
   :transparent])


(def surfaces-light-mode [:minimal-light-mode :convex-light-mode])

(def surfaces 
  (apply conj surfaces-basic surfaces-light-mode ))

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
  (keyed [shapes-basic
          shapes-auxillary
          shapes-basic+rounded
          shapes-rounded
          shapes
          shapes-rounded-medium-xxxlarge
          shapes-rounded-absolute
          shapes-rounded+rounded-absolute
          shapes-rounded-medium-xxxlarge-absolute
          strokes
          shadows
          shadow-colors
          icon-style
          spinner-type
          packings
          positions
          colorways-named
          colorways-semantic
          colorways
          sizes-xsmall-xxlarge
          sizes-xxsmall-xlarge
          sizes-xsmall-xxxlarge
          sizes-large-xxxlarge
          sizes
          weights
          surfaces-basic
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
  {:weight                                   (:weights/set variants)
   :size/xxsmall-xlarge                    (:sizes-xxsmall-xlarge/set variants)
   :size/xsmall-xxxlarge                   (:sizes-xsmall-xxxlarge/set variants)
   :size/xsmall-xxlarge                    (:sizes-xsmall-xxlarge/set variants)
   :size/large-xxxlarge                    (:sizes-large-xxxlarge/set variants)
   :size                                   (:sizes/set variants)
   :colorway                                 (:colorways/set variants)
   :colorway/named                           (:colorways-named/set variants)
   :colorway/semantic                        (:colorways-semantic/set variants)
   :surface/basic                            (:surfaces-basic/set variants)
   :surface                                  (:surfaces/set variants)
   :surface/tag                              (:surfaces-tag/set variants)
   :packing                                  (:packings/set variants)
   :position                                 (:positions/set variants)
   :spinner-type                             (:spinner-type/set variants)
   :shape                                  (:shapes/set variants)
   :shape/basic                            (:shapes-basic/set variants)
   :shape/rounded                          (:shapes-rounded/set variants)
   :shape/basic+rounded                    (:shapes-basic+rounded/set variants)
   :shape/auxillary                        (:shapes-auxillary/set variants)
   :shape/rounded-medium-xxxlarge          (:shapes-rounded-medium-xxxlarge/set variants)
   :shape/rounded-absolute                 (:shapes-rounded-absolute/set variants)
   :shape/rounded-medium-xxxlarge-absolute (:shapes-rounded-medium-xxxlarge-absolute/set variants)
   :shape/rounded+rounded-absolute         (:shapes-rounded+rounded-absolute/set variants)
   :stroke                                   (:strokes/set variants)
   :shadow                              (:shadows/set variants)
   :shadow-color                             (:shadow-colors/set variants)
   :icon-style                               (:icon-style/set variants)})

(def enum-variants-by-custom-opt-key
  {:weight                                   (:weights/enum variants)
   :size/xxsmall-xlarge                    (:sizes-xxsmall-xlarge/enum variants)
   :size/xsmall-xxxlarge                   (:sizes-xsmall-xxxlarge/enum variants)
   :size/xsmall-xxlarge                    (:sizes-xsmall-xxlarge/enum variants)
   :size/large-xxxlarge                    (:sizes-large-xxxlarge/enum variants)
   :size                                   (:sizes/enum variants)
   :colorway                                 (:colorways/enum variants)
   :colorway/named                           (:colorways-named/enum variants)
   :colorway/semantic                        (:colorways-semantic/enum variants)
   :surface/basic                            (:surfaces-basic/enum variants)
   :surface                                  (:surfaces/enum variants)
   :surface/tag                              (:surfaces-tag/enum variants)
   :packing                                  (:packings/enum variants)
   :position                                 (:positions/enum variants)
   :spinner-type                             (:spinner-type/enum variants)
   :shape                                  (:shapes/enum variants)
   :shape/basic                            (:shapes-basic/enum variants)
   :shape/rounded                          (:shapes-rounded/enum variants)
   :shape/basic+rounded                    (:shapes-basic+rounded/enum variants)
   :shape/auxillary                        (:shapes-auxillary/enum variants)
   :shape/rounded-medium-xxxlarge          (:shapes-rounded-medium-xxxlarge/enum variants)
   :shape/rounded-absolute                 (:shapes-rounded-absolute/enum variants)
   :shape/rounded-medium-xxxlarge-absolute (:shapes-rounded-medium-xxxlarge-absolute/enum variants)
   :shape/rounded+rounded-absolute         (:shapes-rounded+rounded-absolute/enum variants)
   :stroke                                   (:strokes/enum variants)
   :shadow                              (:shadows/enum variants)
   :shadow-color                             (:shadow-colors/enum variants)
   :icon-style                               (:icon-style/enum variants)})

(def ordered-variants-by-custom-opt-key
  {:weight                                   (:weights/vector variants)
   :size/xxsmall-xlarge                    (:sizes-xxsmall-xlarge/vector variants)
   :size/xsmall-xxxlarge                   (:sizes-xsmall-xxxlarge/vector variants)
   :size/xsmall-xxlarge                    (:sizes-xsmall-xxlarge/vector variants)
   :size/large-xxxlarge                    (:sizes-large-xxxlarge/vector variants)
   :size                                   (:sizes/vector variants)
   :colorway                                 (:colorways/vector variants)
   :colorway/named                           (:colorways-named/vector variants)
   :colorway/semantic                        (:colorways-semantic/vector variants)
   :surface/basic                            (:surfaces-basic/vector variants)
   :surface                                  (:surfaces/vector variants)
   :surface/tag                              (:surfaces-tag/vector variants)
   :packing                                  (:packings/vector variants)
   :position                                 (:positions/vector variants)
   :spinner-type                             (:spinner-type/vector variants)
   :shape                                  (:shapes/vector variants)
   :shape/basic                            (:shapes-basic/vector variants)
   :shape/rounded                          (:shapes-rounded/vector variants)
   :shape/basic+rounded                    (:shapes-basic+rounded/vector variants)
   :shape/auxillary                        (:shapes-auxillary/vector variants)
   :shape/rounded-medium-xxxlarge          (:shapes-rounded-medium-xxxlarge/vector variants)
   :shape/rounded-absolute                 (:shapes-rounded-absolute/vector variants)
   :shape/rounded-medium-xxxlarge-absolute (:shapes-rounded-medium-xxxlarge-absolute/vector variants)
   :shape/rounded+rounded-absolute         (:shapes-rounded+rounded-absolute/vector variants)
   :stroke                                   (:strokes/vector variants)
   :shadow                              (:shadows/vector variants)
   :shadow-color                             (:shadow-colors/vector variants)
   :icon-style                               (:icon-style/vector variants)})



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

  {
   ;; change to size
   :size           {:default  nil
                      :desc     "Corresponds to the font-size based on Kushi's font-size scale."
                      :class? true
                      :fq?      true}

   :weight           {:default :normal
                      :desc    "Corresponds to the font-weight based on Kushi's font-weight scale."
                      :class?  true
                      :fq?     true}

   :position         {:desc    "A utility class dictating the element's position."
                      :default "relative"
                      :class?  true}

   :display          {:schema  [:or :string :keyword [:vector :keyword]]
                      :desc    "A utility class dictating the element's display properties."
                      :default "inline"
                      :class?  true}

   :colorway         {:default nil   ;;  <- TODO should this be nil?
                      :desc    "Colorway of the element. Must be a named color from Kushi's design system e.g `:red` `:purple` `:gold`, `:positive`, etc."
                      :class?  true}

   :shape            {:desc    "Shape of the element, corresponds to a Kushi's border-radius scale"
                      :default nil
                      :class?  true
                      :fq?     true}

  ;;  :shadows                   {
  ;;                              ;; :schema        #(and (vector? %) (every? (fn [k] (and (keyword? k) (->> k name (re-find #"^--\S+|^\$\S+"))) ) %))
  ;;                              ;; TODO maybe :$myvar or "var(--myvar)" or "0 0 10px red" (legit shadow string)
  ;;                              :schema        [:vector [:and :string [:re #"^var\(--[^\)\s]+\)"]]]
  ;;                              :desc          "Vector of design tokens which are values for the CSS box-shadow property."
  ;;                              :default       nil
  ;;                              :when-not-nil  ""
  ;;                              :style-tokens? true  
  ;;                              }
   
   ;; change to shadow
   :shadow      {:schema  [:or
                                [:and :keyword (:shadows/enum variants)]
                                :string 
                                [:vector :any]]
                      :desc    "Controls the drop shadow. If not combined with a `:stroke`, correspondes to a design token from Kushi's shadow scale."
                      :default nil
                      :class?  true}

   :shadow-color     {:desc    "Controls the drop shadow"
                      :class?  true
                      :default nil}

   :stroke           {:schema  [:or 
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
                      :desc    "Can be set a number of different ways"
                      :default nil
                      :class?  true
                      }

   :stroke-color     {:schema   [:or :keyword :string]
                      :desc     "Controls the stroke color."
                      ;; leave :default off for now
                      ;; :default  "currentColor"
                      :class?  true
                      }

   :stroke-align     {:schema  [:enum :inside :outside]
                      :default nil
                      :desc    "Alignment of the stroke. Only applies to `:surface`."
                      :class?  true}

   :stroke-width     {:schema   [:or :string :keyword]
                      :desc     "Width of the stroke. Only applies to `:surface`. Locally sets the value of `--stroke-width`."
                      }

   :packing          {:default nil
                      :desc    "General amount of padding inside the element."
                      :class?  true}

   ;; TODO should this just be [:or :string :keyword] , :string for text, :keyword for icon ?
   :end-enhancer     {:schema       [:or :string :keyword [:vector :any]]
                      :default      nil
                      :when-not-nil ""
                      :desc         "Content at the inline-end position preceding the element text. Typically an icon."
                      :class?       true
                      }

   :start-enhancer   {:schema       [:or :string :keyword [:vector :any]]
                      :default      nil
                      :when-not-nil ""
                      :desc         "Content at the inline-start position following the element text. Typically an icon."
                      :class?  true
                      }

   :transition       {:schema  :boolean
                      :desc    "When `true` this will enable Kushi's default css `transition-*` values on the element and the elements `:before` and `:after` pseudo-elements"
                      :default true
                      :class?  true
                      }

   :loading          {:schema  :boolean
                      :default false
                      :desc    "When `true` this will set the appropriate values for `aria-busy` and `aria-label`."
                      }

   :surface          {:desc    "Surface variant. Composition of two or more of the following characteristics: background color, foreground color, contrast, surface bevel, and stroke."
                      :default :transparent
                      :class?  true
                      }

   :inert            {:schema  :boolean
                      :desc    "Surface is not interactive meaning no hover or active states."
                      :default true
                      :class?  true
                      }

   ;; Need this since it is an html attribut already?
   :required         {:schema  :boolean
                      :desc    "HTML `required` attribute for elements such as input etc."
                      :default nil}

  ;; Leave out for brevity
  ;;  :text-transform   {:desc    "Equivalent to the css text-transform property."
  ;;                     :default nil}

   :icon-enhanceable {:schema  :boolean
                      :desc    "Element is enhanceable with an icon."
                      :default nil
                      :class?  true
                      }

   :icon-style       {:desc     "Drawn style of icon, e.g. rounded, outlined, sharp"
                      :default  :outlined
                      }

   :icon-filled      {:desc     "Filled or not filled"
                      :schema   :boolean
                      :default  false
                      }

   :spinner-type     {:desc    "The design of the spinner"
                      :default :donut}

   })

(def shared-props-enum
  (->> props keys (into [:enum])))

(def prop-families
  ;; TODO - should packing be in here?
  {:container [:size
               :colorway
               :shape
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
