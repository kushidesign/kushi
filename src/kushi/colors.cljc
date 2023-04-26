(ns ^:dev/always kushi.colors
    (:require [clojure.set :as set :refer [map-invert]]))

(def transparent-neutrals
  [
   :$white-transparent-01 "hsla(0, 0%, 100%, 0.01)"
   :$white-transparent-02 "hsla(0, 0%, 100%, 0.02)"
   :$white-transparent-03 "hsla(0, 0%, 100%, 0.03)"
   :$white-transparent-04 "hsla(0, 0%, 100%, 0.04)"
   :$white-transparent-05 "hsla(0, 0%, 100%, 0.05)"
   :$white-transparent-06 "hsla(0, 0%, 100%, 0.06)"
   :$white-transparent-07 "hsla(0, 0%, 100%, 0.07)"
   :$white-transparent-08 "hsla(0, 0%, 100%, 0.08)"
   :$white-transparent-09 "hsla(0, 0%, 100%, 0.09)"
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

   :$dark-gray-transparent-05 "hsla(0, 0%, 25%, 0.05)"
   :$dark-gray-transparent-10 "hsla(0, 0%, 25%, 0.1)"
   :$dark-gray-transparent-15 "hsla(0, 0%, 25%, 0.15)"
   :$dark-gray-transparent-20 "hsla(0, 0%, 25%, 0.20)"
   :$dark-gray-transparent-25 "hsla(0, 0%, 25%, 0.25)"
   :$dark-gray-transparent-30 "hsla(0, 0%, 25%, 0.30)"
   :$dark-gray-transparent-35 "hsla(0, 0%, 25%, 0.35)"
   :$dark-gray-transparent-40 "hsla(0, 0%, 25%, 0.40)"
   :$dark-gray-transparent-45 "hsla(0, 0%, 25%, 0.45)"
   :$dark-gray-transparent-50 "hsla(0, 0%, 25%, 0.50)"
   :$dark-gray-transparent-55 "hsla(0, 0%, 25%, 0.55)"
   :$dark-gray-transparent-60 "hsla(0, 0%, 25%, 0.60)"
   :$dark-gray-transparent-65 "hsla(0, 0%, 25%, 0.65)"
   :$dark-gray-transparent-70 "hsla(0, 0%, 25%, 0.70)"
   :$dark-gray-transparent-75 "hsla(0, 0%, 25%, 0.75)"
   :$dark-gray-transparent-80 "hsla(0, 0%, 25%, 0.80)"
   :$dark-gray-transparent-85 "hsla(0, 0%, 25%, 0.85)"
   :$dark-gray-transparent-90 "hsla(0, 0%, 25%, 0.90)"
   :$dark-gray-transparent-95 "hsla(0, 0%, 25%, 0.95)"
   :$dark-gray-transparent-100 "hsla(0, 0%, 25%, 1)"

   :$black-transparent-01 "hsla(0, 0%, 0%, 0.01)"
   :$black-transparent-02 "hsla(0, 0%, 0%, 0.02)"
   :$black-transparent-03 "hsla(0, 0%, 0%, 0.03)"
   :$black-transparent-04 "hsla(0, 0%, 0%, 0.04)"
   :$black-transparent-05 "hsla(0, 0%, 0%, 0.05)"
   :$black-transparent-06 "hsla(0, 0%, 0%, 0.06)"
   :$black-transparent-07 "hsla(0, 0%, 0%, 0.07)"
   :$black-transparent-08 "hsla(0, 0%, 0%, 0.08)"
   :$black-transparent-09 "hsla(0, 0%, 0%, 0.09)"
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
            [1000 100 11]]
    #_[[50 100 97]
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
   "blue"
   {:alias "accent",
    :hue   220,
    :scale [[50 100 98]
            [100 96 95]
            [200 93 88]
            [300 90 80]
            [400 85 70]

            ;; [500 100 61]
            [500 85 59]

            [600 80 49]
            [700 85 38.5]
            [800 90 30]
            [900 96 22]
            [1000 100 11]]
    #_[[50 100 98]
            [100 97 95]
            [200 93 89]
            [300 90 78]
            [400 87 68]
            [500 83 57]
            [600 80 45]
            [700 83 37]
            [800 86 29]
            [900 87 22]
            [1000 91 11]]},
   "green"
   {:alias "positive",
    :hue   148,
    :scale
    [[50 100 97]
     [100 93 92]
     [200 87 76]
     [300 81 50]
     [400 75 45]
     [500 95 35]
     [600 96 30]
     [700 99 24]
     [800 100 19]
     [900 100 14]
     [1000 100 10]]
    #_[[50 70 97]
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
   "lime"
   {:hue   83,
    :scale #_[[50  90 97]
              [100 80 93]
              [200 80 86]
              [300 74 71]
              [400 73 58]
              [500 74 47]
              [600 75 39]
              [700 80 30]
              [800 85 22]
              [900 90 16]
              [1000 100 10]]
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
            [1000 97 11]]
    #_[[50 100 97]
            [100 98 93]
            [200 93 85]
            [300 90 76]
            [400 90 66]
            [500 90 58]
            [600 91 47]
            [700 94 37]
            [800 98 29]
            [900 100 20]
            [1000 100 11]]},
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
            [1000 100 11]]
    #_[
            [50 100 98]
            [100 100 95]
            [200 90 89]
            [300 80 79]
            [400 74 66]
            [500 89 49]
            [600 63 45]
            [700 73 37]
            [800 95 29]
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

(def colors-by-alias
  (into {}
        (keep (fn [[k v]]
                (when-let [alias (:alias v)]
                  [alias k]))
              (partition 2 colors))))

(def aliases-by-color
  (map-invert colors-by-alias))
