(ns ^{:kushi/layer "user-styles"} 
  kushi.ui.collapse.demo
  (:require [kushi.core :refer (sx css merge-attrs)]
            [kushi.ui.collapse :refer [collapse]]
            [kushi.core :refer (sx merge-attrs)]
            [kushi.showcase.core :as showcase :refer [samples]]))

(def demos
  (let [row-attrs (sx {:w :fit-content})]
    [{:label     "Basic"
      :row-attrs row-attrs
      :samples   (samples [[collapse
                            {:label-collapsed "Collapsable section label"
                             :header-attrs    (sx {:padding-inline-end :0.5em
                                                   :bgc                :$neutral-100
                                                   :dark:bgc           :$neutral-850})} 
                            [:p "Child 1"] 
                            [:p "Child 2"]]])}
     
     {:label     "Dynamic label"
      :row-attrs row-attrs
      :samples   (samples [[collapse
                            (merge-attrs 
                             {:label-collapsed "Click to expand"
                              :label-expanded  "Click to collapse"
                              :header-attrs    (sx {:padding-inline-end :0.5em
                                                    :bgc                :$neutral-100
                                                    :dark:bgc           :$neutral-850})})
                            [:p "Child 1"] 
                            [:p "Child 2"]]])}
     
     {:label     "Icon on right"
      :row-attrs row-attrs
      :samples   (samples [[collapse
                            {:label-collapsed "Collapsable section label"
                             :icon-position   :end
                             :header-attrs    (sx {:padding-inline-end :0.5em
                                                   :bgc                :$neutral-100
                                                   :dark:bgc           :$neutral-850})}
                            [:p "Child 1"] 
                            [:p "Child 2"]]])}

     {:label     "Borders"
      :row-attrs row-attrs
      :samples   (samples [[collapse
                            (merge-attrs 
                             (sx {:bbe      :1px:solid:$neutral-800
                                  :dark:bbe :1px:solid:$neutral-400})
                             {:label-collapsed "Collapsable section label"
                              :header-attrs    (sx {:bbs      :1px:solid:$neutral-800
                                                    :dark:bbs :1px:solid:$neutral-400
                                                    :bgc      :$neutral-100
                                                    :dark:bgc :$neutral-850
                                                    :pi       :0.5em:1em})})
                            [:p "Child 1"] 
                            [:p "Child 2"]]])}

     {:label     "Label weight"
      :row-attrs row-attrs
      :samples   (samples [[collapse
                            {:label-collapsed "Collapsable section label"
                             :header-attrs    (sx {:fw       :$bold
                                                   :bgc      :$neutral-100
                                                   :dark:bgc :$neutral-850
                                                   :pi       :0.5em:1em})}
                            [:p "Child 1"] 
                            [:p "Child 2"]]])}
     
     {:label     "Body color"
      :row-attrs row-attrs
      :samples   (samples [[collapse
                            {:label-collapsed "Collapsable section label"
                             :body-attrs      (sx {:bgc      :$purple-100 
                                                   :dark:bgc :$purple-900 
                                                   :pi       :0.5em:1em})
                             :header-attrs    (sx {:pi       :0.5em:1em
                                                   :bgc      :$neutral-100
                                                   :dark:bgc :$neutral-850})
                             :speed           1000}
                            [:section
                             (sx {:pb :0.5rem})
                             [:p "Child 1"]
                             [:p "Child 2"] 
                             [:p "Child 3"] 
                             [:p "Child 4"] 
                             [:p "Child 5"]]]])}
     
     {:label     "Header color"
      :row-attrs row-attrs
      :samples   (samples [[collapse
                            {:label-collapsed "Collapsable section label"
                             :body-attrs      (sx {:pis :0.5rem})
                             :header-attrs    (sx {:fw       :$semi-bold
                                                   :pi       :0.5em:1em
                                                   :bgc      :$purple-100
                                                   :dark:bgc :$purple-850})}
                            [:p "Child 1"] 
                            [:p "Child 2"]]])}
     
     {:label     "Click event"
      :row-attrs row-attrs
      :samples   (samples [[collapse
                            {:label-collapsed "Collapsable section label"
                             :-on-click       (fn* [] (js/alert "clicked"))
                             :header-attrs    (sx {:pi       :0.5em:1em
                                                   :bgc      :$neutral-100
                                                   :dark:bgc :$neutral-850})}
                            [:p "Child 1"] 
                            [:p "Child 2"]]])}]))
