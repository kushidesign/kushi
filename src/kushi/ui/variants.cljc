(ns ^:dev/always kushi.ui.variants
  (:require 
   [kushi.ui.util :refer [keyed]]
   #?(:clj [kushi.ui.ordered :refer [ordered-set]])))

(def contours-basic
  [:rounded :pill :sharp])

(def contours-auxillary
  [:circle :squircle])

(def contours
  (apply conj contours-basic contours-auxillary))

(def icon-style
  [:rounded :outlined :sharp])

(def spinner-type
  [:donut :thinking :propeller])

(def packing
  [:compact :default :roomy])

(def colorways-named
  [:gray :purple :blue :green :lime :yellow :gold :orange :red :magenta :brown])

(def colorways-semantic
  [:neutral :accent :positive :warning :negative])

(def colorways
  (apply conj colorways-named colorways-semantic))

(def xxsmall-xlarge
  [:xxsmall :xsmall :small :medium :large :xlarge])

(def sizings 
  [:xxxsmall :xxsmall :xsmall :small :medium :large :xlarge :xxlarge :xxxlarge])

(def weights
  [:thin :extra-light :light :normal :wee-bold :semi-bold :bold :extra-bold :heavy])

(def surfaces 
  [:solid-classic :solid :soft-classic :soft :faint :outline :minimal :transparent])

(def variants*
  (keyed [contours-basic
          contours-auxillary
          contours
          icon-style
          spinner-type
          packing
          colorways-named
          colorways-semantic
          colorways
          xxsmall-xlarge
          sizings
          weights
          surfaces]))

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
  {:weight            (:weights/set variants)
   :sizing            (:sizings/set variants)
   :colorway          (:colorways/set variants)
   :colorway/named    (:colorways-named/set variants)
   :colorway/semantic (:colorways-semantic/set variants)
   :surface           (:surfaces/set variants)
   :packing           (:packing/set variants)
   :spinner-type      (:spinner-type/set variants)
   :contour           (:contours/set variants)
   :contour/basic     (:contours-basic/set variants)
   :contour/auxillary (:contours-auxillary/set variants)
   :icon-style        (:icon-style/set variants)})

(def ordered-variants-by-custom-opt-key
  {:weight            (:weights/vector variants)
   :sizing            (:sizings/vector variants)
   :colorway          (:colorways/vector variants)
   :colorway/named    (:colorways-named/vector variants)
   :colorway/semantic (:colorways-semantic/vector variants)
   :surface           (:surfaces/vector variants)
   :packing           (:packing/vector variants)
   :spinner-type      (:spinner-type/vector variants)
   :contour           (:contours/vector variants)
   :contour/basic     (:contours-basic/vector variants)
   :contour/auxillary (:contours-auxillary/vector variants)
   :icon-style        (:icon-style/vector variants)})

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
