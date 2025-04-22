(ns kushi.ui.defs)

(def all-shapes
  [:rounded :pill :circle :sharp :squircle])

(def all-colors
  ["gray"
   "purple"
   "blue"
   "green"
   "lime"
   "yellow"
   "gold"
   "orange"
   "red"
   "magenta"
   "brown"])

(def basic-shapes
  [:rounded :pill :sharp])

(def packing
  [:compact :default :roomy])

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

(def basic-surfaces-vector
  [:solid-classic :solid :soft-classic :soft :faint :outline :minimal])

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
   {'size    xxxsmall-xxxlarge
    'shape   basic-shapes
    'surface basic-surfaces-vector
    'colorway all-colors
    'packing packing}})

(def variants-syms-set
  (->> variants-ordered
       :defaults
       keys
       (into #{})))
