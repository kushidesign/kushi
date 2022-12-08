(ns ^:dev/always kushi.colors)

(def colors
  [
   "gray"
   {:alias nil,
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
