(ns kushi.ui.avatar.core
  (:require
   [kushi.core :refer [merge-attrs sx]]
   [kushi.ui.core :refer (opts+children)]
   [kushi.ui.icon.core]
   [kushi.ui.shared.theming :refer [data-kushi- get-variants hue-style-map]]
   [kushi.ui.util :refer [maybe nameable?]]))

(defn avatar 
  {:summary "Avatars are graphical thumbnail representations of an individual
             or entity."
   :desc    "Avatars will display fallback text when no image is provided."
   :opts    '[
              {:name    size
               :pred    string?
               :default "36px"
               :desc    "Must be a valid css length unit supplied as a string
                         e.g. `\"24px\"`, `\"1rem\"`, etc."}
              {:name    font-size-ratio
               :pred    float?
               :default "36px"
               :desc    "Must be a valid css length unit supplied as a string
                         e.g. `\"24px\"`, `\"1rem\"`, etc."}
              {:name    colorway
               :pred    #{:neutral :accent :positive :negative :warning}
               :default nil
               :desc    "Colorway of the avatar. Can also be a named color from
                         Kushi's design system, e.g `:red`, `:purple`, `:gold`,
                         etc. Only applies when fallback text is used."}
              {:name    surface
               :pred    #{:soft :solid :minimal :outline}
               :default :round
               :desc    "Surface variant of the avatar. Except for the `:outline`
                         variant, only applies when fallback text is used."}
              #_{:name    stroke-align
                 :pred    #{:inside :outside}
                 :default inside
                 :desc    "Alignment of the stroke used for the avatar border"}
              {:name    shape
               ;; TODO add :squircle
               :pred    #{:sharp :round :pill :circle #_:squircle}
               :default :circle
               :desc    "Shape of the avatar."}
              ;; TODO add
              #_{:name    mask
                 :pred    string?
                 :default nil
                 :desc    "URL of a mask image to clip the avatar with."}]}
  [& args]
  (let [[opts attrs & children]
        (opts+children args)
        
        {:keys [colorway
                stroke-align
                size
                font-size-ratio]}
        opts

        ;; TODO - maybe warning here?
        size
        (or (some-> size (maybe nameable?) name)
            "36px")

        ;; TODO - maybe warning here?
        font-size-ratio
        (when children
          (or (some-> font-size-ratio
                      (maybe #(and (float? %)
                                   (<= 0 % 1))))
              0.4))

        {:keys             [shape surface]
         semantic-colorway :colorway}
        (get-variants opts {:shape :circle})

        hue-style-map                 
        (when-not semantic-colorway 
          (some-> colorway
                  hue-style-map))]
    (into [(if (:src attrs) :img :span)
           (merge-attrs
            {:style {"--width"     (name size)
                     "--font-size" (str "calc(" size " * " font-size-ratio ")")}}
            (sx ".kushi-avatar"
                :cursor--pointer
                :d--inline-flex
                :jc--c
                :ai--c
                :va--m
                :w--$width
                :fs--$font-size
                [:aspect-ratio "1 / 1"]
                :overflow--hidden)
            {:data-kushi-surface surface
             :data-kushi-shape   shape}
            (some-> stroke-align 
                    (maybe #{:outside "outside"})
                    (data-kushi- :stroke-align))
            (some-> (or semantic-colorway
                        (when hue-style-map ""))
                    (data-kushi- :colorway))
            hue-style-map
            (some-> surface (data-kushi- :surface))
            attrs)]
          (when-not (:src attrs)
            children))))
