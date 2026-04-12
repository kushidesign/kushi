(ns ^:dev/always kushi.ui.variants
  (:require 
   [clojure.string :as string]
   [fireworks.core :refer [? !? ?> !?>]]
   [kushi.ui.util :refer [keyed]]
   [kushi.util :refer [when-> as-str insert-at str-ml]]
   [malli.core]
   [kushi.ui.defs :as defs]
   #?(:clj [kushi.ui.ordered :refer [ordered-set]])
   ))

;; TODO - make a subvec utility for generating scales like :md-3xl
(defn- tshirt-size-with-prefix [])

(defn- tshirt-size* [prefix postfix cast-fn with-size-str]
  (-> prefix
      as-str 
      (str (when prefix "-")
           with-size-str
           (when postfix "-")
           postfix)
      cast-fn))

(defn tshirt-sizes
  {:examples [{:call   '(tshirt-sizes [:sm :md :lg]
                                      {:number-of-sizes 3
                                       :prefix          :rounded
                                       :cast-fn         keyword})
               :result [:rounded-3xs
                        :rounded-2xs
                        :rounded-xs
                        :rounded-md
                        :rounded-xl
                        :rounded-2xl
                        :rounded-3xl]}]}
  [[a default b]
   {:keys [prefix postfix cast-fn]
    n     :number-of-sizes
    :or   {cast-fn as-str}}]
  (into [] 
        (let [rng           (range (inc n))
              f             (partial tshirt-size* prefix postfix cast-fn)
              with-size-str #(str (string/join (repeat %1 "x"))
                                  (as-str %2))]
          (concat (for [i (reverse rng)] (f (with-size-str i a)))
                  [(f (as-str default))]
                  (for [i rng] (f (with-size-str i b)))))))

(def sizes-3xs-3xl
 [:3xs :2xs :xs :sm :md :lg :xl :2xl :3xl])

(def font-sizes-3xs-3xl
 [:3xs :2xs :xs :sm :base :lg :xl :2xl :3xl])

(def shapes-rounded
  [:rounded
   :rounded-3xs
   :rounded-2xs
   :rounded-xs
   :rounded-sm
   :rounded-md
   :rounded-lg
   :rounded-xl
   :rounded-2xl
   :rounded-3xl])

(def shapes-rounded-absolute
  [:rounded-absolute
   :rounded-3xs-absolute
   :rounded-2xs-absolute
   :rounded-xs-absolute
   :rounded-sm-absolute
   :rounded-md-absolute
   :rounded-lg-absolute
   :rounded-xl-absolute
   :rounded-2xl-absolute
   :rounded-3xl-absolute])

(def shapes-rounded-md-3xl
  [:rounded-md
   :rounded-lg
   :rounded-xl
   :rounded-2xl
   :rounded-3xl])

(def shapes-rounded-md-3xl-absolute
  [:rounded-md-absolute
   :rounded-lg-absolute
   :rounded-xl-absolute
   :rounded-2xl-absolute
   :rounded-3xl-absolute])

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
  [:none :xsoft :soft :md :hard :xhard])

(def shadow-sizes sizes-3xs-3xl)

(def icon-style
  [:rounded :outlined :sharp])

(def spinner-type
  [:donut :thinking :propeller])

(def packings
  [:xcompact :compact :default :roomy :xroomy])

;; Pull this from colors namespace?
(def colorways-named defs/basic-colors)

(def colorways-semantic defs/semantic-colors)

(def colorways
  (apply conj colorways-named colorways-semantic))

(def shadow-colors
  (apply conj colorways-named colorways-semantic))

(def stroke-colors
  (apply conj colorways-named colorways-semantic))

(def font-sizes-2xs-lg
  [:2xs :xs :sm :base :lg])

(def font-sizes-2xs-xl
  [:2xs :xs :sm :base :lg :xl])

(def font-sizes-xs-2xl
  [:xs :sm :base :lg :xl :2xl])

(def font-sizes-xs-3xl
  [:xs :sm :base :lg :xl :2xl :3xl])

(def font-sizes-lg-3xl
  [:lg :xl :2xl :3xl])
          
(def font-sizes font-sizes-3xs-3xl)

(def weights
  [:thin
   :extra-light
   :light 
   :normal
   :wee-bold
   :semi-bold 
   :bold 
   :extra-bold
   :heavy])


;; TODO - why both basic and simple?
(def surfaces-basic
  [:solid-classic
   :solid
   :convex
   :soft-classic
   :soft
   :faint
   :minimal
   :transparent
   :ghost])

(def surfaces-simple
  [:solid
   :soft
   :faint
   :minimal
   :transparent
   :ghost])

(def surfaces-light-mode [:minimal-light-mode :convex-light-mode])

(def surfaces 
  (apply conj surfaces-basic surfaces-light-mode ))

;; (def displays
;;   )

(def positions 
  [:fixed-inline-start-inside
   :absolute-inline-end-inside
   :fixed-fill
   :absolute-block-end-inside
   :absolute-block-end-outside
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


(def variants*
  (keyed [shapes-basic
          shapes-auxillary
          shapes-basic+rounded
          shapes-rounded
          shapes
          shapes-rounded-md-3xl
          shapes-rounded-absolute
          shapes-rounded+rounded-absolute
          shapes-rounded-md-3xl-absolute
          shadow-sizes
          shadow-colors
          stroke-colors
          icon-style
          spinner-type
          packings
          positions
          colorways-named
          colorways-semantic
          colorways
          font-sizes-xs-2xl
          font-sizes-2xs-xl
          font-sizes-2xs-lg
          font-sizes-xs-3xl
          font-sizes-lg-3xl
          font-sizes
          weights
          surfaces-basic
          surfaces
          surfaces-simple]))

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
  {:weight                         (:weights/set variants)
   :size/font-sizes-2xs-lg         (:font-sizes-2xs-lg/set variants)
   :size/font-sizes-2xs-xl         (:font-sizes-2xs-xl/set variants)
   :size/font-sizes-xs-3xl         (:font-sizes-xs-3xl/set variants)
   :size/font-sizes-xs-2xl         (:font-sizes-xs-2xl/set variants)
   :size/lg-3xl                    (:font-sizes-lg-3xl/set variants)
   :size                           (:font-sizes/set variants)
   :colorway                       (:colorways/set variants)
   :colorway/named                 (:colorways-named/set variants)
   :colorway/semantic              (:colorways-semantic/set variants)
   :surface/basic                  (:surfaces-basic/set variants)
   :surface                        (:surfaces/set variants)
   :surface/simple                 (:surfaces-simple/set variants)
   :packing                        (:packings/set variants)
   :position                       (:positions/set variants)
   :spinner-type                   (:spinner-type/set variants)
   :shape                          (:shapes/set variants)
   :shape/basic                    (:shapes-basic/set variants)
   :shape/rounded                  (:shapes-rounded/set variants)
   :shape/basic+rounded            (:shapes-basic+rounded/set variants)
   :shape/auxillary                (:shapes-auxillary/set variants)
   :shape/rounded-md-3xl           (:shapes-rounded-md-3xl/set variants)
   :shape/rounded-absolute         (:shapes-rounded-absolute/set variants)
   :shape/rounded-md-3xl-absolute  (:shapes-rounded-md-3xl-absolute/set variants)
   :shape/rounded+rounded-absolute (:shapes-rounded+rounded-absolute/set variants)
   :stroke                         (:strokes/set variants)
   :shadow-size                    (:shadow-sizes/set variants)
   :shadow-color                   (:shadow-colors/set variants)
   :stroke-color                   (:shadow-colors/set variants)
   :icon-style                     (:icon-style/set variants)})

(def enum-variants-by-custom-opt-key
  {:weight                         (:weights/enum variants)
   :size/font-sizes-2xs-lg         (:font-sizes-2xs-lg/enum variants)
   :size/font-sizes-2xs-xl         (:font-sizes-2xs-xl/enum variants)
   :size/font-sizes-xs-3xl         (:font-sizes-xs-3xl/enum variants)
   :size/font-sizes-xs-2xl         (:font-sizes-xs-2xl/enum variants)
   :size/font-sizes-lg-3xl         (:font-sizes-lg-3xl/enum variants)
   :size                           (:font-sizes/enum variants)
   :colorway                       (:colorways/enum variants)
   :colorway/named                 (:colorways-named/enum variants)
   :colorway/semantic              (:colorways-semantic/enum variants)
   :surface/basic                  (:surfaces-basic/enum variants)
   :surface                        (:surfaces/enum variants)
   :surface/simple                 (:surfaces-simple/enum variants)
   :packing                        (:packings/enum variants)
   :position                       (:positions/enum variants)
   :spinner-type                   (:spinner-type/enum variants)
   :shape                          (:shapes/enum variants)
   :shape/basic                    (:shapes-basic/enum variants)
   :shape/rounded                  (:shapes-rounded/enum variants)
   :shape/basic+rounded            (:shapes-basic+rounded/enum variants)
   :shape/auxillary                (:shapes-auxillary/enum variants)
   :shape/rounded-md-3xl           (:shapes-rounded-md-3xl/enum variants)
   :shape/rounded-absolute         (:shapes-rounded-absolute/enum variants)
   :shape/rounded-md-3xl-absolute  (:shapes-rounded-md-3xl-absolute/enum variants)
   :shape/rounded+rounded-absolute (:shapes-rounded+rounded-absolute/enum variants)
   :stroke                         (:strokes/enum variants)
   :shadow-size                    (:shadow-sizes/enum variants)
   :shadow-color                   (:shadow-colors/enum variants)
   :stroke-color                   (:stroke-colors/enum variants)
   :icon-style                     (:icon-style/enum variants)})

(def ordered-variants-by-custom-opt-key
  {:weight                         (:weights/vector variants)
   :size/font-sizes-2xs-lg         (:font-sizes-2xs-lg/vector variants)
   :size/font-sizes-2xs-xl         (:font-sizes-2xs-xl/vector variants)
   :size/font-sizes-xs-3xl         (:font-sizes-xs-3xl/vector variants)
   :size/font-sizes-xs-2xl         (:font-sizes-xs-2xl/vector variants)
   :size/font-sizes-lg-3xl         (:font-sizes-lg-3xl/vector variants)
   :size                           (:font-sizes/vector variants)
   :colorway                       (:colorways/vector variants)
   :colorway/named                 (:colorways-named/vector variants)
   :colorway/semantic              (:colorways-semantic/vector variants)
   :surface/basic                  (:surfaces-basic/vector variants)
   :surface                        (:surfaces/vector variants)
   :surface/simple                 (:surfaces-simple/vector variants)
   :packing                        (:packings/vector variants)
   :position                       (:positions/vector variants)
   :spinner-type                   (:spinner-type/vector variants)
   :shape                          (:shapes/vector variants)
   :shape/basic                    (:shapes-basic/vector variants)
   :shape/rounded                  (:shapes-rounded/vector variants)
   :shape/basic+rounded            (:shapes-basic+rounded/vector variants)
   :shape/auxillary                (:shapes-auxillary/vector variants)
   :shape/rounded-md-3xl           (:shapes-rounded-md-3xl/vector variants)
   :shape/rounded-absolute         (:shapes-rounded-absolute/vector variants)
   :shape/rounded-md-3xl-absolute  (:shapes-rounded-md-3xl-absolute/vector variants)
   :shape/rounded+rounded-absolute (:shapes-rounded+rounded-absolute/vector variants)
   :stroke                         (:strokes/vector variants)
   :shadow-size                    (:shadow-sizes/vector variants)
   :shadow-color                   (:shadow-colors/vector variants)
   :stroke-color                   (:stroke-colors/vector variants)
   :icon-style                     (:icon-style/vector variants)})


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

(defn percentage? [x]
  (re-find #"^0\%$|^100%$|^[0-9][0-9]?(?:\.[0-9]+)?%$" (name x)))

(def props
  {:size             {:default  nil
                      :desc     "Corresponds to the font-size based on Kushi's
                                 font-size scale."
                      :class?   true
                      :data-ks? true 
                      :fq?      true}

   :weight           {:default  nil
                      :desc     "Corresponds to the font-weight based on Kushi's
                                 font-weight scale."
                      :class?   true
                      :data-ks? true 
                      :fq?      true}

   :position         {:desc     "A utility class dictating the element's position."
                      :default  "relative"
                      :class?   true
                      :data-ks? true }

   :display          {:schema   [:or :string :keyword [:vector :keyword]]
                      :desc     "A utility class dictating the element's
                                 display properties."
                      :default  nil
                      :class?   true
                      :data-ks? true }

   :colorway         {:default            :nil ;;  <- TODO should this be nil?
                      :default-on-invalid :neutral
                      :desc               (str-ml
                                           "Colorway of the element. Must be a named color
                                            from Kushi's design system e.g `:red` `:purple`
                                            `:gold`, `:positive`, etc.")
                      :data-ks?           true }

   :shape            {:desc     (str-ml
                                 "Shape of the element, corresponds to a Kushi's
                                  border-radius scale")
                      :default  nil
                      :data-ks? true 
                      :fq?      true}

   ;;  :shadow           {:schema   [:or
   ;;                                :string         ; <-css shadow value 
   ;;                                [:vector :any]] ; <-vector of values
   ;;                     ;; provide example
   ;;                     :desc     "Supply a custom drop shadow via a vector"
   ;;                     :default  nil
   ;;                     :class?   true
   ;;                     :data-ks? true }
   
   :shadow-size      {:desc         (str-ml
                                     "If a keyword such as `:2xs` or `:lg` is used,
                                  and not combined with a `:stroke`, correspondes
                                  to a design token from Kushi's shadow scale.")
                      :local-token? true ; <- TODO  not really a local token, but sets :box-shadow in style
                      :default      nil
                      :data-ks?     true}

   :shadow-color     {:desc         (str-ml 
                                     "Controls the drop shadow color. Takes 
                                      effect if a value such as `:2xs` or `:lg`
                                      is supplied to the `:shadow-size` prop.")
                      :local-token? true
                      :default      nil}

   :shadow-opacity   {:schema       [:or
                                     :keyword
                                     :string] ;; <- maybe just :keyword?
                      :desc         (str-ml
                                     "Controls the drop shadow strength. Takes
                                      effect if a value such as `:2xs` or `:lg`
                                      is supplied to the `:shadow` prop.")
                      :local-token? true
                      :default      nil}

   :stroke           {:schema   [:or 
                                 #_[:enum :none :xsoft :soft :md :hard :xhard]
                                 [:tuple
                                  {:desc     "For adding both a width and color"
                                   :examples [
                                              ;; TODO - add :desc to each of these examples?
                                              [:1px :red]
                                              [:2em :$accent-400]
                                              ["4px" "rgb(0 0 0 / 0.5)"]
                                              ["var(--my-width, 1px)" "aliceblue"]]}
                                  [:or :string :keyword]
                                  [:or :string :keyword]]
                                 [:vector 
                                  {:desc     "For creating multi-strokes"
                                   :examples [[[:3px :$red-500]
                                               [:3px :$green-500]
                                               [:3px :$blue-500]]]}
                                  [:tuple [:or :string :keyword] [:or :string :keyword]]]]
                      :desc     "Can be set a number of different ways"
                      :default  nil
                      :data-ks? true}

   :stroke-opacity   {:schema       [:or
                                     [:float {:min 0.0
                                              :max 1.0}]
                                     [:enum 0 1]
                                     [:and 
                                      [:or :string :keyword]
                                      [:fn kushi.ui.variants/percentage?]]]
                      :local-token? true
                      :desc         (str-ml
                                     "Opacity of the stroke. Only applies when a
                                      `:surface` value is provided. Locally sets
                                      the value of `--stroke-opacity`.")}

   :stroke-color     {:schema       [:or :keyword :string]
                      :desc         (str-ml
                                     "Controls the stroke color, unless `:stroke`
                                      is set as a tuple or vector containing a
                                      color value(s).")
                      ;; leave :default off for now
                      ;; :default  "currentColor"
                      :local-token? true}

   :stroke-align     {:schema   [:enum :inside :outside]
                      :default  nil
                      :desc     (str-ml
                                 "Alignment of the stroke. Only applies when
                                      a `:surface` value is provided.")
                      :data-ks? true}

   :stroke-width     {:schema       [:or :string :keyword]
                      :default      nil
                      :local-token? true 
                      :data-ks?     true
                      :desc         (str-ml
                                     "Width of the stroke. Only applies when a
                                      `:surface` value is provided. If set, a
                                      stroke will be rendered with the value of
                                      `currentColor`(or value of `:stroke-color`),
                                      and a default opacity of `50%` (or the value
                                      of `:stroke-opacity`). Locally sets the
                                      value of `--stroke-width`, and locally sets
                                      a `--box-shadow-for-stroke value.")}

   :packing          {:default  nil
                      :desc     "General amount of padding inside the element."
                      :data-ks? true}

   ;; TODO should this just be [:or :string :keyword] , :string for text, :keyword for icon ?
   :end-enhancer     {:schema       [:or :string :keyword [:vector :any]]
                      :default      nil
                      :when-not-nil ""
                      :desc         (str-ml
                                     "Content at the inline-end position preceding
                                      the element text. Typically an icon.")
                      }

   :start-enhancer   {:schema       [:or :string :keyword [:vector :any]]
                      :default      nil
                      :when-not-nil ""
                      :desc         (str-ml
                                     "Content at the inline-start position following
                                      the element text. Typically an icon.")
                      }

   :transition       {:schema   :boolean
                      :desc     (str-ml "When `true` this will enable Kushi's
                                         default css `transition-*` values on
                                         the element and the elements `:before`
                                         and `:after` pseudo-elements")
                      :default  true
                      ;; :class?   true 
                      :data-ks? true}

   :surface          {:desc     (str-ml "Surface variant. Composition of two or
                                         more of the following characteristics:
                                         background color, foreground color, 
                                         contrast, surface bevel, and stroke.")
                      :default  nil ;;  <- TODO should this be nil?
                      ;; :class?   true 
                      :data-ks? true}

   ;; Should this become :interactive?
   ;; TODO - maybe defaults to true, but not for certain tags such as :button :link and similar components
   :inert            {:schema   :boolean
                      :desc     (str-ml
                                 "Surface is not interactive meaning no
                                  hover or active states.")
                      :default  nil
                      ;; :class?   true
                      :data-ks? true}

   ;; Need this since it is an html attribut already?
   :required         {:schema  :boolean
                      :desc    (str-ml
                                "HTML `required` attribute for elements
                                 such as input etc.")
                      :default nil}

   :icon-enhanceable {:schema   :boolean
                      :desc     (str-ml
                                 "Element is enhanceable with an icon.")
                      :default  nil
                      :data-ks? true }

   :icon-style       {:desc    (str-ml "Drawn style of icon, e.g. rounded,
                                        outlined, sharp")
                      :default :outlined}

   :icon-filled      {:desc    "Filled or not filled"
                      :schema  :boolean
                      :default false} ;; why false and not nil
   
   :spinner-type     {:desc    "The design of the spinner"
                      :default :donut}

   :props/custom     {:schema  [:vector :any]
                      :desc    (str-ml "For extra props.")
                      :default nil}})

#?(:cljs
   ()
   :clj
   (do 
    ;;  (!? font-sizes)
    ;;  (!? :pp (keys variants))
     #_(m/validate [] "100%")))

(def malli-map-keys
  "These are used for validation of args to sx during macroexpansion, and also
   during runtime, in dev mode.

   Produces a vector of vecs like:
   [:shadow-size
    {...}
    <schema>]
   
   This vector is used to construct a schema in kushi.css.schemas:
   (into [:map {...}] <vector>)"
  (reduce-kv
      (fn [vc k m]
        (conj vc
              [k
               (!? :no-file k (-> m
                                  (dissoc :schema :default)
                                  (assoc :optional true)
                                  (merge (when-not (nil? (:default m))
                                           {:default (:default m)})))) 
               (!? {:when (= k :shadow-size)}
                   (let [schema       (or (:schema m)
                                          (k enum-variants-by-custom-opt-key))
                         with-symbol  (if (some-> schema
                                                  (when-> vector?)
                                                  first
                                                  (= :or))
                                        (insert-at schema 1 :symbol)
                                        [:or :symbol schema])
                         with-options (insert-at with-symbol 1 {:bling.explain/display-schema? true})]
                     with-options))]))
      []
      props))

(def local-tokens
  (reduce-kv (fn [coll k v]
               (if (:local-token? v) (conj coll k) coll))
             #{} 
             props))

(def defaults-on-invalid
  (reduce-kv (fn [m k v]
               (or (some->> v :default-on-invalid (assoc m k))
                   m))
             {} 
             props))

(defn shadow-or-stroke-box-shadow [kw v]
  (cond-> {:box-shadow
           "var(--box-shadow-for-shadow, 0 0 0 0 transparent), var(--box-shadow-for-stroke, 0 0 0 0 transparent)"}
    (= kw :stroke-width)
    (assoc (str "--" (name kw)) (as-str v))))

(defn shadow-or-stroke-color [kw _ v]
  (let [kushi-color? (contains? (kw variants-by-custom-opt-key) 
                                (if (string? v) (keyword v) v))
        s            (as-str v)
        f            #(if kushi-color? (str "var(--" s "-" % ")") s)]
    {(str "--" (name kw))              (f 700)
     (str "--" (name kw) "-dark-mode") (f 300)}))

(def local-token-transformers
  {:shadow-color (partial shadow-or-stroke-color :shadow-color)
   :shadow-size  shadow-or-stroke-box-shadow
   :stroke-width shadow-or-stroke-box-shadow
   :stroke-color (partial shadow-or-stroke-color :stroke-color)
   #_(fn [_ v]
       (let [kushi-color? (contains?
                           (:stroke-color variants-by-custom-opt-key)
                           v)
             s            (as-str v)]
         {"--stroke-color"           (if kushi-color? 
                                       (str "var(--" s "-700)")
                                       s)
          "--stroke-color-dark-mode" (if kushi-color? 
                                       (str "var(--" s "-300)")
                                       s)}))
   
   })

(def data-ks-transformers 
  {:stroke-width (fn [_ _] {"data-ks-stroke" ""})})


(def shared-props-keys
  (!? (->> props keys (apply hash-set))))


(def shared-props-enum
  (into [:enum] shared-props-keys))


(def prop-families
  ;; TODO - should packing be in here?
  {:container [:size
               :weight
               :display
               :position

               :colorway                   ;; add twists and turns 
               :shape                      ;; add shapes like squircle and blob
               :surface                    ;; add surfaces like ?

               :stroke                     ;; custom vector only?
               :stroke-opacity 
               :stroke-align 
               :stroke-width

               :shadow
               :shadow-color
               :shadow-opacity

               :background-image-behavior
               :inert                      ;; change to interactive and flip logic
               :packing
               
               ;; :transition              ;; include?
               ]})

;; fix colorway

;; fix stroke + shadow

;; test packing 
;; deal with inert

;; try on component

(def generic-props
  (into #{} (:container prop-families)))

;; Just use sx for both defui components and [:div ] components
;; Validate everything the same way
;; how do you 

;; utility-classes for hiccup, mostly for layout:
;; position
;; display
;; items (align-items)
;; font-size ? really
;; font-weight ? really
;; wireframes
;; bg/fg-primary ? really

