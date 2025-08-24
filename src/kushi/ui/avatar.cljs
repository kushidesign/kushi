(ns kushi.ui.avatar
  (:require
   [fireworks.core :refer [? !? ?> !?>]]
   [kushi.core :refer [merge-attrs sx]]
   [kushi.ui.core :refer (defui)]
   [kushi.ui.icon]
   [kushi.ui.util :refer [maybe nameable?]]))


;; TODO - figure this out
(def avatar-sizing 
  {
   "xxxsmall" "24px"
   "xxsmall"  "36px"
   "xsmall"   "48px"
   "small"    "60px"
   "medium"   "72px"
   "large"    "72px"
   "xlarge"   "96px"
   "xxlarge"  "96px"
   "xxxlarge" "96px"
   })


(defui avatar 
  {:summary "Avatars are graphical thumbnail representations of an individual or entity."
   :desc    "Avatars will display fallback text when no image is provided."
   :props/shared [[:sizing {:default "36px"}]
                  [:surface {:default :soft}]
                  :colorway
                  :contour
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
  (let [{:keys [sizing font-size-ratio]}
        &props

        sizing
        (or (!? (some-> sizing (maybe nameable?) name avatar-sizing))
            "36px")

        font-size-ratio
        (when &children
          (or (some-> font-size-ratio
                      (maybe #(and (float? %)
                                   (<= 0 % 1))))
              0.4))]
    (into [(if (:src &attrs) :img :span)
           (merge-attrs
            {:style {"--width"     (name sizing)
                     "--font-size" (str "calc(" sizing " * " font-size-ratio ")")}}
            (sx "[data-ks-ui=\"avatar\"]"
                :.relative
                :d--inline-flex
                :jc--c
                :ai--c
                :va--m
                :w--$width
                :fs--$font-size
                [:aspect-ratio "1 / 1"]
                :overflow--hidden)
            &attrs)]
          (when-not (:src &attrs) &children))))
