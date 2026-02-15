(ns kushi.ui.avatar
  (:require
   [fireworks.core :refer [? !? ?> !?>]]
   [kushi.core :refer [merge-attrs sx]]
   [kushi.ui.core :refer (defui)]
   [kushi.ui.icon]
   [kushi.ui.decoration :as decoration]
   [kushi.ui.util :refer [maybe nameable?]]))


;; TODO - figure this out
(def avatar-size 
  {"3xs" "18px"
   "2xs"  "26px"
   "xs"   "36px"
   "sm"    "48px"
   "md"   "60px"
   "lg"    "78px"
   "xl"   "96px"
   "2xl"  "120px"
   "3xl" "145px"})


(defui avatar 
  {:summary "Avatars are graphical thumbnail representations of an individual or entity."
   :desc    "Avatars will display fallback text when no image is provided."
   :props/shared [[:size {:default "36px"}]
                  [:surface {:default :soft}]
                  [:colorway {:default :neutral}]
                  [:shape {:default :rounded}]
                  :stroke
                  :stroke-align]
   :props   {:font-size-ratio {:schema  :float
                               :default 0.4
                               :desc    "Must be a valid css length unit supplied as a string e.g. `\"24px\"`, `\"1rem\"`, etc."}
             
              ;; TODO add
             #_:mask            #_{:name    mask
                               :schema  string?
                               :default nil
                               :desc    "URL of a mask image to clip the avatar with."} }}
  [& args]
  (let [{:keys [size font-size-ratio stroke-width]}
        &props

        size
        (or (!? (some-> size (maybe nameable?) name avatar-size))
            "36px")

        font-size-ratio
        (when &children
          (or (some-> font-size-ratio
                      (maybe #(and (float? %)
                                   (<= 0 % 1))))
              0.4))]
    (into [(if (:src &attrs) :img :span)
           (merge-attrs
            {:style            {"--width"     (name size)
                                "--font-size" (str "calc(" size " * " font-size-ratio ")")}
             :data-ks-position :relative}
            (sx ".ks-avatar"
                [:--stroke-width :$avatar-stroke-width]
                :cursor--default
                :d--inline-flex
                :jc--c
                :ai--c
                :va--m
                :w--$width
                :fs--$font-size
                [:aspect-ratio "1 / 1"]
                :overflow--hidden)

            (some-> stroke-width
                    (decoration/stroke-width-cssvar "avatar"))

            &attrs)]
          (when-not (:src &attrs) &children))))
