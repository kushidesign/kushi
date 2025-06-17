(ns ^{:kushi/layer "user-styles"} kushi.ui.collapse.demo
  (:require [kushi.core :refer (sx css merge-attrs)]
            [kushi.playground.component-examples :as component-examples]
            [kushi.playground.util :refer-macros [sx-call]]
            [kushi.ui.collapse :refer [accordion collapse]]))

(def examples
  (let [row-attrs
        {:class ["playground-example-row-bounded"]}

        f
        (fn [desc code]
          {:desc      desc
           :row-attrs row-attrs
           :snippets  [(:quoted code)]
           :examples  [{:code code}]})]
    [(f "Basic"
        (sx-call [collapse
                  {:label "Collapsable section label"} 
                  [:p "Child 1"] 
                  [:p "Child 2"]]))
     
     (f "Dynamic label"
        (sx-call [collapse
                  {:label          "Click to expand"
                   :label-expanded "Click to collapse"}
                  [:p "Child 1"] 
                  [:p "Child 2"]]))
     
     (f "Icon on right"
        (sx-call [collapse
                  {:label         "Collapsable section label "
                   :icon-position :end}
                  [:p "Child 1"] 
                  [:p "Child 2"]]))

     (f "Borders"
        (sx-call [collapse
                  (merge-attrs 
                   (sx
                    :bbe--1px:solid:$neutral-800
                    :dark:bbe--1px:solid:$neutral-400)
                   {:label        "Collapsable section label "
                    :header-attrs (sx :bbs--1px:solid:$neutral-800
                                       :dark:bbs--1px:solid:$neutral-400)})
                  [:p "Child 1"] 
                  [:p "Child 2"]]))

     (f "Label weight"
        (sx-call [collapse
                  {:label        "Collapsable section label "
                   :header-attrs (sx :fw--$bold)}
                  [:p "Child 1"] 
                  [:p "Child 2"]]))
     
     (f "Body color"
        (sx-call [collapse
                  {:label      "Collapsable section label "
                   :body-attrs (sx :bgc--$purple-100 
                                    :dark:bgc--$purple-900 
                                    :pis--1rem)
                   :speed      1000}
                  [:section
                   (sx :pb--0.5rem)
                   [:p "Child 1"]
                   [:p "Child 2"] 
                   [:p "Child 3"] 
                   [:p "Child 4"] 
                   [:p "Child 5"]]]))
     
     (f "Header color"
        (sx-call [collapse
                  {:class         (css
                                   :border-block--3px:solid:$purple-100
                                   :dark:border-block--3px:solid:$purple-850)
                   
                   :label        "Collapsable section label "
                   :body-attrs   (sx :pis--0.5rem)
                   :header-attrs (sx
                                   :fw--$semi-bold
                                   :p--10px
                                   :bgc--$purple-100
                                   :dark:bgc--$purple-850)}
                  [:p "Child 1"] 
                  [:p "Child 2"]]))
     
     (f "Click event"
        (sx-call [collapse
                  {:label    "Collapsable section label"
                   :-on-click (fn* [] (js/alert "clicked"))}
                  [:p "Child 1"] 
                  [:p "Child 2"]]))]))

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
                                    {:label "First section" }
                                    [:div (sx :pis--0.5rem) [:p "Child 1"] [:p "Child 2"]]]
                                   [collapse
                                    {:label "Second section" }
                                    [:div (sx :pis--0.5rem) [:p "Child 1"] [:p "Child 2"]]]])}]}

     {:desc      "With slower transition"
      :row-attrs row-attrs
      :examples  [{:code (sx-call [accordion
                                   [collapse
                                    {:label "First section"
                                     :speed 1000}
                                    [:div (sx :pis--0.5rem) [:p "Child 1"] [:p "Child 2"]]]
                                   [collapse
                                    {:label "Second section"
                                     :speed 1000}
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
                                      (merge-attrs (sx :bbe--1px:solid:black)
                                                   {:label label-text})
                                      [:p content]])])}]}]))
