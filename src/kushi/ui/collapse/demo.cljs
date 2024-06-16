(ns kushi.ui.collapse.demo
  (:require [kushi.core :refer (sx)]
            [kushi.playground.component-examples :as component-examples]
            [kushi.playground.util :refer-macros [sx-call]]
            [kushi.ui.collapse.core :refer [accordion collapse]]))


(declare collapse-examples)

(defn demo [component-opts]
  (into [:<>]
        (for [
              ;; example-opts (take 1 collapse-examples)
              example-opts collapse-examples
              ;; example-opts (keep-indexed (fn [idx m] (when (contains? #{9} idx) m)) collapse-examples)
              ]
          [component-examples/examples-section
           component-opts
           example-opts])))

(def collapse-examples
  (let [row-attrs {:class ["playground-example-row-bounded"]} ]
    [{:desc      "Basic" 
      :row-attrs row-attrs 
      :examples  [{
                   :code  (sx-call [collapse
                                    {:-label "Collapsable section label"} 
                                    [:p "Child 1"] 
                                    [:p "Child 2"]])}]}
     
     {:desc      "Dynamic label"
      :row-attrs row-attrs
      :examples  [{
                   :code  (sx-call [collapse
                                    {:-label          "Click to expand"
                                     :-label-expanded "Click to collapse"}
                                    [:p "Child 1"] 
                                    [:p "Child 2"]])}]}
     
     {:desc      "Icon on right"
      :row-attrs row-attrs
      :examples  [{
                   :code  (sx-call [collapse
                                    {:-label         "Collapsable section label "
                                     :-icon-position :end}
                                    [:p "Child 1"] 
                                    [:p "Child 2"]]
                                   )}]}

     {:desc      "Borders"
      :row-attrs row-attrs
      :examples  [{
                   :code  (sx-call [collapse
                                    (sx
                                     :bbe--1px:solid:$neutral-800
                                     :dark:bbe--1px:solid:$neutral-400
                                     {:-label        "Collapsable section label "
                                      :-header-attrs (sx :bbs--1px:solid:$neutral-800
                                                         :dark:bbs--1px:solid:$neutral-400)})
                                    [:p "Child 1"] 
                                    [:p "Child 2"]]
                                   )}]}

     {:desc      "Label weight"
      :row-attrs row-attrs
      :examples  [{
                   :code  (sx-call [collapse
                                    {:-label        "Collapsable section label "
                                     :-header-attrs (sx :.bold)}
                                    [:p "Child 1"] 
                                    [:p "Child 2"]]
                                   )}]}
     
     {:desc      "Body color"
      :row-attrs row-attrs
      :examples  [{
                   :code  (sx-call [collapse
                                    {:-label      "Collapsable section label "
                                     :-body-attrs (sx :bgc--$purple-100 
                                                      :dark:bgc--$purple-900 
                                                      :pis--1rem)
                                     :-speed      1000}
                                    [:section
                                     (sx :pb--0.5rem)
                                     [:p "Child 1"]
                                     [:p "Child 2"] 
                                     [:p "Child 3"] 
                                     [:p "Child 4"] 
                                     [:p "Child 5"]]]
                                   )}]}
     
     {:desc      "Header color"
      :row-attrs row-attrs
      :examples  [{
                   :code  (sx-call [collapse
                                    (sx
                                     :border-block--3px:solid:$purple-100
                                     :dark:border-block--3px:solid:$purple-850
                                     {:-label        "Collapsable section label "
                                      :-body-attrs   (sx :pis--0.5rem)
                                      :-header-attrs (sx
                                                      :.semi-bold
                                                      :p--10px
                                                      :bgc--$purple-100
                                                      :dark:bgc--$purple-850
                                                      ;; :c--white 
                                                      ;; :dark:c--black
                                                      )}) 
                                    [:p "Child 1"] 
                                    [:p "Child 2"]])}]}
     
     {:desc      "Click event"
      :row-attrs row-attrs
      :examples  [{:label "Click event"
                   :code  (sx-call [collapse
                                    {:-label    "Collapsable section label"
                                     :-on-click (fn* [] (js/alert "clicked"))}
                                    [:p "Child 1"] 
                                    [:p "Child 2"]])}]}]))

(declare accordion-examples)

(defn accordion-demo [component-opts]
  (into [:<>]
        (for [
              ;; example-opts (take 1 accordion-examples)
              example-opts accordion-examples
              ;; example-opts (keep-indexed (fn [idx m] (when (contains? #{9} idx) m)) accordion-examples)
              ]
          [component-examples/examples-section
           component-opts
           example-opts])))

(def accordion-examples
  (let [row-attrs {:class ["playground-example-row-bounded"]}]
    [{:desc      "Basic"
      :row-attrs row-attrs
      :examples  [{:code (sx-call [accordion
                                   [collapse
                                    {:-label "First section" }
                                    [:div (sx :pis--0.5rem) [:p "Child 1"] [:p "Child 2"]]]
                                   [collapse
                                    {:-label "Second section" }
                                    [:div (sx :pis--0.5rem) [:p "Child 1"] [:p "Child 2"]]]])}]}

     {:desc      "With slower transition"
      :row-attrs row-attrs
      :examples  [{:code (sx-call [accordion
                                   [collapse
                                    {:-label "First section"
                                     :-speed 1000}
                                    [:div (sx :pis--0.5rem) [:p "Child 1"] [:p "Child 2"]]]
                                   [collapse
                                    {:-label "Second section"
                                     :-speed 1000}
                                    [:div (sx :pis--0.5rem) [:p "Child 1"] [:p "Child 2"]]]])}]}
     
     {:desc      "With block borders"
      :row-attrs row-attrs
      :examples  [{:code (sx-call [accordion
                                   (sx :>section:first-child:bbs--1px:solid:black)
                                   (for
                                    [[label-text content]
                                     [["First section" "Lorem ipsum"]
                                      ["Second section" "Lorem ipsum2"]
                                      ["Third section" "Lorem ipsum3"]]]
                                     [collapse
                                      (sx :bbe--1px:solid:black {:-label label-text})
                                      [:p content]])])}]}]))
