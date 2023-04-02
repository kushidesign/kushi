(ns kushi.ui.typescale
  (:require
   [clojure.string :as string]))


;; primary type scale from xxxxsmall ~ xxxxlarge
(def scale-a
  [0.64
   0.67
   0.71
   0.77
   0.86
   1
   1.21
   1.485
   1.86
   2.36
   3.03])


;; Secondary type scale from xxxxsmall ~ xxxxlarge
;; Shifted "up"
(def scale-b
  [0.655
   0.685
   0.733
   0.805
   0.92
   1.1
   1.33
   1.655
   2.085
   2.68
   3.475])


(defn create-tshirt-sizes [s n]
  (when (and (pos-int? n) (< 2 n 6))
    (map #(keyword (str "$text-" (string/join (repeat % "x")) s)) (range n))))

(defn type-scale-map [{:keys [full shift medium-index expanded-scale?]} f coll]
  (let [fallback        (if (= f -) (first full) (last full))
        step-multiplier (if expanded-scale? 2 1)]
    (into {}
          (map-indexed (fn [idx s]
                         [s (keyword (str (nth full
                                               (f (+ shift medium-index)
                                                  (* step-multiplier (inc idx)))
                                               fallback)
                                          "rem"))])
                       coll))))

(defn warn-and-return-shift [shift-og n]
  (println
   "\n\n"
   "◢◤◢◤ WARNING ◢◤◢◤◢◤◢◤◢◤◢◤◢◤◢◤◢◤◢◤◢◤◢◤◢◤◢◤◢◤◢◤◢◤◢◤\n\n"
   "kushi.ui.typescale/create-typescale\n\n"
   "You provided a `shift` value of:\n"
   shift-og "\n"
   "This will be clamped to:\n"
   n "\n\n"
   "◢◤◢◤◢◤◢◤◢◤◢◤◢◤◢◤◢◤◢◤◢◤◢◤◢◤◢◤◢◤◢◤◢◤◢◤◢◤◢◤◢◤◢◤◢◤◢◤◢◤\n"
   "\n\n")
  n)

(defn create-typescale [{:keys [size-limit shift typescale]}]
  (let [full            (or typescale
                            (interleave scale-a scale-b))
        expanded-scale? (= 22 (count full))
        medium-index    (first (keep-indexed (fn [i x] (when (= x 1) i))
                                             full))
        size-limit      (count (string/trim (if (string? size-limit) size-limit "xxx")))
        num-sizes       (inc (min 4 size-limit))
        shift-og        shift
        shift           (if (number? shift) shift 0)
        shift-min       (if expanded-scale? -3 -1)
        shift-max       (if expanded-scale? 3 1)
        shift           (cond (< shift shift-min)
                              (warn-and-return-shift shift-og shift-min)
                              (> shift shift-max)
                              (warn-and-return-shift shift-og shift-max)
                              :else
                              shift)
        opts            {:full            full
                         :shift           shift
                         :medium-index    medium-index
                         :expanded-scale? expanded-scale?}
        smalls          (create-tshirt-sizes "small" num-sizes)
        smalls+         (type-scale-map opts - smalls)
        larges          (create-tshirt-sizes "large" num-sizes)
        larges+         (type-scale-map opts + larges)
        medium+         {:$medium (keyword (str (nth full (+ shift medium-index)) "rem"))}
        sizes           (concat (reverse smalls+) medium+ larges+)]
    (apply concat sizes)))

