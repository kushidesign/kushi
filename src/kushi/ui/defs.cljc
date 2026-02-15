(ns kushi.ui.defs
  (:require 
   [fireworks.core :refer [? !? ?> !?>]]
   [kushi.ui.util :refer [keyed]]))

(def basic-shapes
  [:rounded :pill :sharp])

(def auxillary-shapes
  [:squircle])

(def all-shapes
  (apply conj [:rounded :pill :circle :sharp :squircle] auxillary-shapes))

(def icon-style
  [:rounded :outlined :sharp])

(def spinner-type
  [:donut :thinking :propeller])

(def packing
  [:compact :default :roomy])

(+ 1 1)

(def colors-from-0
  [:ruby
   :red
   :coral
   :orange 
   :amber
   :apricot
   :gold
   :banana
   :yellow
   :citron
   :acid
   :lime
   :matcha
   :green
   :jade
   :emerald
   :mint
   :teal
   :cyan
   :aqua
   :glacier
   :azure
   :sky
   :blue
   :lapis
   :indigo
   :violet
   :purple 
   :plum
   :magenta 
   :pink
   :rose])

(def basic-colors*
  (apply array-map 
         [:blue 255
          :sky 247
          :azure 234
          :aqua 216
          :cyan 202
          :teal 188
          :mint 174
          :emerald 163
          :jade 151
          :green 143
          :matcha 136
          :lime 131
          :acid 125
          :citron 118
          :yellow 110
          :banana 103
          :gold 96
          :apricot 87
          :amber 78
          :orange 66 
          :coral 49
          :tomato 38
          :red 20
          :ruby 0
          :rose 349
          :pink 335
          :magenta 320
          :plum 304
          :purple 294
          :violet 283
          :indigo 274
          :lapis 266]))


(def basic-colors
  (into [] (keys basic-colors*))
  #_[
     :blue
     :sky
     :azure
     :aqua
     :cyan
     :teal
     :mint
     :emerald
     :jade
     :green
     :matcha
     :lime
     :acid
     :citron
     :yellow
     :banana
     :gold
     :apricot
     :amber
     :orange
     :coral
     :red
     :ruby
     :rose
     ;;  :crimson
     :pink
     :magenta
     :plum
     :purple
     :violet
     :indigo
     :lapis
     :blue
     :sky
     

     ;;  :gray
     ;;  :sand
     ;;  :slate
     ])

(def semantic-colors
  [:neutral
   :accent
   :positive
   :warning
   :negative])



(def all-colors
  (apply 
   conj
   basic-colors
   semantic-colors))

(def sizes-2xs-xl
  [:2xs
   :xs
   :sm
   :md
   :lg
   :xl])

(def sizes-3xs-3xl
  [:3xs
   :2xs
   :xs
   :sm
   :md
   :lg
   :xl
   :2xl
   :3xl])

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
  [:3xs
   :2xs
   :xs
   :sm
   :md
   :lg
   :xl
   :2xl
   :3xl])

(def surfaces 
  [:solid-classic :solid :soft-classic :soft :faint :outline :minimal :transparent])

(def basic-surfaces-vector
  [:solid-classic :solid :soft-classic :soft :faint :outline :minimal :transparent])

(def basic-weights-set
  (into #{} weights))

(def basic-weights-set-of-strs
  (into #{} (map name weights)))

(def basic-sizes-vector
  sizes-3xs-3xl)

(def size-enum
  (into [] (concat [:enum] sizes-3xs-3xl)))

(def colorway-enum
  (into [] (concat [:enum] basic-colors)))

(def basic-sizes-set
  (into #{} sizes-3xs-3xl))

(def basic-sizes-set-of-strs
  (into #{} (map name sizes-3xs-3xl)))

(def basic-surfaces-set
  (into #{} basic-surfaces-vector))

(def basic-surfaces-set-of-strs
  (into #{} (map name basic-surfaces-vector)))

(def variants-ordered 
  {'sizes-xs-xl  
   sizes-2xs-xl

   'sizes-3xs-3xl 
   sizes-3xs-3xl 
   
   :defaults
   {'size            sizes-3xs-3xl
    'weight          weights
    'shape           basic-shapes
    'surface         basic-surfaces-vector
    'colorway        all-colors
    'semantic-colors semantic-colors
    'spinner-type    spinner-type
    'icon-style      icon-style
    'packing         packing}})

(def variants-ordered-kw
  {:sizes-2xs-xl  
   sizes-2xs-xl

   :3xs-3xl 
   sizes-3xs-3xl 
   
   :defaults
   {:size       sizes-3xs-3xl
    :weight     weights
    :shape           basic-shapes
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


