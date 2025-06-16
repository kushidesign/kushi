(ns kushi.ui.defs
  (:require 
   [fireworks.core :refer [? !? ?> !?>]]
   [kushi.ui.util :refer [keyed]]))

(def basic-contours
  [:rounded :pill :sharp])

(def auxillary-contours
  [:squircle])

(def all-contours
  (apply conj [:rounded :pill :circle :sharp :squircle] auxillary-contours))

(def icon-style
  [:rounded :outlined :sharp])

(def spinner-type
  [:donut :thinking :propeller])

(def packing
  [:compact :default :roomy])



(def basic-colors
  [:gray
   :purple
   :blue
   :green
   :lime
   :yellow
   :gold
   :orange
   :red
   :magenta
   :brown])

(def semantic-colors
  [:neutral
   :accent
   :positive
   :warning
   :negative])

(def all-colors
  (apply conj
         [:gray
          :purple
          :blue
          :green
          :lime
          :yellow
          :gold
          :orange
          :red
          :magenta
          :brown]
         semantic-colors))


(def xxsmall-xlarge
  [:xxsmall
   :xsmall
   :small
   :medium
   :large
   :xlarge])

(def xxxsmall-xxxlarge
  [:xxxsmall
   :xxsmall
   :xsmall
   :small
   :medium
   :large
   :xlarge
   :xxlarge
   :xxxlarge])

(def weights
  [:thin
   :extra-light
   :light
   :normal
   :wee-bold
   :semi-bold
   :bold
   :extra-bold
   :heavy])

(def sizes 
  [:xxxsmall
   :xxsmall
   :xsmall
   :small
   :medium
   :large
   :xlarge
   :xxlarge
   :xxxlarge])

(def surfaces 
  [:solid-classic :solid :soft-classic :soft :faint :outline :minimal :transparent])

(def basic-surfaces-vector
  [:solid-classic :solid :soft-classic :soft :faint :outline :minimal :transparent])

(def basic-weights-set
  (into #{} weights))

(def basic-weights-set-of-strs
  (into #{} (map name weights)))

(def basic-sizes-vector
  xxxsmall-xxxlarge)

(def size-enum
  (into [] (concat [:enum] xxxsmall-xxxlarge)))

(def colorway-enum
  (into [] (concat [:enum] basic-colors)))

(def basic-sizes-set
  (into #{} xxxsmall-xxxlarge))

(def basic-sizes-set-of-strs
  (into #{} (map name xxxsmall-xxxlarge)))

(def basic-surfaces-set
  (into #{} basic-surfaces-vector))

(def basic-surfaces-set-of-strs
  (into #{} (map name basic-surfaces-vector)))

(def variants-ordered 
  {'xxsmall-xlarge  
   xxsmall-xlarge

   'xxxsmall-xxxlarge 
   xxxsmall-xxxlarge 
   
   :defaults
   {'size            xxxsmall-xxxlarge
    'weight          weights
    'shape           basic-contours
    'surface         basic-surfaces-vector
    'colorway        all-colors
    'semantic-colors semantic-colors
    'spinner-type    spinner-type
    'icon-style      icon-style
    'packing         packing}})

(def variants-ordered-kw
  {:xxsmall-xlarge  
   xxsmall-xlarge

   :xxxsmall-xxxlarge 
   xxxsmall-xxxlarge 
   
   :defaults
   {:size            xxxsmall-xxxlarge
    :weight          weights
    :contour         basic-contours
    :surface         basic-surfaces-vector
    :colorway        basic-colors
    :semantic-colors semantic-colors
    :spinner-type    spinner-type
    :icon-style      icon-style
    :packing         packing}})

(def variants-syms-set
  (->> variants-ordered
       :defaults
       keys
       (into #{})))

(def variants-kw-set
  (->> variants-ordered-kw
       :defaults
       keys
       (into #{})))


