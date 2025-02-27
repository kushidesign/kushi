(ns kushi.ui.defs)

(def all-shapes
  [:rounded :pill :circle :sharp :squircle])

(def all-colors
  [
   "neutral"
   "purple"
   "blue"
   "green"
   "lime"
   "yellow"
   "gold"
   "orange"
   "red"
   "magenta"
   "brown"
   ])

(def basic-shapes
  [:rounded :pill :sharp])

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

(def basic-surfaces
  [:soft :solid :outline :minimal])

(def variants-ordered 
  {'xxsmall-xlarge  
   xxsmall-xlarge

   'xxxsmall-xxxlarge 
   xxxsmall-xxxlarge 
   
   :defaults
   {'size    xxxsmall-xxxlarge
    'shape   basic-shapes
    'surface basic-surfaces
    'colorway all-colors}})
