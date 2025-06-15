(ns ^:dev/always kushi.ui.variants
  (:require 
   [fireworks.core :refer [? !? ?> !?>]]
   [kushi.ui.util :refer [keyed]]
   #?(:clj [kushi.ui.ordered :refer [ordered-set]])))

(def basic-shapes
  [:rounded :pill :sharp])

(def auxillary-shapes
  [:circle :squircle])

(def all-shapes
  (apply conj basic-shapes auxillary-shapes))

(def icon-style
  [:rounded :outlined :sharp])

(def spinner-type
  [:donut :thinking :propeller])

(def packing
  [:compact :default :roomy])

(def basic-colors
  [:gray :purple :blue :green :lime :yellow :gold :orange :red :magenta :brown])

(def semantic-colors
  [:neutral :accent :positive :warning :negative])

(def colors
  (apply conj basic-colors semantic-colors))

(def xxsmall-xlarge
  [:xxsmall :xsmall :small :medium :large :xlarge])

(def sizes 
  [:xxxsmall :xxsmall :xsmall :small :medium :large :xlarge :xxlarge :xxxlarge])

(def weights
  [:thin :extra-light :light :normal :wee-bold :semi-bold :bold :extra-bold :heavy])

(def surfaces 
  [:solid-classic :solid :soft-classic :soft :faint :outline :minimal :transparent])

(def variants*
  (keyed [basic-shapes
          auxillary-shapes
          all-shapes
          icon-style
          spinner-type
          packing
          basic-colors
          semantic-colors
          colors
          xxsmall-xlarge
          sizes
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
  {:-weight       (:weights/set variants)
   :-size         (:sizes/set variants)
   :-colorway     (:colors/set variants)
   :-surface      (:surfaces/set variants)
   :-packing      (:packing/set variants)
   :-spinner-type (:spinner-type/set variants)
   :-shape        (:shapes/set variants)})


;; :pred -> :schema

;; In this namespace :colors -> colorways

;; size -> sizing

;; shape ->  countour

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
