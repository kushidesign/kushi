(ns kushi.playground.demobox.defs)

(def variants-by-category
  {
   :size          [:xxxsmall :xxsmall :xsmall :small :medium :large :xlarge #_:xxlarge #_:xxxlarge]
   :size-expanded [:xxxsmall :xxsmall :xsmall :small :medium :large :xlarge :xxlarge :xxxlarge :xxxxlarge]
   :tracking      [:xxxtight
                   :xxtight 
                   :xtight  
                   :tight   
                   :default-tracking
                   :loose   
                   :xloose  
                   :xxloose 
                   :xxxloose]
   :elevation     [:elevated-0
                   :elevated-1
                   :elevated-2
                   :elevated-3
                   :elevated-4
                   :elevated-5]
   :kind          [:default :minimal :bordered :filled]
   :kind2         [:default :bordered :filled]
  ;;  :status   [:disabled]
   :shape         [:pill :rounded :sharp]
   :weight        [:thin
                   :extra-light
                   :light
                   :normal
                   :wee-bold
                   :semi-bold
                   :bold
                   :extra-bold]
   :semantic      [:neutral
                   :accent
                   :positive
                   :negative
                   :warning]
   
   :flex          [:flex-row-fs       
                   :flex-row-c        
                   :flex-row-fe       
                   :flex-row-sa       
                   :flex-row-se       
                   :flex-row-sb
                   :flex-col-fs       
                   :flex-col-c        
                   :flex-col-fe       
                   :flex-col-sa       
                   :flex-col-se       
                   :flex-col-sb]
   
   :display       [:block       
                   :inline      
                   :inline-block
                   :flex        
                   :inline-flex 
                   :grid        
                   :inline-grid 
                   :flow-root   
                   :contents]
   })
