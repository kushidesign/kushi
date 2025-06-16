(ns ^:dev/always kushi.ui.variants
  (:require 
   [fireworks.core :refer [? !? ?> !?>]]
   [kushi.ui.util :refer [keyed]]
   #?(:clj [kushi.ui.ordered :refer [ordered-set]])))

(def basic-contours
  [:rounded :pill :sharp])

(def auxillary-contours
  [:circle :squircle])

(def all-contours
  (apply conj basic-contours auxillary-contours))

(def icon-style
  [:rounded :outlined :sharp])

(def spinner-type
  [:donut :thinking :propeller])

(def packing
  [:compact :default :roomy])

(def basic-colorways
  [:gray :purple :blue :green :lime :yellow :gold :orange :red :magenta :brown])

(def semantic-colorways
  [:neutral :accent :positive :warning :negative])

(def colorways
  (apply conj basic-colorways semantic-colorways))

(def xxsmall-xlarge
  [:xxsmall :xsmall :small :medium :large :xlarge])

(def sizings 
  [:xxxsmall :xxsmall :xsmall :small :medium :large :xlarge :xxlarge :xxxlarge])

(def weights
  [:thin :extra-light :light :normal :wee-bold :semi-bold :bold :extra-bold :heavy])

(def surfaces 
  [:solid-classic :solid :soft-classic :soft :faint :outline :minimal :transparent])

(def variants*
  (keyed [basic-contours
          auxillary-contours
          all-contours
          icon-style
          spinner-type
          packing
          basic-colorways
          semantic-colorways
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
  {:weight       (:weights/set variants)
   :sizing       (:sizings/set variants)
   :colorway     (:colorways/set variants)
   :surface      (:surfaces/set variants)
   :packing      (:packing/set variants)
   :spinner-type (:spinner-type/set variants)
   :contour        (:contours/set variants)
   :icon-style   (:icon-style/set variants)
   })

(def variants-by-custom-opt-key-set
  (into #{} (keys variants-by-custom-opt-key)))

;; Use data-ks instead of data-kushi

;; in sx and css, support supplied data selector "[data-kushi-ui=icon]"

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
