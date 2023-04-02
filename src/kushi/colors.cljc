(ns ^:dev/always kushi.colors
    (:require [clojure.set :as set :refer [map-invert]]))

(def transparent-neutrals
  [
   :$white-transparent-05 "hsla(0, 0%, 100%, 0.05)"
   :$white-transparent-10 "hsla(0, 0%, 100%, 0.1)"
   :$white-transparent-15 "hsla(0, 0%, 100%, 0.15)"
   :$white-transparent-20 "hsla(0, 0%, 100%, 0.20)"
   :$white-transparent-25 "hsla(0, 0%, 100%, 0.25)"
   :$white-transparent-30 "hsla(0, 0%, 100%, 0.30)"
   :$white-transparent-35 "hsla(0, 0%, 100%, 0.35)"
   :$white-transparent-40 "hsla(0, 0%, 100%, 0.40)"
   :$white-transparent-45 "hsla(0, 0%, 100%, 0.45)"
   :$white-transparent-50 "hsla(0, 0%, 100%, 0.50)"
   :$white-transparent-55 "hsla(0, 0%, 100%, 0.55)"
   :$white-transparent-60 "hsla(0, 0%, 100%, 0.60)"
   :$white-transparent-65 "hsla(0, 0%, 100%, 0.65)"
   :$white-transparent-70 "hsla(0, 0%, 100%, 0.70)"
   :$white-transparent-75 "hsla(0, 0%, 100%, 0.75)"
   :$white-transparent-80 "hsla(0, 0%, 100%, 0.80)"
   :$white-transparent-85 "hsla(0, 0%, 100%, 0.85)"
   :$white-transparent-90 "hsla(0, 0%, 100%, 0.90)"
   :$white-transparent-95 "hsla(0, 0%, 100%, 0.95)"
   :$white-transparent-100 "hsla(0, 0%, 100%, 1)"

   :$light-gray-transparent-05 "hsla(0, 0%, 80%, 0.05)"
   :$light-gray-transparent-10 "hsla(0, 0%, 80%, 0.1)"
   :$light-gray-transparent-15 "hsla(0, 0%, 80%, 0.15)"
   :$light-gray-transparent-20 "hsla(0, 0%, 80%, 0.20)"
   :$light-gray-transparent-25 "hsla(0, 0%, 80%, 0.25)"
   :$light-gray-transparent-30 "hsla(0, 0%, 80%, 0.30)"
   :$light-gray-transparent-35 "hsla(0, 0%, 80%, 0.35)"
   :$light-gray-transparent-40 "hsla(0, 0%, 80%, 0.40)"
   :$light-gray-transparent-45 "hsla(0, 0%, 80%, 0.45)"
   :$light-gray-transparent-50 "hsla(0, 0%, 80%, 0.50)"
   :$light-gray-transparent-55 "hsla(0, 0%, 80%, 0.55)"
   :$light-gray-transparent-60 "hsla(0, 0%, 80%, 0.60)"
   :$light-gray-transparent-65 "hsla(0, 0%, 80%, 0.65)"
   :$light-gray-transparent-70 "hsla(0, 0%, 80%, 0.70)"
   :$light-gray-transparent-75 "hsla(0, 0%, 80%, 0.75)"
   :$light-gray-transparent-80 "hsla(0, 0%, 80%, 0.80)"
   :$light-gray-transparent-85 "hsla(0, 0%, 80%, 0.85)"
   :$light-gray-transparent-90 "hsla(0, 0%, 80%, 0.90)"
   :$light-gray-transparent-95 "hsla(0, 0%, 80%, 0.95)"
   :$light-gray-transparent-100 "hsla(0, 0%, 80%, 1)"

   :$gray-transparent-05 "hsla(0, 0%, 50%, 0.05)"
   :$gray-transparent-10 "hsla(0, 0%, 50%, 0.1)"
   :$gray-transparent-15 "hsla(0, 0%, 50%, 0.15)"
   :$gray-transparent-20 "hsla(0, 0%, 50%, 0.20)"
   :$gray-transparent-25 "hsla(0, 0%, 50%, 0.25)"
   :$gray-transparent-30 "hsla(0, 0%, 50%, 0.30)"
   :$gray-transparent-35 "hsla(0, 0%, 50%, 0.35)"
   :$gray-transparent-40 "hsla(0, 0%, 50%, 0.40)"
   :$gray-transparent-45 "hsla(0, 0%, 50%, 0.45)"
   :$gray-transparent-50 "hsla(0, 0%, 50%, 0.50)"
   :$gray-transparent-55 "hsla(0, 0%, 50%, 0.55)"
   :$gray-transparent-60 "hsla(0, 0%, 50%, 0.60)"
   :$gray-transparent-65 "hsla(0, 0%, 50%, 0.65)"
   :$gray-transparent-70 "hsla(0, 0%, 50%, 0.70)"
   :$gray-transparent-75 "hsla(0, 0%, 50%, 0.75)"
   :$gray-transparent-80 "hsla(0, 0%, 50%, 0.80)"
   :$gray-transparent-85 "hsla(0, 0%, 50%, 0.85)"
   :$gray-transparent-90 "hsla(0, 0%, 50%, 0.90)"
   :$gray-transparent-95 "hsla(0, 0%, 50%, 0.95)"
   :$gray-transparent-100 "hsla(0, 0%, 50%, 1)"

   :$dark-gray-transparent-05 "hsla(0, 0%, 100%, 0.05)"
   :$dark-gray-transparent-10 "hsla(0, 0%, 100%, 0.1)"
   :$dark-gray-transparent-15 "hsla(0, 0%, 100%, 0.15)"
   :$dark-gray-transparent-20 "hsla(0, 0%, 100%, 0.20)"
   :$dark-gray-transparent-25 "hsla(0, 0%, 100%, 0.25)"
   :$dark-gray-transparent-30 "hsla(0, 0%, 100%, 0.30)"
   :$dark-gray-transparent-35 "hsla(0, 0%, 100%, 0.35)"
   :$dark-gray-transparent-40 "hsla(0, 0%, 100%, 0.40)"
   :$dark-gray-transparent-45 "hsla(0, 0%, 100%, 0.45)"
   :$dark-gray-transparent-50 "hsla(0, 0%, 100%, 0.50)"
   :$dark-gray-transparent-55 "hsla(0, 0%, 100%, 0.55)"
   :$dark-gray-transparent-60 "hsla(0, 0%, 100%, 0.60)"
   :$dark-gray-transparent-65 "hsla(0, 0%, 100%, 0.65)"
   :$dark-gray-transparent-70 "hsla(0, 0%, 100%, 0.70)"
   :$dark-gray-transparent-75 "hsla(0, 0%, 100%, 0.75)"
   :$dark-gray-transparent-80 "hsla(0, 0%, 100%, 0.80)"
   :$dark-gray-transparent-85 "hsla(0, 0%, 100%, 0.85)"
   :$dark-gray-transparent-90 "hsla(0, 0%, 100%, 0.90)"
   :$dark-gray-transparent-95 "hsla(0, 0%, 100%, 0.95)"
   :$dark-gray-transparent-100 "hsla(0, 0%, 100%, 1)"

   :$black-transparent-05 "hsla(0, 0%, 0%, 0.05)"
   :$black-transparent-10 "hsla(0, 0%, 0%, 0.1)"
   :$black-transparent-15 "hsla(0, 0%, 0%, 0.15)"
   :$black-transparent-20 "hsla(0, 0%, 0%, 0.20)"
   :$black-transparent-25 "hsla(0, 0%, 0%, 0.25)"
   :$black-transparent-30 "hsla(0, 0%, 0%, 0.30)"
   :$black-transparent-35 "hsla(0, 0%, 0%, 0.35)"
   :$black-transparent-40 "hsla(0, 0%, 0%, 0.40)"
   :$black-transparent-45 "hsla(0, 0%, 0%, 0.45)"
   :$black-transparent-50 "hsla(0, 0%, 0%, 0.50)"
   :$black-transparent-55 "hsla(0, 0%, 0%, 0.55)"
   :$black-transparent-60 "hsla(0, 0%, 0%, 0.60)"
   :$black-transparent-65 "hsla(0, 0%, 0%, 0.65)"
   :$black-transparent-70 "hsla(0, 0%, 0%, 0.70)"
   :$black-transparent-75 "hsla(0, 0%, 0%, 0.75)"
   :$black-transparent-80 "hsla(0, 0%, 0%, 0.80)"
   :$black-transparent-85 "hsla(0, 0%, 0%, 0.85)"
   :$black-transparent-90 "hsla(0, 0%, 0%, 0.90)"
   :$black-transparent-95 "hsla(0, 0%, 0%, 0.95)"
   :$black-transparent-100 "hsla(0, 0%, 0%, 1)"
   ])

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
   "blue"
   {:alias "accent",
    :hue   212,
    :scale [[50 100 98]
            [100 97 95]
            [200 93 89]
            [300 90 78]
            [400 87 68]
            [500 87 57]
            [600 87 45]
            [700 87 37]
            [800 87 29]
            [900 87 22]
            [1000 90 11]]},
   "green"
   {:alias "positive",
    :hue   144,
    :scale [[50 70 97]
            [100 70 93]
            [200 70 86]
            [300 68 75]
            [400 60 61]
            [500 55 48]
            [600 45 40]
            [700 55 30]
            [800 75 22]
            [900 90 16]
            [1000 100 10]]},
   "red"
   {:alias "negative",
    :hue   358,
    :scale [[50 100 98]
            [100 100 95]
            [200 90 89]
            [300 80 80]
            [400 74 66]
            [500 57 55]
            [600 63 45]
            [700 73 37]
            [800 95 29]
            [900 100 20]
            [1000 100 11]]},
   "yellow"
   {:alias "warning",
    :hue   49,
    :scale [[50 90 95]
            [100 90 90]
            [200 90 83]
            [300 90 71]
            [400 90 52]
            [500 90 46]
            [600 90 40]
            [700 90 33]
            [800 90 25]
            [900 90 17]
            [1000 90 11]]},
   "purple"
   {:alias nil,
    :hue   267,
    :scale [[50 100 97]
            [100 90 93]
            [200 85 87]
            [300 80 78]
            [400 78 68]
            [500 78 58]
            [600 77 50]
            [700 80 42]
            [800 85 33]
            [900 90 24]
            [1000 100 11]]},
   "magenta"
   {:alias nil,
    :hue   324,
    :scale [[50 100 97]
            [100 90 93]
            [200 85 87]
            [300 80 80]
            [400 78 71]
            [500 78 62]
            [600 77 48]
            [700 80 41]
            [800 85 33]
            [900 90 24]
            [1000 100 11]]},
   "orange"
   {:alias nil,
    :hue   32,
    :scale [[50 100 97]
            [100 98 93]
            [200 93 87]
            [300 90 76]
            [400 90 66]
            [500 90 55]
            [600 90 45]
            [700 93 37]
            [800 98 29]
            [900 100 20]
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

(def colors-by-alias
  (into {}
        (keep (fn [[k v]]
                (when-let [alias (:alias v)]
                  [alias k]))
              (partition 2 colors))))

(def aliases-by-color
  (map-invert colors-by-alias))
