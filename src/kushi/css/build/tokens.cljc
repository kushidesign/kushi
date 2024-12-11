;; building elevation scales

;; (defn- box-shadows->str [coll level suffix]
;;   (->> coll
;;        (mapv (fn [settings]
;;                (string/join
;;                 " "
;;                 (conj
;;                  (mapv #(str % "px") settings)
;;                  (str "var(--elevation-shadow-layer-"
;;                       level
;;                       "-color"
;;                       suffix
;;                       ")")))))
;;        (string/join ", ")))

;; (defn- elevation-scale [elevations]
;;   (reduce
;;    (fn [acc [level box-shadows]]
;;      (-> acc
;;          (conj (keyword (str "--elevated-" level "")))
;;          (conj (box-shadows->str box-shadows level ""))
;;          (conj (keyword (str "--elevated-" level "-inverse")))
;;          (conj (box-shadows->str box-shadows level "-inverse"))))
;;    []
;;    elevations))
;; (? :pp  {:non-coll-length-limit 80}
;;    (elevation-scale (array-map
;;                      1
;;                      [[0 3 3 -2]
;;                       [0 3 4 0]
;;                       [0 1 8 0]]
;;                      2
;;                      [[0 3 3 -2]
;;                       [0 3 4 0]
;;                       [0 1 8 0]]
;;                      3
;;                      [[0 3 5 -1]
;;                       [0 6 10 0]
;;                       [0 1 18 0]]
;;                      4
;;                      [[0 5 5 -3]
;;                       [0 8 18 1]
;;                       [0 6 20 2]]
;;                      5
;;                      [[0 7 14 -2]
;;                       [0 6 26 0]
;;                       [0 8 27 0]])))




(ns kushi.css.build.tokens)

(def design-tokens
  [
   ;; All colors - this large list of color tokens is temporary and will be 
   ;; replaced with a dynamically generated array-map based on okclh
  :--gray-hue "0"
  :--gray-50 "hsl(var(--gray-hue), 0%, 98%)"
  :--gray-100 "hsl(var(--gray-hue), 0%, 95%)"
  :--gray-150 "hsl(var(--gray-hue), 0%, 93%)"
  :--gray-200 "hsl(var(--gray-hue), 0%, 91%)"
  :--gray-250 "hsl(var(--gray-hue), 0%, 88%)"
  :--gray-300 "hsl(var(--gray-hue), 0%, 85%)"
  :--gray-350 "hsl(var(--gray-hue), 0%, 81%)"
  :--gray-400 "hsl(var(--gray-hue), 0%, 77%)"
  :--gray-450 "hsl(var(--gray-hue), 0%, 72.5%)"
  :--gray-500 "hsl(var(--gray-hue), 0%, 68%)"
  :--gray-550 "hsl(var(--gray-hue), 0%, 62.5%)"
  :--gray-600 "hsl(var(--gray-hue), 0%, 57%)"
  :--gray-650 "hsl(var(--gray-hue), 0%, 50.5%)"
  :--gray-700 "hsl(var(--gray-hue), 0%, 44%)"
  :--gray-750 "hsl(var(--gray-hue), 0%, 36%)"
  :--gray-800 "hsl(var(--gray-hue), 0%, 28%)"
  :--gray-850 "hsl(var(--gray-hue), 0%, 23%)"
  :--gray-900 "hsl(var(--gray-hue), 0%, 18%)"
  :--gray-950 "hsl(var(--gray-hue), 0%, 13%)"
  :--gray-1000 "hsl(var(--gray-hue), 0%, 8%)"
  :--purple-hue "273"
  :--purple-50 "hsl(var(--purple-hue), 100%, 97%)"
  :--purple-100 "hsl(var(--purple-hue), 90%, 93%)"
  :--purple-150 "hsl(var(--purple-hue), 90%, 89%)"
  :--purple-200 "hsl(var(--purple-hue), 90%, 85%)"
  :--purple-250 "hsl(var(--purple-hue), 90%, 81%)"
  :--purple-300 "hsl(var(--purple-hue), 90%, 77%)"
  :--purple-350 "hsl(var(--purple-hue), 90.5%, 73%)"
  :--purple-400 "hsl(var(--purple-hue), 91%, 69%)"
  :--purple-450 "hsl(var(--purple-hue), 92%, 63.5%)"
  :--purple-500 "hsl(var(--purple-hue), 93%, 58%)"
  :--purple-550 "hsl(var(--purple-hue), 92%, 51.5%)"
  :--purple-600 "hsl(var(--purple-hue), 91%, 45%)"
  :--purple-650 "hsl(var(--purple-hue), 90.5%, 41.5%)"
  :--purple-700 "hsl(var(--purple-hue), 90%, 38%)"
  :--purple-750 "hsl(var(--purple-hue), 90%, 34%)"
  :--purple-800 "hsl(var(--purple-hue), 90%, 30%)"
  :--purple-850 "hsl(var(--purple-hue), 90%, 26%)"
  :--purple-900 "hsl(var(--purple-hue), 90%, 22%)"
  :--purple-950 "hsl(var(--purple-hue), 95%, 16.5%)"
  :--purple-1000 "hsl(var(--purple-hue), 100%, 11%)"
  :--blue-hue "220"
  :--blue-50 "hsl(var(--blue-hue), 100%, 98%)"
  :--blue-100 "hsl(var(--blue-hue), 96%, 95%)"
  :--blue-150 "hsl(var(--blue-hue), 94.5%, 91.5%)"
  :--blue-200 "hsl(var(--blue-hue), 93%, 88%)"
  :--blue-250 "hsl(var(--blue-hue), 91.5%, 84%)"
  :--blue-300 "hsl(var(--blue-hue), 90%, 80%)"
  :--blue-350 "hsl(var(--blue-hue), 87.5%, 75%)"
  :--blue-400 "hsl(var(--blue-hue), 85%, 70%)"
  :--blue-450 "hsl(var(--blue-hue), 85%, 64.5%)"
  :--blue-500 "hsl(var(--blue-hue), 85%, 59%)"
  :--blue-550 "hsl(var(--blue-hue), 82.5%, 54%)"
  :--blue-600 "hsl(var(--blue-hue), 80%, 49%)"
  :--blue-650 "hsl(var(--blue-hue), 82.5%, 43.75%)"
  :--blue-700 "hsl(var(--blue-hue), 85%, 38.5%)"
  :--blue-750 "hsl(var(--blue-hue), 87.5%, 34.25%)"
  :--blue-800 "hsl(var(--blue-hue), 90%, 30%)"
  :--blue-850 "hsl(var(--blue-hue), 93%, 26%)"
  :--blue-900 "hsl(var(--blue-hue), 96%, 22%)"
  :--blue-950 "hsl(var(--blue-hue), 98%, 16.5%)"
  :--blue-1000 "hsl(var(--blue-hue), 100%, 11%)"
  :--green-hue "148"
  :--green-50 "hsl(var(--green-hue), 95%, 97%)"
  :--green-100 "hsl(var(--green-hue), 86%, 93%)"
  :--green-150 "hsl(var(--green-hue), 80.5%, 87%)"
  :--green-200 "hsl(var(--green-hue), 75%, 81%)"
  :--green-250 "hsl(var(--green-hue), 72.5%, 73%)"
  :--green-300 "hsl(var(--green-hue), 70%, 65%)"
  :--green-350 "hsl(var(--green-hue), 72%, 56%)"
  :--green-400 "hsl(var(--green-hue), 74%, 47%)"
  :--green-450 "hsl(var(--green-hue), 84.5%, 41%)"
  :--green-500 "hsl(var(--green-hue), 95%, 35%)"
  :--green-550 "hsl(var(--green-hue), 95.5%, 32.5%)"
  :--green-600 "hsl(var(--green-hue), 96%, 30%)"
  :--green-650 "hsl(var(--green-hue), 97.5%, 27%)"
  :--green-700 "hsl(var(--green-hue), 99%, 24%)"
  :--green-750 "hsl(var(--green-hue), 99.5%, 21.5%)"
  :--green-800 "hsl(var(--green-hue), 100%, 19%)"
  :--green-850 "hsl(var(--green-hue), 100%, 16.5%)"
  :--green-900 "hsl(var(--green-hue), 100%, 14%)"
  :--green-950 "hsl(var(--green-hue), 100%, 12%)"
  :--green-1000 "hsl(var(--green-hue), 100%, 10%)"
  :--lime-hue "83"
  :--lime-50 "hsl(var(--lime-hue), 100%, 96%)"
  :--lime-100 "hsl(var(--lime-hue), 100%, 89%)"
  :--lime-150 "hsl(var(--lime-hue), 100%, 79%)"
  :--lime-200 "hsl(var(--lime-hue), 100%, 69%)"
  :--lime-250 "hsl(var(--lime-hue), 100%, 59%)"
  :--lime-300 "hsl(var(--lime-hue), 100%, 49%)"
  :--lime-350 "hsl(var(--lime-hue), 99%, 47%)"
  :--lime-400 "hsl(var(--lime-hue), 98%, 45%)"
  :--lime-450 "hsl(var(--lime-hue), 97%, 42.5%)"
  :--lime-500 "hsl(var(--lime-hue), 96%, 40%)"
  :--lime-550 "hsl(var(--lime-hue), 95%, 37.5%)"
  :--lime-600 "hsl(var(--lime-hue), 94%, 35%)"
  :--lime-650 "hsl(var(--lime-hue), 95%, 32.5%)"
  :--lime-700 "hsl(var(--lime-hue), 96%, 30%)"
  :--lime-750 "hsl(var(--lime-hue), 96%, 26%)"
  :--lime-800 "hsl(var(--lime-hue), 96%, 22%)"
  :--lime-850 "hsl(var(--lime-hue), 96%, 19%)"
  :--lime-900 "hsl(var(--lime-hue), 96%, 16%)"
  :--lime-950 "hsl(var(--lime-hue), 98%, 13%)"
  :--lime-1000 "hsl(var(--lime-hue), 100%, 10%)"
  :--yellow-hue "59"
  :--yellow-50 "hsl(var(--yellow-hue), 96%, 95%)"
  :--yellow-100 "hsl(var(--yellow-hue), 94%, 90%)"
  :--yellow-150 "hsl(var(--yellow-hue), 93.5%, 82.5%)"
  :--yellow-200 "hsl(var(--yellow-hue), 93%, 75%)"
  :--yellow-250 "hsl(var(--yellow-hue), 92.5%, 62%)"
  :--yellow-300 "hsl(var(--yellow-hue), 92%, 49%)"
  :--yellow-350 "hsl(var(--yellow-hue), 91.5%, 47.5%)"
  :--yellow-400 "hsl(var(--yellow-hue), 91%, 46%)"
  :--yellow-450 "hsl(var(--yellow-hue), 91%, 44%)"
  :--yellow-500 "hsl(var(--yellow-hue), 91%, 42%)"
  :--yellow-550 "hsl(var(--yellow-hue), 91%, 39%)"
  :--yellow-600 "hsl(var(--yellow-hue), 91%, 36%)"
  :--yellow-650 "hsl(var(--yellow-hue), 90%, 33%)"
  :--yellow-700 "hsl(var(--yellow-hue), 89%, 30%)"
  :--yellow-750 "hsl(var(--yellow-hue), 89%, 26%)"
  :--yellow-800 "hsl(var(--yellow-hue), 89%, 22%)"
  :--yellow-850 "hsl(var(--yellow-hue), 89.5%, 19%)"
  :--yellow-900 "hsl(var(--yellow-hue), 90%, 16%)"
  :--yellow-950 "hsl(var(--yellow-hue), 90.5%, 13.5%)"
  :--yellow-1000 "hsl(var(--yellow-hue), 91%, 11%)"
  :--gold-hue "44"
  :--gold-50 "hsl(var(--gold-hue), 98%, 96%)"
  :--gold-100 "hsl(var(--gold-hue), 98%, 92%)"
  :--gold-150 "hsl(var(--gold-hue), 98%, 86.5%)"
  :--gold-200 "hsl(var(--gold-hue), 98%, 81%)"
  :--gold-250 "hsl(var(--gold-hue), 98%, 74.5%)"
  :--gold-300 "hsl(var(--gold-hue), 98%, 68%)"
  :--gold-350 "hsl(var(--gold-hue), 97%, 60%)"
  :--gold-400 "hsl(var(--gold-hue), 96%, 52%)"
  :--gold-450 "hsl(var(--gold-hue), 96%, 48.5%)"
  :--gold-500 "hsl(var(--gold-hue), 96%, 45%)"
  :--gold-550 "hsl(var(--gold-hue), 97%, 41.5%)"
  :--gold-600 "hsl(var(--gold-hue), 98%, 38%)"
  :--gold-650 "hsl(var(--gold-hue), 98%, 35%)"
  :--gold-700 "hsl(var(--gold-hue), 98%, 32%)"
  :--gold-750 "hsl(var(--gold-hue), 98%, 28%)"
  :--gold-800 "hsl(var(--gold-hue), 98%, 24%)"
  :--gold-850 "hsl(var(--gold-hue), 98%, 20.5%)"
  :--gold-900 "hsl(var(--gold-hue), 98%, 17%)"
  :--gold-950 "hsl(var(--gold-hue), 98%, 14%)"
  :--gold-1000 "hsl(var(--gold-hue), 98%, 11%)"
  :--orange-hue "31"
  :--orange-50 "hsl(var(--orange-hue), 98%, 97%)"
  :--orange-100 "hsl(var(--orange-hue), 98%, 93%)"
  :--orange-150 "hsl(var(--orange-hue), 97.5%, 88.5%)"
  :--orange-200 "hsl(var(--orange-hue), 97%, 84%)"
  :--orange-250 "hsl(var(--orange-hue), 97%, 78.5%)"
  :--orange-300 "hsl(var(--orange-hue), 97%, 73%)"
  :--orange-350 "hsl(var(--orange-hue), 97%, 66.5%)"
  :--orange-400 "hsl(var(--orange-hue), 97%, 60%)"
  :--orange-450 "hsl(var(--orange-hue), 97%, 54%)"
  :--orange-500 "hsl(var(--orange-hue), 97%, 48%)"
  :--orange-550 "hsl(var(--orange-hue), 97%, 44.5%)"
  :--orange-600 "hsl(var(--orange-hue), 97%, 41%)"
  :--orange-650 "hsl(var(--orange-hue), 97%, 38%)"
  :--orange-700 "hsl(var(--orange-hue), 97%, 35%)"
  :--orange-750 "hsl(var(--orange-hue), 97%, 31%)"
  :--orange-800 "hsl(var(--orange-hue), 97%, 27%)"
  :--orange-850 "hsl(var(--orange-hue), 97%, 22.5%)"
  :--orange-900 "hsl(var(--orange-hue), 97%, 18%)"
  :--orange-950 "hsl(var(--orange-hue), 97%, 14.5%)"
  :--orange-1000 "hsl(var(--orange-hue), 97%, 11%)"
  :--red-hue "358"
  :--red-50 "hsl(var(--red-hue), 100%, 98%)"
  :--red-100 "hsl(var(--red-hue), 100%, 95%)"
  :--red-150 "hsl(var(--red-hue), 98%, 91.5%)"
  :--red-200 "hsl(var(--red-hue), 96%, 88%)"
  :--red-250 "hsl(var(--red-hue), 91.5%, 83%)"
  :--red-300 "hsl(var(--red-hue), 87%, 78%)"
  :--red-350 "hsl(var(--red-hue), 82.5%, 71.5%)"
  :--red-400 "hsl(var(--red-hue), 78%, 65%)"
  :--red-450 "hsl(var(--red-hue), 77%, 58.25%)"
  :--red-500 "hsl(var(--red-hue), 76%, 51.5%)"
  :--red-550 "hsl(var(--red-hue), 73%, 48.25%)"
  :--red-600 "hsl(var(--red-hue), 70%, 45%)"
  :--red-650 "hsl(var(--red-hue), 74.5%, 40.75%)"
  :--red-700 "hsl(var(--red-hue), 79%, 36.5%)"
  :--red-750 "hsl(var(--red-hue), 86%, 32%)"
  :--red-800 "hsl(var(--red-hue), 93%, 27.5%)"
  :--red-850 "hsl(var(--red-hue), 96.5%, 23.75%)"
  :--red-900 "hsl(var(--red-hue), 100%, 20%)"
  :--red-950 "hsl(var(--red-hue), 100%, 15.5%)"
  :--red-1000 "hsl(var(--red-hue), 100%, 11%)"
  :--magenta-hue "324"
  :--magenta-50 "hsl(var(--magenta-hue), 100%, 98%)"
  :--magenta-100 "hsl(var(--magenta-hue), 100%, 95.5%)"
  :--magenta-150 "hsl(var(--magenta-hue), 97.5%, 92%)"
  :--magenta-200 "hsl(var(--magenta-hue), 95%, 88.5%)"
  :--magenta-250 "hsl(var(--magenta-hue), 93.5%, 83.75%)"
  :--magenta-300 "hsl(var(--magenta-hue), 92%, 79%)"
  :--magenta-350 "hsl(var(--magenta-hue), 90%, 73.5%)"
  :--magenta-400 "hsl(var(--magenta-hue), 88%, 68%)"
  :--magenta-450 "hsl(var(--magenta-hue), 85.5%, 59.5%)"
  :--magenta-500 "hsl(var(--magenta-hue), 83%, 51%)"
  :--magenta-550 "hsl(var(--magenta-hue), 82.5%, 48%)"
  :--magenta-600 "hsl(var(--magenta-hue), 82%, 45%)"
  :--magenta-650 "hsl(var(--magenta-hue), 83.5%, 41.5%)"
  :--magenta-700 "hsl(var(--magenta-hue), 85%, 38%)"
  :--magenta-750 "hsl(var(--magenta-hue), 86.5%, 34%)"
  :--magenta-800 "hsl(var(--magenta-hue), 88%, 30%)"
  :--magenta-850 "hsl(var(--magenta-hue), 91.5%, 25.5%)"
  :--magenta-900 "hsl(var(--magenta-hue), 95%, 21%)"
  :--magenta-950 "hsl(var(--magenta-hue), 97.5%, 16%)"
  :--magenta-1000 "hsl(var(--magenta-hue), 100%, 11%)"
  :--brown-hue "19"
  :--brown-50 "hsl(var(--brown-hue), 40%, 97%)"
  :--brown-100 "hsl(var(--brown-hue), 37%, 93%)"
  :--brown-150 "hsl(var(--brown-hue), 36%, 90%)"
  :--brown-200 "hsl(var(--brown-hue), 35%, 87%)"
  :--brown-250 "hsl(var(--brown-hue), 33.5%, 82.5%)"
  :--brown-300 "hsl(var(--brown-hue), 32%, 78%)"
  :--brown-350 "hsl(var(--brown-hue), 30.5%, 73%)"
  :--brown-400 "hsl(var(--brown-hue), 29%, 68%)"
  :--brown-450 "hsl(var(--brown-hue), 28%, 63.5%)"
  :--brown-500 "hsl(var(--brown-hue), 27%, 59%)"
  :--brown-550 "hsl(var(--brown-hue), 28%, 54.5%)"
  :--brown-600 "hsl(var(--brown-hue), 29%, 50%)"
  :--brown-650 "hsl(var(--brown-hue), 30.5%, 46%)"
  :--brown-700 "hsl(var(--brown-hue), 32%, 42%)"
  :--brown-750 "hsl(var(--brown-hue), 33.5%, 37.5%)"
  :--brown-800 "hsl(var(--brown-hue), 35%, 33%)"
  :--brown-850 "hsl(var(--brown-hue), 36%, 28.5%)"
  :--brown-900 "hsl(var(--brown-hue), 37%, 24%)"
  :--brown-950 "hsl(var(--brown-hue), 38.5%, 17.5%)"
  :--brown-1000 "hsl(var(--brown-hue), 40%, 11%)"
  :--white-transparent-01 "hsla(0, 0%, 100%, 0.01)"
  :--white-transparent-02 "hsla(0, 0%, 100%, 0.02)"
  :--white-transparent-03 "hsla(0, 0%, 100%, 0.03)"
  :--white-transparent-04 "hsla(0, 0%, 100%, 0.04)"
  :--white-transparent-05 "hsla(0, 0%, 100%, 0.05)"
  :--white-transparent-06 "hsla(0, 0%, 100%, 0.06)"
  :--white-transparent-07 "hsla(0, 0%, 100%, 0.07)"
  :--white-transparent-08 "hsla(0, 0%, 100%, 0.08)"
  :--white-transparent-09 "hsla(0, 0%, 100%, 0.09)"
  :--white-transparent-10 "hsla(0, 0%, 100%, 0.1)"
  :--white-transparent-15 "hsla(0, 0%, 100%, 0.15)"
  :--white-transparent-20 "hsla(0, 0%, 100%, 0.20)"
  :--white-transparent-25 "hsla(0, 0%, 100%, 0.25)"
  :--white-transparent-30 "hsla(0, 0%, 100%, 0.30)"
  :--white-transparent-35 "hsla(0, 0%, 100%, 0.35)"
  :--white-transparent-40 "hsla(0, 0%, 100%, 0.40)"
  :--white-transparent-45 "hsla(0, 0%, 100%, 0.45)"
  :--white-transparent-50 "hsla(0, 0%, 100%, 0.50)"
  :--white-transparent-55 "hsla(0, 0%, 100%, 0.55)"
  :--white-transparent-60 "hsla(0, 0%, 100%, 0.60)"
  :--white-transparent-65 "hsla(0, 0%, 100%, 0.65)"
  :--white-transparent-70 "hsla(0, 0%, 100%, 0.70)"
  :--white-transparent-75 "hsla(0, 0%, 100%, 0.75)"
  :--white-transparent-80 "hsla(0, 0%, 100%, 0.80)"
  :--white-transparent-85 "hsla(0, 0%, 100%, 0.85)"
  :--white-transparent-90 "hsla(0, 0%, 100%, 0.90)"
  :--white-transparent-95 "hsla(0, 0%, 100%, 0.95)"
  :--white-transparent-100 "hsla(0, 0%, 100%, 1)"
  :--light-gray-transparent-05 "hsla(0, 0%, 80%, 0.05)"
  :--light-gray-transparent-10 "hsla(0, 0%, 80%, 0.1)"
  :--light-gray-transparent-15 "hsla(0, 0%, 80%, 0.15)"
  :--light-gray-transparent-20 "hsla(0, 0%, 80%, 0.20)"
  :--light-gray-transparent-25 "hsla(0, 0%, 80%, 0.25)"
  :--light-gray-transparent-30 "hsla(0, 0%, 80%, 0.30)"
  :--light-gray-transparent-35 "hsla(0, 0%, 80%, 0.35)"
  :--light-gray-transparent-40 "hsla(0, 0%, 80%, 0.40)"
  :--light-gray-transparent-45 "hsla(0, 0%, 80%, 0.45)"
  :--light-gray-transparent-50 "hsla(0, 0%, 80%, 0.50)"
  :--light-gray-transparent-55 "hsla(0, 0%, 80%, 0.55)"
  :--light-gray-transparent-60 "hsla(0, 0%, 80%, 0.60)"
  :--light-gray-transparent-65 "hsla(0, 0%, 80%, 0.65)"
  :--light-gray-transparent-70 "hsla(0, 0%, 80%, 0.70)"
  :--light-gray-transparent-75 "hsla(0, 0%, 80%, 0.75)"
  :--light-gray-transparent-80 "hsla(0, 0%, 80%, 0.80)"
  :--light-gray-transparent-85 "hsla(0, 0%, 80%, 0.85)"
  :--light-gray-transparent-90 "hsla(0, 0%, 80%, 0.90)"
  :--light-gray-transparent-95 "hsla(0, 0%, 80%, 0.95)"
  :--light-gray-transparent-100 "hsla(0, 0%, 80%, 1)"
  :--gray-transparent-05 "hsla(0, 0%, 50%, 0.05)"
  :--gray-transparent-10 "hsla(0, 0%, 50%, 0.1)"
  :--gray-transparent-15 "hsla(0, 0%, 50%, 0.15)"
  :--gray-transparent-20 "hsla(0, 0%, 50%, 0.20)"
  :--gray-transparent-25 "hsla(0, 0%, 50%, 0.25)"
  :--gray-transparent-30 "hsla(0, 0%, 50%, 0.30)"
  :--gray-transparent-35 "hsla(0, 0%, 50%, 0.35)"
  :--gray-transparent-40 "hsla(0, 0%, 50%, 0.40)"
  :--gray-transparent-45 "hsla(0, 0%, 50%, 0.45)"
  :--gray-transparent-50 "hsla(0, 0%, 50%, 0.50)"
  :--gray-transparent-55 "hsla(0, 0%, 50%, 0.55)"
  :--gray-transparent-60 "hsla(0, 0%, 50%, 0.60)"
  :--gray-transparent-65 "hsla(0, 0%, 50%, 0.65)"
  :--gray-transparent-70 "hsla(0, 0%, 50%, 0.70)"
  :--gray-transparent-75 "hsla(0, 0%, 50%, 0.75)"
  :--gray-transparent-80 "hsla(0, 0%, 50%, 0.80)"
  :--gray-transparent-85 "hsla(0, 0%, 50%, 0.85)"
  :--gray-transparent-90 "hsla(0, 0%, 50%, 0.90)"
  :--gray-transparent-95 "hsla(0, 0%, 50%, 0.95)"
  :--gray-transparent-100 "hsla(0, 0%, 50%, 1)"
  :--dark-gray-transparent-05 "hsla(0, 0%, 25%, 0.05)"
  :--dark-gray-transparent-10 "hsla(0, 0%, 25%, 0.1)"
  :--dark-gray-transparent-15 "hsla(0, 0%, 25%, 0.15)"
  :--dark-gray-transparent-20 "hsla(0, 0%, 25%, 0.20)"
  :--dark-gray-transparent-25 "hsla(0, 0%, 25%, 0.25)"
  :--dark-gray-transparent-30 "hsla(0, 0%, 25%, 0.30)"
  :--dark-gray-transparent-35 "hsla(0, 0%, 25%, 0.35)"
  :--dark-gray-transparent-40 "hsla(0, 0%, 25%, 0.40)"
  :--dark-gray-transparent-45 "hsla(0, 0%, 25%, 0.45)"
  :--dark-gray-transparent-50 "hsla(0, 0%, 25%, 0.50)"
  :--dark-gray-transparent-55 "hsla(0, 0%, 25%, 0.55)"
  :--dark-gray-transparent-60 "hsla(0, 0%, 25%, 0.60)"
  :--dark-gray-transparent-65 "hsla(0, 0%, 25%, 0.65)"
  :--dark-gray-transparent-70 "hsla(0, 0%, 25%, 0.70)"
  :--dark-gray-transparent-75 "hsla(0, 0%, 25%, 0.75)"
  :--dark-gray-transparent-80 "hsla(0, 0%, 25%, 0.80)"
  :--dark-gray-transparent-85 "hsla(0, 0%, 25%, 0.85)"
  :--dark-gray-transparent-90 "hsla(0, 0%, 25%, 0.90)"
  :--dark-gray-transparent-95 "hsla(0, 0%, 25%, 0.95)"
  :--dark-gray-transparent-100 "hsla(0, 0%, 25%, 1)"
  :--black-transparent-01 "hsla(0, 0%, 0%, 0.01)"
  :--black-transparent-02 "hsla(0, 0%, 0%, 0.02)"
  :--black-transparent-03 "hsla(0, 0%, 0%, 0.03)"
  :--black-transparent-04 "hsla(0, 0%, 0%, 0.04)"
  :--black-transparent-05 "hsla(0, 0%, 0%, 0.05)"
  :--black-transparent-06 "hsla(0, 0%, 0%, 0.06)"
  :--black-transparent-07 "hsla(0, 0%, 0%, 0.07)"
  :--black-transparent-08 "hsla(0, 0%, 0%, 0.08)"
  :--black-transparent-09 "hsla(0, 0%, 0%, 0.09)"
  :--black-transparent-10 "hsla(0, 0%, 0%, 0.1)"
  :--black-transparent-15 "hsla(0, 0%, 0%, 0.15)"
  :--black-transparent-20 "hsla(0, 0%, 0%, 0.20)"
  :--black-transparent-25 "hsla(0, 0%, 0%, 0.25)"
  :--black-transparent-30 "hsla(0, 0%, 0%, 0.30)"
  :--black-transparent-35 "hsla(0, 0%, 0%, 0.35)"
  :--black-transparent-40 "hsla(0, 0%, 0%, 0.40)"
  :--black-transparent-45 "hsla(0, 0%, 0%, 0.45)"
  :--black-transparent-50 "hsla(0, 0%, 0%, 0.50)"
  :--black-transparent-55 "hsla(0, 0%, 0%, 0.55)"
  :--black-transparent-60 "hsla(0, 0%, 0%, 0.60)"
  :--black-transparent-65 "hsla(0, 0%, 0%, 0.65)"
  :--black-transparent-70 "hsla(0, 0%, 0%, 0.70)"
  :--black-transparent-75 "hsla(0, 0%, 0%, 0.75)"
  :--black-transparent-80 "hsla(0, 0%, 0%, 0.80)"
  :--black-transparent-85 "hsla(0, 0%, 0%, 0.85)"
  :--black-transparent-90 "hsla(0, 0%, 0%, 0.90)"
  :--black-transparent-95 "hsla(0, 0%, 0%, 0.95)"
  :--black-transparent-100 "hsla(0, 0%, 0%, 1)"
  :--accent-50 "var(--blue-50)"
  :--accent-100 "var(--blue-100)"
  :--accent-150 "var(--blue-150)"
  :--accent-200 "var(--blue-200)"
  :--accent-250 "var(--blue-250)"
  :--accent-300 "var(--blue-300)"
  :--accent-350 "var(--blue-350)"
  :--accent-400 "var(--blue-400)"
  :--accent-450 "var(--blue-450)"
  :--accent-500 "var(--blue-500)"
  :--accent-550 "var(--blue-550)"
  :--accent-600 "var(--blue-600)"
  :--accent-650 "var(--blue-650)"
  :--accent-700 "var(--blue-700)"
  :--accent-750 "var(--blue-750)"
  :--accent-800 "var(--blue-800)"
  :--accent-850 "var(--blue-850)"
  :--accent-900 "var(--blue-900)"
  :--accent-950 "var(--blue-950)"
  :--accent-1000 "var(--blue-1000)"
  :--neutral-50 "var(--gray-50)"
  :--neutral-100 "var(--gray-100)"
  :--neutral-150 "var(--gray-150)"
  :--neutral-200 "var(--gray-200)"
  :--neutral-250 "var(--gray-250)"
  :--neutral-300 "var(--gray-300)"
  :--neutral-350 "var(--gray-350)"
  :--neutral-400 "var(--gray-400)"
  :--neutral-450 "var(--gray-450)"
  :--neutral-500 "var(--gray-500)"
  :--neutral-550 "var(--gray-550)"
  :--neutral-600 "var(--gray-600)"
  :--neutral-650 "var(--gray-650)"
  :--neutral-700 "var(--gray-700)"
  :--neutral-750 "var(--gray-750)"
  :--neutral-800 "var(--gray-800)"
  :--neutral-850 "var(--gray-850)"
  :--neutral-900 "var(--gray-900)"
  :--neutral-950 "var(--gray-950)"
  :--neutral-1000 "var(--gray-1000)"
  :--positive-50 "var(--green-50)"
  :--positive-100 "var(--green-100)"
  :--positive-150 "var(--green-150)"
  :--positive-200 "var(--green-200)"
  :--positive-250 "var(--green-250)"
  :--positive-300 "var(--green-300)"
  :--positive-350 "var(--green-350)"
  :--positive-400 "var(--green-400)"
  :--positive-450 "var(--green-450)"
  :--positive-500 "var(--green-500)"
  :--positive-550 "var(--green-550)"
  :--positive-600 "var(--green-600)"
  :--positive-650 "var(--green-650)"
  :--positive-700 "var(--green-700)"
  :--positive-750 "var(--green-750)"
  :--positive-800 "var(--green-800)"
  :--positive-850 "var(--green-850)"
  :--positive-900 "var(--green-900)"
  :--positive-950 "var(--green-950)"
  :--positive-1000 "var(--green-1000)"
  :--warning-50 "var(--gold-50)"
  :--warning-100 "var(--gold-100)"
  :--warning-150 "var(--gold-150)"
  :--warning-200 "var(--gold-200)"
  :--warning-250 "var(--gold-250)"
  :--warning-300 "var(--gold-300)"
  :--warning-350 "var(--gold-350)"
  :--warning-400 "var(--gold-400)"
  :--warning-450 "var(--gold-450)"
  :--warning-500 "var(--gold-500)"
  :--warning-550 "var(--gold-550)"
  :--warning-600 "var(--gold-600)"
  :--warning-650 "var(--gold-650)"
  :--warning-700 "var(--gold-700)"
  :--warning-750 "var(--gold-750)"
  :--warning-800 "var(--gold-800)"
  :--warning-850 "var(--gold-850)"
  :--warning-900 "var(--gold-900)"
  :--warning-950 "var(--gold-950)"
  :--warning-1000 "var(--gold-1000)"
  :--negative-50 "var(--red-50)"
  :--negative-100 "var(--red-100)"
  :--negative-150 "var(--red-150)"
  :--negative-200 "var(--red-200)"
  :--negative-250 "var(--red-250)"
  :--negative-300 "var(--red-300)"
  :--negative-350 "var(--red-350)"
  :--negative-400 "var(--red-400)"
  :--negative-450 "var(--red-450)"
  :--negative-500 "var(--red-500)"
  :--negative-550 "var(--red-550)"
  :--negative-600 "var(--red-600)"
  :--negative-650 "var(--red-650)"
  :--negative-700 "var(--red-700)"
  :--negative-750 "var(--red-750)"
  :--negative-800 "var(--red-800)"
  :--negative-850 "var(--red-850)"
  :--negative-900 "var(--red-900)"
  :--negative-950 "var(--red-950)"
  :--negative-1000 "var(--red-1000)"
  :--neutral-color "var(--neutral-900)"
  :--neutral-color-hover "var(--neutral-900)"
  :--neutral-color-active "var(--neutral-900)"
  :--neutral-background-color "var(--neutral-100)"
  :--neutral-background-color-hover "var(--neutral-200)"
  :--neutral-background-color-active "var(--neutral-300)"
  :--neutral-info-color "var(--neutral-900)"
  :--neutral-info-color-hover "var(--neutral-900)"
  :--neutral-info-color-active "var(--neutral-900)"
  :--neutral-info-background-color "var(--neutral-100)"
  :--neutral-info-background-color-hover "var(--neutral-100)"
  :--neutral-info-background-color-active "var(--neutral-100)"
  :--neutral-minimal-color "var(--neutral-900)"
  :--neutral-minimal-color-hover "var(--neutral-900)"
  :--neutral-minimal-color-active "var(--neutral-900)"
  :--neutral-minimal-background-color "transparent"
  :--neutral-minimal-background-color-hover "var(--neutral-100)"
  :--neutral-minimal-background-color-active "var(--neutral-200)"
  :--neutral-bordered-color "var(--neutral-900)"
  :--neutral-bordered-color-hover "var(--neutral-900)"
  :--neutral-bordered-color-active "var(--neutral-900)"
  :--neutral-bordered-background-color "transparent"
  :--neutral-bordered-background-color-hover "transparent"
  :--neutral-bordered-background-color-active "transparent"
  :--neutral-bordered-border-color "var(--neutral-900)"
  :--neutral-bordered-border-color-hover "var(--neutral-900)"
  :--neutral-bordered-border-color-active "var(--neutral-900)"
  :--neutral-bordered-info-background-color "transparent"
  :--neutral-bordered-info-background-color-hover "transparent"
  :--neutral-bordered-info-background-color-active "transparent"
  :--neutral-bordered-info-border-color "var(--neutral-900)"
  :--neutral-bordered-info-border-color-hover "var(--neutral-900)"
  :--neutral-bordered-info-border-color-active "var(--neutral-900)"
  :--neutral-filled-color "white"
  :--neutral-filled-color-hover "white"
  :--neutral-filled-color-active "white"
  :--neutral-filled-background-color "var(--neutral-700)"
  :--neutral-filled-background-color-hover "var(--neutral-800)"
  :--neutral-filled-background-color-active "var(--neutral-9000)"
  :--neutral-filled-info-background-color "var(--neutral-700)"
  :--neutral-filled-info-background-color-hover "var(--neutral-700)"
  :--info-background-color-active "var(---650)"
  :--neutral-color-inverse "var(--neutral-50)"
  :--neutral-color-hover-inverse "var(--neutral-0)"
  :--neutral-color-active-inverse "var(--neutral-0)"
  :--neutral-background-color-inverse "var(--neutral-850)"
  :--neutral-background-color-hover-inverse "var(--neutral-800)"
  :--neutral-background-color-active-inverse "var(--neutral-800)"
  :--neutral-info-color-inverse "var(--neutral-50)"
  :--neutral-info-color-hover-inverse "var(--neutral-50)"
  :--neutral-info-color-active-inverse "var(--neutral-50)"
  :--neutral-info-background-color-inverse "var(--neutral-750)"
  :--neutral-info-background-color-hover-inverse "var(--neutral-750)"
  :--neutral-info-background-color-active-inverse "var(--neutral-750)"
  :--neutral-minimal-color-inverse "var(--neutral-150)"
  :--neutral-minimal-color-hover-inverse "var(--neutral-50)"
  :--neutral-minimal-color-active-inverse "var(--neutral-0)"
  :--neutral-minimal-background-color-inverse "transparent"
  :--neutral-minimal-background-color-hover-inverse "var(--neutral-850)"
  :--neutral-minimal-background-color-active-inverse "var(--neutral-800)"
  :--neutral-bordered-color-inverse "var(--neutral-150)"
  :--neutral-bordered-color-hover-inverse "var(--neutral-100)"
  :--neutral-bordered-color-active-inverse "var(--neutral-50)"
  :--neutral-bordered-background-color-inverse "transparent"
  :--neutral-bordered-background-color-hover-inverse "transparent"
  :--neutral-bordered-background-color-active-inverse "transparent"
  :--neutral-bordered-border-color-inverse "var(--neutral-150)"
  :--neutral-bordered-border-color-hover-inverse "var(--neutral-100)"
  :--neutral-bordered-border-color-active-inverse "var(--neutral-50)"
  :--neutral-bordered-info-color-inverse "var(--neutral-100)"
  :--neutral-bordered-info-color-hover-inverse "var(--neutral-100)"
  :--neutral-bordered-info-color-active-inverse "var(--neutral-100)"
  :--neutral-bordered-info-background-color-inverse "transparent"
  :--neutral-bordered-info-background-color-hover-inverse "transparent"
  :--neutral-bordered-info-background-color-active-inverse "transparent"
  :--neutral-bordered-info-border-color-inverse "var(--neutral-50)"
  :--neutral-bordered-info-border-color-hover-inverse "var(--neutral-50)"
  :--neutral-bordered-info-border-color-active-inverse "var(--neutral-50)"
  :--neutral-filled-color-inverse "black"
  :--neutral-filled-color-hover-inverse "black"
  :--neutral-filled-color-active-inverse "black"
  :--neutral-filled-background-color-inverse "var(--neutral-250)"
  :--neutral-filled-background-color-hover-inverse "var(--neutral-100)"
  :--neutral-filled-background-color-active-inverse "var(--neutral-50)"
  :--neutral-filled-info-background-color-inverse "var(--neutral-250)"
  :--neutral-filled-info-background-color-hover-inverse "var(--neutral-250)"
  :--info-background-color-active-inverse "var(---500)"
  :--accent-color "var(--accent-700)"
  :--accent-color-hover "var(--accent-800)"
  :--accent-color-active "var(--accent-900)"
  :--accent-background-color "var(--accent-100)"
  :--accent-background-color-hover "var(--accent-200)"
  :--accent-background-color-active "var(--accent-300)"
  :--accent-info-color "var(--accent-800)"
  :--accent-info-color-hover "var(--accent-800)"
  :--accent-info-color-active "var(--accent-800)"
  :--accent-info-background-color "var(--accent-100)"
  :--accent-info-background-color-hover "var(--accent-100)"
  :--accent-info-background-color-active "var(--accent-100)"
  :--accent-minimal-color "var(--accent-650)"
  :--accent-minimal-color-hover "var(--accent-800)"
  :--accent-minimal-color-active "var(--accent-900)"
  :--accent-minimal-background-color "transparent"
  :--accent-minimal-background-color-hover "var(--accent-100)"
  :--accent-minimal-background-color-active "var(--accent-150)"
  :--accent-bordered-color "var(--accent-600)"
  :--accent-bordered-color-hover "var(--accent-700)"
  :--accent-bordered-color-active "var(--accent-800)"
  :--accent-bordered-background-color "transparent"
  :--accent-bordered-background-color-hover "transparent"
  :--accent-bordered-background-color-active "transparent"
  :--accent-bordered-border-color "var(--accent-600)"
  :--accent-bordered-border-color-hover "var(--accent-700)"
  :--accent-bordered-border-color-active "var(--accent-800)"
  :--accent-bordered-info-color "var(--accent-600)"
  :--accent-bordered-info-color-hover "var(--accent-600)"
  :--accent-bordered-info-color-active "var(--accent-600)"
  :--accent-bordered-info-background-color "transparent"
  :--accent-bordered-info-background-color-hover "transparent"
  :--accent-bordered-info-background-color-active "transparent"
  :--accent-bordered-info-border-color "var(--accent-600)"
  :--accent-bordered-info-border-color-hover "var(--accent-600)"
  :--accent-bordered-info-border-color-active "var(--accent-600)"
  :--accent-filled-color "white"
  :--accent-filled-color-hover "white"
  :--accent-filled-color-active "white"
  :--accent-filled-background-color "var(--accent-600)"
  :--accent-filled-background-color-hover "var(--accent-750)"
  :--accent-filled-background-color-active "var(--accent-850)"
  :--accent-filled-info-background-color "var(--accent-600)"
  :--accent-filled-info-background-color-hover "var(--accent-600)"
  :--accent-color-inverse "var(--accent-100)"
  :--accent-color-hover-inverse "var(--accent-50)"
  :--accent-color-active-inverse "var(--accent-50)"
  :--accent-background-color-inverse "var(--accent-750)"
  :--accent-background-color-hover-inverse "var(--accent-600)"
  :--accent-background-color-active-inverse "var(--accent-500)"
  :--accent-info-color-inverse "var(--accent-100)"
  :--accent-info-color-hover-inverse "var(--accent-100)"
  :--accent-info-color-active-inverse "var(--accent-100)"
  :--accent-info-background-color-inverse "var(--accent-750)"
  :--accent-info-background-color-hover-inverse "var(--accent-750)"
  :--accent-info-background-color-active-inverse "var(--accent-750)"
  :--accent-minimal-color-inverse "var(--accent-300)"
  :--accent-minimal-color-hover-inverse "var(--accent-200)"
  :--accent-minimal-color-active-inverse "var(--accent-100)"
  :--accent-minimal-background-color-inverse "transparent"
  :--accent-minimal-background-color-hover-inverse "var(--accent-750)"
  :--accent-minimal-background-color-active-inverse "var(--accent-850)"
  :--accent-bordered-color-inverse "var(--accent-300)"
  :--accent-bordered-color-hover-inverse "var(--accent-200)"
  :--accent-bordered-color-active-inverse "var(--accent-100)"
  :--accent-bordered-background-color-inverse "transparent"
  :--accent-bordered-background-color-hover-inverse "transparent"
  :--accent-bordered-background-color-active-inverse "transparent"
  :--accent-bordered-border-color-inverse "var(--accent-300)"
  :--accent-bordered-border-color-hover-inverse "var(--accent-200)"
  :--accent-bordered-border-color-active-inverse "var(--accent-100)"
  :--accent-bordered-info-color-inverse "var(--accent-300)"
  :--accent-bordered-info-color-hover-inverse "var(--accent-300)"
  :--accent-bordered-info-color-active-inverse "var(--accent-300)"
  :--accent-bordered-info-background-color-inverse "transparent"
  :--accent-bordered-info-background-color-hover-inverse "transparent"
  :--accent-bordered-info-background-color-active-inverse "transparent"
  :--accent-bordered-info-border-color-inverse "var(--accent-300)"
  :--accent-bordered-info-border-color-hover-inverse "var(--accent-300)"
  :--accent-bordered-info-border-color-active-inverse "var(--accent-300)"
  :--accent-filled-color-inverse "black"
  :--accent-filled-color-hover-inverse "black"
  :--accent-filled-color-active-inverse "black"
  :--accent-filled-background-color-inverse "var(--accent-400)"
  :--accent-filled-background-color-hover-inverse "var(--accent-250)"
  :--accent-filled-background-color-active-inverse "var(--accent-100)"
  :--accent-filled-info-background-color-inverse "var(--accent-400)"
  :--accent-filled-info-background-color-hover-inverse "var(--accent-400)"
  :--positive-color "var(--positive-800)"
  :--positive-color-hover "var(--positive-900)"
  :--positive-color-active "var(--positive-1000)"
  :--positive-background-color "var(--positive-100)"
  :--positive-background-color-hover "var(--positive-150)"
  :--positive-background-color-active "var(--positive-250)"
  :--positive-info-color "var(--positive-800)"
  :--positive-info-color-hover "var(--positive-800)"
  :--positive-info-color-active "var(--positive-800)"
  :--positive-info-background-color "var(--positive-100)"
  :--positive-info-background-color-hover "var(--positive-100)"
  :--positive-info-background-color-active "var(--positive-100)"
  :--positive-minimal-color "var(--positive-700)"
  :--positive-minimal-color-hover "var(--positive-800)"
  :--positive-minimal-color-active "var(--positive-900)"
  :--positive-minimal-background-color "transparent"
  :--positive-minimal-background-color-hover "var(--positive-100)"
  :--positive-minimal-background-color-active "var(--positive-150)"
  :--positive-bordered-color "var(--positive-700)"
  :--positive-bordered-color-hover "var(--positive-800)"
  :--positive-bordered-color-active "var(--positive-900)"
  :--positive-bordered-background-color "transparent"
  :--positive-bordered-background-color-hover "transparent"
  :--positive-bordered-background-color-active "transparent"
  :--positive-bordered-border-color "var(--positive-650)"
  :--positive-bordered-border-color-hover "var(--positive-750)"
  :--positive-bordered-border-color-active "var(--positive-900)"
  :--positive-bordered-info-color "var(--positive-700)"
  :--positive-bordered-info-color-hover "var(--positive-700)"
  :--positive-bordered-info-color-active "var(--positive-700)"
  :--positive-bordered-info-background-color "transparent"
  :--positive-bordered-info-background-color-hover "transparent"
  :--positive-bordered-info-background-color-active "transparent"
  :--positive-bordered-info-border-color "var(--positive-650)"
  :--positive-bordered-info-border-color-hover "var(--positive-650)"
  :--positive-bordered-info-border-color-active "var(--positive-650)"
  :--positive-filled-color "white"
  :--positive-filled-color-hover "white"
  :--positive-filled-color-active "white"
  :--positive-filled-background-color "var(--positive-650)"
  :--positive-filled-background-color-hover "var(--positive-750)"
  :--positive-filled-background-color-active "var(--positive-850)"
  :--positive-filled-info-background-color "var(--positive-650)"
  :--positive-filled-info-background-color-hover "var(--positive-650)"
  :--positive-color-inverse "var(--positive-200)"
  :--positive-color-hover-inverse "var(--positive-100)"
  :--positive-color-active-inverse "var(--positive-50)"
  :--positive-background-color-inverse "var(--positive-800)"
  :--positive-background-color-hover-inverse "var(--positive-700)"
  :--positive-background-color-active-inverse "var(--positive-600)"
  :--positive-info-color-inverse "var(--positive-200)"
  :--positive-info-color-hover-inverse "var(--positive-200)"
  :--positive-info-color-active-inverse "var(--positive-200)"
  :--positive-info-background-color-inverse "var(--positive-800)"
  :--positive-info-background-color-hover-inverse "var(--positive-800)"
  :--positive-info-background-color-active-inverse "var(--positive-800)"
  :--positive-minimal-color-inverse "var(--positive-350)"
  :--positive-minimal-color-hover-inverse "var(--positive-150)"
  :--positive-minimal-color-active-inverse "var(--positive-50)"
  :--positive-minimal-background-color-inverse "transparent"
  :--positive-minimal-background-color-hover-inverse "var(--positive-800)"
  :--positive-minimal-background-color-active-inverse "var(--positive-700)"
  :--positive-bordered-color-inverse "var(--positive-350)"
  :--positive-bordered-color-hover-inverse "var(--positive-150)"
  :--positive-bordered-color-active-inverse "var(--positive-50)"
  :--positive-bordered-background-color-inverse "transparent"
  :--positive-bordered-background-color-hover-inverse "transparent"
  :--positive-bordered-background-color-active-inverse "transparent"
  :--positive-bordered-border-color-inverse "var(--positive-350)"
  :--positive-bordered-border-color-hover-inverse "var(--positive-150)"
  :--positive-bordered-border-color-active-inverse "var(--positive-50)"
  :--positive-bordered-info-color-inverse "var(--positive-350)"
  :--positive-bordered-info-color-hover-inverse "var(--positive-350)"
  :--positive-bordered-info-color-active-inverse "var(--positive-350)"
  :--positive-bordered-info-background-color-inverse "transparent"
  :--positive-bordered-info-background-color-hover-inverse "transparent"
  :--positive-bordered-info-background-color-active-inverse "transparent"
  :--positive-bordered-info-border-color-inverse "var(--positive-350)"
  :--positive-bordered-info-border-color-hover-inverse "var(--positive-350)"
  :--positive-bordered-info-border-color-active-inverse "var(--positive-350)"
  :--positive-filled-color-inverse "black"
  :--positive-filled-color-hover-inverse "black"
  :--positive-filled-color-active-inverse "black"
  :--positive-filled-background-color-inverse "var(--positive-500)"
  :--positive-filled-background-color-hover-inverse "var(--positive-350)"
  :--positive-filled-background-color-active-inverse "var(--positive-250)"
  :--positive-filled-info-background-color-inverse "var(--positive-500)"
  :--positive-filled-info-background-color-hover-inverse "var(--positive-500)"
  :--negative-color "var(--negative-600)"
  :--negative-color-hover "var(--negative-800)"
  :--negative-color-active "var(--negative-900)"
  :--negative-background-color "var(--negative-100)"
  :--negative-background-color-hover "var(--negative-200)"
  :--negative-background-color-active "var(--negative-300)"
  :--negative-info-color "var(--negative-600)"
  :--negative-info-color-hover "var(--negative-600)"
  :--negative-info-color-active "var(--negative-600)"
  :--negative-info-background-color "var(--negative-100)"
  :--negative-info-background-color-hover "var(--negative-100)"
  :--negative-info-background-color-active "var(--negative-100)"
  :--negative-minimal-color "var(--negative-650)"
  :--negative-minimal-color-hover "var(--negative-800)"
  :--negative-minimal-color-active "var(--negative-900)"
  :--negative-minimal-background-color "transparent"
  :--negative-minimal-background-color-hover "var(--negative-100)"
  :--negative-minimal-background-color-active "var(--negative-200)"
  :--negative-bordered-color "var(--negative-600)"
  :--negative-bordered-color-hover "var(--negative-700)"
  :--negative-bordered-color-active "var(--negative-800)"
  :--negative-bordered-background-color "transparent"
  :--negative-bordered-background-color-hover "transparent"
  :--negative-bordered-background-color-active "transparent"
  :--negative-bordered-border-color "var(--negative-600)"
  :--negative-bordered-border-color-hover "var(--negative-700)"
  :--negative-bordered-border-color-active "var(--negative-800)"
  :--negative-bordered-info-color "var(--negative-600)"
  :--negative-bordered-info-color-hover "var(--negative-600)"
  :--negative-bordered-info-color-active "var(--negative-600)"
  :--negative-bordered-info-background-color "transparent"
  :--negative-bordered-info-background-color-hover "transparent"
  :--negative-bordered-info-background-color-active "transparent"
  :--negative-bordered-info-border-color "var(--negative-600)"
  :--negative-bordered-info-border-color-hover "var(--negative-600)"
  :--negative-bordered-info-border-color-active "var(--negative-600)"
  :--negative-filled-color "white"
  :--negative-filled-color-hover "white"
  :--negative-filled-color-active "white"
  :--negative-filled-background-color "var(--negative-500)"
  :--negative-filled-background-color-hover "var(--negative-700)"
  :--negative-filled-background-color-active "var(--negative-800)"
  :--negative-filled-info-background-color "var(--negative-600)"
  :--negative-filled-info-background-color-hover "var(--negative-600)"
  :--negative-color-inverse "var(--negative-100)"
  :--negative-color-hover-inverse "var(--negative-50)"
  :--negative-color-active-inverse "var(--negative-50)"
  :--negative-background-color-inverse "var(--negative-800)"
  :--negative-background-color-hover-inverse "var(--negative-700)"
  :--negative-background-color-active-inverse "var(--negative-600)"
  :--negative-info-color-inverse "var(--negative-100)"
  :--negative-info-color-hover-inverse "var(--negative-100)"
  :--negative-info-color-active-inverse "var(--negative-100)"
  :--negative-info-background-color-inverse "var(--negative-800)"
  :--negative-info-background-color-hover-inverse "var(--negative-800)"
  :--negative-info-background-color-active-inverse "var(--negative-800)"
  :--negative-minimal-color-inverse "var(--negative-300)"
  :--negative-minimal-color-hover-inverse "var(--negative-200)"
  :--negative-minimal-color-active-inverse "var(--negative-100)"
  :--negative-minimal-background-color-inverse "transparent"
  :--negative-minimal-background-color-hover-inverse "var(--negative-850)"
  :--negative-minimal-background-color-active-inverse "var(--negative-750)"
  :--negative-bordered-color-inverse "var(--negative-350)"
  :--negative-bordered-color-hover-inverse "var(--negative-250)"
  :--negative-bordered-color-active-inverse "var(--negative-150)"
  :--negative-bordered-background-color-inverse "transparent"
  :--negative-bordered-background-color-hover-inverse "transparent"
  :--negative-bordered-background-color-active-inverse "transparent"
  :--negative-bordered-border-color-inverse "var(--negative-350)"
  :--negative-bordered-border-color-hover-inverse "var(--negative-250)"
  :--negative-bordered-border-color-active-inverse "var(--negative-150)"
  :--negative-bordered-info-color-inverse "var(--negative-350)"
  :--negative-bordered-info-color-hover-inverse "var(--negative-350)"
  :--negative-bordered-info-color-active-inverse "var(--negative-350)"
  :--negative-bordered-info-background-color-inverse "transparent"
  :--negative-bordered-info-background-color-hover-inverse "transparent"
  :--negative-bordered-info-background-color-active-inverse "transparent"
  :--negative-bordered-info-border-color-inverse "var(--negative-350)"
  :--negative-bordered-info-border-color-hover-inverse "var(--negative-350)"
  :--negative-bordered-info-border-color-active-inverse "var(--negative-350)"
  :--negative-filled-color-inverse "black"
  :--negative-filled-color-hover-inverse "black"
  :--negative-filled-color-active-inverse "black"
  :--negative-filled-background-color-inverse "var(--negative-450)"
  :--negative-filled-background-color-hover-inverse "var(--negative-350)"
  :--negative-filled-background-color-active-inverse "var(--negative-250)"
  :--negative-filled-info-background-color-inverse "var(--negative-450)"
  :--negative-filled-info-background-color-hover-inverse "var(--negative-450)"
  :--warning-color "var(--warning-800)"
  :--warning-color-hover "var(--warning-850)"
  :--warning-color-active "var(--warning-900)"
  :--warning-background-color "var(--warning-150)"
  :--warning-background-color-hover "var(--warning-200)"
  :--warning-background-color-active "var(--warning-300)"
  :--warning-info-color "var(--warning-800)"
  :--warning-info-color-hover "var(--warning-800)"
  :--warning-info-color-active "var(--warning-800)"
  :--warning-info-background-color "var(--warning-150)"
  :--warning-info-background-color-hover "var(--warning-150)"
  :--warning-info-background-color-active "var(--warning-150)"
  :--warning-minimal-color "var(--warning-750)"
  :--warning-minimal-color-hover "var(--warning-850)"
  :--warning-minimal-color-active "var(--warning-900)"
  :--warning-minimal-background-color "transparent"
  :--warning-minimal-background-color-hover "var(--warning-100)"
  :--warning-minimal-background-color-active "var(--warning-150)"
  :--warning-bordered-color "var(--warning-700)"
  :--warning-bordered-color-hover "var(--warning-800)"
  :--warning-bordered-color-active "var(--warning-900)"
  :--warning-bordered-background-color "transparent"
  :--warning-bordered-background-color-hover "transparent"
  :--warning-bordered-background-color-active "transparent"
  :--warning-bordered-border-color "var(--warning-650)"
  :--warning-bordered-border-color-hover "var(--warning-750)"
  :--warning-bordered-border-color-active "var(--warning-850)"
  :--warning-bordered-info-color "var(--warning-650)"
  :--warning-bordered-info-color-hover "var(--warning-650)"
  :--warning-bordered-info-color-active "var(--warning-650)"
  :--warning-bordered-info-background-color "transparent"
  :--warning-bordered-info-background-color-hover "transparent"
  :--warning-bordered-info-background-color-active "transparent"
  :--warning-bordered-info-border-color "var(--warning-650)"
  :--warning-bordered-info-border-color-hover "var(--warning-650)"
  :--warning-bordered-info-border-color-active "var(--warning-650)"
  :--warning-filled-color "white"
  :--warning-filled-color-hover "white"
  :--warning-filled-color-active "white"
  :--warning-filled-background-color "var(--warning-650)"
  :--warning-filled-background-color-hover "var(--warning-700)"
  :--warning-filled-background-color-active "var(--warning-750)"
  :--warning-filled-info-background-color "var(--warning-650)"
  :--warning-filled-info-background-color-hover "var(--warning-650)"
  :--warning-color-inverse "var(--warning-200)"
  :--warning-color-hover-inverse "var(--warning-100)"
  :--warning-color-active-inverse "var(--warning-50)"
  :--warning-background-color-inverse "var(--warning-850)"
  :--warning-background-color-hover-inverse "var(--warning-750)"
  :--warning-background-color-active-inverse "var(--warning-650)"
  :--warning-info-color-inverse "var(--warning-200)"
  :--warning-info-color-hover-inverse "var(--warning-200)"
  :--warning-info-color-active-inverse "var(--warning-200)"
  :--warning-info-background-color-inverse "var(--warning-850)"
  :--warning-info-background-color-hover-inverse "var(--warning-850)"
  :--warning-info-background-color-active-inverse "var(--warning-850)"
  :--warning-minimal-color-inverse "var(--warning-500)"
  :--warning-minimal-color-hover-inverse "var(--warning-350)"
  :--warning-minimal-color-active-inverse "var(--warning-200)"
  :--warning-minimal-background-color-inverse "transparent"
  :--warning-minimal-background-color-hover-inverse "var(--warning-850)"
  :--warning-minimal-background-color-active-inverse "var(--warning-750)"
  :--warning-bordered-color-inverse "var(--warning-500)"
  :--warning-bordered-color-hover-inverse "var(--warning-350)"
  :--warning-bordered-color-active-inverse "var(--warning-200)"
  :--warning-bordered-background-color-inverse "transparent"
  :--warning-bordered-background-color-hover-inverse "transparent"
  :--warning-bordered-background-color-active-inverse "transparent"
  :--warning-bordered-border-color-inverse "var(--warning-450)"
  :--warning-bordered-border-color-hover-inverse "var(--warning-300)"
  :--warning-bordered-border-color-active-inverse "var(--warning-200)"
  :--warning-bordered-info-color-inverse "var(--warning-400)"
  :--warning-bordered-info-color-hover-inverse "var(--warning-400)"
  :--warning-bordered-info-color-active-inverse "var(--warning-400)"
  :--warning-bordered-info-background-color-inverse "transparent"
  :--warning-bordered-info-background-color-hover-inverse "transparent"
  :--warning-bordered-info-background-color-active-inverse "transparent"
  :--warning-bordered-info-border-color-inverse "var(--warning-450)"
  :--warning-bordered-info-border-color-hover-inverse "var(--warning-450)"
  :--warning-bordered-info-border-color-active-inverse "var(--warning-450)"
  :--warning-filled-color-inverse "black"
  :--warning-filled-color-hover-inverse "black"
  :--warning-filled-color-active-inverse "black"
  :--warning-filled-background-color-inverse "var(--warning-500)"
  :--warning-filled-background-color-hover-inverse "var(--warning-350)"
  :--warning-filled-background-color-active-inverse "var(--warning-250)"
  :--warning-filled-info-background-color-inverse "var(--warning-500)"
  :--warning-filled-info-background-color-hover-inverse "var(--warning-500)"
  :--foreground-color "var(--neutral-950)"
  :--foreground-color-inverse "var(--neutral-50)"
  :--background-color "white"
  :--background-color-inverse "var(--neutral-1000)"
  :--neutral-foreground "var(--neutral-minimal-color)"
  :--neutral-foreground-inverse "var(--neutral-minimal-color-inverse)"
  :--neutral-secondary-foreground "var(--neutral-700)"
  :--neutral-secondary-foreground-inverse "var(--neutral-350)"
  :--accent-foreground "var(--accent-minimal-color)"
  :--accent-foreground-inverse "var(--accent-minimal-color-inverse)"
  :--positive-foreground "var(--positive-minimal-color)"
  :--positive-foreground-inverse "var(--positive-minimal-color-inverse)"
  :--warning-foreground "var(--warning-minimal-color)"
  :--warning-foreground-inverse "var(--warning-minimal-color-inverse)"
  :--negative-foreground "var(--negative-minimal-color)"
  :--negative-foreground-inverse "var(--negative-minimal-color-inverse)"


   ;; General colors
   ;; ------------------------------------------------------
   :--foreground-color                       :$neutral-950
   :--foreground-color-inverse               :$neutral-50

   :--background-color                        :white
   :--background-color-inverse                :$neutral-1000

   ;; Neutrals
   :--neutral-foreground                      :$neutral-minimal-color
   :--neutral-foreground-inverse              :$neutral-minimal-color-inverse
   :--neutral-secondary-foreground            :$neutral-700
   :--neutral-secondary-foreground-inverse    :$neutral-350
   :--accent-foreground                       :$accent-minimal-color
   :--accent-foreground-inverse               :$accent-minimal-color-inverse
   :--positive-foreground                     :$positive-minimal-color
   :--positive-foreground-inverse             :$positive-minimal-color-inverse
   :--warning-foreground                      :$warning-minimal-color
   :--warning-foreground-inverse              :$warning-minimal-color-inverse
   :--negative-foreground                     :$negative-minimal-color
   :--negative-foreground-inverse             :$negative-minimal-color-inverse


   ;; Debugging grid
   ;; ------------------------------------------------------
   :--debug-grid-size                         :16px
   :--debug-grid-color                        "hsla(0 0% 90%)"
   :--debug-grid-color-inverse                "hsla(0 0% 25%)"


   ;; Typography
   ;; ------------------------------------------------------
   

   ;; font-family
   ;; TODO should this live in basetheme?
   :--sans-serif-font-stack                  
   "Inter, system-ui, sans-serif, -apple-system, BlinkMacSystemFont, \"Segoe UI\", Roboto, \"Helvetica Neue\", Arial, \"Noto Sans\", sans-serif, \"Apple Color Emoji\", \"Segoe UI Emoji\", \"Segoe UI Symbol\", \"Noto Color Emoji\""
   :--serif-font-stack                       
   "Cormorant, Times, serif"
   :--code-font-stack                        
   "'Fira Code', monospace"
   :--sans                                   :$sans-serif-font-stack
   :--serif                                  :$serif-font-stack


   ;; code
   :--code-font-size                         :$small
   :--code-padding-inline                    :0.2em
   :--code-padding-block                     :0.08em
   :--code-border-radius                     :3px
   :--code-background-color                  :$gray-100
   :--code-background-color-inverse          :$gray-800
   :--code-color-inverse                     :$gray-50


   ;; Intended for css prop `font-weight`
   :--thin                                   100
   :--extra-light                            200
   :--light                                  300
   :--normal                                 400
   :--wee-bold                               500
   :--semi-bold                              600
   :--bold                                   700
   :--extra-bold                             800
   :--heavy                                  900

   :--root-font-size                         :1rem


   ;; Intended for css prop `font-size`
   :--xxxxsmall                              :0.64rem
   :--xxxsmall                               :0.67rem
   :--xxsmall                                :0.71rem
   :--xsmall                                 :0.77rem
   :--small                                  :0.86rem
   :--medium                                 :1rem
   :--large                                  :1.21rem
   :--xlarge                                 :1.485rem
   :--xxlarge                                :1.86rem
   :--xxxlarge                               :2.36rem
   :--xxxxlarge                              :3.03rem

   :--xxxxsmall-b                            :0.655rem
   :--xxxsmall-b                             :0.685rem
   :--xxsmall-b                              :0.733rem
   :--xsmall-b                               :0.805rem
   :--small-b                                :0.92rem
   :--medium-b                               :1.1rem
   :--large-b                                :1.33rem
   :--xlarge-b                               :1.655rem
   :--xxlarge-b                              :2.085rem
   :--xxxlarge-b                             :2.68rem
   :--xxxxlarge-b                            :3.475rem


   ;; Intended for css prop `letterspacing`
   :--xxxtight                               :-0.09em
   :--xxtight                                :-0.06em
   :--xtight                                 :-0.03em
   :--tight                                  :-0.01em
   :--loose                                  :0.04em
   :--xloose                                 :0.08em
   :--xxloose                                :0.12em
   :--xxxloose                               :0.16em


   ;; Intended for css props `border-width` for inputs
   :--input-border-weight-thin               :0.05em
   :--input-border-weight-extra-light        :0.07em
   :--input-border-weight-light              :0.09em
   :--input-border-weight-normal             :0.1em
   :--input-border-weight-wee-bold           :0.12em
   :--input-border-weight-semi-bold          :0.135em
   :--input-border-weight-bold               :0.165em
   :--input-border-weight-extra-bold         :0.195em
   :--input-border-weight-heavy              :0.21em


   ;; Intended for css props: border-*, general
   :--border-width                           :1px
   :--border-style                           :solid

   ;; Intended for divisors and divisor-like borders
   :--divisor-thickness                      :1px
   :--divisor-style                          :solid
   :--divisor-color-0                        :transparent
   :--divisor-color-1                        :$neutral-50
   :--divisor-color-2                        :$neutral-100
   :--divisor-color-3                        :$neutral-150
   :--divisor-color-4                        :$neutral-200
   :--divisor-color-5                        :$neutral-250
   :--divisor-color-6                        :$neutral-300
   :--divisor-color-7                        :$neutral-350
   :--divisor-color-8                        :$neutral-450
   :--divisor-color-9                        :$neutral-500
   :--divisor-color-10                       :$neutral-550
   :--divisor-color-1-inverse                :$neutral-950
   :--divisor-color-2-inverse                :$neutral-900
   :--divisor-color-3-inverse                :$neutral-850
   :--divisor-color-4-inverse                :$neutral-800
   :--divisor-color-5-inverse                :$neutral-750
   :--divisor-color-6-inverse                :$neutral-700
   :--divisor-color-7-inverse                :$neutral-650
   :--divisor-color-8-inverse                :$neutral-600
   :--divisor-color-9-inverse                :$neutral-550
   :--divisor-color-10-inverse               :$neutral-500
   :--divisor-color                          :$divisor-color-3
   :--divisor-color-inverse                  :$divisor-color-5-inverse
   :--divisor-0                              "var(--divisor-thickness) var(--divisor-style) var(--divisor-color-0)"
   :--divisor-1                              "var(--divisor-thickness) var(--divisor-style) var(--divisor-color-1)"
   :--divisor-2                              "var(--divisor-thickness) var(--divisor-style) var(--divisor-color-2)"
   :--divisor-3                              "var(--divisor-thickness) var(--divisor-style) var(--divisor-color-3)"
   :--divisor-4                              "var(--divisor-thickness) var(--divisor-style) var(--divisor-color-4)"
   :--divisor-5                              "var(--divisor-thickness) var(--divisor-style) var(--divisor-color-5)"
   :--divisor-6                              "var(--divisor-thickness) var(--divisor-style) var(--divisor-color-6)"
   :--divisor-7                              "var(--divisor-thickness) var(--divisor-style) var(--divisor-color-7)"
   :--divisor-8                              "var(--divisor-thickness) var(--divisor-style) var(--divisor-color-8)"
   :--divisor-9                              "var(--divisor-thickness) var(--divisor-style) var(--divisor-color-9)"
   :--divisor-10                             "var(--divisor-thickness) var(--divisor-style) var(--divisor-color-10)"
   :--divisor-1-inverse                      "var(--divisor-thickness) var(--divisor-style) var(--divisor-color-1-inverse)"
   :--divisor-2-inverse                      "var(--divisor-thickness) var(--divisor-style) var(--divisor-color-2-inverse)"
   :--divisor-3-inverse                      "var(--divisor-thickness) var(--divisor-style) var(--divisor-color-3-inverse)"
   :--divisor-4-inverse                      "var(--divisor-thickness) var(--divisor-style) var(--divisor-color-4-inverse)"
   :--divisor-5-inverse                      "var(--divisor-thickness) var(--divisor-style) var(--divisor-color-5-inverse)"
   :--divisor-6-inverse                      "var(--divisor-thickness) var(--divisor-style) var(--divisor-color-6-inverse)"
   :--divisor-7-inverse                      "var(--divisor-thickness) var(--divisor-style) var(--divisor-color-7-inverse)"
   :--divisor-8-inverse                      "var(--divisor-thickness) var(--divisor-style) var(--divisor-color-8-inverse)"
   :--divisor-9-inverse                      "var(--divisor-thickness) var(--divisor-style) var(--divisor-color-9-inverse)"
   :--divisor-10-inverse                     "var(--divisor-thickness) var(--divisor-style) var(--divisor-color-10-inverse)"
   :--divisor                                :$divisor-3
   :--divisor-inverse                        :$divisor-5-inverse


   ;; Intended for overlay placement
   :--overlay-placement-inline-offset        :12px
   :--overlay-placement-block-offset         :6px



   ;; Buttons
   ;; ------------------------------------------------------
   :--button-padding-inline-ems              :1.2em
   :--icon-button-padding-inline-ems         :0.69em
   :--button-padding-block-ems               :0.67em
   :--button-with-icon-padding-inline-offset :0.9em
   :--button-border-width                    :1px



   ;; Tags
   :--tag-border-width                       :1px



   ;; pane - floating layer abstraction
   ;; ------------------------------------------------------
   
   ;; pane colors and images
   :--pane-background-color                 :$background-color
   :--pane-background-color-inverse         :$background-color-inverse
   :--pane-background-image                 :white
   :--pane-box-shadow                       :$elevated-5
   :--pane-box-shadow-inverse               :$elevated-5-inverse
   :--pane-border-width                     :0px
   :--pane-border-style                     :solid
   :--pane-border-color                     :transparent
   :--pane-border-color-inverse             :transparent

   ;; pane geometry
   :--pane-min-width                        :70px
   :--pane-min-height                       :35px
   :--pane-padding-inline                   :1em
   :--pane-padding-block                    :0.5em
   :--pane-border-radius                    :$rounded-absolute-large
   :--pane-offset                           :7px
   :--pane-viewport-padding                 :5px 
   :--pane-flip-viewport-edge-threshold     :32px 
   :--pane-auto-placement-y-threshold       :0.1 

   ;; pane choreography
   :--pane-offset-start                     "calc(var(--pane-offset) + 5px)"
   :--pane-z-index                          :auto
   :--pane-delay-duration                   :0ms
   :--pane-transition-duration              :$xfast 
   :--pane-transition-timing-function       :$timing-ease-out-curve 

   ;; pane arrows
   :--pane-arrow-inline-inset               :7px
   :--pane-arrow-block-inset                :2px
   :--pane-arrow-depth                      :7px


   ;; Modals
   ;; ------------------------------------------------------
   :--modal-border-radius                    :$pane-border-radius
   :--modal-border                           :none
   :--modal-padding                          :1.75rem
   :--modal-padding-inline                   :$modal-padding
   :--modal-padding-block                    :$modal-padding
   :--modal-backdrop-color                   :$black-transparent-50
   :--modal-margin                           :1rem
   :--modal-min-width                        :100px
   :--modal-max-height                       :800px
   :--modal-transition-duration              :$pane-transition-duration



   ;; Tooltips
   ;; ------------------------------------------------------
   
   ;; tooltip colors and images
   :--tooltip-color                            :$foreground-color-inverse
   :--tooltip-color-inverse                    :$foreground-color
   :--tooltip-background-color                 :$background-color-inverse
   :--tooltip-background-color-inverse         :$background-color
   :--tooltip-background-image                 :none
   :--tooltip-box-shadow                       :none
   :--tooltip-box-shadow-inverse               :none
   :--tooltip-border-width                     :$pane-border-width
   :--tooltip-border-style                     :$pane-border-style
   :--tooltip-border-color                     :$pane-border-color
   :--tooltip-border-color-inverse             :$pane-border-color-inverse

   ;; pane typography
   :--tooltip-line-height                      1.45
   :--tooltip-font-family                      :$sans-serif-font-stack
   :--tooltip-font-size                        :$xsmall
   :--tooltip-font-weight                      :$wee-bold
   :--tooltip-text-transform                   :none

   ;; tooltip geometry
   :--tooltip-min-width                        :1rem
   :--tooltip-min-height                       :1rem
   :--tooltip-padding-inline                   :1.2em
   :--tooltip-padding-block                    :0.65em
   :--tooltip-border-radius                    :5px
   :--tooltip-offset                           :$pane-offset
   :--tooltip-viewport-padding                 :$pane-viewport-padding 
   :--tooltip-flip-viewport-edge-threshold     :$pane-flip-viewport-edge-threshold 
   :--tooltip-auto-placement-y-threshold       :$pane-auto-placement-y-threshold 

   ;; tooltip choreography
   :--tooltip-offset-start                     :$pane-offset-start
   :--tooltip-z-index                          :$pane-z-index
   :--tooltip-delay-duration                   :550ms
   :--tooltip-text-on-click-duration           :2000ms
   :--tooltip-initial-scale                    1
   :--tooltip-transition-duration              :$pane-transition-duration 
   :--tooltip-transition-timing-function       :$pane-transition-timing-function 

   ;; tooltip arrows
   :--tooltip-arrow-inline-inset               :$pane-arrow-inline-inset
   :--tooltip-arrow-block-inset                :$pane-arrow-inline-inset
   :--tooltip-arrow-depth                      :5px





   ;; Popovers
   ;; ------------------------------------------------------
   
   ;; popover colors and images
   :--popover-background-color                 :$pane-background-color
   :--popover-background-color-inverse         :$pane-background-color-inverse
   :--popover-background-image                 :none
   :--popover-box-shadow                       :$pane-box-shadow
   :--popover-box-shadow-inverse               :$pane-box-shadow-inverse
   :--popover-border-width                     :1px
   :--popover-border-style                     :solid
   :--popover-border-color                     :$neutral-200
   :--popover-border-color-inverse             :$neutral-500

   ;; popover geometry
   :--popover-min-width                        :$pane-min-width
   :--popover-min-height                       :$pane-min-height
   :--popover-border-radius                    :$pane-border-radius
   :--popover-offset                           :$pane-offset
   :--popover-viewport-padding                 :$pane-viewport-padding 
   :--popover-flip-viewport-edge-threshold     :$pane-flip-viewport-edge-threshold 
   :--popover-auto-placement-y-threshold       :$pane-auto-placement-y-threshold 

   ;; popover choreography
   :--popover-offset-start                     :$pane-offset-start
   :--popover-z-index                          :$pane-z-index
   :--popover-delay-duration                   :0ms
   :--popover-initial-scale                    1
   :--popover-transition-duration              :$pane-transition-duration 
   :--popover-transition-timing-function       :$pane-transition-timing-function 
   :--popover-auto-dismiss-duration            :5000ms

   ;; popover arrows
   :--popover-arrow-inline-inset               :$pane-arrow-inline-inset
   :--popover-arrow-block-inset                :$pane-arrow-inline-inset
   :--popover-arrow-depth                      :7px

   
   ;; toasts
   ;; ------------------------------------------------------
   
   ;; toast colors and images
   :--toast-background-color                 :$pane-background-color
   :--toast-background-color-inverse         :$pane-background-color-inverse
   :--toast-background-image                 :none
   :--toast-box-shadow                       :$pane-box-shadow
   :--toast-box-shadow-inverse               :$pane-box-shadow-inverse
   :--toast-border-width                     :1px
   :--toast-border-style                     :solid
   :--toast-border-color                     :$gray-150
   :--toast-border-color-inverse             :$gray-700

   ;; toast geometry
   :--toast-border-radius                    :$pane-border-radius
   :--toast-slot-padding-inline              :1rem
   :--toast-slot-padding-block               :1rem
   :--toast-slot-gap                         :1rem
   :--toast-slot-z-index                     100000

   ;; toast choreography
   :--toast-delay-duration                   :200ms
   :--toast-initial-scale                    1
   :--toast-transition-duration              :$pane-transition-duration 
   :--toast-transition-timing-function       :$pane-transition-timing-function 
   :--toast-auto-dismiss-duration            :5000ms
   

   ;; Modals
   ;; ------------------------------------------------------
   :--modal-border-radius                    :$rounded-absolute-large
   :--modal-border                           :none
   :--modal-padding                          :2rem
   :--modal-padding-block                    :$modal-padding
   :--modal-padding-inline                   :$modal-padding
   :--modal-backdrop-color                   :$black-transparent-40
   :--modal-margin                           :1rem
   :--modal-min-width                        :200px
   :--modal-transition-duration              :$xfast


   ;; Material UI icons
   ;; ------------------------------------------------------
   :--mui-icon-relative-font-size            :inherit



   ;; General icons
   ;; ------------------------------------------------------
   :--icon-enhanceable-gap                   :0.25em


   ;; Intended for css props: border-radius
   ;; ------------------------------------------------------
   
   ;; Absolute versions for panes, cards, etc.
   :--rounded-absolute-xxxsmall              :0.0625rem         ;; 1px
   :--rounded-absolute-xxsmall               :0.125rem         ;; 2px
   :--rounded-absolute-xsmall                :0.25rem          ;; 4px
   :--rounded-absolute-small                 :0.375rem         ;; 6px
   :--rounded-absolute-medium                :0.5rem           ;; 8px
   :--rounded-absolute-large                 :0.75rem          ;; 12px
   :--rounded-absolute-xlarge                :0.1rem           ;; 16px
   :--rounded-absolute-xxlarge               :1.25rem          ;; 20px
   :--rounded-absolute-xxxlarge              :1.5625rem        ;; 25px
   
   ;; Relative (to type size) versions for buttons, badges
   :--rounded-xxxsmall                       :0.04375em  
   :--rounded-xxsmall                        :0.0875em  
   :--rounded-xsmall                         :0.175em   
   :--rounded-small                          :0.2625em  
   :--rounded-medium                         :0.35em    
   :--rounded-large                          :0.525em   
   :--rounded-xlarge                         :0.7em     
   :--rounded-xxlarge                        :0.875em   
   :--rounded-xxxlarge                       :1.09375em 
   :--rounded                                :$rounded-medium
   :--border-weight                          :1px


    ;; Intended for css props: background-image
    ;; ------------------------------------------------------
   
   :--convex-0      :none
   :--convex-1      "linear-gradient(180deg, hsl(0deg 0% 100% / 20%), transparent, hsl(0deg 0% 0% / 15%))"
   :--convex-2     "linear-gradient(180deg, hsl(0deg 0% 100% / 25%), transparent, hsl(0deg 0% 0% / 25%))" 
   :--convex-3     "linear-gradient(180deg, hsl(0deg 0% 0% / 30%), transparent, hsl(0deg 0% 100% / 35%))"
   :--convex-4     "linear-gradient(180deg, hsl(0deg 0% 0% / 35%), transparent, hsl(0deg 0% 100% / 45%))"
   :--convex-5     "linear-gradient(180deg, hsl(0deg 0% 0% / 40%), transparent, hsl(0deg 0% 100% / 50%))"
   :--convex        :$convex-1


    ;; Intended for css props: box-shadow
    ;; ------------------------------------------------------
   
   :--elevated-0
   :none

   :--elevation-shadow-layer-1-color :$black-transparent-08
   :--elevation-shadow-layer-2-color :$black-transparent-05
   :--elevation-shadow-layer-3-color :$black-transparent-03
   :--elevation-shadow-layer-1-color-inverse :$white-transparent-08
   :--elevation-shadow-layer-2-color-inverse :$white-transparent-05
   :--elevation-shadow-layer-3-color-inverse :$white-transparent-03
;; maps to MUI2 level 1
 :--elevated-1
 (str
  "0px 3px 3px -2px var(--elevation-shadow-layer-1-color),"
  "0px 3px 4px 0px var(--elevation-shadow-layer-2-color),"
  "0px 1px 8px 0px var(--elevation-shadow-layer-3-color)")
 :--elevated-1-inverse
 (str
  "0px 3px 3px -2px var(--elevation-shadow-layer-1-color-inverse),"
  "0px 3px 4px 0px var(--elevation-shadow-layer-2-color-inverse),"
  "0px 1px 8px 0px var(--elevation-shadow-layer-3-color-inverse)")
 ;; "rgb(0 0 0 / 20%) 0px 2px 1px -1px, rgb(0 0 0 / 14%) 0px 1px 1px 0px, rgb(0 0 0 / 12%) 0px 1px 3px 0px"
 ;;  
 ;;  
 ;; maps to MUI2 level 3
 :--elevated-2
 (str
  "0px 3px 3px -2px var(--elevation-shadow-layer-1-color),"
  "0px 3px 4px 0px var(--elevation-shadow-layer-2-color),"
  "0px 1px 8px 0px var(--elevation-shadow-layer-3-color)")
 :--elevated-2-inverse
 (str
  "0px 3px 3px -2px var(--elevation-shadow-layer-1-color-inverse),"
  "0px 3px 4px 0px var(--elevation-shadow-layer-2-color-inverse),"
  "0px 1px 8px 0px var(--elevation-shadow-layer-3-color-inverse)")
 ;; "rgb(0 0 0 / 20%) 0px 3px 3px -2px, rgb(0 0 0 / 14%) 0px 3px 4px 0px, rgb(0 0 0 / 12%) 0px 1px 8px 0px"
 ;;  
 ;;  
 ;; maps to MUI2 level 6
 :--elevated-3
 (str
  "0px 3px 5px -1px var(--elevation-shadow-layer-1-color),"
  "0px 6px 10px 0px var(--elevation-shadow-layer-2-color),"
  "0px 1px 18px 0px var(--elevation-shadow-layer-3-color)")
 :--elevated-3-inverse
 (str
  "0px 3px 5px -1px var(--elevation-shadow-layer-1-color-inverse),"
  "0px 6px 10px 0px var(--elevation-shadow-layer-2-color-inverse),"
  "0px 1px 18px 0px var(--elevation-shadow-layer-3-color-inverse)")
 ;; "rgb(0 0 0 / 20%) 0px 3px 5px -1px, rgb(0 0 0 / 14%) 0px 6px 10px 0px, rgb(0 0 0 / 12%) 0px 1px 18px 0px"
 ;;  
 ;;  
 ;; maps to MUI2 level 8
 :--elevated-4
 (str
  "0px 5px 5px -3px var(--elevation-shadow-layer-1-color),"
  "0px 8px 18px 1px var(--elevation-shadow-layer-2-color),"
  "0px 6px 20px 2px var(--elevation-shadow-layer-3-color)")
 :--elevated-4-inverse
 (str
  "0px 5px 5px -3px var(--elevation-shadow-layer-1-color-inverse),"
  "0px 8px 18px 1px var(--elevation-shadow-layer-2-color-inverse),"
  "0px 6px 20px 2px var(--elevation-shadow-layer-3-color-inverse)")
  ;; "rgb(0 0 0 / 20%) 0px 5px 5px -3px, rgb(0 0 0 / 14%) 0px 8px 10px 1px, rgb(0 0 0 / 12%) 0px 3px 14px 2px"

  ;; maps to MUI2 level 12
   :--elevated-5
   (str "0px 7px 14px -2px  var(--elevation-shadow-layer-1-color),"
        "0px 6px 26px 0px var(--elevation-shadow-layer-2-color),"
        "0px 8px 27px 0px  var(--elevation-shadow-layer-3-color)" )

   :--elevated-5-inverse
   (str "0px 7px 14px -2px  var(--elevation-shadow-layer-1-color-inverse),"
        "0px 6px 26px 0px var(--elevation-shadow-layer-2-color-inverse),"
        "0px 8px 27px 0px  var(--elevation-shadow-layer-3-color-inverse)" )
  ;;  ;; "rgb(0 0 0 / 20%) 0px 7px 8px -4px, rgb(0 0 0 / 14%) 0px 12px 17px 2px, rgb(0 0 0 / 12%) 0px 5px 22px 4px"
   
   :--elevated
   :--elevated-4

   :--elevated-inverse
   :--elevated-4-inverse

   ;; Intended for css animations and transitions
   ;; ------------------------------------------------------
   :--timing-linear-curve           "cubic-bezier(0, 0, 1, 1)"
   :--timing-ease-out-curve         "cubic-bezier(.2, .8, .4, 1)"
   :--timing-ease-out-curve-5       "cubic-bezier(.2, .8, .4, 1)"
   :--timing-ease-in-curve          "cubic-bezier(.8, .2, .6, 1)"
   :--timing-ease-in-out-curve      "cubic-bezier(0.4, 0, 0.2, 1)"
   :--timing-ease-out-curve-extreme "cubic-bezier(0.190, 0.510, 0.125, 0.905)"
   :--transition-timing-function    :$timing-linear-curve
   :--transition-duration           :$fast
   :--instant                       :0ms
   :--xxxfast                       :50ms
   :--xxfast                        :100ms
   :--xfast                         :175ms
   :--fast                          :250ms
   :--moderate                      :500ms
   :--slow                          :700ms
   :--xslow                         :1s
   :--xxslow                        :2s
   :--xxxslow                       :4s
   :--spinner-animation-duration   :900ms
   :--loading-spinner-height        :0.8em


   ;; Intended for styling scrollbars with the .styled-scrollbars utility-class
   ;; ------------------------------------------------------
   :--scrollbar-thumb-color                  :$neutral-300
   :--scrollbar-thumb-color-inverse          :$neutral-700
   :--scrollbar-background-color             :$neutral-50
   :--scrollbar-background-color-inverse     :$neutral-900
   :--scrollbar-width                        :5px


   ;; Kushi UI Components (move?)
   ;; ------------------------------------------------------
   :--collapse-transition-duration              :$slow

   ;; kushi.ui.text-field.core/input
   :--text-input-helper-margin-block-start      :0.3em
   :--text-input-label-inline-margin-inline-end :0.7em
   :--text-input-label-block-margin-block-end   :0.4em

   ;; Remove wrapper from this
   :--text-input-border-intensity               :50%
   :--text-input-border-intensity-inverse       :55%
   :--text-input-border-radius                  :0.3em


   ;; Switches
   ;; ------------------------------------------------------
   :--switch-thumb-scale-factor
   1

   :--switch-width-ratio
   2

   :--switch-border-color
   :transparent

   :--switch-border-width
   :2px

   ;; off/unchecked
   :--switch-off-background-color
   :$neutral-400

   :--switch-off-background-color-hover
   :$neutral-500

   ;; off/unchecked dark
   :--switch-off-background-color-inverse
   :$neutral-750

   :--switch-off-background-color-hover-inverse
   :$neutral-700

   ;; Neutral
   :--switch-on-background-color
   :$neutral-700

   :--switch-on-background-color-hover
   :$neutral-750

   :--switch-thumb-on-neutral-color
   :$neutral-minimal-color

   :--switch-thumb-on-neutral-color-hover
   :$neutral-minimal-color-hover

   ;; Accent
   :--switch-on-accent-background-color
   :$accent-500

   :--switch-on-accent-background-color-hover
   :$accent-600

   :--switch-thumb-on-accent-color
   :$accent-minimal-color

   :--switch-thumb-on-accent-color-hover
   :$accent-minimal-color-hover

   ;; Positive
   :--switch-on-positive-background-color
   :$positive-500

   :--switch-on-positive-background-color-hover
   :$positive-600

   :--switch-thumb-on-positive-color
   :$positive-minimal-color

   :--switch-thumb-on-positive-color-hover
   :$positive-minimal-color-hover

   ;; Warning
   :--switch-on-warning-background-color
   :$warning-550

   :--switch-on-warning-background-color-hover
   :$warning-650

   :--switch-thumb-on-warning-color
   :$warning-minimal-color

   :--switch-thumb-on-warning-color-hover
   :$warning-minimal-color-hover

   ;; Negative
   :--switch-on-negative-background-color
   :$negative-filled-background-color

   :--switch-on-negative-background-color-hover
   :$negative-filled-background-color-hover

   :--switch-thumb-on-negative-color
   :$negative-minimal-color

   :--switch-thumb-on-negative-color-hover
   :$negative-minimal-color-hover

   ;; Neutral dark
   :--switch-on-background-color-inverse
   :$neutral-550

   :--switch-on-background-color-hover-inverse
   :$neutral-550

   :--switch-thumb-on-neutral-color-inverse
   :$neutral-minimal-color-inverse

   :--switch-thumb-on-neutral-color-hover-inverse
   :$neutral-minimal-color-hover-inverse

   ;; Accent dark
   :--switch-on-accent-background-color-inverse
   :$accent-450

   :--switch-on-accent-background-color-hover-inverse
   :$accent-500

   :--switch-thumb-on-positive-color-inverse
   :$accent-minimal-color-inverse

   :--switch-thumb-on-positive-color-hover-inverse
   :$accent-minimal-color-hover-inverse

   ;; Positive dark
   ;; :$switch-on-positive-background-color-inverse--$lime-600
   ;; :$switch-on-positive-background-color-hover-inverse--$lime-650
   :--switch-on-positive-background-color-inverse
   :$positive-500

   :--switch-on-positive-background-color-hover-inverse
   :$positive-550

   :--switch-thumb-on-positive-color-inverse
   :$positive-minimal-color-inverse

   :--switch-thumb-on-positive-color-hover-inverse
   :$positive-minimal-color-hover-inverse

   ;; Warning dark
   :--switch-on-warning-background-color-inverse
   :$warning-550

   :--switch-on-warning-background-color-hover-inverse
   :$warning-600

   :--switch-thumb-on-warning-color-inverse
   :$warning-minimal-color-inverse

   :--switch-thumb-on-warning-color-hover-inverse
   :$warning-minimal-color-hover-inverse

   ;; Negative dark
   :--switch-on-negative-background-color-inverse
   :$negative-500

   :--switch-on-negative-background-color-hover-inverse
   :$negative-550

   :--switch-thumb-on-negative-color-inverse
   :$negative-minimal-color-inverse

   :--switch-thumb-on-negative-color-hover-inverse
   :$negative-minimal-color-hover-inverse
   ])
